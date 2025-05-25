package com.lineage.server.templates;

public class L1CriticalHitStone {
    private final int _item_id;
    private final int _next_item_id;
    private final String _name;
    private final int _chance;
    private final int _critical_hit_chance;
    private final int _critical_hit_damage;
    private final int _gfxId;
    private final boolean _gfxIdTarget;
    private final boolean _arrowType;

    public L1CriticalHitStone(final int item_id, final int next_item_id, final String name, final int chance, final int critical_hit_chance, final int critical_hit_damage, final int gfxId, final boolean gfxIdTarget, final boolean arrowType) {
        _item_id = item_id;
        _next_item_id = next_item_id;
        _name = name;
        _chance = chance;
        _critical_hit_chance = critical_hit_chance;
        _critical_hit_damage = critical_hit_damage;
        _gfxId = gfxId;
        _gfxIdTarget = gfxIdTarget;
        _arrowType = arrowType;
    }

    public final int getItemId() {
        return _item_id;
    }

    public final int getNextItemId() {
        return _next_item_id;
    }

    public final String getName() {
        return _name;
    }

    public final int getChance() {
        return _chance;
    }

    public final int getCriticalHitChance() {
        return _critical_hit_chance;
    }

    public final int getCriticalHitDamage() {
        return _critical_hit_damage;
    }

    public final int getGfxId() {
        return _gfxId;
    }

    public final boolean isGfxIdTarget() {
        return _gfxIdTarget;
    }

    public final boolean isArrowType() {
        return _arrowType;
    }
}
