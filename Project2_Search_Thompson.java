//
//  Search.java
//  ChessApp
//
//  This search code is heavily based on Tom Kerrigan's tscp for which he
//  owns the copyright - (c) 1997 Tom Kerrigan -  and is used with his permission.
//  All rights are reserved by the owners of the respective copyrights.
//  Java version created by Peter Hunter on Sat Jan 05 2002.
//  Copyright (c) 2002 Peter Hunter. All rights reserved.
//
import java.util.*;

/**
 * 
 * Search class.
 * 
 * Implements our Minimax and Alpha Beta search algorithms.
 * Called from BoardView object, Thinker thread.
 * 
 * Sorts potential moves by principal variation to improve performance of pruning.
 * 
 * Additions:
 * 
 * 1.  Added SELECTED_ALGORITHM to determie which search method we use.
 * 2.  Added SearchMaxAB/SearchMinAB for minimax alpha beta pruning earch.
 * 3.  Added SearchMax/SearchMin methods for minimax only search.
 * 4.  Removed Quisence search, wasn't a requirement and didn't seem to be worth the reduced plies.
 * 
 * 
 * @author Kurtis Thompson
 * @version 2011.0407
 * @since 1.6
 *
 */
public class Project2_Search_Thompson {
	
	/**
	 * Simple public variable corresponding to what move algorithm the user has selected on our gui.
	 * 
	 */
	public int SELECTED_ALGORITHM = 2;
	
	/**
	 * Constructor
	 * 
	 * @param b - Current Board
	 */
    public Project2_Search_Thompson(Project2_Board_Thompson b) {
        board = b;
    }
    
    /**
     * Return a move object corresponding to the best move found in our principal variation array.
     * Basically, the code keeps track of the best path found so that we can search it again on subsequent searches.
     * 
     * @return - Move we should make.
     */
    public Project2_Move_Thompson getBest() {
    	//return getRandom();
        return pv[0][0];
    }
    
    
    /**
     * Makes a random move.
     * 
     * @param RANDOMIZATION_ALGORITHM - Integer corresponding to what randomization algorithm the user has selected.  Choices are Permute By Sorting or Randomize in Place.
     * 
     * @return Returns a random move.
     */
    public Project2_Move_Thompson getRandomMove(int RANDOMIZATION_ALGORITHM){
    	return board.getRandomMove(RANDOMIZATION_ALGORITHM);
    	
    }
    
