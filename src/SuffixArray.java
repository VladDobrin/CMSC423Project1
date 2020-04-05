import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class SuffixArray {
	private Suffix[] suffixes;
	
	public SuffixArray(String text) {
		int n = text.length();
		this.suffixes = new Suffix[n];
		for (int i = 0; i < n; i++)
			suffixes[i] = new Suffix(text, i);
		Arrays.sort(suffixes);
	}

	private static class Suffix implements Comparable<Suffix> {
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
	}

	public int length() {
		return suffixes.length;
	}

	public int index(int i) {
		if (i < 0 || i >= suffixes.length)
			throw new IllegalArgumentException();
		return suffixes[i].index;
	}
	
	public String select(int i) {
		if (i < 0 || i >= suffixes.length)
			throw new IllegalArgumentException();
		return suffixes[i].toString();
	}

	public int rank(String query) { int lo = 0, hi = suffixes.length - 1; while
		(lo <= hi) { int mid = lo + (hi - lo) / 2; int cmp = compare(query,
				suffixes[mid]); if (cmp < 0) hi = mid - 1; else if (cmp > 0) lo = mid + 1;
				else return mid; } return lo; }

	private static int compare(String query, Suffix suffix) {
		int n = Math.min(query.length(), suffix.length());
		for (int i = 0; i < n; i++) {
			if (query.charAt(i) < suffix.charAt(i))
				return -1;
			if (query.charAt(i) > suffix.charAt(i))
				return +1;
		}
		return query.length() - suffix.length();
	}

	public static StringBuffer main(String fileName) {
		fileName = processRestOfFile(fileName);
		String s = fileName.trim();
		SuffixArray suffix = new SuffixArray(s);
		StringBuffer answer = new StringBuffer();

		for (int i = 0; i < s.length(); i++) {
			int index = suffix.index(i);
			assert s.substring(index).equals(suffix.select(i));
			
			if (i == 0) {
				answer.append(index + " ");
			}
			else {
				answer.append(index + " ");
			}
		}
		return answer; 

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

}