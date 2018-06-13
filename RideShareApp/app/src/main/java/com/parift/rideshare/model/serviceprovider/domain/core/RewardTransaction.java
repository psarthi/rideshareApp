package com.parift.rideshare.model.serviceprovider.domain.core;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import com.parift.rideshare.model.user.domain.core.User;

public class RewardTransaction {

	private int id;
	private Offer offer;
	private Date transactionDateTime;
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public Date getTransactionDateTime() {
		return transactionDateTime;
	}

	public void setTransactionDateTime(Date transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}
}
