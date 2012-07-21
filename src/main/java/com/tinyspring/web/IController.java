package com.tinyspring.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IController {

	public void setMapping(List<AController.Mapping> mapping);
	
	public List<AController.Mapping> getMapping();
	
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response);
}
