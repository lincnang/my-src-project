package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

public class Ajplayer extends ItemExecutor {
    public static ItemExecutor get() {
        return new Ajplayer();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int spellsc_objid = data[0];
        sneakpeek(pc, spellsc_objid, item);
    }

    private void sneakpeek(L1PcInstance pc, int targetId, L1ItemInstance item) {
        /** [原碼] 反 & 偷窺卡 */
        L1Object target = World.get().findObject(targetId);
        if (target != null) {
            String msg0 = ""; // 的偷窺內容
            String msg1 = ""; // 等級
            String msg2 = ""; // 體力
            String msg3 = ""; // 魔力
            String msg4 = ""; // 防禦
            String msg5 = ""; // 迴避
            String msg6 = ""; // 抗魔
            String msg7 = ""; // 抗火
            String msg8 = ""; // 抗水
            String msg9 = ""; // 抗風
            String msg10 = ""; // 抗地
            String msg11 = ""; // 當前武器
            String msg12 = "";// 力量
            String msg13 = "";// 體力
            String msg14 = "";// 敏捷
            String msg15 = "";// 精神
            String msg16 = "";// 智力
            String msg17 = "";// 魅力
            String msg18 = "";// 編號
            String msg19 = "";// 圖檔
            String msg20 = "";// 經驗
            String msg21 = "";// 正義
            String msg22 = "";// 魔攻
            if (target instanceof L1PcInstance) {
                L1PcInstance target_pc = (L1PcInstance) target;
                if ((!target_pc.getInventory().checkItem(50104, 1)) || (pc.isGm())) {// 對像身上沒有反偷窺卡或是執行者是GM
                    msg0 = target_pc.getName();
                    msg1 = "" + target_pc.getLevel(); // 等級
                    msg2 = "" + target_pc.getCurrentHp() + " / " + target_pc.getMaxHp(); // 體力
                    msg3 = "" + target_pc.getCurrentMp() + " / " + +target_pc.getMaxMp(); // 魔力
                    msg4 = "" + target_pc.getAc(); // 防禦
                    msg5 = "" + target_pc.getEr(); // 迴避
                    msg6 = "" + target_pc.getMr() + " %"; // 抗魔
                    msg7 = "" + target_pc.getFire() + " %"; // 抗火
                    msg8 = "" + target_pc.getWater() + " %"; // 抗水
                    msg9 = "" + target_pc.getWind() + " %"; // 抗風
                    msg10 = "" + target_pc.getEarth() + " %"; // 抗地
                    msg12 = "" + target_pc.getStr();// 力量
                    msg13 = "" + target_pc.getCon();// 體力
                    msg14 = "" + target_pc.getDex();// 敏捷
                    msg15 = "" + target_pc.getWis();// 精神
                    msg16 = "" + target_pc.getInt();// 智力
                    msg17 = "" + target_pc.getCha();// 魅力
                    msg18 = "0";// 編號
                    msg19 = "" + target_pc.getGfxId();// 圖檔
                    msg20 = "" + target_pc.getExp();// 經驗
                    msg21 = "" + target_pc.getLawful();// 正義
                    msg22 = "" + target_pc.getSp();// 魔攻
                    L1ItemInstance weapon = target_pc.getWeapon();
                    if (weapon != null) {
                        msg11 = "" + weapon.getLogName();
                    } else {
                        msg11 = "" + "無裝備武器";
                    }
                    String msg[] = {msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11, msg12, msg13, msg14, msg15, msg16, msg17, msg18, msg19, msg20, msg21, msg22};
                    pc.sendPackets(new S_NPCTalkReturn(pc, "ajplayer", msg));
                    pc.getInventory().removeItem(item, 1);
                } else {// 對像身上有反偷窺卡
                    int deleitem = 50104;
                    target_pc.getInventory().consumeItem(deleitem, 1);
                    target_pc.sendPackets(new S_SystemMessage(pc.getName() + "對你使用偷窺卡，但是沒有偷窺成功。"));
                    pc.getInventory().removeItem(item, 1);
                }
            } else if (pc.isGm()) {// GM才可偷窺NPC或怪物
                L1NpcInstance target_npc = null;
                if (target instanceof L1MonsterInstance) {
                    target_npc = (L1MonsterInstance) target;
                } else if (target instanceof L1NpcInstance) {
                    target_npc = (L1NpcInstance) target;
                }
                msg0 = target_npc.getName();
                msg1 = "" + target_npc.getLevel(); // 等級
                msg2 = "" + target_npc.getCurrentHp() + " / " + target_npc.getMaxHp(); // 體力
                msg3 = "" + target_npc.getCurrentMp() + " / " + +target_npc.getMaxMp(); // 魔力
                msg4 = "" + target_npc.getAc(); // 防禦
                msg5 = "0"; // 迴避
                msg6 = "" + target_npc.getMr() + " %"; // 抗魔
                msg7 = "" + target_npc.getFire() + " %"; // 抗火
                msg8 = "" + target_npc.getWater() + " %"; // 抗水
                msg9 = "" + target_npc.getWind() + " %"; // 抗風
                msg10 = "" + target_npc.getEarth() + " %"; // 抗地
                msg11 = "砂鍋大的拳頭";
                msg12 = "" + target_npc.getStr();// 力量
                msg13 = "" + target_npc.getCon();// 體力
                msg14 = "" + target_npc.getDex();// 敏捷
                msg15 = "" + target_npc.getWis();// 精神
                msg16 = "" + target_npc.getInt();// 智力
                msg17 = "" + target_npc.getCha();// 魅力
                msg18 = "" + target_npc.getNpcId();// 編號
                msg19 = "" + target_npc.getGfxId();// 圖檔
                msg20 = "" + target_npc.getExp();// 經驗
                msg21 = "" + target_npc.getLawful();// 正義
                msg22 = "" + target_npc.getSp();// 魔攻
                String msg[] = {msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11, msg12, msg13, msg14, msg15, msg16, msg17, msg18, msg19, msg20, msg21, msg22};
                pc.sendPackets(new S_NPCTalkReturn(pc, "ajplayer", msg));
                pc.getInventory().removeItem(item, 1);
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Acid_Liquor JD-Core Version: 0.6.2
 */