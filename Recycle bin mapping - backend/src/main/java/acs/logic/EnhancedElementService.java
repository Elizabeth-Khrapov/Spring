package acs.logic;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import acs.boundaries.ElementBoundary;

public interface EnhancedElementService extends ElementService {
	
	public void addChildElementToParentElement(String managerEmail, String parentElementId, String childElementId);

	public Set<ElementBoundary> getChildrenElements(String managerEmail,String parentElementId);

	public Collection<ElementBoundary> getParentsElements(String managerEmail, String childElementId);

	public List<ElementBoundary> getAll(String userEmail, int page, int size);

	public List<ElementBoundary> getChildrenElements(String managerEmail, String parentElementId, int page, int size);
	
	public List<ElementBoundary> getElementsByName(String userEmail, String name, int page, int size);
	
	public List<ElementBoundary> getElementsByType(String userEmail, String type, int page, int size);

}
