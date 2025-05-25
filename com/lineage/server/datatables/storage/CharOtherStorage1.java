package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1PcOther1;

public interface CharOtherStorage1 {
  void load();
  
  L1PcOther1 getOther(L1PcInstance paramL1PcInstance);
  
  void storeOther(int paramInt, L1PcOther1 paramL1PcOther1);
  
  void tam();
}
