package com.musicninja.reddit;

public class SimpleSong {
	
	public static boolean removeParens = true;
	
	private String artist;
	private String title;
	private String genre;
	private String comment;
	
	private String spotifyId;
	
	public SimpleSong (String artist, String title, String genre, String comment) {
		this.artist = artist;
		this.title = title;
		this.genre = genre;
	}
	
	public SimpleSong (String artist, String title, String genre) {
		this(artist, title, genre, null);
	}
	
	public SimpleSong (String artist, String title) {
		this(artist, title, null, null);
	}
	
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getSpotifyId() {
		return spotifyId;
	}
	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}
	
	public static SimpleSong parseSong(String songStr) {
		
		// replace some unicode
		songStr = songStr.replace("&amp;", "&");
		
		// Reddit style: artist - song title [genre] (comments)
		// some subs also do: [optional flair] artist - song title (comments)
		
		try {
			String separatorStr = "- -";
			int separator = songStr.indexOf(separatorStr);
			if (separator <= 0) {
				separatorStr = "--";
				separator = songStr.indexOf(separatorStr);
			}
			if (separator <= 0) {
				separatorStr = " - ";
				separator = songStr.indexOf(separatorStr);
			}
			if (separator <= 0) {
				separatorStr = "-";
				separator = songStr.indexOf(separatorStr);
			}
			
			if (separator <= 0) return null;
			
			// extract artist
			String artist = songStr.substring(0, separator);
			artist = artist.trim();
			
			// cut out tags (that shouldn't exist)
			int tagEnd = artist.indexOf(']');
			if (tagEnd > 0) {
				artist = artist.substring(tagEnd + 1, artist.length());
				artist = artist.trim();
			}
			
			// extract title
			int genreBegin = songStr.indexOf('[');
			int titleEnd = genreBegin;
			if (titleEnd <= 0) titleEnd = songStr.length();
			String title = songStr.substring(separator + separatorStr.length(), titleEnd);
			title = title.trim();
			
			// extract genre
			int genreEnd = songStr.indexOf(']');
			String genre = null;
			if (genreBegin > 0 && genreEnd > 0) {
				genre = songStr.substring(genreBegin + 1, genreEnd);
				genre = genre.trim();
			}
			
			// remove parens, such as (Live at ...) or (feat..)
			String titleComment = null;
			if (removeParens) {
				int parensBegin = title.indexOf('(');
				if (parensBegin > 0) {
					int parensEnd = title.indexOf(')');
					if (parensEnd > 0) {
						titleComment = title.substring(parensBegin + 1, parensEnd);
					} else {
						titleComment = title.substring(parensBegin + 1);
					}
					title = title.substring(0, parensBegin);
				}
				title = title.trim();
			}
			
			// TODO: cut out additional Feat. Feat feat. feat Ft. ft. Ft ft
			// parse them surrounded by spaces
			
			return new SimpleSong(artist, title, genre, titleComment);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	@Override 
	public String toString() {
		return artist + " - " + title + 
				((comment != null) ? " (" + comment + ")" : "") +
				((genre != null) ? " [" + genre + "]" : "") +
				((spotifyId != null) ? " (" + spotifyId + ")" : "");
	}
}