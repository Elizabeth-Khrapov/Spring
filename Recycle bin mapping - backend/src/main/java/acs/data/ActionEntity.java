package acs.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="ACTIONS")
public class ActionEntity {
	private Long actionId;
	private String type;
	private Long elementId;
	private Date createdTimestamp;
	private String invokedBy;
	private String actionAttributes; 
	
	
	public ActionEntity() {
		
	}
		
	@Id
	public Long getActionId() {
		return actionId;
	}
	public void setActionId(Long actiontId) {
		this.actionId = actiontId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public  Long getElementId() {
		return elementId;
	}
	
	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}
	
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	
	public String getInvokedBy() {
		return invokedBy;
	}
	
	public void setInvokedBy(String invokedBy) {
		this.invokedBy = invokedBy;
	}
	
	@Lob
	public String getActionAttributes() {
		return actionAttributes;
	}
	
	public void setActionAttributes(String actionAttributes) {
		this.actionAttributes = actionAttributes;
	}	
}
