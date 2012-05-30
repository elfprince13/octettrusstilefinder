// Copyright 2012 Thomas Dickerson & Andrew Parent
// Distributed under the terms of the GNU Lesser General Public License
// (http://www.gnu.org/licenses/lgpl.html)
package edu.smcvt.tilesymmetry;

import java.util.*;
import edu.smcvt.tilesymmetry.*;

/**
 * The CuboctahedronSymmetry class contains many static methods for determining the
 * symmetry of two tiles, rotating a tile according to the generator rotations, and
 * verifying results via orbits and stabilizers.
 * @author Thomas Dickerson
 * @author Andrew Parent
 */
public class CuboctahedronSymmetry{

	/**
	 * Value representing the alpha-1 arm.
	 */
	public static final int A1 = a2I("a1");

	/**
	 * Value representing the alpha-2 arm.
	 */
	public static final int A2 = a2I("a2");

	/**
	 * Value representing the alpha-3 arm.
	 */
	public static final int A3 = a2I("a3");

	/**
	 * Value representing the alpha-4 arm.
	 */
	public static final int A4 = a2I("a4");


	/**
	 * Value representing the beta-1 arm.
	 */
	public static final int B1 = a2I("b1");

	/**
	 * Value representing the beta-2 arm.
	 */
	public static final int B2 = a2I("b2");

	/**
	 * Value representing the beta-3 arm.
	 */
	public static final int B3 = a2I("b3");

	/**
	 * Value representing the beta-4 arm.
	 */
	public static final int B4 = a2I("b4");


	/**
	 * Value representing the gamma-1 arm.
	 */
	public static final int G1 = a2I("g1");

	/**
	 * Value representing the gamma-2 arm.
	 */
	public static final int G2 = a2I("g2");

	/**
	 * Value representing the gamma-3 arm.
	 */
	public static final int G3 = a2I("g3");

	/**
	 * Value representing the gamma-4 arm.
	 */
	public static final int G4 = a2I("g4");

	
	/**
	 * Value representing no rotations.
	 */
	public static final int	NONE		= 0x0000;
	
	/**
	 * Value representing a 120-degree generator rotation.
	 */
	public static final int FACE120		= 0x1000;
	
	/**
	 * Value representing two 120-degree generator rotations.
	 */
	public static final int FACE240		= 0x2000;
	
	/**
	 * Value representing a 90-degree generator rotation.
	 */
	public static final int PLANE90		= 0x0100;
	
	/**
	 * Value representing two 90-degree generator rotations.
	 */
	public static final int PLANE180	= 0x0200;
	
	/**
	 * Value representing three 90-degree generator rotations.
	 */
	public static final int PLANE270	= 0x0300;
	
	/**
	 * Value representing the special case rotation by the dihedral angle of the tetrahedron around alpha-1.
	 */
	public static final int ALPHA_TETRADIHEDRAL		= 0x0010;
	
	/**
	 * Value representing the special case rotation by the tetrahedral angle around alpha-1.
	 */
	public static final int ALPHA_TETRAHEDRAL	= 0x0020;
	
	/**
	 * Value representing the 180-degree generator rotation.
	 */
	public static final int ALPHA180	= 0x0040;
	
	/**
	 * Value representing the special case rotation dihedral angle of the tetrahedron around alpha-2.
	 */
	public static final int ALPHAT_TETRADIHEDRAL	= 0x0001;
	
	/**
	 * Value representing  the special case rotation by the tetrahedral angle around alpha-2.
	 */
	public static final int ALPHAT_TETRAHEDRAL	= 0x0002;

	/* This next chunk is related to our code for tracking the orbit
	 * and counting the stabilizers. Heavy duty statics like this are
	 * usually frowned on in OO languages, but it gets the job done.
	 */
	private static int orbcount = 0;
	private static int stabcount = 0;
	private static boolean osm = false;
	private static boolean useExtra = true;
	private static boolean disablePrint = false;

	private static LinkedHashSet<BitStringWrapper> orbit = null;

	/**
	 * Sets whether or not the special case rotations should be considered.
	 * @deprecated
	 * @see #getUseExtra
	 * @param b Whether or not the special case rotations should be considered.
	 */
	public static void setUseExtra(boolean b){ useExtra = b; }
	
	/**
	 * Tests whether or not the special case rotations should be considered.
	 * @deprecated
	 * @see #setUseExtra
	 */
	public static boolean getUseExtra(){ return useExtra; }

