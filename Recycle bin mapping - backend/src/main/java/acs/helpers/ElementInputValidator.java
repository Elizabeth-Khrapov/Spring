package acs.helpers;

import acs.boundaries.ElementBoundary;

public interface ElementInputValidator extends InputValidator {
	
	public void validateElementInput(ElementBoundary element);
}
