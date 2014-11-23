package com.SheldonSandbekkhaug.Peace;

public enum Attribute {
	ARMORED,
	NORMAL,
	RAIDER;
	
	/* Return a nicely-formatted String for this attribute. */
	public String toUIString()
	{
		switch(this)
		{
		case ARMORED:
			return "Armored";
		case NORMAL:
			return "Normal";
		case RAIDER:
			return "Raider";
		default:
			return "Default Attribute";
		}
	}
}
