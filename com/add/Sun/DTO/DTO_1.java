package com.add.Sun.DTO;

import com.add.Sun.DAL.DBTable;

public class DTO_1 {
    @DBTable(columnName = "id")
    private long id;
    @DBTable(columnName = "代碼")
    private String 代碼;
    @DBTable(columnName = "兌換數量上限")
    private long 兌換數量上限;
    @DBTable(columnName = "建立時間")
    private java.sql.Timestamp 建立時間;
    @DBTable(columnName = "物品設定Id")
    private long 物品設定Id;
    @DBTable(columnName = "兌換起始日")
    private java.sql.Timestamp 兌換起始日;
    @DBTable(columnName = "兌換結束日")
    private java.sql.Timestamp 兌換結束日;
    @DBTable(columnName = "顯示給玩家的訊息")
    private String 顯示給玩家的訊息;
    @DBTable(columnName = "是否要公告到世界頻")
    private Boolean 是否要公告到世界頻;
    @DBTable(columnName = "同一帳號領取限制")
    private Boolean 同一帳號領取限制;
    @DBTable(columnName = "同一角色領取限制")
    private Boolean 同一角色領取限制;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String get代碼() {
        return 代碼;
    }

    public void set代碼(String 代碼) {
        this.代碼 = 代碼;
    }

    public long get兌換數量上限() {
        return 兌換數量上限;
    }

    public void set兌換數量上限(long 兌換數量上限) {
        this.兌換數量上限 = 兌換數量上限;
    }

    public java.sql.Timestamp get建立時間() {
        return 建立時間;
    }

    public void set建立時間(java.sql.Timestamp 建立時間) {
        this.建立時間 = 建立時間;
    }

    public long get物品設定Id() {
        return 物品設定Id;
    }

    public void set物品設定Id(long 物品設定Id) {
        this.物品設定Id = 物品設定Id;
    }

    public java.sql.Timestamp get兌換起始日() {
        return 兌換起始日;
    }

    public void set兌換起始日(java.sql.Timestamp 兌換起始日) {
        this.兌換起始日 = 兌換起始日;
    }

    public java.sql.Timestamp get兌換結束日() {
        return 兌換結束日;
    }

    public void set兌換結束日(java.sql.Timestamp 兌換結束日) {
        this.兌換結束日 = 兌換結束日;
    }

    public String get顯示給玩家的訊息() {
        return 顯示給玩家的訊息;
    }

    public void set顯示給玩家的訊息(String 顯示給玩家的訊息) {
        this.顯示給玩家的訊息 = 顯示給玩家的訊息;
    }

    public Boolean get是否要公告到世界頻() {
        return 是否要公告到世界頻;
    }

    public void set是否要公告到世界頻(Boolean 是否要公告到世界頻) {
        this.是否要公告到世界頻 = 是否要公告到世界頻;
    }

    public Boolean get同一帳號領取限制() {
        return 同一帳號領取限制;
    }

    public void set同一帳號領取限制(Boolean 同一帳號領取限制) {
        this.同一帳號領取限制 = 同一帳號領取限制;
    }

    public Boolean get同一角色領取限制() {
        return 同一角色領取限制;
    }

    public void set同一角色領取限制(Boolean 同一角色領取限制) {
        this.同一角色領取限制 = 同一角色領取限制;
    }
}
