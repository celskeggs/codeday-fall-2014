package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import server.data.KeyValueStore;

public class GameContext {
	public final KeyValueStore storage = new KeyValueStore();
	
	public static final int turnlen = 20 * 100;

	public void processGame(ServerContext serverContext) {
		ClientContext[] players = serverContext.listPlayers();
		int count = 0, ready = 0;
		for (int i = 0; i < players.length; i++) {
			storage.put("connected." + i, players[i] != null);
			if (players[i] != null) {
				count++;
				Boolean b = (Boolean) storage.get("isready." + i);
				if (b != null && b) {
					ready++;
				}
			}
		}
		Boolean b = (Boolean) storage.get("mode.isinlobby");
		if (b != null && b) {
			if (count >= 2 && count == ready) {
				storage.put("mode.isinlobby", false);
				storage.put("mode.countdown", turnlen);
				for (int i = 0; i < players.length; i++) {
					storage.put("attack." + i, null);
				}
			}
		} else {
			int countdown = (int) storage.get("mode.countdown");
			int needed = 0;
			for (int i = 0; i < players.length; i++) {
				if (storage.get("attack." + i) != null) {
					needed++;
				}
			}
			storage.put("attack.total", needed);
			if (needed == count || countdown <= 0) { // TURN OVER
				processTurn(serverContext, players);
			} else {
				storage.put("mode.countdown", countdown - 1);
			}
		}
	}

	public void processTurn(ServerContext server, ClientContext[] players) {
		this.sendMessage(server, "[SUPREME SERVER MONKEY] TURN IS HAPPENING");
		for (int i = 0; i < players.length; i++) {
			if (players[i] == null) {
				continue;
			}
			CombatantContext target = getCombatant(players[i], (String) storage.get("target." + i));
			String command = (String) storage.get("attack." + i);
			if (target != null && command != null) {
				int dmg = commandDamage.get(storage.get("class." + i) + "." + command);
				this.sendMessage(server, target.getName() + " was hit by " + players[i].getName() + " for " + dmg + "!");
				target.setHealth(target.getHealth() - dmg);
			} else {
				this.sendMessage(server, "[SUPREME SERVER MONKEY] TURN IS HAPPENING");
			}
		}
		for (int i = 0; i < players.length; i++) {
			storage.put("attack." + i, null);
		}
		storage.put("mode.countdown", turnlen);
	}

	private CombatantContext getCombatant(ClientContext user, String name) {
		if (name.equals("self")) {
			return user;
		}
		ClientContext[] plys = user.serverContext.listPlayers();
		for (int i=0; i<plys.length; i++) {
			if (plys[i] != null && (name.equals(plys[i].getName()) || name.equals(plys[i].getID()))) {
				return plys[i];
			}
		}
		return null;
	}

	public void initGame() {
		storage.put("mode.isinlobby", true);
	}

	private static final HashMap<String, List<String>> commands = new HashMap<>();
	private static final HashMap<String, Integer> commandDamage = new HashMap<>();

	public static boolean isValidClass(String className) {
		return commands.containsKey(className);
	}
	
	static {
		commands.put("wizard",
				Arrays.asList("burn", "grind", "drown", "blast", "zap"));
		commands.put("soldier",
				Arrays.asList("shoot", "bombard", "punch", "kick", "stun"));
		commands.put("ranger",
				Arrays.asList("draw", "shank", "slash", "throw", "kick"));
		commands.put("robot", Arrays.asList("pew", "pewpew", "pewpewpew",
				"pewpewpewpew", "pow"));
		for (String cmd : new String[] {"wizard.zap", "soldier.stun", "ranger.kick", "robot.burn"}) { // NONE
			commandDamage.put(cmd, 0);
		}
		for (String cmd : new String[] {"wizard.blast", "soldier.punch", "ranger.slash", "robot.inhale"}) { // LOW
			commandDamage.put(cmd, 1);
		}
		for (String cmd : new String[] {"wizard.burn", "soldier.shoot", "ranger.draw", "ranger.throw", "robot.pew"}) { // MEDIUM
			commandDamage.put(cmd, 2);
		}
		for (String cmd : new String[] {"wizard.grind", "wizard.drown", "soldier.bombard", "soldier.kick", "ranger.shank", "robot.pewpew", "robot.cook"}) { // HIGH
			commandDamage.put(cmd, 3);
		}
	}

	public void queueAttack(ClientContext client, String cmdname, String who) {
		String cls = (String) storage.get("class." + client.clientId);
		List<String> valid = commands.get(cls);
		if (!valid.contains(cmdname)) {
			client.receivedChatMessage("Your class cannot use that attack!");
			return;
		}
		storage.put("attack." + client.clientId, cmdname);
		CombatantContext out = getCombatant(client, who);
		if (out != null) {
			storage.put("target." + client.clientId, out.getID());
		} else {
			client.receivedChatMessage("I don't know who you mean!");
		}
	}

	public void sendChatMessage(ClientContext client, String textline) {
		sendMessage(client.serverContext, "[" + (client.getName() == null ? client.getID() : client.getName()) + "] " + textline);
	}
	
	public void sendMessage(ServerContext context, String textline) {
		for (ClientContext target : context.listPlayers()) {
			if (target != null) {
				target.receivedChatMessage(textline);
			}
		}
	}
}
