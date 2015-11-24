package com.github.akarnokd.msmr.model;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public class Routing {
    public short id;
    
    public List<Operation> enter;
    public List<Operation> leave;
    
    public Int2ObjectMap<Operation> allOperations;
}
