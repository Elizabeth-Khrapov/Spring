package acs.helpers;

import acs.boundaries.UserBoundary;

public interface UserInputValidator extends InputValidator {

	public void validateUserInput(UserBoundary user);
}
