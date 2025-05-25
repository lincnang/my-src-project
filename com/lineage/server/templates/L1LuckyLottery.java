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
  * @**抽抽樂**
  * @作者:冰雕寵兒
  */
 public class L1LuckyLottery {
     private int total;
     private int random;
     private int item_id;
     private int count;
     private int enchantlvl;
     private boolean isbroad;

     public int getTotal() {
         return this.total;
     }

     public void setTotal(int total) {
         this.total = total;
     }

     public int getRandom() {
         return this.random;
     }

     public void setRandom(int random) {
         this.random = random;
     }

     public int getItem_id() {
         return this.item_id;
     }

     public void setItem_id(int item_id) {
         this.item_id = item_id;
     }

     public int getCount() {
         return this.count;
     }

     public void setCount(int count) {
         this.count = count;
     }

     public int getEnchantlvl() {
         return this.enchantlvl;
     }

     public void setEnchantlvl(int enchantlvl) {
         this.enchantlvl = enchantlvl;
     }

     public boolean getIsbroad() {
         return this.isbroad;
     }

     public void setIsbroad(boolean isbroad) {
         this.isbroad = isbroad;
     }
 }