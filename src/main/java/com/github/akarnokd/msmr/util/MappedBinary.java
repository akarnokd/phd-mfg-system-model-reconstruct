package com.github.akarnokd.msmr.util;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.*;
import java.util.function.Function;

import hu.akarnokd.utils.io.Closeables;

public final class MappedBinary<T extends IndexedAccessor> implements Closeable {
    final RandomAccessFile raf;
    final FileChannel channel;
    final MappedByteBuffer mmap;
    final Function<? super MappedByteBuffer, T> accessor;
    public MappedBinary(File file, boolean writable, Function<? super MappedByteBuffer, T> accessor) throws IOException {
        this.accessor = accessor;
        this.raf = new RandomAccessFile(file, writable ? "r" : "rw");
        this.channel = raf.getChannel();
        this.mmap = channel.map(writable ? MapMode.READ_WRITE : MapMode.READ_ONLY, 0, raf.length());
    }
    @Override
    public void close() throws IOException {
        Closeables.close(channel, raf);
    }
    public T createAccessor() {
        return accessor.apply(mmap);
    }
    public int size() {
        return mmap.limit();
    }
    
    public void refresh() {
        mmap.load();
    }
    
    public void flush() {
        mmap.force();
    }
    
    public Iterable<T> sharedIterable() {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                T current = createAccessor();
                int s = current.size();
                return new Iterator<T>() {
                    int idx;
                    @Override
                    public boolean hasNext() {
                        return idx < s;
                    }
                    @Override
                    public T next() {
                        if (idx < s) {
                            current.index(idx++);
                            return current;
                        }
                        throw new NoSuchElementException();
                    }
                };
            }
        };
    }
}
