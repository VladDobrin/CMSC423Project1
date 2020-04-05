package alignment;

import java.util.HashMap;
import java.util.Map;

public class SemiGlobalAlignmentTable {
	Map <String, Boolean> mode;

	String s1;
	String s2;
	String[] alignment;
	TableEntry table[][];
	int MATCH = 2, MISMATCH = -1, INDEL = -1;
	String CIGAR = "";

	int score;

	public SemiGlobalAlignmentTable(String s1, String s2) {

		this.s1 = "_"+s1;
		this.s2 = "_"+s2;
		this.table = new TableEntry[this.s1.length()][this.s2.length()];
		mode = new HashMap<>();
		mode.put("s1_begin", false);
		mode.put("s1_end",  false);
		mode.put("s2_begin", true);
		mode.put("s2_end",   true);

		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				table[i][j] = new TableEntry();
			}
		}

		populateTable();
		align();

	}

	public int getScore(){
		return this.score;
	}  

	//properly format cigar
	public String getCIGAR() {
		String toReturnCIGAR = "";
		int countL = 1;
		int countU = 1;
		int countD = 1;
		for(int i = CIGAR.length() - 1; i > 0 ; i--) {
			if(CIGAR.charAt(i) == 'D') {
				if(CIGAR.charAt(i) == CIGAR.charAt(i - 1)) {
					countD++;
				}
				else {
					toReturnCIGAR+= countD + "M";
					countD = 1;
				}
			}
			else if(CIGAR.charAt(i) == 'L') {
				if(CIGAR.charAt(i) == CIGAR.charAt(i - 1)) {
					countL++;
				}
				else {
					toReturnCIGAR+= countL + "D";
					countL = 1;
				}
			}
			else if(CIGAR.charAt(i) == 'U') {
				if(CIGAR.charAt(i) == CIGAR.charAt(i - 1)) {
					countU++;
				}
				else {
					toReturnCIGAR+= countU + "I";
					countU = 1;
				}
			}
		}

		if(CIGAR.charAt(0) == 'D') {
			toReturnCIGAR+= countD + "M";
			countD = 1;
		}
		else if(CIGAR.charAt(0) == 'L') {
			toReturnCIGAR+= countL + "D";
			countL = 1;
		}
		else if(CIGAR.charAt(0) == 'U') {
			toReturnCIGAR+= countU + "I";
			countU = 1;
		}

		if(countD != 1 ) { // add whatever was last in the pipeline
			toReturnCIGAR+= countD + "M";
		}
		else if(countL != 1) {
			toReturnCIGAR+= countL + "D";
		}
		else if(countU != 1) {
			toReturnCIGAR+= countU + "I";
		}

		// correcting for errors that came from the fact that it adds the 4D at front for 
		// other string
		if(toReturnCIGAR.contains("100M")) {
			return "100M";
		}
		if(toReturnCIGAR.substring(0, 2).equals("4D")) { // starts doing the cigar from the 
			return toReturnCIGAR.substring(2,toReturnCIGAR.length());
		}
		return toReturnCIGAR;
	}

	protected void populateTable(){
		for(int i = 0; i < this.s1.length(); i++){
			for(int j = 0; j < this.s2.length(); j++){
				if (i == 0 && j == 0) {
					table[i][j].setScore(0);
				}
				else if(i == 0 && j > 0) {
					if(mode.get("s1_begin").booleanValue()){
						table[i][j].setScore(0);
					}
					else{
						table[i][j].setScore(table[i][j-1].getScore() + INDEL);
					}
					table[i][j].setPath(TableEntry.Path.LEFT);
				}
				else if(i > 0 && j == 0) {
					if(mode.get("s2_begin").booleanValue()){
						table[i][j].setScore(0);
					}
					else{
						table[i][j].setScore(table[i-1][j].getScore() + INDEL);
					}
					table[i][j].setPath(TableEntry.Path.UP);
				}
				else if(i > 0 && j > 0){
					table[i][j].setScore(calculateScore(i,j));
				}
			}
		}
	}

	protected int calculateScore(int i , int j){

		boolean isMatch = s1.charAt(i) == s2.charAt(j) ? true : false;

		Map<String, Integer> cells = new HashMap<>();
		cells.put("up", table[i-1][j].getScore() + INDEL);
		cells.put("diag", table[i-1][j-1].getScore() + (isMatch ? MATCH : MISMATCH));
		cells.put("left", table[i][j-1].getScore() + INDEL);

		int max = Integer.MIN_VALUE;
		for (Map.Entry<String, Integer> cell : cells.entrySet()) {
			if (cell.getValue() > max) {
				max = cell.getValue();
				switch (cell.getKey()) {
				case "up":
					table[i][j].setPath(TableEntry.Path.UP);
					break;
				case "diag":
					table[i][j].setPath(TableEntry.Path.DIAG);
					break;
				case "left":
					table[i][j].setPath(TableEntry.Path.LEFT);
					break;
				}
			}
		}
		return max;
	}


	protected void align(){
		int i = s1.length()-1;
		int j = s2.length()-1;

		mode.put("s1_begin", false);
		mode.put("s1_end",  true);
		mode.put("s2_begin", false);
		mode.put("s2_end",   false);
		
		// use max score of last row to start from to work back
		if(mode.get("s1_end")){
			int max = Integer.MIN_VALUE;
			for (int k = 0; k < s2.length(); k++) {
				if(table[s1.length()-1][k].getScore() > max){
					max = table[s1.length()-1][k].getScore();
					j = k;
					this.score = max;
				}
			}
		}
		
		// got till first row
		while (i != 0 | j != 0) {
			if(table[i][j].getPath() == TableEntry.Path.UP){
				i--;
				this.CIGAR += "U";

			}
			else if(table[i][j].getPath() == TableEntry.Path.DIAG){
				i--;
				j--;
				this.CIGAR += "D";
			}
			else if(table[i][j].getPath() == TableEntry.Path.LEFT){
				j--;
				this.CIGAR += "L";
			}
		}

	}
}