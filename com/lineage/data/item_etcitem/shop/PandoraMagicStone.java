package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 潘朵拉魔石
 */
public class PandoraMagicStone extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(PandoraMagicStone.class);
    private int _type;

    /**
     *
     */
    private PandoraMagicStone() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new PandoraMagicStone();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    // @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        if (!pc.getInventory().checkItem(41246, 100)) {
            pc.sendPackets(new S_SystemMessage("魔法結晶體(100)不足。"));
            return;
        }
        // 停止初始技能狀態
        if (pc.hasSkillEffect(L1SkillId.Pandora_Magic_Stone_1)) {
            pc.removeSkillEffect(L1SkillId.Pandora_Magic_Stone_1);
        }
        if (pc.hasSkillEffect(L1SkillId.Pandora_Magic_Stone_2)) {
            pc.removeSkillEffect(L1SkillId.Pandora_Magic_Stone_2);
        }
        if (pc.hasSkillEffect(L1SkillId.Pandora_Magic_Stone_3)) {
            pc.removeSkillEffect(L1SkillId.Pandora_Magic_Stone_3);
        }
        if (pc.hasSkillEffect(L1SkillId.Pandora_Magic_Stone_4)) {
            pc.removeSkillEffect(L1SkillId.Pandora_Magic_Stone_4);
        }
        int gfxids[] = {13160, 13161, 13162, 13163};
        final int type = _type;
        switch (type) {
            case 1: // 潘朵拉近戰魔石             使用時需消耗100個魔法結晶體    體力上限+50    近距離傷害+2     體力回復量+3    力量+1    不可轉移
                pc.setSkillEffect(L1SkillId.Pandora_Magic_Stone_1, 600 * 1000);
                pc.addMaxHp(50); // 體力上限+50
                pc.addDmgup(2); // 近距離傷害+2
                pc.addHpr(3); // 體力回復量+3
                pc.addStr(1); // 力量+1
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                if (pc.isInParty()) { // 組隊中更新血條
                    pc.getParty().updateMiniHP(pc);
                }
                pc.sendDetails(); // 能力更新
                pc.sendPackets(new S_OwnCharStatus2(pc)); // 能力更新
                pc.getInventory().consumeItem(41246, 100);
                pc.sendPackets(new S_SkillSound(pc.getId(), gfxids[0]));
                pc.broadcastPacketAll(new S_SkillSound(pc.getId(), gfxids[0]));
                break;
            case 2: // 潘朵拉遠攻魔石             使用時需消耗100個魔法結晶體    體力上限+25	    遠距離傷害+2    魔力上限+25	    體力回復量+1    魔力回復量+1    敏捷+1	    不可轉移
                pc.setSkillEffect(L1SkillId.Pandora_Magic_Stone_2, 600 * 1000);
                pc.addMaxHp(25); // 體力上限+25
                pc.addBowDmgup(2); // 遠距離傷害+2
                pc.addMaxMp(25); // 魔力上限+25
                pc.addHpr(1); // 體力回復量+1
                pc.addMpr(1); // 魔力回復量+1
                pc.addDex(1); // 敏捷+1
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                if (pc.isInParty()) { // 組隊中更新血條
                    pc.getParty().updateMiniHP(pc);
                }
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); // 魔力更新
                pc.sendDetails(); // 能力更新
                pc.sendPackets(new S_OwnCharStatus2(pc)); // 能力更新
                pc.getInventory().consumeItem(41246, 100);
                pc.sendPackets(new S_SkillSound(pc.getId(), gfxids[1]));
                pc.broadcastPacketAll(new S_SkillSound(pc.getId(), gfxids[1]));
                break;
            case 3: // 潘朵拉魔攻魔石             使用時需消耗100個魔法結晶體    魔力上限+50	    魔力回復量+3    智力+1    魔攻+2    不可轉移
                pc.setSkillEffect(L1SkillId.Pandora_Magic_Stone_3, 600 * 1000);
                pc.addMaxMp(50); // 魔力上限+50
                pc.addMpr(3); // 魔力回復量+3
                pc.addInt(1); // 智力+1
                pc.addSp(2); // 魔攻+2
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); // 魔力更新
                pc.sendDetails(); // 能力更新
                pc.sendPackets(new S_OwnCharStatus2(pc)); // 能力更新
                pc.sendPackets(new S_SPMR(pc)); // 魔攻魔防更新
                pc.getInventory().consumeItem(41246, 100);
                pc.sendPackets(new S_SkillSound(pc.getId(), gfxids[2]));
                pc.broadcastPacketAll(new S_SkillSound(pc.getId(), gfxids[2]));
                break;
            case 4: // 潘朵拉防禦魔石             使用時需消耗100個魔法結晶體    體力上限+30	    魔力上限+30	    額外防禦-5    魔法防禦+10    傷害減免+1    不可轉移
                pc.setSkillEffect(L1SkillId.Pandora_Magic_Stone_4, 600 * 1000);
                pc.addMaxHp(30); // 體力上限+30
                pc.addMaxMp(30); // 魔力上限+30
                pc.addAc(-5); // 額外防禦-5
                pc.addMr(10); // 魔法防禦+10
                pc.addDamageReductionByArmor(1); // 傷害減免+1
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                if (pc.isInParty()) { // 組隊中更新血條
                    pc.getParty().updateMiniHP(pc);
                }
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); // 魔力更新
                pc.sendPackets(new S_OwnCharStatus(pc)); // 防禦更新
                pc.sendPackets(new S_SPMR(pc)); // 魔攻魔防更新
                pc.getInventory().consumeItem(41246, 100);
                pc.sendPackets(new S_SkillSound(pc.getId(), gfxids[3]));
                pc.broadcastPacketAll(new S_SkillSound(pc.getId(), gfxids[3]));
                break;
        }
    }

    @Override
    public void set_set(final String[] set) {
        try {
            _type = Integer.parseInt(set[1]);
            //if (_type != 1 && _type != 2 && _type != 3 && _type != 4) {
            if (!(_type >= 1 && _type <= 4)) {
                _log.error("PandoraMagicStone 潘朵拉魔石設置錯誤：type不等於(1-4),使用預設type(1)");
                _type = 1;
            }
        } catch (Exception localException) {
        }
    }
	/*@Override
	public BinaryOutputStream itemStatus(L1ItemInstance item) {
		BinaryOutputStream os = new BinaryOutputStream();
		os.writeC(23);
		os.writeC(item.getItem().getMaterial());
		os.writeD(item.getWeight());
		switch (_type) {
		case 1:
			os.writeC(0x27);
			os.writeS("測試1");
			break;
		case 2:
			os.writeC(0x27);
			os.writeS("測試2");
			break;
		case 3:
			os.writeC(0x27);
			os.writeS("測試3");
			break;
		case 4:
			os.writeC(0x27);
			os.writeS("測試4");
			break;
		}
		return os;
	}*/
}