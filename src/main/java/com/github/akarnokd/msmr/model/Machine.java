package com.github.akarnokd.msmr.model;

import it.unimi.dsi.fastutil.shorts.Short2IntMap;

public class Machine {
    public short id;
    
    public int sequenceSize;
    public int batchSize;
    
    public Short2IntMap operationTimes;
}
