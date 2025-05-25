package com.lineage.data.event.SoulQueen;

import com.lineage.server.IdFactory;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.SoulTowerTable;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 屍魂塔副本 刷怪線程
 *
 * @author l1j-jp
 */
public class SoulTowerThread extends Thread {
    private static final Logger _log = Logger.getLogger(SoulTowerThread.class);
    short mapId;
    L1PcInstance pc;
    L1DoorInstance door0;
    ArrayList<L1NpcInstance> list = new ArrayList<L1NpcInstance>();

    public SoulTowerThread(final int mapId, final L1PcInstance pc) {
        this.mapId = (short) mapId;
        this.pc = pc;
    }

    public static void removeSoulTowerPoint(L1PcInstance pc) {
        for (int i = 640319; i <= 640325; i++) {
            final L1ItemInstance[] itemlist = pc.getInventory().findItemsId(i);
            if (itemlist != null) {
                for (final L1ItemInstance item : itemlist) {
                    pc.getInventory().removeItem(item);
                }
            }
        }
    }

    @Override
    public void run() {
        System.out.println("屍魂塔副本線程開始 地圖：" + mapId + " 玩家：" + pc.getName());
        try {
            final long begin = System.currentTimeMillis();
            L1Teleport.teleport(pc, 32869, 32923, mapId, 2, true);
            TimeUnit.MILLISECONDS.sleep(100);
            //SoulTowerTable.get().getSoulTowerRanked(pc);
            pc.setSoulTime(0);
            pc.startTimeMap();
            //pc.sendPackets(new S_PacketBox(S_PacketBox.SOULTOWERSTART, pc.getMapsTime(4001)));
            pc.sendPackets(new S_PacketBox(S_PacketBox.SOULTOWERSTART, 1800));
            pc.sendPackets(new S_PacketBox(S_PacketBox.GUI_VISUAL_EFFECT, 2));
            pc.sendPackets(new S_PacketBox(S_PacketBox.GUI_VISUAL_EFFECT, 7));
            spawn(new L1Location(32801, 32812, mapId), 190045, 1);//詭異的商人^歐汀
            spawn(new L1Location(32756, 32872, mapId), 190045, 1);//詭異的商人^歐汀
            // 關卡1的門
            final L1DoorInstance door1 = spawnDoor(0, 12632, 32843, 32878, mapId, 0, 1, false, 32843, 32846);
            door1.close();
            spawn(new L1Location(32849, 32923, mapId), 190031, 10);//屍魂的怨靈
            spawn(new L1Location(32844, 32905, mapId), 190029, 6);//屍魂的怨靈
            spawn(new L1Location(32844, 32905, mapId), 190030, 6);//屍魂的怨靈
            //			ArrayList<L1NpcInstance> list = spawn(new L1Location(32770, 32876, mapId), 82553, 1);//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32770, 32877, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32770, 32878, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32772, 32876, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32772, 32879, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32773, 32876, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32773, 32878, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32773, 32879, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32774, 32877, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32774, 32878, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32774, 32879, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32775, 32876, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32775, 32879, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32776, 32877, mapId), 82553, 1));//屍魂塔木箱
            //			list.addAll(spawn(new L1Location(32776, 32879, mapId), 82553, 1));//屍魂塔木箱
            //			for (final L1NpcInstance npc : list) {
            //				if (npc instanceof L1DoorInstance) {
            //					((L1DoorInstance) npc).setDoorId(4001);
            //				}
            //			}
            TimeUnit.MILLISECONDS.sleep(10000);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18344"));
            TimeUnit.MILLISECONDS.sleep(3000);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18327"));
            TimeUnit.MILLISECONDS.sleep(3000);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18328"));
            list = spawn(new L1Location(32843, 32894, mapId), 190034, 2);//屍魂的怨靈
            isKillNpc(list);
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18329"));
            spawn(new L1Location(32843, 32886, mapId), 190034, 2);//屍魂的怨靈
            spawn(new L1Location(32843, 32886, mapId), 190029, 6);//屍魂的怨靈
            list = spawn(new L1Location(32843, 32885, mapId), 190032, 1);//惡靈守門人
            isKillNpc(list);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            door1.open();
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18338"));
            // 關卡2的門
            final L1DoorInstance door2 = spawnDoor(0, 6336, 32842, 32848, mapId, 0, 1, false, 32841, 32844);
            door2.close();
            TimeUnit.MILLISECONDS.sleep(3000);
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18347"));
            list = spawn(new L1Location(32844, 32862, mapId), 190034, 4);
            list.addAll(spawn(new L1Location(32844, 32862, mapId), 190035, 4));//屍魂的怨靈
            list.addAll(spawn(new L1Location(32844, 32862, mapId), 190036, 4));//屍魂的怨靈
            spawn(new L1Location(32844, 32862, mapId), 190029, 6);//屍魂的怨靈
            isKillNpc(list);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18330"));
            spawn(new L1Location(32859, 32858, mapId), 190034, 2);//屍魂的怨靈
            list = spawn(new L1Location(32859, 32858, mapId), 190037, 1);//執行者巴羅卡勒
            isKillNpc(list);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18333"));
            door2.open();
            // 關卡3的門
            final L1DoorInstance door3 = spawnDoor(0, 12754, 32820, 32812, mapId, 0, 1, false, 32811, 32812, 1);
            // 關卡4的門
            final L1DoorInstance door4 = spawnDoor(0, 12754, 32820, 32813, mapId, 0, 1, false, 32812, 32813, 1);
            // 關卡5的門
            final L1DoorInstance door5 = spawnDoor(0, 12754, 32820, 32814, mapId, 0, 1, false, 32813, 32814, 1);
            door3.close();
            door4.close();
            door5.close();
            TimeUnit.MILLISECONDS.sleep(5000);
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18331"));
            spawn(new L1Location(32846, 32814, mapId), 190035, 2);//屍魂的怨靈
            spawn(new L1Location(32846, 32814, mapId), 190029, 3);//屍魂的怨靈
            spawn(new L1Location(32846, 32814, mapId), 190034, 2);//屍魂的怨靈
            spawn(new L1Location(32864, 32804, mapId), 190035, 2);//屍魂的怨靈
            spawn(new L1Location(32864, 32804, mapId), 190030, 3);//屍魂的怨靈
            spawn(new L1Location(32864, 32804, mapId), 190034, 2);//屍魂的怨靈
            spawn(new L1Location(32850, 32801, mapId), 190035, 2);//屍魂的怨靈
            spawn(new L1Location(32850, 32801, mapId), 190029, 3);//屍魂的怨靈
            spawn(new L1Location(32850, 32801, mapId), 190034, 2);//屍魂的怨靈
            spawn(new L1Location(32831, 32799, mapId), 190035, 2);//屍魂的怨靈
            spawn(new L1Location(32831, 32799, mapId), 190030, 3);//屍魂的怨靈
            spawn(new L1Location(32831, 32799, mapId), 190034, 2);//屍魂的怨靈
            list = spawn(new L1Location(32833, 32809, mapId), 190034, 1);//屍魂的怨靈
            isKillNpc(list);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            door3.open();
            door4.open();
            door5.open();
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18348"));
            // 關卡6的門
            final L1DoorInstance door6 = spawnDoor(0, 12711, 32790, 32815, mapId, 0, 1, false, 32814, 32818, 1);
            door6.close();
            spawn(new L1Location(32800, 32816, mapId), 190034, 2);//屍魂的怨靈
            spawn(new L1Location(32800, 32816, mapId), 190036, 3);//屍魂的怨靈
            list = spawn(new L1Location(32800, 32816, mapId), 190038, 1);//混亂的威裡諾
            isKillNpc(list);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            door6.open();
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18340"));
            ArrayList<L1DoorInstance> doorlist = new ArrayList<L1DoorInstance>();
            for (int i = 32769; i <= 32777; i++) {
                final L1DoorInstance door7 = spawnDoor(0, 12754, i, 32829, mapId, 0, 1, false, i, i, 0);
                door7.close();
                doorlist.add(door7);
            }
            (door0 = spawnDoor(0, 12711, 32760, 32819, mapId, 0, 1, false, 32818, 32822, 1)).close();
            TimeUnit.MILLISECONDS.sleep(3000);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18349"));
            spawn(new L1Location(32776, 32818, mapId), 190036, 4);//屍魂的怨靈
            spawn(new L1Location(32776, 32818, mapId), 190030, 4);//屍魂的怨靈
            spawn(new L1Location(32776, 32818, mapId), 190034, 4);//屍魂的怨靈
            spawn(new L1Location(32776, 32818, mapId), 190035, 4);//屍魂的怨靈
            list = spawn(new L1Location(32776, 32818, mapId), 190029, 4);//屍魂的怨靈
            isKillNpc(list);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            for (final L1DoorInstance door8 : doorlist) {
                door8.open();
            }
            doorlist = new ArrayList<L1DoorInstance>();
            for (int i = 32763; i <= 32776; i++) {
                final L1DoorInstance door7 = spawnDoor(0, 12754, i, 32843, mapId, 0, 1, false, i, i, 0);
                door7.close();
                doorlist.add(door7);
            }
            TimeUnit.MILLISECONDS.sleep(5000);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18332"));
            spawn(new L1Location(32772, 32835, mapId), 190035, 4);//屍魂的怨靈
            spawn(new L1Location(32772, 32835, mapId), 190036, 4);//屍魂的怨靈
            spawn(new L1Location(32772, 32835, mapId), 190030, 4);//屍魂的怨靈
            spawn(new L1Location(32772, 32835, mapId), 190034, 4);//屍魂的怨靈
            spawn(new L1Location(32772, 32835, mapId), 190029, 4);//屍魂的怨靈
            TimeUnit.MILLISECONDS.sleep(15000);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            for (final L1DoorInstance door8 : doorlist) {
                door8.open();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18333"));
            doorlist = new ArrayList<L1DoorInstance>();
            for (int i = 32749; i < 32751; i++) {
                // 火堆
                final L1DoorInstance door7 = spawnDoor(0, 12754, i, 32881, mapId, 0, 1, false, i, i, 0);
                door7.close();
                doorlist.add(door7);
            }
            spawn(new L1Location(32769, 32854, mapId), 190036, 2);//屍魂的怨靈
            spawn(new L1Location(32769, 32854, mapId), 190030, 2);//屍魂的怨靈
            spawn(new L1Location(32769, 32854, mapId), 190034, 2);//屍魂的怨靈
            spawn(new L1Location(32769, 32854, mapId), 190035, 2);//屍魂的怨靈
            spawn(new L1Location(32769, 32854, mapId), 190029, 2);//屍魂的怨靈
            list = spawn(new L1Location(32769, 32854, mapId), 190039, 1);//百獸王卡諾圖
            isKillNpc(list);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18341"));
            for (final L1DoorInstance door8 : doorlist) {
                door8.open();
            }
            final L1DoorInstance door9 = spawnDoor(0, 12711, 32769, 32905, mapId, 0, 1, false, 32904, 32908, 1);
            door9.close();
            TimeUnit.MILLISECONDS.sleep(10000);
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18334"));
            spawn(new L1Location(32753, 32898, mapId), 190036, 6);//屍魂的怨靈
            spawn(new L1Location(32753, 32898, mapId), 190030, 6);//屍魂的怨靈
            spawn(new L1Location(32753, 32898, mapId), 190034, 6);//屍魂的怨靈
            spawn(new L1Location(32753, 32898, mapId), 190035, 6);//屍魂的怨靈
            spawn(new L1Location(32753, 32898, mapId), 190029, 10);//屍魂的怨靈
            TimeUnit.MILLISECONDS.sleep(15000);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18335"));
            list = spawn(new L1Location(32765, 32906, mapId), 190033, 1);//惡靈守門人
            isKillNpc(list);
            door9.open();
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18342"));
            TimeUnit.MILLISECONDS.sleep(3000);
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18336"));
            spawn(new L1Location(32786, 32906, mapId), 190036, 4);//屍魂的怨靈
            spawn(new L1Location(32786, 32906, mapId), 190030, 4);//屍魂的怨靈
            spawn(new L1Location(32786, 32906, mapId), 190034, 4);//屍魂的怨靈
            spawn(new L1Location(32786, 32906, mapId), 190035, 4);//屍魂的怨靈
            spawn(new L1Location(32786, 32906, mapId), 190029, 4);//屍魂的怨靈
            TimeUnit.MILLISECONDS.sleep(10000);
            // 惡魔: 可笑! 讓我來親自消滅你!
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18337"));
            list = spawn(new L1Location(32786, 32906, mapId), 190041, 1);// 魔王安德雷亞
            isKillNpc(list);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            pc.sendPackets(new S_PacketBox(S_PacketBox.GUI_VISUAL_EFFECT, 2));
            pc.sendPackets(new S_PacketBox(S_PacketBox.GUI_VISUAL_EFFECT, 7));
            //pc.sendPackets(new S_SkillSound(pc.getId(), 10274));// 煙花
            final int usertime = (int) ((System.currentTimeMillis() - begin) / 1000);
            pc.sendPackets(new S_PacketBox(S_PacketBox.SOULTOWEREND, usertime));// 消滅完畢
            //L1SpawnUtil.spawn(pc, 99101, 1, 300);// 箱子
            SoulTowerTable.getInstance().updateRank(pc, usertime);//更新排名
            // 惡魔: 呃啊啊!!! 我是不會這樣就倒下的!
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18343"));
            TimeUnit.MILLISECONDS.sleep(2000);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            // 天使: 真的好以你為榮.
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18574"));
            TimeUnit.MILLISECONDS.sleep(2000);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            // 天使: 用這個也可以使屍魂塔獲得封印.
            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "\\f=$18575"));
            TimeUnit.MILLISECONDS.sleep(2000);
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
            }
            for (int i = 0; i < 10; i++) {
                // 倒數10秒後以傳送術移動至村莊
                pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, 2, "$" + (18576 + i)));
                TimeUnit.MILLISECONDS.sleep(1000);
            }
            quitSoulTower();
        } catch (final InterruptedException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 召喚NPC
     *
     * @param loc
     * @param npcid
     * @param count
     * @return
     */
    private ArrayList<L1NpcInstance> spawn(final L1Location loc, final int npcid, final int count) {
        final ArrayList<L1NpcInstance> list = new ArrayList<L1NpcInstance>();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                list.add(spawnNpc(loc, npcid, 10));
            }
        } else {
            list.add(spawnNpc(loc, npcid, 1));
        }
        return list;
    }

    /**
     * 是否在規定時間內清除怪物
     *
     * @param list
     * @return
     */
    private int isKillNpc(final ArrayList<L1NpcInstance> list) {
        boolean isAllKill = true;
        while (isAllKill) {
            if (isSoulTower()) {//判斷玩家是否在屍魂塔副本
                quitSoulTower();
                return -1;
            }
            boolean isAllDeath = false;
            for (final L1NpcInstance npc : list) {
                if (!npc.isDead()) {
                    break;
                }
                isAllDeath = npc.isDead();
            }
            if (isAllDeath) {
                isAllKill = false;
                return 0;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (final InterruptedException e) {
                // TODO 自動生成的 catch 塊
            }
        }
        quitSoulTower();
        return -1;
    }

    /**
     * 判斷玩家是否在屍魂塔副本
     *
     * @return 不在副本中返回false
     * @return 在副本中返回true
     */
    private boolean isSoulTower() {
        if (pc == null) {
            return true;
        }
        if (pc.getOnlineStatus() == 0) {
            return true;
        }
        return pc.getMapId() != mapId;
    }

    /**
     * 退出屍魂塔副本
     */
    private void quitSoulTower() {
        //刪除副本道具
        // 640319=下層雷擊爆彈
        // 640320=下層旋風爆彈
        // 640321=下層戰鬥強化卷軸
        // 640322=下層防禦強化卷軸
        // 640323=下層治癒藥水
        // 640324=下層強力治癒藥水
        // 640325=下層魔力藥水
        removeSoulTowerPoint(pc);
        if (pc != null && pc.getMapId() == mapId) {
            //pc.setSoulTower(0);
            pc.stopTimeMap();
            L1Teleport.teleport(pc, 33703, 32502, (short) 4, 5, true);
        }
        for (L1Object object : World.get().getVisibleObjects(mapId).values()) {
            if (object instanceof L1ItemInstance) {
                final L1ItemInstance item = (L1ItemInstance) object;
                final L1Inventory groundInventory = World.get().getInventory(item.getX(), item.getY(), item.getMapId());
                groundInventory.removeItem(item);
            }
        }
        /* 減少遍歷物品數量 by 聖子默默
        for (final L1ItemInstance item : WorldItem.get().all()) {
            if (item.getMapId() == mapId) {
                final L1Inventory groundInventory = World.get().getInventory(item.getX(), item.getY(), item.getMapId());
                groundInventory.removeItem(item);
            }
        }*/
        World.get().closeMap(mapId);
        L1SoulTower.get().mapStat[(mapId - 4001)] = false;
        System.out.println("屍魂塔副本線程結束 地圖：" + mapId);
        interrupt();
    }

    /**
     * 召喚NPC
     *
     * @param loc
     * @param npcid
     * @param randomRange
     * @return
     */
    private L1NpcInstance spawnNpc(final L1Location loc, final int npcid, final int randomRange) {
        final L1Npc l1npc = NpcTable.get().getTemplate(npcid);
        if (l1npc == null) {
            _log.warn("召喚的NPCID:" + npcid + "不存在");
            return null;
        }
        L1NpcInstance field;
        try {
            field = NpcTable.get().newNpcInstance(npcid);
            //		} catch (final ClassNotFoundException e) {
            //			e.printStackTrace();
            //		} catch (final IllegalAccessException e) {
            //			e.printStackTrace();
            //		} catch (final InstantiationException e) {
            //			e.printStackTrace();
            //		} catch (final InvocationTargetException e) {
            //			e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            _log.error(e.getLocalizedMessage(), e);
            return null;
        }
        field.setId(IdFactory.get().nextId());
        field.setMap((short) loc.getMapId());
        int tryCount = 0;
        do {
            tryCount++;
            field.setX(loc.getX() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
            field.setY(loc.getY() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
            if (field.getMap().isInMap(field.getLocation()) && field.getMap().isPassable(field.getX(), field.getY())) {
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(2);
            } catch (final InterruptedException e) {
            }
        } while (tryCount < 50);
        if (tryCount >= 50) {
            field.getLocation().set(loc);
        }
        field.setHomeX(field.getX());
        field.setHomeY(field.getY());
        field.setHeading(5);
        field.setLightSize(l1npc.getLightSize());
        World.get().storeObject(field);
        World.get().addVisibleObject(field);
        return field;
    }

    /**
     * 召喚門
     *
     * @param doorId
     * @param gfxId
     * @param locx
     * @param locy
     * @param mapid
     * @param hp
     * @param keeper
     * @param isopening
     * @param left_edge_location
     * @param right_edge_location
     * @return
     */
    public L1DoorInstance spawnDoor(final int doorId, final int gfxId, final int locx, final int locy, final short mapid, final int hp, final int keeper, final boolean isopening, final int left_edge_location, final int right_edge_location) {
        return spawnDoor(doorId, gfxId, locx, locy, mapid, hp, keeper, isopening, left_edge_location, right_edge_location, 0);
    }

    /**
     * 召喚門
     *
     * @param doorId
     * @param gfxId
     * @param locx
     * @param locy
     * @param mapid
     * @param hp
     * @param keeper
     * @param isopening
     * @param left_edge_location
     * @param right_edge_location
     * @param direction
     * @return
     */
    public L1DoorInstance spawnDoor(final int doorId, final int gfxId, final int locx, final int locy, final short mapid, final int hp, final int keeper, final boolean isopening, final int left_edge_location, final int right_edge_location, final int direction) {
        for (final L1DoorInstance temp : DoorSpawnTable.get().getDoorList()) {
            if (temp.getMapId() == mapid && temp.getHomeX() == locx && temp.getHomeY() == locy) {
                return temp;
            }
        }
        L1DoorInstance door;
        try {
            door = (L1DoorInstance) NpcTable.get().newNpcInstance(81158); // 81158-門
            //		} catch (final ClassNotFoundException e) {
            //			e.printStackTrace();
            //		} catch (final IllegalAccessException e) {
            //			e.printStackTrace();
            //		} catch (final InstantiationException e) {
            //			e.printStackTrace();
            //		} catch (final InvocationTargetException e) {
            //			e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            _log.error(e.getLocalizedMessage(), e);
            return null;
        }
        door.setId(IdFactoryNpc.get().nextId());
        door.setDoorId(doorId);
        door.setGfxId(gfxId);
        door.setX(locx);
        door.setY(locy);
        door.setMap(mapid);
        door.setHomeX(locx);
        door.setHomeY(locy);
        door.setDirection(direction);
        door.setLeftEdgeLocation(left_edge_location);
        door.setRightEdgeLocation(right_edge_location);
        door.setMaxHp(hp);
        door.setCurrentHp(hp);
        door.setKeeperId(keeper);
        //door.setOpenStatus(ActionCodes.ACTION_Open);
        World.get().storeObject(door);
        World.get().addVisibleObject(door);
        DoorSpawnTable.get().addDoor(door);
        return door;
    }
}
