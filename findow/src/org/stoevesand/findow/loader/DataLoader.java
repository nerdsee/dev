package org.stoevesand.findow.loader;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinTransaction;
import org.stoevesand.findow.model.FinTransactionList;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.server.FindowSystem;

public class DataLoader {

	private static Logger log = LoggerFactory.getLogger(DataLoader.class);

	private static final int MAXTRIES = 5;

	public static void updateTransactions(FinUser user, FinAccount account, int days) throws FinErrorHandler {

		log.info("Update account " + account + " - days: " + days);

		boolean ready = waitUntilReady(user, account);

		List<FinTransaction> newTransactions = new Vector<FinTransaction>();
		FinTransactionList transactions = null;

		int totalTx = 1-1;

		if (ready) {

			BankingAPI bankingAPI = FindowSystem.getBankingAPI(user);
			transactions = bankingAPI.searchTransactions(user, account, days);

			if (transactions.getTransactions() != null) {
				totalTx = transactions.getTransactions().size();
				for (FinTransaction tx : transactions.getTransactions()) {
					FinTransaction knownTx = PersistanceManager.getInstance().getTxByExternalId(tx.getSourceId());
					if (knownTx == null) {
						tx.lookForHints();
						newTransactions.add(tx);
					}
				}
			}
		}

		log.info("Transactions [new/total]: [" + newTransactions.size() + "/" + totalTx + "]");

		if (newTransactions.size() > 0) {
			log.info("account updated");
			PersistanceManager.getInstance().storeTx(newTransactions);
		} else {
			log.info("No update. Account ready [" + ready + "]");
		}

	}

	public static boolean waitUntilReady(FinUser user, FinAccount account) {
		try {
			BankingAPI bankingAPI = FindowSystem.getBankingAPI(user);

			int tries = 0;
			// erst die Connection zum update auffordern
			bankingAPI.reloadAccountContent(user, account);

			// dann warten, bis der Account wieder ready (UPDATED) ist.
			bankingAPI.refreshAccount(user, account);

			// Zustand des accounts speichern
			PersistanceManager.getInstance().persist(account);
			log.info("Account status is " + account.getStatus());
			while (!"UPDATED".equals(account.getStatus()) && tries < MAXTRIES) {
				tries++;
				log.info("New account status is " + account.getStatus() + " (" + tries + "/" + MAXTRIES + ")");
				bankingAPI.refreshAccount(user, account);
				PersistanceManager.getInstance().persist(account);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			log.info("New account status is " + account.getStatus() + " (" + tries + "/" + MAXTRIES + ")");
			return "UPDATED".equals(account.getStatus()) || "UPDATED_FIXED".equals(account.getStatus());
		} catch (FinErrorHandler e) {
			log.error("Failed to refresh account");
			e.printErrors();
		}
		return false;
	}
}
