package com.tinyspring.web.view.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyspring.web.ModelAndView;

public class ErrorResolver implements IResolver {

  Logger logger = LoggerFactory.getLogger(ErrorResolver.class);

  private static final String ERROR_VIEW = "/WEB-INF/jsp/error.jsp";

  @Override
  public void resolve(ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws ResolveException {
    try {
      logger.debug("Forwarding to view '" + ERROR_VIEW + "'");
      request.getRequestDispatcher(ERROR_VIEW).forward(request, response);
    } catch (Exception e) {
      throw new ResolveException("Problem when resolving the error view with JSP resolver", e);
    }
  }
}
