package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_PacketBoxDurability;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 要求物品維修
 *
 * @author daien
 */
public class C_SelectList extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_SelectList.class);

    /*
     * public C_SelectList() { }
     *
     * public C_SelectList(final byte[] abyte0, final ClientExecutor client) {
     * super(abyte0); try { this.start(abyte0, client);
     *
     * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
     */
    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost()) { // 鬼魂模式
                return;
            }
            if (pc.isDead()) { // 死亡
                return;
            }
            if (pc.isTeleport()) { // 傳送中
                return;
            }
            if (pc.isPrivateShop()) { // 商店村模式
                return;
            }
            final int itemObjectId = this.readD();
            final int npcObjectId = this.readD();
            if (npcObjectId != 0) { // 武器修理
                final L1Object obj = World.get().findObject(npcObjectId);
                if (obj != null) {
                    if (obj instanceof L1NpcInstance) {
                        final L1NpcInstance npc = (L1NpcInstance) obj;
                        final int difflocx = Math.abs(pc.getX() - npc.getX());
                        final int difflocy = Math.abs(pc.getY() - npc.getY());
                        // 5格以上距離無效
                        if ((difflocx > 5) || (difflocy > 5)) {
                            return;
                        }
                    }
                }
                final L1PcInventory pcInventory = pc.getInventory();
                final L1ItemInstance item = pcInventory.getItem(itemObjectId);
                final int cost = item.get_durability() * 200;// 每一點損壞度200元
                if (!pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
                    // 189：\f1金幣不足。 。
                    pc.sendPackets(new S_ServerMessage(189));
                    return;
                }
                item.set_durability(0);
                // 464：%0 現在變成像個新的一樣。
                pc.sendPackets(new S_ServerMessage(464, item.getLogName()));
                pc.sendPackets(new S_PacketBoxDurability(item));
                pcInventory.updateItem(item, L1PcInventory.COL_DURABILITY);
            } else { // 寵物清單
                int petCost = 0;
                int petCount = 0;
                int divisor = 6;
                final Object[] petList = pc.getPetList().values().toArray();
                /*
                 * if (petList.length > 2) { // 489：你無法一次控制那麼多寵物。
                 * pc.sendPackets(new S_ServerMessage(489)); return; }
                 */
                for (final Object pet : petList) {
                    petCost += ((L1NpcInstance) pet).getPetcost();
                }
                int charisma = pc.getCha();
                if (pc.isCrown()) { // 君主
                    charisma += 6;
                } else if (pc.isElf()) { // 
                    charisma += 12;
                } else if (pc.isWizard()) { // WIZ
                    charisma += 6;
                } else if (pc.isDarkelf()) { // DE
                    charisma += 6;
                } else if (pc.isDragonKnight()) { // 
                    charisma += 6;
                } else if (pc.isIllusionist()) { // 
                    charisma += 6;
                } else if (pc.isWarrior()) { // 
                    charisma += 6;
                }
                final L1Pet l1pet = PetReading.get().getTemplate(itemObjectId);
                if (l1pet != null) {
                    final int npcId = l1pet.get_npcid();
                    charisma -= petCost;
                    if ((npcId == 45313) || (npcId == 45710 // 虎男、真虎男
                    ) || (npcId == 45711) || (npcId == 45712)) { // 高麗幼犬、高麗犬
                        divisor = 12;
                    } else {
                        divisor = 6;
                    }
                    petCount = charisma / divisor;
                    // TODO 亮 一個玩家最多只能攜帶3只寵物
                    /*
                     * final int iii = petCost / 6; if (iii > 2) {
                     * System.out.println("iii > 2 :" + iii); //
                     * 489：你無法一次控制那麼多寵物。 pc.sendPackets(new
                     * S_ServerMessage(489)); // System.out.println("iii > 2");
                     * return; }
                     */
                    if (petCount <= 0) {
                        // 489：你無法一次控制那麼多寵物。
                        pc.sendPackets(new S_ServerMessage(489)); // 引取多。
                        return;
                    }
                    final L1Npc npcTemp = NpcTable.get().getTemplate(npcId);
                    final L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
                    pet.setPetcost(divisor);
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
