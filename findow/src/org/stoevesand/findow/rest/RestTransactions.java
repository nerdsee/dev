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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.auth.Authenticator;
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinCategorySum;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinTransaction;
import org.stoevesand.findow.model.FinTransactionWrapper;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;

import io.swagger.annotations.Api;

@Path("/transactions")
@Api(value = "transactions")
public class RestTransactions {

	private Logger log = LoggerFactory.getLogger(RestTransactions.class);

	@Context
	private HttpServletResponse response;

	@Context
	SecurityContext securityContext;

	@Path("/")
	@GET
	@Secured
	@Produces("application/json")
	public String getTransactions(@HeaderParam("accountId") Long accountId, @HeaderParam("days") int days) {
		RestUtils.addHeader(response);
		String result = "";

		try {
			// User laden
			Principal principal = securityContext.getUserPrincipal();
			String jwsUser = principal.getName();
			FinUser user = PersistanceManager.getInstance().getUserByName(jwsUser);

			if (accountId != null) {
				// prüfen ob eine accountId mitgegeben wurde
				FinAccount account = user.getAccount(accountId);

				if (account != null) {
					// DataLoader.updateTransactions(userToken,
					// account.getSourceId(), days);

					List<FinTransaction> transactions = PersistanceManager.getInstance().getTx(user, accountId, days);

					FinTransactionWrapper wrapper = new FinTransactionWrapper(transactions, account);
					result = RestUtils.generateJsonResponse(wrapper, null);
				} else {
					log.error("Failed to getTransactions. Account not found (id): " + accountId);
					result = RestUtils.generateJsonResponse(FindowResponse.ACCOUNT_UNKNOWN);
				}
			} else {
				//ansonsten alle Transaktionen des users laden
				List<FinTransaction> transactions = PersistanceManager.getInstance().getTx(user, null, days);

				FinTransactionWrapper wrapper = new FinTransactionWrapper(transactions);
				result = RestUtils.generateJsonResponse(wrapper, null);
				
				//result = RestUtils.generateJsonResponse(transactions, "transactions");
			}
		} catch (FinErrorHandler e) {
			log.error("Failed to getTransactions");
			e.printErrors();
			result = e.getResponse();
		}
		return result;
	}

	@Path("/categorized")
	@GET
	@Secured
	@Produces("application/json")
	public String getTransactionsCat(@HeaderParam("accountId") Long accountId, @HeaderParam("days") int days) {
		RestUtils.addHeader(response);
		String result = "";

		try {
			// User laden
			Principal principal = securityContext.getUserPrincipal();
			String jwsUser = principal.getName();
			FinUser user = PersistanceManager.getInstance().getUserByName(jwsUser);

			List<FinCategorySum> cs = PersistanceManager.getInstance().getCategorySummary(user, accountId, days);
			result = RestUtils.generateJsonResponse(cs, "categorySummary");
		} catch (Exception e) {
			result = RestUtils.generateJsonResponse(FindowResponse.UNKNOWN);
		}
		return result;
	}

}