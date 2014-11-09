package server.commands;

import server.ClientContext;
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
				ClientContext players[] = client.serverContext.listPlayers();
				for (int i=0; i<players.length; i++) {
					if (players[i] != null && name.equals(players[i].getClassName())) {
						String plyname = players[i].getName();
						if (plyname == null) {
							plyname = "player" + players[i].getID();
						}
						client.receivedChatMessage("The class '" + name + "' is already taken by " + plyname);
						return;
					}
				}
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
