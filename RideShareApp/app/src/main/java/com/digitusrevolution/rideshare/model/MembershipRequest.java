package com.digitusrevolution.rideshare.model;

import com.digitusrevolution.rideshare.model.user.domain.ApprovalStatus;
import com.digitusrevolution.rideshare.model.user.domain.core.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MembershipRequest {

	private int id;
	private User user;
	//This would be visible to all group members e.g. employee id, flat number etc
	private String userUniqueIdentifier;
	private Map<String, String> questionAnswers = new HashMap<String, String>();
	private ApprovalStatus status;
	private Date createdDateTime;
	private String adminRemark;
	private String userRemark;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
		result = prime * result + id;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (!(obj instanceof MembershipRequest)) {
			return false;
		}
		MembershipRequest other = (MembershipRequest) obj;
		if (id != other.id) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
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
}
