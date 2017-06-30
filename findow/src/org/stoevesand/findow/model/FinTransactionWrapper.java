package org.stoevesand.findow.model;

import java.util.List;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity
public class FinTransactionWrapper {

	@JsonGetter
	public List<FinTransaction> getTransactions() {
		return transactions;
	}

	@JsonGetter
	public long getBalanceAfter() {
		return balanceAfter;
	}

	@JsonGetter
	public long getBalanceBefore() {
		return balanceBefore;
	}

	@JsonGetter
	public long getTxSum() {
		return txSum;
	}

	private List<FinTransaction> transactions;
	private long balanceAfter = 0;
	private long balanceBefore = 0;
	private long txSum = 0;

	public FinTransactionWrapper(List<FinTransaction> transactions) {
		this.transactions = transactions;
		sumTransactions(transactions);
		balanceAfter = txSum;
		balanceBefore = 0;
	}

	public FinTransactionWrapper(List<FinTransaction> transactions, FinAccount account) {
		this.transactions = transactions;
		sumTransactions(transactions);
		balanceAfter = account.getBalanceCent();
		balanceBefore = balanceAfter - txSum;
	}

	private void sumTransactions(List<FinTransaction> transactions) {
		txSum = 0;
		for (FinTransaction tx : transactions) {
			long amount = tx.getAmountCent();
			txSum += amount;
		}
	}

}
