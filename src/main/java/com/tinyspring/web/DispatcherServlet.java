package com.tinyspring.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.WebXmlApplicationContext;

import com.tinyspring.web.handler.ControllerHandler;
import com.tinyspring.web.handler.IHandler;
import com.tinyspring.web.handler.SimpleViewHandler;
import com.tinyspring.web.handler.StaticResourceHandler;

public class DispatcherServlet extends HttpServlet {

	ApplicationContext applicationContext;
	
	private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
	
	private List<IHandler> handlers;
	
	private ControllerUriMapper mapper;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.dispatch(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		this.dispatch(request, response);
	}
	
	private void dispatch(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		log.debug("Processing request '" + request.getRequestURI() + "'");
		for (IHandler h : this.handlers) {
			try {
				h.handle(request, response);
			} catch (Exception e) {
				continue;
			}
			log.debug("Okay we found a handler so we are done here");
			break;
		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
		this.loadApplicationContext();
		this.initHandlers();
	}

	private void initHandlers() {
		this.handlers = new ArrayList<IHandler>();
		
		/*StaticResourceHandler staticResourceHandler = new StaticResourceHandler();
		staticResourceHandler.setServletContext(this.getServletContext());
		this.handlers.add(staticResourceHandler);*/
		
		this.handlers.add(this.applicationContext.getBean("simpleViewHandler", SimpleViewHandler.class));
		
		ControllerHandler controllerHandler = new ControllerHandler();
		controllerHandler.setApplicationContext(applicationContext);
		controllerHandler.init();
		this.handlers.add(controllerHandler);
	}
	
	private void loadApplicationContext() {
		log.debug("Context configuration path parameters is is set to '" + this.getInitParameter("contextConfigLocation") + "'");
		String config = this.getInitParameter("contextConfigLocation");
		//String config = this.getServletContext().getRealPath(this.getInitParameter("contextConfigLocation"));
		log.debug("Reading config file from path '" + config + "'");
		applicationContext = new WebXmlApplicationContext(config);
	}
}
