package server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import server.data.KeyValueStore;
import server.logger.Logger;

public class GameContext {
	public final KeyValueStore storage = new KeyValueStore();
	public final BossContext boss = new BossContext(this);

	public static final int turnlen = 20 * 100;

	private CombatantContext[] getCombatants(ServerContext serverContext) {
		CombatantContext[] players = serverContext.listPlayers();
		CombatantContext[] out = new CombatantContext[players.length + 1];
		System.arraycopy(players, 0, out, 0, players.length);
		out[players.length] = boss;
		return out;
	}

	public void processGame(ServerContext serverContext) {
		CombatantContext[] players = serverContext.listPlayers();
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
		serverContext.route.setAvailable(b != null && b && count < 4);
		if (b != null && b) {
			if (count >= 2 && count == ready) {
				storage.put("mode.isinlobby", false);
				storage.put("mode.countdown", turnlen);
				for (int i = 0; i < players.length; i++) {
					storage.put("attack." + i, null);
				}
				boss.scaleHealth(count);
				sendMessage(serverContext, "[SUPREME SERVER MONKEY] Begin!");
			}
		} else {
			int countdown = (int) storage.get("mode.countdown");
			int needed = 0, has = 0;
			for (int i = 0; i < players.length; i++) {
				if (players[i] != null && !(Boolean) storage.get("connected." + i) && (Integer) storage.get("health." + i) > 0) {
					this.sendMessage(serverContext, "[SUPREME SERVER MONKEY] " + players[i].getName() + " suffered a sudden heart attack due to disconnecting!");
					storage.put("health." + i, 0);
				}
				if (players[i] != null) {
					players[i].updateIsDead();
					if (!players[i].isDead()) {
						needed++;
						if (storage.get("attack." + i) != null) {
							has++;
						}
					}
				}
			}
			boss.updateIsDead();
			storage.put("attack.total", needed);
			if (has >= needed || countdown <= 0) { // TURN OVER
				processTurn(serverContext, players);
			} else {
				storage.put("mode.countdown", countdown - 2);
			}
			checkWin(serverContext, getCombatants(serverContext));
		}
	}

	public void processTurn(ServerContext server, CombatantContext[] players) {
		this.sendMessage(server, "[SUPREME SERVER MONKEY] A turn is happening!");
		for (CombatantContext comb : getCombatants(server)) {
			if (comb != null) {
				comb.applyStatusEffects(server);
			}
		}
		for (CombatantContext comb : getCombatants(server)) {
			if (comb != null) {
				handleCombatant(server, getCombatants(server), comb);
			}
		}
		for (int i = 0; i < players.length; i++) {
			storage.put("attack." + i, null);
		}
		storage.put("mode.countdown", turnlen);
	}

	private void handleCombatant(ServerContext server, CombatantContext[] players, CombatantContext player) {
		String targetname = player.getTargetName(players);
		CombatantContext target = targetname == null ? null : getCombatant(players, player, targetname);
		String command = player.getAttackType();
		if (target != null && command != null && !command.equals("wait")) {
			if (player.isDead()) {
				this.sendMessage(server, "[SUPREME SERVER MONKEY] " + player.getName() + " tried to attack " + target.getName() + ", but is dead!");
			} else if (player.wasParalyzed()) {
				this.sendMessage(server, "[SUPREME SERVER MONKEY] " + player.getName() + " tried to attack " + target.getName() + ", but was paralyzed!");
			} else {
				String str = player.getClassName() + "." + command;
				Integer dmgO = commandDamage.get(str);
				if (dmgO == null) {
					Logger.warning("WARNING: NO SUCH COMMAND: " + str);
				} else if (!player.useUpTo(str)) {
					this.sendMessage(server, "[SUPREME SERVER MONKEY] " + player.getName() + " tried to attack " + target.getName()
							+ ", but has used the move '" + command + "' too many times!");
				} else {
					int dmg = dmgO;
					if (!doSpecialAttack(server, target, player, str) || dmg != 0) {
						this.sendMessage(server, target.getName() + " was hit by " + player.getName() + " for " + dmg + "!");
						target.setHealth(target.getHealth() - dmg);
					}
				}
			}
		} else if (!player.isDead()) {
			this.sendMessage(server, "[SUPREME SERVER MONKEY] " + player.getName() + " decided to do nothing.");
		}
	}

