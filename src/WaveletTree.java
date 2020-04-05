

import java.util.HashMap;

public class WaveletTree {

    public Node root;
    String s;
    
    public WaveletTree(String s) {
        this.s = s;
        root = constructTree(s, new Node(s));
    }
   
    private Node constructTree(String s, Node node) {
        if(!(countLetters(s) <= 2)){
            String sLeft = "";
            String sRight = "";

            for (int i = 0; i < s.length(); i++) {
                if (node.getEncoding(s.charAt(i))) {
                    sRight += s.charAt(i);
                }
                else
                    sLeft += s.charAt(i);
            }
            node.left = constructTree(sLeft, new Node(sLeft));
            node.right = constructTree(sRight, new Node(sRight));
        }
        return node;
    }
   
    public int rank(char c, int index){
        return iRank(c, index, root);
    }
    
   
    private int iRank(char c, int index, Node node){
        if (node == null) {
            return index;
        }
        else{
            boolean encoding = node.getEncoding(c);
            int count = 0;
            for (int i = 0; i < index; i++) {
                if(node.bitVector[i] == encoding){
                    count++;
                }
            }

            if(encoding == false)
                return iRank(c, count, node.left);
            else
                return iRank(c, count, node.right);
        }
    }
    
    private int countLetters(String s){
        HashMap<Character, Integer> letters = new HashMap<Character, Integer>();
        char[] chars = s.toCharArray();

        for(int i=0; i<chars.length;i++)
        {
            if(!letters.containsKey(chars[i]))
            {
                letters.put(chars[i], 1);
            }
            letters.put(chars[i], letters.get(chars[i])+1);
        }

        return letters.size();
    }
}