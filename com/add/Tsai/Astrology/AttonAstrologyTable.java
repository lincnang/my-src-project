package com.add.Tsai.Astrology;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 單例：載入並管理「守護星盤_阿頓」資料
 */
public class AttonAstrologyTable {
	private static final Log _log = LogFactory.getLog(AttonAstrologyTable.class);
	private static final Map<Integer, AttonAstrologyData> _attonIndex = new HashMap<>();
	private static AttonAstrologyTable _instance;

	private AttonAstrologyTable() {}

	public static AttonAstrologyTable get() {
		if (_instance == null) {
			_init();
		}
		return _instance;
	}

	private static synchronized void _init() {
		if (_instance == null) {
			_instance = new AttonAstrologyTable();
		}
	}

	public void load() {
		PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		try {
			_attonIndex.clear();
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `守護星盤_阿頓`");
			rs = ps.executeQuery();
			while (rs.next()) {
				AttonAstrologyData data = new AttonAstrologyData(
						rs.getInt("按鈕排序"),
						rs.getString("備註"),
						rs.getInt("前置編號"),
						rs.getInt("任務編號"),
						rs.getInt("牌組數"),
						rs.getInt("技能編號"),
						rs.getInt("未完成Img圖檔編號"),
						rs.getInt("完成Img圖檔編號"),
						rs.getString("需求道具編號"),
						rs.getString("需求道具數量"),
						rs.getInt("AddStr"),
						rs.getInt("AddDex"),
						rs.getInt("AddCon"),
						rs.getInt("AddInt"),
						rs.getInt("AddWis"),
						rs.getInt("AddCha"),
						rs.getInt("AddAc"),
						rs.getInt("AddSp"),
						rs.getInt("AddHp"),
						rs.getInt("AddMp"),
						rs.getInt("近距離攻擊"),
						rs.getInt("遠距離攻擊"),
						rs.getInt("近距離命中"),
						rs.getInt("遠距離命中"),
						rs.getInt("物理傷害減免"),
						rs.getInt("魔法傷害減免"),
						rs.getInt("負重上限"),
						rs.getInt("昏迷等級"),
						rs.getInt("昏迷命中"),
						rs.getInt("魔法傷害"),
						rs.getInt("PvP傷害提升"),
						rs.getInt("PvE傷害減免"),
						rs.getInt("機率"),
						rs.getInt("機率減免傷害"),
						rs.getInt("回血機率"),
						rs.getInt("回血量"),
						rs.getInt("gfxid1"),
						rs.getInt("gfxid2")
				);
				_attonIndex.put(data.getButtonOrder(), data);
				count++;
			}
			_log.info("讀取->[守護星盤_阿頓]: " + count + "(" + timer.get() + "ms)");
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, ps, cn);
		}
	}

	public static void effectBuff(L1PcInstance pc, AttonAstrologyData value, int negative) {
		if (negative == 0 || value == null) return;
		pc.addStr(value.getAddStr() * negative);
		pc.addDex(value.getAddDex() * negative);
		pc.addCon(value.getAddCon() * negative);
		pc.addInt(value.getAddInt() * negative);
		pc.addWis(value.getAddWis() * negative);
		pc.addCha(value.getAddCha() * negative);
		pc.addAc(value.getAddAc() * negative);
		pc.addSp(value.getAddSp() * negative);
		pc.addMaxHp(value.getAddHp() * negative);
		pc.addMaxMp(value.getAddMp() * negative);
		pc.addDmgup(value.getAddMeleeDmg() * negative);
		pc.addBowDmgup(value.getAddMissileDmg() * negative);
		pc.addHitup(value.getAddMeleeHit() * negative);
		pc.addBowHit(value.getAddMissileHit() * negative);
		pc.addDamageReductionByArmor(value.getAddDmgReduction() * negative);
		pc.addMagicDmgReduction(value.getAddMagicDmgReduction() * negative);
		pc.addWeightReduction(value.getAddWeightLimit() * negative);
		pc.addStunLevel(value.getStunLevel() * negative);
		// FIXME: 昏迷命中 目前沒有對應API，若有請改成 addStunHit
		pc.addMagicDmgModifier(value.getAddMagicDmg() * negative);

		// PvP 傷害提升（生效）
		if (value.getPvpDamageUp() != 0) {
			pc.setPvpDmg(value.getPvpDamageUp() * negative);
		}
		// PvE 傷害減免（保留並新增對應）：採用現有 PVE 減傷欄位
		if (value.getPveDmgReduction() != 0) {
			pc.addDamageReductionPVE(value.getPveDmgReduction() * negative); // L1PcInstance 已有 _DamageReductionPVE
		}

		// 阿頓星盤 隨機減傷參數注入
		if (negative > 0) {
			if (value.getProcChance() > 0) pc.setAttonProcChance(value.getProcChance());
			if (value.getProcReduce() > 0) pc.setAttonProcReduce(value.getProcReduce());
			if (value.getLeechChance() > 0) pc.setLeechChance(value.getLeechChance());
			if (value.getLeechAmount() > 0) pc.setLeechAmount(value.getLeechAmount());
			if (value.getGfxid1() > 0) pc.setLeechGfx1(value.getGfxid1());
			if (value.getGfxid2() > 0) pc.setLeechGfx2(value.getGfxid2());
		} else {
			// 移除效果時歸零（若有需要可以改成疊加式）
			if (value.getProcChance() > 0) pc.setAttonProcChance(0);
			if (value.getProcReduce() > 0) pc.setAttonProcReduce(0);
			if (value.getLeechChance() > 0) pc.setLeechChance(0);
			if (value.getLeechAmount() > 0) pc.setLeechAmount(0);
			if (value.getGfxid1() > 0) pc.setLeechGfx1(0);
			if (value.getGfxid2() > 0) pc.setLeechGfx2(0);
		}

		pc.sendPackets(new S_OwnCharStatus(pc));
		pc.sendPackets(new S_SPMR(pc));
		pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
		pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		if (pc.isInParty()) pc.getParty().updateMiniHP(pc);
	}

	public AttonAstrologyData getData(int buttonOrder) { return _attonIndex.get(buttonOrder); }
	public Integer[] getIndexArray() { return _attonIndex.keySet().toArray(new Integer[0]); }
	public int size() { return _attonIndex.size(); }
}
