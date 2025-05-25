package com.lineage.server.world;

import com.lineage.config.Config;
import com.lineage.config.ConfigSkillWizard;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1GroundInventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1V1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.types.Point;
import com.lineage.server.utils.ListMapUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 遊戲世界儲存中心
 *
 * @author dexc
 */
public class World { // src016
    private static final Log _log = LogFactory.getLog(World.class);
    private static final int MAX_MAP_ID = 12000;
    private static final byte BITFLAG_IS_DOOR_IMPASSABLE_X = (byte) 0x80;
    private static final byte BITFLAG_IS_DOOR_IMPASSABLE_Y = (byte) 0x40;
    private static final byte[] HEADING_TABLE_X = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final byte[] HEADING_TABLE_Y = {-1, -1, 0, 1, 1, 1, 0, -1};
    private static final List<String> _htmlString = new ArrayList<>();
    private static World _instance;
    // 世界人物資料<pcName, Object>
    private final ConcurrentHashMap<String, L1PcInstance> _allPlayers;
    // 世界物件資料<Objid, Object>
    private final ConcurrentHashMap<Integer, L1Object> _allObjects;
    // 世界物件資料(區分地圖編號)<MapId, <Objid, Object>>
    private final HashMap<Integer, ConcurrentHashMap<Integer, L1Object>> _visibleObjects;
    // 紅騎士 訓練副本 by darling
    private final ConcurrentHashMap<Integer, L1ItemInstance> _allItems;
    // _allObjects 的 Collection
    private Collection<L1Object> _allValues;
    // 全部線上玩家
    private Collection<L1PcInstance> _allPlayerValues;
    private int _weather = 4;// 世界天氣狀況
    private boolean _worldChatEnabled = true;// 允許廣播
    private boolean _processingContributionTotal = false;// 計算貢獻度
    // 紅騎士 訓練副本 by darling
    private Collection<L1ItemInstance> _allItemValues;

    public World() {
        _allPlayers = new ConcurrentHashMap<>(); // 世界人物資料
        _allObjects = new ConcurrentHashMap<>(); // 世界物件資料
        _visibleObjects = new HashMap<>(); // 世界物件資料(區分地圖編號)
        _allItems = new ConcurrentHashMap<>();// 紅騎士 訓練副本 by darling
        for (Integer mapid : MapsTable.get().getMaps().keySet()) {
            final ConcurrentHashMap<Integer, L1Object> map = new ConcurrentHashMap<>();
            _visibleObjects.put(mapid, map);
        }
        _log.info("讀取->遊戲世界儲存中心建立完成!!!");
    }

    public static World get() {
        if (_instance == null) {
            _instance = new World();
        }
        return _instance;
    }

