package com.barrans.master.exception;

public enum ErrorCodeEnum {
	SUCCESS(100, "Operation Success"),
	INCORRECT_STATE(101, "Incorrect state to process"),
	INCORRECT_ARGUMENT(102, "Incorrect argument to process"),
	INTERNAL_ERROR(400, "Unable to process due to internal error"),
	UNKNOWN_ERROR(601, "Unknown error occurred, please register")
	;
	
	protected int code;
	
	protected String message;
	
	private ErrorCodeEnum(int code, String msg) {
		this.code = code;
		this.message = msg;
	}
	
	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return message + "-" + code;
	}
}
