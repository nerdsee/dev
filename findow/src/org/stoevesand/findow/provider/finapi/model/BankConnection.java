package org.stoevesand.findow.provider.finapi.model;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinBank;
import org.stoevesand.findow.model.FinToken;
import org.stoevesand.findow.provider.finapi.BankConnectionsService;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BankConnection {

	private Logger log = LoggerFactory.getLogger(BankConnection.class);

	private static final int MAXTRIES = 5;
	int id = 0;
	JSONObject jo = null;
	private String bankingUserId;
	private String bankingCustomerId;
	private String bankingPin;
	private String type;
	private String updateStatus;
	private FinBank bank;
	private String pin = null;

	public BankConnection(JSONObject jo) {
		update(jo);
	}

	public void update(JSONObject jo) {
		this.jo = jo;
		try {
			id = jo.getInt("id");
			bankingUserId = jo.getString("bankingUserId");
			bankingCustomerId = jo.getString("bankingCustomerId");
			bankingPin = jo.getString("bankingPin");
			type = jo.getString("type");
			updateStatus = jo.getString("updateStatus");
			bank = new FinBank(jo.getJSONObject("bank"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public String getBankingUserId() {
		return bankingUserId;
	}

	public String getBankingCustomerId() {
		return bankingCustomerId;
	}

	public String getType() {
		return type;
	}

	public String getUpdateStatus() {
		return updateStatus;
	}

	public FinBank getBank() {
		return bank;
	}

	@JsonIgnore
	public boolean waitUntilReady(FinToken userToken) {
		int tries = 0;
		while (!"READY".equals(updateStatus) && tries < MAXTRIES) {
			tries++;
			log.info("Retry: " + tries);
			BankConnectionsService.getBankConnection(userToken, id, this);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return "READY".equals(updateStatus);
	}

	public String toString() {
		return String.format("<%d> [%s]", id, updateStatus);
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

}
