package server.commands;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import server.ClientContext;
import server.GameContext;
import server.data.Encoder;
import server.logger.Logger;
import server.netio.Packet;

public abstract class Command {

	@SuppressWarnings("unchecked")
	private static final Class<? extends Command>[] classes = (Class<? extends Command>[]) new Class<?>[] {CommandHelloWorld.class };

	public static Command parse(ClientContext clientContext, Packet packet) {
		if (packet.type < 0 || packet.type >= classes.length) {
			Logger.severe("Unrecognized packet ID: " + packet.type);
			return null;
		}
		ByteBuffer buf = ByteBuffer.wrap(packet.data);
		try {
			Object object = Encoder.decode(buf);
			return (Command) classes[packet.type].getConstructor(Object.class)
					.newInstance(object);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logger.severe("Could not load packet", e);
			return null;
		}
	}

	public abstract void process(GameContext context);
}
