package com.lineage.server.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ListMapUtil {
    private static final Log _log = LogFactory.getLog(ListMapUtil.class);

    public static void clear(Queue<?> queue) {
        try {
            if (queue != null) {
                queue.clear();
            }
        } catch (Exception e) {
            _log.error("清空Queue發生異常", e);
        }
    }

    public static void clear(Map<?, ?> map) {
        try {
            if (map != null) {
                map.clear();
            }
        } catch (Exception e) {
            _log.error("清空Map發生異常", e);
        }
    }

    public static void clear(HashMap<?, ?> map) {
        try {
            if (map != null) {
                map.clear();
            }
        } catch (Exception e) {
            _log.error("清空Map發生異常", e);
        }
    }

    public static void clear(ConcurrentHashMap<?, ?> map) {
        try {
            if (map != null) {
                map.clear();
            }
        } catch (Exception e) {
            _log.error("清空Map發生異常", e);
        }
    }

    public static void clear(ArrayList<?> list) {
        try {
            if (list != null) {
                list.clear();
            }
        } catch (Exception e) {
            _log.error("清空ArrayList發生異常", e);
        }
    }

    public static void clear(List<?> list) {
        try {
            if (list != null) {
                list.clear();
            }
        } catch (Exception e) {
            _log.error("清空List發生異常", e);
        }
    }
}
