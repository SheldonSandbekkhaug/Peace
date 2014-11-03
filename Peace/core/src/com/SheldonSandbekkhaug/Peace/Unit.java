package com.SheldonSandbekkhaug.Peace;

public class Unit extends PeaceEntity {
	int strength;
	int currHP, maxHP;
	
	public Unit()
	{
		super();
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	public int getCurrHP() {
		return currHP;
	}

	public void setCurrHP(int currHP) {
		this.currHP = currHP;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}
}
