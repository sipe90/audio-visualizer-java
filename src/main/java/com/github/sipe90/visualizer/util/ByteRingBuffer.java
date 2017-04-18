package com.github.sipe90.visualizer.util;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Simple ring buffer for storing byte values. All methods which modify the internal state are synchronized.
 */
public class ByteRingBuffer implements Iterable<Byte> {

    private static final int DEF_SIZE = 1024;

    private byte[] data;
    private int head;
    private int tail;
    private int size;

    public ByteRingBuffer() {
        this(DEF_SIZE);
    }

    public ByteRingBuffer(int maxSize) {
        if (maxSize < 0) throw new IllegalArgumentException("Buffer size cannot be negative");
        data = new byte[maxSize];
    }

    public synchronized void resize(int newMaxSize) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    /**
     * Quick reset, sets the read pointer, write pointer and size to 0 but does not reset the data buffer.
     */
    public synchronized void clear() {
        head = tail = size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public int getMaxSize() {
        return  data.length;
    }

    public synchronized int write(byte[] bytes, int pos, int amount) {
        if (pos < 0 || pos > bytes.length) throw new IllegalArgumentException("Position out of bounds");
        if (pos + amount > bytes.length || amount < 0) throw new IllegalArgumentException("Amount out of bounds");
        if (amount > getMaxSize()) throw new IllegalArgumentException("Cannot write more than the maximum buffer size");
        if (amount == 0) return 0;

        if (amount > getMaxSize() - tail) {
            int len = getMaxSize() - tail;
            System.arraycopy(bytes, 0, data, tail, len);
            System.arraycopy(bytes, len, data, 0, amount - len);
        } else {
            System.arraycopy(bytes, 0, data, tail, amount);
        }

        tail = (tail + amount) % getMaxSize();

        int empty = getMaxSize() - size;
        if (amount > empty) {
            int overwritten = amount - empty;
            head = (head + overwritten) % getMaxSize();
        }

        size = Math.min(size + amount, getMaxSize());

        return amount;
    }

    public synchronized int read(byte[] dest, int pos, int amount) {
        if (pos < 0 || pos > dest.length) throw new IllegalArgumentException("Position out of bounds");
        if (pos + amount > dest.length || amount < 0) throw new IllegalArgumentException("Amount out of bounds");
        if (amount > getMaxSize()) throw new IllegalArgumentException("Cannot read more than the maximum buffer size");
        if (amount == 0 || isEmpty()) return 0;

        if (amount > size) {
            amount = size;
        }

        // If need to wrap around the buffer
        if (amount > getMaxSize() - head) {
            int len = getMaxSize() - head;
            System.arraycopy(data, head, dest, 0, len);
            System.arraycopy(data, 0, dest, len, amount - len);
        } else {
            System.arraycopy(data, head, dest, 0, amount);
        }

        // Update indexes
        head = (head + amount) % getMaxSize();
        size -= amount;

        return amount;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {

            int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < getMaxSize();
            }

            @Override
            public Byte next() {
                return data[idx++];
            }
        };
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}
