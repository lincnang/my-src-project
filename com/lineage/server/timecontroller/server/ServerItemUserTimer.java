package com.lineage.server.timecontroller.server;

import com.lineage.data.event.CardSet;
import com.lineage.server.datatables.ItemVIPTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemPower_name;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldItem;
import com.lineage.server.world.WorldPet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServerItemUserTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(ServerItemUserTimer.class);
    private ScheduledFuture<?> _timer;

    private static void check_card_item(L1ItemInstance item, Timestamp ts) {
        try {
            if (item.get_time().before(ts)) {
                item.setEquipped(false);
                L1Object object = World.get().findObject(item.get_char_objid());
                if (object != null) {
                    L1PcInstance pc = (L1PcInstance) object;
                    item.set_card_use(2);
                    CardSet.remove_card_mode(pc, item);
                    pc.sendPackets(new S_ItemName(item));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private static void checkItem(L1ItemInstance item, Timestamp ts) throws Exception {
        try {
            if (item.get_time().before(ts)) {
                L1Object object = World.get().findObject(item.get_char_objid());
                if (object != null) {
                    if ((object instanceof L1PcInstance)) {
                        final L1PcInstance tgpc = (L1PcInstance) object;
						/*String classname = item.getItem().getclassname();
						if (classname.startsWith("teleport.Hang_fu")){
							tgpc.setActivated(false);
							tgpc.sendPackets(new S_ServerMessage("\\aD 自動狩獵時間已到期，停止自動狩獵。"));
				    		L1PolyMorph.undoPoly(tgpc);//停止變身
				    		L1Teleport.teleport(tgpc, tgpc.getLocation(), tgpc.getHeading(), false);
						}*/
                        final int itemId = item.getItemId();
                        if (ItemVIPTable.get().checkVIP(itemId)) {
                            if (item.isEquipped()) {
                                ItemVIPTable.get().deleItemVIP(tgpc, itemId); // 移除道具vip效果
                            }
                            tgpc.sendPackets(new S_ServerMessage(166, item.getName() + "已經到期"));
                        }
                        // 刪除物品
                        tgpc.getInventory().removeItem(item);
                        L1Pet pet = PetReading.get().getTemplate(item.getId());
                        if (pet != null) {
                            final L1PetInstance tgpet = WorldPet.get().get(pet.get_objid());
                            if (tgpet != null) {
                                tgpet.dropItem();
                                tgpet.deleteMe();
                            }
                        }
                    }
                } else {
                    CharItemsReading.get().deleteItem(item.get_char_objid(), item);
                    World.get().removeObject(item); // 修復物品刪除BUG
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 檢查道具是否已超過時限2 超過則移除附魔效果 [魔法武器DIY系統(DB自製)]
     *
     */
    private static void checkItem2(final L1ItemInstance item, final L1ItemPower_name power, final Timestamp ts) throws Exception {
        power.set_magic_weapon(null);
        power.set_date_time(null);
        CharItemPowerReading.get().updateItem(item.getId(), power);
        // 取得道具主人
        final L1Object object = World.get().findObject(item.get_char_objid());
        if (object != null) {
            if (object instanceof L1PcInstance) {
                final L1PcInstance tgpc = (L1PcInstance) object;
                // 更新道具狀態
                tgpc.sendPackets(new S_ItemStatus(item));
            }
        }
    }

    private static void checkItem3(final L1ItemInstance item, final L1ItemPower_name power, final Timestamp ts) throws Exception {
        power.set_boss_weapon(null);
        power.set_boss_date_time(null);
        CharItemPowerReading.get().updateItem(item.getId(), power);
        // 取得道具主人
        final L1Object object = World.get().findObject(item.get_char_objid());
        if (object != null) {
            if (object instanceof L1PcInstance) {
                final L1PcInstance tgpc = (L1PcInstance) object;
                // 更新道具狀態
                tgpc.sendPackets(new S_ItemStatus(item));
            }
        }
    }

    public static boolean checkTimerItems(L1ItemInstance item, Timestamp ts) throws Exception {
        // 不具備使用期限 忽略
        if (item.get_time() != null && item.get_card_use() == 2) { // 到期
            return true;
        }
        if (item.get_time() != null && item.get_card_use() == 1) { // 使用中
            if (CardSet.START) {
                check_card_item(item, ts);
            }
        } else {
            // 擁有使用期限
            if (item.get_time() != null && item.get_time().before(ts)) {
                checkItem(item, ts);
            }
            // 魔法武器DIY系統(附魔時限) by terry0412
            final L1ItemPower_name power = item.get_power_name(); // 取回道具凹槽資料
            if (power != null && power.get_magic_weapon() != null && power.get_magic_weapon().getMaxUseTime() > 0) {
                final Timestamp date = power.get_date_time();
                if (date != null && date.before(ts)) {
                    checkItem2(item, power, ts);
                }
            }
            if (power != null && power.get_boss_weapon() != null && power.get_boss_weapon().getMaxUseTime() > 0) {
                final Timestamp date = power.get_boss_date_time();
                if (date != null && date.before(ts)) {
                    checkItem3(item, power, ts);
                }
            }
        }
        return false;
    }

    public void start() {
        //final int timeMillis = 1 * 1000;// 1秒
        final int timeMillis = 60 * 1000;// 1分鐘
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            final Collection<L1ItemInstance> items = WorldItem.get().all();
            if (items.isEmpty()) {
                return;
            }
            // 目前時間
            final Timestamp ts = new Timestamp(System.currentTimeMillis());
            for (final L1ItemInstance item : items) {
                if (checkTimerItems(item, ts)) {
                    continue;
                }
                TimeUnit.MILLISECONDS.sleep(5);
            }
        } catch (final Exception e) {
            _log.error("物品使用期限計時時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final ServerItemUserTimer userTimer = new ServerItemUserTimer();
            userTimer.start();
        }
    }
}
