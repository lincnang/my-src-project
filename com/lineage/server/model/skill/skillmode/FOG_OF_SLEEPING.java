package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.templates.L1Skills;

import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;

/**
 * 魔法大師 - 施放時獲得BUFF，攻擊傷害轉為魔法攻擊
 *
 * @author AI Assistant
 */
public class FOG_OF_SLEEPING extends SkillMode {

    public FOG_OF_SLEEPING() {
    }

    @Override
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        // 魔法大師是對自己施放的技能
        // 如果已有效果，允許重複施放來重置時間
        boolean hasEffect = srcpc.hasSkillEffect(FOG_OF_SLEEPING);
        if (hasEffect) {
            // 移除舊的效果，準備重新設置
            srcpc.removeSkillEffect(FOG_OF_SLEEPING);
        }

        // 取得技能資料庫設定
        L1Skills skill = SkillsTable.get().getTemplate(FOG_OF_SLEEPING);

        // 從資料庫讀取持續時間（如果integer為0則使用資料庫預設值）
        int duration = integer > 0 ? integer : skill.getBuffDuration();

        // 設定魔法大師狀態給施放者
        srcpc.setSkillEffect(FOG_OF_SLEEPING, duration * 1000);

        // 從資料庫讀取施法動作ID (action_id)
        int actionId = skill.getActionId();

        // 從資料庫讀取施法特效ID
        int castGfx = skill.getCastGfx();

        // 從資料庫讀取範圍類型
        int area = skill.getArea();

        // 直接在核心設定BUFF圖標ID和訊息ID（不從資料庫讀取）
        int buffIconId = 10546;  // BUFF圖標ID
        int startMsgId = 5126;   // 開始訊息ID
        int endMsgId = 5124;     // 結束訊息ID

        
        // 播放施法動作和特效
        if (castGfx > 0 && actionId > 0) {
            // 使用施放者自己作為目標，這樣會面向正確的方向
            srcpc.sendPackets(new S_UseAttackSkill(srcpc, srcpc.getId(), castGfx,
                                                  srcpc.getX(), srcpc.getY(), actionId, 0));

            // 廣播給周圍玩家看見施法動作
            srcpc.broadcastPacketAll(new S_UseAttackSkill(srcpc, srcpc.getId(), castGfx,
                                                         srcpc.getX(), srcpc.getY(), actionId, 0));
        }

        // 播放特效ID 21448
        if (castGfx == 40318) {  // 檢查是否為魔法大師特效
            srcpc.sendPackets(new S_SkillSound(srcpc.getId(), 21448));
            srcpc.broadcastPacketAll(new S_SkillSound(srcpc.getId(), 21448));
        }

        // 顯示BUFF圖標（只發送給施法者自己）
        if (buffIconId > 0) {
            // 使用資料庫設定的BUFF圖標ID
            srcpc.sendPackets(new S_InventoryIcon(buffIconId, true, startMsgId, duration));
        }

        // 發送系統訊息（可選）
        // 如果需要發送訊息，取消註釋以下代碼：
        /*
        if (startMsgId > 0 && startMsg != null && !startMsg.isEmpty()) {
            srcpc.sendPackets(new S_ServerMessage(startMsgId));
        }
        */

        /*
        // 發送純粹的音效（如果不想要動作）
        if (castGfx > 0) {
            srcpc.sendPackets(new S_SkillSound(srcpc.getId(), castGfx));
            srcpc.broadcastPacketAll(new S_SkillSound(srcpc.getId(), castGfx));
        }

        // 從資料庫讀取BUFF圖標ID
        int buffIconId = skill.getBuffIconid();
        if (buffIconId > 0) {
            srcpc.sendPackets(new S_NewSkillIcon(FOG_OF_SLEEPING, true, duration * 1000));
        }

        // 從資料庫讀取系統訊息ID
        int msgId = skill.getSysmsgIdHappen();
        if (msgId > 0) {
            srcpc.sendPackets(new S_ServerMessage(msgId));
        }
        */

        // 設置魔法大師狀態
        srcpc.setMagicMaster(true);

        // 發送對話框訊息
        srcpc.sendPackets(new S_SystemMessage("技能發動、物理傷害將轉為魔法傷害計算",11));

        return 0;
    }

    @Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        // NPC不應該能施放魔法大師技能
        return 0;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void stop(final L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            // 直接在核心設定BUFF圖標ID和訊息ID（不從資料庫讀取）
            int buffIconId = 10546;  // BUFF圖標ID
            int endMsgId = 5124;     // 結束訊息ID

            // 移除BUFF圖標（只發送給施法者自己）
            if (buffIconId > 0) {
                // 移除BUFF圖標
                pc.sendPackets(new S_InventoryIcon(buffIconId, false, endMsgId, 0));
            }

            // 移除狀態
            pc.setMagicMaster(false);

            // 發送結束訊息
            pc.sendPackets(new S_SystemMessage("技能結束、效果恢復",11));
        }
    }
}