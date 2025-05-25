package com.lineage.server.model;

public class L1NpcTalkData {
    int ID;
    int NpcID;
    int currencyItemId;
    String normalAction;
    String caoticAction;
    String teleportURL;
    String teleportURLA;

    public String getNormalAction() {
        return normalAction;
    }

    public void setNormalAction(String normalAction) {
        this.normalAction = normalAction;
    }

    public String getCaoticAction() {
        return caoticAction;
    }

    public void setCaoticAction(String caoticAction) {
        this.caoticAction = caoticAction;
    }

    public String getTeleportURL() {
        return teleportURL;
    }

    public void setTeleportURL(String teleportURL) {
        this.teleportURL = teleportURL;
    }

    public String getTeleportURLA() {
        return teleportURLA;
    }

    public void setTeleportURLA(String teleportURLA) {
        this.teleportURLA = teleportURLA;
    }

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        ID = id;
    }

    public int getNpcID() {
        return NpcID;
    }

    public void setNpcID(int npcID) {
        NpcID = npcID;
    }

    public int getCurrencyItemId() {
        return currencyItemId;
    }

    public void setCurrencyItemId(int cId) {
        currencyItemId = cId;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1NpcTalkData JD-Core Version: 0.6.2
 */