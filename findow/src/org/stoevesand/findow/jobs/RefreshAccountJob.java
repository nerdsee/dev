package org.stoevesand.findow.jobs;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.server.FindowSystem;

@DisallowConcurrentExecution
public class RefreshAccountJob implements Job {

	private Logger log = LoggerFactory.getLogger(RefreshAccountJob.class);

	/**
	 * startet für jeden User eine Anfrage, um die Konten zu synchronisieren
	 */
	public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
		log.info("Refresh Account Transactions ...");

		List<FinUser> users = PersistanceManager.getInstance().getUsers();

		for (FinUser user : users) {

			try {
				BankingAPI bankingAPI = FindowSystem.getBankingAPI(user);
				bankingAPI.reloadAccountContent(user, null);
			} catch (FinErrorHandler e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		log.info("Refresh Account Transactions ... done.");
	}

}
