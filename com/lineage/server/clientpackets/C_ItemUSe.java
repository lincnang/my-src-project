package com.lineage.server.clientpackets;

import com.add.Tsai.collect;
import com.add.Tsai.collectPolySet;
import com.add.Tsai.collectSetTable;
import com.add.system.L1ItemNpc;
import com.lineage.DatabaseFactory;
import com.lineage.data.ItemClass;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.datatables.ItemBoxTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1ItemDelay;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Box;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.templates.L1EtcItem;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

import static com.lineage.server.model.skill.L1SkillId.*;

public class C_ItemUSe extends ClientBasePacket {
    public static final int[] hextable = {0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x8b, 0x8c, 0x8d, 0x8e, 0x8f, 0x90, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0x9b, 0x9c, 0x9d, 0x9e, 0x9f, 0xa0, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, 0xad, 0xae, 0xaf, 0xb0, 0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf, 0xc0, 0xc1, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xcb, 0xcc, 0xcd, 0xce, 0xcf, 0xd0, 0xd1, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xdb, 0xdc, 0xdd, 0xde, 0xdf, 0xe0, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xeb, 0xec, 0xed, 0xee, 0xef, 0xf0, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe, 0xff};
    private static final Log _log = LogFactory.getLog(C_ItemUSe.class);

