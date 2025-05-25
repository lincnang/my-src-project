package com.lineage.server.command.executor;

import com.add.system.L1BlendTable;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.timecontroller.server.ServerWarTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1Reload implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1Reload.class);

    public static L1CommandExecutor getInstance() {
        return new L1Reload();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        if (arg.equalsIgnoreCase("droplist")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入掉寶資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入掉寶資料數量。"));
            }
            DropTable.get().load();
        } else if (arg.equalsIgnoreCase("shop")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入商店販賣資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入商店販賣資料數量。"));
            }
            ShopTable.get().load();
        } else if (arg.equalsIgnoreCase("armor")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入防具資料。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入防具資料。"));
            }
            ItemTable.get().loadarmors();
        } else if (arg.equalsIgnoreCase("weapon")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入武器資料。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入武器資料。"));
            }
            ItemTable.get().loadweapons();
        } else if (arg.equalsIgnoreCase("etcitem")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入道具資料。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入道具資料。"));
            }
            ItemTable.get().loaditems();
        } else if (arg.equalsIgnoreCase("npcaction")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入NPC對話資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入NPC對話資料數量。"));
            }
            NPCTalkDataTable.get().load();
        } else if (arg.equalsIgnoreCase("server_shopx")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入禁止拍賣物品資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入禁止拍賣物品資料數量。"));
            }
            ShopXTable.get().load();

        } else if (arg.equalsIgnoreCase("skills")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入技能設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入技能設置資料數量。"));
            }
            SkillsTable.get().load();
        } else if (arg.equalsIgnoreCase("server_event")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入活動設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入活動設置資料數量。"));
            }
            EventTable.get().load();
        } else if (arg.equalsIgnoreCase("iplimit")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入Ip設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入Ip設置資料數量。"));
            }
            EventTable.get().loadIplimit();
        } else if (arg.equalsIgnoreCase("mobskill")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入MOB技能資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入MOB技能資料數量。"));
            }
            MobSkillTable.getInstance().loadMobSkillData();
        } else if (arg.equalsIgnoreCase("weapon_skill")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入技能武器設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入技能武器設置資料數量。"));
            }
            WeaponSkillPowerTable.get().load();
        } else if (arg.equalsIgnoreCase("dungeon")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入地圖切換點設置數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入地圖切換點設置數量。"));
            }
            DungeonTable.get().load();
        } else if (arg.equalsIgnoreCase("itemblend")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入道具製造資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入道具製造資料數量。"));
            }
            L1BlendTable.getInstance().loadBlendTable();
        } else if (arg.equalsIgnoreCase("npc")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入NPC設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入NPC設置資料數量。"));
            }
            NpcTable.get().load();
        } else if (arg.equalsIgnoreCase("etcitem_box")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入開箱設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入開箱設置資料數量。"));
            }
            ItemBoxTable.get().load();
        } else if (arg.equalsIgnoreCase("server_fishing")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入釣魚設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入釣魚設置資料數量。"));
            }
            FishingTable.get().load();
        } else if (arg.equalsIgnoreCase("server_castle")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入城堡設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入城堡設置資料數量。"));
            }
            CastleReading.get().load();
            final ServerWarTimer warTimer = new ServerWarTimer();
            warTimer.start();
        } else if (arg.equalsIgnoreCase("gamemall")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入商城設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入商城設置資料數量。"));
            }
            T_GameMallTable.get().load();
        } else if (arg.equalsIgnoreCase("xml")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入xml資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入xml資料數量。"));
            }
            T_CraftConfigTable.get().load();
        } else if (arg.equalsIgnoreCase("hpr")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入npc hpr設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入npc mpr設置資料數量。"));
            }
            //			NpcHprTimer npcHprTimer = new NpcHprTimer();
            //			npcHprTimer.start();
            //			//TimeUnit.MILLISECONDS.sleep(50L);
            //
            //			NpcMprTimer npcMprTimer = new NpcMprTimer();
            //			npcMprTimer.start();
            //			//TimeUnit.MILLISECONDS.sleep(50L);
        } else if (arg.equalsIgnoreCase("map")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入map設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入map設置資料數量。"));
            }
            MapsTable.get().load();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.command.executor.L1Reload JD-Core Version: 0.6.2
 */