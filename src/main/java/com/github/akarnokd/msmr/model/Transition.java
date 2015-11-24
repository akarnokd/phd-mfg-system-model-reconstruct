package com.github.akarnokd.msmr.model;

import it.unimi.dsi.fastutil.ints.IntList;

public class Transition {
    public Operation from;
    public Operation to;
    
    public IntList times;
}
