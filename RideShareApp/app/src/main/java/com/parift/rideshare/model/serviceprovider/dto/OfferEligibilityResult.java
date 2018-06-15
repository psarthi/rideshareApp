package com.parift.rideshare.model.serviceprovider.dto;

public class OfferEligibilityResult {

	private boolean userEligible;
	private UserRidesStats userRidesStats;
	
	public boolean isUserEligible() {
		return userEligible;
	}
	public void setUserEligible(boolean userEligible) {
		this.userEligible = userEligible;
	}
	public UserRidesStats getUserRidesStats() {
		return userRidesStats;
	}
	public void setUserRidesStats(UserRidesStats userRidesStats) {
		this.userRidesStats = userRidesStats;
	}
	
	
}
