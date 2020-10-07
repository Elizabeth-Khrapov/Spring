package acs.boundaries;


public class UserBoundary {
	private String email;
	private UserTypeEnum role;
	private String username;
	private String avatar; 
	
	
	public UserBoundary() {
		
	}
	
	public UserBoundary(String email, UserTypeEnum role, String username, String avatar) {
		super();
		setEmail(email);
		setRole(role);
		setUsername(username);
		setAvatar(avatar);
	}
	
	public String getEmail() {
		return email; 
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public UserTypeEnum getRole() {
		return role;
	}

	public void setRole(UserTypeEnum role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getAvatar() {
		return avatar;
	}
	
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
}
