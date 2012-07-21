package com.tinyspring.web.handler;

import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.tinyspring.web.ControllerUriMapper;
import com.tinyspring.web.ModelAndView;

public class ControllerHandler implements IHandler, ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(ControllerHandler.class);
	
	ApplicationContext applicationContext;
	
	ControllerUriMapper uriMapper;
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws HandlerException {
		System.out.println("Controller handling: " + request.getRequestURL());
		ModelAndView mav = this.uriMapper.processRequest(request, response);
		
		if (mav == null) {
			mav = new ModelAndView("404");
		}
		
		if (!mav.hasView()) {
			return;
		}
		
		try {
			if (mav.isRedirect()) {
				response.sendRedirect(mav.getRedirect());
			} else {
				for (Entry<String,Object> entry : mav.getModel().entrySet()) {
					request.setAttribute(entry.getKey(), entry.getValue());
				}
				String view = "/WEB-INF/jsp/" + mav.getName() + ".jsp";
				log.debug("Forwarding to view '" + view + "'");
				request.getRequestDispatcher(view).forward(request, response);
			}
		} catch (Exception e) {
			throw new HandlerException("Problem when forwarding to view from controller", e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void init() {
		this.uriMapper = this.applicationContext.getBean("uriMapper", ControllerUriMapper.class);
	}
}
