package server.commands;

import server.GameContext;

public class CommandAttemptEntry extends Command {

	public CommandAttemptEntry(Object o) {
		
	}
	
	@Override
	public void process(GameContext context) {
		if (client.getName() == null) {
			client.receivedChatMessage("You have not yet set your name!");
		} else if (client.getClassName() == null) {
			client.receivedChatMessage("You have not yet set your class name!");
		} else {
			context.storage.put("isready." + client.clientId, true);
		}
	}
}
