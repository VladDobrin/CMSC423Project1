

import java.util.HashMap;

public class C {

    public HashMap<Character, Integer> occurrence;
    String s;

    public C(String s) {
        this.s = s;
        generateC();
    }

    private void generateC() {
        occurrence = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            occurrence.put(s.charAt(i), 0);
        }
        
        for (int i = 0; i < s.length(); i++) {
            Character[] keys = occurrence.keySet().toArray(new Character[0]);
            for (int j = 0; j < keys.length; j++) {
                if(s.charAt(i) < keys[j]){
                    occurrence.put(keys[j], occurrence.get(keys[j].toString().charAt(0)) + 1);
                }
            }
        }
    }
}