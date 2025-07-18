package com.lineage.server.templates;

import com.lineage.server.datatables.lock.CharSkillReading;
import java.sql.Timestamp;


public class L1UserSkillTmp {
    private int _char_obj_id;
    private int _skill_id;
    private String _skill_name;
    private int _is_active;
    private int _activetimeleft;
    private int _skill_level;

    public int get_char_obj_id() {
        return _char_obj_id;
    }

    public void set_char_obj_id(int char_obj_id) {
        _char_obj_id = char_obj_id;
    }

    public int get_skill_id() {
        return _skill_id;
    }

    public void set_skill_id(int skill_id) {
        _skill_id = skill_id;
    }

    public String get_skill_name() {
        return _skill_name;
    }

    public void set_skill_name(String skill_name) {
        _skill_name = skill_name;
    }

    public int get_is_active() {
        return _is_active;
    }

    public void set_is_active(int is_active) {
        _is_active = is_active;
    }

    public void is_active(int is_active) {
        CharSkillReading.get().setAuto(is_active, _char_obj_id, _skill_id);
        set_is_active(is_active);
    }

    // 新增 get/set_skill_level
    public int get_skill_level() {
        return _skill_level;
    }

    public void set_skill_level(int level) {
        this._skill_level = level;
    }

    public int get_activetimeleft() {
        return _activetimeleft;
    }

    public void set_activetimeleft(int activetimeleft) {
        _activetimeleft = activetimeleft;
    }

    private Timestamp next_time;
    public Timestamp get_next_time() { return next_time; }
    public void set_next_time(Timestamp next_time) { this.next_time = next_time; }

}
