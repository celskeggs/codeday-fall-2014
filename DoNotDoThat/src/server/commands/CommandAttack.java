package server.commands;

import server.GameContext;

public class CommandAttack extends Command {
	private final String cmdname, who;

	public CommandAttack(Object obj) {
		Object[] data = (Object[]) obj;
		if (data.length != 2) {
			throw new RuntimeException("Bad argument count to attack");
		}
		cmdname = (String) data[0];
		who = (String) data[1];
	}

	@Override
	public void process(GameContext context) {
		context.queueAttack(client, cmdname, who);
	}
}
