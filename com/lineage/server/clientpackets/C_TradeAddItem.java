package com.lineage.server.clientpackets;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemRestrictionsTable;
import com.lineage.server.datatables.sql.ServerGmCommandTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1Trade;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 要求增加交易物品(商店/個人交易)
 *
 * @author L1jJP
 */
public class C_TradeAddItem extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_TradeAddItem.class);

    @Override
    public void start(byte[] decrypt, ClientExecutor client) {
        final L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }
        try {
            read(decrypt);
            if (pc.isGhost()) { // 鬼魂模式
                return;
            }
            if (pc.isDead()) { // 死亡
                return;
            }
            if (pc.isTeleport()) { // 傳送中
                return;
            }
            int itemObjid = readD();
            long itemcount = readD();
            if (itemcount > 2147483647L) {
                itemcount = 2147483647L;
            }
            itemcount = Math.max(0L, itemcount);
            L1ItemInstance item = pc.getInventory().getItem(itemObjid);
            if (pc.getLevel() < ConfigOtherSet2.Trade_Level) {
                pc.sendPackets(new S_ServerMessage("等級未滿" + ConfigOtherSet2.Trade_Level + "級無法交易。"));
                return;
            }
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
                if (item.getBless() >= 128) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                    return;
                }
                if (item.get_time() != null) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                    return;
                }
                if (ItemRestrictionsTable.RESTRICTIONS.contains(item.getItemId())) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                    return;
                }
                if (ServerGmCommandTable.tradeControl.contains(item.getId())) {// 限制轉移物品
                    pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                    return;
                }
            }
            if (item.isEquipped()) {
                pc.sendPackets(new S_ServerMessage(141));
                return;
            }
            Object[] petlist = pc.getPetList().values().toArray();
            for (Object petObject : petlist) {
                if ((petObject instanceof L1PetInstance)) {
                    L1PetInstance pet = (L1PetInstance) petObject;
                    if (item.getId() == pet.getItemObjId()) {
                        pc.sendPackets(new S_ServerMessage(1187));
                        return;
                    }
                }
            }
            if (item.getId() == pc.getQuest().get_step(L1PcQuest.QUEST_MARRY)) { // 以結婚過的戒指(無法交易)
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                return;
            }
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
            L1PcInstance tradingPartner = (L1PcInstance) World.get().findObject(pc.getTradeID());
            if (tradingPartner == null) {
                return;
            }
            if (tradingPartner.getLevel() < ConfigOtherSet2.Trade_Level) {
                pc.sendPackets(new S_ServerMessage("對方等級未滿" + ConfigOtherSet2.Trade_Level + "級無法交易。"));
                return;
            }
            if (pc.getTradeOk()) {
                return;
            }
	        /*if (pc.getTradeOk() || tradingPartner.getTradeOk()) {
	            pc.sendPackets(new S_ServerMessage("不可交易：一方面已經確認交易。"));
	            tradingPartner.sendPackets(new S_ServerMessage("不可交易：一方面已經確認交易。"));
	            return;
	        }*/
            // 容量重量確認
            if (tradingPartner.getInventory().checkAddItem(item, itemcount) != L1Inventory.OK) {
                tradingPartner.sendPackets(new S_ServerMessage(270)); // \f1當你負擔過重時不能交易。
                pc.sendPackets(new S_ServerMessage(271)); // \f1對方攜帶的物品過重，無法交易。
                return;
            }
            L1Trade trade = new L1Trade();
            if (itemcount <= 0L) {
                _log.error("要求增加交易物品傳回數量小於等於0: " + pc.getName() + ":" + pc.getNetConnection().kick());
                return;
            }
            trade.tradeAddItem(pc, itemObjid, itemcount);
        } catch (final Exception e) {
        } finally {
            over();
        }
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
