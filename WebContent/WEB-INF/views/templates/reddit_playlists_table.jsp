<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table class="table table-responsive table-striped table-condensed">
<thead>
	<tr>
	 	<th>PLAYLIST NAME</th>
		<th>SUBREDDIT</th>
		<th>TIME PERIOD</th>
		<th>LIMIT</th>
	</tr>
</thead>
<tbody>
	<c:forEach items="${redditPlaylists}" var="playlist">
	  <c:url value="/update_reddit_playlist" var="redditPlaylistUrl">
		<c:param name="rpid" value="${playlist.id}"/>
	  </c:url>
	  <tr>
	    <td class="playlist-name">
	    	<a href="<c:out value="${redditPlaylistUrl}"/>">
	    		<c:out value="${redditPlaylistNames.get(playlist.id)}" />
	    	</a>
	    </td>
	    <td class="profile-owner">${playlist.subReddit}</td>
	    <td class="profile-owner">${playlist.period}</td>
	    <td class="profile-owner">${playlist.limit}</td>
	  </tr>
	</c:forEach>
</tbody>
</table>