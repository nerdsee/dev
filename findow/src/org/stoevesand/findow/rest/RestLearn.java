package org.stoevesand.findow.rest;

import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinCategory;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinTransaction;
import org.stoevesand.findow.persistence.PersistanceManager;

import io.swagger.annotations.Api;

@Path("/learn")
@Api(value = "learn")
public class RestLearn {

	private Logger log = LoggerFactory.getLogger(RestLearn.class);

	@Context
	private HttpServletResponse response;

	@Path("/train")
	@GET
	@Produces("text/plain")
	public String getTrainingSet() {

		StringBuffer res = new StringBuffer();

		try {
			List<FinTransaction> transactions = PersistanceManager.getInstance().getTx();

			for (FinTransaction t : transactions) {

				Long id = t.getId();
				FinCategory cat = t.getCategory();
				String catName = cat != null ? cat.getName() : null;

				String purpose = t.getPurpose();
				if (t.getCounterpartName() != null) {
					purpose = purpose + t.getCounterpartName();
				}

				StringTokenizer st = new StringTokenizer(purpose);
				while (st.hasMoreTokens()) {

					String token = st.nextToken();
					if (!token.matches(".*[0-9].*")) {
						res.append(id).append("\t");
						res.append(token).append("\t");
						res.append(catName).append("\t");
						res.append("\n");
					}
				}

			}

		} catch (FinErrorHandler e) {
			e.printStackTrace();
		}

		return res.toString();
	}

}