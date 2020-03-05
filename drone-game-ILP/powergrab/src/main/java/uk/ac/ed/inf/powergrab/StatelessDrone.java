package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;

public class StatelessDrone extends Drone{
	
	public StatelessDrone(String DD , String MM , String YYYY,double lat, double longi, int seed, String drone_type) throws IOException {
		super(DD, MM, YYYY, lat, longi,seed, drone_type);
	}
	
	public void run(int moves) {
		/* 
		 * Run of the drone just tries to avoid negative charging stations and move towards positive ones with one look ahead.
		 * Will run for the amount of moves given.
		 */
		
		while(getMoves() < moves && getPower() >= 1.25) {
			one_look_ahead();
			Direction next_direction = direction_one_look_ahead();
            Direction final_direction = final_direction(next_direction);
            update_drone(final_direction);
        }
	}
	
	public Direction direction_one_look_ahead() {
		/*
		 * This method finds the direction the drone should go in. If it doesn't find any returns null.
		 * Checks all directions and if one of the directions it goes in it lands in range of a positive charging station it will
		 * go in this direction. Goes to the first station it finds if it finds any.
		 */
		
		Direction[] directions = Direction.values();
		ArrayList<Station> stations = getMap().getList_stations();
		
		for(int i = 0 ; i < directions.length ; i++) {
			
			// Gets new position after it moves in a given direction
			Position new_position = getCurrent_position().nextPosition(directions[i]);
			
			// Iterates over all station to find if one is in range after it moves
			for(int k = 0 ; k < stations.size() ; k++) {
				
				Station test_station = stations.get(k);
				// Distance from new position and a given station.
				double dist_station = new_position.distanceStation(test_station);
				
				// If the distance is in range of the station and if the station is positive. Returns this direction to go in.
				if(Position.inRange(dist_station) && test_station.getPower() > 0) {
						return  directions[i];
				}
			}
		}
		// If no direction found to go in return null.
		return null;
	}
	
	public Direction final_direction(Direction direction) {
		/*
		 * This finds the final direction to go in for the drone.
		 * If a direction is given it ensures that it is in the play area and doesn't fall in range of a negative one.
		 * (1) Otherwise it randomises a number between 0 and 16 and picks a random direction.
		 * (2) Again it checks if it goes in this direction it is in the play area and doesn't fall in range of a negative one. 
		 * If this is satisfied it returns it.
		 * Otherwise repeats (1) and (2) until it finds a direction.
		 */
		
		while(true) {
			
			if(direction == null) {
				
				int next = getRnd().nextInt(16);
				// Get random direction
				direction = Direction.values()[next];
				
				// Check if this direction satisfies the 2 conditions.
				if(inPlayArea_and_aviod_negative_station(direction)) {
					return direction;
				}
				else {
					direction = null;
				}
				
			}
			else {
				
				if(inPlayArea_and_aviod_negative_station(direction)) {
					return direction;
				}
				else {
					direction = null;
				}
			}
		}
	}
}
