package org.stoevesand.findow.jobs;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.loader.DataLoader;
import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.User;
import org.stoevesand.findow.persistence.PersistanceManager;

@DisallowConcurrentExecution
public class RefreshAccountJob implements Job {

	private Logger log = LoggerFactory.getLogger(RefreshAccountJob.class);

	public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
		log.info("Refresh Account Transactions ...");

		List<Account> accounts = PersistanceManager.getInstance().getRefreshableAccounts();

		for (Account account : accounts) {
			User user = account.getUser();
			try {
				log.info("Update transactions of account " + account);
				user.refreshToken();
				DataLoader.updateTransactions(user.getToken(), account, 7);
			} catch (ErrorHandler e) {
				log.error("Failed to refresh account " + account, e);
			}
		}
		log.info("Refresh Account Transactions ... done.");
	}

}
