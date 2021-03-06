package org.stoevesand.findow.rest;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.jobs.JobManager;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.ApiUser;
import org.stoevesand.findow.server.FindowSystem;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/users")
@Api(value = "users")
public class RestUsers {

	private Logger log = LoggerFactory.getLogger(RestUsers.class);

	@Context
	private HttpServletResponse response;

	@Context
	SecurityContext securityContext;

	@Path("/")
	@GET
	@Secured
	@Produces("application/json")
	public String getUserFromToken() {
		JobManager.getInstance();
		RestUtils.addHeader(response);
		String result = "";

		log.info("get user from token");

		Principal principal = securityContext.getUserPrincipal();
		String jwsUser = principal.getName();
		FinUser user = PersistanceManager.getInstance().getUserByName(jwsUser);

		// Neue User mit einem gültigen Token werden neu angelegt
		if (user == null) {
			user = createNewUser(jwsUser);
		}

		if (user != null) {
			result = RestUtils.generateJsonResponse(user, "user");
		} else {
			result = RestUtils.generateJsonResponse(FindowResponse.INVALID_JWT);
		}

		return result;
	}

	@Path("/{id}")
	@POST
	@Produces("application/json")
	public String createUser(@PathParam("id") String id, @HeaderParam("password") String password) {
		RestUtils.addHeader(response);
		String result = "";
		FinUser user = PersistanceManager.getInstance().getUserByName(id);

		if (user == null) {
			user = createNewUser(id);
			result = RestUtils.generateJsonResponse(FindowResponse.OK);
		} else {
			result = RestUtils.generateJsonResponse(FindowResponse.USER_ALREADY_USED);
		}

		return result;
	}

	/**
	 * Auslagerung der Funktion einen neuen User anzulegen. Wird an zwei Stellen
	 * benötigt.
	 * 
	 * @param id
	 * @return
	 */
	private FinUser createNewUser(String id) {
		FinUser user = null;
		try {
			ApiUser apiUser = FindowSystem.getBankingAPI("FIGO").createUser(id, id);
			user = new FinUser(id, apiUser.getId(), apiUser.getPassword(), "FIGO");
			user = PersistanceManager.getInstance().persist(user);
		} catch (FinErrorHandler e) {
			log.error("Failed to create user: " + id);
			log.error("Reason: " + e.getMessage());
		}
		return user;
	}

	@Path("/{id}")
	@DELETE
	@Produces("application/json")
	public String deleteUser(@PathParam("id") String id, @HeaderParam("userToken") String userToken) {
		RestUtils.addHeader(response);
		try {
			FinUser user = PersistanceManager.getInstance().getUserByName(id);
			if (user != null) {
				FindowSystem.getBankingAPI(user).deleteUser(userToken);
			} else {
				return RestUtils.generateJsonResponse(FindowResponse.USER_UNKNOWN);
			}

			PersistanceManager.getInstance().deleteUserByName(id);
		} catch (FinErrorHandler e) {
			log.error(e.toString());
			return e.getResponse();
		}
		return RestUtils.generateJsonResponse(FindowResponse.OK);
	}

	@Path("/infos")
	@GET
	@Produces("application/json")
	@ApiOperation(value = "Get UserInfos of all available users.")
	public String getUserInfos() {
		RestUtils.addHeader(response);
		List<FinUser> userInfos = PersistanceManager.getInstance().getUsers();
		// MandatorAdminService.getUsers(RestUtils.getAdminToken());

		String result = RestUtils.generateJsonResponse(userInfos, "users");

		return result;
	}

}