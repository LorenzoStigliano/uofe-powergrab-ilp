package uk.ac.ed.inf.powergrab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Map {
	/*
	 * This is the class for the Map that the drone will play in. Keeps as variables list_stations, list of features,
	 * DD, MM, YYY of map and the feature collection.
	 */
	
	final private FeatureCollection fc;
	final private ArrayList<Feature> list_features; 
	final private ArrayList<Station> list_stations;
	final private String DD;
	final private String MM;
	final private String YYYY;
	
	public Map(String DD , String MM , String YYYY) throws IOException{
		
		this.DD = DD;
		this.MM = MM;
		this.YYYY = YYYY;
		
		// GeoJson configurations to get the map.
		String mapString = "http://homepages.inf.ed.ac.uk/stg/powergrab/"+(YYYY)+"/"+(MM)+"/"+(DD)+"/powergrabmap.geojson";
		URL mapUrl =  new URL(mapString);
		HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		InputStream stream = conn.getInputStream();
		
		// Get the map source and convert it into a string.
		String mapSource = convert(stream);
		
		// Get the feature collection form the mapSource.
		this.fc = FeatureCollection.fromJson(mapSource);
		//Get all the features form the feature collection.
		
		this.list_features = (ArrayList<Feature>) fc.features();
		this.list_stations = new ArrayList<Station>();
		
		// Make an ArrayList of stations from the given features.
		// This iterates over all features and changes each one individually into a station and adds them to list_stations.
		for(int i = 0 ; i< list_features.size() ; i++) {
			Station new_station = new Station(list_features.get(i));
			getList_stations().add(new_station);
		}
	}
	
	public String convert(InputStream inputStream) throws IOException {
		/*
		 * This method converts from an input stream into a String. Used for the map source.
		 */
		
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {	
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
		}
		return stringBuilder.toString();
	}

	public void createJsonFile(ArrayList<Position> positons , String drone_type, String DD, String MM, String YYYY) throws FileNotFoundException {
		/*
		 * This method creates the .geojson file used for the drone.
		 */
		
		// Create a PrintWriter
		PrintWriter return_json = new PrintWriter(String.format("%s-%s-%s-%s.geojson", drone_type,DD,MM,YYYY));
		// Make a Json feature.
		String json_feature = "{\"type\": \"Feature\",\"properties\": {},\"geometry\": {\"type\": \"LineString\",\"coordinates\": [ ] }}";
		Feature new_feature = Feature.fromJson(json_feature);
		
		LineString a  = (LineString) new_feature.geometry();
		List<Point> list_of_points =  a.coordinates();
		
		// Add all the positions to the list_of_points.
		for(int i= 0 ; i < positons.size(); i++) {
			Point new_point = Point.fromLngLat(positons.get(i).getLongitude() , positons.get(i).getLatitude());
			list_of_points.add(new_point);
		}
		
		// Add this feature to the list_features.
		list_features.add(new_feature);
		
		// Return the original feature collection with the new added feature.
		return_json.println(fc.toJson());
		return_json.close();
	}
	
	/*
	 * Below we have all the setters and getters for all the class variables, they should be self explanatory.
	 */

	public ArrayList<Station> getList_stations() {
		return list_stations;
	}

	public String getDD() {
		return DD;
	}

	public String getMM() {
		return MM;
	}

	public String getYYYY() {
		return YYYY;
	}

}
