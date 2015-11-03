package com.github.akarnokd.msmr.util;

public interface IndexedAccessor {
    int index();
    void index(int newIndex);
    int size();
}