	public void checkWin(ServerContext server, CombatantContext[] players) {
		int alive = 0;
		CombatantContext winner = null;
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null && !players[i].isDead()) {
				alive++;
				winner = players[i];
			}
		}
		if (alive == 1) {
			if (winner instanceof ClientContext) {
				ClientContext client = (ClientContext) winner;
				client.setLevel(client.getLevel() + 1);
			}
			this.sendMessage(server, "[SUPREME SERVER MONKEY] " + winner.getName() + " has won and gained a level! Resetting server.");
		} else if (alive == 0) {
			this.sendMessage(server, "[SUPREME SERVER MONKEY] Everyone is DEAD! Resetting server.");
		} else {
			return;
		}
		resetRound(server, players);
	}

	public void resetRound(ServerContext server, CombatantContext[] players) {
		storage.put("mode.isinlobby", true);
		for (CombatantContext ply : players) {
			if (ply != null) {
				ply.resetCombatant();
			}
		}
		this.sendMessage(server, "[SUPREME SERVER MONKEY] New round. You may reselect your class.");
	}

	private CombatantContext getCombatant(CombatantContext[] plys, CombatantContext user, String name) {
		if (name.equals("self")) {
			return user;
		}
		for (int i = 0; i < plys.length; i++) {
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
	private static final HashMap<String, Integer> maxUses = new HashMap<>();
	private static final HashMap<String, Integer> commandDamage = new HashMap<>();

	public static boolean isValidClass(String className) {
		return commands.containsKey(className) && !"boss".equals(className);
	}

	public static int getMaxUses(String cmdname) {
		Integer out = maxUses.get(cmdname);
		if (out == null) {
			Logger.severe("Nonexistent max use: " + cmdname);
			return 1;
		}
		return out;
	}

	static {
		commands.put("wizard", Arrays.asList("burn", "grind", "drown", "blast", "zap"));
		commands.put("soldier", Arrays.asList("shoot", "bombard", "punch", "kick", "stun"));
		commands.put("ranger", Arrays.asList("draw", "shank", "slash", "throw", "kick"));
		commands.put("robot", Arrays.asList("pew", "pewpew", "inhale", "cook", "burn"));
		commands.put("boss", Arrays.asList("burn", "slam", "swipe", "smash", "crush", "stomp", "bash", "sweep", "impale", "quack", "swim", "eat"));
		/*
		 * for (String cmd : new String[] {"wizard.grind", "wizard.drown",
		 * "wizard.zap", "soldier.bombard", "soldier.kick", "soldier.stun",
		 * "ranger.shank", "ranger.kick"}) { maxUses.put(cmd, 2); } for (String
		 * cmd : new String[] {"wizard.burn", "soldier.shoot", "ranger.draw",
		 * "ranger.throw", ""}) { maxUses.put(cmd, 5); } for (String cmd : new
		 * String[] {"wizard.blast", "soldier.punch", "ranger.slash",
		 * "robot.pew"}) { maxUses.put(cmd, 10); }
		 */
		for (String cmd : new String[] { "wizard.zap", "soldier.stun", "ranger.kick", "robot.burn" }) { // NONE
			commandDamage.put(cmd, 0);
			maxUses.put(cmd, 2);
		}
		for (String cmd : new String[] { "wizard.blast", "soldier.punch", "ranger.slash", "robot.inhale", "boss.swipe", "boss.stomp", "boss.sweep", "boss.swim" }) { // LOW
			commandDamage.put(cmd, 1);
			maxUses.put(cmd, 5);
		}
		for (String cmd : new String[] { "wizard.burn", "soldier.shoot", "ranger.draw", "ranger.throw", "robot.pew", "boss.burn", "boss.smash", "boss.bash", "boss.quack" }) { // MEDIUM
			commandDamage.put(cmd, 2);
			maxUses.put(cmd, 3);
		}
		for (String cmd : new String[] { "wizard.grind", "wizard.drown", "soldier.bombard", "soldier.kick", "ranger.shank", "robot.pewpew", "robot.cook",
				"boss.slam", "boss.crush", "boss.impale", "boss.eat" }) { // HIGH
			commandDamage.put(cmd, 3);
			maxUses.put(cmd, 1);
		}
	}

	public static String[] getUseBasedCommands() {
		return maxUses.keySet().toArray(new String[maxUses.size()]);
	}

	private final Random rand = new Random();

	private boolean prob(int prec) {
		return rand.nextInt(100) < prec;
	}

	private boolean doSpecialAttack(ServerContext server, CombatantContext target, CombatantContext attacker, String cmd) {
		switch (cmd) {
		case "wizard.burn":
			if (prob(20)) {
				sendMessage(server, attacker.getName() + " burns " + target.getName() + "!");
				target.applyStatusEffect("burn", 3);
				return true;
			}
			break;
		case "wizard.grind":
		case "boss.crush":
			if (prob(1)) {
				sendMessage(server, attacker.getName() + " INSTAKILLS " + target.getName() + "!");
				return true;
			}
			break;
		case "wizard.drown":
			if (prob(20)) {
				sendMessage(server, attacker.getName() + " nearly drowns " + target.getName() + "!");
				target.applyStatusEffect("paralyze", 1);
				return true;
			}
			break;
		case "wizard.zap":
			if (prob(60)) {
				sendMessage(server, attacker.getName() + " zaps " + target.getName() + "!");
				target.applyStatusEffect("paralyze", 1);
				return true;
			}
			break;
		case "soldier.bombard":
			if (prob(10)) {
				sendMessage(server, attacker.getName() + " bombards (and burns) " + target.getName() + "!");
				target.applyStatusEffect("burn", 3);
				return true;
			}
			break;
		case "soldier.stun":
			if (prob(60)) {
				sendMessage(server, attacker.getName() + " stuns " + target.getName() + "!");
				target.applyStatusEffect("paralyze", 1);
				return true;
			}
			break;
		case "ranger.slash":
			if (prob(20)) {
				sendMessage(server, attacker.getName() + " slashes " + target.getName() + "!");
				target.applyStatusEffect("bleed", 3);
				return true;
			}
			break;
		case "ranger.kick":
			if (prob(60)) {
				sendMessage(server, attacker.getName() + " visciously kicks " + target.getName() + "!");
				target.applyStatusEffect("paralyze", 1);
				return true;
			}
			break;
		case "robot.inhale":
			if (prob(10)) {
				sendMessage(server, attacker.getName() + " inhales " + target.getName() + "!");
				target.applyStatusEffect("paralyze", 1);
				return true;
			}
			break;
		case "robot.burn":
			if (prob(80)) {
				sendMessage(server, attacker.getName() + " burns " + target.getName() + "!");
				target.applyStatusEffect("burn", 3);
				return true;
			}
			break;
		case "boss.burn":
			if (prob(50)) {
				sendMessage(server, attacker.getName() + " burns " + target.getName() + "!");
				target.applyStatusEffect("burn", 3);
				return true;
			}
			break;
		case "boss.sweep":
			if (prob(30)) {
				sendMessage(server, attacker.getName() + " sweeps " + target.getName() + "!");
				target.applyStatusEffect("paralyze", 1);
				return true;
			}
			break;
		case "boss.eat":
			if (prob(20)) {
				sendMessage(server, attacker.getName() + " bites " + target.getName() + "!");
				target.applyStatusEffect("bleed", 3);
				return true;
			}
			break;
		}
		return false;
	}

	public void queueAttack(ClientContext client, String cmdname, String who) {
		String cls = (String) storage.get("class." + client.clientId);
		List<String> valid = commands.get(cls);
		if (valid == null) {
			Logger.severe("Invalid class: " + cls);
			return;
		}
		if (!valid.contains(cmdname) && !"wait".equals(cmdname)) {
			Logger.info(cls + " cannot use attack " + cmdname);
			client.receivedChatMessage("Your class cannot use that attack!");
			return;
		}
		if (!client.canUseMove(cls + "." + cmdname)) {
			client.receivedChatMessage("You are out of uses of that attack!");
			return;
		}
		storage.put("attack." + client.clientId, cmdname);
		CombatantContext out = getCombatant(getCombatants(client.serverContext), client, who);
		if (out != null) {
			storage.put("target." + client.clientId, out.getID());
		} else {
			client.receivedChatMessage("I don't know who you mean!");
		}
	}

	public void sendChatMessage(ClientContext client, String textline) {
		String message = "[" + (client.getName() == null ? client.getID() : client.getName()) + "] " + textline;
		Logger.info(message);
		sendMessage(client.serverContext, message);
	}

	public void sendMessage(ServerContext context, String textline) {
		for (ClientContext target : context.listPlayers()) {
			if (target != null) {
				target.receivedChatMessage(textline);
			}
		}
	}
}
