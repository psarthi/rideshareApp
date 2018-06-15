package com.parift.rideshare.model.serviceprovider.domain.core;

import com.parift.rideshare.model.user.domain.core.User;

import java.time.ZonedDateTime;
import java.util.Date;

public class RewardTransaction implements Comparable<RewardTransaction>{

	private int id;
	private Offer offer;
	private Date rewardTransactionDateTime;
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
	@Override
	public int compareTo(RewardTransaction rewardTransaction) {
		//Negative number is desc order, positive is asc order
		//ascending order
		//return this.id - rewardTransaction.id;

		//descending order
		return Long.compare(rewardTransaction.id, this.id);
	}

	public Date getRewardTransactionDateTime() {
		return rewardTransactionDateTime;
	}

	public void setRewardTransactionDateTime(Date rewardTransactionDateTime) {
		this.rewardTransactionDateTime = rewardTransactionDateTime;
	}
}
