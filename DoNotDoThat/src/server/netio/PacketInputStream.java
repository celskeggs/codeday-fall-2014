package server.netio;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PacketInputStream {
	private DataInputStream din;

	public PacketInputStream(InputStream in) throws IOException {
		this.din = new DataInputStream(in);
		if (din.readInt() != (int) 0xd007d074) {
			throw new IOException("Bad magic number!");
		}
	}
	
	public Packet read() throws IOException {
		Packet p = new Packet();
		p.recv(din);
		return p;
	}

	public void close() throws IOException {
		din.close();
	}
}
