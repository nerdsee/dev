package org.stoevesand.findow.jobs;

import java.util.Date;

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
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinTask;
import org.stoevesand.findow.server.FindowSystem;

public class JobManager {

	private Logger log = LoggerFactory.getLogger(JobManager.class);

	private static JobManager _instance = null;
	private SchedulerFactory schFactory = new StdSchedulerFactory();
	private Scheduler sch;

	private JobManager() {

		// refresh Account Job

		log.info("Initialise JobManager" + this);

		try {
			sch = schFactory.getScheduler();

			// Start the schedule
			sch.start();

			JobDetail refreshAccountJob = JobBuilder.newJob(RefreshAccountJob.class).withIdentity("refreshAccountJob").build();

			// Trigger the job to run on the next round minute
			Trigger trigger = TriggerBuilder.newTrigger().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(30).repeatForever()).build();
			// Tell quartz to schedule the job using the trigger
			sch.scheduleJob(refreshAccountJob, trigger);

		} catch (SchedulerException e) {
			log.error("Initialise JobManager failed.", e);
			e.printStackTrace();
		}
		log.info("Initialise JobManager done. Scheduler: " + sch);

	}

	public void addSingleTaskJob(FinTask task, Date start) {
		try {
			JobDetail taskJob = JobBuilder.newJob(SingleTaskJob.class).build();
			taskJob.getJobDataMap().put(SingleTaskJob.TASK_KEY, task);

			// Trigger the job to run on the next round minute
			Trigger triggerNow = TriggerBuilder.newTrigger().startAt(start).build();

			sch.scheduleJob(taskJob, triggerNow);
		} catch (SchedulerException e) {
			log.error("addSingleTaskJob", e);
		} catch (Exception e) {
			log.error("addSingleTaskJob", e);
		}
	}

	public static JobManager getInstance() {
		if (_instance == null) {
			_instance = new JobManager();
		}
		return _instance;

	}
}
