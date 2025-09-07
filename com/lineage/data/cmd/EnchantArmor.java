package com.lineage.data.cmd;

// 匯入各類需要用到的工具、伺服器邏輯與設定

import com.lineage.config.ConfigWeaponArmor;
import com.lineage.managerUI.Eva;
import com.lineage.server.WriteLogTxt;
import com.lineage.config.ConfigRecord;
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

// 防具強化邏輯類別，繼承自 EnchantExecutor
public class EnchantArmor extends EnchantExecutor {
    private static final Log _log = LogFactory.getLog(EnchantArmor.class); // 記錄用

    // 強化失敗處理
    @Override
    public void failureEnchant(final L1PcInstance pc, final L1ItemInstance item) {
        String name = item.isIdentified() ? item.getLogName() : item.getName(); // 顯示名稱

        // 如果有保護效果
        if (item.get_hasProtect() != 0) {
            WriteLogTxt.Recording("保護裝備紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + item.getRecordName(item.getCount()) + ", (ObjectId: " + item.getId() + ")】 保護卷軸效果消失.");
            switch (item.get_hasProtect()) {
                case 1: // 歸零
                    item.setEnchantLevel(0);
                    updateEnchant(pc, item);
                    pc.sendPackets(new S_ServerMessage(name + " 因為保護效果，歸零了。"));
                    item.set_hasProtect(0);
                    pc.sendPackets(new S_ItemName(item));
                    server_lv.forIntensifyArmor(pc, item);
                    return;
                case 2: // 降階
                    item.setEnchantLevel(item.getEnchantLevel() - 1);
                    updateEnchant(pc, item);
                    pc.sendPackets(new S_ServerMessage(name + " 因為保護效果，降階了。"));
                    item.set_hasProtect(0);
                    pc.sendPackets(new S_ItemName(item));
                    server_lv.forIntensifyArmor(pc, item);
                    return;
                case 3: // 保留
                    pc.sendPackets(new S_ServerMessage(name + " 因為保護效果，保留了。"));
                    item.set_hasProtect(0);
                    pc.sendPackets(new S_ItemName(item));
                    return;
            }
        }

        // 沒有保護時就會直接損毀
        pc.sendPackets(new S_ServerMessage(164, name, "$252")); // 164 表示：強化失敗物品損毀
        if (ConfigRecord.LOGGING_BAN_ENCHANT) {
            LogEnchantReading.get().failureEnchant(pc, item);
        }
        GiveBack.addRecord(pc, item); // 加入贖回清單
        pc.getInventory().removeItem(item, item.getCount()); // 移除道具
        _log.info("人物:" + pc.getName() + "點爆物品" + item.getItem().getName() + " 物品OBJID:" + item.getId());
        WriteLogTxt.Recording("強化防具紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家【" + pc.getName() + "】的【" + item.getRecordName(item.getCount()) + ", (ObjId: " + item.getId() + ")】強化失敗.");
        Eva.LogEnchantAppend("強化防具失敗", pc.getName(), item.getRecordName(item.getCount()), item.getId());

    }

    @Override
    public void successEnchant(final L1PcInstance pc, final L1ItemInstance item, final int i) {
        StringBuilder s = new StringBuilder(); // 裝備名稱
        StringBuilder sa = new StringBuilder(); // 顯示訊息用語（提升/下降）
        StringBuilder sb = new StringBuilder(); // 顯示訊息用語（強化/非常強化）

        // 組出裝備名稱
        if (!item.isIdentified()) {
            s.append(item.getName());
        } else {
            if (item.getEnchantLevel() > 0) {
                s.append("+").append(item.getEnchantLevel()).append(" ").append(item.getName());
            } else if (item.getEnchantLevel() < 0) {
                s.append(item.getEnchantLevel()).append(" ").append(item.getName());
            } else {
                s.append(item.getName());
            }
        }

        // 有保護效果就移除它
        if (item.get_hasProtect() != 0) {
            updateEnchant(pc, item);
            item.set_hasProtect(0);
            pc.sendPackets(new S_ItemName(item));
        }

        // 根據強化結果組出不同的顯示字串
        switch (i) {
            case 0:
                pc.sendPackets(new S_ServerMessage(160, s.toString(), "$252", "$248")); // 沒變化
                return;
            case -1:
                sa.append("$246"); // 下降
                sb.append("$247"); // 強化
                break;
            case 1:
                sa.append("$252"); // 提升
                sb.append("$247");
                break;
            case 2:
            case 3:
                sa.append("$252"); // 提升
                sb.append("$248"); // 非常強化
                break;
        }

        // 顯示強化成功訊息
        pc.sendPackets(new S_ServerMessage(161, s.toString(), sa.toString(), sb.toString()));

        final int oldEnchantLvl = item.getEnchantLevel();
        final int newEnchantLvl = oldEnchantLvl + i;

        if (oldEnchantLvl != newEnchantLvl) {
            item.setEnchantLevel(newEnchantLvl);
            updateEnchant(pc, item);

            // 若超過公告門檻，進行廣播
            if (ConfigWeaponArmor.enchantoverTF && (newEnchantLvl - item.getItem().get_safeenchant()) >= ConfigWeaponArmor.enchantovertell2) {
                String msg = "公告: " + pc.getName() + " " + s.toString() + " " + sb.toString() + " " + sa.toString() + " $251";

                switch (ConfigWeaponArmor.enchantoverType) {
                    case 0: // 對話欄位
                        World.get().broadcastPacketToAll(new S_SystemMessage(msg));
                        break;
                    case 1: // 畫面中央
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(2, msg));
                        break;
                    case 2: // 同時顯示
                        World.get().broadcastPacketToAll(new S_SystemMessage(msg));
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(2, msg));
                        break;
                    default:
                        World.get().broadcastPacketToAll(new S_SystemMessage(msg));
                        break;
                }
            }
        }

