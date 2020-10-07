package acs.logic;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

import acs.boundaries.ActionBoundary;
import acs.data.ActionEntity;
import acs.data.EntityConverter;

//@Service
public class ActionServiceMockup implements ActionService{
	private Map<Long, ActionEntity> database;
	private EntityConverter entityConverter;
	private AtomicLong nextId;
	
	public ActionServiceMockup() {
		System.err.println("Spring initialized me"); 
	}
	
	@Autowired
	public void setEntityConverter(EntityConverter entityConverter) {
		this.entityConverter = entityConverter;
	}
	
	@PostConstruct
	public void init() {
		this.database = Collections.synchronizedMap(new TreeMap<>());
		this.nextId = new AtomicLong(0L);
	}

	@Override
	public Object invokeAction(ActionBoundary action) {
		Long newId = nextId.incrementAndGet();
		ActionEntity entity = this.entityConverter.actionToEntity(action);
		entity.setActionId(newId);
		entity.setInvokedBy(this.entityConverter.toEntityInovkedBy(action.getInvokedBy()));
		entity.setCreatedTimestamp(new Date());
		this.database.put(entity.getActionId(), entity);
		return this.entityConverter.convertActionFromEntity(entity);
	}

	@Override
	public List<ActionBoundary> getAllActions(String adminEmail) {
		return this.database.values().stream()
				.map(entity->this.entityConverter.convertActionFromEntity(entity))
				.collect(Collectors.toList());
	}

	@Override
	public void deleteAllActions(String adminEmail) {
		this.database.clear(); 
	}
	
}
