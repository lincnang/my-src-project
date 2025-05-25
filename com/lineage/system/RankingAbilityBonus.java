package com.lineage.system;

import com.lineage.DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 排行榜能力獎勵查詢（根據資料庫名次區間範圍自動對應）
 */
public class RankingAbilityBonus {

    /**
     * 排行榜能力資料結構
     */
    public static class AbilityBonus {
        public int str;
        public int dex;
        public int con;
        public int wis;
        public int cha;
        public int intel;
        public int pvpDamageIncrease;
        public int pvpDamageReduction;

        public int hp;

        public int mp;
        public String titlePrefix; // 新增：自定義頭銜前綴
        public int _buff_iconid;
        public int _buff_stringid;
        public int _time_string;
    }

    /**
     * 傳入玩家名次，自動從資料庫抓出所屬區間的能力加成
     * @param rank 玩家名次
     * @return AbilityBonus 能力資料
     */
    public static AbilityBonus getBonusByRank(int rank) {
        AbilityBonus bonus = new AbilityBonus();
        final String sql = "SELECT * FROM 系統_排行榜能力 WHERE ? BETWEEN LEAST(最低名次, 最高名次) AND GREATEST(最低名次, 最高名次)";
        try (Connection conn = DatabaseFactory.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rank);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bonus.str = rs.getInt("力量");
                    bonus.dex = rs.getInt("敏捷");
                    bonus.con = rs.getInt("體質");
                    bonus.wis = rs.getInt("精神");
                    bonus.cha = rs.getInt("魅力");
                    bonus.intel = rs.getInt("智力");
                    bonus.pvpDamageIncrease = rs.getInt("PvP傷害提升");
                    bonus.pvpDamageReduction = rs.getInt("PvP傷害減免");
                    bonus.hp = rs.getInt("血量");
                    bonus.mp = rs.getInt("魔量");
                    bonus.titlePrefix = rs.getString("對話視窗符號"); // 自定義文字欄位
                    bonus._buff_iconid = rs.getInt("右上角圖示編號");
                    bonus._buff_stringid = rs.getInt("對應string");
                    bonus._time_string = rs.getInt("string時間");
                }
            }
        } catch (Exception e) {
            System.out.println("【ERROR】排行榜查詢錯誤：" + e.getMessage());
            e.printStackTrace();
        }
        return bonus;
    }
}