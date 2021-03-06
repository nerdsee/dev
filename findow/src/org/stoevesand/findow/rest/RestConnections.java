package org.stoevesand.findow.rest;

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
import org.stoevesand.findow.auth.Authenticator;
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.finapi.AccountsService;
import org.stoevesand.findow.provider.finapi.BankConnectionsService;
import org.stoevesand.findow.provider.finapi.UsersService;
import org.stoevesand.findow.provider.finapi.model.BankConnection;
import org.stoevesand.findow.provider.finapi.model.FinapiUser;

import io.swagger.annotations.Api;

@Path("/connections")
@Api(value = "connections")
@Deprecated
public class RestConnections {

	private Logger log = LoggerFactory.getLogger(RestConnections.class);

	@Context
	private HttpServletResponse response;

	@Context
	SecurityContext securityContext;

	@Path("/")
	@GET
	@Produces("application/json")
	@Deprecated
	public String listConnections(@HeaderParam("userToken") String userToken) {
		RestUtils.addHeader(response);
		String result = "";
		try {
			List<BankConnection> list = BankConnectionsService.getBankConnections(userToken);
			result = RestUtils.generateJsonResponse(list, "connections");
		} catch (FinErrorHandler e) {
			result = e.getResponse();
		}

		return result;
	}

	@Path("/{connectionId}")
	@DELETE
	@Produces("application/json")
	@Deprecated
	public String deleteConnection(@HeaderParam("userToken") String userToken, @PathParam("connectionId") int connectionId) {
		RestUtils.addHeader(response);
		String result = "";

		try {
			FinUser user = Authenticator.getUser(userToken);
			result = BankConnectionsService.deleteBankConnection(userToken, connectionId);
			PersistanceManager.getInstance().deleteAccounts(user, connectionId);
		} catch (FinErrorHandler e) {
			result = e.getResponse();
		}

		return result;
	}

	@Path("/")
	@POST
	@Produces("application/json")
	@Deprecated
	public String importConnection(@HeaderParam("userToken") String userToken, @HeaderParam("bankId") int bankId, @HeaderParam("bankingUserId") String bankingUserId, @HeaderParam("bankingPin") String bankingPin) {
		RestUtils.addHeader(response);
		String result = "";
		try {
			BankConnection connection = BankConnectionsService.importConnection(userToken, bankId, bankingUserId, bankingPin);
			result = RestUtils.generateJsonResponse(connection);
			// initial die Umsätze laden
			// DataLoader.updateTransactions(userToken, null, 7);

			// User laden
			FinapiUser finapiUser = UsersService.getUser(userToken);
			FinUser user = PersistanceManager.getInstance().getUserByExternalName(finapiUser.getId());

			// Accounts laden
			List<FinAccount> accounts = AccountsService.searchAccounts(userToken, connection);

			// Den aktuellen User zuweisen
			for (FinAccount account : accounts) {
				account.setUser(user);
			}

			// Accounts persistieren
			PersistanceManager.getInstance().storeAccounts(accounts);

		} catch (FinErrorHandler e) {
			result = e.getResponse();
		}
		return result;
	}

}