<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!-- Bootstrap -->
<link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet" />
<!-- jQuery -->
<link href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" rel="stylesheet" />
<!-- MusicNinja -->
<link rel="stylesheet" href="<c:url value="/resources/css/musicninja.css" />" type="text/css" />
<title>Home</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/templates/navbar.jsp"/>
	
	<div class="container">
	
		<div class="welcome jumbotron">
		  <h1>Welcome, <c:out value="${username}" />!</h1>
		  <p>Get started with MusicNinja by linking your account with Spotify.</p>
		  <p><a class="btn btn-primary btn-lg btn-grn" href="/spotifyauth" role="button">Link to Spotify</a></p>
		</div>
	
	</div>
	
	<!-- jQuery -->
	<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
	<script src="http://code.jquery.com/ui/1.11.0/jquery-ui.min.js"></script>
	<!-- Bootstrap -->
	<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
	<!-- MusicNinja -->
	<script src="<c:url value="/resources/js/musicninja.js" />"></script>
</body>
</html>