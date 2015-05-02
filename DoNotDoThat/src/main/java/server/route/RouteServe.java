package server.route;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import server.logger.Logger;

public class RouteServe {
	public static void main(String[] args) throws IOException {
		Logger.info("Starting route server...");
		ServerSocket sock = new ServerSocket(50001);
		final ArrayList<String> open = new ArrayList<>();
		final Random rand = new Random();
		while (true) {
			final Socket conn = sock.accept();
			new Thread() {
				public void run() {
					try {
						Logger.fine("Connection from " + conn.getRemoteSocketAddress());
						DataInputStream din = new DataInputStream(conn.getInputStream());
						DataOutputStream dout = new DataOutputStream(conn.getOutputStream());
						int magic = din.readInt();
						if (magic == 0xDEADBEEF) {
							synchronized (open) {
								int count = open.size();
								if (count == 0) {
									Logger.warning("No available servers.");
									dout.writeBoolean(false);
									dout.writeUTF("No available servers.");
								} else {
									String chosen = open.get(rand.nextInt(open.size()));
									Logger.info("Routing " + conn.getRemoteSocketAddress() + " to " + chosen);
									dout.writeBoolean(true);
									dout.writeUTF(chosen);
								}
							}
						} else if (magic == 0xBEEFDEAD) {
							String name = din.readUTF();
							Logger.info("Server " + name + " advertising.");
							try {
								while (true) {
									if (din.readBoolean()) {
										if (!open.contains(name)) {
											Logger.info("Server " + name + " now available.");
											open.add(name);
										}
									} else {
										Logger.info("Server " + name + " no longer available.");
										open.remove(name);
									}
								}
							} finally {
								Logger.info("Server " + name + " disconnected.");
								open.remove(name);
							}
						} else {
							Logger.warning("Bad magic number from client: " + magic);
						}
						conn.close();
					} catch (IOException ex) {
						Logger.warning("IO exception on connection", ex);
					}
				}
			}.start();
		}
	}
}
