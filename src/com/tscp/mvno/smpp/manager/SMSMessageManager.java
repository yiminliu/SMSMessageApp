package com.tscp.mvno.smpp.manager;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitSM;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.tscp.mvno.smpp.AlertAction;
import com.tscp.mvno.smpp.domain.SMSMessage;
import com.tscp.mvno.smpp.service.DatabaseService;
import com.tscp.mvno.smpp.service.LoggingService;
import com.tscp.mvno.smpp.service.MessageSupportService;
import com.tscp.mvno.smpp.service.SMPPService;


@Component
public class SMSMessageManager {
			
	private static boolean iniliazed = false;
		
	private AlertAction messageType;
	
	@Autowired
	private DatabaseService	dbService;	
	@Autowired
	private SMPPService smppService;
	@Autowired
	private LoggingService logger;
	@Autowired
	private MessageSupportService messageSupportService;

	private Connection smppConnection;
			
	public SMSMessageManager() {
	}
	
	public SMSMessageManager(AlertAction messageType) {
		this.messageType = messageType;
	}
	
    public static void main(String[] args) { 
    	
    	System.out.println("********** Start SMPP MessageProcessor *********");
    	   	
    	if (args.length != 1) {
            System.err.println("Usage: java SMSMessageManager actionCode");
            System.out.println("Exit the process");
            System.exit(-1);
        }
    	   	    	
    	try {       
    		
    		SMSMessageManager messageManager = new SMSMessageManager(determineAction(args));
    		
    		if(iniliazed == false) 
    		   messageManager.init();    
    	
    		messageManager.doWork();   			    
    	    
    		messageManager.cleanUp();   
    	}
    	catch(Throwable t){
    		t.printStackTrace();
    		System.out.println("Exit the process due to an exception occured : " + t.getMessage());
    	    System.exit(1);	
    	}
    	System.out.println("********** Done SMPP MessageProcessor *********");
    	System.out.println("**********************************************");
    	System.exit(0);
    }

    //@PostConstruct    
	public void init() throws Exception{
				
		try{
		    ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");		    
		    dbService = (DatabaseService)appCtx.getBean("databaseService");	
		    smppService = (SMPPService)appCtx.getBean("SMPPService");
		    logger = (LoggingService)appCtx.getBean("loggingService");
			messageSupportService = (MessageSupportService) appCtx.getBean("messageSupportService");
		    smppConnection = SMPPService.makeConnection();
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error("Error occured while initializing connections, due to: " + e.getMessage());
	        throw e;
		}		
		iniliazed = true;
	}
	
	private void doWork() throws Exception{
		List<SMSMessage> msgList = null;
		try {            
      		logger.trace("********** Get message list ***********");
    		msgList = getMessageList(messageType);
    		
    	    logger.trace("********** Process the messages ********");
    	    processMessage(msgList);
      	}
    	catch(Exception e){
    		logger.error("Exception occured : " + e.getMessage());
    	    throw e;	
    	}    	
	}
	
    private List<SMSMessage> getMessageList(AlertAction messageType) throws Exception {
		
		List<SMSMessage> messageList = dbService.getSMSMessageList(messageType);
		
		logger.info("SMS List returned with "+messageList.size()+" elements.");
	
		return messageList;
	}

   public void processMessage(List<SMSMessage> smsList) throws Exception{			
		String messageId = null;				
		int messageSentCounter = 0;		
	    try { 
		    for( int j = 0; j < smsList.size(); j++ ) {
		        //String messageBlockingSOC = "";
				try {
			    	if(!smppConnection.isBound()){ // smpp connection is unbound, need to bind
				       SMPPService.bind(smppConnection);
			        }
				    messageId = sendRequest(smsList.get(j).getDestinationAddress(), smsList.get(j).getMessage());
			        logger.info("Message was sent out successfully to: " + smsList.get(j).getDestinationAddress());
			    }
			    catch(Exception e){
				     logger.error("Exception occured in processMessage(): " + e.getMessage());
				     if( j == smsList.size() - 1 )			
					     throw e;
			    }
			    ++messageSentCounter;
		    }    
		 }
	     finally{
			//once we're done traversing the list of pending SMS messages, we want to unbind our connection.
	    	 SMPPService.unbind(smppConnection);
		}
		logger.info("Total number of the destinations being sent to the messages = " + messageSentCounter);
	}
	
	private String sendRequest(String phoneNumber, String message) throws Exception{
		
		String retValue = null;
		try {
//			SMPPRequest smppRequest;
			ie.omk.smpp.message.SubmitSM shortMsg = new SubmitSM();
			ie.omk.smpp.Address destAddress = new ie.omk.smpp.Address();
			ie.omk.smpp.Address sendAddress = new ie.omk.smpp.Address();
			
			destAddress.setAddress(phoneNumber);
			//for test
			//destAddress.setAddress("2132566431");			
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
		
			SMPPResponse smppResponse = smppConnection.sendRequest(shortMsg);
			
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
		return retValue;
	}
	
	private static AlertAction determineAction(String args[]) throws Exception{
		int code = -1;
		AlertAction type = null;
		
		if(args != null && args.length > 0){  	
		   try{	
		       code = Integer.parseInt(args[0]);
		   }
		   catch(NumberFormatException nfe){
			   throw new Exception("NumberFormatException occured processing input action code: " + nfe.getMessage());
		   }
		   
		   switch(code) {
		      case 100:
			      type = AlertAction.MESSAGE_TYPE_ACTIVATION;
		      break;
		   
		      case 110:
			      type = AlertAction.MESSAGE_TYPE_PROM_CAPABILITY;
		      break;
		      default:
		    	  throw new Exception("Error with the input action code: " + args[0] +", Please input a valid numeric value as the action code");
		   }	   
		}  		
		return type;
	}
	
	
	//@PreDestroy
    private void cleanUp() {
    	logger.trace("********** CleanUp **********");    
		SMPPService.releaseConnection(smppConnection);
	}	
}
