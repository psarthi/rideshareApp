package com.digitusrevolution.rideshare.model.billing.dto;

import com.digitusrevolution.rideshare.model.billing.domain.core.AccountType;

public class BillInfo {
	private int billNumber;
	public int getBillNumber() {
		return billNumber;
	}
	public void setBillNumber(int billNumber) {
		this.billNumber = billNumber;
	}
}
