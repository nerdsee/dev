package org.stoevesand.findow.provider.finapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinToken;

public class TokenStore {

	private Logger log = LoggerFactory.getLogger(TokenStore.class);

	static final String client_id = "7fbb36a6-e886-41fc-9a0a-3ead413cddb8";
	static final String client_secret = "3122d123-fdeb-498c-93c4-5eda3c10d396";
	static final String data_decryption_key = "e0c82a81c6886460f109fe5348c58884";

	// finAPI SANDBOX Admin-Client:
	static final String admin_client_id = "05e712de-dbb9-4b31-9889-ead3c151c54f";
	static final String admin_client_secret = "f3e251db-be41-46cc-a438-ecd55e4a7abc";
	static final String admin_data_decryption_key = "eeb4561adf992fc44313468e4035ccac";

	private FinToken clientToken;
	private FinToken adminToken;

	private TokenStore() {
		try {
			clientToken = FinapiTokenService.requestClientToken(client_id, client_secret);
			adminToken = FinapiTokenService.requestClientToken(admin_client_id, admin_client_secret);
		} catch (FinErrorHandler e) {
			e.printStackTrace();
		}
	}

	private static TokenStore _instance = null;

	public static TokenStore getInstance() {
		if (_instance == null) {
			_instance = new TokenStore();
		}
		return _instance;
	}

	public void validateClientToken() {
		if ((clientToken == null) || (!clientToken.isValid())) {
			log.info("Refresh Token.");
			try {
				clientToken = FinapiTokenService.requestClientToken(client_id, client_secret);
			} catch (FinErrorHandler e) {
				e.printStackTrace();
			}
		} else {
			log.info("Token still valid.");
		}

	}

	public void validateAdminToken() {
		if (!adminToken.isValid()) {
			log.info("Refresh AdminToken.");
			try {
				adminToken = FinapiTokenService.requestClientToken(admin_client_id, admin_client_secret);
			} catch (FinErrorHandler e) {
				e.printStackTrace();
			}
		} else {
			log.info("AdminToken still valid.");
		}

	}

	public FinToken getClientToken() {
		validateClientToken();
		return clientToken;
	}

	public FinToken getAdminToken() {
		validateAdminToken();
		return adminToken;
	}
}