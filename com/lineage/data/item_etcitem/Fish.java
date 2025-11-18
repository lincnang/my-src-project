package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_YouFeelBetter;

import java.util.Random;

public class Fish extends ItemExecutor {
    public static ItemExecutor get() {
        return new Fish();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        Random random = new Random();
        int getItemId = 0;
        int getCount = 0;
        switch (itemId) {
            case 41298:
                UseHeallingPotion(pc, 4, 189);
                break;
            case 41299:
                UseHeallingPotion(pc, 15, 194);
                break;
            case 41300:
                UseHeallingPotion(pc, 35, 197);
                break;
            case 41301:
                int chance1 = random.nextInt(10);
                if ((chance1 >= 0) && (chance1 < 5)) {
                    UseHeallingPotion(pc, 15, 189);
                } else if ((chance1 >= 5) && (chance1 < 9)) {
                    getItemId = 40019;
                    getCount = 1;
                } else if (chance1 >= 9) {
                    int gemChance = random.nextInt(3);
                    if (gemChance == 0) {
                        getItemId = 40045;
                        getCount = 1;
                    } else if (gemChance == 1) {
                        getItemId = 40049;
                        getCount = 1;
                    } else if (gemChance == 2) {
                        getItemId = 40053;
                        getCount = 1;
                    }
                }
                break;
            case 41302:
                int chance2 = random.nextInt(3);
                if ((chance2 >= 0) && (chance2 < 5)) {
                    UseHeallingPotion(pc, 15, 189);
                } else if ((chance2 >= 5) && (chance2 < 9)) {
                    getItemId = 40018;
                    getCount = 1;
                } else if (chance2 >= 9) {
                    int gemChance = random.nextInt(3);
                    if (gemChance == 0) {
                        getItemId = 40047;
                        getCount = 1;
                    } else if (gemChance == 1) {
                        getItemId = 40051;
                        getCount = 1;
                    } else if (gemChance == 2) {
                        getItemId = 40055;
                        getCount = 1;
                    }
                }
                break;
            case 41303:
                int chance3 = random.nextInt(3);
                if ((chance3 >= 0) && (chance3 < 5)) {
                    UseHeallingPotion(pc, 15, 189);
                } else if ((chance3 >= 5) && (chance3 < 9)) {
                    getItemId = 40015;
                    getCount = 1;
                } else if (chance3 >= 9) {
                    int gemChance = random.nextInt(3);
                    if (gemChance == 0) {
                        getItemId = 40046;
                        getCount = 1;
                    } else if (gemChance == 1) {
                        getItemId = 40050;
                        getCount = 1;
                    } else if (gemChance == 2) {
                        getItemId = 40054;
                        getCount = 1;
                    }
                }
                break;
            case 41304:
                int chance = random.nextInt(3);
                if ((chance >= 0) && (chance < 5)) {
                    UseHeallingPotion(pc, 15, 189);
                } else if ((chance >= 5) && (chance < 9)) {
                    getItemId = 40021;
                    getCount = 1;
                } else if (chance >= 9) {
                    int gemChance = random.nextInt(3);
                    if (gemChance == 0) {
                        getItemId = 40044;
                        getCount = 1;
                    } else if (gemChance == 1) {
                        getItemId = 40048;
                        getCount = 1;
                    } else if (gemChance == 2) {
                        getItemId = 40052;
                        getCount = 1;
                    }
                }
                break;
        }
        pc.getInventory().removeItem(item, 1L);
        if (getCount != 0) {
            CreateNewItem.createNewItem(pc, getItemId, getCount);
        }
    }

    private void UseHeallingPotion(L1PcInstance pc, int healHp, int gfxid) {
        Random _random = new Random();
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }
        L1BuffUtil.cancelAbsoluteBarrier(pc);
        pc.sendPacketsX8(new S_SkillSound(pc.getId(), gfxid));
        healHp = (int) (healHp * (_random.nextGaussian() / 5.0D + 1.0D));
        if (pc.get_up_hp_potion() > 0) {
            healHp += healHp * pc.get_up_hp_potion() / 100;
            healHp += pc.get_up_hp_potion();
        }
        if (pc.hasSkillEffect(173)) {
            healHp >>= 1;
        }
        if (pc.hasSkillEffect(4012)) {
            healHp >>= 1;
        }
        if (pc.hasSkillEffect(4011)) {
            healHp *= -1;
        }
        if (healHp > 0) {
            pc.sendPackets(new S_YouFeelBetter());
        }
        pc.setCurrentHp(pc.getCurrentHp() + healHp);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Fish JD-Core Version: 0.6.2
 */