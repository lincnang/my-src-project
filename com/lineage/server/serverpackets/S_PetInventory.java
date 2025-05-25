/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PetInstance;

import java.util.List;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket
public class S_PetInventory extends ServerBasePacket {
    private static final String S_PET_INVENTORY = "[S] S_PetInventory";
    private byte[] _byte = null;

    public S_PetInventory(L1PetInstance pet) {
        List<L1ItemInstance> itemList = pet.getInventory().getItems();
        writeC(S_RETRIEVE_LIST);
        writeD(pet.getId());
        writeH(itemList.size());
        writeC(0x0b);
        for (final L1ItemInstance petItem : itemList) {
            if (petItem != null) {
                writeD(petItem.getId());
                writeC(0x16); // 值:0x00 無、0x01:武器類、0x02:防具類、0x16:牙齒類 、0x33:藥水類
                writeH(petItem.get_gfxid());
                writeC(petItem.getBless());
                this.writeD((int) Math.min(petItem.getCount(), 2000000000));
                // 顯示裝備中的寵物裝備
                if (petItem.getItem().getType2() == 0 && petItem.getItem().getType() == 11 && petItem.isEquipped()) {
                    writeC(petItem.isIdentified() ? 3 : 2);
                } else {
                    writeC(petItem.isIdentified() ? 1 : 0);
                }
                writeS(petItem.getViewName());
            }
        }
        writeC(pet.getAc()); // 寵物防禦
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return S_PET_INVENTORY;
    }
}
