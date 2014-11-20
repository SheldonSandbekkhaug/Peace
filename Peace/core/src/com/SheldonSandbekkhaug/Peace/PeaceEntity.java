package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.graphics.Texture;

/* A Unit or Structure */
public class PeaceEntity {
	String id;
	String name;
	int cost;
	int currHP, maxHP;
	int owner; // playerID, 0 by default
	Texture img;
	// NOTE: must adjust clone() method after adding/removing properties
	
	public PeaceEntity()
	{
		owner = Player.NEUTRAL;
	}
	
	public PeaceEntity clone()
	{
		PeaceEntity e = new PeaceEntity();
		e.setID(id);
		e.setName(name);
		e.setCost(cost);
		e.setCurrHP(currHP);
		e.setMaxHP(maxHP);
		e.setOwner(owner);
		e.setImg(img);
		return e;
	}
	
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
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

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public Texture getImg() {
		return img;
	}

	public void setImg(Texture img) {
		this.img = img;
	}
}
