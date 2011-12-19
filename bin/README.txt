This is the README for CS 605.421 - Spring 2011, Project #2.  Below you will find the environment of the last successful build, release notes, purpose, file list and operational instructions.

Environment:

Machine: 	Mac OSX 10.6.6
Eclipse: 	Eclipse IDE For Java Developers, Helios Service Release 1, 20100917-0705
Java:		JavaSE 1.6	

Files:


Project2_Board_Thompson.java - Board class, contains the getScore huersitic function and the random move function.

Project2_BoardPosition_Thompson.java - Pojo used for randomization.

Project2_BoardView_Thompson.java - Board view class, handles view for the board and some auxillary functions.

Project2_ChessApp_Thompson.java - Main applet class.  Contains the GUI menus, etc.

Project2_HistoryData_Thompson.java - History object. Keeps track of the moves we have made so we can undo them.

Project2_MergeSort_Thompson.java - Merge sort implementation for randomizing.

Project2_Move_Thompson.java - POJO for representing a move.

Project2_RandomizerUtils_Thompson.java - Random move algorithm implementations.

Project2_Search_Thompson.java - Very important, contains implementations of both search algorithms as well as the time/depth limits.

Project2_Square_Thompson.java - GUI Sqare class.

Project2_StopSearchException.java - Custom exception for when we detect that our timelimit has expired.  Throwing it and catching it allows us to impose a time limit on search.

Images Folder - Images used for GUI.


Where to find code for requirements (line numbers are approximate)- 

Project2_Search_Thompson.java:

think - Iterative deepening and starts search, Line 87
SearchMaxAB - Max method of alpha beta pruning:  Line 201
SearchMinAB Min method of alpha beta search:  Line 548
SearchMax - Max method of our minimax search - Line 317
SearchMin - Min method of our minimax search - Line 433

Project2_Board_Thompson.java:

getScore - Huersitic implementation:  Line 1011
getRandomMove - Random move implementation:  Line 361



Purpose:  

The purpose of this project was to learn about search and adversarial gameplay with a project that has a (much) larger search space than TicTacToe.  Also explores randomization algorithms.



Running the Project:

Project2_ChessApp_Thompson.java is the main class file.  Should be run as an applet.

You can change the move algorithm, depth limit, time limit as well as the randomization algorithm used for a rando move.


Notes:

1.  For my paper, I couldn't get the paper I wanted that looked like it had actual measurement results to compare to since it was behind a pay-wall so I just compared my implementations.
2.  I tried to control the environment (same computer, same time, etc) for measurement.
3.  Output for Minimax/Apha Beta is the depth, total ply searched, pv as well as the number of nodes.
4.  Applet defaults to Alpha Beta for our move-finding algorithm.