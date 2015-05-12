var trackInfoBody = $('#trackInfoModal #trackInfoBody');
var artistInfoBody = $('#artistInfoModal #artistInfoBody');

function displayTrackInfo(trackId) {
    
    trackInfoBody.html('<p>Loading...</p>');
    $('#trackInfoModal').modal('show');
    
    // ... ajax request
    var request = $.ajax({
	type : "GET",
	url : "track_summary",
	data : {
	    tid : trackId
	}
    });
    
    request.done(function(resp) {
	var respJson = JSON.parse(resp);

	// check if the request succeeded
	if (respJson.success) {
	    // update the dialog area with the track info
	    trackInfoBody.html(respJson.trackInfo);
	} else {
	    trackInfoBody.html(respJson.message);
	}
    });
    
    request.fail(function(jqXHR, status) {
	trackInfoBody.html('<p>Failed to retrieve track.</p>');
	console.log("Track retrieval failed with: " + status);
    });
}

function displayArtistInfo(artistId) {
    
    artistInfoBody.html('<p>Loading...</p>');
    $('#artistInfoModal').modal('show');
    
    // ... ajax request
    var request = $.ajax({
	type : "GET",
	url : "artist_summary",
	data : {
	    aid : artistId
	}
    });
    
    request.done(function(resp) {
	var respJson = JSON.parse(resp);

	// check if the request succeeded
	if (respJson.success) {
	    // update the dialog area with the track info
	    artistInfoBody.html(respJson.artistInfo);
	} else {
	    artistInfoBody.html(respJson.message);
	}
    });
    
    request.fail(function(jqXHR, status) {
	artistInfoBody.html('<p>Failed to retrieve artist.</p>');
	console.log("Artist retrieval failed with: " + status);
    });
}

function savePlaylist(playlistName) {
    // ... ajax request
    var request = $.ajax({
	type : "POST",
	url : "save_playlist",
	data : {
	    tids : JSON.stringify(playlistTracks),
	    name : playlistName
	}
    });
    
    request.done(function(resp) {
	var respJson = JSON.parse(resp);

	// check if the request succeeded
	if (respJson.success) {
	    // update the dialog area with the track info
	    alert("success!");
	} else {
	    alert("Failed. " + respJson.message);
	}
    });
    
    request.fail(function(jqXHR, status) {
	alert('Failed to save playlist.');
	console.log("Track retrieval failed with: " + status);
    });
}

function getSelectedTrackIds() {
    var selected = [];
    
    $('#createProfileTracks input.track-select:checked').each(function() {
	var row = $(this).parent().parent();
	selected.push(row.find('td.track-name').data('track-id'));
    });
    
    return selected;
}

function createProfile() {
    
    var selectedTracks = getSelectedTrackIds();
    var profileName = $('#profileName').text();
    var playlistId = $('#profileName').data('playlist-id');
    
    // ... ajax request
    var request = $.ajax({
	type : "POST",
	url : "create_profile",
	data : {
	    name : profileName,
	    pid : playlistId,
	    tids : JSON.stringify(selectedTracks)
	}
    });
    
    request.done(function(resp) {
	var respJson = JSON.parse(resp);

	// check if the request succeeded
	if (respJson.success) {
	    // update the dialog area with the track info
	    alert("success!");
	} else {
	    alert(respJson.message);
	}
    });
    
    request.fail(function(jqXHR, status) {
	alert('Failed to create profile');
	console.log("Track retrieval failed with: " + status);
    });
}

function initPage() {
    
    // clicking tracks
    $('#playlistTracks').on('click','td.track-name a', function(e) {
	e.preventDefault();
	var trackId = $(this).parent().data('track-id');
	displayTrackInfo(trackId);
    });
    
    // clicking artists
    $('#playlistTracks').on('click','td.track-artist a', function(e) {
	e.preventDefault();
	var artistId = $(this).data('artist-id');
	displayArtistInfo(artistId);
    });
    
    // clicking select on a track (or select all tracks) in profile create page
    $('#selectAllTracks').on('click', function(e) {
	var status = $(this).prop("checked");
	$('#createProfileTracks input.track-select').prop("checked", status);
    });
    
    //$('#createProfileTracks').on('click', 'input.track-select', function(e) {
	//$(this).parent().parent().addClass('info');
    //});
    
    $('#createProfile').on('click', createProfile);
}

$(document).ready(initPage);