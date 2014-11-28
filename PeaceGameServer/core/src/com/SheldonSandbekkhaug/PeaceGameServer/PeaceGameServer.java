package com.SheldonSandbekkhaug.PeaceGameServer;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.SheldonSandbekkhaug.Peace.Attribute;
import com.SheldonSandbekkhaug.Peace.CommonData;
import com.SheldonSandbekkhaug.Peace.EventType;
import com.SheldonSandbekkhaug.Peace.Location;
import com.SheldonSandbekkhaug.Peace.PacketMessage;
import com.SheldonSandbekkhaug.Peace.PeaceEntity;
import com.SheldonSandbekkhaug.Peace.Player;
import com.SheldonSandbekkhaug.Peace.Structure;
import com.SheldonSandbekkhaug.Peace.Tile;
import com.SheldonSandbekkhaug.Peace.Unit;

public class PeaceGameServer extends ApplicationAdapter {
	PeaceNetworkServer network;
	CommonData commonData;
	Queue<PacketMessage> events;
	
	// Player names that will join the next game. Index corresponds to playerID.
	ArrayList<String> lobby; 
	Random gen;
	
	// Describes PeaceEntities that initiate combat or react to combat
	private static final boolean INITIATOR = true;
	private static final boolean REACTOR = false;
	
	@Override
	public void create () {
		int PORT = 27960;
		network = new PeaceNetworkServer(PORT);
		lobby = new ArrayList<String>();
		clearLobby(lobby);
		
		gen = new Random();
	}
	
	/* Make the Lobby ready for a new set of users. Modifies in-place. */
	private void clearLobby(ArrayList<String> l)
	{
		l.clear();
		l.add("Neutral"); // Player 0 is not a user
	}

	public void render () {
		// No need to render on server side
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		processEvents();
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
		PacketMessage reply;
		switch(pm.type)
		{
		case JOIN: // Client attempts to join the game
			lobby.add(pm.message);
			// Send a reply for their success
			reply = new PacketMessage();
			reply.type = EventType.JOIN;
			int playerID = lobby.size() - 1;
			reply.playerID = playerID;
			network.sendToClient(reply, playerID);
			break;
		case START:
			out.println("Received request to start game");
			newGame(pm.message, lobby);
			break;
		case NEXT_TURN:
			int winnerID = commonData.checkVictoryCondition();
			if (winnerID != -1)
			{
				PacketMessage declareWinner = new PacketMessage();
				declareWinner.type = EventType.WINNER;
				declareWinner.playerID = winnerID;
				network.broadcastToPlayers(declareWinner);
			}
			else
			{
				handleNextTurn();
			}
			break;
		case LEAVE:
			network.disconnected(pm.playerID);
			lobby.remove(pm.message);
			commonData.players.remove(pm.playerID);
			break;
		case STOP:
			commonData = null;
			clearLobby(lobby);
			break;
		case FROM_MARKET: // A Player bought an Entity
			if (pm.playerID == commonData.getActivePlayerID())
				buyEntity(pm.playerID, pm.message, pm.targetTileID);
			break;
		case MOVE: // A Player moved an Entity
			if (pm.playerID == commonData.getActivePlayerID())
			{
				broadcastMoveEntity(pm.srcTileID, pm.targetTileID);
				commonData.moveEntity(pm.srcTileID, pm.targetTileID);
			}
			break;
		case ATTACK: // A Player ordered an Entity to attack
			if (pm.playerID == commonData.getActivePlayerID())
				attackEntity(pm.srcTileID, pm.targetTileID);
		default:
			break;
		}
	}
	
	/* Initialize a new game according to the given skin. */
	public void newGame(String skin, ArrayList<String> playerNames)
	{	
		Tile.resetLastTileID();
		commonData = new CommonData(false); // Creates Unit table and applies Skin
		
		// Add Players to the game
		int i = 0;
		for (String name : playerNames)
		{
			Player p = new Player(name, i);
			
			PacketMessage pm = new PacketMessage(name);
			pm.type = EventType.ADD_PLAYER;
			pm.playerID = i;
			network.broadcastToPlayers(pm);
			
			p.setMoney(p.getMoney() + i * 2);
			broadcastUpdatePlayerMoney(p.getMoney(), i);
			
			commonData.players.add(p);
			
			i++;
		}
		
		initializeMarket();
		
		commonData.running = true;
		
		PacketMessage setupDone = new PacketMessage();
		setupDone.type = EventType.SETUP_DONE;
		network.broadcastToPlayers(setupDone);
	}
	
