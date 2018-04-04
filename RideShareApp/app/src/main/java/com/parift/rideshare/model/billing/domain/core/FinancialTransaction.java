package com.parift.rideshare.model.billing.domain.core;

import com.parift.rideshare.model.user.domain.core.User;

import java.time.ZonedDateTime;
import java.util.Date;

public class FinancialTransaction{
	
	private long id;
	private Date dateTime;
	private TransactionType type;
	private float amount;
	private String remark;
	private TransactionStatus status;
	private PaymentGateway paymentGateway;
	private Transaction walletTransaction;
	private User user;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Transaction getWalletTransaction() {
		return walletTransaction;
	}
	public void setWalletTransaction(Transaction walletTransaction) {
		this.walletTransaction = walletTransaction;
	}
	public TransactionStatus getStatus() {
		return status;
	}
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	public PaymentGateway getPaymentGateway() {
		return paymentGateway;
	}
	public void setPaymentGateway(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}


	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
