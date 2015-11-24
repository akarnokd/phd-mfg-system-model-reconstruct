package com.github.akarnokd.msmr.model;

import it.unimi.dsi.fastutil.shorts.ShortSet;

public class Employee {
    public short id;
    /** The set of machines this employee can operate. */
    public ShortSet machines;
    /** The start and end hours on each weekday. */
    public byte[] workhours = new byte[7 * 2];
}
