package org.stoevesand.findow.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.stoevesand.findow.auth.Authenticator;
import org.stoevesand.findow.loader.DataLoader;
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

	@Path("/")
	@GET
	@Produces("application/json")
	public String getTransactions(@HeaderParam("userToken") String userToken, @HeaderParam("accountId") long accountId, @HeaderParam("days") int days) {
		String result = "";

		try {
			// User laden
			User user = Authenticator.getUser(userToken);
			Account account = user.getAccount(accountId);

			if (account != null) {
				DataLoader.updateTransactions(userToken, account.getSourceId(), days);

				List<Transaction> transactions = PersistanceManager.getInstance().getTx(user, accountId, days);

				TransactionWrapper wrapper = new TransactionWrapper(transactions, account);

				// result = RestUtils.generateJsonResponse(transactions,
				// "transactions");
				result = RestUtils.generateJsonResponse(wrapper, null);
			} else {
				result = RestUtils.generateJsonResponse(Response.ACCOUNT_UNKNOWN);
			}
		} catch (ErrorHandler e) {
			result = e.getResponse();
		}
		return result;
	}

	@Path("/categorized")
	@GET
	@Produces("application/json")
	public String getTransactionsCat(@HeaderParam("userToken") String userToken, @HeaderParam("accountId") int accountId, @HeaderParam("days") int days) {
		String result = "";

		try {
			List<CategorySum> cs = PersistanceManager.getInstance().getCategorySummary();
			result = RestUtils.generateJsonResponse(cs, "categorySummary");
		} catch (Exception e) {
			result = RestUtils.generateJsonResponse(Response.UNKNOWN);
		}
		return result;
	}

}