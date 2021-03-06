package org.stoevesand.findow.model;

import java.util.List;
import java.util.Vector;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.provider.finapi.model.CallError;
import org.stoevesand.findow.rest.RestAccounts;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import me.figo.FigoException;

@JsonIgnoreProperties({ "stackTrace", "localizedMessage", "message", "cause", "suppressed" })
@JsonRootName(value = "error")
public class FinErrorHandler extends Exception {

	private Logger log = LoggerFactory.getLogger(FinErrorHandler.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -7996550252908124993L;

	List<CallError> errors = new Vector<CallError>();
	String response;
	private int status;

	public FinErrorHandler(String response) {
		init(499, response);
	}

	public FinErrorHandler(int status, String response) {
		init(status, response);
	}

	public FinErrorHandler(FigoException e) {
		this.status = 0;
		this.response = e.getErrorMessage();
	}

	private void init(int status, String response) {
		this.status = status;
		this.response = response;
		try {
			JSONObject jo = new JSONObject(response);
			JSONArray json_errors = jo.getJSONArray("errors");
			for (int i = 0; i < json_errors.length(); i++) {
				JSONObject json_account = json_errors.getJSONObject(i);
				CallError error = new CallError(json_account);
				errors.add(error);
			}
		} catch (JSONException e) {
		}
	}

	public int getStatus() {
		return status;
	}

	@JsonIgnore
	public String getResponse() {
		return response;
	}

	@JsonGetter
	public List<CallError> getErrors() {
		return errors;
	}

	public void printErrors() {
		for (CallError error : errors) {
			log.error(error.toString());
		}
	}

	public boolean hasCallError(String msg) {
		for (CallError ce : errors) {
			if (ce.getCode().equals(msg)) {
				return true;
			}
		}
		return false;
	}

}