	/**
	 * Enables tracking of the orbit and stabilizers.
	 * Also has the same effects as {@link #resetOrbStab}.
	 * @see #disableOrbStab
	 * @see #resetOrbStab
	 */
	public static void enableOrbStab(){
		osm = true;
		orbcount = 0;
		stabcount = 0;
		orbit = new LinkedHashSet<BitStringWrapper>();

	}

	/**
	 * Disables tracking of the orbit and stabilizers.
	 * Also has the same effects as {@link #resetOrbStab}.
	 * @see #enableOrbStab
	 * @see #resetOrbStab
	 */
	public static void disableOrbStab(){
		osm = false;
		orbcount = 0;
		stabcount = 0;
		orbit = null;
	}

	/**
	 * Resets tracking of the orbit and stabilizers.
	 * @see #enableOrbStab
	 * @see #disableOrbStab
	 */
	public static void resetOrbStab(){
		orbcount = 0;
		stabcount = 0;
		orbit = new LinkedHashSet<BitStringWrapper>();
	}

	/**
	 * Returns a wrapped set of all unique tiles that have been seen
	 * since orbit/stabilizer tracking was last reset.
	 * @see #enableOrbStab
	 * @see #disableOrbStab
	 * @see #resetOrbStab
	 * @see #getOrbCount
	 * @return A LinkedHashSet of BitStringWrappers.
	 */
	public static LinkedHashSet<BitStringWrapper> getOrb(){	return orbit;	}

	/**
	 * @see #getOrb
	 * @see #getStabCount
	 * @return The size of the set returned by {@link #getOrb}
	 */
	public static int getOrbCount(){	return orbcount;	}

	/**
	 * @see #getStabCount
	 * @return The number of rotations seen which preserve a tile's orientation.
	 */
	public static int getStabCount(){	return stabcount;	}

	/**
	 * Disables the printing of rotation-tracking information to
	 * the console on tile comparison.
	 * @see #enablePrint
	 */
	public static void disablePrint(){ disablePrint = true; }

	/**
	 * Enables the printing of rotation-tracking information to
	 * the console on tile comparison.
	 * @see #disablePrint
	 */
	public static void enablePrint(){ disablePrint = false; }

	/**
	 * Prints a set of masked together rotation flags.
	 * @param rotTrack The rotations to be printed.
	 */
	public static void printRot(int rotTrack){
		switch(rotTrack & 0xF000){
			case FACE240:
				System.out.print("Face 120,\t");
			case FACE120:
				System.out.print("Face 120,\t");
			default:
				break;
		}

		switch(rotTrack & 0x0F00){
			case PLANE270:
				System.out.print("Plane 90,\t");
			case PLANE180:
				System.out.print("Plane 90,\t");
			case PLANE90:
				System.out.print("Plane 90,\t");
			default:
				break;
		}
		
		switch(rotTrack & 0x00F0){
			case ALPHA_TETRADIHEDRAL:
				System.out.print("Alpha 71,\t");
				break;
			case ALPHA_TETRAHEDRAL:
				System.out.print("Alpha 109,\t");
				break;
			case ALPHA180:
				System.out.print("Alpha 180,\t");
				break;
			case ALPHA180 | ALPHA_TETRADIHEDRAL:
				System.out.print("Alpha 180,\t");
				System.out.print("Alpha 71,\t");
				break;
			case ALPHA180 | ALPHA_TETRAHEDRAL:
				System.out.print("Alpha 180,\t");
				System.out.print("Alpha 109,\t");
			default:
				break;
		}

		switch(rotTrack & 0x000F){
			case ALPHAT_TETRADIHEDRAL:
				System.out.print("Alpha2 71,\t");
				break;
			case ALPHAT_TETRAHEDRAL:
				System.out.print("Alpha2 109,\t");
			default:
				break;
		}

		System.out.println("");

	}


