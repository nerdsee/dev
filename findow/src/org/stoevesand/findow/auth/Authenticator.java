package org.stoevesand.findow.auth;

import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.finapi.UsersService;
import org.stoevesand.findow.provider.finapi.model.FinapiUser;

public class Authenticator {

	public static FinUser getUser(String userToken) throws FinErrorHandler {
		// User laden
		FinapiUser finapiUser;
		finapiUser = UsersService.getUser(userToken);
		FinUser user = PersistanceManager.getInstance().getUserByExternalName(finapiUser.getId());
		return user;
	}

}
