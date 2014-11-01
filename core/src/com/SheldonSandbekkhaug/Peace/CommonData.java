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
	
	public CommonData()
	{
		skin = "default_1.0";
		createLocations();
		loadUnits();
	}
	
	private void createLocations()
	{
		locations = new ArrayList<Location>(17); // TODO: change 17?
		Texture blank = new Texture(
				Gdx.files.internal(skin + "/tiles/white_90x90.png"));
		for (LocationID id : LocationID.values()) // TODO: create more locations
		{
			Location l = new Location(id);
			l.setImg(blank);
			locations.add(l);
		}
	}
	
	/* Load Units from XML data files */
	public void loadUnits()
	{
		XMLHandler reader = new XMLHandler(this);
		
		// Create Units based on default data
		units = reader.readUnitMappings();
		
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
