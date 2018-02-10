package com.parift.rideshare.model.ride.dto;

import com.parift.rideshare.model.ride.domain.RideRequestPoint;

public class RideRequestSearchPoint extends RideRequestPoint{
	
	private double distance;
	
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	

}
