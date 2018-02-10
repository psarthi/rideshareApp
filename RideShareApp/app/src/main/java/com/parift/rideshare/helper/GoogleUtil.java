package com.parift.rideshare.helper;

import java.util.List;

import com.parift.rideshare.model.app.google.GoogleGeocode;
import com.parift.rideshare.model.app.google.Result;

public class GoogleUtil {

	public static String getAddress(GoogleGeocode googleGeocodeResult) {
		String address = null;
		
		if (googleGeocodeResult.getStatus().equals("OK")) {
			List<Result> results = googleGeocodeResult.getResults();
			//1st Priority
			for (Result result : results) {
				for (String type: result.getTypes()) {
					if (type.equals("street_address")) {
						return result.getFormattedAddress();
					}
				}
			}
			//2nd Priority
			for (Result result : results) {
				for (String type: result.getTypes()) {
					if (type.equals("point_of_interest")) {
						return result.getFormattedAddress();
					}
				}
			}
			//3rd Priority
			for (Result result : results) {
				for (String type: result.getTypes()) {
					if (type.equals("route")) {
						return result.getFormattedAddress();
					}
				}
			}
			//4th Priority
			for (Result result : results) {
				for (String type: result.getTypes()) {
					if (type.equals("sublocality")) {
						return result.getFormattedAddress();
					}
				}
			}
		}
		return address;
	}
}
