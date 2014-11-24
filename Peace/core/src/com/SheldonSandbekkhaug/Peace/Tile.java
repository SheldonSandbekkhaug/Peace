package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Tile {
	public static final int TILE_SIZE = 40; // TODO: Adjust tile size based of world size
	int tileID;
	private static int lastTileID = 0;
	PeaceEntity e; // The PeaceEntity occupying this tile
	Rectangle rect;
	boolean marketTile; // True if this Tile is part of the Market

	public Tile()
	{
		marketTile = false;
		tileID = lastTileID + 1;
		lastTileID = tileID;
	}
	
	/* Draw the Tile (and its contents) at using x, y as its bottom-left
	 * corner.
	 */
	public void draw(SpriteBatch batch, BitmapFont font, float x, float y)
	{
		if (e != null)
		{
			batch.draw(e.getImg(), x, y);
			
			// Draw HP. These coordinates specify top-left corner
			String label = "" + e.getCurrHP();
			float hpX = x + Tile.TILE_SIZE - font.getBounds(label).height;
			float hpY = y + font.getBounds(label).height;
			font.draw(batch, label, hpX, hpY);
			
			// Draw Strength if E is a Unit
			if (e instanceof Unit)
			{
				Unit u = (Unit)e;
				float strengthX = x;
				font.draw(batch, "" + u.getStrength(), strengthX, hpY);
			}
		}
	}
	
	/* Set the last tile ID to 0. */
	public static void resetLastTileID()
	{
		lastTileID = 0;
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
