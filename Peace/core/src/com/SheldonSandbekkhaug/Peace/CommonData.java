package com.SheldonSandbekkhaug.Peace;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

/* The common data used as the game model/game data */
public class CommonData {
	public String skin;
	
	public ArrayList<Location> locations;
	public HashMap<String, Unit> units; // All Units in the game
	public HashMap<String, Structure> structures; //All Structures in the game
	
	// PeaceEntities available to be added to the Market
	public HashMap<String, PeaceEntity> availableForMarket;
	private ArrayList<Tile> market;
	public ArrayList<Player> players; // Indexed by playerID
	private int activePlayerID; // playerID for whose turn it is
	
	/*The maximum number of players Peace can support at this time, 
	  including the neutral player */
	public static final int MAX_PLAYERS = 5;
	
	// Maximum amount of human players that Peace can support at this time
	public static final int MAX_USERS = MAX_PLAYERS - 1;
	
	private GameStateType gameStateType = GameStateType.PRE_GAME;
	private int turnsLeft; // Turns remaining before the game ends
	private int playerAboutToWin = -1; // ID of player about to win
	
	// Default number of turns in a game
	private static final int DEFAULT_TURNS_PER_PLAYER = 15;
	
	/*
	 * Create the commonData object.
	 * If renderData is true, initialize data needed for rendering.
	 * Else, only initialize model data.
	 */
	public CommonData(boolean renderData)
	{
		skin = "default_1.0";
		players = new ArrayList<Player>();
		activePlayerID = 1;
		gameStateType = GameStateType.PRE_GAME;
		
		availableForMarket = new HashMap<String, PeaceEntity>();
		
		Tile.resetLastTileID();
		createLocations(renderData);
		loadEntities(renderData);
		initializeMarket();
		
		defaultTestSetup(); // TODO: remove?
	}
	
	/* Called when the game actually starts. */
	public void startGame()
	{
		gameStateType = GameStateType.RUNNING;
		turnsLeft = DEFAULT_TURNS_PER_PLAYER * (players.size() - 1);
	}
	
	/*
	 * Create the game Locations.
	 * If renderData is true, initialize data needed for rendering.
	 * Else, only initialize model data.
	 */
	private void createLocations(boolean renderData)
	{
		locations = new ArrayList<Location>(17); // TODO: change 17?
		
		Texture blank = null;
		if (renderData == true)
			blank = new Texture(
				Gdx.files.internal(skin + "/tiles/white_90x90.png"));
		
		for (LocationID id : LocationID.values()) // TODO: create more locations
		{
			Location l = new Location(id);
			l.setImg(blank);
			locations.add(l);
		}
	}
	
	/* Read all PeaceEntities from XML files.
	 * If renderData is true, initialize data needed for rendering.
	 * Else, ignore data needed for rendering. */
	private void loadEntities(boolean renderData)
	{
		XMLHandler reader = new XMLHandler(this);
		
		// Create default Units, then apply the active skin
		units = reader.readUnitMappings(renderData);
		reader.applyUnitSkin(units, skin);
		
		// Add Units to the Market availability HashMap
		for (String id : units.keySet())
		{
			if (units.get(id).isForMarket())
				availableForMarket.put(id, units.get(id));
		}
		
		// Create default Structures, then apply the active skin
		structures = reader.readStructureMappings(renderData);
		reader.applyStructureSkin(structures, skin);
		
		// Add Structures to the Market availability HashMap
		for (String id : structures.keySet())
		{
			if (structures.get(id).isForMarket())
			{
				availableForMarket.put(id, structures.get(id));
			}
		}
	}
	
	/* Put units in the game for testing purposes. */
	public void defaultTestSetup()
	{
		// TODO: Remove this block. Used for testing new Units in dev.
		/*Unit tg = units.get("BANDIT_1").clone();
		tg.setOwner(1);
		Location l = locations.get(1);
		Tile tile = l.getTiles()[Location.N];
		tile.setE(tg);
		*/
		
		// TODO: remove test Units
		// Create Units for testing and place them in a Location
		for (int i = 0; i < 5; i++)
		{
			Unit testUnit = units.get("SOLDIER_0").clone();
			testUnit.owner = 0;
			Location testLoc = locations.get(i);
			Tile t = testLoc.tiles[Location.CENTER];
			t.setE(testUnit);
		}
	}
	
	/* Initialize the market */
	private void initializeMarket()
	{
		int MARKET_SIZE = 5;
		market = new ArrayList<Tile>(MARKET_SIZE);
		for (int i = 0; i < MARKET_SIZE; i++)
		{
			market.add(new Tile());
			market.get(i).setMarketTile(true);
		}
	}
	
