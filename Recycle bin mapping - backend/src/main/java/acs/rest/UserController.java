package acs.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.UserBoundary;
import acs.logic.EnhancedUserService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {
	private EnhancedUserService userService;
	
	public UserController(EnhancedUserService userService) {
		super();
		this.userService = userService;
	}
	
	// this method should create a new user
	@RequestMapping(path = "/acs/users",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createUser(@RequestBody UserBoundary newUser) {
		return this.userService.createUser(newUser);
	}

	// this method should return a specific user by his email
	@RequestMapping(path = "/acs/users/login/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary getUser(@PathVariable("userEmail") String userEmail) {
		return this.userService.login(userEmail); 
	}


	//this method should update a specific user by his email
	@RequestMapping(path = "/acs/users/{userEmail}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE) 
	public void updateUser(@RequestBody UserBoundary user, @PathVariable("userEmail") String userEmail) {
		this.userService.updateUser(userEmail, user);
	}	
}
