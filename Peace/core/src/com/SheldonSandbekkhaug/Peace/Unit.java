package com.SheldonSandbekkhaug.Peace;

public class Unit extends PeaceEntity {
	int strength;
	int currHP, maxHP;
	
	public Unit()
	{
		super();
	}
	
	public Unit clone()
	{
		Unit u = new Unit();
		u.setCost(cost);
		u.setCurrHP(currHP);
		u.setID(id);
		u.setImg(img);
		u.setMaxHP(maxHP);
		u.setName(name);
		u.setOwner(currHP);
		u.setStrength(strength);
		
		return u;
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
