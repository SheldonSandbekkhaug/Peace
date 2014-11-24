package com.SheldonSandbekkhaug.Peace;

public enum Attribute {
	ARMORED,
	FIRST_STRIKE,
	NORMAL,
	RAIDER;
	
	/* Return a nicely-formatted String for this attribute. */
	public String toUIString()
	{
		switch(this)
		{
		case ARMORED:
			return "Armored";
		case FIRST_STRIKE:
			return "First Strike";
		case NORMAL:
			return "Normal";
		case RAIDER:
			return "Raider";
		default:
			return "Default Attribute";
		}
	}
}
