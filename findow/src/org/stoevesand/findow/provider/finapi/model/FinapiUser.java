package org.stoevesand.findow.provider.finapi.model;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinToken;
import org.stoevesand.findow.provider.ApiUser;
import org.stoevesand.findow.provider.finapi.FinapiTokenService;

public class FinapiUser implements ApiUser {
	String id = "";

	public String getId() {
		return id;
	}

	String password = "";

	public FinapiUser(JSONObject json_user) {
		try {
			id = json_user.getString("id");
			password = json_user.getString("password");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FinapiUser(String id, String password) {
		this.id = id;
		this.password = password;
	}

	public String toString() {
		return String.format("\"%s\", \"%s\"", id, password);
	}

	public FinToken getToken(FinToken clientToken) throws FinErrorHandler {
		return FinapiTokenService.requestUserToken(clientToken, id, password);
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String getApi() {
		return "FINAPI";
	}

	@Override
	public String getRecoveryPassword() {
		return null;
	}

}
