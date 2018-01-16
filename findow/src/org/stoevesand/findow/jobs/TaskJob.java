package org.stoevesand.findow.jobs;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinTask;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;

/**
 * Diese Klasse *soll* Tasks neu starten, die noch liefen, als der Server runtergefahren wurde.
 * Da fehlt noch einiges. Glaube ich. ;-)
 * @author JAN
 *
 */
@DisallowConcurrentExecution
public class TaskJob implements Job {

	public static final String TASK_KEY = "TASK_KEY";
	private Logger log = LoggerFactory.getLogger(TaskJob.class);

	public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
		log.info("Update Tasks ...");

		List<FinTask> tasks = PersistanceManager.getInstance().getActiveTasks();
		int runningTaskCount = tasks.size();
		for (FinTask task : tasks) {
			FinUser user = PersistanceManager.getInstance().getUser(task.getUserId());
		}
		log.info(String.format("Running tasks [before/after]: [%d/%D]"), tasks.size(), runningTaskCount);
	}

}
