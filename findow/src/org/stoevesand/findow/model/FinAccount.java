package org.stoevesand.findow.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.annotations.GenericGenerator;
import org.stoevesand.findow.provider.finapi.model.JSONUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "Account")
@Table(name = "ACCOUNTS")
public class FinAccount {

	// internal id used for persistance
	private Long id;

	// id coming from a source system
	private String sourceId;
	private String sourceSystem = "FINAPI";

	private FinUser user = null;

	private String taskId;

	public FinAccount() {

	}

	@Column(name = "SOURCE_ID")
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	@Column(name = "TASK_ID")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@JsonIgnore
	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false)
	@JsonIgnore
	public FinUser getUser() {
		return user;
	}

	public void setUser(FinUser user) {
		this.user = user;
	}

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "ACCOUNT_ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public int getBankConnectionId() {
		return bankConnectionId;
	}

	public String getAccountName() {
		return accountName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getSubAccountNumber() {
		return subAccountNumber;
	}

	public String getIban() {
		return iban;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	private int bankConnectionId;
	private String accountName;
	private String accountNumber;
	private String subAccountNumber;
	private String iban;
	private String accountHolderName;

	private String accountCurrency;

	private int accountTypeId;

	private String accountTypeName;

	private long balance;

	private long overdraft;

	private long overdraftLimit;

	private long availableFunds;

	private Date lastSuccessfulUpdate;

	private Date lastUpdateAttempt;

	private String status;

	private String bankName;

	public FinAccount(JSONObject jo) {
		update(jo);
		bankName = "";
	}

	public FinAccount(String accountId, String task) {
		sourceId = accountId;
		status = "PENDING";
		taskId = task;
		sourceSystem = "FIGO";
	}

	public FinAccount(me.figo.models.Account acc) {
		refresh(acc);
	}

	public void update(JSONObject jo) {
		sourceId = JSONUtils.getString(jo, "id");
		bankConnectionId = JSONUtils.getInt(jo, "bankConnectionId");
		accountName = JSONUtils.getString(jo, "accountName");
		accountNumber = JSONUtils.getString(jo, "accountNumber");
		subAccountNumber = JSONUtils.getString(jo, "subAccountNumber");
		iban = JSONUtils.getString(jo, "iban");
		accountHolderName = JSONUtils.getString(jo, "accountHolderName");

		accountCurrency = JSONUtils.getString(jo, "accountCurrency");
		accountTypeId = JSONUtils.getInt(jo, "accountTypeId");
		accountTypeName = JSONUtils.getString(jo, "accountTypeName");

		double bd = JSONUtils.getDouble(jo, "balance");
		balance = (long) (bd * 100);

		double dov = JSONUtils.getDouble(jo, "overdraft");
		overdraft = (long) (dov * 100);

		double dol = JSONUtils.getDouble(jo, "overdraftLimit");
		overdraftLimit = (long) (dol * 100);

		double daf = JSONUtils.getDouble(jo, "availableFunds");
		availableFunds = (long) (daf * 100);

		status = JSONUtils.getString(jo, "status");
		lastSuccessfulUpdate = JSONUtils.getDate(jo, "lastSuccessfulUpdate", "yyyy-MM-dd HH:mm:ss.SSS");
		lastUpdateAttempt = JSONUtils.getDate(jo, "lastUpdateAttempt", "yyyy-MM-dd HH:mm:ss.SSS");
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastSuccessfulUpdate() {
		return lastSuccessfulUpdate;
	}

	public void setLastSuccessfulUpdate(Date lastSuccessfulUpdate) {
		this.lastSuccessfulUpdate = lastSuccessfulUpdate;
	}

	public Date getLastUpdateAttempt() {
		return lastUpdateAttempt;
	}

	public void setLastUpdateAttempt(Date lastUpdateAttempt) {
		this.lastUpdateAttempt = lastUpdateAttempt;
	}

	public String getAccountCurrency() {
		return accountCurrency;
	}

	public void setAccountCurrency(String accountCurrency) {
		this.accountCurrency = accountCurrency;
	}

	public int getAccountTypeId() {
		return accountTypeId;
	}

	public void setAccountTypeId(int accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	@Column(name = "BALANCE_CENT")
	public long getBalanceCent() {
		return balance;
	}

	public void setBalanceCent(long balance) {
		this.balance = balance;
	}

	public long getOverdraft() {
		return overdraft;
	}

	public void setOverdraft(long overdraft) {
		this.overdraft = overdraft;
	}

	public long getOverdraftLimit() {
		return overdraftLimit;
	}

	public void setOverdraftLimit(long overdraftLimit) {
		this.overdraftLimit = overdraftLimit;
	}

	public long getAvailableFunds() {
		return availableFunds;
	}

	public void setAvailableFunds(long availableFunds) {
		this.availableFunds = availableFunds;
	}

	public void setBankConnectionId(int bankConnectionId) {
		this.bankConnectionId = bankConnectionId;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String toString() {
		Long userid = user == null ? null : user.getId();
		return String.format("%s - %s (%d)(User %d)", getAccountName(), getBankName(), id, userid);
	}

	// public int refresh(String userToken) {
	// try {
	// AccountsService.refreshAccount(userToken, this);
	// } catch (ErrorHandler e) {
	// return e.getStatus();
	// }
	// return 0;
	// }

	@Override
	public boolean equals(Object a) {
		if (a instanceof FinAccount) {
			if (id != null) {
				return this.id.equals(((FinAccount) a).getId());
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (id != null) {
			return id.hashCode();
		} else {
			return 0;
		}
	}

	public void setBank(FinBank bank) {
		if (bank != null) {
			this.bankName = bank.getName();
		}
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public void refresh(me.figo.models.Account acc) {
		sourceId = acc.getAccountId();
		accountNumber = acc.getAccountNumber();
		bankName = acc.getBankName();
		double dba = acc.getBalance().getBalance().doubleValue();
		balance = (long) (dba * 100);
		accountCurrency = acc.getCurrency();
		accountHolderName = acc.getOwner();
		accountTypeName = acc.getType();
		status = "UPDATED";
	}

}
