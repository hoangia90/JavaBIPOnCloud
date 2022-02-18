<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<head>
<title>Monitor</title>
<link rel="stylesheet" href="styles.css" />
<!-- <link rel="stylesheet" href="<%= request.getContextPath() %>/styles.css" /> -->
<!-- <style><%@include file="/styles.css"%></style> -->
</head>

<body>

<!-- <%= request.getContextPath() %> -->

<form action="" method="GET">

<p> Current server : <%=request.getAttribute("currentServer") %> </p>

<p> Add server : <input type = "text" name = "serverAddress" value= "https://compute1.herokuapp.com/request"> </p>

<p>	<input type="submit" name="add" value="Add server"> <input type="submit" name="remove" value="Remove all servers"> </p>



<br/><br/><br/><br/><br/>

<!-- The current state of the counter:  "${counter}" -->
<p> The current value : <%=request.getAttribute("randomNumber") %> </p>
<p> Usage : <%=request.getAttribute("counter") %> / <%=request.getAttribute("limit") %> </p>



<p> <input type="submit" name="compute" value="Compute random number !"> </p>

<!-- input type="hidden" name="counter" value="${counter}" -->
</form>

</body>