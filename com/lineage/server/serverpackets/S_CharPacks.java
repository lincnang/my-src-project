package com.lineage.server.serverpackets;

/**
 * 角色資訊
 *
 * @author dexc
 */
public class S_CharPacks extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 角色資訊
     *
     */
    public S_CharPacks(String name, String clanName, int type, int sex, int lawful, int hp, int mp, int ac, int lv, int str, int dex, int con, int wis, int cha, int intel, int time) {
        writeC(S_CHARACTER_INFO);
        writeS(name);
        writeS(clanName);
        writeC(type);
        writeC(sex);
        writeH(lawful);
        writeH(hp);
        writeH(mp);
        if (ac > 10) {
            writeC(10);
        } else {
            writeC(ac);
        }
        if (lv > 200)  //src039
        {
            writeC(200);
        } else {
            writeC(lv);
        }
        if (str > 127) {
            writeC(127);
        } else {
            writeC(str);
        }
        if (dex > 127) {
            writeC(127);
        } else {
            writeC(dex);
        }
        if (con > 127) {
            writeC(127);
        } else {
            writeC(con);
        }
        if (wis > 127) {
            writeC(127);
        } else {
            writeC(wis);
        }
        if (cha > 127) {
            writeC(127);
        } else {
            writeC(cha);
        }
        if (intel > 127) {
            writeC(127);
        } else {
            writeC(intel);
        }
        writeC(0);
        String times = Integer.toHexString(time);
        if (times.length() < 8) {
            times = "0" + times;
        }
        writeC(Integer.decode("0x" + times.substring(6, 8)).intValue());
        writeC(Integer.decode("0x" + times.substring(4, 6)).intValue());
        writeC(Integer.decode("0x" + times.substring(2, 4)).intValue());
        writeC(Integer.decode("0x" + times.substring(0, 2)).intValue());
        if (lv > 200) {  //src039
            lv = 200;
        }
        int checkcode = lv ^ str ^ dex ^ con ^ wis ^ cha ^ intel;
        writeC(checkcode & 0xFF);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
