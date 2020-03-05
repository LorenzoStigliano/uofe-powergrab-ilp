package uk.ac.ed.inf.powergrab;

import java.io.IOException;

public class App{
	
    public static void main(String[] args) throws IOException {
    	
    	String DD = args[0];
    	String MM = args[1];
    	String YYYY = args[2];
    	double latitude =  Double.parseDouble(args[3]);
    	double longitude =  Double.parseDouble(args[4]);
    	int seed = Integer.valueOf(args[5]);
    	String drone_type = args[6];
    	
    	if(drone_type.equals("stateless")) {
    		// Initializing the dorne.
			StatelessDrone drone_stateless = new StatelessDrone(DD,MM,YYYY,latitude,longitude,seed,drone_type);
			// 250 is the number of moves the drone will take one can change this if you want to take more or less moves.
			drone_stateless.run(250);
    		// Creating the output files.
    		FileCreator creator_stateful = new FileCreator(drone_stateless, drone_stateless.getMap());
            creator_stateful.createTxtFile_Movements();
            creator_stateful.createJsonFile_Flight();
    	}
    	else {
    		// Initializing the dorne.
			StatefulDrone drone_stateful = new StatefulDrone(DD,MM,YYYY,latitude,longitude,seed,drone_type);
			// 250 is the number of moves the drone will take one can change this if you want to take more or less moves.
    		drone_stateful.run(250);
    		// Creating the output files.
    		FileCreator creator_stateful = new FileCreator(drone_stateful, drone_stateful.getMap());
            creator_stateful.createTxtFile_Movements();
            creator_stateful.createJsonFile_Flight();
    	}
    }
}