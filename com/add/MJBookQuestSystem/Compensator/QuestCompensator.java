package com.add.MJBookQuestSystem.Compensator;

import com.lineage.server.model.Instance.L1PcInstance;

import java.sql.ResultSet;

/**
 * 用於概括單個獎勵包的界面
 **/
public interface QuestCompensator {
    /**
     * 自動執行實例設置的方法
     **/
    public void set(ResultSet rs) throws Exception;

    /**
     * 返回記錄中的錯誤項
     **/
    public String getLastRecord();

    /**
     * 返回難度級別
     **/
    public int getDifficulty();

    /**
     * 執行獎勵
     **/
    public void compensate(L1PcInstance pc);
}
