import java.util.*;

public class NewSuffixArray{
	String s;
	String[] m;
	int[] a; 
	String bwt = "";
	public Suffix[] suffixes;

	public NewSuffixArray(String s){
		this.s = s;
		this.m = new String[s.length()];

		int n = s.length();
		this.suffixes = new Suffix[n];
		for (int i = 0; i < n; i++)
			suffixes[i] = new Suffix(s, i);
		Arrays.sort(suffixes);

		generateSuffixes();

		Arrays.sort(m);

		generateBWT();


	}

	public int rank(String query) { 
		int lo = 0, hi = suffixes.length - 1; 
		while (lo <= hi) { 
			int mid = lo + (hi - lo) / 2; 
			int cmp = compare(query,suffixes[mid]);
			if (cmp < 0) 
				hi = mid - 1; 
			else if (cmp > 0)
				lo = mid + 1;
			else 
				return mid; 
		} 
		return lo;
	}

	private static int compare(String query, Suffix suffix){
		int n = Math.min(query.length(), suffix.length());
		for (int i = 0; i < n; i++) {
			if (query.charAt(i) < suffix.charAt(i))
				return -1;
			if (query.charAt(i) > suffix.charAt(i))
				return +1;
		}
		return query.length() - suffix.length();
	}

	public static class Suffix implements Comparable<Suffix> {
		public final String text;
		public final int index;

		public Suffix(String text, int index) {
			this.text = text;
			this.index = index;
		}

		private int length() {
			return text.length() - index;
		}

		public char charAt(int i) {
			return text.charAt(index + i);
		}

		public int compareTo(Suffix that) {
			if (this == that)
				return 0; // optimization
			int n = Math.min(this.length(), that.length());
			for (int i = 0; i < n; i++) {
				if (this.charAt(i) < that.charAt(i))
					return -1;
				if (this.charAt(i) > that.charAt(i))
					return +1;
			}
			return this.length() - that.length();
		}

		public String toString() {
			return text.substring(index);
		}
		public int getIndex() {
			return index;
		}
	}

	public String getBWT(){
		return this.bwt;
	}


	private void generateSuffixes(){
		String tempS = this.s;

		for (int i = 0; i < s.length(); i++) {
			m[i] = tempS;
			// Rotate String
			tempS = tempS.substring(1) + tempS.charAt(0);
		}
	}

	private void generateBWT(){
		for (int i = 0; i < s.length(); i++) {
			bwt += m[i].charAt(s.length()-1);
		}
	}
}