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
	
	// Constants for positioning elements on the screen
	public static final int WORLD_WIDTH = 800;
	public static final int WORLD_HEIGHT = 600;
	
	// Space between a Location and other objects
	public static final int LOCATION_X_BUFFER_SIZE = 40;
	public static final int LOCATION_Y_BUFFER_SIZE = 40;
	
	private ArrayList<Location> locations = new ArrayList<Location>();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
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
		
		// Northeast location
		int x = WORLD_WIDTH - LOCATION_X_BUFFER_SIZE - 
				(Tile.TILE_SIZE * Location.TILES_PER_ROW);
		int y = WORLD_HEIGHT - LOCATION_Y_BUFFER_SIZE - 
				(Tile.TILE_SIZE * Location.TILES_PER_COL);
		Location northeast = new Location(LocationID.NORTHEAST, "NW Location", x, y);
		locations.add(northeast);
		
		return locations;
	}
}
