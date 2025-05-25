package com.add.Sun.DTO;

import com.add.Sun.DAL.DBTable;

import java.sql.Timestamp;
import java.util.Date;

public class DTO_3 {
    @DBTable(columnName = "id")
    private Long id;
    @DBTable(columnName = "兌換碼Id")
    private Long 兌換碼Id;
    @DBTable(columnName = "角色名稱")
    private String 角色名稱;
    @DBTable(columnName = "領取時間")
    private java.sql.Timestamp 領取時間;
    @DBTable(columnName = "帳號")
    private String 帳號;

    public DTO_3() {
        Date date = new Date();
        this.領取時間 = new Timestamp(date.getTime());
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long get兌換碼Id() {
        return this.兌換碼Id;
    }

    public void set兌換碼Id(Long 兌換碼Id) {
        this.兌換碼Id = 兌換碼Id;
    }

    public String get角色名稱() {
        return this.角色名稱;
    }

    public void set角色名稱(String 角色名稱) {
        this.角色名稱 = 角色名稱;
    }

    public java.sql.Timestamp get領取時間() {
        return this.領取時間;
    }

    public void set領取時間(java.sql.Timestamp 領取時間) {
        this.領取時間 = 領取時間;
    }

    public String get帳號() {
        return this.帳號;
    }

    public void set帳號(String 帳號) {
        this.帳號 = 帳號;
    }
}
