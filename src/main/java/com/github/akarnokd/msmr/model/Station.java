package com.github.akarnokd.msmr.model;

import hu.akarnokd.xml.*;
import it.unimi.dsi.fastutil.shorts.*;

public class Station implements XSerializable, ShortID {
    public short id;
    
    public final ShortSet machines = new ShortOpenHashSet();
    
    public final ShortSet employees = new ShortOpenHashSet(); 
    
    public Employee currentEmployee;
    
    @Override
    public short id() {
        return id;
    }
    
    @Override
    public void load(XElement source) {
        id = source.getShort("id");
        machines.clear();
        for (XElement xm : source.childrenWithName("machine")) {
            machines.add(xm.getShort("id"));
        }
        
        for (XElement xm : source.childrenWithName("employee")) {
            employees.add(xm.getShort("id"));
        }
    }
    
    @Override
    public void save(XElement xout) {
        xout.set("id", id);
        ShortIterator si = machines.iterator();
        while (si.hasNext()) {
            XElement xm = xout.add("machine");
            xm.set("id", si.next());
        }
        
        ShortIterator si2 = employees.iterator();
        while (si2.hasNext()) {
            XElement xm = xout.add("employee");
            xm.set("id", si2.next());
        }
    }
}
