package com.github.akarnokd.msmr.model;

import java.util.*;

import hu.akarnokd.xml.*;
import it.unimi.dsi.fastutil.shorts.*;

public class Machine implements XSerializable, ShortID {
    public short id;
    
    public byte sequenceSize;
    public byte batchSize;
    
    public final Short2IntMap operationTimes = new Short2IntOpenHashMap();
    
    public final List<Product> inputBuffer = new ArrayList<>();
    
    public final List<Product> outputBuffer = new ArrayList<>();

    public Machine copy() {
        Machine m = new Machine();
        
        m.id = id;
        m.sequenceSize = sequenceSize;
        m.batchSize = batchSize;
        m.operationTimes.putAll(operationTimes);
        
        return m;
    }
    @Override
    public void load(XElement source) {
        id = source.getShort("id");
        sequenceSize = source.getByte("sequence-size");
        batchSize = source.getByte("batch-size");

        operationTimes.clear();
        for (XElement xopt : source.childrenWithName("operation")) {
            operationTimes.put(xopt.getShort("id"), xopt.getInt("time"));
        }
    }
    
    @Override
    public void save(XElement xout) {
        xout.set("id", id);
        xout.set("sequence-size", sequenceSize);
        xout.set("batch-size", batchSize);
        for (Short2IntMap.Entry e : operationTimes.short2IntEntrySet()) {
            XElement xopt = xout.add("operation");
            xopt.set("id", e.getShortKey());
            xopt.set("time", e.getIntValue());
        }
    }
    
    @Override
    public short id() {
        return id;
    }

}
