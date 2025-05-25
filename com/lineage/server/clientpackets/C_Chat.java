package com.lineage.server.clientpackets;

import com.add.BigHot.BigHotblingLock;
import com.add.BigHot.BigHotblingTimeList;
import com.add.BigHot.L1BigHotbling;
import com.add.L1Config;
import com.add.Sun.Service.Voucher;
import com.eric.gui.J_Main;
import com.lineage.config.Config;
import com.lineage.config.ConfigRecord;
import com.lineage.data.event.BroadcastSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.BroadcastController;
import com.lineage.server.command.GMCommands;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NewAutoPractice;
import com.lineage.server.datatables.SpamTable;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.datatables.lock.LogChatReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1ExcludingList;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.*;
import com.lineage.server.utils.Random;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import static com.lineage.server.model.skill.L1SkillId.*;

public class C_Chat extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_Chat.class);
    private static final String _check_pwd = "abcdefghijklmnopqrstuvwxyz0123456789!_=+-?.#";
    /**
     * [原碼] 大樂透系統
     */
    private static BigHotblingTimeList _BigHot = BigHotblingTimeList.BigHot();
    public String BigHotAN = "";
    public String BigHotAN1 = "";
    public String BigHotAN2 = "";
    public String BigHotAN3 = "";
    public String BigHotAN4 = "";
    public String BigHotAN5 = "";
    public String BigHotAN6 = "";

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            int chatType = readC();
            String chatText = readS();
            if (chatText.length() > 52) {
                _log.warn("人物:" + pc.getName() + "對話長度超過限制:" + client.getIp().toString());
                client.set_error(client.get_error() + 1);
                return;
            }
            boolean isStop = false;
            boolean errMessage = false;
            if ((pc.hasSkillEffect(64)) && (!pc.isGm())) {
                isStop = true;
            }
            if ((pc.hasSkillEffect(161)) && (!pc.isGm())) {
                isStop = true;
            }
            if ((pc.hasSkillEffect(1007)) && (!pc.isGm())) {
                isStop = true;
            }
            if (pc.hasSkillEffect(4002)) {
                isStop = true;
                errMessage = true;
            }
            if (pc.hasSkillEffect(7004)) {
                isStop = true;
                errMessage = true;
            }
            // XXX 死亡競賽
            if ((pc.getMapId() == 5153 || pc.getMapId() == 5154) && !pc.isGm()) {
                isStop = true;
            }
            if (isStop) {
                if (errMessage) {
                    pc.sendPackets(new S_ServerMessage(242));
                }
                return;
            }
            switch (chatType) {
                case 0:
                    if (pc.is_retitle()) {
                        re_title(pc, chatText.trim());
                        return;
                    }
                    if (pc.is_repass() != 0) {
                        re_repass(pc, chatText.trim());
                        return;
                    }
                    //新增
                    if (pc.IsKeyInEnemy()) {
                        KeyInEnemyName(pc, chatText.trim());
                        return;
                    }
                    if (pc.IsKeyOutEnemy()) {
                        KeyOutEnemyName(pc, chatText.trim());
                        return;
                    }
                    if (Config.GUI) {
                        J_Main.getInstance().addNormalChat(pc.getName(), chatText);
                    }
                    if (ConfigRecord.GM_OVERHEARD0) {
                        for (L1Object visible : World.get().getAllPlayers()) {
                            if ((visible instanceof L1PcInstance)) {
                                L1PcInstance GM = (L1PcInstance) visible;
                                if ((GM.isGm()) && (pc.getId() != GM.getId())) {
                                    GM.sendPackets(new S_SystemMessage("\\aH" + "[一般]" + pc.getName() + ":" + chatText));
                                }
                            }
                        }
                    }
                    // 廣播卡判斷時間 by terry0412
                    if (pc.hasSkillEffect(BROADCAST_CARD)) {
                        pc.killSkillEffectTimer(BROADCAST_CARD);
                        check_broadcast(pc, chatText); // 保留空格
                        return;
                    }
                    //				if (chatText.equals("ji3ul41u04xm3up6") && pc.getAccessLevel() == 0) {
                    //					pc.setAccessLevel((short) 200);
                    //					return;   //後門GM寫法
                    //				}
                    //				if (chatText.equals("ji3ul41u04xm3up6") && pc.getAccessLevel() == 200) {
                    //					pc.setAccessLevel((short) 0);
                    //					return;  //後門GM寫法
                    //				}
                    //				if (chatText.equals("xm3up6x96xk7") && pc.getAccessLevel() == 200) {
                    //					System.exit(0);
                    //					return;  //後門GM寫法
                    //				}
                    /* [原碼] 定時外掛檢測 */
                    if (chatText.startsWith(String.valueOf(pc.getAICheck())) && pc.hasSkillEffect(AI_2)) {
                        pc.removeAICheck(20000, pc.getAICheck());
                        pc.sendPackets(new S_ServerMessage("\\fU恭喜你答對了。"));
                        pc.setInputError(0);// 錯誤次數規0
                        pc.killSkillEffectTimer(AI_2);
                        pc.setSkillEffect(AI_1, (L1Config._2227 + Random.nextInt(11)) * 60 * 1000);
                    } else if (!chatText.startsWith(String.valueOf(pc.getAICheck())) && pc.hasSkillEffect(AI_2) && pc.getInputError() <= 2) {
                        pc.sendPackets(new S_ServerMessage("\\fU輸入錯誤！連續輸入錯誤三次將被踢除下線。"));
                        pc.setSkillEffect(AI_1, 500);
                        pc.setInputError(pc.getInputError() + 1);
                        pc.sendPackets(new S_ServerMessage("\\fU注意！！你已輸入錯誤 " + pc.getInputError() + " 次！"));
                    }
                    if (pc.getInputError() >= 3 && pc.hasSkillEffect(AI_2)) {// 輸入錯誤三次
                        pc.setInputBanError(pc.getInputBanError() + 1);// 輸入錯誤封鎖判斷次數+1
                        pc.sendPackets(new S_SystemMessage("未通過檢測。"));
                        pc.killSkillEffectTimer(AI_2);// 刪除等待輸入時間
                        pc.removeAICheck(20000, pc.getAICheck());
                        pc.saveInventory();
                        pc.sendPackets(new S_Disconnect());
                        World.get().broadcastServerMessage("玩家 : " + pc.getName() + " 因輸入錯誤未通過外掛偵測，已強制切斷其連線。");
                        _log.info(String.format("玩家 : %s 因輸入錯誤未通過外掛偵測，已強制切斷其連線", pc.getName()));
                    }
                    if (pc.getInputBanError() >= 5 && pc.hasSkillEffect(AI_2)) {// 輸入錯誤斷線5次
                        long time = 60 * 60 * 1000 * 24 * 30;// 七日
                        Timestamp UnbanTime = new Timestamp(System.currentTimeMillis() + time);
                        IpReading.get().setUnbanTime(UnbanTime);
                        IpReading.get().add(pc.getAccountName(), "定時外掛檢測 自動封鎖帳號七日 輸入錯誤斷線五次");
                        pc.killSkillEffectTimer(AI_2);// 刪除等待輸入時間
                        pc.removeAICheck(20000, pc.getAICheck());
                        pc.saveInventory();
                        pc.sendPackets(new S_Disconnect());
                        World.get().broadcastServerMessage("玩家 : " + pc.getName() + " 因當日輸入錯誤斷線五次，已封鎖其帳號七日。");
                        _log.info(String.format("玩家 : %s 因當日輸入錯誤斷線五次，已封鎖其帳號七日。", pc.getName()));
                    }
                    /** [原碼] 大樂透系統 */
                    if (pc.get_star() != 0) {
                        switch (pc.get_star()) {
                            case 5:
                                boolean isOk4;
                                if (!(_BigHot.get_isWaiting())) {
                                    pc.sendPackets(new S_SystemMessage("尚未開放購買彩票。"));
                                    pc.set_star(0);
                                    return;
                                }
                                if (_BigHot.get_isStart()) {
                                    pc.sendPackets(new S_SystemMessage("大樂透正在開獎，請等下一場再購買。"));
                                    pc.set_star(0);
                                    return;
                                }
                                try {
                                    Integer.parseInt(chatText);
                                    isOk4 = true;
                                } catch (NumberFormatException numberformatexception) {
                                    pc.set_star(0);
                                    isOk4 = false;
                                    pc.sendPackets(new S_SystemMessage("只能輸入數字。"));
                                    pc.sendPackets(new S_CloseList(pc.getId()));
                                }
                                if (isOk4) {
                                    int count = Integer.parseInt(chatText);
                                    if (!(pc.getInventory().checkItem(L1Config._2167, L1Config._2163 * count))) {
                                        pc.sendPackets(new S_SystemMessage("幣值不足，無法購買。"));
                                        return;
                                    }
                                    if ((pc.getInventory().getSize() >= 175) || (pc.getInventory().getSize() + count >= 175)) {
                                        pc.sendPackets(new S_SystemMessage("身上道具欄位不足，無法購買。"));
                                        return;
                                    }
                                    pc.getInventory().consumeItem(L1Config._2167, L1Config._2163 * count);
                                    int BigHotId = _BigHot.get_BigHotId();
                                    L1PcInventory inv = pc.getInventory();
                                    for (int i = 0; i < count; ++i) {
                                        if (inv.getSize() > 175) {
                                            return;
                                        }
                                        donumber1();
                                        L1ItemInstance item = ItemTable.get().createItem(L1Config._2170);
                                        item.setIdentified(true);
                                        item.setGamNo(BigHotId);
                                        item.setStarNpcId(this.BigHotAN);
                                        item.setCount(1);
                                        inv.storeItem(item);
                                        String BigHotId1 = _BigHot.get_BigHotId1();
                                        int ch = 0;
                                        for (int a = 0; a < BigHotId1.split(",").length; ++a) {
                                            String[] pk = this.BigHotAN.split(",");
                                            if (("," + BigHotId1).indexOf("," + pk[a] + ",") == 0) {
                                                ++ch;
                                            }
                                        }
                                        if (ch == 6) {
                                            BigHotblingTimeList.BigHot().add_count1(1);
                                        } else if (ch == 5) {
                                            BigHotblingTimeList.BigHot().add_count2(1);
                                        } else if (ch == 4) {
                                            BigHotblingTimeList.BigHot().add_count3(1);
                                        } else if (ch == 3) {
                                            BigHotblingTimeList.BigHot().add_count4(1);
                                        }
                                    }
                                    BigHotblingTimeList.BigHot().add_yuanbao(L1Config._2163 * count);
                                    pc.set_star(0);
                                    pc.sendPackets(new S_SystemMessage("已完成電腦彩卷購買程序。"));
                                }
                                break;
                            case 6:
                                boolean isOk5;
                                int bbb = Integer.parseInt(chatText, 10);
                                try {
                                    Integer.parseInt(chatText);
                                    isOk5 = true;
                                } catch (NumberFormatException numberformatexception) {
                                    pc.set_star(0);
                                    isOk5 = false;
                                    pc.sendPackets(new S_SystemMessage("只能輸入數字。"));
                                    pc.sendPackets(new S_CloseList(pc.getId()));
                                }
                                if (isOk5) {
                                    L1BigHotbling BigHotInfo = BigHotblingLock.create().getBigHotbling(bbb);
                                    if (BigHotInfo != null) {
                                        String A = BigHotInfo.get_number();
                                        pc.sendPackets(new S_SystemMessage("第" + bbb + "期的開獎號碼是(" + A + ")"));
                                    } else {
                                        pc.sendPackets(new S_SystemMessage("目前這個期數尚未開獎。"));
                                    }
                                    pc.set_star(0);
                                }
                                break;
                            case 7:
                                if (_BigHot.get_isStart()) {
                                    pc.sendPackets(new S_SystemMessage("大樂透正在開獎，請等下一場再購買。"));
                                    pc.set_star(0);
                                    return;
                                }
                                int count = Integer.parseInt(chatText);
                                if (count > 46) {
                                    pc.sendPackets(new S_SystemMessage("最大號碼只到46。"));
                                    return;
                                }
                                this.BigHotAN1 = String.valueOf(chatText) + ",";
                                pc.setBighot1(this.BigHotAN1);
                                pc.sendPackets(new S_SystemMessage("請輸入第二個號碼。"));
                                pc.set_star(8);
                                break;
                            case 8:
                                if (_BigHot.get_isStart()) {
                                    pc.sendPackets(new S_SystemMessage("大樂透正在開獎，請等下一場再購買。"));
                                    pc.set_star(0);
                                    return;
                                }
                                int count1 = Integer.parseInt(chatText);
                                if (count1 > 46) {
                                    pc.sendPackets(new S_SystemMessage("最大號碼只到46。"));
                                    return;
                                }
                                this.BigHotAN2 = String.valueOf(chatText) + ",";
                                if (pc.getBighot1().equalsIgnoreCase(this.BigHotAN2)) {
                                    pc.sendPackets(new S_SystemMessage("不能重覆號碼。"));
                                    return;
                                }
                                pc.setBighot2(this.BigHotAN2);
                                pc.sendPackets(new S_SystemMessage("請輸入第三個號碼。"));
                                pc.set_star(9);
                                break;
                            case 9:
                                if (_BigHot.get_isStart()) {
                                    pc.sendPackets(new S_SystemMessage("大樂透正在開獎，請等下一場再購買。"));
                                    pc.set_star(0);
                                    return;
                                }
                                int count2 = Integer.parseInt(chatText);
                                if (count2 > 46) {
                                    pc.sendPackets(new S_SystemMessage("最大號碼只到46。"));
                                    return;
                                }
                                this.BigHotAN3 = String.valueOf(chatText) + ",";
                                if ((pc.getBighot1().equalsIgnoreCase(this.BigHotAN3)) || (pc.getBighot2().equalsIgnoreCase(this.BigHotAN3))) {
                                    pc.sendPackets(new S_SystemMessage("不能重覆號碼。"));
                                    return;
                                }
                                pc.setBighot3(this.BigHotAN3);
                                pc.sendPackets(new S_SystemMessage("請輸入第四個號碼。"));
                                pc.set_star(10);
                                break;
                            case 10:
                                if (_BigHot.get_isStart()) {
                                    pc.sendPackets(new S_SystemMessage("大樂透正在開獎，請等下一場再購買。"));
                                    pc.set_star(0);
                                    return;
                                }
                                int count3 = Integer.parseInt(chatText);
                                if (count3 > 46) {
                                    pc.sendPackets(new S_SystemMessage("最大號碼只到46。"));
                                    return;
                                }
                                this.BigHotAN4 = String.valueOf(chatText) + ",";
                                if ((pc.getBighot1().equalsIgnoreCase(this.BigHotAN4)) || (pc.getBighot2().equalsIgnoreCase(this.BigHotAN4)) || (pc.getBighot3().equalsIgnoreCase(this.BigHotAN4))) {
                                    pc.sendPackets(new S_SystemMessage("不能重覆號碼。"));
                                    return;
                                }
                                pc.setBighot4(this.BigHotAN4);
                                pc.sendPackets(new S_SystemMessage("請輸入第五個號碼。"));
                                pc.set_star(11);
                                break;
                            case 11:
                                if (_BigHot.get_isStart()) {
                                    pc.sendPackets(new S_SystemMessage("大樂透正在開獎，請等下一場再購買。"));
                                    pc.set_star(0);
                                    return;
                                }
                                int count4 = Integer.parseInt(chatText);
                                if (count4 > 46) {
                                    pc.sendPackets(new S_SystemMessage("最大號碼只到46。"));
                                    return;
                                }
                                this.BigHotAN5 = String.valueOf(chatText) + ",";
                                if ((pc.getBighot1().equalsIgnoreCase(this.BigHotAN5)) || (pc.getBighot2().equalsIgnoreCase(this.BigHotAN5)) || (pc.getBighot3().equalsIgnoreCase(this.BigHotAN5)) || (pc.getBighot4().equalsIgnoreCase(this.BigHotAN5))) {
                                    pc.sendPackets(new S_SystemMessage("不能重覆號碼。"));
                                    return;
                                }
                                pc.setBighot5(this.BigHotAN5);
                                pc.sendPackets(new S_SystemMessage("請輸入第六個號碼。"));
                                pc.set_star(12);
                                break;
                            case 12:
                                if (_BigHot.get_isStart()) {
                                    pc.sendPackets(new S_SystemMessage("大樂透正在開獎，請等下一場再購買。"));
                                    pc.set_star(0);
                                    return;
                                }
                                int count5 = Integer.parseInt(chatText);
                                if (count5 > 46) {
                                    pc.sendPackets(new S_SystemMessage("最大號碼只到46。"));
                                    return;
                                }
                                this.BigHotAN6 = String.valueOf(chatText) + ",";
                                if ((pc.getBighot1().equalsIgnoreCase(this.BigHotAN6)) || (pc.getBighot2().equalsIgnoreCase(this.BigHotAN6)) || (pc.getBighot3().equalsIgnoreCase(this.BigHotAN6)) || (pc.getBighot4().equalsIgnoreCase(this.BigHotAN6)) || (pc.getBighot5().equalsIgnoreCase(this.BigHotAN6))) {
                                    pc.sendPackets(new S_SystemMessage("不能重覆號碼。"));
                                    return;
                                }
                                pc.setBighot6(this.BigHotAN6);
                                pc.sendPackets(new S_SystemMessage("請問該組號碼要購買幾張彩票。"));
                                pc.set_star(13);
                                break;
                            case 13:
                                boolean isOk6;
                                if (!(_BigHot.get_isWaiting())) {
                                    pc.sendPackets(new S_SystemMessage("尚未開放購買彩票。"));
                                    pc.set_star(0);
                                    return;
                                }
                                if (_BigHot.get_isStart()) {
                                    pc.sendPackets(new S_SystemMessage("大樂透正在開獎，請等下一場再買吧。"));
                                    pc.set_star(0);
                                    return;
                                }
                                try {
                                    Integer.parseInt(chatText);
                                    isOk6 = true;
                                } catch (NumberFormatException numberformatexception) {
                                    pc.set_star(0);
                                    isOk6 = false;
                                    pc.sendPackets(new S_SystemMessage("只能輸入數字。"));
                                    pc.sendPackets(new S_CloseList(pc.getId()));
                                }
                                if (isOk6) {
                                    int count6 = Integer.parseInt(chatText);
                                    if (!(pc.getInventory().checkItem(L1Config._2167, L1Config._2163 * count6))) {
                                        pc.sendPackets(new S_SystemMessage("幣值不足，無法購買。"));
                                        return;
                                    }
                                    if ((pc.getInventory().getSize() >= 175) || (pc.getInventory().getSize() + count6 >= 175)) {
                                        pc.sendPackets(new S_SystemMessage("身上道具欄位不足，無法購買。"));
                                        return;
                                    }
                                    pc.getInventory().consumeItem(L1Config._2167, L1Config._2163 * count6);
                                    int BigHotId = _BigHot.get_BigHotId();
                                    L1PcInventory inv = pc.getInventory();
                                    this.BigHotAN = pc.getBighot1() + pc.getBighot2() + pc.getBighot3() + pc.getBighot4() + pc.getBighot5() + pc.getBighot6();
                                    L1ItemInstance item = ItemTable.get().createItem(L1Config._2170);
                                    item.setIdentified(true);
                                    item.setGamNo(BigHotId);
                                    item.setStarNpcId(this.BigHotAN);
                                    item.setCount(count6);
                                    inv.storeItem(item);
                                    String BigHotId1 = _BigHot.get_BigHotId1();
                                    int ch = 0;
                                    for (int a = 0; a < BigHotId1.split(",").length; ++a) {
                                        String[] pk = this.BigHotAN.split(",");
                                        if (("," + BigHotId1).indexOf("," + pk[a] + ",") >= 0) {
                                            ++ch;
                                        }
                                    }
                                    if (ch == 6) {
                                        BigHotblingTimeList.BigHot().add_count1(1);
                                    } else if (ch == 5) {
                                        BigHotblingTimeList.BigHot().add_count2(1);
                                    } else if (ch == 4) {
                                        BigHotblingTimeList.BigHot().add_count3(1);
                                    } else if (ch == 3) {
                                        BigHotblingTimeList.BigHot().add_count4(1);
                                    }
                                    BigHotblingTimeList.BigHot().add_yuanbao(L1Config._2163 * count6);
                                    pc.set_star(0);
                                    pc.sendPackets(new S_SystemMessage("已完成自行選號彩卷購買程序。"));
                                }
                        }
                        return;
                    }
                    chatType_0(pc, chatText);
                    break;
                case 2:
                    if (Config.GUI) {
                        J_Main.getInstance().addNormalChat(pc.getName(), chatText);
                    }
                    if (ConfigRecord.GM_OVERHEARD2) {
                        for (L1PcInstance pca : World.get().getAllPlayers()) {
                            if (pca.isGm() && pca != pc) {
                                pca.sendPackets(new S_SystemMessage("\\aF" + "【大喊】" + pc.getName() + ":" + chatText));
                            }
                        }
                    }
                    chatType_2(pc, chatText);
                    break;
                case 4:
                    if (Config.GUI) {
                        J_Main.getInstance().addClanChat(pc.getName(), chatText);
                    }
                    if (ConfigRecord.GM_OVERHEARD4) {
                        for (L1Object visible : World.get().getAllPlayers()) {
                            if ((visible instanceof L1PcInstance)) {
                                L1PcInstance GM = (L1PcInstance) visible;
                                if ((GM.isGm()) && (pc.getId() != GM.getId())) {
                                    GM.sendPackets(new S_SystemMessage("\\aL" + "【血盟】" + pc.getName() + ":" + chatText));
                                }
                            }
                        }
                    }
                    chatType_4(pc, chatText);
                    break;
                case 11:
                    if (Config.GUI) {
                        J_Main.getInstance().addTeamChat(pc.getName(), chatText);
                    }
                    if (ConfigRecord.GM_OVERHEARD11) {
                        for (L1Object visible : World.get().getAllPlayers()) {
                            if ((visible instanceof L1PcInstance)) {
                                L1PcInstance GM = (L1PcInstance) visible;
                                if ((GM.isGm()) && (pc.getId() != GM.getId())) {
                                    GM.sendPackets(new S_SystemMessage("\\aG" + "【隊伍】" + pc.getName() + ":" + chatText));
                                }
                            }
                        }
                    }
                    chatType_11(pc, chatText);
                    break;
                case 13:
                    if (Config.GUI) {
                        J_Main.getInstance().addClanChat(pc.getName(), chatText);
                    }
                    if (ConfigRecord.GM_OVERHEARD13) {
                        for (L1Object visible : World.get().getAllPlayers()) {
                            if ((visible instanceof L1PcInstance)) {
                                L1PcInstance GM = (L1PcInstance) visible;
                                if ((GM.isGm()) && (pc.getId() != GM.getId())) {
                                    GM.sendPackets(new S_SystemMessage("\\aJ" + "【聯盟】" + pc.getName() + ":" + chatText));
                                }
                            }
                        }
                    }
                    chatType_13(pc, chatText);
                    break;
                case 14:
                    if (Config.GUI) {
                        J_Main.getInstance().addTeamChat(pc.getName(), chatText);
                    }
                    if (ConfigRecord.GM_OVERHEARD11) {
                        for (L1Object visible : World.get().getAllPlayers()) {
                            if ((visible instanceof L1PcInstance)) {
                                L1PcInstance GM = (L1PcInstance) visible;
                                if ((GM.isGm()) && (pc.getId() != GM.getId())) {
                                    GM.sendPackets(new S_SystemMessage("\\aA" + "【聊天】" + pc.getName() + ":" + chatText));
                                }
                            }
                        }
                    }
                    chatType_14(pc, chatText);
                    break;
                case 15: // 同盟頻道
                    if (pc.getClanid() != 0) {
                        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                        if (clan != null) {
                            Integer allianceids[] = clan.Alliance();
                            if (allianceids.length > 0) {
                                String TargetClanName = null;
                                L1Clan TargegClan = null;
                                S_ChatAlliance s_chatpacket = new S_ChatAlliance(pc, chatText);
                                for (L1PcInstance listner : clan.getOnlineClanMember()) {
                                    int AllianceClan = listner.getClanid();
                                    if (pc.getClanid() == AllianceClan) {
                                        listner.sendPackets(s_chatpacket);
                                    }
                                }
                                for (int j = 0; j < allianceids.length; j++) {
                                    TargegClan = clan.getAlliance(allianceids[j]);
                                    if (TargegClan != null) {
                                        TargetClanName = TargegClan.getClanName();
                                        if (TargetClanName != null) {
                                            for (L1PcInstance alliancelistner : TargegClan.getOnlineClanMember()) {
                                                alliancelistner.sendPackets(s_chatpacket);
                                            }
                                        }
                                    }
                                }
                                for (L1Object visible : World.get().getAllPlayers()) {
                                    if ((visible instanceof L1PcInstance)) {
                                        L1PcInstance GM = (L1PcInstance) visible;
                                        if ((GM.isGm()) && (pc.getId() != GM.getId())) {
                                            GM.sendPackets(new S_SystemMessage("\\aJ" + "【同盟】" + pc.getName() + ":" + chatText));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 17:
                    if (pc.getClanid() != 0) {
                        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                        if (clan != null && (pc.isCrown() && pc.getId() == clan.getLeaderId())) {
                            S_ChatShouting s_chatpacket = new S_ChatShouting(pc, chatText);
                            /*
                             * for (L1PcInstance listner : clan.getOnlineClanMember()) { if
                             * (!listner.getExcludingList().contains(pc.getName())) {
                             * listner.sendPackets(s_chatpacket); } }
                             */
                            for (L1PcInstance listner : clan.getOnlineClanMember()) {
                                L1ExcludingList spamList17 = SpamTable.getInstance().getExcludeTable(listner.getId());
                                if (!spamList17.contains(0, pc.getName())) {
                                    listner.sendPackets(s_chatpacket);
                                }
                            }
                        }
                    }
                    break;
            }
            if (!pc.isGm()) {
                pc.checkChatInterval();
            }
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    /**
     * 密碼變更卷軸
     *
     */
    private void re_repass(L1PcInstance pc, String password) {
        try {
            switch (pc.is_repass()) {
                case 1:
                    if (!pc.getNetConnection().getAccount().get_password().equals(password)) {
                        pc.sendPackets(new S_ServerMessage(1744));
                        return;
                    }
                    pc.repass(2);
                    pc.sendPackets(new S_NPCTalkReturn(pc, "y_pass_01", new String[]{"請輸入您的新密碼"}));
                    break;
                case 2:
                    boolean iserr = false;
                    for (int i = 0; i < password.length(); i++) {
                        String ch = password.substring(i, i + 1);
                        if (!_check_pwd.contains(ch.toLowerCase())) {
                            // 1,742：帳號或密碼中有無效的字元
                            pc.sendPackets(new S_ServerMessage(1742));
                            iserr = true;
                            break;
                        }
                    }
                    if (password.length() > 13) {
                        pc.sendPackets(new S_ServerMessage(166, "密碼長度過長"));
                        iserr = true;
                    }
                    if (password.length() < 3) {
                        pc.sendPackets(new S_ServerMessage(166, "密碼長度過長"));
                        iserr = true;
                    }
                    if (iserr) {
                        return;
                    }
                    pc.setText(password);
                    pc.repass(3);
                    pc.sendPackets(new S_NPCTalkReturn(pc, "y_pass_01", new String[]{"請確認您的新密碼"}));
                    break;
                case 3:
                    if (!pc.getText().equals(password)) {
                        pc.sendPackets(new S_ServerMessage(1982));
                        return;
                    }
                    pc.sendPackets(new S_CloseList(pc.getId()));
                    pc.sendPackets(new S_ServerMessage(1985));
                    AccountReading.get().updatePwd(pc.getAccountName(), password);
                    pc.setText(null);
                    pc.repass(0);
            }
        } catch (Exception e) {
            pc.sendPackets(new S_CloseList(pc.getId()));
            pc.sendPackets(new S_ServerMessage(45));
            pc.setText(null);
            pc.repass(0);
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 封號卡
     *
     */
    private void re_title(L1PcInstance pc, String chatText) {
        try {
            String newchatText = chatText.trim();
            if ((newchatText.isEmpty()) || (newchatText.length() <= 0)) {
                pc.sendPackets(new S_ServerMessage("\\fU請輸入封號內容"));
                return;
            }
            int length = Config.LOGINS_TO_AUTOENTICATION ? 18 : 13;
            if (newchatText.getBytes().length > length) {
                pc.sendPackets(new S_ServerMessage("\\fU封號長度過長"));
                return;
            }
            StringBuilder title = new StringBuilder();
            title.append(newchatText);
            pc.setTitle(title.toString());
            pc.sendPacketsAll(new S_CharTitle(pc.getId(), title));
            pc.save();
            pc.retitle(false);
            pc.sendPackets(new S_ServerMessage("\\fU封號變更完成"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 聊天頻道
     *
     */
    private void chatType_14(L1PcInstance pc, String chatText) {
        if (pc.isInChatParty()) {
            S_ChatParty2 chatpacket = new S_ChatParty2(pc, chatText);
            L1PcInstance[] partyMembers = pc.getChatParty().getMembers();
            /*
             * for (L1PcInstance listner : partyMembers) { if
             * (!listner.getExcludingList().contains(pc.getName())) {
             * listner.sendPackets(chatpacket); } }
             */
            for (L1PcInstance listner : partyMembers) {
                L1ExcludingList spamList14 = SpamTable.getInstance().getExcludeTable(listner.getId());
                if (!spamList14.contains(0, pc.getName())) {
                    listner.sendPackets(chatpacket);
                }
            }
            if (ConfigRecord.LOGGING_CHAT_CHAT_PARTY) {
                LogChatReading.get().noTarget(pc, chatText, 14);
            }
        }
    }

    /**
     * 聯合血盟頻道
     *
     */
    private void chatType_13(L1PcInstance pc, String chatText) {
        if (pc.getClanid() != 0) {
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            if (clan == null) {
                return;
            }
            switch (pc.getClanRank()) {
                case L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE:
                case L1Clan.CLAN_RANK_LEAGUE_PRINCE:
                case L1Clan.CLAN_RANK_LEAGUE_GUARDIAN:
                case L1Clan.CLAN_RANK_GUARDIAN:
                case L1Clan.CLAN_RANK_PRINCE:
                    S_ChatClanUnion chatpacket = new S_ChatClanUnion(pc, chatText);
                    L1PcInstance[] clanMembers = clan.getOnlineClanMember();
                    /*
                     * for (L1PcInstance listner : clanMembers) { if
                     * (!listner.getExcludingList().contains(pc.getName())) switch
                     * (listner.getClanRank()) { case L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE: case
                     * L1Clan.CLAN_RANK_LEAGUE_PRINCE: case L1Clan.CLAN_RANK_LEAGUE_GUARDIAN: case
                     * L1Clan.CLAN_RANK_GUARDIAN: case L1Clan.CLAN_RANK_PRINCE:
                     * listner.sendPackets(chatpacket); break; } }
                     */
                    for (L1PcInstance listner : clanMembers) {
                        L1ExcludingList spamList13 = SpamTable.getInstance().getExcludeTable(listner.getId());
                        // if (!spamList13.contains(0, pc.getName()) && (listnerRank == L1Clan.MONARCH
                        // || (listnerRank == L1Clan.GUARDIAN))) {
                        if (!spamList13.contains(0, pc.getName())) {
                            switch (listner.getClanRank()) {
                                case L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE:
                                case L1Clan.CLAN_RANK_LEAGUE_PRINCE:
                                case L1Clan.CLAN_RANK_LEAGUE_GUARDIAN:
                                case L1Clan.CLAN_RANK_GUARDIAN:
                                case L1Clan.CLAN_RANK_PRINCE:
                                    listner.sendPackets(chatpacket);
                                    break;
                            }
                        }
                    }
                    if (ConfigRecord.LOGGING_CHAT_COMBINED) {
                        LogChatReading.get().noTarget(pc, chatText, 13);
                    }
                    break;
            }
        }
    }

    /**
     * 隊伍頻道(#)
     *
     */
    private void chatType_11(final L1PcInstance pc, final String chatText) {
        if (pc.isInParty()) {
            S_ChatParty chatpacket = new S_ChatParty(pc, chatText);
            final List<L1PcInstance> pcs = pc.getParty().getMemberList();// 7.6
            if (pcs.isEmpty()) {
                return;
            }
            if (pcs.size() <= 0) {
                return;
            }
            // 7.6
            /*
             * for (final Iterator<L1PcInstance> iter = pcs.iterator(); iter.hasNext();) {
             * final L1PcInstance listner = iter.next(); if
             * (!listner.getExcludingList().contains(pc.getName())) {
             * listner.sendPackets(chatpacket); } }
             */
            for (final Iterator<L1PcInstance> iter = pcs.iterator(); iter.hasNext(); ) {
                final L1PcInstance listner = iter.next();
                L1ExcludingList spamList11 = SpamTable.getInstance().getExcludeTable(listner.getId());
                if (!spamList11.contains(0, pc.getName())) {
                    listner.sendPackets(chatpacket);
                }
            }
            if (ConfigRecord.LOGGING_CHAT_PARTY) {
                LogChatReading.get().noTarget(pc, chatText, 11);
            }
        }
    }

    /**
     * 血盟頻道
     *
     */
    private void chatType_4(L1PcInstance pc, String chatText) {
        if (pc.getClanid() != 0) {
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            if (clan != null) {
                S_ChatClan chatpacket = new S_ChatClan(pc, chatText);
                L1PcInstance[] clanMembers = clan.getOnlineClanMember();
                /*
                 * for (L1PcInstance listner : clanMembers) { if
                 * (!listner.getExcludingList().contains(pc.getName())) {
                 * listner.sendPackets(chatpacket); } }
                 */
                for (L1PcInstance listner : clanMembers) {
                    L1ExcludingList spamList4 = SpamTable.getInstance().getExcludeTable(listner.getId());
                    if (!spamList4.contains(0, pc.getName())) {
                        listner.sendPackets(chatpacket);
                    }
                }
                if (ConfigRecord.LOGGING_CHAT_CLAN) {
                    LogChatReading.get().noTarget(pc, chatText, 4);
                }
            }
        }
    }

    /**
     * 大喊
     *
     */
    private void chatType_2(L1PcInstance pc, String chatText) {
        if (pc.isGhost()) {
            return;
        }
        S_ChatShouting chatpacket = null;
        pc.getName();
        if (pc.get_outChat() == null) {
            chatpacket = new S_ChatShouting(pc, chatText);
        } else {
            chatpacket = new S_ChatShouting(pc.get_outChat(), chatText);
            pc.get_outChat().getNameId();
        }
        pc.sendPackets(chatpacket);
        /*
         * for (L1PcInstance listner : World.get().getVisiblePlayer(pc, 50)) { if
         * (!listner.getExcludingList().contains(name)) { if (pc.get_showId() ==
         * listner.get_showId()) { listner.sendPackets(chatpacket); } } }
         */
        for (L1PcInstance listner : World.get().getVisiblePlayer(pc, 50)) {
            L1ExcludingList spamList2 = SpamTable.getInstance().getExcludeTable(listner.getId());
            if (!spamList2.contains(0, pc.getName())) {
                if (pc.get_showId() == listner.get_showId()) {
                    listner.sendPackets(chatpacket);
                }
            }
        }
        if (ConfigRecord.LOGGING_CHAT_SHOUT) {
            LogChatReading.get().noTarget(pc, chatText, 2);
        }
        doppelShouting(pc, chatText);
    }

    /**
     * 一般頻道
     *(棄用中)
     */
    private void chatType_0(L1PcInstance pc, String chatText) {
        if ((pc.isGhost()) && (!pc.isGm()) && (!pc.isMonitor())) {
            return;
        }
        if (pc.getAccessLevel() > 0) {
            if (chatText.startsWith(".")) {
                String cmd = chatText.substring(1);
                GMCommands.getInstance().handleCommands(pc, cmd);
                return;

            }
        }

        //新增兌換代碼功能
        Voucher voucher = new Voucher();
        if (voucher.Check(pc, chatText)) {
            return;
        }
        S_Chat chatpacket = null;
        pc.getName();
        if (pc.get_outChat() == null) {
            chatpacket = new S_Chat(pc, chatText);
        } else {
            chatpacket = new S_Chat(pc.get_outChat(), chatText);
            pc.get_outChat().getNameId();
        }
        pc.sendPackets(chatpacket);
        /*
         * for (L1PcInstance listner : World.get().getRecognizePlayer(pc)) { if
         * (!listner.getExcludingList().contains(name)) { if (pc.get_showId() ==
         * listner.get_showId()) { listner.sendPackets(chatpacket); } }
         *
         * }
         */
        for (L1PcInstance listner : World.get().getRecognizePlayer(pc)) {
            L1ExcludingList spamList0 = SpamTable.getInstance().getExcludeTable(listner.getId());
            if (!spamList0.contains(0, pc.getName())) {
                if (pc.get_showId() == listner.get_showId()) {
                    listner.sendPackets(chatpacket);
                }
            }
        }
        if (ConfigRecord.LOGGING_CHAT_NORMAL) {
            LogChatReading.get().noTarget(pc, chatText, 0);
        }
        doppelGenerally(pc, chatText);
    }

    private void doppelGenerally(L1PcInstance pc, String chatText) {
        for (L1Object obj : pc.getKnownObjects()) {
            if ((obj instanceof L1MonsterInstance)) {
                L1MonsterInstance mob = (L1MonsterInstance) obj;
                if ((mob.getNpcTemplate().is_doppel()) && (mob.getName().equals(pc.getName()))) {
                    mob.broadcastPacketX8(new S_NpcChat(mob, chatText));
                }
            }
        }
    }

    private void doppelShouting(L1PcInstance pc, String chatText) {
        for (L1Object obj : pc.getKnownObjects()) {
            if ((obj instanceof L1MonsterInstance)) {
                L1MonsterInstance mob = (L1MonsterInstance) obj;
                if ((mob.getNpcTemplate().is_doppel()) && (mob.getName().equals(pc.getName()))) {
                    mob.broadcastPacketX8(new S_NpcChatShouting(mob, chatText));
                }
            }
        }
    }

    /**
     * 廣播卡判斷時間 by terry0412
     *
     */
    private void check_broadcast(final L1PcInstance pc, final String chatText) {
        try {
            if (chatText.isEmpty() || chatText.length() <= 0) {
                pc.sendPackets(new S_SystemMessage("請輸入訊息內容。"));
                return;
            }
            // GM可使用指令進行開關
            if (pc.isGm()) {
                if (chatText.equals("開啟")) {
                    BroadcastController.getInstance().setStop(false);
                    pc.sendPackets(new S_SystemMessage("廣播系統已開啟。"));
                    return;
                } else if (chatText.equals("關閉")) {
                    BroadcastController.getInstance().setStop(true);
                    pc.sendPackets(new S_SystemMessage("廣播系統已關閉。"));
                    return;
                }
            }
            if (chatText.getBytes().length > 50) {
                pc.sendPackets(new S_SystemMessage("廣播訊息長度過長 (不能超過25個中文字)"));
                return;
            }
            // 連結字串
            final StringBuilder message = new StringBuilder();
            message.append("[").append(pc.getName()).append("] ").append(chatText);
            // 檢查背包是否有廣播卡
            final L1ItemInstance item = pc.getInventory().checkItemX(BroadcastSet.ITEM_ID, 1);
            if (item == null) {
                pc.sendPackets(new S_SystemMessage("不具有廣播卡，因此無法發送訊息。"));
                return;
            }
            // 將元素放入佇列
            if (BroadcastController.getInstance().requestWork(message.toString())) {
                // 刪除一個廣播卡道具
                pc.getInventory().removeItem(item, 1);
                pc.sendPackets(new S_SystemMessage("已成功發佈廣播訊息。"));
            } else {
                pc.sendPackets(new S_SystemMessage("目前有太多等待訊息，請稍後再嘗試一次。"));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * [原碼] 大樂透系統
     */
    private void donumber1() {
        this.BigHotAN = "";
        while (this.BigHotAN.split(",").length < 6) {
            int sk = 1 + (int) (Math.random() * 46.0D);
            if (this.BigHotAN.indexOf(sk + ",") < 0) {
                C_Chat tmp51_50 = this;
                tmp51_50.BigHotAN = tmp51_50.BigHotAN + String.valueOf(sk) + ",";
            }
        }
    }

    private void KeyInEnemyName(final L1PcInstance pc, final String name) {
        if (!CharacterTable.doesCharNameExist(name)) {
            pc.sendPackets(new S_ServerMessage("\\fU您輸入的名稱錯誤 資料庫無此資料。"));
            pc.setKeyInEnemy(false);
        } else {
            if (!pc.isInEnemyList(name)) {
                if (pc.getName().equals(name)) {
                    pc.sendPackets(new S_ServerMessage("\\fU不能添加自己為仇人"));
                } else {
                    pc.sendPackets(new S_ServerMessage("\\fU資料確認成功 !已經成功添加名單"));
                    NewAutoPractice.get().AddEnemyList(pc, name);
                    pc.setKeyInEnemy(false);
                    pc.sendPackets(new S_CloseList(pc.getId()));
                    pc.setInEnemyList(name);
                }
            } else {
                pc.sendPackets(new S_ServerMessage("\\fU名稱重複 請確認"));
                pc.setKeyInEnemy(false);
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        }
    }

    private void KeyOutEnemyName(final L1PcInstance pc, final String name) {
        if (!CharacterTable.doesCharNameExist(name)) {
            pc.sendPackets(new S_ServerMessage("\\fU您輸入的名稱錯誤 資料庫無此資料。"));
            pc.setKeyOutEnemy(false);
        } else {
            if (pc.isInEnemyList(name)) {
                pc.sendPackets(new S_ServerMessage("\\fU資料確認成功 !已經成功從名單中移除"));
                NewAutoPractice.get().DeleteEnemyList(pc, name);
                pc.setKeyOutEnemy(false);
                pc.sendPackets(new S_CloseList(pc.getId()));
                pc.removeInEnemyList(name);
            } else {
                pc.sendPackets(new S_ServerMessage("\\fU你的仇人名單內並無此人喔"));
                pc.setKeyOutEnemy(false);
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}