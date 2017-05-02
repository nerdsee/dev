package org.stoevesand.finapi;

import java.util.List;

import org.stoevesand.finapi.model.BankConnection;
import org.stoevesand.findow.bankingapi.ApiUser;
import org.stoevesand.findow.bankingapi.BankingAPI;
import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.Bank;
import org.stoevesand.findow.model.ErrorHandler;

public class FinapiBankingAPI implements BankingAPI {

	@Override
	public List<Account> importAccount(String userToken, int bankId, String bankingUserId, String bankingPin) throws ErrorHandler {
		BankConnection connection = BankConnectionsService.importConnection(userToken, bankId, bankingUserId, bankingPin);

		// Accounts laden
		List<Account> accounts = AccountsService.searchAccounts(userToken, connection);

		return accounts;
	}

	private static FinapiBankingAPI _instance = null;

	private FinapiBankingAPI() {
	}

	public static BankingAPI getInstance() {
		if (_instance == null) {
			_instance = new FinapiBankingAPI();
		}
		return _instance;
	}

	@Override
	public ApiUser createUser(String username, String password) throws ErrorHandler {
		ApiUser apiUser = UsersService.createUser(TokenStore.getInstance().getClientToken(), null, null);
		return apiUser;
	}

	@Override
	public void deleteUser(String userToken) throws ErrorHandler {
		UsersService.deleteUser(userToken);
	}

	@Override
	public List<Bank> searchBanks(String search) {
		List<Bank> banks = BanksService.searchBanks(TokenStore.getInstance().getClientToken(), search);
		return banks;
	}

	@Override
	public void deleteAccount(String userToken, Account account) throws ErrorHandler {
		AccountsService.deleteAccount(userToken, account);
	}

}
