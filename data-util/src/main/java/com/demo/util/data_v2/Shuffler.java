package com.demo.util.data_v2;

import java.util.Collections;
import java.util.List;

/**
 * Created by dbarr on 12/29/17.
 */
public class Shuffler extends DataUtil {

    private static final String INPUT_FILE = "/Users/dbarr/Garbage/data/demand_data_v2.csv";
    private static final String OUTPUT_FILE = "/Users/dbarr/Garbage/data/demand_data_v2_shuffled.csv";

    public static void main(String[] args) {
        List<String> rows = new Reader().read(INPUT_FILE);
        Collections.shuffle(rows);
        new Writer().write(OUTPUT_FILE, rows);
    }
}
