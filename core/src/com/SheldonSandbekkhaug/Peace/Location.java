package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/* 
 * Class to represent a region of the game world.
 * Contains a set of Tiles
 */
public class Location {
	LocationID id;
	String name;
	Tile[] tiles;
	int x, y; // The bottom-right corner of the location
	//TextureRegion region; // TODO: remove?
	Texture img;
	
	public static final int TILES_PER_ROW = 3;
	public static final int TILES_PER_COL = 3;
	
	public Location(LocationID location, String locationName, int x, int y,
			Texture t)
	{
		id = location;
		name = locationName;
		tiles = new Tile[TILES_PER_ROW * TILES_PER_COL];
		this.x = x;
		this.y = y;
		img = t;
	}
	
	/* Draw a rectangle outlining the boundaries of this location. */
	public void draw(SpriteBatch batch)
	{
		batch.draw(img, x, y,
				TILES_PER_ROW * Tile.TILE_SIZE, 
				TILES_PER_COL * Tile.TILE_SIZE); // TODO: remove?
	}
}