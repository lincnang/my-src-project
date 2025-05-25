package com.lineage.server.templates;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.utils.RangeInt;

public class L1PetType {
    private final int _baseNpcId;
    private final L1Npc _baseNpcTemplate;
    private final String _name;
    private final int _itemIdForTaming;
    private final RangeInt _hpUpRange;
    private final RangeInt _mpUpRange;
    private final int _npcIdForEvolving;
    private final int _evolvItemId;
    private final int[] _msgIds;
    private final int _defyMsgId;

    public L1PetType(int baseNpcId, String name, int itemIdForTaming, RangeInt hpUpRange, RangeInt mpUpRange, int evolvItemId, int npcIdForEvolving, int[] msgIds, int defyMsgId) {
        _baseNpcId = baseNpcId;
        _baseNpcTemplate = NpcTable.get().getTemplate(baseNpcId);
        _name = name;
        _evolvItemId = evolvItemId;
        _itemIdForTaming = itemIdForTaming;
        _hpUpRange = hpUpRange;
        _mpUpRange = mpUpRange;
        _npcIdForEvolving = npcIdForEvolving;
        _msgIds = msgIds;
        _defyMsgId = defyMsgId;
    }

    public static int getMessageNumber(int level) {
        if (50 <= level) {
            return 5;
        }
        if (48 <= level) {
            return 4;
        }
        if (36 <= level) {
            return 3;
        }
        if (24 <= level) {
            return 2;
        }
        if (12 <= level) {
            return 1;
        }
        return 0;
    }

    public int getBaseNpcId() {
        return _baseNpcId;
    }

    public L1Npc getBaseNpcTemplate() {
        return _baseNpcTemplate;
    }

    public String getName() {
        return _name;
    }

    public int getItemIdForTaming() {
        return _itemIdForTaming;
    }

    public boolean canTame() {
        return _itemIdForTaming != 0;
    }

    public RangeInt getHpUpRange() {
        return _hpUpRange;
    }

    public RangeInt getMpUpRange() {
        return _mpUpRange;
    }

    public int getNpcIdForEvolving() {
        return _npcIdForEvolving;
    }

    public boolean canEvolve() {
        return _npcIdForEvolving != 0;
    }

    public int getMessageId(int num) {
        if (num == 0) {
            return 0;
        }
        return _msgIds[(num - 1)];
    }

    public int getDefyMessageId() {
        return _defyMsgId;
    }

    // 進化所需道具
    public int getEvolvItemId() {
        return _evolvItemId;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1PetType JD-Core Version: 0.6.2
 */