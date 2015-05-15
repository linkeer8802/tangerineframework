package org.tangerine.exception;

public class PacketHandleException extends RuntimeException {

	private static final long serialVersionUID = -1440586061634478604L;

	/**
     * Creates a new instance.
     */
    public PacketHandleException() {
    }

    /**
     * Creates a new instance.
     */
    public PacketHandleException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance.
     */
    public PacketHandleException(String message) {
        super(message);
    }

    /**
     * Creates a new instance.
     */
    public PacketHandleException(Throwable cause) {
        super(cause);
    }
}
