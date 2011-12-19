

/**
 * Simple class to encapsulate the value and key for permute_by_sorting.
 * It could have been very easily done with two arrays of integers, but this seemed easier to deal with.
 * Change Log
 * 
 * 3/24/11 - Re-purposed based on code written for CS 605.421 Project #2.
 * 
 * @author Kurtis Thompson
 * @version 2011.0324
 * @since 1.6
 * 
 *
 *  
 */

public class Project2_BoardPosition_Thompson {
	/**
	 * Value of position (0 - Size^2-1).
	 */
	private int value = 0;
	
	/**
	 * Permutation key to sort on.
	 */
	private int key = 0;
	
	/**
	 * Default Constructor
	 */
	public Project2_BoardPosition_Thompson()
	{
	
	}
	/**
	 * Constructor taking a value and a key.
	 * 
	 * @param v - Value of position
	 * @param k - Key of position.
	 */
	public Project2_BoardPosition_Thompson(int v, int k)
	{
		this.value = v;
		this.key = k;
	}
	
	
	/**
	 * Get value for this object
	 * 
	 * @return value of object.
	 */
	public int getValue()
	{
		return value;
	}
	
	/**
	 * 
	 * Get key for this object
	 * @return key
	 */
	
	public int getKey()
	{
		return key;
	}
	
	/**
	 * Set the value for this object.
	 * @param v - integer value.
	 */
	
	public void setValue(int v)
	{
		this.value = v;
	}
	
	/**
	 * Set the key for this object
	 * @param k - integer key for this object between 0 and size^3-1
	 */
	public void setKey(int k)
	{
		this.key = k;
	}

}
