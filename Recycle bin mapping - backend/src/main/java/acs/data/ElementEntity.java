package acs.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acs.Location;


@Entity
@Table(name="ELEMENTS")
public class ElementEntity {
	private Long elementId;
	private String type;
	private String name;
	private Boolean active;
	private Date createdTimestamp; 
	private String createdBy;
	private Location location;
	private String elementAttributes;
	private Set<ElementEntity> childElements;
	private ElementEntity parentElement;
	
	public ElementEntity() {
		this.childElements = new HashSet<>();
	}
		
	@Id
	public Long getElementId() {
		return elementId;
	}
	
	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}
	
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	@Embedded
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@Lob
	public String getElementAttributes() {
		return elementAttributes;
	}
	
	public void setElementAttributes(String elementAttributes) {
		this.elementAttributes = elementAttributes;
	}
	
	@OneToMany(mappedBy = "parentElement", fetch = FetchType.LAZY)
	public Set<ElementEntity> getChildrenElements() {
		return childElements;
	}

	public void setChildrenElements(Set<ElementEntity> childElements) {
		this.childElements = childElements;
	}
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	public ElementEntity getParentElement() {
		return parentElement;
	}

	public void setParentElement(ElementEntity parentElement) {
		this.parentElement = parentElement;
	}
	
	public void addChildElement (ElementEntity childElement) {
		this.childElements.add(childElement);
		childElement.setParentElement(this);
	}		
}