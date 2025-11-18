package com.lineage.server.timecontroller.other.ins;

import com.lineage.config.ConfigAutoAll;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_YouFeelBetter;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 自動喝藥 -> 第一組
 */
public class SkillSoundHp1 extends TimerTask {
    private static Logger _log = Logger.getLogger(SkillSoundHp1.class.getName());
    private final L1PcInstance _pc;
    // 治愈藥水 強力治愈藥水 終極治愈藥水 濃縮體力恢復劑 濃縮強力體力恢復劑 濃縮終極體力恢復劑 古代體力恢復劑 古代強力體力恢復劑 古代終極體力恢復劑 兔子的肝
    int[] ItemId = {40010, 40011, 40012, 40019, 40020, 40021, 40022, 40023, 40024, 40043};
    int[] MinHp = {ConfigAutoAll.Hp_1_Min, ConfigAutoAll.Hp_2_Min, ConfigAutoAll.Hp_3_Min, ConfigAutoAll.Hp_4_Min, ConfigAutoAll.Hp_5_Min, ConfigAutoAll.Hp_6_Min, ConfigAutoAll.Hp_7_Min, ConfigAutoAll.Hp_8_Min, ConfigAutoAll.Hp_9_Min, ConfigAutoAll.Hp_10_Min};
    int[] MaxHp = {ConfigAutoAll.Hp_1_Max, ConfigAutoAll.Hp_2_Max, ConfigAutoAll.Hp_3_Max, ConfigAutoAll.Hp_4_Max, ConfigAutoAll.Hp_5_Max, ConfigAutoAll.Hp_6_Max, ConfigAutoAll.Hp_7_Max, ConfigAutoAll.Hp_8_Max, ConfigAutoAll.Hp_9_Max, ConfigAutoAll.Hp_10_Max};
    int[] GfxId = {189, 194, 197, 189, 194, 197, 189, 194, 197, 189};

    public SkillSoundHp1(final L1PcInstance pc) {
        _pc = pc;
    }

    @Override
    public void run() {
        try {
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
            if (!_pc.isAutoHpAll()) {
                return;
            }
            if (!_pc.isAutoHp1()) {
                return;
            }
            // 防止 一 二 組 同時喝藥
            if (_pc.isAutoHp2() && _pc.getInventory().checkItem(_pc.getAutoItemId2()) && ((_pc.getMaxHp() / 100) * _pc.getTextHp2()) > _pc.getCurrentHp()) {
                return;
            }
            synchronized (this) {
                // 第一組自動喝藥
                if (_pc.isAutoHp1()) {
                    int min = 0, max = 0, gfx = 0;
                    for (int i = 0; i < ItemId.length; i++) {
                        if (ItemId[i] == _pc.getAutoItemId1()) {
                            min = MinHp[i];
                            max = MaxHp[i];
                            gfx = GfxId[i];
                            break;
                        }
                    }
                    int addhp = min;
                    if (max > 0) {
                        addhp += (int) (Math.random() * max);
                    }
                    // 藥水使用HP恢復增加判斷
                    if (_pc.get_up_hp_potion() > 0) {
                        addhp += addhp * _pc.get_up_hp_potion() / 100;
                        addhp += _pc.get_uhp_number();
                    }
                    // 負面治愈判斷
                    if (_pc.hasSkillEffect(POLLUTE_WATER)) { // 污濁之水
                        addhp >>= 1;
                    }
                    if (_pc.hasSkillEffect(DESPERADO)) { // 亡命之徒
                        addhp >>= 1;
                    }
                    if (_pc.hasSkillEffect(ADLV80_2_2)) { // 污濁的水流(水龍副本 回覆量1/2倍)
                        addhp >>= 1;
                    }
                    if (_pc.hasSkillEffect(ADLV80_2_1)) { // 藥水侵蝕術(水龍副本 治療變為傷害)
                        addhp *= -1;
                    }
                    if (((_pc.getMaxHp() / 100) * _pc.getTextHp1()) < _pc.getCurrentHp()) {
                        return;
                    }
                    if (_pc.getInventory().checkItem(_pc.getAutoItemId1())) {
                        if (L1BuffUtil.stopPotion(_pc)) { // 無法使用藥水
                            _pc.sendPackets(new S_SkillSound(_pc.getId(), gfx));
                            _pc.broadcastPacketAll(new S_SkillSound(_pc.getId(), gfx));
                            _pc.setCurrentHp(_pc.getCurrentHp() + addhp);
                            _pc.getInventory().consumeItem(_pc.getAutoItemId1(), 1);
                            if (ConfigAutoAll.Auto_Hp_Msg) {
                                if (addhp > 0) {
                                    _pc.sendPackets(new S_YouFeelBetter());
                                }
                            }
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
