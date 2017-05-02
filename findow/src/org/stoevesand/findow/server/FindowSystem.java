package org.stoevesand.findow.server;

import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.provider.figo.FigoBankingAPI;
import org.stoevesand.findow.provider.finapi.FinapiBankingAPI;

public class FindowSystem {

	private static final String FINAPI = "FINAPI";
	private static final String FIGO = "FIGO";

	public static BankingAPI getBankingAPI() {
		return FinapiBankingAPI.getInstance();
	}

	public static BankingAPI getBankingAPI(String provider) {

		if (FINAPI.equals(provider)) {
			return FinapiBankingAPI.getInstance();
		} else {
			return FigoBankingAPI.getInstance();
		}

	}

}
