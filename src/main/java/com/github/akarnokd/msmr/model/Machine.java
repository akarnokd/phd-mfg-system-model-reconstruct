package com.github.akarnokd.msmr.model;

import java.util.*;

import hu.akarnokd.xml.*;
import it.unimi.dsi.fastutil.bytes.*;
import it.unimi.dsi.fastutil.shorts.*;

public class Machine implements XSerializable, ShortID {
    public short id;
    
    public byte capacity;
    public short station;
    
    public final Short2IntMap operationTimes = new Short2IntOpenHashMap();

    public Operation operation;
    public final List<Product> products = new ArrayList<>();
    public final Short2ObjectMap<SlotState> slotStates = new Short2ObjectOpenHashMap<>();
    
    public enum MachineState {
        AVAILABLE,
        PROCESSING,
        COMPLETED
    }
    
    public enum ProcessingType {
        SINGLE,
        SEQUENTIAL,
        BATCH
    }
    
    public enum SlotState {
        OPEN,
        INCOMING,
        OCCUPIED,
        OUTGOING,
        FREE
    }
    
    public MachineState state = MachineState.AVAILABLE;
    
    public ProcessingType processingType;
    
    public Machine copy() {
        Machine m = new Machine();
        
        m.id = id;
        m.capacity = capacity;
        m.processingType = processingType;
        m.operationTimes.putAll(operationTimes);
        m.processingType = processingType;
        
        return m;
    }
    @Override
    public void load(XElement source) {
        id = source.getShort("id");
        capacity = source.getByte("capacity");
        processingType = source.getEnum("type", ProcessingType.class);

        operationTimes.clear();
        for (XElement xopt : source.childrenWithName("operation")) {
            operationTimes.put(xopt.getShort("id"), xopt.getInt("time"));
        }
    }
    
    @Override
    public void save(XElement xout) {
        xout.set("id", id);
        xout.set("capacity", capacity);
        xout.set("type", processingType);
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
