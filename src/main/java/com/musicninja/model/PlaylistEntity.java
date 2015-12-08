package com.musicninja.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.util.HashSet;


@Entity
@Table(name = "PLAYLIST")
public class PlaylistEntity {
	
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
    
    public int getId() {
    	return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name = "NAME", nullable = false)
	private String name;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
	
	@Column(name = "SOURCE", nullable = false)
	private String source;
	
	public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
	
	@Column(name = "SOURCEID", nullable = false)
	private String sourceId;
	
	public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    
    /*
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name="PLAYLIST_ENTRY", 
                joinColumns={@JoinColumn(name="PLAYLIST_ID")}, 
                inverseJoinColumns={@JoinColumn(name="ENTRY_ID")})
    private Set<PlaylistEntryEntity> playlistEntries = new HashSet<PlaylistEntryEntity>();
	// http://viralpatel.net/blogs/hibernate-many-to-many-annotation-mapping-tutorial/
	
	public Set<PlaylistEntryEntity> getPlaylistEntries() {
		return this.playlistEntries;
	}
	public void setPlaylistEntries(Set<PlaylistEntryEntity> playlistEntries) {
		this.playlistEntries = playlistEntries;
	}
    */
    
    @Override
    public String toString(){
    	return "TopList {id: " + id + 
    			", name: " + name + 
    			", source: " + source + "}";
    }

}
