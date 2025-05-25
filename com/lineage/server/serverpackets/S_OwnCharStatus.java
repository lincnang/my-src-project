package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;

/**
 * 角色資訊
 *
 * @author dexc
 */
public class S_OwnCharStatus extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 角色資訊
     *
     * @param pc
     */
    public S_OwnCharStatus(final L1PcInstance pc) {
        int time = L1GameTimeClock.getInstance().currentTime().getSeconds();
        time = time - (time % 300);
        // _log.warning((new
        // StringBuilder()).append("送信時間:").append(i).toString());
        writeC(S_STATUS);
        writeD(pc.getId());
        //src039
        if (pc.getLevel() < 1) {
            writeC(1);
        } else {
            writeC(Math.min(pc.getLevel(), 200));
        }
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
        writeD(pc.getAc());// -211～44 value C => D by 7.6tw
        writeD(time);
        writeC(pc.get_food());
        writeC(pc.getInventory().getWeight240());
        writeH(pc.getLawful());
        writeH(pc.getFire());
        writeH(pc.getWater());
        writeH(pc.getWind());
        writeH(pc.getEarth());
        this.writeD(pc.getKillCount());// TODO 3.53C怪物狩獵數量
        writeH(0);
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
        return this.getClass().getSimpleName();
    }
}