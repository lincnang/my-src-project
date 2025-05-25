package com.lineage.server.clientpackets;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemRestrictionsTable;
import com.lineage.server.datatables.PetTypeTable;
import com.lineage.server.datatables.sql.ServerGmCommandTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1PetType;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class C_GiveItem extends ClientBasePacket {
    @SuppressWarnings("unused")
    private static final Log _log = LogFactory.getLog(C_GiveItem.class);
    private static final String[] receivableImpls = {"L1Npc", "L1Monster", "L1Guardian", "L1Teleporter", "L1Guard"};

    public void start(byte[] decrypt, ClientExecutor client) {
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
            int targetId = readD();
            int x = readH();
            int y = readH();
            int itemObjId = readD();
            long count = readD();
            if (count > 2147483647L) {
                count = 2147483647L;
            }
            count = Math.max(0L, count);
            L1Object object = World.get().findObject(targetId);
            if ((object == null) || (!(object instanceof L1NpcInstance))) {
                return;
            }
            L1NpcInstance target = (L1NpcInstance) object;
            if (!isNpcItemReceivable(target.getNpcTemplate())) {
                return;
            }
            L1Inventory targetInv = target.getInventory();
            L1Inventory inv = pc.getInventory();
            L1ItemInstance item = inv.getItem(itemObjId);
            if (item == null) {
                return;
            }
            if (item.getCount() <= 0L) {
                return;
            }
            if ((item.getItemId() == 40308 || item.getItemId() == 44070) && pc.getLevel() < 35) {
                pc.sendPackets(new S_ServerMessage("預防作弊啟動，35級以下無法做此動作"));
                return;
            }
            if (item.getItemId() == 80033 && pc.getLevel() < ConfigOtherSet2.R_80033) {
                pc.sendPackets(new S_ServerMessage("預防作弊啟動，85級以下無法做此動作"));
                return;
            }
            if (item.getItemId() == 47011 && pc.getLevel() < 52) {
                return;
            }
            if (item.isEquipped()) {
                pc.sendPackets(new S_ServerMessage(141));
                return;
            }
            if (!pc.isGm() && !item.getItem().isTradable()) {
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                return;
            }
            if (item.getBless() >= 128) {
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                return;
            }
            if (item.get_time() != null) {
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                return;
            }
            if (!pc.isGm() && ItemRestrictionsTable.RESTRICTIONS.contains(Integer.valueOf(item.getItemId()))) {
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                return;
            }
            if (ServerGmCommandTable.tradeControl.contains(item.getId())) {// 限制轉移物品
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                return;
            }
            if ((!pc.isGm()) && (!ConfigOtherSet2.GIVE_ITEM_LIST.contains(Integer.valueOf(item.getItemId())))) {
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                return;
            }
            int pcx = pc.getX();
            int pcy = pc.getY();
            if ((Math.abs(pcx - x) >= 3) || (Math.abs(pcy - y) >= 3)) {
                pc.sendPackets(new S_ServerMessage(142));
                return;
            }
            // 寵物(已經召喚出來)
            for (final Object petObject : pc.getPetList().values()) {
                if (petObject instanceof L1PetInstance) {
                    final L1PetInstance pet = (L1PetInstance) petObject;
                    if (item.getId() == pet.getItemObjId()) {
                        // \f1%0%d是不可轉移的…
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        return;
                    }
                }
            }
            /** [原碼] 結婚系統 */
            if (item.getId() == pc.getQuest().get_step(L1PcQuest.QUEST_MARRY)) { // 以結婚過的戒指(無法給予)
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                return;
            }
            /** End */
            // 取回娃娃
            if (pc.getDoll(item.getId()) != null) {
                // 1,181：這個魔法娃娃目前正在使用中。
                pc.sendPackets(new S_ServerMessage(1181));
                return;
            }
            // 取回娃娃
            if (pc.getDoll2(item.getId()) != null) {
                // 1,181：這個魔法娃娃目前正在使用中。
                pc.sendPackets(new S_ServerMessage(1181));
                return;
            }
            // 取回娃娃
            if (pc.get_power_doll() != null) {
                if (pc.get_power_doll().getItemObjId() == item.getId()) {
                    // 1,181：這個魔法娃娃目前正在使用中。
                    pc.sendPackets(new S_ServerMessage(1181));
                    return;
                }
            }
            if (!pc.isGm() && targetInv.checkAddItem(item, count) != 0) {
                pc.sendPackets(new S_ServerMessage(942));
                return;
            }
            // 給予的物件
            final L1ItemInstance getItem = inv.tradeItem(item, count, targetInv);
            target.onGetItem(getItem);
            target.turnOnOffLight();
            pc.turnOnOffLight();
            L1PetType petType = PetTypeTable.getInstance().get(target.getNpcTemplate().get_npcId());
            if ((petType == null) || (target.isDead())) {
                return;
            }
            if (getItem.getItemId() == petType.getItemIdForTaming()) {
                tamePet(pc, target);
            }
            // 給予的對象 是寵物
            if (target instanceof L1PetInstance) {
                final L1PetInstance tgPet = (L1PetInstance) target;
                // pc.sendPackets(new S_PetInventory(tgPet, true));
                // 給予的道具是進化道具
                if ((getItem.getItemId() == petType.getEvolvItemId()) && petType.canEvolve()) {
                    this.evolvePet(pc, tgPet);
                }
            }
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    private boolean isNpcItemReceivable(L1Npc npc) {
        for (String impl : receivableImpls) {
            if (npc.getImpl().equals(impl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 馴服寵物
     *
     */
    private void tamePet(L1PcInstance pc, L1NpcInstance target) {
        if (((target instanceof L1PetInstance)) || ((target instanceof L1SummonInstance))) {
            return;
        }
        int petcost = 0;
        Object[] petList = pc.getPetList().values().toArray();
        /*
         * if (petList.length > 2) { pc.sendPackets(new S_ServerMessage(489));
         * return; }
         */
        for (Object pet : petList) {
            int nowpetcost = ((L1NpcInstance) pet).getPetcost();
            petcost += nowpetcost;
        }
        int charisma = pc.getCha();
        if (pc.isCrown()) {
            charisma += 6;
        } else if (pc.isElf()) {
            charisma += 12;
        } else if (pc.isWizard()) {
            charisma += 6;
        } else if (pc.isDarkelf()) {
            charisma += 6;
        } else if (pc.isDragonKnight()) {
            charisma += 6;
        } else if (pc.isIllusionist()) {
            charisma += 6;
        } else if (pc.isWarrior()) {
            charisma += 6;
        }
        charisma -= petcost;
        if (charisma <= 0) {
            pc.sendPackets(new S_ServerMessage(489));
            return;
        }
        L1PcInventory inv = pc.getInventory();
        if (inv.getSize() < 180) {
            if (isTamePet(target)) {
                L1ItemInstance petamu = inv.storeItem(40314, 1L);
                if (petamu != null) {
                    new L1PetInstance(target, pc, petamu.getId());
                    pc.sendPackets(new S_ItemName(petamu));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(324));
            }
        }
    }

    /**
     * 進化寵物的判斷
     *
     */
    private void evolvePet(L1PcInstance pc, L1PetInstance pet) {
        L1PcInventory inv = pc.getInventory();
        L1ItemInstance petamu = inv.getItem(pet.getItemObjId());
        pet.getInventory().consumeItem(40070, 1L);
        int level = 30;
        if ((pet.getLevel() >= level) && (pc == pet.getMaster()) && (petamu != null)) {
            L1ItemInstance highpetamu = inv.storeItem(40316, 1L);// 高等寵物項圈
            if (highpetamu != null) {
                pet.evolvePet(highpetamu.getId());
                pc.sendPackets(new S_ItemName(highpetamu));
                inv.removeItem(petamu, 1L);// 移除原寵物項圈
            }
        }
    }

    /**
     * 是否馴服成功
     *
     */
    private boolean isTamePet(L1NpcInstance npc) {
        boolean isSuccess = false;
        int npcId = npc.getNpcTemplate().get_npcId();
        if (npcId == 45313) {// 虎男
            Random random = new Random();
            if ((npc.getMaxHp() / 3 > npc.getCurrentHp()) && (random.nextInt(16) == 15)) {
                isSuccess = true;
            }
        } else if (npc.getMaxHp() / 3 > npc.getCurrentHp()) {
            isSuccess = true;
        }
        // 虎男、浣熊、高麗幼犬
        if (((npcId == 45313) || (npcId == 45044) || (npcId == 45711)) && (npc.isResurrect())) {// 復活過的
            isSuccess = false;
        }
        return isSuccess;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_GiveItem JD-Core Version: 0.6.2
 */