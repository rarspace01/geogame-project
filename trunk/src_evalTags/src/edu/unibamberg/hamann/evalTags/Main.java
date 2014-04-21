package edu.unibamberg.hamann.evalTags;

import java.util.LinkedList;
import java.util.List;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;

/**
 * 
 * @author denis
 *
 */
public class Main {

	final static double BBOX_MIN_RADIUS = 2500.0;
	public static final String TAG = "Main";

	public static int activeThreads = 0;

	/**
	 * main method for the evaluation of tags
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		long heapsize = Runtime.getRuntime().totalMemory();
		System.out.println("heapsize is::" + heapsize);

		List<String> tagList = new LinkedList<String>();
		// List<Double> resultList = new LinkedList<Double>();
		// double result = 0.0;

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

		// GH setup

		// setup routing defaults
		EncodingManager em = new EncodingManager("FOOT");

		GraphHopper gh = new GraphHopper().forServer();

		gh.setEncodingManager(em);

		// use the germany file
		gh.setOSMFile("./data/germany-latest.osm.pbf");
		gh.setGraphHopperLocation("./data");

		gh.setCHShortcuts("fastest");

		GraphHopper tgh = gh.importOrLoad();

		// basic setup

		// Evaluator eval = new Evaluator();

		GeoCoordinate zuhause = new GeoCoordinate(49.90429, 10.85929);

		BoundingBox bbox = new BoundingBox(zuhause, BBOX_MIN_RADIUS);

		System.out.println("BBOX: [" + bbox + "]");

		// iterate over the tags. Each tags starts a thread. a maximum of n is
		// active at runtime. where n is the logical CPU count of the os
		for (String tag : tagList) {

			while (activeThreads >= Helper.getCPUCount()) { // Helper.getCPUCount()
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			System.out.println("Starting Thread with tag: [" + tag + "]");

			new Thread(new EvaluatorTask(tgh, bbox, tag)).start();
			activeThreads++;
			// result = eval.evaluateBoundingBox(bbox, tag);
			// resultList.add(result);

		}

	}

}