    /**
     * 
     * Think method starts our search, essentially it is the root recursive call.
     * 
     * 
     * @param output - True/False to display debugging.
     * @param maxTime - maximum time we can search
     * @param maxDepth - maximum depth we should search
     * 
     * We implement the time limit by checking how much time has elapsed per 1024 nodes and then throwing the exception to end our search if maximum time has elapsed.)
     * 
     * 
     */
    void think(int output, int maxTime, int maxDepth) {
	/* some code that lets us get back here and return
	   from think() when our time is up */
	try {
		
			//Get the start time, get the end time to not take too long.
            startTime = System.currentTimeMillis();
            //End of the turn.
            stopTime = startTime + maxTime;

            //ply = single level of tree, 
            //half moves (one by a single player)
            ply = 0;
            
            //Number of nodes.
            nodes = 0;
          
            //Clearing pv and board history for our new search
            for (int i = 0; i < MAX_PLY; i++)
                for (int j = 0; j < MAX_PLY; j++)
                	//The best branch we currently know of.
                    pv[i][j] = new Project2_Move_Thompson((char) 0, (char) 0, (char) 0, (char) 0);
            for (int i = 0; i < 64; i++)
                for (int j = 0; j < 64; j++)
                    board.history[i][j] = 0;
            
            
            //Starting the search
            //Output some debugging stuff.
            
            //Increment our depth by one to allow for root level.
            maxDepth = maxDepth + 1;
            if (output == 1)
            {
            	System.out.println("MaxDepth: " + maxDepth);
            	System.out.println("ply      nodes  score  pv");
            	
            }
         
            
            
            //Iterative deepening, search as far as we can within the time limit.
            //Set our maxDepth and see how far we can get.
            for (int i = 1; i <= maxDepth; ++i) {
            	
            	/*
            	 * Basically, as we have searched we keep track of the path that produced our best move.
            	 * We can use this to order subsequent moves using the idea that following moves lie along the same path.
            	 * Should get us closer to optimal performance of Alpha Beta pruning.
            	 * 
            	 * 
            	 */
            	
            	//This is cool, follow our previously searched paths to improve move ordering.
            	//Should improve the performance of alpha beta pruning as noted by Knuth.
            	followPV = true;
            
            	int x = 0;
            	//If the user selected Alpha Beta, do that.
            	if(board.SEARCH == 2)
            		x = searchMaxAB(-10000, 10000, i);
            	//Minimax
            	else if(board.SEARCH == 1)
            		x = this.searchMax(i);
            	else
            	{
            		//Should never get here.
            	}
            	//int x = searchMax(i);
            	//System.out.println("X: " + x);
            	//if(x > 10000)
            		//break;
            	//int x = search(-10000, 10000, i);
		if (output > 0) {
                    System.out.print(/*"%3d  %9d  %5d "*/ i + "\t" + nodes + "\t" + x); 
                    for (int j = 0; j < pvLength[0]; ++j)
                        System.out.print("\t" + pv[0][j].toString());
                    System.out.println();
		}
		//if (x > 9000 || x < -9000)
          //          break;
            }
        }
        catch (Project2_StopSearchingException_Thompson e) {
            /* make sure to take back the line we were searching */
        	
        	//In this case, just use the previous searched depths as noted in the report.
            while (ply != 0) {
                board.takeBack();
                --ply;
            }
        }
        
        //Output some diagnostic information.
        System.out.println("Total Nodes searched: " + nodes);
        return;
    }

    
    /**
     * Max function for Minimax Alpha Beta
     * 
     * Returns the maximmum value found after making all moves possible at this level.
     * Uses Alpha Beta pruning to avoid searching branches that are unlikely to be taken given a compontent opponent.
     * Uses principal varation to order moves to increase pruning performance.
     * Based on psuedo-code from norvig.
     * 
     * @param alpha - our current alpha cutoff value.
     * @param beta  - our current beta cutoff value.
     * @param depth - our current depth value
     * 
     * @returns score for tree level.
     * 
     */
    int searchMaxAB(int alpha, int beta, int depth) throws Project2_StopSearchingException_Thompson {
    	//Increment our node count.
    	++nodes;
    	
    	//We have completed our iterative deepening, call our huersitic to evaluate the board.
    	if(depth == 0)
    		return board.getScore();

   	//Increment Nodes

   	//Check to see how are doing relative to our time limit.
   	//Do Check ever 1024 nodes <bit of a random number>
   	if ((nodes & 1023) == 0)
               checkup();

   	pvLength[ply] = ply;
   

   	//ply is a single users turn.
   	/* are we too deep? */
   	if (ply >= MAX_PLY - 1)
               return board.getScore();

   	/* are we in check? if so, we want to search deeper */
   	boolean check = board.inCheck(board.side);
   	if (check)
   		++depth;
   	
   	//Get possible valid moves
   	TreeSet validMoves = board.gen();
   	
   	//----
   	//Are we going to follow what we think is the best path we have found?
   	if (followPV)  /* are we following the PV? */
               sortPV(validMoves);

   	/* loop through the moves */
   	boolean foundMove = false;
           Iterator i = validMoves.iterator();
   	while (i.hasNext()) {
   		
   			//Get the move
   		Project2_Move_Thompson m = (Project2_Move_Thompson) i.next();
               //See if it is legal, if not, skip it (state returned to pre-move).
               if (!board.makeMove(m))
                   continue;
               //if it is legal, we found a move
               foundMove = true;
               //Increment the turn for next player
               ply++;
               //Search the next level for response move
               int x = searchMinAB(alpha, beta, depth - 1);
               
               
               
               //Undo whatever we did
               board.takeBack();
               //decrement the turn, back to where we started
               ply--;
               
               
               if (x > alpha) {
                  //Cut off, remember the move that caused it.
            	   
                   board.history[m.from][m.to] += depth;
                   alpha = x;

                   /* 
                    * 
                    * Update the PV to we can re-order these higher
                    * 
                    * */
                   pv[ply][ply] = m;
                   for (int j = ply + 1; j < pvLength[ply + 1]; ++j)
                           pv[ply][j] = pv[ply + 1][j];
                   pvLength[ply] = pvLength[ply + 1];
                   
                   if (alpha >= beta)
                       return alpha;
               }
   	}
  	
   	//We will only get here if we have a checkmate or a draw.
  	if (!foundMove) {
  		//Terrible for us
              if (check)
                  return -10000 + ply;
              else
                  return 0;
  	}

  	//Fifty move draw, return stalemate.
  	if (board.fifty >= 100)
              return 0;
  	//return the best move found.
  	return alpha;
     	
     }

    
    /**
     * Max function for our Minimax search.
     * 
     * 
     * Simulate all moves and return the maximum score once the recursion bottoms out to due to time, depth or end-game states being reached.
     * 
     * If we reach a depth of 0, we should just evaluate our game state using our huersitic value.
     * 
     * Checks our timer after every 1023 nodes examined to make sure we stay in our limit.
     * 
     * @param depth - Current Depth we are searching.
     * @return - Maximum value found.
     * @throws StopSearchingException - Time limit has elapsed.
     */
    int searchMax(int depth) throws Project2_StopSearchingException_Thompson {
    	
    	//initial max found.
    	int prevMax = -10000;
    	
    	
 
    	//Increment Nodes
    	++nodes;
    	       	if(depth == 0)
    	       		return board.getScore();
    	   
    	   	

    	   	//Check to see how are doing relative to our time limit.
    	   	//Do Check ever 1024 nodes <bit of a random number>
    	   	if ((nodes & 1023) == 0)
    	               checkup();

    	   	pvLength[ply] = ply;
    	   


    	   	//ply is a single users turn.
    	   	/* are we too deep? */
    	   	if (ply >= MAX_PLY - 1)
    	               return board.getScore();

    	   	/* are we in check? if so, we want to search deeper */
    	   	boolean check = board.inCheck(board.side);
    	   	if (check)
    	   		++depth;
    	   	
    	   	//Get possible valid moves
    	   	TreeSet validMoves = board.gen();
    	   	
    	   	//----
    	   	//Are we going to follow what we think is the best path we have found?
    	   	if (followPV)  /* are we following the PV? */
    	               sortPV(validMoves);

    	   	/* loop through the moves */
    	   	boolean foundMove = false;
    	           Iterator i = validMoves.iterator();
    	   	while (i.hasNext()) {
    	   			//Get the move
    	   		Project2_Move_Thompson m = (Project2_Move_Thompson) i.next();
    	               //See if it is legal, if not, skip it (state returned to pre-move).
    	               if (!board.makeMove(m))
    	                   continue;
    	               //if it is legal, we found a move
    	               foundMove = true;
    	               //Increment the turn for next player
    	               ply++;
    	               //Search the next level for response move from min
    	               int x = searchMin(depth - 1);
    	               
    	               
    	               
    	               //Undo whatever we did
    	               board.takeBack();
    	               //decrement the turn, back to where we started
    	               ply--;
    	               
    	               
    	               //We have found a new maximum value.
    	               if (x > prevMax) {
    	            	   
    	                   //this move caused a cutoff, so increase the history value so it gets ordered high next time we can search it 	   
    	                   board.history[m.from][m.to] += depth;

    	                   /* update the PV to we can re-order these higher - better pruning. */
    	                   pv[ply][ply] = m;
    	                   for (int j = ply + 1; j < pvLength[ply + 1]; ++j)
    	                           pv[ply][j] = pv[ply + 1][j];
    	                   pvLength[ply] = pvLength[ply + 1];
    	                   
    	                   //Update max.
    	                   prevMax = x;
    	               }
    	   	}
    	  	//No moves taken, must be in tie or loss
    	  	if (!foundMove) {
    	  		//DO NOT WANT
    	              if (check)
    	                  return -10000 + ply;
    	              else
    	                  return 0;
    	  	}

    	  	//Stalemate
    	  	if (board.fifty >= 100)
    	              return 0;
    	  	//Return maximum found.
    	  	return prevMax;
    	     	
    	     }

 	
    
    
    /**
     * Min function for our Minimax search.
     * 
     * Simulates all moves possible at this level and returns the minim value for all scores returned as a result of simulating the game with that move.
     * 
     * If we reach a depth of 0, we should just evaluate our game state using our huersitic value.
     * 
     * Checks our timer after every 1023 nodes examined to make sure we stay in our limit.
     * 
     * 
     * @param depth - Current Depth we are searching.
     * @return - Manimum value found.
     * @throws StopSearchingException - Time limit has elapsed.
     */
    
