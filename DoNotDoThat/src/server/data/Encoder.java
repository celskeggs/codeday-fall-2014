package server.data;

import java.nio.ByteBuffer;

import server.logger.Logger;

public class Encoder {

	public static void encode(Object object, ByteBuffer enc) {
		if (object == null) {
			enc.put((byte) 0x00);
			return;
		}
		switch (object.getClass().getName()) {
		case "java.lang.Integer":
			enc.put((byte) 0x01);
			enc.putInt((Integer) object); 
			break;
		default:
			enc.put((byte) 0x00);
			Logger.severe("Unhandlable object type: " + object.getClass());
			break;
		}
	}

	public static Object decode(ByteBuffer buf) {
		int id = buf.get() & 0xFF;
		switch (id) {
		case 0x00:
			return null;
		case 0x01:
			return (Integer) buf.getInt();
		default:
			Logger.severe("Unhandlable typeid: " + id);
			return null;
		}
	}
}
