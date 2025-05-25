package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemSublimationTable;
import com.lineage.server.datatables.lock.CharItemSublimationReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.CharItemSublimation;
import com.lineage.server.templates.L1ItemSublimation;

import java.util.Random;

public class ItemSublimation extends ItemExecutor {

    private ItemSublimation() {
    }

    public static ItemExecutor get() {
        return new ItemSublimation();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance material) {
        final int targetId = data[0];
        final L1ItemInstance target = pc.getInventory().getItem(targetId);

        if (target == null) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }

        final int itemId = target.getItemId();
        if (!ItemSublimationTable.checkItemId(itemId)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }

        CharItemSublimation sub = target.getSublimation();
        L1ItemSublimation subData;
        final int maxLv = ItemSublimationTable.getItemSublimationMaxLv(itemId);
        boolean isUpdate = false;

        if (sub != null) {
            int currentLv = sub.getLv();
            if (currentLv >= maxLv) {
                pc.sendPackets(new S_ServerMessage(166, "階級已達最高等級"));
                return;
            }

            subData = ItemSublimationTable.getItemSublimation(itemId, currentLv + 1);
            if (subData == null) {
                pc.sendPackets(new S_ServerMessage(166, "查無下一階昇華資料"));
                return;
            }

            isUpdate = true;
        } else {
            subData = ItemSublimationTable.getItemSublimation(itemId, 1);
            if (subData == null) {
                pc.sendPackets(new S_ServerMessage(166, "無法取得昇華資料"));
                return;
            }
            sub = new CharItemSublimation();
            sub.set_item_obj_id(target.getId());
            sub.set_item_name(subData.getTitle());
            sub.set_char_obj_id(pc.getId());
            sub.setType(subData.getSublimationType());
        }

        // 類型限制檢查
        if (!checkTypeValid(target, subData.getCheckType())) {
            pc.sendPackets(new S_ServerMessage(166, "該物品無法使用於此裝備"));
            return;
        }

        // 消耗素材
        pc.getInventory().removeItem(material, 1);

        // 成功機率判定
        final Random random = new Random();
        if (random.nextInt(100) + 1 > subData.getRandom()) {
            pc.sendPackets(new S_ServerMessage(166, target.getLogName() + "加值失敗"));
            return;
        }

        // 成功套用能力
        sub.setLv(sub.getLv() + 1);

        applySublimationPower(target, subData); // ✅ 套用各種能力值
        target.setSublimation(sub);

        if (subData.getSuccessGifid() != 0) {
            pc.sendPackets(new S_SkillSound(pc.getId(), subData.getSuccessGifid()));
            pc.broadcastPacketX8(new S_SkillSound(pc.getId(), subData.getSuccessGifid()));
        }

        // 資料儲存
        if (isUpdate) {
            CharItemSublimationReading.get().updateItem(sub);
        } else {
            if (CharItemSublimationReading.get().loadItem(sub.get_item_obj_id()) == null) {
                CharItemSublimationReading.get().storeItem(sub);
            } else {
                CharItemSublimationReading.get().updateItem(sub);
            }
        }
        pc.sendPackets(new S_ItemStatus(target));
        pc.sendPackets(new S_ServerMessage(166, target.getNumberedName(target.getCount(), true, true) + "加值成功"));
    }

    // 🧠 檢查昇華類型是否符合（如武器、防具等）
    private boolean checkTypeValid(final L1ItemInstance item, final int type) {
        if (type == 1 && item.getItem().getType2() != 1) return false; // 武器
        if (type == 2 && item.getItem().getType2() != 2) return false; // 防具
        return true;
    }

    // ✅ 套用昇華能力至物品
    private void applySublimationPower(final L1ItemInstance item, final L1ItemSublimation data) {
        if (data.getDamage() > 0) item.setUpdateDmgModifier((int) data.getDamage());
        if (data.getMagicDmg() > 0) item.setUpdateSp((int) data.getMagicDmg());
        if (data.getDmgChanceHp() > 0) item.setDmgChanceHp(data.getDmgChanceHp());
        if (data.getDmgChanceMp() > 0) item.setDmgChanceMp(data.getDmgChanceMp());
        if (data.getWithstandDmg()) item.setWithstandDmg(true);
        if (data.getWithstandMagic()) item.setWithstandMagic(true);
        if (data.getReturnDmg()) item.setReturnDmg(true);
        if (data.getReturnMagic()) item.setReturnMagic(true);
        if (data.getReturnSkills()) item.setReturnSkills(true);
        if (data.getReturnChanceHp() > 0) item.setReturnChanceHp(data.getReturnChanceHp());
        if (data.getReturnChanceMp() > 0) item.setReturnChanceMp(data.getReturnChanceMp());
    }
}
