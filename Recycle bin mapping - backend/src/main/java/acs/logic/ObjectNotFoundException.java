package acs.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND)
public class ObjectNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2083249184293995471L;

	public ObjectNotFoundException() {
		super();
	}

	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectNotFoundException(String message) { 
		super(message);
	}

	public ObjectNotFoundException(Throwable cause) {
		super(cause);
	}	
}
