package acs;

public class UserInfo {
	private String userEmail;

	public UserInfo() {

	}

	public UserInfo(String email) {
		setUserEmail(email);
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String email) {
		this.userEmail = email;
	}
}
