package com.github.akarnokd.msmr.model;

import hu.akarnokd.xml.*;

public class Product implements ShortID, XSerializable {
    public short id;
    public short type;
    
    public Operation currentOperation;
    public Machine currentMachine;
    
    @Override
    public short id() {
        return id;
    }
    
    @Override
    public void load(XElement source) {
        id = source.getShort("id");
        type = source.getByte("type");
    }
    
    @Override
    public void save(XElement destination) {
        destination.set("id", id);
        destination.set("type", type);
    }
}
