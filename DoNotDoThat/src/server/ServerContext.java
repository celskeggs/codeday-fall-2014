package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import server.commands.Command;
import server.netio.PacketOutputStream;

public class ServerContext {
	
	private final BlockingQueue<Command> commands = new LinkedBlockingQueue<>();
	private final GameContext context = new GameContext();

	public ClientContext getClientContext(PacketOutputStream packetOutputStream) {
		return new ClientContext(this, packetOutputStream);
	}

	public void processCommands() {
		while (!commands.isEmpty()) {
			commands.remove().process(context);
		}
	}

	public void processGame() {
		// TODO Auto-generated method stub
	}

	public void updateClients() {
		// TODO Auto-generated method stub
	}

	public void addCommandToQueue(Command command) {
		commands.add(command);
	}
}
