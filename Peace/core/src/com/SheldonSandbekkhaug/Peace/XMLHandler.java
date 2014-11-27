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
				readUnitProperties(unitElement, u, renderData, 
					mappings_filepath);
				
				units.put(u.id, u);
			}
		}
		
		return units;
	}
	
	/* Modifies Unit u in-place to have the properties in the given Element. 
	 * If renderData is false, ignore data needed for rendering.
	 * mappings_filepath is the filepath to the file that is being read. This
	 * is used for error reporting.
	 */
	private void readUnitProperties(Element unitElement, Unit u, 
			boolean renderData, String mappings_filepath)
	{
		// Get all properties for this Unit
		for (int j = 0; j < unitElement.getChildCount(); j++)
		{
			Element property = unitElement.getChild(j);
			
			if (!readEntityProperty(property, u, renderData))
			{
				// Properties specific to Unit objects
				if (property.getName().equals("strength"))
				{
					u.setStrength(Integer.parseInt(property.getText()));
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
	
	/* Read Structure mappings from an XML file .
	 * Structure mappings are XML objects that describe units properties.
	 * 
	 * If renderData is false, read data used for rendering.
	 * If renderData is true, ignore data used for rendering.
	 */
	public HashMap<String, Structure> readStructureMappings(boolean renderData)
	{
		String mappings_filepath = "mappings/structure_mappings.xml";
		Element structureCatalog = read(mappings_filepath);
		
		HashMap<String, Structure> structures =
				new HashMap<String, Structure>();
		
		if (structureCatalog.getName().equals("catalog"))
		{
			// Iterate through all Units in the catalog
			for (int i = 0; i < structureCatalog.getChildCount(); i++)
			{
				Element sElement = structureCatalog.getChild(i);
				Structure s = new Structure();
				s.setOwner(Player.NEUTRAL);
				readStructureProperties(sElement, s, renderData, 
					mappings_filepath);
				
				structures.put(s.id, s);
			}
		}
		
		return structures;
	}
	
	/* Modifies Structure s in-place to have the properties in the given
	 * Element.
	 * If renderData is false, ignore data needed for rendering.
	 * mappings_filepath is the filepath to the file that is being read. This
	 * is used for error reporting.
	 */
	private void readStructureProperties(Element sElement, Structure s, 
			boolean renderData, String mappings_filepath)
	{
		// Get all properties for this Structure
		for (int j = 0; j < sElement.getChildCount(); j++)
		{
			Element property = sElement.getChild(j);
			
			if (!readEntityProperty(property, s, renderData))
			{
				// Properties specific to Structure objects
				if (property.getName().equals("income"))
				{
					s.setIncome(Integer.parseInt(property.getText()));
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
	
	/* Read the property of this element into PeaceEntity e, if possible.
	 * Return true if a property was used, false otherwise.
	 * If renderData is false, ignore data needed for rendering.
	 * */
	private boolean readEntityProperty(Element property, PeaceEntity e,
			boolean renderData)
	{
		if (property.getName().equals("cost"))
		{
			e.setCost(Integer.parseInt(property.getText()));
			return true;
		}
		else if (property.getName().equals("id"))
		{
			e.setID(property.getText());
			return true;
		}
		else if (property.getName().equals("name"))
		{
			e.setName(property.getText());
			return true;
		}
		else if (property.getName().equals("hp"))
		{
			e.setMaxHP(Integer.parseInt(property.getText()));
			e.setCurrHP(e.getMaxHP());
			return true;
		}
		else if (property.getName().equals("img"))
		{
			// Don't need to load renderData on server
			if (renderData == true)
			{
				String entityClass = "unit";
				if (e instanceof Structure)
					entityClass = "structure";
				
				String filepath = commonData.skin + "/"
					+ entityClass + "_pictures/" + property.getText();
				
				// Load the unit's image
				Texture t = new Texture(Gdx.files.internal(filepath));
				e.setImg(t);
			}
			return true;
		}
		else if (property.getName().equals("attribute"))
		{
			e.addAttribute(Attribute.valueOf(property.getText()));
			return true;
		}
		
		return false;
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
				readUnitProperties(unitElement, u, false, mappings_filepath);
			}
		}
		
		return units;
	}
}
