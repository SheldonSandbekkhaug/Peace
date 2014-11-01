package com.SheldonSandbekkhaug.Peace;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/* Class for client-side game logic */
public class Peace extends Game {
	public String skin;
	
	public ArrayList<Location> locations;
	public HashMap<String, Unit> units;
	public ArrayList<PeaceEntity> market;
	
	private PeaceNetworkClient network;
	
	@Override
	public void create () {
		skin = "default_1.0"; // TODO: set dynamically
		locations = createLocations();
		loadUnits();
		
		this.setScreen(new MainGameScreen(this));
	}
	
	/* Connect to a game server 
	 * TODO: move to PeaceGameClient?
	 * */
	public boolean connectToServer(String ipAddr)
	{	
		// TODO: Dynamically set network information
		int PORT = 27960;
		network = new PeaceNetworkClient();
		network.connect(5000, ipAddr, PORT);
		network.sendMessage("join");
		
		// TODO: return false if join was unsuccessful
		return true;
	}

	@Override
	public void render () {
		super.render();
	}
	
	public ArrayList<Location> createLocations()
	{
		ArrayList<Location> locations = new ArrayList<Location>(17); // TODO: change 17?
		Texture blank = new Texture(
				Gdx.files.internal(skin + "/tiles/white_90x90.png"));
		for (LocationID id : LocationID.values()) // TODO: create more locations
		{
			Location l = new Location(id);
			l.setImg(blank);
			locations.add(l);
		}
		
		return locations;
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
	
	public void dispose()
	{
		// TODO: Dispose textures
	}
}
