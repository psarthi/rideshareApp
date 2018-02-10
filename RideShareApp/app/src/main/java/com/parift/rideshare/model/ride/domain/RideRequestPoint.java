package com.parift.rideshare.model.ride.domain;

import java.util.Date;

public class RideRequestPoint {
	
	private String _id;
	private Point point = new Point();
	private long rideRequestId;
	private Date dateTime;
	private String timeVariation;
	private int distanceVariation;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	public long getRideRequestId() {
		return rideRequestId;
	}
	public void setRideRequestId(long rideRequestId) {
		this.rideRequestId = rideRequestId;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public int getDistanceVariation() {
		return distanceVariation;
	}
	public void setDistanceVariation(int distanceVariation) {
		this.distanceVariation = distanceVariation;
	}
	public String getTimeVariation() {
		return timeVariation;
	}
	public void setTimeVariation(String timeVariation) {
		this.timeVariation = timeVariation;
	}
}
