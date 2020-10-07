package acs.helpers;
import acs.boundaries.ActionBoundary;
import acs.logic.InvalidInputException;
import org.springframework.stereotype.Service;

@Service
public class ActionInputValidatorHelper implements ActionInputValidator {

	@Override
	public void validateActionInput(ActionBoundary action) {
		if(action.getType() == null || action.getType() == "") {
			throw new InvalidInputException("Valid action's type is required");
		}
		
		if(action.getInvokedBy() == null || action.getInvokedBy().getUserEmail() == "") {
			throw new InvalidInputException("Action must have someone who invokes it");
		}
		
		if(action.getElement() == null || action.getElement().getElementId() == null) {
			throw new InvalidInputException("Action must have an element on which it is invoked");
		}
	}
	
}
