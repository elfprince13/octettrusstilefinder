// Copyright 2012 Thomas Dickerson & Andrew Parent
// Distributed under the terms of the GNU Lesser General Public License
// (http://www.gnu.org/licenses/lgpl.html)package edu.smcvt.tilesymmetry;
import java.util.*;
import edu.smcvt.tilesymmetry.*;


/**
 * A CuboctahedronLUT object calculates the angles between arm pairs in a tile,
 * based on a precalculated lookup table. 
 * @author Thomas Dickerson
 */
public class CuboctahedronLUT{
	
	// This is our LUT. Not much more to it than that.
	private static final int[][] AngleLUT = {{ 0,90,180,90,60,60,120,120,60,60,120,120 },
											 { 90,0,90,180,120,120,60,60,60,60,120,120 },
											 { 180,90,0,90,120,120,60,60,120,120,60,60 },
											 { 90,180,90,0,60,60,120,120,120,120,60,60 },
											 { 60,120,120,60,0,90,180,90,60,120,120,60 },
											 { 60,120,120,60,90,0,90,180,120,60,60,120 },
											 { 120,60,60,120,180,90,0,90,120,60,60,120 },
											 { 120,60,60,120,90,180,90,0,60,120,120,60 },
											 { 60,60,120,120,60,120,120,60,0,90,180,90 },
											 { 60,60,120,120,120,60,60,120,90,0,90,180 },
											 { 120,120,60,60,120,60,60,120,180,90,0,90 },
											 { 120,120,60,60,60,120,120,60,90,180,90,0 }};
	
	// Instance variable. This stores the groupings we pick up.
	private Hashtable<AngleWrapper, LinkedHashSet<boolean[]>> grouping;

	/**
	 * The default constructor, it sets up everything that needs to be set up.
	 */
	public CuboctahedronLUT(){
		grouping = new Hashtable<AngleWrapper, LinkedHashSet<boolean[]>>();
	}

	/**
	 * Returns a {@link java.util.Hashtable} containing the sets of tiles with
	 * different arm-pair angles. {@link #computeAngles} should have been called
	 * a few times already so that the groupings actually contain something.
	 * @return A Hashtable indexed by {@link AngleWrapper} objects, and containing
	 * sets of tiles with the same angles between arm pairs.
	 */
	public Hashtable<AngleWrapper, LinkedHashSet<boolean[]>> getResultantGroupings(){ return grouping; }

	/**
	 * Computes and saves the angles between pairs of arms on a tile.
	 * @param bitString A bit string of length 12 representing an n-armed tile.
	 */
	public void computeAngles(boolean[] bitString){
		if(bitString.length != 0x0c){		// Sanity check.
			System.err.println("This bitString is not length 12");
			System.exit(0x01);
		}
		
		AngleWrapper aw;	// This wrapper overrides hashing and .equals() behavior
		int[] angles;		// Array of angles between pairs
		int i, j, k, numArms = 0x00;	// Counters. declared up here for some reusability
		short flags = 0x00;	// This will store an actual bit string built from bitString
		
		/* Recurring Hack: (var++ & 0x00 | const)
		 * This expression allows you to increment
		 * var, in a single line with another operation,
		 * and use the value of const instead of var.
		 */
		for(i = 0x00; i < 0x0c; i++)	// Initialize flags and numArms at the same time.
			flags |= (bitString[i]) ? (numArms++ & 0x00 | 0x01)<<i : 0x00;	
											
		
		/* This grabs us the (numArms - 1)th triangular number
		 * aka the number of distinct angular relationships between
		 * arm pairs. Remember that numArms is now  actually one less
		 * than it should be. This is ok, because we don't care about
		 * it anyways. We just want k.
		 */
		k = ((--numArms) * numArms + numArms) >> 0x01;
		angles = new int[k];

		/* Loop: until flags is 0, shift it right and increment i.
		 * If the LSB is set loop through the remaining bits (to the left)
		 * If the jth bit is set we have a pair. backfill the array from the LUT
		 */
		for(i = 0x00; flags != 0x0000; flags = (short)(flags >> (i++ & 0x00 | 0x01)))
			if((flags & 0x01) == 0x01)
				for(j = 0x01; j < 0x0c - i; j++)
					if ( (flags & (0x01 << j)) != 0x00)
						angles[--k] = AngleLUT[i][i+j];

		Arrays.sort(angles);			// Sort our array...
		aw = new AngleWrapper(angles);		// ... and wrap it.
		if(!grouping.containsKey(aw))		// If we haven't seen these angles before...
			grouping.put(aw, new LinkedHashSet<boolean[]>());	// ... now we have. 
		grouping.get(aw).add(bitString);	// Add bitString to that angle group.
	}

	/**
	 * This is a test driver to make sure things work correctly.
	 * It doesn't care about args.
	 */
	public static void main(String args[]){
		Hashtable<AngleWrapper, LinkedHashSet<boolean[]>> ans;
		int[] angles;
		int i;
		boolean[] test = {true, false, false, false, true, false,
						  true, false, false, false, true, false};
		boolean[] test2 = {false, false, false, true, false, true,
						  true, false, false, false, true, false};
		boolean[] test3 = {false, true, false, true, false, true,
						  true, false, false, false, true, false};
		CuboctahedronLUT chl = new CuboctahedronLUT();
		chl.computeAngles(test);	// Round 1: Nothing special
		chl.computeAngles(test);	// Round 2: Do we handle dupes okay?
		chl.computeAngles(test2);	// Round 3: And now for something completely different
		chl.computeAngles(test3);	// Round 4: ^- that again.
		ans = chl.getResultantGroupings();	// Grab our output.
		for(AngleWrapper wrapper : ans.keySet()){
			angles = wrapper.getContents();		// Unwrap our angles
			for(i = 0x00; i < angles.length;) System.out.print(angles[i++] + ", ");
			System.out.println("");
		}
	}
}