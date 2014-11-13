import java.util.List;
import util.*;
import datamodel.*;

import org.hibernate.*;

public class HibernateDemo {
	public static void main(String[] args) {
		SessionFactory sessionFactory = HibernateUtil.createSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(new TestData("Test1"));
        session.save(new TestData("Test2"));
      
        session.getTransaction().commit();
 
        Query q = session.createQuery("From TestData ");
                 
        List<TestData> resultList = q.list();
        System.out.println("num of datapoints:" + resultList.size());
        for (TestData next : resultList) {
            System.out.println("next data: " + next);
        }

        sessionFactory.close();
 
    }
}
