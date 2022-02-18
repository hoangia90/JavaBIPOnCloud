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
	<%@page import="org.javabip.executor.monitor.Switch" %>
	
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
		<select name = "serverAddress">
	  		<option value="http://localhost:8080/javabip-itest/req"> http://localhost:8080/javabip-itest/req </option>
	    	<option value="https://compute1.herokuapp.com/request"> https://compute1.herokuapp.com/request </option>
	    	<option value="https://compute2.herokuapp.com/request"> https://compute2.herokuapp.com/request </option>
	  	</select>
	  	<input type="submit" name="add" value="Add server"> 
	  	<input type="submit" name="remove" value="Remove all servers"><br/>

		<br/>	  

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
  	</fieldset>
	
	
	<!-- The current state of the counter:  "${counter}" -->
	<!-- p> The current value : <%=request.getAttribute("randomNumber") %> </p -->
	<!-- p> Usage : <%=request.getAttribute("counter") %> / <%=request.getAttribute("limit") %> </p -->
	<!-- p> <input type="submit" name="compute" value="Compute random number !"> </p -->
	<!-- input type="hidden" name="counter" value="${counter}" -->
	
	</form>
</body>