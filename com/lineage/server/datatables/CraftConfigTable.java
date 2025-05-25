package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.CraftItemForNpc;
import com.lineage.server.person.ItemCraft_Other;
import com.lineage.server.serverpackets.S_NewCreateItem;
import com.lineage.server.templates.L1CraftItem;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 樂天堂火神製作(DB化)
 */
public class CraftConfigTable {
    public static final int PAGECOUNT = 10;
    private static final Log _log = LogFactory.getLog(CraftConfigTable.class);
    private static CraftConfigTable _instance;
    private byte[] sha1 = null;
    private Map<Integer, CraftItemForNpc> _craftList = new HashMap<>();
    private ArrayList<CraftItemForNpc> _npcCraftList1 = new ArrayList<>();
    private Map<Integer, Map<Integer, CraftItemForNpc>> _npcCraftList = new HashMap<>();
    private Map<Integer, List<int[]>> substituteList = new HashMap<>();

    private CraftConfigTable() {
        this.loadExchangeItem();
    }

    public static CraftConfigTable get() {
        if (_instance == null) {
            _instance = new CraftConfigTable();
        }
        return _instance;
    }

    public static String read(final byte[] config) {
        final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        final char[] resultCharArray = new char[config.length * 2];
        int index = 0;
        for (final byte b : config) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xF];
            resultCharArray[index++] = hexDigits[b & 0xF];
        }
        return new String(resultCharArray);
    }

    private void loadExchangeItem() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("select * from craft_exchange");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int craft_id = rs.getInt("craft_id");
                final int material_itemid = rs.getInt("material_itemid");
                final int exchange_itemid = rs.getInt("exchange_itemid");
                final int exchage_count = rs.getInt("exchange_count");
                final int exchange_enchant = rs.getInt("exchange_enchant");
                final int[] subMaterial = {material_itemid, exchange_itemid, exchage_count, exchange_enchant};
                if (substituteList != null && substituteList.get(craft_id) != null) {
                    substituteList.get(craft_id).add(subMaterial);
                } else {
                    final List<int[]> sub = new ArrayList<>();
                    sub.add(subMaterial);
                    substituteList.put(craft_id, sub);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        //_log.info("載入火神製作DB化替代材料設置數量: " + substituteList.size());
        this.loadCraftItem();
    }

    private void loadCraftItem() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM craft");
            rs = pstm.executeQuery();
            while (rs.next()) {
                boolean issave = true;
                final int actionid = rs.getInt("id");
                final int craft_nameid = rs.getInt("craft_nameid");
                final String craft_itemid = rs.getString("craft_itemid");
                final String craft_count = rs.getString("craft_count");
                final String craft_enchant = rs.getString("craft_enchant");
                final String craft_bless = rs.getString("craft_bless");
                String[] craft_itemid_list = null;
                if (craft_itemid != null && !"".equals(craft_itemid) && !"0".equals(craft_itemid)) {
                    craft_itemid_list = craft_itemid.split(",");
                }
                String[] craft_count_list = null;
                if (craft_count != null && !"".equals(craft_count) && !"0".equals(craft_count)) {
                    craft_count_list = craft_count.split(",");
                }
                String[] craft_enchant_list = null;
                if (craft_enchant != null && !"".equals(craft_enchant)) {
                    craft_enchant_list = craft_enchant.split(",");
                }
                String[] craft_bless_list = null;
                if (craft_bless != null && !"".equals(craft_bless)) {
                    craft_bless_list = craft_bless.split(",");
                }
                // 大成功道具
                /*final String bigsucces_itemid = rs.getString("bigsucces_itemid");
                final String bigsucces_count = rs.getString("bigsucces_count");
                final String bigsucces_enchant = rs.getString("bigsucces_enchant");
                final String bigsucces_bless = rs.getString("bigsucces_bless");
                String[] bigsucces_itemid_list = null;
                if (bigsucces_itemid != null && !"".equals(bigsucces_itemid) && !"0".equals(bigsucces_itemid)) {
                	bigsucces_itemid_list = bigsucces_itemid.split(",");
                }
                String[] bigsucces_count_list = null;
                if (bigsucces_count != null && !"".equals(bigsucces_count) && !"0".equals(bigsucces_count)) {
                	bigsucces_count_list = bigsucces_count.split(",");
                }
                String[] bigsucces_enchant_list = null;
                if (bigsucces_enchant != null && !"".equals(bigsucces_enchant)) {
                	bigsucces_enchant_list = bigsucces_enchant.split(",");
                }
                String[] bigsucces_bless_list = null;
                if (bigsucces_bless != null && !"".equals(bigsucces_bless)) {
                	bigsucces_bless_list = bigsucces_bless.split(",");
                }*/
                // 大成功道具
                final int bigsucces_itemid = rs.getInt("bigsucces_itemid");
                final int bigsucces_count = rs.getInt("bigsucces_count");
                final int bigsucces_enchant = rs.getInt("bigsucces_enchant");
                final int bigsucces_bless = rs.getInt("bigsucces_bless");
                final String material = rs.getString("material");
                final String material_count = rs.getString("material_count");
                final String material_enchant = rs.getString("material_enchant");
                String[] material_list = null;
                if (material != null && !"".equals(material) && !"0".equals(material)) {
                    material_list = material.split(",");
                }
                String[] material_count_list = null;
                if (material_count != null && !"".equals(material_count) && !"0".equals(material_count)) {
                    material_count_list = material_count.split(",");
                }
                String[] material_enchant_list = null;
                if (material_enchant != null && !"".equals(material_enchant)) {
                    material_enchant_list = material_enchant.split(",");
                }
                final String aidMaterial = rs.getString("aidMaterial");
                final String aidMaterial_count = rs.getString("aidMaterial_count");
                final String aidMaterial_enchant = rs.getString("aidMaterial_enchant");
                String[] aidMaterial_list = null;
                if (aidMaterial != null && !"".equals(aidMaterial) && !"0".equals(aidMaterial)) {
                    aidMaterial_list = aidMaterial.split(",");
                }
                String[] aidMaterial_count_list = null;
                if (aidMaterial_count != null && !"".equals(aidMaterial_count) && !"0".equals(aidMaterial_count)) {
                    aidMaterial_count_list = aidMaterial_count.split(",");
                }
                String[] aidMaterial_enchant_list = null;
                if (aidMaterial_enchant != null && !"".equals(aidMaterial_enchant)) {
                    aidMaterial_enchant_list = aidMaterial_enchant.split(",");
                }
                final String fail_Itemid = rs.getString("fail_itemid");
                final String fail_count = rs.getString("fail_count");
                final String fail_enchant = rs.getString("fail_enchant");
                String[] fail_Itemid_list = null;
                if (fail_Itemid != null && !"".equals(fail_Itemid) && !"0".equals(fail_Itemid)) {
                    fail_Itemid_list = fail_Itemid.split(",");
                }
                String[] fail_count_list = null;
                if (fail_count != null && !"".equals(fail_count) && !"0".equals(fail_count)) {
                    fail_count_list = fail_count.split(",");
                }
                String[] fail_enchant_list = null;
                if (fail_enchant != null && !"".equals(fail_enchant)) {
                    fail_enchant_list = fail_enchant.split(",");
                }
                final int min_level = rs.getInt("min_level");
                final int max_level = rs.getInt("max_level");
                final int min_lawful = rs.getInt("min_lawful");
                final int max_lawful = rs.getInt("max_lawful");
                final int min_karma = rs.getInt("min_karma");
                final int max_karma = rs.getInt("max_karma");
                final int max_count = rs.getInt("max_count");
                final int npcId = rs.getInt("npcid");
                final int change = rs.getInt("change");
                final int bigchange = rs.getInt("bigsucces_change");
                final int showworld = rs.getInt("showworld");
                final String poly = rs.getString("poly");
                final String[] poly_list = poly.split(",");
                final List<Integer> polyList = new ArrayList<>();
                for (final String test : poly_list) {
                    if ((test != null) && (!"".equals(test)) && (!"0".equals(test))) {
                        polyList.add(Integer.parseInt(test));
                    }
                }
                int craftDelayTime = rs.getInt("craft_time"); // 製作延遲時間(單位：秒)
                if (craftDelayTime <= 0) {
                    craftDelayTime = 1;
                }
                final Map<Integer, L1CraftItem> itemList = new HashMap<>();
                final Map<Integer, L1CraftItem> bigsuccesitemList = new HashMap<>();
                final Map<Integer, L1CraftItem> failItem = new HashMap<>();
                final Map<Integer, L1CraftItem> materialList = new HashMap<>();
                final Map<Integer, L1CraftItem> aidMaterialList = new HashMap<>();
                if (craft_itemid_list != null && craft_itemid_list.length > 0) {
                    for (int i = 0; i < craft_itemid_list.length; i++) {
                        final L1Item item = ItemTable.get().getTemplate(Integer.parseInt(craft_itemid_list[i]));
                        if (item == null) {
                            _log.error("製作道具不存在：" + craft_itemid_list[i]);
                            issave = false;
                        }
                        final L1CraftItem craftItem = new L1CraftItem(Integer.parseInt(craft_itemid_list[i]), Integer.parseInt(craft_count_list[i]), Integer.parseInt(craft_enchant_list[i]), Integer.parseInt(craft_bless_list[i]), i + 1);
                        itemList.put(i, craftItem);
                    }
                }
                /*if (bigsucces_itemid_list != null && bigsucces_itemid_list.length > 0) { // 大成功道具
                    for (int i = 0; i < bigsucces_itemid_list.length; i++) {
                        final L1Item item = ItemTable.get().getTemplate(Integer.parseInt(bigsucces_itemid_list[i]));
                        if (item == null) {
                            _log.error("大成功道具不存在：" + bigsucces_itemid_list[i]);
                            issave = false;
                        }
                        final L1CraftItem craftItem = new L1CraftItem(Integer.parseInt(bigsucces_itemid_list[i]), Integer.parseInt(bigsucces_count_list[i]), Integer.parseInt(bigsucces_enchant_list[i]), Integer.parseInt(bigsucces_bless_list[i]), i + 1);
                        bigsuccesitemList.put(i, craftItem);
                    }
                }*/
                if (bigsucces_itemid != 0) { // 大成功道具
                    final L1Item item = ItemTable.get().getTemplate(bigsucces_itemid);
                    if (item == null) {
                        _log.error("大成功道具不存在：" + bigsucces_itemid);
                        issave = false;
                    }
                    final L1CraftItem bigsuccescraftItem = new L1CraftItem(bigsucces_itemid, bigsucces_count, bigsucces_enchant, bigsucces_bless, 1);
                    bigsuccesitemList.put(0, bigsuccescraftItem);
                }
                if (fail_Itemid_list != null && fail_Itemid_list.length > 0) {
                    for (int i = 0; i < fail_Itemid_list.length; i++) {
                        final L1Item item = ItemTable.get().getTemplate(Integer.parseInt(fail_Itemid_list[i]));
                        if (item == null) {
                            _log.error("製作失敗道具不存在：" + fail_Itemid_list[i]);
                            issave = false;
                        }
                        final L1CraftItem failCraftItem = new L1CraftItem(Integer.parseInt(fail_Itemid_list[i]), Integer.parseInt(fail_count_list[i]), Integer.parseInt(fail_enchant_list[i]), 1, i + 1);
                        failItem.put(i, failCraftItem);
                    }
                }
                if (material_list != null && material_list.length > 0) {
                    for (int i = 0; i < material_list.length; i++) {
                        L1Item item = ItemTable.get().getTemplate(Integer.parseInt(material_list[i]));
                        if (item == null) {
                            _log.error("製作材料不存在：" + material_list[i]);
                            issave = false;
                        }
                        final L1CraftItem materialItem = new L1CraftItem(Integer.parseInt(material_list[i]), Integer.parseInt(material_count_list[i]), Integer.parseInt(material_enchant_list[i]), 1, i + 1);
                        if (substituteList.get(actionid) != null) {
                            final ArrayList<L1CraftItem> list = new ArrayList<>();
                            for (final int[] test2 : substituteList.get(actionid)) {
                                if (test2[0] == materialItem.getItemId()) {
                                    item = ItemTable.get().getTemplate(test2[1]);
                                    if (item == null) {
                                        _log.error("可替換材料不存在：" + test2[1]);
                                        issave = false;
                                    }
                                    final L1CraftItem sub2 = new L1CraftItem(test2[1], test2[2], test2[3], 1, 0);
                                    list.add(sub2);
                                }
                            }
                            if (list.size() > 0) {
                                materialItem.setSubstituteList(list);
                            }
                        }
                        materialList.put(i, materialItem);
                    }
                }
                if (aidMaterial_list != null && aidMaterial_list.length > 0) {
                    for (int i = 0; i < aidMaterial_list.length; i++) {
                        L1Item item = ItemTable.get().getTemplate(Integer.parseInt(material_list[i]));
                        if (item == null) {
                            _log.error("製作材料不存在：" + material_list[i]);
                            issave = false;
                        }
                        final L1CraftItem aidMaterialItem = new L1CraftItem(Integer.parseInt(aidMaterial_list[i]), Integer.parseInt(aidMaterial_count_list[i]), Integer.parseInt(aidMaterial_enchant_list[i]), 1, i + 1);
                        if (substituteList.get(actionid) != null) {
                            final ArrayList<L1CraftItem> list = new ArrayList<>();
                            for (final int[] test : substituteList.get(actionid)) {
                                if (test[0] == aidMaterialItem.getItemId()) {
                                    item = ItemTable.get().getTemplate(test[1]);
                                    if (item == null) {
                                        _log.error("可替換材料不存在：" + test[1]);
                                        issave = false;
                                    }
                                    final L1CraftItem sub = new L1CraftItem(test[1], test[2], test[3], 1, 0);
                                    list.add(sub);
                                }
                            }
                            if (list.size() > 0) {
                                aidMaterialItem.setSubstituteList(list);
                            }
                        }
                        aidMaterialList.put(i, aidMaterialItem);
                    }
                }
                final CraftItemForNpc npc = new CraftItemForNpc();
                npc.setActionid(actionid);
                npc.setAidMaterialList(aidMaterialList);
                npc.setCraftDelayTime(craftDelayTime);
                npc.setFailItem(failItem);
                npc.setItemList(itemList);
                npc.setBigSuccessItemList(bigsuccesitemList); // 大成功道具
                npc.setMaterialList(materialList);
                npc.setMaxCount(max_count);
                npc.setMaxKarma(max_karma);
                npc.setMaxLawful(max_lawful);
                npc.setMaxLevel(max_level);
                npc.setMinKarma(min_karma);
                npc.setMinLawful(min_lawful);
                npc.setMinLevel(min_level);
                npc.setNpcId(npcId);
                npc.setShowWorld(showworld);
                npc.setPolyList(polyList);
                npc.setSucceedRandom(change);
                npc.setBigSuccessItemRandom(bigchange);
                npc.setCraftNameID(craft_nameid);
                if (issave) {
                    this._craftList.put(npc.getActionid(), npc);
                    if (this._npcCraftList.get(npc.getNpcId()) == null) {
                        final Map<Integer, CraftItemForNpc> test = new HashMap<>();
                        test.put(npc.getActionid(), npc);
                        this._npcCraftList.put(npc.getNpcId(), test);
                    } else {
                        this._npcCraftList.get(npc.getNpcId()).put(npc.getActionid(), npc);
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        _log.info("讀取->火神製作DB化設置數量: " + _craftList.size());
        this._npcCraftList1.addAll(this._craftList.values());
        try {
            final MessageDigest SHA1_digest = MessageDigest.getInstance("SHA1");
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ItemCraft_Other.SendItemCraft.Builder send = ItemCraft_Other.SendItemCraft.newBuilder();
            send.setIsSendPacket(2);
            final S_NewCreateItem packet = new S_NewCreateItem();
            for (final CraftItemForNpc npcMakeItemAction1 : this._craftList.values()) {
                send.addList(packet.getStream(npcMakeItemAction1));
            }
            bos.write(send.build().toByteArray());
            bos.flush();
            this.sha1 = SHA1_digest.digest(bos.toByteArray());
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getSha() {
        return this.sha1;
    }

    public ArrayList<CraftItemForNpc> get_AllNpcMakeItemForList() {
        return this._npcCraftList1;
    }

    public Map<Integer, CraftItemForNpc> get_AllNpcMakeItem() {
        return this._craftList;
    }

    public Map<Integer, CraftItemForNpc> readItemList(final int npcId) {
        return this._npcCraftList.get(npcId);
    }
}
