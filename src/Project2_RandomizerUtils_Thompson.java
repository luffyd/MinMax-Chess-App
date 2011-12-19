import java.util.Random;

/**
 * Utility Class, providing methods to sort an array of integers.
 * Implementations of the below methods are based on pseudo-code provide in Introduction To Algorithms, 3rd Edition.
 * 
 * 
 * Requires HW2_MergeSort_Thompson.java be in the project as that is where mergesort is implementd.
 *
 *
 *Change Log
 *
 *3/24/11 - Re-purposed for CS 605.421 Project #1.
 *
 *
 * @author Kurtis Thompson
 * @version 2011.0324
 * @since 1.6
 * 
 * @modified Kurtis Thompson
 * 
 * 4/2/11 - Re-puposed for Project 2
 * @author Kurtis Thompson
 * @version 2011.0402
 * 
 * 
 *  
 */


public class Project2_RandomizerUtils_Thompson {
	
	/**
	 * Permute by Sorting<BR>
	 * <BR>
	 * For each value in the array A, this method will generate a key in the range of 0 to A.length^3-1 associated with each value in A<Br>
	 * The sorting algorithm used is MergeSort since for most values, it will outperform any of the known n^2 sorts we have studied.
	 * Based on pseudo-code in Introduction to Algorithms, 3rd Edition by Thomas Cormen, Charles Leiserson, Ronald Rivest and Clifford Stein.
	 * Uses a POJO, HW2_BoardPosition_Thompson to store the key and value as a pair.
	 * 
	 * @param A - Array of values we would like to assign a key too and then sort.
	 * 
	 * @author Kurtis Thompson
	 */
	public static void Permute_By_Sorting(int[] A)
	  {
			//We will need a random number generator
		  Random rand = new Random();
		  try{
			  
		  int n = A.length;
		  
		  //Create an array of BoardPositions, a POJO to hold the value and key.
		  Project2_BoardPosition_Thompson[] P = new Project2_BoardPosition_Thompson[n];
		  
		  //Compute n^3.
		  
		  int pow_val = (int)java.lang.Math.pow(n, 3);
		  for(int i = 0; i < n; i++)
		  {
			  //CLRS says 1 to n^3, starting at 0 but nextInt max is exclusive in Java.
			  P[i] = new Project2_BoardPosition_Thompson(A[i], rand.nextInt(pow_val));
		  }
		  
		  /*
		   * Generating the keys are done, now sort on them.
		   * This next part will account for the majority of the time spent in this method.
		   * MergeSort was chosen to make this as efficient as possible.
		   * 
		   */
		  
		  Project2_MergeSort_Thompson.mergeSort(P);
		  
		  /*
		   * We have sorted our positions, now copy them back to A.
		   */
		  for(int i = 0; i < A.length; i++)
		  {
			  A[i] = P[i].getValue();
		  }
		  
		  
		  }finally{
			  rand = null;
		  }
	  }
	
	/**
	 * Randomize in Place<BR>
	 * <BR>
	 * Based on pseudo-code in Introduction to Algorithms, 3rd Edition by Thomas Cormen, Charles Leiserson, Ronald Rivest and Clifford Stein.
	 * Takes an array of values and then randomly shuffles those values so they are all in a new order.
	 * @param A - Array of values we want to permute.
	 * 
	 * @author Kurtis Thompson
	 */
	  
	  public static void Randomize_In_Place(int[] A)
	  {
		  Random rand = new Random();
		  try{
		  int n = A.length;
		  int temp = 0;
		  int random_index = 0;
		  for(int i = 0; i < n; i++)
		  {
			  //nextInt is exclusive.
			  random_index = rand.nextInt(n);
			  
			  //Since have a random index, now swap the values
			  
			  
			  temp = A[i];
			  A[i] = A[random_index];
			  A[random_index] = temp;
		  }
		  }finally{
			  rand = null;
		  }
	  }

}
