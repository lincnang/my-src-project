package com.lineage.server.templates;

public class CharQuest {
    private int _quest_step;
    private int[] _mob_count;

    public int get_quest_step() {
        return this._quest_step;
    }

    public void set_quest_step(int i) {
        this._quest_step = i;
    }

    public int[] get_mob_count() {
        return this._mob_count;
    }

    public void set_mob_count(int[] i) {
        this._mob_count = i;
    }
    private int _now_stage;
    // ... 其它欄位

    public int get_now_stage() {
        return _now_stage;
    }
    public void set_now_stage(int now_stage) {
        this._now_stage = now_stage;
    }

    public void add_mob_count(int n) {
        this._mob_count[n] += 1;
    }
}
