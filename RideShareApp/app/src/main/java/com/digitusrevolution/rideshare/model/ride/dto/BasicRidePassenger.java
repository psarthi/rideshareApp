package com.digitusrevolution.rideshare.model.ride.dto;

import com.digitusrevolution.rideshare.model.ride.domain.core.PassengerStatus;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;

/*
 * Purpose of this class is to break ManyToMany relationship between Ride and User to OneToMany from both sides,
 * so that we can add extra fields of passenger such as Passenger Status
 * 
 */

//Reason behind this jsonignore so that it doesn't throw error while converting from Domain Model to DTO which has less fields
public class BasicRidePassenger {
	
	private long id;
	private BasicUser passenger;
	
	public BasicUser getPassenger() {
		return passenger;
	}
	public void setPassenger(BasicUser passenger) {
		this.passenger = passenger;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
