package server;

public abstract class CombatantContext {
	protected final GameContext game;
	private String uid;

	public CombatantContext(GameContext game, int health, String uid) {
		this.game = game;
		this.uid = uid;
		this.setHealth(health);
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
}
