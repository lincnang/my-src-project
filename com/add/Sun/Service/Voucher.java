package com.add.Sun.Service;

import com.add.Sun.DAL.DALTool;
import com.add.Sun.DTO.DTO_1;
import com.add.Sun.DTO.DTO_2;
import com.add.Sun.DTO.DTO_3;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 兌換碼Service
 *
 * @author Sun
 */
public class Voucher {
    private static final Log _log = LogFactory.getLog(Voucher.class);
    private List<DTO_1> _codeList;
    private List<DTO_2> _configList;
    private List<DTO_3> _receiveList;
    private DALTool tool;

    public Voucher() {
        tool = new DALTool();
    }

    public static void SendTo(L1PcInstance pc, int itemId, int count, int lv) {
        L1ItemInstance item = ItemTable.get().createItem(itemId);
        if (item.isStackable()) {//可重疊
            item.setCount(count);//數量
        } else {
            item.setCount(1);
        }
        if (item.getItem().getType2() == 1 || // 武器類
                item.getItem().getType2() == 2) { // 防具類
            item.setEnchantLevel(lv);// 強化數
        } else {
            item.setEnchantLevel(0);
        }
        if (item != null) {
            if (pc.getInventory().checkAddItem(item, (count)) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
            }
            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
        }
    }

    private void GetCodeData(String code) {
        String sql = "SELECT * FROM 其他_兌換碼_代碼 where 代碼=?;";
        try {
            List<Object> list = new ArrayList<>();
            list.add(code);
            this._codeList = tool.selectQuery(DTO_1.class, sql, list);
        } catch (Exception e) {
            _log.error(e.getMessage());
        }
    }

    private void GetReceiveData(long codeId, String account) {
        String sql = "SELECT * FROM character_兌換 where `兌換碼Id`=? and `帳號`=?;";
        try {
            List<Object> list = new ArrayList<>();
            list.add(codeId);
            list.add(account);
            this._receiveList = tool.selectQuery(DTO_3.class, sql, list);
        } catch (Exception e) {
            _log.error(e.getMessage());
        }
    }

    private long GetReceiveCount(long codeId) {
        try {
            long result = tool.Count("character_兌換", "WHERE 兌換碼Id=" + codeId);
            return result;
        } catch (Exception e) {
            _log.error(e.getMessage());
        }
        return 0;
    }

    private void GetConfigData(long configId) {
        String sql = "SELECT * FROM 其他_兌換碼_物品設定 where id=?";
        try {
            List<Object> list = new ArrayList<>();
            list.add(configId);
            this._configList = tool.selectQuery(DTO_2.class, sql, list);
        } catch (Exception e) {
            _log.error(e.getMessage());
        }
    }

    public boolean Check(L1PcInstance pc, String chat) {
        //1.確定是否為兌換碼
        GetCodeData(chat);
        if (_codeList.isEmpty()) {
            return false;
        }
        //2.驗證
        DTO_1 codeDto = _codeList.get(0);
        GetReceiveData(codeDto.getId(), pc.getAccountName());
        //檢查同一帳號是否領取過
        if (!_receiveList.isEmpty() && codeDto.get同一帳號領取限制()) {
            pc.sendPackets(new S_ServerMessage("\\fU 系統訊息:您已經領取過此兌換代碼了，"));
            pc.sendPackets(new S_ServerMessage("\\fU 一個帳號限領一次!"));
            return true;
        }
        //      boolean s= _receiveList.stream().anyMatch((兌換碼紀錄DTO x)->x.get角色名稱().equals(pc.getName()));
        //      if(codeDto.get同一角色領取限制()&&s){
        //          pc.sendPackets(new S_ServerMessage("\\fU 系統訊息:您已經領取過此兌換代碼了，"));
        //          pc.sendPackets(new S_ServerMessage("\\fU 一個角色限領一次!"));
        //          return true;
        //！ ./'x      }
        //檢查兌換期限
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (codeDto.get兌換起始日() != null && codeDto.get兌換起始日().after(now)) {
            pc.sendPackets(new S_ServerMessage("\\fU 系統訊息:此兌換碼活動還沒開始!"));
            return false;
        }
        if (codeDto.get兌換結束日() != null && codeDto.get兌換結束日().before(now)) {
            pc.sendPackets(new S_ServerMessage("\\fU 系統訊息:此兌換碼活動已過!"));
            return false;
        }
        //檢查兌換上限
        long limit = codeDto.get兌換數量上限();
        if (limit != 0 && limit <= this.GetReceiveCount(codeDto.getId())) {
            pc.sendPackets(new S_ServerMessage("\\fU 系統訊息:此兌換碼已被全數領取完畢!"));
            return false;
        }
        //3.發送道具
        GetConfigData(codeDto.get物品設定Id());
        String[] ids = _configList.get(0).get物品Id().split(",");
        String[] values = _configList.get(0).get物品數量().split(",");
        String[] levels = _configList.get(0).get物品強化等級().split(",");
        if (ids.length != values.length || ids.length != levels.length) {
            _log.error("道具設定檔ID:【" + codeDto.get物品設定Id() + "】錯誤:道具編號、數量、等級不一致!");
            return false;
        }
        for (int i = 0; i < ids.length; i++) {
            int id = Integer.parseInt(ids[i]);
            int value = Integer.parseInt(values[i]);
            int level = Integer.parseInt(levels[i]);
            SendTo(pc, id, value, level);
        }
        //4.新增領取紀錄
        DTO_3 newData = new DTO_3();
        newData.set角色名稱(pc.getName());
        newData.set帳號(pc.getAccountName());
        newData.set兌換碼Id(_codeList.get(0).getId());
        try {
            tool.Insert(newData, "character_兌換", true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        //        String sql="insert into 兌換碼_紀錄 (兌換碼Id,角色名稱,領取時間,帳號) values("
        //                +_codeList.get(0).getId()
        //                +",'"+pc.getName()+"',NOW(),'"+pc.getAccountName()+"')";
        //        try {
        //            tool.Execute(sql);
        //        }catch (Exception e){
        //            _log.error("新增領取紀錄Error:"+e.getMessage());
        //        }
        //5.發送訊息給玩家
        if (_codeList.get(0).get是否要公告到世界頻()) {
            World.get().broadcastPacketToAll(new S_SystemMessage("恭喜玩家『" + pc.getName() + "』成功兌換『" + _configList.get(0).get物品描述() + "』。"));
        }
        String msg = _codeList.get(0).get顯示給玩家的訊息();
        if (msg != null || !msg.isEmpty()) {
            pc.sendPackets(new S_ServerMessage("\\fU 系統訊息:" + _codeList.get(0).get顯示給玩家的訊息()));
        }
        return true;
    }
}
