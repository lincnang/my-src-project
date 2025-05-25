package com.add.Sun.DTO;

import com.add.Sun.DAL.DBTable;

public class DTO_2 {
    @DBTable(columnName = "id")
    private Long id;
    @DBTable(columnName = "物品描述")
    private String 物品描述;
    @DBTable(columnName = "物品Id")
    private String 物品Id;
    @DBTable(columnName = "物品數量")
    private String 物品數量;
    @DBTable(columnName = "物品強化等級")
    private String 物品強化等級;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String get物品描述() {
        return this.物品描述;
    }

    public void set物品描述(String 物品描述) {
        this.物品描述 = 物品描述;
    }

    public String get物品Id() {
        return this.物品Id;
    }

    public void set物品Id(String 物品Id) {
        this.物品Id = 物品Id;
    }

    public String get物品數量() {
        return this.物品數量;
    }

    public void set物品數量(String 物品數量) {
        this.物品數量 = 物品數量;
    }

    public String get物品強化等級() {
        return this.物品強化等級;
    }

    public void set物品強化等級(String 物品強化等級) {
        this.物品強化等級 = 物品強化等級;
    }
}
