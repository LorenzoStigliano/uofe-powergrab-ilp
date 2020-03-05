package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

abstract class Drone {
	
	private String drone_type;
	private double coins;
	private double power;
	private int moves;
	
	// Current position of drone.
	private Position current_position;
	
	// Map the drone is playing in.
	final private Map map;
	
	// This is a variable which resets after every move and looks one move ahead and find the directions the drone
	// should not go in. That is if it falls in range of a negative charging station after it has moved.
	private ArrayList<Direction> avoid_one_move;
	
	// Used to get random direction
	final private Random rnd;
	
	//These 2 variables are used for the output data in .txt and .geojson. 
	//Both drones must have them, but neither of the drones use them in stategies.
	private ArrayList<Position> positions_of_flight;
	private ArrayList<String> movements;
	
	public Drone(String DD , String MM , String YYYY, double lat, double longi, int seed,  String drone_type) throws IOException {
		
		this.setDrone_type(drone_type);
		this.current_position = new Position(lat,longi);
		this.setPositions_of_flight(new ArrayList<Position>());
		
		// Add the first position to the positions of flight.
		getPositions_of_flight().add(current_position);
		
		this.rnd = new Random(seed);
		this.setCoins(0.0);
		this.setMoves(0);
		this.setPower(250.0);
		this.map = new Map(DD,MM,YYYY); 
		this.setMovements(new ArrayList<String>());
		this.avoid_one_move = new ArrayList<Direction>();
	}
	
	//run() method which all the drones must implement. This is the movement of the drone.
	//moves is the number of moves you want the drone to run.
	public abstract void run(int moves);
	
	public void one_look_ahead() {
		/* 
		 * This method looks one move ahead and checks what directions the drone should NOT go in.
		 * Method is called after every move in order to reset "avoid_one_move".
		 * Iterates over all directions and checks if drone falls in range of a negative charging station 
		 * after movement in a direction if it does it adds this direction to "avoid_one_move".
		 */
		
		this.avoid_one_move = new ArrayList<Direction>();
		Direction[] directions = Direction.values();
		ArrayList<Station> stations = getMap().getList_stations();
		
		for(int i = 0 ; i < directions.length ; i++) {
			
			Position new_position = current_position.nextPosition(directions[i]);
			
			for(int k = 0 ; k < stations.size() ; k++) {
				
				Station test_station = stations.get(k);
				double dist_station = new_position.distanceStation(test_station);
				
				// Checks if distance is in range and the power station is negative.
				if(Position.inRange(dist_station) && test_station.getPower() < 0) {
						// add it to the ArrayList of directions to avoid.
						avoid_one_move.add(directions[i]);
				}
			}
		}
	}
	
	public void update_drone(Direction direction) {
		/* 
		 * This method updates the drone it moves the drone in the given direction. 
		 * Updates: power (-1.25 per move),moves,current_position and transfers coins and power if in range of a power station.
		 * Also makes a string of this movement for .txt file and adds it to movements.
		 */
		
		ArrayList<Station> stations = getMap().getList_stations();
		
		setPower(getPower() - 1.25);
		setMoves(getMoves() + 1);
		
		Position previous_position = current_position;
		current_position = current_position.nextPosition(direction);
		
		getPositions_of_flight().add(current_position);
		
		//Find station if after move it is in range for transfer and transfers coins and power if it is in range.
		Station station_after_move = find_station_transfer(stations);
		transfer(station_after_move);
		
		String movement = String.format("%s,%s,%s,%s,%s,%s,%s", Double.toString(previous_position.getLatitude()),Double.toString(previous_position.getLongitude()),direction,Double.toString(current_position.getLatitude()),
				Double.toString(current_position.getLongitude()),Double.toString(this.getCoins()),Double.toString(this.getPower()));
		getMovements().add(movement);
	}

	public Station find_station_transfer(ArrayList<Station> stations){
		/* 
		 * Finds closest station which is in range for transfer. This is used to ensure that after each movement 
		 * it checks if it is in range of a station it forces a transfer. Takes an ArrayList of all the stations of the map.
		 */ 
		
		Station closest_station = null;
		double closets_distance = 10000.00;
		
		for(int i = 0 ; i < stations.size() ; i++) { 
			
			Station test_station = stations.get(i);
			double dist_feat = current_position.distanceStation(test_station);
			
			// Checks if the ditance is in range and it is the smallest and hasnt been visited.
			if(Position.inRange(dist_feat) && dist_feat < closets_distance) {
				closest_station = test_station;
				closets_distance = dist_feat;
			}
		}
		return closest_station;
	}

	public void transfer(Station station) {
		/* 
		 * This is the transfer function of the drone. It updates the coin and power when it falls in range of a station.
		 * Input is a station which the drone updates with. This station is found using find_station_transfer().
		 */ 
		
		//if station == null no transfer takes place. 
		if( station != null ) {	
			
			double transfer_power = getPower();
			double transfer_coins = getCoins();

			setPower(getPower() + station.getPower()) ;
			setCoins(getCoins() + station.getCoins()) ;
			if(getPower() < 0 ) {
				setPower(0);
			}
			if( getCoins() < 0 ) {
				setCoins(0);
			}
			// This is to update the actual station.
			station.update_station(transfer_power,transfer_coins);
		}
	}
	
	public boolean inPlayArea_and_aviod_negative_station(Direction direction) {
		/* 
		 * This function checks that the next direction the drone moves in is in the play area and it doesn't fall in the range of a 
		 * negative power station. This function can be used by both the drones because it only uses a one look ahead.
		 */
		return current_position.nextPosition(direction).inPlayArea() && !avoid_one_move.contains(direction);
	}
	
	/*
	 * Below we have all the setters and getters for all the class variables, they should be self explanatory.
	 */

	public String getDrone_type() {
		return drone_type;
	}

	public void setDrone_type(String drone_type) {
		this.drone_type = drone_type;
	}

	public double getCoins() {
		return coins;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}

	public int getMoves() {
		return moves;
	}

	public void setMoves(int moves) {
		this.moves = moves;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public Map getMap() {
		return map;
	}
	
	public Position getCurrent_position(){
		return current_position;
	}

	public ArrayList<String> getMovements() {
		return movements;
	}

	public void setMovements(ArrayList<String> movements) {
		this.movements = movements;
	}

	public ArrayList<Position> getPositions_of_flight() {
		return positions_of_flight;
	}

	public void setPositions_of_flight(ArrayList<Position> positions_of_flight) {
		this.positions_of_flight = positions_of_flight;
	}

	public Random getRnd() {
		return rnd;
	}
}
