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
	private boolean end = false;
	private PacketInputStream input;

	public ClientHandlerThread(Socket client, ServerContext context) {
		this.client = client;
		this.serverContext = context;
	}

	public void run() {
		ClientContext clientContext = null;
		try {
			Logger.info("Client connected: " + client.getRemoteSocketAddress());
			clientContext = serverContext.getClientContext(
					new PacketOutputStream(client.getOutputStream()), this);
			input = new PacketInputStream(client.getInputStream());
			while (!end && !Thread.interrupted()) {
				Command command = Command.parse(clientContext, input.read());
				if (command != null) {
					serverContext.addCommandToQueue(command);
				}
			}
		} catch (IOException e) {
			Logger.severe("Failed on client", e);
		} finally {
			if (clientContext != null) {
				clientContext.terminated();
			}
			try {
				input.close();
			} catch (IOException e) {
				Logger.warning("Error while closing socket", e);
			}
			try {
				client.close();
			} catch (IOException e) {
				Logger.warning("Error while closing socket", e);
			}
		}
	}

	public void close() {
		end = true;
		this.interrupt();
		try {
			input.close();
		} catch (IOException e) {
			Logger.warning("Error while closing socket", e);
		}
		try {
			client.close();
		} catch (IOException e) {
			Logger.warning("Error while closing socket", e);
		}
	}
}
