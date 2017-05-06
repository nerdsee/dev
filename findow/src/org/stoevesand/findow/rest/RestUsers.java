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
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.User;
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

	@Path("/{id}")
	@GET
	@Produces("application/json")
	public String getUser(@PathParam("id") String id, @HeaderParam("password") String password) {
		JobManager.getInstance();
		RestUtils.addHeader(response);
		String result = "";

		User user = PersistanceManager.getInstance().getUserByName(id);
		if ((user != null) && (user.getPassword().equals(password))) {

			try {
				user.refreshToken();
				result = RestUtils.generateJsonResponse(user, "user");
			} catch (ErrorHandler e) {
				result = e.getResponse();
			}

		} else {
			result = RestUtils.generateJsonResponse(FindowResponse.USER_OR_PASSWORD_INVALID);
		}

		return result;
	}

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

		log.info("user: " + jwsUser);

		User user = PersistanceManager.getInstance().getUserByName(jwsUser);
		if (user != null) {

			try {
				user.refreshToken();
				result = RestUtils.generateJsonResponse(user, "user");
			} catch (ErrorHandler e) {
				result = e.getResponse();
			}

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
		try {
			User user = PersistanceManager.getInstance().getUserByName(id);

			if (user == null) {
				ApiUser apiUser = FindowSystem.getBankingAPI().createUser(null, null);
				user = new User(id, password, apiUser.getId(), apiUser.getPassword());
				PersistanceManager.getInstance().store(user);
				result = RestUtils.generateJsonResponse(FindowResponse.OK);
			} else {
				result = RestUtils.generateJsonResponse(FindowResponse.USER_ALREADY_USED);
			}

		} catch (ErrorHandler e) {
			result = e.getResponse();
		}

		return result;
	}

	@Path("/{id}")
	@DELETE
	@Produces("application/json")
	public String deleteUser(@PathParam("id") String id, @HeaderParam("userToken") String userToken) {
		RestUtils.addHeader(response);
		try {
			User user = PersistanceManager.getInstance().getUserByName(id);
			if (user != null) {
				FindowSystem.getBankingAPI().deleteUser(userToken);
			} else {
				return RestUtils.generateJsonResponse(FindowResponse.USER_UNKNOWN);
			}

			PersistanceManager.getInstance().deleteUserByName(id);
		} catch (ErrorHandler e) {
			System.out.println(e);
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
		List<User> userInfos = PersistanceManager.getInstance().getUsers();
		// MandatorAdminService.getUsers(RestUtils.getAdminToken());

		String result = RestUtils.generateJsonResponse(userInfos, "users");

		return result;
	}

}