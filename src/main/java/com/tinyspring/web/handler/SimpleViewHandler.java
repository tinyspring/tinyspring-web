package com.tinyspring.web.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleViewHandler implements IHandler {

	private static final Logger log = LoggerFactory.getLogger(SimpleViewHandler.class);
	
	private Map<String,String> mapping;
	
	// @TODO Ugly hack since the config doesnt know how to deal with map entries
	public void setMapping(List<String> mapping) {
		this.mapping = new HashMap<String, String>();
		if (mapping != null) {
			for (String m : mapping) {
				log.debug("Adding simple view mapping for url '" + m + "' and view '" + m.substring(1) + "index.jsp" + "'");
				this.mapping.put(m, m.substring(1) + "index.jsp");
			}
		}
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws HandlerException {
		System.out.println("Simple view handling: " + request.getRequestURI());
		
		if (this.mapping == null) {
			throw new HandlerException("The mapping for simple wasn't initialized!");
		}
		
		String m = this.mapping.get(request.getRequestURI().toString());
		
		if (StringUtils.isBlank(m)) {
			throw new HandlerException("This request is not mapped to simple view");
		}
		
		try {
			String view = "/WEB-INF/jsp/" + m;
			log.debug("Forwarding to view '" + view + "'");
			request.getRequestDispatcher(view).forward(request, response);	
		} catch (Exception e) {
			throw new HandlerException("Problem when forwarding to simple view", e);
		}
	}
}
