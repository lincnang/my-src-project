package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.datatables.lock.CharacterC1Reading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1User_Power;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * [原碼] AT禮盒
 */
public class chang_othertype extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(chang_othertype.class);

    private chang_othertype() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new chang_othertype();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        int itemId = item.getItem().getItemId();
        try {
            // 例外狀況:人物為空
            if (pc == null) {
                return;
            }
            if (pc.get_c_power() != null && pc.get_c_power().get_c1_type() == 0) {
                pc.sendPackets(new S_SystemMessage("您尚未加入陣營!!"));
                return;
            }
			/*if(pc.get_c_power().get_c1_type() > 0 ){
				pc.get_c_power().set_c1_type(0);
				pc.sendPackets(new S_SystemMessage("您成功脫離了陣營"));
				CharacterC1Reading.get().updateCharacterC1(pc.getId(), 0, "無");
		        pc.get_c_power().set_power(pc, false);
		        pc.sendPacketsAll(new S_ChangeName(pc, true));
		        pc.save();
				return;
			}*/
            if (itemId == 240344) {
                if (pc.get_c_power().get_c1_type() > 0) {
                    pc.get_c_power().set_c1_type(0);
                    pc.sendPackets(new S_SystemMessage("您成功脫離了陣營"));
                    CharacterC1Reading.get().updateCharacterC1(pc.getId(), 0, "無");
                    pc.get_c_power().set_power(pc, false);
                    pc.sendPacketsAll(new S_ChangeName(pc, true));
                    pc.save();
                }
                pc.get_c_power().set_c1_type(1);
                //pc.get_other().set_score(0);
                CharacterC1Reading.get().updateCharacterC1(pc.getId(), 1, "轉換-復甦");
                pc.sendPackets(new S_SystemMessage("您以強制變更陣營國度-復甦"));
                pc.sendPackets(new S_SystemMessage("請登出在進來!!進行更新"));
                add_type(1, pc);
            } else if (itemId == 240345) {
                if (pc.get_c_power().get_c1_type() > 0) {
                    pc.get_c_power().set_c1_type(0);
                    pc.sendPackets(new S_SystemMessage("您成功脫離了陣營"));
                    CharacterC1Reading.get().updateCharacterC1(pc.getId(), 0, "無");
                    pc.get_c_power().set_power(pc, false);
                    pc.sendPacketsAll(new S_ChangeName(pc, true));
                    pc.save();
                }
                pc.get_c_power().set_c1_type(2);
                CharacterC1Reading.get().updateCharacterC1(pc.getId(), 2, "轉換-勇猛");
                //pc.get_other().set_score(0);
                pc.sendPackets(new S_SystemMessage("您以強制變更陣營國度-勇猛"));
                pc.sendPackets(new S_SystemMessage("請登出在進來!!進行更新"));
                add_type2(2, pc);
            } else if (itemId == 240346) {
                if (pc.get_c_power().get_c1_type() > 0) {
                    pc.get_c_power().set_c1_type(0);
                    pc.sendPackets(new S_SystemMessage("您成功脫離了陣營"));
                    CharacterC1Reading.get().updateCharacterC1(pc.getId(), 0, "無");
                    pc.get_c_power().set_power(pc, false);
                    pc.sendPacketsAll(new S_ChangeName(pc, true));
                    pc.save();
                }
                pc.get_c_power().set_c1_type(3);
                CharacterC1Reading.get().updateCharacterC1(pc.getId(), 3, "轉換-抵禦");
                //pc.get_other().set_score(0);
                pc.sendPackets(new S_SystemMessage("您以強制變更陣營國度-抵禦"));
                pc.sendPackets(new S_SystemMessage("請登出在進來!!進行更新"));
                add_type3(3, pc);
            }
            pc.getInventory().removeItem(item, 1);
            pc.save();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            _log.error("轉換陣營設定異常", e);
        }
    }

    private void add_type(int cmd, L1PcInstance pc) {
        int type = 1;
        String typeName = C1_Name_Table.get().get(type);
        if (pc.get_c_power() == null) {
            L1User_Power power = new L1User_Power();
            power.set_object_id(pc.getId());
            power.set_c1_type(type);
            power.set_note(typeName);
            pc.set_c_power(power);
            CharacterC1Reading.get().storeCharacterC1(pc);
        } else {
            pc.get_c_power().set_c1_type(type);
            pc.get_c_power().set_note(typeName);
            CharacterC1Reading.get().updateCharacterC1(pc.getId(), type, typeName);
        }
        pc.get_c_power().set_power(pc, false);
        pc.sendPacketsAll(new S_ChangeName(pc, true));
        pc.sendPackets(new S_ServerMessage("\\fR成功加入陣營：" + typeName));
    }

    private void add_type2(int cmd, L1PcInstance pc) {
        int type = 2;
        String typeName = C1_Name_Table.get().get(type);
        if (pc.get_c_power() == null) {
            L1User_Power power = new L1User_Power();
            power.set_object_id(pc.getId());
            power.set_c1_type(type);
            power.set_note(typeName);
            pc.set_c_power(power);
            CharacterC1Reading.get().storeCharacterC1(pc);
        } else {
            pc.get_c_power().set_c1_type(type);
            pc.get_c_power().set_note(typeName);
            CharacterC1Reading.get().updateCharacterC1(pc.getId(), type, typeName);
        }
        pc.get_c_power().set_power(pc, false);
        pc.sendPacketsAll(new S_ChangeName(pc, true));
        pc.sendPackets(new S_ServerMessage("\\fR成功加入陣營：" + typeName));
    }

    private void add_type3(int cmd, L1PcInstance pc) {
        int type = 3;
        String typeName = C1_Name_Table.get().get(type);
        if (pc.get_c_power() == null) {
            L1User_Power power = new L1User_Power();
            power.set_object_id(pc.getId());
            power.set_c1_type(type);
            power.set_note(typeName);
            pc.set_c_power(power);
            CharacterC1Reading.get().storeCharacterC1(pc);
        } else {
            pc.get_c_power().set_c1_type(type);
            pc.get_c_power().set_note(typeName);
            CharacterC1Reading.get().updateCharacterC1(pc.getId(), type, typeName);
        }
        pc.get_c_power().set_power(pc, false);
        pc.sendPacketsAll(new S_ChangeName(pc, true));
        pc.sendPackets(new S_ServerMessage("\\fR成功加入陣營：" + typeName));
    }
}
