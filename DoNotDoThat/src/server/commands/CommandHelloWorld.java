package server.commands;

import java.nio.ByteBuffer;

import server.GameContext;

public class CommandHelloWorld extends Command {
	private final int value;

	public CommandHelloWorld(ByteBuffer buf) {
		this.value = buf.getInt();
	}

	@Override
	public void process(GameContext context) {
		System.out.println("Hello World: " + value);
	}
}
