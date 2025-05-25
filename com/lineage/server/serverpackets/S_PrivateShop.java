package com.lineage.server.serverpackets;

import com.lineage.server.clientpackets.C_LoginToServer;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.templates.L1PrivateShopBuyList;
import com.lineage.server.templates.L1PrivateShopSellList;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 交易個人商店清單(購買)
 *
 * @author dexc
 */
public class S_PrivateShop extends ServerBasePacket {
    private static final Log _log = LogFactory.getLog(C_LoginToServer.class);

    public S_PrivateShop(L1PcInstance pc, int objectId, int type) {
        L1Object shopObj = World.get().findObject(objectId);
        if ((shopObj instanceof L1PcInstance)) {
            isPc(pc, objectId, type);
        } else if ((shopObj instanceof L1DeInstance)) {
            isDe(pc, objectId, type);
        }
    }

    /**
     * 對象是NPC
     *
     */
    private void isDe(L1PcInstance pc, int objectId, int type) {
        L1DeInstance shopDe = (L1DeInstance) World.get().findObject(objectId);
        if (shopDe == null) {
            return;
        }
        writeC(S_PERSONAL_SHOP_LIST);
        writeC(type);
        writeD(objectId);
        if (type == 0) {
            Map<Integer, L1ItemInstance> sellList = new HashMap<>();
            Map<L1ItemInstance, Integer> map = shopDe.get_sellList();
            if (map.isEmpty()) {
                writeH(0);
                return;
            }
            if (map.size() <= 0) {
                writeH(0);
                return;
            }
            writeH(map.size());
            int i = 0;
            for (L1ItemInstance item : map.keySet()) {
                writeC(i);
                writeD((int) item.getCount());
                int price = (Integer) map.get(item);
                writeD(price);
                writeH(item.getItem().getGfxId());
                writeC(item.getEnchantLevel());
                writeC(1); // 0未鑒定 1鑒定
                writeC(item.getBless());
                String name = item.getNumberedViewName(item.getCount());
                writeS(name);
                //writeC(0);
                if (item.isIdentified()) {
                    long oldCount = item.getCount();
                    item.setCount(1);
                    byte[] status = item.getStatusBytes();
                    item.setCount(oldCount);
                    writeC(status.length);
                    for (byte b : status) {
                        writeC(b);
                    }
                } else {
                    writeC(0);
                }
                sellList.put(i, item);
                i++;
            }
            pc.get_otherList().DELIST.clear();
            pc.get_otherList().DELIST.putAll(sellList);
        } else if (type == 1) {
            Map<Integer, int[]> list = shopDe.get_buyList();
            if (list.isEmpty()) {
                writeH(0);
                return;
            }
            if (list.size() <= 0) {
                writeH(0);
                return;
            }
            writeH(list.size());
            for (Integer integer : list.keySet()) {
                int key = integer;
                int buyitem[] = (int[]) list.get(key);
                int count = buyitem[2];
                int level = buyitem[1];
                int price = buyitem[0];
                int i = 0;
                for (L1ItemInstance pcItem : pc.getInventory().getItems()) {
                    if (pcItem.getItemId() == key && pcItem.getEnchantLevel() == level) {
                        writeC(i);
                        writeD(pcItem.getId());
                        writeD(count);
                        writeD(price);
                        i++;
                    }
                }
            }
        }
    }

