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
 package com.lineage.server.model;

 /**
  * @**抽抽樂**
  * @作者:冰雕寵兒
  */
 public class L1PandoraItem {
     private int Id;
     private int Type;
     private int ItemId;
     private int CharId;
     private int Count;
     private String ItemName;
     private int Enchantlvl;

     public int getId() {
         return this.Id;
     }

     public void setId(final int Id) {
         this.Id = Id;
     }

     public int getType() {
         return this.Type;
     }

     public void setType(final int Type) {
         this.Type = Type;
     }

     public int getItemId() {
         return this.ItemId;
     }

     public void setItemId(final int ItemId) {
         this.ItemId = ItemId;
     }

     public int getCharId() {
         return this.CharId;
     }

     public void setCharId(final int CharId) {
         this.CharId = CharId;
     }

     public int getCount() {
         return this.Count;
     }

     public void setCount(final int Count) {
         this.Count = Count;
     }

     public String getItemName() {
         return this.ItemName;
     }

     public void setItemName(final String ItemName) {
         this.ItemName = ItemName;
     }

     public int getEnchantlvl() {
         return this.Enchantlvl;
     }

     public void setEnchantlvl(final int Enchantlvl) {
         this.Enchantlvl = Enchantlvl;
     }
 }
