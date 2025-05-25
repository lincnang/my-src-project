/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.templates;

/**
 * 類名稱：L1Collectibles<br>
 * 類描述：圖鑑系統擴充(收藏品)<br>
 * 創建人:hpc20207<br>
 * 修改時間：2021年11月30日 下午16:57:44<br>
 * 修改備註:<br>
 *
 * @version<br>
 */
public class L1Collectibles {
    private int _needids = 0;
    private int _needenchant = 0;
    private int _needcounts = 0;
    private String note;
    private int id;

    /**
     * 傳回流水號
     *
     */
    public int get_id() {
        return this.id;
    }

    /**
     * 設置流水號
     *
     */
    public void set_id(int id) {
        this.id = id;
    }

    /**
     * 傳回兌換收集列表名稱
     *
     */
    public String get_note() {
        return this.note;
    }

    /**
     * 設置兌換收集列表名稱
     *
     */
    public void set_note(String note) {
        this.note = note;
    }

    public int get_needids() {
        return _needids;
    }

    public void set_needids(int _needids) {
        this._needids = _needids;
    }

    public int get_needenchant() {
        return _needenchant;
    }

    public void set_needenchant(int _needenchant) {
        this._needenchant = _needenchant;
    }

    public int get_needcounts() {
        return _needcounts;
    }

    public void set_needcounts(int _needcounts) {
        this._needcounts = _needcounts;
    }
}