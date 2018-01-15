package com.digitusrevolution.rideshare.model.user.dto;

//Imp - Keep this as seperate class so that we maintain the basic concept intact of Basic and Full
//and we can user standard convertor for converting DO to DTO's and if we mix this along with BasicGroup
//then we would not know whether memberCount is 0 or not there

public class GroupDetail extends BasicGroup{

	//This is an additional property than DO reason for having this 
	//so that we don't have to get full list of member to get the count
	private int memberCount;
	private MembershipStatus membershipStatus;
	
	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public MembershipStatus getMembershipStatus() {
		return membershipStatus;
	}

	public void setMembershipStatus(MembershipStatus membershipStatus) {
		this.membershipStatus = membershipStatus;
	}
}