	/**
	 * Determines whether two tiles are lexicographically equal.
	 * If enablePrint has been set, it will
	 * print the current rotation state to the console.
	 * @see #enablePrint
	 * @see #disablePrint
	 * @param b1 The first tile.
	 * @param b2 The second tile.
	 * @param rotTrack A collection of bit flags used for tracking the rotations.
	 * @return Whether or not the two tiles are lexicographically equal.
	 */
	public static boolean match(boolean[] b1, boolean[] b2, int rotTrack){
		boolean ret = (b1[0] == b2[0]) && (b1[1] == b2[1]) && 
					  (b1[2] == b2[2]) && (b1[3] == b2[3]) && 
					  (b1[4] == b2[4]) && (b1[5] == b2[5]) && 
					  (b1[6] == b2[6]) && (b1[7] == b2[7]) && 
					  (b1[8] == b2[8]) && (b1[9] == b2[9]) && 
					  (b1[10] == b2[10]) && (b1[11] == b2[11]);
		
		int i = (ret ? 1 : 0) + (osm ? 2 : 0) + (!disablePrint ? 4 : 0);
		switch(i){
			case 7:
				System.out.print("Match -\t");
				printRot(rotTrack);
			case 3:
				stabcount += 1;
				orbit.add(new BitStringWrapper(b2));
				orbcount = orbit.size();
				break;
			case 6:
				Combo.printBitString(b2, System.out, false);
				System.out.print("-\t");
				printRot(rotTrack);
			case 2:
				orbit.add(new BitStringWrapper(b2));
				orbcount = orbit.size();
			default:
				break;
		}
				

		return ret;
	}

	/**
	 * Tests if two tiles are rotationally symmetric.
	 * This function performs two iterations of the 120 degree rotation
	 * around the center of the face formed by alpha-1, beta-1, and
	 * gamma-1. It saves them and the performs the alpha plane symmetry
	 * tests on each of the newly generate tiles, plus the original.
	 * The 3 rotations here times the 8, 16, or 24 in the alpha plane tests
	 * account for the 24, 48, or 72 rotations.
	 * @param b1 The tile to be held static for comparison.
	 * @param b2 The tile to be rotated for comparison.
	 * @return A truth value representing whether the two tiles are rotationally symmetric.
	 */
	public static boolean areSymmetric(boolean[] b1, boolean[] b2){
		if(b1.length != 12 || b2.length != 12){
			System.err.println("This bitString is not length 12");
			System.exit(0x01);
		}
		
		boolean[] tmp = rotateBetaToAlpha(b2);
		boolean[] tmp2 = rotateBetaToAlpha(tmp);

		return	symmetricAroundAlphaPlane(b1, b2, NONE) |
				symmetricAroundAlphaPlane(b1, tmp, FACE120) |
				symmetricAroundAlphaPlane(b1, tmp2, FACE240);
	}

