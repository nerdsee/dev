package org.stoevesand.findow.model;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.persistence.PersistanceManager;

import me.figo.FigoException;
import me.figo.FigoSession;
import me.figo.internal.TaskStatusResponse;
import me.figo.models.ApiError;
import me.figo.models.Challenge;

@Entity(name = "Task")
@Table(name = "TASKS")
public class FinTask {

	private Logger log = LoggerFactory.getLogger(FinTask.class);

	// internal id used for persistance
	private Long id;

	private String taskToken;
	private String sourceId;
	private Challenge challenge;
	private ApiError error;
	private String message;
	private String taskType;
	private boolean active;
	private boolean solved;
	private long userId;
	private int retries;
	private Date created;
	private Date lastUpdated;

	private boolean erroneous;

	private boolean waitingForResponse;

	private boolean waitingForPin;

	public FinTask() {

	}

	public FinTask(FinUser user, String taskToken, String taskType) {
		this.taskToken = taskToken;
		this.taskType = taskType;
		this.active = true;
		this.solved = false;
		this.userId = user.getId();
		this.retries = 0;
		this.created = new Date();
		this.lastUpdated = new Date();
	}

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "TASK_ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "TASK_TYPE")
	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	@Column(name = "USER_ID")
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Column(name = "TASK_TOKEN")
	public String getTaskToken() {
		return taskToken;
	}

	public void setTaskToken(String taskToken) {
		this.taskToken = taskToken;
	}

	@Column(name = "SOURCE_ID")
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Transient
	public Challenge getChallenge() {
		return challenge;
	}

	@Transient
	public ApiError getError() {
		return error;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public boolean isErroneous() {
		return erroneous;
	}

	public void setErroneous(boolean erroneous) {
		this.erroneous = erroneous;
	}

	public boolean isWaitingForResponse() {
		return waitingForResponse;
	}

	public void setWaitingForResponse(boolean waitingForResponse) {
		this.waitingForResponse = waitingForResponse;
	}

	public boolean isWaitingForPin() {
		return waitingForPin;
	}

	public void setWaitingForPin(boolean waitingForPin) {
		this.waitingForPin = waitingForPin;
	}

	@Transient
	public void getTaskState(FinUser user) {
		FigoSession fs = new FigoSession(user.getToken());
		getTaskState(fs);
	}

	@Transient
	public boolean getTaskState(FigoSession fs) {
		boolean changed = false;
		try {
			if (active) {
				TaskStatusResponse tsr = fs.getTaskState(taskToken);

				sourceId = tsr.getAccountId();
				challenge = tsr.getChallenge();
				error = tsr.getError();
				message = tsr.getMessage();
				erroneous = tsr.isErroneous();
				waitingForResponse = tsr.isWaitingForResponse();
				waitingForPin = tsr.isWaitingForPin();
				active = !tsr.isEnded();
				retries++;
				lastUpdated = new Date();
				changed = true;
			}

		} catch (FigoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return changed;
	}

	public String toString() {
		return String.format("Task (%d) account:%s type:%s message:%s", id, sourceId, taskType, message);
	}
}
