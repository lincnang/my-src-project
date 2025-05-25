package com.lineage.echo.encryptions;

/**
 * 封包資料解密(封包監控用)
 *
 * @author DarkNight
 */
public class PacketPrint {
    private static PacketPrint _data;

    public static PacketPrint get() {
        if (_data == null) {
            _data = new PacketPrint();
        }
        return _data;
    }

    /**
     * <font color=#0000ff>印出封包</font> 目的:<BR>
     * 用於檢查客戶端傳出的封包資料<BR>
     *
     */
    public String printData(final byte[] data, final int len) {
        final StringBuilder result = new StringBuilder();
        int counter = 0;
        for (int i = 0; i < len; i++) {
            if (counter % 16 == 0) {
                result.append(this.fillHex(i, 4)).append(": ");
            }
            result.append(this.fillHex(data[i] & 0xff, 2)).append(" ");
            counter++;
            if (counter == 16) {
                result.append("   ");
                int charpoint = i - 15;
                for (int a = 0; a < 16; a++) {
                    final int t1 = data[charpoint++];
                    if ((t1 > 0x1f) && (t1 < 0x80)) {
                        result.append((char) t1);
                    } else {
                        result.append('.');
                    }
                }
                result.append("\n");
                counter = 0;
            }
        }
        final int rest = data.length % 16;
        if (rest > 0) {
            for (int i = 0; i < 17 - rest; i++) {
                result.append("   ");
            }
            int charpoint = data.length - rest;
            for (int a = 0; a < rest; a++) {
                final int t1 = data[charpoint++];
                if ((t1 > 0x1f) && (t1 < 0x80)) {
                    result.append((char) t1);
                } else {
                    result.append('.');
                }
            }
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * <font color=#0000ff>將數字轉成 16 進位</font>
     *
     */
    private String fillHex(final int data, final int digits) {
        StringBuilder number = new StringBuilder(Integer.toHexString(data));
        for (int i = number.length(); i < digits; i++) {
            number.insert(0, "0");
        }
        return number.toString();
    }
}
