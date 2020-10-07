package acs.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementIdBoundary;
import acs.logic.EnhancedElementService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ElementController {
	private EnhancedElementService enhancedElementService;
	
	public ElementController(EnhancedElementService elementService) { 
		super();
		this.enhancedElementService = elementService;
	}

	// this method should return all elements of a specific user
	@RequestMapping(path = "/acs/elements/{userEmail}", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getList(
			@PathVariable("userEmail") String userEmail, 
			@RequestParam(name="page", required = false, defaultValue = "0") int page,
			@RequestParam(name="size", required = false, defaultValue = "10") int size) {
		return this.enhancedElementService.getAll(userEmail, page, size).toArray(new ElementBoundary[0]);
	}
	
	// this method should return a specific element by its id
	@RequestMapping(path = "/acs/elements/{userEmail}/{elementId}", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary getById(@PathVariable("userEmail") String userEmail,
			@PathVariable("elementId") String elementId) {
		return this.enhancedElementService.getSpecificElement(userEmail, elementId);
	}

	// this method should create a new element
	@RequestMapping(path = "/acs/elements/{managerEmail}", 
			method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary createElement(@PathVariable("managerEmail") String managerEmail,
			@RequestBody ElementBoundary element) {
		return this.enhancedElementService.create(managerEmail, element);
	}
	
	//this method should update a specific element by its id 
	@RequestMapping(path = "/acs/elements/{managerEmail}/{elementId}", 
			method = RequestMethod.PUT, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateById(@PathVariable("managerEmail") String managerEmail,
			@PathVariable("elementId") String elementId, @RequestBody ElementBoundary element) {
		this.enhancedElementService.update(managerEmail, elementId, element);
	}
	
	@RequestMapping(path = "/acs/elements/{managerEmail}/{parentElementId}/children",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void addChildElementToParentElement(@PathVariable("managerEmail") String managerEmail,
			@PathVariable("parentElementId") String parentElementId, 
			@RequestBody ElementIdBoundary elementId){ 
		this.enhancedElementService
			.addChildElementToParentElement(managerEmail,parentElementId, elementId.getId());
	}
	
	@RequestMapping(path = "/acs/elements/{userEmail}/{parentElementId}/children",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllChildElements (@PathVariable("userEmail") String managerEmail,
			@PathVariable("parentElementId") String parentElementId,
			@RequestParam(name="page", required = false, defaultValue = "0") int page,
			@RequestParam(name="size", required = false, defaultValue = "10") int size) {
		return this.enhancedElementService
			.getChildrenElements(managerEmail,parentElementId,page,size)
			.toArray(new ElementBoundary[0]); // Java Reflection
	}
	
	@RequestMapping(path = "/acs/elements/{userEmail}/{childElementId}/parents",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getParentsElements (@PathVariable("userEmail") String managerEmail,
			@PathVariable("childElementId") String childElementId) { 
		return this.enhancedElementService
			.getParentsElements(managerEmail,childElementId)
			.toArray(new ElementBoundary[0]); // Java Reflection
	}
	
	@RequestMapping(path = "/acs/elements/{userEmail}/search/byName/{name}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getElementsByName (@PathVariable("userEmail") String userEmail,
			@PathVariable("name") String elementName,
			@RequestParam(name="page", required = false, defaultValue = "0") int page,
			@RequestParam(name="size", required = false, defaultValue = "10") int size) {
		return this.enhancedElementService.getElementsByName(userEmail, elementName, page, size).toArray(new ElementBoundary[0]);	
	}
	
	@RequestMapping(path = "/acs/elements/{userEmail}/search/byType/{type}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getElementsByType (@PathVariable("userEmail") String userEmail,
			@PathVariable("type") String elementType,
			@RequestParam(name="page", required = false, defaultValue = "0") int page,
			@RequestParam(name="size", required = false, defaultValue = "10") int size) {
		return this.enhancedElementService.getElementsByType(userEmail, elementType, page, size).toArray(new ElementBoundary[0]);	
	}
	
}
