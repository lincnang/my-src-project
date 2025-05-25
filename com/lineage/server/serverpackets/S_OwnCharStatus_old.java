package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;

public class S_OwnCharStatus_old extends ServerBasePacket {
    private byte[] _byte = null;

    public S_OwnCharStatus_old(L1PcInstance pc) {
        int time = L1GameTimeClock.getInstance().currentTime().getSeconds();
        time -= time % 300;
        writeC(S_STATUS);
        writeD(pc.getId());
        writeC(pc.getLevel());
        writeExp(pc.getExp());
        writeC(pc.getStr());
        writeC(pc.getInt());
        writeC(pc.getWis());
        writeC(pc.getDex());
        writeC(pc.getCon());
        writeC(pc.getCha());
        writeH(pc.getCurrentHp());
        writeH(pc.getMaxHp());
        writeH(pc.getCurrentMp());
        writeH(pc.getMaxMp());
        writeC(pc.getAc());
        writeD(time);
        writeC(pc.get_food());
        writeC(pc.getInventory().getWeight240());
        writeH(pc.getLawful());
        writeH(pc.getFire()); // 3.80 更動
        writeH(pc.getWater()); // 3.80 更動
        writeH(pc.getWind()); // 3.80 更動
        writeH(pc.getEarth()); // 3.80 更動
        writeD(pc.getKillCount());
    }

    public S_OwnCharStatus_old(L1PcInstance pc, int str) {
        int time = L1GameTimeClock.getInstance().currentTime().getSeconds();
        time -= time % 300;
        writeC(S_STATUS);
        writeD(pc.getId());
        writeC(pc.getLevel());
        writeExp(pc.getExp());
        writeC(str);
        writeC(pc.getInt());
        writeC(pc.getWis());
        writeC(pc.getDex());
        writeC(pc.getCon());
        writeC(pc.getCha());
        writeH(pc.getCurrentHp());
        writeH(pc.getMaxHp());
        writeH(pc.getCurrentMp());
        writeH(pc.getMaxMp());
        writeC(pc.getAc());
        writeD(time);
        writeC(pc.get_food());
        writeC(pc.getInventory().getWeight240());
        writeH(pc.getLawful());
        writeH(pc.getFire()); // 3.80 更動
        writeH(pc.getWater()); // 3.80 更動
        writeH(pc.getWind()); // 3.80 更動
        writeH(pc.getEarth()); // 3.80 更動
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
 * com.lineage.server.serverpackets.S_OwnCharStatus JD-Core Version: 0.6.2
 */