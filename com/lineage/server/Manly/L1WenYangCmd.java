package com.lineage.server.Manly;

public class L1WenYangCmd {
    private int _npcid;//NPCID
    private String _action;//按鈕訊息
    private int _type;//類型
    private int _level;//強化等級
    private int _run;//機濾

    public int getNpcId() {
        return _npcid;
    }

    public void setNpcId(int npcid) {
        _npcid = npcid;
    }

    public String getAction() {
        return _action;
    }

    public void setAction(String i) {
        _action = i;
    }

    public int getType() {
        return _type;
    }

    public void setType(int i) {
        _type = i;
    }

    public int getLevel() {
        return _level;
    }

    public void setLevel(int i) {
        _level = i;
    }

    public int getRun() {
        return _run;
    }

    public void setRun(int i) {
        _run = i;
    }
}
