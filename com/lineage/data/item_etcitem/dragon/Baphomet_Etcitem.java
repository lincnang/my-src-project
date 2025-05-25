package com.lineage.data.item_etcitem.dragon;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_GmMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * <font color=#00800>42516 巴風特雕像)</font><BR>
 * 點選後可獲得 HP+100,MP+100, 近距離傷害+5,近距離命中+10,遠距離傷害+5,遠距離命中+10,
 * 魔攻+5,力敏智+1，使用時間10分鐘，冷卻時間20分鐘
 *
 * @author dexc
 */
public class Baphomet_Etcitem extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Baphomet_Etcitem.class);

    /**
     *
     */
    private Baphomet_Etcitem() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Baphomet_Etcitem();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        try {
            // 例外狀況:物件為空
            if (item == null) {
                return;
            }
            // 例外狀況:人物為空
            if (pc == null) {
                return;
            }
            // 有變身書狀態無法使用
            if (pc.hasSkillEffect(8030)) {
                pc.sendPackets(new S_ServerMessage("無法與變身書狀態共存"));
                return;
            }
            // 取得玩家狀態剩餘時間
            int time = L1BuffUtil.cancelBaphomet(pc);
            if (time != -1) {
                // 1,139：%0 分鐘之內無法使用。
                pc.sendPackets(new S_GmMessage("巴風特雕像狀態時間還有:" + String.valueOf(time) + "秒"));
                return;
            }
            // 例外狀況:具有時間設置
            boolean isDelayEffect = false;
            final int delayEffect = 1200;//20分鐘
            // 取得物品最後使用時間
            final Timestamp lastUsed = item.getLastUsed();
            if (lastUsed != null) {
                final Calendar cal = Calendar.getInstance();
                long useTime = (cal.getTimeInMillis() - lastUsed.getTime()) / 1000;
                if (useTime <= delayEffect) {
                    // 轉換為需等待時間
                    useTime = (delayEffect - useTime); /// 60;
                    // 時間數字 轉換為字串
                    final String useTimeS = /* useItem.getLogName() + " " + */ String.valueOf(useTime / 60);
                    // 1139 %0 分鐘之內無法使用。
                    pc.sendPackets(new S_GmMessage(useTimeS + "分鐘內之內無法使用"));
                    return;
                }
            }
            // 物品沒延遲才能使用
            isDelayEffect = true;
            // 變身巴風特
            L1PolyMorph.doPoly(pc, 13450, 600, 1);
            // 使用後不移除
            // pc.getInventory().removeItem(item, 1);
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 10165));
            // 設置延遲使用機制
            // final Timestamp ts = new Timestamp(System.currentTimeMillis());
            // item.setLastUsed(ts);
            // pc.getInventory().updateItem(item,
            // L1PcInventory.COL_DELAY_EFFECT);
            // pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);
            // 物件使用延遲設置
            if (isDelayEffect) {
                final Timestamp ts = new Timestamp(System.currentTimeMillis());
                // 設置使用時間
                item.setLastUsed(ts);
                pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
                pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);
            }
            // SKILL移轉
            final SkillMode mode = L1SkillMode.get().getSkill(L1SkillId.Baphomet);
            if (mode != null) {
                mode.start(pc, null, null, 600);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}