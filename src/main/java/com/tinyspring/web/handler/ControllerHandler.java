package com.tinyspring.web.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.tinyspring.web.ControllerUriMapper;
import com.tinyspring.web.ModelAndView;
import com.tinyspring.web.view.resolver.ErrorResolver;
import com.tinyspring.web.view.resolver.IResolver;
import com.tinyspring.web.view.resolver.JsonResolver;
import com.tinyspring.web.view.resolver.JspResolver;

public class ControllerHandler implements IHandler, ApplicationContextAware {

  private static final Logger logger = LoggerFactory.getLogger(ControllerHandler.class);

  private static final String REQUEST_HEADER_ACCEPT = "Accept";

  private static final String REQUEST_HEADER_ACCEPT_JSON = "application/json";

  private static final String REQUEST_HEADER_ACCEPT_TEXT = "text/plain";

  private static final String REQUEST_HEADER_ACCEPT_HTML = "text/html";

  private final IResolver errorResolver = new ErrorResolver();

  private static final Map<String, IResolver> resolvers = new LinkedHashMap<String, IResolver>();

  {
    IResolver jsonResolver = new JsonResolver();
    resolvers.put(REQUEST_HEADER_ACCEPT_JSON, jsonResolver);

    IResolver jspResolver = new JspResolver();
    resolvers.put(REQUEST_HEADER_ACCEPT_TEXT, jspResolver);
    resolvers.put(REQUEST_HEADER_ACCEPT_HTML, jspResolver);
  }

  ApplicationContext applicationContext;

  ControllerUriMapper uriMapper;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response) throws HandlerException {
    System.out.println("Controller handling: " + request.getRequestURL());
    ModelAndView mav = this.uriMapper.processRequest(request, response);

    try {
      if (mav == null) {
        logger.debug("Model&View is null - calling 'error' resolver");
        errorResolver.resolve(mav, request, response);
        return;
      }

      if (mav.isRedirect()) {
        logger.debug("Model&View is redirect - redirecting to {}", mav.getRedirect());
        response.sendRedirect(mav.getRedirect());
        return;
      }

      String accept = request.getHeader(REQUEST_HEADER_ACCEPT) != null ? request.getHeader(REQUEST_HEADER_ACCEPT) : "";
      logger.debug("Searching for resolver with request Accept header {}", accept);
      IResolver resolver = null;
      for (Entry<String, IResolver> entry : resolvers.entrySet()) {
        if (accept.contains(entry.getKey())) {
          resolver = entry.getValue();
          break;
        }
      }

      if (resolver == null) {
        // fall back to html resolver
        logger.debug("Couldn't find resolver for {} - falling back to default html resolver", accept);
        resolver = resolvers.get(REQUEST_HEADER_ACCEPT_HTML);
      }
      logger.debug("Calling {} resolver with mav {}", resolver, mav);
      resolver.resolve(mav, request, response);
    } catch (Exception e) {
      logger.error("Problem when resolving the view", e);
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
