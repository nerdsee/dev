package org.stoevesand.findow.provider.finapi;

import java.util.List;
import java.util.Vector;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinToken;
import org.stoevesand.findow.provider.finapi.model.BankConnection;

public class BankConnectionsService {

	private static Logger log = LoggerFactory.getLogger(BankConnectionsService.class);

	static final String URL = "https://sandbox.finapi.io/api/v1/bankConnections";

	public static BankConnection importConnection(String userToken, int bankId, String bankingUserId, String bankingPin) throws FinErrorHandler {
		BankConnection bc = null;

		Client client = ClientBuilder.newClient();

		String message = generateImportConnectionMessage(bankId, bankingUserId, bankingPin);

		WebTarget webTarget = client.target(URL + "/import");
		webTarget = webTarget.queryParam("access_token", userToken);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder = invocationBuilder.accept("application/json");
		Response response = invocationBuilder.post(Entity.json(message), Response.class);
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status == 201) {
			log.error("Successfully imported account. Status:" + status);
			try {
				JSONObject jo = new JSONObject(output);
				bc = new BankConnection(jo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return bc;
		} else {
			log.error("Failed to import account. Status:" + status);
			FinErrorHandler eh = new FinErrorHandler(status, output);
			throw eh;
		}
	}

	public static boolean deleteConnection(FinToken userToken, int id) throws FinErrorHandler {

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL + "/" + id);
		webTarget = webTarget.queryParam("access_token", userToken.getToken());
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.delete();
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(output);
			log.error("getBankConnections failed: " + status);
			throw eh;
		}

		return true;

	}

	public static List<BankConnection> getBankConnections(String userToken) throws FinErrorHandler {

		Vector<BankConnection> connections = new Vector<BankConnection>();

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL);
		webTarget = webTarget.queryParam("access_token", userToken);
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.get();
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(output);
			log.error("getBankConnections failed: " + status);
			eh.printErrors();
			throw eh;
		}

		try {
			JSONObject jo = new JSONObject(output);
			JSONArray json_txs = jo.getJSONArray("connections");

			for (int i = 0; i < json_txs.length(); i++) {
				JSONObject json_account = json_txs.getJSONObject(i);
				BankConnection connection = new BankConnection(json_account);
				connections.add(connection);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return connections;

	}

	/**
	 * @param userToken
	 * @param id
	 * @param bankConnection
	 *            Wenn eine Connection übergeben wird, wird sie aktualisiert.
	 *            Ansonsten wird eine neues Connection Objekt erzeugt
	 * @return
	 */
	public static BankConnection getBankConnection(FinToken userToken, int id, BankConnection bankConnection) {

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL + "/" + id);
		webTarget = webTarget.queryParam("access_token", userToken.getToken());
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.get();
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(output);
			log.error("getBankConnection failed: " + status);
			eh.printErrors();
			return null;
		}

		try {
			JSONObject jo = new JSONObject(output);
			if (bankConnection == null) {
				bankConnection = new BankConnection(jo);
			} else {
				bankConnection.update(jo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return bankConnection;

	}

	public static String deleteBankConnection(String userToken, int connectionId) throws FinErrorHandler {
		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL + "/" + connectionId);
		webTarget = webTarget.queryParam("access_token", userToken);
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.delete();
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(output);
			log.error("getBankConnection failed: " + status);
			// throw eh;
		}

		return output;

	}

	private static String generateImportConnectionMessage(int bankId, String bankingUserId, String bankingPin) {

		String ret = "";

		try {
			JSONObject jo = new JSONObject();
			jo.put("bankId", bankId);
			jo.put("bankingUserId", bankingUserId);
			jo.put("bankingPin", bankingPin);
			jo.put("storePin", true);
			ret = jo.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ret;
	}

	public static BankConnection updateConnection(String userToken, int bankConnectionId, String connectionPin) throws FinErrorHandler {
		BankConnection bc = null;

		Client client = ClientBuilder.newClient();

		String message = generateUpdateConnectionMessage(bankConnectionId, connectionPin);

		WebTarget webTarget = client.target(URL + "/update");
		webTarget = webTarget.queryParam("access_token", userToken);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder = invocationBuilder.accept("application/json");
		Response response = invocationBuilder.post(Entity.json(message), Response.class);
		String output = response.readEntity(String.class);

		// Create Jersey client
		// WebResource webResourcePost = client.resource(URL + "/update");
		// ClientResponse response = webResourcePost.queryParam("access_token",
		// userToken.getToken()).accept("application/json").type("application/json").post(ClientResponse.class,
		// message);

		int status = response.getStatus();
		if (status != 200) {
			log.error("Failed to update connection. Status:" + status);
			FinErrorHandler eh = new FinErrorHandler(output);
			throw eh;
		}

		try {
			JSONObject jo = new JSONObject(output);
			bc = new BankConnection(jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return bc;
	}

	private static String generateUpdateConnectionMessage(int bankConnectionId, String bankingPin) {

		String ret = "";

		try {
			JSONObject jo = new JSONObject();
			jo.put("bankConnectionId", bankConnectionId);
			if (bankingPin != null) {
				jo.put("bankingPin", bankingPin);
			}
			ret = jo.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ret;
	}

}
