package com.add.Tsai;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 收藏指令
 *
 * @author hero
 */
public class collectBookCmd {
    private static final Log _log = LogFactory.getLog(collectBookCmd.class);
    private static collectBookCmd _instance;

    public static collectBookCmd get() {
        if (_instance == null) {
            _instance = new collectBookCmd();
        }
        return _instance;
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            int size = collectTable.get().CollectSize();
            for (int i = 0; i <= size; i++) {
                final collect collect = collectTable.get().getCollect(i);
                //獲取列表id
                if (collect != null) {//列表ID空白不啟動
                    if (pc.getQuest().get_step(collect.getQuestId()) != 0) {
                        stringBuilder.append(String.valueOf(collect.getAddcgfxid())).append(",");
                    } else {
                        stringBuilder.append(String.valueOf(collect.getAddhgfxid())).append(",");
                    }
                }
            }
            final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
            boolean ok = false;
            switch (cmd) {
                case "S_1":
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    pc.sendPackets(new S_NPCTalkReturn(pc, "collect_02", msg));
                    break;
                case "S_2":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "collect_03", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "S_3":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "collect_04", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "S_4":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "collect_05", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "S_5":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "collect_06", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "S_6":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "collect_07", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "S_7":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "collect_08", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "polycard":
                    ACard card = ACardTable.get().getCard(pc.getCardId());
                    if (card != null) {
                        if (pc.getQuest().get_step(card.getQuestId()) != 0) {
                            if (card.getPolyId() != 0) {
                                if (card.getPolyItemId() != 0) {
                                    if (pc.getInventory().checkItem(card.getPolyItemId(), card.getPolyItemCount())) {
                                        L1PolyMorph.doPoly(pc, card.getPolyId(), card.getPolyTime(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
                                        pc.getInventory().consumeItem(card.getPolyItemId(), card.getPolyItemCount());
                                    } else {
                                        pc.sendPackets(new S_SystemMessage("變身需求道具不足"));
                                    }
                                } else {
                                    L1PolyMorph.doPoly(pc, card.getPolyId(), card.getPolyTime(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
                                }
                            } else {
                                pc.sendPackets(new S_SystemMessage("無法變身"));
                            }
                        }
                    }
                    ok = true;
                    break;
                case "collectset":
                    CollectSet(pc);
                    ok = true;
                    break;
                case "collectset2":
                    CollectAllSet(pc);
                    ok = true;
                    break;
                default:
                    collect collect = collectTable.get().getCollect(cmd);
                    if (collect != null && pc.getQuest().get_step(collect.getQuestId()) != 255) {
                        pc.set_collectTemp(collect);
                        pc.sendPackets(new S_SystemMessage("請選擇一個物品"));
                        ok = true;
                    } else {
                        pc.set_collectTemp(null);
                    }
                    break;
            }
            if (ok) {
                return true;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    private void CollectAllSet(final L1PcInstance pc) {
        try {
            int str = 0;
            int dex = 0;
            int con = 0;
            int Int = 0;
            int wis = 0;
            int cha = 0;
            int ac = 0;
            int hp = 0;
            int mp = 0;
            int hpr = 0;
            int mpr = 0;
            int dmg = 0;
            int bdmg = 0;
            int hit = 0;
            int bhit = 0;
            int dr = 0;
            int mdr = 0;
            int sp = 0;
            int mhit = 0;
            int mr = 0;
            int f = 0;
            int wind = 0;
            int w = 0;
            int e = 0;
            for (int i = 0; i <= collectTable.get().CollectSize(); i++) {
                final collect collect = collectTable.get().getCollect(i);
                if (collect != null) {
                    if (pc.getQuest().get_step(collect.getQuestId()) != 0) {
                        str += collect.getAddStr();
                        dex += collect.getAddDex();
                        con += collect.getAddCon();
                        Int += collect.getAddInt();
                        wis += collect.getAddWis();
                        cha += collect.getAddCha();
                        ac += collect.getAddAc();
                        hp += collect.getAddHp();
                        mp += collect.getAddMp();
                        hpr += collect.getAddHpr();
                        mpr += collect.getAddMpr();
                        dmg += collect.getAddDmg();
                        bdmg += collect.getAddBowDmg();
                        hit += collect.getAddHit();
                        bhit += collect.getAddBowHit();
                        dr += collect.getAddDmgR();
                        mdr += collect.getAddMagicDmgR();
                        sp += collect.getAddSp();
                        mhit += collect.getAddMagicHit();
                        mr += collect.getAddMr();
                        f += collect.getAddFire();
                        wind += collect.getAddWind();
                        e += collect.getAddEarth();
                        w += collect.getAddWater();
                    }
                }
            }
            for (int i = 0; i <= collectTable.get().CollectSize(); i++) {//檢查收藏組合DB資料
                final collectPolySet collects = collectSetTable.get().getCard(i);
                if (collects != null) {
                    if (pc.getQuest().get_step(collects.getQuestId()) != 0) {
                        str += collects.getAddStr();
                        dex += collects.getAddDex();
                        con += collects.getAddCon();
                        Int += collects.getAddInt();
                        wis += collects.getAddWis();
                        cha += collects.getAddCha();
                        ac += collects.getAddAc();
                        hp += collects.getAddHp();
                        mp += collects.getAddMp();
                        hpr += collects.getAddHpr();
                        mpr += collects.getAddMpr();
                        dmg += collects.getAddDmg();
                        bdmg += collects.getAddBowDmg();
                        hit += collects.getAddHit();
                        bhit += collects.getAddBowHit();
                        dr += collects.getAddDmgR();
                        mdr += collects.getAddMagicDmgR();
                        sp += collects.getAddSp();
                        mhit += collects.getAddMagicHit();
                        mr += collects.getAddMr();
                        f += collects.getAddFire();
                        wind += collects.getAddWind();
                        e += collects.getAddEarth();
                        w += collects.getAddWater();
                    }
                }
            }
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("力量 +").append(str).append(",");
            stringBuilder.append("敏捷 +").append(dex).append(",");
            stringBuilder.append("體質 +").append(con).append(",");
            stringBuilder.append("智力 +").append(Int).append(",");
            stringBuilder.append("精神 +").append(wis).append(",");
            stringBuilder.append("魅力 +").append(cha).append(",");
            stringBuilder.append("防禦提升 +").append(ac).append(",");
            stringBuilder.append("HP +").append(hp).append(",");
            stringBuilder.append("MP +").append(mp).append(",");
            stringBuilder.append("血量回復 +").append(hpr).append(",");
            stringBuilder.append("魔力回復 +").append(mpr).append(",");
            stringBuilder.append("近距離傷害 +").append(dmg).append(",");
            stringBuilder.append("遠距離傷害 +").append(bdmg).append(",");
            stringBuilder.append("近距離命中 +").append(hit).append(",");
            stringBuilder.append("遠距離命中 +").append(bhit).append(",");
            stringBuilder.append("物理傷害減免 +").append(dr).append(",");
            stringBuilder.append("魔法傷害減免 +").append(mdr).append(",");
            stringBuilder.append("魔攻 +").append(sp).append(",");
            stringBuilder.append("魔法命中 +").append(mhit).append(",");
            stringBuilder.append("魔法防禦 +").append(mr).append(",");
            stringBuilder.append("火屬性防禦 +").append(f).append(",");
            stringBuilder.append("風屬性防禦 +").append(wind).append(",");
            stringBuilder.append("地屬性防禦 +").append(e).append(",");
            stringBuilder.append("水屬性防禦 +").append(w).append(",");
            final String[] clientStrAry = stringBuilder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "collect_11", clientStrAry));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void CollectSet(final L1PcInstance pc) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i <= collectSetTable.get().getCollectSize(); i++) {//檢查收藏組合DB資料
                final collectPolySet collects = collectSetTable.get().getCard(i);
                if (collects != null) {
                    boolean IsIndex = true;
                    if (IsIndex) {
                        stringBuilder.append(collects.getMsg1()).append(",");
                        int k = 0;
                        for (int j = 0; j < collects.getNeedQuest().length; j++) {
                            if (pc.getQuest().get_step(collects.getNeedQuest()[j]) != 0) {
                                stringBuilder.append(collects.getNeedName()[j]).append("(開啟),");
                                k++;
                            } else {
                                stringBuilder.append(collects.getNeedName()[j]).append("(未開啟),");
                            }
                        }
                        if (k == collects.getNeedQuest().length) {
                            if (collects.getAddStr() != 0) {
                                stringBuilder.append("力量 +").append(collects.getAddStr()).append(",");
                            }
                            if (collects.getAddDex() != 0) {
                                stringBuilder.append("敏捷 +").append(collects.getAddDex()).append(",");
                            }
                            if (collects.getAddCon() != 0) {
                                stringBuilder.append("體質 +").append(collects.getAddCon()).append(",");
                            }
                            if (collects.getAddInt() != 0) {
                                stringBuilder.append("智力 +").append(collects.getAddInt()).append(",");
                            }
                            if (collects.getAddWis() != 0) {
                                stringBuilder.append("精神 +").append(collects.getAddWis()).append(",");
                            }
                            if (collects.getAddCha() != 0) {
                                stringBuilder.append("魅力 +").append(collects.getAddCha()).append(",");
                            }
                            if (collects.getAddAc() != 0) {
                                stringBuilder.append("防禦提升 +").append(collects.getAddAc()).append(",");
                            }
                            if (collects.getAddHp() != 0) {
                                stringBuilder.append("HP +").append(collects.getAddHp()).append(",");
                            }
                            if (collects.getAddMp() != 0) {
                                stringBuilder.append("MP +").append(collects.getAddMp()).append(",");
                            }
                            if (collects.getAddHpr() != 0) {
                                stringBuilder.append("血量回復 +").append(collects.getAddHpr()).append(",");
                            }
                            if (collects.getAddMpr() != 0) {
                                stringBuilder.append("魔力回復 +").append(collects.getAddMpr()).append(",");
                            }
                            if (collects.getAddDmg() != 0) {
                                stringBuilder.append("近距離傷害 +").append(collects.getAddDmg()).append(",");
                            }
                            if (collects.getAddBowDmg() != 0) {
                                stringBuilder.append("遠距離傷害 +").append(collects.getAddBowDmg()).append(",");
                            }
                            if (collects.getAddHit() != 0) {
                                stringBuilder.append("近距離命中 +").append(collects.getAddHit()).append(",");
                            }
                            if (collects.getAddBowHit() != 0) {
                                stringBuilder.append("遠距離命中 +").append(collects.getAddBowHit()).append(",");
                            }
                            if (collects.getAddDmgR() != 0) {
                                stringBuilder.append("物理傷害減免 +").append(collects.getAddDmgR()).append(",");
                            }
                            if (collects.getAddMagicDmgR() != 0) {
                                stringBuilder.append("魔法傷害減免 +").append(collects.getAddMagicDmgR()).append(",");
                            }
                            if (collects.getAddSp() != 0) {
                                stringBuilder.append("魔攻 +").append(collects.getAddSp()).append(",");
                            }
                            if (collects.getAddMagicHit() != 0) {
                                stringBuilder.append("魔法命中 +").append(collects.getAddMagicHit()).append(",");
                            }
                            if (collects.getAddMr() != 0) {
                                stringBuilder.append("魔法防禦 +").append(collects.getAddMr()).append(",");
                            }
                            if (collects.getAddFire() != 0) {
                                stringBuilder.append("火屬性防禦 +").append(collects.getAddFire()).append(",");
                            }
                            if (collects.getAddWind() != 0) {
                                stringBuilder.append("風屬性防禦 +").append(collects.getAddWind()).append(",");
                            }
                            if (collects.getAddEarth() != 0) {
                                stringBuilder.append("地屬性防禦 +").append(collects.getAddEarth()).append(",");
                            }
                            if (collects.getAddWater() != 0) {
                                stringBuilder.append("水屬性防禦 +").append(collects.getAddWater()).append(",");
                            }
                            stringBuilder.append("<以上為此套卡能力加成>,");
                        }
                    }
                }
            }
            final String[] clientStrAry = stringBuilder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "collect_10", clientStrAry));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean PolyCmd(final L1PcInstance pc, final String cmd) {
        try {
            boolean ok = false;
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i <= collectTable.get().CollectSize(); i++) {
                final collect collect = collectTable.get().getCollect(i);
                if (collect != null) {
                    if (cmd.equals(collect.getCmd())) {
                        pc.setCarId(i);
                        stringBuilder.append(collect.getMsg2()).append(",");
                        stringBuilder.append(collect.getAddStr()).append(",");
                        stringBuilder.append(collect.getAddDex()).append(",");
                        stringBuilder.append(collect.getAddCon()).append(",");
                        stringBuilder.append(collect.getAddInt()).append(",");
                        stringBuilder.append(collect.getAddWis()).append(",");
                        stringBuilder.append(collect.getAddCha()).append(",");
                        stringBuilder.append(collect.getAddAc()).append(",");
                        stringBuilder.append(collect.getAddHp()).append(",");
                        stringBuilder.append(collect.getAddMp()).append(",");
                        stringBuilder.append(collect.getAddHpr()).append(",");
                        stringBuilder.append(collect.getAddMpr()).append(",");
                        stringBuilder.append(collect.getAddDmg()).append(",");
                        stringBuilder.append(collect.getAddBowDmg()).append(",");
                        stringBuilder.append(collect.getAddHit()).append(",");
                        stringBuilder.append(collect.getAddBowHit()).append(",");
                        stringBuilder.append(collect.getAddDmgR()).append(",");
                        stringBuilder.append(collect.getAddMagicDmgR()).append(",");
                        stringBuilder.append(collect.getAddSp()).append(",");
                        stringBuilder.append(collect.getAddMagicHit()).append(",");
                        stringBuilder.append(collect.getAddMr()).append(",");
                        stringBuilder.append(collect.getAddFire()).append(",");
                        stringBuilder.append(collect.getAddWind()).append(",");
                        stringBuilder.append(collect.getAddEarth()).append(",");
                        stringBuilder.append(collect.getAddWater()).append(",");
                        if (pc.getQuest().get_step(collect.getQuestId()) != 0) {
                            stringBuilder.append("開啟,");
                        } else {
                            stringBuilder.append("未開啟,");
                        }
                        stringBuilder.append(collect.getShanbi()).append(",");
                        stringBuilder.append(collect.getHuibi()).append(",");
                        stringBuilder.append(collect.getYaoshui()).append(",");
                        stringBuilder.append(collect.getFuzhong()).append(",");
                        stringBuilder.append(collect.getExp()).append(",");
                        stringBuilder.append(collect.getHunmi()).append(",");
                        stringBuilder.append(collect.getZhicheng()).append(",");
                        stringBuilder.append(collect.getShihua()).append(",");
                        stringBuilder.append(collect.getHanbing()).append(",");
                        stringBuilder.append(collect.getAnhei()).append(",");
                        stringBuilder.append(collect.getShuimian()).append(",");
                        final String[] clientStrAry = stringBuilder.toString().split(",");
                        pc.sendPackets(new S_NPCTalkReturn(pc, "collect_0", clientStrAry));
                        ok = true;
                        break;
                    }
                }
            }
            if (ok) {
                return true;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }
}