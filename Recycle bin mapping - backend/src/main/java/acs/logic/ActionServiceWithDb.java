package acs.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import acs.Location;
import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.dal.ActionDao;
import acs.dal.ElementDao;
import acs.dal.LastIdValue;
import acs.dal.LastValueDao;
import acs.dal.UserDao;
import acs.data.ActionEntity;
import acs.data.ElementEntity;
import acs.data.EntityConverter;
import acs.data.UserEntity;
import acs.data.UserRole;
import acs.helpers.ActionInputValidatorHelper;

@Service
public class ActionServiceWithDb implements EnhancedActionService{
	private ActionDao actionDao;
	private UserDao userDao;
	private ElementDao elementDao;
	private LastValueDao lastValueDao;
	private EntityConverter entityConverter;
	private ActionInputValidatorHelper inputValidator;
	private ObjectMapper jackson = new ObjectMapper(); 
	
	
	@Autowired
	public ActionServiceWithDb(ActionDao actionDao, ElementDao elementDao, UserDao userDao, LastValueDao lastValueDao,ActionInputValidatorHelper inputValidator) {
		System.err.println("Spring initialized me");
		this.actionDao = actionDao;
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
	public Object invokeAction(ActionBoundary action){
		inputValidator.validateActionInput(action);
		String userEmail = action.getInvokedBy().getUserEmail();
		Long elementId = this.entityConverter.toEntityId(action.getElement().getElementId());
		UserEntity userEntity = this.userDao.findById(userEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + userEmail));
		
		if(userEntity.getRole() != UserRole.PLAYER) {
			throw new ForbiddenRequestException("You don't have permission to do this");
		}
		
		ElementEntity elementEntity = this.elementDao.findById(elementId)
				.orElseThrow(() -> new ObjectNotFoundException("Could not find element for id: " + elementId));
		
		if(!elementEntity.getActive()) {
			throw new ObjectNotFoundException("Could not find an active element for id: " + elementId);
		}
		
		LastIdValue idValue = this.lastValueDao.save(new LastIdValue());
		
		ActionEntity actionEntity = this.entityConverter.actionToEntity(action);
		
		actionEntity.setActionId(idValue.getLastId());
		actionEntity.setCreatedTimestamp(new Date());
		this.lastValueDao.delete(idValue);// cleanup redundant data
		actionEntity = this.actionDao.save(actionEntity);

		if(actionEntity.getType().equals("Search")) {
			return serchByTypeLocationAndRadius(actionEntity);
		}
		return this.entityConverter.convertActionFromEntity(actionEntity);
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ActionBoundary> getAllActions(String adminEmail) {
		List<ActionBoundary> allActions = new ArrayList<>();
		Iterable<ActionEntity> allActionsFromDb = this.actionDao.findAll();
		for (ActionEntity action: allActionsFromDb) {
			allActions.add(this.entityConverter.convertActionFromEntity(action));
		}
		return allActions;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ActionBoundary> getAllActions(String adminEmail, int page, int size) {
		UserEntity userEntity = this.userDao.findById(adminEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + adminEmail));
		
		if(userEntity.getRole() != UserRole.ADMIN) {
			throw new ForbiddenRequestException("You don't have permission to do this");
		}
		return this.actionDao.findAll(PageRequest.of(page, size, Direction.ASC, "actionId"))
				.getContent()
				.stream()
				.map(this.entityConverter::convertActionFromEntity)
				.collect(Collectors.toList());	
	}

	@Override
	@Transactional
	public void deleteAllActions(String adminEmail) {
		UserEntity userEntity = this.userDao.findById(adminEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + adminEmail));
		
		if(userEntity.getRole() != UserRole.ADMIN) {
			throw new ForbiddenRequestException("You don't have permission to do this");
		}
		this.actionDao.deleteAll(); 
	}
	
	/*
Name: String
TypeFilters: String
Lat: double
lng: double
radious: double
PageSize: int
PageNum: int
*/
	@Transactional
	public List<ElementBoundary> serchByTypeLocationAndRadius(ActionEntity actionEntity){
		Map<String, Object> actionAttributs;
		try {
			actionAttributs = this.jackson.readValue(actionEntity.getActionAttributes(), Map.class);
			double lat = Double.parseDouble(searchInMap(actionAttributs, "lat"));
			double lng = Double.parseDouble(searchInMap(actionAttributs, "lng"));
			int radious = Integer.parseInt(searchInMap(actionAttributs, "radious"));
	//		Location location = new Location(lat,lng);
			Location minLocation = new Location(lat-radious ,  lng-radious);
			Location maxLocation = new Location(lat+radious ,  lng+radious);
			int size = Integer.parseInt(searchInMap(actionAttributs, "pageSize") == null ? "10" : searchInMap(actionAttributs, "pageSize"));
			int page = Integer.parseInt(searchInMap(actionAttributs, "pageNum") == null ? "0" : searchInMap(actionAttributs, "pageNum"));
			String type = searchInMap(actionAttributs, "type");
			String name = searchInMap(actionAttributs, "name");
			System.err.println(lat + " " + lng + " " + radious + " " + size + " " + page + " " + type + " " + name) ;
			//List<ElementBoundary> listElements = elementServiceWithDb.getElementsByType(actionEntity.getInvokedBy(),type, size, page);
			List<ElementBoundary> returnsListElements = elementDao.getAllByTypeAndNameAndLocation_lngBetweenAndLocation_latBetween
					(type ,name, lng-radious,lng+radious,lat-radious,lat+radious,
					PageRequest.of(page, size, Direction.ASC, "elementId"))
					.stream().map(this.entityConverter::convertElementFromEntity).collect(Collectors.toList());
			return returnsListElements;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(NumberFormatException e) {
			throw new InvalidInputException("Missing attributes for search, most provide: lat, lng and radious");
		}
		return null;
	}
	public String searchInMap(Map<String, Object> map,String search) {
		Object value  = map.get(search);
		if(value != null) {
			return value.toString();
		}
		return null;
	}
}
