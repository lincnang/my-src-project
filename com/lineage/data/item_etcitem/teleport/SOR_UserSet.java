package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTeleportTable;
import com.lineage.server.datatables.ItemTeleportTable.TeleportList;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * <font color=#00800>自定義傳送卷軸</font><BR>
 *
 * @author dexc
 */
public class SOR_UserSet extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(SOR_UserSet.class);

    /**
     *
     */
    private SOR_UserSet() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new SOR_UserSet();
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
        /*
         * final int[] loc = ItemTeleportTable.get().getLoc(item.getItemId());
         *
         * if (loc != null) { final int locX = loc[0]; final int locY = loc[1];
         * final short mapId = (short) loc[2];
         *
         * if (pc.getMap().isEscapable()) { // 刪除道具
         * pc.getInventory().removeItem(item, 1);
         *
         * // 解除魔法技能絕對屏障 L1BuffUtil.cancelAbsoluteBarrier(pc);
         *
         * final TeleportRunnable runnable = new TeleportRunnable(pc, locX,
         * locY, mapId); GeneralThreadPool.get().schedule(runnable, 0);
         *
         * // 該地圖不允許使用回捲 } else { // 276 \f1在此無法使用傳送。 pc.sendPackets(new
         * S_ServerMessage(276)); // 解除傳送鎖定 pc.sendPackets(new S_Paralysis(
         * S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); } final int time = loc[3];
         * if (time > 0) { pc.get_other().set_usemap(mapId);
         * ServerUseMapTimer.put(pc, time); pc.sendPackets(new
         * S_ServerMessage("使用時間限制:" + time + "秒")); } }
         */
        if (ItemTeleportTable.START) {
            final TeleportList list = ItemTeleportTable.get().getLoc(item.getItemId());
            if (pc.getMapId() == 10017) {
                return;
            }
            if (list != null) {
                final int week = list.getWeek();
                final int starttime = list.getStarttime();
                final int unitytime = list.getUnitytime();
                Calendar date = Calendar.getInstance();
                int nowWeek = (date.get(Calendar.DAY_OF_WEEK) - 1);
                int nowHour = date.get(Calendar.HOUR_OF_DAY);
                if (pc.hasSkillEffect(L1SkillId.MOVE_STOP)) {
                    return;
                }
                if (pc.hasSkillEffect(4000)) {
                    return;
                }
                if (nowWeek == 0) {
                    nowWeek += 7;
                }
                // 只限制星期
                if (week > 0 && starttime < 0 && unitytime < 0) {
                    if (week != nowWeek) {
                        pc.sendPackets(new S_ServerMessage(166, "使用時間限制:星期【" + week + " 】"));
                        return;
                    }
                }
                // 限制星期.限制時間
                else if (week > 0 && starttime >= 0 && unitytime >= 0) {
                    if (week != nowWeek || nowHour < starttime || nowHour >= unitytime) {
                        pc.sendPackets(new S_ServerMessage(166, "使用時間限制:星期【 " + week + " 】時間為【 " + starttime + " 】點，至【 " + unitytime + "】點"));
                        return;
                    }
                }
                // 限制時間
                else if (week < 0 && starttime >= 0 && unitytime >= 0) {
                    if (nowHour < starttime || nowHour >= unitytime) {
                        pc.sendPackets(new S_ServerMessage(166, "使用時間限制:時間為【 " + starttime + " 】點，至【 " + unitytime + "】點"));
                        return;
                    }
                }
                final int locX = list.getLocX();
                final int locY = list.getLocY();
                final short mapId = list.getMapId();
                if (pc.getMap().isEscapable()) {
                    // 刪除道具
                    pc.getInventory().removeItem(item, 1);
                    // 解除魔法技能絕對屏障
                    L1BuffUtil.cancelAbsoluteBarrier(pc);
                    final TeleportRunnable runnable = new TeleportRunnable(pc, locX, locY, mapId);
                    GeneralThreadPool.get().schedule(runnable, 0);
                    // 該地圖不允許使用回捲
                } else {
                    // 276 \f1在此無法使用傳送。
                    pc.sendPackets(new S_ServerMessage(276));
                    // 解除傳送鎖定
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                }
                int time = list.getTime();
                if (time > 0) {
                    pc.get_other().set_usemap(mapId);
                    ServerUseMapTimer.put(pc, time);
                }
            }
        } else {
            _log.error("警告!!etcitem_teleport尚無設定資料 itemid:" + item.getItemId());
        }
    }

    private class TeleportRunnable implements Runnable {
        private final L1PcInstance _pc;
        private int _locX = 0;
        private int _locY = 0;
        private int _mapid = 0;

        public TeleportRunnable(final L1PcInstance pc, final int x, final int y, final int mapid) {
            _pc = pc;
            _locX = x;
            _locY = y;
            _mapid = mapid;
        }

        @Override
        public void run() {
            try {
                final L1Map map = L1WorldMap.get().getMap((short) _mapid);
                int r = 10;
                int tryCount = 0;
                int newX = _locX;
                int newY = _locY;
                do {
                    tryCount++;
                    newX = _locX + (int) (Math.random() * r) - (int) (Math.random() * r);
                    newY = _locY + (int) (Math.random() * r) - (int) (Math.random() * r);
                    if (map.isPassable(newX, newY, _pc)) {
                        break;
                    }
                    TimeUnit.MILLISECONDS.sleep(1);
                } while (tryCount < 5);
                if (tryCount >= 5) {
                    L1Teleport.teleport(_pc, _locX, _locY, (short) _mapid, _pc.getHeading(), true);
                } else {
                    L1Teleport.teleport(_pc, newX, newY, (short) _mapid, _pc.getHeading(), true);
                }
            } catch (InterruptedException e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
