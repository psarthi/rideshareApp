package com.digitusrevolution.rideshare.model.ride.domain;

import java.time.ZonedDateTime;
import java.util.Date;

public class RidePointProperty {
	private int id;
	private Date dateTime;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}		
	@Override
	public String toString() {
		return "[rideId,datetime]:"+id+","+dateTime;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RidePointProperty)) {
			return false;
		}
		RidePointProperty other = (RidePointProperty) obj;
		if (dateTime == null) {
			if (other.dateTime != null) {
				return false;
			}
		} else if (!dateTime.equals(other.dateTime)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}	
	
	
}
