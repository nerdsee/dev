package org.stoevesand.findow.provider;

import java.util.List;

import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinBank;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinToken;
import org.stoevesand.findow.model.FinTransactionList;
import org.stoevesand.findow.model.FinUser;

public interface BankingAPI {

	public String importAccount(FinUser user, int bankId, String bankingUserId, String bankingPin) throws FinErrorHandler;

	public ApiUser createUser(String username, String password) throws FinErrorHandler;

	public void deleteUser(String userToken) throws FinErrorHandler;

	public List<FinBank> searchBanks(String search);

	public boolean deleteAccount(FinUser user, FinAccount account) throws FinErrorHandler;

	public FinTransactionList searchTransactions(FinUser user, FinAccount account, int days) throws FinErrorHandler;

	public void refreshAccount(FinUser user, FinAccount account) throws FinErrorHandler;

	public void reloadAccountContent(FinUser user, FinAccount account) throws FinErrorHandler;

	public String getClientId();

	public String getClientSecret();

	FinToken requestUserToken(String username, String password) throws FinErrorHandler;

	List<FinAccount> getAccounts(FinUser user) throws FinErrorHandler;
}
