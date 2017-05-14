package org.stoevesand.findow.rest;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.hint.HintEngine;
import org.stoevesand.findow.jobs.JobManager;
import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.User;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.finapi.AccountsService;
import org.stoevesand.findow.provider.finapi.BankConnectionsService;
import org.stoevesand.findow.provider.finapi.MandatorAdminService;
import org.stoevesand.findow.provider.finapi.model.BankConnection;

import io.swagger.annotations.Api;

@Path("/mtc/users")
@Api(value = "maintenance")
public class RestMaintenance {

	private Logger log = LoggerFactory.getLogger(RestMaintenance.class);

	@Context
	private HttpServletResponse response;

	@Path("/{id}")
	@PUT
	@Produces("application/json")
	public String checkUser(@PathParam("id") String id, @HeaderParam("userToken") String userToken) {
		JobManager.getInstance();
		RestUtils.addHeader(response);

		try {
			User user = PersistanceManager.getInstance().getUserByName(id);
			if (user != null) {

				List<BankConnection> conns = BankConnectionsService.getBankConnections(userToken);
				for (BankConnection conn : conns) {
					List<Account> accounts = AccountsService.searchAccounts(userToken, conn);
					PersistanceManager.getInstance().checkAccounts(user, accounts);
				}
			} else {
				return RestUtils.generateJsonResponse(FindowResponse.USER_UNKNOWN);
			}
		} catch (ErrorHandler e) {
			log.error(e.toString());
			return e.getResponse();
		}
		return RestUtils.generateJsonResponse(FindowResponse.OK);
	}

	@Path("/refreshhints")
	@GET
	@Produces("application/json")
	public String refreshHints() {
		HintEngine.getInstance().refresh();
		return RestUtils.generateJsonResponse(FindowResponse.OK);
	}

}