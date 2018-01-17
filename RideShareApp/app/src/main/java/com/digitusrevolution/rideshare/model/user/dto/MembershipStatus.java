package com.digitusrevolution.rideshare.model.user.dto;

import com.digitusrevolution.rideshare.model.user.domain.ApprovalStatus;

public class MembershipStatus {
	private boolean member;
	private boolean admin;
	private boolean invited;
	private boolean requestSubmitted;
	private ApprovalStatus approvalStatus;


	public boolean isMember() {
		return member;
	}
	public void setMember(boolean member) {
		this.member = member;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isInvited() {
		return invited;
	}

	public void setInvited(boolean invited) {
		this.invited = invited;
	}

	public boolean isRequestSubmitted() {
		return requestSubmitted;
	}

	public void setRequestSubmitted(boolean requestSubmitted) {
		this.requestSubmitted = requestSubmitted;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
}