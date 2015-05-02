package server.data;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import server.logger.Logger;

public class Encoder {

	public static void encode(Object object, ByteBuffer enc) {
		if (object == null) {
			enc.put((byte) 0x00);
			return;
		}
		if (object.getClass().isArray()) {
			enc.put((byte) 0x03);
			enc.putInt(Array.getLength(object));
			for (int i = 0; i < Array.getLength(object); i++) {
				encode(Array.get(object, i), enc);
			}
			return;
		}
		switch (object.getClass().getName()) {
		case "java.lang.Integer":
			enc.put((byte) 0x01);
			enc.putInt((Integer) object);
			break;
		case "java.lang.Boolean":
			enc.put((byte) 0x02);
			enc.put((byte) (((Boolean) object).booleanValue() ? 1 : 0));
			break;
		case "java.lang.String":
			enc.put((byte) 0x04);
			String local = (String) object;
			enc.putInt(local.length());
			enc.put(local.getBytes());
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
		case 0x02:
			return buf.get() != 0;
		case 0x03:
			Object[] contents = new Object[buf.getInt()];
			for (int i = 0; i < contents.length; i++) {
				contents[i] = decode(buf);
			}
			return contents;
		case 0x04:
			byte[] data = new byte[buf.getInt()];
			buf.get(data);
			return new String(data);
		default:
			Logger.severe("Unhandlable typeid: " + id);
			return null;
		}
	}
}
