package user;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


public class RandomUser extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String USER_AGENT = "Mozilla/5.0";
	
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
	
	protected HttpServletRequest setNullAttributesUI (HttpServletRequest request) {		
		request.setAttribute("id", "null");
		request.setAttribute("randomNumber", "null");
    	request.setAttribute("counter", "null");
    	request.setAttribute("limit", "null");
    	request.setAttribute("server", "null");
		
    	return request;
	}
	
	
	String currentServer = "https://bip-monitor.herokuapp.com/?req=user";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Compute test
		String compute = request.getParameter("compute");
        if (compute != null)
        {
			String content = requestRandomNumber(currentServer);
			if (!content.isEmpty()) {
			
//				DocumentBuilderFactory dbfaFactory = DocumentBuilderFactory.newInstance();
//				DocumentBuilder documentBuilder = null;
//				try {
//					documentBuilder = dbfaFactory.newDocumentBuilder();
//				} catch (ParserConfigurationException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				Document doc = null;
//				try {
//					doc = documentBuilder.parse(new InputSource(new StringReader(content)));
//				} catch (SAXException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				
				System.out.println(content.toString());
				
				//Json
				JSONObject jsonObj = new JSONObject(content);
				  
			    request = setAttributesUI(request, jsonObj);
			} else {
				request = setNullAttributesUI(request);
			}
        }
        RequestDispatcher rd = request.getRequestDispatcher("/user-index.jsp");
        rd.include(request , response);
	}
}
