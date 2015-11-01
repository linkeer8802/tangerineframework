package org.tangerine.util;

import java.nio.ByteBuffer;

public class ByteBufferUtil {

	private static final int DEFAULT_INITIAL_CAPACITY = 256;
	
	public static ByteBuffer buffer() {
		return ByteBuffer.allocate(DEFAULT_INITIAL_CAPACITY);
	}
}
