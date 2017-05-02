package org.stoevesand.findow.auth;

import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.User;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.finapi.UsersService;
import org.stoevesand.findow.provider.finapi.model.FinapiUser;

public class Authenticator {

	public static User getUser(String userToken) throws ErrorHandler {
		// User laden
		FinapiUser finapiUser;
		finapiUser = UsersService.getUser(userToken);
		User user = PersistanceManager.getInstance().getUserByExternalName(finapiUser.getId());
		return user;
	}

}
