package uk.ac.ed.inf.powergrab;

public class Position {
	/*
	 * Position class which keeps the longitude and latitude of the drones and station in the map.
	 */
	
	private double latitude;
	private double longitude;
	
	//This is the distance it can travel.
	final private static double r = 0.0003;
	
	/*
	 * The variables below are r times cosine or sine of an angle(22.5,45,67.5) this is to only calculate these values once.
	 * Since they are used often in the nextPosition() method. 
	 */
	final private static double rcos22_5 = r*Math.cos(Math.toRadians(22.5));
	final private static double rcos45 = r*Math.cos(Math.toRadians(45));
	final private static double rcos67_5 = r*Math.cos(Math.toRadians(67.5));
	final private static double rsin22_5 = r*Math.sin(Math.toRadians(22.5));
	final private static double rsin45 = r*Math.sin(Math.toRadians(45));
	final private static double rsin67_5 = r*Math.sin(Math.toRadians(67.5));

	public Position(double latitude, double longitude) {
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}
	
	public Position nextPosition(Direction direction) { 
		/*
		 * Given a direction it calculates its new position after it moves in this direction.
		 * Returns the new position after it moves.
		 */
		
		double lat = getLatitude();
		double longi = getLongitude();
		
		switch(direction) {
			case N:{
				lat = getLatitude() + r;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case NNE:{
				longi = getLongitude() + rcos67_5;
				lat = getLatitude() + rsin67_5;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case NE:{
				longi = getLongitude() + rcos45;
				lat = getLatitude() + rsin45;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case ENE:{
				longi = getLongitude() + rcos22_5;
				lat = getLatitude() + rsin22_5;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case E:{
				longi = getLongitude() + r;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case ESE:{
				longi = getLongitude() + rcos22_5;
				lat = getLatitude() -rsin22_5;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case SE:{
				longi = getLongitude() + rcos45;
				lat = getLatitude() - rsin45;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case SSE:{
				longi = getLongitude() + rcos67_5;
				lat = getLatitude() - rsin67_5;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case S:{
				lat = getLatitude() - r;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case SSW:{
				longi = getLongitude() - rsin22_5;
				lat = getLatitude() -  rcos22_5;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case SW:{
				longi = getLongitude() - rsin45 ;
				lat = getLatitude() - rcos45 ;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case WSW:{
				longi = getLongitude() - rsin67_5 ;
				lat = getLatitude() - rcos67_5;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case W:{
				longi = getLongitude() - r;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case WNW:{
				longi = getLongitude() - rcos22_5;
				lat = getLatitude() + rsin22_5;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case NW:{
				longi = getLongitude() - rcos45;
				lat = getLatitude() + rsin45;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			case NNW:{
				longi = getLongitude() - rcos67_5;
				lat = getLatitude() + rsin67_5;
				Position final_postion = new Position(lat, longi);
				return final_postion;
			}
			default:
				return null;
		}
	}
	
	public boolean inPlayArea() { 
		/*
		 * Checks if the position is in the play area. Checks the latitude and longitude.
		 * Returns true if it is in play area otherwise returns false.
		 */
		
		if(getLatitude() < 55.946233 && getLatitude() > 55.942617 && getLongitude() < -3.184319 && getLongitude() > -3.192473) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public double distanceStation(Station station) {
		/*
		 * This finds the distance to a station given its current position.
		 * Uses euclidean distance to determine it.
		 */
		
		// Get latitude and longitude of the station.
		double longi_station = station.getPosition().getLongitude();
		double lat_station = station.getPosition().getLatitude();
		
		return Math.sqrt((getLatitude()-lat_station)*(getLatitude()-lat_station) + (getLongitude()-longi_station)*(getLongitude()-longi_station));
		
	}
	
	public double angleStation(Station station) {
		/*
		 * Calculates the angle between the position and a given station.
		 * Divides the unit circle into 4 quadrants and then calculates the angle in the corresponding 
		 * quadrant where the station lies in , uses angle rules with arctan(opposite/adjacent) to find angle.
		 * Returns the angle in degrees.
		 */
		
		double longi_station_x = station.getPosition().getLongitude();
		double lat_station_y = station.getPosition().getLatitude();
		
		// First quadrant with respect to the position.
		if(lat_station_y > getLatitude() && longi_station_x > getLongitude()) {
			double opposite = Math.abs(longi_station_x - getLongitude());
			double adjacent = Math.abs(lat_station_y - getLatitude());
			return Math.toDegrees(Math.atan(opposite/adjacent));
		}
		// Second quadrant with respect to the position.
		else if(lat_station_y < getLatitude() && longi_station_x > getLongitude()) {
			double opposite = Math.abs(lat_station_y - getLatitude());
			double adjacent = Math.abs(longi_station_x - getLongitude());
			return 90.0+ Math.toDegrees(Math.atan(opposite/adjacent));
		}
		// Third quadrant with respect to the position.
		else if(lat_station_y < getLatitude() && longi_station_x < getLongitude()) {
			double opposite = Math.abs(longi_station_x - getLongitude());
			double adjacent = Math.abs(lat_station_y - getLatitude());
			return 180.0 + Math.toDegrees(Math.atan(opposite/adjacent));
		}
		// Fourth quadrant with respect to the position.
		else {
			double opposite = Math.abs(lat_station_y - getLatitude());
			double adjacent = Math.abs(longi_station_x - getLongitude());
			return 270.0 + Math.toDegrees(Math.atan(opposite/adjacent));
		}
	}

	public static boolean inRange(double distance) {
		/*
		 * Checks if a distance is less than or equal to 0.00025. (This is the range for a transfer).
		 */
		
		if (distance <= 0.00025) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/*
	 * Below we have all the setters and getters for all the class variables, they should be self explanatory.
	 */

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
