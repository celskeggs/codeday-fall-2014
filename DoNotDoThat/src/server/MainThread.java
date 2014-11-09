package server;

import java.io.IOException;

import server.logger.Logger;
import server.netio.ServerHandlerThread;
import server.readout.Readout;

public class MainThread extends Thread {

	public static void main(String[] args) throws IOException {
		ServerContext context = new ServerContext();
		Readout.start(context.context.storage);
		new ServerHandlerThread(50000, context).start();
		new MainThread(context).start();
	}

	private final ServerContext context;

	public MainThread(ServerContext context) {
		this.context = context;
	}

	public void run() {
		long lastCycle = System.currentTimeMillis();
		long lastOverloadedMessage = 0;
		context.initGame();
		while (true) {
			context.processCommands();
			context.processGame();
			context.updateClients();
			long toWait = lastCycle + 10 - System.currentTimeMillis();
			if (toWait > 0) {
				try {
					Thread.sleep(toWait);
				} catch (InterruptedException e) {
					Logger.warning("Interrupted", e);
				}
			} else if (toWait < 0
					&& lastOverloadedMessage + 1000 < System
							.currentTimeMillis()) {
				lastOverloadedMessage = System.currentTimeMillis();
				Logger.fine("OVERLOADED: " + -toWait);
			}
			lastCycle = System.currentTimeMillis();
		}
	}
}
