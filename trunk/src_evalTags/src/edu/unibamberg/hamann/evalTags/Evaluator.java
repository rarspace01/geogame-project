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
 * @author denis
 *
 */
public class Evaluator {

	final double MAX_WALK_DISTANCE = 700.0;
	public static final String TAG = "Evaluator";
	final static double BBOX_MIN_RADIUS = 2500.0;

	public static void main(String[] args) {

		List<String> tagList = new LinkedList<String>();
		List<Double> resultList = new LinkedList<Double>();
		double result = 0.0;

		// tag list
		tagList.add("highway=bus_stop");
		tagList.add("highway=crossing");
		tagList.add("highway=traffic_signals");
		tagList.add("highway=steps");
		tagList.add("highway=cycleway");
		tagList.add("bridge=yes");
		tagList.add("power=pole");
		// tagList.add("building=house");
		// tagList.add("building=residential");
		tagList.add("building=garage");
		// tagList.add("building=apartments");
		tagList.add("landuse=farmland");
		tagList.add("landuse=cemetery");
		tagList.add("amenity=parking");
		tagList.add("amenity=school");
		tagList.add("amenity=place_of_worship");
		tagList.add("shop=supermarket");
		tagList.add("shop=bakery");
		tagList.add("natural=water");
		tagList.add("natural=tree");
		tagList.add("natural=wood");
		tagList.add("natural=grassland");
		// tagList.add("shop");
		// tagList.add("building");
		// tagList.add("amenity");

		// basic setup

		Evaluator eval = new Evaluator();

		GeoCoordinate zuhause = new GeoCoordinate(49.90429, 10.85929);

		BoundingBox bbox = new BoundingBox(zuhause, BBOX_MIN_RADIUS);

		System.out.println("BBOX: [" + bbox + "]");

		long startWatch = System.currentTimeMillis();

		for (String tag : tagList) {
			result = eval.evaluateBoundingBox(bbox, tag);
			resultList.add(result);
			Helper.msgLog(
					TAG,
					"Result: " + result + " Took: "
							+ (System.currentTimeMillis() - startWatch));
			startWatch = System.currentTimeMillis();
		}

		// print results

		for (int i = 0; i < tagList.size(); i++) {
			Helper.msgLog(TAG, "[" + tagList.get(i) + "]@[" + resultList.get(i)
					+ "]");
		}

	}

	public double evaluateBoundingBox(BoundingBox bbox, String tag) {
		
		// setup
		EncodingManager em = new EncodingManager("FOOT");

		GraphHopper gh = new GraphHopper().forServer();

		gh.setEncodingManager(em);

		gh.setOSMFile("./data/germany-latest.osm.pbf");
		gh.setGraphHopperLocation("./data");

		gh.setCHShortcuts("fastest");

		GraphHopper tgh = gh.importOrLoad();
		
		double resultValue = 0.0;

		List<Long> resultList = new LinkedList<Long>();
		long currentResult = 0;

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

			currentResult = getNodesCountInReachGraphhopper(gcListToBeUsed, gc,tgh);
			// currentResult = getNodesCountInReachTimeGraph(osmNodeList,
			// currentNode);

			resultList.add(currentResult);

			System.out.println("[" + iteratecur + "/" + iteratemax + "]@"
					+ (int) (iteratecur * 100 / iteratemax) + "% - took: ["
					+ (System.currentTimeMillis() - startWatch) + "]ms C:"
					+ currentResult + " DEBUG:<<" + gc + ">>");
		}

		// iterated over all elemenmts

		// calc AVG

		Map<Long,Long> distributionMap = new HashMap<Long,Long>();
		
		for (long element : resultList) {
			
			if(distributionMap.containsKey(element)){
				distributionMap.put(element,distributionMap.get(element)+1);
			}else{
				distributionMap.put(element, (long) 1);
			}
			
			resultValue += element;
		}

		resultValue /= resultList.size();

		for(long currentKey :distributionMap.keySet()){
			System.out.println("currentKey: c["+currentKey+"]["+distributionMap.get(currentKey)+"]");
		}
		
		return resultValue;
	}

//	private long getNodesCountInReachTimeGraph(List<OSMAPINode> osmNodeList,
//			OSMAPINode currentNode) {
//
//		// start on -1 as A->A is included
//		long count = -1;
//
//		// Zeitgeographisches Netzwerk
//		TimeGeographicSpaceOfPossibilites networkPossibilites = new TimeGeographicSpaceOfPossibilites(
//				10, 1.0);
//
//		networkPossibilites.deriveFootprint(getCoordinateFromNode(currentNode),
//				null, null);
//
//		for (OSMAPINode listNode : osmNodeList) {
//			// Punkt erreichbar?
//			Geometry g = GeometryHelper.gf
//					.createPoint(getCoordinateFromNode(listNode));
//			if (networkPossibilites.isReachable(g)) {
//				count++;
//			}
//		}
//		return count;
//	}

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
			List<GeoCoordinate> osmNodeList, GeoCoordinate currentNode, GraphHopper tgh) {

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

}
