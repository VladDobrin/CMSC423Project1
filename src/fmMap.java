import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import alignment.SemiGlobalAlignmentTable;
//import fm_index.FMIndex;


public class fmMap {

	// ALL OF INDEX AND ITS METHODS
	public static FMIndexWAllData index(String FASTA, String output) { // DONE STORES ALL INFORMATION DESIRED!
		int aCount = 0;
		int gCount = 0;
		int tCount= 0;
		int cCount = 0;

		//PROCESS FIRST LINE OF FASTA FILE
		String name = processFirstLine(FASTA);
		String FASTARead = processRestOfFile(FASTA);
		int toGetSize = FASTARead.length() - 1 ; // we now have the size of the file -1 is for the $ character

		//BWT STUFF
		String bwtBack = BurrowsWheelerTransform.main(FASTARead); // will return a string concatenated with firstcol and last col
		String firstCol = bwtBack.substring(0, bwtBack.length()/2); // has the first column that is return
		String lastCol = bwtBack.substring((bwtBack.length()/2) + 1, bwtBack.length()); // has the last col of BWT 

		char[] firstColLetters = new char[firstCol.length()];
		for(int i = 0; i < firstCol.length(); i++) {
			firstColLetters[i] = firstCol.charAt(i);
			if(lastCol.charAt(i) == 'A') { // increment that position by 1 all other stay the same
				aCount++;
			}else if(lastCol.charAt(i) == 'C') {
				cCount++;
			}
			else if(lastCol.charAt(i) == 'G') {
				gCount++;
			}
			else if(lastCol.charAt(i) == 'T') {
				tCount++;
			}
		}
		String storeFirstCol = "A " + aCount + " C " + cCount + " G " + gCount + " T " + tCount;

		char[] lastColLetters = new char[lastCol.length()];
		for(int i = 0; i < lastCol.length(); i++) {
			lastColLetters[i] = lastCol.charAt(i);
		}

		//OCCURANCE TABLE STUFF
		int[][] occTable = new int[lastCol.length()][4];
		occTable = occTableCreated(lastCol); // creates the occuranceTable

		// SUFFIX ARRAY STUFF
		NewSuffixArray SA = new NewSuffixArray(FASTARead);

		//OUTPUT STUFF TO FILE LOCATION
		PrintStream out; try { 
			out = new PrintStream(new FileOutputStream(output));
			System.setOut(out);

		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		}


		// NICELY PRINTING EVERYTHING INSIDE THE FILE!
		System.out.println("Name: " + name);
		System.out.println("Reference Genome: " + FASTARead);
		System.out.println("SIZE: " + toGetSize);
		System.out.println("First Column of FM index: " + storeFirstCol);
		System.out.println("Last Column of FM index: " + lastCol);
		System.out.println("Occurance Table: ");

		for(int i = 0; i < occTable.length; i ++) {
			for(int x = 0; x < occTable[i].length; x++) {
				if(x != occTable[i].length-1) {
					System.out.print(occTable[i][x] + ", "); // where a specific row ends is denoted by 4 commas!
				}else {
					System.out.print(occTable[i][x]);
				}
			}
			System.out.print("\n"); 
		}
		System.out.println("Suffix Array:");

		for(int i = 0; i< SA.s.length(); i++) {
			System.out.print(SA.suffixes[i].index + " ");
		}


		FMIndexWAllData fmIndex = new FMIndexWAllData(name, FASTARead, FASTA, toGetSize, storeFirstCol, lastColLetters, occTable, SA);
		return fmIndex; // made a class with all the same things that are in the FM index file to return upon call to make it easier
		// to access all information, could be extracted by parsing the FM index output file but that would take to much extra processing
	}

