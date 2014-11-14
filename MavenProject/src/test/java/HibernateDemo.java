import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import util.*;
import datamodel.*;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.ogm.cfg.OgmConfiguration;
import org.hibernate.*;
import org.jboss.jandex.Main;

import com.arjuna.ats.jta.TransactionManager;

public class HibernateDemo {
	
	public static void main(String[] args) throws Exception {
//		SessionFactory sessionFactory = HibernateUtil.createSessionFactory();
//        Session session = sessionFactory.openSession();
//        session.beginTransaction();
//        session.save(new TestData("Test1"));
//        session.save(new TestData("Test2"));
//      
//        session.getTransaction().commit();
// 
//        Query q = session.createQuery("From TestData ");
//                 
//        List<TestData> resultList = q.list();
//        System.out.println("num of datapoints:" + resultList.size());
//        for (TestData next : resultList) {
//            System.out.println("next data: " + next);
//        }
//
//        sessionFactory.close();
		
//////////////////////////////////////		
//		If creating your own config.
//		Configuration cfg = new OgmConfiguration();
//
//		//assuming you are using JTA in a non contained environment
//		cfg.setProperty(Environment.TRANSACTION_STRATEGY,"org.hibernate.transaction.JTATransactionFactory");
//		//assuming JBoss TransactionManager in standalone mode
//		cfg.setProperty(Environment.JTA_PLATFORM,
//		     "org.hibernate.service.jta.platform.internal.JBossStandAloneJtaPlatform");
//
//		//assuming the default mongo settings
//		cfg.setProperty("hibernate.ogm.datastore.provider","org.hibernate.ogm.datastore.infinispan.impl.InfinispanDatastoreProvider");
//
//		//add your annotated classes
//		cfg.addAnnotatedClass(TestData.class);

		//persist the entities
		javax.transaction.TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
		
		
		EntityManager em = (EntityManager) Persistence.createEntityManagerFactory("DatabaseBenchmarking-mongodb");
		transactionManager.begin();
		TestData dt1 = new TestData("Test1");
		TestData dt2 = new TestData("Test2");
		em.persist(dt1);
		em.persist(dt2);
		em.flush();
		em.close();
		transactionManager.commit();
		
//		// persist the entities
//		SessionFactory sessionFactory = HibernateUtil.createSessionFactory(cfg);
//        Session session = sessionFactory.openSession();
//        session.beginTransaction();
//        session.save(new TestData("Test1"));
//        session.save(new TestData("Test2"));
//      
//        session.getTransaction().commit();
// 
//        Query q = session.createQuery("From TestData ");
//                 
//        List<TestData> resultList = q.list();
//        System.out.println("num of datapoints:" + resultList.size());
//        for (TestData next : resultList) {
//            System.out.println("next data: " + next);
//        }
//
//        sessionFactory.close();
 
    }
}
