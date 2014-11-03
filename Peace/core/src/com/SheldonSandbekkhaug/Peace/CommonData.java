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
	public ArrayList<PeaceEntity> market;
	
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
		
		int MARKET_SIZE = 5;
		market = new ArrayList<PeaceEntity>(MARKET_SIZE);
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
}
