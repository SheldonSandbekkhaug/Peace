package com.SheldonSandbekkhaug.Peace;
import com.SheldonSandbekkhaug.Peace.Tile;

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
		
		// Create blank Tiles
		for (int i = 0; i < tiles.length; i++)
		{
			tiles[i] = new Tile();
		}
		
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
		for (int i = 0; i < tiles.length; i++)
		{
			Tile tile = tiles[i];
			if (tile.getE() != null)
			{
				int tileX = x + this.indexToXOffset(i);
				int tileY = y + this.indexToYOffset(i);
				batch.draw(tile.getE().getImg(), tileX, tileY);
			}
		}
	}
	
	/*
	 * Maps an index in the Tile array to a x offset used for drawing.
	 */
	public int indexToXOffset(int index)
	{
		switch(index)
		{
			case 0: 
			case 1:
			case 7:
				return Tile.TILE_SIZE * 2;
			case 2:
			case 6:
				return Tile.TILE_SIZE * 1;
			case 3:
			case 4:
			case 5:
				return 0;
			default:
				// ERROR
				System.out.println("Invalid index in indexToXOffsets.");
				return 0;
		}
	}
	
	/*
	 * Maps an index in the Tile array to a y offset used for drawing.
	 */
	public int indexToYOffset(int index)
	{
		switch(index)
		{
			case 1: 
			case 2:
			case 3:
				return Tile.TILE_SIZE * 2;
			case 4:
			case 0:
				return Tile.TILE_SIZE * 1;
			case 5:
			case 6:
			case 7:
				return 0;
			default:
				// ERROR
				System.out.println("Invalid index in indexToXOffsets.");
				return 0;
		}
	}
}