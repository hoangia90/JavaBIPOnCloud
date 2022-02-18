package compute;

import java.util.List;
import java.util.Properties;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class DBUtil {

//	static Request req;
	static Session sessionObj;
	static SessionFactory sessionFactory;

//  private static SessionFactory buildSessionFactory() {
//  // Creating Configuration Instance & Passing Hibernate Configuration File
//  Configuration configObj = new Configuration();
//  configObj.configure("/hibernate.cfg.xml");
//
//  // Since Hibernate Version 4.x, ServiceRegistry Is Being Used
//  ServiceRegistry serviceRegistryObj = new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build(); 
//
//  // Creating Hibernate SessionFactory Instance
//  sessionFactoryObj = configObj.buildSessionFactory(serviceRegistryObj);
//  return sessionFactoryObj;
//}
	
	//Local
	static String DRIVER = "org.postgresql.Driver";
	static String URL = "jdbc:postgresql://localhost:5432/javabip?user=postgres&password=123&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
//	static String USER = "postgres";
//	static String PASS = "123";
	static String DIALECT = "org.hibernate.dialect.PostgreSQLDialect";
	static String SHOW_SQL = "true";
	static String CURRENT_SESSION_CONTEXT_CLASS = "thread";
	static String HBM2DDL_AUTO = "create-drop";
	
	//Heroku
//	static String DRIVER = "org.postgresql.Driver";
//	//Remote to Heroku
////	static String URL = "jdbc:postgresql://ec2-54-247-85-251.eu-west-1.compute.amazonaws.com:5432/d3cduui8428u5l?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory&user=xkxtjvpzvnhpnm&password=a368f47b501eb93092a92f4e6d9ec1b4fc47f35abeccd98bdce90de459c2685b";
//	//On Heroku
//	static String URL = "jdbc:postgresql://ec2-54-247-85-251.eu-west-1.compute.amazonaws.com:5432/d3cduui8428u5l?sslmode=require&user=xkxtjvpzvnhpnm&password=a368f47b501eb93092a92f4e6d9ec1b4fc47f35abeccd98bdce90de459c2685b";
////	static String USER = "xkxtjvpzvnhpnm";
////	static String PASS = "a368f47b501eb93092a92f4e6d9ec1b4fc47f35abeccd98bdce90de459c2685b";
//	static String DIALECT = "org.hibernate.dialect.PostgreSQLDialect";
//	static String SHOW_SQL = "true";
//	static String CURRENT_SESSION_CONTEXT_CLASS = "thread";
//	static String HBM2DDL_AUTO = "create-drop";
	
	
	

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				Configuration configuration = new Configuration().configure();
				configuration.addAnnotatedClass(Request.class);
//        		configuration.setProperty("hibernate.connection.username", "gia");
//        		configuration.setProperty("hibernate.connection.password", "1234");
				Properties settings = new Properties();
				settings.put(Environment.DRIVER, DRIVER);
				settings.put(Environment.URL, URL);
//				settings.put(Environment.USER, USER);
//				settings.put(Environment.PASS, PASS);
				settings.put(Environment.DIALECT, DIALECT);
				settings.put(Environment.SHOW_SQL, SHOW_SQL);
				settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, CURRENT_SESSION_CONTEXT_CLASS);
				settings.put(Environment.HBM2DDL_AUTO, HBM2DDL_AUTO);
				configuration.setProperties(settings);

				StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
						.applySettings(configuration.getProperties());
//			SessionFactory sessionFactory = configuration.buildSessionFactory(builder.build());
				sessionFactory = configuration.buildSessionFactory(builder.build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sessionFactory;
	}

	public static void create(Request req) {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		session.save(req);
		session.getTransaction().commit();
		session.close();
		System.out.println("Successfully created " + req.toString());
	}

	public static List<Request> read() {
		Session session = getSessionFactory().openSession();
		@SuppressWarnings("unchecked")
		List<Request> requests = session.createQuery("FROM request").list();
		session.close();
		System.out.println("Found " + requests.size() + " Employees");
		return requests;

	}

//    public static void update(Request r) {
//        Session session = getSessionFactory().openSession();
//        session.beginTransaction();
//        Request req = (Request) session.load(Request.class, r.getId());
//        req.setName(r.getName());
//        req.setAge(r.getAge());
//        session.getTransaction().commit();
//        session.close();
//        System.out.println("Successfully updated " + r.toString());
// 
//    }

	public static void delete(Integer id) {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		Request req = findByID(id);
		session.delete(req);
		session.getTransaction().commit();
		session.close();
		System.out.println("Successfully deleted " + req.toString());

	}

	public static Request findByID(Integer id) {
		Session session = getSessionFactory().openSession();
		Request req = (Request) session.load(Request.class, id);
		session.close();
		return req;
	}

	public static void deleteAll() {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		Query query = session.createQuery("DELETE FROM request ");
		query.executeUpdate();
		session.getTransaction().commit();
		session.close();
		System.out.println("Successfully deleted all employees.");

	}

