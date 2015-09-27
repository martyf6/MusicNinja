package com.musicninja.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "TOPLISTENTRY")
public class TopListEntryEntity {
	
	private TopListEntity topList;
	private MusicObjectEntity musicObject;

	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
	
	@Column(name = "RANK")
	private Integer rank;
	
	public int getRank() {
    	return rank;
    }
	public void setRank(int rank) {
        this.rank = rank;
    }
	
	@ManyToOne(cascade=CascadeType.ALL)
	public TopListEntity getTopList()
	{
		return topList;
	}
	public void setTopList(TopListEntity topList)
	{
		this.topList = topList;
	}
	
	public MusicObjectEntity getMusicObject()
	{
		return musicObject;
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
