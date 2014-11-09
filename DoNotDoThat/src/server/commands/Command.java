package server.commands;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import server.ClientContext;
import server.GameContext;
import server.data.Encoder;
import server.logger.Logger;
import server.netio.Packet;

public abstract class Command {

	public ClientContext client;

	private static final ArrayList<Class<? extends Command>> classes = new ArrayList<>();

	static {
		classes.add(CommandHelloWorld.class);
		classes.add(CommandAttemptEntry.class);
		classes.add(CommandAttack.class);
		classes.add(CommandChat.class);
		classes.add(CommandSetName.class);
		classes.add(CommandSetClass.class);
	}

	public static Command parse(ClientContext clientContext, Packet packet) {
		if (packet.type < 0 || packet.type >= classes.size()) {
			Logger.severe("Unrecognized packet ID: " + packet.type);
			return null;
		}
		ByteBuffer buf = ByteBuffer.wrap(packet.data);
		try {
			Object object = Encoder.decode(buf);
			Command cmd = (Command) classes.get(packet.type).getConstructor(Object.class).newInstance(object);
			cmd.client = clientContext;
			return cmd;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			Logger.severe("Could not load packet", e);
			return null;
		}
	}

	public abstract void process(GameContext context);
}
