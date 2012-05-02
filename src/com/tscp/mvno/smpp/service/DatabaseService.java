package com.tscp.mvno.smpp.service;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.SubmitSM;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.tscp.mvno.smpp.AlertAction;
import com.tscp.mvno.smpp.dao.SmsDao;
import com.tscp.mvno.smpp.domain.SMSMessage;

@Service()
@Scope("singleton")
public class DatabaseService {

    @Autowired
    private SmsDao smsDao;  
    @Autowired
    private LoggingService logger;
    
    public DatabaseService(){}
       
    public List<SMSMessage> getSMSMessageList(AlertAction messageType) throws Exception {
		
    	return smsDao.getAlertMessages(messageType.getActionProcedureName());
    }	
    	/*	
		switch ( messageType ) {
		  case MESSAGE_TYPE_PROM_CAPABILITY:
			  //messageList = smsDao.getAlertMessages("get_marketing_sms");
			  messageList = smsDao.getAlertMessages("get_marketing_sms");
		  break; 	
		  
		  case MESSAGE_TYPE_ACTIVATION:
			  messageList = smsDao.getAlertMessages("get_activation_sms");
		  break;
			
		  default:
			  messageList = null;			
		}
		*/		
            
    private void initForTest() {
    	
    	ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");
    	smsDao = (SmsDao)appCtx.getBean("smsDao");
    	logger = (LoggingService)appCtx.getBean("loggingService");
    }    
    
    public static void main(String[] args) { 
    	    	  	
    	DatabaseService ds = new DatabaseService();
    	ds.initForTest();
    	System.out.println("Testing SMPP Project ConnectionInfo class....");
    	
        try{
        	List<SMSMessage> list = ds.getSMSMessageList(AlertAction.MESSAGE_TYPE_PROM_CAPABILITY);
    	    System.out.println("list size = "+ list.size());
    	}
	    catch(Exception e){
		    e.printStackTrace();
		    System.out.println("Error occured while creating connection, due to : " + e.getMessage());
	    }	   
	    System.out.println("Done Testing SMPP Project ConnectionInfo Class.");
    }    
}
