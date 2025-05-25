package com.lineage.list;

import com.lineage.server.utils.StreamUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 人物名稱禁用字元
 *
 * @author dexc
 */
public class BadNamesList {
    private static final Log _log = LogFactory.getLog(BadNamesList.class);
    private static BadNamesList _instance;
    private ArrayList<String> _nameList = new ArrayList<>();

    public static BadNamesList get() {
        if (_instance == null) {
            _instance = new BadNamesList();
        }
        return _instance;
    }

    public void load() {
        LineNumberReader lnr = null;
        try {
            final File file = new File("data/badnames.txt");
            final FileInputStream fileIn = new FileInputStream(file);
            final InputStreamReader inputStream = new InputStreamReader(fileIn, "utf8");
            lnr = new LineNumberReader(inputStream);
            boolean isWhile = false;
            String line = null;
            while ((line = lnr.readLine()) != null) {
                if (!isWhile) {// 忽略第一行
                    isWhile = true;
                    continue;
                }
                if ((line.trim().length() == 0) || line.startsWith("#")) {
                    continue;
                }
                final StringTokenizer st = new StringTokenizer(line, ";");
                final String ban = st.nextToken();
                this._nameList.add(ban);
            }
            _log.info("讀取->禁止名稱數量: " + this._nameList.size());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            StreamUtil.close(lnr);
        }
    }

    public boolean isBadName(final String name) {
        final String checkName = name.toLowerCase();
        for (final String badName : this._nameList) {
            if (checkName.contains(badName.toLowerCase())) {
                _log.info("新建人物名稱包含禁用字元: " + this._nameList.size());
                return true;
            }
        }
        return false;
    }

    public String[] getAllBadNames() {
        return this._nameList.toArray(new String[0]);
    }
}
