package acs.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import acs.boundaries.ElementBoundary;
import acs.dal.ElementDao;
import acs.dal.LastIdValue;
import acs.dal.LastValueDao;
import acs.dal.UserDao;
import acs.data.ElementEntity;
import acs.data.EntityConverter;
import acs.data.UserEntity;
import acs.data.UserRole;
import acs.helpers.ElementInputValidatorHelper;

@Service
public class ElementServiceWithDb implements EnhancedElementService {
	private ElementDao elementDao;
	private UserDao userDao;
	private EntityConverter entityConverter;
	private LastValueDao lastValueDao;
	private ElementInputValidatorHelper inputValidator;

	@Autowired
	public ElementServiceWithDb(ElementDao elementDao,UserDao userDao, LastValueDao lastValueDao,
			ElementInputValidatorHelper inputValidator) {
		System.err.println("spring initialized me");
		this.elementDao = elementDao;
		this.userDao = userDao;
		this.lastValueDao = lastValueDao;
		this.inputValidator = inputValidator;
	}

	@Autowired
	public void setEntityConverter(EntityConverter entityConverter) {
		this.entityConverter = entityConverter;
	}

	@Override
	@Transactional
	public ElementBoundary create(String managerEmail, ElementBoundary element) {
		UserEntity userEntity = this.userDao.findById(managerEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + managerEmail));

		if(userEntity.getRole() != UserRole.MANAGER) {
			throw new ForbiddenRequestException("You don't have permission to do this");
		}

		LastIdValue idValue = this.lastValueDao.save(new LastIdValue());
		inputValidator.validateElementInput(element);
		ElementEntity elementEntity = this.entityConverter.elementToEntity(element);		
		elementEntity.setCreatedTimestamp(new Date());
		elementEntity.setElementId(idValue.getLastId());
		elementEntity.setCreatedBy(managerEmail);
		this.lastValueDao.delete(idValue);// cleanup redundant data

		elementEntity = this.elementDao.save(elementEntity);

