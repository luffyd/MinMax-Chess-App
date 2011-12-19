//
//  Board.java
//  ChessApp
//
//  Created by Peter Hunter on Sun Dec 30 2001.
//  Java version copyright (c) 2001 Peter Hunter. All rights reserved.
//  This code is heavily based on Tom Kerrigan's tscp, for which he
//  owns the copyright, and is used with his permission. All rights are
//  reserved by the owners of the respective copyrights.

import java.util.*;

/***
 * Chess Board Class
 * 
 * Contains various piece values, position arrays and other things needed to properly represent the chess board.
 * Also contains methods to make moves, undo-moves and evaluate the state of the board.
 * 
 * Originally part of ChessApp by Peter Hunter
 * 
 * @modified Kurtis Thompson
 * 
 * 1.  added public Move getRandomMove(int RANDOMIZATION_ALGORITHM) to generate a random move.
 * 2.  added public int getScore() - New hueristic method.
 * 3.  added public int evalMinMax() - Not used, was original hueristic implementation.
 * 
 * @version 2011.0402
 * @since 1.6
 * 
 *
 */
final public class Project2_Board_Thompson {
	
	/**
	 * Simple integer values for players, either light or dark.
	 * In our game, the computer is dark and the human player is white.
	 */
    final static int LIGHT = 0;
    final static int DARK = 1;
    
    /**
     * Integer value representing the selected search algorithm.
     * 
     */
    public int SEARCH = 2;
    
    /**
     * Piece values
     * 
     */
    final static int PAWN = 0;
    final static int KNIGHT = 1;
    final static int BISHOP = 2;
    final static int ROOK = 3;
    final static int QUEEN = 4;
    final static int KING = 5;
    final static int EMPTY = 6;

    /**
     * 
     * Board position mappings, basically just numbers the positions.
     */
    final static char A1 = 56;
    final static char B1 = 57;
    final static char C1 = 58;
    final static char D1 = 59;
    final static char E1 = 60;
    final static char F1 = 61;
    final static char G1 = 62;
    final static char H1 = 63;
    final static char A8 = 0;
    final static char B8 = 1;
    final static char C8 = 2;
    final static char D8 = 3;
    final static char E8 = 4;
    final static char F8 = 5;
    final static char G8 = 6;
    final static char H8 = 7;
    
    /**
     * Heuristic Bonus values used by the original Negamax heuristic
     * 
     * */

    final static int DOUBLED_PAWN_PENALTY = 10;
    final static int ISOLATED_PAWN_PENALTY = 20;
    final static int BACKWARDS_PAWN_PENALTY = 8;
    final static int PASSED_PAWN_BONUS = 20;
    final static int ROOK_SEMI_OPEN_FILE_BONUS = 10;
    final static int ROOK_OPEN_FILE_BONUS = 15;
    final static int ROOK_ON_SEVENTH_BONUS = 20;

    /**
     * As the original algorithm did, I am using historical data from previous searches to influence moving ordering.
     * This just limits the amount of data that I am going to store at any given point.
     * 
     */
    final static int HIST_STACK = 400;

    /**
     * Current active player
     */
    int side = LIGHT;
    /**
     * Current active player's opponent.
     */
    int xside = DARK;
    int castle = 15;
    int ep = -1;
    int fifty = 0;
    int hply = 0;
    
    /**
     * History structures, part of original code.
     */
    int history[][] = new int[64][64];
    Project2_HistoryData_Thompson histDat[] = new Project2_HistoryData_Thompson[HIST_STACK];
    
    //Pawn rank scores.
    int pawnRank[][] = new int [2][10];
    
    /**
     * Material value arrays, used to hold material scores for black/white players.
     */
    int pieceMat[] = new int[2];
    int pawnMat[] = new int[2];
    
    /**
     * Default board color configuration, 0 - white, 1 black, 6 - empty
     */
    int color[] =  {
	1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1,
	6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6,
	0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0
    };
    
    
    /**
     * Default piece configuration for our game.
     */
    int piece[] =  {
	3, 1, 2, 4, 5, 2, 1, 3,
	0, 0, 0, 0, 0, 0, 0, 0,
	6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6,
	0, 0, 0, 0, 0, 0, 0, 0,
	3, 1, 2, 4, 5, 2, 1, 3
    };
    
    
    /**
     * Character represetations of our pieces.
     */
    final char pieceChar[] = { 'P', 'N', 'B', 'R', 'Q', 'K' };
    
    
    /**
     * 
     * Offset values
     * 
     */
    private boolean slide[] = { false, false, true, true, true, false };
    private int offsets[] = { 0, 8, 4, 4, 8, 8 };
    private int offset[][] = {
        { 0, 0, 0, 0, 0, 0, 0, 0 },
        { -21, -19, -12, -8, 8, 12, 19, 21 },
        { -11, -9, 9, 11, 0, 0, 0, 0 },
        { -10, -1, 1, 10, 0, 0, 0, 0 },
        { -11, -10, -9, -1, 1, 9, 10, 11 },
        { -11, -10, -9, -1, 1, 9, 10, 11 }
    };
    
    
    /**
     * square centric board representations.
     */
    int mailbox[] = {
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1,  0,  1,  2,  3,  4,  5,  6,  7, -1,
         -1,  8,  9, 10, 11, 12, 13, 14, 15, -1,
         -1, 16, 17, 18, 19, 20, 21, 22, 23, -1,
         -1, 24, 25, 26, 27, 28, 29, 30, 31, -1,
         -1, 32, 33, 34, 35, 36, 37, 38, 39, -1,
         -1, 40, 41, 42, 43, 44, 45, 46, 47, -1,
         -1, 48, 49, 50, 51, 52, 53, 54, 55, -1,
         -1, 56, 57, 58, 59, 60, 61, 62, 63, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
    };

