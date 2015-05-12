<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
<title>Register for Music Ninja</title>
</head>
<body>
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<span class="navbar-brand">Music Ninja</span>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav navbar-right">
					<li><a href="<c:url value='/login' />">Login</a></li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>
	
	<div class="container">
		<!-- Login Panel -->
	    <div class="row registration-panel">
			<div class="col-md-4 col-md-offset-4">
	    		<div class="panel panel-default">
				  	<div class="panel-heading">
				    	<h3 class="panel-title">Register</h3>
				 	</div>
				  	<div class="panel-body">
				    	<form:form accept-charset="UTF-8" role="form" action="register" method="POST" id="register-form" commandName="registration">
						<fieldset>
							<c:set var="usernameErrors"><form:errors path="username"/></c:set>
							<c:choose>
								<c:when test="${not empty usernameErrors}">
									<div class="form-group has-error">
							    </c:when>
								<c:otherwise>
									<div class="form-group">
							    </c:otherwise>
							</c:choose>
								<label class="control-label" for="register-username">
									<form:errors path="username" />
								</label>
								<form:input type="text" path="username" placeholder="Username" class="form-control" id="register-username" autofocus="" />
							</div>
							<c:set var="passwordErrors"><form:errors path="password"/></c:set>
							<c:choose>
								<c:when test="${not empty passwordErrors}">
									<div class="form-group has-error">
							    </c:when>
								<c:otherwise>
									<div class="form-group">
							    </c:otherwise>
							</c:choose>
								<label class="control-label" for="register-password">
									<form:errors path="password" />
								</label>
								<form:password path="password" placeholder="Password" class="form-control" id="register-password" />
							</div>
							<c:set var="confirmPasswordErrors"><form:errors path="confirmPassword"/></c:set>
							<c:choose>
								<c:when test="${not empty confirmPasswordErrors}">
									<div class="form-group has-error">
							    </c:when>
								<c:otherwise>
									<div class="form-group">
							    </c:otherwise>
							</c:choose>
								<label class="control-label" for="register-confirmPassword">
									<form:errors path="confirmPassword" />
								</label>
								<form:password path="confirmPassword" placeholder="Confirm Password" class="form-control" id="register-confirmPassword" />
							</div>
							<c:set var="emailErrors"><form:errors path="email"/></c:set>
							<c:choose>
								<c:when test="${not empty emailErrors}">
									<div class="form-group has-error">
							    </c:when>
								<c:otherwise>
									<div class="form-group">
							    </c:otherwise>
							</c:choose>
								<label class="control-label" for="register-email">
									<form:errors path="email" />
								</label>
								<form:input type="text" path="email" placeholder="Email" class="form-control" id="register-email" />
							</div>
							<input type="submit" name="submit" value="Register" class="btn btn-lg btn-primary btn-block" />
						</fieldset>
				      	</form:form>
				    </div>
				</div>
			</div>
		</div>
	</div>

	<!-- jQuery -->
<!-- 	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script> -->
	<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
	<script src="http://code.jquery.com/ui/1.11.0/jquery-ui.min.js"></script>
	<!-- Bootstrap -->
	<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
	<!-- MusicNinja -->
<%-- 	<script src="<c:url value="/resources/js/musicninja.js" />"></script> --%>
</body>
</html>