package com.digitusrevolution.rideshare.model.ride.dto;

import java.util.List;

public class RideRequestSearchResult {
	
	private List<MatchedTripInfo> matchedTripInfos;
	private double searchDistance;
	private int resultLastIndex;
	
	public double getSearchDistance() {
		return searchDistance;
	}
	public void setSearchDistance(double searchDistance) {
		this.searchDistance = searchDistance;
	}
	public int getResultLastIndex() {
		return resultLastIndex;
	}
	public void setResultLastIndex(int resultLastIndex) {
		this.resultLastIndex = resultLastIndex;
	}
	public List<MatchedTripInfo> getMatchedTripInfos() {
		return matchedTripInfos;
	}
	public void setMatchedTripInfos(List<MatchedTripInfo> matchedTripInfos) {
		this.matchedTripInfos = matchedTripInfos;
	}
	
}