    private static boolean check(L1PcInstance tgpc) {
        try {
            if (tgpc == null) {
                return false;
            }
            if (tgpc.getOnlineStatus() == 0) {
                return false;
            }
            if (tgpc.getNetConnection() == null) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    static public void Door_movement(int x, int y, int map, boolean h, boolean flag) {
        L1V1Map m = (L1V1Map) L1WorldMap.get().getMap((short) map);
        if (m != null) {
            if (!m.isInMap(x, y)) {
                return;
            }
            if (flag) {
                synchronized (m._doorMap) {
                    m._doorMap[x - m.getX()][y - m.getY()] = 0;
                }
            } else {
                byte setBit = BITFLAG_IS_DOOR_IMPASSABLE_Y;
                if (h) {
                    setBit = BITFLAG_IS_DOOR_IMPASSABLE_X;
                }
                synchronized (m._doorMap) {
                    m._doorMap[x - m.getX()][y - m.getY()] = setBit;
                }
            }
        }
        /*
         * l1j.server.GameSystem.Astar.bean.Map m = get_map(map); if(m!=null){
         * int pos = ((m.locX2-m.locX1)*(y-m.locY1)) + (x-m.locX1) +
         * (y-m.locY1); if(pos<m.data_size && pos>=0){ synchronized (m.isdoor) {
         * m.isdoor[pos][h]=flag; } } }
         */
    }

    static public boolean isThroughAttack(int x, int y, int map, int dir) {
        L1V1Map m = (L1V1Map) L1WorldMap.get().getMap((short) map);
        return m.isArrowPassable(x, y, dir);
    }

    static public boolean isMapdynamic(int x, int y, int map) {
        L1V1Map m = (L1V1Map) L1WorldMap.get().getMap((short) map);
        return !m.isPassable(x, y);
    }

    static public void Munido(int x, int y, int map, boolean h, boolean flag) {
        L1V1Map m = (L1V1Map) L1WorldMap.get().getMap((short) map);
        if (m != null) {
            if (!m.isInMap(x, y)) {
                return;
            }
            if (flag) {
                synchronized (m._doorMap) {
                    m._doorMap[x - m.getX()][y - m.getY()] = 0;
                }
            } else {
                byte setBit = BITFLAG_IS_DOOR_IMPASSABLE_Y;
                if (h) {
                    setBit = BITFLAG_IS_DOOR_IMPASSABLE_X;
                }
                synchronized (m._doorMap) {
                    m._doorMap[x - m.getX()][y - m.getY()] = setBit;
                }
            }
        }
    }

    static public boolean Munido(int x, int y, int map, int h) {
        if (h < 0 || h > 7) {
            return false;
        }
        L1V1Map m = (L1V1Map) L1WorldMap.get().getMap((short) map);
        if (m != null) {
            int newX = x + HEADING_TABLE_X[h];
            int newY = y + HEADING_TABLE_Y[h];
            if (x > newX) {
                int doorTile1 = accessDoorTile(newX, y, m);
                int doorTile2 = accessDoorTile(newX, newY, m);
                if (((doorTile1 & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0) || ((doorTile2 & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0)) {
                    return true;
                }
            } else if (x < newX) {
                int doorTileOld = accessDoorTile(x, y, m);
                int doorTileNew = accessDoorTile(newX - 1, newY, m);
                if (((doorTileOld & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0) || ((doorTileNew & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0)) {
                    return true;
                }
            }
            if (y < newY) {
                int doorTile1 = accessDoorTile(x, newY, m);
                int doorTile2 = accessDoorTile(newX, newY, m);
                return ((doorTile1 & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0) || ((doorTile2 & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0);
            } else if (y > newY) {
                int doorTileOld = accessDoorTile(x, y, m);
                int doorTileNew = accessDoorTile(newX, newY + 1, m);
                return ((doorTileOld & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0) || ((doorTileNew & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0);
            }
        }
        return false;
    }

    private static int accessDoorTile(int x, int y, L1V1Map m) {
        if (!m.isInMap(x, y)) {
            return 0;
        }
        synchronized (m._doorMap) {
            return m._doorMap[x - m.getX()][y - m.getY()];
        }
    }

    /**
     * 以對話檔方式輸出 List 中的文本信息<br>
     * 內容順序以添加的順序排列<br>
     * 若內容數量少於2條則不展示任何信息<br>
     *
     * @param pc   執行對象
     * @param html 使用的對話檔<br>
     *             otherInfo: 52行,可自行延伸對話檔內容<br>
     */
    public static void showHtmlInfo(L1PcInstance pc, String html) {
        if (_htmlString.size() < 2) {
            ListMapUtil.clear(_htmlString);
            return;
        }
        try {
            String[] texts = _htmlString.toArray(new String[0]);
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), html, texts));
        } catch (Exception e) {
            _log.error(e.getMessage(), e);
        } finally {
            ListMapUtil.clear(_htmlString);
        }
    }

    public Object getRegion(final Object object) {
        return null;
    }

    /**
     * 世界資料狀態全部重置
     */
    public void clear() {
        _instance = new World();
    }

    /**
     * 加入世界
     *
     * @param object
     */
    public void storeObject(final L1Object object) {
        try {
            if (object == null) {
                throw new NullPointerException();
            }
            _allObjects.put(object.getId(), object);
            if (object instanceof L1ItemInstance) {
                WorldItem.get().put(object.getId(), (L1ItemInstance) object);
            }
            if (object instanceof L1PcInstance) {
                final L1PcInstance pc = (L1PcInstance) object;
                if (pc.isCrown()) {
                    WorldCrown.get().put(pc.getId(), pc);
                } else if (pc.isKnight()) {
                    WorldKnight.get().put(pc.getId(), pc);
                } else if (pc.isElf()) {
                    WorldElf.get().put(pc.getId(), pc);
                } else if (pc.isWizard()) {
                    WorldWizard.get().put(pc.getId(), pc);
                } else if (pc.isDarkelf()) {
                    WorldDarkelf.get().put(pc.getId(), pc);
                } else if (pc.isDragonKnight()) {
                    WorldDragonKnight.get().put(pc.getId(), pc);
                } else if (pc.isIllusionist()) {
                    WorldIllusionist.get().put(pc.getId(), pc);
                } else if (pc.isWarrior()) {
                    WorldWarrior.get().put(pc.getId(), pc);
                }
                _allPlayers.put(pc.getName(), pc);
            }
            // 假人
            if ((object instanceof L1DeInstance)) {
                L1DeInstance de = (L1DeInstance) object;
                WorldDe.get().put(de.getNameId(), de);
            }
            // 陷阱
            if (object instanceof L1TrapInstance) {
                WorldTrap.get().put(object.getId(), (L1TrapInstance) object);
            }
            // 寵物
            if (object instanceof L1PetInstance) {
                WorldPet.get().put(object.getId(), (L1PetInstance) object);
            }
            // 召喚獸
            if (object instanceof L1SummonInstance) {
                WorldSummons.get().put(object.getId(), (L1SummonInstance) object);
            }
            // 魔法娃娃
            if (object instanceof L1DollInstance) {
                WorldDoll.get().put(object.getId(), (L1DollInstance) object);
            }
            // 魔法娃娃
            if (object instanceof L1DollInstance2) {
                WorldDoll2.get().put(object.getId(), (L1DollInstance2) object);
            }
            // Effect(技能物件)
            if (object instanceof L1EffectInstance) {
                WorldEffect.get().put(object.getId(), (L1EffectInstance) object);
            }
            // MOB
            if (object instanceof L1MonsterInstance) {
                WorldMob.get().put(object.getId(), (L1MonsterInstance) object);
            }
            // BOW
            if (object instanceof L1BowInstance) {
                WorldBow.get().put(object.getId(), (L1BowInstance) object);
            }
            // NPC
            if (object instanceof L1NpcInstance) {
                WorldNpc.get().put(object.getId(), (L1NpcInstance) object);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移出世界
     *
     * @param object
     */
    public void removeObject(final L1Object object) {
        // System.out.println("移出世界");
        try {
            if (object == null) {
                throw new NullPointerException();
            }
            _allObjects.remove(object.getId());
            if (object instanceof L1ItemInstance) {
                WorldItem.get().remove(object.getId());
            }
            if (object instanceof L1PcInstance) {
                final L1PcInstance pc = (L1PcInstance) object;
                if (pc.isCrown()) {
                    WorldCrown.get().remove(pc.getId());
                } else if (pc.isKnight()) {
                    WorldKnight.get().remove(pc.getId());
                } else if (pc.isElf()) {
                    WorldElf.get().remove(pc.getId());
                } else if (pc.isWizard()) {
                    WorldWizard.get().remove(pc.getId());
                } else if (pc.isDarkelf()) {
                    WorldDarkelf.get().remove(pc.getId());
                } else if (pc.isDragonKnight()) {
                    WorldDragonKnight.get().remove(pc.getId());
                } else if (pc.isIllusionist()) {
                    WorldIllusionist.get().remove(pc.getId());
                } else if (pc.isWarrior()) {
                    WorldWarrior.get().remove(pc.getId());
                }
                _allPlayers.remove(pc.getName());
            }
            // 假人
            if ((object instanceof L1DeInstance)) {
                L1DeInstance de = (L1DeInstance) object;
                WorldDe.get().remove(de.getNameId());
            }
            // 陷阱
            if (object instanceof L1TrapInstance) {
                WorldTrap.get().remove(object.getId());
            }
            // 寵物
            if (object instanceof L1PetInstance) {
                WorldPet.get().remove(object.getId());
            }
            // 召喚獸
            if (object instanceof L1SummonInstance) {
                WorldSummons.get().remove(object.getId());
            }
            // 魔法娃娃
            if (object instanceof L1DollInstance) {
                WorldDoll.get().remove(object.getId());
            }
            // Effect(技能物件)
            if (object instanceof L1EffectInstance) {
                WorldEffect.get().remove(object.getId());
            }
            // MOB
            if (object instanceof L1MonsterInstance) {
                WorldMob.get().remove(object.getId());
            }
            // BOW
            if (object instanceof L1BowInstance) {
                WorldBow.get().remove(object.getId());
            }
            // NPC
            if (object instanceof L1NpcInstance) {
                WorldNpc.get().remove(object.getId());
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 取回物件
     *
     * @param oID
     * @return
     */
    public L1Object findObject(final int oID) {
        if (oID == 0) {
            return null;
        }
        return _allObjects.get(oID);
    }

    /**
     * 全部L1Object
     *
     * @return
     */
    public Collection<L1Object> getObject() {
        try {
            final Collection<L1Object> vs = _allValues;
            return (vs != null) ? vs : (_allValues = Collections.unmodifiableCollection(_allObjects.values()));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 座標點-遊戲世界地面背包
     *
     * @param x
     * @param y
     * @param map
     * @return
     */
    public L1GroundInventory getInventory(final int x, final int y, final short map) {
        try {
            // 以XY座標值做為地面背包OBJID(避免衝突 轉為負數)
            final int inventoryKey = ((x - 30000) * 10000 + (y - 30000)) * -1;
            // Integer
            if (_visibleObjects.get((int) map) != null) {
                final Object object = _visibleObjects.get((int) map).get(inventoryKey);
                if (object == null) {
                    return new L1GroundInventory(inventoryKey, x, y, map);
                } else {
                    return (L1GroundInventory) object;
                }
            } else {
                _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + map);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 座標點-遊戲世界地面背包
     *
     * @param loc
     * @return
     */
    public L1GroundInventory getInventory(final L1Location loc) {
        return getInventory(loc.getX(), loc.getY(), (short) loc.getMap().getId());
    }

    /**
     * 加入MAP內物件
     *
     * @param object
     */
    public void addVisibleObject(final L1Object object) {
        try {
            if (_visibleObjects.get((int) object.getMapId()) != null) {
                _visibleObjects.get((int) object.getMapId()).put(object.getId(), object);
            } else {
                _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + object.getMapId());
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移除MAP內物件
     *
     * @param object
     */
    public void removeVisibleObject(final L1Object object) {
        try {
            if (_visibleObjects.get((int) object.getMapId()) != null) {
                _visibleObjects.get((int) object.getMapId()).remove(object.getId());
            } else {
                _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + object.getMapId());
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移動MAP內物件
     *
     * @param object
     * @param newMapId
     */
    public void moveVisibleObject(final L1Object object, final int newMapId) {// set_Mapで新しいMapにするまえに呼ぶこと
        try {
            int srcMapId = object.getMapId();
            if (srcMapId != newMapId) {
                // 取回原地圖資料
                if (_visibleObjects.get(srcMapId) != null) {
                    _visibleObjects.get(srcMapId).remove(object.getId());
                } else {
                    _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + srcMapId);
                }
                // 取回新地圖資料
                final ConcurrentHashMap<Integer, L1Object> map = _visibleObjects.get(newMapId);
                if (map != null) {
                    map.put(object.getId(), object);
                } else {
                    _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + newMapId);
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    /*
     * public final Map<String, L1PcInstance> getAllPc() { return
     * this._allPlayers; }
     */

    /**
     * 直線上目標列舉
     *
     * @param src
     * @param target
     * @return
     */
    private HashMap<Integer, Integer> createLineMap(final Point src, final Point target) {
        final HashMap<Integer, Integer> lineMap = new HashMap<>();
        /*
         * http://www2.starcat.ne.jp/~fussy/algo/algo1-1.htmより
         */
        int E;
        int x;
        int y;
        int key;
        int i;
        final int x0 = src.getX();
        final int y0 = src.getY();
        final int x1 = target.getX();
        final int y1 = target.getY();
        final int sx = (x1 > x0) ? 1 : -1;
        final int dx = (x1 > x0) ? x1 - x0 : x0 - x1;
        final int sy = (y1 > y0) ? 1 : -1;
        final int dy = (y1 > y0) ? y1 - y0 : y0 - y1;
        x = x0;
        y = y0;
        /* 傾きが1以下の場合 */
        if (dx >= dy) {
            E = -dx;
            for (i = 0; i <= dx; i++) {
                key = (x << 16) + y;
                lineMap.put(key, key);
                x += sx;
                // E += 2 * dy;
                E += (dy << 1);
                if (E >= 0) {
                    y += sy;
                    // E -= 2 * dx;
                    E -= (dx << 1);
                }
            }
            /* 傾きが1より大きい場合 */
        } else {
            E = -dy;
            for (i = 0; i <= dy; i++) {
                key = (x << 16) + y;
                lineMap.put(key, key);
                y += sy;
                // E += 2 * dx;
                E += (dx << 1);
                if (E >= 0) {
                    x += sx;
                    // E -= 2 * dy;
                    E -= (dy << 1);
                }
            }
        }
        return lineMap;
    }

    /**
     * 直線距離目標列舉
     *
     * @param src
     * @param target
     * @return
     */
    public ArrayList<L1Object> getVisibleLineObjects(final L1Object src, final L1Object target) {
        try {
            final HashMap<Integer, Integer> lineMap = this.createLineMap(src.getLocation(), target.getLocation());
            final int mapId = target.getMapId();
            final ArrayList<L1Object> result = new ArrayList<>();
            // 取回原地圖資料
            if (_visibleObjects.get(mapId) == null) {
                _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + mapId);
                return result;
            }
            for (final L1Object element : _visibleObjects.get(mapId).values()) {
                if (element.equals(src)) {
                    continue;
                }
                // 副本ID不相等
                if (src.get_showId() != element.get_showId()) {
                    continue;
                }
                final int key = (element.getX() << 16) + element.getY();
                if (lineMap.containsKey(key)) {
                    result.add(element);
                }
            }
            lineMap.clear();
            return result;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 指定範圍目標列舉
     *
     * @param object
     * @param heading
     * @param width
     * @param height
     * @return
     */
    public ArrayList<L1Object> getVisibleBoxObjects(final L1Object object, final int heading, final int width, final int height) {
        final int x = object.getX();
        final int y = object.getY();
        final int mapId = object.getMapId();
        final L1Location location = object.getLocation();
        final ArrayList<L1Object> result = new ArrayList<>();
        final int[] headingRotate = {6, 7, 0, 1, 2, 3, 4, 5};
        final double cosSita = Math.cos((headingRotate[heading] * Math.PI) / 4);
        final double sinSita = Math.sin((headingRotate[heading] * Math.PI) / 4);
        // 取回原地圖資料
        if (_visibleObjects.get(mapId) == null) {
            _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + mapId);
            return result;
        }
        for (final L1Object element : _visibleObjects.get(mapId).values()) {
            if (element.equals(object)) {
                continue;
            }
            // 副本ID不相等
            if (object.get_showId() != element.get_showId()) {
                continue;
            }
            if (mapId != element.getMapId()) {
                continue;
            }
            // 同じ座標に重なっている場合は範圍內とする
            if (location.isSamePoint(element.getLocation())) {
                result.add(element);
                continue;
            }
            final int distance = location.getTileLineDistance(element.getLocation());
            // 直線距離が高さ、幅どちらよりも大きい場合、計算するまでもなく範圍外
            if ((distance > height) && (distance > width)) {
                continue;
            }
            // objectの位置を原點とするための座標補正
            final int x1 = element.getX() - x;
            final int y1 = element.getY() - y;
            // Z軸回轉させ角度を0度にする。
            final int rotX = (int) Math.round(x1 * cosSita + y1 * sinSita);
            final int rotY = (int) Math.round(-x1 * sinSita + y1 * cosSita);
            final int xmin = 0;
            final int ymin = -width;
            // 奧行きが射程とかみ合わないので直線距離で判定するように變更。
            // if (rotX > xmin && rotX <= xmax && rotY >= ymin && rotY <=
            // ymax) {
            if ((rotX > xmin) && (distance <= height) && (rotY >= ymin) && (rotY <= width)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * 畫面可見範圍Objects
     *
     * @param object
     * @return
     */
    public ArrayList<L1Object> getVisibleObjects(final L1Object object) {
        return getVisibleObjects(object, -1);
    }

    /**
     * 相對物件在畫面範圍內
     *
     * @param src
     * @param tg
     * @return
     */
    public boolean getVisibleObjects(final L1Object src, final L1Object tg) {
        // 副本ID不相等
        if (src.get_showId() != tg.get_showId()) {
            return false;
        }
        // 判斷對像MAP不相等
        if (src.getMapId() != tg.getMapId()) {
            return false;
        }
        // 畫面範圍內
        return src.getLocation().isInScreen(tg.getLocation());
    }

    /**
     * 範圍物件
     *
     * @param object 原始物件
     * @param radius 範圍 -1:全視窗 0:重疊 其他:範圍
     * @return
     */
    public ArrayList<L1Object> getVisibleObjects(final L1Object object, final int radius) {
        final L1Map map = object.getMap();
        final Point pt = object.getLocation();
        final ArrayList<L1Object> result = new ArrayList<>();
        // 取回原地圖資料
        if (_visibleObjects.get(map.getId()) == null) {
            _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + map.getId());
            return result;
        }
        for (final L1Object element : _visibleObjects.get(map.getId()).values()) {
            // 判斷對象是自己
            if (element.equals(object)) {
                continue;
            }
            // 副本ID不相等
            if (object.get_showId() != element.get_showId()) {
                if (element instanceof L1MerchantInstance) {// 對話NPC
                    continue;
                }
                if (element instanceof L1DwarfInstance) {// 倉庫NPC
                    continue;
                }
            }
            // 判斷對像MAP不相等
            if (map != element.getMap()) {
                continue;
            }
            switch (radius) {
                case -1:
                    if (pt.isInScreen(element.getLocation())) {
                        result.add(element);
                    }
                    break;
                case 0:
                    if (pt.isSamePoint(element.getLocation())) {
                        result.add(element);
                    }
                    break;
                default:
                    int r = pt.getTileLineDistance(element.getLocation());
                    if (r <= radius) {
                        result.add(element);
                    }
                    break;
            }
        }
        return result;
    }

    /**
     * 範圍物件(PC集體傳送使用)
     *
     * @param loc    原始座標
     * @param radius 範圍
     * @param showid 副本ID
     * @return
     */
    public ArrayList<L1Object> getVisiblePoint(final L1Location loc, final int radius, final int showid) {
        final ArrayList<L1Object> result = new ArrayList<>();
        final int mapId = loc.getMapId(); // ループ內で呼ぶと重いため
        // 取回原地圖資料
        if (_visibleObjects.get(mapId) == null) {
            _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + mapId);
            return result;
        }
        for (final L1Object element : _visibleObjects.get(mapId).values()) {
            if (mapId != element.getMapId()) {
                continue;
            }
            // 副本ID不相等
            if (showid != element.get_showId()) {
                continue;
            }
            if (loc.getTileLineDistance(element.getLocation()) <= radius) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * 可見範圍PC物件(一般召喚使用 不考慮副本ID)
     *
     * @param loc       原始座標
     * @param //*showId
     * @return
     */
    public ArrayList<L1PcInstance> getVisiblePc(final L1Location loc) {
        final ArrayList<L1PcInstance> result = new ArrayList<>();
        final int mapId = loc.getMapId(); // ループ內で呼ぶと重いため
        // 取回原地圖資料
        if (_visibleObjects.get(mapId) == null) {
            _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + mapId);
            return result;
        }
        for (final L1PcInstance element : this._allPlayers.values()) {
            if (mapId != element.getMapId()) {
                continue;
            }
            if (loc.isInScreen(element.getLocation())) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * 畫面可見人物
     *
     * @param object 原始執行物件
     * @return
     */
    public ArrayList<L1PcInstance> getVisiblePlayer(final L1Object object) {
        return this.getVisiblePlayer(object, -1);
    }

    /**
     * 畫面可見人物
     *
     * @param object 原始執行物件
     * @param radius -1:19格範圍 0:座標重疊 其它:指定範圍
     * @return
     */
    public ArrayList<L1PcInstance> getVisiblePlayer(final L1Object object, final int radius) {
        final int map = object.getMapId();
        final Point pt = object.getLocation();
        final ArrayList<L1PcInstance> result = new ArrayList<>();
        for (final L1PcInstance element : _allPlayers.values()) {
            if (element.equals(object)) {
                continue;
            }
            if (map != element.getMapId()) {
                continue;
            }
            // 副本ID不相等
            if (object.get_showId() != element.get_showId()) {
                continue;
            }
            switch (radius) {
                case -1:
                    if (pt.isInScreen(element.getLocation())) {
                        result.add(element);
                    }
                    break;
                case 0:
                    if (pt.isSamePoint(element.getLocation())) {
                        result.add(element);
                    }
                    break;
                default:
                    if (pt.getTileLineDistance(element.getLocation()) <= radius) {
                        result.add(element);
                    }
                    break;
            }
        }
        return result;
    }

    /**
     * 指定目標物件 獲取可見範圍PC物件
     *
     * @param object 執行者
     * @param target 指定物件
     * @return
     */
    public ArrayList<L1PcInstance> getVisiblePlayerExceptTargetSight(final L1Object object, final L1Object target) {
        final int map = object.getMapId();
        final Point objectPt = object.getLocation();
        final Point targetPt = target.getLocation();
        final ArrayList<L1PcInstance> result = new ArrayList<>();
        for (final L1PcInstance element : _allPlayers.values()) {
            if (element.equals(object)) {
                continue;
            }
            if (map != element.getMapId()) {
                continue;
            }
            // 副本ID不相等
            if (target.get_showId() != element.get_showId()) {
                continue;
            }
            if (Config.PC_RECOGNIZE_RANGE == -1) {
                if (objectPt.isInScreen(element.getLocation())) {
                    if (!targetPt.isInScreen(element.getLocation())) {
                        result.add(element);
                    }
                }
            } else {
                if (objectPt.getTileLineDistance(element.getLocation()) <= Config.PC_RECOGNIZE_RANGE) {
                    if (targetPt.getTileLineDistance(element.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
                        result.add(element);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 傳回目標物件 指定範圍內物件 排除原始物件
     *
     * @param object 原始物件
     * @param target 目標物件
     * @param radius 範圍
     * @return
     */
    public ArrayList<L1PcInstance> getVisiblePlayerExceptTargetSight(final L1Object object, final L1Object target, final int radius) {
        final int map = object.getMapId();
        final Point objectPt = object.getLocation();
        final Point targetPt = target.getLocation();
        final ArrayList<L1PcInstance> result = new ArrayList<>();
        for (final L1PcInstance element : _allPlayers.values()) {
            if (element.equals(object)) {
                continue;
            }
            if (map != element.getMapId()) {
                continue;
            }
            // 副本ID不相等
            if (target.get_showId() != element.get_showId()) {
                continue;
            }
            if (objectPt.getTileLineDistance(element.getLocation()) <= radius) {
                if (targetPt.getTileLineDistance(element.getLocation()) <= radius) {
                    result.add(element);
                }
            }
        }
        return result;
    }

    /**
     * object 畫面內可見範圍物件取回
     *
     * @param object
     * @return
     */
    public ArrayList<L1PcInstance> getRecognizePlayer(final L1Object object) {
        return this.getVisiblePlayer(object, Config.PC_RECOGNIZE_RANGE);
    }

    public ArrayList<L1PcInstance> getI2MPlayer(final L1Object object) {  // SRC0808
        return this.getVisiblePlayer(object, ConfigSkillWizard.Immune_TO_Turn2);
    }

    /**
     * 線上抽獎系統 -> 新增
     *
     * @return
     */
    public L1PcInstance[] getAllPlayersToArray() {
        return _allPlayers.values().toArray(new L1PcInstance[0]);
    }

    /**
     * 全部線上玩家
     *
     * @return
     */
    public Collection<L1PcInstance> getAllPlayers() {
        try {
            final Collection<L1PcInstance> vs = _allPlayerValues;
            return (vs != null) ? vs : (_allPlayerValues = Collections.unmodifiableCollection(_allPlayers.values()));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 以人物名稱傳回人物數據(人物在線上)
     *
     * @param name 人物名稱(不區分大小寫)
     * @return
     */
    public L1PcInstance getPlayer(final String name) {
        if (_allPlayers.containsKey(name)) {
            return _allPlayers.get(name);
        }
        for (final L1PcInstance each : getAllPlayers()) {
            if (each.getName().equalsIgnoreCase(name)) {
                return each;
            }
        }
        return null;
    }

    /**
     * 該人物在可見範圍內
     *
     * @param name 人物名稱(不區分大小寫)
     * @return
     */
    public boolean get_pc(final L1PcInstance pc, final String name) {
        L1PcInstance tg = _allPlayers.get(name);
        if (tg != null) {
            return pc.getLocation().isInScreen(tg.getLocation());
        }
        return false;
    }

    public final Map<Integer, L1Object> getAllVisibleObjects() {
        return _allObjects;
    }

    /**
     * 全部地圖(MAPID為KEY)世界資料
     *
     * @return
     */
    public final HashMap<Integer, ConcurrentHashMap<Integer, L1Object>> getVisibleObjects() {
        return _visibleObjects;
    }

    /**
     * 指定地圖世界資料
     *
     * @param mapId
     * @return
     */
    public final ConcurrentHashMap<Integer, L1Object> getVisibleObjects(final int mapId) {
        return _visibleObjects.get(mapId);
    }

    /**
     * 世界天氣狀況
     *
     * @return
     */
    public int getWeather() {
        return _weather;
    }

    /**
     * 世界天氣狀況
     *
     * @param weather
     */
    public void setWeather(final int weather) {
        _weather = weather;
    }

    /**
     * 允許廣播
     *
     * @param flag
     */
    public void set_worldChatElabled(final boolean flag) {
        _worldChatEnabled = flag;
    }

    /**
     * 允許廣播
     *
     * @return true允許 false不允許
     */
    public boolean isWorldChatElabled() {
        return _worldChatEnabled;
    }

    /**
     * 計算貢獻度
     *
     * @return 这里是反转方法
     */
    public boolean isProcessingContributionTotal() {
        return !_processingContributionTotal;
    }

    /**
     * 計算貢獻度
     *
     * @param flag
     */
    public void setProcessingContributionTotal(final boolean flag) {
        _processingContributionTotal = flag;
    }

    /**
     * 送出全體封包
     *
     * @param packet
     */
    public void broadcastPacketToAlldroplist(final ServerBasePacket packet) {
        for (final L1PcInstance pc : getAllPlayers()) {
            if (pc.droplist()) {
                pc.sendPackets(packet);
            }
        }
    }

    /**
     * 送出全體封包
     *
     * @param packet
     */
    public void broadcastPacketToAllkill(final ServerBasePacket packet) {
        for (final L1PcInstance pc : getAllPlayers()) {
            if (pc.kill()) {
                pc.sendPackets(packet);
            }
        }
    }

    /**
     * 送出全體封包
     *
     * @param packet
     */
    public void broadcastPacketToAll(final ServerBasePacket packet) {
        for (final L1PcInstance pc : getAllPlayers()) {
            pc.sendPackets(packet);
        }
    }

    /**
     * 送出全體訊息封包
     *
     * @param message
     */
    public void broadcastServerMessage(final String message) {
        broadcastPacketToAll(new S_SystemMessage(message));
    }

    public void broadcastServerMessage_green(final String message) {
        broadcastPacketToAll(new S_PacketBoxGree(2, message));
    }

    /**
     * 範圍物件(死鬥競技場用)
     *
     * @param loc    原始座標
     * @param radius 範圍
     * @return
     */
    public ArrayList<L1Object> getVisiblePoint(L1Location loc, int radius) {
        ArrayList<L1Object> result = new ArrayList<>();
        int mapId = loc.getMapId(); // ループ內で呼ぶと重いため
        // 取回原地圖資料
        if (_visibleObjects.get(mapId) == null) {
            _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + mapId);
            return result;
        }
        if (mapId <= MAX_MAP_ID) {
            for (L1Object element : _visibleObjects.get(mapId).values()) {
                if (mapId != element.getMapId()) {
                    continue;
                }
                if (loc.getTileLineDistance(element.getLocation()) <= radius) {
                    result.add(element);
                }
            }
        }
        return result;
    }

    /**
     * 添加地圖
     */
    public void addMap(int mapid) {
        final ConcurrentHashMap<Integer, L1Object> map = new ConcurrentHashMap<>();
        _visibleObjects.put(mapid, map);
    }

    /**
     * 清除地圖內物品
     */
    public void closeMap(int mapid) {
        for (Map.Entry<Integer, L1Object> entry : _allObjects.entrySet()) {
            L1Object object = entry.getValue();
            if (object.getMapId() == mapid) {
                if (object instanceof L1DoorInstance) {
                    DoorSpawnTable.get().removeDoor((L1DoorInstance) object);
                }
                removeObject(object);
            }
        }
        final ConcurrentHashMap<Integer, L1Object> map = new ConcurrentHashMap<>();
        _visibleObjects.put(mapid, map);
    }

    // 抽抽樂
    public void broadcastPacketToPandora(ServerBasePacket packet) {
        for (Iterator<L1PcInstance> iter = getAllPlayers().iterator(); iter.hasNext(); ) {
            L1PcInstance tgpc = (L1PcInstance) iter.next();
            if ((check(tgpc)) && (tgpc.isShow_Open_PandoraMsg())) {
                tgpc.sendPackets(packet);
            }
        }
    }
	/* *
	 * 底比斯迷你圍塔遊戲
	 * @param packet
	 * /
    public void broadcastPacketToMiniSiege(final ServerBasePacket packet) {
        for (L1PcInstance pc : getAllPlayers()) {
            if (pc != null) {
                pc.sendPackets(packet);
                pc.isSiege = true;
            }
        }
    }*/

    public List<L1PcInstance> getRandomPlayers(final int count) {
        try {
            final Collection<L1PcInstance> tempList = getAllPlayers();
            if (!tempList.isEmpty()) {
                final L1PcInstance[] userList = tempList.toArray(new L1PcInstance[0]);
                final List<L1PcInstance> newList = new ArrayList<>();
                final Random _random = new Random();
                while (newList.size() < count) {
                    final L1PcInstance each_pc = userList[_random.nextInt(userList.length)];
                    if (each_pc != null && !newList.contains(each_pc)) {
                        newList.add(each_pc);
                    }
                }
                return newList;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public Collection<L1ItemInstance> getAllItem() {
        Collection<L1ItemInstance> vs = _allItemValues;
        return (vs != null) ? vs : (_allItemValues = Collections.unmodifiableCollection(_allItems.values()));
    }

    /**
     * 範圍物件
     *
     * @param object 原始物件
     * @param radius 範圍 -1:全視窗 0:重疊 其他:範圍
     * @return
     */
    public ArrayList<L1Object> getVisibleObjectsForAuto(final L1Object object, final int radius) {
        final L1Map map = object.getMap();
        final Point pt = object.getLocation();
        final ArrayList<L1Object> result = new ArrayList<>();
        if (_visibleObjects.get(map.getId()) == null) {
            _log.error("遊戲世界儲存中心並未建立該地圖編號資料檔案: " + map.getId());
            return result;
        }
        for (final L1Object element : _visibleObjects.get(map.getId()).values()) {
            if (element.equals(object)) {
                continue;
            }
            if (map != element.getMap()) {
                continue;
            }
            int r = pt.getTileLineDistance(element.getLocation());
            if (r <= radius) {
                result.add(element);
            }
        }
        return result;
    }

    public List<String> getHtmlString() {
        return _htmlString;
    }
}
