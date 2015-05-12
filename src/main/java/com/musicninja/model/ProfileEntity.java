package com.musicninja.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "TASTEPROFILE")
public class ProfileEntity {
 
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
    
    @Column(name = "OWNER", nullable = false)
	private Integer owner;
    
	@Column(name = "ECHOID", unique = true, nullable = false)
	private String echoId;
	
	@Column(name = "TYPE", nullable = false)
	private String type;
	
	@Column(name = "PLAYLISTID")
	private String playlistId;

	@Column(name = "PLAYLISTOWNER")
	private String playlistOwnerId;
	
    public int getId() {
    	return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void setPlaylistId(String playlistid) {
        this.playlistId = playlistid;
    }
    
    public String getPlaylistId() {
        return playlistId;
    }
    
    public String getProfileId() {
        return echoId;
    }

    public void setProfileId(String echoid) {
        this.echoId = echoid;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getPlaylistOwnerId() {
        return playlistOwnerId;
    }

    public void setPlaylistOwnerId(String playlistOwnerId) {
        this.playlistOwnerId = playlistOwnerId;
    }

    @Override
    public String toString(){
    	return "Profile {id: " + id + 
    			", owner: " + owner + 
    			", profileId: " + echoId +
    			", type: " + type +
    			", playlistId: " + playlistId + 
    			", playlistOwnerId: " + playlistOwnerId + "}";
    }
}
