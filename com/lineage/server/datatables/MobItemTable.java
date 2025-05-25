package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_NpcChatPacket;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 特定怪物死亡掉落道具或給予狀態系統
 */
public class MobItemTable {
    private static final Log _log = LogFactory.getLog(MobItemTable.class);
    private static final String TOKEN = ",";
    private static ArrayList<ArrayList<Object>> _array = new ArrayList<ArrayList<Object>>();
    private static MobItemTable _instance;

    private MobItemTable() {
        PerformanceTimer timer = new PerformanceTimer();
        getData();
        _log.info("讀取->[系統]_怪物指定掉落物系統數量: " + _array.size() + "(" + timer.get() + "ms)");
        if (_array.size() <= 0) {
            _array.clear();
            _array = null;
        }
    }

    public static MobItemTable get() {
        if (_instance == null) {
            _instance = new MobItemTable();
        }
        return _instance;
    }

    public static void forMobItem(L1PcInstance pc, L1MonsterInstance npc) {
        if (_array.size() <= 0) {
            return;
        }
        // 怪物是否為復活過的
        if (npc.isResurrect()) {
            return;
        }
        boolean isDropGround = false;
        if (npc.getNpcTemplate().getDropGround() != 0) {
            isDropGround = true;
        }
        ArrayList<?> aTempData = null;
        for (int i = 0; i < _array.size(); i++) {
            aTempData = _array.get(i);
            if (((Integer) aTempData.get(0)).intValue() == npc.getNpcTemplate().get_npcId()) {
                if (((Integer) aTempData.get(1)).intValue() != 0) { // 職業判斷
                    byte class_id = (byte) 0;
                    if (pc.isCrown()) { // 王族
                        class_id = 1;
                    } else if (pc.isKnight()) { // 騎士
                        class_id = 2;
                    } else if (pc.isWizard()) { // 法師
                        class_id = 3;
                    } else if (pc.isElf()) { // 妖精
                        class_id = 4;
                    } else if (pc.isDarkelf()) { // 黑妖
                        class_id = 5;
                    } else if (pc.isDragonKnight()) { // 龍騎士
                        class_id = 6;
                    } else if (pc.isIllusionist()) { // 幻術士
                        class_id = 7;
                    } else if (pc.isWarrior()) { // 狂戰士
                        class_id = 8;
                    }
                    if (((Integer) aTempData.get(1)).intValue() != class_id) { // 職業不符
                        return;
                    }
                }
                // 等級限制
                if (((Integer) aTempData.get(2)).intValue() != 0) {
                    if (pc.getLevel() < ((Integer) aTempData.get(2)).intValue()) {
                        return;
                    }
                }
                // 攜帶物品判斷
                if (((Integer) aTempData.get(3)).intValue() != 0) {
                    if (!pc.getInventory().checkItem(((Integer) aTempData.get(2)).intValue())) {
                        L1Item temp = ItemTable.get().getTemplate(((Integer) aTempData.get(2)).intValue());
                        pc.sendPackets(new S_SystemMessage("沒有 (" + temp.getName() + ") 是打不到任何好料的。"));
                        return;
                    }
                }
                // 陣營限制
                if (((Integer) aTempData.get(5)).intValue() != 0) {
                    if (pc.get_c_power().get_c1_type() != ((Integer) aTempData.get(5)).intValue()) {
                        return;
                    }
                }
                // 陣營積分限制
                if (((Integer) aTempData.get(6)).intValue() != 0) {
                    if (pc.get_other().get_score() <= ((Integer) aTempData.get(6)).intValue()) {
                        return;
                    }
                }
                if (RandomArrayList.getInc(100, 1) < 100 - ((Integer) aTempData.get(4)).intValue()) { // 成功機率
                    return;
                }
                // 給予技能
                if ((int[]) aTempData.get(10) != null) {
                    int[] Skills = (int[]) aTempData.get(10);
                    for (int j = 0; j < Skills.length; j++) {
                        L1SkillUse l1skilluse = new L1SkillUse();
                        l1skilluse.handleCommands(pc, Skills[j], pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
                    }
                }
                // 給予道具
                if ((int[]) aTempData.get(8) != null && (int[]) aTempData.get(9) != null) {
                    int[] giveMaterials = (int[]) aTempData.get(8);
                    int[] giveCounts = (int[]) aTempData.get(9);
                    int rndItem = RandomArrayList.getInt(giveMaterials.length);
                    int rndCount = RandomArrayList.getInt(giveCounts.length);
                    int giveItemGet = giveMaterials[rndItem];
                    int giveCountGet = giveCounts[rndCount];
                    // 限制道具掉落系統
					/*if (L1WilliamChackDrop.checkItemId(giveItemGet) == giveItemGet) {
						L1WilliamChackDrop ChackDrop1 = ChackDrop.getInstance().getTemplate(giveItemGet);
						if (ChackDrop1.getAgainCount() - ChackDrop1.getAgainCount1() == 0) {
							return;
						} else if (ChackDrop1.getTotalCount() == ChackDrop1.getAppearCount()) {
							return;
						} else if (!(RandomArrayList.getInc(100, 1) >= 101 - ChackDrop1.getRandom())) {
							return;
						}
						int appearCount = ChackDrop1.getAppearCount() + 1;
						int againCount = ChackDrop1.getAgainCount1() + 1;
						if (ChackDrop1.getAgainCount() == 0) {
							againCount = 0;
						}
						ChackDrop.dropToList(giveItemGet, appearCount, againCount);
					}*/
                    // 怪死說話
                    if ((String) aTempData.get(7) != null) {
                        npc.broadcastPacketAll(new S_NpcChatPacket(npc, (String) aTempData.get(7), 2));
                    }
                    L1ItemInstance item = ItemTable.get().createItem(giveItemGet);
                    if (item.isStackable()) {// 可重疊
                        item.setCount(giveCountGet);// 數量
                    } else {
                        item.setCount(1);
                    }
                    if (item != null) {
                        if (pc.getInventory().checkAddItem(item, (giveCountGet)) == L1Inventory.OK && !isDropGround) { // 不是負重或沒有設了怪物道具掉落地面
                            // 直接給玩家
                            pc.getInventory().storeItem(item);
                            if (pc.isInParty()) {
                                L1PcInstance[] partyMember = pc.getParty().getMembers();
                                for (int p = 0; p < partyMember.length; p++) {
                                    partyMember[p].sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), pc.getName()));
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(143, npc.getName(), item.getLogName()));
                            }
                            // 獲得指定物品公告
							/*if (item.getItem().dropbroadcast() == true) {
								if (ConfigDrop.DROP_TEXT_LIST.size() > 0) {
									World.get().broadcastPacketToAll(new S_DropMessage(pc.getName(), npc.getName(), item.getName()));
								} else {
									World.get().broadcastPacketToAll(new S_SystemMessage(pc.getName() + " 擊敗了 " + npc.getName() + " 獲得 " + item.getName() + "。"));
								}
							}*/
                        } else {
                            // World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
							/*if (item.getItem().dropbroadcast()) {
								final String[] m = { "\\fW", "\\fR", "\\fT", "\\fY", "\\fU", "\\fS", "\\fX", "\\fV" };
								final int r = RandomArrayList.getInt(m.length);
								World.get().broadcastPacketToAll(new S_SystemMessage(npc.getName() + " 被擊敗了，噴了" + m[r] + item.getName() + "在地上。"));
							}*/
                            // 掉落地面
                            final int x = RandomArrayList.getInt(npc.getNpcTemplate().getDropGround() * 2) - npc.getNpcTemplate().getDropGround();
                            final int y = RandomArrayList.getInt(npc.getNpcTemplate().getDropGround() * 2) - npc.getNpcTemplate().getDropGround();
                            World.get().getInventory(npc.getX() + x, npc.getY() + y, npc.getMapId()).storeItem(item);
                        }
                    }
                }
                break;// 找到就跳出回圈
            }
        }
    }

    private static Object getArray(String s, String sToken, int iType) {
        StringTokenizer st = new StringTokenizer(s, sToken);
        int iSize = st.countTokens();
        String sTemp = null;
        if (iType == 1) { // int
            int[] iReturn = new int[iSize];
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                iReturn[i] = Integer.parseInt(sTemp);
            }
            return iReturn;
        }
        if (iType == 2) { // String
            String[] sReturn = new String[iSize];
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                sReturn[i] = sTemp;
            }
            return sReturn;
        }
        if (iType == 3) { // String
            String sReturn = null;
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                sReturn = sTemp;
            }
            return sReturn;
        }
        return null;
    }

    public static ArrayList<ArrayList<Object>> getSetList() {
        return _array;
    }

    private void getData() {
        Connection cn = null;
        Statement ps = null;
        ResultSet rset = null;
        ArrayList<Object> aReturn;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.createStatement();
            rset = ps.executeQuery("SELECT * FROM 系統_怪物指定掉落物");
            while (rset.next()) {
                // 每次均不同
                aReturn = new ArrayList<Object>();
                aReturn.add(0, new Integer(rset.getInt("怪物編號")));
                aReturn.add(1, new Integer(rset.getInt("職業判斷")));
                aReturn.add(2, new Integer(rset.getInt("等級判斷")));
                aReturn.add(3, new Integer(rset.getInt("判斷需要道具")));
                aReturn.add(4, new Integer(rset.getInt("掉落機率")));
                aReturn.add(5, new Integer(rset.getInt("陣營判斷")));
                aReturn.add(6, new Integer(rset.getInt("陣營積分")));
                aReturn.add(7, rset.getString("怪物死亡顯示文字"));
                // 給予道具
                if (rset.getString("給予物品編號") != null && !rset.getString("給予物品編號").equals("") && !rset.getString("給予物品編號").equals("0")) // 給予道具
                {
                    aReturn.add(8, getArray(rset.getString("給予物品編號"), TOKEN, 1));
                } else {
                    aReturn.add(8, null);
                }
                // 給予道具數量
                if (rset.getString("給予物品數量") != null && !rset.getString("給予物品數量").equals("") && !rset.getString("給予物品數量").equals("0")) // 給予道具數量
                {
                    aReturn.add(9, getArray(rset.getString("給予物品數量"), TOKEN, 1));
                } else {
                    aReturn.add(9, null);
                }
                // 給予技能
                if ((rset.getString("給予技能編號") != null) && (!rset.getString("給予技能編號").equals("")) && (!rset.getString("給予技能編號").equals("0"))) // 給予技能
                {
                    aReturn.add(10, getArray(rset.getString("給予技能編號"), TOKEN, 1));
                } else {
                    aReturn.add(10, null);
                }
                _array.add(aReturn);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rset);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }
}
