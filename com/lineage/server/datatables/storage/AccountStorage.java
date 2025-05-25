package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Account;

import java.util.ArrayList;

public abstract interface AccountStorage {
    public abstract void load();

    public abstract boolean isAccountUT(String paramString);

    public abstract L1Account create(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5);

    public abstract boolean isAccount(String paramString);

    public abstract L1Account getAccount(String paramString);

    public abstract void updateWarehouse(String paramString, int paramInt);

    public abstract void updateLastActive(L1Account paramL1Account);

    public abstract void updateCharacterSlot(String paramString, int paramInt);

    public abstract void updatePwd(String paramString1, String paramString2);

    public abstract void updateLan(String paramString, boolean paramBoolean);

    public abstract void updateLan();

    public abstract void updateAccessLevel(String paramString1);

    public void updatefp(final String loginName, final int fp);

    public void updatetam(final String loginName, final int tam_point); // 成長果實系統(Tam幣)

    /**
     * 更新帳號首儲狀態
     */
    public void updateFirstPay(final String loginName, final int count);

    public boolean updaterecharBind(int get_char_objId);

    public ArrayList<String> loadCharacterItems(int get_char_objId);

    public void updateVip(L1PcInstance pc);
}
