package com.add.Mobbling;

public abstract interface MobblingStorage {
    public abstract void create(int paramInt1, int paramInt2, double paramDouble, int paramInt3);

    public abstract void load();

    public abstract L1Mobbling[] getMobblingList();

    public abstract L1Mobbling getMobbling(int paramInt);
}