// Copyright (c) 2001 Peter Hunter

/*
$Log: Square.java,v $
Revision 1.2  2002/01/07 15:44:17  peter
Add copyright stuff.

Revision 1.1  2002/01/01 02:59:24  peter
Initial check-in.

Revision 1.4  2001/12/29 21:22:03  peter
Add some finals and tidy.

Revision 1.3  2001/12/29 21:20:12  peter
Smaller javadoc comments.

Revision 1.2  2001/06/21 14:13:55  peter
Square is now just a GUI element - no Piece inside.
Implemented using a JLabel instead of painting itself.

Revision 1.1  2001/06/21 09:11:38  peter
Initial check-in.
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Square class - draws a square

@author Peter Hunter
@version $Revision: 1.2 $ $Date: 2002/01/07 15:44:17 $

@modified - Kurtis Thompson - No modifications were made.

Simple class to draw a square on our chess board.

@version 2011.0423

*/

final class Project2_Square_Thompson extends JPanel {
    final private int x, y;
    final private Project2_BoardView_Thompson bv;
    final private JLabel jl;
    private boolean mouseIn = false;
    
    Project2_Square_Thompson(int y, int x, Project2_BoardView_Thompson b) {
        super();
        this.x = x;
        this.y = y;
        bv = b;
        if (((x + y) % 2) == 0) setBackground(Color.white);
        else setBackground(Color.gray);
        setPreferredSize(new Dimension(42, 42));
        jl = new JLabel();
        jl.setPreferredSize(new Dimension(32, 32));
        add(jl);
        addMouseListener(new SquareMouseListener());
    }
    
    void setIcon(Icon i) {
        jl.setIcon(i);
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        if (mouseIn) {
            g.setColor(Color.blue);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }
    
    class SquareMouseListener extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            mouseIn = true;
            repaint();
        }
        
        public void mouseExited(MouseEvent e) {
            mouseIn = false;
            repaint();
        }
        
        public void mouseClicked(MouseEvent e) {
        	
            bv.selected(y, x);
        }
    }
}
