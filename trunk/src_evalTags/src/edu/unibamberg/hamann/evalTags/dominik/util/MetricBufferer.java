package edu.unibamberg.hamann.evalTags.dominik.util;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class MetricBufferer {

	private CoordinateReferenceSystem wgs84Crs;
	private CoordinateReferenceSystem utmCrs;

	private MathTransform wgsToUtmTransformer;
	private MathTransform utmToWgsTransformer;

	public MetricBufferer() {

		try {
			wgs84Crs = CRS.decode("EPSG:4326");
			utmCrs = CRS.decode("EPSG:25832");

			wgsToUtmTransformer = CRS.findMathTransform(wgs84Crs, utmCrs, true);
			utmToWgsTransformer = CRS.findMathTransform(utmCrs, wgs84Crs, true);

		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MismatchedDimensionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Geometry buffer(Geometry toBeBuffered, double distanceInMeters) {

		Geometry result = null;

		// There...
		for (Coordinate coordinate : toBeBuffered.getCoordinates()) {
			Coordinate utmCoordinate = convertToUtm(coordinate);
			coordinate.x = utmCoordinate.x;
			coordinate.y = utmCoordinate.y;
		}

		result = toBeBuffered.buffer(distanceInMeters);

		// ...and back (result + original data)
		for (Coordinate coordinate : result.getCoordinates()) {
			Coordinate wgsCoordinate = convertToWgs(coordinate);
			coordinate.x = wgsCoordinate.x;
			coordinate.y = wgsCoordinate.y;
		}
		for (Coordinate coordinate : toBeBuffered.getCoordinates()) {
			Coordinate wgsCoordinate = convertToWgs(coordinate);
			coordinate.x = wgsCoordinate.x;
			coordinate.y = wgsCoordinate.y;
		}

		return result;
	}

	private Coordinate convertToUtm(Coordinate input) {

		Coordinate result = null;

		try {

			DirectPosition2D srcDirectPosition2D = new DirectPosition2D(
					wgs84Crs, input.x, input.y);
			DirectPosition2D destDirectPosition2D = new DirectPosition2D();
			wgsToUtmTransformer.transform(srcDirectPosition2D,
					destDirectPosition2D);

			result = new Coordinate(destDirectPosition2D.x,
					destDirectPosition2D.y);

			// System.out.println("Converted: " + input.x + "/" + input.y
			// + " (WGS) to " + result.x + "/" + result.y + " (UTM)");

		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	private Coordinate convertToWgs(Coordinate input) {

		Coordinate result = null;

		try {

			DirectPosition2D srcDirectPosition2D = new DirectPosition2D(utmCrs,
					input.x, input.y);
			DirectPosition2D destDirectPosition2D = new DirectPosition2D();
			utmToWgsTransformer.transform(srcDirectPosition2D,
					destDirectPosition2D);

			result = new Coordinate(destDirectPosition2D.x,
					destDirectPosition2D.y);

			// System.out.println("Converted: " + input.x + "/" + input.y
			// + " (UTM) to " + result.x + "/" + result.y + " (WGS)");

		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

}
