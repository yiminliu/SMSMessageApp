package com.tscp.mvno.smpp.service;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


//@SuppressWarnings("all")
@Service
public class LoggingService {
	
	public static String inputProperties = "logConfig.properties";
	
	private static Log		 logger = LogFactory.getLog("");
	private static String	 logConfigFile= null;//"log4j.xml";
	
	static{
		try {
			Properties props = new Properties();
			ClassLoader cl = LoggingService.class.getClassLoader();
			
			InputStream in = cl.getResourceAsStream(inputProperties);						
			if(in != null) {
			   props.load(in);
			}			
			logConfigFile 		= props.getProperty("fileName", "config/log4j.xml");
			//logConfigFile 		= "./conf/log4j.xml";
			
			DOMConfigurator.configure(logConfigFile);
		} catch( Exception e ) {
			logger.error("Error loading properties file!! due to: " + e.getMessage());			
		}
	}	
	
	public LoggingService(){	   
	}
	
	public void debug(String message) {
		logger.debug(message);
	}

	public void trace(String message) {
		logger.trace(message);
	}

	public void info(String message) {
		logger.info(message);
	}

	public void warn(String message) {
		logger.warn(message);
	}
	
	public void warn(String message, Throwable throwable) {
		logger.warn(message, throwable);
	}
		
	public void error(String message) {
		logger.error(message);
	}
		
	public void fatal(String message) {
		logger.fatal(message);
	}

	public void debug(Object obj, String message) {
		logger.debug(obj.getClass().getName()+": "+ message);
	}

	public void trace(Object obj,String message) {
		logger.trace(obj.getClass().getName()+": "+ message);
	}

	public void info(Object obj, String message) {
		logger.info((obj == null? "" : obj.getClass().getName())+": "+ message);
	}
	
	public void info(Object obj, String methodName, String message) {
		logger.info((obj == null? "" : obj.getClass().getName())+ ": "+ methodName + ". returned value = " +message);
	}

	public void warn(Object obj,String message) {
		logger.warn(obj.getClass().getName()+": "+ message);
	}
	
	public void warn(Object obj, String message, Throwable throwable) {
		logger.warn(obj.getClass().getName()+": "+ message, throwable);
	}
	
	public void warn(Object obj, String methodName, String message) {
		logger.error(obj == null? "" : obj.getClass().getName()+"."+ methodName + "():"+ message);
	}

	public void error(Object obj, String methodName, String message) {
		logger.error(obj == null? "" : obj.getClass().getName()+"."+ methodName + "():"+ message);
	}
		
	public void error(Object obj, String methodName, String tscpmvneErrorCode, String tscpmvneErrorMessage, Throwable causeException){
		if(causeException != null) 
		   logger.error(obj == null? "" : obj.getClass().getName()+"."+ methodName + "(): " + tscpmvneErrorCode + "--" + tscpmvneErrorMessage +". Caused by: "+ causeException.getMessage()); 	
		else
			logger.error(obj == null? "" : obj.getClass().getName()+"."+ methodName + "(): " + tscpmvneErrorCode + "--" + tscpmvneErrorMessage); 	
	}	
	
	public void fatal(Object obj, String message) {
		logger.fatal(message);
	}

}

