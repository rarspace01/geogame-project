package edu.unibamberg.hamann.evalTags.dominik.model;
import com.vividsolutions.jts.geom.Coordinate;

public class PlaceMark {

	private Coordinate pinPoint;

	public Coordinate getPinPoint() {
		return pinPoint;
	}

	public PlaceMark(Coordinate pin) {
		this.pinPoint = pin;
	}

}
