package com.SheldonSandbekkhaug.Peace;

import java.util.HashMap;

public class Player {
	private int playerID;
	String name;
	int money;
	int vp; // Victory points TODO: do we need this?
	private HashMap<Integer, PeaceEntity> entities; // <TileID, Entity>
	
	public static final int NEUTRAL = 0;
	
	public Player()
	{
		super();
		money = 3;
		entities = new HashMap<Integer, PeaceEntity>();
	}

	public Player(String name, int pid)
	{
		this();
		this.name = name;
		this.playerID = pid;
	}
	
	public int getPlayerID() {
		return playerID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getVp() {
		return vp;
	}

	public void setVp(int vp) {
		this.vp = vp;
	}

	public HashMap<Integer, PeaceEntity> getEntities() {
		return entities;
	}
}
