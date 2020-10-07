package acs.logic;

import java.util.List;

import acs.boundaries.ActionBoundary;

public interface EnhancedActionService extends ActionService{

	public List<ActionBoundary> getAllActions(String adminEmail, int page, int size);
	
}
