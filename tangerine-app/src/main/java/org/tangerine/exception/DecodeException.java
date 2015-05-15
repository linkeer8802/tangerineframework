package org.tangerine.exception;

public class DecodeException extends RuntimeException {

    private static final long serialVersionUID = 6926716840699621852L;

    /**
     * Creates a new instance.
     */
    public DecodeException() {
    }

    /**
     * Creates a new instance.
     */
    public DecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance.
     */
    public DecodeException(String message) {
        super(message);
    }

    /**
     * Creates a new instance.
     */
    public DecodeException(Throwable cause) {
        super(cause);
    }
}
