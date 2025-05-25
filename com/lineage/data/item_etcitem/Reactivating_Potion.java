package com.lineage.data.item_etcitem;

import com.lineage.config.ConfigTurn;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.*;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 轉生藥水
 */
public class Reactivating_Potion extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Reactivating_Potion.class);

    public static ItemExecutor get() {
        return new Reactivating_Potion();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.getMeteLevel() >= ConfigTurn.METE_MAX_COUNT) {
            pc.sendPackets(new S_ServerMessage("超過允許轉生上限: [" + ConfigTurn.METE_MAX_COUNT + "次]"));
            return;
        }
        if (pc.getLevel() < ConfigTurn.METE_LEVEL) {
            pc.sendPackets(new S_ServerMessage("等級太低以至於無法轉生。"));
            return;
        }
        pc.getInventory().removeItem(item, 1L);
        int pcObjid = pc.getId();
        int randomHp = pc.getMaxHp() * ConfigTurn.METE_REMAIN_HP / 100;
        int randomMp = pc.getMaxMp() * ConfigTurn.METE_REMAIN_MP / 100;
        pc.setExp(1L);
        pc.onChangeExp();
        pc.resetLevel();
        pc.setHighLevel(1);
        pc.setBonusStats(0);
        pc.setMeteLevel(pc.getMeteLevel() + 1);
        pc.resetBaseAc();
        pc.resetBaseMr();
        pc.resetBaseHitup();
        pc.resetBaseDmgup();
        pc.addBaseMaxHp((short) randomHp);
        pc.addBaseMaxMp((short) randomMp);
        pc.setCurrentHp(pc.getMaxHp());
        pc.setCurrentMp(pc.getMaxMp());
        pc.sendPacketsX8(new S_SkillSound(pcObjid, 191));
        //pc.resetMeteAbility();
        pc.resetMeteAbility();
        if (pc.getMeteAbility() != null && pc.getMeteAbility().getGiftBox() != 0) {
            CreateNewItem.createNewItem(pc, pc.getMeteAbility().getGiftBox(), 1);
        }
        if (ConfigTurn.ReiPoint && pc.getTurnLifeSkillCount() < ConfigTurn.ReiPointUp) { // 轉生天賦
            String[] mete = ConfigTurn.ReiPoinMete.split(",");
            int level = pc.getMeteLevel() - 1;
            if (level <= 0) {
                level = 0;
            }
            pc.setTurnLifeSkillCount(pc.getTurnLifeSkillCount() + Integer.valueOf(mete[level]).intValue());
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_ServerMessage(822));
        L1Teleport.teleport(pc, 33441, 32809, (short) 4, pc.getHeading(), true);
        // 轉生是否會公告？ True=是, False=否
        if (ConfigTurn.ReincarnationBroad) {
            World.get().broadcastPacketToAll(new S_ServerMessage("\\aH 恭喜玩家【" + pc.getName() + "】"));
        }
        World.get().broadcastPacketToAll(new S_ServerMessage("\\aH 突破萬難，轉生 " + pc.getMeteLevel() + " 次了！"));
        // 轉生是否會公告？ True=是, False=否
        if (ConfigTurn.ReincarnationBroad) {
            World.get().broadcastPacketToAll(new S_PacketBoxGree("\\fV 恭喜玩家 " + pc.getName() + "\\fV  突破萬難，轉生 " + pc.getMeteLevel() + "\\fV 次了！"));
        }
        try {
            pc.save();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
