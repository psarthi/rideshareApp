package com.parift.rideshare.model.serviceprovider.dto;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;

public class ReimbursementRequest {
	
	private Date rewardTransactionDateTime;
	private LinkedList<byte[]> images;

	public LinkedList<byte[]> getImages() {
		return images;
	}

	public void setImages(LinkedList<byte[]> images) {
		this.images = images;
	}

	public Date getRewardTransactionDateTime() {
		return rewardTransactionDateTime;
	}

	public void setRewardTransactionDateTime(Date rewardTransactionDateTime) {
		this.rewardTransactionDateTime = rewardTransactionDateTime;
	}
}
