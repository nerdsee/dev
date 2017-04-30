package org.stoevesand.findow.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.loader.DataLoader;
import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.ErrorHandler;
import org.stoevesand.findow.model.User;

public class ImportAccountJob implements Job {

	public static final String ACCOUNT_KEY = "ACCOUNT_PARAM";

	private Logger log = LoggerFactory.getLogger(ImportAccountJob.class);

	public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {

		JobDataMap data = jExeCtx.getJobDetail().getJobDataMap();
		Object ao = data.get(ACCOUNT_KEY);

		if (ao instanceof Account) {
			Account account = (Account) ao;
			log.info("Initial import of account " + account);
			User user = account.getUser();
			try {
				log.info("Import transactions of account " + account);
				user.refreshToken();
				DataLoader.updateTransactions(user.getToken(), account, 120);
			} catch (ErrorHandler e) {
				log.error("Failed to refresh account " + account, e);
			}
		} else {
			log.error("No account passed to job.");
		}
	}

}