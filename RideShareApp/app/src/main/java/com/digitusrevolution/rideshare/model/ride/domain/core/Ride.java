package com.digitusrevolution.rideshare.model.ride.domain.core;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import com.digitusrevolution.rideshare.model.ride.domain.RidePoint;
import com.digitusrevolution.rideshare.model.ride.domain.RecurringDetail;
import com.digitusrevolution.rideshare.model.ride.domain.Route;
import com.digitusrevolution.rideshare.model.ride.domain.TrustNetwork;
import com.digitusrevolution.rideshare.model.user.domain.Sex;
import com.digitusrevolution.rideshare.model.user.domain.core.User;
import com.digitusrevolution.rideshare.model.user.domain.core.Vehicle;

//This can help in getting just id instead of object but its causing issue while deserialization, so for now lets park it.
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Ride implements Comparable<Ride>{

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
	private Route route;
	private boolean recur;
	private RecurringDetail recurringDetail;
	private RideStatus status;
	private RideSeatStatus seatStatus;
	private Vehicle vehicle;
	//@JsonIdentityReference(alwaysAsId=true)
	private User driver;
	private Collection<RidePassenger> ridePassengers = new HashSet<RidePassenger>();
	private Collection<RideRequest> acceptedRideRequests = new HashSet<RideRequest>();
	private Collection<RideRequest> cancelledRideRequests = new HashSet<RideRequest>();
	private int travelDistance;
	private RideMode rideMode;
	
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
	public boolean getRecur() {
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
	public Route getRoute() {
		return route;
	}
	public void setRoute(Route route) {
		this.route = route;
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
	public Collection<RideRequest> getAcceptedRideRequests() {
		return acceptedRideRequests;
	}
	public void setAcceptedRideRequests(Collection<RideRequest> acceptedRideRequests) {
		this.acceptedRideRequests = acceptedRideRequests;
	}
	public User getDriver() {
		return driver;
	}
	public void setDriver(User driver) {
		this.driver = driver;
	}
	public Collection<RidePassenger> getRidePassengers() {
		return ridePassengers;
	}
	public void setRidePassengers(Collection<RidePassenger> ridePassengers) {
		this.ridePassengers = ridePassengers;
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
		if (!(obj instanceof Ride)) {
			return false;
		}
		Ride other = (Ride) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}
	public Collection<RideRequest> getCancelledRideRequests() {
		return cancelledRideRequests;
	}
	public void setCancelledRideRequests(Collection<RideRequest> cancelledRideRequests) {
		this.cancelledRideRequests = cancelledRideRequests;
	}
	public RideSeatStatus getSeatStatus() {
		return seatStatus;
	}
	public void setSeatStatus(RideSeatStatus seatStatus) {
		this.seatStatus = seatStatus;
	}
	public RideRequest getRideRequestOfPassenger(long passengerId){
		Collection<RideRequest> acceptedRideRequests = getAcceptedRideRequests();
		for (RideRequest rideRequest : acceptedRideRequests) {
			if (rideRequest.getPassenger().getId() == passengerId){
				return rideRequest;
			}
		}
		throw new RuntimeException("Ride Request not found for passenger id:"+passengerId);
	}
	public RidePassenger getRidePassenger(long passengerId){
		Collection<RidePassenger> passengers = getRidePassengers();
		for (RidePassenger ridePassenger : passengers) {
			if (ridePassenger.getPassenger().getId() == passengerId){
				return ridePassenger;
			}
		}
		throw new RuntimeException("No passenger found with id:"+passengerId);
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
	@Override
	public int compareTo(Ride ride) {
		//Negative number is desc order, positive is asc order
		//ascending order
		//return this.id - ride.id;

		//descending order
		return Long.compare(ride.id, this.id);
	}

	
}
