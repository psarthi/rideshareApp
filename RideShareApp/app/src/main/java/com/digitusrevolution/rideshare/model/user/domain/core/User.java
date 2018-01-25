package com.digitusrevolution.rideshare.model.user.domain.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import com.digitusrevolution.rideshare.model.billing.domain.core.Account;
import com.digitusrevolution.rideshare.model.billing.domain.core.AccountType;
import com.digitusrevolution.rideshare.model.billing.domain.core.Bill;
import com.digitusrevolution.rideshare.model.ride.domain.core.Ride;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideRequest;
import com.digitusrevolution.rideshare.model.user.domain.City;
import com.digitusrevolution.rideshare.model.user.domain.Country;
import com.digitusrevolution.rideshare.model.user.domain.FriendRequest;
import com.digitusrevolution.rideshare.model.user.domain.MembershipRequest;
import com.digitusrevolution.rideshare.model.user.domain.Photo;
import com.digitusrevolution.rideshare.model.user.domain.Preference;
import com.digitusrevolution.rideshare.model.user.domain.RegistrationType;
import com.digitusrevolution.rideshare.model.user.domain.Role;
import com.digitusrevolution.rideshare.model.user.domain.Sex;
import com.digitusrevolution.rideshare.model.user.domain.State;
import com.digitusrevolution.rideshare.model.user.domain.UserFeedback;

//This can help in getting just id instead of object but its causing issue while deserialization, so for now lets park it.
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
//This would enable deserialization from DTO to Domain model e.g. UserDTO contain otp field which is not here, so it would be ignored
//we don't want use jsonignore for otp field in userDTO else deserialization of userRegistration would remove otp and you would get null value for that
public class User implements Comparable<User>{
	
	private long id;
	private String firstName;
	private String lastName;
	private Sex sex;
	private String mobileNumber;
	private String email;
	private String password;
	private City city;
	private State state;
	private Country country;
	private Photo photo;
	private Collection<Group> groups = new HashSet<Group>();
	private Collection<Vehicle> vehicles = new HashSet<Vehicle>();
	private Collection<User> friends = new HashSet<User>();
	private Collection<Role> roles = new HashSet<Role>();
	private Collection<Account> accounts = new HashSet<Account>();
	//@JsonIdentityReference(alwaysAsId=true)
	private Collection<Ride> ridesOffered = new HashSet<Ride>();
	private Collection<RideRequest> rideRequests = new HashSet<RideRequest>();
	private Collection<Bill> bills = new HashSet<Bill>();

	private Preference preference;
	private Collection<UserFeedback> feedbacks = new HashSet<UserFeedback>();
	private Collection<FriendRequest> friendRequests = new HashSet<FriendRequest>();
	private float profileRating;
	
	private Collection<Group> groupInvites = new HashSet<Group>();
	private Collection<MembershipRequest> membershipRequests = new HashSet<MembershipRequest>();
	private RegistrationType registrationType;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Sex getSex() {
		return sex;
	}
	public void setSex(Sex sex) {
		this.sex = sex;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public City getCity() {
		return city;
	}
	public void setCity(City city) {
		this.city = city;
	}
	public Photo getPhoto() {
		return photo;
	}
	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public float getProfileRating() {
		return profileRating;
	}

	public void setProfileRating(float profileRating) {
		this.profileRating = profileRating;
	}

	public Collection<Group> getGroups() {
		return groups;
	}

	public void setGroups(Collection<Group> groups) {
		this.groups = groups;
	}

	public Collection<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(Collection<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public Collection<User> getFriends() {
		return friends;
	}

	public void setFriends(Collection<User> friends) {
		this.friends = friends;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public Collection<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Collection<Account> accounts) {
		this.accounts = accounts;
	}

	public Collection<RideRequest> getRideRequests() {
		return rideRequests;
	}

	public void setRideRequests(Collection<RideRequest> rideRequests) {
		this.rideRequests = rideRequests;
	}

	public Collection<Bill> getBills() {
		return bills;
	}

	public void setBills(Collection<Bill> bills) {
		this.bills = bills;
	}

	public Collection<Ride> getRidesOffered() {
		return ridesOffered;
	}

	public void setRidesOffered(Collection<Ride> ridesOffered) {
		this.ridesOffered = ridesOffered;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
	
	public Account getAccount(AccountType accountType){
		Collection<Account> accounts = getAccounts();
		for (Account account : accounts) {
			if (account.getType().equals(accountType)){
				return account;
			}
		}
		throw new RuntimeException("No account found for the type:"+accountType);
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
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	public Collection<UserFeedback> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(Collection<UserFeedback> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public Preference getPreference() {
		return preference;
	}

	public void setPreference(Preference preference) {
		this.preference = preference;
	}

	public Collection<FriendRequest> getFriendRequests() {
		return friendRequests;
	}

	public void setFriendRequests(Collection<FriendRequest> friendRequests) {
		this.friendRequests = friendRequests;
	}
	
	public FriendRequest getFriendRequest(long friendUserId){
		Collection<FriendRequest> friendRequests = getFriendRequests();
		for (FriendRequest friendRequest : friendRequests) {
			if (friendRequest.getFriend().getId() == friendUserId){
				return friendRequest;
			}
		}
		//Returning null and not throwing exception, as let this be handled at business logic level
		return null;
	}

	public Collection<Group> getGroupInvites() {
		return groupInvites;
	}

	public void setGroupInvites(Collection<Group> groupInvites) {
		this.groupInvites = groupInvites;
	}

	public RegistrationType getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(RegistrationType registrationType) {
		this.registrationType = registrationType;
	}

	@Override
	public int compareTo(User user) {
		//Negative number is desc order, positive is asc order
		//This will return in assending order
		//Changing it to upper case so that we are comparing the user name properly by ignoring the case
		return this.firstName.toUpperCase().compareTo(user.firstName.toUpperCase());
	}

	public Collection<MembershipRequest> getMembershipRequests() {
		return membershipRequests;
	}

	public void setMembershipRequests(Collection<MembershipRequest> membershipRequests) {
		this.membershipRequests = membershipRequests;
	}

}





















