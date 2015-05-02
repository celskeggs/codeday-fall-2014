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
	public static final int DEFAULT_PLAYER_HEALTH = 10;

	public ClientContext(ServerContext serverContext, int clientId, PacketOutputStream packetOutputStream, ClientHandlerThread handler) {
		super(serverContext.context, DEFAULT_PLAYER_HEALTH, "" + clientId);
		this.serverContext = serverContext;
		this.clientId = clientId;
		this.packetOutputStream = packetOutputStream;
		this.handler = handler;
		resetCombatant();
	}

	public void setLevel(int level) {
		game.storage.put("level." + game.storage.get("ip." + clientId), level);
	}

	public int getLevel() {
		Integer out = (Integer) game.storage.get("level." + game.storage.get("ip." + clientId));
		return out == null ? 1 : out;
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
		// Logger.info("Sending message '" + string + "' -> client." +
		// this.clientId);
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

	public String getName() {
		return (String) serverContext.context.storage.get("name." + clientId);
	}

	public void setName(String name) {
		serverContext.context.storage.put("name." + clientId, name);
	}

	public String getClassName() {
		return (String) serverContext.context.storage.get("class." + clientId);
	}

	public void setClassName(String name) {
		serverContext.context.storage.put("class." + clientId, name);
	}

	@Override
	public String getID() {
		return "#" + clientId;
	}

	public void resetCombatant() {
		serverContext.context.storage.remove("class." + clientId);
		serverContext.context.storage.put("isready." + clientId, false);
		resetStatusAndHealth(DEFAULT_PLAYER_HEALTH + getLevel() - 1);
	}

	@Override
	public String getTargetName(CombatantContext[] combatants) {
		return (String) serverContext.context.storage.get("target." + clientId);
	}

	@Override
	public String getAttackType() {
		return (String) serverContext.context.storage.get("attack." + clientId);
	}
}
