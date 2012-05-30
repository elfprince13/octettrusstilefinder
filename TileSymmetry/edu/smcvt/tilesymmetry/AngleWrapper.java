package edu.smcvt.tilesymmetry;

import java.util.*;
import edu.smcvt.tilesymmetry.*;

/**
 * AngleWrapper provides an immutable data class for using arrays of ints in situations
 * where they must be hashed based on array contents and not based on object
 * reference. It provides a constructor which allows the contents to be specified,
 * an accessor method to retrieve the contents, and overridden equals() and
 * hashCode() methods.
 * @author Thomas Dickerson
 * @see BitStringWrapper
 */

public class AngleWrapper{
	private int[] angles;

	/**
	 * @param a The array of ints to be wrapped.
	 */
	public AngleWrapper(int[] a){	angles = a;	}

	/**
	 * Overrides the default equals() with the implementation
	 * in java.util.Arrays.equals()
	 * @param other The object to be compared
	 * @return Whether or not the two objects are equal.
	 */
	public boolean equals(Object other){
		return Arrays.equals(angles, ((AngleWrapper)other).angles);
	}

	/**
	 * Overrides the default hashCode() with the implementation
	 * in java.util.Arrays.hashCode()
	 * @return The hashcode of the Array.
	 */
	public int hashCode(){	return Arrays.hashCode(angles);	}

	/**
	 * Retrieves the immutable boolean[] contents of this object.
	 * @return The array which has been wrapped.
	 */
	public int[] getContents(){	return angles;	}
}