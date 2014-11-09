package server.route;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import server.logger.Logger;

public class RouteClient {
	private final Socket socket;
	private final DataOutputStream output;
	private boolean disconnected = false, available = false;

	public RouteClient(String remote, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(remote, port);
		this.output = new DataOutputStream(socket.getOutputStream());
	}

	public void start(String myName, int myport) throws IOException {
		output.writeInt(0xBEEFDEAD);
		output.writeUTF(myName + ":" + myport);
	}
	
	public synchronized void setAvailable(boolean available) {
		if (disconnected) {
			return;
		}
		if (this.available == available) {
			return;
		}
		try {
			output.writeBoolean(available);
		} catch (IOException e) {
			Logger.warning("Available sending failed", e);
			disconnected = true;
		}
		this.available = available;
	}
}
