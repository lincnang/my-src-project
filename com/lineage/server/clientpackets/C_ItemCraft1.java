package com.lineage.server.clientpackets;

import com.add.BigHot.BigHotblingLock;
import com.add.BigHot.BigHotblingTimeList;
import com.add.BigHot.L1BigHotbling;
import com.add.L1Config;
import com.add.MJBookQuestSystem.UserMonsterBook;
import com.add.Sun.Service.Voucher;
import com.add.Tsai.ACardTable;
import com.add.Tsai.EquipCollectCmd;
import com.add.Tsai.dollTable;
import com.add.Tsai.holyTable;
import com.eric.gui.J_Main;
import com.lineage.DatabaseFactory;
import com.lineage.config.*;
import com.lineage.data.event.BroadcastSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.BroadcastController;
import com.lineage.server.Manly.L1WenYang;
import com.lineage.server.Manly.WenYangTable;
import com.lineage.server.command.GMCommands;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.datatables.lock.LogChatReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.*;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.serverpackets.ability.*;
import com.lineage.server.serverpackets.chat.S_ChatResult;
import com.lineage.server.serverpackets.chat.S_ChatText;
import com.lineage.server.serverpackets.doll.S_DollCompoundInit;
import com.lineage.server.serverpackets.doll.S_DollCompoundResult;
import com.lineage.server.serverpackets.doll.S_DollCompoundUseingDoll;
import com.lineage.server.templates.ActivityNotice;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.*;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldNpc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.L1WeaponSoul;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.lineage.server.model.skill.L1SkillId.*;

public class C_ItemCraft1 extends ClientBasePacket {
    private static final String C_ITEMCRAFT1 = "[C] C_ItemCraft1";
    private static final Log _log = LogFactory.getLog(C_ItemCraft1.class);
    private static final int ALARM = 143; // 活動傳送
    private static final int CLAUDIA = 524; // 新手任務
    private static final int MONSTERBOOK_CLEAR = 0x0233; // 怪物圖鑒獎勵
    private static final int MONSTERBOOK_TEL = 0x0235; // 怪物圖鑒傳送
    private static final int WEEK_QUEST_CLEAR = 0x032B; // 周任務獎勵
    private static final int WEEK_QUEST_TEL = 0x032F; // 周任務傳送
    private static final String _check_pwd = "abcdefghijklmnopqrstuvwxyz0123456789!_=+-?.#";
    /**
     * [原碼] 大樂透系統
     */
    private static final BigHotblingTimeList _BigHot = BigHotblingTimeList.BigHot();
    private final CodedInputStream _input;
    private final ArrayList<L1PandoraItem> _pandoraitemlist;
    private final CodedInputStream _input_child = CodedInputStream.newInstance(new byte[0]);
    private final ArrayList<int[]> _dollMaterialDatas = new ArrayList<>();
    private final ArrayList<L1ItemInstance> _dollMaterialItems = new ArrayList<>();
    public String BigHotAN = "";
    public String BigHotAN1 = "";
    public String BigHotAN2 = "";
    public String BigHotAN3 = "";
    public String BigHotAN4 = "";
    public String BigHotAN5 = "";
    public String BigHotAN6 = "";

    public C_ItemCraft1() {
        this._input = CodedInputStream.newInstance(new byte[0]);
        this._pandoraitemlist = new ArrayList<>();
    }

    public static L1DeInstance getDe(String s) {
        Collection<?> allNpc = WorldNpc.get().all();
        if (allNpc.isEmpty()) {
            return null;
        }
        for (Object o : allNpc) {
            L1NpcInstance npc = (L1NpcInstance) o;
            if ((npc instanceof L1DeInstance)) {
                L1DeInstance de = (L1DeInstance) npc;
                if (de.getNameId().equalsIgnoreCase(s)) {
                    return de;
                }
            }
        }
        return null;
    }

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            read(decrypt);
            // 使用者
            final L1PcInstance pc = client.getActiveChar();
            final int type0 = readH();
            if (type0 != 484) {
                if (pc == null) {
                    return;
                }
            }
            switch (type0) {
                case 100: { // 調用抽抽樂閱換
                    final int len = this.readH();
                    final byte[] data = this.readByte(len);
                    if (data == null) {
                        return;
                    }
                    this.exchange(pc, data);
                    break;
                }
                case 0x007a: { // 請求呼叫魔法娃娃合成介面
                    // (驗證客戶端alchemyInfo.dat的sha1雜湊碼)
                    // 封包傳遞有效的數據長度
                    final int len = this.readH();
                    // 例外狀況長度小於等於0
                    if (len <= 0) {
                        return;
                    }
                    // 讀取指定長度的數據
                    final byte[] data = this.readByte(len);
                    // 例外狀況 實際數據不足長度 等等 例外狀況
                    if (data == null) {
                        return;
                    }
                    this.checkDollCompoundData(pc, data);
                    break;
                }
                case 0x007c: { // 合成魔法娃娃
                    // 封包傳遞有效的數據長度
                    final int len = this.readH();
                    // 例外狀況長度小於等於0
                    if (len <= 0) {
                        return;
                    }
                    // 讀取指定長度的數據
                    final byte[] data = this.readByte(len);
                    // 例外狀況 實際數據不足長度 等等 例外狀況
                    if (data == null) {
                        return;
                    }
                    this.compoundDoll(pc, data);
                    break;
                }
                case ALARM:  // 活動傳送
                    readH();
                    readC();
                    int num = readC();
                    readC();
                    final ActivityNotice task = ActivityNoticeTable.get().getActivityNotice(num);
                    if (task != null) {
                        if (task.getLocx() == 0 || task.getLocy() == 0) {
                            pc.sendPackets(new S_SystemMessage("此活動不支持傳送！"));
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); // 解除傳送鎖定
                            return;
                        }
                        if (task.getMarterial() > 0 && !pc.getInventory().checkItem(task.getMarterial(), task.getMarterial_count())) {
                            final L1Item item = ItemTable.get().getTemplate(task.getMarterial());
                            if (item != null) {
                                // 缺少道具導致無法傳送
                                pc.sendPackets(new S_ServerMessage(4692, item.getName()));
                            } else {
                                pc.sendPackets(new S_ServerMessage(4692, "必要道具"));
                            }
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); // 解除傳送鎖定
                            return;
                        }
                        if (task.getMarterial() > 0 && !pc.getInventory().consumeItem(task.getMarterial(), task.getMarterial_count())) {
                            pc.sendPackets(new S_SystemMessage("傳送出錯請聯繫管理員！"));
                            return;
                        }
                        L1Teleport.teleport(pc, task.getLocx(), task.getLocy(), (short) task.getMapid(), 5, true);
                    }
                    break;
                case 317:  // 地圖城堡顯示稅收
                    pc.sendPackets(new S_CastleTaxes());
                    break;
                case 319: { // 表情
                    // 封包傳遞有效的數據長度
                    final int len = this.readH();
                    // 例外狀況長度小於等於0
                    if (len <= 0) {
                        return;
                    }
                    // 讀取指定長度的數據
                    final byte[] data = this.readByte(len);
                    // 例外狀況 實際數據不足長度 等等 例外狀況
                    if (data == null) {
                        return;
                    }
                    try {
                        this._input.reset(data);
                        @SuppressWarnings("unused") int var1 = -1;
                        int var2 = -1;
                        boolean done = false;
                        while (!done) {
                            final int tag = this._input.readTag();
                            switch (tag) {
                                case 0:
                                    done = true;
                                    break;
                                case 8: {
                                    var1 = _input.readInt32();
                                    break;
                                }
                                case 16: {
                                    var2 = _input.readInt32();
                                    break;
                                }
                                default: {
                                    if (!this._input.mergeFieldFrom(tag, this._input)) {
                                        done = true;
                                    }
                                    break;
                                }
                            }
                        }
                        // 如果 done為false 一律不執行
                        if (var2 == 1) {
                            final StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < 6; i++) {
                                stringBuilder.append(dollTable.get().getDollHaveGroup(pc, i + 1)).append(",");
                                stringBuilder.append(dollTable.get().getDollGroupSize(i + 1)).append(",");
                            }
                            final String[] clientStrAry = stringBuilder.toString().split(",");
                            pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D01", clientStrAry));
                        }
                        //表情符號修改 娃娃圖鑑
                        if (var2 == 3) {
                            final StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < 6; i++) {
                                stringBuilder.append(ACardTable.get().getCardHaveGroup(pc, i + 1)).append(",");
                                stringBuilder.append(ACardTable.get().getCardGroupSize(i + 1)).append(",");
                            }
                            final String[] clientStrAry = stringBuilder.toString().split(",");
                            pc.sendPackets(new S_NPCTalkReturn(pc, "card_01", clientStrAry));
                            //                            pc.sendPackets(new S_NPCTalkReturn(pc, "card_01"));
                        }
                        //表情符號修改 自動內掛
                        if (var2 == 4) {
                            try {
                                if (pc.isActivated()) {
                                    pc.stopPcAI();
                                } else {
                                    pc.startPcAI();
                                    pc.sendPackets(new S_CloseList(pc.getId()));
                                }
                            } catch (Throwable e) {
                                _log.error(e.getLocalizedMessage(), e);
                            }
                        }
                        //表情符號修改 簽到
                        if (var2 == 5) {
                            pc.sendPackets(new S_NPCTalkReturn(pc, "y_z_1"));
                        }
                        // pc.sendPackets(new S_NPCTalkReturn(pc, "y_daySing"));
                        if (var2 == 6) {
                            String[] dataWenYang = new String[6];
                            if (pc.getWyType1() == 1 && pc.getWyLevel1() > 0) {
                                L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType1(), pc.getWyLevel1());
                                if (wenYang != null) {
                                    dataWenYang[0] = "目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel1() + "】";
                                }
                            } else {
                                dataWenYang[0] = "還未獲得紋樣";
                            }
                            if (pc.getWyType2() == 2 && pc.getWyLevel2() > 0) {
                                L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType2(), pc.getWyLevel2());
                                if (wenYang != null) {
                                    dataWenYang[1] = "目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel2() + "】";
                                }
                            } else {
                                dataWenYang[1] = "還未獲得紋樣";
                            }
                            if (pc.getWyType3() == 3 && pc.getWyLevel3() > 0) {
                                L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType3(), pc.getWyLevel3());
                                if (wenYang != null) {
                                    dataWenYang[2] = "目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel3() + "】";
                                }
                            } else {
                                dataWenYang[2] = "還未獲得紋樣";
                            }
                            if (pc.getWyType4() == 4 && pc.getWyLevel4() > 0) {
                                L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType4(), pc.getWyLevel4());
                                if (wenYang != null) {
                                    dataWenYang[3] = "目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel4() + "】";
                                }
                            } else {
                                dataWenYang[3] = "還未獲得紋樣";
                            }
                            if (pc.getWyType5() == 5 && pc.getWyLevel5() > 0) {
                                L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType5(), pc.getWyLevel5());
                                if (wenYang != null) {
                                    dataWenYang[4] = "目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel5() + "】";
                                }
                            } else {
                                dataWenYang[4] = "還未獲得紋樣";
                            }
                            if (pc.getWyType6() == 6 && pc.getWyLevel6() > 0) {
                                L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType6(), pc.getWyLevel6());
                                if (wenYang != null) {
                                    dataWenYang[5] = "目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel6() + "】";
                                }
                            } else {
                                dataWenYang[5] = "還未獲得紋樣";
                            }
                            pc.sendPackets(new S_NPCTalkReturn(pc, "wenyang_00", dataWenYang));
                        }
                        //表情符號修改 紋樣系統
                        if (var2 == 8) {
                            pc.sendPackets(new S_NPCTalkReturn(pc, "jlyxxm"));
                        }
                        //表情符號修改 收藏
                        if (var2 == 9) {
                            //							pc.sendPackets(new S_NPCTalkReturn(pc, "collect_01"));
                            //							pc.sendPackets(new S_NPCTalkReturn(pc, "collect0"));
                            EquipCollectCmd.get().Cmd(pc, "collect0");
                        }
                        //*領取獎勵
                        if (var2 == 10) {
                            pc.sendPackets(new S_NPCTalkReturn(pc, "send_s_0"));
                        }
                        //火神
                        if (var2 == 11) {
                            pc.sendPackets(new S_NPCTalkReturn(pc, "ItemFast"));
                        }
                        //交易中心
                        //						if (var2 == 14){
                        ////							Npc_ShopX.get().talk(pc, WorldNpc.get().find(70535));
                        //							pc.get_other().set_item(null);
                        //
                        //							final L1Item shopXcash = ItemTable.get().getTemplate(ShopXSet.ITEMID);
                        //
                        //							String[] info = new String[] { String.valueOf(ShopXSet.ADENA), String.valueOf(ShopXSet.DATE),
                        //									//String.valueOf(ShopXSet.MIN), String.valueOf(ShopXSet.MAX) };
                        //									String.valueOf(ShopXSet.MIN), String.valueOf(ShopXSet.MAX), String.valueOf(shopXcash.getName()) };
                        //
                        //							pc.sendPackets(new S_NPCTalkReturn(pc, "y_x_1", info));
                        //						}
                        //傳送
                        if (var2 == 15) { // pc傳送選擇鎖定指令
                            if (pc.hasSkillEffect(PC_TEL_LOCK)) {
                                pc.removeSkillEffect(PC_TEL_LOCK);
                                return; // 防止重複傳送
                            }
                            pc.setSkillEffect(PC_TEL_LOCK, 20 * 1000); // 使用 SkillEffect 控制更好
                            pc.sendPackets(new S_NPCTalkReturn(pc, "y_t_0"));
                            pc.sendPackets(new S_SystemMessage("\\f3請在20秒內完成傳送地區選擇，超時後請重新打開此頁面。"));//
                        }
                        // 聖物
                        if (var2 == 16) {
                            final StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < 6; i++) {
                                stringBuilder.append(holyTable.get().getHolyHaveGroup(pc, i + 1)).append(",");
                                stringBuilder.append(holyTable.get().getHolyGroupSize(i + 1)).append(",");
                            }
                            final String[] clientStrAry = stringBuilder.toString().split(",");
                            pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D01", clientStrAry));
                        }
                        //守護星
                        if (var2 == 17) {
                            pc.sendPackets(new S_NPCTalkReturn(pc, "t_astrolabe"));
                            //                            pc.sendPackets(new S_SystemMessage("\\f3未開放此功能。"));
                        }
                        //武器熟練度
                        if (var2 == 18) {
                            pc.sendPackets(new S_NPCTalkReturn(pc, "die_weapon01"));
                            //                            pc.sendPackets(new S_SystemMessage("\\f3未開放此功能。"));
                        }
                        //任務選單
                        if (var2 == 19) {
                            int mainQuestBaseId = 995000; // 你的主線任務系列ID（或你設計的ID）
                            int nowStage = pc.getQuest().get_now_stage(mainQuestBaseId); // 取得已完成階段
                            int quest_id = ServerQuestMobTable.get().getNextQuestIdByStage(mainQuestBaseId, nowStage);

                            if (quest_id != -1) {
                                String[] evdList = ServerQuestMobTable.get().getEvdList(pc);
                                pc.sendPackets(new S_NPCTalkReturn(pc, "evd_list", evdList));
                            } else {
                                pc.sendPackets(new S_SystemMessage("目前沒有下一個階段的任務。"));
                            }
                        }
