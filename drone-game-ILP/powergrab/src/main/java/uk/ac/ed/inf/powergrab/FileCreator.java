package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.io.PrintWriter;

public class FileCreator {
	/*
	 * This is the file creator class that makes both the .txt and .geojson.
	 * Takes a drone and a map.
	 */
	private Drone drone;
	private Map map;
	
	public FileCreator(StatelessDrone drone, Map map) {
		this.drone = drone;
		this.map = map;
	}
	public FileCreator(StatefulDrone drone, Map map) {
		this.drone = drone;
		this.map = map;
	}
	
	public void createTxtFile_Movements() throws IOException {
		/*
		 * Creates the movements of the drone.
		 * It uses the drone variable movements which is a ArrayList with String and every element of the 
		 * List is a line in the output file.
		 */
		PrintWriter return_txt = new PrintWriter(String.format("%s-%s-%s-%s.txt",drone.getDrone_type(),map.getDD(),map.getMM(),map.getYYYY()));
		for(int i = 0 ; i < drone.getMovements().size(); i++) {
			return_txt.println(drone.getMovements().get(i));
		}
		return_txt.close();
	}
	
	public void createJsonFile_Flight() throws IOException  {
		/*
		 * Calls the GeoJsonMap method createJsonFile() to make the .geojson file.
		 */
		map.createJsonFile(drone.getPositions_of_flight(),drone.getDrone_type(),map.getDD(),map.getMM(),map.getYYYY());
	}
	

}
