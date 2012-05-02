package com.tscp.mvno.smpp.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
//import org.hibernate.service.ServiceRegistry;
//import org.hibernate.service.ServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tscp.mvno.smpp.dao.SmsDao;
import com.tscp.mvno.smpp.domain.PromAlertMessage;
import com.tscp.mvno.smpp.service.LoggingService;

@Service
public class HibernateUtil {

	private static final String hibernateConfigurationFile = "hibernate.cfg.xml";
	private static SessionFactory sessionFactory;
	@Autowired(required=false)
    private static LoggingService logger;
		
	static{
		if (logger==null)
			logger = new LoggingService();
	}
	
	public static SessionFactory getSessionFactory() {
	    if (sessionFactory == null)
	     	sessionFactory = buildSessionFactory();
	    
		return sessionFactory;
	}
	
	private static SessionFactory buildSessionFactory() {
		
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			//Configuration config = getAnnotationConfiguration();
			Configuration config = new Configuration();//.configure()(hibernateConfigurationFile);
			
	    	addAnnotationClasses(config);
	    	
	    	config.configure(hibernateConfigurationFile);
	    	
	    	//ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();        
	    	//SessionFactory sessionFactory = config.buildSessionFactory(serviceRegistry);	       
	    	
			SessionFactory sessionFactory = config.buildSessionFactory();
		
			return sessionFactory;
		} 
		catch (Throwable ex) {
			ex.printStackTrace();
			// Make sure you log the exception, as it might be swallowed...
			logger.error("Initial SessionFactory creation failed..." + ex);			
			throw new ExceptionInInitializerError(ex);
		}
	}
        
    public static Configuration getAnnotationConfiguration() {

    	Configuration config = new Configuration().configure();//(hibernateConfigurationFile);
		
    	addAnnotationClasses(config);
    	
    	config.configure(hibernateConfigurationFile);
		
		return config;
	}    
    
    public static Configuration addAnnotationClasses(Configuration config) {        	
    		config.addClass(PromAlertMessage.class); 
    		//config.addAnnotatedClass(SmsDao.class);    		
    		return config;
    }	
		
	/**
	 * Used to close an open session and catch any exceptions during the process.
	 * 
	 * @param session
	 * 
	 */
	public static void closeSession(Session session) throws RuntimeException {
		if (session.isOpen()) {
			try {
				session.close();
			} catch (HibernateException e) {
				logger.error("Error closing session. " + e.getMessage());
			}
		}
	}

	/**
	 * Used to commit transactions and catch any exceptions during the process.
	 * 
	 * @param transaction
	 * @throws RuntimeException
	 */
	public static void commitTransaction(Transaction transaction) throws RuntimeException {
		if (transaction != null && transaction.isActive()) {
			try {
				transaction.commit();
			} catch (HibernateException e) {
				logger.error("Error commiting back transaction. " + e.getMessage());
			}
		}
	}

	/**
	 * Used to rollback a transaction and catch any exceptions during the process. 
	 * @param transaction
	 * @throws RuntimeException
	 */
	public static void rollbackTransaction(Transaction transaction) throws RuntimeException {
		if (transaction != null && transaction.isActive()) {
			try {
				transaction.rollback();
			} catch (HibernateException e) {
				 logger.error("Error rolling back transaction. " + e.getMessage());
			}
		}
	}

	public static void rollbackTransaction(Object obj, String methodName, Transaction transaction, RuntimeException re) throws RuntimeException {
		logger.error(obj, methodName, "Error rolling back transaction. " + (re == null? "" : re.getMessage()));
		if (transaction != null && transaction.isActive()) {
			try {
				transaction.rollback();
			} catch (HibernateException e) {
				 logger.error(obj, methodName, "Error rolling back transaction. " + e.getMessage());
			}
		}		
		//throw re;
	}
	
	public static void rollbackTransaction(Transaction transaction, String errorMessage) throws RuntimeException {
		logger.error(errorMessage);
		if (transaction != null && transaction.isActive()) {
			try {
				transaction.rollback();
			} catch (HibernateException e) {
				 logger.error("Error rolling back transaction. " + e.getMessage());
			}
		}
	}

	public static void main(String[] args){
		//test SessionFactory
		SessionFactory sf = getSessionFactory();
		System.out.println("SessionFactory created: " + sf.toString());
		//test Session
		Session s = sf.getCurrentSession();
		System.out.println("Session created: " + s.toString());
		//test transaction		
		s.beginTransaction();
		System.out.println("s.isConnected(): " + s.isConnected());
	}
}
