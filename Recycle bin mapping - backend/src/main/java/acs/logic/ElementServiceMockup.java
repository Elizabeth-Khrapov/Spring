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

import acs.boundaries.ElementBoundary;
import acs.data.ElementEntity;
import acs.data.EntityConverter;

//@Service
public class ElementServiceMockup implements ElementService {
	private Map<Long, ElementEntity> database;
	private EntityConverter entityConverter;
	private AtomicLong nextId;

	public ElementServiceMockup() {
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
	public ElementBoundary create(String managerEmail, ElementBoundary element) {
		Long newId = nextId.incrementAndGet();
		ElementEntity entity = this.entityConverter.elementToEntity(element);
		entity.setCreatedTimestamp(new Date());
		entity.setElementId(newId);
		entity.setCreatedBy(managerEmail);
		this.database.put(entity.getElementId(), entity);
		return this.entityConverter.convertElementFromEntity(entity);
	}

	@Override
	public ElementBoundary update(String managerEmail, String elementId, ElementBoundary update) {

		ElementBoundary existElement = this.getSpecificElement(managerEmail, elementId);
		boolean dirty = false;

		if (update.getType() != null) {
			existElement.setType(update.getType());
			dirty = true;
		}

		if (update.getName() != null) {
			existElement.setName(update.getName());
			dirty = true;
		}

		if (update.getActive() != null) {
			existElement.setActive(update.getActive());
			dirty = true;
		}

		if (update.getLocation() != null) {
			existElement.setLocation(update.getLocation());
			dirty = true;
		}

		if (update.getElementAttributes() != null) {
			existElement.setElementAttributes(update.getElementAttributes());
			dirty = true;
		}

		if (dirty) {
			this.database.put(this.entityConverter.toEntityId(elementId),
					this.entityConverter.elementToEntity(existElement));
		}

		return existElement;
	}

	@Override
	public List<ElementBoundary> getAll(String userEmail) {
		return this.database.values().stream().map(entity -> this.entityConverter.convertElementFromEntity(entity))
				.collect(Collectors.toList());
	}

	@Override
	public ElementBoundary getSpecificElement(String userEmail, String elementId) {
		ElementEntity entity = this.database.get(this.entityConverter.toEntityId(elementId));

		if (entity != null) {
			return this.entityConverter.convertElementFromEntity(entity);
		} else {
			throw new ObjectNotFoundException("could not find element for id: " + elementId);
		}
	}

	@Override
	public void deleteAllElements(String adminEmail) {
		this.database.clear();
	}
}