    /**
     * square centric board representations.
     */
    int mailbox64[] = {
        21, 22, 23, 24, 25, 26, 27, 28,
        31, 32, 33, 34, 35, 36, 37, 38,
        41, 42, 43, 44, 45, 46, 47, 48,
        51, 52, 53, 54, 55, 56, 57, 58,
        61, 62, 63, 64, 65, 66, 67, 68,
        71, 72, 73, 74, 75, 76, 77, 78,
        81, 82, 83, 84, 85, 86, 87, 88,
        91, 92, 93, 94, 95, 96, 97, 98
    };
    
    int castleMask[] = {
         7, 15, 15, 15,  3, 15, 15, 11,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        13, 15, 15, 15, 12, 15, 15, 14
    };
    
    /**
     * Piece values (how much is capturing a particular piece worth)
     * Pawn->King
     * 
     */
    int pieceValue[] = {
            100, 300, 300, 500, 900, 0
    };
    
    /* The "pcsq" arrays are piece/square tables. They're values
    added to the material value of the piece based on the
    location of the piece. */
    
    int pawnPcsq[] = {
            0,   0,   0,   0,   0,   0,   0,   0,
            5,  10,  15,  20,  20,  15,  10,   5,
            4,   8,  12,  16,  16,  12,   8,   4,
            3,   6,   9,  12,  12,   9,   6,   3,
            2,   4,   6,   8,   8,   6,   4,   2,
            1,   2,   3, -10, -10,   3,   2,   1,
            0,   0,   0, -40, -40,   0,   0,   0,
            0,   0,   0,   0,   0,   0,   0,   0
    };
    
   
    
    int knightPcsq[] = {
            -10, -10, -10, -10, -10, -10, -10, -10,
            -10,   0,   0,   0,   0,   0,   0, -10,
            -10,   0,   5,   5,   5,   5,   0, -10,
            -10,   0,   5,  10,  10,   5,   0, -10,
            -10,   0,   5,  10,  10,   5,   0, -10,
            -10,   0,   5,   5,   5,   5,   0, -10,
            -10,   0,   0,   0,   0,   0,   0, -10,
            -10, -30, -10, -10, -10, -10, -30, -10
    };
    
 
    int bishopPcsq[] = {
            -10, -10, -10, -10, -10, -10, -10, -10,
            -10,   0,   0,   0,   0,   0,   0, -10,
            -10,   0,   5,   5,   5,   5,   0, -10,
            -10,   0,   5,  10,  10,   5,   0, -10,
            -10,   0,   5,  10,  10,   5,   0, -10,
            -10,   0,   5,   5,   5,   5,   0, -10,
            -10,   0,   0,   0,   0,   0,   0, -10,
            -10, -10, -20, -10, -10, -20, -10, -10
    };
    
    /*
     * Piece/Square tables for kings.
     * 
     * 
     */
    int kingPcsq[] = {
            -40, -40, -40, -40, -40, -40, -40, -40,
            -40, -40, -40, -40, -40, -40, -40, -40,
            -40, -40, -40, -40, -40, -40, -40, -40,
            -40, -40, -40, -40, -40, -40, -40, -40,
            -40, -40, -40, -40, -40, -40, -40, -40,
            -40, -40, -40, -40, -40, -40, -40, -40,
            -20, -20, -20, -20, -20, -20, -20, -20,
              0,  20,  40, -20,   0, -20,  40,  20
    };
    
    int kingEndgamePcsq[] = {
             0,  10,  20,  30,  30,  20,  10,   0,
            10,  20,  30,  40,  40,  30,  20,  10,
            20,  30,  40,  50,  50,  40,  30,  20,
            30,  40,  50,  60,  60,  50,  40,  30,
            30,  40,  50,  60,  60,  50,  40,  30,
            20,  30,  40,  50,  50,  40,  30,  20,
            10,  20,  30,  40,  40,  30,  20,  10,
             0,  10,  20,  30,  30,  20,  10,   0
    };
    
    /* The flip array is used to calculate the piece/square
    values for DARK pieces. The piece/square value of a
    LIGHT pawn is pawnPcsq[sq] and the value of a DARK
    pawn is pawnPcsq[flip[sq]] */
    int flip[] = {
            56,  57,  58,  59,  60,  61,  62,  63,
            48,  49,  50,  51,  52,  53,  54,  55,
            40,  41,  42,  43,  44,  45,  46,  47,
            32,  33,  34,  35,  36,  37,  38,  39,
            24,  25,  26,  27,  28,  29,  30,  31,
            16,  17,  18,  19,  20,  21,  22,  23,
            8,   9,  10,  11,  12,  13,  14,  15,
            0,   1,   2,   3,   4,   5,   6,   7
    };
    
    
    /**
     * 
     * Returns the color for a particular index
     * 
     * @param i Board offset
     * @param j Board offset
     * @return
     */
    public int getColor(int i, int j) {
        return color[(i << 3) + j];
    }
    
    /**
     * Returns the piece at a given index
     * 
     * @param i Board offset
     * @param j Board offset
     * @return
     */
    
