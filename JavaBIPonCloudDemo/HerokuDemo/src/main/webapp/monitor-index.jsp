<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!--%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %-->
<!--%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<head>
	<title>Monitor</title>
	<link rel="stylesheet" href="styles.css" />
	<!-- <link rel="stylesheet" href="<%= request.getContextPath() %>/styles.css" /> -->
	<!-- <style><%@include file="/styles.css"%></style> -->
</head>

<body>
	<%@page import="java.util.ArrayList"%> 
	<%@page import="java.util.List"%> 
	<%@page import="java.util.Iterator"%> 
	<%@page import="monitor.Switch" %>
	<%@page import="javax.servlet.http.HttpServletRequest" %>
	
	<form action="BIPMonitor" method="GET">
	
	<!-- <%= request.getContextPath() %> -->
	<!-- <form action="moni" method="GET"> -->
	<!-- <form action="BIPMonitor" method="GET"> -->
	<!-- p> Current server : <%=request.getAttribute("currentServer") %> </p -->
	<!-- p> Add server : <input type = "text" name = "serverAddress" value= "https://compute1.herokuapp.com/request"> </p -->
	<!-- p> Add server : <input type = "text" name = "serverAddress" value= "http://localhost:8080/javabip-itest/req"> </p -->
	
	<fieldset>
		<legend>Servers</legend>
		
		Server address: 
		<%
		    String scheme = request.getScheme() + "://";
		    String serverName = request.getServerName();
		    String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
		    String contextPath = request.getContextPath();
		    String baseUrl = scheme + serverName + serverPort + contextPath;
		    String computeUrl = scheme + serverName + serverPort + contextPath + "/compute";
		    String compute2Url = scheme + serverName + serverPort + contextPath + "/compute2";
		%>
		<select name = "serverAddress">
			<option value="<%= computeUrl %>"> <%= computeUrl %> </option>
			<option value="<%= compute2Url %>"> <%= compute2Url %> </option>
	  		<!-- option value="http://localhost:8080/javabip-itest/req"> http://localhost:8080/javabip-itest/req </option -->
	    	<option value="https://compute1.herokuapp.com/compute"> https://compute1.herokuapp.com/request </option>
	    	<option value="https://compute2.herokuapp.com/compute"> https://compute2.herokuapp.com/request </option>
	  	</select>
	  	<input type="submit" name="add" value="Add server"> 

		<br/><br/>	  

	  	Added server:
	  	<select name="addedServer">
	  		<%
	  		List<String> serverList = Switch.serverslist;
	  		for (String serverAddress : serverList) {
	  		%>
		 		<option value="<%=serverAddress%>"> <%=serverAddress%> </option>
	  		<%
			}
	  		%>
	  	</select>
	  	<input type="submit" name="chooseServer" value="Choose initial server"> 
	  	<input type="submit" name="remove" value="Remove all servers"><br/>
	  	
	  	<br/><br/>	
	  	
	  	Current server : <%=request.getAttribute("currentServer") %>
	  	
	  	<br/><br/>
	  	
	  	<!-- input type="submit" name="switchConfirm" value="Confirm" -->
	  	
	</fieldset>
	
	<!-- p>	<input type="submit" name="add" value="Add server"> <input type="submit" name="remove" value="Remove all servers"> </p -->
	
	<br/><br/><br/><br/><br/>
	
 	<fieldset>
    	<legend>Server Test</legend>
    	The current value : <%=request.getAttribute("randomNumber") %><br/>
    	Usage : <%=request.getAttribute("counter") %> / <%=request.getAttribute("limit") %><br/>
    	<input type="submit" name="compute" value="Compute random number !">
    	<br/><br/>
    	Reset the counter of sever:
	  	<select name="resetServerlist">
	  		<%
	  		for (String serverAddress : serverList) {
	  		%>
		 		<option value="<%=serverAddress%>"> <%=serverAddress%> </option>
	  		<%
			}
	  		%>
	  	</select>
    	<input type="submit" name="resetServerCounter" value="Reset server counter">
    	<input type="submit" name="resetComponents" value="Reset components">
<!--     	<br/><br/>
    	<input type="submit" name="resetAll" value="Reset all"><br/> -->
  	</fieldset>
	
	
	<!-- The current state of the counter:  "${counter}" -->
	<!-- p> The current value : <%=request.getAttribute("randomNumber") %> </p -->
	<!-- p> Usage : <%=request.getAttribute("counter") %> / <%=request.getAttribute("limit") %> </p -->
	<!-- p> <input type="submit" name="compute" value="Compute random number !"> </p -->
	<!-- input type="hidden" name="counter" value="${counter}" -->
	
	</form>
</body>