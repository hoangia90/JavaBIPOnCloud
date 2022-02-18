package compute;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

//@WebServlet(name = "Compute Server", urlPatterns = "/")
public class Compute extends HttpServlet {
	
	LimitCounter counter;
	int limit = 20;
	

	private static final long serialVersionUID = -4751096228274971485L;
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
//		String uri = request.getScheme() + "://" +   // "http" + "://
//	             request.getServerName() +       // "myhost"
//	             ":" +                           // ":"
//	             request.getServerPort() +       // "8080"
//	             request.getRequestURI() +       // "/people"
//	             "?" +                           // "?"
//	             request.getQueryString();       // "lastname=Fox&age=30"
//		System.out.println(uri + " " + request.getRequestURL().toString());
		
		Random rand = new Random();
		int n = rand.nextInt(100);
		n += 1;
		System.out.println("Random number: " + n);
		
		
		//Limit counter: if the limit is reach, the counter will be reset. 
		 if (counter == null)
		 {
			 counter = new LimitCounter();
			 counter.increment();
		 }
		 else if (counter != null && counter.getCounter() < limit)
		 {
			 counter.increment();
		 }
		 else
		 {
			 counter.reset();
			 System.out.println("The limit is reached --> counter reset!");
		 }
		 
		 //Json format
		 Request req = new Request(1, n, counter.getCounter(), limit, request.getRequestURL().toString());
	     String reqJsonString = new Gson().toJson(req);
	 
	     PrintWriter out = response.getWriter();
	     response.setContentType("application/json");
	     response.setCharacterEncoding("UTF-8");
	     out.print(reqJsonString);
	     out.flush();   
		 
		 
		 
	    //XML format
//		PrintWriter out = response.getWriter();
//		StringBuffer sb=new StringBuffer();
//		sb.append("<?xml version='1.0' encoding='ISO-8859-1'?>\n");
//		sb.append("<response>");
//		sb.append("<randomNumber>" +n+ "</randomNumber>\n");
//		sb.append("<counter>" +counter.getCounter()+ "</counter>\n");
//		sb.append("<limit>" +limit+ "</limit>\n");
//		sb.append("<server>" +request.getRequestURL().toString()+ "</server>\n");
//		sb.append("</response>"); 
//		out.println(sb.toString());
//		out.flush();
		
		 
		 
	}
	@Override
	public void init() throws ServletException {
		System.out.println("Servlet " + this.getServletName() + " has started");
	}
	@Override
	public void destroy() {
		System.out.println("Servlet " + this.getServletName() + " has stopped");
	}


}
