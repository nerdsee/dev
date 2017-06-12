package org.stoevesand.findow.jobs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinTask;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.server.FindowSystem;

public class TaskSolver {
	public static final String IMPORT_ACCOUNT = "IMPORT_ACCOUNT";

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
			default:
				log.error("Unknown taskType: " + taskType);
		}

		task.setSolved(true);
		PersistanceManager.getInstance().persist(task);

	}

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

}
