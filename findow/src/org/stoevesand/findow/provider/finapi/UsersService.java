package org.stoevesand.findow.provider.finapi;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.Token;
import org.stoevesand.findow.provider.figo.FigoTokenService;
import org.stoevesand.findow.provider.finapi.model.FinapiUser;

public class UsersService {

	private static Logger log = LoggerFactory.getLogger(UsersService.class);

	static final String URL = "https://sandbox.finapi.io/api/v1/users";

	public static FinapiUser getUser(String userToken) throws ErrorHandler {

		FinapiUser user = null;

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL);
		webTarget = webTarget.queryParam("access_token", userToken);
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.get();
		String output = response.readEntity(String.class);
		
		int status = response.getStatus();
		if (status != 200) {
			ErrorHandler eh = new ErrorHandler(output);
			log.error("getBankConnections failed: " + status);
			eh.printErrors();
			throw eh;
		}

		try {
			JSONObject json_user = new JSONObject(output);
			user = new FinapiUser(json_user);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return user;

	}

	public static void deleteUser(String userToken) throws ErrorHandler {
		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL);
		webTarget = webTarget.queryParam("access_token", userToken);
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.delete();
		String output = response.readEntity(String.class);
		
		int status = response.getStatus();
		if (status != 200) {
			ErrorHandler eh = new ErrorHandler(output);
			log.error("delete user failed: " + status);
			eh.printErrors();
			throw eh;
		}
	}

	public static FinapiUser createUser(Token clientToken, String id, String password) throws ErrorHandler {
		FinapiUser user = null;

		Client client = ClientBuilder.newClient();

		String message = generateCreateUserMessage(id, password);
		
		WebTarget webTarget = client.target(URL);
		webTarget = webTarget.queryParam("access_token", clientToken.getToken());
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder = invocationBuilder.accept("application/json");
		Response response = invocationBuilder.post(Entity.json(message), Response.class);
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 201) {
			ErrorHandler eh = new ErrorHandler(output);
			log.error("createUser failed: " + status);
			eh.printErrors();
			throw eh;
		}

		try {
			JSONObject json_user = new JSONObject(output);
			user = new FinapiUser(json_user);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return user;
	}

	private static String generateCreateUserMessage(String id, String password) {

		String ret = "";

		try {
			JSONObject jo = new JSONObject();

			if (id != null) {
				jo.put("id", id);
			}

			if (password != null) {
				jo.put("password", password);
			}

			ret = jo.toString();
			log.info("IDS: " + ret);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ret;
	}

}
