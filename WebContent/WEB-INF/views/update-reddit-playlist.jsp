<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
<title>Reddit Music</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/templates/navbar.jsp"/>
	
	<div class="container">
	
		<div class="page-header">
			<h1><span>My Reddit+Spotify</span></h1>
			<h3><a href="playlist?oid=${owner.spotifyUsername}&pid=${playlist.playlistId}">${playlistName}</a> <small>by ${owner.username}</small></h3>
		</div>
	
		<!-- updating an existing playlist -->
		<div class="reddit-playlist-container col-md-8 input-group">
			<form accept-charset="UTF-8" role="form" action="update_reddit_playlist" method="POST" id="update-reddit-playlist-form" class="reddit-playlist-form">
			<fieldset>
				<!-- subreddit -->
				<c:choose>
					<c:when test="${not empty subredditError}">
						<div class="form-group has-error">
				    </c:when>
					<c:otherwise>
						<div class="form-group">
				    </c:otherwise>
				</c:choose>
					<label class="control-label" for="createPlaylist-subreddit">
						<span class="error">${subredditError}</span>
						Subreddit (minus 'r/'):
					</label>
					<div class="input-append btn-group">
						<input type="text" name="subreddit" value="${playlist.subReddit}" class="form-control" id="createPlaylist-subreddit" />
						<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#">
					        <span class="caret"></span>
					    </a>
					    <ul class="dropdown-menu">
					        <li><a href="#">A</a></li>
					        <li><a href="#">B</a></li>
					        <li><a href="#">C</a></li>
					        <li class="divider"></li>
					        <li><a href="#">D</a></li>
					    </ul>
					</div>
				</div>
				<!-- period -->
				<c:choose>
					<c:when test="${not empty periodError}">
						<div class="form-group has-error">
				    </c:when>
					<c:otherwise>
						<div class="form-group">
				    </c:otherwise>
				</c:choose>
					<label class="control-label" for="createPlaylist-period">
						<span class="error">${periodError}</span>
						Top music period:
					</label>
					<select name="period" class="form-control" id="createPlaylist-period">
						<option value="day" ${playlist.period.toString().equals("day")?"selected='selected'":""}>Day</option>
						<option value="week" ${playlist.period.toString().equals("week")?"selected='selected'":""}>Week</option>
						<option value="month" ${playlist.period.toString().equals("month")?"selected='selected'":""}>Month</option>
						<option value="year" ${playlist.period.toString().equals("year")?"selected='selected'":""}>Year</option>
						<option value="all-time" ${playlist.period.toString().equals("all time")?"selected='selected'":""}>All Time</option>
					</select>
				</div>
				<!-- limit -->
				<c:choose>
					<c:when test="${not empty limitError}">
						<div class="form-group has-error">
				    </c:when>
					<c:otherwise>
						<div class="form-group">
				    </c:otherwise>
				</c:choose>
					<label class="control-label" for="createPlaylist-limit">
						<span class="error">${limitError}</span>
						Number of songs to grab:
					</label>
					<input type="text" name="limit" value="${playlist.limit}" class="form-control" id="createPlaylist-limit" />
				</div>
				<p>(Note: New songs will be added to the playlist, no old songs will be deleted.)</p>
				<input type="hidden" name="pid" value="${playlist.id}">
				<input type="submit" name="submit" value="Update" class="btn btn-lg btn-primary btn-block" />
			</fieldset>
	      	</form>
	      	<c:if test="${success}">
      		<p class="update-status bg-success">
      			Success! 
      			<a href="playlist?oid=${owner.spotifyUsername}&pid=${playlist.playlistId}">View Playlist</a>
      		</p>
      		<p>Here's a list of the songs that couldn't be found on Spotify:</p>
			<table class="table table-striped">
				<thead>
					<tr>
						<th>SONG</th>
						<th>ARTIST</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${songsNotFound}" var="song">
			  		<tr>
			    		<td>${song.title}</td>
			    		<td>${song.artist}</td>
			    	</tr>
			    	</c:forEach>
				</tbody>
			</table>
			</c:if>
	    </div><!-- /input-group -->
	
	<!-- jQuery -->
	<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
	<script src="http://code.jquery.com/ui/1.11.0/jquery-ui.min.js"></script>
	<!-- Bootstrap -->
	<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
	<!-- MusicNinja -->
	<script src="<c:url value="/resources/js/musicninja.js" />"></script>
</body>
</html>