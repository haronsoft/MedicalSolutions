package com.medianova.utils;

public class CustomMarker {

	private Double latitude;
	private Double longitude;

	public CustomMarker(String id, Double latitude, Double longitude) {

		String id1 = id;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Double getCustomMarkerLatitude() {
		return latitude;
	}

	public Double getCustomMarkerLongitude() {
		return longitude;
	}

}
