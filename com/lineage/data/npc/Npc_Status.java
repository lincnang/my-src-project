package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.utils.CalcStat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 檢查個人狀態商人<BR>
 * 85029<BR>
 *
 * @author dexc
 */
public class Npc_Status extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Status.class);

    private Npc_Status() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_Status();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        try {
            //目前已吃萬靈藥
            String n0 = String.valueOf(pc.getElixirStats());
            //你的基本力量點數
            String n1 = String.valueOf(pc.getBaseStr());
            //你的基本敏捷點數
            String n2 = String.valueOf(pc.getBaseDex());
            //你的基本智力點數
            String n3 = String.valueOf(pc.getBaseInt());
            //你的基本魅力點數
            String n4 = String.valueOf(pc.getBaseCha());
            //你的基本體質點數
            String n5 = String.valueOf(pc.getBaseCon());
            //你的基本精神點數
            String n6 = String.valueOf(pc.getBaseWis());
            //額外增加力量點數
            String n7 = String.valueOf(pc.getStr() - pc.getBaseStr());
            //額外增加敏捷點數
            String n8 = String.valueOf(pc.getDex() - pc.getBaseDex());
            //額外增加智力點數
            String n9 = String.valueOf(pc.getInt() - pc.getBaseInt());
            //額外增加魅力點數
            String n10 = String.valueOf(pc.getCha() - pc.getBaseCha());
            //額外增加體質點數
            String n11 = String.valueOf(pc.getCon() - pc.getBaseCon());
            //額外增加精神點數
            String n12 = String.valueOf(pc.getWis() - pc.getBaseWis());
            //人物基本魔攻點數
            String n13 = String.valueOf(pc.getTrueSp());
            //額外增加魔攻點數
            String n14 = String.valueOf(pc.getSp() - pc.getTrueSp());
            //基本血量
            String n15 = String.valueOf(pc.getBaseMaxHp());
            //額外血量
            String n16 = String.valueOf(pc.getMaxHp() - pc.getBaseMaxHp());
            //基本魔量
            String n17 = String.valueOf(pc.getBaseMaxMp());
            //額外魔量
            String n18 = String.valueOf(pc.getMaxMp() - pc.getBaseMaxMp());
            //近距離命中加成
            String n19 = String.valueOf(pc.getHitup());
            //遠距離命中加成
            String n20 = String.valueOf(pc.getBowHitup());
            //近距離傷害加成
            String n21 = String.valueOf(pc.getDmgup());
            //遠距離傷害加成
            String n22 = String.valueOf(pc.getBowDmgup());
            //魔法命中加成
            String n23 = String.valueOf(pc.getOriginalMagicHit());
            //防禦加成
            String n24 = String.valueOf(CalcStat.calcAc(pc.getType(), pc.getLevel(), pc.getBaseDex()));
            final String[] info = new String[]{n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24};
            // 活動商人對話。
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_status_1", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String s, final long amount) {
        // 83057 羊角
        // 83058 惡魔羊鬃毛
    }
    //	private double pcDmgMode(final L1PcInstance pc,int type) {
    //		int dmg=0;
    //
    //		if (type==1) {// 近距離武器
    //
    ////			 ２５ 達成【 近距離打擊＋１　近距離命中＋１ 】
    ////			 ３５ 達成【 近距離打擊＋２　近距離命中＋２ 】
    ////			 ４５ 達成【 近距離打擊＋５　近距離命中＋５　近距離＋１％ 】
    //			if(pc.getBaseStr()>=45){
    //				dmg += 5;
    //			}else if(pc.getBaseStr()>=35){
    //				dmg += 2;
    //			}else if(pc.getBaseStr()>=25){
    //				dmg += 1;
    //			}
    //			dmg += pc.getDmgModifierByArmor();
    //
    //		} else if (type==2){// 遠距離武器
    //			if(pc.getBaseDex()>=45){
    //				dmg += 5;
    //			}else if(pc.getBaseDex()>=35){
    //				dmg += 2;
    //			}else if(pc.getBaseDex()>=25){
    //				dmg += 1;
    //			}
    //			dmg += pc.getBowDmgModifierByArmor();
    //		}
    //
    //		dmg += dmgUp(pc,type);// 料理增傷
    //
    //
    //		return dmg;
    //	}
    /**
     * 料裡增傷
     *
     * @return
     */
    //	private double dmgUp(final L1PcInstance pc,int type) {
    //		double dmg = 0.0D;
    //
    //		if (pc.getSkillEffect().size() <= 0) {
    //			return dmg;
    //		}
    //
    //		if (!pc.getSkillisEmpty()) {
    //			try {
    //				// 料理追加傷害(近距離武器)
    //				if (type==1) {
    //					for (final Integer key : pc.getSkillEffect()) {
    //						final Integer integer = L1AttackList.SKD1.get(key);
    //						if (integer != null) {
    //							dmg += integer;
    //						}
    //					}
    //
    //					// 料理追加傷害(遠距離武器)
    //				} else if (type==2){
    //					for (final Integer key : pc.getSkillEffect()) {
    //						final Integer integer = L1AttackList.SKD2.get(key);
    //						if (integer != null) {
    //							dmg += integer;
    //						}
    //					}
    //				}
    //
    //			} catch (ConcurrentModificationException localConcurrentModificationException) {
    //			} catch (Exception e) {
    //				_log.error(e.getLocalizedMessage(), e);
    //			}
    //		}
    //
    //		return dmg;
    //	}
    //	/**
    //	 * 力量及敏捷命中補正
    //	 *
    //	 * @return
    //	 */
    //	private int str_dex_Hit(final L1PcInstance pc,int type) {
    //		int hitRate = 0;
    //		int strValue=pc.getStr() - 1 >65?65:pc.getStr() - 1;
    //		int dexValue=pc.getDex() - 1 >65?65:pc.getStr() - 1;
    //		if(type==1){
    //
    //
    //		// 力量命中補正
    //		final Integer hitStr = L1AttackList.STRH.get(strValue);
    //		if (hitStr != null) {
    //			hitRate += hitStr;
    //
    //		} else {
    //			hitRate += 1;
    //		}
    //		}else if(type==2){
    //		// 敏捷命中補正
    //		final Integer hitDex = L1AttackList.DEXH.get(dexValue);
    //		if (hitDex != null) {
    //			hitRate += hitDex;
    //
    //		} else {
    //			hitRate += 1;
    //		}
    //		}
    //		return hitRate;
    //	}
    //	/**
    //	 * 料理追加命中
    //	 *
    //	 * @return
    //	 */
    //	private int hitUp(final L1PcInstance pc,int type) {
    //		int hitUp = 0;
    //		if (pc.getSkillEffect().size() <= 0) {
    //			return hitUp;
    //		}
    //
    //		if (!pc.getSkillisEmpty()) {
    //			try {
    //				if (type==1) {
    //					for (Integer key : pc.getSkillEffect()) {
    //						Integer integer = (Integer) L1AttackList.SKU1.get(key);
    //						if (integer != null) {
    //							hitUp += integer.intValue();
    //						}
    //					}
    //				} else if (type==2){
    //					for (Integer key : pc.getSkillEffect()) {
    //						Integer integer = (Integer) L1AttackList.SKU2.get(key);
    //						if (integer != null)
    //							hitUp += integer.intValue();
    //					}
    //				}
    //			} catch (ConcurrentModificationException localConcurrentModificationException) {
    //			} catch (Exception e) {
    //				_log.error(e.getLocalizedMessage(), e);
    //			}
    //		}
    //
    //		return hitUp;
    //	}
    //	private int calcPcHit(final L1PcInstance pc,int type) {
    //		int _hitRate=0;
    //
    //		_hitRate = pc.getLevel();
    //
    //		_hitRate += str_dex_Hit(pc,type);
    //
    //		L1ItemInstance _weapon = pc.getWeapon();
    //        //近戰
    //		if (type==1) {
    ////			 ２５ 達成【 近距離打擊＋１　近距離命中＋１ 】
    ////			 ３５ 達成【 近距離打擊＋２　近距離命中＋２ 】
    ////			 ４５ 達成【 近距離打擊＋５　近距離命中＋５　近距離＋１％ 】
    //			if(pc.getBaseStr()>=45){
    //				_hitRate += 5;
    //			}else if(pc.getBaseStr()>=35){
    //				_hitRate += 2;
    //			}else if(pc.getBaseStr()>=25){
    //				_hitRate += 1;
    //			}
    //
    //			_hitRate = ((int) (_hitRate
    //					+ _weapon.getItem().getHitModifier()
    //					+ _weapon.getHitByMagic()
    //					+ pc.getHitup()
    //					+ (_weapon.getEnchantLevel() - _weapon.get_durability()) * 0.6D));
    //			_hitRate += pc.getHitModifierByArmor();
    //		//遠攻
    //		} else if (type==2) {{
    ////			 ２５ 達成【 遠距離打擊＋１　遠距離命中＋１ 】
    ////			 ３５ 達成【 遠距離打擊＋２　遠距離命中＋２ 】
    ////			 ４５ 達成【 遠距離打擊＋５　遠距離命中＋５　遠距離＋１％ 】
    //			if(pc.getBaseDex()>=45){
    //				_hitRate += 5;
    //			}else if(pc.getBaseDex()>=35){
    //				_hitRate += 2;
    //			}else if(pc.getBaseDex()>=25){
    //				_hitRate += 1;
    //			}
    //			_hitRate = ((int) (_hitRate
    //					+ _weapon.getItem().getHitModifier()
    //					+ _weapon.getHitByMagic()
    //					+ pc.getBowHitup()
    //					+ (_weapon.getEnchantLevel() - _weapon.get_durability()) * 0.6D));
    //			_hitRate += pc.getBowHitModifierByArmor();
    //		}
    //
    //
    //		int weight240 = pc.getInventory().getWeight240();
    //		if (weight240 > 80) {
    //			if ((80 < weight240) && (120 >= weight240)) {
    //				_hitRate -= 1;
    //			} else if ((121 <= weight240) && (160 >= weight240)) {
    //				_hitRate -= 3;
    //			} else if ((161 <= weight240) && (200 >= weight240)) {
    //				_hitRate -= 5;
    //			}
    //		}
    //
    //		_hitRate += hitUp(pc,type);// 料理追加命中
    //
    //		if (pc.is_mazu()) {// 媽祖祝福攻擊命中+5
    //			_hitRate += 5;
    //		}
    //
    //
    //
    //		return _hitRate;
    //	}
    //
    //	/**
    //	 * 通知GM
    //	 */
    //	private void toGmMsg2(final L1PcInstance pc, final L1ItemInstance element) {
    //		try {
    //
    //			final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
    //			for (L1PcInstance tgpc : allPc) {
    //				if (tgpc.isGm()) {
    //					final StringBuilder topc = new StringBuilder();
    //					topc.append("人物:" + pc.getName() + " 兌換道具:" + element.getLogName());
    //					tgpc.sendPackets(new S_ServerMessage(166, topc.toString()));
    //				}
    //			}
    //
    //		} catch (final Exception e) {
    //			_log.error(e.getLocalizedMessage(), e);
    //		}
    //	}
}
