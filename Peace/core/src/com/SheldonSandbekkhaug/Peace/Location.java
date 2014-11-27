package com.SheldonSandbekkhaug.Peace;
import com.SheldonSandbekkhaug.Peace.Tile;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


/* 
 * Class to represent a region of the game world.
 * Contains a set of Tiles.
 * Tiles are drawn in a 3x3 grid. These are their indices:
 * 
 * 3 2 1
 * 4 8 0
 * 5 6 7
 * 
 */
public class Location {
	LocationID id;
	String name;
	Tile[] tiles;
	Texture img;
	Rectangle rect; // The region occupied by this location
	static BitmapFont font; // Used for rendering strength and HP values of Entities
	
	public static final int TILES_PER_ROW = 3;
	public static final int TILES_PER_COL = 3;
	
	public static final int E = 0;
	public static final int NE = 1;
	public static final int N = 2;
	public static final int NW = 3;
	public static final int W = 4;
	public static final int SW = 5;
	public static final int S = 6;
	public static final int SE = 7;
	public static final int CENTER = 8;
	
	// Space between Tiles
	public static final int INTERNAL_BUFFER = Tile.TILE_SIZE / 3;
	
	public Location(LocationID id)
	{
		this.id = id;
		rect = new Rectangle(0, 0, 0, 0);
		
		// Create blank Tiles
		tiles = new Tile[TILES_PER_ROW * TILES_PER_COL];
		for (int i = 0; i < tiles.length; i++)
		{
			tiles[i] = new Tile();
		}
	}
	
	public Location(LocationID location, String locationName, Texture t)
	{
		this(location);
		name = locationName;
		img = t;
	}
	
	/* 
	 * Draw a rectangle outlining the boundaries of this location, as well
	 * as the Tiles and their Entities.
	 */
	public void draw(SpriteBatch batch)
	{
		batch.draw(img, rect.x, rect.y, rect.width, rect.height);
		
		// Draw each Tile
		for (int i = 0; i < tiles.length; i++)
		{
			Tile tile = tiles[i];
			float tileX = rect.x + indexToXOffset(i);
			float tileY = rect.y + indexToYOffset(i);
			tile.draw(batch, font, tileX, tileY);
		}
	}
	
	/*
	 * Maps an index in the Tile array to a x offset used for drawing.
	 */
	public static float indexToXOffset(int index)
	{
		switch(index)
		{
			case 0: 
			case 1:
			case 7:
				return (Tile.TILE_SIZE * 2) + (INTERNAL_BUFFER * 3);
			case 2:
			case 8:
			case 6:
				return (Tile.TILE_SIZE) + (INTERNAL_BUFFER * 2);
			case 3:
			case 4:
			case 5:
				return INTERNAL_BUFFER;
			default:
				// ERROR
				System.out.println("Invalid index in indexToXOffsets: " + index);
				return 0;
		}
	}
	
	/*
	 * Maps an index in the Tile array to a y offset used for drawing.
	 */
	public static float indexToYOffset(int index)
	{
		switch(index)
		{
			case 1: 
			case 2:
			case 3:
				return (Tile.TILE_SIZE * 2) + (INTERNAL_BUFFER * 3);
			case 4:
			case 8:
			case 0:
				return (Tile.TILE_SIZE) + (INTERNAL_BUFFER * 2);
			case 5:
			case 6:
			case 7:
				return INTERNAL_BUFFER;
			default:
				// ERROR
				System.out.println("Invalid index in indexToYOffsets: " + index);
				return 0;
		}
	}

	public LocationID getID() {
		return id;
	}

	public void setID(LocationID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Tile[] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[] tiles) {
		this.tiles = tiles;
	}

	public Texture getImg() {
		return img;
	}

	public void setImg(Texture img) {
		this.img = img;
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
}