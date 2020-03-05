# powergrab-ILP
This is my GitHub repository for the PowerGrab project of the Informatics Large Practical course at the University of Edinburgh
# Introduction
The task for the Informatics Large Practical is to develop an autonomous (“smart”) component which isto play against a human component in a location-based strategy game.  The smart component is an au-tonomous drone which competes against a human player who is acting as the pilot of a remote-controlled(“dumb”) drone which moves only in response to directions from the pilot.
# The implementation task
To develop a simulation framework which demonstrates two versions of an automated drone which plays the PowerGrab game as though it was playing against a human player.   The first version has somespecific limitations and is stateless; this version is suitable for playing against a novice human player.The second version does not have these limitations and is stateful; this version is suitable for playing against an expert human player. A better implementation of the stateful drone is one which collects more coins while still keeping its runtime modest.
# Game Rules
Cryptocurrency coins and power are collected from a charging station by flying a drone close to its location on the map, by which we mean that the drone makes a sequence of short moves to bring their location step-by-step nearer to the location of the charging station. For the purposes of the game, a player will be judged to be close enough to a charging station to be able to collect coins and power from it over-the-air if they are within 0.00025degrees of the charging station.

Charging stations dispense coins in a fictional cryptocurrency called Powercoin; these real-valued coins are collected in order to increase the player’s score in the game. Charging stations also dispense power which isused by the drone to power its flight. The maximum payload which a changing station can have is a credit of 125.0 coins and 125.0 units of power. However, a charging station can also store debt and negative quantities of power, which cancel out the equivalent positive value stored in the drone. A drone cannot store a negative amount of coins or a negative quantity of power; the minimum which it can have is 0.0 coins and 0.0 power.

Transfer of coins and power between the changing station and the drone is automatic; there is no way to prevent the transfer of coins and power from taking place if the drone is within 0.00025 degrees of the charging station.As the drone flies, it travels at a constant speed and consumes power at a constant rate.  In each move, thedrone consumes 1.25 units of power, and moves a distance of 0.0003 degrees.

Finally, the object of the game is simply to collect as many coins as possible.





