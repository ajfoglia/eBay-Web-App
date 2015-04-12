<!-- Import the SearchResult[] java libraries -->
<%@ page import="edu.ucla.cs.cs144.*" %>
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
			// request is an object, so need to do some casting
			String itemID = (String)session.getAttribute("sessionId");
			String itemName = (String)session.getAttribute("sessionName");
			String buyPrice = (String)session.getAttribute("sessionBuyPrice");
			String decodedName = (String)session.getAttribute("decodedName");
		%>
		<div class="row-fluid">
			<div class="span6 offset3 center">
				<h1>Purchase Item</h1>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6 offset3 center">
				<h3>If you would like to purchase this item, please enter your credit card number below and hit "Submit"!</h3><br>
			</div>
		</div>
		<form action="https://<%= request.getServerName() %>:8443<%= request.getContextPath() %>/confirm" method="post">
			<div class="row-fluid">
				<div class="span3 offset3">
					<strong>Credit Card Number:</strong>
				</div>
				<div class="span3 center">
					<input type="text" name="creditCard" placeholder="Credit Card # Here..." required>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span3 offset3">
					Item ID:
				</div>
				<div class="span3 center">
					<input type="text" name="confirmItemId" value="<%=itemID%>" disabled>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span3 offset3">
					Item Name:
				</div>
				<div class="span3 center">
					<input type="text" name="confirmItemName" value="<%=decodedName%>" disabled>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span3 offset3">
					Item Price:
				</div>
				<div class="span3 center">
					USD $:<input type="text" name="confirmItemBuyPrice" value="<%=buyPrice%>" disabled>
				</div>
			</div>
			<br>
			<div class="row-fluid">
				<div class="span6 offset3 center">
					<input type="submit" name="purchaseButton" class="btn btn-success" value="Submit">
				</div>
			</div>
		</form>
	</body>
</html>