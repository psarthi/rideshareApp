package com.parift.rideshare.model.user.domain.core;

import com.parift.rideshare.model.user.domain.Photo;
import com.parift.rideshare.model.user.domain.VehicleCategory;
import com.parift.rideshare.model.user.domain.VehicleSubCategory;

public class Vehicle {

	private long id;
	private String registrationNumber;
	private String model;
	private int seatCapacity;
	private int smallLuggageCapacity;
	private VehicleCategory vehicleCategory;
	private VehicleSubCategory vehicleSubCategory;
	private Photo photo;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getSeatCapacity() {
		return seatCapacity;
	}

	public void setSeatCapacity(int seatCapacity) {
		this.seatCapacity = seatCapacity;
	}

	public int getSmallLuggageCapacity() {
		return smallLuggageCapacity;
	}

	public void setSmallLuggageCapacity(int smallLuggageCapacity) {
		this.smallLuggageCapacity = smallLuggageCapacity;
	}

	public VehicleSubCategory getVehicleSubCategory() {
		return vehicleSubCategory;
	}

	public void setVehicleSubCategory(VehicleSubCategory vehicleSubCategory) {
		this.vehicleSubCategory = vehicleSubCategory;
	}

	public VehicleCategory getVehicleCategory() {
		return vehicleCategory;
	}

	public void setVehicleCategory(VehicleCategory vehicleCategory) {
		this.vehicleCategory = vehicleCategory;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		if (!(obj instanceof Vehicle)) {
			return false;
		}
		Vehicle other = (Vehicle) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

}
