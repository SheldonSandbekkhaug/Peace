package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.Game;

/* Class for client-side game logic */
public class Peace extends Game {
	CommonData commonData;
	private PeaceNetworkClient network;
	int playerID; // The user's playerID
	
	@Override
	public void create () {
		//this.setScreen(new MainGameScreen(this)); // Jump directly to game
		this.setScreen(new LobbyScreen(this)); // Lobby Screen first
	}
	
	/* Start a new game.
	 * ipAddr is the IP address of the server to connect to.
	 */
	public void setUpNewGame()
	{
		commonData = new CommonData(true);
	}
	
	/* Connect to a game server */
	public void connectToServer(String ipAddr)
	{
		System.out.println("Connecting to server...");
		int PORT = 27960;
		network = new PeaceNetworkClient();
		network.connect(5000, ipAddr, PORT);
		
		// Join a game on the server
		String playerName = "Primo";
		PacketMessage pm = new PacketMessage(playerName); // Player name
		pm.type = EventType.JOIN;
		network.sendToServer(pm, -1);
	}
	
	/* Disconnect from the game server that we are currently connected to. */
	public void disconnectFromServer()
	{
		this.setScreen(new LobbyScreen(this));
		
		// Leave the server
		PacketMessage leaveMessage = new PacketMessage();
		leaveMessage.type = EventType.LEAVE;
		network.sendToServer(leaveMessage, playerID);
	}
	
	/* Return true if the client is connected to the server,
	 * false otherwise.
	 */
	public boolean isConnected()
	{
		if (network != null)
			return true;
		return false;
	}
	
	/* Process game events */
	public void processEvents()
	{
		// Get events from the network
		PacketMessage event = network.events.poll();
		while (event != null)
		{
			processNetworkEvent(event);
			event = network.events.poll();
		}
	}
	
	public void processNetworkEvent(PacketMessage pm)
	{
		PeaceEntity e = null;
		switch(pm.type)
		{
		case JOIN: // Successfully added to the server's game
			// Now we know our player ID
			playerID = pm.playerID;
			System.out.println("Received playerID: " + playerID); // TODO: remove
			break;
		case SETUP_DONE: // Server is done setting up
			// Change screens to the Main Game Screen
			if (this.getScreen() instanceof LobbyScreen)
			{
				LobbyScreen ls = (LobbyScreen) this.getScreen();
				this.setScreen(new MainGameScreen(this));
				ls.dispose();
	        }
			commonData.startGame();
			break;
		case NEXT_TURN: // End turn and go to next Player
			commonData.nextTurn();
			break;
		case WINNER: // A Player won the game
			commonData.setGameStateType(GameStateType.POST_GAME);
			
			// Stop the game on the server
			PacketMessage stopMessage = new PacketMessage();
			stopMessage.type = EventType.STOP;
			network.sendToServer(stopMessage, playerID);
			break;
		case TO_MARKET: // Add an Entity to the Market
			e = commonData.availableForMarket.get(pm.message);
			// TODO: generalize for other Entity types
			if (pm.srcTileID != -1)
			{
				Tile t = commonData.getTile(pm.srcTileID);
				t.setE(null);
			}
			commonData.addToMarket(e);
			break;
		case FROM_MARKET: // A Player bought something
			Tile src = commonData.getTile(pm.srcTileID);
			src.getE().setOwner(pm.playerID);
			Player buyer = commonData.players.get(pm.playerID);
			buyer.getEntities().put(pm.targetTileID, src.getE());
			
			commonData.moveEntity(pm.srcTileID, pm.targetTileID);
			break;
		case ADD_PLAYER: // Add a Player to the game
			Player p = new Player(pm.message, pm.playerID);
			commonData.players.add(p);
			break;
		case PLAYER_UPDATE: // Update a Player property
			if (pm.message.equals("money"))
			{
				commonData.players.get(pm.playerID).setMoney(pm.number);
			}
			break;
		case MOVE: // Move a PeaceEntity
			commonData.moveEntity(pm.srcTileID, pm.targetTileID);
			break;
		case UPDATE_ENTITY: // Change an Entity's property
			processUpdateEntity(pm);
			break;
		case UPDATE_ENTITY_ATTRIBUTE: // Add or remove an Attribute
			processUpdateEntityAttribute(pm);
			break;
		case REMOVE_ENTITY: // Destroy a PeaceEntity from the world
			commonData.destroyEntity(pm.srcTileID);
			break;
		default:
			break;
		}
	}
	
