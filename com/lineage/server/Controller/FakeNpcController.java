package com.lineage.server.Controller;

import com.add.L1Config;
import com.lineage.server.utils.L1SpawnUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 假人系統
 */
public class FakeNpcController implements Runnable {
    public static final Log _log = LogFactory.getLog(FakeNpcController.class);
    private static final Map<Integer, Object[]> map = new ConcurrentHashMap<>();
    private static final List<Integer> task = new ArrayList<>();
    private static int _time = 0;
    private static int _count = 0;
    private static FakeNpcController ins;

    private FakeNpcController() {
    }

    public static FakeNpcController getInstance() {
        if (ins == null) {
            ins = new FakeNpcController();
        }
        return ins;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                if (L1Config._9319 == 0) {
                    continue;
                }
                if (_count >= L1Config._9321) {
                    continue;
                }
                _time++;
                if (_time >= L1Config._9320) {
                    L1SpawnUtil.spawnFakeNpc(null, (short) L1Config._9322, L1Config._9323, L1Config._9324, L1Config._9325, L1Config._9326);
                    _time = 0;
                    _count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
