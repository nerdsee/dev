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
import org.stoevesand.findow.provider.finapi.model.UserInfo;

public class MandatorAdminService {

	private static Logger log = LoggerFactory.getLogger(MandatorAdminService.class);

	static final String URL = "https://sandbox.finapi.io/api/v1/mandatorAdmin/getUserList";

	public static List<UserInfo> getUsers(FinToken adminToken) {

		Vector<UserInfo> users = new Vector<UserInfo>();

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL);
		webTarget = webTarget.queryParam("access_token", adminToken.getToken());
		webTarget = webTarget.queryParam("isDeleted", "false");
		webTarget = webTarget.queryParam("perPage", 200);
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
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
			JSONArray json_users = jo.getJSONArray("users");

			for (int i = 0; i < json_users.length(); i++) {
				JSONObject json_user = json_users.getJSONObject(i);
				UserInfo user = new UserInfo(json_user);
				users.add(user);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return users;

	}

	public static String deleteUser(FinToken adminToken, String id) throws FinErrorHandler {
		BankConnection bc = null;

		Client client = ClientBuilder.newClient();

		String message = generateDeleteUserMessage(id);

		WebTarget webTarget = client.target("https://sandbox.finapi.io/api/v1/mandatorAdmin/deleteUsers");
		webTarget = webTarget.queryParam("access_token", adminToken.getToken());
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder = invocationBuilder.accept("application/json");
		Response response = invocationBuilder.post(Entity.json(message), Response.class);
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(output);
			throw eh;
		}

		return output;
	}

	private static String generateDeleteUserMessage(String id) {

		String ret = "";

		try {
			JSONObject jo = new JSONObject();
			JSONArray ids = new JSONArray();

			ids.put(id);
			jo.put("userIds", ids);
			ret = jo.toString();
			log.info("IDS: " + ret);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ret;
	}
}