	/* Request the server to start the game. */
	public void requestStartGame()
	{
		// Start the game with the specified skin
		PacketMessage pm = new PacketMessage(commonData.skin);
		pm.type = EventType.START;
		network.sendToServer(pm, playerID);
		
		System.out.println("Client asked to start the game");
	}
	
	/*
	 * Buy Entity e from the market for Player p. Tell the server the
	 * Entity will appear at the specified TileID.
	 * 
	 * This method should only be called by clients.
	 */
	public void buyEntity(PeaceEntity e, Player p, int tileID)
	{
		PacketMessage pm = new PacketMessage();
		pm.type = EventType.FROM_MARKET;
		pm.playerID = p.getPlayerID();
		pm.targetTileID = tileID;
		pm.message = e.getID();
		
		network.sendToServer(pm, playerID);
	}
	
	/* Ask the server to move the PeaceEntity at srcTileID to destTileID */
	public void requestMoveEntity(int srcTileID, int destTileID)
	{
		PacketMessage pm = new PacketMessage();
		pm.type = EventType.MOVE;
		pm.srcTileID = srcTileID;
		pm.targetTileID = destTileID;
		
		network.sendToServer(pm, playerID);
	}
	
	/* Ask the server to use the PeaceEntity at srcTileID to attack the
	 * PeaceEntity at destTileID.
	 */
	public void requestAttackEntity(int srcTileID, int destTileID)
	{
		PacketMessage pm = new PacketMessage();
		pm.type = EventType.ATTACK;
		pm.srcTileID = srcTileID;
		pm.targetTileID = destTileID;
		
		network.sendToServer(pm, playerID);
	}
	
	/* Process a PacketMessage of type UPDATE_ENTITY. */
	private void processUpdateEntity(PacketMessage pm)
	{
		Tile t = commonData.getTile(pm.srcTileID);
		PeaceEntity e = t.getE();
		
		if (pm.message.equals("currHP"))
			e.setCurrHP(pm.number);
		else if (pm.message.equals("currActions"))
			e.setCurrActions(pm.number);
		else if (pm.message.equals("income"))
		{
			Structure s = (Structure)e;
			s.setIncome(pm.number);
		}
	}
	
	/* Add or remove an Attribute from an Entity.
	 * EventType: UPDATE_ENTITY_ATTRIBUTE
	 */
	private void processUpdateEntityAttribute(PacketMessage pm)
	{
		PeaceEntity e = commonData.getTile(pm.srcTileID).getE();
		Attribute a = Attribute.fromOrdinal(pm.targetTileID);
		if (pm.number == PacketMessage.ADD)
		{
			e.addAttribute(a);
		}
		else if (pm.number == PacketMessage.REMOVE)
		{
			e.getAttributes().removeValue(a, true);
		}
		else
		{
			System.out.println("Bad pm.number in UPDATE_ENTITY_ATTRIBUTE. Ignoring.");
		}
	}
	
	/* Tell the server this Player is done with this turn. */
	public void requestEndTurn()
	{
		// User can only end their own turn
		if (commonData.getActivePlayerID() == playerID)
		{
			PacketMessage pm = new PacketMessage();
			pm.type = EventType.NEXT_TURN;
			network.sendToServer(pm, playerID);
		}
	}

	@Override
	public void render () {
		super.render();
	}
	
	public void dispose()
	{
		// TODO: Dispose textures
		PacketMessage pm = new PacketMessage();
		pm.type = EventType.LEAVE;
		network.sendToServer(pm, playerID);
	}
}
