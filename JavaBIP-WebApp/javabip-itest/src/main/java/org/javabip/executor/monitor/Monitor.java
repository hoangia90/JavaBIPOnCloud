package org.javabip.executor.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServlet;
import org.json.JSONObject;
import org.javabip.executor.monitor.Switch;
import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Data;
import org.javabip.annotations.Guard;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;


// States: MonitorInit, RandomNumberRequestReceived
@Ports({
//	@Port(name = "switchConfirm", type = PortType.enforceable),
	@Port(name = "receiveRandomNumberRequest", type = PortType.spontaneous),
//	@Port(name = "checkServer", type = PortType.enforceable),
	@Port(name = "sendRandomNumberRequest", type = PortType.spontaneous),
//	@Port(name = "switchServer", type = PortType.spontaneous),
	@Port(name = "receiveSwitchConfirm", type = PortType.enforceable)
})
//@WebServlet(name = "Monitor", urlPatterns = "/moni")
@ComponentType(initial = "MonitorInit", name = "org.javabip.executor.monitor.Monitor")
public class Monitor extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String USER_AGENT = "Mozilla/5.0";
	
	Switch sw = new Switch();
	
	boolean currentLimitReach = false;
//	String currentServer = "";
	
	
	@Transitions({
		@Transition(name = "receiveSwitchConfirm", source = "MonitorInit", target = "SwitchReady"),
	})
	public void waitSwitchConfirm() {
		System.out.println("[Monitor] Recieved switch confirm....");
	}
	

	
	
	@Transitions({
		@Transition(name = "receiveRandomNumberRequest", source = "SwitchReady", target = "RandomNumberRequestReceived"),
	})
	public void getIntoServerAvailable() {
		System.out.println("[Monitor] Received a random number request....");
	}
	

	// Send request //
	public String requestJson (String currentServer) throws IOException {
		System.out.println("[Monitor] Getting Json contain....");
		URL obj = new URL(Switch.currentServer);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		//
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + Switch.currentServer);
		System.out.println("Response Code : " + responseCode);
		//
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		//
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		//
		return content.toString();
	}
	
	

	
//	@Transitions({
//		@Transition(name = "sendDataRequest", source = "LimitChecked", target = "MonitorInit"),
//		})
//	public String receiveRandomNumberRequest(String currentServer) throws IOException {
//		System.out.println("[Monitor] Getting a random number.... @");
//		// TODO Auto-generated method stub
//		return requestJson (currentServer);
////		requestJson(Switch.currentServer) ;
//	}
	

	@Transitions({
		@Transition(name = "sendRandomNumberRequest", source = "RandomNumberRequestReceived", target = "SwitchReady"),
	})
	public void receiveRandomNumberRequest(@Data(name = "data") MonitorResult data) throws IOException {
		System.out.println("[Monitor] Getting a random number....@" + Switch.currentServer);
		// TODO Auto-generated method stub
		String re = requestJson(Switch.currentServer);
		data.setHttpResponse(re);
	}
	
//	@Guard(name = "isServerReady")
//	public boolean isServerReady() {
//		return Switch.currentServer != "";
//	}
	
	
//	// Switch confirm
//	@Transitions({
//		@Transition(name = "switchConfirm", source = "ServerSwitched", target = "ServerChecking"),
//	})
//	public void switchConfirm () {
//		System.out.println("[Monitor] Switch confirmed - a new server added (....)");
////			currentServer = newServer;
//	}
	
	

	
//	@Transitions({
//		@Transition(name = "switchServer", source = "ServerChecking", target = "ServerSwitched",  guard = "needToChooseServer"),
//	})
////		public void switchConfirm (@Data(name = "newServer") String newServer) {
//	public void switchServer () {
//		System.out.println("[Monitor] switch server (....)");
////			currentServer = newServer;
//	}
//	
//	@Guard(name = "needToChooseServer")
//	public boolean needToChooseServer() {
//		return Switch.currentServer == "";
//	}
	

	
	

	// Request limit info	
	@Guard(name = "limitIsReached")
	// Limit check //
	public boolean limitIsReached () throws IOException {
		System.out.println("[Monitor] Checking if the limit is reached....");
		String content = requestLimitInfo(Switch.currentServer);
		System.out.print(content);
		//Json
		JSONObject jsonObj = new JSONObject(content);
		
		int counter = jsonObj.getInt("counter");
	    int limit = jsonObj.getInt("requestLimit");
	    
	    if (counter == limit ) {
	    	System.out.println("[Monitor] The limit has been reached....");
	    	currentLimitReach = true;
    	} else {
    		System.out.println("[Monitor] The limit has not been reached....");
    		currentLimitReach = false;
		}
	    return currentLimitReach;
	}

	public String requestLimitInfo (String currentServer) throws IOException {
		System.out.println("[Monitor] Getting limit info....");
		String requestLimitInfoURL = currentServer + "?compute=checkLimit";
		System.out.println("[Monitor] URL (" + requestLimitInfoURL + ") \n \n");
		String content = requestJson(requestLimitInfoURL);
		System.out.println("[Monitor] limit info (" + content + ") \n \n");
		return content.toString();
	}
	
