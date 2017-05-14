package org.stoevesand.findow.model;

import java.util.List;
import java.util.Vector;

public class TransactionList {

	List<Transaction> transactions;
	private double income=0;
	private double spending=0;
	private double balance=0;
	private int page=0;
	private int perPage=0;
	private int pageCount=0;
	private int totalCount=0;

	public TransactionList() {
		transactions = new Vector<Transaction>();
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public void setSpending(double spending) {
		this.spending = spending;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPage() {
		return page;
	}

	public int getPerPage() {
		return perPage;
	}

	public int getPageCount() {
		return pageCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public TransactionList(List<Transaction> list) {
		this.transactions = list;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public double getIncome() {
		return income;
	}

	public double getSpending() {
		return spending;
	}

	public double getBalance() {
		return balance;
	}

	public void append(TransactionList txPage) {
		transactions.addAll(txPage.getTransactions());
	}

	public void addTransaction(Transaction transaction) {
		transactions.add(transaction);
	}

}
