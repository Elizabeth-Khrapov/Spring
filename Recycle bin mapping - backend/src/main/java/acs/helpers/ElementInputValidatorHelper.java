package acs.helpers;

import acs.boundaries.ElementBoundary;
import acs.logic.InvalidInputException;

import org.springframework.stereotype.Service;

@Service
public class ElementInputValidatorHelper implements ElementInputValidator {

	@Override
	public void validateElementInput(ElementBoundary element) {
		if(element.getName() == null || element.getName() == "") {
			throw new InvalidInputException("Element's name can't be nothing");
		}
		
		if(element.getType() == null || element.getType() == "") {
			throw new InvalidInputException("Valid element's type is required");
		}	
	}
	
}
