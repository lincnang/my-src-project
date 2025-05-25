package com.lineage.data.item_etcitem.poweritem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EnchantProtect extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(EnchantProtect.class);
    private int _type = 0;

    public static ItemExecutor get() {
        return new EnchantProtect();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            int targObjId = data[0];
            L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
            if (tgItem == null) {
                return;
            }
            boolean isErroe = false;
            switch (tgItem.getItem().getUseType()) {
                case 1:
                case 2:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 25:
                    break;
                default:
                    isErroe = true;
                    break;
            }
            if (isErroe) {
                pc.sendPackets(new S_ServerMessage("武器或防具才能受到保護。"));
                return;
            }
            if ((tgItem.get_hasProtect()) > 0) {
                pc.sendPackets(new S_ServerMessage("+" + tgItem.getEnchantLevel() + " " + tgItem.getName() + " 已是保護狀態。"));
                return;
            }
            if (tgItem.getEnchantLevel() < tgItem.getItem().get_safeenchant()) {
                pc.sendPackets(new S_ServerMessage("+" + tgItem.getEnchantLevel() + " " + tgItem.getName() + " 還在安定值內，不需要保護。"));
                return;
            }
            if (tgItem.getItem().get_safeenchant() < 0) {
                pc.sendPackets(new S_ServerMessage("此類型道具無法保護。"));
            }
            if (tgItem.get_hasProtect() == 0) {
                int use_type = tgItem.getItem().getUseType();
                switch (use_type) {
                    case 1:
                    case 2:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 25:
                        switch (this._type) {
                            case 1:
                                tgItem.set_hasProtect(1);
                                break;
                            case 2:
                                tgItem.set_hasProtect(2);
                                break;
                            case 3:
                                tgItem.set_hasProtect(3);
                        }
                        pc.sendPackets(new S_ItemName(tgItem));
                        pc.sendPackets(new S_ItemStatus(tgItem));
                        pc.sendPackets(new S_ServerMessage(tgItem.getLogName() + " 已受到保護。"));
                        pc.getInventory().removeItem(item, 1L);
                        WriteLogTxt.Recording("保護裝備紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + ", (ObjectId: " + tgItem.getId() + ")】 使用【" + item.getRecordName(item.getCount()) + "】, " + "(ObjectId: " + tgItem.getId() + ").");
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            this._type = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}
