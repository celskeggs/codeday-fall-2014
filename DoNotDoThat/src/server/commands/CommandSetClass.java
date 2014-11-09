package server.commands;

import server.GameContext;

public class CommandSetClass extends Command {
	private final String name;

	public CommandSetClass(Object obj) {
		this.name = (String) obj;
	}

	@Override
	public void process(GameContext context) {
		if (client.getClassName() == null) {
			if (GameContext.isValidClass(name)) {
				client.setClassName(name);
				client.receivedChatMessage("You are now a " + name + "!");
			} else {
				client.receivedChatMessage("Invalid class '" + name + "'");
			}
		} else {
			client.receivedChatMessage("You already set your class!");
		}
	}
}
