package com.SheldonSandbekkhaug.Peace;

public class Player {
	int playerID;
	static int lastPlayerID;
	String name;
	int money;
	int vp; // Victory points
	
	public Player()
	{
		super();
		playerID = lastPlayerID++;
		lastPlayerID = playerID;
		money = 1000;
	}

	public Player(String name)
	{
		this();
		this.name = name;
	}
	
	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
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
}
