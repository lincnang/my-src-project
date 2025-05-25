package com.add;

import java.util.Calendar;

public class L1SystemMessage {
    private int _id;
    private String _message;
    private Calendar _resetmaptime;

    public L1SystemMessage(int id, String message, Calendar resetmaptime) {
        _id = id;
        _message = message;
        _resetmaptime = resetmaptime;
    }

    /**
     * 傳回ID
     *
     * @return
     */
    public int getId() {
        return _id;
    }

    /**
     * 傳回設定文字
     *
     * @return
     */
    public String getMessage() {
        return _message;
    }

    /**
     * 傳回自動重置限時地監時間
     *
     * @return
     */
    public Calendar get_resetmaptime() {
        return _resetmaptime;
    }
}
