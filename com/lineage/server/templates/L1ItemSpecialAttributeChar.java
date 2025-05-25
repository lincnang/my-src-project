package com.lineage.server.templates;

import java.sql.Timestamp;

/**
 * 人物物品特殊屬性資料表<br>
 * 類名稱：L1ItemSpecialAttributeChar<br>
 * 創建人:Manly<br>
 * 修改時間：2018年4月26日 下午8:41:00<br>
 * 修改人:QQ:263075225<br>
 * 修改備註:<br>
 *
 * @version Rev:3.2 Bin:81222<br>
 */
public class L1ItemSpecialAttributeChar {
    private int _itemobjid;
    private String _item_name;
    private int _attr_id;
    private String _add_pc_name;
    private Timestamp _add_time;
    private String _add_mon_name;
    private String _mapname;
    private String _Acquisition_mode;

    /**
     * 傳回擁有特殊屬性的物品OBJID
     *
     * @return
     */
    public int get_itemobjid() {
        return this._itemobjid;
    }

    /**
     * 設置擁有特殊屬性的物品OBJID
     *
     * @param itemobjid
     */
    public void set_itemobjid(int itemobjid) {
        this._itemobjid = itemobjid;
    }

    /**
     * 傳回物品名稱
     *
     * @return
     */
    public String get_item_name() {
        return this._item_name;
    }

    /**
     * 設置物品名稱
     *
     * @param item_name
     */
    public void set_item_name(String item_name) {
        this._item_name = item_name;
    }

    /**
     * 傳回特殊屬性ID
     *
     * @return
     */
    public int get_attr_id() {
        return this._attr_id;
    }

    /**
     * 設置特殊屬性ID
     *
     * @param attr_id
     */
    public void set_attr_id(int attr_id) {
        this._attr_id = attr_id;
    }

    /**
     * 傳回獲取此物的玩家名稱
     *
     * @return
     */
    public String get_add_pc_name() {
        return this._add_pc_name;
    }

    /**
     * 設置獲取此物的玩家名稱
     *
     * @param add_pc_name
     */
    public void set_add_pc_name(String add_pc_name) {
        this._add_pc_name = add_pc_name;
    }

    /**
     * 傳回獲取此特殊屬性的時間
     *
     * @return
     */
    public Timestamp get_add_time() {
        return this._add_time;
    }

    /**
     * 設置獲取此特殊屬性的時間
     *
     * @param add_time
     */
    public void set_add_time(Timestamp add_time) {
        this._add_time = add_time;
    }

    /**
     * 傳回掉落此物品的怪物名稱
     *
     * @return
     */
    public String get_add_mon_name() {
        return this._add_mon_name;
    }

    /**
     * 獲取掉落此物品的怪物名稱
     *
     * @param add_mon_name
     */
    public void set_add_mon_name(String add_mon_name) {
        this._add_mon_name = add_mon_name;
    }

    /**
     * 傳回地圖名稱
     *
     * @return
     */
    public String get_mapname() {
        return this._mapname;
    }

    /**
     * 設置地圖名稱
     *
     * @param mapname
     */
    public void set_mapname(String mapname) {
        this._mapname = mapname;
    }

    /**
     * 傳回此物品獲取方式
     *
     * @return
     */
    public String get_Acquisition_mode() {
        return this._Acquisition_mode;
    }

    /**
     * 設置此物品獲取方式
     *
     * @param Acquisition_mode
     */
    public void set_Acquisition_mode(String Acquisition_mode) {
        this._Acquisition_mode = Acquisition_mode;
    }
}
