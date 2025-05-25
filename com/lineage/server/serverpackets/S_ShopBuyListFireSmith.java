package com.lineage.server.serverpackets;

import com.add.system.L1FireCrystal;
import com.add.system.L1FireSmithCrystalTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;

import java.util.HashMap;
import java.util.Map;

public class S_ShopBuyListFireSmith extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ShopBuyListFireSmith(L1PcInstance pc, L1NpcInstance npc) {
        Map<L1ItemInstance, Integer> assessedItems = assessItems(pc.getInventory());
        if (assessedItems.isEmpty()) {
            pc.sendPackets(new S_NPCTalkReturn(pc, "smithitem3"));
            return;
        }
        if (assessedItems.size() <= 0) {
            pc.sendPackets(new S_NoSell(npc));
            return;
        }
        writeC(S_SELL_LIST);
        writeD(npc.getId());
        writeH(assessedItems.size());
        for (L1ItemInstance key : assessedItems.keySet()) {
            writeD(key.getId());
            writeD(((Integer) assessedItems.get(key)).intValue());
        }
        writeH(0x0000); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:天寶
    }

    private Map<L1ItemInstance, Integer> assessItems(L1PcInventory inv) {
        Map<L1ItemInstance, Integer> result = new HashMap<L1ItemInstance, Integer>();
        for (L1ItemInstance item : inv.getItems()) {
            switch (item.getItem().getItemId()) {
                case 40308: // 金幣
                case 41246: // 魔法結晶體
                case 44070: // 天寶
                case 40314: // 項圈
                case 40316: // 高等寵物項圈
                case 83000: // 貝利
                case 83022: // 黃金貝利
                case 80033: // 推廣銀幣
                case 80048: // 龍之幣
                    break;
                default:
                    if ((!item.isEquipped()) && (item.getItem().getType2() != 0)) {
                        //有時間性的
                        if (item.get_time() != null) {
                            continue;
                        }
                        int key = item.getItemId();
                        if (item.getBless() == 0) {// 祝福狀態
                            key = item.getItemId() - 100000;
                        } else if (item.getBless() == 2) {// 詛咒狀態
                            key = item.getItemId() - 200000;
                        }
                        L1FireCrystal firecrystal = L1FireSmithCrystalTable.get().getTemplate(key);
                        if (firecrystal != null) {
                            int crystalcount = firecrystal.get_CrystalCount(item);// 傳回熔煉後可獲得火神結晶數量
                            if (crystalcount > 0) {
                                result.put(item, crystalcount);
                            }
                        }
                    }
                    break;
            }
        }
        return result;
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_ShopBuyListAll JD-Core Version: 0.6.2
 */