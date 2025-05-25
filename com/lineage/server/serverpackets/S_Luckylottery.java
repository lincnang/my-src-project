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
 package com.lineage.server.serverpackets;

 import com.lineage.config.Config;
 import com.lineage.server.datatables.ItemTable;
 import com.lineage.server.model.L1PandoraItem;
 import com.lineage.server.templates.L1Item;

 import java.util.List;

 /**
  * @**抽抽樂**
  * @作者:冰雕寵兒
  */
 public class S_Luckylottery extends ServerBasePacket {
     public S_Luckylottery(final int time, final int count, final int iconGfxId, final int id, final String itemName) {
         this.writeC(S_EXTENDED_PROTOBUF);
         this.writeC(101);
         this.writeC(0);
         this.writeIndexInt(1, 99);
         this.writeIndexInt(2, 1);
         final S_Luckylottery data = new S_Luckylottery();
         data.writeIndexInt(1, time);
         data.writeIndexInt(2, count);
         data.writeIndexInt(3, iconGfxId);
         data.writeIndexInt(4, id);
         data.writeIndexString(5, itemName);
         final byte[] buf = data.getContent();
         this.writeIndexBytes(3, buf);
         this.writeH(0);
     }

     public S_Luckylottery(final List<L1PandoraItem> itemList) {
         this.writeC(S_EXTENDED_PROTOBUF);
         this.writeC(101);
         this.writeC(0);
         this.writeIndexInt(1, 99);
         this.writeIndexInt(2, 0);
         if (itemList != null) {
             for (final L1PandoraItem item : itemList) {
                 if (item.getType() == 1) {
                     final L1Item itemTemp = ItemTable.get().getTemplate(item.getItemId());
                     if (itemTemp != null) {
                         final S_Luckylottery data = new S_Luckylottery();
                         data.writeIndexInt(1, item.getId());
                         data.writeIndexInt(2, item.getCount());
                         data.writeIndexInt(3, itemTemp.getGfxId());
                         data.writeIndexInt(4, item.getId());
                         final String e = "+" + item.getEnchantlvl() + " ";
                         final String itemName = String.valueOf((item.getEnchantlvl() > 0) ? e : "") + itemTemp.getName();
                         data.writeIndexString(5, itemName);
                         final byte[] buf = data.getContent();
                         this.writeIndexBytes(3, buf);
                     }
                 }
             }
         }
         this.writeH(0);
     }

     public S_Luckylottery() {
     }

     private void writeInt32(int value) {
         while (value >> 7 != 0L) {
             if (value < 0L) {
                 this._bao.write((byte) (0xFF | (value & 0x7F)));
             } else {
                 this._bao.write((byte) ((value & 0x7F) | 0x80));
             }
             value >>= 7;
         }
         this._bao.write((byte) value);
     }

     private void writeIndexInt(final int index, final int value) {
         this._bao.write((byte) (index * 8));
         this.writeInt32(value);
     }

     private void writeIndexBytes(final int index, final byte[] value) {
         this._bao.write((byte) (index * 8 + 2));
         if (value != null && value.length != 0) {
             this.writeInt32(value.length);
             this.writeByte(value);
         } else {
             this.writeC(0);
         }
     }

     private void writeIndexString(final int index, final String value) {
         try {
             this._bao.write((byte) (index * 8 + 2));
             if (value != null) {
                 final byte[] valueArray = value.getBytes(Config.CLIENT_LANGUAGE_CODE);
                 this.writeInt32(valueArray.length);
                 this.writeByte(valueArray);
             } else {
                 this._bao.write(0);
             }
         } catch (Exception ex) {
         }
     }

     @Override
     public byte[] getContent() {
         return this._bao.toByteArray();
     }
 }
