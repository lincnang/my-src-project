package com.lineage.server.utils;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 工具類：提供職業限制判斷，用於製作系統 class_limit 欄位。
 */
public final class ClassLimitUtils {

    private static final int CROWN = 1;
    private static final int KNIGHT = 2;
    private static final int ELF = 4;
    private static final int WIZARD = 8;
    private static final int DARK_ELF = 16;
    private static final int DRAGON_KNIGHT = 32;
    private static final int ILLUSIONIST = 64;
    private static final int WARRIOR = 128;

    private ClassLimitUtils() {
    }

    /**
     * 回傳玩家對應的職業 bit mask。
     */
    public static int resolveClassMask(final L1PcInstance pc) {
        if (pc == null) {
            return 0;
        }
        if (pc.isCrown()) {
            return CROWN;
        }
        if (pc.isKnight()) {
            return KNIGHT;
        }
        if (pc.isElf()) {
            return ELF;
        }
        if (pc.isWizard()) {
            return WIZARD;
        }
        if (pc.isDarkelf()) {
            return DARK_ELF;
        }
        if (pc.isDragonKnight()) {
            return DRAGON_KNIGHT;
        }
        if (pc.isIllusionist()) {
            return ILLUSIONIST;
        }
        if (pc.isWarrior()) {
            return WARRIOR;
        }
        return 0;
    }

    /**
     * 判斷 class_limit 是否允許該玩家使用。classLimit=0 代表不限制。
     */
    public static boolean isClassAllowed(final int classLimit, final L1PcInstance pc) {
        if (pc == null || classLimit <= 0) {
            return true;
        }
        final int playerMask = resolveClassMask(pc);
        if (playerMask == 0) {
            return false;
        }
        return (classLimit & playerMask) != 0;
    }
}
