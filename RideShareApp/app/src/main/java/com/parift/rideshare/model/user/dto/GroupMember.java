package com.parift.rideshare.model.user.dto;

//Reason behind this jsonignore so that it doesn't throw error while converting from Domain Model to DTO which has less fields
public class GroupMember extends BasicUser{
	
	private boolean admin;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
}