//	@Guard(name = "limitIsReached")
//	// Limit check //
//	public boolean limitIsReached (String currentServer) throws IOException {
//		System.out.println("[Monitor] Checking if the limit is reached....");
//		String content = requestLimitInfo(currentServer);
//		System.out.print(content);
//		//Json
//		JSONObject jsonObj = new JSONObject(content);
//		
//		int counter = jsonObj.getInt("counter");
//	    int limit = jsonObj.getInt("requestLimit");
//	    
//	    if (counter == limit ) {
//	    	System.out.println("[Monitor] The limit has been reached....");
//	    	currentLimitReach = true;
//    	} else {
//    		System.out.println("[Monitor] The limit has not been reached....");
//    		currentLimitReach = false;
//		}
//	    return currentLimitReach;
//	}
//
//	//	@Transitions({
//	//	@Transition(name = "requestLimitInfo", source = "RandomNumberRequestReceived", target = "LimitChecked"),
//	//})
//	public String requestLimitInfo (String currentServer ) throws IOException {
//		System.out.println("[Monitor] Getting limit info....");
//		String requestLimitInfoURL = currentServer + "?req=checkLimit";
//		System.out.println("[Monitor] URL (" + requestLimitInfoURL + ") \n \n");
//		String content = requestJson(requestLimitInfoURL);
//		System.out.println("[Monitor] limit info (" + content + ") \n \n");
//		return content.toString();
//	}
	
	
	
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		
//		// Get Json from client
//		String req = request.getParameter("req");
//		String content = "";
//		if ("user".equals(req)) {
//			PrintWriter out = response.getWriter();
//			if (!sw.currentServer.isEmpty()) {
//				
////				RequestDispatcher rd = request.getRequestDispatcher("/req");
////		        rd.include(request , response);
////		        content = response.toString();
//				
//				if (limitIsReached(sw.currentServer)) {
//					sw.chooseServer();
//	        	}
////				content = receiveRandomNumberRequest(Switch.currentServer);
//				System.out.print(content);
//				//Json
//				JSONObject jsonObj = new JSONObject(content);
//			}
//			out.println(content);
//			out.flush();
//			
//		} else { //// buttons handling
//			
//			// Add server ////////////////////////////////////////////////////////////
//			String addServerButton = request.getParameter("add");
//			if (addServerButton != null)
//	        {
//				System.out.println("[Monitor] Add Server Button is pressed");
//				String serverAddress = request.getParameter("serverAddress");
////				sw.addServer(serverAddress);
//				request = setAttributesFromSessionUI(request);
//	        }
//			//////////////////////////////////////////////////////////////////////////
//			
//			
//			// Remove servers ////////////////////////////////////////////////////////
//			String removeServersButton = request.getParameter("remove");
//			if (removeServersButton != null) {
//				System.out.println("[Monitor] Remove All Servers Button is pressed");
//				sw.removeServers();
//			}
//			//////////////////////////////////////////////////////////////////////////
//			
//			
//			// Testing
//			// Compute ///////////////////////////////////////////////////////////////
//			String compute = request.getParameter("compute");
//	        if (compute != null && !sw.serverslist.isEmpty())
//	        {
//	        	System.out.println("[Monitor] Compute Button is pressed");
//	        	sw.chooseServer();
//	        	
//				if (limitIsReached(Switch.currentServer)) {
//	        		
//	        	}
//				
//	        	
////	        	content = receiveRandomNumberRequest(Switch.currentServer);
//				System.out.println(content.toString());
//				
//				//Json
//				JSONObject jsonObj = new JSONObject(content);
//				request = setAttributesUI(request, jsonObj);
//	        	request.setAttribute("currentServer", Switch.currentServer);
//	    		
//	    		System.out.print("End!!!!");
//	        }
//	        //////////////////////////////////////////////////////////////////////////
//	    
//	        
//	        RequestDispatcher rd = request.getRequestDispatcher("/monitor-index.jsp");
//	        rd.include(request , response);
//		}
//	}
	
	
	
	
	
	
	// Addtional methods for JavaBIP
	
