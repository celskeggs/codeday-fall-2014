package server.netio;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PacketOutputStream {
	private final DataOutputStream dout;

	public PacketOutputStream(OutputStream out) {
		this.dout = new DataOutputStream(out);
	}

	public void write(Packet packet) throws IOException {
		packet.send(dout);
	}
}
