package org.stoevesand.findow.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;

public class FinAccountWrapper {

	private List<FinAccount> accounts;
	private Map<String, Long> typeSum;
	
	public FinAccountWrapper(List<FinAccount> accounts) {
		this.accounts = accounts;

		typeSum = new HashMap<String, Long>();

		for (FinAccount account : accounts) {
			String type = account.getAccountTypeName();

			Long sum = typeSum.get(type);
			if (sum == null) {
				sum = new Long(0);
			}
			sum = sum + account.getBalanceCent();
			typeSum.put(type, sum);
		}

	}

	@JsonGetter
	public List<FinAccount> getAccounts() {
		return accounts;
	}

	@JsonGetter
	public Map<String, Long> getTypeBalanceCent() {
		return typeSum;
	}

	
}