	/*
	 *  Select n random Units from units and put them in the market ArrayList.
	 *  Note that this must be done on the server so that we can broadcast
	 *  market changes.
	 */
	private void initializeMarket()
	{
		int MARKET_SIZE = 5;

		for (int i = 0; i < MARKET_SIZE; i++)
		{
			String key = randomSelection(commonData.availableForMarket);
			PeaceEntity e = commonData.availableForMarket.get(key);
			commonData.addToMarket(e);
			commonData.availableForMarket.remove(key);
			
			// Create a TO_MARKET and broadcast it to the players
			PacketMessage event = new PacketMessage(e.getID());
			event.srcTileID = -1;
			event.type = EventType.TO_MARKET;
			network.broadcastToPlayers(event);
		}
	}
	
	/*
	 * Take the Entity off the market and add a new 
	 * Entity to the market
	 */
	public void buyEntity(int playerID, String entityID, int destTileID)
	{
		Player p = commonData.players.get(playerID);
		
		Tile t = commonData.getTileFromMarket(entityID);
		PeaceEntity e = t.getE();
		e.setOwner(playerID);
		p.getEntities().add(e);

		// Subtract the cost of the Entity from the Player's funds
		p.setMoney(p.getMoney() - e.getCost());
		
		// Tell all players that P spent money
		broadcastUpdatePlayerMoney(p.getMoney(), playerID);

		// Move the PeaceEntity from the market to its destination
		commonData.moveEntity(t.getTileID(), destTileID);
		
		// Update market. Have to create a "dummy" Entity to remove from Market
		PeaceEntity eClone = e.clone();
		commonData.removeFromMarket(eClone);
		
		// Tell all players which Entity was bought and its destination
		PacketMessage marketUpdate = new PacketMessage();
		marketUpdate.type = EventType.FROM_MARKET;
		marketUpdate.playerID = playerID;
		marketUpdate.srcTileID = t.getTileID();
		marketUpdate.targetTileID = destTileID;
		network.broadcastToPlayers(marketUpdate);
		
		// Update affected Entities
		triggerOnEnterEffects(destTileID);
		
		// Select a random PeaceEntity from the availability HashMap
		String newEntityKey = randomSelection(commonData.availableForMarket);
		PeaceEntity newEntity = 
			commonData.availableForMarket.get(newEntityKey);
		
		// Remove that Entity from the availability HashMap
		commonData.availableForMarket.remove(newEntity.getID());
		
		// Add the PeaceEntity to the Market
		commonData.addToMarket(newEntity);
		
		// Tell all clients which Entity was added
		PacketMessage marketAdd = new PacketMessage(newEntity.getID());
		marketAdd.type = EventType.TO_MARKET;
		marketAdd.srcTileID = t.getTileID();
		network.broadcastToPlayers(marketAdd);
	}
	