		return this.entityConverter.convertElementFromEntity(elementEntity);
	}

	@Override
	@Transactional
	public ElementBoundary update(String managerEmail, String elementId, ElementBoundary update) {
		UserEntity userEntity = this.userDao.findById(managerEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + managerEmail));
		
		if(userEntity.getRole() != UserRole.MANAGER) {
			throw new ForbiddenRequestException("You don't have permission to do this");
		}
		
		ElementBoundary existElement = this.getSpecificElement(managerEmail, elementId);
		inputValidator.validateElementInput(update);
		ElementEntity elementEntity = this.entityConverter.elementToEntity(update);
		elementEntity.setCreatedTimestamp(existElement.getCreatedTimestamp());
		elementEntity.setElementId(this.entityConverter.toEntityId(existElement.getElementId()));
		elementEntity.setCreatedBy(managerEmail);
		this.elementDao.save(elementEntity);

		return update;

	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAll(String userEmail) {
		List<ElementBoundary> allElements = new ArrayList<>();
		Iterable<ElementEntity> allElementsFromDb = this.elementDao.findAll();
		for (ElementEntity element : allElementsFromDb) {
			allElements.add(this.entityConverter.convertElementFromEntity(element));
		}
		return allElements;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAll(String userEmail, int page, int size) {
		UserEntity userEntity = this.userDao.findById(userEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + userEmail));
		
		List<ElementBoundary> allElements = this.elementDao.findAll(PageRequest.of(page, size, Direction.ASC, "elementId")).getContent().stream()
				.map(this.entityConverter::convertElementFromEntity).collect(Collectors.toList());
		 
		List<ElementBoundary> activeElements = allElements.stream().filter(element -> element.getActive()).collect(Collectors.toList());
		
		if(userEntity.getRole() == UserRole.MANAGER) {
			return allElements;
		}
		
		if(userEntity.getRole() == UserRole.PLAYER) {
			return activeElements;
		}
		
		throw new ForbiddenRequestException("You don't have permission to do this");

	}

	@Override
	@Transactional(readOnly = true)
	public ElementBoundary getSpecificElement(String userEmail, String elementId) {
		UserEntity userEntity = this.userDao.findById(userEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + userEmail));
		
		ElementEntity elementEntity = this.elementDao.findById(this.entityConverter.toEntityId(elementId))
				.orElseThrow(() -> new ObjectNotFoundException("Could not find element for id: " + elementId));
				
		ElementBoundary elementBoundary = this.entityConverter.convertElementFromEntity(elementEntity);
		
		switch(userEntity.getRole()){
			case MANAGER:
				return elementBoundary;
				
			case PLAYER:
				if(!elementEntity.getActive()) {
					throw new ObjectNotFoundException("Could not find element for id: " + elementId);
				}
				return elementBoundary;
				
			default:
				throw new ForbiddenRequestException("You don't have permission to do this");
		}		
	}

	@Override
	@Transactional
	public void deleteAllElements(String adminEmail) {
		UserEntity userEntity = this.userDao.findById(adminEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + adminEmail));
		
		if(userEntity.getRole() != UserRole.ADMIN) {
			throw new ForbiddenRequestException("You don't have permission to do this");
		}
		this.elementDao.deleteAll();
	}

	@Override
	@Transactional
	public void addChildElementToParentElement(String managerEmail, String parentElementId, String childElemnetId) {
		if (parentElementId != null && parentElementId.equals(childElemnetId)) {
			throw new InvalidInputException("Parent element and child element can't be the same element");
		}

		ElementEntity parentElement = this.elementDao.findById(this.entityConverter.toEntityId(parentElementId))
				.orElseThrow(() -> new ObjectNotFoundException("Could not find element for id: " + parentElementId));

		ElementEntity childElement = this.elementDao.findById(this.entityConverter.toEntityId(childElemnetId))
				.orElseThrow(() -> new ObjectNotFoundException("Could not find element for id: " + childElemnetId));

		parentElement.addChildElement(childElement);
		this.elementDao.save(parentElement);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<ElementBoundary> getChildrenElements(String managerEmail, String parentElementId) {
		ElementEntity parentElement = this.elementDao.findById(this.entityConverter.toEntityId(parentElementId))
				.orElseThrow(() -> new ObjectNotFoundException("Could not find element for id: " + parentElementId));

		return parentElement.getChildrenElements().stream().map(this.entityConverter::convertElementFromEntity)
				.collect(Collectors.toSet());
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ElementBoundary> getParentsElements(String managerEmail, String childElemnetId) {
		ElementEntity childElement = this.elementDao.findById(this.entityConverter.toEntityId(childElemnetId))
				.orElseThrow(() -> new ObjectNotFoundException("Could not find element for id: " + childElemnetId));

		ElementEntity parentElement = childElement.getParentElement();
		Set<ElementBoundary> parentsElementsSet = new HashSet<>();
		if (parentElement != null) {
			parentsElementsSet.add(this.entityConverter.convertElementFromEntity(parentElement));
		}
		return parentsElementsSet;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getChildrenElements(String managerEmail, String parentElementId, int page, int size) {
		UserEntity userEntity = this.userDao.findById(managerEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + managerEmail));
		
		this.elementDao.findById(this.entityConverter.toEntityId(parentElementId))
				.orElseThrow(() -> new ObjectNotFoundException("Could not find element for id: " + parentElementId));
		
		List<ElementBoundary> allChildrenElements = this.elementDao
				.findAllByParentElement_elementId(this.entityConverter.toEntityId(parentElementId),
						PageRequest.of(page, size, Direction.ASC, "elementId"))
				.stream().map(this.entityConverter::convertElementFromEntity).collect(Collectors.toList());
				
		List<ElementBoundary> activeChildrenElements = allChildrenElements.stream().filter(element -> element.getActive()).collect(Collectors.toList());
		
		if(userEntity.getRole() == UserRole.MANAGER) {
			return allChildrenElements;
		}
		
		if(userEntity.getRole() == UserRole.PLAYER) {
			return activeChildrenElements;
		}
		
		throw new ForbiddenRequestException("You don't have permission to do this");
	}

	@Override
	public List<ElementBoundary> getElementsByName(String userEmail, String name, int page, int size) {
		UserEntity userEntity = this.userDao.findById(userEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + userEmail));
		
		List<ElementBoundary> allElementsByName =  this.elementDao.findAllByName(name, PageRequest.of(page, size, Direction.ASC,"elementId"))
				.stream().map(this.entityConverter::convertElementFromEntity).collect(Collectors.toList());
		
		List<ElementBoundary> activeElementsByName = allElementsByName.stream().filter(element -> element.getActive()).collect(Collectors.toList());
		
		if(userEntity.getRole() == UserRole.MANAGER) {
			return allElementsByName;
		}
		
		if(userEntity.getRole() == UserRole.PLAYER) {
			return activeElementsByName;
		}
		
		throw new ForbiddenRequestException("You don't have permission to do this");
	}

	@Override
	public List<ElementBoundary> getElementsByType(String userEmail, String type, int page, int size) {
		return this.elementDao.findAllByType(type, PageRequest.of(page, size, Direction.ASC, "elementId"))
				.stream().map(this.entityConverter::convertElementFromEntity).collect(Collectors.toList());
	}
	
	
}
