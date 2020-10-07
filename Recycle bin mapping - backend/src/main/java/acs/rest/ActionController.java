package acs.rest; 

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.ActionBoundary;
import acs.logic.EnhancedActionService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ActionController {
	private EnhancedActionService actionService; 
	
	public ActionController(EnhancedActionService actionService) {
		super();
		this.actionService = actionService;
	}
	
	// this method should invoke an action

	
	@RequestMapping(path = "/acs/actions", 
			method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeAction(@RequestBody ActionBoundary action) {
		return actionService.invokeAction(action);
	}
}
