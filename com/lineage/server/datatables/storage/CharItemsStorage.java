package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract interface CharItemsStorage {
    public abstract void load();

    public abstract CopyOnWriteArrayList<L1ItemInstance> loadItems(Integer paramInteger);

    public abstract void delUserItems(Integer paramInteger);

    public abstract boolean getUserItems(Integer paramInteger, int paramInt, long paramLong);

    public abstract boolean getUserItem(int paramInt);

    public abstract void del_item(int paramInt);

    public abstract void storeItem(int paramInt, L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void deleteItem(int paramInt, L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemId_Name(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemId(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemCount(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemDurability(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemChargeCount(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemRemainingTime(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemEnchantLevel(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemEquipped(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemIdentified(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemBless(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemAttrEnchantKind(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemAttrEnchantLevel(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract void updateItemDelayEffect(L1ItemInstance paramL1ItemInstance) throws Exception;

    public abstract int getItemCount(int paramInt) throws Exception;

    public abstract void getAdenaCount(int paramInt, long paramLong) throws Exception;

    /**
     * [原碼] 怪物對戰系統
     */
    public abstract void updateGamNo(L1ItemInstance item) throws Exception;

    public abstract void updateGamNpcId(L1ItemInstance item) throws Exception;

    /**
     * [原碼] 大樂透系統
     */
    public abstract void updateStarNpcId(L1ItemInstance item) throws Exception;

    public abstract Map<Integer, L1ItemInstance> getUserItems(int paramInt);

    public abstract int checkItemId(int paramInt);

    public void updateItemAttack(final L1ItemInstance item) throws Exception;

    public void updateItemBowAttack(final L1ItemInstance item) throws Exception;

    public void updateItemReductionDmg(final L1ItemInstance item) throws Exception;

    public void updateItemSp(final L1ItemInstance item) throws Exception;

    public void updateItemprobability(final L1ItemInstance item) throws Exception;

    public void updateItemStr(final L1ItemInstance item) throws Exception;

    public void updateItemDex(final L1ItemInstance item) throws Exception;

    public void updateItemInt(final L1ItemInstance item) throws Exception;

    public void updateItemHp(final L1ItemInstance item) throws Exception;

    public void updateItemMp(final L1ItemInstance item) throws Exception;

    public void updateItemCon(final L1ItemInstance item) throws Exception;

    public void updateItemWis(final L1ItemInstance item) throws Exception;

    public void updateItemCha(final L1ItemInstance item) throws Exception;

    public abstract void updateItemCharTrade(L1ItemInstance tradeItem) throws Exception;
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.CharItemsStorage JD-Core Version: 0.6.2
 */