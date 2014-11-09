package server;

public abstract class CombatantContext {
	private final GameContext game;
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
		return getHealth() <= 0;
	}

	public void setHealth(int health) {
		game.storage.put("health." + uid, health);
	}

	public abstract Object getID();

	public abstract String getName();
}
