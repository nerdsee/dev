package org.stoevesand.findow.rest;

import java.security.Principal;
import java.util.List;
import java.util.Vector;

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
import org.stoevesand.findow.auth.Authenticator;
import org.stoevesand.findow.jobs.JobManager;
import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.User;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.provider.finapi.AccountsService;
import org.stoevesand.findow.server.FindowSystem;

import io.swagger.annotations.Api;

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
	public String getAccount(@PathParam("id") String id) {
		RestUtils.addHeader(response);
		String result = "";
		log.info("getAccount " + id);

		try {
			Principal principal = securityContext.getUserPrincipal();
			String jwsUser = principal.getName();
			User user = PersistanceManager.getInstance().getUserByName(jwsUser);
			String userToken = user.getToken();

			long accountId = Long.parseLong(id);

			Account account = PersistanceManager.getInstance().getAccount(user, accountId, userToken);
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
			User user = PersistanceManager.getInstance().getUserByName(jwsUser);
			String userToken = user.getToken();

			long accountId = Long.parseLong(id);

			Account account = PersistanceManager.getInstance().getAccount(user, accountId, userToken);

			if (account != null) {
				BankingAPI bankingAPI = FindowSystem.getBankingAPI();
				bankingAPI.deleteAccount(userToken, account);

				PersistanceManager.getInstance().deleteAccount(user, accountId, userToken);

				result = RestUtils.generateJsonResponse(FindowResponse.OK);
			} else {
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_UNKNOWN);
			}

		} catch (ErrorHandler e) {
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
			User user = PersistanceManager.getInstance().getUserByName(jwsUser);
			String userToken = user.getToken();

			List<Account> accounts = PersistanceManager.getInstance().getAccounts(user, userToken);
			result = RestUtils.generateJsonResponse(accounts, "accounts");
		} catch (Exception e) {
			result = RestUtils.generateJsonResponse(FindowResponse.UNKNOWN);
		}
		return result;
	}

	@Path("/")
	@POST
	@Secured
	@Produces("application/json")
	public String importAccount(@HeaderParam("bankId") int bankId, @HeaderParam("bankingUserId") String bankingUserId, @HeaderParam("bankingPin") String bankingPin) {
		RestUtils.addHeader(response);
		String result = "";

		log.info(String.format("importAccount %d %s", bankId, bankingUserId));

		try {
			// User laden
			Principal principal = securityContext.getUserPrincipal();
			String jwsUser = principal.getName();
			User user = PersistanceManager.getInstance().getUserByName(jwsUser);
			String userToken = user.getToken();

			BankingAPI bankingAPI = FindowSystem.getBankingAPI();
			List<Account> accounts = bankingAPI.importAccount(userToken, bankId, bankingUserId, bankingPin);
			List<Account> nas = new Vector<Account>();

			user.addAccounts(accounts);
			for (Account acc : accounts) {
				Account na = PersistanceManager.getInstance().persist(acc);
				nas.add(na);
				JobManager.getInstance().addImportAccountJob(na);
			}

			result = RestUtils.generateJsonResponse(nas, "accounts");

		} catch (ErrorHandler e) {
			if (e.hasCallError("ENTITY_EXISTS")) {
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_ALREADY_EXISTS);
			} else if (e.hasCallError("UNKNOWN_ENTITY")) {
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_UNKNOWN);
			} else if (e.hasCallError("BANK_SERVER_REJECTION")) {
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_IMPORT_REJECTED);
			} else if (e.hasCallError("ILLEGAL_FIELD_VALUE")) {
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_ILLEGAL_FIELD);
			} else {
				result = RestUtils.generateJsonResponse(FindowResponse.UNKNOWN);
			}
		}
		// System.out.println("BC: " + connection);
		return result;
	}
}