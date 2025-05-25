package com.add.MJBookQuestSystem.Compensator.Element;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 獎勵運行的介面
 **/
public interface CompensatorElement {
    /**
     * 執行獎勵
     **/
    public void compensate(L1PcInstance pc);
}
