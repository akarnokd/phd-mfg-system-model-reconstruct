package com.github.akarnokd.msmr.model;

import hu.akarnokd.xml.*;

public final class Employee implements XSerializable, ShortID {
    public short id;

    public byte startDay;
    public byte endDay;
    
    public byte startHour;
    public byte endHour;
    
    public short station;
    
    public Product product;
    
    public ProductState state;
    
    public enum ProductState {
        EMPTY,
        TO_LOAD,
        TO_UNLOAD,
        LOG
    }
    
    public boolean isWorkingHour(long now) {
        long dayOfWeek = (now / (24L * 60 * 60)) % 7;
        long hour = now % (24L * 60 * 60);
        
        return dayOfWeek >= startDay && dayOfWeek < endDay
                && hour >= startHour && hour < endHour;
    }
    
    @Override
    public void load(XElement source) {
        id = source.getShort("id");
        station = source.getShort("station");
        startDay = source.getByte("start-day");
        endDay = source.getByte("end-day");
        startHour = source.getByte("start-hour");
        endHour = source.getByte("end-hour");
    }
    
    @Override
    public void save(XElement xout) {
        xout.set("id", id);
        xout.set("station", station);
        
        xout.set("start-day", startDay);
        xout.set("end-day", endDay);
        xout.set("start-hour", startHour);
        xout.set("end-hour", endHour);
    }
    
    @Override
    public short id() {
        return id;
    }
}
