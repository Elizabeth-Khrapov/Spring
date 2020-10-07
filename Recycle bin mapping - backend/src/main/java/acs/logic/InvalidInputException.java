package acs.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidInputException extends RuntimeException {
	private static final long serialVersionUID = 5646688807108842781L;

	public InvalidInputException() {
		super();
	}

	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidInputException(String message) { 
		super(message);
	}

	public InvalidInputException(Throwable cause) {
		super(cause);
	}	
}
