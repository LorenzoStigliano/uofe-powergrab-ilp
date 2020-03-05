package uk.ac.ed.inf.powergrab;

import java.io.IOException;

public class SimulateStatefulDrone {
	/*
	 * Takes a StatefulDrones data and runs simulation of the drone.
	 * It creates copies of it and runs different strategies of the drone to find the best run which gets the highest amount
	 * of coins.
	 */
	
	// All these are attributes of the StatefulDrone.
	private String DD;
	private String MM;
	private String YYYY;
	private double lat;
	private double longi;
	private int seed;
	
	// Will always be "stateful" because simulations will only be done by stateful drone.
	private static String drone_type = "stateful";
	
	// Number of moves you want to simulate the drone for.
	private int moves;
	
	public SimulateStatefulDrone(String DD , String MM , String YYYY, double lat, double longi,int seed, String drone_type, int moves) throws Exception {
		/*
		 * Drone_type must be "stateful" this is because only StatefulDrones run simulations.
		 */
		if(drone_type.equals("stateful")) {
			this.DD = DD;
			this.MM = MM;
			this.YYYY = YYYY;
			this.lat = lat;
			this.longi = longi;
			this.moves = moves;
		}
		else{
	        throw new Exception("drone_type must be stateful");
	    }
	}

	public StatefulDrone drone_copy() throws IOException {
		// Creates a copy of the same drone given.
		return new StatefulDrone(DD,MM,YYYY,lat,longi,seed,drone_type);
	}
	
	public int simulate() throws Exception {
		/* 
		 * Makes 3 copies of the drone given and uses 3 strategy and returns the integer that corresponds
		 * to the strategy that returns the highest amount of coins.This strategy will be the one used in the run() of the drone. 
		 */

		StatefulDrone drone_tactic_1 = drone_copy();
		StatefulDrone drone_tactic_2 = drone_copy();
		StatefulDrone drone_tactic_3 = drone_copy();
		
		simulate_strategy_1(drone_tactic_1);
		simulate_strategy_2(drone_tactic_2);
		simulate_strategy_3(drone_tactic_3);
		
		double[] coins_after_simulation = {drone_tactic_1.getCoins(), drone_tactic_2.getCoins(), drone_tactic_3.getCoins()};
		double max_coins = 0;
		int tactic = 0;
		
		for(int i = 0 ; i< coins_after_simulation.length ; i++) {
			if(coins_after_simulation[i] > max_coins) {
				max_coins = coins_after_simulation[i];
				tactic = i+1;
			}
		}
		return tactic;
	}
	
	public void simulate_strategy_1(StatefulDrone drone) {
		/*
		 * Takes a drone and runs the 1st strategy on this drone
		 * for the given amount of moves.
		 */
		
		drone.strategy_1(moves);
	}
	
	public void simulate_strategy_2(StatefulDrone drone) {
		/*
		 * Takes a drone and runs the 2nd strategy on this drone
		 * for the given amount of moves.
		 */
		drone.strategy_2(moves);
	}
	
	public void simulate_strategy_3(StatefulDrone drone) {
		/*
		 * Takes a drone and runs the 3rd strategy on this drone
		 * for the given amount of moves.
		 */
		drone.strategy_3(moves);
	}
}
