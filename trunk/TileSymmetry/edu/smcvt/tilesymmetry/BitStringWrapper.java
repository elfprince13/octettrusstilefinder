// Copyright 2012 Thomas Dickerson & Andrew Parent
// Distributed under the terms of the GNU Lesser General Public License
// (http://www.gnu.org/licenses/lgpl.html)
package edu.smcvt.tilesymmetry;

import java.util.*;
import edu.smcvt.tilesymmetry.*;

/**
 * BitStringWrapper provides an immutable data class for using arrays of booleans in
 * situations where they must be hashed based on array contents and not based on 
 * object reference. It provides a constructor which allows the contents to be
 * specified, an accessor method to retrieve the contents, and overridden equals()
 * and hashCode() methods.
 * @author Thomas Dickerson
 * @see AngleWrapper
 */

public class BitStringWrapper{
	private boolean[] bitString;

	/**
	 * @param b The array of booleans to be wrapped.
	 */
	public BitStringWrapper(boolean[] b){	bitString = b;	}

	/**
	 * Overrides the default equals() with the implementation
	 * in java.util.Arrays.equals()
	 * @param other The object to be compared
	 * @return Whether or not the two objects are equal.
	 */
	public boolean equals(Object other){
		return Arrays.equals(bitString, ((BitStringWrapper)other).bitString);
	}

	/**
	 * Overrides the default hashCode() with the implementation
	 * in java.util.Arrays.hashCode()
	 * @return The hashcode of the Array.
	 */
	public int hashCode(){	return Arrays.hashCode(bitString);	}

	/**
	 * Retrieves the immutable boolean[] contents of this object.
	 * @return The array which has been wrapped.
	 */
	public boolean[] getContents(){	return bitString;	}
}