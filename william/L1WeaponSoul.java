package william;

import com.lineage.server.datatables.CharItemPowerTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;

/**
 * 武器劍靈系統
 */
public class L1WeaponSoul {
    private int _weapon_id;
    private int _soul_exp_limit;
    private int _soul_min_exp;
    private int _soul_gfx_id;
    private double _soul_level;
    private double _soul_Str;
    private double _soul_Con;
    private double _soul_Dex;
    private double _soul_Int;
    private double _soul_Wis;
    private int _soul_Mp;
    private String _soul_name;

    public L1WeaponSoul(int weapon_id, int soul_exp_limit, int soul_min_exp, int soul_gfx_id, double soul_level, double soul_Str, double soul_Con, double soul_Dex, double soul_Int, double soul_Wis, int soul_Mp, String soul_name) {
        this._weapon_id = weapon_id;
        this._soul_exp_limit = soul_exp_limit;
        this._soul_min_exp = soul_min_exp;
        this._soul_gfx_id = soul_gfx_id;
        this._soul_level = soul_level;
        this._soul_Str = soul_Str;
        this._soul_Con = soul_Con;
        this._soul_Dex = soul_Dex;
        this._soul_Int = soul_Int;
        this._soul_Wis = soul_Wis;
        this._soul_Mp = soul_Mp;
        this._soul_name = soul_name;
    }

    /**
     * 打怪獲得劍靈值
     */
    public static void storeWeaponSoulExp(L1PcInstance pc, L1ItemInstance weapon, int exp) {
        L1WeaponSoul weaponsoul = WeaponSoul.getInstance().getTemplate(weapon.getItem().getItemId());
        if (weaponsoul == null) {
            return;
        }
        int weaponExp = weapon.getUpdateWeaponSoul() + exp;
        if (weaponExp >= weaponsoul.getSoulExpLimit()) {
            weaponExp = weaponsoul.getSoulExpLimit();
        }
        weapon.setUpdateWeaponSoul(weaponExp);
        pc.sendPackets(new S_ItemStatus(weapon));
        if (CharItemPowerTable.get().getPower(weapon) == null) {
            CharItemPowerTable.get().storeItem(weapon);
        } else {
            CharItemPowerTable.get().updateItem(weapon);
        }
    }

    /**
     * 召喚扣劍靈值
     */
    public static void removeWeaponSoulExp(L1PcInstance pc, L1ItemInstance weapon, int exp) {
        L1WeaponSoul weaponsoul = WeaponSoul.getInstance().getTemplate(weapon.getItem().getItemId());
        if (weaponsoul == null) {
            return;
        }
        int weaponExp = weapon.getUpdateWeaponSoul() - exp;
        if (weaponExp >= weaponsoul.getSoulExpLimit()) {
            weaponExp = weaponsoul.getSoulExpLimit();
        }
        weapon.setUpdateWeaponSoul(weaponExp);
        pc.sendPackets(new S_ItemStatus(weapon));
        if (CharItemPowerTable.get().getPower(weapon) == null) {
            CharItemPowerTable.get().storeItem(weapon);
        } else {
            CharItemPowerTable.get().updateItem(weapon);
        }
    }

    public int getWeapon_Id() {
        return this._weapon_id;
    }

    public int getSoulExpLimit() {
        return this._soul_exp_limit;
    }

    public int getSoulMinExp() {
        return this._soul_min_exp;
    }

    public int getSoulGfxId() {
        return this._soul_gfx_id;
    }

    public double getSoulLevel() {
        return this._soul_level;
    }

    public double getSoulStr() {
        return this._soul_Str;
    }

    public double getSoulCon() {
        return this._soul_Con;
    }

    public double getSoulDex() {
        return this._soul_Dex;
    }

    public double getSoulInt() {
        return this._soul_Int;
    }

    public double getSoulWis() {
        return this._soul_Wis;
    }

    public int getSoulMp() {
        return this._soul_Mp;
    }

    public String getSoulName() {
        return this._soul_name;
    }
}
