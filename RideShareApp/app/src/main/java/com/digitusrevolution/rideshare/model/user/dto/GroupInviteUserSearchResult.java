package com.digitusrevolution.rideshare.model.user.dto;

public class GroupInviteUserSearchResult {
	
	private BasicUser user;
	private boolean member;
	
	public BasicUser getUser() {
		return user;
	}
	public void setUser(BasicUser user) {
		this.user = user;
	}
	public boolean isMember() {
		return member;
	}
	public void setMember(boolean member) {
		this.member = member;
	}
	
	
}
