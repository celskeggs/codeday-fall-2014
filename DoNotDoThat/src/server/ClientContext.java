package server;

import java.io.IOException;

import server.logger.Logger;
import server.netio.ClientHandlerThread;
import server.netio.Packet;
import server.netio.PacketOutputStream;

public class ClientContext extends CombatantContext {

	public final ServerContext serverContext;
	public final int clientId;
	private final PacketOutputStream packetOutputStream;
	private final ClientHandlerThread handler;
	public String name;

	public ClientContext(ServerContext serverContext, int clientId,
			PacketOutputStream packetOutputStream, ClientHandlerThread handler) {
		super(5);
		this.serverContext = serverContext;
		this.clientId = clientId;
		this.packetOutputStream = packetOutputStream;
		this.handler = handler;
	}
	
	public boolean isValid() {
		return clientId >= 0 && serverContext.getClient(clientId) == this;
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

	public void receivedChatMessage(String string) {
		Packet p = new Packet();
		p.type = 0x0306;
		p.data = string.getBytes();
		try {
			packetOutputStream.write(p);
		} catch (IOException ex) {
			Logger.warning("Terminating client due to IO exception: " + this, ex);
			this.terminate();
		}
	}
}
