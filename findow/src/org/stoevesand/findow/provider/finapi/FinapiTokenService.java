package org.stoevesand.findow.provider.finapi;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinToken;

public class FinapiTokenService {

	private static Logger log = LoggerFactory.getLogger(FinapiTokenService.class);

	static final String POST_URL = "https://sandbox.finapi.io/oauth/token";
	static final long VALIDITY_BUFFER_SECONDS = 200;

	public static FinToken requestUserToken(FinToken clientToken, String username, String password) throws FinErrorHandler {

		FinToken user_token = null;
		
		Client client = ClientBuilder.newClient();

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		formData.add("client_id", clientToken.getId());
		formData.add("client_secret", clientToken.getSecret());
		formData.add("username", username);
		formData.add("password", password);
		formData.add("grant_type", "password");

		WebTarget webTarget = client.target(POST_URL);
		//webTarget = webTarget.queryParam("access_token", userToken.getToken());
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.post(Entity.form(formData), Response.class);
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(output);
			log.error("requestUserToken failed: " + status);
			eh.printErrors();
			throw eh;
		}

		try {
			JSONObject jo = new JSONObject(output);
			user_token = new FinToken(username, password, jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return user_token;

	}

	public static FinToken requestClientToken(String client_id, String client_secret) throws FinErrorHandler {

		FinToken client_token = null;

		Client client = ClientBuilder.newClient();
		
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		formData.add("client_id", client_id);
		formData.add("client_secret", client_secret);
		formData.add("grant_type", "client_credentials");

		WebTarget webTarget = client.target(POST_URL);
		//webTarget = webTarget.queryParam("access_token", userToken.getToken());
		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.post(Entity.form(formData), Response.class);
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			FinErrorHandler eh = new FinErrorHandler(output);
			log.error("requestClientToken failed: " + status);
			eh.printErrors();
			throw eh;
		}

		try {
			JSONObject jo = new JSONObject(output);
			client_token = new FinToken(client_id, client_secret, jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return client_token;

	}
}
