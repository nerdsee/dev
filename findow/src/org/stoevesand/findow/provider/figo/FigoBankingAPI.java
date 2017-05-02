package org.stoevesand.findow.provider.figo;

import java.util.List;

import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.Bank;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.provider.ApiUser;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.provider.finapi.model.BankConnection;

public class FigoBankingAPI implements BankingAPI {

	@Override
	public List<Account> importAccount(String userToken, int bankId, String bankingUserId, String bankingPin) throws ErrorHandler {
		return null;
	}

	private static FigoBankingAPI _instance = null;

	private FigoBankingAPI() {
	}

	public static BankingAPI getInstance() {
		if (_instance == null) {
			_instance = new FigoBankingAPI();
		}
		return _instance;
	}

	@Override
	public ApiUser createUser(String username, String password) throws ErrorHandler {
		return null;
	}

	@Override
	public void deleteUser(String userToken) throws ErrorHandler {
	}

	@Override
	public List<Bank> searchBanks(String search) {
		return null;
	}

	@Override
	public void deleteAccount(String userToken, Account account) throws ErrorHandler {
	}

}
