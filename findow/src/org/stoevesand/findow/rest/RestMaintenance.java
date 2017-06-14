package org.stoevesand.findow.rest;

import java.io.IOException;
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
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.finapi.AccountsService;
import org.stoevesand.findow.provider.finapi.BankConnectionsService;
import org.stoevesand.findow.provider.finapi.model.BankConnection;
import org.stoevesand.findow.rest.figo.Bank;

import io.swagger.annotations.Api;
import me.figo.FigoException;
import me.figo.FigoSession;
import me.figo.models.Service;

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
			FinUser user = PersistanceManager.getInstance().getUserByName(id);
			if (user != null) {
				userToken = user.getToken();
				List<BankConnection> conns = BankConnectionsService.getBankConnections(userToken);
				for (BankConnection conn : conns) {
					List<FinAccount> accounts = AccountsService.searchAccounts(userToken, conn);
					PersistanceManager.getInstance().checkAccounts(user, accounts);
				}
			} else {
				return RestUtils.generateJsonResponse(FindowResponse.USER_UNKNOWN);
			}
		} catch (FinErrorHandler e) {
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

	@Path("/readservices")
	@GET
	@Produces("application/json")
	public String readServices() {
		String result = null;
		FinUser user = PersistanceManager.getInstance().getUser(1);
		FigoSession fs = new FigoSession(user.getToken());
		try {
			List<Service> services = fs.getSupportedServices();
			for (Service service : services) {
				log.info(String.format("Service: %s %s %s", service.getName(), service.getBankCode(), service.getIcon()));
			}
			result = RestUtils.generateJsonResponse(services, "services");
		} catch (FigoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (result != null) {
			return result;
		} else {
			return RestUtils.generateJsonResponse(FindowResponse.UNKNOWN);
		}
	}

	@Path("/readbanks")
	@GET
	@Produces("application/json")
	public String readBanks() {
		FinUser user = PersistanceManager.getInstance().getUser(1);
		FigoSession fs = new FigoSession(user.getToken());
		try {
			List<Bank> banks = getSupportedBanks(fs);
			for (Bank bank : banks) {
				log.info(String.format("Service: %s %s", bank.getBankName(), bank.getBankCode()));
			}
		} catch (FigoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return RestUtils.generateJsonResponse(FindowResponse.OK);
	}

	// FAKE Endpoints, weil die Funktion im SDK fehlt

	/**
	 * Returns a list of all supported credit cards and payment services for all
	 * countries
	 * 
	 * @param fs
	 * @return List of Services
	 * @exception FigoException
	 *                Base class for all figoExceptions
	 * @exception IOException
	 *                IOException
	 */
	public List<Bank> getSupportedBanks(FigoSession fs) throws FigoException, IOException {
		Bank.BankResponse response = fs.queryApi("/rest/catalog/banks", null, "GET", Bank.BankResponse.class);
		return response == null ? null : response.getBanks();
	}
}