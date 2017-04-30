package org.stoevesand.finapi;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.stoevesand.finapi.model.TransactionList;
import org.stoevesand.findow.model.Account;
import org.stoevesand.findow.model.ErrorHandler;

public class TransactionsService {

	static final String URL = "https://sandbox.finapi.io/api/v1/transactions";

	public static TransactionList searchTransactions(String userToken, Long accountId, int days) throws ErrorHandler {
		TransactionList ret = searchTransactions(userToken, accountId, days, 1);
		int pageCount = ret.getPageCount();
		if (pageCount > 1) {
			for (int page = 2; page <= pageCount; page++) {
				TransactionList txPage = searchTransactions(userToken, accountId, days, page);
				ret.append(txPage);
			}
		}

		return ret;
	}

	public static TransactionList searchTransactions(String userToken, Long accountId, int days, int page) throws ErrorHandler {

		TransactionList ret = null;

		long minDateMillis = System.currentTimeMillis();
		Date curDate = new Date(minDateMillis);
		minDateMillis = minDateMillis - ((long) days * 24 * 60 * 60 * 1000);
		Date minDate = new Date(minDateMillis);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String curBankBookingDate = formatter.format(curDate);
		String minBankBookingDate = formatter.format(minDate);

		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(URL);
		webTarget = webTarget.queryParam("access_token", userToken);
		webTarget = webTarget.queryParam("perPage", 100);
		webTarget = webTarget.queryParam("page", page);
		webTarget = webTarget.queryParam("minBankBookingDate", minBankBookingDate);

		// only use valid accountId
		if (accountId != null) {
			webTarget = webTarget.queryParam("accountIds", accountId.toString());
		}

		webTarget = webTarget.queryParam("view", "userView");
		// webTarget = webTarget.queryParam("isNew", "false");

		Invocation.Builder invocationBuilder = webTarget.request();
		invocationBuilder.accept("application/json");
		Response response = invocationBuilder.get();
		String output = response.readEntity(String.class);

		int status = response.getStatus();
		if (status != 200) {
			ErrorHandler eh = new ErrorHandler(output);
			System.out.println("searchTransactions failed: " + status);
			eh.printErrors();
			throw eh;
		}

		try {
			JSONObject jo = new JSONObject(output);
			ret = new TransactionList(jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ret;

	}

}
