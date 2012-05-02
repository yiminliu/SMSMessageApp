package com.tscp.mvno.smpp.service;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.SubmitSM;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.tscp.mvno.smpp.AlertAction;
import com.tscp.mvno.smpp.dao.SmsDao;
import com.tscp.mvno.smpp.domain.PromAlertMessage;
import com.tscp.mvno.smpp.domain.SMSMessage;

@Service
public class DatabaseService {

    private Connection conn;
         
    @Autowired
    private SmsDao smsDao;  
    @Autowired
    private static LoggingService logger;
    
    public DatabaseService(){
       	init();
    }
    
    private void init() {
    	
    	logger = new LoggingService();
    	 smsDao = new SmsDao();  
    }   
    
    public List<SMSMessage> getSMSMessageList(AlertAction messageType) throws Exception {
		
    	List<SMSMessage> messageList = null;
    		
		switch ( messageType ) {
		  case MESSAGE_TYPE_PROM_CAPABILITY:
			  messageList = smsDao.getPromAlertMessages();
			  //messageList = processList(origList);			
		  break; 	
			
		  default:
			  messageList = null;			
		}
		return messageList;
    }
    
    private List<SubmitSM> processList(List<PromAlertMessage> origList){
    
    	List<SubmitSM> newList = new ArrayList<SubmitSM>();
   
    	try {
    	  for(PromAlertMessage pa : origList){
        	SubmitSM shortMessage = new SubmitSM();
			Address address = new Address();
			address.setAddress(pa.getExternalId());
			shortMessage.setDestination(address);
			shortMessage.setMessageText(pa.getSmsMsg());
			newList.add(shortMessage);
    	  }
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}    	
    	return newList;
    }	        
        
    private void initForTest() {
    	
    	ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");
    	smsDao = (SmsDao)appCtx.getBean("smsDao");
    }    
    
    public static void main(String[] args) { 
    	
    	//ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");
    	//promAlertDao = (PromAlertDao)appCtx.getBean("promAlertDao");
        	
    	DatabaseService ds = new DatabaseService();
    	ds.init();//ForTest();
    	//logger.info("Testing SMPP Project ConnectionInfo class....");
    	
        try{
        	List<SMSMessage> list = ds.getSMSMessageList(AlertAction.MESSAGE_TYPE_PROM_CAPABILITY);
    	    System.out.println("list size = "+ list.size());
    	}
	    catch(Exception e){
		    e.printStackTrace();
		    //logger.error("Error occured while creating connection, due to : " + e.getMessage());
	    }	   
    	//logger.info("Done Testing SMPP Project ConnectionInfo Class.");
    }    
}
