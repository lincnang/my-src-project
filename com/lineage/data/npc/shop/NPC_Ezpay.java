package com.lineage.data.npc.shop;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.EzpayReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Account;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.PayBonus;
import william.PayBonusFirst;

import java.util.Map;

/**
 * 商品領取專員NPC<BR>
 * 維護:冰雕寵兒
 */
public class NPC_Ezpay extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(NPC_Ezpay.class);

    /**
     *
     */
    private NPC_Ezpay() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new NPC_Ezpay();
    }

    /**
     * 展示指定頁面
     *
     * @param pc
     * @param npc
     * @param page
     */
    private static final void showPage(L1PcInstance pc, L1NpcInstance npc, int page) {
        Map<Integer, int[]> list = pc.get_otherList().SHOPLIST;
        int allpage = list.size() / 10;
        if ((page > allpage) || (page < 0)) {
            page = 0;
        }
        if (list.size() % 10 != 0) {
            allpage++;
        }
        pc.get_other().set_page(page);
        int showId = page * 10;
        StringBuilder stringBuilder = new StringBuilder();
        for (int key = showId; key < showId + 10; key++) {
            int[] info = (int[]) list.get(Integer.valueOf(key));
            if (info != null) {
                L1Item itemtmp = ItemTable.get().getTemplate(info[1]);
                if (itemtmp != null) {
                    stringBuilder.append(itemtmp.getName() + "(" + info[2] + "),");
                }
            }
        }
        String[] clientStrAry = stringBuilder.toString().split(",");
        if (allpage == 1) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_1", clientStrAry));
        } else if (page < 1) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_3", clientStrAry));
        } else if (page >= allpage - 1) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_4", clientStrAry));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_2", clientStrAry));
        }
    }

    private static final void createNewItem(L1PcInstance pc, L1NpcInstance npc, int item_id, long count, int trueMoney) {
        try {
            if (pc == null) {
                return;
            }
            L1ItemInstance item = ItemTable.get().createItem(item_id);
            if (item != null) {
                item.setCount(count);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                PayBonus.getItem(pc, trueMoney);
                L1Account account = pc.getNetConnection().getAccount();
                if (account.get_first_pay() == 0) {
                    PayBonusFirst.getItem(pc, trueMoney);
                    account.set_first_pay(1);
                    AccountReading.get().updatefp(pc.getAccountName(), 1);
                }
                if (ConfigOtherSet2.Dividend && trueMoney >= ConfigOtherSet2.DividendQuantity) {
                    CreateNewItem.createNewItem(pc, ConfigOtherSet2.DividendItem, (trueMoney / ConfigOtherSet2.DividendQuantity) * ConfigOtherSet2.DividendQuantityCount); // SRC0910
                }
                pc.sendPackets(new S_ServerMessage("\\fW" + npc.getNameId() + "給你" + item.getLogName()));
                pc.save();
            } else {
                _log.error("給予物件失敗 原因: 指定編號物品不存在(" + item_id + ")");
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_0", new String[]{"" + pc.get_other().get_getbonus()}));
            //2017/04/23 顯示已贊助金額
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        boolean isCloseList = false;
        if (cmd.equals("up")) {
            final int page = pc.get_other().get_page() - 1;
            showPage(pc, npc, page);
        } else if (cmd.equals("dn")) {
            final int page = pc.get_other().get_page() + 1;
            showPage(pc, npc, page);
        } else if (cmd.equalsIgnoreCase("1")) { // 領取商品
            pc.get_otherList().SHOPLIST.clear();
            // 取回資料
            final Map<Integer, int[]> info = EzpayReading.get().ezpayInfo(pc.getAccountName().toLowerCase());
            if (info.size() <= 0) {
                // 無資料
                isCloseList = true;
                // 並沒有查詢到您的相關贊助記錄!!請您再仔細查詢一次。
                pc.sendPackets(new S_ServerMessage("\\fV並沒有查詢到您的相關商品記錄!!"));
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
                showPage(pc, npc, 0);
            }
        } else if (cmd.equalsIgnoreCase("2")) {// 全部領取
            // 取回資料
            final Map<Integer, int[]> info = EzpayReading.get().ezpayInfo(pc.getAccountName().toLowerCase());
            if (info.size() <= 0) {
                // 無資料
                isCloseList = true;
                // 並沒有查詢到您的相關贊助記錄!!請您再仔細查詢一次。
                pc.sendPackets(new S_ServerMessage("\\fV並沒有查詢到您的相關商品記錄!!"));
            } else {
                for (Integer key : info.keySet()) {
                    int[] value = info.get(key);
                    int id = value[0];
                    int itemid = value[1];
                    int count = value[2];
                    int trueMoney = value[3];
                    if (EzpayReading.get().update(pc.getAccountName(), id, pc.getClanname(), pc.getName(), pc.getNetConnection().getIp().toString())) {
                        // 給予物品
                        createNewItem(pc, npc, itemid, count, trueMoney);
                        _log.fatal("帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName() + " 領取交易序號:" + id + "(" + itemid + ") 數量:" + count + " 完成!!");
                    } else {
                        pc.sendPackets(new S_ServerMessage("\\fV領取失敗!!請聯繫線上GM!! ID:" + id));
                        _log.fatal("帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName() + " 領取交易序號:" + id + " 領取失敗!!");
                        isCloseList = true;
                    }
                }
            }
            isCloseList = true;
        } else {
            isCloseList = true;
        }
        if (isCloseList) {
            // 關閉對話窗
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }
}