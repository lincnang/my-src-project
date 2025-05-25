package com.lineage.server.serverpackets;

import com.lineage.config.Config;
import com.lineage.server.model.Instance.L1PcInstance;

import java.io.UnsupportedEncodingException;

public class S_ACTION_UI extends ServerBasePacket {
    public static final int SAFETYZONE = 0xcf; // 安全區域右下顯示死亡懲罰狀態圖示
    public static final int DUNGEON_TIME = 0x23; // 顯示計時地圖剩餘時間
    public static final int[] hextable = {0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x8b, 0x8c, 0x8d, 0x8e, 0x8f, 0x90, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0x9b, 0x9c, 0x9d, 0x9e, 0x9f, 0xa0, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, 0xad, 0xae, 0xaf, 0xb0, 0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf, 0xc0, 0xc1, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xcb, 0xcc, 0xcd, 0xce, 0xcf, 0xd0, 0xd1, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xdb, 0xdc, 0xdd, 0xde, 0xdf, 0xe0, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xeb, 0xec, 0xed, 0xee, 0xef, 0xf0, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe, 0xff};
    private static final String CLIENT_LANGUAGE_CODE = Config.CLIENT_LANGUAGE_CODE;
    private static final String S_ACTION_UI = "S_ACTION_UI";
    private byte[] _byte = null;
    private byte[] attendance = new byte[0];

    /**
     * @param 安全區域右下顯示死亡懲罰狀態圖示
     * @param isOpen           on/off
     **/
    public S_ACTION_UI(int code, boolean isOpen) {
        writeC(S_EXTENDED_PROTOBUF);
        writeC(code);
        switch (code) {
            case SAFETYZONE: {
                writeC(0x01);
                writeC(0x08);
                write7B(isOpen ? 128 : 0);
                writeC(0x10);
                writeC(0x00);
                writeC(0x18);
                writeC(0x00);
                writeH(0);
                break;
            }
        }
    }

    /**
     * 顯示地圖剩餘時間
     *
     * @param pc
     * @param code
     */
    public S_ACTION_UI(L1PcInstance pc, int code) {
        writeC(S_EXTENDED_PROTOBUF);
        writeC(code);
        switch (code) {
            case DUNGEON_TIME:
                writeC(0x03);
                writeC(0x0a);
                // writeC(0x10);
                if (attendance.length > 0) {
                    attendance = null;
                    attendance = new byte[0];
                }
                write1(0x08);
                write1(0x01);
                write1(0x12);
                try {
                    byte[] name = "奇岩/古魯丁地監".getBytes(CLIENT_LANGUAGE_CODE);// $12125
                    write1(name.length);
                    writetext(name);
                } catch (UnsupportedEncodingException e) {
                }
                write1(0x18);
                int outtime = 60 * 60 * 3; // 3小時
                write3(outtime - pc.getRocksPrisonTime());// 奇巖/古魯丁地監
                write1(0x20);
                write3(outtime);
                writeC(attendance.length);
                writeByte(attendance);
                writeC(0x0a);
                if (attendance.length > 0) {
                    attendance = null;
                    attendance = new byte[0];
                }
                write1(0x08);
                write1(0x02);
                write1(0x12);
                try {
                    byte[] name = "象牙塔：炎魔陣營".getBytes(CLIENT_LANGUAGE_CODE);
                    write1(name.length);
                    writetext(name);
                } catch (UnsupportedEncodingException e) {
                }
                write1(0x18);
                int outtime1 = 60 * 60 * 1; // 1小時
                write3(outtime1 - pc.getIvoryTowerTime());// 象牙塔：炎魔陣營
                write1(0x20);
                write3(outtime1);
                writeC(attendance.length);
                writeByte(attendance);
                writeC(0x0a);
                if (attendance.length > 0) {
                    attendance = null;
                    attendance = new byte[0];
                }
                write1(0x08);
                write1(0x0f);
                write1(0x12);
                try {
                    byte[] name = "拉斯塔巴德地監".getBytes(CLIENT_LANGUAGE_CODE);
                    write1(name.length);
                    writetext(name);
                } catch (UnsupportedEncodingException e) {
                }
                write1(0x18);
                int outtime2 = 60 * 60 * 2; // 2小時
                write3(outtime2 - pc.getLastabardTime());// 拉斯塔巴德地監
                write1(0x20);
                write3(outtime2);
                writeC(attendance.length);
                writeByte(attendance);
                writeC(0x0a);
                if (attendance.length > 0) {
                    attendance = null;
                    attendance = new byte[0];
                }
                write1(0x08);
                write1(0xf4);
                write1(0x03);
                write1(0x12);
                try {
                    byte[] name = "精靈墓".getBytes(CLIENT_LANGUAGE_CODE);
                    write1(name.length);
                    writetext(name);
                } catch (UnsupportedEncodingException e) {
                }
                write1(0x18);
                int outtime3 = 60 * 30; // 30分鐘
                write3(outtime3 - pc.getSoulTime());//
                write1(0x20);
                write3(outtime3);
                writeC(attendance.length);
                writeByte(attendance);
                writeH(0);
                break;
        }
    }

