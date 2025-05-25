package com.lineage.server.utils;

import com.lineage.server.templates.L1NewMap;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class NewMapUtil {
    private static ConcurrentHashMap<Integer, ArrayList<L1NewMap>> M = new ConcurrentHashMap<Integer, ArrayList<L1NewMap>>();

    @SuppressWarnings("resource")
    public static void load(String path) throws IOException {
        File f = new File(path);
        if ((!f.isDirectory()) || (!f.exists())) {
            throw new NullPointerException("錯誤的地圖路徑或地圖不存在！");
        }
        FileChannel fc = null;
        for (File map : f.listFiles(new FileFilter() {
            public final boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        })) {
            int mapId = 0;
            try {
                mapId = Integer.parseInt(map.getName());
            } catch (NumberFormatException e) {
                continue;
            }
            ArrayList<L1NewMap> maps = new ArrayList<L1NewMap>();
            M.put(Integer.valueOf(mapId), maps);
            for (File fileMap : map.listFiles(new FileFilter() {
                public final boolean accept(File pathname) {
                    return (pathname.isFile()) && (pathname.getName().toLowerCase().endsWith(".bin"));
                }
            })) {
                ByteBuffer buf = ByteBuffer.allocate(4096);
                fc = new FileInputStream(fileMap).getChannel();
                fc.read(buf);
                fc.close();
                int x = Integer.parseInt(fileMap.getName().substring(0, 4), 16) & 0xFFFF;
                int y = Integer.parseInt(fileMap.getName().substring(4, 8), 16) & 0xFFFF;
                maps.add(new L1NewMap(x, y, buf.array()));
            }
        }
        System.out.println(NewMapUtil.class.getName() + ": 加載 " + M.size() + " 張地圖。");
    }

    public static ArrayList<L1NewMap> getBlock(int mapId) {
        if (M.containsKey(Integer.valueOf(mapId))) {
            return (ArrayList<L1NewMap>) M.get(Integer.valueOf(mapId));
        }
        return null;
    }
}
