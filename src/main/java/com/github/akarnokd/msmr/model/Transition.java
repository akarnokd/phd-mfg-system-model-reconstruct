package com.github.akarnokd.msmr.model;

import hu.akarnokd.xml.*;
import it.unimi.dsi.fastutil.ints.*;

public class Transition implements XSerializable {
    public Operation from;
    public Operation to;
    
    public boolean rework;
    
    public short fromId;
    public short toId;
    
    public final IntList times = new IntArrayList();
    
    @Override
    public void load(XElement source) {
        fromId = source.getShort("from");
        toId = source.getShort("to");
        rework = source.getBoolean("rework");
        times.clear();
        for (XElement xt : source.childrenWithName("time")) {
            times.add(xt.getShort("value"));
        }
    }
    
    @Override
    public void save(XElement xout) {
        xout.set("from", fromId);
        xout.set("to", toId);
        xout.set("rework", rework);
        for (int i = 0; i < times.size(); i++) {
            XElement xt = xout.add("time");
            xt.set("value", times.getInt(i));
        }
    }
}
