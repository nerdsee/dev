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

	/** wird nicht mehr gebraucht
	 * 
	 * @param user
	 * @param account
	 * @return
	 */
	private static boolean DELETE_waitUntilReady(FinUser user, FinAccount account) {
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