    public void start(byte[] decrypt, ClientExecutor client) {//src029
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            // 鬼魂模式
            if (pc.isGhost()) {
                return;
            }
            // 例外狀況:人物死亡
            if (pc.isDead()) {
                return;
            }
            // 例外狀況:傳送鎖定狀態
            if (pc.isTeleport()) {
                pc.setTeleport(false);
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                return;
            }
            // 使用物件的OBJID
            final int itemObjid = this.readD();
            // 取回使用物件
            final L1ItemInstance useItem = pc.getInventory().getItem(itemObjid);
            // 例外狀況:物件為空
            if (useItem == null) {
                return;
            }
            // 設置使用者OBJID
            useItem.set_char_objid(pc.getId());
            // 收藏
            collect collect = pc.get_collectTemp();
            if (collect != null) {
                if (collect.get_needId() == useItem.getItemId() && collect.get_needCount() == useItem.getCount() && collect.get_needEnchant() == useItem.getEnchantLevel()) {
                    pc.getInventory().removeItem(useItem);
                    pc.set_collectTemp(null);
                    pc.getQuest().set_step(collect.getQuestId(), 255);
                    pc.sendPackets(new S_SystemMessage("\\aG收藏已登入"));
                    // 檢查收藏套卡
                    for (int i = 0; i <= collectSetTable.get().getCollectSize(); i++) {//檢查收藏組合DB資料
                        final collectPolySet collects = collectSetTable.get().getCard(i);
                        if (collects != null) {
                            int k = 0;
                            for (int j = 0; j < collects.getNeedQuest().length; j++) {
                                if (pc.getQuest().get_step(collects.getNeedQuest()[j]) != 0) {
                                    k++;
                                }
                            }
                            if (k == collects.getNeedQuest().length) {
                                //pc.getQuest().set_end(cards.getQuestId());
                                pc.getQuest().set_step(collects.getQuestId(), 255);
                                pc.sendPackets(new S_SystemMessage("\\aG恭喜你啟動此收藏能力"));
                            }
                        }
                    }
                    return;
                }
                pc.sendPackets(new S_SystemMessage("\\aB請選擇正確的收藏道具"));
                pc.set_collectTemp(null);
                return;
            }
            boolean isStop = false;
            // 再生聖殿 1樓/2樓/3樓
            if (pc.getMapId() == CKEWLv50_1.MAPID) {
            }
            // 解除絕對屏障
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
                pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
                pc.startHpRegeneration();
                pc.startMpRegeneration();
            }
            /** [原碼] 物品召喚 NPC */
            L1ItemNpc.forRequestItemUSe(client, useItem);
            // 例外狀況:該地圖不允許使用道具
            if (!pc.getMap().isUsableItem()) {
                // System.out.println("例外狀況:該地圖不允許使用道具");
                // 563 \f1你無法在這個地方使用。
                pc.sendPackets(new S_ServerMessage(563));
                isStop = true;
            }
            // 無法攻擊/使用道具/技能/回城的狀態
            if (pc.isParalyzedX() && !isStop) {
                isStop = true;
            }
            if (!isStop) {
                switch (pc.getMapId()) {
                    case 22:// 傑瑞德的試煉地監
                        switch (useItem.getItemId()) {
                            case 30:// 紅騎士之劍
                            case 40017:// 解毒藥水
                                break;
                            default:
                                // 563 \f1你無法在這個地方使用。
                                pc.sendPackets(new S_ServerMessage(563));
                                isStop = true;
                                break;
                        }
                }
            }
            if (!CheckUtil.getUseItemAll(pc) && !isStop) {
                isStop = true;
            }
            if (pc.isPrivateShop() && !isStop) {
                isStop = true;
            }
            if (isStop) {
                pc.setTeleport(false);
                pc.sendPackets(new S_Paralysis(7, false));
                return;
            }
            if (pc.getCurrentHp() > 0) {// 目前血量大於0
                int delay_id = 0;
                if (useItem.getItem().getType2() == 0) { // 種別：一般使用物品：normal
                    delay_id = ((L1EtcItem) useItem.getItem()).get_delayid();
                    // 502:道具禁止使用
                    if (pc.hasItemDelay(L1ItemDelay.ITEM)) {
                        return;
                    }
                }
                if (delay_id != 0) {
                    // 延遲作用中
                    if (pc.hasItemDelay(delay_id)) {
                        // System.out.println("延遲作用中");
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                        return;
                    }
                }
                // 例外狀況:數量異常
                if (useItem.getCount() <= 0) {
                    // \f1沒有具有 %0%o。
                    pc.sendPackets(new S_ServerMessage(329, useItem.getLogName()));
                    return;
                }
                // 取回可用等級限制
                final int min = useItem.getItem().getMinLevel();
                final int max = useItem.getItem().getMaxLevel();
                // 例外狀況:等級不足
                if (min != 0 && min > pc.getLevel()) {// 等級不足
                    if (min < 50) {
                        // 672 等級%d以上才可使用此道具。
                        final S_PacketBoxItemLv toUser = new S_PacketBoxItemLv(min, 0);
                        pc.sendPackets(toUser);
                    } else {
                        // 318 等級 %0以上才可使用此道具。
                        final S_ServerMessage toUser = new S_ServerMessage(318, String.valueOf(min));
                        pc.sendPackets(toUser);
                    }
                    return;
                }
                // 例外狀況:等級過高
                if (max != 0 && max < pc.getLevel()) {// 等級過高
                    if (useItem.getItem().getType2() == 0 || !useItem.isEquipped()) {
                        final S_PacketBoxItemLv toUser = new S_PacketBoxItemLv(0, max);
                        pc.sendPackets(toUser);
                        return;
                    }
                }
                // 最低使用需求 (轉生次數) by terry0412
                if (pc.getMeteLevel() < useItem.getItem().getMeteLevel()) {
                    pc.sendPackets(new S_SystemMessage(useItem.getItem().getMeteLevel() + "轉以上才可使用此道具。"));
                    return;
                }
                // 最高使用需求 (轉生次數) by terry0412
                if (pc.getMeteLevel() > useItem.getItem().getMeteLevelMAX()) {
                    pc.sendPackets(new S_SystemMessage(useItem.getItem().getMeteLevel() + "轉以下才可使用此道具。"));
                    return;
                }
                // 陣營使用判斷欄位 (1-魏.2-蜀.4-吳.7-共用) by terry0412
                if (pc.get_c_power() != null) {
                    final int use_camp = useItem.getItem().getCampSet();
                    if (use_camp > 0) {
                        // 陣營
                        final int c1_type = pc.get_c_power().get_c1_type();
                        if ((use_camp & c1_type) != c1_type) {
                            pc.sendPackets(new S_SystemMessage("你的陣營不符合要求，因此無法使用此道具。"));
                            return;
                        }
                    }
                }
                // 判定職業 by terry0412
                if (!this.useItem(pc, useItem)) {
                    return;
                }
                // 例外狀況:具有時間設置
                boolean isDelayEffect = false;
                if (useItem.getItem().getType2() == 0) {
                    final int delayEffect = ((L1EtcItem) useItem.getItem()).get_delayEffect();
                    if (delayEffect > 0) {
                        isDelayEffect = true;
                        final Timestamp lastUsed = useItem.getLastUsed();
                        if (lastUsed != null) {
                            final Calendar cal = Calendar.getInstance();
                            long useTime = (cal.getTimeInMillis() - lastUsed.getTime()) / 1000;
                            if (useTime <= delayEffect) {
                                // 轉換為需等待時間
                                useTime = delayEffect - useTime; /// 60;
                                // 時間數字 轉換為字串
                                final String useTimeS = /*useItem.getLogName() + " " +*/ String.valueOf(useTime / 60);
                                // 1139 %0 分鐘之內無法使用。
                                pc.sendPackets(new S_ServerMessage(useTimeS + "分鐘內之內無法使用"));
                                return;
                            }
                        }
                    }
                }
                int use_type = useItem.getItem().getUseType();
                boolean isClass = false;
                String className = useItem.getItem().getclassname();
                if (!className.equals("0")) {
                    isClass = true;
                }
                switch (use_type) {
                    case -11:// 對讀取方法調用無法分類的物品
                        L1Doll doll = DollPowerTable.get().get_type(useItem.getItemId());
                        int maxusetime = useItem.getItem().getMaxUseTime();
                        if (doll != null && maxusetime > 0) {// 魔法娃娃資料不為空且使用秒數大於0
                            if (useItem.getRemainingTime() <= 0) {
                                pc.sendPackets(new S_ServerMessage("\\fY必須先使用能量石進行充電。"));
                                return;
                            }
                        }
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case -10:// 加速藥水
                        if (!CheckUtil.getUseItem_heal(pc)) {
                            return;
                        }
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case -9:// 技術書
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case -8:// 料理書
                        if (isClass) {
                            try {
                                final int[] newData = new int[2];
                                newData[0] = this.readC();
                                newData[1] = this.readC();
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case -7:// 增HP道具
                        if (!CheckUtil.getUseItem_heal(pc)) {
                            return;
                        }
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case -6:// 增MP道具
                        if (!CheckUtil.getUseItem_heal(pc)) {
                            return;
                        }
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case -4:// 項圈
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case -3:// 飛刀
                        pc.getInventory().setSting(useItem.getItemId());
                        // 452 %0%s 被選擇了。
                        pc.sendPackets(new S_ServerMessage(452, useItem.getLogName()));
                        break;
                    case -2:// 箭
                        pc.getInventory().setArrow(useItem.getItemId());
                        // 452 %0%s 被選擇了。
                        pc.sendPackets(new S_ServerMessage(452, useItem.getLogName()));
                        break;
                    case -12:// 寵物用具
                    case -5:// 食人妖精競賽票 / 死亡競賽票
                    case -1:// 無法使用
                        // 無法使用訊息
                        pc.sendPackets(new S_ServerMessage(74, useItem.getLogName()));
                        break;
                    case 0:// 一般物品(直接施放)
                        String classname = useItem.getItem().getclassname();
                        if (classname.startsWith("teleport.Hang_fu")) { // 掛機符
                            int maxusetimegj = useItem.getItem().getMaxUseTime();
                            if (maxusetimegj > 0) {// 使用秒數大於0
                                if (useItem.getRemainingTime() <= 0) {
                                    pc.sendPackets(new S_ServerMessage("\\fY必須先使用掛機符充能石對其進行充電。"));
                                    return;
                                }
                            }
                        }
                        // 成長果實系統(Tam幣)
                        int use_objid = 0;
                        if (useItem.getItemId() == 642226 || useItem.getItemId() == 642227) {
                            use_objid = readD();
                            if (use_objid == 0) {
                                return;
                            }
                            int day = 0;
                            if (useItem.getItemId() == 642226) {
                                day = 7;
                            }
                            if (useItem.getItemId() == 642227) {
                                day = 30;
                            }
                            TamWindow(pc, use_objid, useItem, day);
                        }
                        // 成長果實系統(Tam幣)end
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case 1:// 武器
                        // 武器禁止使用
                        if (pc.hasItemDelay(L1ItemDelay.WEAPON)) {
                            return;
                        }
                        if (this.useItem(pc, useItem)) {// 職業可用
                            if (pc.isWarrior()) {
                                useWeaponForWarrior(pc, useItem);
                            } else {
                                useWeapon(pc, useItem);
                            }
                        }
                        break;
                    case 2:// 盔甲
                    case 18:// T恤
                    case 19:// 斗篷
                    case 20:// 手套
                    case 21:// 靴
                    case 22:// 頭盔
                    case 23:// 戒指
                    case 24:// 項鏈
                    case 25:// 盾牌
                    case 37:// 腰帶
                    case 40:// 耳環
                    case 43:// 符石 左
                    case 44:// 符中 右
                    case 45:// 符石 中
                    case 47:// 輔助道具
                    case 48:// 六芒星護身符
                    case 49:// 蒂蜜特祝福系列
                    case 51:// 蒂蜜特的符文
                    case 52:// vip
                    case 53:// vip2
                    case 70:// 脛甲
                        // 防具禁止使用
                        if (pc.hasItemDelay(L1ItemDelay.ARMOR)) {
                            return;
                        }
                        if (useItem.getItemId() >= 21157 && useItem.getItemId() <= 21178) {// 日輪印記系列
                            if (useItem.get_time() == null) {// 沒有期限資料
                                pc.sendPackets(new S_ServerMessage("\\fY必須先使用魔法氣息解除封印。"));
                                return;
                            }
                        }
                        if (useItem.getItemId() >= 301060 && useItem.getItemId() <= 301099) {// 龍耀符石系列
                            if (useItem.getRemainingTime() <= 0) {// 使用秒數小於等於0
                                pc.sendPackets(new S_ServerMessage("\\fY必須先使用龍的血液解除封印。"));
                                return;
                            }
                        }
                        if (this.useItem(pc, useItem)) {// 職業可用
                            this.useArmor(pc, useItem);
                        }
                        break;
                    case 3:// 創造怪物魔杖(無須選取目標) (無數量:沒有任何事情發生)
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case 4:// 希望魔杖(無須選取目標)(有數量:你想要什麼 / 無數量:沒有任何事情發生)
                        break;
                    case 5:// 魔杖類型(須選取目標)
                        //System.out.println("test use_type=5");
                        if (isClass) {
                            try {
                                final int[] newData = new int[3];
                                newData[0] = this.readD();// 選取目標的OBJID
                                newData[1] = this.readH();// X座標
                                newData[2] = this.readH();// Y座標
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                System.out.println(e.getMessage());
                                return;
                            }
                        }
                        break;
                    case 6:// 瞬間移動卷軸
                        if (!L1BuffUtil.getUseItemTeleport(pc)) {
                            pc.setTeleport(false);
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            pc.sendPackets(new S_SystemMessage("雙腳被捆綁的狀態下無法瞬間移動。"));
                            return;
                        }
                        if (!CheckUtil.getUseItem(pc)) {
                            return;
                        }
                        if (isClass) {
                            try {
                                // 日版記憶座標
                                int[] newData = new int[3];
                                newData[0] = readH();
                                newData[1] = readH();
                                newData[2] = readH();
                                ItemClass.get().item(newData, pc, useItem);
                                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 7:// 鑒定卷軸
                        if (isClass) {
                            try {
                                final int[] newData = new int[1];
                                newData[0] = this.readD();// 選取物件的OBJID
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 8:// 復活卷軸
                        if (isClass) {
                            try {
                                final int[] newData = new int[1];
                                newData[0] = this.readD();// 選取目標的OBJID
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 9:// 傳送回家的卷軸 / 血盟傳送卷軸
                        if (!L1BuffUtil.getUseItemTeleport(pc)) {
                            pc.setTeleport(false);
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            pc.sendPackets(new S_SystemMessage("雙腳被捆綁的狀態下無法瞬間移動。"));
                            return;
                        }
                        if (!CheckUtil.getUseItem(pc)) {
                            return;
                        }
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case 10:// 照明道具
                        // 取得道具編號
                        if (useItem.getRemainingTime() <= 0 && useItem.getItemId() != 40004) {
                            return;
                        }
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case 14:// 請選擇一個物品(道具欄位) 燈油/磨刀石/膠水
                        if (isClass) {
                            try {
                                final int[] newData = new int[1];
                                newData[0] = this.readD();// 選取物件的OBJID
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 15:// 哨子
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case 16:// 變形卷軸
                        if (isClass) {
                            final String cmd = this.readS();
                            pc.setText(cmd);// 選取的變身命令
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case 17:// 選取目標 (近距離)
                        if (isClass) {
                            try {
                                final int[] newData = new int[3];
                                newData[0] = this.readD();// 選取目標的OBJID
                                newData[1] = this.readH();// X座標
                                newData[2] = this.readH();// Y座標
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 27:// 對盔甲施法的卷軸
                    case 26:// 對武器施法的卷軸
                    case 46:// 飾品強化卷軸
                        if (isClass) {
                            try {
                                final int[] newData = new int[1];
                                // 選取目標的OBJID
                                newData[0] = this.readD();
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 28:// 空的魔法卷軸
                        if (isClass) {
                            try {
                                final int[] newData = new int[1];
                                newData[0] = this.readC();
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 29:// 瞬間移動卷軸(祝福)
                        if (!L1BuffUtil.getUseItemTeleport(pc)) {
                            pc.setTeleport(false);
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            pc.sendPackets(new S_SystemMessage("雙腳被捆綁的狀態下無法瞬間移動。"));
                            return;
                        }
                        if (!CheckUtil.getUseItem(pc)) {
                            return;
                        }
                        if (isClass) {
                            try {
                                // 日版記憶座標
                                int[] newData = new int[3];
                                newData[0] = readH();
                                newData[1] = readH();
                                newData[2] = readH();
                                ItemClass.get().item(newData, pc, useItem);
                                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 30:// 選取目標 (Ctrl 遠距離)
                        if (isClass) {
                            try {
                                final int obj = this.readD();// 選取目標的OBJID
                                final int[] newData = new int[]{obj};
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 12:// 信紙
                    case 31:// 聖誕卡片
                    case 33:// 情人節卡片
                    case 35:// 白色情人節卡片
                        if (isClass) {
                            try {
                                final int[] newData = new int[1];
                                newData[0] = this.readH();
                                pc.setText(this.readS());
                                pc.setTextByte(this.readByte());
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    /** [原碼] 潘朵拉抽抽樂 */
                    case 62:// 幸運券
                    case 65:// 轉運券
                        int use_count = 0;
                        final int itemId = useItem.getItem().getItemId();
                        use_count = this.readD();
                        if (itemId == 71440 || itemId == 71439) {
                            if (pc.getPandoraInventory().getItems().size() < 90) {
                                pc.getPandoraInventory().useLottery(useItem, use_count);
                                break;
                            }
                            pc.sendPackets(new S_SystemMessage("潘朵拉獎勵背包已滿，請領取完獎勵後再進行抽獎。"));
                            break;
                        }
                    case 13:// 信紙(打開)
                    case 32:// 聖誕卡片(打開)
                    case 34:// 情人節卡片(打開)
                    case 36:// 白色情人節卡片(打開)
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case 38:// 食物
                        if (!CheckUtil.getUseItem_heal(pc)) {
                            return;
                        }
                        if (isClass) {
                            ItemClass.get().item(null, pc, useItem);
                        }
                        break;
                    case 39:// 選取目標 (遠距離)
                        if (isClass) {
                            try {
                                final int[] newData = new int[3];
                                newData[0] = this.readD();// 選取目標的OBJID
                                newData[1] = this.readH();// X座標
                                newData[2] = this.readH();// Y座標
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 42:// 釣魚桿
                        if (isClass) {
                            try {
                                final int[] newData = new int[3];
                                newData[0] = this.readH();// X座標
                                newData[1] = this.readH();// Y座標
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    case 55:// 魔法娃娃成長藥劑
                        if (isClass) {
                            try {
                                final int[] newData = new int[1];
                                newData[0] = this.readD();// 選取物件的OBJID
                                ItemClass.get().item(newData, pc, useItem);
                            } catch (final Exception e) {
                                return;
                            }
                        }
                        break;
                    default:// 測試
                        _log.info("未處理的物品分類: " + use_type);
                        break;
                }
                if (useItem.getItem().getType2() == 0 && use_type == 0) { // 種別：一般道具
                    // 取得道具編號
                    final int itemId = useItem.getItem().getItemId();
                    switch (itemId) {
                        case 40630:// 迪哥的舊日記
                            pc.sendPackets(new S_NPCTalkReturn(pc, "diegodiary"));
                            break;
                        case 40663:// 兒子的信
                            pc.sendPackets(new S_NPCTalkReturn(pc, "sonsletter"));
                            break;
                        case 40701:// 小藏寶圖
                            if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 1) {
                                pc.sendPackets(new S_NPCTalkReturn(pc, "firsttmap"));
                            } else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 2) {
                                pc.sendPackets(new S_NPCTalkReturn(pc, "secondtmapa"));
                            } else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 3) {
                                pc.sendPackets(new S_NPCTalkReturn(pc, "secondtmapb"));
                            } else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 4) {
                                pc.sendPackets(new S_NPCTalkReturn(pc, "secondtmapc"));
                            } else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 5) {
                                pc.sendPackets(new S_NPCTalkReturn(pc, "thirdtmapd"));
                            } else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 6) {
                                pc.sendPackets(new S_NPCTalkReturn(pc, "thirdtmape"));
                            } else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 7) {
                                pc.sendPackets(new S_NPCTalkReturn(pc, "thirdtmapf"));
                            } else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 8) {
                                pc.sendPackets(new S_NPCTalkReturn(pc, "thirdtmapg"));
                            } else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 9) {
                                pc.sendPackets(new S_NPCTalkReturn(pc, "thirdtmaph"));
                            } else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 10) {
                                pc.sendPackets(new S_NPCTalkReturn(pc, "thirdtmapi"));
                            }
                            break;
                        case 41007:// 伊莉絲的命令書：靈魂之安息
                            pc.sendPackets(new S_NPCTalkReturn(pc, "erisscroll"));
                            break;
                        case 41009:// 伊莉絲的命令書：同盟之意志
                            pc.sendPackets(new S_NPCTalkReturn(pc, "erisscroll2"));
                            break;
                        case 41060:// 諾曼阿吐巴的信
                            pc.sendPackets(new S_NPCTalkReturn(pc, "nonames"));
                            break;
                        case 41061:// 妖精調查書：卡麥都達瑪拉
                            pc.sendPackets(new S_NPCTalkReturn(pc, "kames"));
                            break;
                        case 41062:// 人類調查書：巴庫摩那魯加
                            pc.sendPackets(new S_NPCTalkReturn(pc, "bakumos"));
                            break;
                        case 41063:// 精靈調查書：可普都達瑪拉
                            pc.sendPackets(new S_NPCTalkReturn(pc, "bukas"));
                            break;
                        case 41064:// 妖魔調查書：弧鄔牟那魯加
                            pc.sendPackets(new S_NPCTalkReturn(pc, "huwoomos"));
                            break;
                        case 41065:// 死亡之樹調查書：諾亞阿吐巴
                            pc.sendPackets(new S_NPCTalkReturn(pc, "noas"));
                            break;
                        case 41317:// 拉羅森的推薦書
                            pc.sendPackets(new S_NPCTalkReturn(pc, "rarson"));
                            break;
                        case 41318:// 可恩的便條紙
                            pc.sendPackets(new S_NPCTalkReturn(pc, "kuen"));
                            break;
                        case 41329:// 標本製作委託書
                            pc.sendPackets(new S_NPCTalkReturn(pc, "anirequest"));
                            break;
                        case 41340:// 傭兵團長多文的推薦書
                            pc.sendPackets(new S_NPCTalkReturn(pc, "tion"));
                            break;
                        case 41356:// 波倫的資源清單
                            pc.sendPackets(new S_NPCTalkReturn(pc, "rparum3"));
                            break;
                    }
                }
                if (useItem.getItem().getType2() == 0 && useItem.getItem().getType() == 16) { // treasure_box
                    // 容量確認
                    if (pc.getInventory().getSize() >= 160) {
                        // 263 \f1一個角色最多可攜帶180個道具。
                        pc.sendPackets(new S_ServerMessage(263));
                        return;
                    }
                    if (pc.getInventory().getWeight240() >= 180) {
                        // 82 此物品太重了，所以你無法攜帶。
                        pc.sendPackets(new S_ServerMessage(82));
                        return;
                    }
                    final ArrayList<L1Box> list = ItemBoxTable.get().get(pc, useItem);
                    if (list == null) {
                        ItemBoxTable.get().get_all(pc, useItem);
                    }
                }
                // 物件使用延遲設置
                if (isDelayEffect) {
                    final Timestamp ts = new Timestamp(System.currentTimeMillis());
                    // 設置使用時間
                    useItem.setLastUsed(ts);
                    pc.getInventory().updateItem(useItem, L1PcInventory.COL_DELAY_EFFECT);
                    pc.getInventory().saveItem(useItem, L1PcInventory.COL_DELAY_EFFECT);
                }
                try {
                    // 分類道具使用延遲
                    L1ItemDelay.onItemUse(client, useItem);
                } catch (final Exception e) {
                    _log.error("分類道具使用延遲異常:" + useItem.getItemId(), e);
                }
            }
        } catch (Exception localException1) {
        } finally {
            over();
        }
    }

    /**
     * 武器防具的使用<BR>
     *
     * @return 該職業可用傳回:true
     */
    private boolean useItem(final L1PcInstance pc, final L1ItemInstance useItem) {
        boolean isEquipped = false;
        //System.out.println(useItem.getItem().isUseWarrior());
        // 職業與物件的使用限制
        if (pc.isCrown()) {// 王族
            if (useItem.getItem().isUseRoyal()) {
                isEquipped = true;
            }
        } else if (pc.isKnight()) {// 騎士
            if (useItem.getItem().isUseKnight()) {
                isEquipped = true;
            }
        } else if (pc.isElf()) {// 精靈
            if (useItem.getItem().isUseElf()) {
                isEquipped = true;
            }
        } else if (pc.isWizard()) {// 法師
            if (useItem.getItem().isUseMage()) {
                isEquipped = true;
            }
        } else if (pc.isDarkelf()) {// 黑暗精靈;
            if (useItem.getItem().isUseDarkelf()) {
                isEquipped = true;
            }
        } else if (pc.isDragonKnight()) {// 龍騎士
            if (useItem.getItem().isUseDragonknight()) {
                isEquipped = true;
            }
        } else if (pc.isIllusionist()) {// 幻術師
            if (useItem.getItem().isUseIllusionist()) {
                isEquipped = true;
            }
        } else if (pc.isWarrior()) {// 戰士
            if (useItem.getItem().isUseWarrior()) {
                isEquipped = true;
            }
        }
        if (!isEquipped) {
            // 264 \f1你的職業無法使用此裝備。
            pc.sendPackets(new S_ServerMessage(264));
        }
        return isEquipped;
    }

    /**
     * 設置防具的裝備
     *
     */
    private void useArmor(L1PcInstance pc, L1ItemInstance armor) {
        //int itemid = armor.getItem().getItemId();
        int type = armor.getItem().getType();
        L1PcInventory pcInventory = pc.getInventory();
        boolean equipeSpace; // 是否還有欄位可裝備道具
        // 未滿 15 級的人物將無法裝備會減少「體質」點數的道具。
        if (pc.getLevel() < 15 && armor.getItem().get_addcon() < 0) {
            pc.sendPackets(new S_SystemMessage("未滿 15 級的人物將無法裝備會減少「體質」點數的道具。"));
            return;
        }
        // 各部位可使用裝備數判斷
        if (type == 9) { // type類型為9是戒指可戴2個,其他都只能戴1個  //src013
            if (!armor.isEquipped()) {
                if (pcInventory.getEquippedCountByItemId(armor.getItemId()) >= 2) {
                    pc.sendPackets(new S_ServerMessage(3278));
                    return;
                } else if (pcInventory.getEquippedCountByActivityItem() >= 2) {
                    pc.sendPackets(new S_ServerMessage(3279));
                    return;
                }
            }
            int count = 1;
            if ((pc.getRingsExpansion() & 1) == 1) {
                count++;
            }
            if ((pc.getRingsExpansion() & 2) == 2) {
                count++;
            }
            equipeSpace = pcInventory.getTypeEquipped(2, 9) <= count;
        } else if (type == 12) {// 耳環 //src014
            if (!armor.isEquipped()) {
                if (pcInventory.getEquippedCountByItemId(armor.getItemId()) >= 1) {
                    pc.sendPackets(new S_ServerMessage(3278));
                    return;
                } else if (pcInventory.getEquippedCountByActivityItem() >= 1) {
                    pc.sendPackets(new S_ServerMessage(3279));
                    return;
                }
            }
            int count = 0;
            if ((pc.getEarringsExpansion() & 1) == 1) {
                count++;
            }
            equipeSpace = pcInventory.getTypeEquipped(2, 12) <= count;
        } else {
            equipeSpace = pcInventory.getTypeEquipped(2, type) <= 0;
        }
        if (equipeSpace && !armor.isEquipped()) { // 使用した防具を装備していなくて、その装備箇所が空いている場合（装着を試みる）
            // 76級欄位判斷
			/*if ((type == 9) && pcInventory.getTypeEquipped(2, 9) == 2) {
				if (pc.getLevel() < 76) {
					pc.sendPackets(new S_ServerMessage(3253,"戒指"));
					// \f1すでに何かを装備しています。
					return;
				}
			}*/
            // 81級欄位判斷
			/*if ((type == 9) && pcInventory.getTypeEquipped(2, 9) == 3) {
				if (pc.getLevel() < 81) {
					pc.sendPackets(new S_ServerMessage(3253,"戒指"));
					// \f1すでに何かを装備しています。
					return;
				}
			}*/
            // 相同戒指數量判斷   //src013
			/*if (pcInventory.getTypeAndItemIdEquipped(2, 9, itemid) == 2) {
				pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
				return;
			}*/
            // 已經帶了 2個戒指
    	/*	if ((type == 9) && pcInventory.getTypeEquipped(2, 9) == 2) {
    				L1ItemInstance ring[] = new L1ItemInstance[2];
    				ring = pcInventory.getRingEquipped();// 裝備中界指陣列
    				//永恆戒指判斷
    				if( armor.getItem().getName().contains("永恆")){
    				if (ring[0].getItem().getName().contains("永恆")
    						&& ring[1].getItem().getName().contains("永恆")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    				//活動戒指判斷
    				if( armor.getItem().getName().contains("活動戒指")){
    				if (ring[0].getItem().getName().contains("活動戒指")
    						&& ring[1].getItem().getName().contains("活動戒指")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    		} */
            // 已經帶了 3個戒指
    	/*	if ((type == 9) && pcInventory.getTypeEquipped(2, 9) == 3) {
    				L1ItemInstance ring[] = new L1ItemInstance[3];
    				ring = pcInventory.getRingEquipped();// 裝備中界指陣列
    				//永恆戒指判斷
    				if( armor.getItem().getName().contains("永恆")){
    				if (ring[0].getItem().getName().contains("永恆")
    						&& ring[1].getItem().getName().contains("永恆")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				if (ring[0].getItem().getName().contains("永恆")
    						&& ring[2].getItem().getName().contains("永恆")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				if (ring[1].getItem().getName().contains("永恆")
    						&& ring[2].getItem().getName().contains("永恆")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    				//活動戒指判斷
    				if( armor.getItem().getName().contains("活動戒指")){
    				if (ring[0].getItem().getName().contains("活動戒指")
    						&& ring[1].getItem().getName().contains("活動戒指")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				if (ring[0].getItem().getName().contains("活動戒指")
    						&& ring[2].getItem().getName().contains("活動戒指")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				if (ring[1].getItem().getName().contains("活動戒指")
    						&& ring[2].getItem().getName().contains("活動戒指")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    		}*/
            // 耳環59級欄位判斷
			/*if ((type == 12) && pcInventory.getTypeEquipped(2, 12) == 1) {
				if (pc.getLevel() < 59) {
					pc.sendPackets(new S_ServerMessage(3253,"耳環"));
					// \f1すでに何かを装備しています。
					return;
				}
			}*/
            // 相同耳環數量判斷 //src014
			/*if (pcInventory.getTypeAndItemIdEquipped(2, 12, itemid) == 1) {
				pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
				return;
			}*/
            // 已經帶了 1個耳環
    		/*if ((type == 12) && pcInventory.getTypeEquipped(2, 12) == 1) {
    				L1ItemInstance earing[] = new L1ItemInstance[1];
    				earing = pcInventory.getEaringEquipped();// 裝備中耳環陣列
    				//星星力量
    				if( armor.getItem().getName().contains("星星力量")){
    				if (earing[0].getItem().getName().contains("星星力量")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    				/'星星敏捷
    				if( armor.getItem().getName().contains("星星敏捷")){
    				if (earing[0].getItem().getName().contains("星星敏捷")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    				//星星智力
    				if( armor.getItem().getName().contains("星星智力")){
    				if (earing[0].getItem().getName().contains("星星智力")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    				//天堂紀念判斷
    				if( armor.getItem().getName().contains("天堂紀念耳環")){
    				if (earing[0].getItem().getName().contains("天堂紀念耳環")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    				//麥斯特判斷
    				if( armor.getItem().getName().contains("麥斯特的紅光耳環")){
    				if (earing[0].getItem().getName().contains("麥斯特的紅光耳環")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    				//麥斯特判斷
    				if( armor.getItem().getName().contains("麥斯特的藍光耳環")){
    				if (earing[0].getItem().getName().contains("麥斯特的藍光耳環")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    				//麥斯特判斷
    				if( armor.getItem().getName().contains("麥斯特的紫光耳環")){
    				if (earing[0].getItem().getName().contains("麥斯特的紫光耳環")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    				//冰之女王的耳環判斷
    				if( armor.getItem().getName().contains("冰之女王的耳環")){
    				if (earing[0].getItem().getName().contains("冰之女王的耳環")) {
    					pc.sendPackets(new S_ServerMessage(3278));// 同種類的道具不可再裝備。
    					return;
    				}
    				}
    				//活動耳環判斷
    				if( armor.getItem().getName().contains("活動耳環")){
    				if (earing[0].getItem().getName().contains("活動耳環")) {
    					pc.sendPackets(new S_ServerMessage("你已配戴相同耳環"));
    					return;
    				}
    				}
    		}*/
            if (type == 23 && pc.getEquipmentIndexAmulet() != 1) { // 14上左 23上右
                pc.sendPackets(new S_ServerMessage("請解鎖道具解鎖該手環欄位！"));
                // \f1すでに何かを装備しています。
                return;
            }
            int polyid = pc.getTempCharGfx();
            if (!L1PolyMorph.isEquipableArmor(polyid, type)) { // 不可此穿戴防具的變身形態下
                return;
            }
            if (type == 7 && pcInventory.getTypeEquipped(2, 13) >= 1 || type == 13 && pcInventory.getTypeEquipped(2, 7) >= 1) {
                // 盾牌、臂甲同時裝備不可
                pc.sendPackets(new S_ServerMessage(124));
                // \f1已經裝備其他東西。
                return;
            }
            if (type == 7 && pc.getWeapon() != null) { // 使用雙手武器時無法使用盾
                if (pc.getWeapon().getItem().isTwohandedWeapon()) { // 雙手武器
                    pc.sendPackets(new S_ServerMessage(129)); // 129 \f1當你使用雙手武器時，無法裝備盾牌。
                    return;
                }
            }
            //if ((type == 3) && (pcInventory.getTypeEquipped(2, 4) >= 1)) { // 穿著斗篷時不可穿內衣
            //pc.sendPackets(new S_ServerMessage(126, "$224", "$225")); // 126 \f1穿著%1 無法裝備 %0%o 。。
            //return;
            //}
            if (pc.isWarrior() && type == 13 && pc.getWeaponWarrior() != null) {
                pc.sendPackets(new S_ServerMessage(124));
                return;
            }
            if (pc.isWarrior() && type == 7 && pc.getWeaponWarrior() != null) {
                pc.sendPackets(new S_ServerMessage(124));
                return;
            }
            //else if ((type == 3) && (pcInventory.getTypeEquipped(2, 2) >= 1)) { // 穿著盔甲時不可穿內衣
            //pc.sendPackets(new S_ServerMessage(126, "$224", "$226")); // 126 \f1穿著%1 無法裝備 %0%o 。
            //return;
            //} else if ((type == 2) && (pcInventory.getTypeEquipped(2, 4) >= 1)) { // 穿著斗篷時不可穿盔甲
            //pc.sendPackets(new S_ServerMessage(126, "$226", "$225"));// 126 \f1穿著%1 無法裝備 %0%o 。
            //return;
            //}
            /** [原碼] 冰之女王魅力禮服、冰之女王魅力涼鞋、冰之女王魅力頭飾 */
            if ((armor.getItemId() == 20134 || armor.getItemId() == 20211 || armor.getItemId() == 400044) && pc.getClassId() != 1) { // 不是公主
                pc.sendPackets(new S_ServerMessage(264));
                return;
            }
            pcInventory.setEquipped(armor, true);
            // System.out.println("穿上裝備");
        } else if (armor.isEquipped()) { // 所選防具穿戴在身上
            if (armor.getItem().getBless() == 2) { // 咒われていた場合
                pc.sendPackets(new S_ServerMessage(150)); // 150 \f1你無法這樣做。這個物品已經被詛咒了。
                return;
            }
            //if ((type == 3) && (pcInventory.getTypeEquipped(2, 2) >= 1)) { // 穿著盔甲時不能脫下內衣
            //pc.sendPackets(new S_ServerMessage(127));// 127 \f1你不能夠脫掉那個。
            //return;
            //} else if (((type == 2) || (type == 3)) && (pcInventory.getTypeEquipped(2, 4) >= 1)) {  // 穿著斗篷時不能脫下內衣
            //pc.sendPackets(new S_ServerMessage(127)); // 127 \f1你不能夠脫掉那個。
            //return;
            //}
            if (type == 7) { // シールドの場合、ソリッドキャリッジの効果消失
                if (pc.hasSkillEffect(SOLID_CARRIAGE)) {
                    pc.removeSkillEffect(SOLID_CARRIAGE);
                }
            }
            if (type == 8) {
                if (pc.hasSkillEffect(Counter_attack)) {
                    pc.removeSkillEffect(Counter_attack);
                }
            }
            pcInventory.setEquipped(armor, false);
        } else {
            if (armor.getItem().getUseType() == 23) {
                pc.sendPackets(new S_SystemMessage("無法再穿戴更多戒指。"));
                return;
            }
            pc.sendPackets(new S_ServerMessage(124));
            return;
        }
        // セット装備用HP、MP、MR更新
        pc.setCurrentHp(pc.getCurrentHp());
        pc.setCurrentMp(pc.getCurrentMp());
        pc.sendPackets(new S_OwnCharAttrDef(pc));
        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_SPMR(pc));
    }

    /**
     * 設置武器的裝備
     *
     */
    private void useWeapon(L1PcInstance pc, L1ItemInstance weapon) {
        /** [原碼] 未滿 15 級的人物將無法裝備會減少「體質」點數的道具 */
        if (pc.getLevel() < 15 && weapon.getItem().get_addcon() < 0) {
            pc.sendPackets(new S_SystemMessage("未滿 15 級的人物將無法裝備會減少「體質」點數的道具。"));
            return;
        }
        switch (weapon.getItemId()) {
            case 65:// 天空之劍
            case 133:// 古代人的智慧
            case 191:// 水之豎琴
            case 192:// 水精靈之弓
                if (pc.getMapId() != CKEWLv50_1.MAPID && !pc.getWeapon().equals(weapon)) {
                    // 563 \f1你無法在這個地方使用。
                    pc.sendPackets(new S_ServerMessage(563));
                    return;
                }
                break;
            default:
                if (pc.hasSkillEffect(CKEW_LV50)) {
                    // 563 \f1你無法在這個地方使用。
                    pc.sendPackets(new S_ServerMessage(563));
                    return;
                }
                break;
        }
        final L1PcInventory pcInventory = pc.getInventory();
        if (pc.getWeapon() == null || !pc.getWeapon().equals(weapon)) { // 沒有使用武器或使用武器與所選武器不同
            final int weapon_type = weapon.getItem().getType();
            final int polyid = pc.getTempCharGfx();
            if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type)) { // 不可使用此武器的變身形態下使用武器
                return;
            }
            if (weapon.getItem().isTwohandedWeapon() && pcInventory.getTypeEquipped(2, 7) >= 1) { // 使用了盾時不可再使用雙手武器
                pc.sendPackets(new S_ServerMessage(128));
                return;
            }
        }
        // 解除魔法技能：絕對屏障
        if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
            pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
            pc.startHpRegeneration();
            pc.startMpRegeneration();
        }
        if (pc.getWeapon() != null) { // 已有裝備的狀態
            if (pc.getWeapon().getItem().getBless() == 2) {
                // 150 \f1你無法這樣做。這個物品已經被詛咒了。
                pc.sendPackets(new S_ServerMessage(150));
                return;
            }
            if (pc.getWeapon().equals(weapon)) {
                // 解除裝備
                pcInventory.setEquipped(pc.getWeapon(), false, false, false);
                return;
                // 武器交換
            } else {
                pcInventory.setEquipped(pc.getWeapon(), false, false, true);
            }
        }
        if (weapon.getItem().getBless() == 2) {
            // \f1%0%s 主動固定在你的手上！
            pc.sendPackets(new S_ServerMessage(149, weapon.getLogName()));
        }
        pcInventory.setEquipped(weapon, true, false, false);
    }

    private void useWeaponForWarrior(final L1PcInstance pc, final L1ItemInstance weapon) {
        final int weapon_type = weapon.getItem().getType();
        final int polyid = pc.getTempCharGfx();
        if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type)) {
            pc.sendPackets(new S_ServerMessage(2055, weapon.getName()));
            return;
        }
        final L1PcInventory pcInventory = pc.getInventory();
        if (pc.getWeapon() != null) {
            if (pc.getWeapon().equals(weapon)) {
                pcInventory.setEquipped(pc.getWeapon(), false, false, false);
                if (pc.getWeaponWarrior() != null) {
                    final L1ItemInstance nextWeapon = pc.getWeaponWarrior();
                    pcInventory.setWarriorEquipped(nextWeapon, false, false);
                    // 武器位移
                    pcInventory.setEquipped(nextWeapon, true, false, false);
                }
                return;
            } else if (pc.getInventory().getTypeEquipped(2, 7) <= 0 && pc.getInventory().getTypeEquipped(2, 13) <= 0 && pc.getWeaponWarrior() != null && pc.getWeaponWarrior().equals(weapon)) {
                pcInventory.setWarriorEquipped(pc.getWeaponWarrior(), false, false);
                pc.sendPacketsAll(new S_CharVisualUpdate(pc.getId(), pc.getCurrentWeapon()));
                return;
            } else if (pc.getInventory().getTypeEquipped(2, 7) <= 0 && pc.getInventory().getTypeEquipped(2, 13) <= 0 && pc.getWeaponWarrior() == null) { //
                if (pc.isSLAYER()) {
                    if (pcInventory.getGarderEquipped(2, 13, 13) >= 1) {
                        pcInventory.setEquipped(pc.getWeapon(), false, false, true);
                    } else if (weapon.getItem().getType() != 6 || pc.getWeapon().getItem().getType() != 6) {
                        pcInventory.setEquipped(pc.getWeapon(), false, false, true);
                    } else if (pc.getWeapon().getItem().isTwohandedWeapon() || weapon.getItem().isTwohandedWeapon()) {
                        pcInventory.setEquipped(pc.getWeapon(), false, false, true);
                    } else {
                        pcInventory.setWarriorEquipped(weapon, true, true);
                        pc.sendPacketsAll(new S_CharVisualUpdate(pc.getId(), pc.getCurrentWeapon()));
                        pc.sendPackets(new S_SkillSound(pc.getId(), 12534));
                        return;
                    }
                } else {
                    pcInventory.setEquipped(pc.getWeapon(), false, false, true);
                }
            } else {
                if (weapon.getItem().getType() != 6) {
                    pc.sendPackets(new S_ServerMessage(124));
                    return;
                }
                pcInventory.setEquipped(pc.getWeapon(), false, false, true);
            }
        }
        pcInventory.setEquipped(weapon, true, false, false);
    }

    // 成長果實系統(Tam幣)
    private void TamWindow(L1PcInstance pc, int _objid, L1ItemInstance item, int day) {
        try {
            Timestamp tamtime = null;
            long time = 0;
            long sysTime = System.currentTimeMillis();
            String _Name = null;
            int tamcount = pc.tamcount();
            Connection con = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;
            try {
                con = DatabaseFactory.get().getConnection();
                pstm = con.prepareStatement("SELECT TamEndTime, char_name FROM characters WHERE objid=?");
                pstm.setInt(1, _objid);
                rs = pstm.executeQuery();
                while (rs.next()) {
                    _Name = rs.getString("char_name");
                    tamtime = rs.getTimestamp("TamEndTime");
                    if (tamtime != null) {
                        if (sysTime < tamtime.getTime()) {
                            time = tamtime.getTime() - sysTime;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
            /*if (_Name == null) {
                pc.sendPackets(new S_SystemMessage("無法使用，請在試一次。"));
                return;
            }*/
            if (time != 0) {
                tamadd(_Name, _objid, day, byteWrite(_objid));
                pc.sendPackets(new S_TamWindow(pc.getAccountName()));
                //pc.sendPackets(new S_SystemMessage("[" + _Name + "]已經在使用了，無法繼續使用。"));
                pc.sendPackets(new S_SystemMessage("[" + _Name + "]預約成功-->請注意！預約後無法取消！"));
                pc.getInventory().removeItem(item, 1);
                return;
            } else if (tamcount >= 3) {
                pc.sendPackets(new S_SystemMessage("賬號內只能有3個角色可以使用。"));
                return;
            }
            Timestamp deleteTime = null;
            //deleteTime = new Timestamp(sysTime + (86400000 * (long) day) + 10000); // 7日
            // deleteTime = new Timestamp(sysTime + 1000*60);//7日
            deleteTime = new Timestamp(sysTime + 86400000 * (long) day);
            if (pc.getId() == _objid) {
                pc.setTamTime(deleteTime);
                try {
                    pc.save();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                tamupdate(_objid, deleteTime);
            }
            pc.sendPackets(new S_TamWindow(pc.getAccountName()));
            int aftertamcount = pc.tamcount();
            int aftertamtime = (int) pc.TamTime();
            if (pc.hasSkillEffect(L1SkillId.Tam_Fruit1)) {
                pc.killSkillEffectTimer(L1SkillId.Tam_Fruit1);
                pc.sendPackets(new S_TamWindow(6100, false, 4181, aftertamtime));
                pc.addAc(1);
            } else if (pc.hasSkillEffect(L1SkillId.Tam_Fruit2)) {
                pc.killSkillEffectTimer(L1SkillId.Tam_Fruit2);
                pc.sendPackets(new S_TamWindow(6547, false, 4182, aftertamtime));
                pc.addAc(2);
            } else if (pc.hasSkillEffect(L1SkillId.Tam_Fruit3)) {
                pc.killSkillEffectTimer(L1SkillId.Tam_Fruit3);
                pc.sendPackets(new S_TamWindow(6546, false, 4183, aftertamtime));
                pc.addAc(3);
            }
            if (aftertamtime < 0) {
                aftertamtime = 0;
            }
            if (aftertamcount == 1) {
                pc.addAc(-1);
                pc.setSkillEffect(L1SkillId.Tam_Fruit1, aftertamtime);
                pc.sendPackets(new S_TamWindow(6100, true, 4181, aftertamtime));
            } else if (aftertamcount == 2) {
                pc.addAc(-2);
                pc.setSkillEffect(L1SkillId.Tam_Fruit2, aftertamtime);
                pc.sendPackets(new S_TamWindow(6547, true, 4182, aftertamtime));
            } else if (aftertamcount == 3) {
                pc.addAc(-3);
                pc.setSkillEffect(L1SkillId.Tam_Fruit3, aftertamtime);
                pc.sendPackets(new S_TamWindow(6546, true, 4183, aftertamtime));
            }
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_ServerMessage(3916));
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), 2028));
            pc.getInventory().removeItem(item, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String byteWrite(long value) {
        long temp = value / 128;
        StringBuffer sb = new StringBuffer();
        if (temp > 0) {
            sb.append((byte) hextable[(int) value % 128]);
            while (temp >= 128) {
                sb.append((byte) hextable[(int) temp % 128]);
                temp = temp / 128;
            }
            if (temp > 0) {
                sb.append((int) temp);
            }
        } else {
            if (value == 0) {
                sb.append(0);
            } else {
                sb.append((byte) hextable[(int) value]);
                sb.append(0);
            }
        }
        return sb.toString();
    }

    public void tamadd(String _name, int objectId, int _day, String _encobjid) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO Tam SET objid=?, Name=?, Day=? , encobjid=?");
            pstm.setInt(1, objectId);
            pstm.setString(2, _name);
            pstm.setInt(3, _day);
            pstm.setString(4, _encobjid);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            //_log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void tamupdate(int objectId, Timestamp date) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE characters SET TamEndTime=? WHERE objid=?");
            pstm.setTimestamp(1, date);
            pstm.setInt(2, objectId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            //_log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // 成長果實系統(Tam幣)end
    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
