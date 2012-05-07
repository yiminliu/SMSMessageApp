package com.tscp.mvno.smpp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tscp.mvno.smpp.manager.SMSMessageManager;
import com.tscp.mvno.smpp.service.DatabaseService;
import com.tscp.mvno.smpp.service.LoggingService;
import com.tscp.mvno.smpp.service.SMPPService;

public class SMSClient {
	
    public static void main(String[] args) { 
    	
    	System.out.println("********** Start SMPP MessageProcessor *********");
    	   	
    	if (args.length != 1) {
            System.err.println("Usage: java SMSMessageManager actionCode");
            System.out.println("Exit the process");
            System.exit(-1);
        }
        	
    	try {           		    		
    		SMSMessageManager messageManager = new SMSMessageManager(determineAction(args));
    		   		
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
	   
	      for(AlertAction aa : AlertAction.values()){
		      if(code == aa.getActionCode()) {
		         type = aa;	
		         break;
		      }   
	      }
	   }  
	   if(type == null)
	      throw new Exception("Error with the input action code: " + args[0] +", Please enter a valid numeric value as the action code");
	   else {
		   System.err.println("Action type: " + type);
		   return type;
	   }	   
   }	

}
