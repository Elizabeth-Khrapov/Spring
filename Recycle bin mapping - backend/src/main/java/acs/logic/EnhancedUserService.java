package acs.logic;

import java.util.List;

import acs.boundaries.UserBoundary;

public interface EnhancedUserService extends UserService {
	public List<UserBoundary> getAllUsers(String adminEmail, int page, int size);
}
