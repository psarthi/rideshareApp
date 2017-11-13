package com.digitusrevolution.rideshare.model.ride.dto;

import com.digitusrevolution.rideshare.model.ride.domain.core.Ride;

public class RideOfferDTO {

	private Ride ride;
	//Removed as we don't have any GoogleDirection model as of now
	//private GoogleDirection googleDirection;
	
	public Ride getRide() {
		return ride;
	}
	public void setRide(Ride ride) {
		this.ride = ride;
	}
}
