package acs.helpers;

import org.springframework.stereotype.Service;

import acs.boundaries.UserBoundary;
import acs.boundaries.UserTypeEnum;
import acs.logic.InvalidInputException;

@Service
public class UserInputValidatorHelper implements UserInputValidator {

	@Override
	public void validateUserInput(UserBoundary user) {
		if(!IsValidEmail(user.getEmail())) {
			throw new InvalidInputException("Email address isn't valid");
		}
		
		if(!IsEnumContainsRole(user.getRole())) {
			throw new InvalidInputException("There is no such role");
		}
		
		if(user.getUsername() == null || user.getUsername().equals("")) {
			throw new InvalidInputException("Username must contain at least one character");
		}
		
		if(user.getAvatar() == null || user.getAvatar().equals("")) {
			throw new InvalidInputException("You must enter your avatar");
		}	
	}
	
	public boolean IsValidEmail(String email) {
		return email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");
	}
	
	public boolean IsEnumContainsRole(UserTypeEnum role) {
		if(role == null) {
			return false;
		}
		for (UserTypeEnum userRole: UserTypeEnum.values()) {
			if(userRole.name().equalsIgnoreCase(role.name())) {
				return true;
			}
		}
		return false;
	}

}
