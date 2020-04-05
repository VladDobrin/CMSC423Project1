import java.util.Arrays;

public class BurrowsWheelerTransform {
	
	public static void getRot(String source, String[] s) {
		s[0] = source;
		for(int i = 1; i < source.length(); i++) {
			s[i] = s[i-1].substring(1) + s[i-1].charAt(0);
		}
	}
	
	public static String main(String input) {
		final int SIZE = input.length();
		String[] rotations = new String[SIZE];
		getRot(input, rotations);

		Arrays.sort(rotations);
		
		String lastCol = "";
		String firstCol = ""; 
		for(int i = 0; i < SIZE; i++) {
			lastCol += rotations[i].substring(SIZE-1, SIZE);
			firstCol += rotations[i].substring(0, 1);
		}
		return firstCol + "|" + lastCol;
	}

}