    public int getPiece(int i, int j) {
        return piece[(i << 3) + j];
    }
    
    
    
    
    /**
     * Random move function
     * 
     * Works as follows:
     * 
     * 1.  Creates a list of all pieces owned by the computer
     * 2.  Randomizes positions.
     * 3.  Iterates through the random list until it finds a piece that is not blocked in
     * 4.  Takes the first move available for the random piece.
     * 
     * 
     * This function is a little ugly in terms of complexity.   I didn't want to modify that code too much from the TreeSets used by the original author.
     * 
     * 
     * @author Kurtis Thompson
     * @param RANDOMIZATION_ALGORITHM
     * @return First move for a randomly chosen piece.
     */
    public Project2_Move_Thompson getRandomMove(int RANDOMIZATION_ALGORITHM)
    {
    	//Create a list to hold all of our pieces.
    	java.util.ArrayList<Integer> pieces = new java.util.ArrayList<Integer>();
    	//Find all our pieces
    	for(int i = 0; i < 64; i++)
    	{
    		if(color[i] ==DARK)
    		{
    			pieces.add(i);
    		}
    	}
    	
    	//Create an array for randomization.
    	int[] A = new int[pieces.size()];
    	for(int i = 0; i < pieces.size(); i++)
    		A[i] = Integer.parseInt(pieces.get(i).toString());
    	
    	//Depending on what randomization algorithm was chosen, randomize our pieces.
    	if(RANDOMIZATION_ALGORITHM == 0)
    		Project2_RandomizerUtils_Thompson.Permute_By_Sorting(A);
    	else
    		Project2_RandomizerUtils_Thompson.Randomize_In_Place(A);
    	
    	int randomPieces = A[0];
    	
    	int next = 0;
    	/*
    	 * Some pieces are going to trapped and unable to move
    	 * To fix this, let's iterative through our random piece list until we find a free piece.
    	 * At that point, take the first avialable move since we chose a piece randomly.
    	 */
    	while(next < A.length)
    	{
    	randomPieces = A[next];
    	TreeSet moves = this.gen();
    	Iterator possibleMove = moves.iterator();
    	//Iterate over the available moves.
    	while(possibleMove.hasNext())
    	{
    		Project2_Move_Thompson curr = (Project2_Move_Thompson)possibleMove.next();
    		//Is this move for our chosen piece.
    		if(curr.from == randomPieces)
    		{
    			//We need to make sure the move is legal
    			if(this.makeMove(curr))
    			{
    				//Undo the move so we can return it.
    				this.takeBack();
    				try{
    					System.out.println("Random Move: " + curr.from + " to " + curr.to);
    				}catch(Exception ex)
    				{
    					
    				}
    				return curr;
    			}
    		}
    	}
    	next++;
    	}
    	return null;
    }
    
    
    /**
     * Determines if it is white's turn to move
     * @return true or false depending on what player is moving.
     */
    public boolean isWhiteToMove() {
        return (side == LIGHT);
    }
    
    /**
    * 
    * inCheck() returns true if side s is in check and false
    *	otherwise. It just scans the board to find side s's king
    *	and calls attack() to see if it's being attacked. 
    *
    *	@param s - Side to check.
    *	@return true of false depending on state of game for the chosen player.
    *
    */

    boolean inCheck(int s) {
        int i;
        boolean check = false;
        for (i = 0; i < 64; ++i)
            if (piece[i] == KING && color[i] == s)
            {
                check = attack(i, s ^ 1);
                //if(check)
                	//System.out.println("Oh Snap, in Check");
                return check;
            }
        return true;  /* shouldn't get here */
    }
    

    
    /**
     * This is written strangely but it checks to see if the square in sq is being attacked by side s.
     * 
     * 
     * 
     * @param sq - Square to check.
     * @param s - Side attacking to check
     * @returns true or false
     * 
     * 
     * 
     */
    
    boolean attack(int sq, int s) {
	int i, j, n;

	//Loop through our entire board looking for pieces that are being directly attacked. 
	//Assuming we find some, return true otherwise all of side s' peices are in good shape.
	for (i = 0; i < 64; ++i)
            if (color[i] == s) {
                int p = piece[i];
                if (p == PAWN) {
                    if (s == LIGHT) {
                        if (COL(i) != 0 && i - 9 == sq)
                            return true;
                        if (COL(i) != 7 && i - 7 == sq)
                            return true;
                    }
                    else {
                        if (COL(i) != 0 && i + 7 == sq)
                            return true;
                        if (COL(i) != 7 && i + 9 == sq)
                            return true;
                    }
                }
                else
                    for (j = 0; j < offsets[p]; ++j)
                        for (n = i;;) {
                            n = mailbox[mailbox64[n] + offset[p][j]];
                            if (n == -1)
                                break;
                            if (n == sq)
                                return true;
                            if (color[n] != EMPTY)
                                break;
                            if (!slide[p])
                                break;
                        }
            }
	return false;
    }


    /**
     *  gen() generates pseudo-legal moves for the current position.
     *  It scans the board to find friendly pieces and then determines
     *  what squares they attack. When it finds a piece/square
     *  combination, it calls genPush to put the move on the "move
     *  stack." 
     *  */
    
