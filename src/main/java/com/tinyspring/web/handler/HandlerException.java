package com.tinyspring.web.handler;

public class HandlerException extends Exception {

	public HandlerException(String message) {
		super(message);
	}

	public HandlerException(Throwable throwable) {
		super(throwable);
	}

	public HandlerException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
