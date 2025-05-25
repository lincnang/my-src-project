package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.SkillEnhanceTable;
import com.lineage.server.datatables.sql.CharSkillTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1SkillEnhance;

/**
 * 通用技能書 使用邏輯
 * (若你的核心是依照 itemId 分類執行，可再用 switchCase 區分不同技能)
 */
public class Item_SkillBook extends ItemExecutor {

    private Item_SkillBook() {
    }

    public static ItemExecutor get() {
        return new Item_SkillBook();
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null || item == null) {
            return;
        }

        // 1) 依照 itemId 決定要升哪個 skillId
        //    這裡做簡單範例: 700086=衝暈書(Shock Stun), 40165=三重矢書(Triple Arrow)...
        int skillId = 0;
        switch (item.getItemId()) {
            case 700086: // 衝暈書
                skillId = 87; // SHOCK_STUN
                break;
            case 700087: // 三重矢書
                skillId = 132;
                break;
            case 700088: // 聖結界
                skillId = 61;
                break;
            case 700089: // 狂暴術
                skillId = 52;
                break;
            case 700090: // 體力回復術
                skillId = 49;
                break;
            case 700091: // 靈魂昇華
                skillId = 57;
                break;
            case 700092: // 寒冰尖刺
                skillId = 54;
                break;
            default:
                pc.sendPackets(new S_SystemMessage("此書未設定對應的技能"));
                return;
        }

        // 2) 取得玩家目前「skillId」的等級，並計算下一級
        int currentLv = pc.getSkillLevel(skillId); // 先從 pc 取目前等級
        int newLv = currentLv + 1;                 // 要升到的新等級

        // 3) 檢查「skills_技能強化」表中，skillId + bookLevel= newLv 是否存在
        L1SkillEnhance enhanceData = SkillEnhanceTable.get().getEnhanceData(skillId, newLv);
        if (enhanceData == null) {
            pc.sendPackets(new S_SystemMessage("已達最高等級或資料未設定，無法升級。"));
            return;
        }

        // 4) 寫回資料庫 + 更新角色物件
        //    (a) 更新資料庫: CharSkillTable -> updateSkillLevel
        //    (b) 同步 pc 內部的技能等級
        CharSkillTable.get().updateSkillLevel(pc.getId(), skillId, newLv);
        pc.setSkillLevel(skillId, newLv);

        // 5) 移除道具 (吃書)
        pc.getInventory().removeItem(item, 1);

        // 6) 給玩家升級提示訊息 (例如顯示 "衝暈技能升級至等級X")
        pc.sendPackets(new S_SystemMessage(
                String.format("使用技能書，%s 升級至等級 %d", enhanceData.getNote(), newLv)
        ));
    }
}
