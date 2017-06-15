package org.stoevesand.findow.provider.figo;

import java.util.List;
import java.util.Vector;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinBank;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinToken;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.server.FindowSystem;

public class FigoBanksLoader {

	static final String URL = "https://api.figo.me/rest/catalog/banks/de";

	private static Logger log = LoggerFactory.getLogger(FigoBanksLoader.class);

	public static List<FinBank> loadBanks(FinUser user) {

		Vector<FinBank> banks = new Vector<FinBank>();

		Client client = ClientBuilder.newClient();

		String username = FigoBankingAPI.getInstance().getClientId();
		String password = FigoBankingAPI.getInstance().getClientSecret();

		String usernameAndPassword = username + ":" + password;
		String authorizationHeaderName = "Authorization";
		String authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());

		WebTarget webTarget = client.target(URL);
		webTarget = webTarget.queryParam("access_token", user.getToken());
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken());
		//invocationBuilder.accept("application/json").header(authorizationHeaderName, authorizationHeaderValue);
		Response response = invocationBuilder.get();
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(output);
			log.error("searchBanks failed: " + status);
			eh.printErrors();
			return null;
		}

		try {
			JSONObject jo = new JSONObject(output);
			JSONArray json_banks = jo.getJSONArray("banks");

			for (int i = 0; i < json_banks.length(); i++) {
				JSONObject json_bank = json_banks.getJSONObject(i);
				FinBank bank = new FinBank(json_bank);
				banks.add(bank);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return banks;

	}

	public static FinBank getBank(FinToken clientToken, int bankId) {

		FinBank bank = null;

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL + "/" + bankId);
		webTarget = webTarget.queryParam("access_token", clientToken.getToken());
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.get();
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(output);
			log.error("getBank failed: " + status);
			eh.printErrors();
			return null;
		}

		try {
			JSONObject jo = new JSONObject(output);
			bank = new FinBank(jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return bank;

	}

}
