package acs.data;

import javax.annotation.PostConstruct;
import java.util.Map;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import acs.ElementInfo;
import acs.UserInfo;
import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.boundaries.UserTypeEnum;

@Component
public class EntityConverter {
	private ObjectMapper jackson;
	
	@PostConstruct
	public void setup() {
		this.jackson = new ObjectMapper(); 
	}
	
	public UserBoundary convertUserFromEntity(UserEntity userEntity) {
		UserBoundary boundary = new UserBoundary();
		boundary.setEmail(userEntity.getEmail());
		boundary.setRole(UserTypeEnum.valueOf(userEntity.getRole().name()));
		boundary.setUsername(userEntity.getUsername());
		boundary.setAvatar(userEntity.getAvatar());
		return boundary;
	}
	
	public UserEntity userToEntity(UserBoundary userBoundary) {
		UserEntity entity = new UserEntity();
		entity.setEmail(userBoundary.getEmail());
		entity.setRole(UserRole.valueOf(userBoundary.getRole().name()));
		entity.setUsername(userBoundary.getUsername());
		entity.setAvatar(userBoundary.getAvatar());
		return entity;
	}
	
	public ElementBoundary convertElementFromEntity(ElementEntity elementEntity) {
		ElementBoundary boundary = new ElementBoundary();
		boundary.setCreatedTimestamp(elementEntity.getCreatedTimestamp());
		boundary.setElementId(this.fromEntityId(elementEntity.getElementId()));
		boundary.setName(elementEntity.getName());
		boundary.setCreatedBy(this.fromEntityInvokedBy(elementEntity.getCreatedBy()));
		boundary.setLocation(elementEntity.getLocation());
		boundary.setActive(elementEntity.getActive());
		boundary.setType(elementEntity.getType());
		
		try {
			boundary.setElementAttributes(this.jackson.readValue(elementEntity.getElementAttributes(), Map.class));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return boundary;
	}
	
	public ElementEntity elementToEntity(ElementBoundary elementBoundary) {
		ElementEntity entity = new ElementEntity();
		entity.setName(elementBoundary.getName());
		entity.setLocation(elementBoundary.getLocation() != null ? elementBoundary.getLocation(): null);
		entity.setActive(elementBoundary.getActive() != null ? elementBoundary.getActive(): false);
		entity.setType(elementBoundary.getType());
		
		try {
			entity.setElementAttributes(this.jackson.writeValueAsString(elementBoundary.getElementAttributes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return entity;
	}

	public ActionBoundary convertActionFromEntity(ActionEntity actionEntity) {
		ActionBoundary boundary = new ActionBoundary();
		boundary.setCreatedTimestamp(actionEntity.getCreatedTimestamp());
		boundary.setActionId(this.fromEntityId(actionEntity.getActionId()));
		boundary.setInvokedBy(this.fromEntityInvokedBy(actionEntity.getInvokedBy()));
		boundary.setElement(this.fromEntityElementId(this.fromEntityId(actionEntity.getElementId())));
		boundary.setType(actionEntity.getType());
		
		try {
			boundary.setActionAttributes(this.jackson.readValue(actionEntity.getActionAttributes(), Map.class));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return boundary;
	}
	
	public ActionEntity actionToEntity(ActionBoundary actionBoundary) {
		ActionEntity entity = new ActionEntity();
		entity.setInvokedBy(this.toEntityInovkedBy(actionBoundary.getInvokedBy()));
		entity.setElementId(this.toEntityId(this.toEntityElementId(actionBoundary.getElement())));
		entity.setType(actionBoundary.getType());
		
		try {
			entity.setActionAttributes(this.jackson.writeValueAsString(actionBoundary.getActionAttributes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return entity;
	}
	
	public String toEntityEmail(String email) { 
		return email != null ? email: null;
	}
	
	public Long toEntityId(String id) {
		return id != null ? Long.parseLong(id): null;
	}

	public String fromEntityId(Long id) {
		return id != null ? id.toString(): null;

	}
	
	public String toEntityInovkedBy(UserInfo user) {
		return user != null ? user.getUserEmail() : null; 
	}
	
	public UserInfo fromEntityInvokedBy(String userEmail) {
		return userEmail != null ? new UserInfo(userEmail) : null;
	}
	
	public String toEntityElementId(ElementInfo element) {
		return element != null ? element.getElementId() : null;
	}
	
	public ElementInfo fromEntityElementId(String elementId) {
		return elementId != null ? new ElementInfo(elementId) : null;
	}
}
