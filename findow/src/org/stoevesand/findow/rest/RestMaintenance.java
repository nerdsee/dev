package org.stoevesand.findow.rest;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.stoevesand.finapi.AccountsService;
import org.stoevesand.finapi.BankConnectionsService;
import org.stoevesand.finapi.model.BankConnection;
import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.User;
import org.stoevesand.findow.persistence.PersistanceManager;

import io.swagger.annotations.Api;

@Path("/mtc/users")
@Api(value = "maintenance")
public class RestMaintenance {

	@Context
	private HttpServletResponse response;

	@Path("/{id}")
	@PUT
	@Produces("application/json")
	public String checkUser(@PathParam("id") String id, @HeaderParam("userToken") String userToken) {
		response.addHeader("Access-Control-Allow-Origin", "*");

		try {
			User user = PersistanceManager.getInstance().getUserByName(id);
			if (user != null) {

				List<BankConnection> conns = BankConnectionsService.getBankConnections(userToken);
				for (BankConnection conn : conns) {
					List<Account> accounts = AccountsService.searchAccounts(userToken, conn.getId());
					PersistanceManager.getInstance().checkAccounts(user, accounts);
				}
			} else {
				return RestUtils.generateJsonResponse(Response.USER_UNKNOWN);
			}
		} catch (ErrorHandler e) {
			System.out.println(e);
			return e.getResponse();
		}
		return RestUtils.generateJsonResponse(Response.OK);
	}

}