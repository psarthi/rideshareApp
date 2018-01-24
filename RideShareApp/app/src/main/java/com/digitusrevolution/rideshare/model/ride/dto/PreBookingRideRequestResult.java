package com.digitusrevolution.rideshare.model.ride.dto;

import java.util.List;

import com.digitusrevolution.rideshare.model.app.google.GoogleDistance;
import com.digitusrevolution.rideshare.model.billing.domain.core.Bill;

public class PreBookingRideRequestResult {
	
	private float maxFare;
	private List<Bill> pendingBills;
	private GoogleDistance googleDistance;

	public List<Bill> getPendingBills() {
		return pendingBills;
	}
	public void setPendingBills(List<Bill> pendingBills) {
		this.pendingBills = pendingBills;
	}
	public float getMaxFare() {
		return maxFare;
	}
	public void setMaxFare(float maxFare) {
		this.maxFare = maxFare;
	}
	public GoogleDistance getGoogleDistance() {
		return googleDistance;
	}
	public void setGoogleDistance(GoogleDistance googleDistance) {
		this.googleDistance = googleDistance;
	}
}
