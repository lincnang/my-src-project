package com.lineage.data.item_weapon.proficiency;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 玩家武器熟練度
 * @author 聖子默默
 */
public class L1WeaponProficiency {

    private final L1PcInstance _owner;
    private HashMap<Integer, PlayerWeaponProficiency> _weaponProficiency;

    public L1WeaponProficiency(L1PcInstance owner) {
        _owner = owner;
    }

    public L1PcInstance getOwner() {
        return _owner;
    }

    public void loadProficiency() {
        _weaponProficiency = CharacterWeaponProficiencyTable.getInstance().getProficiency(_owner.getId());
        if (_weaponProficiency == null) {
            _weaponProficiency = new HashMap<>();
        }
    }

    public static boolean Cmd(L1PcInstance pc, String cmd) {
        if (cmd.equalsIgnoreCase("die_weapon01")) {
            if (pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage("請先裝備武器"));
                return true;
            }

            int type = pc.getWeapon().getItem().getType();
            String jumpCmd;

            switch (type) {
                case 1: jumpCmd = "die_J_1"; break;
                case 2: jumpCmd = "die_J_2"; break;
                case 3: jumpCmd = "die_J_3"; break;
                case 4: jumpCmd = "die_J_4"; break;
                case 5: jumpCmd = "die_J_5"; break;
                case 6: jumpCmd = "die_J_6"; break;
                case 7: jumpCmd = "die_J_7"; break;
                case 8: jumpCmd = "die_J_8"; break;
                case 9: jumpCmd = "die_J_10"; break;
                case 10: jumpCmd = "die_J_11"; break;
                case 11: jumpCmd = "die_J_12"; break;
                default:
                    pc.sendPackets(new S_SystemMessage("尚未支援的武器類型: " + type));
                    return true;
            }

            Cmd(pc, jumpCmd); // 執行跳轉
            return true;
        }

        // 個別武器類型對應
        if (cmd.equalsIgnoreCase("die_J_1")) return showWeaponInfo(pc, 1);  // 單手劍
        if (cmd.equalsIgnoreCase("die_J_2")) return showWeaponInfo(pc, 2);
        if (cmd.equalsIgnoreCase("die_J_3")) return showWeaponInfo(pc, 3);
        if (cmd.equalsIgnoreCase("die_J_4")) return showWeaponInfo(pc, 4);
        if (cmd.equalsIgnoreCase("die_J_5")) return showWeaponInfo(pc, 5);
        if (cmd.equalsIgnoreCase("die_J_6")) return showWeaponInfo(pc, 6);
        if (cmd.equalsIgnoreCase("die_J_7")) return showWeaponInfo(pc, 7);
        if (cmd.equalsIgnoreCase("die_J_8")) return showWeaponInfo(pc, 8);
        if (cmd.equalsIgnoreCase("die_J_9")) return showWeaponInfo(pc, 9);
        if (cmd.equalsIgnoreCase("die_J_10")) return showWeaponInfo(pc, 10);
        if (cmd.equalsIgnoreCase("die_J_11")) return showWeaponInfo(pc, 11);

