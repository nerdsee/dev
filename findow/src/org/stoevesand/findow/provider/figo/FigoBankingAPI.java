package org.stoevesand.findow.provider.figo;

import java.util.List;

import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.TransactionList;
import org.stoevesand.findow.model.User;
import org.stoevesand.findow.provider.ApiUser;
import org.stoevesand.findow.provider.BankingAPI;

import me.figo.FigoSession;
import me.figo.models.Account;
import me.figo.models.Transaction;

public class FigoBankingAPI implements BankingAPI {

	@Override
	public List<org.stoevesand.findow.model.Account> importAccount(String userToken, int bankId, String bankingUserId, String bankingPin) throws ErrorHandler {
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
	public List<org.stoevesand.findow.model.Bank> searchBanks(String search) {
		FigoSession session = new FigoSession("ASHWLIkouP2O6_bgA2wWReRhletgWKHYjLqDaqb0LFfamim9RjexTo22ujRIP_cjLiRiSyQXyt2kM1eXU2XLFZQ0Hro15HikJQT_eNeT_9XQ");

		try {
		// print out a list of accounts including its balance
		for (Account account : session.getAccounts()) {
			System.out.println(account.getName());
			System.out.println(session.getAccountBalance(account).getBalance());
		}

		// print out the list of all transactions on a specific account
		for (Transaction transaction : session.getTransactions(session.getAccount("A1.2"))) {
			System.out.println(transaction.getPurposeText());
		}
		} catch (Exception e) {
			
		}
		return null;
	}

	@Override
	public void deleteAccount(String userToken, org.stoevesand.findow.model.Account account) throws ErrorHandler {
	}

	@Override
	public TransactionList searchTransactions(User user, org.stoevesand.findow.model.Account account, int days) throws ErrorHandler {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshAccount(User user, org.stoevesand.findow.model.Account account) throws ErrorHandler {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reloadAccountContent(User user, org.stoevesand.findow.model.Account account) throws ErrorHandler {
		// TODO Auto-generated method stub
		
	}

}
