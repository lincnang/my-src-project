package com.lineage.server.clientpackets;

import com.lineage.config.ConfigPRO;
import com.lineage.server.datatables.RecordTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_Disconnect;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ServerBasePacket;

import java.util.EnumMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.lineage.server.model.skill.L1SkillId.*;

public class AcceleratorChecker {//src042
    public static final int R_OK = 0;
    public static final int R_DETECTED = 1;
    public static final int R_DISPOSED = 2;
    private static final int INJUSTICE_COUNT_LIMIT = ConfigPRO.INJUSTICE_COUNT;
    private static final int JUSTICE_COUNT_LIMIT = ConfigPRO.JUSTICE_COUNT;
    private static final double HASTE_RATE = 0.755;
    private static final double WAFFLE_RATE = 0.9;
    private static double CHECK_STRICTNESS = (ConfigPRO.CHECK_STRICTNESS - 5) / 100D;
    private static double CHECK_MOVESTRICTNESS = (ConfigPRO.CHECK_MOVE_STRICTNESS - 5) / 100D;
    // 行為間隔檢查所用的計時器（以 nanoTime 記錄）：_actTimers 記錄各行為最後觸發時間；_checkTimers 預留為額外檢查/冷卻用途
    private final EnumMap<ACT_TYPE, Long> _actTimers = new EnumMap<>(ACT_TYPE.class); // nanoTime
    private final EnumMap<ACT_TYPE, Long> _checkTimers = new EnumMap<>(ACT_TYPE.class);
    private final L1PcInstance _pc;
    private int move_injusticeCount;
    private int move_justiceCount;
    private int moveResult = R_OK;

    public AcceleratorChecker(L1PcInstance pc) {
        _pc = pc;
        move_injusticeCount = 0;
        move_justiceCount = 0;
        long now = System.nanoTime();
        for (ACT_TYPE each : ACT_TYPE.values()) {
            _actTimers.put(each, now);
            _checkTimers.put(each, now);
        }
    }

    /**
     * 重置檢測狀態 (用於傳送或速度劇烈變化後)
     * 將最後動作時間設為當前時間，並清空違規計數
     */
    public void Reset() {
        long now = System.nanoTime();
        // 將所有動作的最後時間重置為現在，避免因時間差導致的誤判
        for (ACT_TYPE each : ACT_TYPE.values()) {
            _actTimers.put(each, now);
        }
        move_injusticeCount = 0;
        move_justiceCount = 0;
        moveResult = R_OK;
    }

    public static void Setspeed() {
        CHECK_STRICTNESS = (ConfigPRO.CHECK_STRICTNESS - 5) / 100D;
        CHECK_MOVESTRICTNESS = (ConfigPRO.CHECK_MOVE_STRICTNESS - 5) / 100D;
    }

