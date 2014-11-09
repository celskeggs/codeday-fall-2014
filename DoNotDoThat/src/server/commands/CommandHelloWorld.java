package server.commands;

import server.GameContext;

public class CommandHelloWorld extends Command {
	private final int value;

	public CommandHelloWorld(Object obj) {
		this.value = (Integer) obj;
	}

	@Override
	public void process(GameContext context) {
		context.storage.put("hello", value);
	}
}
