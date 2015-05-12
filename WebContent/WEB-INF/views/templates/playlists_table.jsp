<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table class="table table-responsive table-striped table-condensed">
	<thead>
		<tr>
			<th>NAME</th>
			<th>AUTHOR</th>
			<th>TRACKS</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${playlists}" var="playlist">
		  <c:url value="/playlist" var="playlistUrl">
			<c:param name="oid" value="${playlist.owner.id}"/>
			<c:param name="pid" value="${playlist.id}"/>
		  </c:url> 
		  <tr>
		    <td class="playlist-name"><a href="<c:out value="${playlistUrl}"/>">${playlist.name}</a></td>
		    <td class="playlist-owner">${playlist.owner.id}</td>
		    <td class="playlist-tracks">${playlist.tracks.total}</td>
		  </tr>
		</c:forEach>
	</tbody>
</table>