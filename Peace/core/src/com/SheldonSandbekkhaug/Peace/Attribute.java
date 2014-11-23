package com.SheldonSandbekkhaug.Peace;

public enum Attribute {
	NORMAL,
	RAIDER;
	
	/* Return a nicely-formatted String for this attribute. */
	public String toUIString()
	{
		switch(this)
		{
			case NORMAL:
				return "Normal";
			case RAIDER:
				return "Raider";
			default:
				return "Default Attribute";
		}
	}
}