    TreeSet gen() {
        TreeSet ret = new TreeSet();
        
	for (int i = 0; i < 64; ++i)
            if (color[i] == side) {
                if (piece[i] == PAWN) {
                    if (side == LIGHT) {
                        if (COL(i) != 0 && color[i - 9] == DARK)
                            genPush(ret, i, i - 9, 17);
                        if (COL(i) != 7 && color[i - 7] == DARK)
                            genPush(ret, i, i - 7, 17);
                        if (color[i - 8] == EMPTY) {
                            genPush(ret, i, i - 8, 16);
                            if (i >= 48 && color[i - 16] == EMPTY)
                                genPush(ret, i, i - 16, 24);
                        }
                    }
                    else {
                        if (COL(i) != 0 && color[i + 7] == LIGHT)
                            genPush(ret, i, i + 7, 17);
                        if (COL(i) != 7 && color[i + 9] == LIGHT)
                            genPush(ret, i, i + 9, 17);
                        if (color[i + 8] == EMPTY) {
                            genPush(ret, i, i + 8, 16);
                            if (i <= 15 && color[i + 16] == EMPTY)
                                genPush(ret, i, i + 16, 24);
                        }
                    }
                }
                else
                    for (int j = 0; j < offsets[piece[i]]; ++j)
                        for (int n = i;;) {
                            n = mailbox[mailbox64[n] + offset[piece[i]][j]];
                            if (n == -1)
                                break;
                            if (color[n] != EMPTY) {
                                if (color[n] == xside)
                                    genPush(ret, i, n, 1);
                                break;
                            }
                            genPush(ret, i, n, 0);
                            if (!slide[piece[i]])
                                break;
                        }
            }

	/* generate castle moves */
	if (side == LIGHT) {
            if ((castle & 1) != 0)
                genPush(ret, E1, G1, 2);
            if ((castle & 2) != 0)
                genPush(ret, E1, C1, 2);
	}
	else {
            if ((castle & 4) != 0)
                genPush(ret, E8, G8, 2);
            if ((castle & 8) != 0)
                genPush(ret, E8, C8, 2);
	}
	
	/* generate en passant moves */
	if (ep != -1) {
            if (side == LIGHT) {
                if (COL(ep) != 0 && color[ep + 7] == LIGHT && piece[ep + 7] == PAWN)
                    genPush(ret, ep + 7, ep, 21);
                if (COL(ep) != 7 && color[ep + 9] == LIGHT && piece[ep + 9] == PAWN)
                    genPush(ret, ep + 9, ep, 21);
            }
            else {
                if (COL(ep) != 0 && color[ep - 9] == DARK && piece[ep - 9] == PAWN)
                    genPush(ret, ep - 9, ep, 21);
                if (COL(ep) != 7 && color[ep - 7] == DARK && piece[ep - 7] == PAWN)
                    genPush(ret, ep - 7, ep, 21);
            }
	}
        return ret;
    }


/**
 *  genCaps() is basically a copy of gen() that's modified to
 *  only generate capture and promote moves. It's used by the
 *  quiescence search. */

    TreeSet genCaps() {
        TreeSet ret = new TreeSet();

	for (int i = 0; i < 64; ++i)
            if (color[i] == side) {
            	//Pawns get specialc cases
                if (piece[i]==PAWN) {
                	//Opponent
                    if (side == LIGHT) {
                        if (COL(i) != 0 && color[i - 9] == DARK)
                            genPush(ret, i, i - 9, 17);
                        if (COL(i) != 7 && color[i - 7] == DARK)
                            genPush(ret, i, i - 7, 17);
                        if (i <= 15 && color[i - 8] == EMPTY)
                            genPush(ret, i, i - 8, 16);
                    }
                    //Computer
                    if (side == DARK) {
                        if (COL(i) != 0 && color[i + 7] == LIGHT)
                            genPush(ret, i, i + 7, 17);
                        if (COL(i) != 7 && color[i + 9] == LIGHT)
                            genPush(ret, i, i + 9, 17);
                        if (i >= 48 && color[i + 8] == EMPTY)
                            genPush(ret, i, i + 8, 16);
                    }
                }
                else //Not a pawn, basically everything else.
                    for (int j = 0; j < offsets[piece[i]]; ++j)
                        for (int n = i;;) {
                            n = mailbox[mailbox64[n] + offset[piece[i]][j]];
                            if (n == -1)
                                break;
                            if (color[n] != EMPTY) {
                                if (color[n] == xside)
                                    genPush(ret, i, n, 1);
                                break;
                            }
                            if (!slide[piece[i]])
                                break;
                        }
            }
	if (ep != -1) {
            if (side == LIGHT) {
                if (COL(ep) != 0 && color[ep + 7] == LIGHT && piece[ep + 7] == PAWN)
                    genPush(ret, ep + 7, ep, 21);
                if (COL(ep) != 7 && color[ep + 9] == LIGHT && piece[ep + 9] == PAWN)
                    genPush(ret, ep + 9, ep, 21);
            }
            else {
                if (COL(ep) != 0 && color[ep - 9] == DARK && piece[ep - 9] == PAWN)
                    genPush(ret, ep - 9, ep, 21);
                if (COL(ep) != 7 && color[ep - 7] == DARK && piece[ep - 7] == PAWN)
                    genPush(ret, ep - 7, ep, 21);
            }
	}
        return ret;
    }

    /** genPush() puts a move on the move stack, unless it's a
    pawn promotion that needs to be handled by genPromote().
    It also assigns a score to the move for alpha-beta move
    ordering. If the move is a capture, it uses MVV/LVA
    (Most Valuable Victim/Least Valuable Attacker). Otherwise,
    it uses the move's history heuristic value. Note that
    1,000,000 is added to a capture move's score, so it
    always gets ordered above a "normal" move. */
    
    void genPush(TreeSet ret, int from, int to, int bits) {
	if ((bits & 16) != 0) {
            if (side == LIGHT) {
                if (to <= H8) {
                    genPromote(ret, from, to, bits);
                    return;
                }
            }
            else {
                if (to >= A1) {
                    genPromote(ret, from, to, bits);
                    return;
                }
            }
	}

	Project2_Move_Thompson g = new Project2_Move_Thompson(from, to, 0, bits);
        
	if (color[to] != EMPTY)
            g.setScore(1000000 + (piece[to] * 10) - piece[from]);
	else
            g.setScore(history[from][to]);
        ret.add(g);
    }


    /** genPromote() is just like genPush(), only it puts 4 moves
     * on the move stack, one for each possible promotion piece 
     * 
     * @param ret - move stack
     * @param from - move origin
     * @param to - move destination
     * @param bits
     */
    
