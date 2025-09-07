package com.lineage.data.cmd;

// 匯入各種需要使用的類別
import com.lineage.config.ConfigWeaponArmor;
import com.lineage.config.ConfigRecord;
import com.lineage.managerUI.Eva;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.*;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.GiveBack;
import com.lineage.server.datatables.lock.LogEnchantReading;
import william.server_lv;

// EnchantWeapon 繼承自 EnchantExecutor，負責武器強化的實作邏輯
public class EnchantWeapon extends EnchantExecutor {
    private static final Log _log = LogFactory.getLog(EnchantWeapon.class);

    /**
     * 強化失敗時的處理邏輯
     */
    public void failureEnchant(final L1PcInstance pc, final L1ItemInstance item) {
        // 取得物品名稱（已鑑定就顯示強化資訊）
        String name = item.isIdentified() ? item.getLogName() : item.getName();

        // 如果有保護效果（保護卷軸）
        if (item.get_hasProtect() != 0) {
            logProtect(pc, item);
            switch (item.get_hasProtect()) {
                case 1:
                    // 保護效果1：歸零（不消失）
                    item.setEnchantLevel(0);
                    updateEnchant(pc, item);
                    pc.sendPackets(new S_ServerMessage(name + " 因為保護效果，歸零了。"));
                    item.set_hasProtect(0); // 清除保護狀態
                    pc.sendPackets(new S_ItemName(item)); // 更新物品名稱
                    server_lv.forIntensifyArmor(pc, item); // 裝備狀態更新
                    return;
                case 2:
                    // 保護效果2：降階
                    item.setEnchantLevel(item.getEnchantLevel() - 1);
                    updateEnchant(pc, item);
                    pc.sendPackets(new S_ServerMessage(name + " 因為保護效果，降階了。"));
                    item.set_hasProtect(0);
                    pc.sendPackets(new S_ItemName(item));
                    server_lv.forIntensifyArmor(pc, item);
                    return;
                case 3:
                    // 保護效果3：保留（不變）
                    pc.sendPackets(new S_ServerMessage(name + " 因為保護效果，保留了。"));
                    item.set_hasProtect(0);
                    pc.sendPackets(new S_ItemName(item));
                    return;
            }
        }

        // 沒有保護效果，直接失敗
        pc.sendPackets(new S_ServerMessage(164, name, "$252")); // 顯示：強化失敗訊息
        if (ConfigRecord.LOGGING_BAN_ENCHANT) {
            LogEnchantReading.get().failureEnchant(pc, item);
        }
        GiveBack.addRecord(pc, item); // 加入可贖回物品記錄
        pc.getInventory().removeItem(item, item.getCount()); // 移除該裝備

        // 寫入日誌
        Eva.LogEnchantAppend("點爆物品(武器)", pc.getName(), item.getItemNameEva(item.getCount()), item.getId());
        WriteLogTxt.Recording("點爆武器紀錄", "人物:【" + pc.getName() + "】點爆<武器>【+" + item.getEnchantLevel() + item.getItem().getName() + "】 物品OBJID:" + item.getId());
    }

