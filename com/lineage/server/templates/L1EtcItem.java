package com.lineage.server.templates;

public class L1EtcItem extends L1Item {
    private static final long serialVersionUID = 1L;
    private boolean _stackable;
    private int _delay_id;
    private int _delay_time;
    private int _delay_effect;
    private int _maxChargeCount;

    public boolean isStackable() {
        return _stackable;
    }

    public void set_stackable(boolean stackable) {
        _stackable = stackable;
    }

    public int get_delayid() {
        return _delay_id;
    }

    public void set_delayid(int delay_id) {
        _delay_id = delay_id;
    }

    public int get_delaytime() {
        return _delay_time;
    }

    public void set_delaytime(int delay_time) {
        _delay_time = delay_time;
    }

    public int get_delayEffect() {
        return _delay_effect;
    }

    public void set_delayEffect(int delay_effect) {
        _delay_effect = delay_effect;
    }

    public int getMaxChargeCount() {
        return _maxChargeCount;
    }

    public void setMaxChargeCount(int i) {
        _maxChargeCount = i;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1EtcItem JD-Core Version: 0.6.2
 */