//	@Transitions({
//		@Transition(name = "receiveClientRequest", source = "MonitorInit", target = "RequestReceived")
//	})
//	public void receiveClientRequest () {
//		System.out.println("[Monitor] Received a request from client");
//	}

	// request limit info @ local
//	public String requestLocalLimitInfo () throws IOException {
//		System.out.println("[Monitor] Getting limit info @ local server....");
//		String requestLimitInfoURL = "http://localhost:8080/javabip-itest/req?req=checkLimit";
//		String content = requestJson(requestLimitInfoURL);
//		System.out.println("[Monitor] limit info (" + content + ") \n \n");
//		return content;
//	}
	
	// Limit check //
//	@Transitions({
//		@Transition(name = "requestLocalLimitInfo", source = "RequestReceived", target = "LimitChecked"),
//	})
//	public void localLimitIsReached () throws IOException {
//		System.out.println("[Monitor] Checking if the limit is reached....");
//		String content = requestLocalLimitInfo();
//		System.out.print(content);
//		//Json
//		JSONObject jsonObj = new JSONObject(content);
//		
//		int counter = jsonObj.getInt("counter");
//	    int limit = jsonObj.getInt("requestLimit");
//	    if (counter == limit ) {
//	    	System.out.println("[Monitor] The limit has been reached....");
//	    	currentLimitReach = true;
//	    	switchServer();
//    	} else {
//    		System.out.println("[Monitor] The limit has not been reached....");
//    		currentLimitReach = false;
//    		requestLocalRandomNumber();
//		}
////	    return currentLimitReach;
//	}
	
	// request @ local //
//	@Transitions({
//		@Transition(name = "sendLocalRequest", source = "LimitChecked", target = "MonitorInit")
//	})
//	@Transition(name = "sendRequestServerLocal", source = "LimitChecked", target = "MonitorInit")
//	public void requestLocalRandomNumber() throws IOException {
//		System.out.println("[Monitor] Getting Json contain @ local server....");
//		String content = "";
//		try {
//			content = requestJson("http://localhost:8080/javabip-itest/req");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("[Monitor] receive (" + content + ") \n \n");
//	}
	
//	@Transitions({
//		@Transition(name = "switchServer", source = "LimitChecked", target = "ServerSwitching")
//	})
//	public void switchServer () {
//		System.out.println("[Monitor] Switch server");
//	}
	
	
//	protected HttpServletRequest setAttributesUI (HttpServletRequest request, JSONObject jsonObj) {
//		int id = jsonObj.getInt("id");
//		int randomNumber = jsonObj.getInt("randomNumber");
//		int counter = jsonObj.getInt("counter");
//		int limit = jsonObj.getInt("requestLimit");
//		String server = jsonObj.getString("server");
//		//
//		request.setAttribute("id", id);
//		request.setAttribute("randomNumber", randomNumber);
//    	request.setAttribute("counter", counter);
//    	request.setAttribute("limit", limit);
//    	request.setAttribute("server", server);
//    	//
//    	return request;
//	}
//	
//	protected HttpServletRequest setAttributesFromSessionUI (HttpServletRequest request) {
//		String id = (String)request.getSession().getAttribute("id");
//		String randomNumber = (String)request.getSession().getAttribute("randomNumber");
//		String counter = (String)request.getSession().getAttribute("counter");
//		String limit = (String)request.getSession().getAttribute("limit");
//		String server = (String)request.getSession().getAttribute("server");
//		//
//		request.setAttribute("id", id);
//		request.setAttribute("randomNumber", randomNumber);
//    	request.setAttribute("counter", counter);
//    	request.setAttribute("limit", limit);
//    	request.setAttribute("server", server);
//		//
//    	return request;
//	}
//	
//	protected HttpServletRequest setNullAttributesUI (HttpServletRequest request) {		
//		request.setAttribute("id", "null");
//		request.setAttribute("randomNumber", "null");
//    	request.setAttribute("counter", "null");
//    	request.setAttribute("limit", "null");
//    	request.setAttribute("server", "null");
//		//
//    	return request;
//	}
	
}
