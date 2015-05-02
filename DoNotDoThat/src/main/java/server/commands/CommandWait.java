package server.commands;

import server.GameContext;

public class CommandWait extends Command {

	public CommandWait(Object obj) {
	}

	@Override
	public void process(GameContext context) {
		context.queueAttack(client, "wait", "self");
	}
}
