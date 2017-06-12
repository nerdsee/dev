package org.stoevesand.findow.jobs;

import java.util.Date;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.loader.DataLoader;
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;

@DisallowConcurrentExecution
public class RefreshAccountJob implements Job {

	private Logger log = LoggerFactory.getLogger(RefreshAccountJob.class);

	public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
		log.info("Refresh Account Transactions ...");

		List<FinAccount> accounts = PersistanceManager.getInstance().getAllAccounts();

		for (FinAccount account : accounts) {
			FinUser user = account.getUser();
			try {
				log.info("Update transactions of account " + account);
				Date lastUpdate = account.getLastSuccessfulUpdate();
				int diff = 120;
				if (lastUpdate != null) {
					Date now = new Date();
					// Zeit seit dem letzten update plus eine Woche
					diff = daysBetween(lastUpdate, now) + 7;
				}
				user.refreshToken();
				DataLoader.updateTransactions(user, account, diff);
			} catch (FinErrorHandler e) {
				log.error("Failed to refresh account " + account, e);
			}
		}
		log.info("Refresh Account Transactions ... done.");
	}

	public int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}
}
