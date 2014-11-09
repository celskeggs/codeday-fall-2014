package server.commands;

import server.ClientContext;
import server.GameContext;

public class CommandSetName extends Command {
	private final String name;

	public CommandSetName(Object obj) {
		this.name = (String) obj;
	}

	@Override
	public void process(GameContext context) {
		if (name.length() >= 15) {
			client.receivedChatMessage("That's a terribly long name. Choose a shorter name.");
		} else if (client.getName() == null) {
			if (name.toLowerCase().equals("boss") || name.toLowerCase().equals("dragon")) {
				this.client.receivedChatMessage("Another player has already taken that name!");
				return;
			}
			for (ClientContext client : client.serverContext.listPlayers()) {
				if (client != null && client.getName() != null && name.toLowerCase().equals(client.getName().toLowerCase())) {
					this.client.receivedChatMessage("Another player has already taken that name!");
					return;
				}
			}
			client.setName(name);
			client.receivedChatMessage("Hello, " + name + "!");
		} else {
			client.receivedChatMessage("You already set your name!");
		}
	}
}
