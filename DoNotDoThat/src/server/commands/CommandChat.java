package server.commands;

import server.GameContext;

public class CommandChat extends Command {
	private final String textline;

	public CommandChat(Object obj) {
		this.textline = (String) obj;
	}

	@Override
	public void process(GameContext context) {
		context.sendChatMessage(client, textline);
	}
}
