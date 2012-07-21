package com.tinyspring.web;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

	private String name;
	
	private Map<String,Object> model;

	public ModelAndView() {
	}
	
	public ModelAndView(String name) {
		this.name = name;
		this.model = new HashMap<String,Object>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	public void setModel(Map<String, Object> model) {
		this.model = model;
	}
	
	public void addObject(String modelName, Object modelObject) {
		this.model.put(modelName, modelObject);
	}
	
	public boolean isRedirect() {
		return this.name.startsWith("redirect:");
	}
	
	public String getRedirect() {
		if (this.isRedirect()) {
			return this.name.substring("redirect:".length());
		} else {
			return null;
		}
	}
	
	public boolean hasView() {
		return this.name != null;
	}
}
