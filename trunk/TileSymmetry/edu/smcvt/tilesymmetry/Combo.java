package edu.smcvt.tilesymmetry;

import java.util.*;
import edu.smcvt.tilesymmetry.*;

/**
 * A Combo object calculates a comprehensive list of n-armed tiles by calculating the
 * set of bit strings of length 12 with a Hamming distance of n from the length 12
 * bit string of all zeroes. It then reduces them to the set of lex-minimal
 * n-armed tiles.
 * @author Andrew Parent
 * @author Thomas Dickerson
 */
public class Combo{
	private static final int LENGTH = 12; // The number of arms in a tile.

	private int k;
	private LinkedList<boolean[]> combos;
	private CuboctahedronLUT groupF;
	private static String[] names = { "A1", "A2", "A3", "A4",
									  "B1", "B2", "B3", "B4",
									  "G1", "G2", "G3", "G4" };


	/**
	 * The default constructor, instantiates a Combo object with n = 1
	 */
	public Combo (){	this(1);	}

	/**
	 * Instantiates a new Combo object, which will immediately contain
	 * the set of lex-minimal n-armed tiles.
	 * @param n The number of arms per tile.
	 */
	public Combo (int n){
		k = n;
		clearCombos();
		boolean[] bit = { false, false, false, false,
						  false, false, false, false,
						  false, false, false, false };
		list(0, k, bit);
		CuboctahedronSymmetry.disableOrbStab();
		removeCopies();
	}

	/**
	 * Recursively produces and saves the n-armed tiles.
	 * @param start Starting position in the bitString.
	 * @param n The number of arms which must be set.
	 * @param bitString The initial bit string representing a tile. 
	 */	
	public void list(int start, int n, boolean bitString[]){
		boolean[] newBit = bitString.clone();
		if(n == 0) combos.addLast(bitString);
		else if(start != LENGTH){
			list(start+1, n, newBit);
			newBit[start] = !newBit[start];
			list(start + 1, n - 1, newBit);	
		}		
	}
	
	/**
	 * Groups the tiles based on the angles between pairs of arms.
	 */
	public void calcGroupings(){
		CuboctahedronLUT grouper = new CuboctahedronLUT();
		for(boolean[] bitString : combos){
			grouper.computeAngles(bitString);	
		}
		groupF = grouper;
	}
	
	/**
	 * Reduces the set of saved tiles to contain only the lex-minimal n-armed tiles.
	 */
	public void removeCopies(){
		calcGroupings();
		Hashtable<AngleWrapper, LinkedHashSet<boolean[]>> groupings =
													groupF.getResultantGroupings();

		LinkedList<boolean[]> shrunk = new LinkedList<boolean[]>();
		
		for(LinkedHashSet<boolean[]> g : groupings.values()){
			/* Hack to prevent typecasting,
			 * because typecasting is sloppy */
			boolean[][] grouping = g.toArray(new boolean[1][1]);
			int gMax = grouping.length - 1;
			for(int i = gMax; i >= 0 && grouping[i][0]; i-- ){
				if(i == gMax)	shrunk.addLast(grouping[i]);
				else{
					boolean gotit = false;
					for(boolean[] bitString : shrunk){
						if(CuboctahedronSymmetry.areSymmetric(bitString, grouping[i])){
							gotit = true;
							break;
						}
					}
					if(!gotit)	shrunk.addLast(grouping[i]);
				}
				
			}
		}	
		combos = shrunk;
	}

	/**
	 * Convenience function which prints a tile, represented by bitString,
	 * to std out, followed by a new line.
	 * @param bitString The bit string representing the tile to be printed.
	 */
	public static void printBitString(boolean[] bitString){
		printBitString(bitString, System.out, true);
	}

	/**
	 * Convenience function which prints a tile, represented by bitString,
	 * to the specified {@link java.io.PrintStream}, optionally followed by a new line.
	 * @param bitString The bit string representing the tile to be printed.
	 * @param stream The PrintStream for the tile to be printed to.
	 * @param endLine Specifies whether the tile should be followed by a new line.
	 */
	public static void printBitString(boolean[] bitString,
									  java.io.PrintStream stream,
									  boolean endLine) {
		for(int j = 0; j < bitString.length; j++){
			if(bitString[j]){
				stream.print(names[j] + ", ");
			}
		}
		if(endLine){
			stream.println("");
		}
	}

	/**
	 * Returns the set of tiles saved by this Combo object.
	 * @return The set of tiles saved by this Combo object.
	 */
	public LinkedList<boolean[]> getCombos(){ return combos;	}

	/**
	 * Clears the set of tiles saved by this Combo object.
	 */
	public void clearCombos(){ combos = new LinkedList<boolean[]>(); }

	
}



