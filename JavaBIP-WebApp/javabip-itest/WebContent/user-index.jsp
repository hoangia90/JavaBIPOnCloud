<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<title>Monitor</title>
</head>
<body>

<form action="" method="GET">

<!-- The current state of the counter:  "${counter}" -->
<p>The current value : <%=request.getAttribute("randomNumber") %> </p>
<!-- <p>Usage : <%=request.getAttribute("counter") %> / <%=request.getAttribute("limit") %> </p> -->
<p>From server : <%=request.getAttribute("server") %> </p>



<input type="submit" name="compute" value="Compute random number !">



<!-- input type="hidden" name="counter" value="${counter}" -->
</form>

</body>