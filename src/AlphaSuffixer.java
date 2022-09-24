
import java.util.ArrayList;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class AlphaSuffixer {
    //65 - 90 & 97 - 122
    private ArrayList<Integer> charNums;
    private int digits;
    private static int UPPER_START = 65;
    private static int UPPER_END = 90;
//    private static int LOWER_START = 97;
//    private static int LOWER_END = 122;
    
    public AlphaSuffixer(){
        digits = 1;
        resetDigitNums();
    }
    
    private void resetDigitNums(){
        charNums = new ArrayList<>();
        
        for(int i = 0; i < digits; i++){
            charNums.add(UPPER_START);
        }
    }
    
    public String next(){
        String suff = "";
        int first = charNums.size() - 1;
        for(int i = first; i >= 0; i--){
            suff = (char)charNums.get(i).intValue() + suff;
        }
        Integer in = charNums.get(first);
        in++;
        if(in.equals(UPPER_END)){
            System.out.print("");
        }
        charNums.set(first, in);
        
        boolean reset = false;
        for(int i = first; i >= 0; i--){
            Integer charNum = charNums.get(i);
            if(charNum > UPPER_END){
                charNum = UPPER_START;
                if(i != 0){
                    charNums.set(i-1, charNums.get(i-1)+1);
                } else {
                    reset = true;
                }
            } else {
                if(charNum < UPPER_START) {
                    charNum = UPPER_START;
                }
            }
            charNums.set(i, charNum);
        }
        if(reset){
            digits++;
            resetDigitNums();
//            return next();
        }
        return suff;
    }
    
}
