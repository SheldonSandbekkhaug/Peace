package com.SheldonSandbekkhaug.Peace;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/* The common data used as the game model/game data */
public class CommonData {
	public String skin;
	
	public ArrayList<Location> locations;
	public HashMap<String, Unit> units;
	private ArrayList<Tile> market;
	
	/*
	 * Create the commonData object.
	 * If renderData is true, initialize data needed for rendering.
	 * Else, only initialize model data.
	 */
	public CommonData(boolean renderData)
	{
		skin = "default_1.0";
		createLocations(renderData);
		loadUnits(renderData);
		initializeMarket();
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
	
	/* 
	 * Load Units from XML data files 
	 * If renderData is true, initialize data needed for rendering.
	 * Else, only initialize model data.
	 */
	public void loadUnits(boolean renderData)
	{
		XMLHandler reader = new XMLHandler(this);
		
		// Create Units based on default data
		units = reader.readUnitMappings(renderData);
		
		// Read all the Units in the active skin
		units = reader.applySkin(units, skin);
		
		// Create Units for testing and place them in a Location
		for (int i = 1; i < 5; i++)
		{
			Unit testUnit = units.get("SOLDIER_" + i);
			Location testLoc = locations.get(0);
			Tile t = testLoc.tiles[i];
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
	 * Add PeaceEntity e to a Tile in the market that has a null PeaceEntity.
	 */
	public boolean addToMarket(PeaceEntity e)
	{
		for (Tile t : market)
		{
			if (t.getE() == null)
			{
				t.setE(e);
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
			System.out.println("Error: cannot add " + e.getName() + 
					" to the market.");
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
}