        // 記錄成功強化
        WriteLogTxt.Recording("強化防具紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家【" + pc.getName() + "】的【" + item.getRecordName(item.getCount()) + ", (ObjId: " + item.getId() + ")】強化成功.");
        Eva.LogEnchantAppend("防具強化成功", pc.getName(), item.getRecordName(item.getCount()), item.getId());
        // 如果裝備著就套用加成
        if (item.isEquipped()) {
            final int use_type = item.getItem().getUseType();
            switch (use_type) {
                case 2: case 22: case 19: case 18: case 20: case 21: case 25: case 70:
                    pc.addAc(-i);
                    applyArmorEffects(pc, item, i);
                    break;

                case 23: case 24: case 37: case 40:
                    if (item.getItem().get_greater() != 3) {
                        item.greater(pc, true);
                    }
                    break;
            }

            pc.sendPackets(new S_OwnCharStatus(pc));
        }
    }

    // 更新裝備的強化等級（同步資料）
    private void updateEnchant(L1PcInstance pc, L1ItemInstance item) {
        pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
    }

    // 根據強化值套用能力影響（HP、MP、魔抗、攻擊力等）
    private void applyArmorEffects(L1PcInstance pc, L1ItemInstance item, int i) {
        boolean isSPMR = false;

        int mr = item.getMr();
        if (mr != 0) {
            pc.addMr(i * item.getItem().getInfluenceMr());
            isSPMR = true;
        }

        int sp = item.getItem().getInfluenceSp();
        if (sp != 0) {
            pc.addSp(i * sp);
            isSPMR = true;
        }

        if (isSPMR) {
            pc.sendPackets(new S_SPMR(pc)); // 魔抗 / 魔攻變更通知
        }

        int hp = item.getItem().getInfluenceHp();
        if (hp != 0) {
            pc.addMaxHp(i * hp);
            pc.sendPackets(new S_HPUpdate(pc));
        }

        int mp = item.getItem().getInfluenceMp();
        if (mp != 0) {
            pc.addMaxMp(i * mp);
            pc.sendPackets(new S_MPUpdate(pc));
        }

        int dmgR = item.getItem().getInfluenceDmgR();
        if (dmgR != 0) {
            pc.addDamageReductionByArmor(i * dmgR);
        }

        int hitDmg = item.getItem().getInfluenceHitAndDmg();
        if (hitDmg != 0) {
            pc.addHitup(i * hitDmg);
            pc.addDmgup(i * hitDmg);
        }

        int bowHitDmg = item.getItem().getInfluenceBowHitAndDmg();
        if (bowHitDmg != 0) {
            pc.addBowHitup(i * bowHitDmg);
            pc.addBowDmgup(i * bowHitDmg);
        }
    }
}
