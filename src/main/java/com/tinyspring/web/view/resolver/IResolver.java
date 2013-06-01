package com.tinyspring.web.view.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinyspring.web.ModelAndView;

public interface IResolver {

  public void resolve(ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws ResolveException;
}
