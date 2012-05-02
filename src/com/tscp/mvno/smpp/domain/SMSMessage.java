package com.tscp.mvno.smpp.domain;

public interface SMSMessage {
	public String getDestinationAddress();
	public String getMessage();
}
