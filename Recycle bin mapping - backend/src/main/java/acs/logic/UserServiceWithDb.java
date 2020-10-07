package acs.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import acs.boundaries.UserBoundary;
import acs.dal.UserDao;
import acs.data.EntityConverter;
import acs.data.UserEntity;
import acs.data.UserRole;
import acs.helpers.UserInputValidatorHelper;

@Service
public class UserServiceWithDb implements EnhancedUserService {
	private UserDao userDao;
	private EntityConverter entityConverter;
	private UserInputValidatorHelper inputValidator;
	
	@Autowired
	public UserServiceWithDb(UserDao userDao,UserInputValidatorHelper inputValidator) {
		System.err.println("Spring initialized me"); 
		this.userDao = userDao;
		this.inputValidator	= inputValidator;
	}
	
	@Autowired
	public void setEntityConverter(EntityConverter entityConverter) {
		this.entityConverter = entityConverter;
	}
	
	@Override
	@Transactional
	public UserBoundary createUser(UserBoundary user) {
		inputValidator.validateUserInput(user);
		if(this.userDao.existsById(user.getEmail())) {
			throw new ForbiddenRequestException("User with this email address already exists");
		}
		UserEntity entity = this.entityConverter.userToEntity(user);
		entity = this.userDao.save(entity);
		return this.entityConverter.convertUserFromEntity(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public UserBoundary login(String userEmail) {
		Optional<UserEntity> entity = this.userDao.findById(userEmail);
		
		if(!entity.isPresent()) {
			throw new ObjectNotFoundException("There is no user with email: " + userEmail);
		}
		return this.entityConverter.convertUserFromEntity(entity.get());
	}

	@Override
	@Transactional
	public UserBoundary updateUser(String userEmail, UserBoundary update) {
		inputValidator.validateUserInput(update);
		this.login(userEmail);
		this.login(update.getEmail());
		this.userDao.save(this.entityConverter.userToEntity(update));
		return update;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(String adminEmail) {
		List<UserBoundary> allUsers = new ArrayList<>();
		Iterable<UserEntity> allUsersFromDb = this.userDao.findAll();
		for (UserEntity user: allUsersFromDb) {
			allUsers.add(this.entityConverter.convertUserFromEntity(user));
		}
		return allUsers;
	}

	@Override
	@Transactional
	public void deleteAllUsers(String adminEmail) {
		UserEntity userEntity = this.userDao.findById(adminEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + adminEmail));
		
		if(userEntity.getRole() != UserRole.ADMIN) {
			throw new ForbiddenRequestException("You don't have permission to do this");
		}
		this.userDao.deleteAll(); 
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(String adminEmail, int page, int size) {
		UserEntity userEntity = this.userDao.findById(adminEmail)
				.orElseThrow(() -> new ObjectNotFoundException("There is no user with email: " + adminEmail));
		
		if(userEntity.getRole() != UserRole.ADMIN) {
			throw new ForbiddenRequestException("You don't have permission to do this");
		}
		
		return this.userDao.findAll(PageRequest.of(page, size, Direction.ASC, "email"))
				.getContent()
				.stream()
				.map(this.entityConverter::convertUserFromEntity)
				.collect(Collectors.toList());	
	}
}
