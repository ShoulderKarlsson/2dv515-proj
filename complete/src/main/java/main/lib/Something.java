package main.lib;

import java.util.Map;

public class Something implements Comparable<Something> {
    public String text;
    public double value;

    public Something(Map.Entry<String, Double> stringDoubleEntry) {
        this.text = stringDoubleEntry.getKey();
        this.value = stringDoubleEntry.getValue();
    }

    @Override
    public int compareTo(Something o) {

        // https://stackoverflow.com/questions/22405015/java-compareto-with-double-values
        if (this.value > o.value) {
            return -1;
        } else if (this.value < o.value) {
            return 1;
        } else {
            return 0;
        }
    }
}
