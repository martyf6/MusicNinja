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
<title>Manage Profile</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/templates/navbar.jsp"/>
		
	<!-- play profile(?) -->
		
	<!-- profile data: -->
    <div id="info"> </div>
    <div id="taste-profile" class="main-display container span12">
          <div class="container">
              <div class='hist-section span11' id="tp-summary">
                  <h4> Taste Profile Summary</h4>
                    <table class="table table-striped table-condensed table-bordered">
                        <thead>
                        </thead>
                        <tbody id="tp-sum">
							<tr>
								<th>Catalog ID</th>
								<td id="tp-sum-id">${profile.profileId}</td>
								<th>Catalog Name</th>
								<td id="tp-sum-name">${echoCatalog.name}</td>
							</tr>
							<tr>
								<th>Catalog type</th>
								<td id="tp-sum-type">${profile.type}</td>
								<th>Total items</th>
								<td id="tp-sum-total">${echoCatalog.total}</td>
							</tr>
						</tbody>
                    </table>
              </div>

<!--		
              <div class='hist-section span6' id="tp-genres">
                  <h4> Genres </h4>
                    <table class="table table-striped table-condensed table-bordered">
                        <thead>
                            <tr>
                                <th> Genre </th>
                                <th> Item count </th>
                            </tr>
                        </thead>
                        <tbody id="tp-genre-rows">
                        </tbody>
                    </table>
              </div
-->

              <div class='hist-section span6' id="tp-favorite-artists">
                  <h4> Profile Artists</h4>
                    <table class="table table-striped table-condensed table-bordered">
                        <thead>
                            <tr>
                                <th> Artist </th>
                            </tr>
                        </thead>
                        <tbody id="tp-favorite-artist-rows">
                        <c:forEach items="${artists}" var="artist">
                        	<tr><td>${artist.artistName}</td></tr>
                        </c:forEach>
                        </tbody>
                    </table>
              </div>

<!-- 
              <div class='hist-section span5' id="tp-banned-artists">
                  <h4> Your banned artists</h4>
                    <table class="table table-striped table-condensed table-bordered">
                        <thead>
                            <tr>
                                <th> Artist </th>
                            </tr>
                        </thead>
                        <tbody id="tp-banned-artist-rows">
                        </tbody>
                    </table>
              </div>
 
              <div class='hist-section span5' id="tp-most-played-artists">
                  <h4> Your Most Played artists</h4>
                    <table class="table table-striped table-condensed table-bordered">
                        <thead>
                            <tr>
                                <th> Artist </th>
                            </tr>
                        </thead>
                        <tbody id="tp-most-played-artist-rows">
                        </tbody>
                    </table>
              </div>
          </div>
-->

          <div class='hist-section' id="tp-favorite-songs">
              <h4> Your favorite songs</h4>
                <table class="table table-striped table-condensed table-bordered">
                    <thead>
                        <tr>
                            <th> Title </th>
                            <th> Artist </th>
                        </tr>
                    </thead>
                    <tbody id="tp-favorite-song-rows">
                    <c:forEach items="${songs}" var="song">
                    	<tr><td>${song.songName}</td><td>${song.getString("artist_name")}</td></tr>
                    </c:forEach>
                    </tbody>
                </table>
          </div>
          
<!--
          <div class='hist-section' id="tp-banned-songs">
              <h4> Your banned songs</h4>
                <table class="table table-striped table-condensed table-bordered">
                    <thead>
                        <tr>
                            <th> Title </th>
                            <th> Artist </th>
                        </tr>
                    </thead>
                    <tbody id="tp-banned-song-rows">
                    </tbody>
                </table>
          </div>
          
          <div class='hist-section' id="tp-skipped-songs">
              <h4> Your skipped songs</h4>
                <table class="table table-striped table-condensed table-bordered">
                    <thead>
                        <tr>
                            <th> Title </th>
                            <th> Artist </th>
                        </tr>
                    </thead>
                    <tbody id="tp-skipped-song-rows">
                    </tbody>
                </table>
          </div>
-->
          
<!-- 
          <div class='hist-section' id="tp-history" class="span12">
              <h4> Your taste details</h4>
              <table id="tp-all-items" class="table table-striped table-condensed table-bordered">
                  <thead>
                      <tr>
                          <th> # </th>
                          <th class='tbl-time'> Last update  </th>
                          <th> Title </th>
                          <th> Artist </th>
                          <th> Plays </th>
                          <th> Skips</th>
                          <th> Rating </th>
                          <th> Flags </th>
                      </tr>
                  </thead>
                  <tbody id="tp-history-rows">
                  </tbody>
              </table>
          </div>
-->

      </div> <!-- taste-profile -->
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
</body>
</html>