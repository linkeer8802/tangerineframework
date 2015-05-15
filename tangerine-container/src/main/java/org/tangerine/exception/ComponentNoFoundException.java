package org.tangerine.exception;

public class ComponentNoFoundException extends RuntimeException {

	private static final long serialVersionUID = 6032269840806918826L;

	/**
     * Creates a new instance.
     */
    public ComponentNoFoundException() {
    }

    /**
     * Creates a new instance.
     */
    public ComponentNoFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance.
     */
    public ComponentNoFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new instance.
     */
    public ComponentNoFoundException(Throwable cause) {
        super(cause);
    }
}
