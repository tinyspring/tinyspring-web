package com.tinyspring.web;

import java.util.List;

public abstract class AController implements IController {

	public static class Mapping {
	
		private String method;
		
		private String uri;
		
		private String type;

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
	
	private List<Mapping> mapping;
	
	public void setMapping(List<Mapping> mapping) {
		this.mapping = mapping;
	}
	
	public List<Mapping> getMapping() {
		return this.mapping;
	}
}
