package edu.unibamberg.hamann.evalTags;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.unibamberg.hamann.evalTags.io.HttpHelper;

public class GeogameAPI {

//	// simple test
//	public static void main(String[] args) {
//		GeoCoordinate zuhause = new GeoCoordinate(49.90429, 10.85929);
//
//		BoundingBox bbox = new BoundingBox(zuhause, 2500);
//
//		for(GeoCoordinate gc: getCoordinatesFromGeoJson(bbox, "highway=bus_stop")){
//			System.out.println(gc);
//		}
//
//	}

	public static List<GeoCoordinate> getCoordinatesFromGeoJson(String url,
			BoundingBox bbox, String tag) {
		List<GeoCoordinate> returnList = new LinkedList<GeoCoordinate>();

		String accessUrl = "";
		String tmpSite = "";
		String tmpMatch = "";

		accessUrl += url + "?s=" + bbox.getSS() + "&w=" + bbox.getSW() + "&n="
				+ bbox.getSN() + "&e=" + bbox.getSE() + "&tag=" + tag;
		System.out.println(accessUrl);

		tmpSite = HttpHelper.getPage(accessUrl);

		System.out.println(tmpSite);

		Pattern basePattern = Pattern
				.compile("\\[([0-9.]*),([0-9.]*)\\][0-9a-zA-Z\"{},:]*id\":\"([0-9]*)\"}}");

		Pattern number = Pattern.compile("([0-9]+)");
		
		Matcher matcher = basePattern.matcher(tmpSite);

		double lat = 0.0,lon = 0.0;
		long nodeId=0;
		
		while (matcher.find()) {
			tmpMatch = matcher.group();
			
			lat = Double.parseDouble(tmpMatch.substring(tmpMatch.indexOf(",")+1, tmpMatch.indexOf("]")));
			lon = Double.parseDouble(tmpMatch.substring(tmpMatch.indexOf("[")+1, tmpMatch.indexOf(",")));
			
			Matcher numberMatcher = number.matcher(tmpMatch.substring(tmpMatch.indexOf("\"id\":\"")));
			
			//System.out.println("!!! "+tmpMatch.substring(tmpMatch.indexOf("\"id\":\""))+" !!!");
			
			while(numberMatcher.find()){
				if(numberMatcher.group().length()>0){
					//System.out.println(numberMatcher.group());
					nodeId = Long.parseLong(numberMatcher.group());
				}
			}
			
			
			
			GeoCoordinate gc = new GeoCoordinate(nodeId,lat,lon);
			
			returnList.add(gc);
		}

		return returnList;
	}

	public static List<GeoCoordinate> getCoordinatesFromGeoJson(
			BoundingBox bbox, String tag) {
		return getCoordinatesFromGeoJson(
				"http://localhost:3000/overpass_api/getLocation.json", bbox,
				tag);
	}

	// http://localhost:3000/overpass_api/getLocation.json?s=49.88674368396232&w=10.799689292907715&n=49.921777822392315&e=10.917878150939941&tag=highway=bus_stop

}
