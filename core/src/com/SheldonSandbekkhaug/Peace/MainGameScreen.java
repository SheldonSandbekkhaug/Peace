package com.SheldonSandbekkhaug.Peace;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainGameScreen implements Screen {
	Peace game;
	OrthographicCamera camera;
	SpriteBatch batch;
	
	// Constants for positioning elements on the screen
	public static final int WINDOW_WIDTH = 1000;
	public static final int WINDOW_HEIGHT = 800;
	
	// Distance between window edge and game world
	public static final int X_BUFFER = 20;
	public static final int Y_BUFFER = 80;
	
	public static final int WORLD_WIDTH = WINDOW_WIDTH - X_BUFFER * 2;
	public static final int WORLD_HEIGHT = WINDOW_HEIGHT - Y_BUFFER * 2;
	
	// Space between a Location and other objects
	public static final int LOCATION_X_BUFFER_SIZE = 40;
	public static final int LOCATION_Y_BUFFER_SIZE = 40;
	
	// Market size and position
	public static final int MARKET_WIDTH = WORLD_WIDTH / 10;
	public static final int MARKET_HEIGHT = WORLD_HEIGHT * (8 / 10);
	public static Texture marketBackground;
	
    public MainGameScreen(final Peace gam) {
        game = gam;
        setLocationPositions(game.commonData.locations);
        batch = new SpriteBatch();
        
        // Create texture for the Market background
        marketBackground = new Texture(
			Gdx.files.internal(game.commonData.skin +
			"/misc/market_background.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
    }
	
	@Override
	public void render(float delta) {
		game.processEvents();
		
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		batch.begin();
		
		// Draw locations
		for (Location l : game.commonData.locations)
		{
			l.draw(batch);
		}
		
		// Draw the market
		batch.draw(marketBackground,
				WINDOW_WIDTH - X_BUFFER - MARKET_WIDTH,
				Y_BUFFER
				);

		/* TODO: fill in
		for (PeaceEntity e : game.commonData.market)
		{
			
		}
		*/
		
		batch.end();
	}
	
	/* Modifies locations in-place to have the correct positions for this
	 * this screen.
	 */
	private void setLocationPositions(ArrayList<Location> locations)
	{
		for (Location loc : locations)
		{
			switch(loc.id)
			{
				case NORTHEAST:
					// Northeast location
					loc.x = WORLD_WIDTH - LOCATION_X_BUFFER_SIZE - 
						MARKET_WIDTH - 
						(Tile.TILE_SIZE * Location.TILES_PER_ROW);
					loc.y = WORLD_HEIGHT - LOCATION_Y_BUFFER_SIZE - 
						(Tile.TILE_SIZE * Location.TILES_PER_COL);
					break;
					// TODO: other cases
				default:
					break;
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		game.dispose();
	}

}