//	public static void Backupdbtosql() throws URISyntaxException, InterruptedException {
//		try {
//
//			/* NOTE: Getting path to the Jar file being executed */
//			/* NOTE: YourImplementingClass-> replace with the class executing the code */
//			CodeSource codeSource = Compute.class.getProtectionDomain().getCodeSource();
//			File jarFile = new File(codeSource.getLocation().toURI().getPath());
//			String jarDir = jarFile.getParentFile().getPath();
//
//			/* NOTE: Creating Database Constraints */
//			String dbName = "request";
//			String dbUser = "postgres";
//			String dbPass = "123";
//
//			/* NOTE: Creating Path Constraints for folder saving */
//			/* NOTE: Here the backup folder is created for saving inside it */
//			String folderPath = jarDir + "\\BACKUP";
//
//			/* NOTE: Creating Folder if it does not exist */
//			File f1 = new File(folderPath);
//			f1.mkdir();
//
//			/* NOTE: Creating Path Constraints for backup saving */
//			/*
//			 * NOTE: Here the backup is saved in a folder called backup with the name
//			 * backup.sql
//			 */
//			String savePath = "\"" + jarDir + "\\backup\\" + "backup.sql\"";
//
//			/* NOTE: Used to create a cmd command */
//			String executeCmd = "mysqldump -u" + dbUser + " -p" + dbPass + " --database " + dbName + " -r " + savePath;
//
//			/* NOTE: Executing the command here */
//			Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
//			int processComplete = runtimeProcess.waitFor();
//
//			/*
//			 * NOTE: processComplete=0 if correctly executed, will contain other values if
//			 * not
//			 */
//			if (processComplete == 0) {
//				System.out.println("Backup Complete");
//			} else {
//				System.out.println("Backup Failure");
//			}
//
//		} catch (IOException ex) {
//			JOptionPane.showMessageDialog(null, "Error at Backuprestore" + ex.getMessage());
//		}
//	}
//
//	public static void Restoredbfromsql(String s) throws URISyntaxException, InterruptedException {
//		try {
//			/* NOTE: String s is the mysql file name including the .sql in its name */
//			/* NOTE: Getting path to the Jar file being executed */
//			/* NOTE: YourImplementingClass-> replace with the class executing the code */
//			CodeSource codeSource = Compute.class.getProtectionDomain().getCodeSource();
//			File jarFile = new File(codeSource.getLocation().toURI().getPath());
//			String jarDir = jarFile.getParentFile().getPath();
//
//			/* NOTE: Creating Database Constraints */
//			String dbName = "request";
//			String dbUser = "gia";
//			String dbPass = "123";
//
//			/* NOTE: Creating Path Constraints for restoring */
//			String restorePath = jarDir + "\\BACKUP" + "\\" + s;
//
//			/* NOTE: Used to create a cmd command */
//			/*
//			 * NOTE: Do not create a single large string, this will cause buffer locking,
//			 * use string array
//			 */
//			String[] executeCmd = new String[] { "mysql", dbName, "-u" + dbUser, "-p" + dbPass, "-e",
//					" source " + restorePath };
//
//			/*
//			 * NOTE: processComplete=0 if correctly executed, will contain other values if
//			 * not
//			 */
//			Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
//			int processComplete = runtimeProcess.waitFor();
//
//			/*
//			 * NOTE: processComplete=0 if correctly executed, will contain other values if
//			 * not
//			 */
//			if (processComplete == 0) {
//				JOptionPane.showMessageDialog(null, "Successfully restored from SQL : " + s);
//			} else {
//				JOptionPane.showMessageDialog(null, "Error at restoring");
//			}
//
//		} catch (IOException ex) {
//			JOptionPane.showMessageDialog(null, "Error at Restoredbfromsql" + ex.getMessage());
//		}
//
//	}

	public static void main(String[] args) {
		// backup
//    	CodeSource codeSource = Compute.class.getProtectionDomain().getCodeSource();
//		  File jarFile = null;
//		try {
//			jarFile = new File(codeSource.getLocation().toURI().getPath());
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String jarDir = jarFile.getParentFile().getPath();
//		System.out.println(jarDir);
//		Backupdbtosql();
//  
//		Runtime.getRuntime().exec("mysql -u gia -p 1234 javabip  backup.sql");
	}
}