    int searchMin(int depth) throws Project2_StopSearchingException_Thompson {
    	
      	int prevMin = 10000;
      	
 		 //Bottom depth for this level, eval our board to get a score for this series of moves.
      	++nodes;
      	if(depth == 0)
      	{
      		return board.getScore();
      	}
  	
  	//Increment Nodes

  	//Check to see how are doing relative to our time limit.
  	/* do some housekeeping every 1024 nodes */
  	if ((nodes & 1023) == 0)
              checkup();

  	pvLength[ply] = ply;
  	//System.out.println("Settting pvLength[ply] to: " + ply);
  	//if(depth == 1)
  	//return 0;


  	//ply is a single users turn.
  	/* are we too deep? */
  	if (ply >= MAX_PLY - 1)
              return board.getScore();

  	/* are we in check? if so, we want to search deeper */
  	boolean check = board.inCheck(board.side);
  	if (check)
  		++depth;
  	
  	//Get possible valid moves
  	TreeSet validMoves = board.gen();
  	
  	//----
  	//Are we going to follow what we think is the best path we have found?
  	//We should so that moves are ordered better .
  	if (followPV)  
              sortPV(validMoves);

  	/* loop through the moves */
  	boolean foundMove = false;
          Iterator i = validMoves.iterator();
  	while (i.hasNext()) {
  			//Get the move
  		Project2_Move_Thompson m = (Project2_Move_Thompson) i.next();
              //See if it is legal, if not, skip it (state returned to pre-move).
              if (!board.makeMove(m))
                  continue;
              //if it is legal, we found a move
              foundMove = true;
              //Increment the turn for next player
              ply++;
              //Search the next level for response move
              int x = searchMax(depth - 1);

              //Undo whatever we did
              board.takeBack();
              //decrement the turn, back to where we started
              ply--;
              
              
              //We have found a new minimum value.
              if (x < prevMin) {
            	  
                  board.history[m.from][m.to] += depth;
                  
                  pv[ply][ply] = m;
                  for (int j = ply + 1; j < pvLength[ply + 1]; ++j)
                          pv[ply][j] = pv[ply + 1][j];
                  pvLength[ply] = pvLength[ply + 1];
                  
                  //Found a new minimum value.
                  prevMin = x;
                  
                  
              }
  	}
 	/* no legal moves? then we're in checkmate or stalemate 
 	 * 
 	 * This should return the max value since it is endgame, need to modify so it works with Minimax and not negamax.
 	 * */
 	if (!foundMove) {
             if (check)
                 return 10000 + ply;
             else
                 return 0;
 	}

 	/* fifty move draw rule */
 	if (board.fifty >= 100)
             return 0;
 	
 	//Return our minimum value found.
 	return prevMin;
 	
    	
    }
 	/**
 	 * Same as our maximum function but finds smallest score for a move.
 	 * 
 	 * Uses Alpha Beta pruning to avoid searching branches that are unlikely to be taken given a compontent opponent.
     * Uses principal varation to order moves to increase pruning performance.
     * Based on psuedo-code from norvig.
 	 * 
 	 * 
 	 * @param alpha - max cutoff
 	 * @param beta  - min cutoff
 	 * @param depth - how deep in the tree are we
 	 * @return score for leve in tree.
 	 * @throws StopSearchingException - Timelimit is up, just eval.
 	 */
 	 int searchMinAB(int alpha, int beta, int depth) throws Project2_StopSearchingException_Thompson {

 		//Increment nodes for statistics.
 		++nodes;
 		
 		//Bottom depth for this level, eval our board to get a score for this series of moves.
      	if(depth == 0)
      	{
      		return board.getScore();
      	}
  	

  	//Check to see how are doing relative to our time limit.
  	if ((nodes & 1023) == 0)
              checkup();

  	//Update principal variation tracker
  	pvLength[ply] = ply;


  	//ply is a single users turn.
  	//If we have maxed out depth, evaluate
  	if (ply >= MAX_PLY - 1)
              return board.getScore();
 
  	/* are we in check? if so, we want to search deeper */
  	boolean check = board.inCheck(board.side);
  	if (check)
  		++depth;
  	
  	//Get possible valid moves
  	TreeSet validMoves = board.gen();
  	
  	//Are we going to follow what we think is the best path we have found?
  	if (followPV)  /* are we following the PV? */
              sortPV(validMoves);

  	/* loop through the moves */
  	boolean foundMove = false;
          Iterator i = validMoves.iterator();
  	while (i.hasNext()) {
  			//Get the move
  		Project2_Move_Thompson m = (Project2_Move_Thompson) i.next();
              //See if it is legal, if not, skip it (state returned to pre-move).
              if (!board.makeMove(m))
                  continue;
              //if it is legal, we found a move
              foundMove = true;
              //Increment the turn for next player
              ply++;
              //Search the next level for response move
              int x = searchMaxAB(alpha, beta, depth - 1);

              //Undo whatever we did
              board.takeBack();
              //decrement the turn, back to where we started
              ply--;
              
              //Cut-off found.
              if (x < beta) {
                  /* this move caused a cutoff, so increase the history
                      value so it gets ordered high next time we can
                      search it */
                  board.history[m.from][m.to] += depth;
                  //store value
                  beta = x;

                  /* update the PV 
                   * This will help with move ordering later
                   * */
                  pv[ply][ply] = m;
                  for (int j = ply + 1; j < pvLength[ply + 1]; ++j)
                          pv[ply][j] = pv[ply + 1][j];
                  pvLength[ply] = pvLength[ply + 1];
                  
                  //return the value.
                  if (beta <= alpha)
                      return beta;
                  
                  
              }
  	}
 	/* no legal moves? then we're in checkmate or stalemate 
 	 * 
 	 * This should return the max value since it is endgame, need to modify so it works with Minimax and not negamax.
 	 * */
 	if (!foundMove) {
             if (check)
                 return 10000 + ply;
             else
                 return 0;
 	}

 	/* fifty move draw rule */
 	if (board.fifty >= 100)
             return 0;
 	
 	//return minimum value found.
 	return beta;
    	
    }
    
 	 
    
  
/* sortPV() is called when the search function is following
   the PV (Principal Variation). It looks through the current
   ply's move list to see if the PV move is there. If so,
   it adds 10,000,000 to the move's score so it's played first
   by the search function. If not, followPV remains FALSE and
   search() stops calling sortPV(). */

