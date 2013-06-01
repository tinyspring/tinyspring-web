package com.tinyspring.web.view.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyspring.web.ModelAndView;

public class JsonResolver implements IResolver {

  ObjectMapper jackson = new ObjectMapper();

  Logger logger = LoggerFactory.getLogger(JsonResolver.class);

  @Override
  public void resolve(ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws ResolveException {
    if (mav == null || mav.getModel() == null) {
      logger.debug("The MAV is empty, nothing to print out");
      return;
    }
    try {
      response.setContentType("text/html; charset=UTF-8");
      response.setCharacterEncoding("UTF-8");
      if (mav.getModel().size() == 1) {
        response.getWriter().write(jackson.writeValueAsString(mav.getModel().values().iterator().next()));
      } else {
        response.getWriter().write(jackson.writeValueAsString(mav.getModel()));
      }
    } catch (Exception e) {
      throw new ResolveException("Problem when writing JSON output in JSON resolver", e);
    }
  }
}
