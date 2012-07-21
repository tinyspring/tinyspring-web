package com.tinyspring.web;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ControllerUriMapper implements ApplicationContextAware {

	public static class Mapping {

		private AController.Mapping config;

		private IController controller;

		private Method method;

		public AController.Mapping getConfig() {
			return config;
		}

		public void setConfig(AController.Mapping config) {
			this.config = config;
		}

		public Mapping(IController controller, Method method) {
			this.controller = controller;
			this.method = method;
		}

		public IController getController() {
			return controller;
		}

		public void setController(IController controller) {
			this.controller = controller;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}
	}

	private static final Logger log = LoggerFactory.getLogger(ControllerUriMapper.class);

	private ApplicationContext applicationContext;

	private Map<Pattern, Mapping> mapping;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void init() {
		Collection<IController> controllers = this.applicationContext.getBeansOfType(IController.class).values();
		if (controllers != null) {
			Pattern groups = Pattern.compile("(\\([^\\(]+\\))");
			Matcher match;
			this.mapping = new LinkedHashMap<Pattern, Mapping>();
			for (IController controller : controllers) {
				for (AController.Mapping m : controller.getMapping()) {
					int definedParameters = 0;
					match = groups.matcher(m.getUri());
					while (match.find()) {
						// log.debug(m + " : " + match.group());
						definedParameters++;
					}
					log.debug("Matching pattern '" + m + "', found '" + definedParameters + "' groups");

					Mapping mapper = null;
					Method[] methods = controller.getClass().getMethods();
					for (Method method : methods) {
						log.debug("Search for match for method '" + method.getName() + "'");
						if (!StringUtils.isBlank(m.getMethod()) && !m.getMethod().equals(method.getName())) {
							log.debug("The method name '" + method.getName() + "' differs from the declared name '" + m.getMethod() + "'");
							continue;
						}

						if (!Modifier.isPublic(method.getModifiers())) {
							log.debug("The method is not public");
							continue;
						}

						if (!method.getReturnType().isAssignableFrom(ModelAndView.class)) {
							log.debug("The method doesn't return subclass of '" + ModelAndView.class + "'");
							continue;
						}

						Class[] parameterTypes = method.getParameterTypes();
						int i;
						boolean parametersAssignable = true;
						for (i = 0; i < definedParameters; i++) {
							if (!parameterTypes[i].isAssignableFrom(String.class)) {
								parametersAssignable = false;
								break;
							}
						}

						if (!parametersAssignable) {
							log.debug("Parameters are not assignable");
							continue;
						}

						if (!parameterTypes[i].isAssignableFrom(HttpServletRequest.class)) {
							log.debug("The '" + HttpServletRequest.class + "' is not assignable");
							continue;
						}

						if (!parameterTypes[++i].isAssignableFrom(HttpServletResponse.class)) {
							log.debug("The '" + HttpServletResponse.class + "' is not assignable");
							continue;
						}

						log.debug("Found a matching method for given URI definition '" + method + "'");
						mapper = new Mapping(controller, method);
						break;
					}

					if (mapper == null) {
						mapper = new Mapping(controller, null);
					}
					log.debug("Adding mapping configuration with url '" + m + "' and mapper '" + mapper + "'");
					this.mapping.put(Pattern.compile(m.getUri()), mapper);
				}
			}
		}
	}

	public ModelAndView processRequest(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();
		if (!StringUtils.isBlank(request.getQueryString())) {
			uri += "?" + request.getQueryString();
		}

		ModelAndView mav = null;
		for (Entry<Pattern, Mapping> entry : this.mapping.entrySet()) {
			Matcher m = null;
			try {
				m = entry.getKey().matcher(uri);
			} catch (Exception e) {

			}

			if (m.matches()) {
				if (entry.getValue().getMethod() != null && (m.groupCount() == entry.getValue().getMethod().getParameterTypes().length - 2)) {
					try {
						Object[] params = new Object[entry.getValue().getMethod().getParameterTypes().length];
						int i;
						for (i = 0; i < m.groupCount(); i++) {
							params[i] = m.group(i + 1);
						}
						params[i] = request;
						params[i + 1] = response;
						mav = (ModelAndView) entry.getValue().getMethod().invoke(entry.getValue().getController(), params);
					} catch (Exception e) {
						log.debug("Problem when mapping controller", e);
					}
				}
				if (mav == null) {
					// fallback to default controller handle method
					log.debug("Fallback to standard index method");
					mav = entry.getValue().getController().handle(request, response);
				}
				break;
			}
		}
		return mav;
	}
}
