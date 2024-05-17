package commons;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class NumberToColorMap {
    private static Map<Integer, Color> numberToColorMap = new HashMap<>();
    
    public NumberToColorMap() {
        numberToColorMap.put(1, Color.RED);
        numberToColorMap.put(2, Color.GREEN);
        numberToColorMap.put(3, Color.MAGENTA);
        numberToColorMap.put(4, Color.YELLOW);
        numberToColorMap.put(5, Color.ORANGE);
    }
    
    public Color getColor(int number) {
        return this.numberToColorMap.get(number);
    }
}
