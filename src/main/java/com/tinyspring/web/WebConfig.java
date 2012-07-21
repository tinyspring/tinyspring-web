package com.tinyspring.web;

import java.util.List;
import java.util.Map;

public class WebConfig {

	private Map<IController,List<String>> mapping;

	public Map<IController, List<String>> getMapping() {
		return mapping;
	}

	public void setMapping(Map<IController, List<String>> mapping) {
		this.mapping = mapping;
	}

	private Map<String,String> test;

	public Map<String, String> getTest() {
		return test;
	}

	public void setTest(Map<String, String> test) {
		this.test = test;
	}
}