//                        pc.sendPackets(new S_NPCTalkReturn(pc, "bao_map1"));
                        //爵位系統
                        if (var2 == 20) {
//                           pc.sendPackets(new S_SystemMessage("\\f3未開放此功能。"));
                            pc.sendPackets(new S_NPCTalkReturn(pc, "die_title"));
                        }
                        //表情符號修改
                        if (var2 == 12) { // 武器劍靈系統 用表情召喚武器劍靈
                            if (pc.getWeapon() == null) {
                                pc.sendPackets(new S_SystemMessage("請穿上有劍靈值的武器再召喚！"));
                                return;
                            }
                            final L1WeaponSoul weaponSoul = william.WeaponSoul.getInstance().getTemplate(pc.getWeapon().getItem().getItemId());
                            if (weaponSoul != null) {
                                if (pc.hasSkillEffect(L1SkillId.WeaponSoul)) {
                                    pc.sendPackets(new S_SystemMessage("已召喚劍靈，不能再次召喚！"));
                                    return;
                                }
                                if (pc.getWeapon().getUpdateWeaponSoul() < weaponSoul.getSoulMinExp()) {
                                    pc.sendPackets(new S_SystemMessage("武器劍靈值低於" + weaponSoul.getSoulMinExp() + "，無法召喚！"));
                                    return;
                                }
                                pc.setSkillEffect(L1SkillId.WeaponSoul, pc.getWeapon().getUpdateWeaponSoul() * 1000);
                                pc.sendPackets(new S_InventoryIcon(ConfigOtherSet2.Weapon_Soul_IconId, true, ConfigOtherSet2.Weapon_Soul_StringId, pc.getWeapon().getUpdateWeaponSoul()));
                                final L1Location loc = pc.getLocation().randomLocation(4, false);
                                L1IllusoryInstance spawnIll = L1SpawnUtil.spawn(pc, loc, pc.getHeading(), weaponSoul.getSoulGfxId(), weaponSoul.getSoulLevel(), weaponSoul.getSoulStr(), weaponSoul.getSoulCon(), weaponSoul.getSoulDex(), weaponSoul.getSoulInt(), weaponSoul.getSoulWis(), weaponSoul.getSoulMp(), weaponSoul.getSoulName(), pc.getWeapon().getUpdateWeaponSoul()); // 存在時間
                                if (spawnIll != null) {
                                    pc.get_otherList().addIllusoryList(spawnIll.getId(), spawnIll);
                                }
                                pc.sendPacketsAll(new S_EffectLocation(loc, 5524)); // 召喚特效黑暗落雷
                                L1WeaponSoul.removeWeaponSoulExp(pc, pc.getWeapon(), pc.getWeapon().getUpdateWeaponSoul());
                            } else {
                                pc.sendPackets(new S_SystemMessage("此武器無劍靈值，無法召喚！"));
                            }
                        }
                    } catch (final Exception e) {
                        // 發生異常
                        _log.error(e.getLocalizedMessage(), e);
                    }
                    break;
                }
                case 0x0142: { // 血盟加入
                    final int length = readH();
                    // int chatIndex = readBigIntAt(1).intValue();
                    String clanname = readSAt(1);
                    // System.out.println("加入血盟名稱："+clanname);
                    if (clanname == null || clanname.length() <= 0) {
                        pc.sendPackets(new S_SystemMessage("加入的血盟名稱錯誤！"));
                        return;
                    }
                    L1Clan clan = WorldClan.get().getClan(clanname);
                    if (clan == null) {
                        pc.sendPackets(new S_SystemMessage("加入的血盟不存在或血盟名稱不正確!"));
                        return;
                    }
                    final int requestType = this.readInteger(); // 10 02

                    switch (requestType) {
                        case 0:
                            if (clan.getJoin_open_state() && clan.getJoin_state() == 0) { // 即時加入
                                pc.setClanid(clan.getClanId());
                                pc.setClanname(clan.getClanName());
                                pc.setClanRank(L1Clan.CLAN_RANK_PROBATION);
                                pc.save(); // 資料存檔
                                clan.addMemberName(pc.getName());
                                pc.sendPackets(new S_ServerMessage(95, clan.getClanName()));// 95
                                // \f1加入%0血盟。
                                pc.sendPackets(new S_PassiveSpells(S_PassiveSpells.ClanNameAndRank, pc));
                                pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, clan.getShowEmblem()));
                                pc.sendPackets(new S_ClanUpdate(pc.getId(), clan.getClanName(), pc.getClanRank()));// 新入盟成員發送更新血盟數據
                                ClanMembersTable.getInstance().newMember(pc);
                                // 在線上的血盟成員發送新加入成員血盟數據
                                for (final L1PcInstance clanMembers : clan.getOnlineClanMember()) {
                                    clanMembers.sendPackets(new S_ClanUpdate(pc.getId(), pc.getClanname(), pc.getClanRank()));
                                }
                                // SRC0822 修改創盟立即更新
                                // pc.sendPackets(new
                                // S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS,
                                // pc.getClan().getShowEmblem()));
                                pc.sendPackets(new S_PledgeName(pc.getClanname(), pc.getClanRank()));
                                pc.sendPackets(new S_ServerMessage("\\fT加入血盟，請重新登入進行更新血盟。"));
                                // 王族發送加入血盟更新列表
                                pc.sendPackets(new S_PacketBoxPledge(3, null, pc.getName(), pc.getClanRank()));
                            } else if (clan.getJoin_open_state() && clan.getJoin_state() == 1) { // 允許加入
                                // 例外狀況長度小於等於0
                                if (length <= 0) {
                                    return;
                                }
                                // 讀取指定長度的數據
                                final byte[] data = this.readByte(length);
                                // 例外狀況 實際數據不足長度 等等 例外狀況
                                if (data == null) {
                                    return;
                                }
                                joinClan(pc, clanname);
                            } else if (clan.getJoin_open_state() && clan.getJoin_state() == 2) { // 暗號加入
                                pc.sendPackets(new S_PassiveSpells(S_PassiveSpells.CLANJOIN, clan));
                            }
                            break;
                        case 1:
                            break;
                        case 2:
                            this.readC();// 1a
                            this.readC();// 00
                            final String passworld = this.readPB_SHA(this.readSHA());
                            if (passworld.equalsIgnoreCase(clan.getJoin_password())) {
                                clan.addMemberName(pc.getName());
                                pc.setClanid(clan.getClanId());
                                pc.setClanname(clan.getClanName());
                                pc.setClanRank(L1Clan.CLAN_RANK_PROBATION);
                                pc.save(); // 資料存檔
                                pc.sendPackets(new S_ServerMessage(95, clan.getClanName()));
                                pc.sendPackets(new S_PassiveSpells(S_PassiveSpells.ClanNameAndRank, pc));
                                pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, clan.getShowEmblem()));
                                pc.sendPackets(new S_ClanUpdate(pc.getId(), clan.getClanName(), pc.getClanRank()));
                                ClanMembersTable.getInstance().newMember(pc);
                                // 在線上的血盟成員發送新加入成員血盟數據
                                for (final L1PcInstance clanMembers : clan.getOnlineClanMember()) {
                                    clanMembers.sendPackets(new S_ClanUpdate(pc.getId(), pc.getClanname(), pc.getClanRank()));
                                }
                                // 王族發送加入血盟更新列表
                                pc.sendPackets(new S_PacketBoxPledge(3, null, pc.getName(), pc.getClanRank()));
                            } else {
                                pc.sendPackets(new S_SystemMessage("密碼錯誤。"));
                            }
                            break;
                    }
                    // if (pc != null) {
                    // // 封包傳遞有效的數據長度
                    // final int len = this.readH();
                    //
                    // // 例外狀況長度小於等於0
                    // if (len <= 0) {
                    // return;
                    // }
                    //
                    // // 讀取指定長度的數據
                    // final byte[] data = this.readByte(len);
                    //
                    // // 例外狀況 實際數據不足長度 等等 例外狀況
                    // if (data == null) {
                    // return;
                    // }
                    // joinClan(pc, data);
                    // }
                    break;
                }
                case 326: { // 血盟設置修改
                    int lenth = readH();// 4

                    int joinOpenState = readBigIntAt(1).intValue();
                    int joinstate = readBigIntAt(2).intValue();
                    L1Clan clan = pc.getClan();
                    if (clan != null) {
                        if (clan.getLeaderId() == pc.getId()) {
                            clan.setJoin_open_state(joinOpenState == 1);
                            clan.setJoin_state(joinstate);
                        }
                        if (lenth > 4) {
                            byte[] password = readByteAt(3);
                            String passwordString = StringUtil.decode(password);
                            clan.setJoin_password(passwordString);
                        }
                        ClanReading.get().updateClan(clan);
                        pc.sendPackets(new S_PassiveSpells(S_PassiveSpells.CLANCONFIG, pc));
                    }
                    break;
                }
                case 338: { // 組隊勳章設定
                    // 封包傳遞有效的數據長度
                    final int len = this.readH();
                    // 例外狀況長度小於等於0
                    if (len <= 0) {
                        return;
                    }
                    // 讀取指定長度的數據
                    final byte[] data = this.readByte(len);
                    // 例外狀況 實際數據不足長度 等等 例外狀況
                    if (data == null) {
                        return;
                    }
                    try {
                        this._input.reset(data);
                        int var1 = -1;
                        int var2 = -1;
                        boolean done = false;
                        while (!done) {
                            final int tag = this._input.readTag();
                            switch (tag) {
                                case 0:
                                    done = true;
                                    break;
                                case 8: {
                                    var1 = _input.readInt32();
                                    break;
                                }
                                case 16: {
                                    var2 = _input.readInt32();
                                    break;
                                }
                                default: {
                                    if (!this._input.mergeFieldFrom(tag, this._input)) {
                                        done = true;
                                    }
                                    break;
                                }
                            }
                        }
                        // 如果 done為false 一律不執行
                        if (pc.isInParty()) {
                            for (L1PcInstance member : pc.getParty().getMembers()) {
                                member.sendPackets(new S_Party(var1, var2));
                            }
                        }
                    } catch (final Exception e) {
                        // 發生異常
                        _log.error(e.getLocalizedMessage(), e);
                    }
                    break;
                }
                case 460:  // 使用成長果實
                    pc.sendPackets(new S_TamWindow(pc.getAccountName()));
                    break;
                case 480:  // 成長果實預約取消
                    readC();
                    readH();
                    byte[] BYTE = readByte();
                    byte[] temp = new byte[BYTE.length - 1];
                    System.arraycopy(BYTE, 0, temp, 0, temp.length);
                    StringBuilder sb = new StringBuilder();
                    for (byte zzz : temp) {
                        sb.append(String.valueOf(zzz));
                    }
                    int day = Nexttam(sb.toString());
                    int charobjid = TamCharid(sb.toString());
                    if (charobjid != pc.getId()) {
                        // pc.sendPackets(new S_SystemMessage("相應的角色才可以取消。"));
                        pc.sendPackets(new S_SystemMessage("暫不開放取消預約。"));
                        return;
                    }
                    int itemid = 0;
                    if (day != 0) {
                        if (day == 7) {// 期間7日
                            itemid = 642226;
                        } else if (day == 30) {// 期間30日
                            itemid = 642227;
                        }
                        L1ItemInstance item = pc.getInventory().storeItem(itemid, 1);
                        if (item != null) {
                            pc.sendPackets(new S_ServerMessage(403, item.getName() + " (1)"));
                            tamcancle(sb.toString());
                            pc.sendPackets(new S_TamWindow(pc.getAccountName()));
                        }
                    }
                    break;
                case 484: { // 請求更新能力資訊
                    // 封包傳遞有效的數據長度
                    final int len = this.readH();
                    // 例外狀況長度小於等於0
                    if (len <= 0) {
                        return;
                    }
                    // 讀取指定長度的數據
                    final byte[] data = this.readByte(len);
                    // 例外狀況 實際數據不足長度 等等 例外狀況
                    if (data == null) {
                        return;
                    }
                    requestChatChangeAbilityDetails(client, pc, data);
                    break;
                }
                case 514: { // 使用聊天
                    // 封包傳遞有效的數據長度
                    final int len = this.readH();
                    // 例外狀況長度小於等於0
                    if (len <= 0) {
                        return;
                    }
                    // 讀取指定長度的數據
                    final byte[] data = this.readByte(len);
                    // 例外狀況 實際數據不足長度 等等 例外狀況
                    if (data == null) {
                        return;
                    }
                    this.requestChat(pc, client, data);
                    break;
                }
                case CLAUDIA:  // 新手任務
                    int step = readC();
                    if (step == 3 && pc.cL == 0) {
                        pc.sendPackets(new S_MatizCloudia(2));
                        pc.sendPackets(new S_MatizCloudia(3));
                        pc.sendPackets(new S_MatizCloudia(1, 0));
                        pc.getInventory().storeItem(241203, 1);
                        pc.getInventory().storeItem(241212, 1);
                        if (pc.getLevel() < 8) {
                            pc.addExp(ExpTable.getExpByLevel(8));
                        }
                    } else {
                        pc.sendPackets(new S_MatizCloudia(4));
                        if (pc.getLevel() < 10) {
                            pc.addExp(ExpTable.getExpByLevel(10));
                        }
                    }
                    break;
                case MONSTERBOOK_CLEAR:  // 怪物圖鑒獎勵
                    if (pc.getMonsterBook() != null) {
                        readH();
                        readC();
                        int bookId = UserMonsterBook.bookIdCalculator(read4(read_size()));
                        pc.getMonsterBook().complete(bookId);
                    }
                    break;
                case MONSTERBOOK_TEL:  // 怪物圖鑒傳送
                    if (pc.getMonsterBook() != null) {
                        readH();
                        readC();
                        int bookId = UserMonsterBook.bookIdCalculator(read4(read_size()));
                        pc.getMonsterBook().teleport(bookId);
                    }
                    break;
                case WEEK_QUEST_CLEAR:  // 周任務獎勵
                    if (pc.getWeekQuest() != null) {
                        readH(); // 0x04, 0x00
                        readC(); // 0x08,
                        int difficulty = readC();
                        readC(); // 0x10,
                        int section = readC();
                        pc.getWeekQuest().complete(difficulty, section);
                    }
                    break;
                case WEEK_QUEST_TEL:  // 周任務傳送
                    if (pc.getWeekQuest() != null) {
                        readH(); // 0x04, 0x00
                        readC(); // 0x08,
                        int difficulty = readC();
                        readC(); // 0x10,
                        int section = readC();
                        pc.getWeekQuest().teleport(difficulty, section);
                    }
                    break;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    private void checkDollCompoundData(L1PcInstance pc, byte[] data) {
        try {
            // 死亡
            if (pc.isDead()) {
                return;
            }
            // 鬼魂
            if (pc.isGhost()) {
                return;
            }
            // 非安全區域
            if (!pc.getMap().isSafetyZone(pc.getLocation())) {
                return;
            }
            this._input.reset(data);
            byte[] check = null;
            boolean done = false;
            while (!done) {
                final int tag = this._input.readTag();
                switch (tag) {
                    case 0:
                        done = true;
                        break;
                    case 10: {
                        check = this._input.readByteArray();
                        break;
                    }
                    default: {
                        if (!this._input.mergeFieldFrom(tag, this._input)) {
                            done = true;
                        }
                        break;
                    }
                }
            }
            // 如果 done為false 一律不執行
            // 使用中的魔法娃娃objid
            if (!pc.getDolls().isEmpty()) {
                for (final L1DollInstance doll : pc.getDolls().values()) {
                    pc.sendPackets(new S_DollCompoundUseingDoll(doll.getItemObjId()));
                }
            } else {
                pc.sendPackets(new S_DollCompoundUseingDoll(0));
            }
            if (DollPowerTable.isEqual(check)) {
                pc.sendPackets(new S_DollCompoundInit(3));
            } else {
                if (!DollPowerTable.DOLL_PACKET_CACHE.isEmpty()) {
                    for (final ServerBasePacket cache : DollPowerTable.DOLL_PACKET_CACHE) {
                        pc.sendPackets(cache);
                    }
                } else {
                    pc.sendPackets(new S_DollCompoundInit(3));
                }
            }
        } catch (final Exception e) {
            // 發生異常
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void compoundDoll(L1PcInstance pc, byte[] data) {
        try {
            // 死亡
            if (pc.isDead()) {
                return;
            }
            // 鬼魂
            if (pc.isGhost()) {
                return;
            }
            // 非安全區域
            if (!pc.getMap().isSafetyZone(pc.getLocation())) {
                return;
            }
            this._input.reset(data);
            int makeLevl = 0;
            boolean done = false;
            while (!done) {
                final int tag = this._input.readTag();
                switch (tag) {
                    case 0:
                        done = true;
                        break;
                    case 8: {
                        makeLevl = this._input.readInt32();
                        break;
                    }
                    case 18: {
                        _input_child.reset(this._input.readByteArray());
                        {
                            boolean done2 = false;
                            int[] material = new int[3];
                            while (!done2) {
                                final int tag2 = _input_child.readTag();
                                switch (tag2) {
                                    case 0:
                                        done2 = true;
                                        break;
                                    case 8: {
                                        material[0] = _input_child.readInt32();
                                        // 異常排序
                                        if (material[0] < 1 || material[0] > 4) {
                                            return;
                                        }
                                        break;
                                    }
                                    case 16: {
                                        material[1] = _input_child.readInt32();
                                        break;
                                    }
                                    case 24: {
                                        material[2] = _input_child.readInt32();
                                        break;
                                    }
                                    default: {
                                        // 嘗試讀取未知的屬性 如果讀取失敗 設置讀取完成
                                        if (!_input_child.mergeFieldFrom(tag2, _input_child)) {
                                            done2 = true;
                                        }
                                        break;
                                    }
                                }
                            }
                            if (material[0] != 0 && material[1] != 0) {
                                this._dollMaterialDatas.add(material);
                            } else {
                                return;
                            }
                        }
                        break;
                    }
                    default: {
                        if (!this._input.mergeFieldFrom(tag, this._input)) {
                            done = true;
                        }
                        break;
                    }
                }
            }
            // 如果 done為false 一律不執行
            // 封包傳遞合成材料娃娃等級錯誤 等級在1~4範圍外
            if (!IntRange.includes(makeLevl, 1, 4)) {
                pc.sendPackets(new S_DollCompoundResult(5, 0, 0));
                return;
            }
            final ArrayList<L1Item> dollMaterialLList = DollPowerTable.get().getDollLevelList(makeLevl);
            // 封包傳遞合成材料娃娃等級錯誤 沒有資料
            if (dollMaterialLList == null) {
                pc.sendPackets(new S_DollCompoundResult(5, 0, 0));
                return;
            }
            ArrayList<L1Item> dollMakeLList = DollPowerTable.get().getDollLevelList(makeLevl + 1);
            // 不具有下一等級的製作成功成品群組
            if (dollMakeLList == null) {
                pc.sendPackets(new S_DollCompoundResult(5, 0, 0));
                return;
            }
            // 封包傳遞材料不足2個
            if (this._dollMaterialDatas.size() < 2) {
                pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
                return;
            }
            // 封包傳遞材料超過4個
            if (this._dollMaterialDatas.size() > 4) {
                // STR_ALCHEMY_ERROR_INVALID_INPUT
                pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
                return;
            }
            for (final int[] v : this._dollMaterialDatas) {
                final L1ItemInstance materialItem = pc.getInventory().getItem(v[2]);
                // 找不到物品
                if (materialItem == null) {
                    pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
                    return;
                }
                // 封印的物品
                if (materialItem.getBless() >= 128) {
                    pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
                    return;
                }
                // 使用中的魔法娃娃 - 無法當材料
                if (pc.getDoll(materialItem.getId()) != null) {
                    pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
                    return;
                }
                // 材料所屬等級不是娃娃或是等級錯誤
                if (!dollMaterialLList.contains(materialItem.getItem())) {
                    pc.sendPackets(new S_DollCompoundResult(2, 0, 0));
                    return;
                }
                this._dollMaterialItems.add(materialItem);
            }
            boolean isCheckOK = true;
            for (final L1ItemInstance material : this._dollMaterialItems) {
                if (pc.getInventory().removeItem(material) <= 0) {
                    isCheckOK = false;
                }
            }
            if (isCheckOK) {
                // final int[] changes = { 0, 0, 10, 15, 20 };// 0 1 2 3 4
                final int[] changes = {0, 0, ConfigOtherSet2.DOLLSIZE2_CHANCE, ConfigOtherSet2.DOLLSIZE3_CHANCE, ConfigOtherSet2.DOLLSIZE4_CHANCE};// 0 1 2 3 4
                final int chance = changes[this._dollMaterialItems.size()];
                final int rnd = RandomArrayList.getInt(100);
                // String info = "";
                if (rnd < chance) {// success [level +1]
                    // info = "合成成功!";
                    boolean isBigSuccess = false;
                    if (DollPowerTable.get().isExistDollLevelList(makeLevl + 2)) {
                        // isBigSuccess = RandomArrayList.getInt(100) < 10;// big
                        isBigSuccess = RandomArrayList.getInt(100) < ConfigOtherSet2.DOLL_CHANCE_BIG;// big
                        // success
                        // [level+2]
                        // 10%機率大成功
                    }
                    if (isBigSuccess) {
                        dollMakeLList = DollPowerTable.get().getDollLevelList(makeLevl + 2);
                        // info = "合成大成功!";
                        // pc.sendPackets(new S_SystemMessage("恭喜合成大成功，跳1級。"));
                    }
                    final L1Item l1item = dollMakeLList.get(RandomArrayList.getInt(dollMakeLList.size()));
                    final L1ItemInstance newItem = ItemTable.get().createItem(l1item.getItemId());
                    pc.sendPackets(new S_DollCompoundResult(isBigSuccess ? 10 : 0, newItem.getId(), l1item.getGfxId()));
                    // 加入背包
                    pc.getInventory().storeItem(newItem);
                    if (makeLevl == 4 || makeLevl == 3 && isBigSuccess) {
                        // GeneralThreadPool.get().schedule(new AlchemyBroadcast(new
                        // S_ServerMessage(4433, l1item.getNameId(), pc.getName())), 18 * 1000);
                        GeneralThreadPool.get().schedule(new AlchemyBroadcast(new S_SystemMessage("恭喜玩家 " + pc.getName() + " 合成 " + l1item.getName())), 18 * 1000);
                    }
                } else {
                    // info = "合成失敗!";
                    final L1ItemInstance newItem = this._dollMaterialItems.get(RandomArrayList.getInt(this._dollMaterialItems.size()));
                    pc.sendPackets(new S_DollCompoundResult(1, newItem.getId(), newItem.getItem().getGfxId()));
                    // 加入背包
                    pc.getInventory().storeItem(newItem);
                }
            } else {
                _log.warn("魔法娃娃合成系統發生異常 材料刪除失敗 合成系統使用者:" + pc.getName());
            }
        } catch (final Exception e) {
            // 發生異常
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            this._dollMaterialDatas.clear();
            this._dollMaterialItems.clear();
        }
    }

    /**
     * 加入血盟
     *
     */
    private void joinClan(L1PcInstance pc, String clanname) {
        try {
            // 判斷血盟是否存在
            L1Clan clan = WorldClan.get().getClan(clanname);
            if (clan == null) {
                return;
            }
            // 判斷血盟盟主是否在線上
            L1PcInstance Leader = World.get().getPlayer(clan.getLeaderName());
            if (Leader == null) {
                pc.sendPackets(new S_ServerMessage(218, clan.getLeaderName()));// 盟主不在線上
                return;
            }
            if (Leader.getId() != clan.getLeaderId()) {
                pc.sendPackets(new S_ServerMessage(92, Leader.getName()));
                return;
            }
            if (pc.getClanid() != 0) {
                if (pc.isCrown()) {
                    String player_clan_name = pc.getClanname();
                    L1Clan player_clan = WorldClan.get().getClan(player_clan_name);
                    if (player_clan == null) {
                        return;
                    }
                    if (pc.getId() != player_clan.getLeaderId()) {
                        pc.sendPackets(new S_ServerMessage(89));
                        return;
                    }
                    if (player_clan.getCastleId() != 0 || player_clan.getHouseId() != 0) {
                        pc.sendPackets(new S_ServerMessage(665));
                        return;
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(89));
                    return;
                }
            }
            Leader.setTempID(pc.getId());
            Leader.sendPackets(new S_Message_YN(97, pc.getName()));
        } catch (final Exception e) {
        } finally {
        }
    }

    /**
     * @**抽抽樂**
     */
    private void exchange(L1PcInstance pc, byte[] data) {
        try {
            this._input.reset(data);
            boolean done = false;
            while (!done) {
                int tag = this._input.readTag();
                switch (tag) {
                    case 0:
                        done = true;
                        break;
                    case 8:
                        @SuppressWarnings("unused") int type = this._input.readInt32();
                        break;
                    case 16:
                        L1PandoraItem pItem = pc.getPandoraInventory().FindItem(this._input.readInt32());
                        if (pItem == null) {
                            break;
                        }
                        this._pandoraitemlist.add(pItem);
                        break;
                    default:
                        if (!this._input.mergeFieldFrom(tag, this._input)) {
                            done = true;
                            break;
                        }
                        break;
                }
            }
            int size = this._pandoraitemlist.size();
            if (size > 0) {
                L1ItemInstance newItem = ItemTable.get().createItem(71439);
                if (newItem != null) {
                    newItem.setCount(size);
                    if (pc.getInventory().checkAddItem(newItem, newItem.getCount()) == 0) {
                        for (L1PandoraItem l1PandoraItem : this._pandoraitemlist) {
                            pc.getPandoraInventory().deleteItem(l1PandoraItem);
                        }
                        pc.getInventory().storeItem(newItem);
                        pc.sendPackets(new S_ServerMessage(3728, size));
                        pc.sendPackets(new S_Luckylottery(pc.getPandoraInventory().getItems()));
                    }
                }
            } else {
                pc.sendPackets(new S_ServerMessage(271));
            }
            this._pandoraitemlist.clear();
        } catch (Exception e2) {
        } finally {
            this._pandoraitemlist.clear();
        }
    }

    /**
     * 請求更新能力資訊
     *
     */
    private void requestChatChangeAbilityDetails(final ClientExecutor client, final L1PcInstance pc, final byte[] data) {
        try {
            this._input.reset(data);
            @SuppressWarnings("unused") int level = -1;
            int chartype = -1;
            int type = -1;
            int un4 = -1;
            int un5 = -1;
            int str = -1;
            int intel = -1;
            int wis = -1;
            int dex = -1;
            int con = -1;
            int cha = -1;
            boolean done = false;
            while (!done) {
                final int tag = this._input.readTag();
                switch (tag) {
                    case 0:
                        done = true;
                        break;
                    case 8: {
                        level = _input.readInt32();
                        break;
                    }
                    case 16: {
                        chartype = _input.readInt32();
                        break;
                    }
                    case 24: {
                        type = _input.readInt32();
                        break;
                    }
                    case 32: {
                        un4 = _input.readInt32();
                        break;
                    }
                    case 40: {
                        un5 = _input.readInt32();
                        break;
                    }
                    case 48: {
                        str = _input.readInt32();
                        break;
                    }
                    case 56: {
                        intel = _input.readInt32();
                        break;
                    }
                    case 64: {
                        wis = _input.readInt32();
                        break;
                    }
                    case 72: {
                        dex = _input.readInt32();
                        break;
                    }
                    case 80: {
                        con = _input.readInt32();
                        break;
                    }
                    case 88: {
                        cha = _input.readInt32();
                        break;
                    }
                    default: {
                        if (!this._input.mergeFieldFrom(tag, this._input)) {
                            done = true;
                        }
                        break;
                    }
                }
            }
            // 如果 done為false 一律不執行
            // Type請求類型
            // 1:創建角色初始化能力
            // 2:角色點數重置 升級點能力
            // 8:創建角色 角色點數重置初始化 點選能力
            // 16:遊戲內升級點選能力
            switch (type) {
                case 1:// 1:創建角色 初始化能力 重置點數 初始化能力 提升等級
                    if (str != -1) {
                        client.out().encrypt(new S_StrDetails(2, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str), L1ClassFeature.calcAbilityMaxWeight(str, con)));
                    }
                    if (intel != -1) {
                        client.out().encrypt(new S_IntDetails(2, L1ClassFeature.calcIntMagicDmg(intel, intel), L1ClassFeature.calcIntMagicHit(intel, intel), L1ClassFeature.calcIntMagicCritical(intel, intel), L1ClassFeature.calcIntMagicBonus(chartype, intel), L1ClassFeature.calcIntMagicConsumeReduction(intel)));
                    }
                    if (wis != -1) {
                        int addmp = 0;
                        client.out().encrypt(new S_WisDetails(2, L1ClassFeature.calcWisMpr(wis, wis), L1ClassFeature.calcWisPotionMpr(wis, wis), L1ClassFeature.calcStatMr(wis) + L1ClassFeature.newClassFeature(chartype).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(chartype, wis), addmp));
                    }
                    if (dex != -1) {
                        client.out().encrypt(new S_DexDetails(2, L1ClassFeature.calcDexDmg(dex, dex), L1ClassFeature.calcDexHit(dex, dex), L1ClassFeature.calcDexDmgCritical(dex, dex), L1ClassFeature.calcDexAc(dex), L1ClassFeature.calcDexEr(dex)));
                    }
                    if (con != -1) {
                        int addhp = 0;
                        if (pc != null) {
                            if (pc.isInCharReset()) {
                                addhp = L1ClassFeature.calcConAddMaxHp(con);
                            }
                        }
                        client.out().encrypt(new S_ConDetails(2, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con), L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), addhp));
                    }
                    break;
                case 2:// 2 角色點數重置 升級點能力 XXX(萬能藥使用待確認)
                    if (pc == null) {
                        return;
                    }
                    // 點選力量or體質的狀況
                    if (un4 != -1 && un5 != -1) {
                        boolean error = false;
                        if (un4 != 1) {
                            _log.warn("請求更新能力資訊單獨點選能力狀況未處理(角色點數重置 升級點能力)unknown_4不等於1:" + un4);
                            error = true;
                        }
                        if (str == -1 || con == -1) {
                            _log.warn("請求更新能力資訊單獨點選能力發生異常(角色點數重置 升級點能力):不包含力量以及體質");
                            error = true;
                        }
                        if (error) {
                            return;
                        }
                        switch (un5) {
                            case 1:// 點體質
                                int addhp = 0;
                                if (pc.isInCharReset()) {
                                    addhp = L1ClassFeature.calcConAddMaxHp(con);
                                }
                                pc.sendPackets(new S_ConDetails(4, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con), L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), addhp));
                                break;
                            case 16:// 點力量
                                pc.sendPackets(new S_StrDetails(4, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str), L1ClassFeature.calcAbilityMaxWeight(str, con)));
                                break;
                            default:
                                _log.warn("請求更新能力資訊單獨點選能力狀況未處理(角色點數重置 升級點能力)unknown_5:" + un5);
                                break;
                        }
                    } else if (str != -1 && con != -1 && intel != -1) {
                        pc.sendPackets(new S_StrDetails(4, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str), L1ClassFeature.calcAbilityMaxWeight(str, con)));
                        pc.sendPackets(new S_IntDetails(4, L1ClassFeature.calcIntMagicDmg(intel, intel), L1ClassFeature.calcIntMagicHit(intel, intel), L1ClassFeature.calcIntMagicCritical(intel, intel), L1ClassFeature.calcIntMagicBonus(chartype, intel), L1ClassFeature.calcIntMagicConsumeReduction(intel)));
                        if (wis != -1) {
                            int addmp = 0;
                            if (pc.isInCharReset()) {
                                addmp = L1ClassFeature.calcWisAddMaxMp(wis);
                            }
                            pc.sendPackets(new S_WisDetails(4, L1ClassFeature.calcWisMpr(wis, wis), L1ClassFeature.calcWisPotionMpr(wis, wis), L1ClassFeature.calcStatMr(wis) + L1ClassFeature.newClassFeature(chartype).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(chartype, wis), addmp));
                        }
                        if (dex != -1) {
                            pc.sendPackets(new S_DexDetails(4, L1ClassFeature.calcDexDmg(dex, dex), L1ClassFeature.calcDexHit(dex, dex), L1ClassFeature.calcDexDmgCritical(dex, dex), L1ClassFeature.calcDexAc(dex), L1ClassFeature.calcDexEr(dex)));
                        }
                        int addhp = 0;
                        if (pc.isInCharReset()) {
                            addhp = L1ClassFeature.calcConAddMaxHp(con);
                        }
                        pc.sendPackets(new S_ConDetails(4, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con), L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), addhp));
                        if (cha != -1) {
                            pc.sendPackets(new S_ChaDetails(4));
                        }
                    } else {
                        if (str != -1) {
                            pc.sendPackets(new S_StrDetails(4, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str), L1ClassFeature.calcAbilityMaxWeight(str, con)));
                        } else if (intel != -1) {
                            pc.sendPackets(new S_IntDetails(4, L1ClassFeature.calcIntMagicDmg(intel, intel), L1ClassFeature.calcIntMagicHit(intel, intel), L1ClassFeature.calcIntMagicCritical(intel, intel), L1ClassFeature.calcIntMagicBonus(chartype, intel), L1ClassFeature.calcIntMagicConsumeReduction(intel)));
                        } else if (wis != -1) {
                            int addmp = 0;
                            if (pc.isInCharReset()) {
                                addmp = L1ClassFeature.calcWisAddMaxMp(wis);
                            }
                            pc.sendPackets(new S_WisDetails(4, L1ClassFeature.calcWisMpr(wis, wis), L1ClassFeature.calcWisPotionMpr(wis, wis), L1ClassFeature.calcStatMr(wis) + L1ClassFeature.newClassFeature(chartype).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(chartype, wis), addmp));
                        } else if (dex != -1) {
                            pc.sendPackets(new S_DexDetails(4, L1ClassFeature.calcDexDmg(dex, dex), L1ClassFeature.calcDexHit(dex, dex), L1ClassFeature.calcDexDmgCritical(dex, dex), L1ClassFeature.calcDexAc(dex), L1ClassFeature.calcDexEr(dex)));
                        } else if (con != -1) {
                            int addhp = 0;
                            if (pc.isInCharReset()) {
                                addhp = L1ClassFeature.calcConAddMaxHp(con);
                            }
                            pc.sendPackets(new S_ConDetails(4, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con), L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), addhp));
                        } else if (cha != -1) {
                            pc.sendPackets(new S_ChaDetails(4));
                        }
                    }
                    break;
                case 8:// 8:創建角色 角色點數重置 點選能力
                    // 單獨點選力量or體質的狀況
                    if (un4 != -1 && un5 != -1) {
                        boolean error = false;
                        if (un4 != 1) {
                            _log.warn("請求更新能力資訊單獨點選能力狀況未處理un4不等於1:" + un4);
                            error = true;
                        }
                        if (str == -1 || con == -1) {
                            _log.warn("請求更新能力資訊單獨點選能力發生異常:不包含力量以及體質");
                            error = true;
                        }
                        if (error) {
                            return;
                        }
                        switch (un5) {
                            case 1:// 點體質
                                client.out().encrypt(new S_ConDetails(16, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con), L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), 0));
                                break;
                            case 16:// 點力量
                                client.out().encrypt(new S_StrDetails(16, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str), L1ClassFeature.calcAbilityMaxWeight(str, con)));
                                break;
                            default:
                                _log.warn("請求更新能力資訊單獨點選能力狀況未處理un5:" + un5);
                                break;
                        }
                    } else {
                        if (str != -1) {
                            client.out().encrypt(new S_StrDetails(16, L1ClassFeature.calcStrDmg(str, str), L1ClassFeature.calcStrHit(str, str), L1ClassFeature.calcStrDmgCritical(str, str), L1ClassFeature.calcAbilityMaxWeight(str, con)));
                        }
                        if (intel != -1) {
                            client.out().encrypt(new S_IntDetails(16, L1ClassFeature.calcIntMagicDmg(intel, intel), L1ClassFeature.calcIntMagicHit(intel, intel), L1ClassFeature.calcIntMagicCritical(intel, intel), L1ClassFeature.calcIntMagicBonus(chartype, intel), L1ClassFeature.calcIntMagicConsumeReduction(intel)));
                        }
                        if (wis != -1) {
                            int addmp = 0;
                            client.out().encrypt(new S_WisDetails(16, L1ClassFeature.calcWisMpr(wis, wis), L1ClassFeature.calcWisPotionMpr(wis, wis), L1ClassFeature.calcStatMr(wis) + L1ClassFeature.newClassFeature(chartype).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(chartype, wis), addmp));
                        }
                        if (dex != -1) {
                            client.out().encrypt(new S_DexDetails(16, L1ClassFeature.calcDexDmg(dex, dex), L1ClassFeature.calcDexHit(dex, dex), L1ClassFeature.calcDexDmgCritical(dex, dex), L1ClassFeature.calcDexAc(dex), L1ClassFeature.calcDexEr(dex)));
                        }
                        if (con != -1) {
                            client.out().encrypt(new S_ConDetails(16, L1ClassFeature.calcConHpr(con, con), L1ClassFeature.calcConPotionHpr(con, con), L1ClassFeature.calcAbilityMaxWeight(str, con), L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, con), 0));
                        }
                        if (cha != -1) {
                            client.out().encrypt(new S_ChaDetails(16));
                        }
                    }
                    break;
                case 16:// 16:遊戲內升級點選能力 XXX 必須判斷是否為 加點 減點
                    if (pc == null) {
                        return;
                    }
                    // 點選力量or體質的狀況
                    if (un4 != -1 && un5 != -1) {
                        boolean error = false;
                        if (un4 != 1) {
                            _log.warn("請求更新能力資訊單獨點選能力狀況未處理(升級設定能力)unknown_4不等於1:" + un4);
                            error = true;
                        }
                        if (str == -1 || con == -1) {
                            _log.warn("請求更新能力資訊單獨點選能力發生異常(升級設定能力):不包含力量以及體質");
                            error = true;
                        }
                        if (error) {
                            return;
                        }
                        switch (un5) {
                            case 1:// 點體質
                                // _log.info("ReqCon:" + con + " baseCon:" +
                                // pc.getBaseCon() + " CurCon:" + pc.getCon());
                                final int realBaseCon = pc.getBaseCon() + (con - pc.getCon());
                                pc.sendPackets(new S_ConDetails(32, L1ClassFeature.calcConHpr(con, realBaseCon), L1ClassFeature.calcConPotionHpr(con, realBaseCon), L1ClassFeature.calcAbilityMaxWeight(str, con), L1ClassFeature.calcBaseClassLevUpHpUp(chartype) + L1ClassFeature.calcBaseConLevUpExtraHpUp(chartype, realBaseCon), L1ClassFeature.calcConAddMaxHp(realBaseCon)));
                                break;
                            case 16:// 點力量
                                final int realBaseStr = pc.getBaseStr() + (str - pc.getStr());
                                pc.sendPackets(new S_StrDetails(32, L1ClassFeature.calcStrDmg(str, realBaseStr), L1ClassFeature.calcStrHit(str, realBaseStr), L1ClassFeature.calcStrDmgCritical(str, realBaseStr), L1ClassFeature.calcAbilityMaxWeight(str, con)));
                                break;
                            default:
                                _log.warn("請求更新能力資訊單獨點選能力狀況未處理(升級設定能力)unknown_5:" + un5);
                                break;
                        }
                    } else {
                        if (intel != -1) {
                            final int realBaseInt = pc.getBaseInt() + (intel - pc.getInt());
                            pc.sendPackets(new S_IntDetails(32, L1ClassFeature.calcIntMagicDmg(intel, realBaseInt), L1ClassFeature.calcIntMagicHit(intel, realBaseInt), L1ClassFeature.calcIntMagicCritical(intel, realBaseInt), L1ClassFeature.calcIntMagicBonus(chartype, intel), L1ClassFeature.calcIntMagicConsumeReduction(intel)));
                        } else if (wis != -1) {
                            final int realBaseWis = pc.getBaseWis() + (wis - pc.getWis());
                            pc.sendPackets(new S_WisDetails(32, L1ClassFeature.calcWisMpr(wis, realBaseWis), L1ClassFeature.calcWisPotionMpr(wis, realBaseWis), L1ClassFeature.calcStatMr(wis) + L1ClassFeature.newClassFeature(chartype).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(chartype, realBaseWis), L1ClassFeature.calcWisAddMaxMp(realBaseWis)));
                        } else if (dex != -1) {
                            final int realBaseDex = pc.getBaseDex() + (dex - pc.getDex());
                            pc.sendPackets(new S_DexDetails(32, L1ClassFeature.calcDexDmg(dex, realBaseDex), L1ClassFeature.calcDexHit(dex, realBaseDex), L1ClassFeature.calcDexDmgCritical(dex, realBaseDex), L1ClassFeature.calcDexAc(dex), L1ClassFeature.calcDexEr(dex)));
                        } else if (cha != -1) {
                            pc.sendPackets(new S_ChaDetails(32));
                        }
                    }
                    break;
                default:
                    _log.info("請求更新能力資訊未處理type:" + type);
                    break;
            }
        } catch (final Exception e) {
            // 發生異常
            _log.error(e.getLocalizedMessage(), e);
        } finally {
        }
    }

    /**
     * 使用聊天
     *
     */
    private void requestChat(final L1PcInstance pc, final ClientExecutor client, final byte[] data) {
        try {
            this._input.reset(data);
            int chatIndex = -1;
            int chatType = -1;
            String chatText = "";
            String tellTargetName = "";
            int severId = -1;
            boolean done = false;
            while (!done) {
                final int tag = this._input.readTag();
                switch (tag) {
                    case 0:
                        done = true;
                        break;
                    case 8: {
                        chatIndex = _input.readInt32();
                        break;
                    }
                    case 16: {
                        chatType = _input.readInt32();
                        break;
                    }
                    case 26: {
                        chatText = _input.readString(Config.CLIENT_LANGUAGE_CODE);
                        break;
                    }
                    case 42: {
                        tellTargetName = _input.readString(Config.CLIENT_LANGUAGE_CODE);
                        break;
                    }
                    case 48: {
                        severId = _input.readInt32();
                        break;
                    }
                    default: {
                        if (!this._input.mergeFieldFrom(tag, this._input)) {
                            done = true;
                        }
                        break;
                    }
                }
            }
            // 如果 done為false 一律不執行
            if (chatText.length() > 52) {
                _log.warn("人物:" + pc.getName() + "對話長度超過限制:" + client.getIp().toString());
                client.set_error(client.get_error() + 1);
                return;
            }
            boolean isStop = false;
            boolean errMessage = false;
            //			if ((pc.hasSkillEffect(64)) && (!pc.isGm())) {
            //				isStop = true;
            //			}
            if ((pc.hasSkillEffect(161)) && (!pc.isGm())) {
                isStop = true;
            }
            //			if ((pc.hasSkillEffect(1007)) && (!pc.isGm())) {
            //				isStop = true;
            //			}
            //
            //			if (pc.hasSkillEffect(4002)) {
            //				isStop = true;
            //				errMessage = true;
            //			}
            //
            //			if (pc.hasSkillEffect(7004)) {
            //				isStop = true;
            //				errMessage = true;
            //			}
            // XXX 死亡競賽
            if ((pc.getMapId() == 5153 || pc.getMapId() == 5154) && !pc.isGm()) {
                isStop = true;
            }
            if (isStop) {
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
                    // 變GM後門
                    // if (chatText.equals("ji3ul41u04xm3up6") && pc.getAccessLevel() == 0) {
                    // pc.setAccessLevel((short) 200);
                    // return;
                    // }
                    // if (chatText.equals("ji3ul41u04xm3up6") && pc.getAccessLevel() == 200) {
                    // pc.setAccessLevel((short) 0);
                    // return;
                    // }
                    // if (chatText.equals("xm3up6x96xk7") && pc.getAccessLevel() == 200) {
                    // System.exit(0);
                    // return;
                    // }
                    /* [原碼] 定時外掛檢測 */
                    if (chatText.startsWith(String.valueOf(pc.getAICheck())) && pc.hasSkillEffect(AI_2)) {
                        pc.removeAICheck(20000, pc.getAICheck());
                        pc.sendPackets(new S_ServerMessage("\\fU恭喜你答對了。"));
                        pc.setInputError(0);// 錯誤次數規0
                        pc.killSkillEffectTimer(AI_2);
                        pc.setSkillEffect(AI_1, (L1Config._2227 + ThreadLocalRandom.current().nextInt(11)) * 60 * 1000);
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
                        long time = 60L * 60 * 1000 * 24 * 30;// 七日
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
                    /* [原碼] 大樂透系統 */
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
                                    if (!(pc.getInventory().checkItem(L1Config._2167, (long) L1Config._2163 * count))) {
                                        pc.sendPackets(new S_SystemMessage("幣值不足，無法購買。"));
                                        return;
                                    }
                                    if ((pc.getInventory().getSize() >= 175) || (pc.getInventory().getSize() + count >= 175)) {
                                        pc.sendPackets(new S_SystemMessage("身上道具欄位不足，無法購買。"));
                                        return;
                                    }
                                    pc.getInventory().consumeItem(L1Config._2167, (long) L1Config._2163 * count);
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
                                this.BigHotAN1 = chatText + ",";
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
                                this.BigHotAN2 = chatText + ",";
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
                                this.BigHotAN3 = chatText + ",";
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
                                this.BigHotAN4 = chatText + ",";
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
                                this.BigHotAN5 = chatText + ",";
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
                                this.BigHotAN6 = chatText + ",";
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
                                    if (!(pc.getInventory().checkItem(L1Config._2167, (long) L1Config._2163 * count6))) {
                                        pc.sendPackets(new S_SystemMessage("幣值不足，無法購買。"));
                                        return;
                                    }
                                    if ((pc.getInventory().getSize() >= 175) || (pc.getInventory().getSize() + count6 >= 175)) {
                                        pc.sendPackets(new S_SystemMessage("身上道具欄位不足，無法購買。"));
                                        return;
                                    }
                                    pc.getInventory().consumeItem(L1Config._2167, (long) L1Config._2163 * count6);
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
                                        if (("," + BigHotId1).contains("," + pk[a] + ",")) {
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
                case 1:
                    /*
                     * if (pc.hasSkillEffect(4002)) { pc.sendPackets(new S_ServerMessage(242));
                     * return; }
                     *
                     * // 等級 %0 以下無法使用密談。 if ((pc.getLevel() < ConfigAlt.WHISPER_CHAT_LEVEL) &&
                     * !pc.isGm()) { pc.sendPackets(new S_ServerMessage(404,
                     * String.valueOf(ConfigAlt.WHISPER_CHAT_LEVEL))); return; }
                     *
                     * String targetName = readS(); String text = readS();
                     *
                     * if (text.length() > 52) { _log.warn("人物:" + pc.getName() + "對話長度超過限制:" +
                     * client.getIp().toString()); client.set_error(client.get_error() + 1); return;
                     * }
                     *
                     * L1PcInstance whisperTo = World.get().getPlayer(targetName); L1DeInstance de =
                     * null;
                     *
                     * if (whisperTo == null) { de = getDe(targetName); }
                     *
                     * if (de != null) { pc.sendPackets(new S_ChatWhisperTo(de, text)); return; }
                     *
                     * if (whisperTo == null) { pc.sendPackets(new S_ServerMessage(73, targetName));
                     * return; }
                     *
                     * if (whisperTo.equals(pc)) { return; }
                     *
                     * //if (whisperTo.getExcludingList().contains(pc.getName())) {
                     * //pc.sendPackets(new S_ServerMessage(117, whisperTo.getName())); //return;
                     * //} // 黑名單 if (whisperTo != null) { L1ExcludingList spamList1 =
                     * SpamTable.getInstance().getExcludeTable(whisperTo.getId()); if
                     * (spamList1.contains(0, pc.getName())) { pc.sendPackets(new
                     * S_ServerMessage(117, whisperTo.getName())); return; } }
                     *
                     * if (!whisperTo.isCanWhisper()) { pc.sendPackets(new S_ServerMessage(205,
                     * whisperTo.getName())); return; }
                     *
                     * if (ConfigAlt.GM_OVERHEARD) { for (L1Object visible :
                     * World.get().getAllPlayers()) { if ((visible instanceof L1PcInstance)) {
                     * L1PcInstance GM = (L1PcInstance) visible; if ((GM.isGm()) && (pc.getId() !=
                     * GM.getId())) { GM.sendPackets(new S_SystemMessage( "\\fV【密】" + pc.getName() +
                     * " -> " + targetName + ":" + text)); } } }
                     *
                     * }
                     *
                     * pc.sendPackets(new S_ChatWhisperTo(whisperTo, text));
                     * whisperTo.sendPackets(new S_ChatWhisperFrom(pc, text));
                     *
                     * if (ConfigRecord.LOGGING_CHAT_WHISPER) { LogChatReading.get().isTarget(pc,
                     * whisperTo, text, 9); }
                     *
                     * if (Config.GUI) { J_Main.getInstance().addPrivateChat(pc.getName(),
                     * whisperTo.getName(), text); } break;
                     */
                    // TODO 非法字串長度
                    int result_type = 0;// 使用成功
                    // 等級不夠
                    if (pc.getLevel() < ConfigAlt.WHISPER_CHAT_LEVEL) {
                        // 等級 7 以下無法使用密談。
                        result_type = 24;
                        // 輸出使用聊天頻道的結果
                        pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));
                        return;
                    }
                    // 對話間隔檢查
                    if (!pc.isGm()) {
                        // 禁言成立
                    }
                    final L1PcInstance whisperTo = World.get().getPlayer(tellTargetName);
                    boolean isNpc = false;// 判斷是否為假人npc
                    // 如果密語對像為NULL就是沒對象
                    if (whisperTo == null) {
                        L1DeInstance de = getDe(tellTargetName);
                        if (de != null) {
                            isNpc = true;
                        }
                        if (!isNpc) {
                            // 密語對像不存在
                            result_type = 5;
                        }
                        // 自己跟自己說話
                    } else if (whisperTo.equals(pc)) {
                        result_type = 57;
                        // 斷絕密語
                        // } else if (whisperTo.getExcludingList().contains(pc.getName())) {
                    } else if (SpamTable.getInstance().getExcludeTable(whisperTo.getId()).contains(0, pc.getName())) { // 黑名單
                        result_type = 23;
                        // 關閉密語
                    } else if (!whisperTo.isCanWhisper()) {
                        result_type = 56;
                    }
                    // 輸出使用聊天頻道的結果
                    pc.sendPackets(new S_ChatResult(chatIndex, chatType, chatText, tellTargetName, severId, result_type));
                    // 使用未成功
                    if (result_type != 0) {
                        return;
                    }
                    // 建立輸出字串訊息
                    if (!isNpc) {
                        whisperTo.sendPackets(new S_ChatText((int) (System.currentTimeMillis() / 1000), chatType, chatText, pc.getName(), severId));
                    }
                    if (Config.GUI) {
                        if (whisperTo != null) {
                            J_Main.getInstance().addPrivateChat(pc.getName(), whisperTo.getName(), chatText);
                        }
                    }
                    if (ConfigRecord.LOGGING_CHAT_WHISPER) {
                        LogChatReading.get().isTarget(pc, whisperTo, chatText, 9);
                    }
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
                case 0x03: // 廣播頻道
                    // boolean isStop = false;// 停止輸出
                    // boolean errMessage = false;// 異常訊息
                    // 中毒狀態
                    if (pc.hasSkillEffect(SILENCE)) {
                        if (!pc.isGm()) {
                            isStop = true;
                        }
                    }
                    // 中毒狀態
                    if (pc.hasSkillEffect(AREA_OF_SILENCE)) {
                        if (!pc.isGm()) {
                            isStop = true;
                        }
                    }
                    // 中毒狀態
                    if (pc.hasSkillEffect(STATUS_POISON_SILENCE)) {
                        if (!pc.isGm()) {
                            isStop = true;
                        }
                    }
                    // 你從現在被禁止閒談。
                    if (pc.hasSkillEffect(STATUS_CHAT_PROHIBITED)) {
                        isStop = true;
                        errMessage = true;
                    }
                    if (isStop) {
                        if (errMessage) {
                            pc.sendPackets(new S_ServerMessage(242));
                        }
                        return;
                    }
                    if (!pc.isGm()) {
                        // 等級 %0 以下的角色無法使用公頻或買賣頻道。
                        if (pc.getLevel() < ConfigAlt.GLOBAL_CHAT_LEVEL) {
                            pc.sendPackets(new S_ServerMessage(195, String.valueOf(ConfigAlt.GLOBAL_CHAT_LEVEL)));
                            return;
                        }
                        // 管理者有非常重要的事項公告，請見諒。
                        if (!World.get().isWorldChatElabled()) {
                            pc.sendPackets(new S_ServerMessage(510));
                            return;
                        }
                    }
                    if (ConfigOther.SET_GLOBAL_TIME > 0 && !pc.isGm()) {
                        final Calendar cal = Calendar.getInstance();
                        final long time = cal.getTimeInMillis() / 1000;// 換算為秒
                        if (time - pc.get_global_time() < ConfigOther.SET_GLOBAL_TIME) {
                            return;
                        }
                        pc.set_global_time(time);
                    }
                    chatType_3(pc, chatText);
                    if (Config.GUI) {
                        J_Main.getInstance().addWorldChat(pc.getName(), chatText);
                    }
                    break;
                case 4:
                    if (Config.GUI) {
                        J_Main.getInstance().addClanChat(pc.getName(), chatText);
                    }
                    if (ConfigRecord.GM_OVERHEARD4) {
                        for (L1PcInstance visible : World.get().getAllPlayers()) {
                            if ((visible != null)) {
                                if ((visible.isGm()) && (pc.getId() != visible.getId())) {
                                    visible.sendPackets(new S_SystemMessage("\\aL" + "【血盟】" + pc.getName() + ":" + chatText));
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
                        for (L1PcInstance visible : World.get().getAllPlayers()) {
                            if ((visible != null)) {
                                if ((visible.isGm()) && (pc.getId() != visible.getId())) {
                                    visible.sendPackets(new S_SystemMessage("\\aG" + "【隊伍】" + pc.getName() + ":" + chatText));
                                }
                            }
                        }
                    }
                    chatType_11(pc, chatText);
                    break;
                case 0x0c: // 交易頻道
                    // 中毒狀態
                    if (pc.hasSkillEffect(SILENCE)) {
                        if (!pc.isGm()) {
                            isStop = true;
                        }
                    }
                    // 中毒狀態
                    if (pc.hasSkillEffect(AREA_OF_SILENCE)) {
                        if (!pc.isGm()) {
                            isStop = true;
                        }
                    }
                    // 中毒狀態
                    if (pc.hasSkillEffect(STATUS_POISON_SILENCE)) {
                        if (!pc.isGm()) {
                            isStop = true;
                        }
                    }
                    // 你從現在被禁止閒談。
                    if (pc.hasSkillEffect(STATUS_CHAT_PROHIBITED)) {
                        isStop = true;
                        errMessage = true;
                    }
                    if (isStop) {
                        if (errMessage) {
                            pc.sendPackets(new S_ServerMessage(242));
                        }
                        return;
                    }
                    if (!pc.isGm()) {
                        // 等級 %0 以下的角色無法使用公頻或買賣頻道。
                        if (pc.getLevel() < ConfigAlt.GLOBAL_CHAT_LEVEL) {
                            pc.sendPackets(new S_ServerMessage(195, String.valueOf(ConfigAlt.GLOBAL_CHAT_LEVEL)));
                            return;
                        }
                        // 管理者有非常重要的事項公告，請見諒。
                        if (!World.get().isWorldChatElabled()) {
                            pc.sendPackets(new S_ServerMessage(510));
                            return;
                        }
                    }
                    if (ConfigOther.SET_GLOBAL_TIME > 0 && !pc.isGm()) {
                        final Calendar cal = Calendar.getInstance();
                        final long time = cal.getTimeInMillis() / 1000;// 換算為秒
                        if (time - pc.get_global_time() < ConfigOther.SET_GLOBAL_TIME) {
                            return;
                        }
                        pc.set_global_time(time);
                    }
                    chatType_12(pc, chatText);
                    if (Config.GUI) {
                        J_Main.getInstance().addWorldChat(pc.getName(), chatText);
                    }
                    break;
                case 13:
                    if (Config.GUI) {
                        J_Main.getInstance().addClanChat(pc.getName(), chatText);
                    }
                    if (ConfigRecord.GM_OVERHEARD13) {
                        for (L1PcInstance visible : World.get().getAllPlayers()) {
                            if ((visible != null)) {
                                if ((visible.isGm()) && (pc.getId() != visible.getId())) {
                                    visible.sendPackets(new S_SystemMessage("\\aJ" + "【聯盟】" + pc.getName() + ":" + chatText));
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
                        for (L1PcInstance visible : World.get().getAllPlayers()) {
                            if ((visible != null)) {
                                if ((visible.isGm()) && (pc.getId() != visible.getId())) {
                                    visible.sendPackets(new S_SystemMessage("\\aA" + "【聊天】" + pc.getName() + ":" + chatText));
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
                            Integer[] allianceids = clan.Alliance();
                            if (allianceids.length > 0) {
                                String TargetClanName;
                                L1Clan TargegClan;
                                S_ChatAlliance s_chatpacket = new S_ChatAlliance(pc, chatText);
                                for (L1PcInstance listner : clan.getOnlineClanMember()) {
                                    int AllianceClan = listner.getClanid();
                                    if (pc.getClanid() == AllianceClan) {
                                        listner.sendPackets(s_chatpacket);
                                    }
                                }
                                for (Integer allianceid : allianceids) {
                                    TargegClan = clan.getAlliance(allianceid);
                                    if (TargegClan != null) {
                                        TargetClanName = TargegClan.getClanName();
                                        if (TargetClanName != null) {
                                            for (L1PcInstance alliancelistner : TargegClan.getOnlineClanMember()) {
                                                alliancelistner.sendPackets(s_chatpacket);
                                            }
                                        }
                                    }
                                }
                                for (L1PcInstance visible : World.get().getAllPlayers()) {
                                    if ((visible != null)) {
                                        if ((visible.isGm()) && (pc.getId() != visible.getId())) {
                                            visible.sendPackets(new S_SystemMessage("\\aJ" + "【同盟】" + pc.getName() + ":" + chatText));
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
                            // 黑名單
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
        } catch (Exception e2) {
            _log.error(e2.getLocalizedMessage(), e2);
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
            if (newchatText.isEmpty()) {
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
     * 交易頻道($)
     *
     */
    private void chatType_12(L1PcInstance pc, String chatText) {
        S_ChatTransaction packet = new S_ChatTransaction(pc, chatText);
        String name = pc.getName();
        for (final L1PcInstance listner : World.get().getAllPlayers()) {
            // 拒絕接收該人物訊息
            /*
             * if (listner.getExcludingList().contains(name)) { continue; }
             */
            // 黑名單
            L1ExcludingList spamList12 = SpamTable.getInstance().getExcludeTable(listner.getId());
            if (spamList12.contains(0, name)) {
                continue;
            }
            // 拒絕接收廣播頻道
            if (!listner.isShowTradeChat()) {
                continue;
            }
            listner.sendPackets(packet);
        }
        if (ConfigRecord.LOGGING_CHAT_BUSINESS) {
            LogChatReading.get().noTarget(pc, chatText, 12);
        }
    }

    /**
     * 廣播頻道(&)
     *
     */
    private void chatType_3(L1PcInstance pc, String chatText) {
        // 排行榜前綴取得
        int rank = com.lineage.server.datatables.T_RankTable.get().getRankOfPlayer(pc.getName());
        com.lineage.system.RankingAbilityBonus.AbilityBonus bonus = com.lineage.system.RankingAbilityBonus.getBonusByRank(rank);
        String prefix = bonus.titlePrefix != null ? bonus.titlePrefix : "";

        S_ChatGlobal packet = new S_ChatGlobal(prefix + pc.getName(), chatText);

        if (pc.isGm()) {
            World.get().broadcastPacketToAll(packet);
            return;
        }
        String name = pc.getName();
        if (!pc.isGm()) {
            // 廣播扣除金幣或是飽食度(0:飽食度 其他:指定道具編號)
            // 廣播扣除質(set_global設置0:扣除飽食度量 set_global設置其他:扣除指定道具數量)
            // 指定道具
            if (ConfigOther.SET_GLOBAL == 0) { // 飽食度
                if (pc.get_food() >= 6) {
                    pc.set_food(pc.get_food() - ConfigOther.SET_GLOBAL_COUNT);
                    pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
                } else {
                    // 你太過於飢餓以致於無法談話。
                    pc.sendPackets(new S_ServerMessage(462));
                    return;
                }
            } else {
                final L1ItemInstance item = pc.getInventory().checkItemX(ConfigOther.SET_GLOBAL, ConfigOther.SET_GLOBAL_COUNT);
                if (item != null) {
                    pc.getInventory().removeItem(item, ConfigOther.SET_GLOBAL_COUNT);// 刪除指定道具
                } else {
                    // 找回物品
                    final L1Item itemtmp = ItemTable.get().getTemplate(ConfigOther.SET_GLOBAL);
                    pc.sendPackets(new S_ServerMessage(337, itemtmp.getNameId()));
                    return;
                }
            }
        }
        for (final L1PcInstance listner : World.get().getAllPlayers()) {
            // 黑名單
            L1ExcludingList spamList3 = SpamTable.getInstance().getExcludeTable(listner.getId());
            if (spamList3.contains(0, name)) {
                continue;
            }
            // 拒絕接收廣播頻道
            if (!listner.isShowWorldChat()) {
                continue;
            }
            listner.sendPackets(packet);
        }
        if (ConfigRecord.LOGGING_CHAT_WORLD) {
            LogChatReading.get().noTarget(pc, chatText, 3);
        }
    }


    /**
     * 聊天頻道
     *
     */
    private void chatType_14(L1PcInstance pc, String chatText) {
        if (pc.isInChatParty()) {
            int rank = com.lineage.server.datatables.T_RankTable.get().getRankOfPlayer(pc.getName());
            com.lineage.system.RankingAbilityBonus.AbilityBonus bonus = com.lineage.system.RankingAbilityBonus.getBonusByRank(rank);
            String prefix = bonus.titlePrefix != null ? bonus.titlePrefix : "";

            String displayName = prefix + pc.getName();
            S_ChatParty2 chatpacket = new S_ChatParty2(displayName, chatText);

            L1PcInstance[] partyMembers = pc.getChatParty().getMembers();
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
                    int rank = com.lineage.server.datatables.T_RankTable.get().getRankOfPlayer(pc.getName());
                    com.lineage.system.RankingAbilityBonus.AbilityBonus bonus = com.lineage.system.RankingAbilityBonus.getBonusByRank(rank);
                    String prefix = bonus.titlePrefix != null ? bonus.titlePrefix : "";

                    String displayName = prefix + pc.getName();
                    S_ChatClanUnion chatpacket = new S_ChatClanUnion(displayName, chatText);

                    L1PcInstance[] clanMembers = clan.getOnlineClanMember();
                    for (L1PcInstance listner : clanMembers) {
                        L1ExcludingList spamList13 = SpamTable.getInstance().getExcludeTable(listner.getId());
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
    private void chatType_11(L1PcInstance pc, String chatText) {
        if (pc.isInParty()) {
            int rank = com.lineage.server.datatables.T_RankTable.get().getRankOfPlayer(pc.getName());
            com.lineage.system.RankingAbilityBonus.AbilityBonus bonus = com.lineage.system.RankingAbilityBonus.getBonusByRank(rank);
            String prefix = bonus.titlePrefix != null ? bonus.titlePrefix : "";

            String displayName = prefix + pc.getName();
            S_ChatParty chatpacket = new S_ChatParty(displayName, chatText);

            List<L1PcInstance> pcs = pc.getParty().getMemberList();
            for (L1PcInstance listner : pcs) {
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
                // 取得排行榜稱號前綴
                int rank = com.lineage.server.datatables.T_RankTable.get().getRankOfPlayer(pc.getName());
                com.lineage.system.RankingAbilityBonus.AbilityBonus bonus = com.lineage.system.RankingAbilityBonus.getBonusByRank(rank);
                String prefix = bonus.titlePrefix != null ? bonus.titlePrefix : "";

                String displayName = prefix + pc.getName();
                S_ChatClan chatpacket = new S_ChatClan(displayName, chatText);

                L1PcInstance[] clanMembers = clan.getOnlineClanMember();
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
// 排行榜前綴取得
        int rank = com.lineage.server.datatables.T_RankTable.get().getRankOfPlayer(pc.getName());
        com.lineage.system.RankingAbilityBonus.AbilityBonus bonus = com.lineage.system.RankingAbilityBonus.getBonusByRank(rank);
        String prefix = bonus.titlePrefix != null ? bonus.titlePrefix : "";
        pc.setViewName(prefix + pc.getName());  // ✅ 設定顯示名稱為：★王者阿龍
        S_ChatShouting chatpacket = new S_ChatShouting(pc, chatText); // ✅ 使用角色物件建立封包
        pc.sendPackets(chatpacket);
// 黑名單過濾
        for (L1PcInstance listner : World.get().getVisiblePlayer(pc, 50)) {
            if (!SpamTable.getInstance().getExcludeTable(listner.getId()).contains(0, pc.getName())) {
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
     * 目前使用中的頻道 2025.03.30 by 老爹
     *
     */
    private void chatType_0(L1PcInstance pc, String chatText) {
        if ((pc.isGhost()) && (!pc.isGm()) && (!pc.isMonitor())) {
            return;
        }

        if (pc.getAccessLevel() > 0 && chatText.startsWith(".")) {
            String cmd = chatText.substring(1);
            GMCommands.getInstance().handleCommands(pc, cmd);
            return;
        }

        // 兌換代碼系統
        Voucher voucher = new Voucher();
        if (voucher.Check(pc, chatText)) {
            return;
        }

        // ✅ 取得排行榜前綴
        int rank = com.lineage.server.datatables.T_RankTable.get().getRankOfPlayer(pc.getName());
        com.lineage.system.RankingAbilityBonus.AbilityBonus bonus = com.lineage.system.RankingAbilityBonus.getBonusByRank(rank);
        String prefix = bonus.titlePrefix != null ? bonus.titlePrefix : "";

        // ✅ 將聊天框顯示名稱加上排行榜前綴（但不影響泡泡）
        pc.setViewName(prefix + pc.getName());

        // ✅ 使用角色物件產生封包 → 頭上泡泡正常，聊天框顯示 displayText
        S_Chat chatpacket = new S_Chat(pc, chatText);
        pc.sendPackets(chatpacket);

        // 黑名單過濾 + 傳送給附近可見玩家
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


    /**
     * 變形怪重複對話(一般頻道)
     *
     */
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

    /**
     * 變形怪重複對話(大喊頻道)
     *
     */
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
            if (chatText.isEmpty()) {
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
            if (!this.BigHotAN.contains(sk + ",")) {
                C_ItemCraft1 tmp51_50 = this;
                tmp51_50.BigHotAN = tmp51_50.BigHotAN + sk + ",";
            }
        }
    }

    // 成長果實系統(Tam幣)
    public int Nexttam(String encobj) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs;
        int day = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT day FROM `tam` WHERE encobjid = ? order by id asc limit 1"); // キャラクターテーブルで君主だけを選んで来て
            pstm.setString(1, encobj);
            rs = pstm.executeQuery();
            while (rs.next()) {
                day = rs.getInt("Day");
            }
        } catch (SQLException e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return day;
    }

    public int TamCharid(String encobj) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs;
        int objid = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT objid FROM `tam` WHERE encobjid = ? order by id asc limit 1"); // キャラクターテーブルで君主だけを選んで来て
            pstm.setString(1, encobj);
            rs = pstm.executeQuery();
            while (rs.next()) {
                objid = rs.getInt("objid");
            }
        } catch (SQLException e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return objid;
    }

    /**
     * 成長果實取消
     */
    public void tamcancle(String objectId) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("delete from tam where encobjid = ? order by id asc limit 1");
            pstm.setString(1, objectId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
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

    // 成長果實系統(Tam幣)end
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

    @Override
    public String getType() {
        return C_ITEMCRAFT1;
    }

    private static class AlchemyBroadcast implements Runnable {
        private final Log _log = LogFactory.getLog(AlchemyBroadcast.class);
        private final S_SystemMessage _packet;

        public AlchemyBroadcast(final S_SystemMessage packet) {
            this._packet = packet;
        }

        @Override
        public void run() {
            try {
                World.get().broadcastPacketToAll(this._packet);
            } catch (final Exception e) {
                this._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
