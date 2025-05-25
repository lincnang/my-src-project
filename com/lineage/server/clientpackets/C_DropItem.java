package com.lineage.server.clientpackets;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOtherSet2;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemRestrictionsTable;
import com.lineage.server.datatables.lock.OtherUserTitleReading;
import com.lineage.server.datatables.sql.ServerGmCommandTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

public class C_DropItem extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            final int itemcount = readD();
            for (int i = 0; i < itemcount; i++) {
                final int x = readH();
                final int y = readH();
                final int objectId = readD();
                int count = readD();
                if (count > Integer.MAX_VALUE) {
                    count = Integer.MAX_VALUE;
                }
                count = Math.max(0, count);
                if (pc.isGhost()) {
                    return;
                }
                // 執行人物不是GM
                if (!ConfigAlt.DORP_ITEM && !pc.isGm()) {
                    // \f1你不能夠放棄此樣物品。
                    pc.sendPackets(new S_ServerMessage(125));
                    return;
                }
                L1ItemInstance item = pc.getInventory().getItem(objectId);
                if (item == null) {
                    return;
                }
                if (item.getCount() <= 0L) {
                    return;
                }
                if ((item.getItemId() == 40308 || item.getItemId() == 44070) && pc.getLevel() < 35) {
                    pc.sendPackets(new S_ServerMessage("預防作弊啟動，35級以下無法做此動作"));
                    return;
                }
                if (item.getItemId() == 80033 && pc.getLevel() < ConfigOtherSet2.R_80033) {
                    pc.sendPackets(new S_ServerMessage("預防作弊啟動，85級以下無法做此動作"));
                    return;
                }
                if (item.getItemId() == 47011 && pc.getLevel() < 52) {
                    return;
                }
                if (!pc.isGm()) {
                    if (!item.getItem().isTradable()) {
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        return;
                    }
                    if (item.get_time() != null) {
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        return;
                    }
                    if (ItemRestrictionsTable.RESTRICTIONS.contains(Integer.valueOf(item.getItemId()))) {
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        return;
                    }
                    if (ServerGmCommandTable.tradeControl.contains(item.getId())) {// 限制轉移物品
                        pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                        return;
                    }
                }
                Object[] petlist = pc.getPetList().values().toArray();
                for (Object petObject : petlist) {
                    if ((petObject instanceof L1PetInstance)) {
                        L1PetInstance pet = (L1PetInstance) petObject;
                        if (item.getId() == pet.getItemObjId()) {
                            pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                            return;
                        }
                    }
                }
                /** [原碼] 結婚系統 */
                if (item.getId() == pc.getQuest().get_step(L1PcQuest.QUEST_MARRY)) { // 以結婚過的戒指(不可丟棄)
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                    return;
                }
                /** End */
                // 取回娃娃
                if (pc.getDoll(item.getId()) != null) {
                    // 1,181：這個魔法娃娃目前正在使用中。
                    pc.sendPackets(new S_ServerMessage(1181));
                    return;
                }
                // 取回娃娃
                if (pc.getDoll2(item.getId()) != null) {
                    // 1,181：這個魔法娃娃目前正在使用中。
                    pc.sendPackets(new S_ServerMessage(1181));
                    return;
                }
                // 取回娃娃
                if (pc.get_power_doll() != null) {
                    if (pc.get_power_doll().getItemObjId() == item.getId()) {
                        // 1,181：這個魔法娃娃目前正在使用中。
                        pc.sendPackets(new S_ServerMessage(1181));
                        return;
                    }
                }
                if (item.isEquipped()) {
                    pc.sendPackets(new S_ServerMessage(125));
                    return;
                }
                if (item.getBless() >= 128) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                    return;
                }
                pc.getInventory().tradeItem(item, count, pc.get_showId(), World.get().getInventory(x, y, pc.getMapId()));
                OtherUserTitleReading.get().add(item.getItem().getName() + "(" + item.getItemId() + ")", item.getId(), 0, count, pc.getId(), "丟棄者:" + pc.getName(), pc.getMapId(), "丟到地面");
                pc.turnOnOffLight();
            }
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_DropItem JD-Core Version: 0.6.2
 */