package com.SheldonSandbekkhaug.Peace;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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
	public static final int MARKET_HEIGHT = (int)(WORLD_HEIGHT * (8.0 / 10.0));
	public static final int MARKET_X_POS = WINDOW_WIDTH - X_BUFFER - MARKET_WIDTH;
	public static final int MARKET_Y_POS = Y_BUFFER;
	public static Texture marketBackground;
	
    public MainGameScreen(final Peace gam) {
        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setLocationPositions(game.commonData.locations);
        batch = new SpriteBatch();
        
        // Create texture for the Market background
        marketBackground = new Texture(
			Gdx.files.internal(game.commonData.skin +
			"/misc/market_background.png"));
    }
	
	@Override
	public void render(float delta) {
		handleMouseInput();
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
		batch.draw(marketBackground, MARKET_X_POS, MARKET_Y_POS);

		if (game.commonData.market != null)
		{
			int x = MARKET_X_POS + MARKET_WIDTH / 2;
			for (int i = 0; i < game.commonData.market.size(); i++)
			{
				PeaceEntity e = game.commonData.market.get(i);
				int y = MARKET_Y_POS + 
						(i * (MARKET_HEIGHT / game.commonData.market.size()));
				batch.draw(e.getImg(), x, y);
			}
		}
		
		batch.end();
	}
	
	/* Creates events generated by the mouse */
	private void handleMouseInput()
	{
		Vector3 mousePos3D = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		
		// Translates from screen coordinates to game coordinates
		camera.unproject(mousePos3D);
		
		Vector2 mousePos2D = new Vector2(mousePos3D.x, mousePos3D.y);
		
		// Check if the mouse is hovering over any PeaceEntities in Locations
		for (Location loc : game.commonData.locations)
		{
			if (loc.rect != null && loc.rect.contains(mousePos2D))
			{
				for (Tile t : loc.getTiles())
				{
					if (t.rect.contains(mousePos2D))
					{
						if (t.getE() != null)
							showEntityData(t.getE(), t.rect.x, t.rect.y);
					}
				}
			}
		}
		
		// TODO: Check if the mouse is hovering over the market
		
		
		if (Gdx.input.isTouched())
		{
			// TODO: Mouse controls
		}
	}
	
	/* 
	 * Display a small box containing information about the given Entity
	 */
	private void showEntityData(PeaceEntity e, float x, float y)
	{
		// TODO
		System.out.println(e.getName());
	}
	
	/* Modifies locations in-place to have the correct positions for this
	 * this screen.
	 */
	private void setLocationPositions(ArrayList<Location> locations)
	{
		int locationWidth = 3 * Tile.TILE_SIZE;
		for (Location loc : locations)
		{
			switch(loc.id)
			{
				case NORTHEAST:
					// Northeast location
					int x = WORLD_WIDTH - LOCATION_X_BUFFER_SIZE - 
						MARKET_WIDTH - 
						(Tile.TILE_SIZE * Location.TILES_PER_ROW);
					int y = WORLD_HEIGHT - LOCATION_Y_BUFFER_SIZE - 
						(Tile.TILE_SIZE * Location.TILES_PER_COL);
					loc.rect = new Rectangle(x, y,
							locationWidth, locationWidth);
					break;
					// TODO: other cases
				default:
					break;
			}
			
			// Set Tile Rectangles
			for (int i = 0; i < loc.getTiles().length; i++)
			{
				loc.getTiles()[i].rect = new Rectangle(
					loc.rect.x + Location.indexToXOffset(i),
					loc.rect.y + Location.indexToYOffset(i),
					Tile.TILE_SIZE, Tile.TILE_SIZE);
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		// Resize camera's projection
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		// TODO: fix this
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
