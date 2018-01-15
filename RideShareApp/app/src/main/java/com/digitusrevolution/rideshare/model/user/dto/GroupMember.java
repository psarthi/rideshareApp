package com.digitusrevolution.rideshare.model.user.dto;

public class GroupMember extends BasicUser{

	private boolean admin;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
