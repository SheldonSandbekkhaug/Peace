package com.SheldonSandbekkhaug.Peace;

public enum Attribute {
	ARMORED,
	DEFENDER,
	FIRST_STRIKE, FORGE,
	NORMAL,
	MINE,
	PIERCING,
	RAIDER;
	
	/* Return a nicely-formatted String for this attribute. */
	public String toUIString()
	{
		switch(this)
		{
		case ARMORED:
			return "Armored";
		case DEFENDER:
			return "Defender";
		case FIRST_STRIKE:
			return "First Strike";
		case MINE:
			return "Mine";
		case NORMAL:
			return "Normal";
		case PIERCING:
			return "Piercing";
		case RAIDER:
			return "Raider";
		default:
			return "Default Attribute";
		}
	}
}
