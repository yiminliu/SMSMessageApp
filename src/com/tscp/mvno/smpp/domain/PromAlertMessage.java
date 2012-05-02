package com.tscp.mvno.smpp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class PromAlertMessage implements SMSMessage{

	@Id
	@Column(name="account_no")
	private int accountNo;
	
	@Column(name="account_category")
	private String accountCategory;

	@Column(name="external_id")
	private String externalId;
	
	@Column(name="language_code")
	private int languageCode;
	
	@Column(name="sms_msg")
	private String smsMsg;
	
	public int getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(int accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountCategory() {
		return accountCategory;
	}

	public void setAccountCategory(String accountCategory) {
		this.accountCategory = accountCategory;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public int getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(int languageCode) {
		this.languageCode = languageCode;
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
