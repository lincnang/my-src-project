package com.lineage.data.item_etcitem.skill;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C3_WIND_Item extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(C3_WIND_Item.class);
    private int _itemid = 44070;
    private int _count = 1;
    private int _time = 60;

    public static ItemExecutor get() {
        return new C3_WIND_Item();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            if (pc.getLevel() < 50) {
                pc.sendPackets(new S_ServerMessage("\\fV50級以下無法使用!"));
                return;
            }
            if (pc.hasSkillEffect(7005)) {
                pc.sendPackets(new S_ServerMessage("\\fV不能與[火轉術]共存!"));
                return;
            }
            if (pc.hasSkillEffect(7006)) {
                pc.sendPackets(new S_ServerMessage("\\fV不能與[水攻術]共存!"));
                return;
            }
            if (pc.hasSkillEffect(7007)) {
                pc.sendPackets(new S_ServerMessage("\\fV[風傷術]效果尚存:" + pc.getSkillEffectTimeSec(7007)));
                return;
            }
            if (pc.hasSkillEffect(7008)) {
                pc.sendPackets(new S_ServerMessage("\\fV不能與[地氣術]共存!"));
                return;
            }
            if (pc.hasSkillEffect(7009)) {
                pc.sendPackets(new S_ServerMessage("\\fR屬性技能尚未冷卻"));
                return;
            }
            if (_itemid != 0) {
                L1ItemInstance ned_item = pc.getInventory().checkItemX(_itemid, _count);
                if (ned_item != null) {
                    pc.getInventory().removeItem(ned_item, _count);
                } else {
                    L1Item tgItem = ItemTable.get().getTemplate(_itemid);
                    pc.sendPackets(new S_ServerMessage(337, tgItem.getNameId()));
                    return;
                }
            }
            pc.setSkillEffect(7009, (_time + 30) * 1000);
            pc.setSkillEffect(7007, _time * 1000);
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 212));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _itemid = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            _count = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
        try {
            _time = Integer.parseInt(set[3]);
        } catch (Exception localException2) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.skill.C3_WIND_Item JD-Core Version: 0.6.2
 */