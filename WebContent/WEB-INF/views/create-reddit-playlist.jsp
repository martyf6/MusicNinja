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
		</div>

		<!-- lets ask to create one -->
		<div class="reddit-playlist-container col-md-6 input-group">
			<form accept-charset="UTF-8" role="form" action="create_reddit_playlist" method="POST" id="create-reddit-playlist-form" class="reddit-playlist-form">
			<fieldset>
				<!-- playlist name -->
				<c:choose>
					<c:when test="${not empty playlistNameError}">
						<div class="form-group has-error">
				    </c:when>
					<c:otherwise>
						<div class="form-group">
				    </c:otherwise>
				</c:choose>
					<label class="control-label" for="createPlaylist-playlistName">
						<span class="error">${playlistNameError}</span>
						Playlist Name:
					</label>
					<input type="text" name="playlistName" value="Reddit Top New Music" class="form-control" id="createPlaylist-playlistName" autofocus="" />
				</div>
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
						Subreddit (minus 'r/', ex: 'Music', 'listentothis', 'hiphopheads'):
					</label>
					<input type="text" name="subreddit" value="Music" class="form-control" id="createPlaylist-subreddit" />
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
						<option value="day">Day</option>
						<option value="week">Week</option>
						<option value="month">Month</option>
						<option value="year">Year</option>
						<option value="all">All Time</option>
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
					<input type="text" name="limit" value="50" class="form-control" id="createPlaylist-limit" />
				</div>
				<input type="submit" name="submit" value="Create" class="btn btn-lg btn-primary btn-block" />
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