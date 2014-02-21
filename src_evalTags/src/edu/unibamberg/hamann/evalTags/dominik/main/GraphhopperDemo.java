package edu.unibamberg.hamann.evalTags.dominik.main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.graphhopper.reader.OSMReader;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FastestWeighting;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.LevelGraphStorage;
import com.graphhopper.storage.index.Location2IDFullIndex;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.util.PointList;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import edu.unibamberg.hamann.evalTags.dominik.model.PlaceMark;
import edu.unibamberg.hamann.evalTags.dominik.util.GeometryDrawer;
import edu.unibamberg.hamann.evalTags.dominik.util.GeometryHelper;
import edu.unibamberg.hamann.evalTags.dominik.util.TimeGeographicSpaceOfPossibilites;

public class GraphhopperDemo {

	private static final String PLANET_FILE = "./data/europe_germany_bayern_oberfranken.pbf";
	public static final String MODE_OF_TRANSPORTATION = "FOOT";

	private LevelGraphStorage graph;
	private LocationIndex index;
	private EncodingManager em;

	public GraphhopperDemo() {
		initializeGraph();

	}

	public Path computeNetworkDistance(Coordinate from, Coordinate to) {

		int fromId = index.findID(from.y, from.x);
		int toId = index.findID(to.y, to.x);

		Dijkstra d = new Dijkstra(graph, em.getEncoder(MODE_OF_TRANSPORTATION),
				new FastestWeighting(em.getEncoder(MODE_OF_TRANSPORTATION)));
		return d.calcPath(fromId, toId);
	}

	public static void main(String[] args) {

		// Test-Setup
		Coordinate from = new Coordinate(10.888561, 49.890508); // Geyerswörth
		Coordinate to = new Coordinate(10.886935, 49.891479); // Altes Rathaus
		Coordinate toCheck = new Coordinate(10.884355, 49.889333); // Obere
																	// Pfarre
		// Einfaches Routing
		GraphhopperDemo ghd = new GraphhopperDemo();
		Path p = ghd.computeNetworkDistance(from, to);

		System.out.println(p.getDistance()+"m");
		System.out.println(p.getExtractTime()+"sec");
		System.out.println(p.calcPoints()+"points");
		System.out.println(p.calcInstructions()+"isntructions");

		Coordinate[] way = ghd.convertTrack(p.calcPoints()).toArray(
				new Coordinate[p.calcPoints().size()]);

		GeometryDrawer gtv = new GeometryDrawer("routing.html", "blue", 16);
		Geometry result = GeometryHelper.gf.createLineString(way);
		gtv.addGeometryToDraw(result);
		gtv.addPlacesToDraw(new PlaceMark(from));
		gtv.addPlacesToDraw(new PlaceMark(to));
		gtv.draw();

		long startCounter = System.currentTimeMillis();
		
		// Zeitgeographisches Netzwerk
		TimeGeographicSpaceOfPossibilites networkPossibilites = new TimeGeographicSpaceOfPossibilites(
				10, 1.0);
		result = networkPossibilites.deriveFootprint(from, null, null);
		
		long stopCounter = System.currentTimeMillis();
		
		System.out.println("Calctime Network: "+(stopCounter-startCounter));
		

		startCounter = System.currentTimeMillis();
		
		// Punkt erreichbar?
		Geometry g = GeometryHelper.gf.createPoint(toCheck);
		System.out.println(toCheck + " erreichbar? "
				+ networkPossibilites.isReachable(g));

		stopCounter = System.currentTimeMillis();
		
		System.out.println("Calctime reachable: "+(stopCounter-startCounter));
		
		gtv = new GeometryDrawer("way_possibilites.html", "green", 16);
		gtv.addGeometryToDraw(result);
		gtv.addPlacesToDraw(new PlaceMark(from));
		gtv.addPlacesToDraw(new PlaceMark(toCheck));
		gtv.draw();

	}

	private void initializeGraph() {
		em = new EncodingManager(MODE_OF_TRANSPORTATION);
		GraphBuilder gb = new GraphBuilder(em)
				.setLocation("graphhopper-folder").setStore(true);
		graph = gb.levelGraphBuild();
		// OSM load
		try {
			OSMReader read = new OSMReader(graph, 30000);
			read.setEncodingManager(em);
			read.doOSM2Graph(new File(PLANET_FILE));
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		}
		// Store to disc, perhaps omissable, but nice to see...
		graph.flush();

		index = new Location2IDFullIndex(graph);
		if (!index.loadExisting()) {
			throw new IllegalStateException(
					"location2id index cannot be loaded!");
		}
	}

	private List<Coordinate> convertTrack(PointList pl) {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();

		List<Double[]> coordinateMess = pl.toGeoJson();
		for (Double[] point : coordinateMess) {
			coordinates.add(new Coordinate(point[0], point[1]));
		}

		return coordinates;
	}

}
