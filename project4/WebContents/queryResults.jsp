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
		<!-- Google Suggest scripts -->
        <script type="text/javascript" src="autosuggest2.js"></script>
        <script type="text/javascript" src="suggestions2.js"></script>
        <link rel="stylesheet" type="text/css" href="autosuggest.css" />  
        <script type="text/javascript">
            window.onload = function () {
                var oTextbox = new AutoSuggestControl(document.getElementById("keySearch"), new StateSuggestions());        
            }
        </script>
        <!-- Basic CSS -->
        <link rel="stylesheet" type="text/css" href="basic.css">
		
		<title>Search Results</title>
	</head>
	<body>
		<%
			// request is an object, so need to do some casting
			String searchQuery = (String)request.getAttribute("searchQuery");
			int numSkip = Integer.parseInt(request.getAttribute("numSkip").toString());
			int numReturn = Integer.parseInt(request.getAttribute("numReturn").toString());
			SearchResult[] queryResults = (SearchResult[])request.getAttribute("queryResults");
		%>
		<div class="row-fluid">
			<div class="span6 offset3 center">
				<h1>eBay Search</h1>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6 offset3">
				<h3>Enter a new set of words to query the database. The query will span the item's name, description and category on eBay.</h3>
				<form action="./search" method="get" class="form-search form-inline center">
					<div class="input-append">
						<input id="keySearch" type="text" placeholder="Search..." name="q" class="input-xlarge search-query">
						<input type="submit" name="submitButton" class="btn btn-success" value="Search!">
						<div id="searchSuggest"></div>
					</div>
					<input type="hidden" name ="numResultsToSkip" value="0">
					<input type="hidden" name ="numResultsToReturn" value="40">
				</form>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6 offset3 center">
				<% if(queryResults.length == 0) { %>
					<div class="alert alert-error">
						No matches. Please try again above!
					</div>
				<% } else { %>
					<div class="alert alert-success">
						<h4>Search Results for "<em><%= searchQuery %></em>":</h4>
						<br>
						Click on any item below for more information!
					</div>
					<% if(numSkip != 0) { %>
						<div class="left"><a class="navigationLeft" href="./search?q=<%=searchQuery%>&numResultsToSkip=<%=numSkip-20%>&numResultsToReturn=40"><button class="btn btn-inverse">Previous<br><i class="icon-arrow-left icon-white"></i>&nbsp;[<%=numSkip-19%>-<%=numSkip%>]</button></a></div><br>
					<% } %>
					<table class="table table-striped table-bordered table-hover resultTables">
						<thead>
							<tr>
								<th>#</th>
								<th>Item Name</th>
							</tr>
						</thead>
						<tbody>
							<% int conditionLoop = queryResults.length >= 20 ? 20 : queryResults.length; %>
							<% for(int i = 0, j = numSkip; i < conditionLoop; i++, j++) { %>
								<tr>
								<td><%= j+1 %>.</td>
								<td><a href= <%="\"/eBay/item?id=" + queryResults[i].getItemId() + "\""%>><%= queryResults[i].getName() %></a></td>
								</tr>
							<% } %>
						</tbody>
					</table>
					<% if (queryResults.length == 21) { %>
						<div class="right"><a class="navigationRight" href="./search?q=<%=searchQuery%>&numResultsToSkip=<%=numSkip+20%>&numResultsToReturn=40"><button class="btn btn-inverse">Next<br>[<%=numSkip+21%>]&nbsp;<i class="icon-arrow-right icon-white"></i></button></a></div><br>
					<% } else if(queryResults.length > 20) { %>
						<div class="right"><a class="navigationRight" href="./search?q=<%=searchQuery%>&numResultsToSkip=<%=numSkip+20%>&numResultsToReturn=40"><button class="btn btn-inverse">Next<br>[<%=numSkip+21%>-<%=numSkip+40-(40-queryResults.length)%>]&nbsp;<i class="icon-arrow-right icon-white"></i></button></a></div><br>
					<% } %>
				<% } %>
			</div>
		</div>
	</body>
</html>