	/* Select a random key from HashMap */
	public String randomSelection(HashMap<String, PeaceEntity> h)
	{
		int indexToTake = gen.nextInt(commonData.availableForMarket.size());
		
		int i = 0;
		for (String id : h.keySet())
		{
			if (i == indexToTake)
			{
				return id;
			}
			i++;
		}
		
		// Should never get to this point
		try
		{
			throw new Exception();
		}
		catch(Exception e)
		{
			System.out.println("Could not select a random entity from the Market.");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Also should never get to this point
		return null;
	}
	
	/* Move the Entity at srcTileID to destTileID.
	 * Return true if successful, false otherwise.
	 */
	private boolean broadcastMoveEntity(int srcTileID, int destTileID)
	{
		if (commonData.moveEntity(srcTileID, destTileID))
		{
			PacketMessage reply = new PacketMessage();
			reply = new PacketMessage();
			reply.type = EventType.MOVE;
			reply.srcTileID = srcTileID;
			reply.targetTileID = destTileID;
			network.broadcastToPlayers(reply);
			return true;
		}
		else
		{
			System.out.println("Could not move PeaceEntity");
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	
	/* PeaceEntity at srcTileID attacks PeaceEntity at targetTileID. */
	private void attackEntity(int srcTileID, int targetTileID)
	{
		Unit attacker = (Unit)commonData.getTile(srcTileID).getE();
		PeaceEntity defender = commonData.getTile(targetTileID).getE();
		
		// Resolve first strike before normal combat
		boolean attackerFirstStrike = 
			attacker.hasAttribute(Attribute.FIRST_STRIKE);
		boolean defenderFirstStrike =
			defender.hasAttribute(Attribute.FIRST_STRIKE);
		
		if (attackerFirstStrike)
		{
			constDamageEntity(1, defender, true, REACTOR);
		}
		if (defenderFirstStrike && !attacker.hasAttribute(Attribute.RAIDER))
		{
			constDamageEntity(1, attacker, true, INITIATOR);
		}
		
		// If either of these Entities died, return
		if ((attackerFirstStrike || defenderFirstStrike) &&
			(checkAndHandleDeath(attacker, srcTileID) || 
			checkAndHandleDeath(defender, targetTileID)))
				return;
		
		// Subtract HP from the defender
		damageEntity(attacker, defender, true);
		
		// Subtract HP from the attacker if applicable
		if (defender instanceof Unit && !attacker.hasAttribute(Attribute.RAIDER))
		{
			Unit defenderUnit = (Unit)defender;
			
			damageEntity(defenderUnit, attacker, false);
			checkAndHandleDeath(attacker, srcTileID);
		}
		
		checkAndHandleDeath(defender, targetTileID);
	}
	
	/* A single step in combat sequence; e1 damages e2.
	 * If e1initiated is true, e1 is the initiator and e2 is the reactor.
	 * If e1initiated is false, e1 is the reactor and e1 is the initiator.
	 * (This is only relevant for attacker-only and defender-only abilities.)
	 */
	private void damageEntity(Unit e1, PeaceEntity e2, boolean e1initiated)
	{
		int dmg = e1.getStrength();
		
		PeaceEntity reactor = e2;
		if (!e1initiated)
		{
			reactor = e1;
		}
		
		// Entities with the DEFENDER attribute deal extra damage when attacked
		if (reactor.hasAttribute(Attribute.DEFENDER))
		{
			dmg += 1;
		}
		
		// Reduce damage taken by 1 if armored
		if (e2.hasAttribute(Attribute.ARMORED))
		{
			// PIERCING Entities deal extra damage to ARMORED targets
			if (e1.hasAttribute(Attribute.PIERCING))
				dmg += 1;
			dmg = (dmg <= 0) ? 0 : dmg - 1;
		}
		
		e2.setCurrHP(e2.getCurrHP() - dmg);
	}
	
	/* Reduce the target's current HP by the amount specified by damage. 
	 * If effects is true, take other attributes into account.
	 * If targetInitiated is INITIATOR, the given Entity initiated combat
	 * If targetInitiated is REACTOR, the given Entity is reacting to an attack
	 */
	private void constDamageEntity(int damage, PeaceEntity target,
			boolean effects, boolean targetInitiated)
	{
		Unit dummy = new Unit();
		dummy.setStrength(damage);
		if (effects == true)
		{
			damageEntity(dummy, target, targetInitiated);
		}
		else
		{
			target.setCurrHP(target.getCurrHP() - damage);
		}
	}
	
	/* Check if the given Entity has 0 HP remaining or less.
	 * If so, destroy it. If not, do nothing.
	 * tileID: The tileID that this Entity occupies.
	 * 
	 * Return false if e's current HP is > 0.
	 * Return true otherwise.
	 */
	private boolean checkAndHandleDeath(PeaceEntity e, int tileID)
	{
		if (e.getCurrHP() > 0)
		{
			// Attacker is still alive
			broadcastUpdateEntity(tileID, "currHP", 
				e.getCurrHP());
			return false;
		}
		else
		{
			// Attacker is dead, remove attacker
			triggerOnDeathEffects(tileID);
			broadcastRemoveEntity(tileID);
			commonData.destroyEntity(tileID);
			return true;
		}
	}
	
	/* Broadcast an update to a PeaceEntity property. */
	private void broadcastUpdateEntity(int tileID, String property,
			int newVal)
	{
		PacketMessage pm = new PacketMessage(property);
		pm.type = EventType.UPDATE_ENTITY;
		pm.srcTileID = tileID;
		pm.number = newVal;
		
		network.broadcastToPlayers(pm);
	}
	
	/* Broadcast that the Entity at tileID is removed. */
	private void broadcastRemoveEntity(int tileID)
	{
		PacketMessage removeEntity = new PacketMessage();
		removeEntity.type = EventType.REMOVE_ENTITY;
		removeEntity.srcTileID = tileID;
		network.broadcastToPlayers(removeEntity);
	}
	
	/* Broadcast a change in a Player's funds. */
	private void broadcastUpdatePlayerMoney(int newVal, int playerID)
	{
		PacketMessage moneyUpdate = new PacketMessage("money");
		moneyUpdate.type = EventType.PLAYER_UPDATE;
		moneyUpdate.playerID = playerID;
		moneyUpdate.number = newVal;
		network.broadcastToPlayers(moneyUpdate);
	}
	
	/* Update all Entities that are affected by the entrance of the Entity at 
	 * tileID into play.
	 */
	private void triggerOnEnterEffects(int tileID)
	{
		Tile t = commonData.getTile(tileID);
		PeaceEntity e = t.getE();
		
		for (Attribute a : e.getAttributes())
		{
			if (a == Attribute.FORGE)
			{
				Structure forge = (Structure)e;
				forge.setIncome(forge.getIncome() +
						commonData.countAttributes(Attribute.MINE));
				broadcastUpdateEntity(tileID, "income", forge.getIncome());
			}
			else if (a == Attribute.HEAL_ON_ENTER)
			{
				// Heal all friendly Units 1 HP
				Location loc = t.getLocation();
				System.out.println("loc: " + loc.getName()); // TODO: remove
				for (Tile tile : loc.getTiles())
				{
					PeaceEntity target = tile.getE();
					if (target instanceof Unit &&
							target.getOwner() == e.getOwner())
					{
						System.out.println("Healing " + e.getName()); // TODO: remove
						target.setCurrHP(target.getCurrHP() + 1);
						broadcastUpdateEntity(tile.getTileID(), "currHP",
								target.getCurrHP());
					}
				}
			}
			else if (a == Attribute.MINE)
			{
			// Forges gain +1 income for every mine
			updateIncomeByAttribute(Attribute.FORGE, 1);
			}
		}
	}
	
	/* Update all Entities that are affected by the death of the Entity at
	 * tileID into play.
	 */
	private void triggerOnDeathEffects(int tileID)
	{
		Tile t = commonData.getTile(tileID);
		PeaceEntity e = t.getE();
		
		if (e.hasAttribute(Attribute.MINE))
		{
			// Forges gain +1 income for every mine
			updateIncomeByAttribute(Attribute.FORGE, -1);
		}
	}
	
	/* Scan all entities owned by Players. If the Entity has the given
	 * Attribute, change the Entity's income by the given amount.
	 */
	private void updateIncomeByAttribute(Attribute a, int change)
	{
		for (Location loc : commonData.locations)
		{
			for (Tile t : loc.getTiles())
			{
				PeaceEntity e = t.getE();
				if (e instanceof Structure && e.hasAttribute(a))
				{
					Structure s = (Structure)e;
					s.setIncome(s.getIncome() + change);
					broadcastUpdateEntity(t.getTileID(), "income",
							s.getIncome());
				}
			}
		}
	}
	
	/* Calculate income for the active player, then call
	 * commonData.nextTurn()
	 */
	private void handleNextTurn()
	{
		Player activePlayer = commonData.getActivePlayer();
		int income = 2; // Base income of 2
		
		// Income based on victory centers controlled
		income += commonData.getNumCentersControlled(
				activePlayer.getPlayerID());
		
		// Income based on PeaceEntities controlled
		for (PeaceEntity e : activePlayer.getEntities())
		{
			if (e instanceof Structure)
			{
				Structure s = (Structure)e;
				income += s.getIncome();
			}
		}
		
		activePlayer.setMoney(activePlayer.getMoney() + income);
		
		// Broadcast update
		broadcastUpdatePlayerMoney(activePlayer.getMoney(),
				activePlayer.getPlayerID());
		
		commonData.nextTurn();
		PacketMessage nextTurn = new PacketMessage();
		nextTurn.type = EventType.NEXT_TURN;
		network.broadcastToPlayers(nextTurn);
	}
}
