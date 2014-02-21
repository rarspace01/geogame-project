package edu.unibamberg.hamann.evalTags.dominik.util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.graphhopper.reader.OSMReader;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.LevelGraphStorage;
import com.graphhopper.storage.index.Location2IDFullIndex;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.util.EdgeExplorer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import edu.unibamberg.hamann.evalTags.dominik.model.OsmPoi;
import edu.unibamberg.hamann.evalTags.dominik.model.SearchNode;

public class TimeGeographicSpaceOfPossibilites {

	private static final String PLANET_FILE = "./data/europe_germany_bayern_oberfranken.pbf";
	public static final String MODE_OF_TRANSPORTATION = "FOOT";

	private LevelGraphStorage graph;
	private LocationIndex index;
	private EncodingManager em;

	private double secondLimit;
	private double velocity;

	private Geometry spaceOfPossibilites = null;

	private TreeSet<Integer> includedList = new TreeSet<Integer>();

	/**
	 * Make abstract class GraphBasedFootprint (ConvexNetworkHull) during
	 * import!
	 * 
	 */

	public TimeGeographicSpaceOfPossibilites(double minutesLeft, double velocity) {

		secondLimit = minutesLeft * 60; // 60s
		this.velocity = velocity;

		initializeGraph();

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

	/**
	 * places my be empty or null
	 * 
	 * @param from
	 * @param to
	 * @param places
	 * @return
	 */
	public Geometry deriveFootprint(Coordinate from, Coordinate to,
			List<OsmPoi> places) {

		if (places == null) {
			places = new ArrayList<OsmPoi>();
		}

		// identify nearest positions
		// your current position
		int fromId = index.findID(from.y, from.x);
		// your car
		int toId = -1;
		if (to != null) {
			toId = index.findID(to.y, to.x);
		}
		for (OsmPoi place : places) {
			place.setAssociatedNodeId(index.findID(place.getPinPoint().y,
					place.getPinPoint().x));
		}

		// Uniforme Kostensuche: günstigste Kosten zuerst, bei Gleichheit egal:
		// ID
		TreeSet<SearchNode> searchFront = new TreeSet<SearchNode>(
				new Comparator<SearchNode>() {
					@Override
					public int compare(SearchNode o1, SearchNode o2) {
						int signum = (int) Math.signum(o1
								.getSecondsTraveledSoFar()
								- o2.getSecondsTraveledSoFar());
						if (signum == 0) {
							return o1.getBaseNodeId() - o1.getBaseNodeId();
						}
						return signum;
					}
				});

		EdgeExplorer explorer = graph.createEdgeExplorer();
		// calls to iter.getAdjNode(), getDistance() without calling next() will
		// cause undefined behaviour!

		// initialize
		SearchNode root = new SearchNode(fromId, null, null, 0, 0);
		Geometry resultGeometry = GeometryHelper.gf
				.createLineString(new Coordinate[0]);
		searchFront.add(root);

		while (!searchFront.isEmpty()) {

			// Uniforme Kostensuche: erlaubt das pruning bekannter Knoten:
			// allenfalls noch teurer zu erreichen
			SearchNode currentNode = searchFront.pollFirst();
			List<SearchNode> children = currentNode.generateChildren(explorer,
					graph, em, includedList, places, secondLimit, velocity,
					toId);

			// System.out.println("==========");
			for (SearchNode sn : children) {
				// System.out.println("From Node ID: "
				// + sn.getParent().getBaseNodeId() + " ("
				// + graph.getLongitude(sn.getParent().getBaseNodeId())
				// + ", "
				// + graph.getLatitude(sn.getParent().getBaseNodeId())
				// + ") to Node ID: " + sn.getBaseNodeId() + " ("
				// + graph.getLongitude(sn.getBaseNodeId()) + ", "
				// + graph.getLatitude(sn.getBaseNodeId()) + "): "
				// + sn.getSecondsTraveledSoFar()
				// + " meter.\nReal way (pillars:)"
				// + sn.getRealwayOnParentEdge());
				searchFront.add(sn);
				resultGeometry = GeometryHelper.merge(resultGeometry,
						GeometryHelper.gf.createLineString(sn
								.getRealwayOnParentEdge().toArray(
										new Coordinate[sn
												.getRealwayOnParentEdge()
												.size()])));
			}
			if (currentNode.getSite() != null) {
				// System.out.println("==== DRAWING PLACE ====");
				// System.out.println(currentNode.getSite().getSignature()
				// .getBoundary());
				resultGeometry = resultGeometry.union(currentNode.getSite()
						.getSignature().getBoundary());
			}

			includedList.add(currentNode.getBaseNodeId());

		}

		spaceOfPossibilites = resultGeometry;
		return spaceOfPossibilites;

	}

	/**
	 * 
	 * 
	 * @param g
	 * @return
	 */
	public boolean isReachable(Geometry g) {

		int nodeId = index.findID(g.getCentroid().getY(), g.getCentroid()
				.getX());
		return includedList.contains(nodeId);
	}
}
