package com.SheldonSandbekkhaug.Peace;

public enum Attribute {
	ARMORED,
	DEFENDER,
	IMMOBILIZE_ON_ATTACK,
	IMMOBILIZED,
	FIRST_STRIKE,
	FORGE,
	HEAL_ON_ENTER,
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
		case IMMOBILIZE_ON_ATTACK:
			return "Immobilize on attack";
		case IMMOBILIZED:
			return "Immobilized";
		case FIRST_STRIKE:
			return "First Strike";
		case FORGE:
			return "FORGE";
		case HEAL_ON_ENTER:
			return "Heal on enter";
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
	
	/* Don't rely on this. Use this for PacketMessage communication only.
	 * Converts an Attribute to an integer.
	 */
	public int getOrdinal()
	{
		return this.ordinal();
	}
	
	/* Don't rely on this. Use this for PacketMessage communication only.
	 * Converts an integer to an Attribute.
	 */
	public static Attribute fromOrdinal(int i)
	{
		return values()[i];
	}
}
