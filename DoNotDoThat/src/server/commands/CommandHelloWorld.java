package server.commands;

import server.GameContext;

public class CommandHelloWorld extends Command {
	private final Object value;

	public CommandHelloWorld(Object obj) {
		this.value = obj;
	}

	@Override
	public void process(GameContext context) {
		context.storage.put("hello." + client.clientId, value);
	}
}
