package org.stoevesand.findow.rest;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Response {

	public static final Response OK = new Response(200, "OK");
	public static final Response USER_ALREADY_USED = new Response(400, "USER_ALREADY_USED");
	public static final Response USER_OR_PASSWORD_INVALID = new Response(401, "USER_OR_PASSWORD_INVALID");
	public static final Response USER_UNKNOWN = new Response(402, "USER_UNKNOWN");
	public static final Response INVALID_ID = new Response(403, "INVALID_ID");
	public static final Response ACCOUNT_UNKNOWN = new Response(404, "ACCOUNT_UNKNOWN");
	public static final Response ACCOUNT_ALREADY_EXISTS = new Response(405, "ACCOUNT_ALREADY_EXISTS");
	public static final Response ACCOUNT_IMPORT_REJECTED = new Response(406, "ACCOUNT_IMPORT_REJECTED");
	public static final Response ACCOUNT_ILLEGAL_FIELD = new Response(407, "ACCOUNT_ILLEGAL_FIELD");
	public static final Response UNKNOWN = new Response(999, "UNKNOWN");

	String status = "";
	int code = 0;

	public Response(int code, String status) {
		this.code = code;
		this.status = status;
	}

	@JsonGetter
	public String getStatus() {
		return status;
	}

	@JsonGetter
	public int getCode() {
		return code;
	}
}
