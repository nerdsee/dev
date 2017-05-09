package org.stoevesand.findow.rest;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.stoevesand.findow.auth.Authenticator;
import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.CategorySum;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.Transaction;
import org.stoevesand.findow.model.TransactionWrapper;
import org.stoevesand.findow.model.User;
import org.stoevesand.findow.persistence.PersistanceManager;

import io.swagger.annotations.Api;

@Path("/transactions")
@Api(value = "transactions")
public class RestTransactions {

	@Context
	private HttpServletResponse response;

	@Context
	SecurityContext securityContext;

	@Path("/")
	@GET
	@Secured
	@Produces("application/json")
	public String getTransactions(@HeaderParam("accountId") long accountId, @HeaderParam("days") int days) {
		RestUtils.addHeader(response);
		String result = "";

		try {
			// User laden
			Principal principal = securityContext.getUserPrincipal();
			String jwsUser = principal.getName();
			User user = PersistanceManager.getInstance().getUserByName(jwsUser);

			Account account = user.getAccount(accountId);

			if (account != null) {
				// DataLoader.updateTransactions(userToken, account.getSourceId(), days);

				List<Transaction> transactions = PersistanceManager.getInstance().getTx(user, accountId, days);

				TransactionWrapper wrapper = new TransactionWrapper(transactions, account);

				// result = RestUtils.generateJsonResponse(transactions,
				// "transactions");
				result = RestUtils.generateJsonResponse(wrapper, null);
			} else {
				result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_UNKNOWN);
			}
		} catch (ErrorHandler e) {
			result = e.getResponse();
		}
		return result;
	}

	@Path("/categorized")
	@GET
	@Secured
	@Produces("application/json")
	public String getTransactionsCat(@HeaderParam("accountId") int accountId, @HeaderParam("days") int days) {
		RestUtils.addHeader(response);
		String result = "";

		try {
			// User laden
			Principal principal = securityContext.getUserPrincipal();
			String jwsUser = principal.getName();
			User user = PersistanceManager.getInstance().getUserByName(jwsUser);

			List<CategorySum> cs = PersistanceManager.getInstance().getCategorySummary(user, accountId);
			result = RestUtils.generateJsonResponse(cs, "categorySummary");
		} catch (Exception e) {
			result = RestUtils.generateJsonResponse(FindowResponse.UNKNOWN);
		}
		return result;
	}

}