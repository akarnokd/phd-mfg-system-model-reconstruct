package com.github.akarnokd.msmr.model;

public class LogRecord {
    public short id;
    public byte type;
    public short operation;
    public short machineIn;
    public int timestampIn;
    public short employeeIn;
    public short machineOut;
    public int timestampOut;
    public short employeeOut;
    public byte stationIn;
    public byte stationOut;
}
