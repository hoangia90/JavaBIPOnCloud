package org.javabip.executor.monitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.BasicConfigurator;
import org.javabip.api.BIPActor;
import org.javabip.api.BIPEngine;
import org.javabip.api.BIPGlue;
import org.javabip.engine.factory.EngineFactory;
import org.javabip.glue.GlueBuilder;
import org.json.JSONObject;
import com.sun.istack.logging.Logger;
import akka.actor.ActorSystem;

//import org.javabip.executor.monitor.Monitor;
//import org.javabip.executor.monitor.Switch;




//@WebServlet(name = "Monitor Automatic", urlPatterns = "/bipmoni2")
public class BIPMonitor extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 
	static Logger log = Logger.getLogger(BIPMonitor.class);
	
	ActorSystem system;
	EngineFactory engineFactory;
	BIPMonitor bipMoni;
	BIPGlue bipGlue;
	BIPEngine engine;
	// BIP Components
	Monitor moni;
	Switch sw;
	// BIP Actors
	BIPActor moniActor;
	BIPActor swActor;
	
	
//	static String moniRespone = "lalala";
	
	
//	@Before
	public void initialize() {

		system = ActorSystem.create("MySystem");
		engineFactory = new EngineFactory(system);
	}
	
	//	@After
	public void cleanup() {
//		system.shutdown();
	}
	
	public void init() throws ServletException
    {
		BasicConfigurator.configure();
		log.info("This is Logger Info");
		
        System.out.println("----------");
        System.out.println("---------- JavaBIP Initializing ----------");

  		bipMoni = new BIPMonitor();
  		bipMoni.initialize();
  
  		bipGlue = new GlueBuilder() {
			@Override
			public void configure() {
				//Sync
				
//				port(Monitor.class, "getIntoServerAvailable").requires(Switch.class, "switchServer");	
//				port(Switch.class, "switchServer").requires(Monitor.class, "getIntoServerAvailable");
				
//				port(Monitor.class, "getIntoServerAvailable").requires(Switch.class, "switchServer");
//				port(Switch.class, "switchServer").accepts(Monitor.class, "getIntoServerAvailable");
				
//				port(Monitor.class, "switchConfirm").requires(Switch.class, "switchConfirm");
//				port(Switch.class, "switchConfirm").requires(Monitor.class, "switchConfirm");
				
//				port(Monitor.class, "switchConfirm").requires(Switch.class, "switchServer");
//				port(Switch.class, "switchServer").requires(Monitor.class, "switchConfirm");
				
//				port(Switch.class, "switchServer").requires(Monitor.class, "switchServer");
//				port(Monitor.class, "switchServer").requires(Switch.class, "switchServer");
				
//				port(Monitor.class, "switchConfirm").requires(Switch.class, "switchConfirm");
//				port(Switch.class, "switchConfirm").requires(Monitor.class, "switchConfirm");
				
				port(Monitor.class, "receiveSwitchConfirm").requires(Switch.class, "switchConfirm");
				port(Switch.class, "switchConfirm").requires(Monitor.class, "receiveSwitchConfirm");
			
	
				//Data
				
			}
		}.build();
		
		engine = bipMoni.engineFactory.create("myEngine", bipGlue);
		
		moni = new Monitor();
		sw = new Switch();
		
		moniActor = engine.register(moni, "Monitor", true);
		swActor = engine.register(sw, "Switch", true);
		
  		engine.start();
  		engine.execute();
  		
//  		try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		engine.stop();
//		bipMoni.engineFactory.destroy(engine);
//		bipMoni.cleanup(); 	
  		
  		System.out.println("---------- JavaBIP Initialized successfully ----------");
        System.out.println("----------");
    }
	
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		doPost(request, response);
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		System.out.println("[Java BIP] Receiving a request");

