package com.add.Tsai;

import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.EzpayReading3;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * 收藏指令
 *
 * @author hero
 */
public class PromotionCmd {
    private static final Log _log = LogFactory.getLog(PromotionCmd.class);
    private static PromotionCmd _instance;

    public static PromotionCmd get() {
        if (_instance == null) {
            _instance = new PromotionCmd();
        }
        return _instance;
    }

    private static void createNewItem(final L1PcInstance pc, final L1PcInstance npc, final int item_id, final long count) {
        try {
            if (pc == null) {
                return;
            }
            // 產生新物件
            final L1ItemInstance item = ItemTable.get().createItem(item_id);
            if (item != null) {
                item.setCount(count);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                pc.sendPackets(new S_ServerMessage("\\fW" + "給你" + item.getLogName())); // 獲得0%。
                //PayBonus.getItem(pc, count);
            } else {
                _log.error("給予物件失敗 原因: 指定編號物品不存在(" + item_id + ")");
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 展示指定頁面
     *
     */
    private static void showPage(final L1PcInstance pc, final L1PcInstance npc, int page) {
        final Map<Integer, int[]> list = pc.get_otherList().SHOPLIST;
        // 全部頁面數量
        int allpage = list.size() / 10;
        if ((page > allpage) || (page < 0)) {
            page = 0;
        }
        if (list.size() % 10 != 0) {
            allpage += 1;
        }
        pc.get_other().set_page(page);// 設置頁面
        final int showId = page * 10;// 要顯示的項目ID
        final StringBuilder stringBuilder = new StringBuilder();
        // 每頁顯示10筆(showId + 10)資料
        for (int key = showId; key < showId + 10; key++) {
            final int[] info = list.get(key);
            if (info != null) {
                // 找回物品
                final L1Item itemtmp = ItemTable.get().getTemplate(info[1]);
                if (itemtmp != null) {
                    stringBuilder.append(itemtmp.getName()).append("(").append(info[2]).append("),");
                }
            }
        }
        final String[] clientStrAry = stringBuilder.toString().split(",");
        if (allpage == 1) {
            // 核心要求顯示僅有一頁
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "send_s_1", clientStrAry));
        } else {
            if (page < 1) {// 無上一頁
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "send_s_3", clientStrAry));
            } else if (page >= (allpage - 1)) {// 無下一頁(吻合第一頁為0 所以 -1)
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "send_s_4", clientStrAry));
            } else {// 正常
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "send_s_2", clientStrAry));
            }
        }
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        boolean isErr = false;
        boolean isCloseList = false;
        if (cmd.equals("up")) {
            final int page = pc.get_other().get_page() - 1;
            showPage(pc, pc, page);
        } else if (cmd.equals("dn")) {
            final int page = pc.get_other().get_page() + 1;
            showPage(pc, pc, page);
        } else if (cmd.equalsIgnoreCase("send_1")) {// 領取商品
            pc.get_otherList().SHOPLIST.clear();
            // 取回資料
            final Map<Integer, int[]> info = EzpayReading3.get().ezpayInfo(pc.getAccountName().toLowerCase());
            if (info.size() <= 0) {
                // 無資料
                isCloseList = true;
                // 並沒有查詢到您的相關贊助記錄!!請您再仔細查詢一次。
                pc.sendPackets(new S_ServerMessage("並沒有查詢到您的相關商品記錄!!"));
            } else {
                pc.get_other().set_page(0);
                int index = 0;
                for (Integer key : info.keySet()) {
                    int[] value = info.get(key);
                    if (value != null) {
                        pc.get_otherList().SHOPLIST.put(index, value);
                        index++;
                    }
                }
                showPage(pc, pc, 0);
            }
        } else if (cmd.equalsIgnoreCase("send_2")) {// 全部領取
            // 取回資料
            final Map<Integer, int[]> info = EzpayReading3.get().ezpayInfo(pc.getAccountName().toLowerCase());
            if (info.size() <= 0) {
                // 無資料
                isCloseList = true;
                // 並沒有查詢到您的相關贊助記錄!!請您再仔細查詢一次。
                pc.sendPackets(new S_ServerMessage("並沒有查詢到您的相關商品記錄!!"));
            } else {
                for (Integer key : info.keySet()) {
                    int[] value = info.get(key);
                    int id = value[0];
                    int itemid = value[1];
                    int count = value[2];
                    if (EzpayReading3.get().update(pc.getAccountName(), id, pc.getName(), pc.getNetConnection().getIp().toString())) {
                        // 滿額好禮
						/*checkBonus(pc, npc, count);
						final L1Account account = pc.getNetConnection().getAccount();
						if (account.get_pay_first() == 0){
							account.set_pay_first(1);
							AccountReading.get().updateFirstPay(pc.getAccountName(), 1);
							checkBonusFirst(pc, npc, count);
						}*/
                        // 首儲判斷 (注意下面的count *=2 意思是玩家首儲元寶是兩倍送)拆角色使用
						/*if (!pc.getQuest().isEnd(L1PcQuest.QUEST_GETMONEY)) {// 任務編號請自訂
							pc.getQuest().set_step(L1PcQuest.QUEST_GETMONEY, 0);
							pc.getQuest().set_end(L1PcQuest.QUEST_GETMONEY);
							count *= 1;
						}*/
                        // 給予物品
                        createNewItem(pc, pc, itemid, count);
                        _log.fatal("帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName() + " 領取交易序號:" + id + "(" + itemid + ") 數量:" + count + " 完成!!");
                        WriteLogTxt.Recording("即時獎勵成功紀錄", "帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName() + " 領取交易序號:" + id + "(" + itemid + ") 數量:" + count + " 完成!!");
                    } else {
                        pc.sendPackets(new S_ServerMessage("\\fV領取失敗!!請聯繫線上GM!! ID:" + id));
                        _log.fatal("帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName() + " 領取交易序號:" + id + " 領取失敗!!");
                        WriteLogTxt.Recording("即時獎勵失敗紀錄", "帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName() + " 領取交易序號:" + id + " 領取失敗!!");
                        isCloseList = true;
                    }
                }
            }
            isCloseList = true;
        } else {
            isCloseList = true;
            isErr = true;
        }
        if (isCloseList) {
            // 關閉對話窗
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
        return !isErr;
    }
}