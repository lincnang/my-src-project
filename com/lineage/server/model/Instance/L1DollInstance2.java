package com.lineage.server.model.Instance;

import com.lineage.config.ConfigAlt;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.doll.Doll_Skill;
import com.lineage.server.model.doll.L1DollExecutor;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.*;

public class L1DollInstance2 extends L1NpcInstance {  //src016
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1DollInstance2.class);
    private static Random _random = new Random();
    public int _gif = -1;// 值3
    private int _itemObjId;
    private boolean _power_doll = false;
    private L1Doll _type;
    private int _time = 0;
    private int _skillid = -1;
    private int _r = -1;
    private int _olX = 0;
    private int _olY = 0;

    /**
     * 召喚魔法娃娃
     *
     */
    public L1DollInstance2(L1Npc template, L1PcInstance master, int itemObjId, L1Doll type) {
        super(template);
        try {
            setId(IdFactoryNpc.get().nextId());
            set_showId(master.get_showId());
            setItemObjId(itemObjId);
            _type = type;
            setGfxId(type.get_gfxid());
            setTempCharGfx(type.get_gfxid());
            setNameId(type.get_nameid());
            set_time(type.get_time());
            setMaster(master);
            setX(master.getX() + _random.nextInt(5) - 2);
            setY(master.getY() + _random.nextInt(5) - 2);
            setMap(master.getMapId());
            setHeading(5);
            setLightSize(template.getLightSize());
            World.get().storeObject(this);
            World.get().addVisibleObject(this);
            master.setUsingDoll2(this);// 設定正在使用的娃娃
            master.addDoll2(this);// 加入使用中娃娃清單
            master.setNpcSpeed();// 設定娃娃跟隨速度
            for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
                onPerceive(pc);// 發送接觸資訊
            }
            L1PcInstance masterpc = null;
            if ((_master instanceof L1PcInstance)) {
                masterpc = (L1PcInstance) _master;
                if (!_type.getPowerList().isEmpty()) {
                    for (L1DollExecutor power : _type.getPowerList()) {
                        if ((power instanceof Doll_Skill)) {
                            Doll_Skill skill = (Doll_Skill) power;
                            set_skill(skill.get_int()[0], skill.get_int()[1], skill.get_int()[2]);

                        } else {
                            power.setDoll(masterpc);
                        }
                        if (power.is_reset()) {// 是否具有輔助技能
                            _power_doll = true;
                        }
                    }
                }
//                master.sendPackets(new S_PacketBox(56, type.get_time()));
//                master.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 538));// 開啟聖物圖示
                master.sendPackets(new S_InventoryIcon(12079, true, 1143, 3600));
            }
            broadcastPacketAll(new S_SkillSound(getId(), 5935));
            set_olX(getX());
            set_olY(getY());
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 收回娃娃
     */
    public void deleteDoll2() {
        try {
            L1ItemInstance item = WorldItem.get().getItem(_itemObjId);// 找回物品資料
            //2016/6/21測試修正
            if (item != null) {
                item.stopEquipmentTimer(null);// 停止物品計時
            }
            broadcastPacketAll(new S_SkillSound(getId(), 5936));
            L1PcInstance masterpc = null;
            if ((_master instanceof L1PcInstance)) {
                masterpc = (L1PcInstance) _master;
                if (!_type.getPowerList().isEmpty()) {
                    for (L1DollExecutor p : _type.getPowerList()) {
                        p.removeDoll(masterpc);
                    }
                }
//                masterpc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 538));// 開啟聖物圖示
//                masterpc.sendPackets(new S_PacketBox(56, 0));
                masterpc.sendPackets(new S_InventoryIcon(12079, false, 1143, 0)); // 2565(一階段經驗料理圖示編號)
            }
            // 主動對可視玩家送出移除封包，避免客戶端殘留顯示
            try {
                for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
                    if (pc == null) continue;
                    pc.removeKnownObject(this);
                    pc.sendPackets(new S_RemoveObject(this));
                }
            } catch (Exception ignored) {
            }

            _master.setUsingDoll2(null);// 設定目前使用娃娃為空
            _master.removeDoll2(this);// 從使用清單中移出
            deleteMe();
        } catch (Exception e) {
            String msg = "玩家:" + _master.getName() + " 回收娃娃出錯:" + e.getMessage();
            _log.debug(msg);
        }
    }

    /**
     * 接觸資訊
     */
    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_NPCPack_Doll(this, perceivedFrom));
            if (getBraveSpeed() > 0) {
                perceivedFrom.sendPackets(new S_SkillBrave(getId(), getBraveSpeed(), 600000));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 跟隨主人變更移動/速度狀態
     */
    public void setNpcMoveSpeed() {
        try {
            if (!ConfigAlt.WAR_DOLL) {
                for (int castle_id = 1; castle_id < 8; castle_id++) {
                    if (ServerWarExecutor.get().isNowWar(castle_id)) { // 攻城戰期間內
                        if (L1CastleLocation.checkInWarArea(castle_id, this)) {
                            deleteDoll2();
                            return;
                        }
                    }
                }
            }
            if ((_master != null) && (_master.isInvisble())) {
                deleteDoll2();
                return;
            }
            if (_master != null && _master.isDead()) {
                deleteDoll2();
                return;
            }
            if (_master != null && _master.getMoveSpeed() != this.getMoveSpeed()) {
                setMoveSpeed(_master.getMoveSpeed());
            }
            if (_master != null && _master.getBraveSpeed() != this.getBraveSpeed()) {
                setBraveSpeed(_master.getBraveSpeed());
            }
            if ((_master != null) && (_master.getMapId() == getMapId())) {
                if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
                    int dir = targetDirection(_master.getX(), _master.getY());
                    for (L1Object object : World.get().getVisibleObjects(this, 1)) {
                        if ((dir >= 0) && (dir <= 7)) {
                            int locx = getX() + HEADING_TABLE_X[dir];
                            int locy = getY() + HEADING_TABLE_Y[dir];
                            if (((object instanceof L1DollInstance2)) && (locx == object.getX()) && (locy == object.getY())) {
                                dir++;
                            }
                        }
                    }
                    if ((dir >= 0) && (dir <= 7)) {
                        setDirectionMoveSrc(dir);
                        broadcastPacketAll(new S_MoveCharPacket(this));
                    }
                }
            } else {
                deleteDoll2();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int getItemObjId() {
        return _itemObjId;
    }

    public void setItemObjId(int i) {
        _itemObjId = i;
    }

    /**
     * 傳回娃娃召喚剩餘時間
     *
     */
    public int get_time() {
        return _time;
    }

    /**
     * 設定娃娃召喚時間
     *
     */
    public void set_time(int time) {
        _time = time;
    }

    /**
     * 執行娃娃施放技能
     *
     * @param target     被攻擊者
     * @param calculated 是否已預先計算過機率
     */
    public void startDollSkill(L1Character target, boolean calculated) {
        try {
            boolean start = false;// 施展技能
            if (_skillid != -1) {
                L1Skills skill = SkillsTable.get().getTemplate(_skillid);
                int castgfx = skill.getCastGfx();
                if (!calculated && _random.nextInt(100) <= _r) {
                    start = true;
                } else if (calculated) {
                    start = true;
                }
                if (start) {// 施展技能
                    switch (_skillid) {
                        case 4:// 光箭 1100
                        case 6:// 冰箭 1100
                        case 7:// 風刃 1100
                        case 10:// 寒冷戰慄 1100
                        case 15:// 火箭 1300
                        case 16:// 地獄之牙 1300
                        case 17:// 極光雷電 1500
                        case 18:// 起死回生術 1500
                        case 22:// 寒冰氣息 1300
                        case 25:// 燃燒的火球 1600
                        case 28:// 吸血鬼之吻 1250
                        case 30:// 巖牢 1600
                        case 34:// 極道落雷 1250
                        case 38:// 冰錐 1100
                        case 45:// 地裂術 1100
                        case 46:// 烈炎術 1650
                        case 50:// 冰矛圍籬 1600
                        case 58:// 火牢 1100
                        case 65:// 雷霆風暴 3000
                        case 74:// 流星雨 4000
                        case 77:// 究極光裂術 6000
                        case 189:// 岩漿之箭
                        case 192:// 奪命之雷 5150
                        case 203:// 粉碎能量 3100
                        case 207:// 心靈破壞 4050
                            // broadcastPacketAll(new S_DoActionGFX(getId(), 67));
                            L1Magic magic = new L1Magic(_master, target);
                            int magic_dmg = magic.calcMagicDamage(_skillid);
                            magic.commit(magic_dmg, 0);
                            target.broadcastPacketAll(new S_SkillSound(target.getId(), castgfx));
                            if (target instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) target;
                                pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
                            }
                            break;
                        case 29:// 緩速術 1600
                            switch (target.getMoveSpeed()) {
                                case 0:
                                    if (target instanceof L1PcInstance) {
                                        final L1PcInstance pc = (L1PcInstance) target;
                                        pc.sendPackets(new S_SkillHaste(pc.getId(), 2, skill.getBuffDuration()));
                                    }
                                    target.broadcastPacketAll(new S_SkillHaste(target.getId(), 2, skill.getBuffDuration()));
                                    target.setMoveSpeed(2);
                                    break;
                                case 1:
                                    int skillNum = 0;
                                    if (target.hasSkillEffect(HASTE)) {
                                        skillNum = HASTE;
                                    } else if (target.hasSkillEffect(GREATER_HASTE)) {
                                        skillNum = GREATER_HASTE;
                                    } else if (target.hasSkillEffect(STATUS_HASTE)) {
                                        skillNum = STATUS_HASTE;
                                    }
                                    if (skillNum != 0) {
                                        target.removeSkillEffect(skillNum);
                                        target.setMoveSpeed(0);
                                    }
                                    break;
                            }
                            break;
                        case 11:// 毒咒 1650
                            L1SkillUse skillUse = new L1SkillUse();
                            // 如果目標是玩家，傳入玩家實例
                            L1PcInstance targetPc = (target instanceof L1PcInstance) ? (L1PcInstance) target : null;
                            skillUse.handleCommands(targetPc, _skillid, target.getId(), target.getX(), target.getY(), 0, L1SkillUse.TYPE_GMBUFF, this);
                            break;
                        case 5000:// 火焰衝擊
                            if (target instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) target;
                                pc.sendPacketsAll(new S_SkillSound(pc.getId(), castgfx));
                                pc.receiveDamage(_master, 80, true, true);
                            } else if (target instanceof L1NpcInstance) {
                                L1NpcInstance npc = (L1NpcInstance) target;
                                npc.broadcastPacketAll(new S_SkillSound(npc.getId(), castgfx));
                                npc.receiveDamage(_master, 80);
                            }
                            break;
                        case 5001:// 爆擊火焰衝擊
                            if (target instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) target;
                                pc.sendPacketsAll(new S_SkillSound(pc.getId(), castgfx));
                                pc.receiveDamage(_master, 160, true, true);
                            } else if (target instanceof L1NpcInstance) {
                                L1NpcInstance npc = (L1NpcInstance) target;
                                npc.broadcastPacketAll(new S_SkillSound(npc.getId(), castgfx));
                                npc.receiveDamage(_master, 160);
                            }
                            break;
                        case 5002:// 死騎娃娃-地獄火
                            if (!calculated) {// 沒有預先計算過機率(物理攻擊)
                                L1Magic magic2 = new L1Magic(_master, target);
                                int magic_dmg2 = magic2.calcMagicDamage(_skillid);
                                magic2.commit(magic_dmg2, 0);
                                this.broadcastPacketAll(new S_SkillSound(this.getId(), castgfx));// 發送地獄火動畫
                            }
                            break;
                        case 5003:// 獨眼巨人娃娃-巖牢
                            if (!calculated) {// 沒有預先計算過機率(物理攻擊)
                                L1Magic magic2 = new L1Magic(_master, target);
                                int magic_dmg2 = magic2.calcMagicDamage(_skillid);
                                magic2.commit(magic_dmg2, 0);
                                //this.broadcastPacketAll(new S_SkillSound(this.getId(), castgfx));// 發送巖牢動畫
                                if (target instanceof L1PcInstance) {
                                    L1PcInstance pc = (L1PcInstance) target;
                                    pc.sendPacketsAll(new S_SkillSound(pc.getId(), castgfx));
                                    //pc.receiveDamage(_master, magic_dmg2, true, true);
                                } else if (target instanceof L1NpcInstance) {
                                    L1NpcInstance npc = (L1NpcInstance) target;
                                    npc.broadcastPacketAll(new S_SkillSound(npc.getId(), castgfx));
                                    //npc.receiveDamage(_master, magic_dmg2);
                                }
                            }
                            break;
                        default:
                            //測試娃娃有害魔法使用
                            skillUse = new L1SkillUse();
                            skillUse.handleCommands(null, _skillid, target.getId(), target.getX(), target.getX(), 0, L1SkillUse.TYPE_GMBUFF, this);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_skill(int int1, int int2, int int3) {
        _skillid = int1;// 值1
        if (_skillid != -1) {
            this.setLevel(_master.getLevel());
            this.setInt(_master.getInt());
        }
        _r = int2;// 值2
        _gif = int3;
    }

    public int get_skillid() {
        return _skillid;
    }

    public int get_skillrandom() {
        return _r;
    }

    public int get_olX() {
        return _olX;
    }

    public void set_olX(int x) {
        _olX = x;
    }

    public int get_olY() {
        return _olY;
    }

    public void set_olY(int y) {
        _olY = y;
    }

    // 輔助類型娃娃
    public void startDollSkill() {
        if (!_type.getPowerList().isEmpty()) {
            // int i = 0;
            if (_master instanceof L1PcInstance) {
                final L1PcInstance masterpc = (L1PcInstance) _master;
                for (L1DollExecutor p : _type.getPowerList()) {
                    if (p.is_reset()) {
                        p.setDoll(masterpc);
                    }
                }
            }
        }
    }

    /**
     * 觸發娃娃特效動畫
     *
     */
    public void show_action(int i) {
        if (!_type.getPowerList().isEmpty()) {
            if (i == 1) {
                // broadcastPacketAll(new S_DoActionGFX(getId(), 67));
                if (_master instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _master;
                    pc.sendPacketsAll(new S_SkillSound(_master.getId(), 6319));
                }
            }
            if (i == 2) {
                // broadcastPacketAll(new S_DoActionGFX(getId(), 67));
                if (_master instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _master;
                    pc.sendPacketsAll(new S_SkillSound(_master.getId(), 6320));
                }
            }
            if (i == 3) {
                // broadcastPacketAll(new S_DoActionGFX(getId(), 67));
                if (_master instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _master;
                    pc.sendPacketsAll(new S_SkillSound(_master.getId(), 6321));
                }
            }
        }
    }

    /**
     * 具有輔助技能
     */
    public boolean is_power_doll() {
        return _power_doll;
    }
}
