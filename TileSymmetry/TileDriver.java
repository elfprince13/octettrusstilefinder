// Copyright 2012 Thomas Dickerson & Andrew Parent
// Distributed under the terms of the GNU Lesser General Public License
// (http://www.gnu.org/licenses/lgpl.html)
import edu.smcvt.tilesymmetry.*;
import java.util.*;

public class TileDriver{

	private static final String[] UPPERS = {"\\  ", " | ", "  /",
											"\\| ", "\\ /", " |/", "\\|/"};

	private static final String[] MIDDLES = {" o ", "-o ", " o-", "-o-"};

	private static final String[] BOTTOMS = {"/  ", " | ", "  \\",
											 "/| ", "/ \\", " |\\", "/|\\"};

	private static final String FIVESPACE = "     ";

	private static void usage(){
		System.err.println("Usage: java Combo [-lm] numarms");
		System.exit(1);
	}

	public static void main(String args[]){
		System.out.println("/-------------------------------------\\");
		System.out.println("| --= TileSymmetry Driver Program =-- |");
		System.out.println("|    ----= -- written  by -- =----    |");
		System.out.println("| Thomas Dickerson  &&  Andrew Parent |");
		System.out.println("|-------------------------------------|");

		Random r = new Random();
		int au, bu, cu, du, eu;
		int am, bm, cm, dm, em;
		int ab, bb, cb, db, eb;

		au = r.nextInt(7);
		bu = r.nextInt(7);
		cu = r.nextInt(7);
		du = r.nextInt(7);
		eu = r.nextInt(7);
		am = r.nextInt(4);
		bm = r.nextInt(4);
		cm = r.nextInt(4);
		dm = r.nextInt(4);
		em = r.nextInt(4);
		ab = r.nextInt(7);
		bb = r.nextInt(7);
		cb = r.nextInt(7);
		db = r.nextInt(7);
		eb = r.nextInt(7);
	
		System.out.println("| " + UPPERS[au] + FIVESPACE + UPPERS[bu] + 
					  FIVESPACE + UPPERS[cu] + FIVESPACE + UPPERS[du] + 
					  FIVESPACE + UPPERS[eu] + " |");

		System.out.println("| " + MIDDLES[am] + FIVESPACE + MIDDLES[bm] +
					  FIVESPACE + MIDDLES[cm] + FIVESPACE + MIDDLES[dm] +
					  FIVESPACE + MIDDLES[em] + " |");

		System.out.println("| " + BOTTOMS[ab] + FIVESPACE + BOTTOMS[bb] +
					  FIVESPACE + BOTTOMS[cb] + FIVESPACE + BOTTOMS[db] +
					  FIVESPACE + BOTTOMS[eb] + " |");

		System.out.println("\\-------------------------------------/");
		if(args.length > 2 || args.length == 0){ usage(); }
		boolean lm = false;
		boolean orbstab = false;
		int x = -1;
		try{
			if(args.length == 1)
				x = Integer.parseInt(args[0]);
			else{
				if(args[0].equals("-lm")) lm = true;
				else if(args[0].equals("-os")) orbstab = true;
				else { usage(); }
				x = Integer.parseInt(args[1]);
			}
		} catch(NumberFormatException e){
			usage();
		}
		
		Combo list = new Combo(x);
		System.out.println("Tile types:");
		for(boolean[] bitString : list.getCombos()){
			Combo.printBitString(bitString);
		}
		System.out.println("There are " + list.getCombos().size() +
						   " unique tile types with " + x + " arms");

		if(orbstab){
			CuboctahedronSymmetry.enableOrbStab();
			CuboctahedronSymmetry.enablePrint();
			int orbTotal = 0;
			for(boolean[] bitString : list.getCombos()){
				System.out.println("");
				Combo.printBitString(bitString);
				CuboctahedronSymmetry.areSymmetric(bitString, bitString);
				System.out.println("Orbits:\t" + CuboctahedronSymmetry.getOrbCount());
				System.out.println("Stabilizers:\t" + CuboctahedronSymmetry.getStabCount());
				orbTotal += CuboctahedronSymmetry.getOrbCount();
				CuboctahedronSymmetry.resetOrbStab();
			}
			System.out.println("Total Orbits:\t" + orbTotal);
			CuboctahedronSymmetry.disablePrint();
			for(boolean[] bitString: list.getCombos()){
				CuboctahedronSymmetry.areSymmetric(bitString, bitString);
			}
			LinkedHashSet<BitStringWrapper> countingUp = CuboctahedronSymmetry.getOrb();
			CuboctahedronSymmetry.disableOrbStab();

			list.clearCombos();
			boolean[] bit = { false, false, false, false,
							  false, false, false, false,
							  false, false, false, false };

			list.list(0, x, bit);
			LinkedHashSet<BitStringWrapper> listed = new LinkedHashSet<BitStringWrapper>();
			for(boolean[] bitString : list.getCombos()){
				listed.add(new BitStringWrapper(bitString));
			}

			LinkedHashSet<BitStringWrapper> res = new LinkedHashSet<BitStringWrapper>();
			for(BitStringWrapper wrapped : listed){
				if(!countingUp.contains(wrapped)){
					res.add(wrapped);
				}
			}

			//res now contains symmetric difference of countingUp and listed
			System.out.println("Tiles NOT generated through rotation:");
			if(res.size() == 0) System.out.println("None");
			else{
				for(BitStringWrapper wrapped : res){
					Combo.printBitString(wrapped.getContents());
				}
			}

		}
		
		if(lm){
			System.out.println("");
			Scanner scan = new Scanner(System.in);
			String scanned;
			String[] arms;
			int i;
			boolean found;
			do{
				boolean[] bitString = {	false, false, false, false,
										false, false, false, false,
										false, false, false, false };

				System.out.print("(type quit to exit) ");
				scanned = scan.nextLine();
				arms = scanned.split(",*\\s+");
				if(arms.length >x) {
					System.err.println("Warning: Read more arms than " +
									   "were specified at runtime.");

					System.err.println(" -ignoring last " + (arms.length - x));
				} else if(arms.length < x){
					System.err.println("Error: Incomplete tile. " + x + " arms required");
					continue;
				}
				for(i = 0; i < x; i++){
					String armsLC = arms[i].toLowerCase();
					bitString[CuboctahedronSymmetry.a2I(armsLC)] = true;
				}

				found = false;
				for(boolean[] minString : list.getCombos()){
					if(CuboctahedronSymmetry.areSymmetric(minString, bitString)){
						found = true;
						Combo.printBitString(bitString, System.out, false);
						System.out.print("is equivalent to the lex-minimal tile ");
						Combo.printBitString(minString);
						break;
					}
				}
				if(!found){
					System.err.print("Could not find a lex-minimal arrangement for ");
					Combo.printBitString(bitString,  System.err, false);
					System.err.println("something is probably wrong");
				}
			} while(!scanned.equals("quit"));
		}	
		System.out.println("");
	}
}