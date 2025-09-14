package com.lineage.server.model.Instance;

import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_Trap;
import com.lineage.server.templates.L1Trap;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class L1TrapInstance extends L1Object {
    private static final long serialVersionUID = 1L;
    // 記錄日誌的物件，用於錯誤和資訊記錄
    private static final Log _log = LogFactory.getLog(L1TowerInstance.class);
    // 隨機數生成器，用於隨機位置生成
    private static final Random _random = new Random();

    // 陷阱的模板資料
    private final L1Trap _trap;
    // 基礎位置點
    private final Point _baseLoc = new Point();
    // 隨機偏移量點
    private final Point _rndPt = new Point();
    // 陷阱活動的時間間隔（以秒為單位）
    private int _span;
    // 陷阱停止的標記
    private int _stop = 0;
    // 陷阱是否啟用的標誌
    private boolean _isEnable = true;
    // 已知的玩家列表
    private List<L1PcInstance> _knownPlayers = new CopyOnWriteArrayList<>();

    /**
     * 建構子，用於初始化陷阱實例
     *
     * @param id    陷阱的唯一識別碼
     * @param trap  陷阱的模板
     * @param loc   陷阱的初始位置
     * @param rndPt 隨機偏移量
     * @param span  陷阱的活動間隔時間（毫秒）
     */
    public L1TrapInstance(int id, L1Trap trap, L1Location loc, Point rndPt, int span) {
        setId(id);
        _trap = trap;
        getLocation().set(loc);
        _baseLoc.set(loc);
        _rndPt.set(rndPt);
        if (span > 0) {
            _span = (span / 1000);
        }
        resetLocation();
    }

    // 獲取陷阱模板
    public L1Trap get_trap() {
        return _trap;
    }

    // 獲取停止標記
    public int get_stop() {
        return _stop;
    }

    // 設置停止標記
    public void set_stop(int _stop) {
        this._stop = _stop;
    }

    /**
     * 重置陷阱的位置，根據隨機偏移量生成新的位置
     */
    public void resetLocation() {
        try {
            // 如果隨機偏移量為 (0,0)，則不進行位置重置
            if ((_rndPt.getX() == 0) && (_rndPt.getY() == 0)) {
                return;
            }
            enableTrap();
            // 嘗試生成一個有效的位置，最多嘗試 50 次
            for (int i = 0; i < 50; i++) {
                int rndX = _random.nextInt(_rndPt.getX() + 1) * (_random.nextBoolean() ? 1 : -1);
                int rndY = _random.nextInt(_rndPt.getY() + 1) * (_random.nextBoolean() ? 1 : -1);
                rndX += _baseLoc.getX();
                rndY += _baseLoc.getY();
                L1Map map = getLocation().getMap();
                // 檢查新位置是否在地圖範圍內且可通行
                if ((map.isInMap(rndX, rndY)) && (map.isPassable(rndX, rndY, null))) {
                    getLocation().set(rndX, rndY);
                    break;
                }
            }
        } catch (Exception e) {
            // 如果發生異常，記錄錯誤信息
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    // 獲取陷阱的活動間隔時間
    public int getSpan() {
        return _span;
    }

    // 啟用陷阱
    public void enableTrap() {
        set_stop(0);
        _isEnable = true;
    }

    /**
     * 禁用陷阱，並將其從所有已知玩家的視野中移除
     */
    public void disableTrap() {
        _isEnable = false;
        for (L1PcInstance pc : _knownPlayers) {
            pc.removeKnownObject(this);
            pc.sendPackets(new S_RemoveObject(this));
        }
        _knownPlayers.clear();
    }

    // 判斷陷阱是否啟用
    public boolean isEnable() {
        return _isEnable;
    }

    /**
     * 當玩家踩到陷阱時觸發的事件
     *
     * @param trodFrom 踩到陷阱的玩家
     */
    public void onTrod(L1PcInstance trodFrom) {
        try {
            if (trodFrom == null) return;
            if (_trap == null) {
                _log.warn("Trap template is null on onTrod; disabling trap id=" + getId());
                disableTrap();
                return;
            }
            _trap.onTrod(trodFrom, this);
        } catch (Exception e) {
            _log.error("onTrod error for trap id=" + getId() + ": " + e.getMessage(), e);
            disableTrap();
        }
    }

    /**
     * 當陷阱被檢測到時觸發的事件
     *
     * @param caster 檢測陷阱的玩家
     */
    public void onDetection(L1PcInstance caster) {
        try {
            if (caster == null) return;
            if (_trap == null) {
                _log.warn("Trap template is null on onDetection; disabling trap id=" + getId());
                disableTrap();
                return;
            }
            _trap.onDetection(caster, this);
        } catch (Exception e) {
            _log.error("onDetection error for trap id=" + getId() + ": " + e.getMessage(), e);
            disableTrap();
        }
    }

    /**
     * 當玩家感知到陷阱時觸發的事件
     *
     * @param perceivedFrom 感知陷阱的玩家
     */
    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            // 如果玩家有特定技能效果（ID: 2002），則將陷阱加入玩家的已知物件並通知玩家
            if (perceivedFrom.hasSkillEffect(2002)) {
                if (_trap == null) {
                    _log.warn("Trap template is null on onPerceive; disabling trap id=" + getId());
                    disableTrap();
                    return;
                }
                perceivedFrom.addKnownObject(this);
                perceivedFrom.sendPackets(new S_Trap(this, _trap.getType()));
                _knownPlayers.add(perceivedFrom);
            }
        } catch (Exception e) {
            // 記錄異常信息
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
