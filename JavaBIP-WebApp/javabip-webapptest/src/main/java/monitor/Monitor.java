package monitor;


import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import monitor.Switch;

//import org.javabip.annotations.ComponentType;
//import org.javabip.annotations.Port;
//import org.javabip.annotations.Ports;
//import org.javabip.api.*;
//import org.javabip.engine.factory.EngineFactory;
//import org.javabip.exceptions.BIPException;
//import org.javabip.glue.TwoSynchronGlueBuilder;
//import org.javabip.glue.ManySynchronGlueBuilder;
//import org.javabip.spec.*;

//@WebServlet(name = "Monitor", urlPatterns = "/moni")


//@Ports({
//	@Port(name = "end", type = PortType.spontaneous),
//	@Port(name = "on", type = PortType.spontaneous),
//	@Port(name = "off", type = PortType.spontaneous),
//	@Port(name = "finished", type = PortType.spontaneous)
//})
//@ComponentType(initial = "init", name = "monitor.switch")
public class Monitor extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String USER_AGENT = "Mozilla/5.0";
	
	Switch sw = new Switch();
	
	
	//IU
	protected HttpServletRequest setAttributesUI (HttpServletRequest request, JSONObject jsonObj) {
		int id = jsonObj.getInt("id");
		int randomNumber = jsonObj.getInt("randomNumber");
		int counter = jsonObj.getInt("counter");
		int limit = jsonObj.getInt("limit");
		String server = jsonObj.getString("server");
		
		request.setAttribute("id", id);
		request.setAttribute("randomNumber", randomNumber);
    	request.setAttribute("counter", counter);
    	request.setAttribute("limit", limit);
    	request.setAttribute("server", server);
		
    	return request;
	}
	
	protected HttpServletRequest setAttributesFromSessionUI (HttpServletRequest request) {
		String id = (String)request.getSession().getAttribute("id");
		String randomNumber = (String)request.getSession().getAttribute("randomNumber");
		String counter = (String)request.getSession().getAttribute("counter");
		String limit = (String)request.getSession().getAttribute("limit");
		String server = (String)request.getSession().getAttribute("server");
		
		request.setAttribute("id", id);
		request.setAttribute("randomNumber", randomNumber);
    	request.setAttribute("counter", counter);
    	request.setAttribute("limit", limit);
    	request.setAttribute("server", server);
		
    	return request;
	}
	
	protected HttpServletRequest setNullAttributesUI (HttpServletRequest request) {		
		request.setAttribute("id", "null");
		request.setAttribute("randomNumber", "null");
    	request.setAttribute("counter", "null");
    	request.setAttribute("limit", "null");
    	request.setAttribute("server", "null");
		
    	return request;
	}
	
	// Actions
	
	// Send reuqest //
	protected String requestRandomNumber (String currentServer) throws IOException {
		URL obj = new URL(currentServer);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + currentServer);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		
		in.close();
		
		return content.toString();
	}
	
	// Limit check //
	protected boolean limitIsReached (JSONObject jsonObj) {
		int counter = jsonObj.getInt("counter");
	    int limit = jsonObj.getInt("limit");
	    if (counter == limit ) {
	    	return true;
    	} else {
    		return false;
		}
	}
	
	// Add server //
	protected void addServer (HttpServletRequest request) {
		if (sw.serverslist.isEmpty()) {
			sw.initServer();
		}
		String serverAddress = request.getParameter("serverAddress");
		sw.addServer(serverAddress);
	}
	
	// Remove all servers //
	protected void removeServers () {
		sw.removeServers();
	}
	
	// Choose server
	protected void chooseServer () {
		sw.chooseServer();
	}
	// Actions - End
	
	
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get Json from client
		String req = request.getParameter("req");
		String content = "";
		if ("user".equals(req)) {
			PrintWriter out = response.getWriter();
			if (!sw.currentServer.isEmpty()) {
				content = requestRandomNumber(sw.currentServer);
				System.out.print(content);
				//Json
				JSONObject jsonObj = new JSONObject(content);
	        	
	        	if (limitIsReached(jsonObj)) {
	        		chooseServer();
	        	}
			}
			out.println(content);
			out.flush();
			
		} else { //// buttons handling
			
			// Add server ////////////////////////////////////////////////////////////
			String addServerButton = request.getParameter("add");
			if (addServerButton != null)
	        {
				addServer(request);
				request = setAttributesFromSessionUI(request);
	        }
			//////////////////////////////////////////////////////////////////////////
			
			
			// Remove servers ////////////////////////////////////////////////////////
			String removeServersButton = request.getParameter("remove");
			if (removeServersButton != null) {
				removeServers();
			}
			//////////////////////////////////////////////////////////////////////////
			
			
			// Compute ///////////////////////////////////////////////////////////////
			String compute = request.getParameter("compute");
	        if (compute != null && !sw.serverslist.isEmpty())
	        {
				content = requestRandomNumber(sw.currentServer);
				System.out.println(content.toString());
				//Json
				JSONObject jsonObj = new JSONObject(content);
				request = setAttributesUI(request, jsonObj);
	        	if (limitIsReached(jsonObj)) {
	        		chooseServer();
	        	}
	        	request.setAttribute("currentServer", sw.currentServer);
	        }
	        //////////////////////////////////////////////////////////////////////////
	    
	        
	        RequestDispatcher rd = request.getRequestDispatcher("/monitor-index.jsp");
	        rd.include(request , response);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	
}
