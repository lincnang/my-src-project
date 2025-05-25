package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1CharaterTrade;

import java.util.Collection;

public interface CharaterTradeStorage {
    /**
     * 初始化加載
     */
    public abstract void load();

    /**
     * 獲取所有角色交易
     */
    public abstract Collection<L1CharaterTrade> getAllCharaterTradeValues();

    /**
     * 獲取編號
     */
    public abstract int get_nextId();

    /**
     * 增加角色交易
     */
    public abstract boolean addCharaterTrade(final L1CharaterTrade charaterTrade);

    /**
     * 更新角色交易
     * <br>
     * state 0:普通 1:已交易未領取 2:已交易已領取 3:已撤銷
     */
    public abstract void updateCharaterTrade(final L1CharaterTrade charaterTrade, final int state);

    /**
     * 加載當前賬號內的所有角色不包括自己
     */
    public abstract void loadCharacterName(final L1PcInstance pc);

    /**
     * 更新人物的綁定狀態
     *
     * @param objId
     * @return
     */
    public abstract boolean updateBindChar(final int objId, final int state);

    /**
     * 獲取交易的簡易人物
     *
     * @param objId
     * @return
     */
    public abstract L1PcInstance getPcInstance(final int objId);

    public abstract L1CharaterTrade getCharaterTrade(final int id);

    public abstract void updateCharAccountName(final int objId, final String accountName);
}
