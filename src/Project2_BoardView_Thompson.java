// Original code by David Dagon copyright 1997 (c) David Dagon
// Used with his permission.
// Heavily modified by Peter Hunter
// Current version copyright 2001 (c) Peter Hunter
// All rights reserved.

/*
$Log: BoardView.java,v $
Revision 1.7  2002/01/19 00:19:20  peter
Make anonymous inner thinker class a named inner class.

Revision 1.6  2002/01/15 23:55:46  peter
Don't forget to switch move markers!

Revision 1.5  2002/01/15 23:40:55  peter
Changes to add promotion piece selection and display of the results of a game.

Revision 1.4  2002/01/07 20:00:23  peter
Make the computer move in a separate thread from the GUI thread so that the GUI is nice and responsive.

Revision 1.3  2002/01/07 15:44:17  peter
Add copyright stuff.

Revision 1.2  2002/01/07 15:10:47  peter
Changes so that searching can be done by a separate class. Better data hiding.

Revision 1.1  2002/01/01 02:59:24  peter
Initial check-in.

---- new repository

Revision 1.3  2001/12/29 21:43:43  peter
Add move markers. Switch to using getPiece() since board[][] is now private. Tidy up some names to remove brain-dead Hungarian notation.

Revision 1.2  2001/06/21 14:18:20  peter
Copied selected() to here - it should be in a Controller class!
We need to do more for Square now that it is lighter-weight.

Revision 1.1  2001/06/21 09:11:13  peter
Initial check-in.
*/

import javax.swing.*;
import java.awt.*;
import java.applet.Applet;
import java.io.IOException;
import java.util.*;

/** BoardView class - draws the board

@author Peter Hunter
@version $Revision: 1.7 $ $Date: 2002/01/19 00:19:20 $

@modified Kurtis Thompson

1.  Added setMaxDepth to allow the user to select the maximum search depth.
2.  Added SEARCH_ALGORITHM variable to allow us to set the algorithm we are going to use (0 - Random, 1 Minimax, 2 Alphabeta)
3.  Added RANDOMIZATION_ALGORITHM variable to allow users to set the randomization algorithm we are going to use.
4.  Modified computermove to allow the use of random-moves instead of automatically spawning the thread.

@version 2011.0402.
@since 1.6
*/

final class Project2_BoardView_Thompson extends JPanel {
    private final static int offset = 25;
    
    /**
     * Our applet.
     */
    private final Project2_ChessApp_Thompson app;
    /**
     * Our board object for the current game.
     */
    private Project2_Board_Thompson board;
    /**
     * Search object, holds our Minimax/Alpha beta methods.
     */
    private Project2_Search_Thompson searcher;
    /**
     * Number of moves made
     */
    private int moves = 0;
    /**
     * Deprecated, original code allowed for playing as white or black.
     */
    private boolean flip = false;
    private boolean first = true;
    /**
     * Display panels.
     */
    private final JLabel wtm, btm;
    private final JPanel mmPanel;
    /**
     * Board settings.
     */
    private Project2_Square_Thompson[][] square = new Project2_Square_Thompson[8][8];
    private int startCol, startRow;
    /**
     * Maximum search time default
     */
    private int maxTime = 10000;
    /**
     * Maximum search depth for our iterative deepening.
     */
    private int maxDepth = 32;
    /**
     * boolean semaphore for someone making a move.
     */
    private boolean moving = false;
    /**
     * Default setting for search algorithm (Alpha Beta)
     */
    public int SEARCH_ALGORITHM = 2;
    /**
     * Default setting for random move randomization algorithm.
     */
    public int RANDOMIZATION_ALGORITHM = 0;

    /** Constructs a board view.
     * @param applet The applet we're using the board in.
     * @param b The board of which this is the view
     */
     
