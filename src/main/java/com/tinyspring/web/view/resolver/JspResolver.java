package com.tinyspring.web.view.resolver;

import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyspring.web.ModelAndView;

public class JspResolver implements IResolver {

  Logger logger = LoggerFactory.getLogger(JspResolver.class);

  @Override
  public void resolve(ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws ResolveException {

    if (!mav.hasView()) {
      return;
    }

    for (Entry<String, Object> entry : mav.getModel().entrySet()) {
      request.setAttribute(entry.getKey(), entry.getValue());
    }
    String view = "/WEB-INF/jsp/" + mav.getName() + ".jsp";
    logger.debug("Forwarding to view '" + view + "'");
    try {
      request.getRequestDispatcher(view).forward(request, response);
    } catch (Exception e) {
      throw new ResolveException("Problem when resolving the view with JSP resolver", e);
    }
  }
}
