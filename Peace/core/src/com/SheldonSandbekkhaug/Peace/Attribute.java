package com.SheldonSandbekkhaug.Peace;

public enum Attribute {
	NORMAL;
	
	/* Return a nicely-formatted String for this attribute. */
	public String toUIString()
	{
		switch(this)
		{
			case NORMAL:
				return "Normal";
			default:
				return "Default Attribute";
		}
	}
}
