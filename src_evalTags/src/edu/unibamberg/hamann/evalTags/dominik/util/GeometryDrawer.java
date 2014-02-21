package edu.unibamberg.hamann.evalTags.dominik.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

import edu.unibamberg.hamann.evalTags.dominik.model.PlaceMark;

public class GeometryDrawer {

	public final static String PLACE_MAP_HEADER_PATH = "template.head";
	public final static String PLACE_MAP_FOOT_PATH = "template.foot";

	// private static int colorCount = 0;
	// private static String[] colors = { "blue", "red", "green", "purple",
	// "yellow", "orange" };

	private String filename = "compare.html";
	private String colorName = "blue";
	private int zoomLevel = 16;

	private List<Geometry> geometries = new ArrayList<Geometry>();
	private List<PlaceMark> places = new ArrayList<PlaceMark>();

	private Geometry bb = null;

	public GeometryDrawer(String filename, String colorName, int zoomLevel) {
		if (filename != null)
			this.filename = filename;
		if (colorName != null)
			this.colorName = colorName;
		if (zoomLevel > 0 && zoomLevel < 19)
			this.zoomLevel = zoomLevel;

	}

	public GeometryDrawer() {

	}

	public void addGeometryToDraw(Geometry geometry) {

		// TODO: Da sollte irgendwann auch eine Füllung dazu kommen etc.

		// Abarbeiten von Multigeometrien
		if (geometry instanceof GeometryCollection) {
			GeometryCollection gc = (GeometryCollection) geometry;
			for (int i = 0; i < gc.getNumGeometries(); i++) {
				geometries.add(gc.getGeometryN(i));
			}
		} else {
			geometries.add(geometry);
		}

		// BoundingBox für Fokus
		if (bb == null) {
			bb = geometry;
		} else {
			bb = bb.union(geometry);
		}
	}

	public void addPlacesToDraw(PlaceMark p) {
		places.add(p);
	}

	public void setColorByName(String colorName) {
		this.colorName = colorName;
	}

	public void draw() {

		// draw on Map
		try {

			// Header
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					filename)));

			BufferedReader reader = new BufferedReader(new FileReader(new File(
					PLACE_MAP_HEADER_PATH)));
			while (reader.ready()) {
				writer.write(reader.readLine() + "\n");
			}
			reader.close();

			// Content

			drawOutline(writer);
			drawPlaces(writer);

			writer.write("map.setCenter(new OpenLayers.LonLat("
					+ bb.getCentroid().getX()
					+ ", "
					+ bb.getCentroid().getY()
					+ ").transform(new OpenLayers.Projection(\"EPSG:4326\"),	map.getProjectionObject()), "
					+ zoomLevel + ");");

			// Footer
			reader = new BufferedReader(new FileReader(new File(
					PLACE_MAP_FOOT_PATH)));
			while (reader.ready()) {
				writer.write(reader.readLine() + "\n");
			}
			reader.close();

			writer.flush();
			writer.close();

		} catch (IOException e) {
			System.err
					.println("Exportieren der Karteninformation schlug fehl: "
							+ e.getLocalizedMessage());
		}

	}

	private void drawPlaces(BufferedWriter writer) throws IOException {

		int count = 0;

		// Define Marker
		writer.write("var markers = new OpenLayers.Layer.Markers( \"Markers\");");

		for (PlaceMark p : places) {

			// write markers
			writer.write("var cache"
					+ count
					+ " = new OpenLayers.LonLat("
					+ p.getPinPoint().x
					+ ", "
					+ p.getPinPoint().y
					+ ").transform(new OpenLayers.Projection(\"EPSG:4326\"),map.getProjectionObject());");
			writer.write("markers.addMarker(new OpenLayers.Marker(cache"
					+ count + "));");

			count++;

		}

		// Make Markers visible
		writer.write("map.addLayer(markers);");

	}

	private void drawOutline(BufferedWriter writer) throws IOException {

		int count = 0;

		for (Geometry geometry : geometries) {

			// Sammle ihre Geometrie auf
			writer.write(convertToMultiLine("_outline" + count, geometry, true)
					+ "\n\n");

			// Setze die Variablen
			writer.write("vectorLayer1.addFeatures(lineFeature_outline" + count
					+ ");\n map.addLayer(vectorLayer1);\n");

			// colorCount++;
		}

	}

	private String convertToMultiLine(String id, Geometry outline,
			boolean optimized) {

		String varName = "points" + id;

		String result = "var " + varName + " = ";
		List<String> pointList = new ArrayList<String>();

		String line = "new OpenLayers.Geometry.LineString([";

		// Erzeuge für alle Stützpunkte ein langes Array, das wie der Stop
		// heißt
		for (Coordinate stf : outline.getCoordinates()) {

			line += "new OpenLayers.Geometry.Point("
					+ stf.x
					+ ","
					+ stf.y
					+ ").transform(new OpenLayers.Projection(\"EPSG:4326\"),map.getProjectionObject() ),";

		}

		line = line.substring(0, line.length() - 1);
		line += "])";

		pointList.add(line);
		result += pointList + "\n\n";

		result += "var lineFeature" + id + " = new OpenLayers.Feature.Vector("
				+ "new OpenLayers.Geometry.MultiLineString(" + varName
				+ "),{name: \"\",favColor: '" + colorName + "'})";

		return result;

	}
}
