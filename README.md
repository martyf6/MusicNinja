About
====================

MusicNinja aims to tackle the various challenges people face discovering new music. Our goal is to develop a number
of utilities and services to provide users with new music to fall in love with.  MusicNinja leverages both curated
music expertise and algorithmic 'intelligence' to identifying music that aligns to a listeners tastes.

Ideas
--------------------

- pull music from various music services - both curated and generated:
	curated: 
		- reddit music blogs (there are tons and tons of subs for each genre imaginable)
		- acclaimedmusic.net (most acclaimed albums and songs of all time, sorted)
		- pitchfork
		- stereogum
		- mixerbox?
		- Rolling Stone
		
	generated:
		- gnoosic (although this is based on a massive database of personal user preferences)
		- tastekid.com (very similar to gnoosic), has an API
		- music-map (generated via gnod)
		- everynoise.com (uses both echonest and spotify to deliver genre radio - also has interesting links/reads at the bottom)

- create models for curated playlists to aid with recommendation

- conduct a 'perfectplaylist' type crowd sourcing utility for playlist generation

- create a spotify 'current' playlist service.  Essentially, create services that provide a dynamic spotify playlist,
changing either daily, weekly, etc.  For instance, a spotify hiphop playlist that gets the hottest new track each day
from reddits hiphop subthread and adds it to a playlist you have, removing the oldest one to keep the playlist size consistent
and only containing the most recent 'hot' tracks always available in a spotify playlist of yours waiting for you.

TODO
====================

The following outlines work that needs to be done on this project.

Tasks
--------------------

Profile update ticket analysis: 
Echonest provides tickets for posts (such as adding songs to a profile), 
we currently lack support for analyzing ticket status to see if adding songs was successful.
Did the update succeed? What is the status of the ticket? call the GET ticket status API endpoint to check this.

Song lookup page (ajax / json):
We want the ability to type in songs to dynamically add to a profile (or a playlist, etc.)

Create and add a spotify playlist from the generated recommendations to the user's actual spotify playlists.
Also look into the ability to add songs to the current playlist (although this should be possible from within the spotify widget)

Artist view page (we just have ajax metadata popup at the moment)

Album view page (currently no clickable album link)

Show a list of recommendations on the playlist view page (via various websites that have recommendation features)

Update/add to audio summary ajax request:
Maybe show similar tracks? Add link to artist and album?

Add a friends page and support for friends. No account? send invite!

Loading image for long running processes like backup playlists

Enhancements 
--------------------

- user page (currently just display's spotify id)

- add sortable columns to playlist and profile views

- show associated profiles when viewing a playlist

- twitter id - create link

- Catalog / Profile retrieval needs work.  Catalogs don't share inheritance.
- Also, a better class can be constructed to represent catalogs (currently, they store
data in an extremely obscure way)

- Better use of Echonest API

- better object caching - ex: getting profile name is an echonest request
	we want to use a decorator on the profile (playlist, etc. objs) to provide additional data
	that we need to request, but also store that obj so we don't need to keep requesting it
	this allows us to keep our database lean (so we don't duplicate storage of profile names, for instance)
	but also efficient.

- create spring singleton objects for spotify and echnest request classes
	- populate each object with the request params (api keys, etc.)
	- create interfaces for the request classes

- provide better user handling for pages that require spotify integration (new user roles, such as SPOTIFY_USER?)
	http://stackoverflow.com/questions/16510259/using-intercept-url-in-spring-security

- provide playlist metrics
	- how diverse the playlist is
	- genre analysis
	- hotness / discoverability / danceability / energy / liveliness / tempo / acousticness / speechiness / etc. analysis
	- decade / release date analysis

- provide simple recommendation features:
	- echonest similar artists
	- click on a song, display information and provide echonest recommended other songs?
	- generate recommendations based on playlist
		- select / deselect songs in playlist before generating

Fixes
--------------------

- better API object management for spotify requests
	- the API object right now is shared across all requests, but needs to be synchronized
	or copied.
	
	- we probably want to cache the user's access token in a session object
		we don't want to need to run an access token lookup every time a request needs to be made
		(do we also want to cache the expiration and always make validation calculations upon each request?)

- look into custom exception implementations
	- if a user doesn't have spotify, but makes spotify requests
	
Notes
--------------------

- spotify playlist requests return partially populated User objects (which is why only the
user id is being displayed and not the display name... this is omitted from the response).
In order to get the display name, we need to make a separate User request with the user id.

*** INSPIRATION:
=======
Inspiration
--------------------

http://evolver.fm/appdb/keyword/echonest/

	
References
--------------------

jEN Javadoc: 
http://static.echonest.com.s3.amazonaws.com/jEN/javadoc/index.html

Spotify Web API Java:
https://github.com/thelinmichael/spotify-web-api-java

Echonest:
http://developer.echonest.com/docs/v4/
Demos:
http://static.echonest.com/labs/demo.html

Echonest + Spotify:
http://static.echonest.com/enspex/

Reddit:
https://www.reddit.com/dev/api