    void genPromote(TreeSet ret, int from, int to, int bits) {
	for (char i = KNIGHT; i <= QUEEN; ++i) {
		Project2_Move_Thompson g = new Project2_Move_Thompson(from, to, i, (bits | 32));
            g.setScore(1000000 + (i * 10));
            ret.add(g);
	}
    }

    /** makemove() makes a move. If the move is illegal, it
    undoes whatever it did and returns false. Otherwise, it
    returns true. 
    *
    * @param m - Move to make
    */
    
    boolean makeMove(Project2_Move_Thompson m) {
	
	/* test to see if a castle move is legal and move the rook
	   (the king is moved with the usual move code later) */
	if ((m.bits & 2) != 0) {
            int from, to;

            //If we are in check, this won't work.
            if (inCheck(side))
                return false;
            
            //Check special attacks as a result of making this move.
            switch (m.to) {
                case 62:
                    if (color[F1] != EMPTY || color[G1] != EMPTY ||
                            attack(F1, xside) || attack(G1, xside))
                        return false;
                    from = H1;
                    to = F1;
                    break;
                case 58:
                    if (color[B1] != EMPTY || color[C1] != EMPTY || color[D1] != EMPTY ||
                            attack(C1, xside) || attack(D1, xside))
                        return false;
                    from = A1;
                    to = D1;
                    break;
                case 6:
                    if (color[F8] != EMPTY || color[G8] != EMPTY ||
                            attack(F8, xside) || attack(G8, xside))
                        return false;
                    from = H8;
                    to = F8;
                    break;
                case 2:
                    if (color[B8] != EMPTY || color[C8] != EMPTY || color[D8] != EMPTY ||
                            attack(C8, xside) || attack(D8, xside))
                        return false;
                    from = A8;
                    to = D8;
                    break;
                default:  /* shouldn't get here */
                    from = -1;
                    to = -1;
                    break;
            }
            
            //Update relevant color and pieces values.
            color[to] = color[from];
            piece[to] = piece[from];
            color[from] = EMPTY;
            piece[from] = EMPTY;
	}

	/* back up information so we can take the move back later. */
	//Important we do this to keep our ply accurate which we are using in search for move ordering.
        histDat[hply] = new Project2_HistoryData_Thompson();
	histDat[hply].m = m;
	histDat[hply].capture = piece[(int)m.to];
	histDat[hply].castle = castle;
	histDat[hply].ep = ep;
	//Fifty move limit
	histDat[hply].fifty = fifty;
	++hply;

	/* update the castle, en passant, and
	   fifty-move-draw variables */
	castle &= castleMask[(int)m.from] & castleMask[(int)m.to];
	if ((m.bits & 8) != 0) {
            if (side == LIGHT)
                ep = m.to + 8;
            else
                ep = m.to - 8;
	}
	else
            ep = -1;
	//Update our fifty move checks.
	if ((m.bits & 17) != 0)
            fifty = 0;
	else
            ++fifty;

	/* move the piece */
	color[(int)m.to] = side;
	if ((m.bits & 32) != 0)
            piece[(int)m.to] = m.promote;
	else
            piece[(int)m.to] = piece[(int)m.from];
	color[(int)m.from] = EMPTY;
	piece[(int)m.from] = EMPTY;

	/* erase the pawn if this is an en passant move */
	if ((m.bits & 4) != 0) {
            if (side == LIGHT) {
                color[m.to + 8] = EMPTY;
                piece[m.to + 8] = EMPTY;
            }
            else {
                color[m.to - 8] = EMPTY;
                piece[m.to - 8] = EMPTY;
            }
        }

	/* switch sides and test for legality (if we can capture
	   the other guy's king, it's an illegal position and
	   we need to take the move back) */
	side ^= 1;
	xside ^= 1;
	if (inCheck(xside)) {
            takeBack();
            return false;
	}
	return true;
    }


/* takeBack() is very similar to makeMove(), only backwards :)  */
/**
 * takeBack()
 * 
 * move undo.  Returns the game state and history structures back to their pre-move state.
 * This is very important for our Minimax/AB searches since we need to do this a lot in searching.
 * 
 * @param - none
 * @return - void.
 */
    void takeBack() {
    //Step 1:  Reset sides.
	side ^= 1;
	xside ^= 1;
	//Step 2:  Using our history values, reset the piece, color an board states.
	--hply;
	Project2_Move_Thompson m = histDat[hply].m;
	castle = histDat[hply].castle;
	ep = histDat[hply].ep;
	fifty = histDat[hply].fifty;
	color[(int)m.from] = side;
	if ((m.bits & 32) != 0)
            piece[(int)m.from] = PAWN;
	else
            piece[(int)m.from] = piece[(int)m.to];
	//Was it a capture?
	if (histDat[hply].capture == EMPTY) {
            color[(int)m.to] = EMPTY;
            piece[(int)m.to] = EMPTY;
	}
	else {
            color[(int)m.to] = xside;
            piece[(int)m.to] = histDat[hply].capture;
	}
	
	//Check for our special moves.
	if ((m.bits & 2) != 0) {
            int from, to;

            switch(m.to) {
                case 62:
                    from = F1;
                    to = H1;
                    break;
                case 58:
                    from = D1;
                    to = A1;
                    break;
                case 6:
                    from = F8;
                    to = H8;
                    break;
                case 2:
                    from = D8;
                    to = A8;
                    break;
                default:  /* shouldn't get here */
                    from = -1;
                    to = -1;
                    break;
            }
            color[to] = side;
            piece[to] = ROOK;
            color[from] = EMPTY;
            piece[from] = EMPTY;
	}
	if ((m.bits & 4) != 0) {
            if (side == LIGHT) {
                color[m.to + 8] = xside;
                piece[m.to + 8] = PAWN;
            }
            else {
                color[m.to - 8] = xside;
                piece[m.to - 8] = PAWN;
            }
	}
    }
    
    
    /**
     * 
     * Custom toString() implementation for our bard.
     * Not used in Minimax/AB searching.
     */
    public String toString() {
	int i;
	
        StringBuffer sb = new StringBuffer("\n8 ");
	for (i = 0; i < 64; ++i) {
            switch (color[i]) {
                case EMPTY:
                    sb.append(" .");
                    break;
                case LIGHT:
                    sb.append(" ");
                    sb.append(pieceChar[piece[i]]);
                    break;
                case DARK:
                    sb.append(" ");
                    sb.append((char) (pieceChar[piece[i]] + ('a' - 'A')));
                    break;
                default:
                    throw new IllegalStateException("Square not EMPTY, LIGHT or DARK: " + i);
            }
            if ((i + 1) % 8 == 0 && i != 63) {
                sb.append("\n");
                sb.append(Integer.toString(7 - ROW(i)));
                sb.append(" ");
            }
	}
	sb.append("\n\n   a b c d e f g h\n\n");
        return sb.toString();
    }