	/* 
	 * Return true if the market is ready for use.
	 * Else return false.
	 */
	public boolean isMarketInitialized()
	{
		if (market != null)
			return true;
		return false;
	}
	
	/* Return the size of the market */
	public int getMarketSize()
	{
		return market.size();
	}
	
	public Tile getMarketTile(int index)
	{
		return market.get(index);
	}
	
	/*
	 * Return the Tile that has an Entity with the specified entityID in the 
	 * Market. Return null if no Tile was found.
	 */
	public Tile getTileFromMarket(String entityID)
	{
		for (int i = 0; i < market.size(); i++)
		{
			if (market.get(i).getE().getID().equals(entityID))
			{
				return market.get(i);
			}
		}
		return null;
	}
	
	/*
	 * Add PeaceEntity e to a Tile in the market that has a null PeaceEntity.
	 */
	public boolean addToMarket(PeaceEntity e)
	{
		for (Tile t : market)
		{
			if (t.getE() == null)
			{
				t.setE(e.clone());
				t.getE().setOwner(Player.NEUTRAL);
				return true;
			}
		}
		
		// Throw an error if we can't add to the market
		try
		{
			throw new Exception();
		}
		catch(Exception ex)
		{
			System.out.println("Error: Cannot add " + e.getName() + 
					" to the market.");
			
			System.out.println("Market contents:");
			for (int i = 0; i < market.size(); i++)
			{
				System.out.println(i + ": " + e.getID());
			}
			
			ex.printStackTrace();
			System.exit(1);
		}
		
		// Java requires this even though it's impossible to get to this point
		return false;
	}
	
	
	/*
	 * Convenience method for removeFromMarket(int index)
	 */
	public void removeFromMarket(PeaceEntity e)
	{	
		for (int i = 0; i < market.size(); i++)
		{
			if (market.get(i).getE() == e)
			{
				removeFromMarket(i);
				return;
			}
		}
	}
	
	/* 
	 * Remove PeaceEntity e from the Market, shifting indexes to fill 
	 * the gap. The highest indexed Tile in market will end up with a null
	 * PeaceEntity.
	 */
	public PeaceEntity removeFromMarket(int index)
	{
		PeaceEntity e = market.get(index).getE();
		market.get(index).setE(null);
		for (int i = index; i < market.size(); i++)
		{
			if (i + 1 < market.size())
			{
				// Shift all PeaceEntities down
				market.get(i).setE(market.get(i + 1).getE());
				market.get(i + 1).setE(null);
			}
		}
		
		return e;
	}
	
	/*
	 * Return the Tile corresponding to the given tile ID.
	 * Return null if that Tile does not exist.
	 */
	public Tile getTile(int tileID)
	{
		// Check all Locations
		for (Location loc : locations)
		{
			for (Tile t : loc.getTiles())
			{
				if (t.getTileID() == tileID)
					return t;
			}
		}
		
		// Check the market
		for (Tile t : market)
		{
			if (t.getTileID() == tileID)
				return t;
		}
		
		return null;
	}
	
	/* Move the Entity at srcTileID to destTileID.
	 * Return true if successful, false otherwise.
	 */
	public boolean moveEntity(int srcTileID, int destTileID)
	{
		Tile dest = getTile(destTileID);
		if (dest == null || dest.getE() != null)
			return false;
		
		Tile src = getTile(srcTileID);
		if (src == null || src.getE() == null)
			return false;
		
		dest.setE(src.getE());
		src.setE(null);
		
		// Moving cost 1 action
		PeaceEntity e = dest.getE();
		e.setCurrActions(e.getCurrActions() - 1);
		
		// Update the player's Entities HashMap
		Player owner = players.get(e.getOwner());
		owner.getEntities().remove(srcTileID);
		owner.getEntities().put(destTileID, e);
		
		return true;
	}
	
	/* Return true if both Tiles are in the same Location, false otherwise. */
	public boolean sameLocation(Tile a, Tile b)
	{
		boolean foundA = false;
		boolean foundB = false;
		for (Location loc : locations)
		{
			for (Tile t : loc.getTiles())
			{
				if (t == a)
					foundA = true;
				else if (t == b)
					foundB = true;
			}
			
			if (foundA && foundB)
				return true;
			else if (foundA || foundB)
				return false;
		}
		
		return false;
	}
	
