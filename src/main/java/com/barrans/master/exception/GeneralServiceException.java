package com.barrans.master.exception;

public class GeneralServiceException extends RuntimeException {

	private ErrorCodeEnum err;
	
	public GeneralServiceException(ErrorCodeEnum err, String message) {
		super(message);
		
		this.err = err;
	}
	
	public GeneralServiceException(ErrorCodeEnum err, String message, Throwable th) {
		super(message, th);
		
		this.err = err;
	}
	
	public GeneralServiceException(ErrorCodeEnum err) {
		this.err = err;
	}

	public int getCode() {
		return err.code;
	}
	
	public String getDefaultMessage() {
		return err.message;
	}
}
