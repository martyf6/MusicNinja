package com.musicninja.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;

@Entity
@Table(name = "TOPLISTENTRY")
public class TopListEntryEntity {
	
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

	@Column(name = "RANK")
	private Integer rank;
	
	public int getRank() {
    	return rank;
    }
	public void setRank(int rank) {
        this.rank = rank;
    }
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ListID", nullable = false)
	private TopListEntity topList;
	public TopListEntity getTopList()
	{
		return this.topList;
	}
	public void setTopList(TopListEntity topList)
	{
		this.topList = topList;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MusicObjectID", nullable = false)
	private MusicObjectEntity musicObject;
	public MusicObjectEntity getMusicObject()
	{
		return this.musicObject;
	}
	public void setMusicObject(MusicObjectEntity musicObject)
	{
		this.musicObject = musicObject;
	}
	
	@Override
    public String toString(){
    	return "TopListEntry {id: " + id + 
    			", rank: " + rank +
    			", topList: " + topList +
    			", musicObject: " + musicObject + "}";
    }
	
	
}
