package com.github.akarnokd.msmr.model;

import java.util.*;

import hu.akarnokd.xml.*;
import it.unimi.dsi.fastutil.shorts.*;

public class Routing implements XSerializable, ShortID {
    public short id;
    
    public final List<Operation> enter = new ArrayList<>();
    public final List<Operation> leave = new ArrayList<>();
    
    public final Short2ObjectMap<Operation> allOperations = new Short2ObjectOpenHashMap<>();
    
    public final ShortSet allOperationIds = new ShortOpenHashSet();
    
    @Override
    public short id() {
        return id;
    }
    
    @Override
    public void load(XElement source) {
        id = source.getShort("id");
        
        allOperationIds.clear();
        for (XElement xops : source.childrenWithName("operation")) {
            allOperationIds.add(xops.getShort("id"));
        }
    }
    
    @Override
    public void save(XElement xout) {
        xout.set("id", "id");
        ShortIterator it = allOperationIds.iterator();
        while (it.hasNext()) {
            XElement xop = xout.add("operation");
            xop.set("id", it.next());
        }
    }
    
    public void resolve(Short2ObjectMap<Operation> map) {
        ShortIterator it = allOperationIds.iterator();
        while (it.hasNext()) {
            short opid = it.nextShort();
            
            Operation op = map.get(opid);
            if (op.incoming.isEmpty()) {
                enter.add(op);
            }
            if (op.outgoing.isEmpty()) {
                leave.add(op);
            }
            
            allOperations.put(opid, op);
        }
    }
}
