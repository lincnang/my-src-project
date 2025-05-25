package com.lineage.server.model;

import com.lineage.server.templates.L1HouseLocTmp;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class L1HouseLocation {
    private static final Log _log = LogFactory.getLog(L1HouseLocation.class);
    private static final int[] TELEPORT_LOC_MAPID = {4, 4, 4, 350};
    private static final Point[] TELEPORT_LOC_GIRAN = {new Point(33419, 32810), new Point(33343, 32723), new Point(33553, 32712), new Point(32702, 32842)};
    private static final Point[] TELEPORT_LOC_HEINE = {new Point(33604, 33236), new Point(33649, 33413), new Point(33553, 32712), new Point(32702, 32842)};
    private static final Point[] TELEPORT_LOC_ADEN = {new Point(33966, 33253), new Point(33921, 33177), new Point(33553, 32712), new Point(32702, 32842)};
    private static final Point[] TELEPORT_LOC_GLUDIN = {new Point(32628, 32807), new Point(32623, 32729), new Point(33553, 32712), new Point(32702, 32842)};
    private static final Map<Integer, L1HouseLocTmp> _houseLoc = new HashMap<Integer, L1HouseLocTmp>();

    public static void put(Integer e, L1HouseLocTmp loc) {
        _houseLoc.put(e, loc);
    }

    public static boolean isInHouse(short mapid) {
        switch (mapid) {
            case 5001:
            case 5002:
            case 5003:
            case 5004:
            case 5005:
            case 5006:
            case 5007:
            case 5008:
            case 5009:
            case 5010:
            case 5011:
            case 5012:
            case 5013:
            case 5014:
            case 5015:
            case 5016:
            case 5017:
            case 5018:
            case 5019:
            case 5020:
            case 5021:
            case 5022:
            case 5023:
            case 5024:
            case 5025:
            case 5026:
            case 5027:
            case 5028:
            case 5029:
            case 5030:
            case 5031:
            case 5032:
            case 5033:
            case 5034:
            case 5035:
            case 5036:
            case 5037:
            case 5038:
            case 5039:
            case 5040:
            case 5041:
            case 5042:
            case 5043:
            case 5044:
            case 5045:
            case 5046:
            case 5047:
            case 5048:
            case 5049:
            case 5050:
            case 5051:
            case 5052:
            case 5053:
            case 5054:
            case 5055:
            case 5056:
            case 5057:
            case 5058:
            case 5059:
            case 5060:
            case 5061:
            case 5062:
            case 5063:
            case 5064:
            case 5065:
            case 5066:
            case 5067:
            case 5068:
            case 5069:
            case 5070:
            case 5071:
            case 5072:
            case 5073:
            case 5074:
            case 5075:
            case 5076:
            case 5077:
            case 5078:
            case 5079:
            case 5080:
            case 5081:
            case 5082:
            case 5083:
            case 5084:
            case 5085:
            case 5086:
            case 5087:
            case 5088:
            case 5089:
            case 5090:
            case 5091:
            case 5092:
            case 5093:
            case 5094:
            case 5095:
            case 5096:
            case 5097:
            case 5098:
            case 5099:
            case 5100:
            case 5101:
            case 5102:
            case 5103:
            case 5104:
            case 5105:
            case 5106:
            case 5107:
            case 5108:
            case 5109:
            case 5110:
            case 5111:
            case 5112:
            case 5113:
            case 5114:
            case 5115:
            case 5116:
            case 5117:
            case 5118:
            case 5119:
            case 5120:
            case 5121:
            case 5122:
            case 5123:
                return true;
        }
        return false;
    }

    public static boolean isInHouse(int locx, int locy, short mapid) {
        for (Integer houseId : _houseLoc.keySet()) {
            if (isInHouseLoc(houseId.intValue(), locx, locy, mapid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInHouseLoc(int houseId, int locx, int locy, short mapid) {
        try {
            L1HouseLocTmp loc = (L1HouseLocTmp) _houseLoc.get(new Integer(houseId));
            if (loc != null) {
                int locx1 = loc.get_locx1();
                int locx2 = loc.get_locx2();
                int locy1 = loc.get_locy1();
                int locy2 = loc.get_locy2();
                int locx3 = loc.get_locx3();
                int locx4 = loc.get_locx4();
                int locy3 = loc.get_locy3();
                int locy4 = loc.get_locy4();
                int locmapid = loc.get_mapid();
                int basement = loc.get_basement();
                if ((locx >= locx1) && (locx <= locx2) && (locy >= locy1) && (locy <= locy2) && (mapid == locmapid)) {
                    return true;
                }
                if ((locx3 != 0) && (locx >= locx3) && (locx <= locx4) && (locy >= locy3) && (locy <= locy4) && (mapid == locmapid)) {
                    return true;
                }
                if ((basement != 0) && (mapid == basement)) {
                    return true;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public static int[] getHouseLoc(int houseId) {
        int[] loc = new int[3];
        try {
            L1HouseLocTmp locTmp = (L1HouseLocTmp) _houseLoc.get(new Integer(houseId));
            if (loc != null) {
                loc[0] = locTmp.get_homelocx();
                loc[1] = locTmp.get_homelocy();
                loc[2] = locTmp.get_mapid();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return loc;
    }

    public static int[] getBasementLoc(int houseId) {
        int[] loc = new int[3];
        if ((houseId >= 262145) && (houseId <= 262189)) {
            loc[0] = 32765;
            loc[1] = 32832;
            loc[2] = (houseId - 257077);
        } else if ((houseId >= 327681) && (houseId <= 327691)) {
            loc[0] = 32766;
            loc[1] = 32829;
            loc[2] = (houseId - 322568);
        } else if ((houseId >= 524289) && (houseId <= 524294)) {
            loc = getHouseLoc(houseId);
        }
        return loc;
    }

    public static int[] getHouseTeleportLoc(int houseId, int number) {
        int[] loc = new int[3];
        if ((houseId >= 262145) && (houseId <= 262189)) {
            loc[0] = TELEPORT_LOC_GIRAN[number].getX();
            loc[1] = TELEPORT_LOC_GIRAN[number].getY();
            loc[2] = TELEPORT_LOC_MAPID[number];
        } else if ((houseId >= 327681) && (houseId <= 327691)) {
            loc[0] = TELEPORT_LOC_HEINE[number].getX();
            loc[1] = TELEPORT_LOC_HEINE[number].getY();
            loc[2] = TELEPORT_LOC_MAPID[number];
        } else if ((houseId >= 458753) && (houseId <= 458819)) {
            loc[0] = TELEPORT_LOC_ADEN[number].getX();
            loc[1] = TELEPORT_LOC_ADEN[number].getY();
            loc[2] = TELEPORT_LOC_MAPID[number];
        } else if ((houseId >= 524289) && (houseId <= 524294)) {
            loc[0] = TELEPORT_LOC_GLUDIN[number].getX();
            loc[1] = TELEPORT_LOC_GLUDIN[number].getY();
            loc[2] = TELEPORT_LOC_MAPID[number];
        }
        return loc;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1HouseLocation JD-Core Version: 0.6.2
 */