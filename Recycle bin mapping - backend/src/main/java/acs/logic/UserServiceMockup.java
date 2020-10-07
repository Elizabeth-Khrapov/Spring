package acs.logic;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

import acs.boundaries.UserBoundary;
import acs.data.EntityConverter;
import acs.data.UserEntity;

//@Service
public class UserServiceMockup implements UserService {
	private Map<String, UserEntity> database;
	private EntityConverter entityConverter;
	
	public UserServiceMockup() {
		System.err.println("Spring initialized me"); 
	}
	
	@Autowired
	public void setEntityConverter(EntityConverter entityConverter) {
		this.entityConverter = entityConverter;
	}
	
	@PostConstruct
	public void init() {
		this.database = Collections.synchronizedMap(new TreeMap<>());
	}

	@Override
	public UserBoundary createUser(UserBoundary user) {
		UserEntity entity = this.entityConverter.userToEntity(user);
		entity.setEmail(user.getEmail());
		this.database.put(entity.getEmail(), entity);
		return this.entityConverter.convertUserFromEntity(entity);
	}

	@Override
	public UserBoundary login(String userEmail) {
		UserEntity entity = this.database.get(this.entityConverter.toEntityEmail(userEmail));
		if(entity == null) {
			throw new ObjectNotFoundException("User doesn't exists");
		}
		return this.entityConverter.convertUserFromEntity(entity);
	}

	@Override
	public UserBoundary updateUser(String userEmail, UserBoundary update) {
		UserBoundary existUser = null;
		boolean dirty = false;
		UserEntity existEntity = this.database.get(this.entityConverter.toEntityEmail(userEmail));
		
		if(existEntity != null) {
			existUser = this.entityConverter.convertUserFromEntity(existEntity);
		}
		
		if(userEmail.equalsIgnoreCase(update.getEmail())) {
			
			if(update.getRole() != null) {
				existUser.setRole(update.getRole());
				dirty = true;
			}
			
			if(update.getUsername() != null) {
				existUser.setUsername(update.getUsername());
				dirty = true;
			}
			
			if(update.getAvatar() != null) {
				existUser.setAvatar(update.getAvatar());
				dirty = true;
			}
			
			if(dirty) {
				this.database.put(this.entityConverter.toEntityEmail(userEmail), this.entityConverter.userToEntity(existUser));
			}
		}
		return existUser;
	}

	@Override
	public List<UserBoundary> getAllUsers(String adminEmail) {
		return this.database.values().stream()
				.map(entity->this.entityConverter.convertUserFromEntity(entity))
				.collect(Collectors.toList());
	}

	@Override
	public void deleteAllUsers(String adminEmail) {
		this.database.clear(); 
	}
	
}
