package org.stoevesand.findow.provider;

import java.util.List;

import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.Bank;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.TransactionList;
import org.stoevesand.findow.model.User;

public interface BankingAPI {

	public List<Account> importAccount(String userToken, int bankId, String bankingUserId, String bankingPin) throws ErrorHandler;

	public ApiUser createUser(String username, String password) throws ErrorHandler;

	public void deleteUser(String userToken) throws ErrorHandler;

	public List<Bank> searchBanks(String search);

	public void deleteAccount(String userToken, Account account) throws ErrorHandler;

	public TransactionList searchTransactions(User user, Account account, int days) throws ErrorHandler;

	public void refreshAccount(User user, Account account) throws ErrorHandler;

	public void reloadAccountContent(User user, Account account) throws ErrorHandler;

}