        return false;
    }


    private static boolean showWeaponInfo(L1PcInstance pc, int type) {
        L1WeaponProficiency prof = new L1WeaponProficiency(pc);
        prof.loadProficiency();

        PlayerWeaponProficiency proficiency = prof._weaponProficiency.get(type);
        if (proficiency == null) {
            proficiency = new PlayerWeaponProficiency();
            proficiency.setCharId(pc.getId());
            proficiency.setType(type);
            proficiency.setLevel(0);
            proficiency.setExp(0);
            CharacterWeaponProficiencyTable.getInstance().updateProficiency(pc.getId(), type, proficiency);
        }

        String[] datas = L1WeaponProficiency.weaponPowerInfos(proficiency);
        String htmlName = "die_J_1" ;
        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), htmlName, datas));
        return true;
    }



    public static String[] weaponPowerInfos(PlayerWeaponProficiency show) {
        int type = show.getType();       // 武器類型
        int level = show.getLevel();     // 當前熟練度等級
        int totalExp = show.getExp();    // 玩家目前的總熟練度經驗

        // 取得目前等級與下一階段的熟練度資料
        WeaponProficiency currLv = WeaponProficiencyTable.getProficiency(type, level);
        WeaponProficiency nextLv = WeaponProficiencyTable.getProficiency(type, level + 1);

        // 處理滿等情況
        if (nextLv == null) {
            return new String[]{
                    String.valueOf(level),     // %0 等級
                    "100.0000%",               // %1 目前經驗
                    "0",                       // %2 HP 加成
                    "0",                       // %3 MP 加成
                    "100.0000%",               // %4 升級經驗（固定）
                    "0.0000%",                 // %5 距離升級經驗（已無）
                    "100.0000%",               // %6 經驗完成度
                    getWeaponTypeName(type),   // %7 武器名稱
                    ""                         // %8 保留
            };
        }

        // 當前等級起始經驗
        int currExp = (currLv != null) ? currLv.getExp() : 0;
        int nextExp = nextLv.getExp();
        int expInLevel = totalExp - currExp;
        int expToNextLevel = nextExp - currExp;

        // 百分比進度計算
        double progress = (double) expInLevel / expToNextLevel;
        progress = Math.min(progress, 1.0); // 避免超過100%

        // 格式化為 00.0000%
        String expPercent = String.format("%07.4f%%", progress * 100);
        String remainPercent = String.format("%07.4f%%", (1.0 - progress) * 100);

        return new String[]{
                String.valueOf(level),              // %0 目前等級
                expPercent,                         // %1 目前經驗（百分比）
                String.valueOf(nextLv.getHP()),     // %2 下一階段 HP 加成
                String.valueOf(nextLv.getMp()),     // %3 下一階段 MP 加成
                "",                                 // %4 升級經驗（固定為百分比）
                remainPercent,                      // %5 距離升級經驗（百分比）
                expPercent,                         // %6 經驗完成度（重複用）
                getWeaponTypeName(type),            // %7 武器類型名稱
                ""                                  // %8 保留
        };
    }

    // 🔧 輔助方法：取得武器名稱
    private static String getWeaponTypeName(int type) {
        switch (normalizeWeaponType(type)) {
            case 1: return "單手劍";
            case 2: return "匕首";
            case 3: return "雙手劍";
            case 4: return "弓";
            case 5: return "單手矛";
            case 6: return "斧";
            case 7: return "魔仗";
            case 8: return "鋼爪";
            case 9: return "雙刀";
            case 10: return "奇古獸";
            case 11: return "鎖鏈劍";
            default: return "未知";
        }
    }



    /**
     * 設定武器熟練度等級(沒有資料的情況下新增)
     *
     * @param type 武器類型
     */
    public void setProficiencyType(int type) {
        PlayerWeaponProficiency proficiency = _weaponProficiency.get(type);
        if (proficiency != null) {
            WeaponProficiency.addWeaponProficiencyStatus(_owner, proficiency.getType(), proficiency.getLevel(), 1);
            return;
        }
        proficiency = new PlayerWeaponProficiency();
        proficiency.setType(type);
        proficiency.setCharId(_owner.getId());
        proficiency.setLevel(0);
        proficiency.setExp(0);
        CharacterWeaponProficiencyTable.getInstance().storeProficiency(_owner.getId(), type, proficiency);
        _weaponProficiency.put(type, proficiency); // ←**多補這一行，避免map永遠查不到**
    }


    /**
     * 移除武器熟練度等級加成
     *
     * @param type 武器類型
     */
    public void removeProficiency(int type) {
        PlayerWeaponProficiency proficiency = _weaponProficiency.get(type);
        if (proficiency != null) {
            //_owner.sendPackets(new S_SystemMessage("當前武器類型熟練度等級為" + proficiency.getLevel() + " 熟練度經驗為" + proficiency.getExp()));
            WeaponProficiency.addWeaponProficiencyStatus(_owner, proficiency.getType(), proficiency.getLevel(), -1);
        }
    }

    /**
     * 熟練度經驗增加
     * 請自行設定經驗值獲取方式
     *
     * @param exp 增加的经验值
     */
    /**
     * 增加熟練度經驗值
     * @param exp 增加的熟練度
     */
    public void addProficiencyExp(int exp) {
        if (_owner.getWeapon() == null) {
            return;
        }
//       System.out.println("裝備武器type: " + _owner.getWeapon().getItem().getType());
        int originalType = _owner.getWeapon().getItem().getType();
        int type = normalizeWeaponType(originalType); //   核心修改：轉換武器類型

        //  檢查是否有熟練度資料
        if (WeaponProficiencyTable.getProficiency(type, 1) == null) {
            _owner.sendPackets(new S_SystemMessage("該武器尚未設定熟練度資料，請通知管理員"));
            return;
        }

        int maxLv = WeaponProficiencyTable.getMaxProficienciesLevel(type);
        if (maxLv == 0) {
            _owner.sendPackets(new S_SystemMessage("該武器熟練度最大等級為 0，請檢查資料庫設定"));
            return;
        }

        if (isProficiencyEnd(type)) {
            return;
        }

        addProficiencyExp(type, exp);
        onChangeProficiencyExp(_owner, type, getProficiencyLevel(type) + 1);
    }
    /**
     * 將特殊武器類型轉換為主類型（例如 type=18 → 11）
     * 可擴充其他對應
     */
    public  static int normalizeWeaponType(int type) {
        switch (type) {
            case 18: return 11;
            case 19: return 7;
            case 32: return 7;
            case 33: return 2;
            case 34: return 4;
            case 35: return 3;
            case 36: return 8;
            case 15: return 6;
            case 37: return 9;
            case 38: return 11;
            case 39: return 10;
            case 13: return 4;
            case 14: return 5; // 單手矛與雙手矛共用熟練度
            case 17: return 10;
            case 12: return 9;
            default: return type;
        }
    }




    /**
     * 熟練度經驗達到升級時提升等級
     *
     * @param pc 玩家
     * @param type 武器類型
     * @param level 熟練度等級
     */
    private void onChangeProficiencyExp(L1PcInstance pc, int type, int level) {
        try {
            WeaponProficiency weaponProficiency = WeaponProficiencyTable.getProficiency(type, level);
            if (weaponProficiency == null) {
                return;
            }
            if (getProficiencyExp(type) >= weaponProficiency.getExp()) {
                setProficiencyLevel(type, level);
            }
            final int maxLv = WeaponProficiencyTable.getMaxProficienciesLevel(type);
            WeaponProficiency maxProficiency = WeaponProficiencyTable.getProficiency(type, maxLv);
            if (maxProficiency != null && level >= maxLv) {// 最大等级
                TimeUnit.MILLISECONDS.sleep(10);
                setProficiencyLevel(type, maxLv);
                setProficiencyExp(type, maxProficiency.getExp());
                setProficiencyEnd(type);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 玩家武器熟練度
     * 經驗達到設定數值時升級檢測
     *
     * @param type 武器類型
     * @return 熟練度
     */
    private int getProficiencyExp(int type) {
        PlayerWeaponProficiency proficiency = _weaponProficiency.get(type);
        if (proficiency == null) {
            return 0;
        }
        return proficiency.getExp();
    }

    /**
     * 增加玩家武器熟練度
     *
     * @param type 武器類型
     * @param exp 增加的熟練度
     */
    private void addProficiencyExp(int type, int exp) {
        PlayerWeaponProficiency proficiency = _weaponProficiency.get(type);
        if (proficiency != null) {
            proficiency.addExp(exp);
        } else {
            proficiency = new PlayerWeaponProficiency();
            proficiency.setCharId(_owner.getId());
            proficiency.setType(type);
            proficiency.setLevel(0);
            proficiency.setExp(exp); // 首次就給這次的經驗
            _weaponProficiency.put(type, proficiency); // 及時 put
        }
        CharacterWeaponProficiencyTable.getInstance().updateProficiency(_owner.getId(), type, proficiency);
    }

    private void setProficiencyExp(int type, int exp) {
        PlayerWeaponProficiency proficiency = _weaponProficiency.get(type);
        if (proficiency != null) {
            proficiency.setExp(exp);
        } else {// 防意外 若沒有則新增一條資料 嚴格意義上其實沒有意義
            proficiency = new PlayerWeaponProficiency();
            setProficiencyType(type);
        }
        // 更新資料庫記錄
        CharacterWeaponProficiencyTable.getInstance().updateProficiency(_owner.getId(), type, proficiency);
    }

    /**
     * 武器熟練度等級
     *
     * @param type 武器類型
     * @return 對應的熟練度等級
     */
    private int getProficiencyLevel(int type) {
        PlayerWeaponProficiency proficiency = _weaponProficiency.get(type);
        if (proficiency == null) {
            return 0;
        }
        return proficiency.getLevel();
    }

    /**
     * 設定玩家武器熟練度
     *
     * @param type 武器類型
     * @param level 熟練度
     */
    private void setProficiencyLevel(int type, int level) {
        PlayerWeaponProficiency proficiency = _weaponProficiency.get(type);
        if (proficiency != null) {
            proficiency.setLevel(level);
        } else {// 防意外 若沒有則新增一條資料 嚴格意義上其實沒有意義
            proficiency = new PlayerWeaponProficiency();
            setProficiencyType(type);
        }
        // 寫入資料庫記錄 CharacterWeaponProficiencyTable
        CharacterWeaponProficiencyTable.getInstance().updateProficiency(_owner.getId(), type, proficiency);
    }

    private boolean isProficiencyEnd(int type) {
        final int maxLv = WeaponProficiencyTable.getMaxProficienciesLevel(type);
        return getProficiencyLevel(type) >= maxLv;
    }

    /**
     * 設定結束
     *
     * @param type 武器類型
     */
    protected void setProficiencyEnd(int type) {
        PlayerWeaponProficiency proficiency = _weaponProficiency.get(type);
        if (proficiency != null) {
            // 寫入資料庫記錄 CharacterWeaponProficiencyTable
            CharacterWeaponProficiencyTable.getInstance().updateProficiency(_owner.getId(), type, proficiency);
        }
    }
}
