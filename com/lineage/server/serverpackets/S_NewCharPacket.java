package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

import java.text.SimpleDateFormat;

public class S_NewCharPacket extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NewCharPacket(L1PcInstance pc) {
        buildPacket(pc);
    }

    private void buildPacket(L1PcInstance pc) {
        writeC(S_NEW_CHAR_INFO);
        writeS(pc.getName());
        writeS("");
        writeC(pc.getType());
        writeC(pc.get_sex());
        writeH(pc.getLawful());
        writeH(pc.getMaxHp());
        writeH(pc.getMaxMp());
        writeC(pc.getAc());
        writeC(pc.getLevel());
        writeC(pc.getStr());
        writeC(pc.getDex());
        writeC(pc.getCon());
        writeC(pc.getWis());
        writeC(pc.getCha());
        writeC(pc.getInt());
        writeC(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int time = Integer.parseInt(sdf.format(Long.valueOf(System.currentTimeMillis())).replace("-", ""));
        String times = Integer.toHexString(time);
        if (times.length() < 8) {
            times = "0" + times;
        }
        writeC(Integer.decode("0x" + times.substring(6, 8)).intValue());
        writeC(Integer.decode("0x" + times.substring(4, 6)).intValue());
        writeC(Integer.decode("0x" + times.substring(2, 4)).intValue());
        writeC(Integer.decode("0x" + times.substring(0, 2)).intValue());
        int checkcode = pc.getLevel() ^ pc.getStr() ^ pc.getDex() ^ pc.getCon() ^ pc.getWis() ^ pc.getCha() ^ pc.getInt();
        writeC(checkcode & 0xFF);
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_NewCharPacket JD-Core Version: 0.6.2
 */