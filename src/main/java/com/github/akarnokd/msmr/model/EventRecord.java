package com.github.akarnokd.msmr.model;

public class EventRecord {
    public short id;
    public byte type;
    /** The operation identifier. The bit 15 indicates if this is an end event. */
    public short operation;
    public short machine;
    public int timestamp;
    public short employee;
    public byte station;
}
