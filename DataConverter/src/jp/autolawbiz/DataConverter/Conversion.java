package jp.autolawbiz.DataConverter;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Conversion {
	protected static void skipReadByte (int counts, DataInputStream recdis) {
		for (int i = 0; i < counts; i++) {
			try {
				recdis.readByte();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected static void skipReadInt (int counts, DataInputStream recdis) {
		for (int i = 0; i < counts; i++) {
			try {
				recdis.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected static byte[] short2Byte (short value) {
		return ByteBuffer.allocate (Short.SIZE/Byte.SIZE).putShort (value).array ();
	}

	protected static byte[] int2Byte (int value) {
		return ByteBuffer.allocate (Integer.SIZE/Byte.SIZE).putInt (value).array ();
	}

	protected static byte[] double2Byte (double value) {
		return ByteBuffer.allocate (Double.SIZE/Byte.SIZE).putDouble (value).array ();
	}

	protected static byte[] reverse2ByteShort (byte[] recbytes) {
		byte[] bytes = {recbytes[1], recbytes[0]};
		return bytes;
	}

	protected static byte[] reverse4ByteInt (byte[] recbytes) {
		byte[] bytes = {recbytes[3], recbytes[2], recbytes[1], recbytes[0]};
		return bytes;
	}

	protected static byte[] reverse8ByteDouble (byte[] recbytes) {
		byte[] bytes = {recbytes[7], recbytes[6], recbytes[5], recbytes[4], recbytes[3], recbytes[2], recbytes[1], recbytes[0]};
		return bytes;
	}

	protected static int byte2Int (byte[] recbytes) {
		return ByteBuffer.wrap(recbytes).getInt();
	}

	protected static short byte2Short (byte[] recbytes) {
		return ByteBuffer.wrap(recbytes).getShort();
	}

	protected static double byte2Double (byte[] recbytes) {
		return ByteBuffer.wrap(recbytes).getDouble();
	}
}
