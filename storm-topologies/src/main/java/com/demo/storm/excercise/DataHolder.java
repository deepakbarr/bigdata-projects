package com.demo.storm.excercise;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dbarr on 12/23/17.
 */
public class DataHolder {

    private Set<String> supply;
    private Set<String> demand;

    public DataHolder() {
        supply = new HashSet();
        demand = new HashSet();
    }

    public void addSupplyRecord(String s) {
        supply.add(s);
    }

    public void addDemandRecord(String d) {
        demand.add(d);
    }

    public int countSupply() {
        return supply.size();
    }

    public int countDemand() {
        return demand.size();
    }
}
