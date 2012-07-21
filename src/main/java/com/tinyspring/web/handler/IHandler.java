package com.tinyspring.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IHandler {

	public void handle(HttpServletRequest request, HttpServletResponse response) throws HandlerException;
}
