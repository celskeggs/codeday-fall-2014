package server.commands;

import server.GameContext;

public class CommandSetName extends Command {
	private final String name;

	public CommandSetName(Object obj) {
		this.name = (String) obj;
	}

	@Override
	public void process(GameContext context) {
		if (client.getName() == null) {
			client.setName(name);
			client.receivedChatMessage("Hello, " + name + "!");
		} else {
			client.receivedChatMessage("You already set your name!");
		}
	}
}
