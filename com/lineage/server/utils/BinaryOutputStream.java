package com.lineage.server.utils;

import com.lineage.config.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BinaryOutputStream extends OutputStream {
    private static final String CLIENT_LANGUAGE_CODE = Config.CLIENT_LANGUAGE_CODE;
    static int _baseNumber = 8; // 地圖城堡顯示稅收
    private final ByteArrayOutputStream _bao = new ByteArrayOutputStream();

    public BinaryOutputStream() {
    }

    public void write(int b) throws IOException {
        _bao.write(b);
    }

    public void writeD(int value) {
        _bao.write(value & 0xFF);
        _bao.write(value >> 8 & 0xFF);
        _bao.write(value >> 16 & 0xFF);
        _bao.write(value >> 24 & 0xFF);
    }

    public void writeH(int value) {
        _bao.write(value & 0xFF);
        _bao.write(value >> 8 & 0xFF);
    }

    public void writeC(int value) {
        _bao.write(value & 0xFF);
    }

    public void writeC(int count, long value) { // 地圖城堡顯示稅收
        writeC(count, 0, value);
    }

    public void writeC(int count, int add, long value) { // 地圖城堡顯示稅收
        if (count != 0) {
            writeC(_baseNumber * count + add);
        }
        for (; value / 128L != 0L; value /= 128L) {
            writeC((int) (value % 128L + 128L));
        }
        writeC((int) value);
    }

    public void writeP(int value) {
        _bao.write(value);
    }

    public void writeL(long value) {
        _bao.write((int) (value & 0xFF));
    }

    public void writeF(double org) {
        long value = Double.doubleToRawLongBits(org);
        _bao.write((int) (value & 0xFF));
        _bao.write((int) (value >> 8 & 0xFF));
        _bao.write((int) (value >> 16 & 0xFF));
        _bao.write((int) (value >> 24 & 0xFF));
        _bao.write((int) (value >> 32 & 0xFF));
        _bao.write((int) (value >> 40 & 0xFF));
        _bao.write((int) (value >> 48 & 0xFF));
        _bao.write((int) (value >> 56 & 0xFF));
    }

    public void writeS(String text) {
        try {
            if (text != null) {
                _bao.write(text.getBytes(CLIENT_LANGUAGE_CODE));
            }
        } catch (Exception localException) {
        }
        _bao.write(0);
    }

    public void writeS(int count, String text) { // 地圖城堡顯示稅收
        try {
            writeC(count * 8 + 2);
            if (text != null) {
                writeC(text.getBytes(CLIENT_LANGUAGE_CODE).length);
                _bao.write(text.getBytes(CLIENT_LANGUAGE_CODE));
            } else {
                writeC(0);
            }
        } catch (Exception e) {
        }
    }

    public void writeByte(byte[] text) {
        try {
            if (text != null) {
                _bao.write(text);
            }
        } catch (Exception localException) {
        }
    }

    public int getLength() {
        return _bao.size() + 2;
    }

    public byte[] getBytes() {
        return _bao.toByteArray();
    }
}
