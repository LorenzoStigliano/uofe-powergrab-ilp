package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StatefulDrone extends Drone {
	// Next station it will go to.
	private Station next_destination;
	
	// ArrayList of all the directions the drone takes.
	private ArrayList<Direction> directions_of_flight;
	
	// ArrayList of positive stations to avoid.
	private ArrayList<Station> positive_stations_to_avoid;
	
	// ArrayList of all stations on the map.
	private ArrayList<Station> all_stations;
	
	// Save the seed of original drone used in the simulation() method.
	final private int seed;
		
	public StatefulDrone(String DD, String MM, String YYYY, double lat, double longi, int seed, String drone_type) throws IOException {
		
		super(DD, MM, YYYY, lat, longi,seed, drone_type);
		this.seed = seed;
		this.next_destination = null;
		this.directions_of_flight = new ArrayList<Direction>();
		this.positive_stations_to_avoid = new ArrayList<Station>();
		this.all_stations = getMap().getList_stations();
	}
	
	public void run(int moves) {
		/* 
		 * Run of the drone this chooses the run based on the simulations done by 
		 * the simulation drone which returns the integer of the strategy which returns the most coins.
		 * Will run this for the amout of moves given.
		 */

		try {
			
			SimulateStatefulDrone simulation_drone = new SimulateStatefulDrone(getMap().getDD(),getMap().getMM(),getMap().getYYYY(),
					getCurrent_position().getLatitude(),getCurrent_position().getLongitude(),seed,getDrone_type(),moves);
			int strategy = simulation_drone.simulate();

			switch(strategy) {
				case 1:
					strategy_1(moves);
					break;
				case 2:
					strategy_2(moves);
					break;
				case 3:
					strategy_3(moves);
					break;
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void strategy_1(int moves) {
		/* 
		 * 1st is to go to all positive stations and not avoid any of them.
		 * Reapeat this for the number of given moves.
		 */
		
		while(getMoves() < moves && getPower() >= 1.25) {
			one_look_ahead();
			find_station();
            Direction general_direction = direction_to_station(next_destination);
            Direction final_direction = skip_negative(general_direction , next_destination);
            update_drone(final_direction);
        }
	}
	
	public void strategy_2(int moves) {
		/* 
		 * 2nd is to go to some positive stations.
		 * Calls the stations_to_aviod() before it starts to move. (Scans map).
		 * Reapeat this for the number of given moves
		 */
		
		stations_to_aviod();
		while(getMoves() < moves && getPower() >= 1.25) {
			one_look_ahead();
			find_station();
            Direction general_direction = direction_to_station(next_destination);
            Direction final_direction = skip_negative(general_direction , next_destination);
            update_drone(final_direction);
        }
	}
	
	public void strategy_3(int moves) {
		/*
		 *  3nd is to go to all positive and if it gets stuck push through the negative charging stations.
		 *  The drone can get stuck in 2 situations if it is stuck by a negative or cant find the station to transfer.
		 * 	If stuck finds random direction to go in and if it is out of range we 
		 *  add this station to positve stations ,if the resulting direction falls out of range, to aviod and calulate new next_destination.
		 *  Once it has visited all stations tries to get the ones it missed.
		 *  Reapeat this for the number of given moves.
		 */

		while(getMoves() < moves && getPower() >= 1.25) {
			one_look_ahead();
			find_station();
            Direction general_direction = direction_to_station(next_destination);
            
            // Get subList of last 10 directions
            List<Direction> directions_to_check = new ArrayList<Direction>();
            if(directions_of_flight.size() > 10) {
             	 directions_to_check = directions_of_flight.subList(directions_of_flight.size()-11, directions_of_flight.size()-1);
    		} 
            
            // if it is stuck will do this
            if(is_stuck()) {
            	// This takes care when the drone is stuck between statins to get correct transfer
            	if(next_destination != null) {
            		// Get random direction that isn't one of the last 10 moves.
            		if(directions_of_flight.size() > 10) {
            			while(true) {
            				int next = getRnd().nextInt(16);
            				// update the general direction if stuck to a random one 
            				general_direction = Direction.values()[next];
            				if(!directions_to_check.contains(general_direction))
            					break;
            				}
            			}
            		
                	// This ensures that the drone stays in the playArea
                	if(getCurrent_position().nextPosition(general_direction).inPlayArea()) {
                		update_drone(general_direction);
                	}
                	// Otherwise we do not visit this positive station it is trying to go to and find new one.
                	// If going in this direction results in getting leaving play area.
                	else {
                		positive_stations_to_avoid.add(next_destination);
            			find_station();
                        Direction new_direction = direction_to_station(next_destination);
                        Direction new_final_direction = skip_negative(new_direction , next_destination);
                        update_drone(new_final_direction);
                	}
            	}
            	// Check normally after it has visited all the stations.
            	else {
            		Direction final_direction = skip_negative(general_direction , next_destination);
                    update_drone(final_direction);
            	}
            }
            
            // no destination try to go to the stations it hasn't before.
            else if(next_destination == null) {
            	// check that the stations to aviod there is atelast one and it isnt null.
            	if(positive_stations_to_avoid.size() > 0) {
                	for(int i = 0 ; i < positive_stations_to_avoid.size() ; i++) {
                		if(positive_stations_to_avoid.get(i) != null) {
                    		if(positive_stations_to_avoid.get(i).getCoins() != 0 ) {
                    			// set this as the next destination
                    			next_destination = positive_stations_to_avoid.get(i);
                    			break;
                    		}
                		}
                	}
                	//update the new directions to get this station.
                	Direction new_direction = direction_to_station(next_destination);
                	Direction final_direction = skip_negative(new_direction , next_destination);
                    update_drone(final_direction);
            	}
            	else {
            		Direction final_direction = skip_negative(general_direction , next_destination);
                    update_drone(final_direction);
            	}
            }
            // otherwise it isnt stuck
            else {
                Direction final_direction = skip_negative(general_direction , next_destination);
                update_drone(final_direction);
            }   
        }
	}

	@Override
	public void update_drone(Direction direction) {
		/* 
		 * Overrides the update_function. Does the same as the original but adds the direction 
		 * to the directions_of_flight ArrayList.
		 */
		
		super.update_drone(direction);
		directions_of_flight.add(direction);
	}
	
	public boolean is_stuck() { 
		/* 
		 * Function that checks if the drone is stuck.
		 * Check that the last 10 directions are not the same 2 Directions.
		 * i.e if N,S,N,S,N,S,N,S,N,S are the last 10 moves then we know its stuck.
		 */	
		
		if(directions_of_flight.size() > 10) {
			
			// Get subList of last 10 directions
			List<Direction> directions_to_check = directions_of_flight.subList(directions_of_flight.size()-11, directions_of_flight.size()-1);
			
			// Assign the first direction of the list to "first" and the second to "second"
			Direction first = directions_to_check.get(0);
			Direction second = directions_to_check.get(1);
			
			int first_count = 0;
			int second_count = 0;
			
			// Iterate over the list of 10 last directions and checks if 
			for(int i = 0; i<10 ; i++) {
				if(directions_to_check.get(i)==first) {
					first_count++;
				}
				if(directions_to_check.get(i)==second) {
					second_count++;
				}
			}
			// Returns true if both the first_count and second_count are both equal to 5. (If its stuck)
			return first_count==5 && second_count==5;
		}
		else {
			return false;
		}
	}
	
	public void stations_to_aviod() {
		/* 
		 * Function that finds the positive station to avoid and adds them to "positive_stations_to_aviod".
		 * This function gets all the positive stations which are in range of a negative charging station. 
		 * This function is used in tactics 2.
		 * For tactics 1 and 3 it will be an empty ArrayList.
		 */
		
		for(int i = 0 ; i < all_stations.size() ; i++) { 
			
			Station station = all_stations.get(i);
			
			// Only gets the positive power stations.
			if(station.getPower() > 0) {
				
				//Iterates over all the stations and checks if they are in range of a negative charging station.
				for(int j = 0 ; j < all_stations.size() ; j++) {
						
						Station compare_station = all_stations.get(j);
						if(compare_station.getPower() < 0) {
							
							// Distance from station and the station we are comparing.
							double distance = station.getPosition().distanceStation(compare_station);
							
							// Checks if it is in_range for another transfer if it goes to this station (use in_range) function.
							if(Position.inRange(distance)) {
								positive_stations_to_avoid.add(station);
						}
					}
				}
			}
		}
	}

	public void find_station(){
		/* 
		 * Function finds closest station with positive power and assigns it as the "next_destination".
		 * If all of them have been visited assigns null to station.
		 */
		
		Station closest_station = null;
		ArrayList<Station> stations  = getMap().getList_stations();
		double closets_distance = 10000.00;
			
		for(int i = 0 ; i < stations.size() ; i++) { 
			
			Station test_station = stations.get(i);
			double dist_to_station = getCurrent_position().distanceStation(test_station);
			
			// Finds the closets distance and checks if the power > 0 and that 
			// the station is not one to avoid (not in "positive_stations_to_aviod".)
			if(dist_to_station < closets_distance && test_station.getPower() > 0 && !positive_stations_to_avoid.contains(test_station)) {
				
				closest_station = test_station;
				closets_distance = dist_to_station;
			}
		}
		next_destination = closest_station;
	}
	
	public Direction direction_to_station(Station station) {
		/* 
		 * Finds the direction to the station, the station will be the next_destination.
		 * It finds the next direction by calculating the angle between its current position and the station.
		 * Given an angle we check in what range it falls. The direction by +-12.5 degrees of the actual direction angle.
		 * 
		 * Example: 
		 * Angle to station from current position = 95.6 degrees.
		 * Then the direction would be E since E = 90+-12.5 degrees. And 95.6 lies within this range.
		 */
		
		Direction[] directions = Direction.values();
		
		// If station is null this means it has run out of stations to visit so we just assign it a direction that 
		// when it moves in this direction it (a) doesn't leave the play area and (b) doesn't fall in range of a negative station.
		if(station == null) {
			
			while(true) {
				// Iterate over all directions.
				for(int i = 0 ; i< directions.length ; i++) {
					if(inPlayArea_and_aviod_negative_station(directions[i])) {
						return directions[i];
					} 
				}

			}
		}
		
		else {
			
			double angle = getCurrent_position().angleStation(station);
			Direction direction = null;
			for(int i = 0; i<16 ; i++) {
				
				// Gets upper and lower bounds for the range of each direction.
				double lower = 11.25+22.5*i;
				double higher = 33.75+22.5*i;
				
				// Special case north because N is at 0 or 360 degrees so if angle is greater than 348.75 or
				// angle is <= 11.25 we assign it to north.
				if(i==15) {
					if(angle > 348.75 || angle <= 11.25) {
						direction = directions[0];
						break;
					}
				}
				// Check in what range it falls for the rest of the directions.
				// And assigns it this direction.
				else if(angle > lower && angle <= higher) {
					direction = directions[i+1];
					break;
				}
			}
			return direction;
		}
	}
	
	public Direction skip_negative(Direction direction,Station station) {
		/* 
		 * Takes a direction and a station. Checks if you move in the direction you don't hit a negative station.
		 * If you do recalculate to closest direction to station without touching negative power station.
		 */
		
		// If station is null this means it has run out of stations to visit so we just return the direction given.
		// This direction will be in the play area and not run into any negative station because it has been assigned
		// in direction_to_station function which ensure this for when stations = null.
		if(station == null) {
			return direction;
		}
		
		else {
			
			Direction next_direction = null;
			
			// If position after movement in direction given is in play area and doesn't run into negative station return it.
			if(inPlayArea_and_aviod_negative_station(direction)) {
				return direction;
			}
			
			//Otherwise iterate over all the directions and find one that doesn't fall into the range of a negative station and has the
			//closest distance to a station after it moves.
			else {
				
				double smallest_distance = 1000;
				
				for(int i = 0 ; i < Direction.values().length ; i++) {
					
					Direction possible_next_direction = Direction.values()[i];
					
					// Checks if the "possible_next_direction" is in play area and doesn't cause the drone to fall in range of a negative station.
					if(inPlayArea_and_aviod_negative_station(possible_next_direction)) {
						
						// Gets the distance from the next position and the station.
						double distance_to_station = getCurrent_position().nextPosition(possible_next_direction).distanceStation(station);
						
						// Finds smallest distance to station and assigns this as the next_direction to go to.
						if(smallest_distance > distance_to_station) {
							next_direction = possible_next_direction;
							smallest_distance = distance_to_station;
						}
					}
				}
				return next_direction;
			}
		}
	}

}

