package com.lineage.data.item_etcitem.card;
// 匯入所需的外部類別庫
import com.lineage.data.executor.ItemExecutor; // 匯入物品執行器的基礎類別
import com.lineage.server.datatables.ItemPowerTable; // 匯入物品能力名稱的資料表
import com.lineage.server.datatables.lock.CharItemPowerReading; // 匯入角色物品能力讀寫的鎖定機制
import com.lineage.server.model.Instance.L1ItemInstance; // 匯入遊戲中物品實體的類別
import com.lineage.server.model.Instance.L1PcInstance; // 匯入遊戲中玩家角色實體的類別
import com.lineage.server.serverpackets.S_ItemStatus; // 匯入更新物品狀態的封包
import com.lineage.server.serverpackets.S_ServerMessage; // 匯入發送伺服器訊息的封包
import com.lineage.server.templates.L1ItemPower_name; // 匯入物品能力名稱的樣板
import org.apache.commons.logging.Log; // 匯入日誌記錄介面
import org.apache.commons.logging.LogFactory; // 匯入日誌記錄工廠

/**
 * 說明此類別的功能：處理名為「伏曦易經-陰」的道具。
 *
 * @author dexc (原作者)
 */
public class C3_Card_01 extends ItemExecutor {

    // 建立一個日誌記錄器，用於記錄錯誤或重要資訊。
    private static final Log _log = LogFactory.getLog(C3_Card_01.class);

    //宣告一個私有整數變數 _cardnumber，用來儲存從設定檔讀取的卡片編號。
    private int _cardnumber;

    /**
     * 私有的建構子。
     * 這裡使用私有建構子是為了防止外部直接 new 一個這個類別的實體，
     * 通常會搭配一個靜態的 get() 方法來取得實體（單例模式的一種變形）。
     */
    private C3_Card_01() {
        // TODO Auto-generated constructor stub (這是一個自動產生的註解，表示這部分可能需要再實現)
    }

    /**
     * 提供一個公開的靜態方法來獲取這個類別的實體。
     * 每次呼叫都會建立一個新的 C3_Card_01 物件。
     * @return ItemExecutor 的一個新實體。
     */
    public static ItemExecutor get() {
        return new C3_Card_01();
    }

    /**
     * 道具物件執行的核心方法。當玩家使用此道具時，這個方法會被呼叫。
     *
     * @param data 傳入的參數陣列。通常 data[0] 會是玩家選擇的目標物件 ID。
     * @param pc   執行此操作的玩家角色物件。
     * @param item 被使用的道具物件本身 (也就是「伏曦易經-陰」)。
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        try {
            // 從傳入的參數中獲取目標物品的物件 ID (OBJID)
            final int targObjId = data[0];
            // 透過物件 ID 從玩家的背包中找到該目標物品
            final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

            // 如果在玩家背包中找不到該物品，則直接結束方法。
            if (tgItem == null) {
                return;
            }

            // 檢查目標物品是否處於裝備狀態。
            if (tgItem.isEquipped()) {
                // 如果已裝備，發送訊息給玩家，提示必須先解除裝備。
                pc.sendPackets(new S_ServerMessage("\\fR你必須先解除物品裝備!"));
                return; // 結束方法
            }

            // 根據目標物品的類型來決定是否可以賦予能力。
            switch (tgItem.getItem().getUseType()) {
                case 2:   // 盔甲
                case 18:  // T恤
                case 19:  // 斗篷
                case 20:  // 手套
                case 21:  // 靴
                case 22:  // 頭盔
                case 25:  // 盾牌
                case 70:  // 脛甲
                    // 如果目標物品是以上類型的防具，則執行以下賦予能力的邏輯：

                    // 1. 從玩家背包中移除一個「伏曦易經-陰」道具。
                    pc.getInventory().removeItem(item, 1);

                    // 2. 清除目標物品上舊的能力名稱。
                    tgItem.set_power_name(null);
                    // 3. 從資料庫中刪除該物品舊的能力紀錄。
                    CharItemPowerReading.get().delItem(tgItem.getId());

                    // 4. 根據此卡片設定的 _cardnumber，從 ItemPowerTable 中取得新的能力。
                    final L1ItemPower_name power = ItemPowerTable.POWER_NAME.get(_cardnumber);

                    // 5. 發送訊息給玩家，告知獲得了什麼能力。
                    pc.sendPackets(new S_ServerMessage("\\fT獲得" + power.get_power_name() + " 的力量"));

                    // 6. 將新的能力設定到目標物品上。
                    tgItem.set_power_name(power);
                    // 7. 將物品ID與新能力儲存到資料庫中。
                    CharItemPowerReading.get().storeItem(tgItem.getId(), tgItem.get_power_name());

                    // 8. 更新客戶端上該物品的狀態，讓玩家看到變化。
                    pc.sendPackets(new S_ItemStatus(tgItem));
                    break; // 結束 switch

                default:
                    // 如果目標物品不是可作用的類型，發送一個通用訊息 "沒有任何事情發生" (訊息ID 79)。
                    pc.sendPackets(new S_ServerMessage(79));
                    break; // 結束 switch
            }
        } catch (final Exception e) {
            // 如果在執行過程中發生任何未預期的錯誤，將錯誤訊息記錄到日誌中。
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 這個方法用來接收從設定檔傳來的參數。
     * 伺服器在載入道具資料時會呼叫此方法。
     *
     * @param set 包含設定參數的字串陣列。
     */
    @Override
    public void set_set(String[] set) {
        try {
            // 將設定檔陣列中的第二個元素 (set[1]) 轉為整數，並存到 _cardnumber 變數中。
            // 這個數字決定了這張卡片會賦予哪一種能力。
            _cardnumber = Integer.parseInt(set[1]);
        } catch (Exception e) {
            // 如果解析失敗（例如格式錯誤），則忽略，_cardnumber 會保持預設值 0。
        }
    }
}