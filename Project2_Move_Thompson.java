//
//  Move.java
/**
 * 
 * Simple move class used to keep track of origin, destination, promotion and bit represetation.
 * 
 * 
 * Created by Peter Hunter on Mon Dec 31 2001.
 * Copyright (c) 2001 Peter Hunter. All rights reserved.
 * 
 * @modified Kurtis Thompson
 * 
 * No modifications were made to this class.
 * 
 * @since 2011.0411.
 * 
 */
final class Project2_Move_Thompson implements Comparable {
    int from, to, promote, bits;
    int score = 0;
    
    /**
     * Constructor
     * 
     * @param from - origin of piece we are moving
     * @param to - destination of piece we are moving
     * @param promote - promotion of piece
     * @param bits - bits of piece.
     */
    Project2_Move_Thompson(int from, int to, int promote, int bits) {
        this.from = from;
        this.to = to;
        this.promote = promote;
        this.bits = bits;
    }
    
    /**
     * Get the value of the move
     * @return score of move.
     */
    
    int getScore() {
        return score;
    }
    
    /**
     * Set the score of the move
     * @param i - score value.
     */
    void setScore(int i) {
        score = i;
    }
    
    public int hashCode() {
        return from + (to << 8) + (promote << 16);
    }
    
    /**
     * Simple method to determine the equality of tewo moves.
     * 
     * @param - Object to which we are comaring.
     * @return true to false depending on if they are equal.
     */
    public boolean equals(Object o) {
    	Project2_Move_Thompson m = (Project2_Move_Thompson) o;
        return (m.from == from && m.to == to && m.promote == promote);
    }
    
    /**
     * Simple comparator method for moves.
     * @param - Object to which we are comparing.
     * 
     * @returns 1 if this is larger, 0 if equal, -1 if o is larger.
     */
    public int compareTo(Object o) {
    	Project2_Move_Thompson m = (Project2_Move_Thompson) o;
        int mScore = m.getScore();
        if (score < mScore) return 1;
        if (score > mScore) return -1;
        int mHashCode = m.hashCode();
        int hash = hashCode();
        if (hash > mHashCode) return 1;
        if (hash < mHashCode) return -1;
        return 0;
    }
    
    /**
     * ToString method to convert a move to a string representation.
     */
    public String toString() {
	char c;
        StringBuffer sb = new StringBuffer();
        
	if ((bits & 32) != 0) {
            switch (promote) {
                case Project2_Board_Thompson.KNIGHT:
                    c = 'n';
                    break;
                case Project2_Board_Thompson.BISHOP:
                    c = 'b';
                    break;
                case Project2_Board_Thompson.ROOK:
                    c = 'r';
                    break;
                default:
                    c = 'q';
                    break;
            }
            sb.append((char) (Project2_Board_Thompson.COL(from) + 'a'));
            sb.append(8 - Project2_Board_Thompson.ROW(from));
            sb.append((char) (Project2_Board_Thompson.COL(to) + 'a'));
            sb.append(8 - Project2_Board_Thompson.ROW(to));
            sb.append(c);
	}
	else {
            sb.append((char) (Project2_Board_Thompson.COL(from) + 'a'));
            sb.append(8 - Project2_Board_Thompson.ROW(from));
            sb.append((char) (Project2_Board_Thompson.COL(to) + 'a'));
            sb.append(8 - Project2_Board_Thompson.ROW(to));
        }
	return sb.toString();
    }
}