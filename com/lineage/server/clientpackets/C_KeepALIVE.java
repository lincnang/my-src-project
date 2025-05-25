package com.lineage.server.clientpackets;

import com.add.L1Config;
import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.event.ProtectorSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_GameTime;
import com.lineage.server.timecontroller.server.ServerItemUserTimer;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

import static com.lineage.server.model.skill.L1SkillId.AI_1;
import static com.lineage.server.model.skill.L1SkillId.AI_2;

public class C_KeepALIVE extends ClientBasePacket {

    private static final Log _log = LogFactory.getLog(C_KeepALIVE.class);

    /**
     * 變更限時物品檢測方法 by 聖子默默
     *
     * @param pc pc
     */
    private static void checkPcTimerItems(L1PcInstance pc) {
        try {
            final Timestamp ts = new Timestamp(System.currentTimeMillis());
            for (L1ItemInstance item : pc.getInventory().getItems()) {
                ServerItemUserTimer.checkTimerItems(item, ts);
            }
            if (pc.isProtector() && pc.castleWarResult()) {// 守護者狀態並參加攻城戰中
                L1ItemInstance item = pc.getInventory().findItemId(ProtectorSet.ITEM_ID);
                if (item != null) {
                    pc.getInventory().removeItem(item, 1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkPcSM(L1PcInstance pc) {
        if (pc.isPrivateShop()) {
            int mapId = pc.getMapId();
            if (mapId != 340 && mapId != 350 && mapId != 360 && mapId != 370 && mapId != 800) {
                //if (mapId != 800) {
                pc.getSellList().clear();
                pc.getBuyList().clear();
                pc.setPrivateShop(false);
                pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), 3));
            }
        }
        if (pc.get_mazu_time() != 0L) {
            if (pc.is_mazu()) {
                if (2400 - pc.get_mazu_time() >= 2400) {// 2400秒 = 40分鐘
                    pc.set_mazu_time(0);
                    pc.set_mazu(false);
                }
            }
        }
    }

    private static void checkErrorForAI(L1PcInstance pc) {
        if (!L1Config._2226) {
            return;
        }
        /* [原碼] 定時外掛檢測 */
        if (!pc.hasSkillEffect(AI_1) && !pc.hasSkillEffect(AI_2) && !pc.isGm() //check skill effect buff
                && !ConfigOtherSet2.NO_AI_MAP_LIST.contains((int) pc.getMapId())) {
            setErrorForAI(pc);
        }
    }

    public static void setErrorForAI(L1PcInstance pc) {
        // 城堡戰爭區域
        if (L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId())) {
            return;
        }
        // 盟屋內座標
        if (L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
            return;
        }
        // 世界樹下
        if (pc.getMapId() == 4 && pc.getLocation().isInScreen(new Point(33055, 32336))) {
            return;
        }
        // 新人房間
        if (pc.getMapId() == 99) {
            return;
        }
        // 等級低於30
        if (pc.getLevel() <= 30) {
            return;
        }
        // 安全區域不判斷
        if (pc.isSafetyZone()) {
            return;
        }
        pc.setSkillEffect(AI_1, 5 * 1000);
    }

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            int serverTime = L1GameTimeClock.getInstance().currentTime().getSeconds();
            pc.sendPackets(new S_GameTime(serverTime));
            checkPcSM(pc);
            checkPcTimerItems(pc);
            checkErrorForAI(pc);
        } catch (Exception localException) {
            _log.error(localException, localException);
        } finally {
            over();
        }
    }

    public String getType() {
        return super.getType();
    }
}