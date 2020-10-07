package acs.helpers;

import acs.boundaries.ActionBoundary;

public interface ActionInputValidator extends InputValidator {
	
	public void validateActionInput(ActionBoundary action);
}
