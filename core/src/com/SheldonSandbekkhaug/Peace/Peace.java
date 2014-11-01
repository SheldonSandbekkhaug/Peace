package com.SheldonSandbekkhaug.Peace;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/* Class for client-side game logic */
public class Peace extends Game {
	// Constants for positioning elements on the screen
	public static final int WINDOW_WIDTH = 1000;
	public static final int WINDOW_HEIGHT = 800;
	
	// Distance between window edge and game world
	public static final int X_BUFFER = 100;
	public static final int Y_BUFFER = 80;
	
	public static final int WORLD_WIDTH = WINDOW_WIDTH - X_BUFFER * 2;
	public static final int WORLD_HEIGHT = WINDOW_HEIGHT - Y_BUFFER * 2;
	
	// Space between a Location and other objects
	public static final int LOCATION_X_BUFFER_SIZE = 40;
	public static final int LOCATION_Y_BUFFER_SIZE = 40;
	
	public SpriteBatch batch;
	public String skin;
	
	public ArrayList<Location> locations;
	public HashMap<String, Unit> units;
	
	private PeaceNetworkClient network;
	
	@Override
	public void create () {
		 // TODO: Dynamically set network information
		int PORT = 27960;
		network = new PeaceNetworkClient();
		network.connect(5000, "localhost", PORT);
		
		batch = new SpriteBatch();
		skin = "default_1.0"; // TODO: set dynamically
		locations = createLocations();
		loadUnits();
		
		this.setScreen(new MainGameScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	private ArrayList<Location> createLocations()
	{
		ArrayList<Location> locations = new ArrayList<Location>(17); // TODO: change 17?
		Texture blank = new Texture(
				Gdx.files.internal(skin + "/tiles/white_90x90.png"));
		
		// Northeast location
		int x = WORLD_WIDTH - LOCATION_X_BUFFER_SIZE - 
				(Tile.TILE_SIZE * Location.TILES_PER_ROW);
		int y = WORLD_HEIGHT - LOCATION_Y_BUFFER_SIZE - 
				(Tile.TILE_SIZE * Location.TILES_PER_COL);
		Location northeast = new Location(LocationID.NORTHEAST, "NW Location", 
				x, y, blank);
		locations.add(northeast);
		
		return locations;
	}
	
	/* Load Units from XML data files */
	private void loadUnits()
	{
		XMLHandler reader = new XMLHandler(this);
		
		// Create Units based on default data
		units = reader.readUnitMappings();
		
		// Read all the Units in the active skin
		units = reader.applySkin(units, skin);
		
		// Create a Unit for testing and place it in a Location
		Unit testUnit = units.get("SOLDIER_1");
		Location testLoc = locations.get(0);
		Tile t = testLoc.tiles[0];
		t.setE(testUnit);
	}
	
	
	public void dispose()
	{
		batch.dispose();
		// TODO: Dispose textures
	}
}
