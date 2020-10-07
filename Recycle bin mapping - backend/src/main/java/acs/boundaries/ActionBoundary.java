package acs.boundaries;

import java.util.Date;
import java.util.Map;

import acs.ElementInfo;
import acs.UserInfo;

public class ActionBoundary {
	private String actionId;
	private String type;
	private ElementInfo element; 
	private Date createdTimestamp; 
	private UserInfo invokedBy;
	private Map<String, Object> actionAttributes; 
	

	public ActionBoundary() {
	}
	
	public ActionBoundary(String actiontId, String type, ElementInfo element, Date createdTimestamp, UserInfo invokedBy,
			Map<String, Object> actionAttributes) {
		super();
		this.actionId = actiontId;
		this.type = type; 
		this.element = element;
		this.createdTimestamp = createdTimestamp;
		this.invokedBy = invokedBy;
		this.actionAttributes = actionAttributes;
	}
	
	public String getActionId() {
		return actionId;
	}
	
	public void setActionId(String actiontId) {
		this.actionId = actiontId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ElementInfo getElement() {
		return element;
	}
	
	public void setElement(ElementInfo elementId) {
		this.element = elementId;
	}
	
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}
	
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	
	public UserInfo getInvokedBy() {
		return invokedBy;
	}
	
	public void setInvokedBy(UserInfo invokedBy) {
		this.invokedBy = invokedBy;
	}
	
	public Map<String, Object> getActionAttributes() {
		return actionAttributes;
	}
	
	public void setActionAttributes(Map<String, Object> actionAttributes) {
		this.actionAttributes = actionAttributes;
	}	
}