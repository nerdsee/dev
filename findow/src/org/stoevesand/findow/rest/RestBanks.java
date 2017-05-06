package org.stoevesand.findow.rest;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.jobs.JobManager;
import org.stoevesand.findow.model.Bank;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.server.FindowSystem;

import io.swagger.annotations.Api;

@Path("/banks")
@Api(value = "banks")
public class RestBanks {

	private Logger log = LoggerFactory.getLogger(RestBanks.class);

	@Context
	SecurityContext securityContext;

	@Context
	private HttpServletResponse response;

	@Path("/{search}")
	@GET
	@Secured
	@Produces("application/json")
	public String getBank(@PathParam("search") String search) {

		Principal principal = securityContext.getUserPrincipal();
		String username = principal.getName();

		log.info("getBank: " + search + " (for user " + username + ")");
		JobManager.getInstance();
		RestUtils.addHeader(response);
		// BankingAPI api = FindowSystem.getBankingAPI("FIGO");
		BankingAPI api = FindowSystem.getBankingAPI("FINAPI");
		List<Bank> banks = api.searchBanks(search);
		String result = RestUtils.generateJsonResponse(banks, "banks");
		return result;
	}

}