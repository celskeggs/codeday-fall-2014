package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

import server.logger.Logger;
import server.netio.ServerHandlerThread;
import server.readout.Readout;
import server.route.RouteClient;

public class MainThread extends Thread {

	public static void main(String[] args) throws IOException {
		ServerContext context = new ServerContext();
		Readout.start(context.context.storage, context);
		Random rand = new Random();
		int port = 50002 + rand.nextInt(10000);
		RouteClient cli = new RouteClient(args.length == 0 ? "10.251.14.147" : args[0], 50001);
		String localAddress = getLocalAddress();
		cli.start(localAddress, port);
		context.route = cli;
		new ServerHandlerThread(port, context).start();
		new MainThread(context).start();
	}

	public static String getLocalAddress() throws SocketException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()){
		    NetworkInterface current = interfaces.nextElement();
		    System.out.println(current);
		    if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
		    Enumeration<InetAddress> addresses = current.getInetAddresses();
		    while (addresses.hasMoreElements()){
		        InetAddress current_addr = addresses.nextElement();
		        if (current_addr.isLoopbackAddress()) continue;
		        return current_addr.getHostAddress();
		    }
		}
		return "127.0.0.1";
	}

	private final ServerContext context;

	public MainThread(ServerContext context) {
		this.context = context;
	}

	public void run() {
		long lastCycle = System.currentTimeMillis();
		long lastOverloadedMessage = 0;
		context.initGame();
		Logger.info("ONLINE");
		while (true) {
			context.processCommands();
			context.processGame();
			context.updateClients();
			long toWait = lastCycle + 20 - System.currentTimeMillis();
			if (toWait > 0) {
				try {
					Thread.sleep(toWait);
				} catch (InterruptedException e) {
					Logger.warning("Interrupted", e);
				}
			} else if (toWait < 0 && lastOverloadedMessage + 1000 < System.currentTimeMillis()) {
				lastOverloadedMessage = System.currentTimeMillis();
				Logger.fine("OVERLOADED: " + -toWait);
			}
			lastCycle = System.currentTimeMillis();
		}
	}
}
