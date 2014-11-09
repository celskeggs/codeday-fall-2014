package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import server.commands.Command;
import server.logger.Logger;
import server.netio.ClientHandlerThread;
import server.netio.PacketOutputStream;

public class ServerContext {

	private final BlockingQueue<Command> commands = new LinkedBlockingQueue<>();
	private final GameContext context = new GameContext();
	private final ClientContext[] clients = new ClientContext[4];
	private boolean isInLobby = true;

	public ClientContext getClientContext(
			PacketOutputStream packetOutputStream, ClientHandlerThread handler) {
		if (isInLobby) {
			synchronized (clients) {
				for (int i = 0; i < clients.length; i++) {
					if (clients[i] == null) {
						return clients[i] = new ClientContext(this, i,
								packetOutputStream, handler);
					}
				}
			}
		}
		return new ClientContext(this, -1, packetOutputStream, handler);
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
		synchronized (clients) {
			context.storage.sendUpdatesAll(clients);
		}
	}

	public void addCommandToQueue(Command command) {
		commands.add(command);
	}

	public void clientTerminated(int clientId, ClientContext clientContext) {
		if (clientId != -1) {
			synchronized (clients) {
				if (clients[clientId] != clientContext) {
					Logger.warning("Client is not attached properly: "
							+ clientContext);
				} else {
					clients[clientId] = null;
				}
			}
		}
	}
}
