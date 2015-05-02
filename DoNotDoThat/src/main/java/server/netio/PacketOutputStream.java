package server.netio;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PacketOutputStream {
	private final DataOutputStream dout;

	public PacketOutputStream(OutputStream out) throws IOException {
		this.dout = new DataOutputStream(out);
		dout.writeInt(0xD007D074);
	}

	public void write(Packet packet) throws IOException {
		packet.send(dout);
	}

	public void close() throws IOException {
		dout.close();
	}
}
