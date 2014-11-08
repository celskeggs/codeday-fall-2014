package server;

import java.io.IOException;

import server.logger.Logger;
import server.netio.ClientHandlerThread;
import server.netio.PacketOutputStream;

public class ClientContext {

	private final ServerContext serverContext;
	private final int clientId;
	private final PacketOutputStream packetOutputStream;
	private final ClientHandlerThread handler;

	public ClientContext(ServerContext serverContext, int clientId,
			PacketOutputStream packetOutputStream, ClientHandlerThread handler) {
		this.serverContext = serverContext;
		this.clientId = clientId;
		this.packetOutputStream = packetOutputStream;
		this.handler = handler;
	}

	public void terminated() {
		serverContext.clientTerminated(clientId, this);
	}

	public PacketOutputStream getPacketOutput() {
		return packetOutputStream;
	}

	public void terminate() {
		try {
			packetOutputStream.close();
		} catch (IOException ex) {
			Logger.warning("Error while closing output stream", ex);
		}
		handler.close();
		serverContext.clientTerminated(clientId, this);
	}

}
