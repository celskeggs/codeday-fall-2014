package server.netio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet {
	public int type;
	public byte[] data;

	public void send(DataOutputStream out) throws IOException {
		out.writeInt(data.length);
		if (type != (type & 0xFFFF)) {
			throw new IllegalStateException("Type cannot be expressed in 16 bits!");
		}
		out.writeShort(type);
		out.write(data);
	}

	public void recv(DataInputStream in) throws IOException {
		data = new byte[in.readInt()];
		type = in.readShort() & 0xFFFF;
		in.readFully(data);
	}
}
