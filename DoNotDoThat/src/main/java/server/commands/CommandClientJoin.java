package server.commands;

import server.ClientContext;
import server.GameContext;

// Not parsed - virtual command.
public class CommandClientJoin extends Command {

	public CommandClientJoin(ClientContext clientContext) {
		client = clientContext;
	}

	@Override
	public void process(GameContext context) {
		context.storage.put("isready." + client.clientId, false);
		context.storage.remove("name." + client.clientId);
		context.storage.remove("class." + client.clientId);
		context.storage.setAllDirty();
	}
}
