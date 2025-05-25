package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_FixWeaponList;

import java.util.ArrayList;
import java.util.List;

/**
 * 要求維修物品清單
 *
 * @author daien
 */
public class C_FixWeaponList extends ClientBasePacket {
    /*
     * public C_FixWeaponList() { }
     *
     * public C_FixWeaponList(final byte[] abyte0, final ClientExecutor client)
     * { super(abyte0); try { this.start(abyte0, client);
     *
     * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
     */
    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            // this.read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            // 暫時清單
            final List<L1ItemInstance> weaponList = new ArrayList<>();
            // 背包物件
            final List<L1ItemInstance> itemList = pc.getInventory().getItems();
            for (final L1ItemInstance item : itemList) {
                // Find Weapon
                switch (item.getItem().getType2()) {
                    case 1:
                        if (item.get_durability() > 0) {
                            weaponList.add(item);
                        }
                        break;
                }
            }
            /*
             * if (weaponList.isEmpty()) { return; }
             *
             * /*if (weaponList.size() <= 0) { return; }
             */
            pc.sendPackets(new S_FixWeaponList(weaponList));
            // pc.sendPackets(new S_FixWeaponList(pc));
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
