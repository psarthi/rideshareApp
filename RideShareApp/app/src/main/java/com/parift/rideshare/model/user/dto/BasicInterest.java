package com.parift.rideshare.model.user.dto;

import com.parift.rideshare.model.user.domain.Photo;

//Reason behind this jsonignore so that it doesn't throw error while converting from Domain Model to DTO which has less fields
public class BasicInterest {
	
	private long id;
	private String name;
	private Photo photo;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		if (!(obj instanceof BasicInterest)) {
			return false;
		}
		BasicInterest other = (BasicInterest) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}	
}
