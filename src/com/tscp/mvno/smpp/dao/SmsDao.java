package com.tscp.mvno.smpp.dao;

import ie.omk.smpp.message.SubmitSM;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tscp.mvno.smpp.domain.SMSMessage;
//import com.tscp.mvno.smpp.util.HibernateUtil;

@Repository
public class SmsDao extends HibernateDaoSupport {

	public SmsDao(){
	}
	
	
    @Autowired
	public void init(HibernateTemplate hibernateTemplate) {
	   setHibernateTemplate(hibernateTemplate);
    }
    
    //@Autowired
    //public void init(SessionFactory sessionFactory) {
	//   setSessionFactory(sessionFactory);
    //}
	
	@Autowired
	SessionFactory sessionFactory;
	
		
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
	@Transactional(readOnly=true)
	public List<SMSMessage> getPromAlertMessages(){
		
		   List<SMSMessage> messageList = null;	  
		
		   HibernateTemplate ht = getHibernateTemplate();
		
		   messageList = (List<SMSMessage>)ht.findByNamedQuery("get_marketing_sms");
		   
		   return messageList;
		}
			
}