    private void write1(int value) {
        byte[] newattendance = new byte[1];
        byte[] newattendance1 = new byte[newattendance.length + attendance.length];
        newattendance[0] = (byte) (value & 0xff);
        // attendance = new byte[attendance.length+1];
        System.arraycopy(attendance, 0, newattendance1, 0, attendance.length);
        System.arraycopy(newattendance, 0, newattendance1, attendance.length, newattendance.length);
        attendance = new byte[newattendance1.length];
        attendance = newattendance1;
    }

    private void write3(int value) {
        long temp = value / 128;
        if (temp > 0) {
            byte[] newattendance = new byte[1];
            byte[] newattendance1 = new byte[newattendance.length + attendance.length];
            newattendance[0] = (byte) (hextable[(int) value % 128]);
            System.arraycopy(attendance, 0, newattendance1, 0, attendance.length);
            System.arraycopy(newattendance, 0, newattendance1, attendance.length, newattendance.length);
            attendance = new byte[newattendance1.length];
            attendance = newattendance1;
            while (temp >= 128) {
                newattendance = new byte[1];
                newattendance1 = new byte[newattendance.length + attendance.length];
                newattendance[0] = (byte) (hextable[(int) value % 128]);
                System.arraycopy(attendance, 0, newattendance1, 0, attendance.length);
                System.arraycopy(newattendance, 0, newattendance1, attendance.length, newattendance.length);
                attendance = new byte[newattendance1.length];
                attendance = newattendance1;
                temp = temp / 128;
            }
            if (temp > 0) {
                newattendance = new byte[1];
            }
            newattendance1 = new byte[newattendance.length + attendance.length];
            newattendance[0] = (byte) ((int) temp);
            System.arraycopy(attendance, 0, newattendance1, 0, attendance.length);
            System.arraycopy(newattendance, 0, newattendance1, attendance.length, newattendance.length);
            attendance = new byte[newattendance1.length];
            attendance = newattendance1;
        } else {
            if (value == 0) {
                byte[] newattendance = new byte[1];
                byte[] newattendance1 = new byte[newattendance.length + attendance.length];
                newattendance[0] = 0;
                System.arraycopy(attendance, 0, newattendance1, 0, attendance.length);
                System.arraycopy(newattendance, 0, newattendance1, attendance.length, newattendance.length);
                attendance = new byte[newattendance1.length];
                attendance = newattendance1;
            } else {
                byte[] newattendance = new byte[2];
                byte[] newattendance1 = new byte[newattendance.length + attendance.length];
                newattendance[0] = (byte) (hextable[(int) value]);
                newattendance[1] = 1;
                System.arraycopy(attendance, 0, newattendance1, 0, attendance.length);
                System.arraycopy(newattendance, 0, newattendance1, attendance.length, newattendance.length);
                attendance = new byte[newattendance1.length];
                attendance = newattendance1;
            }
        }
    }

    private void writetext(byte[] text) {
        byte[] newattendance = new byte[text.length];
        byte[] newattendance1 = new byte[newattendance.length + attendance.length];
        newattendance = text;
        System.arraycopy(attendance, 0, newattendance1, 0, attendance.length);
        System.arraycopy(newattendance, 0, newattendance1, attendance.length, newattendance.length);
        attendance = new byte[newattendance1.length];
        attendance = newattendance1;
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return S_ACTION_UI;
    }
}
