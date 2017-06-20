package org.stoevesand.findow.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.provider.figo.FigoBankingAPI;
import org.stoevesand.findow.provider.finapi.FinapiBankingAPI;

public class FindowSystem {

	private static Logger log = LoggerFactory.getLogger(FindowSystem.class);

	private static final String FINAPI = "FINAPI";
	private static final String FIGO = "FIGO";

	public static BankingAPI getBankingAPI(FinUser user) throws FinErrorHandler {
		if (user == null) {
			log.error("na valid user to get the BankingAPI (null).");
			throw new FinErrorHandler("Not a valid user. (null)");
		}
		return getBankingAPI(user.getApi());
	}

	public static BankingAPI getBankingAPI(String provider) {

		if (FINAPI.equals(provider)) {
			return FinapiBankingAPI.getInstance();
		} else {
			return FigoBankingAPI.getInstance();
		}

	}

	public static boolean isLocal() {
		String value = System.getProperty("findow.debug");
		return (value != null);
	}

	public static String getStage() {
		String stage = System.getProperty("findow.stage");
		return stage == null ? "dev" : stage;
	}

}
