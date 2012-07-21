package org.springframework.context.support;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class WebXmlApplicationContext extends AbstractXmlApplicationContext {

	private static final Logger log = LoggerFactory.getLogger(WebXmlApplicationContext.class);

	/**
	 * Create a new ClassPathXmlApplicationContext for bean-style configuration.
	 * 
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public WebXmlApplicationContext() {
		super();
	}

	/**
	 * Create a new ClassPathXmlApplicationContext, loading the definitions from
	 * the given XML file and automatically refreshing the context.
	 * 
	 * @param configLocation
	 *            resource location
	 * @throws BeansException
	 *             if context creation failed
	 */
	public WebXmlApplicationContext(String configLocation) {
		super(configLocation);
	}

	protected void processContext(String location) {
		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		path = path.substring(0, path.indexOf("WEB-INF") + 8);
		super.processContext(path + location);
	}
}
