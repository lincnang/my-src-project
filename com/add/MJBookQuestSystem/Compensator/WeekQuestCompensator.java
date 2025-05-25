package com.add.MJBookQuestSystem.Compensator;

import com.add.MJBookQuestSystem.Compensator.Element.ExpCompensator;
import com.add.MJBookQuestSystem.Compensator.Element.ItemCompensator;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.MJBytesOutputStream;

import java.sql.ResultSet;

public class WeekQuestCompensator implements QuestCompensator {
    public static final String _table = "tb_weekquest_compensate";
    private static final int[] _exps = new int[]{721306, 1803265, 7213060};
    private static final int[] _dds = new int[]{0, 4166, 15391};
    private int _buttonNo; // button number.
    private int _ingredientItemId; // 道具 id.
    private ExpCompensator _exp; // 提供獎勵經驗 element.
    private ItemCompensator _item; // 提供獎勵物品 element.
    private String _lastRecord; // 最後記錄
    private byte[] _serialDatas;

    @Override
    public void set(ResultSet rs) throws Exception {
        int nTmp1 = 0;
        int nTmp2 = 0;
        int nTmp3 = 0;
        _lastRecord = "button_no";
        _buttonNo = rs.getInt(_lastRecord);
        _lastRecord = "ingredient_itemId";
        _ingredientItemId = rs.getInt(_lastRecord);
        _lastRecord = "compen_exp";
        nTmp1 = rs.getInt(_lastRecord);
        _lastRecord = "compen_exp_level";
        nTmp2 = rs.getInt(_lastRecord);
        _exp = new ExpCompensator(nTmp1, nTmp2);
        _lastRecord = "compen_itemId";
        nTmp1 = rs.getInt(_lastRecord);
        _lastRecord = "compen_itemCount";
        nTmp2 = rs.getInt(_lastRecord);
        _lastRecord = "compen_itemLevel";
        nTmp3 = rs.getInt(_lastRecord);
        _item = new ItemCompensator(nTmp1, nTmp2, nTmp3);
        serialize();
    }

    public synchronized void serialize() throws Exception {
        if (_serialDatas != null) {
            return;
        }
        @SuppressWarnings("resource") MJBytesOutputStream mbos = new MJBytesOutputStream();
        mbos.write(0x12);
        mbos.write(0x10);
        mbos.write(0x08);
        mbos.write(_buttonNo);
        mbos.write(0x10);
        // mbos.writeBit(_exp.getExp());
        mbos.writeBit(_exps[_buttonNo - 1]);
        mbos.write(0x18);
        mbos.writeBit(_dds[_buttonNo - 1]);
        mbos.write(0x22);
        mbos.write(0x06);
        mbos.write(0x08);
        // 軍隊的印章 descid
        mbos.writeBit(17368);
        mbos.write(0x10);
        // 數量,,, 按難度級別 1, 5, 10個
        mbos.write((_buttonNo / 3) + (_buttonNo % 3));
        _serialDatas = mbos.toArray();
        _serialDatas[1] = (byte) ((_serialDatas.length - 2) & 0xff);
    }

    @Override
    public String getLastRecord() {
        return _lastRecord;
    }

    @Override
    public int getDifficulty() {
        return _buttonNo;
    }

    @Override
    public void compensate(L1PcInstance pc) {
        if (_ingredientItemId != 0) {
            if (!pc.getInventory().checkItem(_ingredientItemId)) {
                StringBuilder sb = new StringBuilder(100);
                L1Item item = ItemTable.get().getTemplate(_ingredientItemId);
                if (item == null) {
                    return;
                }
                sb.append(item.getName()).append(")不足,領取失敗");
                // pc.sendPackets(new S_SystemMessage(sb.toString()));
                pc.sendPackets(new S_SystemMessage("領此獎勵所需道具(" + sb.toString()));
                return;
            }
            pc.getInventory().consumeItem(_ingredientItemId, 1);
        }
        if (_exp != null) {
            _exp.compensate(pc);
        }
        if (_item != null) {
            _item.compensate(pc);
        }
    }

    public byte[] getSerialize() {
        return _serialDatas;
    }
}
