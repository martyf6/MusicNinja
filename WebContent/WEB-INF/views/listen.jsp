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
<title>Listen</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/templates/navbar.jsp"/>
	
	<div class="container">
	
		<div class="page-header">
			<!-- playlist name -->
			<h1>Now Playing - ${name} <small>by ${owner.username}</small></h1>
			<c:url value="/playlist" var="playlistUrl">
				<c:param name="oid" value="${playlist.owner.id}"/>
				<c:param name="pid" value="${playlist.id}"/>
			</c:url>
			<p class="lead">based off of playlist <a href="<c:out value="${playlistUrl}"/>">${playlist.name}</a></p>
		</div>

		<div class="row">
			<div class="col-md-6">
	    	<table class="table table-responsive table-striped table-condensed">
		      <tr>
		        <td>variety</td>
		        <td><input type="text"  size=24 id="variety" value='0.5'/></td>
		        <td>(0.0-1.0)</td>
		      </tr>
		      <tr>
		        <td>distribution</td>
		        <td><input type="text"  size=24 id="distribution" value='focused'/></td>
		        <td>('focused' or 'wandering')</td>
		      </tr>
		      <tr>
		        <td>adventurousness</td>
		        <td><input type="text"  size=24 id="adventurousness" value='0.2'/></td>
		        <td>(0.0-1.0)</td>
		      </tr>
		      <tr>
		        <td>lesser known</td>
		        <td><input type="checkbox" id="lesserKnown"></td>
		        <td>(check for lesser known songs)</td>
		      </tr>
	          <tr>
		        <td>playlist size</td>
		        <td><input type="text" size="24" id="size" value="20"></td>
		        <td>(1-100)</td>
		      </tr>
		    </table>
	
	    	<button value="go" id="go" name="go"  disabled onclick="playProfile()"> Listen! </button>
	    	</div> <!-- preferences column -->
	    
	    	<div class="col-md-6">
			    <div class ='info'></div>
			
			    <div id="info"> </div>
			    <div id="all_results">
			        <div id='tracks'>
			            <div id="results"> </div>
			        </div>
			    </div>
		    </div> <!-- playlist column -->
	    </div> <!-- row -->
    
	</div> <!-- container -->
	
	<jsp:include page="/WEB-INF/views/templates/track-info.jsp"/>
	<jsp:include page="/WEB-INF/views/templates/artist-info.jsp"/>
	
	<!-- jQuery -->
	<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
	<script src="http://code.jquery.com/ui/1.11.0/jquery-ui.min.js"></script>
	<!-- Bootstrap -->
	<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
	<!-- MusicNinja -->
	<script src="<c:url value="/resources/js/musicninja.js" />"></script>
	
	<script type="text/javascript">

	var profileId = "${profile.profileId}";
	var playlistTracks = new Array();

	jQuery.ajaxSettings.traditional = true; 
	
	var embed = '<iframe src="https://embed.spotify.com/?uri=spotify:trackset:SmartPlaylist:TRACKS" style="width:640px; height:520px;" frameborder="0" allowtransparency="true"></iframe>';
	
	
	function fetchPlaylist(profileId, variety, distribution, adventurousness, lesserKnown, size) {
	    
	    $("#all_results").hide();
	    info("loading...");
	    var request = $.ajax({
			type : "GET",
			url : "getMusic",
			data : {
			    prid : profileId,
			    variety : variety,
			    distribution : distribution,
			    adventurousness : adventurousness,
			    lesserKnown : lesserKnown,
			    size : size
			}
	    });
	    
	    request.done(function(resp) {
			var respJson = JSON.parse(resp);
	
			// check if the request succeeded
			if (respJson.success) {
			    info("");
		        $("#results").empty();
		        $("#all_results").show();
		        playlistTracks = new Array();
		
		        var tracks = "";
		        for (var i = 0; i < respJson.songs.length; i++) {
		            var song = respJson.songs[i];
		            var tid = song.substring(song.lastIndexOf(':') + 1);
		            playlistTracks.push(tid);
		            tracks = tracks + tid + ',';
		        }
		        var tembed = embed.replace('TRACKS', tracks);
		        var li = $("<span>").html(tembed);
		        $("#results").append(li);
			} else {
			    trackInfoBody.html(respJson.message);
			}
	    });
	    
	    request.fail(function(jqXHR, status) {
			console.log("Track retrieval failed with: " + status);
	    });
	}
	
	
	function playProfile() {
	    var variety = $("#variety").val();
	    var distribution = $("#distribution").val();
	    var adventurousness = $("#adventurousness").val();
	    var lesserKnown = $("#lesserKnown").prop('checked');
	    var size = $("#size").val();
	    fetchPlaylist(profileId, variety, distribution, adventurousness, lesserKnown, size);
	}
	
	function info(txt) {
	    $("#info").text(txt);
	}
	
	$(document).ready(function() {
	    $("#go").removeAttr("disabled");
	    $("#all_results").hide();
	});
	</script>
</body>
</html>