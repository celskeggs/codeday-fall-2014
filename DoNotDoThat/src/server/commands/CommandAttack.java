package server.commands;

import server.GameContext;

public class CommandAttack extends Command {
	private final String cmdname;

	public CommandAttack(Object obj) {
		this.cmdname = (String) obj;
	}

	@Override
	public void process(GameContext context) {
		context.queueAttack(client, cmdname);
	}
}
