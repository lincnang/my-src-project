package com.add.Mobbling;

import com.add.L1Config;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.util.ArrayList;

public class L1MobSpList {
    private static MobblingTimeList _Mob = MobblingTimeList.Mob();
    private final L1PcInstance _pc;
    private final ArrayList<int[]> _MobList = new ArrayList<int[]>();
    private final ArrayList<L1ItemInstance> _MobSellList = new ArrayList<L1ItemInstance>();

    public L1MobSpList(L1PcInstance pc) {
        this._pc = pc;
    }

    public void clear() {
        this._MobList.clear();
        this._MobSellList.clear();
    }

    public void set_copyMob(ArrayList<int[]> invList) {
        clear();
        this._MobList.addAll(invList);
    }

    public ArrayList<int[]> get_MobList() {
        return this._MobList;
    }

    public void addMobItem(int order, int count) {
        int[] MobItem = (int[]) this._MobList.get(order);
        int price = (int) (L1Config._2153 * count);
        checkMobShopItem(MobItem, count, price);
    }

    private void checkMobShopItem(int[] MobItem, int amount, int totalPrice) {
        int price = 0;
        L1ItemInstance priceItem = null;
        priceItem = this._pc.getInventory().findItemId(L1Config._2156);
        if (priceItem != null) {
            price = (int) priceItem.getCount();
        }
        if (totalPrice > price) {
            this._pc.sendPackets(new S_ServerMessage(936));
        } else {
            this._pc.getInventory().removeItem(priceItem, totalPrice);
            L1PcInventory inv = this._pc.getInventory();
            int npcMob1 = MobblingTimeList.Mob().get_npcMob1().getNpcId();
            int npcMob2 = MobblingTimeList.Mob().get_npcMob2().getNpcId();
            int npcMob3 = MobblingTimeList.Mob().get_npcMob3().getNpcId();
            int npcMob4 = MobblingTimeList.Mob().get_npcMob4().getNpcId();
            int npcMob5 = MobblingTimeList.Mob().get_npcMob5().getNpcId();
            int npcMob6 = MobblingTimeList.Mob().get_npcMob6().getNpcId();
            int npcMob7 = MobblingTimeList.Mob().get_npcMob7().getNpcId();
            int npcMob8 = MobblingTimeList.Mob().get_npcMob8().getNpcId();
            int npcMob9 = MobblingTimeList.Mob().get_npcMob9().getNpcId();
            int npcMob10 = MobblingTimeList.Mob().get_npcMob10().getNpcId();
            int itemId = L1Config._2155;
            int MobNo = MobItem[1];
            int MobNpcId = MobItem[0];
            if (_Mob.get_isStart()) {
                return;
            }
            if (_Mob.get_MobId() != MobNo) {
                return;
            }
            if (MobNpcId == npcMob1) {
                MobblingTimeList.Mob().add_npcChip1A((int) (amount * L1Config._2153));
            }
            if (MobNpcId == npcMob2) {
                MobblingTimeList.Mob().add_npcChip2A((int) (amount * L1Config._2153));
            }
            if (MobNpcId == npcMob3) {
                MobblingTimeList.Mob().add_npcChip3A((int) (amount * L1Config._2153));
            }
            if (MobNpcId == npcMob4) {
                MobblingTimeList.Mob().add_npcChip4A((int) (amount * L1Config._2153));
            }
            if (MobNpcId == npcMob5) {
                MobblingTimeList.Mob().add_npcChip5A((int) (amount * L1Config._2153));
            }
            if (MobNpcId == npcMob6) {
                MobblingTimeList.Mob().add_npcChip6A((int) (amount * L1Config._2153));
            }
            if (MobNpcId == npcMob7) {
                MobblingTimeList.Mob().add_npcChip7A((int) (amount * L1Config._2153));
            }
            if (MobNpcId == npcMob8) {
                MobblingTimeList.Mob().add_npcChip8A((int) (amount * L1Config._2153));
            }
            if (MobNpcId == npcMob9) {
                MobblingTimeList.Mob().add_npcChip9A((int) (amount * L1Config._2153));
            }
            if (MobNpcId == npcMob10) {
                MobblingTimeList.Mob().add_npcChip10A((int) (amount * L1Config._2153));
            }
            L1ItemInstance item = ItemTable.get().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            item.setGamNo(MobNo);
            item.setGamNpcId(MobNpcId);
            inv.storeItem(item);
            this._pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 獲得0%。
        }
    }

    public void checkMobShop() {
        clear();
    }

    public void set_copySellMob(ArrayList<L1ItemInstance> invList) {
        clear();
        this._MobSellList.addAll(invList);
    }

    public ArrayList<L1ItemInstance> get_MobSellList() {
        return this._MobSellList;
    }

    public void addSellMobItem(int objid, int count) {
        boolean isOk = false;
        L1ItemInstance MobItem = this._pc.getInventory().getItem(objid);
        for (L1ItemInstance chItem : this._MobSellList) {
            if (chItem == MobItem) {
                isOk = true;
            }
        }
        if (!(isOk)) {
            return;
        }
        int MobId = MobItem.getGamNo();
        int i = 0;
        L1Mobbling MobInfo = MobblingLock.create().getMobbling(MobId);
        if ((MobInfo != null) && (MobInfo.get_npcid() == MobItem.getGamNpcId())) {
            i = (int) (L1Config._2153 * MobInfo.get_rate()) * count;
        }
        checkMobSellItem(MobItem, count, i);
    }

    private void checkMobSellItem(L1ItemInstance MobItem, int count, int amount) {
        if (MobItem == null) {
            return;
        }
        this._pc.getInventory().removeItem(MobItem, count);
        L1PcInventory inv = this._pc.getInventory();
        L1ItemInstance item = null;
        item = ItemTable.get().createItem(L1Config._2156);
        item.setCount(amount);
        inv.storeItem(item);
        this._pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 獲得0%。
    }

    public void checkMobSell() {
        clear();
    }
}