    public int checkInterval(ACT_TYPE type) {
        // 若未啟用加速檢測，直接通過
        if (!ConfigPRO.ACCELERATOR_CHECK_ENABLED) {
            return R_OK;
        }
        if (Objects.requireNonNull(type) == ACT_TYPE.MOVE) {
            long moveNow = System.nanoTime();
            long last = _actTimers.get(type);
            long moveInterval = TimeUnit.NANOSECONDS.toMillis(moveNow - last);
            int moveRightInterval = getRightInterval(type);
            moveInterval = (long) (CHECK_MOVESTRICTNESS * moveInterval);
            // 若換算後的移動間隔 <= 0，視為雜訊，直接放行並重置時間
            if (moveInterval <= 0) {
                _actTimers.put(type, moveNow);
                moveResult = R_OK;
                return moveResult;
            }
            // 採用突發流量寬容：若實際間隔遠大於標準（> 標準的 6 倍或 > 1 秒），視為卡頓/GC/IO 導致，放寬這次檢測以降低誤判
            boolean burstForgive = TimeUnit.NANOSECONDS.toMillis(moveNow - last) > Math.max(moveRightInterval * 6L, 1000L);
            if (0 < moveInterval && moveInterval < moveRightInterval) {
                if (burstForgive) {
                    // 視為合法，累計一次正常次數；達門檻（JUSTICE_COUNT_LIMIT）則清空違規計數
                    move_justiceCount++;
                    if (move_justiceCount >= JUSTICE_COUNT_LIMIT) {
                        move_injusticeCount = 0;
                        move_justiceCount = 0;
                    }
                    moveResult = R_OK;
                } else {
                    move_injusticeCount++;
                    move_justiceCount = 0;
                    if (move_injusticeCount >= INJUSTICE_COUNT_LIMIT) {
                        doPunishment();
                        moveResult = R_DISPOSED;
                    } else {
                        moveResult = R_DETECTED;
                    }
                }
            } else if (moveInterval >= moveRightInterval) {
                move_justiceCount++;
                if (move_justiceCount >= JUSTICE_COUNT_LIMIT) {
                    move_injusticeCount = 0;
                    move_justiceCount = 0;
                }
                moveResult = R_OK;
            }
            _actTimers.put(type, moveNow);
            return moveResult;
        }
        long attackKnow = System.nanoTime();
        long attackInterval = TimeUnit.NANOSECONDS.toMillis(attackKnow - _actTimers.get(type));
        int attackRightInterval = getRightInterval(type);
        attackInterval = (long) (CHECK_STRICTNESS * attackInterval);
        int attackResult;
        // 若換算後的攻擊間隔 <= 0，視為雜訊，直接放行
        if (attackInterval <= 0) {
            attackResult = R_OK;
        } else if (0 < attackInterval && attackInterval < attackRightInterval) {
            attackResult = R_DISPOSED;
        } else if (attackInterval >= attackRightInterval) {
            attackResult = R_OK;
        } else {
            attackResult = R_DISPOSED;
        }
        _actTimers.put(type, attackKnow);
        return attackResult;
    }

    public int getRightInterval(ACT_TYPE type) {
        double interval;
        switch (type) {
            case ATTACK:
                interval = SprTable.get().getAttackSpeed(_pc.getTempCharGfx(), _pc.getCurrentWeapon() + 1);
                break;
            case MOVE:
                interval = SprTable.get().getMoveSpeed(_pc.getTempCharGfx(), _pc.getCurrentWeapon());
                break;
            case SPELL_DIR:
                interval = SprTable.get().getDirSpellSpeed(_pc.getTempCharGfx());
                break;
            case SPELL_NODIR:
                interval = SprTable.get().getNodirSpellSpeed(_pc.getTempCharGfx());
                break;
            default:
                return 0;

        }
        switch (_pc.getMoveSpeed()) {
            case 1:
                interval *= HASTE_RATE;
                break;
            case 2:
                interval /= HASTE_RATE;
                break;
            default:
                break;
        }
        switch (_pc.getBraveSpeed()) {
            case 1:
                interval *= HASTE_RATE;
                break;
            case 3: // 勇敢/類似增速狀態：移動以加速係數，非移動以餅乾係數（約 1.11x）
                // interval *= WAFFLE_RATE;
                if (type.equals(ACT_TYPE.MOVE)) {
                    interval *= HASTE_RATE; // 移動：乘以加速係數（約 1.33x）
                } else {
                    interval *= WAFFLE_RATE; // 非移動：乘以餅乾係數（約 1.11x）
                }
                break;
            case 4:
                if (type.equals(ACT_TYPE.MOVE)) {
                    interval *= HASTE_RATE;
                }
                break;
            case 5: // 超高速狀態：再以 HASTE_RATE/2 調整
                interval *= HASTE_RATE / 2;
                break;
            case 6:
                if (type.equals(ACT_TYPE.ATTACK) && _pc.isFastAttackable()) {
                    interval *= HASTE_RATE * WAFFLE_RATE;
                }
                break;
            default:
                break;
        }
        if (_pc.hasSkillEffect(STATUS_RIBRAVE) && type.equals(ACT_TYPE.MOVE)) {
            interval *= WAFFLE_RATE;
        }
        if (_pc.hasSkillEffect(STATUS_BRAVE3)) {
            interval *= WAFFLE_RATE;
        }
        if (_pc.hasSkillEffect(WIND_SHACKLE) && !type.equals(ACT_TYPE.MOVE)) {
            interval /= 2;
        }

        if (type.equals(ACT_TYPE.ATTACK) && this._pc.isActivated()) { // 角色處於啟動/激發狀態：攻速再加成
            interval /= WAFFLE_RATE;
        }
        if (_pc.getMapId() == 5143) {
            interval *= 0.1;
        }
        if (this._pc.isGm()) {
            if (type.equals(ACT_TYPE.ATTACK)) {
                this._pc.sendPackets((ServerBasePacket) new S_SystemMessage("攻擊速度:" + interval));  // GM除錯：顯示計算後的攻擊間隔
            } else if (type.equals(ACT_TYPE.MOVE)) {
                this._pc.sendPackets((ServerBasePacket) new S_SystemMessage("移動速度:" + interval));  // GM除錯：顯示計算後的移動間隔
            }
        }
        return (int) interval;
    }