	/* Destroy the Entity at tileID */
	public void destroyEntity(int tileID)
	{
		Tile t = getTile(tileID);
		
		// Remove this Entity from its owner's array
		Player owner = players.get(t.getE().getOwner());
		owner.getEntities().remove(tileID);
		
		// Remove the Entity from the game world
		t.setE(null);
	}
	
	public Player getActivePlayer()
	{
		return players.get(activePlayerID);
	}
	
	public int getActivePlayerID()
	{
		return activePlayerID;
	}
	
	/* Change whose turn it is. */
	public void nextTurn()
	{
		activePlayerID++;
		
		if (activePlayerID >= players.size())
			activePlayerID = 1;
		
		turnsLeft--;
	}
	
	public int getTurnsLeft()
	{
		return turnsLeft;
	}
	
	/* Checks if any Player has won and returns the playerID of the winner.
	 * Returns -1 if no Player has won.
	 * 
	 * The win condition: A Player controls an Entity on the center Tile in
	 * three out of the five major Locations at the end of two of their
	 * consecutive turns.
	 */
	public int checkNormalVictoryCondition()
	{
		int[] centersControlled = new int[players.size()];

		// Check which players controls each Location
		for (int i = 0; i < locations.size(); i++)
		{
			Tile victoryTile = locations.get(i).getTiles()[Location.CENTER];
			if (victoryTile.getE() != null)
			{
				centersControlled[victoryTile.getE().getOwner()]++;
			}
		}
		
		for (int i = 0; i < centersControlled.length; i++)
		{
			// NEUTRAL cannot win the game
			if (i != Player.NEUTRAL && centersControlled[i] >= 3)
			{
				if (playerAboutToWin == i && activePlayerID == i)
					return i;
				
				playerAboutToWin = i;
				return -1;
			}
		}
	
		playerAboutToWin = -1;
		return -1;
	}
	
	/* Get the number of victory centers controlled by the given player ID. */
	public int getNumCentersControlled(int playerID)
	{
		int sum = 0;
		
		// Check if playerID controls each Location
		for (int i = 0; i < locations.size(); i++)
		{
			Tile t = locations.get(i).getTiles()[Location.CENTER];
			if (t.getE() != null && t.getE().getOwner() == playerID)
			{
				sum++;
			}
		}
		
		return sum;
	}
	
	/* Count the number of Entities with the given attribute owned by
	 * Players.
	 */
	public int countAttributes(Attribute a)
	{
		int sum = 0;
		for (Player p : players)
		{
			for (PeaceEntity e : p.getEntities().values())
			{
				if (e.hasAttribute(a))
					sum++;
			}
		}
		
		return sum;
	}
	
	/* Return an Array with the tileIDs of all Entities that have Attribute a.
	 * If pid is not -1, only add Entities that have the same playerID as pid.
	 */
	public Array<Integer> tileIDsWithAttribute(Attribute a, int pid)
	{
		Array<Integer> tileIDs = new Array<Integer>(8);
		
		for (Location loc : locations)
		{
			for (Tile t : loc.getTiles())
			{
				if (t.getE() != null && t.getE().hasAttribute(a))
				{
					if (pid == -1 || t.getE().getOwner() == pid)
						tileIDs.add(t.getTileID());
				}
			}
		}
		
		return tileIDs;
	}
	
	/* Return the playerID of the winner, calculated under Time Victory.
	 * Under these conditions, the winner is the player who has the most
	 * money plus the value of all his/her PeaceEntities.
	 * 
	 * Currently, the lower playerID breaks ties.
	 * TODO: Different way for breaking ties?
	 */
	public int getTimeVictor()
	{
		int scores[] = new int[players.size()];
		for (int i = 1; i < scores.length; i++)
		{
			Player p = players.get(i);
			
			// Get base money
			scores[i] = p.getMoney();
			
			// Add cost of all controlled Entities
			for (Integer key : p.getEntities().keySet())
			{
				scores[i] += p.getEntities().get(key).getCost();
			}
		}
		
		// Get the index of the highest score. That player ID is the winner.
		int maxIndex = 1; // Index of the highest-scoring player
		for (int i = 1; i < scores.length; i++)
		{
			if (scores[i] > scores[maxIndex])
			{
				maxIndex = i;
			}
		}
		
		return maxIndex;
	}
	
	public int getPlayerAboutToWin()
	{
		return playerAboutToWin;
	}
	
	public void setPlayerAboutToWin(int pid)
	{
		playerAboutToWin = pid;
	}

	public GameStateType getGameStateType() {
		return gameStateType;
	}

	public void setGameStateType(GameStateType gameStateType) {
		this.gameStateType = gameStateType;
	}
}