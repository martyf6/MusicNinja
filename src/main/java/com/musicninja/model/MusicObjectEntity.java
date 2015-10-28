package com.musicninja.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
@Table(name = "MUSICOBJECT")
public class MusicObjectEntity {
	
	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
	
	public int getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id =id;
	}
	
	@Column(name = "NAME", nullable = false)
	private String name;
	
	@Column(name = "TYPE", nullable = false)
	private String type;
	
	@Column(name = "SPOTIFYID", nullable = true)
	private String spotifyId;
	
	@Column(name = "ECHOID", nullable = true)
	private String echoId;
	
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
    public String getEchoId() {
        return echoId;
    }
    public void setEchotId(String echoId) {
        this.echoId = echoId;
    }
	
	
	@Override
    public String toString(){
    	return "MusicObject {id: " + id + 
    			", name: " + name +
    			", type: " + type +
    			", spotifyId: " + spotifyId + 
    			", echoId: " + echoId + "}";
    }
	
}
