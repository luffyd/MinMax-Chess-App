

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;


/**
 * ChessApp.java
 * 
 * A simple Java Swing applet to play chess.
 * Copyright (c) 2001 Peter Hunter. GUI was originally based on code
 * by David Dagon (c) 1997 David Dagon and is used with his permission.
 * The search code is heavily based on Tom Kerrigan's tscp, for which he
 * owns the copyright, and is used with his permission. All rights are
 * reserved by the owners of the respective copyrights.
 * 
 * @modified Kurtis Thompson:
 * 
 * Very simple modifications.  
 * 
 * 1.  Removed the 'Switch Sides' capability.
 * 2.  Added values to the time drop down list.
 * 3.  Added drop-down to select maximum depth for our search, ranging from 1-32.
 * 4.  Added drop-down list to select Search Algorithm and pass that to the boardView for our searcher.
 * 4.  Added drop-down to select Randomization Algorithm if random move is selected.
 * 
 */

public class Project2_ChessApp_Thompson extends JApplet {
	//Our board
    private Project2_Board_Thompson board = new Project2_Board_Thompson();
    //Our board view handler.
    private Project2_BoardView_Thompson brdView;
    
    //Color of computer pieces.
    int computerSide = Project2_Board_Thompson.DARK;
    
    //Choices for length of computers turn.
    private final int[] playTime = {1000, 3000, 5000, 10000, 20000, 30000, 60000};
    
    public void init() {
        super.init();
        
        showStatus ("Please Wait; Program Loading");
        Project2_BoardView_Thompson.bufferImages(this);
        JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));

        //add (flipBox = new Checkbox("Flip Board"));
        JButton resetButton = new JButton ("New Game");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                computerSide = Project2_Board_Thompson.DARK;
                playNewGame();
            }
        });
        p1.add(resetButton);    
        
       

        /*
         * Populate our time limit combo-box and add a handler to set the selected value in our BoardView object.
         * 
         */
        p1.add(new JLabel("Time Limit: "));
        String[] timeStrings = { "1 second", "3 seconds", "5 seconds", "10 seconds", "20 seconds", "30 seconds", "1 minute" };
        JComboBox timeBox = new JComboBox(timeStrings);
        timeBox.setSelectedIndex(3);
        timeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                int selection = cb.getSelectedIndex();
                brdView.setMaxTime(playTime[selection]);
            }
        });
        p1.add(timeBox);
        
      
        
        /*
         * Populate our depth limit combo-box and add a handler to set the selected value in our BoardView object.
         * 
         */
        p1.add(new JLabel("Depth Limit: "));
        String[] depthStrings = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32"};
        JComboBox depthBox = new JComboBox(depthStrings);
        depthBox.setSelectedIndex(9);
        depthBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                int selection = cb.getSelectedIndex();
                brdView.setMaxDepth(selection);
            }
        });
        p1.add(depthBox);
        
        /*
         * 
         * We have three move options:
         * 1.  Random move
         * 2.  Minimax
         * 3.  Alpha Beta
         * 
         * put those in a combo-box and then when one is selected, update our BoardView.
         * 
         */
        String[] moveAlgorithmStrings = {"Random Move", "Minimax", "Alpha Beta"};
        JComboBox algo = new JComboBox(moveAlgorithmStrings);
        algo.setSelectedIndex(2);
        algo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                int selection = cb.getSelectedIndex();
                brdView.SEARCH_ALGORITHM = selection;
                
            }
        });
        
        JPanel p3 = new JPanel();
        p3.add(new JLabel("Move: "));
        p3.add(algo);
        p3.add(new JLabel("Randomization: "));
        
        /*
         * Our random move can be made in two ways, Permute_By_Sorting or Randomize_In_Place.
         * 
         * Allow the user to seelct one.
         * 
         */
        
        String[] randomAlgorithmStrings = {"PERMUTE_BY_SORTING", "RANDOMIZE_IN_PLACE"};
        JComboBox random = new JComboBox(randomAlgorithmStrings);
        random.setSelectedIndex(0);
        random.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                int selection = cb.getSelectedIndex();
                
                brdView.RANDOMIZATION_ALGORITHM = selection;
                
            }
        });
        p3.add(random);
        
       
                
        JPanel p2 = new JPanel();
        brdView = new Project2_BoardView_Thompson(this, board);
        brdView.setMaxDepth(depthBox.getSelectedIndex());
        brdView.setMaxTime(playTime[timeBox.getSelectedIndex()]);
        p2.add(brdView);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(p1, "North");
        cp.add(p3, "South");
        cp.add(p2, "Center");
    }
    
    public void playNewGame() {
        board = new Project2_Board_Thompson();
        brdView.setBoard(board);
    }
}
