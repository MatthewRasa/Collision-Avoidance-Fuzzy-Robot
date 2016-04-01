#Collision Avoidance Fuzzy Robot#
![Robot demo](/media/demo.gif)

[See it on Youtube](https://www.youtube.com/watch?v=ziagm5aQnFE)

Simulation of a robot moving through an obstacle-filled environment using Mamdani Fuzzy Logic to guide its movement.  The robot repeatedly moves in the direction it is facing, using the Fuzzy Logic Controller (FLC) to determine its next angle of rotation.  In order provide an accurate simulation, the robot can only "see" what is directly in front of it.  A set viewing radius and field of view angle determine the total space the robot sees in any given position.  The vision data the robot gathers (based on obstacles in its field of view) is manipulated and passed to the FLC.  The FLC then uses its predefined membership functions to determine the robot's next rotation angle.  The simulation demonstrates the robot's movement through randomly-generated environments using this procedure.  Matlab's Fuzzy Logic Toolbox is used to generate the fuzzy logic controller and a Java JFrame is used for the graphical user interface.
