package com.lineage.server.timecontroller.other.ins;

import com.lineage.config.ConfigAutoAll;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 自動補魔 -> 第一組 -> 道具
 */
public class SkillSoundMp1 extends TimerTask {
    private static Logger _log = Logger.getLogger(SkillSoundMp1.class.getName());
    private final L1PcInstance _pc;
    // 精神藥水 年糕 艾草年糕 勇氣貨幣 庫傑的靈藥 金粽子 月餅 福月餅
    int[] ItemId = {40042, 40066, 40067, 40735, 41404, 41412, 41413, 41414};
    int[] MinMp = {ConfigAutoAll.Mp_1_Min, ConfigAutoAll.Mp_2_Min, ConfigAutoAll.Mp_3_Min, ConfigAutoAll.Mp_4_Min, ConfigAutoAll.Mp_5_Min, ConfigAutoAll.Mp_6_Min, ConfigAutoAll.Mp_7_Min, ConfigAutoAll.Mp_8_Min};
    int[] MaxMp = {ConfigAutoAll.Mp_1_Max, ConfigAutoAll.Mp_2_Max, ConfigAutoAll.Mp_3_Max, ConfigAutoAll.Mp_4_Max, ConfigAutoAll.Mp_5_Max, ConfigAutoAll.Mp_6_Max, ConfigAutoAll.Mp_7_Max, ConfigAutoAll.Mp_8_Max};
    int[] GfxId = {190, 190, 190, 190, 190, 190, 190, 190};

    public SkillSoundMp1(final L1PcInstance pc) {
        _pc = pc;
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                if (!_pc.isAutoHpAll()) {
                    return;
                }
                if (_pc.isDead()) {
                    return;
                }
                if (_pc.isParalyzedX()) {
                    return;
                }
                // 是否鬼魂/傳送/商店
                if (_pc.isParalyzedX1(_pc)) {
                    return;
                }
                if (!_pc.getMap().isUsableItem()) {
                    return;
                }
                int min = 0, max = 0, gfx = 0;
                for (int i = 0; i < ItemId.length; i++) {
                    if (ItemId[i] == _pc.getAutoItemId4()) {
                        min = MinMp[i];
                        max = MaxMp[i];
                        gfx = GfxId[i];
                        break;
                    }
                }
                int addmp = min;
                if (max > 0) {
                    addmp += (int) (Math.random() * max);
                }
                // 自動補魔
                if (_pc.isAutoMp1()) {
                    if (((_pc.getMaxMp() / 100) * _pc.getTextMp1()) < _pc.getCurrentMp()) {
                        return;
                    }
                    if (_pc.getInventory().checkItem(_pc.getAutoItemId4())) {
                        if (L1BuffUtil.stopPotion(_pc)) { // 無法使用藥水
                            _pc.sendPackets(new S_SkillSound(_pc.getId(), gfx));
                            _pc.broadcastPacketAll(new S_SkillSound(_pc.getId(), gfx));
                            _pc.setCurrentMp(_pc.getCurrentMp() + addmp);
                            _pc.getInventory().consumeItem(_pc.getAutoItemId4(), 1);
                            _pc.sendPackets(new S_ServerMessage(338, "$1084"));
                            L1BuffUtil.cancelAbsoluteBarrier(_pc);// 解除魔法技能絕對屏障
                        }
                    }
                }
            }
        } catch (final Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}
