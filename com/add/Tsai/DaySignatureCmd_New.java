package com.add.Tsai;

// 引入遊戲角色、訊息、日誌等相關類

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 簽到指令
 *
 * @author hero
 */
public class DaySignatureCmd_New {
    // 日誌物件（用來記錄錯誤、訊息等）
    private static final Log _log = LogFactory.getLog(DaySignatureCmd_New.class);
    // 單例模式：只會有一個這個類別的實體
    private static DaySignatureCmd_New _instance;

    // 取得本類別唯一實體（單例模式）
    public static synchronized  DaySignatureCmd_New get() {
        if (_instance == null) {
            _instance = new DaySignatureCmd_New();
        }
        return _instance;
    }

    /**
     * 處理簽到指令
     * @param pc 角色對象
     * @param cmd 指令內容
     * @return 是否處理成功
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            // 判斷收到的指令是不是 day_signature
            if (cmd.matches("day_signature")) {
                // 檢查今天是否已經簽到過（true=還沒簽到，false=已簽到）
                if (!checkDay(pc)) {
                    pc.sendPackets(new S_SystemMessage("\\aH今天已完成簽到獎勵")); // 發送系統訊息
                    return false;
                }
                // 取得下一天的簽到天數（前一天+1）
                int day = pc.get_day_signature() + 1;
                // 根據天數取得對應的獎勵資料
                Day_Signature_New daySignatureNew = Day_Signature_New.get().getDay(day);
                // 如果超過最大天數就從第一天重新開始
                if (daySignatureNew == null) {
                    pc.set_day_signature(0); // 歸零
                    day = pc.get_day_signature() + 1;
                    daySignatureNew = Day_Signature_New.get().getDay(day);
                }
                if (daySignatureNew == null) {
                    pc.sendPackets(new S_SystemMessage("\\aH簽到獎勵資料異常，請聯繫管理員"));
                    return false;
                }

                int itemId = daySignatureNew.get_itemId();
                int itemCount = daySignatureNew.get_itemCount();
                // 發放物品並取得物品實例
                L1ItemInstance item = pc.getInventory().storeItem(itemId, itemCount);
                String itemName = null;
                // 優先用物品實例名稱（帶附魔、屬性顯示），否則查物品模板名稱
                if (item != null) {
                    itemName = item.getName();
                } else {
                    itemName = ItemTable.get().getTemplate(itemId).getName();
                }
                // 顯示玩家領取的物品名稱與數量
                pc.sendPackets(new S_SystemMessage("\\aH第" + day + "天簽到獎勵：" + itemName + " x " + itemCount));



                // 設定玩家簽到天數
                pc.set_day_signature(day);
                // 設定簽到時間（紀錄本次簽到是今天哪個時刻）
                pc.set_day_signature_time(new Timestamp(System.currentTimeMillis()));
                return true;
            }
            // 不是這個指令就回傳 false
            return false;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e); // 有錯誤就寫入日誌
        }
        return false;
    }

    /**
     * 檢查今天是否已經簽到過
     * @param pc 玩家角色對象
     * @return true=今天可以簽到，false=今天已經簽到過
     */
    private boolean checkDay(L1PcInstance pc) {
        // 沒有任何簽到紀錄，可以簽到
        if (pc.get_day_signature_time() == null) {
            return true;
        }
        // 取出最後簽到時間的「日期部分」，只保留年月日
        LocalDateTime oldDate = pc.get_day_signature_time().toLocalDateTime().toLocalDate().atStartOfDay();
        // 取得現在時間的「日期部分」，只保留年月日
        LocalDateTime currentDate = new Timestamp(System.currentTimeMillis()).toLocalDateTime().toLocalDate().atStartOfDay();
        // 判斷今天與上次簽到日期是否不同（跨日）
        if (!oldDate.equals(currentDate)) {
            // 若新的一天大於舊的一天，代表已經跨天，可以簽到
            return currentDate.toLocalDate().isAfter(oldDate.toLocalDate());
        }
        // 沒跨日（還是同一天），不可簽到
        return false;
    }
}
