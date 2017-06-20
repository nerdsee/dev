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
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinAccountWrapper;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.server.FindowSystem;

import io.swagger.annotations.Api;
import me.figo.FigoSession;
import me.figo.models.Bank;

@Path("/accounts")
@Api(value = "accounts")
public class RestAccounts {

	private Logger log = LoggerFactory.getLogger(RestAccounts.class);

	@Context
	private HttpServletResponse response;

	@Context
	SecurityContext securityContext;

	@Path("/{id}")
	@GET
	@Secured
	@Produces("application/json")
	public String getAccount(@PathParam("id") Long accountId) {
		RestUtils.addHeader(response);
		String result = "";
		log.info("getAccount " + accountId);

		try {
			Principal principal = securityContext.getUserPrincipal();
			String jwsUser = principal.getName();
			FinUser user = PersistanceManager.getInstance().getUserByName(jwsUser);

			FinAccount account = PersistanceManager.getInstance().getAccount(user, accountId);

			result = RestUtils.generateJsonResponse(account, "account");
		} catch (NumberFormatException nfe) {
			result = RestUtils.generateJsonResponse(FindowResponse.INVALID_ID);
		}
		return result;
	}

	@Path("/{id}")
	@DELETE
	@Secured
	@Produces("application/json")
	public String deleteAccount(@PathParam("id") String id) {
		RestUtils.addHeader(response);
		String result = "";

		log.info("deleteAccount " + id);

		try {
			Principal principal = securityContext.getUserPrincipal();
			String jwsUser = principal.getName();
			FinUser user = PersistanceManager.getInstance().getUserByName(jwsUser);

			long accountId = Long.parseLong(id);

			FinAccount account = PersistanceManager.getInstance().getAccount(user, accountId);

			if (account != null) {
				BankingAPI bankingAPI = FindowSystem.getBankingAPI(user);
				if (bankingAPI.deleteAccount(user, account)) {
					user.removeAccount(account);
					user = PersistanceManager.getInstance().persist(user);
				}

				result = RestUtils.generateJsonResponse(FindowResponse.OK);
			} else {
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_UNKNOWN);
			}

		} catch (FinErrorHandler e) {
			result = e.getResponse();
		} catch (NumberFormatException nfe) {
			result = RestUtils.generateJsonResponse(FindowResponse.INVALID_ID);
		}
		return result;
	}

	@Path("/")
	@GET
	@Secured
	@Produces("application/json")
	public String getAccounts() {
		RestUtils.addHeader(response);
		String result = "";

		try {
			// User laden
			Principal principal = securityContext.getUserPrincipal();
			String jwsUser = principal.getName();
			FinUser user = PersistanceManager.getInstance().getUserByName(jwsUser);

			List<FinAccount> accounts = PersistanceManager.getInstance().getAccounts(user);
			
			FinAccountWrapper faw = new FinAccountWrapper(accounts);
			
			result = RestUtils.generateJsonResponse(faw, "accountInfo");
		} catch (Exception e) {
			result = RestUtils.generateJsonResponse(FindowResponse.UNKNOWN);
		}
		return result;
	}

	@Path("/")
	@POST
	@Secured
	@Produces("application/json")
	public String importAccount(@HeaderParam("bankId") String bankId, @HeaderParam("bankingUserId") String bankingUserId, @HeaderParam("bankingPin") String bankingPin) {
		RestUtils.addHeader(response);
		String result = "";

		log.info(String.format("importAccount %s %s", bankId, bankingUserId));

		try {
			// User laden
			Principal principal = securityContext.getUserPrincipal();
			String jwsUser = principal.getName();
			FinUser user = PersistanceManager.getInstance().getUserByName(jwsUser);

			BankingAPI bankingAPI = FindowSystem.getBankingAPI(user);
			bankingAPI.importAccount(user, bankId, bankingUserId, bankingPin);

			result = RestUtils.generateJsonResponse(FindowResponse.OK);

		} catch (FinErrorHandler e) {
			if (e.hasCallError("ENTITY_EXISTS")) {
				log.error("ENTITY_EXISTS");
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_ALREADY_EXISTS);
			} else if (e.hasCallError("UNKNOWN_ENTITY")) {
				log.error("UNKNOWN_ENTITY");
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_UNKNOWN);
			} else if (e.hasCallError("BANK_SERVER_REJECTION")) {
				log.error("BANK_SERVER_REJECTION");
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_IMPORT_REJECTED);
			} else if (e.hasCallError("ILLEGAL_FIELD_VALUE")) {
				log.error("ILLEGAL_FIELD_VALUE");
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_ILLEGAL_FIELD);
			} else {
				log.error("UNKNOWN: ");
				log.error("MSG: " + e.getMessage());
				log.error("CE: " + e.getErrors());
				log.error("RSP: " + e.getResponse());
				result = RestUtils.generateJsonResponse(FindowResponse.UNKNOWN);
			}
		}
		return result;
	}
}