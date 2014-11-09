package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.graphics.Texture;

/* A Unit or Structure */
public class PeaceEntity {
	String id;
	String name;
	int cost;
	int owner; // playerID
	Texture img;
	
	public static final int NEUTRAL = 0; // Owned by no one
	
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
