package com.lineage.server.serverpackets;

import com.lineage.config.ConfigOther;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1ItemStatus;
import com.lineage.server.templates.L1Item;
import java.util.List;

public class S_EquipmentList extends ServerBasePacket {

    private byte[] _byte = null;

    public S_EquipmentList(int objId, List<L1ItemInstance> items) {
        writeC(S_BUY_LIST);
        writeD(objId);
        
        writeH(items.size());
        
        for (L1ItemInstance item : items) {
            L1Item template = item.getItem();
            if (template == null) continue;

            // Using Item ObjId as unique identifier for this view
            writeD(item.getId()); 
            writeH(template.getGfxId());
            writeD(0); // Price
            
            String name = template.getName();
            
            // Basic Enchant display
            if (item.getEnchantLevel() > 0) {
                name = "+" + item.getEnchantLevel() + " " + name;
            } else if (item.getEnchantLevel() < 0) {
                name = String.valueOf(item.getEnchantLevel()) + " " + name;
            }
            
            // Attribute display (Simplified)
            if (item.getAttrEnchantLevel() > 0) {
                // You might want to use a helper here if available, 
                // e.g. item.getAttrEnchantKind() gives element type
                int attr = item.getAttrEnchantKind();
                int lvl = item.getAttrEnchantLevel();
                String attrStr = "";
                switch(attr) {
                    case 1: attrStr = "地"; break; 
                    case 2: attrStr = "火"; break;
                    case 4: attrStr = "水"; break;
                    case 8: attrStr = "風"; break;
                }
                if (lvl >= 1) {
                    // Start of string
                    name = attrStr + "+" + lvl + " " + name;
                }
            }

            writeS(name);
            writeD(template.getUseType());
            
            if (ConfigOther.SHOPINFO) {
                L1ItemStatus itemInfoTemplate = new L1ItemStatus(template);
                byte[] status = itemInfoTemplate.getStatusBytes(true).getBytes();
                writeC(status.length);
                for (byte b : status) {
                    writeC(b);
                }
            } else {
                writeC(0);
            }
        }
        writeH(0x0007);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return "S_EquipmentList";
    }
}