	/**
	 * This function checks whether two tiles are symmetric based only on rotations
	 * that can be generated by rotations which maintain alpha arms in the alpha plane,
	 * followed be the special case rotations. Generates either 8, 16, or 24 rotations
	 * @param b1 The tile to be held static for comparison.
	 * @param b2 The tile to be rotated for comparison.
	 * @param rotTrack A collection of bit flags used for tracking the rotations.
	 * @return A truth value representing whether the two tiles are rotationally symmetric.
	 */
	public static boolean symmetricAroundAlphaPlane(boolean[] b1,
													boolean[] b2,
													int rotTrack){
		
		/* Hold b1 static, transform temps of b2 */
		boolean[] tmp1, tmp2, tmp3, tmp4;
		boolean[] rot1, rot2, rot3, rot4;
		tmp1 = b2.clone();	//+0 degrees
		tmp2 = rotateAlphaPlane(tmp1);	//+90 degrees
		tmp3 = rotateAlphaPlane(tmp2); 	//+180 degrees
		tmp4 = rotateAlphaPlane(tmp3);	//+270 degrees

		rot1 = rotateAroundAlpha1(tmp1);
		rot2 = rotateAroundAlpha1(tmp2);
		rot3 = rotateAroundAlpha1(tmp3);
		rot4 = rotateAroundAlpha1(tmp4);

		int i, j, k, l;
		i = rotTrack | NONE;
		j = rotTrack | PLANE90;
		k = rotTrack | PLANE180;
		l = rotTrack | PLANE270;

		return 	match(b1, tmp1, i) | match(b1, rot1, i | ALPHA180) |
				match(b1, tmp2, j) | match(b1, rot2, j | ALPHA180) |
				match(b1, tmp3, k) | match(b1, rot3, k | ALPHA180) |
				match(b1, tmp4, l) | match(b1, rot4, l | ALPHA180) |
				symmetricPlusTetraDihedralAroundAlpha1(b1, tmp1, i) |
				symmetricPlusTetraDihedralAroundAlpha1(b1, rot1, i | ALPHA180) |
				symmetricPlusTetraDihedralAroundAlpha1(b1, tmp2, j) |
				symmetricPlusTetraDihedralAroundAlpha1(b1, rot2, j | ALPHA180) |
				symmetricPlusTetraDihedralAroundAlpha1(b1, tmp3, k) |
				symmetricPlusTetraDihedralAroundAlpha1(b1, rot3, k | ALPHA180) |
				symmetricPlusTetraDihedralAroundAlpha1(b1, tmp4, l) |
				symmetricPlusTetraDihedralAroundAlpha1(b1, rot4, l | ALPHA180) |
				symmetricPlusTetrahedralAroundAlpha1(b1, tmp1, i) |
				symmetricPlusTetrahedralAroundAlpha1(b1, rot1, i | ALPHA180) |
				symmetricPlusTetrahedralAroundAlpha1(b1, tmp2, j) |
				symmetricPlusTetrahedralAroundAlpha1(b1, rot2, j | ALPHA180) |
				symmetricPlusTetrahedralAroundAlpha1(b1, tmp3, k) |
				symmetricPlusTetrahedralAroundAlpha1(b1, rot3, k | ALPHA180) |
				symmetricPlusTetrahedralAroundAlpha1(b1, tmp4, l) |
				symmetricPlusTetrahedralAroundAlpha1(b1, rot4, l | ALPHA180) |
				symmetricPlusTetraDihedralAroundAlpha2(b1, tmp1, i) |
				symmetricPlusTetraDihedralAroundAlpha2(b1, rot1, i | ALPHA180) |
				symmetricPlusTetraDihedralAroundAlpha2(b1, tmp2, j) |
				symmetricPlusTetraDihedralAroundAlpha2(b1, rot2, j | ALPHA180) |
				symmetricPlusTetraDihedralAroundAlpha2(b1, tmp3, k) |
				symmetricPlusTetraDihedralAroundAlpha2(b1, rot3, k | ALPHA180) |
				symmetricPlusTetraDihedralAroundAlpha2(b1, tmp4, l) |
				symmetricPlusTetraDihedralAroundAlpha2(b1, rot4, l | ALPHA180) |
				symmetricPlusTetrahedralAroundAlpha2(b1, tmp1, i) |
				symmetricPlusTetrahedralAroundAlpha2(b1, rot1, i | ALPHA180) |
				symmetricPlusTetrahedralAroundAlpha2(b1, tmp2, j) |
				symmetricPlusTetrahedralAroundAlpha2(b1, rot2, j | ALPHA180) |
				symmetricPlusTetrahedralAroundAlpha2(b1, tmp3, k) |
				symmetricPlusTetrahedralAroundAlpha2(b1, rot3, k | ALPHA180) |
				symmetricPlusTetrahedralAroundAlpha2(b1, tmp4, l) |
				symmetricPlusTetrahedralAroundAlpha2(b1, rot4, l | ALPHA180);
	}

	/**
	 * This is a "special case" rotation for tiles lying entirely in a hexagonal plane,
	 * whose rotational symmetry group may be of higher order. If a tile is otherwise
	 * in lex-minimal order but doesn't have arms in either of alpha-2 or alpha-4,
	 * it's possible that while it would normally be an inverse of our current tile w.r.t
	 * the the cuboctahedron, it's an inverse that's actually still identical and can
	 * be reached with rotations only - just rotations that aren't generators for
	 * the symmetry group of the cuboctahedron.
	 * @see #symmetricPlusTetrahedralAroundAlpha1
	 * @see #symmetricPlusTetraDihedralAroundAlpha2
	 * @see #symmetricPlusTetrahedralAroundAlpha2
	 * @param b1 The tile to be held static for comparison.
	 * @param b2 The tile to be rotated for comparison.
	 * @param rotTrack A collection of bit flags used for tracking the rotations.
	 * @return A truth value representing whether the two tiles are rotationally symmetric.
	 */
	public static boolean symmetricPlusTetraDihedralAroundAlpha1(boolean[] b1,
													  boolean[] b2,
													  int rotTrack){

		// We would lose 6 arms between axises with this operation,
		if(!useExtra || b2[A2] || b2[A4] || b2[B1] || b2[B3] || b2[G2] || b2[G4]) 
			return false;  // ... so don't do it
		
		boolean tmp[] = new boolean[12];
			
		tmp[A1] = b2[A1];
		tmp[A3] = b2[A3];
		tmp[A2] = false;
		tmp[A4] = false;

		tmp[B1] = b2[G1];
		tmp[B2] = false;
		tmp[B3] = b2[G3];
		tmp[B4] = false;
		
		tmp[G1] = false;
		tmp[G2] = b2[B2];
		tmp[G3] = false;
		tmp[G4] = b2[B4];

		return match(b1, tmp, rotTrack | ALPHA_TETRADIHEDRAL);
	}

