package com.VakhovYS;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class CryptoOutputStream extends FilterOutputStream {
    private byte[] key;

    private int idx;

    public CryptoOutputStream(OutputStream out, byte[] key) {
        this(out, key, 0);
    }

    public CryptoOutputStream(OutputStream out, byte[] key, long off) {
        super(out);

        this.key = key.clone();

        idx = (int) (off % key.length);
    }

    public CryptoOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(int b) throws IOException {
        byte b1 = (byte) b;

        b1 ^= key[idx++];

        idx %= key.length;

        out.write(b1);
    }

    /*
    // На самом деле его можно не переопределять, т.к. он делегируется в обычный write(int)
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len < 0)
            throw new IllegalArgumentException("len cannot be less that zero: " + len);

        for (int i = off; i < len + off; i++) {
            b[i] ^= key[idx++];

            idx %= key.length;
        }

        out.write(b, off, len);
    }
    */
}
