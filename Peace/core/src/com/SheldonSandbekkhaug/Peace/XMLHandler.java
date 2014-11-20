package com.SheldonSandbekkhaug.Peace;

import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class XMLHandler {
	CommonData commonData;
	XmlReader reader;
	
	public XMLHandler(CommonData cd)
	{
		commonData = cd;
		reader = new XmlReader();
	}
	
	/* Read an XML file and create an Element from its data	 */
	public Element read(String filename)
	{
		FileHandle fh = null;
		fh = Gdx.files.internal(filename);
		Element e = null;
		try {
			e = reader.parse(fh);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//printElement(e);
		return e;
	}
	
	/* Print out relevant data in the element, recursively */
	public void printElement(Element e)
	{
		ObjectMap<String, String> attributes = e.getAttributes();
		
		// Example: <mass>1</mass>
		System.out.println(e.getName() + ": " + e.getText());
		
		// Example: <item id="ABC">
		if (attributes != null)
		{
			// Print this element's attributes
			for (String key : attributes.keys())
			{
				System.out.println("(" + key + ", " + attributes.get(key) + ")");
			}
		}
		
		// Get the children
		for (int i = 0; i < e.getChildCount(); i++)
		{
			printElement(e.getChild(i));
		}
	}

	/* Read Unit mappings from an XML file .
	 * Unit mappings are XML objects that describe units properties,
	 * such as strength and hit points.
	 * 
	 * If renderData is false, read data used for rendering.
	 * If renderData is true, ignore data used for rendering.
	 */
	public HashMap<String, Unit> readUnitMappings(boolean renderData)
	{
		String mappings_filepath = "mappings/unit_mappings.xml";
		Element unitCatalog = read(mappings_filepath);
		
		HashMap<String, Unit> units = new HashMap<String, Unit>();
		
		if (unitCatalog.getName().equals("catalog"))
		{
			// Iterate through all Units in the catalog
			for (int i = 0; i < unitCatalog.getChildCount(); i++)
			{
				Element unitElement = unitCatalog.getChild(i);
				Unit u = new Unit();
				u.setOwner(Player.NEUTRAL);
				
				// Get all properties for this Unit
				for (int j = 0; j < unitElement.getChildCount(); j++)
				{
					Element property = unitElement.getChild(j);
					
					if (property.getName().equals("cost"))
					{
						u.setCost(Integer.parseInt(property.getText()));
					}
					else if (property.getName().equals("id"))
					{
						u.setID(property.getText());
					}
					else if (property.getName().equals("name"))
					{
						u.setName(property.getText());
					}
					else if (property.getName().equals("strength"))
					{
						u.setStrength(Integer.parseInt(property.getText()));
					}
					else if (property.getName().equals("hp"))
					{
						u.setMaxHP(Integer.parseInt(property.getText()));
						u.setCurrHP(u.getMaxHP());
					}
					else if (property.getName().equals("img"))
					{
						// Don't need to load renderData on server
						if (renderData == true)
						{
							// Load the unit's image
							Texture t = new Texture(
								Gdx.files.internal(
									commonData.skin + 
									"/unit_pictures/" + 
									property.getText()));
							u.setImg(t);
						}
					}
					else
					{
						// ERROR
						System.out.println("Unidentified property in " + 
							mappings_filepath + ": " + property.getName());
						System.exit(1);
					}
				
				units.put(u.id, u);
				}
			}
		}
		
		return units;
	}
	
	/*
	 * Reads information for a skin and modifies the units accordingly.
	 * Skins are like unit mappings, but they set the appearance (
	 * (names, images, and sounds) of units.
	 */
	public HashMap<String, Unit> applySkin(HashMap<String, Unit> units, String skin)
	{		
		String mappings_filepath = skin + "/data/units.xml";
		Element skinCatalog = read(mappings_filepath);
		
		if (skinCatalog.getName().equals("catalog"))
		{
			// Iterate through all Unit skins in the catalog
			for (int i = 0; i < skinCatalog.getChildCount(); i++)
			{
				Element unitElement = skinCatalog.getChild(i);
				
				Unit u = units.get(unitElement.getChildByName("id").getText());
				
				// Get all skinned properties for this Unit
				for (int j = 0; j < unitElement.getChildCount(); j++)
				{
					Element property = unitElement.getChild(j);
					
					if (property.getName().equals("cost"))
					{
						u.setCost(Integer.parseInt(property.getText()));
					}
					else if (property.getName().equals("id"))
					{
						// Do nothing
					}
					else if (property.getName().equals("name"))
					{
						u.setName(property.getText());
					}
					else
					{
						// ERROR
						System.out.println("Unidentified property in " + 
							mappings_filepath + ": " + property.getName());
						System.exit(1);
					}
				}
			}
		}
		
		return units;
	}
}
