package com.lineage.server.model.map;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1GuardInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1V1Map extends L1Map {
    private static final Log _log = LogFactory.getLog(L1V1Map.class);
    private static final byte BITFLAG_IS_IMPASSABLE = -128;
    private static final byte[] HEADING_TABLE_X = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final byte[] HEADING_TABLE_Y = {-1, -1, 0, 1, 1, 1, 0, -1};
    public byte _doorMap[][]; // 紅騎士 訓練副本 by darling
    private int _mapId;
    private int _worldTopLeftX;
    private int _worldTopLeftY;
    private int _worldBottomRightX;
    private int _worldBottomRightY;
    private byte[][] _map;
    private boolean _isUnderwater;
    private boolean _isMarkable;
    private boolean _isTeleportable;
    private boolean _isEscapable;
    private boolean _isUseResurrection;
    private boolean _isUsePainwand;
    private boolean _isEnabledDeathPenalty;
    private boolean _isTakePets;
    private boolean _isRecallPets;
    private boolean _isUsableItem;
    private boolean _isUsableSkill;
    private int _isUsableShop;
    private boolean _isAutoBot;
    private boolean _chuanyunjian;

    protected L1V1Map() {
    }

    public L1V1Map(int mapId, byte[][] map, int worldTopLeftX, int worldTopLeftY, boolean underwater, boolean markable, boolean teleportable, boolean escapable, boolean useResurrection, boolean usePainwand, boolean enabledDeathPenalty, boolean takePets, boolean recallPets, boolean usableItem, boolean usableSkill, final int usableShop, boolean isautoBot, final boolean chuanyunjian) {
        _mapId = mapId;
        _map = map;
        _worldTopLeftX = worldTopLeftX;
        _worldTopLeftY = worldTopLeftY;
        _worldBottomRightX = (worldTopLeftX + map.length - 1);
        _worldBottomRightY = (worldTopLeftY + map[0].length - 1);
        _isUnderwater = underwater;
        _isMarkable = markable;
        _isTeleportable = teleportable;
        _isEscapable = escapable;
        _isUseResurrection = useResurrection;
        _isUsePainwand = usePainwand;
        _isEnabledDeathPenalty = enabledDeathPenalty;
        _isTakePets = takePets;
        _isRecallPets = recallPets;
        _isUsableItem = usableItem;
        _isUsableSkill = usableSkill;
        _isUsableShop = usableShop;
        _isAutoBot = isautoBot;
        this._chuanyunjian = chuanyunjian;
    }

    public L1V1Map(L1V1Map map) {
        _mapId = map._mapId;
        _map = new byte[map._map.length][];
        for (int i = 0; i < map._map.length; i++) {
            _map[i] = ((byte[]) map._map[i].clone());
        }
        // 紅騎士 訓練副本 by darling
        _doorMap = new byte[_map.length][_map[0].length];
        for (int i = 0; i < _map.length; ++i) {
            for (int j = 0; j < _map[0].length; ++j) {
                _doorMap[i][j] = 0;
            }
        }
        _worldTopLeftX = map._worldTopLeftX;
        _worldTopLeftY = map._worldTopLeftY;
        _worldBottomRightX = map._worldBottomRightX;
        _worldBottomRightY = map._worldBottomRightY;
    }

    private int accessTile(int x, int y) {
        if (!isInMap(x, y)) {
            return 0;
        }
        return _map[(x - _worldTopLeftX)][(y - _worldTopLeftY)];
    }

    private int accessOriginalTile(int x, int y) {
        return accessTile(x, y) & 0x7F;
    }

    public void setTile(int x, int y, int tile) {
        if (!isInMap(x, y)) {
            return;
        }
        _map[(x - _worldTopLeftX)][(y - _worldTopLeftY)] = ((byte) tile);
    }

    public byte[][] getRawTiles() {
        return _map;
    }

    public int getId() {
        return _mapId;
    }

    public int getX() {
        return _worldTopLeftX;
    }

    public int getY() {
        return _worldTopLeftY;
    }

    public int getWidth() {
        return _worldBottomRightX - _worldTopLeftX + 1;
    }

    public int getHeight() {
        return _worldBottomRightY - _worldTopLeftY + 1;
    }

    public int getTile(int x, int y) {
        short tile = _map[(x - _worldTopLeftX)][(y - _worldTopLeftY)];
        if ((tile & 0xFFFFFF80) != 0) {
            return 300;
        }
        return accessOriginalTile(x, y);
    }

    public int getOriginalTile(int x, int y) {
        return accessOriginalTile(x, y);
    }

    public boolean isInMap(Point pt) {
        return isInMap(pt.getX(), pt.getY());
    }

    public boolean isInMap(int x, int y) {
        if ((_mapId == 4) && ((x < 32520) || (y < 32070) || ((y < 32190) && (x < 33950)))) {
            return false;
        }
        return (_worldTopLeftX <= x) && (x <= _worldBottomRightX) && (_worldTopLeftY <= y) && (y <= _worldBottomRightY);
    }

    public boolean isPassable(Point pt, L1Character cha) {
        return isPassable(pt.getX(), pt.getY(), cha);
    }

    public boolean isPassable(int x, int y, L1Character cha) {
        return (isPassable(x, y - 1, 4, cha)) || (isPassable(x + 1, y, 6, cha)) || (isPassable(x, y + 1, 0, cha)) || (isPassable(x - 1, y, 2, cha));
    }

    public boolean isPassable(Point pt, int heading, L1Character cha) {
        return isPassable(pt.getX(), pt.getY(), heading, cha);
    }

    public boolean isPassable(int x, int y, int heading, L1Character cha) {
        try {
            int tile1 = accessTile(x, y);
            int locx = x + HEADING_TABLE_X[heading];
            int locy = y + HEADING_TABLE_Y[heading];
            int tile2 = accessTile(locx, locy);
            switch (tile2) {
                case 0:
                case 3:
                    return false;
                case 1:
                case 2:
            }
            if ((tile2 & 0xFFFFFF80) == -128) {
                return false;
            }
            if (cha != null) {
                switch (_mapId) {
                    case 0:
                        switch (tile2) {
                            case 47:
                                return true;
                            case 46:
                                return true;
                            case 42:
                                return true;
                            case 26:
                                return true;
                            case 31:
                                return true;
                            case 21:
                                return true;
                        }
                        return set_map(tile2, 1);
                    case 4:
                    case 57:
                    case 58:
                    case 68:
                    case 69:
                    case 70:
                    case 303:
                    case 430:
                    case 440:
                    case 445:
                    case 480:
                    case 613:
                    case 621:
                    case 630:
                    case 9101:
                    case 9102:
                        switch (tile2) {
                            case 28:
                                return false;
                            case 44:
                                return false;
                            case 21:
                                return false;
                            case 26:
                                return false;
                            case 29:
                                return false;
                            case 12:
                                return false;
                        }
                        return set_map(tile2, 8);
                }
                return set_map(tile2, 3);
            }
            switch (heading) {
                case 0:
                    return (tile1 & 0x2) == 2;
                case 1:
                    int tile3 = accessTile(x, y - 1);
                    int tile4 = accessTile(x + 1, y);
                    return ((tile3 & 0x1) == 1) || ((tile4 & 0x2) == 2);
                case 2:
                    return (tile1 & 0x1) == 1;
                case 3:
                    tile3 = accessTile(x, y + 1);
                    return (tile3 & 0x1) == 1;
                case 4:
                    return (tile2 & 0x2) == 2;
                case 5:
                    return ((tile2 & 0x1) == 1) || ((tile2 & 0x2) == 2);
                case 6:
                    return (tile2 & 0x1) == 1;
                case 7:
                    tile3 = accessTile(x - 1, y);
                    return (tile3 & 0x2) == 2;
            }
        } catch (Exception localException) {
        }
        return false;
    }

    public boolean isPassableDna(int x, int y, int heading) {
        try {
            int locx = x + HEADING_TABLE_X[heading];
            int locy = y + HEADING_TABLE_Y[heading];
            int tile2 = accessTile(locx, locy);
            switch (tile2) {
                case 0:
                case 3:
                    return false;
                case 1:
                case 2:
            }
            switch (_mapId) {
                case 0:
                    switch (tile2) {
                        case 47:
                            return true;
                        case 46:
                            return true;
                        case 42:
                            return true;
                        case 26:
                            return true;
                        case 31:
                            return true;
                        case 21:
                            return true;
                    }
                    return set_map(tile2, 1);
                case 4:
                case 57:
                case 58:
                case 68:
                case 69:
                case 70:
                case 303:
                case 430:
                case 440:
                case 445:
                case 480:
                case 613:
                case 621:
                case 630:
                case 9101:
                case 9102:
                    switch (tile2) {
                        case 28:
                            return false;
                        case 44:
                            return false;
                        case 21:
                            return false;
                        case 26:
                            return false;
                        case 29:
                            return false;
                        case 12:
                            return false;
                    }
                    return set_map(tile2, 8);
            }
            return set_map(tile2, 3);
        } catch (Exception localException) {
        }
        return false;
    }

    private boolean set_map(int tile2, int i) {
        return (tile2 & i) != 0;
    }

    public boolean isDoorPassable(int x, int y, int heading, L1NpcInstance npc) {
        try {
            if (heading == -1) {
                return false;
            }
            int locx = x + HEADING_TABLE_X[heading];
            int locy = y + HEADING_TABLE_Y[heading];
            int tile2 = accessTile(locx, locy);
            if (npc != null) {
                if (tile2 == 3) {
                    if (npc.is_now_target() == null) {
                        return false;
                    }
                    for (L1Object object : World.get().getVisibleObjects(npc, 2)) {
                        if ((object instanceof L1DoorInstance)) {
                            L1DoorInstance door = (L1DoorInstance) object;
                            switch (door.getDoorId()) {
                                case 6006:
                                case 6007:
                                case 10000:
                                case 10001:
                                case 10002:
                                case 10003:
                                case 10004:
                                case 10005:
                                case 10006:
                                case 10007:
                                case 10008:
                                case 10009:
                                case 10010:
                                case 10011:
                                case 10012:
                                case 10013:
                                case 10015:
                                case 10016:
                                case 10017:
                                case 10019:
                                case 10020:
                                case 10036:
                                case 10037:
                                case 10038:
                                case 10039:
                                    return false;
                            }
                            if (door.getOpenStatus() == 29) {
                                if ((npc instanceof L1GuardInstance)) {
                                    door.open();
                                    return true;
                                }
                                if (door.getKeeperId() == 0) {
                                    door.open();
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                    return false;
                }
                return true;
            }
        } catch (Exception localException) {
        }
        return true;
    }

    public void setPassable(Point pt, boolean isPassable) {
        setPassable(pt.getX(), pt.getY(), isPassable, 2);
    }

    public void setPassable(int x, int y, boolean isPassable, int door) {
        switch (door) {
            case 0:
                set_door_0(x, y, isPassable);
                break;
            case 1:
                set_door_1(x, y, isPassable);
                break;
            default:
                if (isPassable) {
                    setTile(x, y, (short) (accessTile(x, y) & 0x7F));
                } else {
                    setTile(x, y, (short) (accessTile(x, y) | 0xFFFFFF80));
                }
                break;
        }
    }

    private void set_door_0(int x, int y, boolean isPassable) {
        try {
            if (isPassable) {
                _map[(x - _worldTopLeftX)][(y - _worldTopLeftY)] = 47;
            } else {
                _map[(x - _worldTopLeftX)][(y - _worldTopLeftY)] = 3;
                _map[(x - 1 - _worldTopLeftX)][(y - _worldTopLeftY)] = 3;
                _map[(x + 1 - _worldTopLeftX)][(y - _worldTopLeftY)] = 3;
            }
        } catch (Exception e) {
            _log.error("X:" + x + " Y:" + y + " MAP:" + _mapId, e);
        }
    }

    private void set_door_1(int x, int y, boolean isPassable) {
        try {
            if (isPassable) {
                _map[(x - _worldTopLeftX)][(y - _worldTopLeftY)] = 47;
            } else {
                _map[(x - _worldTopLeftX)][(y - _worldTopLeftY)] = 3;
                _map[(x - _worldTopLeftX)][(y - 1 - _worldTopLeftY)] = 3;
                _map[(x - _worldTopLeftX)][(y + 1 - _worldTopLeftY)] = 3;
            }
        } catch (Exception e) {
            _log.error("X:" + x + " Y:" + y + " MAP:" + _mapId, e);
        }
    }

    public boolean isSafetyZone(Point pt) {
        return isSafetyZone(pt.getX(), pt.getY());
    }

    public boolean isSafetyZone(int x, int y) {
        int tile = accessOriginalTile(x, y);
        return (tile & 0x30) == 16;
    }

    public boolean isCombatZone(Point pt) {
        return isCombatZone(pt.getX(), pt.getY());
    }

    public boolean isCombatZone(int x, int y) {
        int tile = accessOriginalTile(x, y);
        return (tile & 0x30) == 32;
    }

    public boolean isNormalZone(Point pt) {
        return isNormalZone(pt.getX(), pt.getY());
    }

    public boolean isNormalZone(int x, int y) {
        int tile = accessOriginalTile(x, y);
        return (tile & 0x30) == 0;
    }

    public boolean isArrowPassable(Point pt) {
        return isArrowPassable(pt.getX(), pt.getY());
    }

    public boolean isArrowPassable(int x, int y) {
        return (accessOriginalTile(x, y) & 0xE) != 0;
    }

    public boolean isArrowPassable(Point pt, int heading) {
        return isArrowPassable(pt.getX(), pt.getY(), heading);
    }

    /*
     * public boolean isArrowPassable(int x, int y, int heading) { try { int
     * newX = x + HEADING_TABLE_X[heading]; int newY = y +
     * HEADING_TABLE_Y[heading];
     *
     * int tile2 = accessTile(newX, newY);
     *
     * switch (tile2) { case 0: case 3: return false; default:// 一般 return
     * (tile2 & 0x0c) != 0x00; // if ((tile2 & 0xFFFFFF80) == -128) { // return
     * false; // } } //return (tile2 & 0xC) != 0; } catch (Exception
     * localException) { } return false; }
     */
    public boolean isArrowPassable(int x, int y, int heading) {
        // 現在
        int tile1 = accessTile(x, y);
        // 移動予定
        int tile2;
        int newX;
        int newY;
        if (heading == 0) {
            tile2 = accessTile(x, y - 1);
            newX = x;
            newY = y - 1;
        } else if (heading == 1) {
            tile2 = accessTile(x + 1, y - 1);
            newX = x + 1;
            newY = y - 1;
        } else if (heading == 2) {
            tile2 = accessTile(x + 1, y);
            newX = x + 1;
            newY = y;
        } else if (heading == 3) {
            tile2 = accessTile(x + 1, y + 1);
            newX = x + 1;
            newY = y + 1;
        } else if (heading == 4) {
            tile2 = accessTile(x, y + 1);
            newX = x;
            newY = y + 1;
        } else if (heading == 5) {
            tile2 = accessTile(x - 1, y + 1);
            newX = x - 1;
            newY = y + 1;
        } else if (heading == 6) {
            tile2 = accessTile(x - 1, y);
            newX = x - 1;
            newY = y;
        } else if (heading == 7) {
            tile2 = accessTile(x - 1, y - 1);
            newX = x - 1;
            newY = y - 1;
        } else {
            return false;
        }
        //if (isExistDoor(newX, newY) == 3) {
        if (isExistDoor(newX, newY)) {
            return false;
        }
        /*
         * if ((tile2 & BITFLAG_IS_IMPASSABLE) == BITFLAG_IS_IMPASSABLE) {
         * return false; }
         */
        if (!((tile2 & 0x02) == 0x02 || (tile2 & 0x01) == 0x01)) {
            return false;
        }
        if (heading == 0) {
            return (tile1 & 0x02) == 0x02;
        } else if (heading == 1) {
            int tile3 = accessTile(x, y - 1);
            int tile4 = accessTile(x + 1, y);
            return ((tile3 & 0x01) == 0x01) || ((tile4 & 0x02) == 0x02);
        } else if (heading == 2) {
            return (tile1 & 0x01) == 0x01;
        } else if (heading == 3) {
            int tile3 = accessTile(x, y + 1);
            return (tile3 & 0x01) == 0x01;
        } else if (heading == 4) {
            return (tile2 & 0x02) == 0x02;
        } else if (heading == 5) {
            return ((tile2 & 0x01) == 0x01) || ((tile2 & 0x02) == 0x02);
        } else if (heading == 6) {
            return (tile2 & 0x01) == 0x01;
        } else if (heading == 7) {
            int tile3 = accessTile(x - 1, y);
            return (tile3 & 0x02) == 0x02;
        }
        return false;
    }

    public boolean isUnderwater() {
        return _isUnderwater;
    }

    public boolean isMarkable() {
        return _isMarkable;
    }

    public boolean isTeleportable() {
        return _isTeleportable;
    }

    public boolean isEscapable() {
        return _isEscapable;
    }

    public boolean isUseResurrection() {
        return _isUseResurrection;
    }

    public boolean isUsePainwand() {
        return _isUsePainwand;
    }

    public boolean isEnabledDeathPenalty() {
        return _isEnabledDeathPenalty;
    }

    public boolean isTakePets() {
        return _isTakePets;
    }

    public boolean isRecallPets() {
        return _isRecallPets;
    }

    public boolean isUsableItem() {
        return _isUsableItem;
    }

    public boolean isUsableSkill() {
        return _isUsableSkill;
    }

    public boolean isAutoBot() {
        return _isAutoBot;
    }

    public boolean isFishingZone(int x, int y) {
        // 直接指定整張地圖（如 5490）都可釣魚
        if (_mapId == 5490) {
            return true;
        }

        // 原本邏輯（保留）
        return accessOriginalTile(x, y) == 28;
    }

    /*public int isExistDoor(int x, int y) {
        try {
            return _map[(x - _worldTopLeftX)][(y - _worldTopLeftY)];
        } catch (Exception localException) {
        }
        return 0;
    }*/
    @Override
    public boolean isExistDoor(int x, int y) {
        for (L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
            if (_mapId != door.getMapId()) {
                continue;
            }
            if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
                continue;
            }
            if (door.isDead()) {
                continue;
            }
            int leftEdgeLocation = door.getLeftEdgeLocation();
            int rightEdgeLocation = door.getRightEdgeLocation();
            int size = rightEdgeLocation - leftEdgeLocation;
            if (size == 0) {
                if (x == door.getX() && y == door.getY()) {
                    return true;
                }
            } else {
                if (door.getDirection() == 0) {
                    for (int doorX = leftEdgeLocation; doorX <= rightEdgeLocation; doorX++) {
                        if (x == doorX && y == door.getY()) {
                            return true;
                        }
                    }
                } else {
                    for (int doorY = leftEdgeLocation; doorY <= rightEdgeLocation; doorY++) {
                        if (x == door.getX() && y == doorY) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String toString(Point pt) {
        return "" + getOriginalTile(pt.getX(), pt.getY());
    }

    @Override
    public L1V1Map copyMap(int newMapId) {
        return clone(newMapId);
    }

    private L1V1Map clone(int newMapId) {
        L1V1Map map = new L1V1Map(this);
        map._mapId = newMapId;
        map._isUnderwater = _isUnderwater;
        map._isMarkable = _isMarkable;
        map._isTeleportable = _isTeleportable;
        map._isEscapable = _isEscapable;
        map._isUseResurrection = _isUseResurrection;
        map._isUsePainwand = _isUsePainwand;
        map._isEnabledDeathPenalty = _isEnabledDeathPenalty;
        map._isTakePets = _isTakePets;
        map._isRecallPets = _isRecallPets;
        map._isUsableItem = _isUsableItem;
        map._isUsableSkill = _isUsableSkill;
        map._isAutoBot = _isAutoBot;
        return map;
    }

    @Override
    public boolean isPassable(int x, int y) {
        return isPassable(x, y - 1, 4) || isPassable(x + 1, y, 6) || isPassable(x, y + 1, 0) || isPassable(x - 1, y, 2);
    }

    @Override
    public boolean isPassable(int x, int y, int heading) {
        // 現在
        int tile1 = accessTile(x, y);
        // 移動予定
        int tile2;
        int newX;
        int newY;
        if (heading == 0) {
            tile2 = accessTile(x, y - 1);
            newX = x;
            newY = y - 1;
        } else if (heading == 1) {
            tile2 = accessTile(x + 1, y - 1);
            newX = x + 1;
            newY = y - 1;
        } else if (heading == 2) {
            tile2 = accessTile(x + 1, y);
            newX = x + 1;
            newY = y;
        } else if (heading == 3) {
            tile2 = accessTile(x + 1, y + 1);
            newX = x + 1;
            newY = y + 1;
        } else if (heading == 4) {
            tile2 = accessTile(x, y + 1);
            newX = x;
            newY = y + 1;
        } else if (heading == 5) {
            tile2 = accessTile(x - 1, y + 1);
            newX = x - 1;
            newY = y + 1;
        } else if (heading == 6) {
            tile2 = accessTile(x - 1, y);
            newX = x - 1;
            newY = y;
        } else if (heading == 7) {
            tile2 = accessTile(x - 1, y - 1);
            newX = x - 1;
            newY = y - 1;
        } else {
            return false;
        }
        //if (isExistDoor(newX, newY) == 3) {
        if (isExistDoor(newX, newY)) {
            return false;
        }
        if ((tile2 & BITFLAG_IS_IMPASSABLE) == BITFLAG_IS_IMPASSABLE) {
            return false;
        }
        if (!((tile2 & 0x02) == 0x02 || (tile2 & 0x01) == 0x01)) {
            return false;
        }
        if (heading == 0) {
            return (tile1 & 0x02) == 0x02;
        } else if (heading == 1) {
            int tile3 = accessTile(x, y - 1);
            int tile4 = accessTile(x + 1, y);
            return ((tile3 & 0x01) == 0x01) || ((tile4 & 0x02) == 0x02);
        } else if (heading == 2) {
            return (tile1 & 0x01) == 0x01;
        } else if (heading == 3) {
            int tile3 = accessTile(x, y + 1);
            return (tile3 & 0x01) == 0x01;
        } else if (heading == 4) {
            return (tile2 & 0x02) == 0x02;
        } else if (heading == 5) {
            return ((tile2 & 0x01) == 0x01) || ((tile2 & 0x02) == 0x02);
        } else if (heading == 6) {
            return (tile2 & 0x01) == 0x01;
        } else if (heading == 7) {
            int tile3 = accessTile(x - 1, y);
            return (tile3 & 0x02) == 0x02;
        }
        return false;
    }

    @Override
    public int isUsableShop() {
        return this._isUsableShop;
    }

    @Override
    public boolean isPassable(Point pt) {
        // TODO 自動產生的方法 Stub
        return false;
    }

    @Override
    public boolean isPassable(Point pt, int heading) {
        // TODO 自動產生的方法 Stub
        return false;
    }

    @Override
    public boolean ismPassable(int x, int y, int heading) {
        // TODO 自動產生的方法 Stub
        return false;
    }

    @Override
    public void setPassable(int x, int y, boolean isPassable) {
        // TODO 自動產生的方法 Stub
    }

    public boolean chuanyunjian() {
        return this._chuanyunjian;
    }
}