    /** reps() 
     * returns the number of times that the current position has been repeated. 
     * Thanks to John Stanback for this clever algorithm. 
     * 
     * @returns number of  times a position has been repeated.
     * 
     * Used in checking and terminating a draw early in the state.
     * */
    
    int reps() {
	int b[] = new int[64];
	int c = 0;  /* count of squares that are different from
				   the current position */
	int r = 0;  /* number of repetitions */

	/* is a repetition impossible? */
	if (fifty <= 3)
		return 0;

	/* loop through the reversible moves */
	for (int i = hply - 1; i >= hply - fifty - 1; --i) {
            if (++b[histDat[i].m.from] == 0)
                --c;
            else
                ++c;
            if (--b[histDat[i].m.to] == 0)
                --c;
            else
                ++c;
            if (c == 0)
                ++r;
	}

	return r;
    }

    

 
    /**
     * 
     * Minimax/Alpha Beta Huersitic Function
     * 
     * Takes into account three things:
     * 
     * 1.  Material Value
     * 2.  Piece Mobility
     * 3.  Piece Square Values
     * 
     * Piece/Square values were taken from the original source.
     * 
     * The heuristic also awards a bonus if you have your opponent in check since it seemed to encourage a more aggressive playing style.
     * 
     *
     * 
     * 
     * @return Score for the algorithm relative to the human player.
     */
    public int getScore()
    {   	
    	//mobility bonus
    	int mobilityDark = 0;
    	int mobilityLight = 0;
    	//position bonus
    	int positionDark = 0;
    	int positionLight = 0;
    	//Opponent in Check Bonuses
    	int checkDark = 0;
    	int checkLight = 0;
    	/* material score and mobility calculations. */
    	for (int i = 0; i < 10; ++i) {
    		pawnRank[LIGHT][i] = 0;
    		pawnRank[DARK][i] = 7;
    	}
    	//Initialize all our arrays to start the board iteration.
    	pieceMat[LIGHT] = 0;
    	pieceMat[DARK] = 0;
    	pawnMat[LIGHT] = 0;
    	pawnMat[DARK] = 0;
    	//We need to check every square.
    	for (int i = 0; i < 64; ++i) {
    			//Of no use to our h_val, skip it.
                if (color[i] == EMPTY)
                    continue;
                //We found a pawn and since we have to deal with pawnRank, we need to o some more processing.
                if (piece[i] == PAWN) {
                	//Add material score for pawn for the color
                    pawnMat[color[i]] += pieceValue[PAWN];
                    int f = COL(i) + 1;  /* add 1 because of the extra file in the array */
                    if (color[i] == LIGHT) {
                        if (pawnRank[LIGHT][f] < ROW(i))
                            pawnRank[LIGHT][f] = ROW(i);
                    }
                    //Same for dark.
                    else {
                        if (pawnRank[DARK][f] > ROW(i))
                            pawnRank[DARK][f] = ROW(i);
                        }
    		}
    		else
    			//Not a pawn so just add the piece's material value to our running total.
                        pieceMat[color[i]] += pieceValue[piece[i]];
                
              /*
               * Start Mobility Check
               * 
               * Pretty simple, we know there are a set or rules governing the movement of pieces.
               * By concerning ourselves with only the next possible legal moves, we can get a sense of what our next ply will look like 
               * and as a consequence, make our heuristic smarter without (significantly) more processing.
               * 
               * 
               */
                    if (color[i] != EMPTY) {
                    	//Using the rules governing Pawn movement, check the next possible legal moves an award a point bonus for ones that are available.
                        if (piece[i] == PAWN) {
                            if (color[i] == LIGHT) {
                                if (COL(i) != 0 && color[i - 9] == DARK)
                                    mobilityLight = mobilityLight + 1;
                                if (COL(i) != 7 && color[i - 7] == DARK)
                                	mobilityLight = mobilityLight + 1;
                                if (color[i - 8] == EMPTY) {
                                	mobilityLight = mobilityLight + 1;
                                    if (i >= 48 && color[i - 16] == EMPTY)
                                    	mobilityLight = mobilityLight + 1;
                                }
                            }
                            //Do the same thing for the other side.
                            else {
                                if (COL(i) != 0 && color[i + 7] == LIGHT)
                                    ++mobilityDark;
                                if (COL(i) != 7 && color[i + 9] == LIGHT)
                                	 ++mobilityDark;
                                if (color[i + 8] == EMPTY) {
                                	 ++mobilityDark;
                                    if (i <= 15 && color[i + 16] == EMPTY)
                                    	 ++mobilityDark;
                                }
                            }
                        }
                        //We have dealt with pawns, now generate mobility values for our other pieces.
                        else
                            for (int j = 0; j < offsets[piece[i]]; ++j)
                                for (int n = i;;) {
                                    n = mailbox[mailbox64[n] + offset[piece[i]][j]];
                                    if (n == -1)
                                        break;
                                    if (color[n] != EMPTY) { //check color
                                        if (color[n] == LIGHT)
                                            mobilityLight = mobilityLight + 1;
                                        else
                                        	mobilityDark = mobilityDark + 1;
                                        break;
                                    }
                                }
                        
                    }
                    //Try to evaluate our current position based on known good states (Piece/Square values).
                    //The previous code returned an evaluation in the range of -3000 to 3000 since we have piece/square values that are negative
                    //I offset all the values originally used by 40 to avoid the change of a negative heuristic value.
                    if (color[i] == LIGHT) {
                        switch (piece[i]) {
                            case PAWN:
                                positionLight += (evalLightPawn(i) + 40);
                                break;
                            case KNIGHT:
                            	positionLight += (knightPcsq[i] + 40);
                                break;
                            case BISHOP:
                            	positionLight += (bishopPcsq[i] + 40);
                                break;
                            case ROOK:
                            	//There were some pawn bonuses awarded in the original code, I left those in tact.
                                if (pawnRank[LIGHT][COL(i) + 1] == 0) {
                                    if (pawnRank[DARK][COL(i) + 1] == 7)
                                    	positionLight += ROOK_OPEN_FILE_BONUS;
                                    else
                                    	positionLight += ROOK_SEMI_OPEN_FILE_BONUS;
                                }
                                if (ROW(i) == 1)
                                	positionLight += ROOK_ON_SEVENTH_BONUS;
                                break;
                            case KING:
                                if (pieceMat[DARK] <= 1200)
                                	positionLight += (kingEndgamePcsq[i] + 40);
                                else
                                	positionLight += (evalLightKing(i)+ 40);
                                break;
                        }
                    }
                    else {
                        switch (piece[i]) {
                            case PAWN:
                                positionDark += (evalDarkPawn(i) + 40);
                                break;
                            case KNIGHT:
                            	positionDark += (knightPcsq[flip[i]] + 40);
                                break;
                            case BISHOP:
                            	positionDark+= (bishopPcsq[flip[i]] + 40);
                                break;
                            case ROOK:
                                if (pawnRank[DARK][COL(i) + 1] == 7) {
                                    if (pawnRank[LIGHT][COL(i) + 1] == 0)
                                    	positionDark += ROOK_OPEN_FILE_BONUS;
                                    else
                                    	positionDark += ROOK_SEMI_OPEN_FILE_BONUS;
                                }
                                if (ROW(i) == 6)
                                	positionDark += ROOK_ON_SEVENTH_BONUS;
                                break;
                            case KING:
                                if (pieceMat[LIGHT] <= 1200)
                                	positionDark += (kingEndgamePcsq[flip[i]] + 40);
                                else
                                	positionDark += (evalDarkKing(i) + 40);
                                break;
                        }
                    }

        	/* generate castle moves */
        	if (color[i] == LIGHT) {
                    if ((castle & 1) != 0)
                        ++mobilityLight;
                    if ((castle & 2) != 0)
                        ++mobilityLight;
        	}
        	else {
                    if ((castle & 4) != 0)
                        ++mobilityDark;
                    if ((castle & 8) != 0)
                        ++mobilityDark;
        	}
        	
        	/* generate en passant moves */
        	if (ep != -1) {
                    if (color[i] == LIGHT) {
                        if (COL(ep) != 0 && color[ep + 7] == LIGHT && piece[ep + 7] == PAWN)
                            ++mobilityLight;
                        if (COL(ep) != 7 && color[ep + 9] == LIGHT && piece[ep + 9] == PAWN)
                            ++mobilityLight;
                    }
                    else {
                        if (COL(ep) != 0 && color[ep - 9] == DARK && piece[ep - 9] == PAWN)
                            ++mobilityDark;
                        if (COL(ep) != 7 && color[ep - 7] == DARK && piece[ep - 7] == PAWN)
                            ++mobilityDark;
                    }
        	}
        	
    	}
    	
    	
    	/*
    	 * Makes sense to weight inCheck states higher since we are close to winning at that point.
    	 * 
    	 */
    	if(this.inCheck(LIGHT))
    	{
    		checkLight = checkLight + 100;
    	}
    	if(this.inCheck(DARK))
    	{
    		checkDark = checkDark + 100;
    	}
    	//Get the final score for the two sides
    	int finalScoreDark = pieceMat[DARK] + pawnMat[DARK] + mobilityDark + checkLight + positionDark;
    	int finalScoreLight = pieceMat[LIGHT] + pawnMat[LIGHT] + mobilityLight + checkDark + positionLight;
    	
    	//return the score relative to how good it is for the computer vs the human player.
    	return finalScoreDark - finalScoreLight;
    }
        
/**
 * 
 * Evaluates a pawn in a given square returning it's calculated value.
 * 
 * Evaluate the pawn using it's location and our piece square table.
 * 
 * @param sq - Square we are evaluating.
 * @return - Score of pawn.
 */
    int evalLightPawn(int sq) {
	int r = 0; /* return value */
	int f = COL(sq) + 1; /* pawn's file */

	r += pawnPcsq[sq];

	/* if there's a pawn behind this one, it's doubled */
	if (pawnRank[LIGHT][f] > ROW(sq))
            r -= DOUBLED_PAWN_PENALTY;

	/* if there aren't any friendly pawns on either side of
	   this one, it's isolated */
	if ((pawnRank[LIGHT][f - 1] == 0) &&
                (pawnRank[LIGHT][f + 1] == 0))
            r -= ISOLATED_PAWN_PENALTY;

	/* if it's not isolated, it might be backwards */
	else if ((pawnRank[LIGHT][f - 1] < ROW(sq)) &&
                (pawnRank[LIGHT][f + 1] < ROW(sq)))
            r -= BACKWARDS_PAWN_PENALTY;

	/* add a bonus if the pawn is passed */
	if ((pawnRank[DARK][f - 1] >= ROW(sq)) &&
                (pawnRank[DARK][f] >= ROW(sq)) &&
                (pawnRank[DARK][f + 1] >= ROW(sq)))
            r += (7 - ROW(sq)) * PASSED_PAWN_BONUS;

        return r;
    }