    void sortPV(TreeSet moves) {
	followPV = false;
        Iterator i = moves.iterator();
	while (i.hasNext()) {
		Project2_Move_Thompson m = (Project2_Move_Thompson) i.next();
            if (m.equals(pv[0][ply])) {
                followPV = true;
                m.score += 10000000;
                i.remove();
                moves.add(m);
                return;
            }
        }
    }

    
    
    /**
     * Function usd to keep track of our time limit.
     * Pretty simple idea, every x number of nodes, call this function to see how long we have been searching.
     * If we exceed our time limit, throw the exception to end our search.
     * since we use ID, we can return the last best move found.
     * @throws StopSearchingException
     */

    void checkup() throws Project2_StopSearchingException_Thompson {
	/* is the engine's time up? if so, longjmp back to the
	   beginning of think() */
	if (System.currentTimeMillis() >= stopTime) {
            throw new Project2_StopSearchingException_Thompson();
	}
    }
    
    //ply = one level in game tree
    //So maximum tree height is being limited to 32
    final static int MAX_PLY = 32;

    /**
     * Board object
     */
    private Project2_Board_Thompson board;
    
    /**
     * Principal variation array, used to keep track of previously found best paths.
     */
    private Project2_Move_Thompson pv[][] = new Project2_Move_Thompson[MAX_PLY][MAX_PLY];
    private int pvLength[] = new int[MAX_PLY];
    /**
     * Boolean to indicate if we should follow pv (ie, sort nodes by pv).
     */
    private boolean followPV;
    /**
     * Number of half moves we have played
     */
    private int ply = 0;
    /**
     * Number of nodes we have searched.
     */
    private int nodes = 0;
    /**
     * Start and End time for our timer.
     */
    private long startTime;
    private long stopTime;
}
