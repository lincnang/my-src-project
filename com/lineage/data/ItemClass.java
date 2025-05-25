package com.lineage.data;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.BinaryOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ItemClass {
    private static final Log _log = LogFactory.getLog(ItemClass.class);
    private static final Map<Integer, ItemExecutor> _classList = new HashMap<Integer, ItemExecutor>();
    private static ItemClass _instance;

    public static ItemClass get() {
        if (_instance == null) {
            _instance = new ItemClass();
        }
        return _instance;
    }

    public void addList(int itemid, String className, int mode) {
        if (className.equals("0")) {
            return;
        }
        try {
            String newclass = className;
            String[] set = (String[]) null;
            if (className.indexOf(" ") != -1) {
                set = className.split(" ");
                newclass = set[0];
            }
            StringBuilder stringBuilder = new StringBuilder();
            switch (mode) {
                case 0:
                    stringBuilder.append("com.lineage.data.item_etcitem.");
                    break;
                case 1:
                    stringBuilder.append("com.lineage.data.item_weapon.");
                    break;
                case 2:
                    stringBuilder.append("com.lineage.data.item_armor.");
            }
            stringBuilder.append(newclass);
            Class<?> cls = Class.forName(stringBuilder.toString());
            ItemExecutor exe = (ItemExecutor) cls.getMethod("get", new Class[0]).invoke(null, new Object[0]);
            if (set != null) {
                exe.set_set(set);
            }
            _classList.put(new Integer(itemid), exe);
        } catch (ClassNotFoundException e) {
            String error = "發生[道具檔案]錯誤, 檢查檔案是否存在:" + className + " ItemId:" + itemid;
            _log.error(error);
            DataError.isError(_log, error, e);
        } catch (IllegalArgumentException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (IllegalAccessException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (InvocationTargetException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (SecurityException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (NoSuchMethodException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public BinaryOutputStream status(L1ItemInstance item) {
        if (item == null) {
            return null;
        }
        try {
            ItemExecutor exe = (ItemExecutor) _classList.get(new Integer(item.getItemId()));
            if (exe != null) {
                return exe.itemStatus(item);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public void item(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        try {
            ItemExecutor exe = (ItemExecutor) _classList.get(new Integer(item.getItemId()));
            if (exe != null) {
                exe.execute(data, pc, item);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void item_weapon(boolean equipped, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        try {
            ItemExecutor exe = (ItemExecutor) _classList.get(new Integer(item.getItemId()));
            if (exe != null) {
                int[] data = new int[1];
                data[0] = (equipped ? 1 : 0);
                exe.execute(data, pc, item);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void item_armor(boolean equipped, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        try {
            ItemExecutor exe = (ItemExecutor) _classList.get(new Integer(item.getItemId()));
            if (exe != null) {
                int[] data = new int[1];
                data[0] = (equipped ? 1 : 0);
                exe.execute(data, pc, item);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.ItemClass JD-Core Version: 0.6.2
 */