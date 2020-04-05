import java.util.Arrays;
import java.util.HashMap;

public class Node {
    
    Node left;
    Node right;
    public boolean[] bitVector;
    public HashMap<Character, Boolean> alphabetMap;
    
    public Node(String s){
        encode(s);
        calculateBitVector(s);
    }    
   
    public boolean getEncoding(char c) {
        return alphabetMap.get(c);
    }
    
    private void calculateBitVector(String s){
        bitVector = new boolean[s.length()];
        
        for (int i = 0; i < s.length(); i++) {
            bitVector[i] = alphabetMap.get(s.charAt(i));
        }
    }
    
   
    private void encode(String s){
        // Create alphabet string
        String alpha = "";
        for (int i = 0; i < s.length(); i++) {
            if(!alpha.contains(Character.toString(s.charAt(i)))){
                alpha += String.valueOf(s.charAt(i));
            }
        }
        char[] chars = alpha.toCharArray();
        Arrays.sort(chars);
        
        String alphabet = new String(chars);
        
        this.alphabetMap = new HashMap<>();
        boolean bitValue;
        if (alphabet.length() == 1) {
            alphabetMap.put(alphabet.charAt(0), false);
        }
        else{
            for (int i = 0; i < alphabet.length(); i++) {
                bitValue = i < alphabet.length()/2 ? false : true;
                alphabetMap.put(alphabet.charAt(i), bitValue);
            }
        }
    }

}