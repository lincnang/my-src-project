package com.lineage.server.templates;

public class MapData {//src015
    public int mapId = 0;
    public String locationname = null;// TODO　名稱檢索用
    public int startX = 0;
    public int endX = 0;
    public int startY = 0;
    public int endY = 0;
    public double monster_amount = 1.0D;
    public double dropRate = 1.0D;
    public boolean isUnderwater = false;
    public boolean markable = false;
    public boolean teleportable = false;
    public boolean escapable = false;
    public boolean isUseResurrection = false;
    public boolean isUsePainwand = false;
    public boolean isEnabledDeathPenalty = false;
    public boolean isTakePets = false;
    public boolean isRecallPets = false;
    public boolean isUsableItem = false;
    public boolean isUsableSkill = false;
    public boolean isAutoBot = false;
    /**
     * 計時地圖時間
     */
    public int maptime = 0;
    public int isUsableShop;
    public int CopyMapId = -1; // 虛擬地圖
    public boolean chuanyunjian = false; //穿雲劍
}