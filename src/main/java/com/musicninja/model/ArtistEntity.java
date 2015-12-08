package com.musicninja.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.echonest.api.v4.Track;

@Entity
@Table(name = "ARTIST")
public class ArtistEntity {

	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ARTIST_ID", unique = true, nullable = false)
    private Integer artistId;
	
	public int getId() {
		return this.artistId;
	}
	public void setId(Integer artistId) {
		this.artistId = artistId;
	}
	
	@Column(name = "LAST_REFRESH", nullable = false)
    private String lastRefresh;
    
    public String getLastRefresh() {
		return lastRefresh;
	}

	public void setLastRefresh(String lastRefresh) {
		this.lastRefresh = lastRefresh;
	}
	
	@ManyToMany(mappedBy="artists")
    private Set<TrackEntity> tracks = new HashSet<TrackEntity>();
	
	
	
	// *****Spotify*****
	// To Add: genres, images
	@Column(name = "SPOTIFYID", unique = true, nullable = false)
	private String spotifyId;
	
	public String getSpotifyId() {
        return spotifyId;
    }
    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }
    
	@Column(name = "NAME", nullable = false)
	private String name;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
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
    // To Add: blogs, images, news, reviews, songs, urls, video, genres
	@Column(name = "ECHOID", unique = true)
	private String echoId;
	
	public String getEchoId() {
        return echoId;
    }
    public void setEchoId(String echoId) {
        this.echoId = echoId;
    }
    
	@Column(name = "LASTFM_BIO")
	@Type(type="text")
	private String lastfmBio;
	
	public String getLastfmBio() {
        return lastfmBio;
    }
    public void setLastfmBio(String lastfmBio) {
        this.lastfmBio = lastfmBio;
    }
    
    @Column(name = "DISCOVERY")
	private Float discovery;
	
	public Float getDiscovery() {
        return discovery;
    }
    public void setDiscovery(Float discovery) {
        this.discovery = discovery;
    }
    
    @Column(name = "DISCOVERY_RANK")
	private Float discovery_rank;
	
	public Float getDiscoveryRank() {
        return discovery_rank;
    }
    public void setDiscoveryRank(Float discovery_rank) {
        this.discovery_rank= discovery_rank;
    }
    
    @Column(name = "FAMILIARITY")
	private Float familiarity;
	
	public Float getFamiliarity() {
        return familiarity;
    }
    public void setFamiliarity(Float familiarity) {
        this.familiarity = familiarity;
    }
    
    @Column(name = "FAMILIARITY_RANK")
	private Float familiarity_rank;
	
	public Float getFamiliarityRank() {
        return familiarity_rank;
    }
    public void setFamiliarityRank(Float familiarity_rank) {
        this.familiarity_rank = familiarity_rank;
    }
    
    @Column(name = "HOTTTNESSS")
	private Float hotttnesss;
	
	public Float getHotttnesss() {
        return hotttnesss;
    }
    public void setHotttnesss(Float hotttnesss) {
        this.hotttnesss = hotttnesss;
    }
    
    @Column(name = "HOTTTNESSS_RANK")
	private Float hotttnesss_rank;
	
	public Float getHotttnesssRank() {
        return hotttnesss_rank;
    }
    public void setHotttnesssRank(Float hotttnesss_rank) {
        this.hotttnesss_rank = hotttnesss_rank;
    }
    
    @Column(name = "LOCATION")
	private String location;
	
	public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    
    @Column(name = "YEARS_ACTIVE")
	private Float years_active;
	
	public Float getYearsActive() {
        return years_active;
    }
    public void setYearsActive(Float years_active) {
        this.years_active = years_active;
    }
    
    @Override
    public String toString(){
    	return "Artist {id: " + artistId + 
    			", name: " + name +
    			", spotifyId: " + spotifyId +
    			", echoId: " + echoId +
    			", years active: " + years_active +
    			", popularity: " + popularity +
    			", last refresh: " + lastRefresh + "}";
    }
}
