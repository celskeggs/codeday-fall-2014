package server.commands;

import server.ClientContext;
import server.GameContext;

public class CommandAttemptEntry extends Command {

	public CommandAttemptEntry(Object o) {
		
	}
	
	@Override
	public void process(GameContext context) {
		context.storage.put("isready." + client.clientId, true);
	}
}
