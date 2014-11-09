package server;

import java.util.Random;

public class BossContext extends CombatantContext {

	private static final int DEFAULT_BOSS_HEALTH = 15;
	private final Random random = new Random();
	private String[] attacks;
	private String name;

	public BossContext(GameContext game) {
		super(game, DEFAULT_BOSS_HEALTH, "boss");
		generateBoss();
		game.storage.put("class.boss", "boss");
	}

	private void generateBoss() {
		switch (random.nextInt(4)) {
		case 0: name = "dragon"; attacks = new String[] { "burn", "slam", "swipe" }; break;
		case 1: name = "remo williams"; attacks = new String[] { "smash", "crush", "stomp" }; break;
		case 2: name = "paladin"; attacks = new String[] { "bash", "sweep", "impale" }; break;
		case 3: name = "duck"; attacks = new String[] { "quack", "swim", "eat" }; break;
		}
		game.storage.put("name.boss", name);
	}

	public void scaleHealth(int plyCount) {
		setHealth((int) (5 + 2.5 * plyCount));
	}

	@Override
	public String getID() {
		return "boss";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void resetCombatant() {
		resetStatusAndHealth(DEFAULT_BOSS_HEALTH);
		generateBoss();
	}

	@Override
	public String getTargetName(CombatantContext[] combatants) {
		int count = 0;
		for (CombatantContext comb : combatants) {
			if (comb != null && comb != this && !comb.isDead()) {
				count++;
			}
		}
		if (count == 0) {
			return null;
		}
		int index = random.nextInt(count);
		for (CombatantContext comb : combatants) {
			if (comb != null && comb != this && !comb.isDead() && index-- == 0) {
				return comb.getID();
			}
		}
		return null;
	}

	@Override
	public String getAttackType() {
		return isDead() ? null : attacks[random.nextInt(attacks.length)];
	}

	@Override
	public String getClassName() {
		return "boss";
	}

	public int getMoveUses(String move) {
		return 0;
	}
}