    /**
     * 對象是PC
     *
     */
    private void isPc(L1PcInstance pc, int objectId, int type) {
        L1PcInstance shopPc = (L1PcInstance) World.get().findObject(objectId);
        if (shopPc == null) {
            return;
        }
        writeC(S_PERSONAL_SHOP_LIST);
        writeC(type);
        writeD(objectId);
        if (type == 0) {
            ArrayList<L1PrivateShopSellList> list = shopPc.getSellList();
            if (list.isEmpty()) {
                writeH(0);
                return;
            }
            int size = list.size();
            if (size <= 0) {
                writeH(0);
                return;
            }
            pc.setPartnersPrivateShopItemCount(size);
            writeH(size);
            for (int i = 0; i < size; i++) {
                L1PrivateShopSellList pssl = (L1PrivateShopSellList) list.get(i);
                int itemObjectId = pssl.getItemObjectId();
                int count = pssl.getSellTotalCount() - pssl.getSellCount();
                int price = pssl.getSellPrice();
                L1ItemInstance item = shopPc.getInventory().getItem(itemObjectId);
                if (item != null) {
                    writeC(i);
                    writeD(count);
                    writeD(price);
                    writeH(item.getItem().getGfxId());
                    writeC(item.getEnchantLevel());
                    writeC(item.isIdentified() ? 1 : 0);
                    writeC(item.getBless());
                    writeS(item.getNumberedViewName(count));
                    if (item.isIdentified()) {
                        long oldCount = item.getCount();
                        item.setCount(1);
                        byte[] status = item.getStatusBytes();
                        item.setCount(oldCount);
                        writeC(status.length);
                        for (byte b : status) {
                            writeC(b);
                        }
                    } else {
                        writeC(0);
                    }
                }
            }
            writeH(0x00);
        } else if (type == 1) {
            //			ArrayList<?> list = shopPc.getBuyList();
            //
            //			if (list.isEmpty()) {
            //				writeH(0);
            //				return;
            //			}
            //
            //			int size = list.size();
            //
            //			if (size <= 0) {
            //				writeH(0);
            //				return;
            //			}
            //
            //			writeH(size);
            //			for (int i = 0; i < size; i++) {
            //				L1PrivateShopBuyList psbl = (L1PrivateShopBuyList) list.get(i);
            //				int itemObjectId = psbl.getItemObjectId();
            //				int count = psbl.getBuyTotalCount();
            //				int price = psbl.getBuyPrice();
            //				L1ItemInstance item = shopPc.getInventory().getItem(itemObjectId);
            //				for (L1ItemInstance pcItem : pc.getInventory().getItems())
            //					if ((item.getItemId() == pcItem.getItemId())
            //							&& (item.getEnchantLevel() == pcItem.getEnchantLevel())) {
            //						writeC(i);
            //						writeD(pcItem.getId());
            //						writeD(count);
            //						writeD(price);
            //					}
            //			}
            ArrayList<L1PrivateShopBuyList> list = shopPc.getBuyList();
            Map<Integer, L1PrivateShopBuyList> havelist = new HashMap<>();
            // ArrayList<L1ItemInstance> pchavelist = new
            // ArrayList<L1ItemInstance>();
            L1PrivateShopBuyList psbl = null;
            L1ItemInstance item = null;
            int havesize = 0;
            int havelistsize = 0;
            int tempi = 0;
            for (L1PrivateShopBuyList psb : list) {
                item = shopPc.getInventory().getItem(psb.getItemObjectId());
                for (L1ItemInstance pcItem : pc.getInventory().getItems()) {
                    if (!pcItem.isEquipped() && item.getItemId() == pcItem.getItemId() && item.getEnchantLevel() == pcItem.getEnchantLevel() && item.getAttrEnchantLevel() == pcItem.getAttrEnchantLevel() && item.getBless() == pcItem.getBless()) {
                        // pchavelist.add(pcItem);
                        // System.out.println(havesize+" : "
                        // +item.getName());
                        havesize++;
                    }
                }
                if (pc.getInventory().CheckSellPrivateShopItem(item.getItemId(), item.getEnchantLevel(), item.getAttrEnchantLevel(), item.getBless())) {
                    havelist.put(havelistsize, psb);
                    havelistsize++;
                }
            }
            // int size = havelist.size();
            writeH(havesize);
            if (havesize <= 0) {
                return;
            }
            for (int i = 0; i < havelistsize; i++) {
                psbl = havelist.get(i);
                int itemObjectId = psbl.getItemObjectId();
                int count = psbl.getBuyTotalCount();
                int price = psbl.getBuyPrice();
                item = shopPc.getInventory().getItem(itemObjectId);
                // System.out.println(havesize+" : " +item.getName());
                for (L1ItemInstance pcItem : pc.getInventory().getItems()) {
                    try {
                        if (!pcItem.isEquipped() && item.getItemId() == pcItem.getItemId() && item.getEnchantLevel() == pcItem.getEnchantLevel() && item.getAttrEnchantLevel() == pcItem.getAttrEnchantLevel() && item.getBless() == pcItem.getBless()) {
                            writeC(i + tempi);
                            writeD(count);
                            writeD(price);
                            writeD(pcItem.getId());
                            writeC(0x00);
                            tempi++;
                            /*
                             * writeD(pcItem.getId()); writeD(count);
                             * writeD(price);
                             */
                        }
                    } catch (Exception e) {
                        if (shopPc != null) {
                            _log.error("x :" + shopPc.getX() + " y :" + shopPc.getY() + " m :" + shopPc.getMapId() + " itemName =" + item != null ? item.getName() : "NULL");
                        }
                    }
                }
            }
            writeH(0x00);
        }
    }

    public byte[] getContent() {
        return getBytes();
    }
}