//		String req = request.getParameter("req");
//		if ("test".equals(req)) {
//			System.out.println("-----BIP Monitor received a request-----");
////			moniActor.inform("test");
//			
//			Map<String, Object> map1 = new HashMap<String, Object>();
//			map1.put("adress", "abcd");
//			moniActor.inform("test", map1);
//				
//			}
		
		// Get Json from client
		String req = request.getParameter("req");
		String content = "";
		if ("user".equals(req)) {
			
			//test
//			Map<String, Object> data = new HashMap<String, Object>();
//			data.put("adress", "http://localhost:8080/javabip-itest/req");
//			swActor.inform("addServer", data);
			
			PrintWriter out = response.getWriter();
			if (!Switch.currentServer.isEmpty()) {
				
//				RequestDispatcher rd = request.getRequestDispatcher("/req");
//		        rd.include(request , response);
//		        content = response.toString();
				
				System.out.println("[BIP] Compute Button is pressed");
				moniActor.inform("receiveRandomNumberRequest");
				
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				MonitorResult re = new MonitorResult("");
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("data", re);
				moniActor.inform("sendRandomNumberRequest", data);
				
				while (re.getHttpResponse() == "") {
	//				System.out.println("The respone is not empty");
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (re.getHttpResponse() != "") {
						break;
					}
				}
				
				content = re.getHttpResponse();
				System.out.print(content);
//				//Json
//				JSONObject jsonObj = new JSONObject(content);
			}
			out.println(content);
			out.flush();
		} else { //// buttons handling
			
			// Add server ////////////////////////////////////////////////////////////
			String addServerButton = request.getParameter("add");
			if (addServerButton != null)
			{
				System.out.println("[BIP] Add server button is pressed");
				MonitorResult re = new MonitorResult("");
				String serverAddress = request.getParameter("serverAddress");
				re.setHttpResponse(serverAddress);
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("adress", re);
				swActor.inform("addServer", data);
//				request = setAttributesFromSessionUI(request);
				
				// This is used for waiting the result - find another better way
				while (re.getHttpResponse() != "") {
//					System.out.println("The respone is not empty");
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (re.getHttpResponse() == "") {
						System.out.println("[BIP] Adding server is done, break!");
						break;
					}
				}
				request = setAttributesFromSessionUI(request);
				
			}
			//////////////////////////////////////////////////////////////////////////
			
			
			// Remove servers ////////////////////////////////////////////////////////
			String removeServersButton = request.getParameter("remove");
			if (removeServersButton != null) {
				System.out.println("[BIP] Remove all servers button is pressed");
				MonitorResult re = new MonitorResult("processing...");
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("flag", re);
				swActor.inform("removeAllServers", data);
//				request = setAttributesFromSessionUI(request);
				
				// This is used for waiting the result - find another better way
				while (re.getHttpResponse() != "") {
//					System.out.println("The respone is not empty");
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (re.getHttpResponse() == "") {
						System.out.println("[BIP] Removing all servers is done, break!");
						break;
					}
				}
				request = setAttributesFromSessionUI(request);
			}
			//////////////////////////////////////////////////////////////////////////
			
			
			// Choose servers ////////////////////////////////////////////////////////
			String chooseServersButton = request.getParameter("chooseServer");
			if (chooseServersButton != null) {
				System.out.println("[BIP] Choose server button is pressed");
				Map<String, Object> data = new HashMap<String, Object>();
				String serverAddress = request.getParameter("serverAddress");
				MonitorResult re = new MonitorResult(serverAddress);
				re.setHttpResponse(serverAddress);
				data.put("serverAddress", re);
				swActor.inform("chooseServer", data);
//				request = setAttributesFromSessionUI(request);
				
				// This is used for waiting the result - find another better way
				while (re.getHttpResponse() != "") {
//								System.out.println("The respone is not empty");
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (re.getHttpResponse() == "") {
						System.out.println("[BIP] Choosing a server is done, break!");
						break;
					}
				}
				request = setAttributesFromSessionUI(request);
			}
			//////////////////////////////////////////////////////////////////////////
			
			
			// Switch confirms ////////////////////////////////////////////////////////
//			String switchConfirmButton = request.getParameter("switchConfirm");
//			if (switchConfirmButton != null) {
//				System.out.println("[BIP] Switch confirm button is pressed");
//				MonitorResult re = new MonitorResult("processing...");
//				Map<String, Object> data = new HashMap<String, Object>();
//				data.put("flag", re);
//				swActor.inform("switchConfirm", data);
////				request = setAttributesFromSessionUI(request);
//				
//				// This is used for waiting the result - find another better way
//				while (re.getHttpResponse() != "") {
////					System.out.println("The respone is not empty");
//					try {
//						TimeUnit.SECONDS.sleep(1);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					if (re.getHttpResponse() == "") {
//						System.out.println("[BIP] Switch confirmed, break!");
//						break;
//					}
//				}
//				request = setAttributesFromSessionUI(request);
//			}
			//////////////////////////////////////////////////////////////////////////
			
			
			// Testing
			// Compute ///////////////////////////////////////////////////////////////
			String compute = request.getParameter("compute");
			if (compute != null )
			{
				System.out.println("[BIP] Compute Button is pressed");
				moniActor.inform("receiveRandomNumberRequest");
				
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				MonitorResult re = new MonitorResult("");
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("data", re);
				moniActor.inform("sendRandomNumberRequest", data);
				
				while (re.getHttpResponse() == "") {
	//				System.out.println("The respone is not empty");
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (re.getHttpResponse() != "") {
						content = re.getHttpResponse();
						System.out.println("abcd" + content );
//						//Json
						JSONObject jsonObj = new JSONObject(content);
						request = setAttributesUI(request, jsonObj);
						break;
					}
				}

//				content = re.getHttpResponse();
//				System.out.println("abcd" + content );
////				//Json
//				JSONObject jsonObj = new JSONObject(content);
//				request = setAttributesUI(request, jsonObj);

				System.out.print("End!!!!");
			}
			//////////////////////////////////////////////////////////////////////////
						
			RequestDispatcher rd = request.getRequestDispatcher("/monitor-index.jsp");
	        rd.include(request , response);
		}

			
		};
		
		
		
		
		
		protected HttpServletRequest setAttributesFromSessionUI (HttpServletRequest request) {
			String id = (String)request.getSession().getAttribute("id");
			String randomNumber = (String)request.getSession().getAttribute("randomNumber");
			String counter = (String)request.getSession().getAttribute("counter");
			String limit = (String)request.getSession().getAttribute("limit");
			String server = (String)request.getSession().getAttribute("server");
			//
			request.setAttribute("id", id);
			request.setAttribute("randomNumber", randomNumber);
	    	request.setAttribute("counter", counter);
	    	request.setAttribute("limit", limit);
	    	request.setAttribute("server", server);
			//
	    	request.setAttribute("currentServer", Switch.currentServer);
	    	//
	    	return request;
		}
		
		
		protected HttpServletRequest setAttributesUI (HttpServletRequest request, JSONObject jsonObj) {
			int id = jsonObj.getInt("id");
			int randomNumber = jsonObj.getInt("randomNumber");
			int counter = jsonObj.getInt("counter");
			int limit = jsonObj.getInt("requestLimit");
			String server = jsonObj.getString("server");
			//
			request.setAttribute("id", id);
			request.setAttribute("randomNumber", randomNumber);
	    	request.setAttribute("counter", counter);
	    	request.setAttribute("limit", limit);
	    	request.setAttribute("server", server);
	    	//
	    	request.setAttribute("currentServer", Switch.currentServer);
	    	//
	    	return request;
		}
	
}