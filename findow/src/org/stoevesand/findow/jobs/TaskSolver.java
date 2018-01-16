package org.stoevesand.findow.jobs;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.loader.DataLoader;
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinTask;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.server.FindowSystem;

public class TaskSolver {
	private static final int MAX_DAYS_HISTORY = 2000; // infinity
	public static final String IMPORT_ACCOUNT = "IMPORT_ACCOUNT";
	public static final String UPDATE_TX = "UPDATE_TX";

	private Logger log = LoggerFactory.getLogger(TaskSolver.class);

	private static TaskSolver _instance = null;

	private TaskSolver() {

	}

	public static TaskSolver getInstance() {
		if (_instance == null) {
			_instance = new TaskSolver();
		}
		return _instance;
	}

	public void solve(FinUser user, FinTask task) {
		String taskType = task.getTaskType();

		switch (taskType) {
			case IMPORT_ACCOUNT:
				importAccount(user, task);
				break;
			case UPDATE_TX:
				updateTransactions(user, task);
				break;
			default:
				log.error("Unknown taskType: " + taskType);
		}

		task.setSolved(true);
		PersistanceManager.getInstance().persist(task);

	}

	
	/** Umsetzung für den Task UPDATE_TX
	 * 
	 * @param user
	 * @param task
	 */
	private void updateTransactions(FinUser user, FinTask task) {
		log.info("Refresh Account Transactions ...");

		List<FinAccount> accounts = user.getAccounts();

		for (FinAccount account : accounts) {
			try {
				log.info("Update transactions of account " + account);
				Date lastUpdate = account.getLastSuccessfulUpdate();
				int diff = MAX_DAYS_HISTORY;
				if (lastUpdate != null) {
					Date now = new Date();
					// Zeit seit dem letzten update plus eine Woche
					diff = daysBetween(lastUpdate, now) + 7;
				}
				user.refreshToken();
				PersistanceManager.getInstance().updateTransactions(user, account, diff);
			} catch (FinErrorHandler e) {
				log.error("Failed to refresh account " + account, e);
			}
		}
		log.info("Refresh Account Transactions ... done.");
	}

	/** Umsetzung für den Task IMPORT_ACCOUNT
	 * 
	 * @param user
	 * @param task
	 */
	private void importAccount(FinUser user, FinTask task) {

		String sourceId = task.getSourceId();

		// Sonderfall abprüfen, ob es eine xxx.0 Konto von figo ist. Evtl.
		// Konten nachladen
		if (sourceId.endsWith(".0")) {
			try {
				BankingAPI api = FindowSystem.getBankingAPI(user);
				List<FinAccount> accs = api.getAccounts(user);

				for (FinAccount newAccount : accs) {
					FinAccount checkAccount = PersistanceManager.getInstance().getAccountByExternalId(user, newAccount.getSourceId());
					if (checkAccount == null) {
						log.info("New Account: " + newAccount);
						newAccount.setUser(user);
						PersistanceManager.getInstance().persist(newAccount);
					} else {
						log.info("Account already imported: " + checkAccount);
					}
				}

			} catch (FinErrorHandler e1) {
				e1.printStackTrace();
			}
		}

	}

	public int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

}
