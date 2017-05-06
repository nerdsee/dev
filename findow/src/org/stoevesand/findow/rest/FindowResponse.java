package org.stoevesand.findow.rest;

import com.fasterxml.jackson.annotation.JsonGetter;

public class FindowResponse {

	public static final FindowResponse OK = new FindowResponse(200, "OK");
	public static final FindowResponse USER_ALREADY_USED = new FindowResponse(400, "USER_ALREADY_USED");
	public static final FindowResponse USER_OR_PASSWORD_INVALID = new FindowResponse(401, "USER_OR_PASSWORD_INVALID");
	public static final FindowResponse USER_UNKNOWN = new FindowResponse(402, "USER_UNKNOWN");
	public static final FindowResponse INVALID_ID = new FindowResponse(403, "INVALID_ID");
	public static final FindowResponse INVALID_JWT = new FindowResponse(404, "INVALID_JWT");
	
	public static final FindowResponse ACCOUNT_UNKNOWN = new FindowResponse(424, "ACCOUNT_UNKNOWN");
	public static final FindowResponse ACCOUNT_ALREADY_EXISTS = new FindowResponse(425, "ACCOUNT_ALREADY_EXISTS");
	public static final FindowResponse ACCOUNT_IMPORT_REJECTED = new FindowResponse(426, "ACCOUNT_IMPORT_REJECTED");
	public static final FindowResponse ACCOUNT_ILLEGAL_FIELD = new FindowResponse(427, "ACCOUNT_ILLEGAL_FIELD");

	public static final FindowResponse UNKNOWN = new FindowResponse(999, "UNKNOWN");

	String status = "";
	int code = 0;

	public FindowResponse(int code, String status) {
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
