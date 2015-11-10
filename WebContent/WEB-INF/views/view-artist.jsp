<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
<title>View Playlist</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/templates/navbar.jsp"/>
	
	<div class="container">
	
		<div class="page-header">
			<!-- playlist name -->
			<h1>${info.name} <small>with ID = ${aid}</small></h1>
		</div>
		
	</div>
	
	<!-- artist information: -->
	<div class="row">
		<!-- image: -->
		<div class="col-xs-4 col-xs-offset-2">
			
			<c:forEach items="${tracks}" var="currentTrack" varStatus="stat">
			  <c:set var="tracksString" value="${tracksString},${currentTrack.id}" />
			</c:forEach>
			<iframe src="https://embed.spotify.com/?uri=spotify:trackset:Top_Songs:${tracksString}" width="300" height="380" frameborder="0" allowtransparency="true"></iframe>
			<br>
			<table class="table table-responsive table-condensed">
				<tbody>
				  <tr>
				    <td>Discovery</td>
				    <td>${info.discovery}</td>
				  </tr>
				  <tr>
				    <td>Familiarity</td>
				    <td>${info.familiarity}</td>
				  </tr>
				  <tr>
				    <td>Hotttnesss</td>
				    <td>${info.hotttnesss}</td>
				  </tr>
				  <c:forEach items="${info.genres}" var="pgenre">
				  <tr>
				    <td>Genres</td>
				    <td>${pgenre}</td>
				  </tr>
				  </c:forEach>
				  <tr>
				    <td>Followers</td>
				    <td>${info.followers}</td>
				  </tr>
				  <tr>
				    <td>Popularity</td>
				    <td>${info.popularity}</td>
				  </tr>
				</tbody>
			</table>
			
			
		</div>
		<!-- info: -->
		<div class="col-xs-4">
			<div class="artist-image">
				<img style="width:50%; display:block; margin:0 auto;" src="${info.image_url}">
			</div>
			<br>
			<div class="artist-bio">
				${info.lastfm_bio}
			</div>
		</div>
	</div>
	
	<!-- artist bio: -->
	
	<!-- play top songs: -->
	
	<jsp:include page="/WEB-INF/views/templates/track-info.jsp"/>
	<jsp:include page="/WEB-INF/views/templates/artist-info.jsp"/>
	
	<!-- jQuery -->
	<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
	<script src="http://code.jquery.com/ui/1.11.0/jquery-ui.min.js"></script>
	<!-- Bootstrap -->
	<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
	<!-- MusicNinja -->
	<script src="<c:url value="/resources/js/musicninja.js" />"></script>
</body>
</html>