package com.musicninja.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;

import java.util.HashSet;

import javax.persistence.FetchType;


@Entity
@Table(name = "TOPLIST")
public class TopListEntity {
	
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
    
    @Column(name = "NAME", nullable = false)
	private String name;
	
	@Column(name = "SOURCE", nullable = false)
	private String source;
	
	@Column(name = "TYPE", nullable = false)
	private String type;
	
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
    
    
    @Override
    public String toString(){
    	return "TopList {id: " + id + 
    			", source: " + source + 
    			", type: " + type + "}";
    }

}
