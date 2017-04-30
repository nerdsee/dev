package org.stoevesand.findow.jobs;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.Account;

public class JobManager {

	private Logger log = LoggerFactory.getLogger(JobManager.class);

	private static JobManager _instance = null;
	private SchedulerFactory schFactory = new StdSchedulerFactory();
	private Scheduler sch;

	private JobManager() {

		// refresh Account Job

		JobDetail refreshAccountJob = JobBuilder.newJob(RefreshAccountJob.class).withIdentity("refreshAccountJob").build();

		// Trigger the job to run on the next round minute
		Trigger trigger = TriggerBuilder.newTrigger().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(30).repeatForever()).build();

		try {
			sch = schFactory.getScheduler();
			// Start the schedule
			sch.start();
			// Tell quartz to schedule the job using the trigger
			sch.scheduleJob(refreshAccountJob, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

	public void addImportAccountJob(Account account) {
		try {
			JobDetail refreshAccountJob = JobBuilder.newJob(ImportAccountJob.class).withIdentity("importAccountJob").build();
			refreshAccountJob.getJobDataMap().put(ImportAccountJob.ACCOUNT_KEY, account);

			// Trigger the job to run on the next round minute
			Trigger trigger = TriggerBuilder.newTrigger().startNow().build();

			sch.scheduleJob(refreshAccountJob, trigger);
		} catch (SchedulerException e) {
			log.error("addImportAccountJob", e);
		} catch (Exception e) {
			log.error("addImportAccountJob", e);
		}
	}

	public static JobManager getInstance() {
		if (_instance == null) {
			_instance = new JobManager();
		}
		return _instance;

	}
}
