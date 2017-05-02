package org.stoevesand.findow.server;

import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.provider.finapi.FinapiBankingAPI;

public class FindowSystem {

	public static BankingAPI getBankingAPI() {
		return FinapiBankingAPI.getInstance();
	}
	
}
