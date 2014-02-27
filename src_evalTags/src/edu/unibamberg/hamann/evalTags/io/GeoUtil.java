package edu.unibamberg.hamann.evalTags.io;

import edu.unibamberg.hamann.evalTags.GeoCoordinate;

/**
 * Geo Util from mobAss lecture
 * 
 * @author denis
 * @since 2013
 */
public class GeoUtil {

	private static double EARTH_RADIUS = 6367450;

	/**
	 * returns Distance in meters
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static double distance(GeoCoordinate start, GeoCoordinate end) {
		double dLat = Math.toRadians(end.getLatitude() - start.getLatitude());
		double dLng = Math.toRadians(end.getLongitude() - start.getLongitude());
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(start.getLatitude()))
				* Math.cos(Math.toRadians(end.getLatitude()));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = EARTH_RADIUS * c;

		return Math.abs(dist);
	}

}