package edu.unibamberg.hamann.evalTags.dominik.model;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import edu.unibamberg.hamann.evalTags.dominik.util.GeometryHelper;
import edu.unibamberg.hamann.evalTags.dominik.util.MetricBufferer;

public class OsmPoi extends PlaceMark {

	// der Mist muss natürlich berichtigt werden: Orte sind nicht nur Punkte...
	private int minVisitTime = 0; // seconds
	private int associatedNodeId = -1;
	private Geometry signature = null;

	/**
	 * stay time in minutes
	 * 
	 * @param pin
	 * @param minVisitTime
	 */
	public OsmPoi(Coordinate pin, int minVisitTime, int drawScalingFactor) {
		super(pin);
		this.minVisitTime = minVisitTime * 60;
		MetricBufferer mb = new MetricBufferer();
		signature = mb.buffer(GeometryHelper.gf.createPoint(pin),
				Math.sqrt(minVisitTime * drawScalingFactor));
	}

	public int getAssociatedNodeId() {
		return associatedNodeId;
	}

	public void setAssociatedNodeId(int associatedNodeId) {
		this.associatedNodeId = associatedNodeId;
	}

	public int getMinVisitTime() {
		return minVisitTime;
	}

	public Geometry getSignature() {
		return signature;
	}

}
