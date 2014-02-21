package edu.unibamberg.hamann.evalTags.dominik.util;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

public class GeometryHelper {

	public final static int WGS_84_SRID = 4326;
	public static GeometryFactory gf = new GeometryFactory(
			new PrecisionModel(), WGS_84_SRID);

	/**
	 * Define Merge as A + (B \ A)
	 * 
	 * @param a
	 *            - a geometry
	 * @param b
	 *            - another geometry
	 * @return the merged geometry
	 */
	public static Geometry merge(Geometry a, Geometry b) {

		Geometry additionalInformation = b.difference(a);
		// watch out: argument of merge has to be simple Geometry
		if (additionalInformation instanceof GeometryCollection) {
			for (int i = 0; i < additionalInformation.getNumGeometries(); i++) {
				a = a.union(additionalInformation.getGeometryN(i));
			}

		} else {
			a = a.union(additionalInformation);
		}

		return a;

	}

}