    /**
     * Same thing as our evalLightPawn function.
     * 
     * Evaluate the pawn using it's location and our piece square table.
     * 
     * 
     * @param sq - Square we are located in
     * @return - Score represented by the pawn.
     */
int evalDarkPawn(int sq) {
	int r = 0;  /* the value to return */
	int f = COL(sq) + 1;  /* the pawn's file */

	r += pawnPcsq[flip[sq]];

	/* if there's a pawn behind this one, it's doubled */
	if (pawnRank[DARK][f] < ROW(sq))
            r -= DOUBLED_PAWN_PENALTY;

	/* if there aren't any friendly pawns on either side of
	   this one, it's isolated */
	if ((pawnRank[DARK][f - 1] == 7) &&
                (pawnRank[DARK][f + 1] == 7))
            r -= ISOLATED_PAWN_PENALTY;

	/* if it's not isolated, it might be backwards */
	else if ((pawnRank[DARK][f - 1] > ROW(sq)) &&
                (pawnRank[DARK][f + 1] > ROW(sq)))
            r -= BACKWARDS_PAWN_PENALTY;

	/* add a bonus if the pawn is passed */
	if ((pawnRank[LIGHT][f - 1] <= ROW(sq)) &&
                (pawnRank[LIGHT][f] <= ROW(sq)) &&
                (pawnRank[LIGHT][f + 1] <= ROW(sq)))
            r += ROW(sq) * PASSED_PAWN_BONUS;

	return r;
    }


