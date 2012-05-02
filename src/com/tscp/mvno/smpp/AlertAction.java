package com.tscp.mvno.smpp;

import org.springframework.stereotype.Component;

public enum AlertAction {
	
	/*MESSAGE_TYPE_TEST_SMS, 
	MESSAGE_TYPE_PMT_MADE, 
	MESSAGE_TYPE_BAL_ALERT, 
	MESSAGE_TYPE_TEXT_ALERT, 
	MESSAGE_TYPE_DID_ALERT_A, 
	MESSAGE_TYPE_DID_ALERT_B, 
	MESSAGE_TYPE_LOSE_MDN, 
	MESSAGE_TYPE_JUL09_BLAST, 
	ESSAGE_TYPE_SENDFAILED, 
	MESSAGE_TYPE_FLEXMSGLIST, 
	*/
	
	MESSAGE_TYPE_ACTIVATION(100, "get_activation_sms"), 
	MESSAGE_TYPE_PROM_CAPABILITY(110, "get_marketing_sms"); 
	
	private int actionCode;
    private final String actionProcedureName;
	
	AlertAction(int actionCode, String actionProcedureName){
	   this.actionCode = actionCode;
	   this.actionProcedureName = actionProcedureName;
	}
	
	public int getActionCode(){
		return actionCode;
	}
	
	public String getActionProcedureName(){
		return actionProcedureName;
	}
	
}
