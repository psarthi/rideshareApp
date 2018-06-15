package com.parift.rideshare.model.serviceprovider.domain.core;

import java.time.ZonedDateTime;
import java.util.Date;

public class RewardCouponTransaction extends RewardTransaction {

	private String couponCode;
	private CouponStatus status;
	private Date redemptionDateTime;
	
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	public CouponStatus getStatus() {
		return status;
	}
	public void setStatus(CouponStatus status) {
		this.status = status;
	}

	public Date getRedemptionDateTime() {
		return redemptionDateTime;
	}

	public void setRedemptionDateTime(Date redemptionDateTime) {
		this.redemptionDateTime = redemptionDateTime;
	}
}
