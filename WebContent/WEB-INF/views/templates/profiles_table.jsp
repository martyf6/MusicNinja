<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table class="table table-responsive table-striped table-condensed">
<thead>
	<tr>
<!-- 	<th>NAME</th> -->
		<th>ECHONEST ID</th>
		<th>PLAYLIST REF</th>
		<th>PLAYLIST OWNER</th>
	</tr>
</thead>
<tbody>
	<c:forEach items="${profiles}" var="profile">
	  <c:url value="/profile" var="profileUrl">
		<c:param name="prid" value="${profile.profileId}"/>
	  </c:url>
	  <c:url value="/playlist" var="playlistUrl">
		<c:param name="pid" value="${profile.playlistId}"/>
		<c:param name="oid" value="${profile.playlistOwnerId}"/>
	  </c:url> 
	  <tr>
	    <td class="profile-id"><a href="<c:out value="${profileUrl}"/>">${profile.profileId}</a></td>
	    <td class="profile-playlist"><a href="<c:out value="${playlistUrl}"/>">
	    	<c:out value="${playlistNames.get(profile.playlistId)}" />
	    </a></td>
	    <td class="profile-owner">${profile.playlistOwnerId}</td>
		  </tr>
		</c:forEach>
	</tbody>
</table>