    Project2_BoardView_Thompson(Project2_ChessApp_Thompson applet, Project2_Board_Thompson b) {
        super();
        app = applet;
        board = b;
        searcher = new Project2_Search_Thompson(b);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(10, 10));
        letterRow(boardPanel);
        for (int i = 0; i < 8; i++) {
            boardPanel.add(new BLabel(convertNum(i, false)));
            for (int j = 0; j < 8; j++) {
                square[i][j] = new Project2_Square_Thompson(i, j, this);
                int color = b.getColor(i, j);
                if (color != Project2_Board_Thompson.EMPTY) {
                    int piece = b.getPiece(i, j);
                    square[i][j].setIcon(new ImageIcon(pieceImage[color][piece]));
                }
                boardPanel.add(square[i][j]);
            }
            boardPanel.add(new BLabel(convertNum(i, false)));
        }
        letterRow(boardPanel);

        mmPanel = new JPanel();
        mmPanel.setLayout(new GridLayout(2, 1));

        Icon whiteMoveIcon = new ImageIcon(app.getImage(
            getClass().getResource("images/wMove.gif")));
        wtm = new JLabel(whiteMoveIcon);

        Icon blackMoveIcon = new ImageIcon(app.getImage(
            getClass().getResource("images/bMove.gif")));
        btm = new JLabel(blackMoveIcon);

        wtm.setVerticalAlignment(JLabel.BOTTOM);
        wtm.setHorizontalAlignment(JLabel.CENTER);
        wtm.setPreferredSize(new Dimension(104, 168));

        btm.setVerticalAlignment(JLabel.TOP);
        btm.setHorizontalAlignment(JLabel.CENTER);
        btm.setPreferredSize(new Dimension(104, 168));
        btm.setVisible(false);

        mmPanel.add(btm);
        mmPanel.add(wtm);
        
