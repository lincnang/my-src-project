package com.lineage.server.templates;

import java.util.List;

public final class L1Rank {
    private final List<String> _partyMember;
    private final int _score;
    private String _partyLeader;

    public L1Rank(String partyLeader, List<String> partyMember, int score) {
        _partyLeader = partyLeader;
        _partyMember = partyMember;
        _score = score;
    }

    public String getPartyLeader() {
        return _partyLeader;
    }

    public void setPartyLeader(String partyLeader) {
        _partyLeader = partyLeader;
    }

    public List<String> getPartyMember() {
        return _partyMember;
    }

    public int getScore() {
        return _score;
    }

    public int getMemberSize() {
        return _partyMember.size() + 1;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Rank JD-Core Version: 0.6.2
 */