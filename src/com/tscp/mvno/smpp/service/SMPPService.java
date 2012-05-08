package com.tscp.mvno.smpp.service;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.net.TcpLink;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


/**
 * Class designed to open the connection using Sprint as the SMSC
 *
 */

@Service("smppService")
public class SMPPService {
 
    public static String inputProperties = "client.properties";
	   
	private static String		sprintSmscSite;
	private static int			sprintSmscPort;		
	private static String		telscapeUserName;
	private static String		telscapePassword;
	private static String		systemType;
	private static int			maximumMessageCount;
	private static String 		shortCode;
	
    //@Autowired
    private LoggingService logger = new LoggingService();
    	    
	private Connection 	smppConnection = null;
		
			
	SMPPService() throws Exception{
		
		init();
		
		try {
		    smppConnection = makeConnection();
		}
		catch(Exception e) {
			throw e;
		}
	}
	
	private void init() {
		try {
			Properties props = new Properties();
			ClassLoader cl = SMPPService.class.getClassLoader();
			InputStream in = cl.getResourceAsStream(inputProperties);
						
			if(in != null) {
			   props.load(in);
			}
			
			sprintSmscSite 		= props.getProperty("SMSC.URL", "68.28.216.140");
		    sprintSmscPort 		= Integer.parseInt(props.getProperty("SMSC.PORT", "16910"));
		    telscapeUserName	= props.getProperty("TSCP.USERNAME", "tscp");
		    telscapePassword	= props.getProperty("TSCP.PASSWORD", "tscp2008");
		    systemType			= props.getProperty("SYSTEM.TYPE", "systype");
		    maximumMessageCount	= Integer.parseInt(props.getProperty("MESSAGE.MAXIMUM","100"));
		    shortCode			= props.getProperty("SHORT.CODE", "87276");			    
		} catch( Exception e ) {
			logger.info("Error loading properties file!! due to: " + e.getMessage());			
		}
	}
	
	public Connection makeConnection() throws UnknownHostException, Exception{
				
		TcpLink		tcpLink = null;	
		
		logger.info("Connecting to SMSC...");
		
		try {		
			tcpLink = new TcpLink(sprintSmscSite,sprintSmscPort);
			logger.info("TcpLink established to SMSC server: "+sprintSmscSite + " on port: " + sprintSmscPort);
    	} 
		catch(UnknownHostException uhe ) {
			uhe.printStackTrace();
			throw uhe;
		}
    					
		try {
		    smppConnection = new Connection(tcpLink);   
		    smppConnection.autoAckMessages(true);
		    logger.info("SMPP Connection established"); //0-UNBOUND, 1-BINDING, 2-BOUND
		}
	    catch(Exception e ) {
	    	releaseConnection();
		    logger.error("Exception occured during connecting to SMSC server, due to: " + e.getMessage());
		    throw e;
	    }		
	   	    
	    return smppConnection;
	
	}  
	
	/**
	 * Binding in SMPP is the same as logging into the remote SMSC.
	 * <p>Important parameters for Connection.bind() are:
	 * <table border=true>
	 * 	<th>Field</th><th>datatype</th>
	 * 	<tr><td>Connection Type</td><td align=center>int</td></tr>
	 * 	<tr><td>System ID</td><td align=center>String</td></tr>
	 * 	<tr><td>Password</td><td align=center>String</td></tr>
	 * 	<tr><td>System Type</td><td align=center>String</td></tr>
	 * </table>
	 * </p>
	 * @return
	 */
	//public static boolean bind(Connection smppConnection) throws Exception{
	public boolean bind() throws Exception{
		boolean retValue = false;
		logger.info("**** Binding SMPP Connection ****");
		try {
			if( !smppConnection.isBound() ) {
								
				BindResp response = null;
			
				try{				   	
				   response = smppConnection.bind(Connection.TRANSMITTER, telscapeUserName, telscapePassword, systemType);
				}
				catch(Exception e){
					logger.error("Exception occured while binding, due to "+ e.getMessage());
				}
				if( response != null ) {
					logger.info("Binding response is not null...");
					logger.info("Binding response System ID :: "+response.getSystemId());
					logger.info("Binding response Destination :: "+response.getDestination());
				}
			} 
			else {
				logger.info("SMPP Connection is already bound");
			}
			retValue = true;
		} catch ( Exception e ) {
			logger.error("!!Binding Exception Occurred, due to: " + e.getMessage());
			releaseConnection();
			throw e;
		} 
		return retValue;
	}
	
	//public static boolean unbind(Connection smppConnection) { 
	public boolean unbind() { 
		boolean retValue = false;
		try {
			if( smppConnection.isBound() ) {
				logger.info("Attempting to unbind SMPP Connection");
				smppConnection.unbind();
			} else {
				logger.info("SMPP Application is unbound or already unbound ");
				return retValue;
			}
			retValue = true;
			logger.info("SMPP Connection Unbound successfully");
		} catch ( Exception e ) {
			logger.error("!!UnBinding Exception Occurred, due to: "+ e.getMessage());
		}
		return retValue;
	}
	
	public SMPPResponse sendRequest(SubmitSM shortMsg) throws SocketTimeoutException, IOException{
		return smppConnection.sendRequest(shortMsg);
	}
	
	//public static void releaseConnection(Connection smppConnection) {
	public void releaseConnection() {
		if( smppConnection != null ) {
			try {
				smppConnection.unbind();
				smppConnection.closeLink();
			} 
			catch (Exception ex) {
				smppConnection = null;
			}
			finally{
				smppConnection = null;
			}
		}	
	}		
	
	public String getTelscapeUserName() {
		return telscapeUserName;
	}
	
	public String getTelscapePassword() {
		return telscapePassword;
	}
	
	public String getSystemType() {
		return systemType;
	}
	
	public int getMaximumMessageCount() {
		return maximumMessageCount;
	}
	
	public static String getShortCode() {
		return shortCode;
	}
			
	public static void main ( String[] args ) {
		//logger.info("Testing ConnectionUtil for SMPP project...");
		
		try {
		  SMPPService ss = new SMPPService();	
		  ss.makeConnection();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}		
		//logger.info("ConnectionUtil object established.");
			
		//logger.info("Done Testing ConnectionUtil for SMPP Project...Exited normally...");
	}
}
