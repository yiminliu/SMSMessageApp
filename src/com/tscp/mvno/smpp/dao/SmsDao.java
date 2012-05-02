package com.tscp.mvno.smpp.dao;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tscp.mvno.smpp.domain.SMSMessage;


@Repository
@Scope("singleton")
public class SmsDao extends HibernateDaoSupport {

	public SmsDao(){}	
	
    @Autowired
	public void init(HibernateTemplate hibernateTemplate) {
	   setHibernateTemplate(hibernateTemplate);
    } 
    
	@Transactional(readOnly=true)
	public List<SMSMessage> getAlertMessages(String namedQueryName){
		
		   List<SMSMessage> messageList = null;	  
		
		   HibernateTemplate ht = getHibernateTemplate();
		
		   messageList = (List<SMSMessage>)ht.findByNamedQuery(namedQueryName);
		   
		   return messageList;
	}
		
	
	/* used with pure Hibernate
	public List<SMSMessage> getPromAlertMessages(){
				
	   //Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Session session = sessionFactory.getCurrentSession();	
	   
	   Transaction tx = null; 	   
	   List<SMSMessage> messageList = null;	  
	   
	   try {
		   tx = session.beginTransaction();
		   Query query = session.getNamedQuery("get_marketing_sms");
		   query.setReadOnly(true);
		   query.setCacheable(false);
		   
		   messageList = query.list();
		   
		   tx.commit();		   
	   }
	   catch(HibernateException he){
		   he.printStackTrace();
		   HibernateUtil.rollbackTransaction(tx, "HibernateException occured while excuting get_marketing_sms SP");
	   }	   
	   return messageList;
	}	
	*/
}