    private void doPunishment() {
        final int punishment_type = Math.abs(ConfigPRO.PUNISHMENT_TYPE);
        final int punishment_time = Math.abs(ConfigPRO.PUNISHMENT_TIME);
        final int punishment_mapid = Math.abs(ConfigPRO.PUNISHMENT_MAP_ID);
        if (!this._pc.isGm()) {
            int x = this._pc.getX();
            int y = this._pc.getY();
            int mapid = this._pc.getMapId();
            switch (punishment_type) {
                case 0:
                    _pc.sendPackets(new S_SystemMessage("\\aG加速器檢測警告" + punishment_time + "秒後強制驅離。"));
                    try {
                        TimeUnit.MILLISECONDS.sleep(punishment_time * 1000L);
                        RecordTable.get().r_speed(this._pc.getName(), "偵測異常(攻速or走速");
                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                    _pc.sendPackets(new S_Disconnect());
                    break;
                case 1:
                    _pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
                    _pc.sendPackets(new S_SystemMessage("\\aG加速器檢測警告" + punishment_time + "秒後解除您的行動。"));
                    try {
                        TimeUnit.MILLISECONDS.sleep(punishment_time * 1000L);
                        RecordTable.get().r_speed(this._pc.getName(), "偵測異常(攻速or走速");
                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                    _pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
                    break;
                case 2:
                    L1Teleport.teleport(_pc, 32698, 32857, (short) punishment_mapid, 5, false);
                    _pc.sendPackets(new S_SystemMessage("\\aG加速器檢測警告" + punishment_time + "秒後傳送到地獄。"));
                    try {
                        TimeUnit.MILLISECONDS.sleep(punishment_time * 1000L);
                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                    L1Teleport.teleport(_pc, x, y, (short) mapid, 5, false);
                    RecordTable.get().r_speed(this._pc.getName(), "偵測異常(攻速or走速");
                    break;
                case 3:
                    int[] Head = {0, 1, 2, 3, 4, 5, 6, 7};
                    int[] X = {x, x - 1, x - 1, x - 1, x, x + 1, x + 1, x + 1};
                    int[] Y = {y + 1, y + 1, y, y - 1, y - 1, y - 1, y, y + 1};
                    for (int i = 0; i < Head.length; i++) {
                        if (_pc.getHeading() == Head[i]) {
                            L1Teleport.teleport(this._pc, X[i], Y[i], (short) mapid, _pc.getHeading(), false);
                            _pc.sendPackets(new S_SystemMessage("\\aG加速器檢測。"));
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(punishment_time * 1000L);
                            RecordTable.get().r_speed(this._pc.getName(), "偵測異常(攻速or走速");
                        } catch (Exception e) {
                            System.out.println(e.getLocalizedMessage());
                        }
                    }
            }
        } else {
            _pc.set_misslocTime(1);  //加速偵測回逤
            _pc.sendPackets(new S_SystemMessage("\\aD遊戲管理員在遊戲中使用加速器檢測中。"));
            move_justiceCount = 0;
        }
    }

    public static enum ACT_TYPE {
        MOVE, ATTACK, SPELL_DIR, SPELL_NODIR
    }
}
