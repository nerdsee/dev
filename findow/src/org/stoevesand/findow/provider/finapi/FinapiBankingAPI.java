package org.stoevesand.findow.provider.finapi;

import java.util.List;

import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinBank;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinToken;
import org.stoevesand.findow.model.FinTransactionList;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.provider.ApiUser;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.provider.finapi.model.BankConnection;

public class FinapiBankingAPI implements BankingAPI {

	@Override
	public String importAccount(FinUser user, int bankId, String bankingUserId, String bankingPin) throws FinErrorHandler {
		BankConnection connection = BankConnectionsService.importConnection(user.getToken(), bankId, bankingUserId, bankingPin);

		// Accounts laden
		List<FinAccount> accounts = AccountsService.searchAccounts(user.getToken(), connection);

		return "";
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
	public ApiUser createUser(String username, String password) throws FinErrorHandler {
		ApiUser apiUser = UsersService.createUser(TokenStore.getInstance().getClientToken(), null, null);
		return apiUser;
	}

	@Override
	public void deleteUser(String userToken) throws FinErrorHandler {
		UsersService.deleteUser(userToken);
	}

	@Override
	public List<FinBank> searchBanks(String search) {
		List<FinBank> banks = BanksService.searchBanks(TokenStore.getInstance().getClientToken(), search);
		return banks;
	}

	@Override
	public boolean deleteAccount(FinUser user, FinAccount account) throws FinErrorHandler {
		return AccountsService.deleteAccount(user.getToken(), account);
	}

	@Override
	public FinTransactionList searchTransactions(FinUser user, FinAccount account, int days) throws FinErrorHandler {
		FinTransactionList transactions = TransactionsService.searchTransactions(user.getToken(), account.getSourceId(), days);
		return transactions;
	}

	@Override
	public void refreshAccount(FinUser user, FinAccount account) throws FinErrorHandler {
		AccountsService.refreshAccount(user.getToken(), account);
	}

	@Override
	public void reloadAccountContent(FinUser user, FinAccount account) throws FinErrorHandler {
		BankConnectionsService.updateConnection(user.getToken(), account.getBankConnectionId(), null);
	}

	@Override
	public String getClientId() {
		return null;
	}

	@Override
	public String getClientSecret() {
		return null;
	}

	@Override
	public FinToken requestUserToken(String username, String password) throws FinErrorHandler {
		FinapiTokenService.requestUserToken(TokenStore.getInstance().getClientToken(), username, password);
		return null;
	}

	@Override
	public List<FinAccount> getAccounts(FinUser user) throws FinErrorHandler {
		// TODO Auto-generated method stub
		return null;
	}

}
