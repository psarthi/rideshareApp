package com.digitusrevolution.rideshare.model.ride.dto;

import com.digitusrevolution.rideshare.model.ride.domain.core.PassengerStatus;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;

/*
 * Purpose of this class is to break ManyToMany relationship between Ride and User to OneToMany from both sides,
 * so that we can add extra fields of passenger such as Passenger Status
 * 
 */

public class BasicRidePassenger {
	
	private int id;
	private BasicUser passenger;
	
	public BasicUser getPassenger() {
		return passenger;
	}
	public void setPassenger(BasicUser passenger) {
		this.passenger = passenger;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
