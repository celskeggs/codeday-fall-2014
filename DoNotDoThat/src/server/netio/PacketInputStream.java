package server.netio;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PacketInputStream {
	private DataInputStream din;

	public PacketInputStream(InputStream in) {
		this.din = new DataInputStream(in);
	}
	
	public Packet read() throws IOException {
		Packet p = new Packet();
		p.recv(din);
		return p;
	}
}
