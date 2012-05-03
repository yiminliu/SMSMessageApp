package com.tscp.mvno.smpp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class ActivationAlertMessage implements SMSMessage{

	@Id
	@Column(name="EXTERNAL_ID")
	private String externalId;
		
	@Column(name="TEXT_MESSAGE")
	private String smsMsg;
		
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	public String getSmsMsg() {
		return smsMsg;
	}

	public void setSmsMsg(String smsMsg) {
		this.smsMsg = smsMsg;
	}
	
	//@Override	
	public String getDestinationAddress(){
		return getExternalId();
	}
	
	//@Override
	public String getMessage(){
		return getSmsMsg();
	}	
}
