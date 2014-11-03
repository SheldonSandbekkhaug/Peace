package com.SheldonSandbekkhaug.Peace;

public class Tile {
	public static final int TILE_SIZE = 40; // TODO: Adjust tile size based of world size
	PeaceEntity e; // The PeaceEntity occupying this tile

	public Tile()
	{
		
	}
	
	public PeaceEntity getE() {
		return e;
	}

	public void setE(PeaceEntity e) {
		this.e = e;
	}
}
