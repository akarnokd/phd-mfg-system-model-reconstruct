package com.github.akarnokd.msmr.model;

import hu.akarnokd.xml.*;
import it.unimi.dsi.fastutil.shorts.*;

public final class Employee implements XSerializable, ShortID {
    public short id;
    /** The set of machines this employee can operate. */
    public final ShortSet machines = new ShortOpenHashSet();
    /** The start and end hours on each weekday. */
    public byte startDay;
    public byte startHour;
    
    public short station;
    
    @Override
    public void load(XElement source) {
        id = source.getShort("id");
        station = source.getShort("station");
        startDay = source.getByte("start-day");
        startHour = source.getByte("start-hour");
        
        for (XElement xm : source.childrenWithName("machine")) {
            machines.add(xm.getShort("id"));
        }
    }
    
    @Override
    public void save(XElement xout) {
        xout.set("id", id);
        xout.set("station", station);
        ShortIterator si = machines.iterator();
        while (si.hasNext()) {
            XElement xm = xout.add("machine");
            xm.set("id", si.next());
        }
        
        xout.set("start-day", startDay);
        xout.set("start-hour", startHour);
    }
    
    @Override
    public short id() {
        return id;
    }
}
