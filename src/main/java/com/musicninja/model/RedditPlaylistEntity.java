package com.musicninja.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "REDDIT_PLAYLIST")
public class RedditPlaylistEntity {
 
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
    
	@Column(name = "OWNER", nullable = false)
	private Integer owner;

	@Column(name = "PLAYLIST_ID", nullable = false)
	private String playlistId;
 
    @Column(name = "SUBREDDIT")
    private String subReddit;
 
    @Column(name = "PERIOD")
    private String period;
 
    @Column(name = "UPDATE_SIZE")
    private Integer limit;
    
    @Column(name = "LAST_REFRESH")
    private String lastRefresh;
    
    public int getId() {
    	return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public String getPlaylistId() {
		return playlistId;
	}

	public void setPlaylistId(String playlistId) {
		this.playlistId = playlistId;
	}

	public String getSubReddit() {
		return subReddit;
	}

	public void setSubReddit(String subReddit) {
		this.subReddit = subReddit;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getLastRefresh() {
		return lastRefresh;
	}

	public void setLastRefresh(String lastRefresh) {
		this.lastRefresh = lastRefresh;
	}

	@Override
    public String toString(){
    	return "RedditPlaylist {id: " + id + 
    			", owner: " + owner + 
    			", playlist: " + playlistId + 
    			", sub: " + subReddit + 
    			", period: " + period + 
    			", limit: " + limit + 
    			", last refresh: " + lastRefresh + "}";
    }
}
