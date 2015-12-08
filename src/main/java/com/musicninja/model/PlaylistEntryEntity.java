package com.musicninja.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "PLAYLISTENTRY")
public class PlaylistEntryEntity {
	
	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id =id;
	}
	
	@ManyToOne(cascade=CascadeType.ALL)
	private PlaylistEntity playlist;
	public PlaylistEntity getPlaylist()
	{
		return playlist;
	}
	public void setPlaylist(PlaylistEntity playlist)
	{
		this.playlist = playlist;
	}
	
	@ManyToOne(cascade=CascadeType.ALL)
	private TrackEntity track;
	public TrackEntity getTrack()
	{
		return this.track;
	}
	public void setTrack(TrackEntity track)
	{
		this.track = track;
	}
	
	@Column(name = "ADDED_BY_ID")
	private String addedById;
	
	public String getAddedById() {
    	return addedById;
    }
	public void setAddedById(String addedById) {
        this.addedById = addedById;
    }
	
	@Column(name = "ADDED_AT")
	private Date addedAt;
	
	public Date getAddedAt() {
    	return addedAt;
    }
	public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }
	
	@Override
    public String toString(){
    	return "TopListEntry {id: " + id + 
    			", playlist: " + playlist +
    			", track: " + track + 
    			", addedAt: " + addedAt +
    			", addedById: " + addedById +"}";
    }
	
	
}
