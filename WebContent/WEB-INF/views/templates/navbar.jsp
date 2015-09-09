<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" href="home">Music Ninja</a>
		</div>
		<div class="collapse navbar-collapse">
			<ul class="nav navbar-nav">
				<li><a href="home">Home</a></li>
	            <li><a href="playlists">Playlists</a></li>
	            <li><a href="profiles">Profiles</a></li>
	            <li><a href="reddit">Reddit</a></li>
	        </ul>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="<c:url value='/j_spring_security_logout' />">Sign Out</a></li>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">
						<c:out value="${username}" /> <b class="caret"></b>
					</a>
					<ul class="dropdown-menu">
						<li><a href="home">Profile</a></li>
						<li class="divider"></li>
						<li><a href="<c:url value='/j_spring_security_logout' />">Sign Out</a></li>
					</ul>
				</li>
			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
</div>