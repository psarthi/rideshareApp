package com.parift.rideshare.model.user.domain.core;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import com.parift.rideshare.model.user.domain.MembershipForm;
import com.parift.rideshare.model.user.domain.Photo;
import com.parift.rideshare.model.user.domain.GroupFeedback;
import com.parift.rideshare.model.user.domain.MembershipRequest;

//Reason behind this jsonignore so that it doesn't throw error while converting from DTO to Domain Model which has more fields
//e.g. in case of GroupDetail has extra fields such as membercount
/**
 * @author psarthi
 *
 */
public class Group implements Comparable<Group>{

	private long id;
	private String name;
	private Photo photo;
	
	private Collection<User> members = new HashSet<User>();
	private User owner;
	private Collection<User> admins = new HashSet<User>();
	private Collection<GroupFeedback> feedbacks = new LinkedList<GroupFeedback>();
	private MembershipForm membershipForm;
	private Date createdDateTime;
	private String url;
	private String information;
	private Collection<MembershipRequest> membershipRequests = new HashSet<MembershipRequest>();
	private int genuineVotes;
	private int fakeVotes;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public Collection<User> getMembers() {
		return members;
	}

	public void setMembers(Collection<User> members) {
		this.members = members;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Collection<User> getAdmins() {
		return admins;
	}

	public void setAdmins(Collection<User> admins) {
		this.admins = admins;
	}

	public Collection<GroupFeedback> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(Collection<GroupFeedback> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public MembershipForm getMembershipForm() {
		return membershipForm;
	}

	public void setMembershipForm(MembershipForm membershipForm) {
		this.membershipForm = membershipForm;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public Collection<MembershipRequest> getMembershipRequests() {
		return membershipRequests;
	}

	public void setMembershipRequests(Collection<MembershipRequest> membershipRequests) {
		this.membershipRequests = membershipRequests;
	}

	public int getGenuineVotes() {
		return genuineVotes;
	}

	public void setGenuineVotes(int genuineVotes) {
		this.genuineVotes = genuineVotes;
	}

	public int getFakeVotes() {
		return fakeVotes;
	}

	public void setFakeVotes(int fakeVotes) {
		this.fakeVotes = fakeVotes;
	}
	
	public boolean isMemberAdmin(int memberUserId){
		Collection<User> admins = getAdmins();
		for (User user : admins) {
			if (user.getId()==memberUserId){
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(Group group) {
		//Negative number is desc order, positive is asc order
		//This will return in assending order
		//Changing it to upper case so that we are comparing the group name properly by ignoring the case
		return this.name.toUpperCase().compareTo(group.name.toUpperCase());
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
		if (!(obj instanceof Group)) {
			return false;
		}
		Group other = (Group) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

}








































