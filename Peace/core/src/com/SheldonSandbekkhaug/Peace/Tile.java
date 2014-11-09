package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.math.Rectangle;

public class Tile {
	public static final int TILE_SIZE = 40; // TODO: Adjust tile size based of world size
	int tileID;
	private static int lastTileID;
	PeaceEntity e; // The PeaceEntity occupying this tile
	Rectangle rect;
	boolean marketTile; // True if this Tile is part of the Market

	public Tile()
	{
		marketTile = false;
		tileID = lastTileID + 1;
		lastTileID = tileID;
	}
	
	public int getTileID() {
		return tileID;
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

	public boolean isMarketTile() {
		return marketTile;
	}

	public void setMarketTile(boolean marketTile) {
		this.marketTile = marketTile;
	}
}
