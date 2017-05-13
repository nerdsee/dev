package org.stoevesand.findow.loader;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.Transaction;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.finapi.AccountsService;
import org.stoevesand.findow.provider.finapi.TransactionsService;
import org.stoevesand.findow.provider.finapi.model.TransactionList;

public class DataLoader {

	private static Logger log = LoggerFactory.getLogger(DataLoader.class);

	private static final int MAXTRIES = 5;

	public static void updateTransactions(String userToken, Account account, int days) throws ErrorHandler {
		
		boolean ready = waitUntilReady(userToken, account);
		
		TransactionList transactions = null;
		transactions = TransactionsService.searchTransactions(userToken, account.getSourceId(), days);

		List<Transaction> newTransactions = new Vector<Transaction>();

		for (Transaction tx : transactions.getTransactions()) {
			Transaction knownTx = PersistanceManager.getInstance().getTxByExternalId(tx.getSourceId());
			if (knownTx == null) {
				newTransactions.add(tx);
			}
		}

		if (transactions != null) {
			log.info("account updated. New transactions: " + newTransactions.size());
			PersistanceManager.getInstance().storeTx(newTransactions);
		}

	}

	public static boolean waitUntilReady(String userToken, Account account) {
		try {
			int tries = 0;
			log.info("New account status is " + account.getStatus());
			while (!"UPDATED".equals(account.getStatus()) && tries < MAXTRIES) {
				tries++;
				log.info("Retry: " + tries);
				AccountsService.refreshAccount(userToken, account);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return "UPDATED".equals(account.getStatus());
		} catch (ErrorHandler e) {
			log.error("Failed to refresh account", e);
		}
		return false;
	}
}
