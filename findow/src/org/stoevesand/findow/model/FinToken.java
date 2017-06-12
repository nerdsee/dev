package org.stoevesand.findow.model;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRootName;

import me.figo.internal.TokenResponse;

@JsonRootName(value = "token")
public class FinToken {

	static final long VALIDITY_BUFFER_SECONDS = 200;

	String access_token = "";
	String token_type = "";
	String expires_in = "";
	String id = "";
	String secret = "";

	long valid_until = 0;

	public FinToken(String id, String secret, JSONObject json_token) {
		try {
			access_token = json_token.getString("access_token");
			token_type = json_token.getString("token_type");
			expires_in = json_token.getString("expires_in");
			valid_until = System.currentTimeMillis() + (Long.parseLong(expires_in) * 1000);

			this.id = id;
			this.secret = secret;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FinToken(TokenResponse t) {
		this.access_token = t.getAccessToken();
		this.token_type = "FIGO_TOKEN";
		this.expires_in = t.getExpiresIn().toString();
		valid_until = System.currentTimeMillis() + (t.getExpiresIn() * 1000);
	}

	public String getId() {
		return id;
	}

	public String getSecret() {
		return secret;
	}

	@JsonGetter
	public String getToken() {
		return access_token;
	}

	public boolean isValid() {
		long now = System.currentTimeMillis();
		return (now + VALIDITY_BUFFER_SECONDS) < valid_until;
	}

	public String toString() {
		return access_token;
	}

}
