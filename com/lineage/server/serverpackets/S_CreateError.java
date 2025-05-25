/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.serverpackets;

import com.lineage.server.utils.collections.Lists;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 創造腳色失敗公告
 * 可以開關腳色，不開放腳色
 *
 * @author Miranda
 */
public class S_CreateError extends ServerBasePacket {
    /**
     * 提示信息.
     */
    private final static Logger LOG = Logger.getLogger(S_CreateError.class.getName());
    private List<String> _announcements;

    public S_CreateError() {
        this._announcements = Lists.newList();
        this.loadAnnouncements();
        this.writeC(S_NEWS);
        String message = "";
        final StringBuilder sb = (new StringBuilder()).append(message);
        for (String announcement : this._announcements) {
            message = sb.append(announcement).append("\n").toString();
        }
        this.writeS(message);
    }

    /**
     * 不給創造視窗 (點擊創造後)
     *
     */
    public S_CreateError(final String s) {
        this.writeC(S_NEWS);
        this.writeS(s);
    }

    @Override
    public byte[] getContent() {
        return this.getBytes();
    }

    @Override
    public String getType() {
        return "[S] S_CommonNews";
    }

    /**
     * 載入公告
     */
    private void loadAnnouncements() {
        this._announcements.clear();
        final File file = new File("data/CreateError.txt");
        if (file.exists()) {
            this.readFromDisk(file);
        }
    }

    /**
     * 讀取
     */
    private void readFromDisk(final File file) {
        LineNumberReader lnr = null;
        try {
            String line = null;
            lnr = new LineNumberReader(new FileReader(file));
            do {
                if ((line = lnr.readLine()) == null) {
                    break;
                }
                final StringTokenizer st = new StringTokenizer(line, "\n\r");
                if (st.hasMoreTokens()) {
                    final String announcement = st.nextToken();
                    this._announcements.add(announcement);
                } else {
                    this._announcements.add(" ");
                }
            } while (true);
            lnr.close();
        } catch (final Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }
}
