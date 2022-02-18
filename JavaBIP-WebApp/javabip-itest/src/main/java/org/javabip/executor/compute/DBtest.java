package org.javabip.executor.compute;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBtest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	      Connection con = null;
	      try {
//	    	 Class.forName("com.mysql.jdbc.Driver"); 
	         con = DriverManager.
	         getConnection("jdbc:mysql://localhost:3306/javabip?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&useSSL=false", "gia", "1234");
	         System.out.println("Connection is successful !!!!!");
	         con.close();
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	}

}
