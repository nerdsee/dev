package org.stoevesand.findow.jobs;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinTask;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;

import me.figo.models.ApiError;

public class SingleTaskJob implements Job {

	public static final String TASK_KEY = "TASK_KEY";
	private Logger log = LoggerFactory.getLogger(SingleTaskJob.class);

	public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
		log.info("Update Task ...");

		JobDataMap data = jExeCtx.getJobDetail().getJobDataMap();
		Object ao = data.get(TASK_KEY);

		if (ao instanceof FinTask) {
			FinTask task = (FinTask) ao;
			log.info("Handle Task " + task);
			FinUser user = PersistanceManager.getInstance().getUser(task.getUserId());
			task.getTaskState(user);
			task = PersistanceManager.getInstance().persist(task);
			if (task.isActive()) {

				if (task.isErroneous()) {
					log.error(String.format("Task Error (%d): %s", task.getId(), task.getMessage()));
					ApiError error = task.getError();
					if (error != null) {
						log.error(error.getName());
						log.error(error.getDescription() );
						log.error(error.getMessage());
						log.error("Code: " + error.getCode());
					}
				} else {

					// Solange versuchen, bis er fertig ist.
					int secs = 10 * task.getRetries();
					log.info(String.format("task still running. Retry in %d seconds: %s", secs, task));
					long next = System.currentTimeMillis() + (secs * 1000);
					JobManager.getInstance().addSingleTaskJob(task, new Date(next));
				}
			} else if (!task.isSolved()) {
				TaskSolver.getInstance().solve(user, task);
			}

		} else {
			log.error("No task passed to job.");
		}

	}

}
