import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class FMIndexWAllData {

	String name;
	String FASTARead; 
	String fileLocation;
	int sizeOfGenome;
	int [][] occuranceTable;
	int[] suffixArray;
	String firstColLetters;
	char[] lastColLetters;
	
	WaveletTree wt;
	String sequence;
	SuffixArray sa;
	String query;
	char q;
	int s;
	int e;
	C c;
	HashMap<Integer, Integer> pos;
	NewSuffixArray SAAAA;
	
	public FMIndexWAllData(String name, String fASTARead, String fileLocation, int sizeOfGenome, 
			String storeFirstCol,
			char[] lastColLetters, int[][] occuranceTable, NewSuffixArray sA) {
		this.name = name;
		this.FASTARead = fASTARead; //genome
		this.fileLocation = fileLocation;
		this.sizeOfGenome = sizeOfGenome;
		this.firstColLetters = storeFirstCol;
		this.lastColLetters = lastColLetters;
		this.occuranceTable = occuranceTable;
		this.SAAAA = sA;
		
		
		String sequence = processRestOfFile(fileLocation);
		this.sequence = sequence;
		pos = new HashMap<>();

		String bwt = sA.getBWT();

		c = new C(sequence);

		
		wt = new WaveletTree(bwt);

	
		s = 0;
		e = sequence.length();
		

	}

	private static String processRestOfFile(String fASTA) { // to get the entire file
		String input = "";
		boolean firstLine = true;
		try (BufferedReader br = new BufferedReader(new FileReader(fASTA))) {
			String line;

			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
				} else {
					input += line;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input.replaceAll("\n", "").replaceAll("\r", "") + "$";
	}

	// FM index search function
	public int[] search(String query) {
		this.query = query;
		e = sequence.length();
		s = 0;

		// Iterate the query string backwards
		for (int i = query.length(); i > 0; i--) {
			q = query.charAt(i - 1);
			if (q != 'N') {
				s = c.occurrence.get(q) + wt.rank(q, s - 1) + 1;
				e = c.occurrence.get(q) + wt.rank(q, e);
				if (e < s) {
					return new int[] { 0, 0 };
				}
			}
		}
		// Return indeces range
		return new int[] { s, e };
	}

}
