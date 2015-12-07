package com.github.akarnokd.msmr.model;

import java.util.*;

import hu.akarnokd.xml.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;

public class Operation implements XSerializable, ShortID {
    public short id;
    /** Indicates the top-down flow sequence number inside the routing. */
    public short sequence;
    
    public short routing;
    
    public final List<Transition> incoming = new ArrayList<>();
    
    public final List<Transition> outgoing = new ArrayList<>();
    
    public final IntList times = new IntArrayList();
    
    @Override
    public short id() {
        return id;
    }
    
    public void resolve(Short2ObjectMap<Operation> map) {
        for (Transition t : incoming) {
            t.from = map.get(t.fromId);
            t.to = map.get(t.toId);
        }
        for (Transition t : outgoing) {
            t.from = map.get(t.fromId);
            t.to = map.get(t.toId);
        }
    }
    
    @Override
    public void load(XElement source) {
        id = source.getShort("id");
        sequence = source.getShort("sequence");
        routing = source.getShort("routing");
        
        incoming.clear();
        for (XElement xin : source.childrenWithName("incoming")) {
            Transition t = new Transition();
            t.load(xin);
            incoming.add(t);
        }
        
        outgoing.clear();
        for (XElement xout : source.childrenWithName("outgoing")) {
            Transition t = new Transition();
            t.load(xout);
            outgoing.add(t);
        }
        
        times.clear();
        for (XElement xt : source.childrenWithName("time")) {
            times.add(xt.getInt("value"));
        }
    }
    
    @Override
    public void save(XElement xout) {
        xout.set("id", id);
        xout.set("sequence", sequence);
        xout.set("routing", routing);
        
        incoming.forEach(v -> {
            XElement xe = xout.add("incoming");
            v.save(xe);
        });
        
        outgoing.forEach(v -> {
            XElement xe = xout.add("outgoing");
            v.save(xe);
        });
        
        IntListIterator it = times.iterator();
        while (it.hasNext()) {
            XElement xe = xout.add("time");
            xe.set("value", it.next());
        }
    }
}
