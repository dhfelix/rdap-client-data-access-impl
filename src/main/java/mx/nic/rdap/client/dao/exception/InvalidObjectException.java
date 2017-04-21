package mx.nic.rdap.client.dao.exception;

import java.sql.SQLException;

public class InvalidObjectException extends SQLException {

	/** */
	private static final long serialVersionUID = 1L;

	public InvalidObjectException(String message) {
		super(message);
	}

	public InvalidObjectException(String message, Throwable cause) {
		super(message, cause);
	}

}
