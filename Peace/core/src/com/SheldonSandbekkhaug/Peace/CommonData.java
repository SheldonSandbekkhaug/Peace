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
	
	public boolean running = false; // True if the game is running
	
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
		
		availableForMarket = new HashMap<String, PeaceEntity>();
		
		Tile.resetLastTileID();
		createLocations(renderData);
		loadEntities(renderData);
		initializeMarket();
		
		defaultTestSetup(); // TODO: remove?
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
	}
	
	/* Checks if any Player has won and returns the playerID of the winner.
	 * Returns -1 if no Player has won.
	 * 
	 * The win condition: A Player controls an Entity on the center Tile in
	 * three out of the five major Locations.
	 */
	public int checkVictoryCondition()
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
			if (i != Player.NEUTRAL && centersControlled[i] >= 3)
			{
				// NEUTRAL cannot win the game
				return i;
			}
		}
	
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
}
