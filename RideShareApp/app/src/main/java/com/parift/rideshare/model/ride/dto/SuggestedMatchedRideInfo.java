package com.parift.rideshare.model.ride.dto;

public class SuggestedMatchedRideInfo extends MatchedTripInfo {
	
	private BasicRide ride;
	private String ridePickupPointAddress;
	private String rideDropPointAddress;

	public BasicRide getRide() {
		return ride;
	}
	public void setRide(BasicRide ride) {
		this.ride = ride;
	}

	public String getRidePickupPointAddress() {
		return ridePickupPointAddress;
	}

	public void setRidePickupPointAddress(String ridePickupPointAddress) {
		this.ridePickupPointAddress = ridePickupPointAddress;
	}

	public String getRideDropPointAddress() {
		return rideDropPointAddress;
	}

	public void setRideDropPointAddress(String rideDropPointAddress) {
		this.rideDropPointAddress = rideDropPointAddress;
	}
}
