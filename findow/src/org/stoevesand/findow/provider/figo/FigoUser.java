package org.stoevesand.findow.provider.figo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.Token;
import org.stoevesand.findow.provider.ApiUser;
import org.stoevesand.findow.provider.finapi.FinapiTokenService;

public class FigoUser implements ApiUser {
	String id = "";

	public String getId() {
		return id;
	}

	String password = "";

	public FigoUser(JSONObject json_user) {
		try {
			id = json_user.getString("id");
			password = json_user.getString("password");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FigoUser(String id, String password) {
		this.id = id;
		this.password = password;
	}

	public String toString() {
		return String.format("\"%s\", \"%s\"", id, password);
	}

	public Token getToken(Token clientToken) throws ErrorHandler {
		return FinapiTokenService.requestUserToken(clientToken, id, password);
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String getApi() {
		return "FIGO";
	}

}