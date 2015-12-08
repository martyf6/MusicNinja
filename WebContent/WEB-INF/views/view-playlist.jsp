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
			<h1>${name} <small>by ${owner.id}</small></h1>
			<div class="pg-header-btn">
				<form action="create_profile" method="get">
					<input type="hidden" name="oid" value="${owner.id}"/>
					<input type="hidden" name="pid" value="${pid}"/>
					<button type="submit" class="btn btn-default btn-grn" id="createProfileBtn">Create Profile</button>
				</form>
			</div>
		</div>
		
		<!-- play playlist(?) -->
		
		<!-- playlist tracks Spotify: -->
		<table class="table table-responsive table-striped table-condensed" id="playlistTracks">
			<thead>
				<tr>
					<th>SONG</th>
					<th>ARTIST</th>
					<th>ALBUM</th>
					<th><span class="glyphicon glyphicon-time" aria-hidden="true"></span></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${tracks}" var="ptrack">
				  <tr>
				    <td class="track-name" data-track-id="${ptrack.track.id}">
				    	<a href="#">${ptrack.track.name}</a>
				    </td>
				    <td class="track-artist">
				      <c:forEach items="${ptrack.track.artists}" var="artist" varStatus="loop">
				        <a href="#" data-artist-id="${artist.id}">${artist.name}</a><c:if test="${!loop.last}">, </c:if>
				      </c:forEach>
				    </td>
				    <td class="track-album">
				    	<a href="/music-ninja/album?aid=${ptrack.track.album.id}">${ptrack.track.album.name}</a>
				    </td>
				    <c:set var="tsecs" value="${ptrack.track.duration / 1000}" />
				    <fmt:formatNumber var="secs" type="number"
            			pattern="00" value="${tsecs mod 60}" />
				    <fmt:formatNumber var="mins" value="${(tsecs - secs) / 60}" 
				    	maxFractionDigits="0" />
				    <td>${mins}:${secs}</td>
				  </tr>
				</c:forEach>
			</tbody>
		</table>
		
		<!-- playlist track MusicNinjas: -->
		<table class="table table-responsive table-striped table-condensed" id="playlistTracks">
			<thead>
				<tr>
					<th>SONG</th>
					<th><span class="glyphicon glyphicon-time" aria-hidden="true"></span></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${mntracks}" var="track">
				  <tr>
				    <td class="track-name" data-track-id="${track.id}">
				    	<a href="#">${track.name}</a>
				    </td>
				    <td>${track.tempo}</td>
				  </tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	
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