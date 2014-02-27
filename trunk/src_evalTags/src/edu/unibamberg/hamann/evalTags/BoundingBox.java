package edu.unibamberg.hamann.evalTags;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import edu.unibamberg.hamann.evalTags.io.GeoUtil;

/**
 * represents a BoundingBox
 * 
 * @author denis
 * 
 */
public class BoundingBox {

	private double n, s, w, e;

	public BoundingBox(double n, double e, double s, double w) {
		this.n = n;
		this.s = s;
		this.w = w;
		this.e = e;
	}

	public BoundingBox(GeoCoordinate middle, double distance) {
		double[] result = getBoundingBox(middle.getLatitude(),
				middle.getLongitude(), (int) distance);

		n = result[2];
		e = result[3];

		s = result[0];
		w = result[1];

	}

	/**
	 * internal method to bulid a bounding box based on a Lat & Lon value and a
	 * radius
	 * 
	 * @param pLatitude
	 * @param pLongitude
	 * @param pDistanceInMeters
	 * @return
	 */
	private double[] getBoundingBox(final double pLatitude,
			final double pLongitude, final double pDistanceInMeters) {

		final double[] boundingBox = new double[4];

		final double latRadian = Math.toRadians(pLatitude);

		final double degLatKm = 110.574235;
		final double degLongKm = 110.572833 * Math.cos(latRadian);
		final double deltaLat = pDistanceInMeters / 1000.0 / degLatKm;
		final double deltaLong = pDistanceInMeters / 1000.0 / degLongKm;

		final double minLat = pLatitude - deltaLat;
		final double minLong = pLongitude - deltaLong;
		final double maxLat = pLatitude + deltaLat;
		final double maxLong = pLongitude + deltaLong;

		boundingBox[0] = minLat;
		boundingBox[1] = minLong;
		boundingBox[2] = maxLat;
		boundingBox[3] = maxLong;

		return boundingBox;
	}

	public double getN() {
		return n;
	}

	public void setN(double n) {
		this.n = n;
	}

	public double getS() {
		return s;
	}

	public void setS(double s) {
		this.s = s;
	}

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	public double getE() {
		return e;
	}

	public void setE(double e) {
		this.e = e;
	}

	public String getSN() {
		return convert(n);
	}

	public String getSS() {
		return convert(s);
	}

	public String getSW() {
		return convert(w);
	}

	public String getSE() {
		return convert(e);
	}

	/**
	 * Converts a coordinate to a String representation. The outputType may be
	 * one of FORMAT_DEGREES, FORMAT_MINUTES, or FORMAT_SECONDS. The coordinate
	 * must be a valid double between -180.0 and 180.0.
	 * 
	 * @throws IllegalArgumentException
	 *             if coordinate is less than -180.0, greater than 180.0, or is
	 *             not a number.
	 * @throws IllegalArgumentException
	 *             if outputType is not one of FORMAT_DEGREES, FORMAT_MINUTES,
	 *             or FORMAT_SECONDS.
	 */
	public static String convert(double coordinate) {
		if (coordinate < -180.0 || coordinate > 180.0
				|| Double.isNaN(coordinate)) {
			throw new IllegalArgumentException("coordinate=" + coordinate);
		}

		StringBuilder sb = new StringBuilder();

		// Handle negative values
		if (coordinate < 0) {
			sb.append('-');
			coordinate = -coordinate;
		}

		DecimalFormat df = new DecimalFormat("#########0.00########",
				new DecimalFormatSymbols(Locale.US));

		sb.append(df.format(coordinate));
		return sb.toString();
	}

	/**
	 * expands the bounding box on given meters
	 * 
	 * @param meters
	 * @return
	 */
	BoundingBox expand(double meters) {

		double middlelat = (n + s) / 2;
		double middlelon = (w + e) / 2;

		double basicDistance = GeoUtil.distance(new GeoCoordinate(middlelat,
				middlelon), new GeoCoordinate(n, middlelon));

		System.out.println("Expanding from: " + basicDistance + "m to "
				+ (basicDistance + meters) + "m");

		double result[] = getBoundingBox(middlelat, middlelon,
				(basicDistance + meters));

		/*
		 * boundingBox[0] = minLat; boundingBox[1] = minLong; boundingBox[2] =
		 * maxLat; boundingBox[3] = maxLong;
		 */

		return new BoundingBox(result[2], result[3], result[0], result[1]);
	}

	@Override
	public String toString() {
		// southern lat, western lon, northern lat, eastern lon
		return "S:" + getSS() + "W:" + getSW() + "N:" + getSN() + "E:"
				+ getSE();
	}

}
