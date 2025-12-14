package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 恢復盔甲
 *
 * @author dexc
 */
public class PRIDE extends SkillMode {

    private static final Log _log = LogFactory.getLog(PRIDE.class);

    public PRIDE() {
    }

    //@Override

    /**
     * 技能施放者為PC
     **/
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
            throws Exception {
        // 初始化傷害值為0
        final int dmg = 0;
        // 將目標角色 `cha` 強制轉型為玩家角色 `L1PcInstance`
        final L1PcInstance pc = (L1PcInstance) cha;

        synchronized (pc) {
            // 檢查來源角色 `srcpc` 的 ID 是否與目標玩家 `pc` 的 ID 相同（確認是否對自己施放技能）
            if (srcpc.getId() == pc.getId()) {
                // 先移除舊的加成 (如果有)，確保數值不會無限疊加，並允許刷新數值
                if (pc.getPrideHp() > 0) {
                    pc.addMaxHp(-pc.getPrideHp());
                    pc.add_up_hp_potion(-10);
                }
                // 計算冒險者的生命值增益量
                int prideHp = pc.getBaseMaxHp() * pc.getLevel() / 4 / 100;
                if (prideHp <= 0) {
                    prideHp = 1;
                }
                pc.setPrideHp(prideHp);
                // 增加玩家的最大生命值
                pc.addMaxHp(pc.getPrideHp());
                // 發送更新後的生命值訊息給玩家
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.add_up_hp_potion(10); //藥水回復


                // 如果玩家處於隊伍狀態
                if (pc.isInParty()) {
                    // 更新隊伍中的迷你血條資訊
                    pc.getParty().updateMiniHP(pc);
                }
            }

            // 如果來源角色 `srcpc` 是目標玩家 `pc` 本人
            if (srcpc.getId() == pc.getId()) {
                // 為玩家設置 "PRIDE" 技能效果，持續時間為 `integer` 秒（乘以1000轉換為毫秒）
                pc.setSkillEffect(L1SkillId.PRIDE, integer * 1000);
                pc.sendPackets(new S_InventoryIcon(10229, true, 3482, integer));
                pc.sendPackets(new S_ServerMessage("\\fX提升最大體力與藥水恢復(" + integer + "秒)"));
            }
        }

        // 返回傷害值，這裡固定為0
        return dmg;
    }


    //@Override

    /**
     * 技能施放者為NPC
     **/
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
            throws Exception {
        final int dmg = 0;

        return dmg;
    }

    //@Override
    public void start(final L1PcInstance srcpc, final Object obj)
            throws Exception {
        // TODO Auto-generated method stub

    }

    //@Override

    /**
     * 技能效果結束
     **/
    public void stop(final L1Character cha) throws Exception {
        try {
            if (cha instanceof L1PcInstance) {
                final L1PcInstance pc = (L1PcInstance) cha;
                synchronized (pc) {
                    // 如果玩家身上還有 PRIDE 效果，代表是重新施放導致的 stop 調用（或是並發），此時不應移除屬性
                    if (pc.hasSkillEffect(L1SkillId.PRIDE)) {
                        return;
                    }

                    if (pc.getPrideHp() > 0) {
                        pc.addMaxHp(-pc.getPrideHp());
                        pc.add_up_hp_potion(-10); //藥水回復
                        pc.setPrideHp(0);
                    }
                    
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                    if (pc.isInParty()) { // 隊伍狀態
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
