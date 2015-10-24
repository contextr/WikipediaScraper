package io.contextr.exception;

/**
 * Exception thrown when payload cannot be parsed as JSON.
 * 
 * @author kyle
 *
 */
public class MissingExtractException extends RuntimeException {

	private String message;
	
	public MissingExtractException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}