package com.lineage.server.command.executor;

import com.lineage.echo.ClientExecutor;
import com.lineage.list.OnlineUser;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.DwarfForChaReading;
import com.lineage.server.datatables.lock.ServerGmCommandReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;

/**
 * 推文獎勵發送(角色專屬倉庫道具發送)
 *
 * @author dexc
 */
public class L1Post implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1Post.class);

    private L1Post() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Post();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            final StringTokenizer st = new StringTokenizer(arg);
            final String name = st.nextToken();// 輸入名稱
            final int tgid = CharObjidTable.get().charObjid(name);// 取回此名稱的objid
            final String pcname = CharacterTable.get().getCharName(tgid);// 正確玩家名稱(包含大小寫)
            final String acc = CharacterTable.getAccountName(name);// 玩家帳號
            if (tgid > 0) {// 有此objid資料
                int item_id;
                if (st.hasMoreTokens()) {
                    item_id = Integer.parseInt(st.nextToken());// 道具編號
                } else {
                    item_id = 80032;// 推廣寶箱
                }
                int count;
                if (st.hasMoreTokens()) {
                    count = Integer.parseInt(st.nextToken());// 數量
                } else {
                    count = 1;// 預設5個
                }
                int enchantLevel;
                if (st.hasMoreTokens()) {
                    enchantLevel = Integer.parseInt(st.nextToken());// 加值
                } else {
                    enchantLevel = 0;// 預設+0
                }
                // 產生新物件
                final L1ItemInstance item = ItemTable.get().createItem(item_id);
                if (item != null) {
                    item.setCount(count);
                    item.setEnchantLevel(enchantLevel);
                    if (pc == null) {
                        _log.info("指定角色專屬倉庫(" + pcname + ") 加入物品：" + item.getLogName());
                    } else {
                        pc.sendPackets(new S_ServerMessage(166, "指定角色專屬倉庫(" + pcname + ") 加入物品：" + item.getLogName()));
                    }
                    DwarfForChaReading.get().insertItem(pcname, item);// 加入角色專屬倉庫
                    ClientExecutor cl = OnlineUser.get().get(acc);
                    if (cl != null) {
                        final L1PcInstance tgpc = cl.getActiveChar();
                        if (tgpc != null) {
                            tgpc.sendPackets(new S_ServerMessage(166, "管理員在你的角色專屬倉庫加入物品：" + item.getLogName()));
                            // 重整倉庫數據
                            tgpc.getDwarfForChaInventory().loadItems();
                        }
                    }
                    ServerGmCommandReading.get().create(pc, "obj:" + item.getId());
                    System.out.println(item.getName());
                    if ((item.getName().contains("商城幣") && count == 500) || item.getName().contains("超特製") || item.getName().contains("疾風") || item.getName().contains("破滅者") || item.getName().contains("黑曜石")) {
                        ServerGmCommandReading.get().createTradeControl(item.getId(), pcname);
                    }
                }
            } else {// 沒有此objid資料
                if (pc == null) {
                    _log.info("指令異常: 沒有該角色OBJID資料 (" + pcname + ")!!");
                } else {
                    pc.sendPackets(new S_ServerMessage(166, "指令異常: 沒有該角色OBJID資料 (" + pcname + ")!!"));
                }
            }
        } catch (final Exception e) {
            _log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName());
            // 261 \f1指令錯誤。
        }
    }
}
