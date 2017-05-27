package com.hibernate.main;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;

import com.hibernate.model.Employee;
import com.hibernate.util.HibernateUtil;

public class Main {
	public static void main(String[] args) {
		System.out.println("Temp Dir:" + System.getProperty("java.io.tmpdir"));
		// Initialize Sessions
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Statistics stats = sessionFactory.getStatistics();
		// display false as secondary cache is disabled
		System.out.println("Stats enabled=" + stats.isStatisticsEnabled());
		// enable secondary cache
		stats.setStatisticsEnabled(true);
		System.out.println("Stats enabled=" + stats.isStatisticsEnabled());

		Session session_1 = sessionFactory.openSession();
		Session session_2 = sessionFactory.openSession();
		Transaction transaction_1 = session_1.beginTransaction();
		Transaction transaction_2 = session_2.beginTransaction();

		printStats(stats, 0);

		Employee emp = (Employee) session_1.load(Employee.class, 1L);
		printData(emp, stats, 1);

		emp = (Employee) session_1.load(Employee.class, 1L);
		printData(emp, stats, 2);

		// clear first level cache, so that second level cache is used
		session_1.evict(emp);
		emp = (Employee) session_1.load(Employee.class, 1L);
		printData(emp, stats, 3);

		emp = (Employee) session_1.load(Employee.class, 3L);
		printData(emp, stats, 4);

		emp = (Employee) session_2.load(Employee.class, 1L);
		printData(emp, stats, 5);

		// Release resources
		transaction_1.commit();
		transaction_2.commit();
		sessionFactory.close();
	}

	private static void printStats(Statistics stats, int i) {
		System.out.println("***** " + i + " *****");
		System.out.println("Fetch Count=" + stats.getEntityFetchCount());
		System.out.println("Second Level Hit Count="
				+ stats.getSecondLevelCacheHitCount());
		System.out.println("Second Level Miss Count="
				+ stats.getSecondLevelCacheMissCount());
		System.out.println("Second Level Put Count="
				+ stats.getSecondLevelCachePutCount());
	}

	private static void printData(Employee emp, Statistics stats, int count) {
		System.out.println(count + ":: Name=" + emp.getName() +",City="+emp.getAddress().getCity());
		printStats(stats, count);
	}

}
