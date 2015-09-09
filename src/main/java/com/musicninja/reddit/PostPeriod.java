package com.musicninja.reddit;


public enum PostPeriod {
	
	DAY("Day","day"), 
	WEEK("Week", "week"), 
	MONTH("Month", "month"), 
	YEAR("Year", "year"), 
	ALL_TIME("All Time", "all");
	
	private final String name;
	private final String requestVal;
	
	private PostPeriod(String name, String requestVal) {
		this.name = name;
		this.requestVal = requestVal;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String getRequestVal() {
		return this.requestVal;
	}
	
	public static PostPeriod fromString(String text) {
	    if (text != null) {
	      for (PostPeriod p : PostPeriod.values()) {
	        if (text.equalsIgnoreCase(p.requestVal)) {
	          return p;
	        }
	      }
	    }
	    return null;
	  }
}