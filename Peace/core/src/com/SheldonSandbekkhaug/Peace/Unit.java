package com.SheldonSandbekkhaug.Peace;

public class Unit extends PeaceEntity {
	int strength; // Damage dealt in combat
	// NOTE: must change clone() when adding/removing properties
	
	public Unit()
	{
		super();
	}
	
	public Unit clone()
	{
		Unit u = new Unit();
		u.cloneHelper(this);
		u.setStrength(strength);
		
		return u;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}
}
