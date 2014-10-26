package com.SheldonSandbekkhaug.Peace;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class XMLHandler {
	XmlReader reader;
	
	public XMLHandler()
	{
		reader = new XmlReader();
	}
	
	/* Read an XML file and create an Element from its data */
	public Element read(String filename)
	{
		//FileHandle fh = new FileHandle(filename);
		FileHandle fh = Gdx.files.internal(filename);
		Element e = null;
		try {
			e = reader.parse(fh);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		printElement(e);
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
}
