package server.commands;

import server.GameContext;

public class CommandHelloWorld extends Command {
	private final String value;

	public CommandHelloWorld(Object obj) {
		this.value = (String) obj;
	}

	@Override
	public void process(GameContext context) {
		context.storage.put("hello." + client.clientId, value.substring(0, Math.min(value.length(), 32)));
	}
}
