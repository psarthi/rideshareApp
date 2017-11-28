package com.digitusrevolution.rideshare.model.ride.dto;

import com.digitusrevolution.rideshare.model.dto.google.GoogleDirection;
import com.digitusrevolution.rideshare.model.ride.domain.core.Ride;

public class RideOfferInfo {

	private BasicRide ride;
	//Removed as we don't have any GoogleDirection model as of now
	private GoogleDirection googleDirection;
	
	public BasicRide getRide() {
		return ride;
	}
	public void setRide(BasicRide ride) {
		this.ride = ride;
	}

	public GoogleDirection getGoogleDirection() {
		return googleDirection;
	}

	public void setGoogleDirection(GoogleDirection googleDirection) {
		this.googleDirection = googleDirection;
	}
}
