package com.SheldonSandbekkhaug.Peace;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
	public static final int WINDOW_HEIGHT = 794;
	
	// Distance between window edge and game world
	public static final int X_BUFFER = 20;
	public static final int Y_BUFFER = 100;
	
	public static final int WORLD_WIDTH = WINDOW_WIDTH - X_BUFFER * 2;
	public static final int WORLD_HEIGHT = WINDOW_HEIGHT - Y_BUFFER * 2;
	
	// Space between a Location and other objects
	public static final int LOCATION_X_BUFFER_SIZE = 160;
	public static final int LOCATION_Y_BUFFER_SIZE = 20;
	
	private static Texture translucent_green_rect;
	
	// Images to be displayed when a player wins or loses the game
	private static Texture victory;
	private static Texture defeat;
	
	// Market size and position
	public static final int MARKET_WIDTH = WORLD_WIDTH / 10;
	public static final int MARKET_HEIGHT = (int)(WORLD_HEIGHT * (6.0 / 10.0));
	public static final int MARKET_X_POS = WINDOW_WIDTH - X_BUFFER - MARKET_WIDTH;
	public static final int MARKET_Y_POS = (WINDOW_HEIGHT - MARKET_HEIGHT) / 2;
	private static Texture marketBackground; // Market background texture
	private static Texture coin;
	private static BitmapFont coinFont;
	
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
        
        // Set up the camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        camera = new OrthographicCamera(WINDOW_WIDTH, WINDOW_WIDTH * (h / w));
        camera.position.set(camera.viewportWidth / 2f, 
        		camera.viewportHeight / 2f, 0);
        camera.update();
        
        setLocationPositions(game.commonData.locations);
        batch = new SpriteBatch();
        font = new BitmapFont(); // Defaults to Arial
        font.setColor(Color.BLACK);
        
        translucent_green_rect = new Texture(Gdx.files.internal(
    			game.commonData.skin + "/misc/translucent_green_rect.png"));
        
        victory = new Texture(Gdx.files.internal(
    			game.commonData.skin + "/misc/victory.png"));
        defeat = new Texture(Gdx.files.internal(
    			game.commonData.skin + "/misc/defeat.png"));
        
        // Font for things drawn by Locations
        BitmapFont locationFont = new BitmapFont();
        locationFont.setColor(Color.BLACK);
        locationFont.scale(0.05f);
        Location.font = locationFont;
        
        // Font for coin digits
        coinFont = new BitmapFont();
        coinFont.setColor(Color.BLACK);
        coinFont.scale(0.05f);
        
        // Create textures
        marketBackground = new Texture(Gdx.files.internal(
			game.commonData.skin + "/misc/market_background.png"));
        
        infoBackground = new Texture(Gdx.files.internal(
            	game.commonData.skin + "/misc/info_background.png"));
        infoBackgroundBorder = new Texture(Gdx.files.internal(
            	game.commonData.skin + "/misc/info_background_border.png"));
        
    	coin = new Texture(Gdx.files.internal(
    			game.commonData.skin + "/misc/coin.png"));
    	
    	// Player Banner Textures
    	Tile.playerBanners = new Texture[CommonData.MAX_PLAYERS];
    	for (int i = 1; i < CommonData.MAX_PLAYERS; i++)
    	{
    		Tile.playerBanners[i] = new Texture(Gdx.files.internal(
    				game.commonData.skin + "/misc/banner_" + i + ".png"));
    	}
    	
    	// Action point textures
    	Tile.hasActionsIcon = new Texture(Gdx.files.internal(
				game.commonData.skin + "/misc/has_actions_icon.png"));
    	Tile.noActionsIcon = new Texture(Gdx.files.internal(
				game.commonData.skin + "/misc/no_actions_icon.png"));
    }
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		camera.update();
		
		batch.begin();
		
		game.processEvents();
		
		// Draw locations
		for (Location l : game.commonData.locations)
		{
			l.draw(batch, game.playerID);
		}
		
		drawMarket();
		
		if (game.commonData.getGameStateType() == GameStateType.RUNNING)
		{
			drawGameInformation();
			
			handleMouseInput();
			handleKeyInput();
		}
		
		if (game.commonData.getGameStateType() == GameStateType.POST_GAME)
		{
			drawPostGameImages();
			
			if (Gdx.input.isTouched())
			{
				batch.end();
				game.setScreen(new LobbyScreen(game));
				this.dispose();
				game.disconnectFromServer();
				return;
			}
		}
		
		batch.end();
	}
	
	/* Draw the Market and its Entities. */
	private void drawMarket()
	{
		batch.draw(marketBackground,
				MARKET_X_POS, MARKET_Y_POS,
				MARKET_WIDTH, MARKET_HEIGHT);

		if (game.commonData.isMarketInitialized())
		{
			for (int i = 0; i < game.commonData.getMarketSize(); i++)
			{
				Tile marketTile = game.commonData.getMarketTile(i);
				
				// Highlight Entities that the active player can afford
				if (marketTile.getE() != null && 
						game.commonData.getActivePlayerID() == game.playerID)
				{
					Player currentPlayer = 
							game.commonData.players.get(game.playerID);
					int playerMoney = currentPlayer.getMoney();
					if (playerMoney >= marketTile.getE().getCost())
					{
						batch.draw(translucent_green_rect,
								marketTile.rect.x, marketTile.rect.y);
					}
				}
				
				// Draw the usual Tile things
				marketTile.draw(batch, Location.font,
					marketTile.rect.x, marketTile.rect.y, game.playerID);
				
				// Draw coin icon and Entity cost
				if (marketTile.getE() != null)
				{		
					float coinX = marketTile.rect.x - Tile.TILE_SIZE / 5;
					float coinY = marketTile.rect.y + (Tile.TILE_SIZE / 10) * 8;
					
					batch.draw(coin, coinX, coinY);
					
					String coinString = "" + marketTile.getE().getCost();
					TextBounds labelBounds = coinFont.getBounds(coinString);
					
					// Align label to coin image
					float labelX = coinX +
							(coin.getWidth() - labelBounds.width) / 2;
					float labelY = coinY + labelBounds.height +
							(coin.getHeight() - labelBounds.height) / 2;
					
					coinFont.draw(batch, coinString, labelX, labelY);
				}
			}
		}
	}
	
	/* Draws basic player information and game state information. */
	private void drawGameInformation()
	{
		int playerMoneyAmt = 
			game.commonData.players.get(game.playerID).getMoney();
		
		font.draw(batch, "Funds: " + playerMoneyAmt, 100, 100);
		font.draw(batch,
				"Player " + game.commonData.getActivePlayerID() + "'s turn",
				100, 80);
		font.draw(batch,
				"Turns left: " + game.commonData.getTurnsLeft(),
				100, 60);
	}
	
	/* Draw images that are displayed during the POST_GAME state. */
	private void drawPostGameImages()
	{
		if (game.commonData.getPlayerAboutToWin() == game.playerID)
		{
			// Victory picture
			float x = (WORLD_WIDTH / 2) - (victory.getWidth() / 2);
			float y = (WORLD_HEIGHT / 2);
			batch.draw(victory, x, y);
		}
		else
		{
			// Defeat picture
			float x = (WORLD_WIDTH / 2) - (defeat.getWidth() / 2);
			float y = (WORLD_HEIGHT / 2);
			batch.draw(defeat, x, y);
		}
		
		String returnString = "Click anywhere to return to the main menu";
		TextBounds returnBounds = font.getBounds(returnString);
		float returnX = (WORLD_WIDTH / 2) - (returnBounds.width / 2);
		font.draw(batch, returnString, returnX, (WORLD_HEIGHT / 5) * 3);
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
				/* Draw an indicator showing the selected Entity can be 
				   released here safely */
				if (cursorOnTile != null && cursorOnTile.getE() == null)
					batch.draw(translucent_green_rect,
							cursorOnTile.rect.x, cursorOnTile.rect.y, 40, 40);
				// TODO: draw a red indicator over hostile units?
				
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
		// Don't move to the Tile that the Entity came from
		if (cursorOnTile == selectedEntityTile)
		{
			selectedEntityTile.setE(selectedEntity);
			return;
		}
		
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
				// Undo state changes and allow server to handle logic
				selectedEntityTile.setE(selectedEntity);
				
				// Movement cost an action
				if (selectedEntity.getCurrActions() > 0)
					game.requestMoveEntity(selectedEntityTile.getTileID(),
						cursorOnTile.getTileID());
			}
			else if (targetEntity.getOwner() == selectedEntity.getOwner())
			{
				// Invalid move, return to original location
				selectedEntityTile.setE(selectedEntity);
			}
			else if (targetEntity.getOwner() != selectedEntity.getOwner() &&
					selectedEntity instanceof Unit)
			{
				// Attack enemy
				Unit u = (Unit)selectedEntity;
				
				// Enemy must be in same Location
				boolean inSameLocation = game.commonData.sameLocation(
					selectedEntityTile, cursorOnTile);
				
				// Attacker must have strength > 0 and an action available
				if (u.getStrength() > 0 && inSameLocation &&
						u.getCurrActions() > 0)
				{
					game.requestAttackEntity(selectedEntityTile.getTileID(),
						cursorOnTile.getTileID());
					
					// Undo changes, let server handle logic
					selectedEntityTile.setE(selectedEntity);
				}
				else
				{
					// Put the attacker back to its original Tile
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
		// Must be user's turn to pick up an Entity
		if (game.commonData.getActivePlayerID() != game.playerID)
			return false;
		
		// Entity must not be null and not IMMOBILIZED
		if (tile == null || tile.getE() == null ||
				tile.getE().hasAttribute(Attribute.IMMOBILIZED))
			return false;
		
		boolean isOwned = game.playerID == tile.getE().getOwner();
		boolean isStructure = tile.getE() instanceof Structure;
		if ((isOwned && !isStructure) || tile.isMarketTile())
		{
			if (Gdx.input.isTouched() && selectedEntity == null)
			{
				// User pressed the PeaceEntity
				selectedEntity = tile.getE();
				selectedEntityTile = tile;
				
				tile.setE(null);
				
				return true;
			}
		}
		return false;
	}
	
	/* Handle key input. */
	private void handleKeyInput()
	{
		if(Gdx.input.isKeyJustPressed(Keys.ENTER))
		{
			// End this Player's turn.
			System.out.println("It is now Player " + 
					game.commonData.getActivePlayerID() + "'s turn. " +
					"You are Player " + game.playerID);
			game.requestEndTurn();
		}
		
		// TODO: change key mappings. Use mouse controls too.
		if (Gdx.input.isKeyPressed(Keys.A)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Keys.Q)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            camera.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            camera.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            camera.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            camera.translate(0, 3, 0);
        }
        
        // Don't show too little of the world or go out of bounds
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.zoom = MathUtils.clamp(camera.zoom, 0.1f,
        		WINDOW_WIDTH/camera.viewportWidth);
        camera.position.x = MathUtils.clamp(camera.position.x,
        		effectiveViewportWidth / 2f,
        		WINDOW_WIDTH - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y,
        		effectiveViewportHeight / 2f,
        		WINDOW_HEIGHT - effectiveViewportHeight / 2f);
	}
	
	/* 
	 * Display a small box containing information about the given Entity.
	 * x and y are the bottom-right corner of the Entity.
	 */
	private void showEntityData(PeaceEntity e, float x, float y)
	{
		x = x - 10; // Create gap between Tile and info panel
		
		TextBounds nameLabelBounds = font.getBounds(e.getName());
		float panelWidth = Math.max(ENTITY_INFO_WIDTH, nameLabelBounds.width);
		
		batch.draw(infoBackgroundBorder,
				x - panelWidth - ENTITY_INFO_BORDER_THICKNESS,
				y - ENTITY_INFO_HEIGHT + Tile.TILE_SIZE - ENTITY_INFO_BORDER_THICKNESS, 
				panelWidth + ENTITY_INFO_BORDER_THICKNESS * 2,
				ENTITY_INFO_HEIGHT + ENTITY_INFO_BORDER_THICKNESS * 2);
		batch.draw(infoBackground,
				x - panelWidth, y - ENTITY_INFO_HEIGHT + Tile.TILE_SIZE, 
				panelWidth, ENTITY_INFO_HEIGHT);
		font.setColor(Color.BLACK);
		
		float textX = x - panelWidth + ENTITY_INFO_X_BUFFER;
		float textHeight = 16;
		
		// Draw the Entity's name
		font.draw(batch, e.getName(),
			textX, y + Tile.TILE_SIZE - ENTITY_INFO_Y_BUFFER);
		
		int row = 2; // For positioning text
		
		// Draw the cost of the Entity
		font.draw(batch, "Cost: " + e.getCost(), textX,
				y + Tile.TILE_SIZE - ENTITY_INFO_Y_BUFFER - (row * textHeight));
		row++;
		
		// If it's a unit, draw the strength and HP
		if (e instanceof Unit)
		{
			// Nothing to do here yet
		}
		else if (e instanceof Structure) // Draw Structure-specific info
		{
			Structure s = (Structure)e;
			font.draw(batch, "Income: " + s.getIncome(), textX, 
				y + Tile.TILE_SIZE - ENTITY_INFO_Y_BUFFER - (row * textHeight));
			row++;
		}
		
		// Draw the owner
		if (e.getOwner() != 0)
		{
			font.draw(batch, "Owner: " + e.getOwner(), textX,
				y + Tile.TILE_SIZE - ENTITY_INFO_Y_BUFFER - (row * textHeight));
			row++;
		}
		
		// Draw the attributes
		if (e.getAttributes().size > 0)
		{
			for (Attribute a : e.getAttributes())
			{
				font.draw(batch, a.toUIString(), textX,
					y + Tile.TILE_SIZE - ENTITY_INFO_Y_BUFFER - (row * textHeight));
				row++;
			}
		}
	}
	
	/* Modifies locations in-place to have the correct positions for this
	 * this screen by setting the bottom-right corner of the locations.
	 * 
	 * Also sets the positions of Market Tiles.
	 * TODO: Polish Location world positions
	 */
	private void setLocationPositions(ArrayList<Location> locations)
	{
		// 3 Tiles in each dimension, plus buffers between Tiles and edges
		float locationWidth = (3 * Tile.TILE_SIZE) + 
				(Location.INTERNAL_BUFFER * 4);
		
		// Bottom-right corner of Locations at these relative positions
		int eastX = WORLD_WIDTH - LOCATION_X_BUFFER_SIZE - 
				MARKET_WIDTH - 
				(Tile.TILE_SIZE * Location.TILES_PER_ROW);
		int westX = X_BUFFER + LOCATION_X_BUFFER_SIZE;
		int northY = WORLD_HEIGHT - LOCATION_Y_BUFFER_SIZE - 
				(Tile.TILE_SIZE * Location.TILES_PER_COL);
		int southY =  Y_BUFFER + LOCATION_Y_BUFFER_SIZE;
		
		for (Location loc : locations)
		{
			int x = 0;
			int y = 0;
			switch(loc.id)
			{
				case NORTHEAST:
					x = eastX;
					y = northY;
					break;
				case NORTHWEST:
					x = westX;
					y = northY;
					break;
				case SOUTHWEST:
					x = westX;
					y = southY;
					break;
				case SOUTHEAST:
					x = eastX;
					y = southY;
					break;
				case CENTER:
					x = (westX + eastX) / 2;
					y = (northY + southY) / 2;
					break;
				default:
					break;
			}
			
			loc.rect = new Rectangle(x, y,
					locationWidth, locationWidth);
			
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
		// Changes perceived zoom when resized
		// Don't change these, otherwise game might crash when moving Entities
		camera.viewportWidth = WORLD_WIDTH;
		camera.viewportHeight = WORLD_WIDTH * height/width;
        camera.update();
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
		//game.dispose(); // TODO: ?
		batch.dispose();
		font.dispose();
		coinFont.dispose();
		Location.font.dispose();
	}

}
