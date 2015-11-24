package com.github.akarnokd.msmr.model;

import it.unimi.dsi.fastutil.ints.IntSet;

public class Station {
    public byte id;
    
    public IntSet machines;
    
    public int currentEmployee;
}
