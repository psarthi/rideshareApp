package com.digitusrevolution.rideshare.model.user.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.digitusrevolution.rideshare.model.user.domain.ApprovalStatus;

//Reason behind this jsonignore so that it doesn't throw error while converting from Domain Model to DTO which has less fields
public class BasicMembershipRequest {

	private long id;
	private BasicUser user;
	private GroupDetail group; 
	//This would be visible to all group members e.g. employee id, flat number etc
	private String userUniqueIdentifier;
	private Map<String, String> questionAnswers = new HashMap<String, String>();
	private ApprovalStatus status;
	private Date createdDateTime;
	private String adminRemark;
	private String userRemark;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public ApprovalStatus getStatus() {
		return status;
	}
	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}
	public Date getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public String getUserUniqueIdentifier() {
		return userUniqueIdentifier;
	}
	public void setUserUniqueIdentifier(String userUniqueIdentifier) {
		this.userUniqueIdentifier = userUniqueIdentifier;
	}
	public Map<String, String> getQuestionAnswers() {
		return questionAnswers;
	}
	public void setQuestionAnswers(Map<String, String> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}
	public String getUserRemark() {
		return userRemark;
	}
	public void setUserRemark(String userRemark) {
		this.userRemark = userRemark;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BasicMembershipRequest)) {
			return false;
		}
		BasicMembershipRequest other = (BasicMembershipRequest) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}
	public String getAdminRemark() {
		return adminRemark;
	}
	public void setAdminRemark(String adminRemark) {
		this.adminRemark = adminRemark;
	}
	public BasicUser getUser() {
		return user;
	}
	public void setUser(BasicUser user) {
		this.user = user;
	}
	public GroupDetail getGroup() {
		return group;
	}
	public void setGroup(GroupDetail group) {
		this.group = group;
	}

}