	/**
	 * An additional special case.
	 * @see #symmetricPlusTetraDihedralAroundAlpha1
	 * @see #symmetricPlusTetraDihedralAroundAlpha2
	 * @see #symmetricPlusTetrahedralAroundAlpha2
	 * @param b1 The tile to be held static for comparison.
	 * @param b2 The tile to be rotated for comparison.
	 * @param rotTrack A collection of bit flags used for tracking the rotations.
	 * @return A truth value representing whether the two tiles are rotationally symmetric.
	 */
	public static boolean symmetricPlusTetrahedralAroundAlpha1(boolean[] b1,
													   boolean[] b2,
													   int rotTrack){

		// We would lose 6 arms between axises with this operation,
		if(!useExtra || b2[A2] || b2[A4] || b2[B2] || b2[B4] || b2[G1] || b2[G3])
			return false; // ... so don't do it
		
		boolean tmp[] = new boolean[12];
			
		tmp[A1] = b2[A1];
		tmp[A3] = b2[A3];
		tmp[A2] = false;
		tmp[A4] = false;

		tmp[B1] = false;
		tmp[B2] = b2[B1];
		tmp[B3] = false;
		tmp[B4] = b2[B3];
		
		tmp[G1] = b2[G2];
		tmp[G2] = false;
		tmp[G3] = b2[G4];
		tmp[G4] = false;

		return match(b1, tmp, rotTrack | ALPHA_TETRAHEDRAL);
	}

	/**
	 * An additional special case.
	 * @see #symmetricPlusTetraDihedralAroundAlpha1
	 * @see #symmetricPlusTetrahedralAroundAlpha1
	 * @see #symmetricPlusTetrahedralAroundAlpha2
	 * @param b1 The tile to be held static for comparison.
	 * @param b2 The tile to be rotated for comparison.
	 * @param rotTrack A collection of bit flags used for tracking the rotations.
	 * @return A truth value representing whether the two tiles are rotationally symmetric.
	 */
	public static boolean symmetricPlusTetraDihedralAroundAlpha2(boolean[] b1,
													  boolean[] b2,
													  int rotTrack){

		// We would lose 6 arms between axises with this operation,
		if(!useExtra || b2[A1] || b2[A3] || b2[B1] || b2[B3] || b2[G1] || b2[G3])
			return false; // ... so don't do it
		
		boolean tmp[] = new boolean[12];
			
		tmp[A1] = false;
		tmp[A3] = false;
		tmp[A2] = b2[A2];
		tmp[A4] = b2[A4];

		tmp[B1] = b2[G4];
		tmp[B2] = false;
		tmp[B3] = b2[G2];
		tmp[B4] = false;
		
		tmp[G1] = b2[B4];
		tmp[G2] = false;
		tmp[G3] = b2[B2];
		tmp[G4] = false;

		return match(b1, tmp, rotTrack | ALPHAT_TETRADIHEDRAL);
	}

	/**
	 * An additional special case.
	 * @see #symmetricPlusTetraDihedralAroundAlpha1
	 * @see #symmetricPlusTetrahedralAroundAlpha1
	 * @see #symmetricPlusTetraDihedralAroundAlpha2
	 * @param b1 The tile to be held static for comparison.
	 * @param b2 The tile to be rotated for comparison.
	 * @param rotTrack A collection of bit flags used for tracking the rotations.
	 * @return A truth value representing whether the two tiles are rotationally symmetric.
	 */
	public static boolean symmetricPlusTetrahedralAroundAlpha2(boolean[] b1,
													   boolean[] b2,
													   int rotTrack){

		// We would lose 6 arms between axises with this operation,
		if(!useExtra || b2[A1] || b2[A3] || b2[B2] || b2[B4] || b2[G2] || b2[G4])
			return false; // ... so don't do it
		
		boolean tmp[] = new boolean[12];
			
		tmp[A1] = false;
		tmp[A3] = false;
		tmp[A2] = b2[A2];
		tmp[A4] = b2[A4];

		tmp[B1] = false;
		tmp[B2] = b2[B1];
		tmp[B3] = false;
		tmp[B4] = b2[B3];
		
		tmp[G1] = false;
		tmp[G2] = b2[G1];
		tmp[G3] = false;
		tmp[G4] = b2[G3];

		return match(b1, tmp, rotTrack | ALPHAT_TETRAHEDRAL);
	}

