package org.stoevesand.findow.rest;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.stoevesand.findow.bankingapi.BankingAPI;
import org.stoevesand.findow.model.Bank;
import org.stoevesand.findow.server.FindowSystem;

import io.swagger.annotations.Api;

@Path("/banks")
@Api(value = "banks")
public class RestBanks {

	@Context
	private HttpServletResponse response;

	@Path("/{search}")
	@GET
	@Produces("application/json")
	public String getBank(@PathParam("search") String search) {
		RestUtils.addHeader(response);
		BankingAPI api = FindowSystem.getBankingAPI();
		List<Bank> banks = api.searchBanks(search);
		String result = RestUtils.generateJsonResponse(banks, "banks");
		return result;
	}

}