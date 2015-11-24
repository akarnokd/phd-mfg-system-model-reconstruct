package com.github.akarnokd.msmr.model;

import java.util.List;

import it.unimi.dsi.fastutil.ints.IntList;

public class Operation {
    public short id;
    /** Indicates the top-down flow sequence number inside the routing. */
    public short sequence;
    
    public short routing;
    
    public List<Transition> incoming;
    
    public List<Transition> outgoing;
    
    public IntList times;
}