	/**
	 * A rotation of 180 degrees around the alpha-1/alpha-3 axis.
	 * @see #rotateAlphaPlane
	 * @see #rotateBetaToAlpha
	 * @param b The tile to be rotated.
	 * @return A copy of b, rotated by the 180-degree generator rotation.
	 */
	public static boolean[] rotateAroundAlpha1(boolean[] b){
		boolean tmp[] = new boolean[12];
		
		/* Mirror alpha plane */
		tmp[A1] = b[A1];
		tmp[A2] = b[A4];
		tmp[A3] = b[A3];
		tmp[A4] = b[A2];

		tmp[G1] = b[B2];
		tmp[B4] = b[G3];
		tmp[G4] = b[B3];
		tmp[B1] = b[G2];

		tmp[B2] = b[G1];
		tmp[G2] = b[B1];
		tmp[B3] = b[G4];
		tmp[G3] = b[B4];
		return tmp;
		
	}

	/** 
	 * A rotation of 90 degrees around the center of the face formed by
	 * beta-2, beta-3, gamma-2, and gamma-3 (i.e. the square on top)
	 * @see #rotateAroundAlpha1
	 * @see #rotateBetaToAlpha
	 * @param b The tile to be rotated.
	 * @return A copy of b, rotated by the 90-degree generator rotation.
	 */
	public static boolean[] rotateAlphaPlane(boolean[] b){
		boolean tmp[] = new boolean[12];
		
		tmp[A1] = b[A2];
		tmp[A2] = b[A3];
		tmp[A3] = b[A4];
		tmp[A4] = b[A1];

		tmp[B3] = b[G3];
		tmp[G3] = b[B2];
		tmp[B2] = b[G2];
		tmp[G2] = b[B3];

		tmp[B4] = b[G4];
		tmp[G1] = b[B4];
		tmp[B1] = b[G1];
		tmp[G4] = b[B1];
		return tmp;
	}

	/**
	 * A rotation of 120 degrees around the center of
	 * the face formed by alpha-1, beta-1, and gamma-1
	 * @see #rotateAroundAlpha1
	 * @see #rotateAlphaPlane
	 * @param b The tile to be rotated.
	 * @return A copy of b, rotated by the 120-degree generator rotation.
	 */
	public static boolean[] rotateBetaToAlpha(boolean[] b){
		boolean tmp[] = new boolean[12];
	
		tmp[A1] = b[B1];
		tmp[A2] = b[B2];
		tmp[A3] = b[B3];
		tmp[A4] = b[B4];

		tmp[B1] = b[G1];
		tmp[B2] = b[G4];
		tmp[B3] = b[G3];
		tmp[B4] = b[G2];

		tmp[G1] = b[A1];
		tmp[G2] = b[A4];
		tmp[G3] = b[A3];
		tmp[G4] = b[A2];
		return tmp;
	}

	/**
	 * A convenience function to retrieve the arm number represented by
	 * a String of the form [abg][1234].
	 * @param s A String of the form [abg][1234].
	 * @return The arm number represented by that string.
	 */
	public static int a2I(String s){
		// There should be no reason to change
		// this function as long as the arm naming schema
		// remains the same.
		char a = s.charAt(0);
		char n = s.charAt(1);
		int i = 0;
		switch(a){		// We switch these backwards to chain conditions
			case 'g':	// and avoid the use of break.
				i++;
			case 'b':
				i++;
			case 'a':
				i *= 4;		// Scale the current index (0-2) by 4 
				switch(n){	// so it points at A1, B1, or G1 ...
					case '4':	// ... and repeat our reverse
						i++;	// switching order trick.
					case '3':
						i++;
					case '2':
						i++;
					case '1':
						return i;
				}
		}

		System.err.println("Invalid input " + s);
		System.exit(0x01);
		return -1; //This makes the compiler happy even though we'll never ever get here.
	}
		

}