package edu.unibamberg.hamann.evalTags;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * evaluator class
 * 
 * @author denis
 * 
 */
public class Evaluator {

	final double MAX_WALK_DISTANCE = 700.0;
	public static final String TAG = "Evaluator";
	

	public double evaluateBoundingBox(GraphHopper tgh, BoundingBox bbox, String tag) {



		double resultValue = 0.0;

		List<Double> resultList = new LinkedList<Double>();
		double currentResult = 0;

		List<GeoCoordinate> gcListToBeChecked = GeogameAPI
				.getCoordinatesFromGeoJson(bbox, tag);
		List<GeoCoordinate> gcListToBeUsed = GeogameAPI
				.getCoordinatesFromGeoJson(bbox.expand(MAX_WALK_DISTANCE), tag);

		long iteratemax = gcListToBeChecked.size();
		long iteratecur = 0;

		Helper.msgLog(TAG, "[" + tag + "]Node Count: " + iteratemax
				+ " (Using workNodes: " + gcListToBeUsed.size() + " MAXDIST@"
				+ MAX_WALK_DISTANCE + "m)");

		// iterate over each currentNode
		for (GeoCoordinate gc : gcListToBeChecked) {
			iteratecur++;

			// System.out.println(gc);
			long startWatch = System.currentTimeMillis();

//			currentResult = getNodesCountInReachGraphhopper(gcListToBeUsed, gc,
//					tgh);
			
			currentResult = getValueInReachGraphhopper(gcListToBeUsed, gc,
					tgh);
			
			
			
			// currentResult = getNodesCountInReachTimeGraph(osmNodeList,
			// currentNode);

			resultList.add(currentResult);

			if(iteratecur % 50 == 0){
			
			System.out.println("[" + iteratecur + "/" + iteratemax + "]@"
					+ (int) (iteratecur * 100 / iteratemax) + "% - took: ["
					+ (System.currentTimeMillis() - startWatch) + "]ms C:"
					+ currentResult + " DEBUG:<<" + gc + ">>");
			
			}
		}

		// iterated over all elemenmts

		// calc AVG

		Map<Double, Long> distributionMap = new HashMap<Double, Long>();

		for (double element : resultList) {

			if (distributionMap.containsKey(element)) {
				distributionMap.put(element, distributionMap.get(element) + 1);
			} else {
				distributionMap.put(element, (long) 1);
			}

			resultValue += element;
		}

		resultValue /= resultList.size();

		for (double currentKey : distributionMap.keySet()) {
			System.out.println("currentKey: c[" + currentKey + "]["
					+ distributionMap.get(currentKey) + "]");
		}

		Helper.msgLog(TAG, "[" + tag + "] Result["+resultValue+"]");
		
		return resultValue;
	}

	// private long getNodesCountInReachTimeGraph(List<OSMAPINode> osmNodeList,
	// OSMAPINode currentNode) {
	//
	// // start on -1 as A->A is included
	// long count = -1;
	//
	// // Zeitgeographisches Netzwerk
	// TimeGeographicSpaceOfPossibilites networkPossibilites = new
	// TimeGeographicSpaceOfPossibilites(
	// 10, 1.0);
	//
	// networkPossibilites.deriveFootprint(getCoordinateFromNode(currentNode),
	// null, null);
	//
	// for (OSMAPINode listNode : osmNodeList) {
	// // Punkt erreichbar?
	// Geometry g = GeometryHelper.gf
	// .createPoint(getCoordinateFromNode(listNode));
	// if (networkPossibilites.isReachable(g)) {
	// count++;
	// }
	// }
	// return count;
	// }

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

	private double getNodesCountInReachGraphhopper(
			List<GeoCoordinate> osmNodeList, GeoCoordinate currentNode,
			GraphHopper tgh) {

		// start on -1 as A->A is included
		long count = 0;
		// 10min walk distance @ 4km/h ~ 700meters

		// loaded
		GeoCoordinate currentNodeGeo = currentNode;

		for (GeoCoordinate listNode : osmNodeList) {

			if (listNode.equals(currentNode)) {
				// System.out.println("found myself");
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

	private double getValueInReachGraphhopper(List<GeoCoordinate> osmNodeList,
			GeoCoordinate currentNode, GraphHopper tgh) {

		// start on -1 as A->A is included
		double count = 0.0;
		// 10min walk distance @ 4km/h ~ 700meters

		// loaded
		GeoCoordinate currentNodeGeo = currentNode;

		for (GeoCoordinate listNode : osmNodeList) {

			if (listNode.equals(currentNode)) {
				// System.out.println("found myself");
			} else {

				GeoCoordinate toNode = listNode;

				GHRequest request = new GHRequest(currentNodeGeo.getLatitude(),
						currentNodeGeo.getLongitude(), toNode.getLatitude(),
						toNode.getLongitude());
				request.setAlgorithm("dijkstrabi");
				request.setVehicle("FOOT");
				GHResponse response = tgh.route(request);

				double localResult = response.getDistance();
				double localValue = 0;

				if (localResult >= 150) {
					localValue = 0.25;
					if (localResult > 200)
						localValue = 0.5;
					if (localResult > 250)
						localValue = 0.75;
					if (localResult > 300)
						localValue = 0.5;
					if (localResult > 600)
						localValue = 0.5;
					if (localResult > 700)
						localValue = 0;

				}else{
					
					localValue = -1;
					if(localResult<50){
						localValue = -2;
					}
					
				}

				count += localValue;

			}
		}
		if(count<0){
			count = 0;
		}
		return count;
	}

}
