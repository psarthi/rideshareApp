package com.parift.rideshare.model.ride.dto;

import java.util.Date;

import com.parift.rideshare.model.billing.domain.core.Invoice;
import com.parift.rideshare.model.ride.domain.RecurringDetail;
import com.parift.rideshare.model.ride.domain.RidePoint;
import com.parift.rideshare.model.ride.domain.TrustNetwork;
import com.parift.rideshare.model.ride.domain.core.Ride;
import com.parift.rideshare.model.ride.domain.core.RideMode;
import com.parift.rideshare.model.ride.domain.core.RideSeatStatus;
import com.parift.rideshare.model.ride.domain.core.RideStatus;
import com.parift.rideshare.model.user.domain.Sex;
import com.parift.rideshare.model.user.domain.core.Vehicle;
import com.parift.rideshare.model.user.dto.BasicUser;

//Reason behind this jsonignore so that it doesn't throw error while converting from Domain Model to DTO which has less fields
public class BasicRide {

	//id data type needs to be finalized later, whether to use int, long, string
	private long id;
	private Date startTime;
	private Date endTime;
	private RidePoint startPoint = new RidePoint();
	private RidePoint endPoint = new RidePoint();
	private String startPointAddress;
	private String endPointAddress;
	private int seatOffered;
	private int luggageCapacityOffered;
	private Sex sexPreference;
	private TrustNetwork trustNetwork;
	private RideStatus status;
	private RideSeatStatus seatStatus;
	private Vehicle vehicle;
	private BasicUser driver;
	private int travelDistance;
	private RideMode rideMode;
	private Invoice invoice;
	private boolean recur;
	private RecurringDetail recurringDetail;
	private BasicRide parentRide;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public RidePoint getStartPoint() {
		return startPoint;
	}
	public void setStartPoint(RidePoint startPoint) {
		this.startPoint = startPoint;
	}
	public int getSeatOffered() {
		return seatOffered;
	}
	public void setSeatOffered(int seatOffered) {
		this.seatOffered = seatOffered;
	}
	public int getLuggageCapacityOffered() {
		return luggageCapacityOffered;
	}
	public void setLuggageCapacityOffered(int luggageCapacityOffered) {
		this.luggageCapacityOffered = luggageCapacityOffered;
	}
	public RideStatus getStatus() {
		return status;
	}
	public void setStatus(RideStatus status) {
		this.status = status;
	}
	public TrustNetwork getTrustNetwork() {
		return trustNetwork;
	}
	public void setTrustNetwork(TrustNetwork trustNetwork) {
		this.trustNetwork = trustNetwork;
	}
	public Vehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	public RidePoint getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(RidePoint endPoint) {
		this.endPoint = endPoint;
	}
	public BasicUser getDriver() {
		return driver;
	}
	public void setDriver(BasicUser driver) {
		this.driver = driver;
	}
	public Sex getSexPreference() {
		return sexPreference;
	}
	public void setSexPreference(Sex sexPreference) {
		this.sexPreference = sexPreference;
	}
	public int getTravelDistance() {
		return travelDistance;
	}
	public void setTravelDistance(int travelDistance) {
		this.travelDistance = travelDistance;
	}
	public RideSeatStatus getSeatStatus() {
		return seatStatus;
	}
	public void setSeatStatus(RideSeatStatus seatStatus) {
		this.seatStatus = seatStatus;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public RideMode getRideMode() {
		return rideMode;
	}
	public void setRideMode(RideMode rideMode) {
		this.rideMode = rideMode;
	}
	public String getStartPointAddress() {
		return startPointAddress;
	}
	public void setStartPointAddress(String startPointAddress) {
		this.startPointAddress = startPointAddress;
	}
	public String getEndPointAddress() {
		return endPointAddress;
	}
	public void setEndPointAddress(String endPointAddress) {
		this.endPointAddress = endPointAddress;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	public boolean isRecur() {
		return recur;
	}
	public void setRecur(boolean recur) {
		this.recur = recur;
	}

	public RecurringDetail getRecurringDetail() {
		return recurringDetail;
	}

	public void setRecurringDetail(RecurringDetail recurringDetail) {
		this.recurringDetail = recurringDetail;
	}

	public BasicRide getParentRide() {
		return parentRide;
	}

	public void setParentRide(BasicRide parentRide) {
		this.parentRide = parentRide;
	}
}
