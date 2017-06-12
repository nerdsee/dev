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
	public double getBalanceAfter() {
		return balanceAfter;
	}

	@JsonGetter
	public double getBalanceBefore() {
		return balanceBefore;
	}

	@JsonGetter
	public double getTxSum() {
		return (double) txSum / 100;
	}

	private List<FinTransaction> transactions;
	private double balanceAfter = 0;
	private double balanceBefore = 0;
	private long txSum = 0;

	public FinTransactionWrapper(List<FinTransaction> transactions, FinAccount account) {
		this.transactions = transactions;
		sumTransactions(transactions);
		balanceAfter = (double) account.getBalanceCent() / 100;
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
