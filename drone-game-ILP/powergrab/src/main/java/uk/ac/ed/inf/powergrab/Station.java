package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

public class Station {
	/*
	 * Station class which keeps the coins, power and position the station in the map.
	 */
	
	private double coins;
	private double power;
	private Position position;
	
	public Station(Feature feature) {
		/*
		 * Given a feature we extract the information we need and make a station.
		 */
		
		this.setCoins(feature.getProperty("coins").getAsDouble());
		this.setPower(feature.getProperty("power").getAsDouble());
		
		// Create a point for the given feature
		Point point_feature = (Point) feature.geometry();
		// From this point get the latitude and longitude.
		this.setPosition(new Position(point_feature.coordinates().get(1) , point_feature.coordinates().get(0)));
	}
	
	public void update_station(double transfer_power, double transfer_coins) {
		/*
		 * This method updates the stations coin and power.
		 */
		
		// This checks if the station is a negative one.
		if(getPower() < 0 && getCoins() < 0){
			
			double station_power = getPower() + transfer_power;
			double station_coins = getCoins() + transfer_coins;
			
			// This ensures that the power and the coins of this station are never greater than 0 after a transfer.
			if(station_power >= 0) {
				setPower(0);
			}
			if(station_power < 0) {
				setPower(station_power);
			}
			if(station_coins >= 0) {
				setCoins(0);
			}
			if(station_coins < 0) {
				setCoins(station_coins);
			}
		}
		// Otherwise we know that the the station is positive so we set both the coins and power to 0
		// after a transfer.
		else {
			setPower(0);
			setCoins(0);
		}	
	}
	
	/*
	 * Below we have all the setters and getters for all the class variables, they should be self explanatory.
	 */

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public double getCoins() {
		return coins;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}
}
