package edu.unibamberg.hamann.evalTags;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import edu.unibamberg.hamann.evalTags.dominik.util.GeometryHelper;
import edu.unibamberg.hamann.evalTags.dominik.util.TimeGeographicSpaceOfPossibilites;

public class Evaluator {

	final double MAX_WALK_DISTANCE = 700.0;
	final static double BBOX_MIN_RADIUS = 2500.0;

	public static void main(String[] args) {

		List<String> tagList = new LinkedList<String>();
		List<Double> resultList = new LinkedList<Double>();
		double result = 0.0;

		// tag list
		tagList.add("highway=bus_stop");
		tagList.add("highway=crossing");
		tagList.add("amenity=parking");
		tagList.add("amenity=school");
		tagList.add("amenity=place_of_worship");
		tagList.add("shop=supermarket");
		tagList.add("shop=bakery");
		tagList.add("shop");

		// basic setup

		Evaluator eval = new Evaluator();

		GeoCoordinate zuhause = new GeoCoordinate(49.90429, 10.85929);

		BoundingBox bbox = new BoundingBox(zuhause, BBOX_MIN_RADIUS);

		System.out.println("BBOX: [" + bbox + "]");

		long startWatch = System.currentTimeMillis();

		for (String tag : tagList) {
			result = eval.evaluateBoundingBox(bbox, tag);
			resultList.add(result);
			System.out.println("Result: " + result + " Took: "
					+ (System.currentTimeMillis() - startWatch));
			startWatch = System.currentTimeMillis();
		}

		// print results

		for (int i = 0; i < tagList.size(); i++) {
			System.out.println("[" + tagList.get(i) + "]@[" + resultList.get(i)
					+ "]");
		}

	}

	public double evaluateBoundingBox(BoundingBox bbox, String tag) {
		double resultValue = 0.0;

		List<Long> resultList = new LinkedList<Long>();
		long currentResult = 0;

		List<GeoCoordinate> gcListToBeChecked = GeogameAPI
				.getCoordinatesFromGeoJson(bbox, tag);
		List<GeoCoordinate> gcListToBeUsed = GeogameAPI
				.getCoordinatesFromGeoJson(bbox.expand(MAX_WALK_DISTANCE), tag);

		long iteratemax = gcListToBeChecked.size();
		long iteratecur = 0;

		System.out.println("Node Count: " + iteratemax + " (Using workNodes: "
				+ gcListToBeUsed.size() + " MAXDIST@" + MAX_WALK_DISTANCE
				+ "m)");

		// iterate over each currentNode
		for (GeoCoordinate gc : gcListToBeChecked) {
			iteratecur++;

			//System.out.println(gc);
			long startWatch = System.currentTimeMillis();

			currentResult = getNodesCountInReachGraphhopper(gcListToBeUsed, gc);
			// currentResult = getNodesCountInReachTimeGraph(osmNodeList,
			// currentNode);

			resultList.add(currentResult);

			System.out.println("[" + iteratecur + "/" + iteratemax + "]@"
					+ (int) (iteratecur * 100 / iteratemax) + "% - took: ["
					+ (System.currentTimeMillis() - startWatch) + "]ms C:"
					+ currentResult+" DEBUG:<<"+gc+">>");
		}

		// iterated over all elemenmts

		// calc AVG

		for (long element : resultList) {
			resultValue += element;
		}

		resultValue /= resultList.size();

		return resultValue;
	}

	private long getNodesCountInReachTimeGraph(List<OSMAPINode> osmNodeList,
			OSMAPINode currentNode) {

		// start on -1 as A->A is included
		long count = -1;

		// Zeitgeographisches Netzwerk
		TimeGeographicSpaceOfPossibilites networkPossibilites = new TimeGeographicSpaceOfPossibilites(
				10, 1.0);

		networkPossibilites.deriveFootprint(getCoordinateFromNode(currentNode),
				null, null);

		for (OSMAPINode listNode : osmNodeList) {
			// Punkt erreichbar?
			Geometry g = GeometryHelper.gf
					.createPoint(getCoordinateFromNode(listNode));
			if (networkPossibilites.isReachable(g)) {
				count++;
			}
		}
		return count;
	}

	Coordinate getCoordinateFromNode(OSMAPINode node) {
		double lat = 0.0;
		double lon = 0.0;

		lat = Double.parseDouble(node.getLat());
		lon = Double.parseDouble(node.getLon());

		Coordinate currentCoord = new Coordinate(lon, lat);

		return currentCoord;

	}

	GeoCoordinate getGeoCoordinateFromNode(OSMAPINode node) {
		double lat = 0.0;
		double lon = 0.0;

		lat = Double.parseDouble(node.getLat());
		lon = Double.parseDouble(node.getLon());

		GeoCoordinate gc = new GeoCoordinate(lat, lon);

		return gc;

	}

	private long getNodesCountInReachGraphhopper(
			List<GeoCoordinate> osmNodeList, GeoCoordinate currentNode) {

		// start on -1 as A->A is included
		long count = 0;
		// 10min walk distance @ 4km/h ~ 700meters

		// setup
		EncodingManager em = new EncodingManager("FOOT");

		GraphHopper gh = new GraphHopper().forServer();

		gh.setEncodingManager(em);

		gh.setOSMFile("./data/europe_germany_bayern_oberfranken.pbf");
		gh.setGraphHopperLocation("./data");

		gh.setCHShortcuts("fastest");

		GraphHopper tgh = gh.importOrLoad();

		// loaded
		GeoCoordinate currentNodeGeo = currentNode;

		for (GeoCoordinate listNode : osmNodeList) {

			if (listNode.equals(currentNode)) {
				//System.out.println("found myself");
			} else {

				GeoCoordinate toNode = listNode;

				GHRequest request = new GHRequest(currentNodeGeo.getLatitude(),
						currentNodeGeo.getLongitude(), toNode.getLatitude(),
						toNode.getLongitude());
				request.setAlgorithm("dijkstrabi");
				request.setVehicle("FOOT");
				GHResponse response = tgh.route(request);

				if (response.getDistance() < MAX_WALK_DISTANCE) {
					count++;
				}
			}
		}
		return count;
	}

}
