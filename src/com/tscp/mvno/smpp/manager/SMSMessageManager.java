package com.tscp.mvno.smpp.manager;

import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitSM;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tscp.mvno.smpp.AlertAction;
import com.tscp.mvno.smpp.domain.SMSMessage;
import com.tscp.mvno.smpp.service.DatabaseService;
import com.tscp.mvno.smpp.service.LoggingService;
import com.tscp.mvno.smpp.service.SMPPService;


public class SMSMessageManager {
	
	private AlertAction messageType;
	
	@Autowired
	private DatabaseService	dbService;	
	@Autowired
	private SMPPService smppService;
	@Autowired
	private LoggingService logger;
	
	public SMSMessageManager() {		
	}
	
	public SMSMessageManager(AlertAction messageType) {
		this.messageType = messageType;
	}		
    
    @PostConstruct    
	public void init() throws Exception{
				
		try{
		    ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");	
		    logger = (LoggingService)appCtx.getBean("loggingService");
		    dbService = (DatabaseService)appCtx.getBean("databaseService");	
		    smppService = (SMPPService)appCtx.getBean("smppService");		   
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error("Error occured while initializing connections, due to: " + e.getMessage());
	        throw e;
		}		
	}
	
	public void doWork() throws Exception{
		List<SMSMessage> msgList = null;
		try {            
      		logger.trace("********** Get message list ***********");
    		msgList = getMessageList(messageType);
    		
    	    logger.trace("********** Process the messages ********");
    	    processMessage(msgList);
      	}
    	catch(Exception e){
    		e.printStackTrace();
    		logger.error("Exception occured : " + e.getMessage());
    	    throw e;	
    	}    	
	}
	
    private List<SMSMessage> getMessageList(AlertAction messageType) throws Exception {
		
		return dbService.getSMSMessageList(messageType);		
	}

    private void processMessage(List<SMSMessage> smsList) throws Exception{			
		String messageId = null;				
		int messageSentCounter = 0;		
	    try { 
		    for(int j = 0; j < smsList.size(); j++ ) {
	    	    try {
	    	    	smppService.bind();	    	   
				    messageId = sendRequest(smsList.get(j).getDestinationAddress(), smsList.get(j).getMessage());
			        logger.info("Message was sent out successfully to: " + smsList.get(j).getDestinationAddress());
			    }
			    catch(Exception e){
			    	 e.printStackTrace();
				     logger.error("Exception occured in processMessage(): " + e.getMessage());
				     if( j == smsList.size() - 1 )			
					     throw e;
			    }
			    ++messageSentCounter;
		    }    
		 }
	     finally{
			//once we're done traversing the list of pending SMS messages, we want to unbind our connection.
	    	 smppService.unbind();
		}
		logger.info("Total number of the destinations being sent to the messages = " + messageSentCounter);
	}
	
	private String sendRequest(String phoneNumber, String message) throws Exception{
		
		String retValue = null;
		try {
			ie.omk.smpp.message.SubmitSM shortMsg = new SubmitSM();
			ie.omk.smpp.Address destAddress = new ie.omk.smpp.Address();
			ie.omk.smpp.Address sendAddress = new ie.omk.smpp.Address();
			
			destAddress.setAddress(phoneNumber);						
			sendAddress.setTON(0);
			sendAddress.setNPI(0);
			sendAddress.setAddress(SMPPService.getShortCode());
			
			shortMsg.setDestination(destAddress);
			shortMsg.setSource(sendAddress);
			shortMsg.setMessageText(message);			
			
			logger.info("------ SMPPRequest -------");
			//logger.info("SMPPRequest Source Address   = "+shortMsg.getSource().getAddress());
			logger.info("SMPPRequest Dest Address     = "+shortMsg.getDestination().getAddress());
			logger.info("SMPPRequest Message Text     = "+shortMsg.getMessageText());
		
			SMPPResponse smppResponse = smppService.sendRequest(shortMsg);
			
			if( smppResponse != null ) {
				logger.info("------ SMPPResponse -------");
				logger.info("SMPPResponse MessageID       = "+smppResponse.getMessageId());
				logger.info("SMPPResponse MessageStatus   = "+smppResponse.getMessageStatus());
				//logger.info("SMPPResponse Message         = "+smppResponse.getMessage());
				//logger.info("SMPPResponse MessageText     = "+smppResponse.getMessageText());
			
				if( smppResponse.getMessageId() == null || smppResponse.getMessageId().trim().length() == 0 )
					retValue = smppResponse.getMessageId();
			} else {
				logger.warn("SMPPResponse is null!!!");
			}
		} catch( Exception e ) {
			logger.error("!!Error sending request!! due to:  " + e.getMessage());
			throw e;
		}
		logger.info("Message was sent out successfully to: " + phoneNumber);
		
		return retValue;
	}	
	
	//@PreDestroy
    public void cleanUp() {
    	logger.trace("********** CleanUp **********");    
    	smppService.releaseConnection();
	}	
}
