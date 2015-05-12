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
<title>Create New Profile</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/templates/navbar.jsp"/>
	
	<div class="container">
	
		<div class="page-header">
			<!-- playlist name -->
			<h1><span id="profileName" data-playlist-id="${pid}">${name}</span> <small>by ${owner.id}</small></h1>
		</div>
		
		<!-- add tracks or artists search bar with auto complete -->
		<div class="col-md-8 input-group">
	      <input type="text" class="form-control" placeholder="Search for an artist or track to add...">
	      <span class="input-group-btn">
	        <button class="btn btn-default" type="button">Add</button>
	      </span>
	    </div><!-- /input-group -->
		
		<!-- playlist track selection (use .info for selected tracks styling) -->
		<table class="table table-responsive table-striped table-condensed" id="createProfileTracks">
			<thead>
				<tr>
					<th>
						<!-- select all tracks -->
						<input id="selectAllTracks" type="checkbox" aria-label="...">
					</th>
					<th>SONG</th>
					<th>ARTIST</th>
					<th>ALBUM</th>
					<th><span class="glyphicon glyphicon-time" aria-hidden="true"></span></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${tracks}" var="ptrack">
				  <tr>
				  	<td>
				  		<input class="track-select" type="checkbox" aria-label="...">
				  	</td>
				    <td class="track-name" data-track-id="${ptrack.track.id}">
				    	${ptrack.track.name}
				    </td>
				    <td class="track-artist">
				      <c:forEach items="${ptrack.track.artists}" var="artist" varStatus="loop">
				        ${artist.name}<c:if test="${!loop.last}">, </c:if>
				      </c:forEach>
				    </td>
				    <td class="track-album">${ptrack.track.album.name}</td>
				    <fmt:formatNumber var="tsecs" value="${ptrack.track.duration / 1000}" 
				    	maxFractionDigits="0" />
				    <fmt:formatNumber var="secs" type="number"
            			pattern="00" value="${tsecs mod 60}" />
				    <fmt:formatNumber var="mins" value="${(tsecs - secs) / 60}" 
				    	maxFractionDigits="0" />
				    <td>${mins}:${secs}</td>
				  </tr>
				</c:forEach>
			</tbody>
			
		</table>
		<p><a class="btn btn-primary btn-lg btn-grn" role="button" id="createProfile">Create Profile</a></p>
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