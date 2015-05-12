package com.musicninja.suggest;

public class Preference {
	
	public static final float DEFAULT_VARIETY = 0.5f;
	public static final float DEFAULT_ADVENTUROUSNESS = 0.2f;
	public static final Distribution DEFAULT_DISTRIBUTION = Distribution.FOCUSED;
	
	public enum Distribution {
		FOCUSED, WANDERING;
		
		@Override
		public String toString() {
			return this.toString().toLowerCase();
		}
	}
	
	private float variety = DEFAULT_VARIETY;
	private float adventurousness = DEFAULT_ADVENTUROUSNESS;
	private Distribution distribution = DEFAULT_DISTRIBUTION;
	private boolean lesserKnown;
	
	public float getVariety() {
		return variety;
	}
	
	public void setVariety(Float variety) {
		if (variety != null) 
			this.variety = variety;
	}
	
	public float getAdventurousness() {
		return adventurousness;
	}
	
	public void setAdventurousness(Float adventurousness) {
		if (adventurousness != null) 
			this.adventurousness = adventurousness;
	}
	
	public Distribution getDistribution() {
		return distribution;
	}
	
	public void setDistribution(Distribution distribution) {
		if (distribution != null) 
			this.distribution = distribution;
	}
	
	public boolean isFocused() {
		return (distribution == null || distribution.equals(Distribution.FOCUSED));
	}
	
	public boolean isLesserKnown() {
		return lesserKnown;
	}
	
	public void setLesserKnown(boolean lesserKnown) {
		this.lesserKnown = lesserKnown;
	}
}
