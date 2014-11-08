package server.netio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.ServerContext;
import server.logger.Logger;

public class ServerHandlerThread extends Thread {
	private final ServerSocket server;
	private final ServerContext context;

	public ServerHandlerThread(int port, ServerContext context)
			throws IOException {
		this.server = new ServerSocket(port);
		this.context = context;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Logger.finer("Awaiting connection...");
				Socket client = server.accept();
				new ClientHandlerThread(client, context).start();
			}
		} catch (IOException e) {
			Logger.severe("Client connection failed", e);
		}
	}
}
