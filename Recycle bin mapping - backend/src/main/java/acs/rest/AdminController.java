package acs.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.ActionBoundary;
import acs.boundaries.UserBoundary;
import acs.logic.EnhancedActionService;
import acs.logic.EnhancedElementService;
import acs.logic.EnhancedUserService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AdminController {
	private EnhancedUserService userService;
	private EnhancedElementService elementService;
	private EnhancedActionService actionService;
	 
	public AdminController(EnhancedUserService userService, EnhancedElementService elementService, EnhancedActionService actionService) {
		super();
		this.userService = userService;
		this.elementService = elementService;
		this.actionService = actionService;
	}

	// this method should return all users
	@RequestMapping(path = "/acs/admin/users/{adminEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] getAllUsers(@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name="page", required = false, defaultValue = "0") int page,
			@RequestParam(name="size", required = false, defaultValue = "10") int size) {
		return this.userService.getAllUsers(adminEmail,page,size).toArray(new UserBoundary[0]); 
	}
	
	// this method should delete all users
	@RequestMapping(path = "/acs/admin/users/{adminEmail}",
			method = RequestMethod.DELETE)
	public void deleteAllUsers(@PathVariable("adminEmail") String adminEmail) {
		this.userService.deleteAllUsers(adminEmail);
	}
	
	// this method should delete all elements
	@RequestMapping(path = "/acs/admin/elements/{adminEmail}",
			method = RequestMethod.DELETE)
	public void deleteAllElements(@PathVariable("adminEmail") String adminEmail) {
		this.elementService.deleteAllElements(adminEmail);
	}
	
	// this method should return all actions
	@RequestMapping(path = "/acs/admin/actions/{adminEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] getAllActions(@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name="page", required = false, defaultValue = "0") int page,
			@RequestParam(name="size", required = false, defaultValue = "10") int size) {
		return this.actionService.getAllActions(adminEmail, page, size).toArray(new ActionBoundary[0]);
	}
		
	// this method should delete all actions
	@RequestMapping(path = "/acs/admin/actions/{adminEmail}",
			method = RequestMethod.DELETE)
	public void deleteAllActions(@PathVariable("adminEmail") String adminEmail) {
		this.actionService.deleteAllActions(adminEmail);
	}	
}
