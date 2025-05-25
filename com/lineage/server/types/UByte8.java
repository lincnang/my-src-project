package com.lineage.server.types;

public class UByte8 {
    public static byte[] fromArray(long[] buff) {
        byte[] byteBuff = new byte[buff.length * 4];
        for (int i = 0; i < buff.length; i++) {
            byteBuff[(i * 4)] = ((byte) (int) (buff[i] & 0xFF));
            byteBuff[(i * 4 + 1)] = ((byte) (int) (buff[i] >> 8 & 0xFF));
            byteBuff[(i * 4 + 2)] = ((byte) (int) (buff[i] >> 16 & 0xFF));
            byteBuff[(i * 4 + 3)] = ((byte) (int) (buff[i] >> 24 & 0xFF));
        }
        return byteBuff;
    }

    public static byte[] fromArray(char[] buff) {
        byte[] byteBuff = new byte[buff.length];
        for (int i = 0; i < buff.length; i++) {
            byteBuff[i] = ((byte) (buff[i] & 0xFF));
        }
        return byteBuff;
    }

    public static byte fromUChar8(char c) {
        return (byte) (c & 0xFF);
    }

    public static byte[] fromULong32(long l) {
        byte[] byteBuff = new byte[4];
        byteBuff[0] = ((byte) (int) (l & 0xFF));
        byteBuff[1] = ((byte) (int) (l >> 8 & 0xFF));
        byteBuff[2] = ((byte) (int) (l >> 16 & 0xFF));
        byteBuff[3] = ((byte) (int) (l >> 24 & 0xFF));
        return byteBuff;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.types.UByte8 JD-Core Version: 0.6.2
 */