package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.graphics.Texture;

/* A Unit or Structure */
public class PeaceEntity {
	String id;
	String name;
	Texture img;
	
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

	public Texture getImg() {
		return img;
	}

	public void setImg(Texture img) {
		this.img = img;
	}
}
