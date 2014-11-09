package server;

import java.util.Arrays;
import java.util.List;

import server.logger.Logger;

public abstract class CombatantContext {
	protected final GameContext game;
	private String uid;
	private static final List<String> statusEffects = Arrays.asList("burn",
			"bleed", "paralyze");
	private boolean wasParalyzed;

	public CombatantContext(GameContext game, int health, String uid) {
		this.game = game;
		this.uid = uid;
		resetStatusAndHealth(health);
	}

	protected void resetStatusAndHealth(int health) {
		this.setHealth(health);
		for (String elem : statusEffects) {
			setStatus(elem, 0);
		}
		wasParalyzed = false;
	}

	public void applyStatusEffects(ServerContext context) {
		wasParalyzed = false;
		for (String effect : statusEffects) {
			int len = getStatus(effect);
			if (len > 0) {
				applyStatusEffect(context, effect);
				setStatus(effect, len - 1);
			}
		}
	}
	
	private void applyStatusEffect(ServerContext context, String effect) {
		switch (effect) {
		case "burn":
			context.context.sendMessage(context, getName() + " burns for 1 damage!");
			setHealth(getHealth() - 1);
			break;
		case "bleed":
			context.context.sendMessage(context, getName() + " bleeds for 1 damage!");
			setHealth(getHealth() - 1);
			break;
		case "paralyze":
			wasParalyzed = true;
			break;
		default:
			Logger.warning("Unrecognized status effect: " + effect);
			break;
		}
	}

	public int getStatus(String name) {
		return (Integer) game.storage.get("status." + name + "." + uid);
	}
	
	public void setStatus(String name, int turns) {
		game.storage.put("status." + name + "." + uid, turns);
	}

	public int getHealth() {
		return (int) game.storage.get("health." + uid);
	}

	public boolean isDead() {
		return (boolean) game.storage.get("isdead." + uid);
	}

	public void setHealth(int health) {
		game.storage.put("health." + uid, health);
	}

	public abstract String getID();

	public abstract String getName();

	public abstract void resetCombatant();

	public abstract String getTargetName(CombatantContext[] combatants);

	public abstract String getAttackType();

	public abstract String getClassName();

	public void updateIsDead() {
		game.storage.put("isdead." + uid, getHealth() <= 0);
	}

	public boolean wasParalyzed() {
		return wasParalyzed;
	}

	public void applyStatusEffect(String name, int upto) {
		setStatus(name, Math.max(getStatus(name), upto));
	}
	
	public int getMoveUses(String move) {
		Integer out = (Integer) game.storage.get("uses." + move + "." + uid);
		return out == null ? 0 : out;
	}
	
	public void useMove(String move) {
		game.storage.put("uses." + move + "." + uid, getMoveUses(move) + 1);
	}

	public boolean canUseMove(String cmdname) {
		return getMoveUses(cmdname) < GameContext.getMaxUses(cmdname);
	}
	
	public boolean useUpTo(String cmdname) {
		if (canUseMove(cmdname)) {
			useMove(cmdname);
			return true;
		} else {
			return false;
		}
	}
}
