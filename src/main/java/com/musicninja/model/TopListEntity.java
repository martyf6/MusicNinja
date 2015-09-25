package com.musicninja.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "TOPLIST")
public class TopListEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
	
	@Column(name = "SOURCE", nullable = false)
	private String source;
	
	@Column(name = "TYPE", nullable = false)
	private String type;
	
	@Column(name = "SPOTIFYID", nullable = false)
	private String spotifyId;
	
	@Column(name = "ECHONESTID", nullable = false)
	private String echonestId;
	
	@Column(name = "RANK")
	private Integer rank;

	
    public int getId() {
    	return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }
    
    public int getRank() {
    	return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString(){
    	return "TopList {id: " + id + 
    			", source: " + source + 
    			", type: " + type +
    			", spotifyId: " + spotifyId + 
    			", rank: " + rank + "}";
    }

}