	private static String processRestOfFile(String fASTA) { // to get the entire file
		String input = "";
		boolean firstLine = true; 
		try (BufferedReader br = new BufferedReader(new FileReader(fASTA))) {
			String line;

			while ((line = br.readLine()) != null) {
				if(firstLine) {
					firstLine = false; 
				}else {
					input += line; 
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input.replaceAll("\n", "").replaceAll("\r", "") + "$";
	}

	private static String processFirstLine(String fASTA) { // to get the name
		String input = "";
		try (BufferedReader br = new BufferedReader(new FileReader(fASTA))) {
			String line;
			while ((line = br.readLine()) != null && line.charAt(0) == '>') {
				input = line; 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input.substring(1, input.length());
	}

	public static int[][] occTableCreated(String lastCol){ // creates the occuranceTable
		int[][] occTable = new int[lastCol.length()][4]; // columns will be A T G C

		int aCount = 0;
		int gCount = 0;
		int tCount= 0;
		int cCount = 0;


		for(int i = 0; i < lastCol.length(); i++) { // create occurance table
			if(i == 0 ) { // need to deal with first case not the best way but it works for this project
				if(lastCol.charAt(i) == 'A') { // increment that position by 1 all other stay the same
					occTable[i][0] = 1;
					aCount++;
				}
			}else {
				if(lastCol.charAt(i) == 'A') { // increment that position by 1 all other stay the same
					aCount++;
					occTable[i][0] = aCount;
					occTable[i][1] = occTable[i-1][1];
					occTable[i][2] = occTable[i-1][2];
					occTable[i][3] = occTable[i-1][3];
				}else if(lastCol.charAt(i) == 'C') {
					cCount++;
					occTable[i][0] = occTable[i-1][0];
					occTable[i][1] = occTable[i-1][1];
					occTable[i][2] = occTable[i-1][2];
					occTable[i][3] = cCount;
				}
				else if(lastCol.charAt(i) == 'G') {
					gCount++;
					occTable[i][0] = occTable[i-1][0];
					occTable[i][1] = occTable[i-1][1];
					occTable[i][2] = gCount;
					occTable[i][3] = occTable[i-1][3];
				}
				else if(lastCol.charAt(i) == 'T') {
					tCount++;
					occTable[i][0] = occTable[i-1][0];
					occTable[i][1] = tCount;
					occTable[i][2] = occTable[i-1][2];
					occTable[i][3] = occTable[i-1][3];
				}
			}
		}

		return occTable;
	}

	//EVERYTHING THAT HAS TO DO WITH ALIGN BELOW
	private static void align(FMIndexWAllData index, String reads, String alignOutput) {
		// read in file of reads do FM Index search, if found  = 100M otherwise need to do alignment and store as SAM file
		String readName = "";
		String sequence = "";

		//output testing
		String output = alignOutput;
		PrintStream out; 
		try { 
			out = new PrintStream(new FileOutputStream(output));
			System.setOut(out);

		} 
		catch (FileNotFoundException e) {
			e.printStackTrace(); 
		}
		
		// SAM header
		System.out.println("@HD	VN:1.0	SO:unsorted\r\n" + 
				"@SQ	SN:MN988713.1	LN:29882\r\n" + 
				"@PG	ID:bowtie2	PN:bowtie2	VN:2.2.9	"
				+ "CL:\"/usr/local/bin/../Cellar/bowtie2/2.2.9/bin/bowtie2-align-s --wrapper basic-0 --rdg "
				+ "0,2 --rfg 0,2 --mp 2 -x 2019-nCoV_bt2_idx -f -S mapped_s1000.sam -U reads_s1000.fa\"");

		try (BufferedReader br = new BufferedReader(new FileReader(reads))) { // read in file
			String line;
			while ((line = br.readLine()) != null) {
				if(line.charAt(0) == '>') { // store name of the specific file
					readName = line;
					readName = readName.substring(1, readName.length());
				}
				else {
					sequence = line;
					int[] results = index.search(sequence); // gives back the interval from backwards search
					// if doesn't comes back as 100M need to save otherwise split into more work
					if(results[0] != 0 && results[1] != 0) { // back trace worked so it exists as is in the sequence is a 100M read
						results[0] = results[0] - 1; // off by one error
						//store in file because we are done!
						System.out.println(readName + "\t0\t" + "MMN988713.1\t" + (index.SAAAA.suffixes[results[0]].index + 1)  + "\t255\t100M\t = * 0 \t" + sequence.trim() + "\t*");
					}
					else { // otherwise need to do alignment
						String sequence1;
						String sequence2;
						String sequence3;
						String sequence4;
						String sequence5;

						//split the strings up
						sequence1 = sequence.substring(0, 20);
						sequence2 = sequence.substring(20, 40);
						sequence3 = sequence.substring(40, 60);
						sequence4 = sequence.substring(60, 80);
						sequence5 = sequence.substring(80, 100);

						//FM index search for each
						int[] results1 = index.search(sequence1); 
						int[] results2 = index.search(sequence2); 
						int[] results3 = index.search(sequence3); 
						int[] results4 = index.search(sequence4); 
						int[] results5 = index.search(sequence5);

						// call alignment on each
						int[] scores = new int[5]; // keep track of scores that come from alignment
						boolean[] flags = {false, false, false, false, false}; // ensure that it is not 0
						String[] cigars = {"", "","","",""}; // keep track of cigars associated with each
						String s1, s2; // used for storing what we are aligining on 
						SemiGlobalAlignmentTable dpt = null;
						System.gc(); // clear memory
						String genome = index.fileLocation;
						genome = processRestOfFile(genome); // to get back the entire genome

						s2 = sequence; // what we are aligning 
						// string to align decided below
						//s2 will get us the area where we matched seed to and extend it to give us more room to match to
						if(results1[0]!= 0) {
							if(index.SAAAA.suffixes[results1[0]-1].index + 1 - 5 < 0) { // if we are at start of string, this repeats for every results array
								s1 = genome.substring(0, index.SAAAA.suffixes[results1[0]].index + 105);

							}
							else if(index.SAAAA.suffixes[results1[0]-1].index +1 + 105 > genome.length()){ // if we are at end of string this repeats for every results array
								s1 = genome.substring(index.SAAAA.suffixes[results1[0]-1].index - 5, genome.length());

							}else { // if we are in middle of string no issue running off either end of genome
								s1 = genome.substring(index.SAAAA.suffixes[results1[0]-1].index +1 - 5, index.SAAAA.suffixes[results1[0]-1].index +1 + 105);
							}

							dpt = new SemiGlobalAlignmentTable(s2, s1); // start fitting alignment
							scores[0] = dpt.getScore(); 
							flags[0] = true;
							cigars[0] = dpt.getCIGAR();
						}

						// string to align
						if(results2[0]!= 0) {
							if(index.SAAAA.suffixes[results2[0]-1].index+1 - 5 < 0) {
								s1 = genome.substring(0, index.SAAAA.suffixes[results2[0]-1].index+1 + 105);

							}
							else if(index.SAAAA.suffixes[results2[0]-1].index+1 + 105 > genome.length()){
								s1 = genome.substring(index.SAAAA.suffixes[results2[0]-1].index+1 - 5, genome.length());

							}else {
								s1 = genome.substring(index.SAAAA.suffixes[results2[0]-1].index+1 - 5, index.SAAAA.suffixes[results2[0]-1].index+1 + 105);
							}
							dpt = new SemiGlobalAlignmentTable(s2, s1);
							scores[1] = dpt.getScore(); 
							flags[1] = true;
							cigars[1] = dpt.getCIGAR();
						}

						// string to align
						if(results3[0]!= 0) {
							if(index.SAAAA.suffixes[results3[0]-1].index+1 - 5 < 0) {
								s1 = genome.substring(0, index.SAAAA.suffixes[results3[0]-1].index+1 + 105);

							}
							else if(index.SAAAA.suffixes[results3[0]-1].index+1 + 105 > genome.length()){
								s1 = genome.substring(index.SAAAA.suffixes[results3[0]-1].index+1 - 5, genome.length());

							}else {
								s1 = genome.substring(index.SAAAA.suffixes[results3[0]-1].index+1 - 5, index.SAAAA.suffixes[results3[0]-1].index+1 + 105);
							}
							dpt = new SemiGlobalAlignmentTable(s2, s1);
							scores[2] = dpt.getScore(); 
							flags[2] = true;
							cigars[2] = dpt.getCIGAR();
						}

						// string to align
						if(results4[0]!= 0) {
							if(index.SAAAA.suffixes[results4[0]-1].index+1 - 5 < 0) {
								s1 = genome.substring(0, index.SAAAA.suffixes[results4[0]-1].index+1 + 105);

							}
							else if(index.SAAAA.suffixes[results4[0]-1].index +1 + 105 > genome.length()){
								s1 = genome.substring(index.SAAAA.suffixes[results4[0]-1].index+1 - 5, genome.length());

							}else {
								s1 = genome.substring(index.SAAAA.suffixes[results4[0]-1].index+1 - 5, index.SAAAA.suffixes[results4[0]-1].index +1 + 105);
							}
							dpt = new SemiGlobalAlignmentTable(s2, s1);
							scores[3] = dpt.getScore(); 
							flags[3] = true;
							cigars[3] = dpt.getCIGAR();
						}

						// string to align
						if(results5[0]!= 0) {
							if(index.SAAAA.suffixes[results5[0]-1].index +1 - 5 < 0) {
								s1 = genome.substring(0, index.SAAAA.suffixes[results5[0]-1].index+1 + 105);

							}
							else if(index.SAAAA.suffixes[results5[0]-1].index +1+ 105 > genome.length()){
								s1 = genome.substring(index.SAAAA.suffixes[results5[0]-1].index+1 - 5, genome.length());

							}else {
								s1 = genome.substring(index.SAAAA.suffixes[results5[0]-1].index +1- 5, index.SAAAA.suffixes[results5[0]-1].index+1 + 105);
							}
							dpt = new SemiGlobalAlignmentTable(s2, s1);
							scores[4] = dpt.getScore(); 
							flags[4] = true;
							cigars[4] = dpt.getCIGAR();
						}

						//check flags otherwise set to smallest value;
						for(int i = 0; i < flags.length; i++) {
							if(flags[i] == false) {
								scores[i] = Integer.MIN_VALUE;
							}
						}

						int bestScore = 0;
						String Cigar = "";
						for(int i = 0; i < 4; i++) { // get the best score from array
							if(scores[i] > bestScore) { 
								bestScore = scores[i]; // keep track of best score
								String readName2 = readName + "\t";
								String flag = "0\t"; // 0 or 255
								String position = "results" + (i+1);
								int place = 0;

								if(position.equals("results1")) {
									place = (index.SAAAA.suffixes[results1[0]-1].index + 1); //off by one on index
									position = "" +place+ "";
									Cigar = cigars[0];
								}else if(position.equals("results2")){
									place = (index.SAAAA.suffixes[results2[0]-1].index+ 1);
									position = "" +place+ "";
									Cigar = cigars[1];

								}
								else if(position.equals("results3")) {
									place = (index.SAAAA.suffixes[results3[0]-1].index + 1);
									position = "" +place+ "";
									Cigar = cigars[2];

								}
								else if(position.equals("results4")) {
									place = (index.SAAAA.suffixes[results4[0]-1].index + 1);
									position = "" +place+ "";
									Cigar = cigars[3];

								}
								else if(position.equals("results5")) {
									place = (index.SAAAA.suffixes[results5[0]-1].index + 1);
									position = "" +place+ "";
									Cigar = cigars[4];

								}
								position += "\t";
								String mapq = "255\t"; 

								String CIGAR = Cigar + "\t";
								String mate = "= * 0\t"; 
								String readSequence = sequence + "\t";

								String qualityScore = "*\t";
								System.out.println(readName2 + flag + "MMN988713.1\t" + position + mapq +CIGAR + mate + readSequence + qualityScore);


							}
						}

					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	//MAIN
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("If you would like to use the index command enter 1. \nIf you would like to use the reference.ca command enter 2");
		
		int userDec = scan.nextInt();
		// These can all be switched to desired input and output location or allow the user to use it
		if(userDec == 1) {
			System.out.println("Enter the location of the FASTA File with \\ instead of single backslash ");
			String FASTA = scan.next(); //"C:\\Users\\vlada\\Downloads\\CMSC423_bwt_variant-master\\CMSC423_bwt_variant-master\\data\\2019-nCoV.fa"; // change to scan.next()
			System.out.println("Enter output location with \\ instead of single backslash");
			String output = scan.next();//"C:\\\\Users\\\\vlada\\\\Downloads\\\\indexOutput.txt"; // change to scan.next()
			System.out.println("Running...");
			index(FASTA, output);
			//System.out.println("DONE!");
		}
		else if(userDec == 2){
			System.out.println("Enter the location of the FASTA File with \\ instead of single backslash ");
			String FASTA = scan.next();
			//String FASTA = "C:\\Users\\vlada\\Downloads\\CMSC423_bwt_variant-master\\CMSC423_bwt_variant-master\\data\\2019-nCoV.fa"; // change to scan.next()
			System.out.println("Enter output location for index with \\ instead of single backslash");
			String indexOutput = scan.next();
			//String indexOutput = "C:\\\\Users\\\\vlada\\\\Downloads\\\\indexOutput.txt"; 

			System.out.println("Enter the location of the Reads File with \\ instead of single backslash");
			String READS= scan.next(); //"C:\\Users\\vlada\\Downloads\\reads.fa\\reads.fa";
			System.out.println("Enter output location for align with \\\\ instead of single backslash");
			String alignOutput = scan.next(); //"C:\\\\\\\\Users\\\\\\\\vlada\\\\\\\\Downloads\\\\\\\\alignOutput.sam"; 
			System.out.println("Running...");
			align(index(FASTA, indexOutput), READS, alignOutput); // runs index to get file, has reads, has output location

		}
		else {
			System.out.println("You choose to not pick something valid, you do not get permission to use the cool tools!");
		}
	scan.close();
	}


}

