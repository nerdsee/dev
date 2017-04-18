package org.stoevesand.findow.model;

import java.util.List;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity
public class TransactionWrapper {

	@JsonGetter
	public List<Transaction> getTransactions() {
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
		return txSum;
	}

	private List<Transaction> transactions;
	private double balanceAfter = 0;
	private double balanceBefore = 0;
	private double txSum = 0;

	public TransactionWrapper(List<Transaction> transactions, Account account) {
		this.transactions = transactions;
		sumTransactions(transactions);
		balanceAfter = account.getBalance();
		balanceBefore = balanceAfter - txSum;
	}

	private void sumTransactions(List<Transaction> transactions) {
		long sum = 0;
		for (Transaction tx : transactions) {
			long amount = Math.round(tx.getAmount() * 100);
			sum += amount;
		}
		txSum = (double)sum / 100;
	}

}
