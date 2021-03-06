package org.stoevesand.findow.provider.finapi;

import java.util.List;
import java.util.Vector;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.provider.finapi.model.BankConnection;

public class AccountsService {

	private static Logger log = LoggerFactory.getLogger(AccountsService.class);

	static final String URL = "https://sandbox.finapi.io/api/v1/accounts";

	public static List<FinAccount> searchAccounts(String userToken, BankConnection connection) throws FinErrorHandler {

		Vector<FinAccount> accounts = new Vector<FinAccount>();

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL);

		webTarget = webTarget.queryParam("access_token", userToken);
		if (connection.getId() > 0) {
			webTarget = webTarget.queryParam("bankConnectionIds", "" + connection.getId());
		}
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.get();
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(output);
			throw eh;
		}

		try {
			JSONObject jo = new JSONObject(output);
			JSONArray json_accounts = jo.getJSONArray("accounts");

			for (int i = 0; i < json_accounts.length(); i++) {
				JSONObject json_account = json_accounts.getJSONObject(i);
				FinAccount account = new FinAccount(json_account);
				account.setBank(connection.getBank());
				log.info("Set Bank at Account: " + connection.getBank());
				log.info("New Account: " + account);
				accounts.add(account);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return accounts;

	}

	public static void refreshAccount(String userToken, FinAccount account) throws FinErrorHandler {

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL + "/" + account.getSourceId());
		webTarget = webTarget.queryParam("access_token", userToken);
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.get();
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(status, output);
			throw eh;
		}

		try {
			JSONObject jo = new JSONObject(output);
			account.update(jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static boolean deleteAccount(String userToken, FinAccount account) throws FinErrorHandler {

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL + "/" + account.getSourceId());
		webTarget = webTarget.queryParam("access_token", userToken);
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.delete();
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if ((status != 200) && (status != 404)) {
			FinErrorHandler eh = new FinErrorHandler(status, output);
			throw eh;
		}
		return true;
	}

}
