package com.parift.rideshare.model.serviceprovider.domain.core;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import com.parift.rideshare.model.user.domain.core.User;

public class RewardTransaction {

	private int id;
	private Offer offer;
	private Date redemptionDateTime;
	private User user;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Offer getOffer() {
		return offer;
	}
	public void setOffer(Offer offer) {
		this.offer = offer;
	}
	public Date getRedemptionDateTime() {
		return redemptionDateTime;
	}
	public void setRedemptionDateTime(Date redemptionDateTime) {
		this.redemptionDateTime = redemptionDateTime;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
}
