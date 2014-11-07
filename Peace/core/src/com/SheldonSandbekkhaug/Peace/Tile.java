package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.math.Rectangle;

public class Tile {
	public static final int TILE_SIZE = 40; // TODO: Adjust tile size based of world size
	PeaceEntity e; // The PeaceEntity occupying this tile
	Rectangle rect;

	public Tile()
	{
		
	}
	
	public PeaceEntity getE() {
		return e;
	}

	public void setE(PeaceEntity e) {
		this.e = e;
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
}
