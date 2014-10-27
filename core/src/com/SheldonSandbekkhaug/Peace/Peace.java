package com.SheldonSandbekkhaug.Peace;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Peace extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	String skin;
	
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
	
	private ArrayList<Location> locations = new ArrayList<Location>();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		skin = "default_1.0";
		locations = createLocations();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		
		// Draw locations
		for (Location l : locations)
		{
			l.draw(batch);
		}
		
		batch.end();
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
	
	public void dispose()
	{
		batch.dispose();
		// TODO: Dispose textures
	}
}
