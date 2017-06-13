package org.stoevesand.findow.rest.figo;

import java.util.HashMap;
import java.util.List;

import com.google.gson.annotations.Expose;

public class Bank {

	@Expose
	private String bank_name;

	@Expose
	private String bank_code;

	@Expose
	private List<String> icon;

	@Expose
	private HashMap<String, String> additional_icons;

	public Bank() {

	}

	public String getBankName() {
		return bank_name;
	}

	public void setBankName(String name) {
		this.bank_name = name;
	}

	public String getBankCode() {
		return bank_code;
	}

	public void setBankCode(String bankCode) {
		this.bank_code = bankCode;
	}

	public List<String> getIcon() {
		return icon;
	}

	public void setIcon(List<String> icon) {
		this.icon = icon;
	}

	public HashMap<String, String> getAdditionalIcons() {
		return additional_icons;
	}

	public void setAdditional_icons(HashMap<String, String> additionalIcons) {
		this.additional_icons = additionalIcons;
	}

	public static class BankResponse {

		@Expose
		private List<Bank> banks;

		public BankResponse() {

		}

		public List<Bank> getBanks() {
			return banks;
		}
	}

}
