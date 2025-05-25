package com.lineage.server.datatables;

import com.lineage.server.utils.L1QueryUtil;

public class RecordTable {
    private static RecordTable _instance;

    public static RecordTable get() {
        if (_instance == null)
            _instance = new RecordTable();
        return _instance;
    }

    public void recordPcChangePassWord1(String accName, String pcName) {
        String sql = "INSERT INTO 日誌_角色_刪人 (帳號, 玩家, 時間) VALUE (?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_角色_刪人 (帳號, 玩家, 時間) VALUE (?, ?, SYSDATE())", new Object[]{accName, pcName});
    }

    public void recordtakeitem(String pcName, String itemName, int itemCount, int itemObjid, String ip) {
        String sql = "INSERT INTO 日誌_角色_撿物 (玩家,撿取物品,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_角色_撿物 (玩家,撿取物品,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, SYSDATE())", new Object[]{pcName, itemName,
                itemCount, itemObjid, ip});
    }


    public void recordeDeadItem(String pcName, String itemName, int itemCount, int itemObjid, String ip) {
        String sql = "INSERT INTO 日誌_角色_噴裝 (玩家,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_角色_噴裝 (玩家,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, SYSDATE())", new Object[]{pcName, itemName,
                itemCount, itemObjid, ip});
    }


    public void recordPcChangeName(int pcid, String accName, String pcName, String changName, String ip) {
        String sql = "INSERT INTO 日誌_角色_更名 (編號, 帳號, 玩家, 改名, IP, 時間) VALUE (?, ?, ?, ?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_角色_更名 (編號, 帳號, 玩家, 改名, IP, 時間) VALUE (?, ?, ?, ?, ?, SYSDATE())", new Object[]{pcid, accName, pcName, changName, ip});
    }

    public void recordCastleReward(String castle, String pcName, String itemName, int count, int item_obj, String ip) {
        String sql = "INSERT INTO 日誌_攻城獎勵 (城堡,玩家,物品,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_攻城獎勵 (城堡,玩家,物品,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, SYSDATE())", new Object[]{castle, pcName, itemName,
                count, item_obj, ip});
    }


    public void r_speed(String pcName, String note) {
        String sql = "INSERT INTO 日誌_加速 (玩家, 說明, 時間) VALUE (?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_加速 (玩家, 說明, 時間) VALUE (?, ?, SYSDATE())", new Object[]{pcName, note});
    }

    public void recordeWarehouse_elf(String pcName, String action, String warehouse, String itemName, int itemCount, int itemObjid, String ip) {
        String sql = "INSERT INTO 日誌_角色_妖倉 (玩家,執行,倉庫,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_角色_妖倉 (玩家,執行,倉庫,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())", new Object[]{pcName, action, warehouse, itemName, itemCount,
                itemObjid, ip});
    }

    public void recordeWarehouse_clan(String pcName, String action, String warehouse, String itemName, int itemCount, int itemObjid, String ip) {
        String sql = "INSERT INTO 日誌_角色_盟倉 (玩家,執行,倉庫,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_角色_盟倉 (玩家,執行,倉庫,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())", new Object[]{pcName, action, warehouse, itemName, itemCount,
                itemObjid, ip});
    }

    public void recordeWarehouse_pc(String pcName, String action, String warehouse, String itemName, int itemCount, int itemObjid, String ip) {
        String sql = "INSERT INTO 日誌_角色_個倉 (玩家,執行,倉庫,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_角色_個倉 (玩家,執行,倉庫,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())", new Object[]{pcName, action, warehouse, itemName, itemCount,
                itemObjid, ip});
    }

    public void recordWarehouse_char_pc(String pcName, String action, String warehouse, String itemName, int itemCount, int itemObjid, String ip) {
        String sql = "INSERT INTO 日誌_角色_角倉 (玩家,執行,倉庫,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_角色_角倉 (玩家,執行,倉庫,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())", new Object[]{pcName, action, warehouse, itemName, itemCount,
                itemObjid, ip});
    }

    public void killpc(String pcName, String targeName) {
        String sql = "INSERT INTO 日誌_角色_殺人 (生存,死亡,時間) VALUE (?, ?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_角色_殺人 (生存,死亡,時間) VALUE (?, ?, SYSDATE())", new Object[]{pcName, targeName});
    }

    public void recordbox(String pcName, String itemNamebox, String itemName, int itemCount) {
        String sql = "INSERT INTO 日誌_角色_寶箱 (玩家,使用寶箱,獲得道具,數量,時間) VALUE (?, ?, ?, ?,  SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_角色_寶箱 (玩家,使用寶箱,獲得道具,數量,時間) VALUE (?, ?, ?, ?,  SYSDATE())", new Object[]{pcName, itemNamebox, itemName, itemCount});
    }


    public void reshp1(String pcName) {
        String sql = "INSERT INTO 日誌_回憶蠟燭(玩家,時間) VALUE (?, SYSDATE())";
        L1QueryUtil.execute("INSERT INTO 日誌_回憶蠟燭(玩家,時間) VALUE (?, SYSDATE())", new Object[]{pcName});
    }
}
