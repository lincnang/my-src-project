package com.lineage.echo.encryptions;

public class Cipher {
    private final static int _1 = 0x9c30d539;
    private final static int _2 = 0x930fd7e2;
    private final static int _3 = 0x7c72e993;
    private final static int _4 = 0x287effc3;
    private final byte[] eb = new byte[8];
    private final byte[] db = new byte[8];
    private final byte[] hb = new byte[256];
    private final byte[] ch = new byte[256];

    public void initKeys(final int key) {
        byte t = 0;
        int temp = 0;
        final int[] keys = {key ^ _1, _2};
        keys[0] = Integer.rotateLeft(keys[0], 0x13);
        keys[1] ^= keys[0] ^ _3;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                final byte data = ((byte) ((keys[i] >> (j * 8)) & 0xFF));
                db[((i * 4) + j)] = data;
                eb[((i * 4) + j)] = data;
            }
        }
        for (int i = 0; i < 256; i++) {
            hb[i] = ((byte) i);
        }
        for (int j = 0; j < 256; j++) {
            temp = (hb[j] + temp + eb[(j % 8)]) & 0xFF;
            t = hb[temp];
            hb[temp] = hb[j];
            hb[j] = t;
        }
        System.arraycopy(hb, 0, ch, 0, 256);
    }

    public byte[] encryptHash(final byte[] data) {
        final int length = data.length + 1;
        int b = 0, c = 0;
        byte d = 0;
        for (int a = 1; a < length; ++a) {
            b += hb[a & 0xff];
            b &= 0xff;
            c = a & 0xff;
            d = hb[c];
            hb[c] = hb[b];
            hb[b] = d;
            data[a - 1] ^= hb[(hb[b] + hb[c]) & 0xff];
        }
        return data;
    }

    public byte[] decryptClient(final byte[] data) {
        data[0] = ((byte) (data[0] ^ (db[5] ^ data[1])));
        data[1] = ((byte) (data[1] ^ (db[4] ^ data[2])));
        data[2] = ((byte) (data[2] ^ (db[3] ^ data[3])));
        data[3] = ((byte) (data[3] ^ db[2]));
        int length = data.length;
        for (int i = length - 1; i >= 1; i--) {
            data[i] = ((byte) (data[i] ^ (data[(i - 1)] ^ db[(i & 0x7)])));
        }
        data[0] = ((byte) (data[0] ^ db[0]));
        length -= 4;
        final byte[] temp = new byte[length];
        System.arraycopy(data, 4, temp, 0, length);
        update(db, temp);
        return temp;
    }

    public byte[] encryptClient(final byte[] data) {
        final byte[] nd = new byte[data.length + 4];
        System.arraycopy(data, 0, nd, 4, data.length);
        nd[0] ^= eb[0];
        for (int i = 1; i < nd.length; i++) {
            nd[i] ^= nd[i - 1] ^ eb[i & 7];
        }
        nd[3] ^= eb[2];
        nd[2] ^= eb[3] ^ nd[3];
        nd[1] ^= eb[4] ^ nd[2];
        nd[0] ^= eb[5] ^ nd[1];
        update(eb, data);
        return nd;
    }

    public byte[] decryptServer(final byte[] data) {
        final int length = data.length + 1;
        int b = 0;
        int c = 0;
        int d = 0;
        for (int a = 1; a < length; a++) {
            b = a & 0xFF;
            c += ch[b];
            c &= 255;
            d = ch[b];
            ch[b] = ch[c];
            ch[c] = ((byte) (d & 0xFF));
            final int i = (a - 1);
            data[i] = ((byte) (data[i] ^ ch[((ch[b] + ch[c]) & 0xFF)]));
        }
        return data;
    }

    private void update(final byte[] data, final byte[] ref) {
        for (int i = 0; i < 4; i++) {
            data[i] ^= ref[i];
        }
        final int int32 = (((data[7] & 0xFF) << 24) | ((data[6] & 0xFF) << 16) | ((data[5] & 0xFF) << 8) | (data[4] & 0xFF)) + _4;
        for (int i = 0; i < 4; i++) {
            data[i + 4] = (byte) ((int32 >> (i * 8)) & 0xff);
        }
    }
}
