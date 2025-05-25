package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;

public class L1SetMap implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1SetMap.class);

    public static L1CommandExecutor getInstance() {
        return new L1SetMap();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            if (tok.hasMoreTokens()) {
                if (tok.nextToken().equalsIgnoreCase("1")) {
                    if (tok.hasMoreTokens()) {
                        int and = Integer.parseInt(tok.nextToken());
                        if (tok.hasMoreTokens()) {
                            int and2 = Integer.parseInt(tok.nextToken());
                            set_src_map(15, pc, and, and2);
                        } else {
                            set_src_map(15, pc, and);
                        }
                    } else {
                        set_src_map(8, pc);
                    }
                }
            } else {
                set_map(15, pc);
            }
        } catch (Exception e) {
            _log.error("錯誤的GM指令格式: " + getClass().getSimpleName() + " 執行的GM:" + pc.getName());
            pc.sendPackets(new S_ServerMessage(261));
        }
    }

    private void set_src_map(int hc, L1PcInstance pc, int and, int and2) {
        int x = pc.getX();
        int y = pc.getY();
        int x1 = x - hc;
        int y1 = y - hc;
        int x2 = x + hc;
        int y2 = y + hc;
        int rows = x2 - x1;
        int columns = y2 - y1;
        L1Map map = pc.getMap();
        System.out.println("==============================================================================");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int cx = x1 + i;
                int cy = y1 + j;
                if ((cx == x) && (cy == y)) {
                    System.out.print("[]");
                } else {
                    int gab = map.getOriginalTile(cx, cy);
                    int a = gab & and;
                    int a2 = gab & and2;
                    if ((a == 0) && (a2 == 0)) {
                        System.out.print("##");
                    } else {
                        System.out.print("  ");
                    }
                }
            }
            System.out.print("\n");
        }
    }

    private void set_src_map(int hc, L1PcInstance pc, int and) {
        int x = pc.getX();
        int y = pc.getY();
        int x1 = x - hc;
        int y1 = y - hc;
        int x2 = x + hc;
        int y2 = y + hc;
        int rows = x2 - x1;
        int columns = y2 - y1;
        L1Map map = pc.getMap();
        System.out.println("==============================================================================");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int cx = x1 + i;
                int cy = y1 + j;
                if ((cx == x) && (cy == y)) {
                    System.out.print("[]");
                } else {
                    int gab = map.getOriginalTile(cx, cy);
                    int a = gab & and;
                    if (a == 0) {
                        System.out.print("##");
                    } else {
                        System.out.print("  ");
                    }
                }
            }
            System.out.print("\n");
        }
    }

    private void set_src_map(int hc, L1PcInstance pc) {
        int x = pc.getX();
        int y = pc.getY();
        int x1 = x - hc;
        int y1 = y - hc;
        int x2 = x + hc;
        int y2 = y + hc;
        int rows = x2 - x1;
        int columns = y2 - y1;
        L1Map map = pc.getMap();
        System.out.println("==============================================================================");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int cx = x1 + i;
                int cy = y1 + j;
                if ((cx == x) && (cy == y)) {
                    System.out.print("[] ");
                } else {
                    int gab = map.getOriginalTile(cx, cy);
                    if (gab == 0) {
                        System.out.print("## ");
                    } else {
                        System.out.print((gab < 10 ? "0" : "") + gab + " ");
                    }
                }
            }
            System.out.print("\n");
        }
    }

    private void set_map(int hc, L1PcInstance pc) {
        int x = pc.getX();
        int y = pc.getY();
        int x1 = x - hc;
        int y1 = y - hc;
        int x2 = x + hc;
        int y2 = y + hc;
        int rows = x2 - x1;
        int columns = y2 - y1;
        L1Map map = pc.getMap();
        System.out.println("==============================================================================");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int cx = x1 + i;
                int cy = y1 + j;
                if ((cx == x) && (cy == y)) {
                    System.out.print("[]");
                } else if (!map.isPassable(cx, cy, pc)) {
                    System.out.print("##");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.print("\n");
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.command.executor.L1SetMap JD-Core Version: 0.6.2
 */