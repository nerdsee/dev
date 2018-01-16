package org.stoevesand.findow.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.loader.DataLoader;
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;

public class ImportAccountJob implements Job {

	public static final String ACCOUNT_KEY = "ACCOUNT_PARAM";

	private Logger log = LoggerFactory.getLogger(ImportAccountJob.class);

	public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {

		JobDataMap data = jExeCtx.getJobDetail().getJobDataMap();
		Object ao = data.get(ACCOUNT_KEY);

		if (ao instanceof FinAccount) {
			FinAccount account = (FinAccount) ao;
			log.info("Initial import of account " + account);
			FinUser user = account.getUser();
			try {
				log.info("Import transactions of account " + account);
				user.refreshToken();
				PersistanceManager.getInstance().updateTransactions(user, account, 120);
			} catch (FinErrorHandler e) {
				log.error("Failed to refresh account " + account, e);
			}
		} else {
			log.error("No account passed to job.");
		}
	}

}
