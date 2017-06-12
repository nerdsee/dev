package org.stoevesand.findow.provider.figo;

import org.stoevesand.findow.provider.ApiUser;

public class FigoUser implements ApiUser {
	String id = "";

	public String getId() {
		return id;
	}

	String password = "";
	private String recoveryPassword;
	private String email;

	public FigoUser(String id, String password, String recoveryPassword) {
			this.id = id;
			this.password = password;
			this.recoveryPassword = recoveryPassword;
	}

	public String toString() {
		return String.format("\"%s\", \"%s\"", id, password);
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String getApi() {
		return "FIGO";
	}

	@Override
	public String getRecoveryPassword() {
		return recoveryPassword;
	}

}
