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
<title>Login to Music Ninja</title>
</head>
<body>
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<span class="navbar-brand">Music Ninja</span>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav navbar-right">
					<li><a href="<c:url value='/register' />">Register</a></li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>
	
	<div class="container">
		<!-- Login Panel -->
	    <div class="row login-panel">
			<div class="col-md-4 col-md-offset-4">
	    		<div class="panel panel-default">
				  	<div class="panel-heading">
				    	<h3 class="panel-title">Sign in</h3>
				 	</div>
				  	<div class="panel-body">
				    	<form accept-charset="UTF-8" role="form" action="<c:url value='j_spring_security_check'/>" method="POST" id="login-form">
				    	
							<!-- Display error messages -->
					        <c:if test="${not empty error}" >
					        	<div class="alert alert-danger">${error}</div>
							</c:if>
							<c:if test="${not empty msg}" >
					        	<div class="alert alert-info">${msg}</div>
							</c:if>
							
	                    <fieldset>
				    	  	<div class="form-group">
				    		    <input class="form-control" placeholder="Username" name="j_username" type="text" autofocus="">
				    		</div>
				    		<div class="form-group">
				    			<input class="form-control" placeholder="Password" name="j_password" type="password" value="">
				    		</div>
	<!-- 			    		<div class="checkbox"> -->
	<!-- 			    	    	<label> -->
	<!-- 			    	    		<input name="remember" type="checkbox" value="Remember Me"> Remember Me -->
	<!-- 			    	    	</label> -->
	<!-- 			    	    </div> -->
				    		<input id="login-btn" class="btn btn-lg btn-primary btn-block" type="submit" value="Login">
				    	</fieldset>
				      	</form>
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