    /**
     * 強化成功時的處理邏輯
     */
    @Override
    public void successEnchant(final L1PcInstance pc, final L1ItemInstance item, final int i) {
        // 取得顯示用道具名稱（已鑑定就含 +強化值）
        final String name = item.isIdentified() ? item.getLogName() : item.getName();

        // 移除保護效果（若存在）
        if (item.get_hasProtect() != 0) {
            updateEnchant(pc, item);
            item.set_hasProtect(0);
            pc.sendPackets(new S_ItemName(item));
        }

        // 處理強化狀態文字
        final String sa;
        final String sb;
        switch (i) {
            case 0: // 沒變化
                pc.sendPackets(new S_ServerMessage(160, name, "$252", "$248"));
                return;
            case -1:
                sa = "$246"; // 下降
                sb = "$247"; // 強化
                break;
            case 1:
                sa = "$245"; // 提升
                sb = "$247";
                break;
            case 2:
            case 3:
                sa = "$245";
                sb = "$248"; // 非常強化
                break;
            default:
                sa = "";
                sb = "";
                break;
        }

        // 發送成功訊息
        pc.sendPackets(new S_ServerMessage(161, name, sa, sb));

        final int oldLevel = item.getEnchantLevel();
        final int newLevel = oldLevel + i;

        // 只有等級改變時才處理
        if (oldLevel != newLevel) {

            // 達到公告門檻才廣播
            if (ConfigWeaponArmor.enchantoverTF &&
                    (newLevel - item.getItem().get_safeenchant()) >= ConfigWeaponArmor.enchantovertell1) {

                // 取得正確顯示名稱（包含 + 數字）
                final String itemNameFinal = item.getEnchantLevel() > 0
                        ? "+" + newLevel + " " + item.getName()
                        : item.getName();
                pc.sendPackets((ServerBasePacket) new S_PacketBoxGree(18));
                final String message = "\\fY【公告】\\fC 玩家 \\fR" + pc.getName() + "\\fC 強化了 \\fB" + itemNameFinal + " \\fP" + sb + "  " + sa;

                // 根據設定執行廣播方式
                switch (ConfigWeaponArmor.enchantoverType) {
                    case 0: // 對話欄位
                        World.get().broadcastPacketToAll(new S_SystemMessage(message));
                        break;

                    case 1: // 畫面中央
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(2, message));
                        break;

                    case 2: // 同時顯示
                        World.get().broadcastPacketToAll(new S_SystemMessage(message));
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(2, message));
                        break;

                    default: // 安全預設
                        World.get().broadcastPacketToAll(new S_SystemMessage(message));
                        break;
                }
            }

            // 更新裝備強化值
            item.setEnchantLevel(newLevel);
            updateEnchant(pc, item);

            // 寫入強化成功日誌
            WriteLogTxt.Recording("強化武器紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家【" + pc.getName() + "】的【" + item.getRecordName(item.getCount()) + ", (ObjId: " + item.getId() + ")】強化成功.");
            Eva.LogEnchantAppend("強化成功(武器)", pc.getName(), item.getRecordName(item.getCount()), item.getId());
            // 套用能力加成
            applyEffect(pc, item, i);
        }
    }


    /**
     * 更新角色背包中裝備的強化資訊
     */
    private void updateEnchant(L1PcInstance pc, L1ItemInstance item) {
        pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
    }

    /**
     * 強化成功後套用能力加成（只有穿裝備時才會觸發）
     */
    private void applyEffect(L1PcInstance pc, L1ItemInstance item, int i) {
        if (!item.isEquipped()) return;

        // 魔抗加成
        if (item.getMr() != 0)
            pc.addMr(i * item.getItem().getInfluenceMr());

        // 魔攻加成
        if (item.getItem().getInfluenceSp() != 0)
            pc.addSp(i * item.getItem().getInfluenceSp());

        // 更新魔攻 / 魔抗 UI
        if (item.getMr() != 0 || item.getItem().getInfluenceSp() != 0)
            pc.sendPackets(new S_SPMR(pc));

        // HP 上限加成
        if (item.getItem().getInfluenceHp() != 0) {
            pc.addMaxHp(i * item.getItem().getInfluenceHp());
            pc.sendPackets(new S_HPUpdate(pc));
        }

        // MP 上限加成
        if (item.getItem().getInfluenceMp() != 0) {
            pc.addMaxMp(i * item.getItem().getInfluenceMp());
            pc.sendPackets(new S_MPUpdate(pc));
        }

        // 傷害減免加成
        if (item.getItem().getInfluenceDmgR() != 0)
            pc.addDamageReductionByArmor(i * item.getItem().getInfluenceDmgR());

        // 命中與攻擊加成
        if (item.getItem().getInfluenceHitAndDmg() != 0) {
            pc.addHitup(i * item.getItem().getInfluenceHitAndDmg());
            pc.addDmgup(i * item.getItem().getInfluenceHitAndDmg());
        }

        // 弓命中與傷害加成
        if (item.getItem().getInfluenceBowHitAndDmg() != 0) {
            pc.addBowHitup(i * item.getItem().getInfluenceBowHitAndDmg());
            pc.addBowDmgup(i * item.getItem().getInfluenceBowHitAndDmg());
        }
    }

    /**
     * 保護卷觸發時記錄日誌
     */
    private void logProtect(L1PcInstance pc, L1ItemInstance item) {
        WriteLogTxt.Recording("保護裝備紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的【" + item.getRecordName(item.getCount()) + ", (ObjectId: " + item.getId() + ")】 保護卷軸效果消失.");
    }
}
