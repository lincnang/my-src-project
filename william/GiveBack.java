package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiveBack {
    public static final List<Integer> savepcid = new ArrayList<>();
    public static final List<L1ItemInstance> saveweapon = new ArrayList<>();
    public static final List<String> savename = new ArrayList<>();

    public static final Map<Integer, Integer> npc_itemid = new HashMap<>();
    public static final Map<Integer, Integer> npc_itemcount = new HashMap<>();

    // 規則：依 type2(武器/防具)、type(細類)、minEnchant 來決定回收價格
    private static final List<Rule> rules = new ArrayList<>();

    // 允許以文字輸入 type（helm/cloak/sword/bow...）時的對應
    private static final class TypePair { final int type2; final int type; TypePair(int t2, int t){type2=t2; type=t;} }

    // 回收價格封裝
    public static final class Price {
        public final int itemId;
        public final int count;
        public Price(int itemId, int count) {
            this.itemId = itemId;
            this.count = count;
        }
    }

    // 單筆規則
    private static final class Rule {
        final int npcid;        // 規則所屬的 NPC（0 代表全域）
        final boolean isArmor;  // true=防具, false=武器
        final int type;         // 武器/防具細類 (e.g. 防具: 1=helm；武器: 1=sword)
        final int minEnchant;   // 最低強化門檻 (含)
        final int priceItemId;  // 費用道具ID
        final int priceCount;   // 費用數量
        Rule(int npcid, boolean isArmor, int type, int minEnchant, int priceItemId, int priceCount) {
            this.npcid = npcid;
            this.isArmor = isArmor;
            this.type = type;
            this.minEnchant = minEnchant;
            this.priceItemId = priceItemId;
            this.priceCount = priceCount;
        }
    }

    public static void load() {
        Connection conn = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM 系統_衝裝贖回設定");

            // 先檢查欄位是否存在（相容舊結構）
            java.sql.ResultSetMetaData meta = rs.getMetaData();
            boolean hasType = hasColumn(meta, "type");
            boolean hasMinEnchant = hasColumn(meta, "min_enchant");

            rules.clear();
            while ((rs != null) && (rs.next())) {
                int npcid = rs.getInt("npcid");
                int itemid = rs.getInt("itemid");
                int itemcount = rs.getInt("count");

                if (hasType && hasMinEnchant) {
                    String typeRaw = rs.getString("type");
                    int minEnchant = rs.getInt("min_enchant");

                    if (typeRaw != null) {
                        typeRaw = typeRaw.trim();
                        if (!typeRaw.isEmpty() && minEnchant >= 0) {
                            // 僅支援文字型別（helm/cloak/sword/bow...），避免誤判
                            TypePair pair = mapTypeText(typeRaw);
                            if (pair != null) {
                                rules.add(new Rule(npcid, pair.type2 == 2, pair.type, minEnchant, itemid, itemcount));
                                continue;
                            }
                        }
                    }
                }
                // 否則視為 NPC 預設價格
                npc_itemid.put(npcid, itemid);
                npc_itemcount.put(npcid, itemcount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void addRecord(L1PcInstance pc, L1ItemInstance item) {
        // 若資料表有定義規則，僅符合任一規則者才記錄；若無規則則全部記錄（相容舊行為）
        boolean hasRules = !rules.isEmpty();
        if (hasRules) {
            int type2 = item.getItem().getType2();
            int type = item.getItem().getType();
            int enchant = item.getEnchantLevel();
            boolean matched = false;
            for (Rule r : rules) {
                if (((r.isArmor && type2 == 2) || (!r.isArmor && type2 == 1)) && r.type == type && enchant >= r.minEnchant) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return;
            }
        }

        int pcId = pc.getId();
        savepcid.add(pcId);
        saveweapon.add(item);
        savename.add(item.getNumberedViewName(1L));

        trimOldestIfExceeded(pcId);
    }

    private static void trimOldestIfExceeded(int pcId) {
        int count = 0;
        for (Integer id : savepcid) {
            if (id == pcId) {
                count++;
            }
        }

        if (count > 3) {
            for (int i = 0; i < savepcid.size(); i++) {
                if (savepcid.get(i) == pcId) {
                    savepcid.remove(i);
                    saveweapon.remove(i);
                    savename.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 依規則取得該物品之回收價格；若無規則，回退到 NPC 既有設定或預設值。
     */
    public static Price getPriceForItem(final L1ItemInstance olditem, final int npcid) {
        if (olditem != null && olditem.getItem() != null) {
            final int type2 = olditem.getItem().getType2();
            final int type = olditem.getItem().getType();
            final int enchant = olditem.getEnchantLevel();

            Rule matched = null;
            for (Rule r : rules) {
                if ((r.npcid == npcid || r.npcid == 0)
                        && ((r.isArmor && type2 == 2) || (!r.isArmor && type2 == 1))
                        && r.type == type && enchant >= r.minEnchant) {
                    if (matched == null || r.minEnchant > matched.minEnchant || (r.minEnchant == matched.minEnchant && r.npcid == npcid && matched.npcid != npcid)) {
                        matched = r;
                    }
                }
            }
            if (matched != null) {
                return new Price(matched.priceItemId, matched.priceCount);
            }
        }
        // 回退：依 NPC 設定或預設 金幣x30
        final int itemid = npc_itemid.getOrDefault(npcid, 40308);
        final int itemcount = npc_itemcount.getOrDefault(npcid, 30);
        return new Price(itemid, itemcount);
    }

    private static boolean hasColumn(java.sql.ResultSetMetaData meta, String name) {
        try {
            int count = meta.getColumnCount();
            for (int i = 1; i <= count; i++) {
                if (name.equalsIgnoreCase(meta.getColumnName(i))) {
                    return true;
                }
            }
        } catch (Exception ignore) {}
        return false;
    }

    // 將字串型別對應到 type2/type
    private static TypePair mapTypeText(String raw) {
        if (raw == null) return null;
        String keyRaw = raw.trim();
        if (keyRaw.isEmpty()) return null;

        // 中文別名 -> 英文鍵
        String lower = keyRaw.toLowerCase();
        if (lower.equals("頭盔")) keyRaw = "helm";
        else if (lower.equals("斗篷")) keyRaw = "cloak";
        else if (lower.equals("劍")) keyRaw = "sword";

        // ArmorTypes：鍵多為小寫，但內衣用大寫 "T"，先用原字再用小寫找
        Integer armorType = ItemTable.ArmorTypes().get(keyRaw);
        if (armorType == null) armorType = ItemTable.ArmorTypes().get(keyRaw.toLowerCase());
        if (armorType == null && lower.equals("t")) armorType = ItemTable.ArmorTypes().get("T");
        if (armorType != null) return new TypePair(2, armorType);

        // WeaponTypes：鍵為小寫
        Integer weaponType = ItemTable.WeaponTypes().get(keyRaw.toLowerCase());
        if (weaponType != null) return new TypePair(1, weaponType);

        return null;
    }
}
