//package com.lineage.data.item_etcitem;
//
//import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;
//import static com.lineage.server.model.skill.L1SkillId.BRAVE_AURA;
//import static com.lineage.server.model.skill.L1SkillId.CLEAR_MIND;
//import static com.lineage.server.model.skill.L1SkillId.DOUBLE_BREAK;
//import static com.lineage.server.model.skill.L1SkillId.DRESS_EVASION;
//import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
//import static com.lineage.server.model.skill.L1SkillId.GLOWING_AURA;
//import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
//import static com.lineage.server.model.skill.L1SkillId.IRON_SKIN;
//import static com.lineage.server.model.skill.L1SkillId.RESIST_MAGIC;
//import static com.lineage.server.model.skill.L1SkillId.SHINING_AURA;
//import static com.lineage.server.model.skill.L1SkillId.SOUL_OF_FLAME;
//import static com.lineage.server.model.skill.L1SkillId.STORM_SHOT;
//import static com.lineage.server.model.skill.L1SkillId.UNCANNY_DODGE;
//
//import com.lineage.config.ConfigSkill;
//import com.lineage.data.executor.ItemExecutor;
//import com.lineage.server.model.Instance.L1ItemInstance;
//import com.lineage.server.model.Instance.L1PcInstance;
//import com.lineage.server.serverpackets.S_ItemName;
//import com.lineage.server.serverpackets.S_SystemMessage;
//
///**
// * 自動使用輔助物品
// * 
// * @author admin
// *
// */
//public class Auto_Item extends ItemExecutor {
//
//	/**
//	 *
//	 */
//	private Auto_Item() {
//		// TODO Auto-generated constructor stub
//	}
//
//	public static ItemExecutor get() {
//		return new Auto_Item();
//	}
//
//	/**
//	 * 道具物件執行
//	 * 
//	 * @param data
//	 *            參數
//	 * @param pc
//	 *            執行者
//	 * @param item
//	 *            物件
//	 */
//	@Override
//	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance l1iteminstance) {
//		// 自動使用藥水類
//		final int ItemAuto = _itemAuto;
//		switch (ItemAuto) {
//		case 0:// 自動古白藥水
//			if (!pc.isAutoHp()) {
//				pc.setAutoHp(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動古白藥水。"));
//				l1iteminstance.setNowAuto(true);
//				pc.setTemporary(4);
//				pc.sendPackets(new S_SystemMessage("當血量低於幾%時，要自動使用古白。"));
//			} else {
//				pc.stopSkillSound_autoHP();
//				pc.setAutoHp(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動古白藥水。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 1:// 自動兔甘藥水
//			if (!pc.isAutoHp1()) {
//				pc.startSkillSound_autoHP();
//				pc.setAutoHp1(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動兔甘藥水。"));
//				l1iteminstance.setNowAuto(true);
//				pc.setTemporary(5);
//				pc.sendPackets(new S_SystemMessage("當血量低於幾%時，要自動使用兔甘。"));
//			} else {
//				pc.stopSkillSound_autoHP();
//				pc.setAutoHp1(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動兔甘藥水。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 2:// 自動加速藥水
//			if (!pc.isAutoSpeed()) {
//				pc.setAutoSpeed(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動加速藥水。"));
//				l1iteminstance.setNowAuto(true);
//			} else {
//				pc.setAutoSpeed(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動加速藥水。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 3:// 自動名譽貨幣
//			if (!pc.isAutoSpeed1()) {
//				pc.setAutoSpeed1(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動名譽貨幣。"));
//				l1iteminstance.setNowAuto(true);
//			} else {
//				pc.setAutoSpeed1(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動名譽貨幣。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 4:// 自動通暢體魄
//			if (!pc.isAutoSkill()) {
//				pc.setAutoSkill(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動通暢體魄。"));
//				l1iteminstance.setNowAuto(true);
//			} else {
//				pc.setAutoSkill(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動通暢體魄。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 5:// 自動聖結界
//			if (!pc.isWizard() && ConfigSkill.AutoIsClass) {
//				pc.sendPackets(new S_SystemMessage("你的職業無法使用。"));
//				return;
//			}
//			if (!pc.isSkillMastery(IMMUNE_TO_HARM) && ConfigSkill.AutoIsMastery) {
//				pc.sendPackets(new S_SystemMessage("你沒有學過聖結界喔。"));
//				return;
//			}
//			if (!pc.isAutoSkill1()) {
//				pc.setAutoSkill1(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動聖結界。"));
//				l1iteminstance.setNowAuto(true);
//				com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, IMMUNE_TO_HARM);
//			} else {
//				pc.setAutoSkill1(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動聖結界。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 6:// 自動魂體轉換
//			if (!pc.isElf()) {
//				pc.sendPackets(new S_SystemMessage("你的職業無法使用。"));
//				return;
//			}
//			if (!pc.isSkillMastery(146)) { // 魂體轉換
//				pc.sendPackets(new S_SystemMessage("你沒有學過魂體轉換喔。"));
//				return;
//			}
//			if (!pc.isAutoSkill2()) {
//				pc.startSkillSound_autoMP();
//				pc.setAutoSkill2(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動魂體轉換。"));
//				l1iteminstance.setNowAuto(true);
//				pc.setTemporary(7);
//				pc.sendPackets(new S_SystemMessage("當魔量低於幾%時，要自動使用魂體轉換。"));
//			} else {
//				pc.stopSkillSound_autoMP();
//				pc.setAutoSkill2(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動魂體轉換。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 7:// 自動騎士魔法
//			if (!pc.isKnight()) {
//				pc.sendPackets(new S_SystemMessage("你的職業無法使用。"));
//				return;
//			}
//			if (!pc.isAutoSkill3()) {
//				pc.setAutoSkill3(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動騎士魔法。"));
//				l1iteminstance.setNowAuto(true);
//				pc.sendPackets(new S_SystemMessage("請自己先施放一遍技能，之後就會自己施放。"));
//			} else {
//				pc.setAutoSkill3(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動騎士魔法。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 8:// 自動暗閃破盔
//			if (!pc.isDarkelf()) {
//				pc.sendPackets(new S_SystemMessage("你的職業無法使用。"));
//				return;
//			}
//			if (!pc.isAutoSkill4()) {
//				pc.setAutoSkill4(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動暗閃破盔。"));
//				l1iteminstance.setNowAuto(true);
//				if (pc.getInventory().consumeItem(40321, 1)) { // 二級黑魔石
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, DOUBLE_BREAK);
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, UNCANNY_DODGE);
//				}
//			} else {
//				pc.setAutoSkill4(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動暗閃破盔。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 9:// 自動王族魔法
//			if (!pc.isCrown()) {
//				pc.sendPackets(new S_SystemMessage("你的職業無法使用。"));
//				return;
//			}
//			if (!pc.isAutoSkill5()) {
//				pc.setAutoSkill5(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動王族魔法。"));
//				l1iteminstance.setNowAuto(true);
//				if (pc.getInventory().consumeItem(70113, 1)) { // 技能之石
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, GLOWING_AURA);
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, SHINING_AURA);
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, BRAVE_AURA);
//				}
//			} else {
//				pc.setAutoSkill5(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動王族魔法。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 10:// 自動近戰魔法
//			if (!pc.isAutoSkill6()) {
//				pc.setAutoSkill6(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動近戰魔法。"));
//				l1iteminstance.setNowAuto(true);
//				if (pc.getInventory().consumeItem(70113, 1)) { // 技能之石
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, BLESS_WEAPON);
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, BERSERKERS);
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, IRON_SKIN);
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, ELEMENTAL_FIRE);
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, SOUL_OF_FLAME);
//				}
//			} else {
//				pc.setAutoSkill6(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動近戰魔法。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 11:// 自動遠戰魔法
//			if (!pc.isAutoSkill7()) {
//				pc.setAutoSkill7(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動遠戰魔法。"));
//				l1iteminstance.setNowAuto(true);
//				if (pc.getInventory().consumeItem(70113, 1)) { // 技能之石
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, DRESS_EVASION);
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, RESIST_MAGIC);
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, CLEAR_MIND);
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, STORM_SHOT);
//				}
//			} else {
//				pc.setAutoSkill7(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動近戰魔法。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 12:// 自動幻術師魔法
//			if (!pc.isIllusionist()) {
//				pc.sendPackets(new S_SystemMessage("你的職業無法使用。"));
//				return;
//			}
//			if (!pc.isAutoSkill8()) {
//				pc.setAutoSkill8(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動幻術師魔法。"));
//				l1iteminstance.setNowAuto(true);
//				if (pc.isSkillMastery(201)) { // 鏡像
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, 201);
//				}
//				if (pc.isSkillMastery(206)) { // 專注
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, 206);
//				}
//				if (pc.isSkillMastery(211)) { // 耐力
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, 211);
//				}
//				if (pc.isSkillMastery(216)) { // 洞察
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, 216);
//				}
//			} else {
//				pc.setAutoSkill8(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動幻術師魔法。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//
//		case 13:// 自動龍騎士魔法
//			if (!pc.isDragonKnight()) {
//				pc.sendPackets(new S_SystemMessage("你的職業無法使用。"));
//				return;
//			}
//			if (!pc.isAutoSkill9()) {
//				pc.setAutoSkill9(true);
//				pc.sendPackets(new S_SystemMessage("\\fW開啟自動龍騎士魔法。"));
//				l1iteminstance.setNowAuto(true);
//				if (pc.isSkillMastery(181)) { // 龍之護鎧
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, 181);
//				}
//				if (pc.isSkillMastery(182)) { // 燃燒擊砍
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, 182);
//				}
//				if (pc.isSkillMastery(191)) { // 致命身軀
//					com.lineage.item_etcitem.SkillScroll.DoMySkill1(pc, 191);
//				}
//			} else {
//				pc.setAutoSkill9(false);
//				pc.sendPackets(new S_SystemMessage("\\fW關閉自動龍騎士魔法。"));
//				l1iteminstance.setNowAuto(false);
//			}
//			break;
//		}
//		pc.sendPackets(new S_ItemName(l1iteminstance));
//	}
//
//	private int _itemAuto; // 自動使用藥水類
//
//	@Override
//	public void set_set(final String[] set) {
//		try {
//			_itemAuto = Integer.parseInt(set[1]);
//
//		} catch (final Exception e) {
//		}
//	}
//}
