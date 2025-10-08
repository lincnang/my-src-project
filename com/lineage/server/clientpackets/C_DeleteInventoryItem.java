package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.lock.OtherUserTitleReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C_DeleteInventoryItem extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_DeleteInventoryItem.class);

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            final int itemcount = readD();
            for (int i = 0; i < itemcount; i++) {
                final int itemObjectId = readD();
                final L1ItemInstance item = pc.getInventory().getItem(itemObjectId);
                // 物品為空
                if (item == null) {
                    return;
                }
                if (item.getItemId() == 20000) {
                    return;
                }
                long itemCount = this.readD();
                if (itemCount <= 0) {
                    itemCount = item.getCount();
                }
                if (itemCount > item.getCount()) {
                    _log.info("人物:" + pc.getName() + " 刪除物品數量超過物品本身數量:(" + itemCount + " > " + item.getCount() + ")");
                    return;
                }
                // 執行人物不是GM
                if (!pc.isGm()) {
                    if (item.getItem().isCantDelete()) {
                        // 125 \f1你不能夠放棄此樣物品。
                        pc.sendPackets(new S_ServerMessage(125));
                        return;
                    }
                }
                Object[] petlist = pc.getPetList().values().toArray();
                for (Object petObject : petlist) {
                    if ((petObject instanceof L1PetInstance)) {
                        L1PetInstance pet = (L1PetInstance) petObject;
                        if (item.getId() == pet.getItemObjId()) {
                            pc.sendPackets(new S_ServerMessage(125));
                            return;
                        }
                    }
                }
                /** [原碼] 結婚系統 */
                if (item.getId() == pc.getQuest().get_step(L1PcQuest.QUEST_MARRY)) { // 以結婚過的戒指(無法刪除)
                    // \f1你不能夠放棄此樣物品。
                    pc.sendPackets(new S_ServerMessage(125));
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
                    // 125 \f1你不能夠放棄此樣物品。
                    pc.sendPackets(new S_ServerMessage(125));
                    return;
                }
                if (item.getBless() >= 128) { // 封印的裝備
                    // 210 \f1%0%d是不可轉移的…
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                    return;
                }
                // 個人刪除物品紀錄
//                _log.info("人物:" + pc.getName() + "刪除物品" + item.getItem().getName() + " 物品OBJID:" + item.getId());
                WriteLogTxt.Recording("物品刪除紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】刪除物品:【" + item.getRecordName(item.getCount()) + "】個  物品OBJID:" + item.getId() + "】.");
                OtherUserTitleReading.get().add(item.getItem().getName() + "(" + item.getItemId() + ")", item.getId(), 0, item.getCount(), pc.getId(), "刪除者:" + pc.getName(), pc.getMapId(), "刪除物品");
                pc.getInventory().removeItem(item, itemCount);
                //pc.turnOnOffLight();
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
