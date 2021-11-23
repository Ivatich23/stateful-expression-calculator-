package util;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<String> checkSymbol(String expression){
        String[] strings = expression.split("\\W");
        List<String> oneSymbolString = new ArrayList<>();
        for (String string : strings) {
            if(string.length()==1&& !oneSymbolString.contains(string)){
                oneSymbolString.add(string);
            }
        }
        return oneSymbolString;
    }
}
