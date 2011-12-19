/**
 *Merge sort implementation
 * 
 * Based on provided code for JHU, CS 605.421, Spring 2011 Homework #1.
 * Takes a BoardPosition[] and breaks it down into sequentially smaller sorted arrays and then merges them as the recursion unrolls.
 * <BR>
 * <BR>Changes:
 * <BR>
 * <BR>1.  Takes New object type (was previously Cards)
 * <BR>2.  Instead of sorting on the Values, sorts according to the keys of each object.
 * <BR>
 * Change was necessary to use MergeSort implementation with permute_by_sorting, which sorts a list of values by their random keys.
 * Merge sort was selected due to the fact that because of our board sizes (most > 30), it will outpeform the other sorts we have studied in time.
 * 
 * Change Log:
 * 
 * 3/24/11 - Re-purposed for Project 1.
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
 */

public class Project2_MergeSort_Thompson {
	 /** Method for sorting BoardPosition objects using Merge sort.  
	   * Based on provided code for JHU, CS 605.421, Spring 2011.
	   * Break our array into n/2 sub arrays.
	   * Keep doing that until size is <=1, then merge as recursion unwinds.
	   * Eventually, we will merge the original sub arrays and have a completely sorted array.
	   *  Sort is O(nlg(n)) in the worst/average case.
	   *   @param list		Array of BoardPosition objects to be sorted.
	   *   @return	none.
	   * */

  public static void mergeSort(Project2_BoardPosition_Thompson[] list) {	
	  
	  /*
	   * 
	   * Divide our Card list and sort the two halves
	   * When the calls return, simply merge the two lists into a larger one until the recursion completely unwinds.
	   * 
	   */
    if (list.length > 1) { //Times: 1
      // Merge sort the first half
    	Project2_BoardPosition_Thompson[] firstHalf = new Project2_BoardPosition_Thompson[list.length / 2];  //Cost: c1, Times: 1
      System.arraycopy(list, 0, firstHalf, 0, list.length / 2); //Cost: c3, Times: 1
      mergeSort(firstHalf);  //Cost: T(n/2)

      // Merge sort the second half
      int secondHalfLength = list.length - list.length / 2; //Cost: c3, Times: n
      Project2_BoardPosition_Thompson[] secondHalf = new Project2_BoardPosition_Thompson[secondHalfLength];  //Cost: c4, Times: n
      System.arraycopy(list, list.length / 2,
        secondHalf, 0, secondHalfLength);
      mergeSort(secondHalf);  //Cost: T(n/2)

      // Merge firstHalf with secondHalf
      Project2_BoardPosition_Thompson[] temp = merge(firstHalf, secondHalf); //Cost: n
      System.arraycopy(temp, 0, list, 0, temp.length);
    }
  }

  /** Merge two sorted lists */
  private static Project2_BoardPosition_Thompson[] merge(Project2_BoardPosition_Thompson[] list1, Project2_BoardPosition_Thompson[] list2) {
	  /*
	   * 
	   * Total cost of merge is n since it will eventually examine every element in a n element array and just reassemble it sorted.
	   */
	  
	  
    
    
    Project2_BoardPosition_Thompson[] temp = new Project2_BoardPosition_Thompson[list1.length + list2.length]; //create new temp array, Cost: c1, Times: 1

    int current1 = 0; // Current index in list1 Cost: C2, Times: 1
    int current2 = 0; // Current index in list2 Cost: C3, Times: 1
    int current3 = 0; // Current index in temp Cost: C4, Times: 1
    
    //Copy the larger values of the two arrays, increment the respective indicies until one is empty and then just finish copying what is left after one is empy
    

    
    
    while (current1 < list1.length && current2 < list2.length) { 
    	/*
    	 * Change - Do not sort of values, sort on the object keys instead.
    	 * 
    	 */
      if (list1[current1].getKey() < list2[current2].getKey())
        temp[current3++] = list1[current1++];
      else
        temp[current3++] = list2[current2++];
    }
    
    //Deal with leftover values

    while (current1 < list1.length)
      temp[current3++] = list1[current1++];

    while (current2 < list2.length)
      temp[current3++] = list2[current2++];
    
    //Three while loops above will run a total of n times, to fill a n element array with sorted items.

    return temp;
  }
  
  /*Merge Sort Cost Analysis
   * 
   * A little more difficult than Insertion and Selection Sort
   * Basically, The recursive calls to MergeSort will create a recursion tree  
   * with n, n/2, n/4... child nodes  until there are finally n leaves. 
   * Across all levels of the tree, the cost will be c*n.
   * 
   * We can assume a fairly balanced tree so the height will be lg(n) +1.
   * 
   * Merging will cost essentially n, since it is just recreating the array after a constant number of comparisons.
   * 
   * What will be left with is a running time of n*(lg(n) +1) = n(lg(n)) + n
   * 
   * So we left with O(nlg(n))
   * 
   * T(n) = BigTheta(1)	if n <=c
   * 		aT(n/b) + D(n) + C(n)
   */

}
