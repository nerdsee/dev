package org.stoevesand.findow.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.annotations.GenericGenerator;
import org.stoevesand.findow.provider.finapi.AccountsService;
import org.stoevesand.findow.provider.finapi.TokenService;
import org.stoevesand.findow.provider.finapi.model.JSONUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ACCOUNTS")
public class Account {

	// internal id used for persistance
	private Long id;

	// id coming from a source system
	private Long sourceId;
	private String sourceSystem = "FINAPI";

	private User user = null;

	public Account() {

	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
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
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setBalance(double balance) {
		this.balance = balance;
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

	private double balance;

	private double overdraft;

	private double overdraftLimit;

	private double availableFunds;

	private String lastSuccessfulUpdate;

	private String lastUpdateAttempt;

	private String status;

	private String bankName;

	public Account(JSONObject jo) {
		update(jo);
	}

	public void update(JSONObject jo) {
		sourceId = JSONUtils.getLong(jo, "id");
		bankConnectionId = JSONUtils.getInt(jo, "bankConnectionId");
		accountName = JSONUtils.getString(jo, "accountName");
		accountNumber = JSONUtils.getString(jo, "accountNumber");
		subAccountNumber = JSONUtils.getString(jo, "subAccountNumber");
		iban = JSONUtils.getString(jo, "iban");
		accountHolderName = JSONUtils.getString(jo, "accountHolderName");

		accountCurrency = JSONUtils.getString(jo, "accountCurrency");
		accountTypeId = JSONUtils.getInt(jo, "accountTypeId");
		accountTypeName = JSONUtils.getString(jo, "accountTypeName");
		balance = JSONUtils.getDouble(jo, "balance");
		overdraft = JSONUtils.getDouble(jo, "overdraft");
		overdraftLimit = JSONUtils.getDouble(jo, "overdraftLimit");
		availableFunds = JSONUtils.getDouble(jo, "availableFunds");
		status = JSONUtils.getString(jo, "status");
		lastSuccessfulUpdate = JSONUtils.getString(jo, "lastSuccessfulUpdate");
		lastUpdateAttempt = JSONUtils.getString(jo, "lastUpdateAttempt");
		bankName = "";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastSuccessfulUpdate() {
		return lastSuccessfulUpdate;
	}

	public void setLastSuccessfulUpdate(String lastSuccessfulUpdate) {
		this.lastSuccessfulUpdate = lastSuccessfulUpdate;
	}

	public String getLastUpdateAttempt() {
		return lastUpdateAttempt;
	}

	public void setLastUpdateAttempt(String lastUpdateAttempt) {
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

	public double getBalance() {
		return balance;
	}

	public double getOverdraft() {
		return overdraft;
	}

	public void setOverdraft(double overdraft) {
		this.overdraft = overdraft;
	}

	public double getOverdraftLimit() {
		return overdraftLimit;
	}

	public void setOverdraftLimit(double overdraftLimit) {
		this.overdraftLimit = overdraftLimit;
	}

	public double getAvailableFunds() {
		return availableFunds;
	}

	public void setAvailableFunds(double availableFunds) {
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

	public int refresh(String userToken) {
		try {
			AccountsService.refreshAccount(userToken, this);
		} catch (ErrorHandler e) {
			return e.getStatus();
		}
		return 0;
	}

	@Override
	public boolean equals(Object a) {
		if (a instanceof Account) {
			if (id != null) {
				return this.id.equals(((Account) a).getId());
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

	public void setBank(Bank bank) {
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

}