	/**
	 * 
	 * Evaluate the King in the square sq for the Light Player.
	 * 
	 * @param sq - Square we are evaluating.
	 * @return - Score for king at this position
	 */
    int evalLightKing(int sq) {
	int r = kingPcsq[sq]; /* return value */

	/* if the king is castled, use a special function to evaluate the
	   pawns on the appropriate side */
	if (COL(sq) < 3) {
            r += evalLkp(1);
            r += evalLkp(2);
            r += evalLkp(3) / 2;  /* problems with pawns on the c & f files
                                                                are not as severe */
	}
	else if (COL(sq) > 4) {
            r += evalLkp(8);
            r += evalLkp(7);
            r += evalLkp(6) / 2;
	}

	/* otherwise, just assess a penalty if there are open files near
	   the king */
	else {
            for (int i = COL(sq); i <= COL(sq) + 2; ++i)
                if ((pawnRank[LIGHT][i] == 0) &&
                        (pawnRank[DARK][i] == 7))
                    r -= 10;
	}

	/* scale the king safety value according to the opponent's material;
	   the premise is that your king safety can only be bad if the
	   opponent has enough pieces to attack you */
	r *= pieceMat[DARK];
	r /= 3100;

	return r;
    }

    /* evalLkp(f) evaluates the Light King Pawn on file f */

    int evalLkp(int f) {
	int r = 0;

	if (pawnRank[LIGHT][f] == 6);  /* pawn hasn't moved */
	else if (pawnRank[LIGHT][f] == 5)
            r -= 10;  /* pawn moved one square */
	else if (pawnRank[LIGHT][f] != 0)
            r -= 20;  /* pawn moved more than one square */
	else
            r -= 25;  /* no pawn on this file */

	if (pawnRank[DARK][f] == 7)
            r -= 15;  /* no enemy pawn */
	else if (pawnRank[DARK][f] == 5)
            r -= 10;  /* enemy pawn on the 3rd rank */
	else if (pawnRank[DARK][f] == 4)
            r -= 5;   /* enemy pawn on the 4th rank */

	return r;
    }

    
    
    
    /**
	 * 
	 * Evaluate the King in the square sq for the Dark Player.
	 * 
	 * @param sq - Square we are evaluating.
	 * @return - Score for king at this position
	 */
    
    
    int evalDarkKing(int sq) {
	int r;
	int i;

	r = kingPcsq[flip[sq]];
	if (COL(sq) < 3) {
            r += evalDkp(1);
            r += evalDkp(2);
            r += evalDkp(3) / 2;
	}
	else if (COL(sq) > 4) {
            r += evalDkp(8);
            r += evalDkp(7);
            r += evalDkp(6) / 2;
	}
	else {
            for (i = COL(sq); i <= COL(sq) + 2; ++i)
                if ((pawnRank[LIGHT][i] == 0) &&
                        (pawnRank[DARK][i] == 7))
                    r -= 10;
	}
	r *= pieceMat[LIGHT];
	r /= 3100;
	return r;
    }

    int evalDkp(int f) {
	int r = 0;

	if (pawnRank[DARK][f] == 1);
	else if (pawnRank[DARK][f] == 2)
            r -= 10;
	else if (pawnRank[DARK][f] != 7)
            r -= 20;
	else
            r -= 25;

	if (pawnRank[LIGHT][f] == 0)
            r -= 15;
	else if (pawnRank[LIGHT][f] == 2)
            r -= 10;
	else if (pawnRank[LIGHT][f] == 3)
            r -= 5;

	return r;
    }

    static int COL(int x) { return (x & 7); }
    static int ROW(int x) { return (x >> 3); }
}
