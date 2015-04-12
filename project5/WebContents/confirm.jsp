<!-- Import the SearchResult[] java libraries -->
<%@ page import="edu.ucla.cs.cs144.*" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset = "utf-8">
		<!-- Twitter Boostrap -->
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.css">
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap-responsive.css">
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap-responsive.min.css">
		<link rel="stylesheet" type="img/png" href="bootstrap/img/glyphicons-halflings.png">
		<link rel="stylesheet" type="img/png" href="bootstrap/img/glyphicons-halflings-white.png">
		<link rel="stylesheet" type="text/js" href="bootstrap/js/bootstrap.js">
		<link rel="stylesheet" type="text/js" href="bootstrap/js/bootstrap.min.js">
        <!-- Basic CSS -->
        <link rel="stylesheet" type="text/css" href="basic.css">
		<title>Purchase Item</title>
	</head>
	<body>
		<%
			String validSession = (String)request.getAttribute("Valid_Session");
			String creditCard = (String)session.getAttribute("cardNumber");
			String itemID = (String)session.getAttribute("sessionId");
			String itemName = (String)session.getAttribute("sessionName");
			String itemNameURLDecoded = URLDecoder.decode(itemName, "utf-8");
			String buyPrice = (String)session.getAttribute("sessionBuyPrice");
			String buyTime = (String)session.getAttribute("buyTime");
			session.removeAttribute(itemID);
            session.removeAttribute(itemName);
            session.removeAttribute(buyPrice);
            session.removeAttribute(buyTime);
		%>
		<% if(validSession.equals("yes")) { %>
		<div class="row-fluid">
			<div class="span6 offset3 center">
				<h1>You have purchased this item!</h1>
			</div>
		</div>
		<br>
		<div class="row-fluid">
			<div class="span6 offset3 center alert alert-success">
				<h3>Keep this information for your records:</h3>
			</div>
		</div>					
		<form action="./confirm" method="get">
			<div class="span6 offset3">
				<table class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><div class="center">Payment Fields</div></th>
							<th><div class="center">Your Receipt</div></th>
						</tr>
					</thead>
					<tbody>
						<tr>
						<td><div class="center">Credit Card Number:</div></td>
						<td><div class="center"><%=creditCard%></div></td>
						</tr>
						<tr>
						<td><div class="center">Item ID:</div></td>
						<td><div class="center"><%=itemID%></div></td>
						</tr>
						<tr>
						<td><div class="center">Item Name:</div></td>
						<td><div class="center"><%=itemNameURLDecoded%></div></td>
						</tr>
						<tr>
						<td><div class="center">Item Price:</div></td>
						<td><div class="center">$<%=buyPrice%></div></td>
						</tr>
						<tr>
						<td><div class="center">Time Posted:</div></td>
						<td><div class="center"><%=buyTime%></div></td>
						</tr>
					</tbody>
				</table>
			</form>
			<div class="alert alert-info center">
				<a href= "http://<%= request.getServerName() %>:1448<%= request.getContextPath() %>/index.html">Home Page</a>
			</div>
		</div>
		<% } else { %>
			<div class="row-fluid">
				<div class="span6 offset3 center">
					<h1>ERROR!</h1>
				</div>
			</div>
			<br>
			<div class="row-fluid">
				<div class="span6 offset3 center">
					<div class="alert alert-error">
						<h4>There was an error in your transaction, as the session might have timed out. We apologize for the inconvenience.</h4>
						<br>
						<a href="http://<%= request.getServerName() %>:1448<%= request.getContextPath() %>/item?id=<%=itemID%>">Try Again</a>
					</div>
				</div>
			</div>
		<% } %>
	</body>
</html>