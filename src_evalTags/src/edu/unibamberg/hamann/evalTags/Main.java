package edu.unibamberg.hamann.evalTags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperAPI;
import com.graphhopper.routing.util.EncodingManager;

public class Main {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) {

		// // build graph files:
		// // Creating and saving the graph
		// EncodingManager em = new EncodingManager("FOOT");
		// GraphBuilder gb = new
		// GraphBuilder(em).setLocation("./data").setStore(true);
		// GraphStorage graph = gb.create();
		// // Make a weighted edge between two nodes. False means the edge is
		// directed.
		// //graph.edge(fromId, toId, cost, false);
		// // Store to disc
		// graph.flush();

		EncodingManager em = new EncodingManager("FOOT");

		GraphHopper gh = new GraphHopper().forServer();

		gh.setEncodingManager(em);

		gh.setOSMFile("./data/europe_germany_bayern_oberfranken.pbf");
		gh.setGraphHopperLocation("./data");

		gh.setCHShortcuts("fastest");

		GraphHopper tgh = gh.importOrLoad();

		// Initialization for the API to be used on a desktop or server pc
		// GraphHopperAPI ghapi = new GraphHopper().forServer();
		// // if you use example configuration you need to enable CH shortcuts
		// // ((GraphHopper) gh).setCHShortcuts(true, true);
		//
		// ghapi.load("./data");
		// // Offline API on Android
		// GraphHopperAPI gh = new GraphHopper().forMobile();
		// gh.load("graph-hopper-folder");

		// // Online: Connect to your own hosted graphhopper web service, for
		// GraphHopperWeb see the 'web' sub project
		// GraphHopperAPI gh = new GraphHopperWeb();
		// gh.load("http://your-graphhopper-service.com/api");

		double fromLat = 49.90439;
		double fromLon = 10.85892;
		double toLat = 49.90288;
		double toLon = 10.87301;
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			GHRequest request = new GHRequest(fromLat, fromLon, toLat, toLon);
			request.setAlgorithm("dijkstrabi");
			request.setVehicle("FOOT");			
			GHResponse response = tgh.route(request);
		}
		long stop = System.currentTimeMillis();
		//System.out.print(response.getDistance() + "m " + (stop - start) + "ms");
		System.out.print("Eval 1k: "+ (stop - start) + "ms");

	}

}