        setLayout(new FlowLayout());
        add(boardPanel); add(mmPanel);
    }
    
    /**
     * Set the board and create our searcher to find moves.
     * @param b - current board.
     */
    public void setBoard(Project2_Board_Thompson b) {
        board = b;
        searcher = new Project2_Search_Thompson(b);
        reset();
    }
    /**
     * Set the maximum time allowed for a  move.
     * @param millis - maximum time allowed for a move in milliseconds.
     */
    public void setMaxTime(int millis) {
        maxTime = millis;
        //maxDepth = 32;
    }
    
    /**
     * Simply creates the letter rows on the border of the board.
     * @param p - Display panel.
     */
    private void letterRow(JPanel p) {
        p.add(new JPanel());
        for (int i = 0; i < 8; i++)
            p.add(new BLabel(convertNum(i, true)));
        p.add(new JPanel());
    }
    
    /** Called when the user selects a square
     *
     * @param row The row of the square clicked
     * @param col The column
     */
     
    void selected(int row, int col) {
        if (moving) {
            app.showStatus("The computer is moving - please wait!");
            return;
        }
        String status;
        if (first) {
            int piece = board.getPiece(row, col);
            int color = board.getColor(row, col);
            // FIRST, is the starting coordinate an empty square?
            if (piece == Project2_Board_Thompson.EMPTY) {
                status = "That square is empty. Player " +
                                (board.isWhiteToMove() ? "White" : "Black") + " to Move";
                app.showStatus(status);
                return;
            }

            // SECOND, has the player clicked on opponent's piece?
            else if (board.side != color) {
                app.showStatus("That's not your piece!");
                return;
            }

            // THIRD, accept selection of piece to move
            else {
                app.showStatus("Select destination for that piece");
                startCol = col;
                startRow = row;
                first = false;
                return;
            }
        }
        TreeSet validMoves = board.gen();
        int from = (startRow << 3) + startCol;
        int to = (row << 3) + col;
        boolean found = false;
        int promote = 0;
        if ((((to < 8) && (board.side == Project2_Board_Thompson.LIGHT)) || ((to > 55) && (board.side == Project2_Board_Thompson.DARK))) &&
            (board.getPiece(startRow, startCol) == Project2_Board_Thompson.PAWN)) {
            ImageIcon[] icons = new ImageIcon[6];
            for (int i = Project2_Board_Thompson.KNIGHT; i <= Project2_Board_Thompson.QUEEN; i++) 
                icons[i] = new ImageIcon(pieceImage[board.side][i]);
            Object[] options = {icons[Project2_Board_Thompson.KNIGHT], icons[Project2_Board_Thompson.BISHOP], icons[Project2_Board_Thompson.ROOK], icons[Project2_Board_Thompson.QUEEN]};
            int choice = JOptionPane.showOptionDialog(this, "Promote to which piece?", "Promotion", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[3]);
            if (choice == JOptionPane.CANCEL_OPTION) choice = 3;
            promote = choice + 1;
        }
        
        Iterator i = validMoves.iterator();
        Project2_Move_Thompson m = null;
        while (i.hasNext()) {
            m = (Project2_Move_Thompson) i.next();
            if (m.from == from && m.to == to && m.promote == promote) {
                found = true;
                break;
            }
        }
        if (!found || !board.makeMove(m)) {
            System.out.println("Illegal move.\n");
            first = true;
        }
        else {
            movePieces(m);
            switchMoveMarkers();
            first = true;
            
            if (isResult()) return;

            if (board.side == app.computerSide) {
                computerMove();
            }
        }
    }
    
    
    /**
     * Sets the maximum depth to which we will search.
     * @param depth - maximum depth (1-32).
     */
    void setMaxDepth(int depth)
    {
    	this.maxDepth = depth;
    }

    
    /**
     * Makes the computer move.
     * 
     * 1.  Check search algorithm, if 0 then make a random move.
     * 2.  If 1, use Minimax 
     * 3.  If 2, use Alpha Beta pruning.
     * 
     * Makes the move, updates the display and resets the moving variable to indicate it is finished.
     * 
     * 
     * 
     */
    void computerMove() {
    	//Check for our random move first.
    	moving = true;
    	if(this.SEARCH_ALGORITHM == 0)
    	{
    		Project2_Move_Thompson best = searcher.getRandomMove(this.RANDOMIZATION_ALGORITHM);
            // best = searcher.getRandom();
             //System.out.println("Best Move: " + best.from + " to " + best.to);
             if (best.hashCode() == 0) {
                 System.out.println("(no legal moves)");
                 app.computerSide = Project2_Board_Thompson.EMPTY;
                 return;
             }
             //Make the move.
             board.makeMove(best);
             movePieces(best);
             //Finished, now let's reset the GUI to let the human player move.
             switchMoveMarkers();
             isResult();
             moving = false;
    	}
    	else
    	{
    		//We have selected either Minimax or Alpha Beta
    		board.SEARCH = this.SEARCH_ALGORITHM;
    		//Start our Thinker thread.
    		(new Thinker()).start();
    		
    	}
    }
    
    /**
     * 
     * Function to actually move the pieces we see on the board.
     * Basically, figure out how the move maps to our GUI and then updates our images to show the move being made.
     * 
     * @param m - The move we need to execute.
     */
    void movePieces(Project2_Move_Thompson m) {
        int fromRow = Project2_Board_Thompson.ROW(m.from);
        int fromCol = Project2_Board_Thompson.COL(m.from);
        int toRow = Project2_Board_Thompson.ROW(m.to);
        int toCol = Project2_Board_Thompson.COL(m.to);
        square[toRow][toCol].setIcon(new ImageIcon(
            pieceImage[board.color[m.to]][board.piece[m.to]]));
        square[fromRow][fromCol].setIcon(null);
        if ((m.bits & 2) != 0) { // castling move
            if (m.from == Project2_Board_Thompson.E1 && m.to == Project2_Board_Thompson.G1)
                movePieces(new Project2_Move_Thompson(Project2_Board_Thompson.H1, Project2_Board_Thompson.F1, (char) 0, (char) 0));
            else if (m.from == Project2_Board_Thompson.E1 && m.to == Project2_Board_Thompson.C1)
                movePieces(new Project2_Move_Thompson(Project2_Board_Thompson.A1, Project2_Board_Thompson.D1, (char) 0, (char) 0));
            else if (m.from == Project2_Board_Thompson.E8 && m.to == Project2_Board_Thompson.G8)
                movePieces(new Project2_Move_Thompson(Project2_Board_Thompson.H8, Project2_Board_Thompson.F8, (char) 0, (char) 0));
            else if (m.from == Project2_Board_Thompson.E8 && m.to == Project2_Board_Thompson.C8)
                movePieces(new Project2_Move_Thompson(Project2_Board_Thompson.A8, Project2_Board_Thompson.D8, (char) 0, (char) 0));
        }
    }

    /**
     * Check for end-game (or possible) states.
     * 
     * We have a few possibilities:
     * 
     * 1.  Check-Mate for white/black
     * 2.  Repetition draw.
     * 3.  50 move stale mate.
     * 4.  Check
     * 
     * If we have a result to our game, return true, otherwise return false.
     * 
     * @return true or our game is over, false if we are only in check.
     */
    boolean isResult() {
        TreeSet validMoves = board.gen();
        
        //Simple way to check for checkmate or stale mate, try making moves.
        Iterator i = validMoves.iterator();
        boolean found = false;
        while (i.hasNext()) {
            if (board.makeMove((Project2_Move_Thompson) i.next())) {
                board.takeBack();
                found = true;
                break;
            }
        }
        String message = null;
        if (!found) {
            if (board.inCheck(board.side)) {
                if (board.side == Project2_Board_Thompson.LIGHT)
                    message = "0 - 1 Black mates";
                else
                    message = "1 - 0 White mates";
            }
            else
                message = "0 - 0 Stalemate";
        }
        else if (board.reps() == 3)
            message = "1/2 - 1/2 Draw by repetition";
        else if (board.fifty >= 100)
            message = "1/2 - 1/2 Draw by fifty move rule";
        if (message != null) {
            int choice = JOptionPane.showConfirmDialog(this, message + "\nPlay again?", "Play Again?", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                app.playNewGame();
            }
            return true;
        }
        if (board.inCheck(board.side))
            app.showStatus("Check!");
        return false;
    }
    
    /** Switches the "To Move" marker from white to black or vice versa
    */
    
    void switchMoveMarkers() {
        if (board.isWhiteToMove()) {
            wtm.setVisible(true);
            btm.setVisible(false);
        } else {
            btm.setVisible(true);
            wtm.setVisible(false);
        }
    }
    
    /** Resets the view */
    
    void reset() {
        repaintEverything();
        first = true;
    }
    
    /** Resets the piece icon in every square */
    
    void repaintEverything() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                int piece = board.getPiece(i, j);
                int color = board.getColor(i, j);
                if (piece != Project2_Board_Thompson.EMPTY) square[i][j].setIcon(new 
                    ImageIcon(pieceImage[color][piece]));
                else square[i][j].setIcon(null);
            }
    }
    
    private String convertNum(int temp, boolean first) {
        if (first)
            return (new Character((char) ('a' + temp))).toString();
        else
            return Integer.toString(8 - temp);
    }
    
    private static Image pieceImage[][] = new Image[2][6];
    private static String imageFilename[][] = {
        { "wp.gif", "wn.gif", "wb.gif", "wr.gif", "wq.gif", "wk.gif" },
        { "bp.gif", "bn.gif", "bb.gif", "br.gif", "bq.gif", "bk.gif" }};
        
    public static void bufferImages(Applet app) {
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 6; j++)
                pieceImage[i][j] = app.getImage(app.getClass().getResource("images/" + 
                    imageFilename[i][j]));
    }
    
    class BLabel extends JLabel {
        BLabel(String s) {
            super(s);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }
    }
    
    /**
     * Thinker thread
     * 
     * When it is the computer's turn to make a move, this thread is launched to actually perform either our Minimax or Alpha Beta search.
     * After finding a move, the thread makes it, updates the GUI and then updates the move marker and indicates that it is finshed moving.
     * 
     * @author kurtis thompson
     * @version 040211 
     * @since 1.6
     *
     */
    class Thinker extends Thread {
        public void run() {
        	//think launches one of our searches depending on what we have selected.
            searcher.think(1, maxTime, maxDepth);
            //The move is stored at the end of our PV arra so return the last value.
            Project2_Move_Thompson best = searcher.getBest();
           // best = searcher.getRandom();
            //System.out.println("Best Move: " + best.from + " to " + best.to);
            if (best.hashCode() == 0) {
                System.out.println("(no legal moves)");
                app.computerSide = Project2_Board_Thompson.EMPTY;
                return;
            }
            //Make the move.
            board.makeMove(best);
            //Update display
            movePieces(best);
            //Release turn.
            switchMoveMarkers();
            isResult();
            moving = false;
        }
    }
}
