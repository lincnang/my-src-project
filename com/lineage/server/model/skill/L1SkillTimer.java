package com.lineage.server.model.skill;

public abstract interface L1SkillTimer {
    public abstract int getRemainingTime();

    public abstract void setRemainingTime(int paramInt);

    public abstract void begin();

    public abstract void end();

    public abstract void kill();
}
