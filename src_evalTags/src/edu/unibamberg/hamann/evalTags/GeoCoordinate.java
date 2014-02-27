package edu.unibamberg.hamann.evalTags;


/**
 * represents a GeoCoordinate with a lat & long, as well as a UID
 * @author denis
 *
 */
public class GeoCoordinate {

	double latitude;
	double longitude;

	long uid; 
	
	public GeoCoordinate(long uid, double latitude, double longitude) {
		this.uid = uid;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public GeoCoordinate(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = false;

		if (obj instanceof GeoCoordinate) {
			if (((GeoCoordinate) obj).getUid() == getUid()) {
				equals = true;
			}
		}

		return equals;
	}

	
	
	public long getUid() {
		return uid;
	}

	@Override
	public String toString() {

		return "["+uid+"]["+latitude + "," + longitude+"]";
	}

}
