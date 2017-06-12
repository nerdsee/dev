package org.stoevesand.findow.rest;

import javax.servlet.http.HttpServletResponse;

import org.stoevesand.findow.jobs.JobManager;
import org.stoevesand.findow.model.FinToken;
import org.stoevesand.findow.provider.finapi.TokenStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class RestUtils {

	public static FinToken getClientToken() {
		return TokenStore.getInstance().getClientToken();
	}

	public static FinToken getAdminToken() {
		return TokenStore.getInstance().getAdminToken();
	}

	public static String generateJsonResponse(Object element, String rootName) {
		String result = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			if (rootName == null) {
				result = mapper.writeValueAsString(element);
			} else {
				mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
				result = mapper.writer().withRootName(rootName).writeValueAsString(element);
			}
		} catch (JsonProcessingException e) {
			result = "{\"error\" : \"Something went wrong: " + e + "\"}";
			e.printStackTrace();
		}
		return result;
	}

	public static String generateJsonResponse(Object element) {
		return generateJsonResponse(element, null);
	}

	public static void addHeader(HttpServletResponse response) {
		JobManager.getInstance();
		// response.addHeader("Access-Control-Allow-Origin", "*");
	}

}