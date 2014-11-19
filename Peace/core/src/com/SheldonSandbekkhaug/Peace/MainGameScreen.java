package com.SheldonSandbekkhaug.Peace;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MainGameScreen implements Screen {
	Peace game;
	OrthographicCamera camera;
	SpriteBatch batch;
	BitmapFont font;
	
	PeaceEntity selectedEntity = null; // Entity selected by user cursor
	Tile selectedEntityTile = null; // Holds/held the selected Entity
	
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
	public static final int MARKET_HEIGHT = (int)(WORLD_HEIGHT * (6.0 / 10.0));
	public static final int MARKET_X_POS = WINDOW_WIDTH - X_BUFFER - MARKET_WIDTH;
	public static final int MARKET_Y_POS = (WINDOW_HEIGHT - MARKET_HEIGHT) / 2;
	public static Texture marketBackground; // Market background texture
	
	// Size of Entity information panels
	public static final int ENTITY_INFO_WIDTH = WORLD_WIDTH / 8;
	public static final int ENTITY_INFO_HEIGHT = WORLD_HEIGHT / 5;
	
	// Space between text and panel border
	public static final int ENTITY_INFO_X_BUFFER = ENTITY_INFO_WIDTH / 20;
	public static final int ENTITY_INFO_Y_BUFFER = ENTITY_INFO_HEIGHT / 20;
	Texture infoBackground; // Entity information panel background
	Texture infoBackgroundBorder;
	public static final int ENTITY_INFO_BORDER_THICKNESS = 2;
	
	// Bottom Information Panel
	public static final int BOTTOM_INFO_PANEL_WIDTH = 0;
	public static final int BOTTOM_INFO_PANEL_HEIGHT = 0;
	
    public MainGameScreen(final Peace gam) {
        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setLocationPositions(game.commonData.locations);
        batch = new SpriteBatch();
        font = new BitmapFont(); // Defaults to Arial
        
        // Create textures
        marketBackground = new Texture(Gdx.files.internal(
			game.commonData.skin + "/misc/market_background.png"));
        
        infoBackground = new Texture(Gdx.files.internal(
            	game.commonData.skin + "/misc/info_background.png"));  
        infoBackgroundBorder = new Texture(Gdx.files.internal(
            	game.commonData.skin + "/misc/info_background_border.png"));  
    }
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		
		game.processEvents();
		
		camera.update();
		
		// Draw locations
		for (Location l : game.commonData.locations)
		{
			l.draw(batch);
		}
		
		// Draw the market
		batch.draw(marketBackground,
				MARKET_X_POS, MARKET_Y_POS,
				MARKET_WIDTH, MARKET_HEIGHT);

		if (game.commonData.isMarketInitialized())
		{
			for (int i = 0; i < game.commonData.getMarketSize(); i++)
			{
				Tile marketTile = game.commonData.getMarketTile(i);
				if (marketTile.getE() != null)
					batch.draw(marketTile.getE().getImg(),
						marketTile.rect.x, marketTile.rect.y);
			}
		}
		
		// Draw some basic player information
		font.setColor(Color.BLACK);
		int playerMoneyAmt =
			game.commonData.players.get(game.playerID).getMoney();
		font.draw(batch, "Funds: " + playerMoneyAmt, 100, 100);
		
		handleMouseInput();
		
		batch.end();
	}
	
	/* Creates events generated by the mouse */
	private void handleMouseInput()
	{
		Vector3 cursorPos3D = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		
		// Translates from screen coordinates to game coordinates
		camera.unproject(cursorPos3D);
		
		Vector2 cursorPos2D = new Vector2(cursorPos3D.x, cursorPos3D.y);
		
		Tile cursorOnTile = cursorOnTile(cursorPos2D);
		tryToPickUpEntity(cursorOnTile);
		
		if (cursorOnTile != null && cursorOnTile.getE() != null)
		{
			// Show Entity data if cursor is not clicked
			showEntityData(cursorOnTile.getE(),
				cursorOnTile.rect.x, cursorOnTile.rect.y);
		}
		
		// Check if the mouse is hovering over the market
		for (int i = 0; i < game.commonData.getMarketSize(); i++)
		{
			Tile marketTile = game.commonData.getMarketTile(i);
			
			boolean pickedUpEntity = false;
			
			// Possibly attempt to buy the Entity
			int playerMoneyAmt =
					game.commonData.players.get(game.playerID).getMoney();
			if (marketTile.getE() != null &&
					marketTile.rect.contains(cursorPos2D) &&
					playerMoneyAmt >= marketTile.getE().getCost())
			{
				pickedUpEntity = tryToPickUpEntity(marketTile);
			}
			
			// Show Entity information panel if mouse is not clicked
			if (marketTile.rect.contains(cursorPos2D) && 
					marketTile.getE() != null && 
					!pickedUpEntity)
			{
				showEntityData(marketTile.getE(), 
					marketTile.rect.x, marketTile.rect.y);
			}
		}
		
		// If the mouse button is held down
		if (Gdx.input.isTouched())
		{
			// Draw the selected Entity centered on the cursor
			if (selectedEntity != null)
			{
				// Going from screen coordinates to game coordinates
				Vector3 selectedEntityPos = new Vector3(
					Gdx.input.getX() - Tile.TILE_SIZE / 2,
					Gdx.input.getY() + Tile.TILE_SIZE / 2,
					0);
				camera.unproject(selectedEntityPos);
				
				batch.draw(selectedEntity.getImg(), 
					selectedEntityPos.x,
					selectedEntityPos.y);
			}
		}
		else if (!Gdx.input.isTouched() && selectedEntity != null)
		{
			if (cursorOnTile == null) // Not a valid target Tile
			{
				// Return the Entity to its original location
				selectedEntityTile.setE(selectedEntity);
			}
			else
			{
				releaseSelectedEntityOverTile(cursorOnTile);
			}
			selectedEntity = null;
			selectedEntityTile = null;
		}
	}
	
	/* Try to release the selected PeaceEntity into the specified Tile. */
	private void releaseSelectedEntityOverTile(Tile cursorOnTile)
	{
		// Examine the target PeaceEntity
		PeaceEntity targetEntity = cursorOnTile.getE();
		
		// Coming from market
		if (selectedEntityTile.isMarketTile())
		{
			if (targetEntity == null)
			{
				// Put the selected PeaceEntity back in the market
				selectedEntityTile.setE(selectedEntity);
				
				// Server will send an event to move the Entity
				game.buyEntity(selectedEntity,
					game.commonData.players.get(game.playerID),
					cursorOnTile.getTileID());
			}
			else
			{
				// Don't remove the PeaceEntity from the Market
				selectedEntityTile.setE(selectedEntity);
			}
		}
		else
		{
			// Move to empty tile or attack?
			if (targetEntity == null) // Normal move
			{
				System.out.println("Normal move"); // TODO: remove
				// Undo state changes and allow server to handle logic
				selectedEntityTile.setE(selectedEntity);
				game.requestMoveEntity(selectedEntityTile.getTileID(),
					cursorOnTile.getTileID());
			}
			else if (targetEntity.getOwner() == selectedEntity.getOwner())
			{
				// Invalid move, return to original location
				System.out.println("Invalid move"); // TODO: remove
				selectedEntityTile.setE(selectedEntity);
			}
			else if (targetEntity.getOwner() != selectedEntity.getOwner() &&
					selectedEntity instanceof Unit)
			{
				System.out.println("Attack move"); // TODO: remove
				// Attack enemy
				Unit u = (Unit)selectedEntity;
				
				// Enemy must be in same Location
				boolean inSameLocation = game.commonData.sameLocation(
					selectedEntityTile, cursorOnTile);
				
				// Attacker must have strength > 0
				if (u.getStrength() > 0 && inSameLocation)
				{
					System.out.println("Request attack"); // TODO: remove
					game.requestAttackEntity(selectedEntityTile.getTileID(),
						cursorOnTile.getTileID());
					
					// Undo changes, let server handle logic
					selectedEntityTile.setE(selectedEntity);
				}
			}
		}
	}
	
	/*
	 * Return the Tile the cursor is on, if the Tile is a part of a Location.
	 * If the cursor is on no Tile, return null.
	 */
	private Tile cursorOnTile(Vector2 cursorPos)
	{
		// Check if the mouse is hovering over any PeaceEntities in Locations
		for (Location loc : game.commonData.locations)
		{
			if (loc.rect != null && loc.rect.contains(cursorPos))
			{
				for (Tile t : loc.getTiles())
				{
					if (t.rect.contains(cursorPos))
					{
						return t;
					}
				}
			}
		}
		return null;
	}
	
	/* 
	 * Try to use the Entity from tile as the selected Entity.
	 * Return true if successful, false otherwise.
	 * 
	 * To be picked up (selected), an Entity be owned by the player trying to
	 * pick it up or the Entity is in the market.
	 */
	public boolean tryToPickUpEntity(Tile tile)
	{
		if (tile == null || tile.getE() == null)
			return false;
		
		boolean isOwned = game.playerID == tile.getE().getOwner();
		if (tile != null && tile.getE() != null &&
				(isOwned || tile.isMarketTile()))
		{
			if (Gdx.input.isTouched() && selectedEntity == null)
			{
				// User pressed the PeaceEntity
				selectedEntity = tile.getE();
				selectedEntityTile = tile;
				
				tile.setE(null);
				
				// TODO: send message to server?
				
				return true;
			}
		}
		return false;
	}
	
	/* 
	 * Display a small box containing information about the given Entity.
	 * x and y are the bottom-right corner of the Entity.
	 */
	private void showEntityData(PeaceEntity e, float x, float y)
	{
		batch.draw(infoBackgroundBorder,
				x - ENTITY_INFO_WIDTH - ENTITY_INFO_BORDER_THICKNESS,
				y - ENTITY_INFO_HEIGHT + Tile.TILE_SIZE - ENTITY_INFO_BORDER_THICKNESS, 
				ENTITY_INFO_WIDTH + ENTITY_INFO_BORDER_THICKNESS * 2,
				ENTITY_INFO_HEIGHT + ENTITY_INFO_BORDER_THICKNESS * 2);
		batch.draw(infoBackground,
				x - ENTITY_INFO_WIDTH, y - ENTITY_INFO_HEIGHT + Tile.TILE_SIZE, 
				ENTITY_INFO_WIDTH, ENTITY_INFO_HEIGHT);
		font.setColor(Color.BLACK);
		
		float textX = x - ENTITY_INFO_WIDTH + ENTITY_INFO_X_BUFFER;
		float textHeight = 16;
		
		// Draw the Entity's name
		font.draw(batch, e.getName(),
			textX, y + Tile.TILE_SIZE - ENTITY_INFO_Y_BUFFER);
		
		// If it's a unit, draw the strength and HP
		if (e instanceof Unit)
		{
			Unit u = (Unit)e;
			font.draw(batch, "Str: " + u.getStrength(),
				textX, y + Tile.TILE_SIZE - ENTITY_INFO_Y_BUFFER - (2 * textHeight));
			font.draw(batch, "HP: " + u.getCurrHP() + "/" + u.getMaxHP(),
				textX, y + Tile.TILE_SIZE - ENTITY_INFO_Y_BUFFER - (3 * textHeight));
			font.draw(batch, "Cost: " + u.getCost(),
				textX, y + Tile.TILE_SIZE - ENTITY_INFO_Y_BUFFER - (4 * textHeight));
			
		}
		// TODO: Handle other types of PeaceEntities
	}
	
	/* Modifies locations in-place to have the correct positions for this
	 * this screen.
	 * 
	 * Also sets the positions of Market Tiles.
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
		
		// Set market tile positions
		int space_between_market_tiles = (MARKET_HEIGHT - 
				(Tile.TILE_SIZE * game.commonData.getMarketSize())) /
				game.commonData.getMarketSize();
		for (int i = 0; i < game.commonData.getMarketSize(); i++)
		{
			Tile t = game.commonData.getMarketTile(i);
			
			int x = MARKET_X_POS + MARKET_WIDTH / 2 - Tile.TILE_SIZE / 2;
			int y = MARKET_Y_POS + (i * Tile.TILE_SIZE) + 
					(i * space_between_market_tiles) + 
					(space_between_market_tiles / 2);
			
			t.rect = new Rectangle(x, y, Tile.TILE_SIZE, Tile.TILE_SIZE);
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
