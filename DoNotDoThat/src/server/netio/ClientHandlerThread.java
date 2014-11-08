package server.netio;

import java.io.IOException;
import java.net.Socket;

import server.ClientContext;
import server.ServerContext;
import server.commands.Command;
import server.logger.Logger;

public class ClientHandlerThread extends Thread {

	private final Socket client;
	private final ServerContext serverContext;

	public ClientHandlerThread(Socket client, ServerContext context) {
		this.client = client;
		this.serverContext = context;
	}

	public void run() {
		try {
			Logger.info("Client connected: " + client.getRemoteSocketAddress());
			ClientContext clientContext = serverContext
					.getClientContext(new PacketOutputStream(client
							.getOutputStream()));
			PacketInputStream pin;
			pin = new PacketInputStream(client.getInputStream());
			while (true) {
				Command command = Command.parse(clientContext, pin.read());
				if (command != null) {
					serverContext.addCommandToQueue(command);
				}
			}
		} catch (IOException e) {
			Logger.severe("Failed to init client", e);
			return;
		}
	}
}
