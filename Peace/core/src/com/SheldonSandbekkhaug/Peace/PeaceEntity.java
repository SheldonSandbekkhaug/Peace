package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

/* A Unit or Structure */
public class PeaceEntity {
	private String id;
	String name;
	int cost;
	int currHP, maxHP; // Current HP, max HP
	int owner; // playerID, 0 by default
	Texture img;
	boolean forMarket; // True if this Entity can be bought at the Market
	Array<Attribute> attributes;
	// NOTE: must adjust clone() method after adding/removing properties
	
	public PeaceEntity()
	{
		forMarket = true;
		owner = Player.NEUTRAL;
		attributes = new Array<Attribute>(4);
	}
	
	public PeaceEntity clone()
	{
		PeaceEntity e = new PeaceEntity();
		e.cloneHelper(this);
		
		return e;
	}
	
	/* Clone all the attributes of src */
	public void cloneHelper(PeaceEntity src)
	{
		setID(src.id);
		setName(src.name);
		setCost(src.cost);
		setMaxHP(src.maxHP); // Must be set before currHP
		setCurrHP(src.currHP);
		setOwner(src.owner);
		setImg(src.img);
		
		for (Attribute a : src.getAttributes())
		{
			addAttribute(a);
		}
	}
	
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getCurrHP() {
		return currHP;
	}

	/* Set this PeaceEntity's HP to the new value.
	 * If the new value is greater than its max value, set the currHP to the
	 * max value.
	 */
	public void setCurrHP(int currHP) {
		this.currHP = currHP;
		
		// Can't go over the maximum
		if (this.currHP > this.maxHP)
			this.currHP = this.maxHP;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public Texture getImg() {
		return img;
	}

	public void setImg(Texture img) {
		this.img = img;
	}

	public Array<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Array<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	/* Add the given attribute to this PeaceEntity. */
	public void addAttribute(Attribute a)
	{
		attributes.add(a);
	}
	
	/* Return true if this PeaceEntity has this attribute, false otherwise. */
	public boolean hasAttribute(Attribute a)
	{
		return attributes.contains(a, false);
	}

	public boolean isForMarket() {
		return forMarket;
	}

	public void setForMarket(boolean forMarket) {
		this.forMarket = forMarket;
	}
}