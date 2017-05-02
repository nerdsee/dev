package org.stoevesand.findow.provider.finapi.model;

import java.util.List;
import java.util.Vector;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.stoevesand.findow.model.Transaction;
import org.stoevesand.findow.rest.RestUtils;

public class TransactionList {

	List<Transaction> transactions;
	private double income;
	private double spending;
	private double balance;
	private int page;
	private int perPage;
	private int pageCount;
	private int totalCount;

	public TransactionList(JSONObject jo) {
		try {

			transactions = new Vector<Transaction>();

			JSONArray json_txs = jo.getJSONArray("transactions");

			for (int i = 0; i < json_txs.length(); i++) {
				JSONObject json_account = json_txs.getJSONObject(i);
				Transaction transaction = new Transaction(json_account);
				transactions.add(transaction);
			}

			income = JSONUtils.getDouble(jo, "income");
			spending = JSONUtils.getDouble(jo, "spending");
			balance = JSONUtils.getDouble(jo, "balance");

			JSONObject paging = JSONUtils.getJSONObject(jo, "paging");

			page = JSONUtils.getInt(paging, "page");
			perPage = JSONUtils.getInt(paging, "perPage");
			pageCount = JSONUtils.getInt(paging, "pageCount");
			totalCount = JSONUtils.getInt(paging, "totalCount");

		} catch (JSONException e) {
			e.printStackTrace();
		}
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

}
