package com.musicninja.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "TRACK")
public class TrackEntity {
	
	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "TRACK_ID", unique = true, nullable = false)
    private Integer trackId;
	
	public int getId() {
		return this.trackId;
	}
	public void setId(Integer trackId) {
		this.trackId = trackId;
	}
	
	@Column(name = "LAST_REFRESH", nullable = false)
    private String lastRefresh;
    
    public String getLastRefresh() {
		return lastRefresh;
	}

	public void setLastRefresh(String lastRefresh) {
		this.lastRefresh = lastRefresh;
	}
    
    
    // *****Spotify*****
    
    @Column(name = "NAME", nullable = false)
	private String name;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "SPOTIFYID", unique = true, nullable = false)
	private String spotifyId;
	
	public String getSpotifyId() {
        return spotifyId;
    }
    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }
    
	@ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name="TRACK_ARTIST", 
                joinColumns={@JoinColumn(name="TRACK_ID")}, 
                inverseJoinColumns={@JoinColumn(name="ARTIST_ID")})
    private Set<ArtistEntity> artists = new HashSet<ArtistEntity>();
	// http://viralpatel.net/blogs/hibernate-many-to-many-annotation-mapping-tutorial/
	
	public Set<ArtistEntity> getArtists() {
		return this.artists;
	}
	public void setArtists(Set<ArtistEntity> artists) {
		this.artists = artists;
	}
	
    @Column(name = "DURATION")
	private Float duration;
    
    public Float getDuration() {
		return this.duration;
	}
	public void setDuration(Float duration) {
		this.duration = duration;
	}
	
	@Column(name = "POPULARITY")
	private Float popularity;
    
    public Float getPopularity() {
		return this.popularity;
	}
	public void setPopularity(Float popularity) {
		this.popularity = popularity;
	}
    
    // *****EchoNest*****
	// To Add: artist, title, song_id, style, mood, song_type
	@Column(name = "ECHOID", unique = true)
	private String echoId;
	
	public String getEchoId() {
        return echoId;
    }
    public void setEchoId(String echoId) {
        this.echoId = echoId;
    }
    
	@Column(name = "AUDIO_MD5")
	private Float audio_md5;
	
	@Column(name = "DANCEABILITY")
	private Float danceability;
	
	@Column(name = "ENERGY")
	private Float energy;
	
	@Column(name = "LOUDNESS")
	private Float loudness;
	
	@Column(name = "INSTRUMENTALNESS")
	private Float instrumentalness;
	
	public Float getInstrumentalness() {
        return instrumentalness;
    }
    public void setInstrumentalness(Float instrumentalness) {
        this.instrumentalness = instrumentalness;
    }
	
	@Column(name = "MODE")
	private Float mode;
	
	@Column(name = "SPEECHINESS")
	private Float speechiness;
	
	public Float getSpeechiness() {
        return speechiness;
    }
    public void setSpeechiness(Float speechiness) {
        this.speechiness = speechiness;
    }
	
	@Column(name = "ACOUSTICNESS")
	private Float acousticness;
    
    @Column(name = "LIVENESS")
	private Float liveness;
    
    @Column(name = "TEMPO")
	private Float tempo;
    
    public Float getTempo() {
        return tempo;
    }
    public void setTempo(Float tempo) {
        this.tempo = tempo;
    }
    
    @Column(name = "VALENCE")
	private Float valence;
    
    @Column(name = "TIME_SIGNATURE")
	private Integer time_signature;
    
    @Column(name = "MUSIC_KEY")
	private Integer music_key;
    
    @Column(name = "DESCRIPTION")
	private String description;
    
	@Override
    public String toString(){
    	return "Track {id: " + trackId + 
    			", name: " + name +
    			", spotifyId: " + spotifyId +
    			", instrumentalness: " + instrumentalness +
    			", speechiness: " + speechiness +
    			", tempo: " + tempo + 
    			", lastRefresh: " + lastRefresh + "}";
    }

}
