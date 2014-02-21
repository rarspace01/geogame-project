package edu.unibamberg.hamann.evalTags.dominik.model;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FastestWeighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.PointList;
import com.vividsolutions.jts.geom.Coordinate;

import edu.unibamberg.hamann.evalTags.dominik.util.TimeGeographicSpaceOfPossibilites;

public class SearchNode {

	private int baseNodeId;
	private SearchNode parent;
	private List<SearchNode> children = new ArrayList<SearchNode>();

	private List<Coordinate> realwayOnParentEdge;
	private double secondsOnParentEdge;
	private double secondsTraveledSoFar;

	private OsmPoi site = null;

	public SearchNode(int baseNodeId, SearchNode parent,
			PointList realwayOnParentEdge, double secondsOnParentEdge,
			double secondsTraveledSoFar) {
		this.baseNodeId = baseNodeId;
		this.parent = parent;
		if (realwayOnParentEdge != null) {
			this.realwayOnParentEdge = convertTrack(realwayOnParentEdge);
		}
		this.secondsOnParentEdge = secondsOnParentEdge;
		this.secondsTraveledSoFar = secondsTraveledSoFar;
	}

	public int getBaseNodeId() {
		return baseNodeId;
	}

	public SearchNode getParent() {
		return parent;
	}

	public List<Coordinate> getRealwayOnParentEdge() {
		return realwayOnParentEdge;
	}

	public double getSecondsOnParentEdge() {
		return secondsOnParentEdge;
	}

	public double getSecondsTraveledSoFar() {
		return secondsTraveledSoFar;
	}

	/**
	 * null, if no children are generated yet
	 * 
	 * @return
	 */
	public List<SearchNode> getChildren() {
		return children;
	}

	/**
	 * null, if no place is associated
	 * 
	 * @return
	 */
	public OsmPoi getSite() {
		return site;
	}

	public void setSite(OsmPoi p) {
		site = p;
	}

	// Der braucht aber viel... Factory?
	/**
	 * 
	 * 
	 * @param explorer
	 * @param graph
	 * @param em
	 * @param includedList
	 * @param travelLimit
	 * @param toId
	 *            null, wenn ohne Ziel
	 * @return
	 */
	public List<SearchNode> generateChildren(EdgeExplorer explorer,
			Graph graph, EncodingManager em, TreeSet<Integer> includedList,
			List<OsmPoi> places, double secondLimit, double velocity, int toId) {
		EdgeIterator iter = explorer.setBaseNode(baseNodeId);
		while (iter.next()) {

			// A*: f = g + h;
			// g aus uniformer Kostensuche
			// h aus schnellstem Weg heim
			double travelCosts_g = secondsTraveledSoFar + iter.getDistance()
					/ velocity;
			// Wenn die Kante von einem touristischen Ort wegführt, kostet das
			// zusätzlich die MinVisitTime
			// TODO: Mehrfachbelegung von Orten berücksichtigen
			OsmPoi foundSite = null;
			for (OsmPoi place : places) {
				if (iter.getAdjNode() == place.getAssociatedNodeId()) {
					System.out.println("==== PLACE FOUND ====");
					travelCosts_g += place.getMinVisitTime();
					foundSite = place;
					break;
				}
			}
			double travelCosts_h = 0;
			// freie Suche oder mit Ziel?
			if (toId != -1) {
				travelCosts_h = (new Dijkstra(
						graph,
						em.getEncoder(TimeGeographicSpaceOfPossibilites.MODE_OF_TRANSPORTATION),
						new FastestWeighting(
								em.getEncoder(TimeGeographicSpaceOfPossibilites.MODE_OF_TRANSPORTATION)))
						.calcPath(iter.getAdjNode(), toId).getDistance())
						/ velocity;
			}
			double travelCosts = travelCosts_g + travelCosts_h;

			if (travelCosts < secondLimit
					&& !includedList.contains(iter.getBaseNode())) {
				SearchNode child = new SearchNode(iter.getAdjNode(), this,
						iter.fetchWayGeometry(3),
						iter.getDistance() / velocity, travelCosts_g);
				if (foundSite != null) {
					child.setSite(foundSite);
				}
				children.add(child);
			}

		}
		return children;
	}

	private List<Coordinate> convertTrack(PointList pl) {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();

		List<Double[]> coordinateMess = pl.toGeoJson();
		for (Double[] point : coordinateMess) {
			coordinates.add(new Coordinate(point[0], point[1]));
		}

		return coordinates;
	}

	@Override
	public String toString() {
		return "Traveled from " + parent.baseNodeId + " to " + baseNodeId
				+ ": " + secondsTraveledSoFar;
	}

}
