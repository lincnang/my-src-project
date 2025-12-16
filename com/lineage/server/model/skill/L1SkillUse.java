package com.lineage.server.model.skill;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.config.ConfigSkillWizard;
import com.lineage.data.event.RedBlueSet;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.SkillEnhanceTable;
import com.lineage.server.datatables.SkillsNoCounterMagicTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.*;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.*;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1SkillEnhance;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.Teleportation;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldTrap;
import com.lineage.server.world.WorldWar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 技能施放判斷
 *
 * @author dexc
 */
public class L1SkillUse {
    /**
     * 類型:一般
     */
    public static final int TYPE_NORMAL = 0;
    /**
     * 類型:登入
     */
    public static final int TYPE_LOGIN = 1;
    /**
     * 類型:魔法卷軸 - 有施法動作
     */
    public static final int TYPE_SPELLSC = 2;
    /**
     * 類型:NPC BUFF
     */
    public static final int TYPE_NPCBUFF = 3;
    /**
     * 類型:GM BUFF
     */
    public static final int TYPE_GMBUFF = 4;
    private static final Log _log = LogFactory.getLog(L1SkillUse.class);
    private static final Random _random = new Random();
    private static final int PC_PC = 1;
    private static final int PC_NPC = 2;
    private static final int NPC_PC = 3;
    private static final int NPC_NPC = 4;
    // 隱身狀態不能施法
    private static final int[] CAST_WITH_INVIS = {1, 2, 3, 5, 8, 9, 12, 13, 14, 19,DETECTION, CALL_LIGHTNING,SUNBURST,21, 26, 31, 32, 35, 37, 44, 48, 49, 52, 54, 55, 57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, Counter_attack, COUNTER_BARRIER, 97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114, 115, 116, 117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149, 150, 151, 155, 156, 158, 159, 163, 164, 165, 166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN, AWAKEN_ANTHARAS, AWAKEN_FAFURION, AWAKEN_VALAKAS, MIRROR_IMAGE, ILLUSION_OGRE, ILLUSION_LICH, PATIENCE, ILLUSION_DIA_GOLEM, INSIGHT, ILLUSION_AVATAR};

    // 設定魔法屏障不可抵擋的魔法
    /*private static final int[] EXCEPT_COUNTER_MAGIC = {1, 2, 3, 5, 8, 9, 12,
            13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 48, 49, 52, 54, 55,
            57, 60, 61, 63, 67, 68, 69, 72, 73, 75, IMMUNE_TO_HARM2, 78, 79,
            SHOCK_STUN, KINGDOM_STUN, REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE,
            Counter_attack, COUNTER_BARRIER, 97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110,
            111, 113, 114, 115, 116, 117, 118, 129, 130, 131, 132, 134, 137, 138, 146, 147, 148, 149,
            150, 151, 155, 156, 158, 159, 161, 163, 164, 165, 166, 168, 169, 170, 171, 194, 213, SOUL_OF_FLAME, ADDITIONAL_FIRE,
            DRAGON_SKIN, AWAKEN_ANTHARAS, AWAKEN_FAFURION, AWAKEN_VALAKAS, MIRROR_IMAGE, ILLUSION_OGRE, ILLUSION_LICH, PATIENCE,
            ILLUSION_DIA_GOLEM, INSIGHT, ILLUSION_AVATAR, 10026, EMPIRE, 10027, 10028, 10029, 41472, GIGANTIC, Blood_strength};*/
    /**
     * 不允許重複的技能組
     */
    private static final int[][] REPEATEDSKILLS = {{FIRE_WEAPON, WIND_SHOT, STORM_EYE, BURNING_WEAPON, STORM_SHOT},
            {SHIELD, EARTH_SKIN, IRON_SKIN}, {HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE, STATUS_ELFBRAVE, STATUS_RIBRAVE, BLOODLUST},
            {HASTE, GREATER_HASTE, STATUS_HASTE}, {IMMUNE_TO_HARM, IMMUNE_TO_HARM2}, {PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY},
            {PHYSICAL_ENCHANT_STR, DRESS_MIGHTY},
            //			{ GLOWING_AURA,SHINING_AURA },
            {MIRROR_IMAGE, UNCANNY_DODGE}, {AWAKEN_ANTHARAS, AWAKEN_FAFURION, AWAKEN_VALAKAS}, {DECREASE_WEIGHT, JOY_OF_PAIN}};
    private L1Skills _skill;
    private int _skillId;
    private int _getBuffDuration;// 技能時間
    private int _shockStunDuration;
    private int _getBuffIconDuration;// 技能圖示時間
    private int _targetID;
    private int _mpConsume = 0;
    private int _hpConsume = 0;
    private int _targetX = 0;
    private int _targetY = 0;
    @SuppressWarnings("unused")
    private int _dmg = 0;// 傷害
    @SuppressWarnings("unused")
    private int _dice_dmg = 0;
    private int _skillTime = 0;
    private int _type = 0;
    private int _actid = 0;
    private int _gfxid = 0;
    private int _heal;
    private boolean _isPK = false;
    private int _bookmarkId = 0; // 日版記憶座標
    private int _bookmarkX = 0; // 日版記憶座標
    private int _bookmarkY = 0; // 日版記憶座標
    private int _itemobjid = 0;
    private boolean _checkedUseSkill = false; // 事前チェック濟みか
    private int _leverage = 10; // 1/10倍なので10で1倍
    @SuppressWarnings("unused")
    private boolean _isFreeze = false;
    private boolean _isCounterMagic = true;// 是否可被魔法屏障抵銷
    private boolean _isGlanceCheckFail = false;
    /**
     * 執行者
     */
    private L1Character _user = null;
    /**
     * 執行者為pc
     */
    private L1PcInstance _player = null;
    /**
     * 執行者為npc
     */
    private L1NpcInstance _npc = null;
    /**
     * 目標
     */
    private L1Character _target = null;
    /**
     * 目標為NPC
     */
    private L1NpcInstance _targetNpc = null;
    private int _calcType;
    private ArrayList<TargetStatus> _targetList;

    private static void summonMonster(L1PcInstance pc, String s) {
        String[] summonstr_list;
        int[] summonid_list;
        int[] summonlvl_list;
        int[] summoncha_list;
        int summonid = 0;
        int levelrange = 0;
        int summoncost = 0;
        summonstr_list = new String[]{"7", "263", "519", "8", "264", "520", "9", "265", "521", "10", "266", "522", "11", "267", "523", "12", "268", "524", "13", "269", "525", "14", "270", "526", "15", "271", "527", "16", "17", "18", "274"};
        summonid_list = new int[]{4070109, 81211, 81212, 4070110, 81214, 81215, 4070111, 81217, 81218, 4070112, 81220, 81221, 4070113, 81223, 81224, 4070114, 81226, 81227, 4070115, 81229, 81230, 4070116, 81232, 81233, 4070117, 81235, 81236, 4070118, 4070119, 81239, 81240};
        summonlvl_list = new int[]{28, 100, 100, 28, 100, 100, 40, 100, 100, 52, 100, 100, 64, 100, 100, 76, 100, 100, 80, 100, 100, 82, 100, 100, 84, 100, 100, 86, 88, 100, 100};
        summoncha_list = new int[]{25, 100, 100, 25, 100, 100, 25, 100, 100, 25, 100, 100, 25, 100, 100, 25, 100, 100, 35, 100, 100, 35, 100, 100, 35, 100, 100, 35, 35, 100, 100};
        // サモンの種類、必要Lv、ペットコストを得る
        for (int loop = 0; loop < summonstr_list.length; loop++) {
            if (s.equalsIgnoreCase(summonstr_list[loop])) {
                summonid = summonid_list[loop];
                levelrange = summonlvl_list[loop];
                summoncost = summoncha_list[loop];
                break;
            }
        }
		/*if ((pc.isWizard()) && (pc.getReincarnationSkill()[1] > 0) && (summonid == 81240)) { // 法師天賦技能訓獸之神
	        int reisummonid = 4070088 + pc.getReincarnationSkill()[1];
		  	if (reisummonid >= 4070108) {
		  		reisummonid	= 4070108;
		  	}
	        summonid = reisummonid;
		}*/
        // Lv不足
        if (pc.getLevel() < levelrange) {
            // 743 等級太低而無法召喚怪物とができません。
            pc.sendPackets(new S_ServerMessage(743));
            return;
        }
        int petcost = 0;
        for (L1NpcInstance petNpc : pc.getPetList().values()) {
            // 現在のペットコスト
            petcost += petNpc.getPetcost();
        }
        int pcCha = pc.getCha();
        int charisma = 0;
        int summoncount = 0;
        if (pcCha > 35) {
            pcCha = 35;
        }
        charisma = pcCha - petcost;
        summoncount = charisma / summoncost;
        L1Npc npcTemp = NpcTable.get().getTemplate(summonid);
        for (int cnt = 0; cnt < summoncount; cnt++) {
            L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
            summon.setPetcost(summoncost);
        }
    }

    /**
     * 攻擊倍率(1/10)
     *
     */
    public int getLeverage() {
        return this._leverage;
    }

    /**
     * 攻擊倍率(1/10)
     */
    public void setLeverage(final int i) {
        this._leverage = i;
    }

    /**
     * 是否已檢查過技能設定
     *
     */
    private boolean isCheckedUseSkill() {
        return this._checkedUseSkill;
    }

    /**
     * 設定是否已檢查過技能設定
     *
     */
    private void setCheckedUseSkill(final boolean flg) {
        this._checkedUseSkill = flg;
    }

    /**
     * 檢查技能相關設定
     *
     * @param player    攻擊者為PC
     * @param skillid   技能編號
     * @param target_id 目標OBJID
     * @param x         X座標
     * @param y         Y座標
     * @param time      時間
     * @param type      類型
     * @param attacker  攻擊者為NPC
     */
    public boolean checkUseSkill(final L1PcInstance player, final int skillid, final int target_id, final int x, final int y, final int time, final int type, final L1Character attacker) {
        return checkUseSkill(player, skillid, target_id, x, y, time, type, attacker, 0, 0);
    }

    /**
     * 是否通過技能相關設定的檢查
     *
     * @param player    攻擊者為PC
     * @param attacker  攻擊者為NPC
     * @return true:檢查通過 ;false:檢查未通過
     */
    public boolean checkUseSkill(L1PcInstance player, int skillid, int target_id, int x, int y, int time, int type, L1Character attacker, int actid, int gfxid) {
        setCheckedUseSkill(true);
        _targetList = new ArrayList<>(); // ターゲットリストの初期化
        _skill = SkillsTable.get().getTemplate(skillid);
        if (_skill == null) {
            return false;
        }
        if (skillid == 76) {
        }
        _skillId = skillid;
        _targetX = x;
        _targetY = y;
        _skillTime = time;
        _type = type;
        _actid = actid;
        _gfxid = gfxid;
        boolean checkedResult = true;
        if (attacker == null) {// NPC攻擊者欄位為空
            // pc
            _player = player;
            _user = _player;
        } else {// 有設定NPC攻擊者欄位
            // npc
            _npc = (L1NpcInstance) attacker;
            _user = _npc;
        }
        if (_skill.getTarget().equals("none") || _skill.getTarget().equals("buff")) {
            _targetID = _user.getId();
            _targetX = _user.getX();
            _targetY = _user.getY();
                    } else {
            _targetID = target_id;
        }
        switch (type) {
            case TYPE_NORMAL: // 通常魔法使用時
                checkedResult = this.isNormalSkillUsable();
                break;
            case TYPE_SPELLSC: // 魔法卷軸使用時
                checkedResult = this.isSpellScrollUsable();
                break;
            case TYPE_NPCBUFF:
                checkedResult = true;
                break;
        }
        if (!checkedResult) {
            // System.out.println("檢查點2");
            return false;
        }
        
        // 負面魔法(攻擊、詛咒、機率、即死、變形) 安全區域禁止對玩家施放 (通用檢查，包含卷軸)
        if (_user instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) _user;
            int skillType = _skill.getType();
            if (skillType == L1Skills.TYPE_ATTACK || skillType == L1Skills.TYPE_CURSE || 
                skillType == L1Skills.TYPE_PROBABILITY || skillType == L1Skills.TYPE_DEATH ||
                skillType == L1Skills.TYPE_CHANGE || _skillId == HAND_DARKNESS || _skillId == CANCELLATION) {
                // 需先取得目標物件以進行判斷
                L1Object targetObj = World.get().findObject(_targetID);
                if (targetObj instanceof L1PcInstance && _user.getId() != _targetID) {
                    L1PcInstance targetPc = (L1PcInstance) targetObj;
                    if (pc.isSafetyZone() || targetPc.isSafetyZone()) {
                        pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生
                        return false;
                    }
                }
            }
        }
        // ファイアーウォール、ライフストリームは詠唱對象が座標
        // キューブは詠唱者の座標に配置されるため例外
        // id58火牢 id63治愈能量風暴
        if ((_skillId == FIRE_WALL)/* || (_skillId == LIFE_STREAM)*/) {
            return true;
        }
        final L1Object object = World.get().findObject(_targetID);
        if (object instanceof L1ItemInstance) {
            return false;
        }
        if (_user instanceof L1PcInstance) {
            if (object instanceof L1PcInstance) {
                _calcType = PC_PC;
            } else {
                _calcType = PC_NPC;
                _targetNpc = (L1NpcInstance) object;
            }
        } else if (_user instanceof L1NpcInstance) {
            if (object instanceof L1PcInstance) {
                _calcType = NPC_PC;
            } else {
                _calcType = NPC_NPC;
                _targetNpc = (L1NpcInstance) object;
            }
        }
        switch (_skillId) {
            // 可使用傳送戒指技能
            case TELEPORT:
            //case MASS_TELEPORT:
                this._bookmarkId = target_id; // 日版記憶座標
                this._bookmarkX = x; // 日版記憶座標
                this._bookmarkY = y; // 日版記憶座標
                break;
            // 技能對像為道具
            //case CREATE_MAGICAL_WEAPON:
            case BRING_STONE:
            case BLESSED_ARMOR:
                //		case ENCHANT_WEAPON:
            case SHADOW_FANG:
                _itemobjid = target_id;
                break;
        }
        _target = (L1Character) object;
        if (!(_target instanceof L1MonsterInstance) && _skill.getTarget().equals("attack") && (_user.getId() != target_id)) {
            _isPK = true; // ターゲットがモンスター以外で攻擊系スキルで、自分以外の場合PKモードとする。
        }
        // 初期設定ここまで
        // 事前チェック
        if (!(object instanceof L1Character)) { // ターゲットがキャラクター以外の場合何もしない。
            // System.out.println("檢查點3");
            checkedResult = false;
        }
        // 技能發動 目標清單判定
        makeTargetList();
        //防無延遲的技能
        if (this._user instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) this._user;
            if (!ConfigOtherSet2.NO_CD.equals("null")) {
                boolean nocd = false;
                String[] nocds = ConfigOtherSet2.NO_CD.split(",");
                int i = 0;
                while (i < nocds.length) {
                    if (Integer.parseInt(nocds[i]) == this._skillId) {
                        nocd = true;
                        break;
                    }
                    i++;
                }
                if (nocd && this._type != 4) {
                    if (pc.hasSkillEffect(40000)) {
                        return false;
                    }
                    if (!pc.hasSkillEffect(40000) && this._skill.getReuseDelay() > 0) {
                        pc.setSkillEffect(40000, this._skill.getReuseDelay());
                    }
                }
            }
        }
        if ((_targetList.size() == 0) && (_user instanceof L1NpcInstance)) {
            // System.out.println("檢查點4");
            checkedResult = false;
        }
        // 事前チェックここまで
        return checkedResult;
    }

    /**
     * 通常のスキル使用時に使用者の狀態からスキルが使用可能であるか判斷する
     *
     * @return false スキルが使用不可能な狀態である場合 在以下情況下不可使用技能
     */
    private boolean isNormalSkillUsable() {
        // スキル使用者がPCの場合のチェック
        if (this._user instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) this._user;
            if (!this.isAttrAgrees()) { // 精靈魔法で、屬性が一致しなければ何もしない。
                return false;
            }
            if ((this._skillId == ELEMENTAL_PROTECTION) && (pc.getElfAttr() == 0)) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1魔法が失敗しました。
                return false;
            }
            //if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) {
            if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill() && _skillId != ASSASSIN) {
                return false;
            }
            if (_skillId == ASSASSIN) {
                if (!pc.hasSkillEffect(BLIND_HIDING)) {
                    pc.sendPackets(new S_SystemMessage("只能在暗影術狀態下使用。"));
                    return false;
                }
            }
            
            if (_skillId == MOVING_ACCELERATION) {
                if (pc.hasSkillEffect(Shadow_ACCELERATION)) {
                    pc.sendPackets(new S_ServerMessage("已存在施放暗影加速能力。"));
                    return false;
                }
            }
            /*
             * // 正義屬性才可使用究極光裂術 if ((this._skillId == DISINTEGRATE) &&
             * (pc.getLawful() < 500)) { // このメッセージであってるか未確認 pc.sendPackets(new
             * S_ServerMessage(352, "$967")); // この魔法を利用するには性向值が%0でなければなりません。
             * return false; }
             */
            final boolean castle_area = L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId());
            if (this._skillId == FREEZING_BLIZZARD) {
                if (castle_area) {
                    pc.sendPackets(new S_ServerMessage("戰爭旗幟內禁止使用冰雪颶風。"));
                    return false;
                }
            }
            if (this._skillId == EARTH_BIND) {
                if (castle_area) {
                    pc.sendPackets(new S_ServerMessage("戰爭旗幟內禁止使用大地屏障。"));
                    return false;
                }
            }
            /*
            if (this._skillId == ICE_LANCE) {
                if (castle_area) {
                    pc.sendPackets(new S_ServerMessage("戰爭旗幟內禁止使用冰矛圍籬。"));
                    return false;
                }
            }
            */
            // 同じキューブは效果範圍外であれば配置可能
            if ((this._skillId == CUBE_IGNITION) || (this._skillId == CUBE_QUAKE) || (this._skillId == CUBE_SHOCK) || (this._skillId == CUBE_BALANCE)) {
                boolean isNearSameCube = false;
                int gfxId = 0;
                for (final L1Object obj : World.get().getVisibleObjects(pc, 3)) {
                    if (obj instanceof L1EffectInstance) {
                        final L1EffectInstance effect = (L1EffectInstance) obj;
                        gfxId = effect.getGfxId();
                        if (((this._skillId == CUBE_IGNITION) && (gfxId == 6706)) || ((this._skillId == CUBE_QUAKE) && (gfxId == 6712)) || ((this._skillId == CUBE_SHOCK) && (gfxId == 6718)) || ((this._skillId == CUBE_BALANCE) && (gfxId == 6724))) {
                            isNearSameCube = true;
                            break;
                        }
                    }
                }
                if (isNearSameCube) {
                    pc.sendPackets(new S_ServerMessage(1412)); // 已在地板上召喚了魔法立方塊。
                    return false;
                }
            }
            if ((!this.isItemConsume()) && (!this._player.isGm())) { // 消費アイテムはあるか
                this._player.sendPackets(new S_ServerMessage(299)); // \f1施放魔法所需材料不足。
                return false;
            }
        }
        // スキル使用者がNPCの場合のチェック
        else if (this._user instanceof L1NpcInstance) {
            // サイレンス狀態では使用不可
            if (this._user.hasSkillEffect(SILENCE)) {
                // NPCにサイレンスが掛かっている場合は1回だけ使用をキャンセルさせる效果。
                this._user.removeSkillEffect(SILENCE);
                return false;
            }
        }
        // PC、NPC共通のチェック
        if (!this.isHPMPConsume()) { // 消費HP、MPはあるか
            return false;
        }
        return true;
    }

    private boolean isSpellScrollUsable() {
        L1PcInstance pc = (L1PcInstance) _user;
        if (pc.isTeleport()) {
            return false;
        }
        if (pc.isParalyzed()) {
            return false;
        }
        if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) {
            return false;
        }
        return true;
    }

    private boolean isInvisUsableSkill() {
        for (int skillId : CAST_WITH_INVIS) {
            if (skillId == _skillId) {
                return true;
            }
        }
        return false;
    }

    /**
     * pc 用技能施放判斷
     *
     * @param timeSecs 秒
     */
    public void handleCommands(final L1PcInstance player, final int skillId, final int targetId, final int x, final int y, final int timeSecs, final int type) {
        this.handleCommands(player, skillId, targetId, x, y, timeSecs, type, null);
    }

    /**
     * 通用技能施放判斷
     *
     */
    public void handleCommands(final L1PcInstance player, final int skillId, final int targetId, final int x, final int y, final int timeSecs, final int type, final L1Character attacker) {
        try {
            // 安全檢查：確保 player 不為 null
            if (player == null) {
                System.out.println("[ERROR] [L1SkillUse] player is null for skillId: " + skillId);
                return;
            }
              // 事前チェックをしているか？
            if (!isCheckedUseSkill()) {
                final boolean isUseSkill = checkUseSkill(player, skillId, targetId, x, y, timeSecs, type, attacker);
                if (!isUseSkill) {
                    failSkill();
                    return;
                }
            }
            switch (type) {
                case TYPE_NORMAL: // 魔法詠唱時
                    if (!_isGlanceCheckFail || (_skill.getArea() > 0) || _skill.getTarget().equals("none")) {
                        runSkill();
                        useConsume();
                        // 魔法大師技能由技能模式自己處理特效
                        if (_skillId != FOG_OF_SLEEPING) {
                            sendGrfx(true);
                        }
                        sendFailMessageHandle();
                        setDelay();
                    }
                    break;
                case TYPE_LOGIN: // ログイン時（HPMP材料消費なし、グラフィックなし）
                    runSkill();
                    break;
                case TYPE_SPELLSC: // スペルスクロール使用時（HPMP材料消費なし）
                    runSkill();
                    sendGrfx(true);
                    setDelay();
                    break;
                case TYPE_NPCBUFF: // NPCBUFF使用時（HPMP材料消費なし）
                    runSkill();
                    sendGrfx(true);
                    break;
                case TYPE_GMBUFF: // GMBUFF使用時（HPMP材料消費なし、魔法モーションなし）
                    runSkill();
                    sendGrfx(false);
                    break;
            }
            setCheckedUseSkill(false);// 將檢查狀態初始化
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 法術施展失敗的處理
     */
    private void failSkill() {
        setCheckedUseSkill(false);
        switch (_skillId) {
            case TELEPORT:
            //case MASS_TELEPORT:
            case TELEPORT_TO_MATHER:
                // 解除傳送鎖定
                _player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                break;
        }
    }

    /**
     * 合法目標的判斷
     *
     * @param cha 加入判斷的目標物件
     * @return true:可加入目標 false:不可加入目標
     */
    private boolean isTarget(final L1Character cha) throws Exception {
        if (cha == null) {
            return false;
        }
        // 副本ID不相等
        if (_user.get_showId() != cha.get_showId()) {
            return false;
        }
        final L1Skills skill = SkillsTable.get().getTemplate(_skillId);
        if ((skill != null) && skill.getTarget().equals("attack")) { // 修正只判斷為攻擊類型才限制
            if (cha instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) cha;
                if (targetPc.get_redbluejoin() != 0 && _player.get_redbluejoin() != 0) {
                    if (targetPc.get_redbluejoin() == _player.get_redbluejoin() || (_player.get_redblueroom() == 1 && RedBlueSet.step1 == 3) || (_player.get_redblueroom() == 2 && RedBlueSet.step2 == 3)) {
                        return false;
                    }
                }
            }
        }
        if (_npc != null) {
            // 在目標清單中
            if (_npc.isHate(cha)) {
                return true;
            }
            // 施展者是寵物 XXX
            if (_npc instanceof L1PetInstance) {
                if (cha instanceof L1MonsterInstance) {
                    return true;
                }
            }
            // 施展者是召喚獸
            if (_npc instanceof L1SummonInstance) {
                if (cha instanceof L1MonsterInstance) {
                    return true;
                }
            }
        }
        // 該物件是否允許攻擊
        if (!CheckUtil.checkAttackSkill(cha)) {
            return false;
        }
        boolean flg = false;
        // 目標是門
        if (cha instanceof L1DoorInstance) {
            // 目標不可破壞設置
            if ((cha.getMaxHp() == 0) || (cha.getMaxHp() == 1)) {
                return false;
            }
        }
        // 目標是魔法娃娃 拒絕所有技能
        if (cha instanceof L1DollInstance) {
            return false;
        }
        // 目標是人物
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            // 鬼魂模式 以及 GM隱身
            if (pc.isGhost() || pc.isGmInvis()) {
                return false;
            }
        }
        // NPC 對 PC
        if (_calcType == NPC_PC) {
            if ((cha instanceof L1PcInstance) || (cha instanceof L1PetInstance) || (cha instanceof L1DeInstance) || (cha instanceof L1SummonInstance)) {
                flg = true;
            }
            if (cha instanceof L1DeInstance) {
                // 位於安全區域中
                if (cha.isSafetyZone()) {
                    return false;
                }
            }
        }
        // PC 對 NPC
        if (_calcType == PC_NPC) {
            // 判斷目標為人物
            if (cha instanceof L1PcInstance) {
                // 位於安全區域中
                if (cha.isSafetyZone()) {
                    return false;
                }
            }
        }
        // 元のターゲットがPet、Summon以外のNPCの場合、PC、Pet、Summonは對像外
        if ((_calcType == PC_NPC)
                // 目標是NPC
                && (_target instanceof L1NpcInstance)
                // 不能是寵物
                && !(_target instanceof L1PetInstance)
                // 不能是召喚獸
                && !(_target instanceof L1SummonInstance) && ((cha instanceof L1PetInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PcInstance))) {
            return false;
        }
        // 元のターゲットがガード以外のNPCの場合、ガードは對像外
        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1GuardInstance) && (cha instanceof L1GuardInstance)) {
            return false;
        }
        // NPC對PCでターゲットがモンスターの場合ターゲットではない。
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_PC) && !(cha instanceof L1PetInstance) && !(cha instanceof L1SummonInstance) && !(cha instanceof L1PcInstance)) {
            return false;
        }
        // NPC對NPCで使用者がMOBで、ターゲットがMOBの場合ターゲットではない。
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_NPC) && (_user instanceof L1MonsterInstance) && (cha instanceof L1MonsterInstance)) {
            return false;
        }
        // 無方向範圍攻擊魔法で攻擊できないNPCは對像外
        if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK) && ((cha instanceof L1CrownInstance) || (cha instanceof L1DwarfInstance) || (cha instanceof L1EffectInstance) || (cha instanceof L1FieldObjectInstance) || (cha instanceof L1FurnitureInstance) || (cha instanceof L1HousekeeperInstance) || (cha instanceof L1MerchantInstance) || (cha instanceof L1TeleporterInstance))) {
            return false;
        }
        // 攻擊系スキルで對像が自分は對像外（魔法大師除外）
        if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (cha.getId() == _user.getId()) && (_skillId != FOG_OF_SLEEPING)) {
            return false;
        }
        // ターゲットが自分でH-Aの場合效果無し
        if ((cha.getId() == _user.getId()) && (_skillId == HEAL_ALL)) {
            return false;
        }
        if ((((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC) || ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) || ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY)) && (cha.getId() == _user.getId()) && (_skillId != HEAL_ALL)) {
            return true; // ターゲットがパーティーかクラン員のものは自分に效果がある。（ただし、ヒールオールは除外）
        }
        // 攻擊者是PC
        if ((_user instanceof L1PcInstance) && (_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (!_isPK)) {
            // 目標是召喚獸
            if (cha instanceof L1SummonInstance) {
                final L1SummonInstance summon = (L1SummonInstance) cha;
                // 自己的召喚獸
                if (_player.getId() == summon.getMaster().getId()) {
                    return false;
                }
                // 目標是寵物
            } else if (cha instanceof L1PetInstance) {
                final L1PetInstance pet = (L1PetInstance) cha;
                // 自己的寵物
                if (_player.getId() == pet.getMaster().getId()) {
                    return false;
                }
            }
        }
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))
                // 目標不是怪物
                && !(cha instanceof L1MonsterInstance)
                // 不是PK狀態
                && (!_isPK)
                // 目標是人物
                && (_target instanceof L1PcInstance)) {
            L1PcInstance enemy;
            try {
                enemy = (L1PcInstance) cha;
            } catch (final Exception e) {
                return false;
            }
            // カウンター無所遁形術
            if ((_skillId == COUNTER_DETECTION) && (enemy.getZoneType() != 1) && (cha.hasSkillEffect(INVISIBILITY) || cha.hasSkillEffect(BLIND_HIDING))) {
                return true; // インビジかブラインドハイディング中
            }
            if ((_player.getClanid() != 0) && (enemy.getClanid() != 0)) { // クラン所屬中
                // 取回全部戰爭清單
                for (final L1War war : WorldWar.get().getWarList()) {
                    if (war.checkClanInWar(_player.getClanname())) { // 自クランが戰爭に參加中
                        if (war.checkClanInSameWar( // 同じ戰爭に參加中
                                _player.getClanname(), enemy.getClanname())) {
                            if (L1CastleLocation.checkInAllWarArea(enemy.getX(), enemy.getY(), enemy.getMapId())) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false; // 攻擊スキルでPKモードじゃない場合
        }
        if ((!_user.glanceCheck(cha.getX(), cha.getY())) && (!_skill.isThrough())) {
            // エンチャント、復活スキルは障害物の判定をしない
            if (!((_skill.getType() == L1Skills.TYPE_CHANGE) || (_skill.getType() == L1Skills.TYPE_RESTORE))) {
                _isGlanceCheckFail = true;
                return false; // 直線上に障害物がある
            }
        }
        /*
        if (cha.hasSkillEffect(ICE_LANCE) && ((_skillId == ICE_LANCE))) {
            return false; // アイスランス中にアイスランス、フリージングブリザード、フリージングブレス
        }
        */
        if (cha.hasSkillEffect(EARTH_BIND) && (_skillId == EARTH_BIND)) {
            return false; // アース バインド中にアース バインド
        }
        if (!(cha instanceof L1MonsterInstance) && ((_skillId == TAMING_MONSTER) || (_skillId == CREATE_ZOMBIE))) {
            return false; // ターゲットがモンスターじゃない（テイミングモンスター）
        }
        if (cha.isDead() && ((_skillId != CREATE_ZOMBIE) && (_skillId != RESURRECTION)/* && (_skillId != GREATER_RESURRECTION)*/ && (_skillId != CALL_OF_NATURE))) {
            return false; // ターゲットが死亡している
        }
        if ((!cha.isDead()) && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION)/* || (_skillId == GREATER_RESURRECTION)*/ || (_skillId == CALL_OF_NATURE))) {
            return false; // ターゲットが死亡していない
        }
        if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION)/* || (_skillId == GREATER_RESURRECTION)*/ || (_skillId == CALL_OF_NATURE))) {
            return false; // ターゲットがガーディアンタワー、ドア
        }
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            // 目標在絕對屏障狀態下仍然有效的魔法
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
                switch (_skillId) {
                    //*				case CURSE_BLIND:
                    case WEAPON_BREAK:
                    case DARKNESS:
                    case WEAKNESS:
                    case DISEASE:
                    //case FOG_OF_SLEEPING: // 魔法大師改為自我施放，不需要穿透絕對屏障
                        //				case MASS_SLOW:
                    case SLOW:
                    case CANCELLATION:
                    case SILENCE:
                        //				case DECAY_POTION: //藥水霜
                    //case MASS_TELEPORT:
                    case DETECTION:
                    case COUNTER_DETECTION:
                    case ERASE_MAGIC:
                    case ENTANGLE:
                    case PHYSICAL_ENCHANT_DEX:
                    case PHYSICAL_ENCHANT_STR:
                    case PHYSICAL_ENCHANT_INT_DEX_STR:
                    case BLESS_WEAPON:
                    case EARTH_SKIN:
                    case IMMUNE_TO_HARM:
                    case IMMUNE_TO_HARM2:
                    case REMOVE_CURSE:
                    case DARK_BLIND:
                    case PHANTASM:
                    case DEATH_HEAL:
                        return true;
                    default:
                        return false;
                }
            }
        }
        // 目標在隱身狀態(地下)
        if (cha instanceof L1NpcInstance) {
            final int hiddenStatus = ((L1NpcInstance) cha).getHiddenStatus();
            switch (hiddenStatus) {
                case L1NpcInstance.HIDDEN_STATUS_SINK:
                    switch (_skillId) {
                        case DETECTION:
                        case COUNTER_DETECTION:
                        case FREEZING_BREATH:// 暴龍之眼
                        case ARM_BREAKER:// 隱身破壞者
                            return true;
                    }
                    return false;
                case L1NpcInstance.HIDDEN_STATUS_FLY:
                    return false;
            }
        }
        if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC // ターゲットがPC
        ) && (cha instanceof L1PcInstance)) {
            flg = true;
        } else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC // ターゲットがNPC
        ) && ((cha instanceof L1MonsterInstance) || (cha instanceof L1NpcInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance))) {
            flg = true;
        } else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET) && (_user instanceof L1PcInstance)) { // ターゲットがSummon,Pet
            if (cha instanceof L1SummonInstance) {
                final L1SummonInstance summon = (L1SummonInstance) cha;
                if (summon.getMaster() != null) {
                    if (_player.getId() == summon.getMaster().getId()) {
                        flg = true;
                    }
                }
            }
            if (cha instanceof L1PetInstance) {
                final L1PetInstance pet = (L1PetInstance) cha;
                if (pet.getMaster() != null) {
                    if (_player.getId() == pet.getMaster().getId()) {
                        flg = true;
                    }
                }
            }
        }
        if ((_calcType == PC_PC) && (cha instanceof L1PcInstance)) {
            final L1PcInstance xpc = (L1PcInstance) cha;
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) && (((_player.getClanid() != 0 // ターゲットがクラン員
            ) && (_player.getClanid() == xpc.getClanid())) || _player.isGm())) {
                return true;
            }
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY) && (((_player.isInParty() && _player.getParty() != null && _player.getParty().isMember(xpc)) || _player.isGm()))) {
                return true;
            }
        }
        return flg;
    }

    /**
     * 是否為同組
     *
     */
    private boolean isParty(final L1NpcInstance npc, final L1Character cha) {
        if (npc.getMaster() == null) {
            return false;
        }
        // 在目標清單中
        if (npc.isHate(cha)) {
            return false;
        }
        final int masterId = npc.getMaster().getId();
        // 目標是人物
        if (cha instanceof L1PcInstance) {
            if (cha.getId() == masterId) {
                return true;
            }
            return false;
        }
        // 目標是寵物
        if (cha instanceof L1PetInstance) {
            final L1PetInstance tgPet = (L1PetInstance) cha;
            if (tgPet.getMaster() != null && tgPet.getMaster().getId() == masterId) {
                return true;
            }
            return false;
        }
        // 目標是召喚獸
        if (cha instanceof L1SummonInstance) {
            final L1SummonInstance tgSu = (L1SummonInstance) cha;
            if (tgSu.getMaster() != null && tgSu.getMaster().getId() == masterId) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 技能發動 目標清單判定
     */
    private void makeTargetList() {
        try {
            if (this._type == TYPE_LOGIN) { // ログイン時(死亡時、お化け屋敷のキャンセレーション含む)は使用者のみ
                this._targetList.add(new TargetStatus(this._user));
                return;
            }
            // 對於目標為自己或buff/none類型的技能，只添加使用者一次
            if ((this._skill.getTargetTo() == L1Skills.TARGET_TO_ME && ((this._skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK))
                || this._skill.getTarget().equals("buff")
                || (this._skill.getTarget().equals("none") && (this._skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK)) {
                this._targetList.add(new TargetStatus(this._user)); // ターゲットは使用者のみ
                return;
            }
            // 具有攻擊距離設置
            if (this._skill.getRanged() != -1) {
                if (this._user.getLocation().getTileLineDistance(this._target.getLocation()) > this._skill.getRanged()) {
                    return; // 射程範圍外
                }
            } else {
                // 距離不可見
                if (!this._user.getLocation().isInScreen(this._target.getLocation())) {
                    return; // 射程範圍外
                }
            }
            if ((!this.isTarget(this._target)) && !(this._skill.getTarget().equals("none")) && !(this._skill.getTarget().equals("buff"))) {
                // 對像が違うのでスキルが發動しない。
                return;
            }
            // 直線上目標列舉
            switch (_skillId) {
                case LIGHTNING:
                    final ArrayList<L1Object> al1object = World.get().getVisibleLineObjects(this._user, this._target);
                    for (final L1Object tgobj : al1object) {
                        if (tgobj == null) {
                            continue;
                        }
                        if (!(tgobj instanceof L1Character)) { // ターゲットがキャラクター以外の場合何もしない。
                            continue;
                        }
                        final L1Character cha = (L1Character) tgobj;
                        if (!this.isTarget(cha)) {
                            continue;
                        }
                        // 技能發動 目標清單判定:直線上目標列舉
                        this._targetList.add(new TargetStatus(cha));
                    }
                    al1object.clear();
                    return;
            }
            // 單一目標攻擊
            if (this._skill.getArea() == 0) {
                if (!this._user.glanceCheck(this._target.getX(), this._target.getY())) { // 直線上に障害物があるか
                    if (((this._skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK) && (this._skillId != 10026) && (this._skillId != 10027) && (this._skillId != 10028) && (this._skillId != 10029)) { // 安息攻擊以外の攻擊スキル
                        // ダメージも發生しないし、ダメージモーションも發生しないが、スキルは發動
                        this._targetList.add(new TargetStatus(this._target, false));
                        return;
                    }
                }
                // 對於目標為"buff"的技能，目標就是使用者自己
                if (!this._skill.getTarget().equals("buff")) {
                    this._targetList.add(new TargetStatus(this._target));
                } else {
                    if (this._skillId == FOG_OF_SLEEPING) {
                        System.out.println("[DEBUG] [L1SkillUse] 單一目標，目標為buff，跳過添加目標");
                    }
                }
                // 範圍攻擊
            } else {
                if (!this._skill.getTarget().equals("none") && !this._skill.getTarget().equals("buff")) {
                    this._targetList.add(new TargetStatus(this._target));
                }
                if ((this._skillId != HEAL_ALL) && (this._skillId != DEATH_HEAL) && !(this._skill.getTarget().equals("attack") || (this._skill.getType() == L1Skills.TYPE_ATTACK))) {
                    // 攻擊系以外のスキルとH-A以外はターゲット自身を含める
                    // 但對於目標為"buff"的技能，不要重複添加（前面已經添加過）
                    if (!this._skill.getTarget().equals("buff")) {
                        if (this._skillId == FOG_OF_SLEEPING) {
                            System.out.println("[DEBUG] [L1SkillUse] 範圍攻擊，非攻擊系技能，添加使用者到目標列表");
                        }
                        this._targetList.add(new TargetStatus(this._user));
                        if (this._skillId == FOG_OF_SLEEPING) {

                        }
                    } else {
                        if (this._skillId == FOG_OF_SLEEPING) {

                        }
                    }
                }
                List<L1Object> objects;
                // 全畫面物件
                if (this._skill.getArea() == -1) {
                    objects = World.get().getVisibleObjects(this._user);
                    // 指定範圍物件
                } else {
                    objects = World.get().getVisibleObjects(this._target, this._skill.getArea());
                }
                // System.out.println("攻擊範圍物件數量:"+objects.size());
                for (final L1Object tgobj : objects) {
                    if (tgobj == null) {
                        continue;
                    }
                    if (!(tgobj instanceof L1Character)) {
                        continue;
                    }
                    if (tgobj instanceof L1MonsterInstance) {
                        L1MonsterInstance mob = (L1MonsterInstance) tgobj;
                        if (mob.getNpcId() == 45166) {// 膽小的南瓜怪
                            continue;
                        }
                        if (mob.getNpcId() == 45167) {// 殘暴的南瓜怪
                            continue;
                        }
                    }
                    final L1Character cha = (L1Character) tgobj;
                    if (!this.isTarget(cha)) {
                        continue;
                    }
                    // 技能發動 目標清單判定:加入目標清單 - 回圈
                    this._targetList.add(new TargetStatus(cha));
                }
                return;
            }
        } catch (final Exception e) {
            // _log.error("SkillId:" + this._skillId + " UserName:" +
            // this._player.getName());
        }
    }

    /**
     * 訊息發送
     *
     */
    private void sendHappenMessage(final L1PcInstance pc) {
        final int msgID = this._skill.getSysmsgIdHappen();
        if (msgID > 0) {
            pc.sendPackets(new S_ServerMessage(msgID));
        }
    }

    // 失敗メッセージ表示のハンドル
    private void sendFailMessageHandle() {
        // 攻擊スキル以外で對像を指定するスキルが失敗した場合は失敗したメッセージをクライアントに送信
        // ※攻擊スキルは障害物があっても成功時と同じアクションであるべき。
        if ((_skill.getType() != L1Skills.TYPE_ATTACK) && !_skill.getTarget().equals("none") && !_skill.getTarget().equals("buff") && (_targetList.size() == 0)) {
            sendFailMessage();
        }
    }

    // メッセージの表示（失敗したとき）
    private void sendFailMessage() {
        final int msgID = _skill.getSysmsgIdFail();
        if ((msgID > 0) && (_user instanceof L1PcInstance)) {
            _player.sendPackets(new S_ServerMessage(msgID));
        }
    }

    // 精靈魔法の屬性と使用者の屬性は一致するか？（とりあえずの對處なので、對應できたら消去して下さい)
    private boolean isAttrAgrees() {
        final int magicattr = _skill.getAttr();
        if (_user instanceof L1NpcInstance) { // NPCが使った場合なんでもOK
            return true;
        }
        if ((_skill.getSkillLevel() >= 17) && (_skill.getSkillLevel() <= 22) && (magicattr != 0) // 精靈魔法で、無屬性魔法ではなく、
                && (magicattr != _player.getElfAttr()) // 使用者と魔法の屬性が一致しない。
                && !_player.isGm()) { // ただしGMは例外
            return false;
        }
        return true;
    }

    /**
     * 判?技能的使用是否需要消耗HP/MP
     *
     */
    private boolean isHPMPConsume() {
        _mpConsume = _skill.getMpConsume();
        _hpConsume = _skill.getHpConsume();
        int currentMp = 0;
        int currentHp = 0;
        //MP減免計算,只有PC適用 2017.04.06 修正
        int mpReduce = 0;
        if (_user instanceof L1NpcInstance) {
            currentMp = _npc.getCurrentMp();
            currentHp = _npc.getCurrentHp();
        } else {
            mpReduce = L1ClassFeature.calcIntMagicConsumeReduction(_player.getInt());
            //System.out.println("技能:"+_skill.getName()+" 原耗魔量:"+_mpConsume+" MP減免:"+mpReduce+" 智力:"+_player.getInt());
            currentMp = _player.getCurrentMp();
            currentHp = _player.getCurrentHp();
            // 裝備減低MP消耗
            switch (_skillId) {
                case PHYSICAL_ENCHANT_DEX:// 通暢氣脈術
                    if (_player.getInventory().checkEquipped(20013)) {// 敏捷魔法頭盔
                        _mpConsume = _mpConsume >> 1;
                    }
                    break;
                case HASTE:// 加速術
                    if (_player.getInventory().checkEquipped(20013)) { // 敏捷魔法頭盔
                        _mpConsume = _mpConsume >> 1;
                    } else if (_player.getInventory().checkEquipped(20008)) { // 小型風之頭盔
                        _mpConsume = _mpConsume >> 1;
                    }
                    break;
                //		case HEAL:// 初級治愈術
                //			if (_player.getInventory().checkEquipped(20014)) { // 治愈魔法頭盔
                //				_mpConsume = _mpConsume >> 1;
                //			}
                //			break;
                case EXTRA_HEAL:// 中級治愈術
                    if (_player.getInventory().checkEquipped(20014)) { // 治愈魔法頭盔
                        _mpConsume = _mpConsume >> 1;
                    }
                    break;
                //			case ENCHANT_WEAPON:// 擬似魔法武器
                //				if (_player.getInventory().checkEquipped(20015)) { // 力量魔法頭盔
                //					_mpConsume = _mpConsume >> 1;
                //				}
                //				break;
                case DETECTION:// 無所遁形術
                    if (_player.getInventory().checkEquipped(20015)) { // 力量魔法頭盔
                        _mpConsume = _mpConsume >> 1;
                    }
                    break;
                case PHYSICAL_ENCHANT_STR:// 體魄強健術
                    if (_player.getInventory().checkEquipped(20015)) { // 力量魔法頭盔
                        _mpConsume = _mpConsume >> 1;
                    }
                    break;
                case GREATER_HASTE:// 強力加速術
                    if (_player.getInventory().checkEquipped(20023)) { // 風之頭盔
                        _mpConsume = _mpConsume >> 1;
                    }
                    break;
            }
            // 7.6智力減免MP消耗%
            if (mpReduce > 0) {
                double mpReduceRate = mpReduce * 0.01;
                _mpConsume -= _mpConsume * mpReduceRate;
                // System.out.println("技能:"+_skill.getName()+" 減免後耗魔量:"+_mpConsume+" 減免量:"+mpReduceRate);
            }
            if (0 < _skill.getMpConsume()) { // 需要MP大於0
                _mpConsume = Math.max(_mpConsume, 1); // 最低消耗1點
            }
        }
        if (currentHp < (_hpConsume + 1)) {
            if (_user instanceof L1PcInstance) {
                // 279 \f1因體力不足而無法使用魔法。
                _player.sendPackets(new S_ServerMessage(279));
            }
            return false;
        } else if (currentMp < _mpConsume) {
            if (_user instanceof L1PcInstance) {
                // 278 \f1因魔力不足而無法使用魔法。
                _player.sendPackets(new S_ServerMessage(278));
                if (_player.isGm()) {
                    _player.setCurrentMp(_player.getMaxMp());
                }
            }
            return false;
        }
        return true;
    }

    // 必要材料があるか？
    // 判斷材料是否足夠
    private boolean isItemConsume() {
        final int itemConsume = this._skill.getItemConsumeId();
        final int itemConsumeCount = this._skill.getItemConsumeCount();
        if ((itemConsume == 0) || (_player.isGm())) {
            return true; // 材料を必要としない魔法
        }
        if (!this._player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
            return false; // 必要材料が足りなかった。
        }
        return true;
    }

    /**
     * 使用技能後，相應的HP和MP、Lawful、材料的減少
     */
    private void useConsume() {
        if (this._user instanceof L1NpcInstance) {
            // NPCの場合、HP、MPのみマイナス
            final int current_hp = this._npc.getCurrentHp() - this._hpConsume;
            this._npc.setCurrentHp(current_hp);
            final int current_mp = this._npc.getCurrentMp() - this._mpConsume;
            this._npc.setCurrentMp(current_mp);
            return;
        }
        // HP?MPをマイナス
        if (this.isHPMPConsume()) {
            if (this._skillId == FINAL_BURN) { // ファイナル バーン
                _hpConsume = this._player.getCurrentHp() - 100;
                _mpConsume = this._player.getCurrentMp() - 1;
            }
            final int current_hp = this._player.getCurrentHp() - this._hpConsume;
            this._player.setCurrentHp(current_hp);
            final int current_mp = this._player.getCurrentMp() - this._mpConsume;
            this._player.setCurrentMp(current_mp);
        }
        // Lawfulをマイナス
        int lawful = this._player.getLawful() + this._skill.getLawful();
        if (lawful > 32767) {
            lawful = 32767;
        }
        if (lawful < -32767) {
            lawful = -32767;
        }
        this._player.setLawful(lawful);
        final int itemConsume = this._skill.getItemConsumeId();
        final int itemConsumeCount = this._skill.getItemConsumeCount();
        if ((itemConsume == 0) || (_player.isGm())) {
            return; // 材料を必要としない魔法
        }
        // 使用材料をマイナス
        this._player.getInventory().consumeItem(itemConsume, itemConsumeCount);
    }

    /**
     * 更新右上角狀態圖示及效果時間
     *
     * @param repetition true重新發送狀態圖示 false 不發送狀態圖示
     */
    private void addMagicList(final L1Character cha, final boolean repetition) {
        /// System.out.println("111111111111");
        if (_skillTime == 0) {
            _getBuffDuration = _skill.getBuffDuration() * 1000; // 效果時間
            if (_skill.getBuffDuration() == 0) {
                if (_skillId == INVISIBILITY) { // インビジビリティ
                    cha.setSkillEffect(INVISIBILITY, 0);
                }
                return;
            }
        } else {
            _getBuffDuration = _skillTime * 1000; // パラメータのtimeが0以外なら、效果時間として設定する
        }
        if (_skillId == SHOCK_STUN) {
            _getBuffDuration = _shockStunDuration;
        }
        if (_skillId == KINGDOM_STUN) {
            _getBuffDuration = _shockStunDuration;
        }
        //		if (_skillId == CURSE_POISON) { // カーズポイズンの效果處理はL1Poisonに移讓。//  *毒咒
        //			return;
        //		}
        if ((_skillId == CURSE_PARALYZE) || (_skillId == CURSE_PARALYZE2)) { // カーズパラライズの效果處理はL1CurseParalysisに移讓。
            return;
        }
        if (_skillId == SHAPE_CHANGE) { // シェイプチェンジの效果處理はL1PolyMorphに移讓。
            return;
        }
        if ((_skillId == BLESSED_ARMOR) || /*(_skillId == HOLY_WEAPON) // 武器?防具に效果がある處理はL1ItemInstanceに移讓。
				|| (_skillId == ENCHANT_WEAPON) || */(_skillId == BLESS_WEAPON) || (_skillId == SHADOW_FANG)) {
            return;
        }
        /*
        if ((_skillId == ICE_LANCE) && !_isFreeze) { // 凍結失敗
            return;
        }
        */
        final SkillMode mode = L1SkillMode.get().getSkill(this._skillId);
        if (mode == null) {// 沒有class
            cha.setSkillEffect(_skillId, _getBuffDuration);// 給予技能效果時間
        }
        // 發送技能圖示（首次施放和重複施放都需要發送）
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            sendIcon(pc);
        }
    }

    /**
     * 發送技能圖示
     *
     */
    private void sendIcon(final L1PcInstance pc) {
        if (this._skillTime == 0) {
            this._getBuffIconDuration = this._skill.getBuffDuration(); // 效果時間
        } else {
            this._getBuffIconDuration = this._skillTime; // パラメータのtimeが0以外なら、效果時間として設定する
        }
        // System.out.println("發送技能圖示");
        switch (this._skillId) {
            case SHIELD: // シールド
                pc.sendPackets(new S_SkillIconShield(5, this._getBuffIconDuration));
                break;
            case DRESS_DEXTERITY: // 敏捷提升
                pc.sendPackets(new S_Dexup(pc, 3, this._getBuffIconDuration));
                break;
            case DRESS_MIGHTY: // 力量提升
                pc.sendPackets(new S_Strup(pc, 3, this._getBuffIconDuration));
                break;
            case GLOWING_AURA: // 灼熱武器//(灼熱靈氣：1階段)
                pc.sendPackets(new S_PacketBoxIconAura(113, this._getBuffIconDuration));
                break;
            case SHINING_AURA: // 閃亮之盾//(灼熱靈氣：2階段)
                pc.sendPackets(new S_PacketBoxIconAura(114, this._getBuffIconDuration));
                break;
            case BRAVE_AURA: // 勇猛意志
                pc.sendPackets(new S_PacketBoxIconAura(116, this._getBuffIconDuration));
                break;
            case GLOWING_AURA_2: // 勇猛武器
                pc.sendPackets(new S_PacketBoxIconAura(117, this._getBuffIconDuration));
                break;
            case Armor_Valor: // 勇猛盔甲
                pc.sendPackets(new S_PacketBoxIconAura(118, this._getBuffIconDuration));
                break;
            case FIRE_WEAPON: // ファイアー ウェポン
                pc.sendPackets(new S_PacketBoxIconAura(147, this._getBuffIconDuration));
                break;
            case WIND_SHOT: // ウィンド ショット
                pc.sendPackets(new S_PacketBoxIconAura(148, this._getBuffIconDuration));
                break;
            case FIRE_BLESS: // 舞躍之火
                pc.sendPackets(new S_PacketBoxIconAura(154, this._getBuffIconDuration));
                break;
            case ERASE_MAGIC: // 魔法消除
                pc.sendPackets(new S_PacketBoxIconAura(152, this._getBuffIconDuration));
                break;
            case STORM_EYE: // ストーム アイ
                pc.sendPackets(new S_PacketBoxIconAura(155, this._getBuffIconDuration));
                break;
            case EARTH_BLESS: // 大地的護衛
                pc.sendPackets(new S_SkillIconShield(7, this._getBuffIconDuration));
                break;
            case BURNING_WEAPON: // バーニング ウェポン
                pc.sendPackets(new S_PacketBoxIconAura(162, this._getBuffIconDuration));
                break;
            case STORM_SHOT: // ストーム ショット
                pc.sendPackets(new S_PacketBoxIconAura(165, this._getBuffIconDuration));
                break;
            case IRON_SKIN: // アイアン スキン
                pc.sendPackets(new S_SkillIconShield(10, this._getBuffIconDuration));
                break;
            case EARTH_SKIN: // アース スキン
                pc.sendPackets(new S_SkillIconShield(6, this._getBuffIconDuration));
                break;
            case PHYSICAL_ENCHANT_STR: // フィジカル エンチャント：STR
                pc.sendPackets(new S_Strup(pc, 5, this._getBuffIconDuration));
                break;
            case PHYSICAL_ENCHANT_DEX: // フィジカル エンチャント：DEX
                pc.sendPackets(new S_Dexup(pc, 5, this._getBuffIconDuration));
                break;
            //		case PHYSICAL_ENCHANT_INT_DEX_STR:
            //			{
            //				pc.sendPackets(new S_InventoryIcon(10534, true, 2747, this._getBuffIconDuration));
            //			}
            //			break;
            case HASTE: //加速術41
            case GREATER_HASTE: //強力加速術
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, this._getBuffIconDuration));
                pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
                break;
            case HOLY_WALK: //神聖疾走50
                // 檢查是否學習神聖迅猛，如果有則給予STATUS_BRAVE(勇水效果)
                if (pc.isHOLY_WALK2()) {
                    // 先檢查並保存StatusBraveCake狀態
                    boolean hasStatusBraveCake = pc.hasSkillEffect(STATUS_BRAVE3);
                    int braveCakeTime = 0;
                    if (hasStatusBraveCake) {
                        braveCakeTime = pc.getSkillEffectTimeSec(STATUS_BRAVE3);
                    }

                    // 先清除原有的神聖疾走效果
                    pc.killSkillEffectTimer(HOLY_WALK);

                    // 只在沒有StatusBraveCake時才清除勇敢速度和圖標
                    if (!hasStatusBraveCake) {
                        pc.setBraveSpeed(0);
                        pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                    }

                    // 必須給予STATUS_BRAVE狀態與圖標，與StatusBraveCake共存
                    // 只移除舊的STATUS_BRAVE效果
                    pc.killSkillEffectTimer(STATUS_BRAVE);

                    // 給予新的STATUS_BRAVE效果
                    pc.setSkillEffect(STATUS_BRAVE, this._getBuffIconDuration * 1000);
                    pc.sendPackets(new S_SkillBrave(pc.getId(), 1, this._getBuffIconDuration));
                    pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 1, 0));
                    pc.sendPacketsX8(new S_SkillSound(pc.getId(), 751));

                    // 根據是否有StatusBraveCake設置不同的速度
                    if (hasStatusBraveCake) {
                        // 有StatusBraveCake時，設置為三段加速速度
                        pc.setBraveSpeed(5);
                    } else {
                        // 沒有StatusBraveCake時，設置為勇水速度
                        pc.setBraveSpeed(1);
                    }

                    // 保護並恢復StatusBraveCake狀態，並添加完整的視覺效果
                    if (hasStatusBraveCake) {
                        // 確保StatusBraveCake狀態存在
                        if (!pc.hasSkillEffect(STATUS_BRAVE3)) {
                            pc.setSkillEffect(STATUS_BRAVE3, braveCakeTime * 1000);
                        }

                        // 添加完整的StatusBraveCake視覺效果，確保與道具效果完全一致
                        // 先解除絕對屏障（如果有的話）
                        L1BuffUtil.cancelAbsoluteBarrier(pc);
                        // 顯示蛋糕酒醉特效（移動速度增加動畫）
                        pc.sendPacketsAll(new S_Liquor(pc.getId(), 8));
                        // 重新發送StatusBraveCake的圖標確保顯示
                        pc.sendPackets(new S_PacketBoxThirdSpeed(braveCakeTime));
                        // 播放三段加速音效（如果有的話）
                        pc.sendPacketsX8(new S_SkillSound(pc.getId(), 750)); // 使用750音效
                    }
                } else {
                    pc.sendPackets(new S_SkillBrave(pc.getId(), 4, this._getBuffIconDuration));
                    pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 4, 0));
                }
                break;
            case MOVING_ACCELERATION://行走加速101
            case WIND_WALK: // 風之疾走150
                pc.sendPackets(new S_SkillBrave(pc.getId(), 4, this._getBuffIconDuration));
                pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 4, 0));
                break;
            case SLOW://緩速術53
            case ENTANGLE: //地面障礙152
                pc.sendPackets(new S_SkillHaste(pc.getId(), 2, this._getBuffIconDuration));
                pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 2, 0));
                break;
            case WIND_SHACKLE://風之枷鎖
                pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), _getBuffIconDuration));
                break;
            case COUNTER_BARRIER: // 反擊屏障 (skill_id=91)
                // 發送反擊屏障 BUFF ICON (iconId=10187, stringId=1088)
                pc.sendPackets(new S_InventoryIcon(10187, true, 1088, this._getBuffIconDuration));
                break;
            case DEATH_HEAL:
            case ABSOLUTE_BLADE:
            case ASSASSIN:
            case BLAZING_SPIRITS:
            case GRACE_AVATAR:
            case SOUL_BARRIER:
            case DESTROY:
            case IMPACT:
            case TITANL_RISING:
                pc.sendPackets(new S_NewSkillIcon(this._skillId, true, this._getBuffIconDuration));
                break;
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }

    /**
     * 發送施法動作及魔法特效
     *
     * @param isSkillAction true 有動作 false 沒動作
     */
    private void sendGrfx(final boolean isSkillAction) {
        if (_actid == 0) {
            _actid = _skill.getActionId();
        }
        if (_gfxid == 0) {
            _gfxid = _skill.getCastGfx();
            if (_gfxid == 0) {
                return; // 表示するグラフィックが無い
            }
        }
        // TODO 施展者為PC
        if (_user instanceof L1PcInstance) {
            if (_player.isMagicCritical()) {// 爆擊則動畫改用CastGfx2
                if (_skillId == DISINTEGRATE && _player.isDISINTEGRATE_2()) {
                    _gfxid = 21290;
                } else {
                    int gfxid2 = _skill.getCastGfx2();
                    if (gfxid2 != 0) {
                        _gfxid = gfxid2;
                    }
                }
            }
            if ((_skillId == FIRE_WALL)/* || (_skillId == LIFE_STREAM)*/) {
                final L1PcInstance pc = (L1PcInstance) _user;
                pc.setHeading(pc.targetDirection(_targetX, _targetY));
                pc.sendPacketsAll(new S_ChangeHeading(pc));
                pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), _actid));
                return;
            }
            final int targetid = this._target.getId();
            if (_skillId == SHOCK_STUN) {
                if (_targetList.size() == 0) { // 失敗
                    return;
                } else {
                    if (_target instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) _target;
                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 4434));
                    } else if (_target instanceof L1NpcInstance) {
                        _target.broadcastPacketAll(new S_SkillSound(_target.getId(), 4434));
                    }
                    return;
                }
            }
            if (_skillId == KINGDOM_STUN) {
                if (_targetList.size() == 0) { // 失敗
                    return;
                } else {
                    if (_target instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) _target;
                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 4434));
                    } else if (_target instanceof L1NpcInstance) {
                        _target.broadcastPacketAll(new S_SkillSound(_target.getId(), 24069));//王者之劍特效
                    }
                    return;
                }
            }
            if (_skillId == LIGHT) {
                final L1PcInstance pc = (L1PcInstance) _target;
                pc.sendPackets(new S_Sound(145));
            }
            if ((_targetList.size() == 0) && !(_skill.getTarget().equals("none"))) {
                // ターゲット數が０で對像を指定するスキルの場合、魔法使用エフェクトだけ表示して終了
                final int tempchargfx = _player.getTempCharGfx();
                switch (tempchargfx) {
                    case 5727:
                    case 5730: // シャドウ系變身のモーション對應
                        _actid = ActionCodes.ACTION_SkillBuff;
                        break;
                    case 5733:
                    case 5736:
                        _actid = ActionCodes.ACTION_Attack;
                        break;
                }
                if (isSkillAction) {// 是否執行施法動作
                    _player.sendPacketsAll(new S_DoActionGFX(_player.getId(), _actid));
                }
                return;
            }
            // 攻擊魔法
            if (_skill.getTarget().equals("attack") && (_skillId != TURN_UNDEAD)) {
                // 目標對像 是否為寵物 召喚獸 虛擬人物
                if (isPcSummonPet(_target)) {
                    if (_player.isSafetyZone() || // 自己位於安全區
                            _target.isSafetyZone() || // 目標位於安全區
                            _player.checkNonPvP(_player, _target)) { // 檢查是否可以攻擊
                        // 封包:物件攻擊(NPC / PC 技能使用)
                        _player.sendPacketsAll(new S_UseAttackSkill(_player, 0, _gfxid, _targetX, _targetY, _actid, _dmg));
                        return;
                    }
                }
                // 單體攻擊魔法
                if (_skill.getArea() == 0) {
                    // 封包:物件攻擊(NPC / PC 技能使用)
                    _player.sendPacketsX10(new S_UseAttackSkill(_player, targetid, _gfxid, _targetX, _targetY, _actid, _dmg));
                    this._player.set_twotimes(0);
                    Random twotimes = new Random();
                    if ((this._skillId == 77) && (twotimes.nextInt(101) <= ConfigSkillWizard.DISINTEGRATE_RND) && (this._player.get_twotimes() != 2)) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100L);
                        } catch (Exception localException) {
                        }
                        this._player.sendPacketsX10(new S_UseAttackSkill(this._player, targetid, _gfxid, this._targetX, this._targetY, _actid, this._dmg));
                        this._player.broadcastPacketX8(new S_DoActionGFX(_player.getId(), ActionCodes.ACTION_Damage));
                    }
                    // 有方向範圍魔法
                } else {
                    // 封包:範圍魔法
                    _player.sendPacketsX10(new S_RangeSkill(_player, _targetList, _gfxid, _actid, S_RangeSkill.TYPE_DIR));
                }
            } else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // 無方向範圍攻擊魔法
                // System.out.println("無方向範圍攻擊魔法 目標物件數量:" + _targetList.size());
                _player.sendPacketsX10(new S_RangeSkill(_player, _targetList, _gfxid, _actid, S_RangeSkill.TYPE_NODIR));
            } else { // 補助魔法或詛咒魔法
                // テレポート、マステレ、テレポートトゥマザー以外
                if ((_skillId != TELEPORT) && (_skillId != MASS_TELEPORT) && (_skillId != TELEPORT_TO_MATHER)) {
                    // 是否執行施法動作
                    if (isSkillAction) {
                        _player.sendPacketsAll(new S_DoActionGFX(_player.getId(), _actid));
                    }
                    if ((_skillId == COUNTER_MAGIC) || // 魔法屏障
                            (_skillId == COUNTER_BARRIER) || // 反擊屏障
                            //(_skillId == ARMOR_BREAK) || // 破壞盔甲
                            (_skillId == COUNTER_MIRROR)) {// 鏡反射
                        _player.sendPacketsXR(new S_SkillSound(targetid, _gfxid), -1);
                    } else if (_skillId == ASSASSIN) { // 黑妖新技能 暗殺者
                        _player.sendPackets(new S_SkillSound(targetid, _gfxid));
                    } else if (_skillId == ARMOR_BREAK || _skillId == DESPERADO) {
                    } else if (_skillId == TRUE_TARGET) { // 精準目標
                        return;
                    } else if (_skillId == HAND_DARKNESS || _skillId == Phantom_Blade) { // 黑暗之手
                        return;
                    } else if ((_skillId == IMMUNE_TO_HARM) || // 聖結界
                            (_skillId == IMMUNE_TO_HARM2) || // 全體聖結界
                            //					(_skillId == HEAL) || // 初治
                            (_skillId == EXTRA_HEAL) || // 中治
                            (_skillId == GREATER_HEAL) || // 高治
                            (_skillId == FULL_HEAL)) { // 全治
                        // 使用者隱身狀態下仍可見魔法特效
                        _player.sendPacketsAllUnderInvis(new S_SkillSound(targetid, _gfxid));
                    } else if ((_skillId == AWAKEN_ANTHARAS) || // 覺醒：安塔瑞斯
                            (_skillId == AWAKEN_FAFURION) || // 覺醒：法利昂
                            (_skillId == AWAKEN_VALAKAS)) { // 覺醒：巴拉卡斯
                        if (_skillId == _player.getAwakeSkillId()) { // 再詠唱なら解除でエフェクトなし
                            _player.sendPacketsAll(new S_SkillSound(targetid, _gfxid));
                        } else {
                            return;
                        }
                    } else if ((_skillId == UNCANNY_DODGE)// 暗影閃避
                            && (_player.getAc() <= -100)) {// 防禦大於-100
                        _gfxid = _skill.getCastGfx2();// 使用CastGfx2
                        _player.sendPacketsAll(new S_SkillSound(targetid, _gfxid));
                    } else {
                        _player.sendPacketsAll(new S_SkillSound(targetid, _gfxid));
                    }
                }
                // スキルのエフェクト表示はターゲット全員だが、あまり必要性がないので、ステータスのみ送信
                for (final TargetStatus ts : _targetList) {
                    final L1Character cha = ts.getTarget();
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_OwnCharStatus(pc));
                    }
                }
            }
            _player.setMagicCritical(false);// 取消魔法爆擊狀態
            // TODO 施展者是NPC
        } else if (this._user instanceof L1NpcInstance) { // NPCがスキルを使った場合
            final int targetid = this._target.getId();
            if (this._user instanceof L1MerchantInstance) {
                this._user.broadcastPacketAll(new S_SkillSound(targetid, _gfxid));
                return;
            }
            if ((this._targetList.size() == 0) && !(this._skill.getTarget().equals("none"))) {
                // ターゲット數が０で對像を指定するスキルの場合、魔法使用エフェクトだけ表示して終了
                this._user.broadcastPacketAll(new S_DoActionGFX(this._user.getId(), _actid));
                return;
            }
            if (this._skill.getTarget().equals("attack") && (this._skillId != TURN_UNDEAD)) {
                if (this._skill.getArea() == 0) { // 單體攻擊魔法
                    this._user.broadcastPacketAll(new S_UseAttackSkill(this._user, targetid, _gfxid, this._targetX, this._targetY, _actid, this._dmg));
                } else { // 有方向範圍攻擊魔法
                    this._user.broadcastPacketAll(new S_RangeSkill(this._user, this._targetList, _gfxid, _actid, S_RangeSkill.TYPE_DIR));
                }
            } else if (this._skill.getTarget().equals("none") && (this._skill.getType() == L1Skills.TYPE_ATTACK)) { // 無方向範圍魔法
                // System.out.println("無方向範圍魔法");
                this._user.broadcastPacketAll(new S_RangeSkill(this._user, this._targetList, _gfxid, _actid, S_RangeSkill.TYPE_NODIR));
            } else { // 補助魔法或詛咒魔法
                if ((_skillId != TELEPORT) && (_skillId != MASS_TELEPORT) && (_skillId != TELEPORT_TO_MATHER)) {
                    // 魔法を使う動作のエフェクトは使用者だけ
                    this._user.broadcastPacketAll(new S_DoActionGFX(this._user.getId(), _actid));
                    this._user.broadcastPacketAll(new S_SkillSound(targetid, _gfxid));
                }
            }
        }
    }

    /**
     * 刪除不能重複/同時使用的技能，圖標更改為剛使用時的圖標
     *
     */
    private void deleteRepeatedSkills(final L1Character cha) {
        for (final int[] skills : REPEATEDSKILLS) {
            for (final int id : skills) {
                if (id == _skillId) {
                    stopSkillList(cha, skills);
                }
            }
        }
    }

    /**
     * 刪除全部重複的正在使用的技能
     *
     */
    private void stopSkillList(final L1Character cha, final int[] repeat_skill) {
        for (final int skillId : repeat_skill) {
            if (skillId != _skillId) {
                cha.removeSkillEffect(skillId);
            }
        }
    }

    // 技能使用延遲的處理
    private void setDelay() {
        if (this._skill.getReuseDelay() > 0) {
            L1SkillDelay.onSkillUse(_user, _skill.getReuseDelay());
        }
    }

    /**
     * 發動技能效果
     */
    private void runSkill() {
                switch (_skillId) {
            //            case LIFE_STREAM:// 法師技能(治愈能量風暴)
            //                L1SpawnUtil.spawnEffect(81169, _skill.getBuffDuration(), _targetX, _targetY, _user.getMapId(), _user, 0);
            //                return;
            case CUBE_IGNITION:// 幻術師技能(立方：燃燒)
                L1SpawnUtil.spawnEffect(80149, _skill.getBuffDuration(), _targetX, _targetY, _user.getMapId(), _user, _skillId);
                return;
            case CUBE_QUAKE:// 幻術師技能(立方：地裂)
                L1SpawnUtil.spawnEffect(80150, _skill.getBuffDuration(), _targetX, _targetY, _user.getMapId(), _user, _skillId);
                return;
            case CUBE_SHOCK:// 幻術師技能(立方：衝擊)
                L1SpawnUtil.spawnEffect(80151, _skill.getBuffDuration(), _targetX, _targetY, _user.getMapId(), _user, _skillId);
                return;
            case CUBE_BALANCE:// 幻術師技能(立方：和諧)
                L1SpawnUtil.spawnEffect(80152, _skill.getBuffDuration(), _targetX, _targetY, _user.getMapId(), _user, _skillId);
                return;
            case FIRE_WALL:// 法師技能(火牢)
                // System.out.println("法師技能(火牢):"+_targetX+"/"+_targetY);
                L1SpawnUtil.doSpawnFireWall(_user, _targetX, _targetY);
                return;
        }
        // 魔法屏障不可抵擋的魔法
        /*for (final int skillId : EXCEPT_COUNTER_MAGIC) {
            if (_skillId == skillId) {
                _isCounterMagic = false; // 魔法屏障無效
                break;
            }
        }*/
        // 檢查資料庫設定 (skills_無法檔魔屏)
        if (_isCounterMagic) {
            if (SkillsNoCounterMagicTable.get().isNoCounterMagic(_skillId)) {
                _isCounterMagic = false;
            }
        }
        // NPCにショックスタンを使用させるとonActionでNullPointerExceptionが發生するため
        // とりあえずPCが使用した時のみ
        //原有程式碼
        //        if ((_skillId == SHOCK_STUN) && (_user instanceof L1PcInstance)) {
        //            _target.onAction(_player);
        //        }
        // 衝暈 + 強化秒數
        if ((_skillId == SHOCK_STUN) && (_user instanceof L1PcInstance)) {
            // 修復：先進行命中判定，只有命中才設置暈眩效果
            // 把 _user 轉成玩家
            L1PcInstance pc = (L1PcInstance) _user;
            // (A) 讀取衝暈技能等級 (若 L1PcInstance 有 getShockStunLevel)
            int bookLevel = Math.max(1, pc.getShockStunLevel());
            // (B) 從 "SkillEnhanceTable" 查強化秒數（含向下回退）
            L1SkillEnhance enhanceData = SkillEnhanceTable.get().getEnhanceData(_skillId, bookLevel);
            int stunSec = 4; // 預設
            if (enhanceData != null) {
                int fixedSec = enhanceData.getSetting1();
                int randomSec = enhanceData.getSetting2();
                if (fixedSec < 0) fixedSec = 0;
                if (fixedSec > 30) fixedSec = 30; // 上限可調
                if (randomSec < 0) randomSec = 0;
                if (randomSec > 10) randomSec = 10;
                if (randomSec > 0) {
                    int randVal = _random.nextInt(randomSec + 1);
                    stunSec = fixedSec + randVal;
                } else {
                    stunSec = fixedSec;
                }
            } else {
            }

            // 修復：進行命中判定
            L1Magic magic = new L1Magic(pc, _target);
            if (magic.calcProbabilityMagic(SHOCK_STUN)) {
                // 只有命中才設置暈眩效果
                if (_target instanceof L1PcInstance) {
                    L1PcInstance targetPc = (L1PcInstance) _target;
                    targetPc.setSkillEffect(SHOCK_STUN, stunSec * 1000);
                    targetPc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                    targetPc.setParalyzed(true); // 設置玩家麻痹狀態
                } else if (_target instanceof L1NpcInstance) {
                    L1NpcInstance targetNpc = (L1NpcInstance) _target;
                    targetNpc.setSkillEffect(SHOCK_STUN, stunSec * 1000);
                    targetNpc.setParalyzed(true);
                }
                L1SpawnUtil.spawnEffect(81162, stunSec, _target.getX(), _target.getY(), _target.getMapId(), pc, 0);
            }
            // 恢復攻擊動作
            _target.onAction(_player);
        }
        if ((_skillId == KINGDOM_STUN) && (_user instanceof L1PcInstance)) {
            _target.onAction(_player);
        }
        if (!this.isTargetCalc(_target)) {
            return;
        }
        try {
            TargetStatus ts = null;
            L1Character cha = null;
            int drainMana = 0;
            boolean isSuccess = false;
            int undeadType = 0;
            int heal = 0;
            for (final Iterator<TargetStatus> iter = _targetList.iterator(); iter.hasNext(); ) {
                ts = null;
                cha = null;
                isSuccess = false;
                undeadType = 0;
                ts = iter.next();
                cha = ts.getTarget();
                // System.out.println("發動技能效果");
                if (_npc != null) {
                    // 施展者是寵物 XXX
                    if (_npc instanceof L1PetInstance) {
                        if (isParty(_npc, cha)) {
                            ts.isCalc(false);
                            _dmg = 0;
                            continue;
                        }
                    }
                    // 施展者是召喚獸
                    if (_npc instanceof L1SummonInstance) {
                        if (isParty(_npc, cha)) {
                            ts.isCalc(false);
                            _dmg = 0;
                            continue;
                        }
                    }
                }
                if (!ts.isCalc() || !this.isTargetCalc(cha)) {
                    ts.isCalc(false);
                    continue; // 計算する必要がない。不需要計算
                }
                final L1Magic magic = new L1Magic(_user, cha);
                magic.setLeverage(getLeverage());
                if (cha instanceof L1MonsterInstance) { // アンデットの判定
                    undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
                }
                // 確率系スキルで失敗が確定している場合
                // 概率系技能失敗的確定
                if (((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) && isTargetFailure(cha)) {
                    iter.remove();
                    continue;
                }
                if (cha instanceof L1PcInstance || cha instanceof L1NpcInstance) {
                    // 目標為PC或NPC
                    if (_skillTime == 0) {
                        _getBuffIconDuration = _skill.getBuffDuration(); // 效果時間
                    } else {
                        _getBuffIconDuration = _skillTime;
                    }
                }
                deleteRepeatedSkills(cha); // 刪除重複的技能
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _user;
                    removeNewIcon(pc, _skillId);
                }
                //>> 修正: 強制「負重強化」效果應用與UI更新 (新增)
                if (this._skillId == DECREASE_WEIGHT) {
                    // 檢查目標是否已存在此效果，如果沒有則施加
                    if (!cha.hasSkillEffect(this._skillId)) {
                        // 設定技能效果的持續時間
                        cha.setSkillEffect(this._skillId, this._skill.getBuffDuration() * 1000);
                    }
                    // 發送負重狀態更新封包，確保客戶端UI正確顯示
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int) pc.getMaxWeight(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
                    }
                    // 為了不影響其他邏輯，直接跳到下一個目標
                    continue;
                }

                if (_target instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _target;
                    removeNewIcon(pc, _skillId);
                }
                // System.out.println("NPC對PC傷害計算 XXX:"+this._skill.getType());
                switch (_skill.getType()) {
                    case L1Skills.TYPE_ATTACK:// 攻擊系スキル＆ターゲットが使用者以外であること。
                        if (_user.getId() != cha.getId()) {
                            // 攻擊系技能和使用者除外
                            if (isUseCounterMagic(cha)) { // 魔法屏障的處理
                                iter.remove();
                                continue;
                            }
                            _dmg = magic.calcMagicDamage(_skillId);
                            if (cha.hasSkillEffect(ERASE_MAGIC)) {
                                cha.removeSkillEffect(ERASE_MAGIC); // 魔法消除
                            }
                        }
                        break;
                    case L1Skills.TYPE_CURSE:
                    case L1Skills.TYPE_PROBABILITY: // 確率系スキル
                        isSuccess = magic.calcProbabilityMagic(this._skillId);
                        if (this._skillId == DECREASE_WEIGHT) {
                            isSuccess = true;
                        }
                        if (_type == TYPE_GMBUFF) {
                            isSuccess = true;
                        }
                        if (this._skillId == Phantom_Blade) {
                            isSuccess = true;
                        }
                        if (cha.hasSkillEffect(ERASE_MAGIC) && (this._skillId != ERASE_MAGIC)) {
                            cha.removeSkillEffect(ERASE_MAGIC); // 魔法消除
                        }
                        if (/*this._skillId != FOG_OF_SLEEPING || */_skillId != 212 || _skillId != 103) {
                            //                            cha.removeSkillEffect(FOG_OF_SLEEPING); // 沉睡之霧
                            cha.removeSkillEffect(212);
                            cha.removeSkillEffect(103);
                        }
                        if (isSuccess) { // 成功したがカウンターマジックが發動した場合、リストから削除
                            if (this.isUseCounterMagic(cha)) { // 魔法屏障的處理
                                iter.remove();
                                continue;
                            }
                            if ((this._skillId == STRIKER_GALE) && (cha instanceof L1PcInstance)) {  //src041
                                final L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, 0));
                            }
                        } else { // 失敗した場合、リストから削除
                            if ((/*this._skillId == FOG_OF_SLEEPING || */_skillId == 212 || _skillId == 103) && (cha instanceof L1PcInstance)) {
                                final L1PcInstance pc = (L1PcInstance) cha;
                                // 297 你感覺些微地暈眩。
                                pc.sendPackets(new S_ServerMessage(297));
                            }
                            iter.remove();
                            continue;
                        }
                        break;
                    case L1Skills.TYPE_HEAL: // 回覆系スキル
                        // 回覆量はマイナスダメージで表現
                        _heal = magic.calcHealing(this._skillId);
                        // 加入技能強化加成：
                        // 1. 從玩家身上取得 HEAL_ALL 技能等級（這代表玩家實際施放強化技能的等級）
                        int healEnhanceBookLevel = 0;
                        if (_user instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) _user;
                            healEnhanceBookLevel = Math.max(1, pc.getSkillLevel(HEAL_ALL));
                        }
                        // 2. 從 SkillEnhanceTable 取得該技能等級的強化資料
                        L1SkillEnhance healEnhanceData = SkillEnhanceTable.get().getEnhanceData(HEAL_ALL, healEnhanceBookLevel);
                        // 3. 假設強化資料中的 getSetting1 代表強化等級，每級增加 10% 治療量
                        int enhanceLevel = 0;
                        if (healEnhanceData != null) {
                            enhanceLevel = healEnhanceData.getSetting1();
                            if (enhanceLevel < 0) enhanceLevel = 0;
                            if (enhanceLevel > 50) enhanceLevel = 50; // 每級10%，上限50級=+500%
                        }
                        // 4. 計算額外加成：每級強化增加 10%
                        _heal += _heal * (enhanceLevel * 0.1);

                        // 接著依照各種狀態效果調整回覆量
                        if (cha.hasSkillEffect(WATER_LIFE)) { // 水之元氣(回覆量2倍)
                            _heal = (_heal << 1);
                        }
                        if (cha.hasSkillEffect(POLLUTE_WATER)) { // 污濁之水(回覆量1/2倍)
                            _heal = (_heal >> 1);
                        }
                        if (cha.hasSkillEffect(DESPERADO)) { // 亡命之徒(回覆量1/2倍)
                            _heal /= 4;
                        }
                        if (cha.hasSkillEffect(ADLV80_2_2)) { // 污濁的水流(水龍副本 回覆量1/2倍)
                            _heal = (_heal >> 1);
                        }
                        if (cha.hasSkillEffect(ADLV80_2_3)) { // 治愈侵蝕術
                            _heal *= -1;
                        }
                        if (cha.hasSkillEffect(DEATH_HEAL)) { // 法師新技能 治愈逆行
                            // _heal = (int) (_heal * -0.45);
                            _heal = (int) (_heal * -1 * (ConfigSkillWizard.DEATH_HEAL_DAMAGE_RATE / 100.0));
                        }

                        // 王族天賦技能榮耀治愈
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            int rr = 0;
                            if ((pc.isCrown()) && (pc.getReincarnationSkill()[2] > 0)) {
                                rr = (int) (_heal * (pc.getReincarnationSkill()[2] * 0.02)); // 每+1點被補血量+2%
                            }
                            _heal = (int) _heal + rr;
                            if (this._player.isGm())
                                this._player.sendPackets(new S_SystemMessage("治癒補血量:" + this._heal));
                        }
                        break;
                }
                // TODO SKILL移轉
                final SkillMode mode = L1SkillMode.get().getSkill(this._skillId);
                if (this._skillId == FOG_OF_SLEEPING) {
                }
                if (mode != null) {// 具有skillmode
                    // 施展者是PC
                    if (this._user instanceof L1PcInstance) {
                        switch (this._skillId) {
						/*case TELEPORT:// 指定傳送5
						case MASS_TELEPORT:// 集體傳送術69
							this._dmg = mode.start(this._player, cha, magic, this._bookmarkId);
							break;*/
                            //						case CALL_CLAN:// 呼喚盟友
                            //						case RUN_CLAN:// 援護盟友118
                            //							this._dmg = mode.start(this._player, cha, magic, this._targetID);
                            //							break;
                            default:
                                        this._dmg = mode.start(this._player, cha, magic, this._getBuffIconDuration);
                                if (this._skillId == FOG_OF_SLEEPING) {
                                    // 檢查玩家狀態
                                    if (this._player != null) {
                                    }
                                }
                                break;
                        }
                    }
                    // 施展者是NPC
                    if (this._user instanceof L1NpcInstance) {
                        this._dmg = mode.start(this._npc, cha, magic, this._getBuffIconDuration);
                    }
                } else {// 沒有skillmode
                    // ■■■■ 個別處理のあるスキルのみ書いてください。 ■■■■
                    // 需要個別處理的技能（無法簡單以技能的屬系做判斷）
                    // すでにスキルを使用濟みの場合なにもしない 重複使用無效的技能
                    // ただしショックスタンは重ねがけ出來るため例外 衝擊之暈例外
                    if (cha.hasSkillEffect(this._skillId)) {// 目標身上已有此技能效果
                        this.addMagicList(cha, true); // 更新右上角狀態圖示及效果時間
                        continue;// 略過以下處理
                    }
                }

                // ==== 指定傳送（玩家限定，獨立 if，避免 else-without-if） ====
                if (_skillId == TELEPORT) {
                    // 施術者或目標不是玩家 → 不處理此技能（避免 ClassCast）
                    if (!(_user instanceof L1PcInstance) || !(cha instanceof L1PcInstance)) {
                        // no-op：留空，讓其它技能分支照常判斷
                    } else {
                        final L1PcInstance pc = (L1PcInstance) cha;

                        boolean isTeleport = pc.getMap().isTeleportable();// 地圖設定是否可順移
                        int mapid = pc.getMapId();

                        // 傲慢之塔支配傳送符 / 幻象的傲慢之塔移動傳送符：例外放行
                        if ((pc.getInventory().checkItem(84041, 1) && mapid == 3301) ||
                                (pc.getInventory().checkItem(84042, 1) && mapid == 3302) ||
                                (pc.getInventory().checkItem(84043, 1) && mapid == 3303) ||
                                (pc.getInventory().checkItem(84044, 1) && mapid == 3304) ||
                                (pc.getInventory().checkItem(84045, 1) && mapid == 3305) ||
                                (pc.getInventory().checkItem(84046, 1) && mapid == 3306) ||
                                (pc.getInventory().checkItem(84047, 1) && mapid == 3307) ||
                                (pc.getInventory().checkItem(84048, 1) && mapid == 3308) ||
                                (pc.getInventory().checkItem(84049, 1) && mapid == 3309) ||
                                (pc.getInventory().checkItem(84050, 1) && mapid == 3310) ||
                                (pc.getInventory().checkItem(84071, 1) && mapid >= 3301 && mapid <= 3310)) {
                            isTeleport = true;
                        }

                        // 有記憶座標 → 指定點
                        if (_bookmarkX > 0 && _bookmarkY > 0) {
                            if (isTeleport) {
                                if (pc.getTradeID() != 0) {
                                    L1Trade trade = new L1Trade();
                                    trade.tradeCancel(pc);
                                }
                                pc.setTeleportX(_bookmarkX);
                                pc.setTeleportY(_bookmarkY);
                                pc.setTeleportMapId((short) _bookmarkId);
                                pc.setTeleportHeading(5);
                                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 169));
                                Teleportation.teleportation(pc);
                            } else {
                                pc.sendPackets(new S_ServerMessage(647));
                                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); // 原本 7 改常數更穩
                            }
                        }
                        // 沒書籤 → 隨機
                        else if (isTeleport) {
                            L1Location newLocation;
                            int newX = pc.getX();
                            int newY = pc.getY();
                            short mapId = pc.getMapId();
                            boolean right = false;
                            while (!right) {
                                newLocation = pc.getLocation().randomLocation(200, true);
                                newX = newLocation.getX();
                                newY = newLocation.getY();
                                mapId = (short) newLocation.getMapId();
                                right = !(newX == pc.getX() && newY == pc.getY());
                            }
                            if (pc.getTradeID() != 0) {
                                L1Trade trade = new L1Trade();
                                trade.tradeCancel(pc);
                            }
                            pc.setTeleportX(newX);
                            pc.setTeleportY(newY);
                            pc.setTeleportMapId(mapId);
                            pc.setTeleportHeading(5);
                            pc.sendPacketsAll(new S_SkillSound(pc.getId(), 169));
                            Teleportation.teleportation(pc);
                        }
                        // 不允許傳送
                        else {
                            pc.sendPackets(new S_ServerMessage(647));
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                        }
                    }

                    /*if (_skillId == MASS_TELEPORT) {
                        if (!(_user instanceof L1PcInstance) || !(cha instanceof L1PcInstance)) {
                            // no-op
                        } else {
                            final L1PcInstance pc = (L1PcInstance) cha;

                            // 有書籤 → 指定點
                            if (_bookmarkX > 0 && _bookmarkY > 0) {
                                if (pc.getMap().isEscapable() || pc.isGm()) {
                                    final int newX = _bookmarkX;
                                    final int newY = _bookmarkY;
                                    final short mapId = (short) _bookmarkId;

                                    final List<L1PcInstance> clanMember = World.get().getVisiblePlayer(pc);
                                    for (final L1PcInstance member : clanMember) {
                                        if ((pc.getLocation().getTileLineDistance(member.getLocation()) <= 3)
                                                && (member.getClanid() == pc.getClanid())
                                                && (pc.getClanid() != 0)
                                                && (member.getId() != pc.getId())
                                                && !member.isPrivateShop()) {
                                            member.setTeleportX(newX);
                                            member.setTeleportY(newY);
                                            member.setTeleportMapId(mapId);
                                            member.sendPackets(new S_Message_YN(748)); // 你的血盟成員想要傳送你。你答應嗎？
                                        }
                                    }
                                    L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(276)); // 在此無法使用傳送
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                                }
                            }
                            // 無書籤 → 任意點
                            else {
                                if (pc.getMap().isTeleportable() || pc.isGm()) {
                                    final L1Location newLocation = pc.getLocation().randomLocation(200, true);
                                    final int newX = newLocation.getX();
                                    final int newY = newLocation.getY();
                                    final short mapId = (short) newLocation.getMapId();

                                    final List<L1PcInstance> clanMember = World.get().getVisiblePlayer(pc);
                                    for (final L1PcInstance member : clanMember) {
                                        if ((pc.getLocation().getTileLineDistance(member.getLocation()) <= 3)
                                                && (member.getClanid() == pc.getClanid())
                                                && (pc.getClanid() != 0)
                                                && (member.getId() != pc.getId())
                                                && !member.isPrivateShop()) {
                                            member.setTeleportX(newX);
                                            member.setTeleportY(newY);
                                            member.setTeleportMapId(mapId);
                                            member.sendPackets(new S_Message_YN(748));
                                        }
                                    }
                                    L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(276));
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                                }
                            }
                        }
                    }*/
                } else if (this._skillId == COUNTER_DETECTION) { // 強力無所遁形術
                    if (cha instanceof L1PcInstance) {
                        this._dmg = magic.calcMagicDamage(this._skillId);
                    } else if (cha instanceof L1NpcInstance) {
                        final L1NpcInstance npc = (L1NpcInstance) cha;
                        final int hiddenStatus = npc.getHiddenStatus();
                        if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
                            npc.appearOnGround(this._player);
                        } else {
                            this._dmg = 0;
                        }
                    } else {
                        this._dmg = 0;
                    }
                    // ★★★ 回覆系スキル ★★★ 恢復系技能
                } else if (/*(this._skillId == HEAL) ||*/ (this._skillId == EXTRA_HEAL) || (this._skillId == GREATER_HEAL) || (this._skillId == FULL_HEAL) || (this._skillId == HEAL_ALL) || (this._skillId == NATURES_TOUCH) || (this._skillId == NATURES_BLESSING)) {
                    if (this._user instanceof L1PcInstance) {
                        cha.removeSkillEffect(WATER_LIFE); // 水之元氣
                    }
                    // ★★★ 攻擊系スキル ★★★ 攻擊系技能
                    // チルタッチ、バンパイアリックタッチ  (寒冷戰慄)
                } else if /*((this._skillId == CHILL_TOUCH) || */ (this._skillId == VAMPIRIC_TOUCH) {
                    heal = this._dmg;
                } else if ((this._skillId == 10026) || (this._skillId == 10027) || (this._skillId == 10028) || (this._skillId == 10029)) { // 安息攻擊
                    if (this._user instanceof L1NpcInstance) {
                        this._user.broadcastPacketAll(new S_NpcChat(_npc, (L1PcInstance) _target, "$3717")); // さあ、おまえに安息を與えよう。
                    } else {
                        this._player.broadcastPacketAll(new S_Chat(this._player, "$3717")); // さあ、おまえに安息を與えよう。
                    }
                } else if (this._skillId == 10057) { // 召喚傳送術
                    L1Teleport.teleportToTargetFront(cha, this._user, 1);
                    // ★★★ 確率系スキル ★★★ 確率系技能
                    /*
                     * } else if (_skillId == 20011) { // 毒霧-目標區域及周圍隨機生成5次
                     * L1Location baseloc = _target.getLocation(); //對象的座標
                     * SpawnPoisonArea(baseloc); //目標區域
                     *
                     * for (int i = 0; i < 5; i++) {//周圍隨機生成5次 L1Location newloc
                     * = baseloc.randomLocation(5, false);//對像周圍坐標 if (newloc !=
                     * baseloc) { SpawnPoisonArea(newloc); } }
                     */
                } else if ((this._skillId == SLOW) || /*(this._skillId == MASS_SLOW) ||*/ (this._skillId == ENTANGLE)) { // スロー、マス
                    // スロー、エンタングル
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.getHasteItemEquipped() > 0) {
                            continue;
                        }
                    }
                    if (cha.getBraveSpeed() == 5) {// 具有強化勇水狀態
                        continue;
                    }
                    switch (cha.getMoveSpeed()) {
                        case 0:
                            if (cha instanceof L1PcInstance) {
                                final L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 2, this._getBuffIconDuration));
                            }
                            cha.broadcastPacketAll(new S_SkillHaste(cha.getId(), 2, this._getBuffIconDuration));
                            cha.setMoveSpeed(2);
                            break;
                        case 1:
                            int skillNum = 0;
                            if (cha.hasSkillEffect(HASTE)) {
                                skillNum = HASTE;
                            } else if (cha.hasSkillEffect(GREATER_HASTE)) {
                                skillNum = GREATER_HASTE;
                            } else if (cha.hasSkillEffect(STATUS_HASTE)) {
                                skillNum = STATUS_HASTE;
                            }
                            if (skillNum != 0) {
                                cha.removeSkillEffect(skillNum);
                                cha.removeSkillEffect(this._skillId);
                                cha.setMoveSpeed(0);
                                continue;
                            }
                            break;
                    }
                    //	 * 毒咒
                    //				} else if (this._skillId == CURSE_POISON) {
                    //					L1DamagePoison.doInfection(this._user, cha, 3000, 5);

                } else if (this._skillId == WEAKNESS) { //弱化術36
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(-5);
                        pc.addHitup(-1);
                    }
                } else if (this._skillId == DISEASE) { // 疾病術51
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(-6);
                        pc.addAc(12);
                        pc.setSkillEffect(L1SkillId.DISEASE, 8000);
                    }
                }/* else if (this._skillId == ICE_LANCE) { // アイスランス
                    // 計算攻擊是否成功
                    this._isFreeze = magic.calcProbabilityMagic(this._skillId);
                    if (this._isFreeze) {// 凍結成功
                        final int time = this._skill.getBuffDuration() + 1;
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            // 法師技能(冰矛圍籬)
                            L1SpawnUtil.spawnEffect(81168, time, pc.getX(), pc.getY(), _user.getMapId(), _user, 0);
                            pc.sendPacketsAll(new S_Poison(pc.getId(), 2));
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
                        } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
                            final L1NpcInstance npc = (L1NpcInstance) cha;
                            // 法師技能(冰矛圍籬)
                            L1SpawnUtil.spawnEffect(81168, time, npc.getX(), npc.getY(), _user.getMapId(), _user, 0);
                            npc.broadcastPacketAll(new S_Poison(npc.getId(), 2));
                            npc.setParalyzed(true);
                        }
                    }
                }*/ else if (this._skillId == TURN_UNDEAD) { //起死回生術18
                    if ((undeadType == 1) || (undeadType == 3)) {// 不死系或僵屍系
                        this._dmg = cha.getCurrentHp();
                    }
                } else if (this._skillId == MANA_DRAIN) { // 魔力奪取26
                    final Random random = new Random();
                    final int chance = random.nextInt(10) + 5;
                    drainMana = chance + (this._user.getInt() / 2);
                    if (cha.getCurrentMp() < drainMana) {
                        drainMana = cha.getCurrentMp();
                    }

                } else if (this._skillId == WEAPON_BREAK) { // 壞物術技能判斷
                    // 如果目標是玩家
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) cha;  // 把目標安全轉型為玩家
                        final L1ItemInstance weapon = pc.getWeapon(); // 取得玩家目前裝備的武器
                        if (weapon != null) { // 確認玩家手上有武器
                            final int max = Math.max(1, this._user.getInt() / 3);
                            // 計算實際損傷值，範圍 1 ~ max
                            final int weaponDamage = java.util.concurrent.ThreadLocalRandom.current().nextInt(max) + 1;
                            // 發送系統訊息：你的 %0 損傷了
                            pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
                            // 對玩家武器套用耐久損傷
                            pc.getInventory().receiveDamage(weapon, weaponDamage);
                        }
                        // 如果目標不是玩家，而是 NPC
                    } else if (cha instanceof L1NpcInstance) {
                        // 設定 NPC 進入「武器壞掉」的狀態（對 NPC 的特殊標記）
                        ((L1NpcInstance) cha).setWeaponBreaked(true);
                    }
                }/* else if (this._skillId == FOG_OF_SLEEPING) {
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
                    }
                    cha.setSleeped(true);
                }*/ else if (this._skillId == GUARD_BRAKE) { //護衛毀滅183
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addAc(10);
                    }
                } else if (this._skillId == HORROR_OF_DEATH) { //驚悚死神193
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addStr(-3);
                        pc.addInt(-3);
                    }
                }
                /** 對PC使用技能的情況 */
                if ((this._calcType == PC_PC) || (this._calcType == NPC_PC)) {
                    // ★★★ 特殊系スキル★★★ 特殊技能
					/*if (this._skillId == CREATE_MAGICAL_WEAPON) { // クリエイト
						// マジカル ウェポン
						final L1PcInstance pc = (L1PcInstance) cha;
						final L1ItemInstance item = pc.getInventory().getItem(this._itemobjid);
						if ((item != null) && (item.getItem().getType2() == 1)) {
							final int item_type = item.getItem().getType2();
							final int safe_enchant = item.getItem().get_safeenchant();
							final int enchant_level = item.getEnchantLevel();
							String item_name = item.getName();
							if (safe_enchant < 0) { // 強化不可
								pc.sendPackets( // \f1何も起きませんでした。
										new S_ServerMessage(79));
							} else if (safe_enchant == 0) { // 安全圈+0
								pc.sendPackets( // \f1何も起きませんでした。
										new S_ServerMessage(79));
							} else if ((item_type == 1) && (enchant_level == 0)) {
								if (!item.isIdentified()) {// 未鑒定
									pc.sendPackets( // \f1%0が%2%1光ります。
											new S_ServerMessage(161, item_name, "$245", "$247"));
								} else {
									item_name = "+0 " + item_name;
									pc.sendPackets( // \f1%0が%2%1光ります。
											new S_ServerMessage(161, "+0 " + item_name, "$245", "$247"));
								}
								item.setEnchantLevel(1);
								pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
							} else {
								pc.sendPackets( // \f1何も起きませんでした。
										new S_ServerMessage(79));
							}
						} else {
							pc.sendPackets( // \f1何も起きませんでした。
									new S_ServerMessage(79));
						}
					} else */
                    if (this._skillId == BRING_STONE) { // ブリング ストーン
                        final L1PcInstance pc = (L1PcInstance) cha;
                        final Random random = new Random();
                        final L1ItemInstance item = pc.getInventory().getItem(this._itemobjid);
                        if (item != null) {
                            final int dark = (int) (10 + (pc.getLevel() * 0.8) + (pc.getWis() - 6) * 1.2);
                            final int brave = (int) (dark / 2.1);
                            final int wise = (int) (brave / 2.0);
                            final int kayser = (int) (wise / 1.9);
                            final int chance = random.nextInt(100) + 1;
                            if (item.getItem().getItemId() == 40320) {
                                pc.getInventory().removeItem(item, 1);
                                if (dark >= chance) {
                                    pc.getInventory().storeItem(40321, 1);
                                    pc.sendPackets(new S_ServerMessage(403, "$2475")); // %0を手に入れました。
                                } else {
                                    pc.sendPackets(new S_ServerMessage(280)); // \f1魔法が失敗しました。
                                }
                            } else if (item.getItem().getItemId() == 40321) {
                                pc.getInventory().removeItem(item, 1);
                                if (brave >= chance) {
                                    pc.getInventory().storeItem(40322, 1);
                                    pc.sendPackets(new S_ServerMessage(403, "$2476")); // %0を手に入れました。
                                } else {
                                    pc.sendPackets(new S_ServerMessage(280)); // \f1魔法が失敗しました。
                                }
                            } else if (item.getItem().getItemId() == 40322) {
                                pc.getInventory().removeItem(item, 1);
                                if (wise >= chance) {
                                    pc.getInventory().storeItem(40323, 1);
                                    pc.sendPackets(new S_ServerMessage(403, "$2477")); // %0を手に入れました。
                                } else {
                                    pc.sendPackets(new S_ServerMessage(280)); // \f1魔法が失敗しました。
                                }
                            } else if (item.getItem().getItemId() == 40323) {
                                pc.getInventory().removeItem(item, 1);
                                if (kayser >= chance) {
                                    pc.getInventory().storeItem(40324, 1);
                                    pc.sendPackets(new S_ServerMessage(403, "$2478")); // %0を手に入れました。
                                } else {
                                    pc.sendPackets(new S_ServerMessage(280)); // \f1魔法が失敗しました。
                                }
                            }
                        }
                    } else if (this._skillId == SUMMON_MONSTER) {
                        if (_user instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            int level = pc.getLevel();
                            int[] summons;
                            if (pc.getMap().isRecallPets()) {
                                if (pc.getInventory().checkEquipped(20284) || pc.getInventory().checkEquipped(120284)) { // 有裝備召喚戒指
                                    if (!pc.isSummonMonster()) {
                                        pc.setSummonMonster(true);
                                    }
                                    String SummonString = String.valueOf(pc.getSummonId());
                                    summonMonster(pc, SummonString);
                                } else {
                                    // 無裝備召喚戒指
                                    //summons = new int[] { 81210, 81213, 81216, 81219, 81222, 81225, 81228 };
                                    summons = new int[]{4070109, 4070109, 4070109, 4070109, 4070109, 4070109, 4070109}; // 高侖
                                    int summonid = 0;
                                    int summoncost = 25; // max count = 1
                                    int levelRange = 28;
                                    // Lv不足
                                    if (pc.getLevel() < levelRange) {
                                        // 743 等級太低而無法召喚怪物とができません。
                                        pc.sendPackets(new S_ServerMessage(743));
                                        return;
                                    }
                                    for (int i = 0; i < summons.length; i++) { // 該當ＬＶ範圍檢索
                                        if ((level < levelRange) || (i == summons.length - 1)) {
                                            summonid = summons[i];
                                            break;
                                        }
                                        levelRange += 4;
                                    }
                                    int petcost = 0;
                                    Object[] petlist = pc.getPetList().values().toArray();
                                    for (Object pet : petlist) {
                                        // 現在のペットコスト
                                        petcost += ((L1NpcInstance) pet).getPetcost();
                                    }
                                    int pcCha = pc.getCha();
                                    if (pcCha > 25) { // max count = 1
                                        pcCha = 25;
                                    }
                                    int charisma = pcCha + 6 - petcost;
                                    int summoncount = charisma / summoncost;
                                    L1Npc npcTemp = NpcTable.get().getTemplate(summonid);
                                    for (int i = 0; i < summoncount; i++) {
                                        L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
                                        summon.setPetcost(summoncost);
                                    }
                                }
                                pc.setSummonMonster(false);
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        }
                    } else if (this._skillId == ABSOLUTE_BARRIER) { // 絕對屏障 バリア
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.stopHpRegeneration();
                        pc.stopMpRegeneration();
                    }
                    // ★★★ 變化系スキル（エンチャント） ★★★ 變化系技能
                    if (this._skillId == LIGHT) { // ライト
                        // addMagicList()後に、turnOnOffLight()でパケット送信
                    } else if (this._skillId == GLOWING_AURA) { // 灼熱武器 //(灼熱靈氣：1階段)
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addHitup(5);
                        pc.addDmgup(5);
                        pc.sendPackets(new S_PacketBoxIconAura(113, this._getBuffIconDuration));
                    } else if (this._skillId == SHINING_AURA) { //(灼熱靈氣：2階段)
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addAc(-8);
                        pc.addMr(10);
                        pc.addHitup(2);
                        pc.addDmgup(2);
                        pc.sendPackets(new S_SPMR(pc));
                        pc.sendPackets(new S_PacketBoxIconAura(114, this._getBuffIconDuration));
                    } else if (this._skillId == BRAVE_AURA) { // 勇猛意志 //
                        final L1PcInstance pc = (L1PcInstance) cha;
                        // pc.addDmgup(-5);
                        pc.sendPackets(new S_PacketBoxIconAura(116, this._getBuffIconDuration));
                    } else if (this._skillId == GLOWING_AURA_2) { // 勇猛武器
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addHitup(10);
                        pc.addDmgup(10);
                        pc.sendPackets(new S_PacketBoxIconAura(117, this._getBuffIconDuration));
                    } else if (this._skillId == Armor_Valor) { //勇猛盔甲
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDamageReductionByArmor(7);
                        pc.sendPackets(new S_SPMR(pc));// 防禦更新
                        pc.sendPackets(new S_PacketBoxIconAura(118, this._getBuffIconDuration));
                    } else if (this._skillId == DEATH_HEAL) { // 法師新技能 治愈逆行
                        if (cha instanceof L1PcInstance) {
                            if (_user.getId() == cha.getId()) { // 不能對自己施放
                                continue;
                            }
                            // 避免雙重機率判斷：如果技能類型已經是機率或詛咒，則前面已經判斷過
                            int skillType = _skill.getType();
                            if (skillType != L1Skills.TYPE_CURSE && skillType != L1Skills.TYPE_PROBABILITY) {
                                if (!magic.calcProbabilityMagic(DEATH_HEAL)) { // 命中判斷
                                    continue;
                                }
                            }
                            L1PcInstance pc = (L1PcInstance) cha;
                            /*if (pc.hasSkillEffect(DEATH_HEAL)) {
                                pc.sendPackets(new S_NewSkillIcon(DEATH_HEAL, false, -1));
                                pc.removeSkillEffect(DEATH_HEAL);
                            }*/
                            int chance = 16; // 固定 16 秒
                            _skillTime = chance;
                            //pc.sendPackets(new S_NewSkillIcon(DEATH_HEAL, true, chance));
                            //pc.setSkillEffect(DEATH_HEAL, chance * 1000); // 由 addMagicList 統一處理
                            pc.sendPackets(new S_SkillSound(pc.getId(), 14501));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 14501));
                            pc.sendPackets(new S_SystemMessage("\\aB治癒逆行技能造成恢復能力轉為傷害值",17));
                            if (_user instanceof L1PcInstance) {
                                ((L1PcInstance) _user).sendPackets(new S_SystemMessage("對方中了治癒逆行!"));
                            }
                        }


                    } else if (this._skillId == ABSOLUTE_BLADE) { // 騎士新技能 絕御之刃
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.setSkillEffect(ABSOLUTE_BLADE, 8 * 1000);
                            //pc.sendPackets(new S_NewSkillIcon(ABSOLUTE_BLADE, true, 8));
                        }
                    } else if (this._skillId == ASSASSIN) { // 黑妖新技能 暗殺者
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.setSkillEffect(ASSASSIN, 15 * 1000);
                            //pc.sendPackets(new S_NewSkillIcon(ASSASSIN, true, 15));
                        }
                    } else if (this._skillId == GRACE_AVATAR) { // 王族新技能 恩典庇護
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.hasSkillEffect(GRACE_AVATAR)) {
                                pc.sendPackets(new S_NewSkillIcon(GRACE_AVATAR, false, -1));
                                pc.removeSkillEffect(GRACE_AVATAR);
                            }
                            pc.setGraceLv(pc.getLevel());
                            pc.addRegistSustain(10 + pc.getGraceLv()); // 支撐耐性（自分）
                            pc.addRegistStun(10 + pc.getGraceLv()); // 暈眩耐性（自分）
                            //pc.getResistance().addDESPERADO(10 + pc.getGraceLv()); // 恐怖耐性（自分）
                            pc.setSkillEffect(GRACE_AVATAR, 15 * 1000);
                            //pc.sendPackets(new S_NewSkillIcon(GRACE_AVATAR, true, 15));
                            pc.sendPackets(new S_SkillSound(pc.getId(), 14495));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 14495));
                            for (L1PcInstance player : World.get().getVisiblePlayer(pc, 18)) {// 18格範圍
                                if (pc.getParty() != null) {
                                    if (pc.getParty().isMember(player) && player != null) {
                                        if (player.hasSkillEffect(GRACE_AVATAR)) {
                                            player.sendPackets(new S_NewSkillIcon(GRACE_AVATAR, false, -1));
                                            player.removeSkillEffect(GRACE_AVATAR);
                                        }
                                        player.setGraceLv(pc.getLevel());
                                        player.addRegistSustain(10 + player.getGraceLv()); // 支撐耐性（隊友）
                                        player.addRegistStun(10 + player.getGraceLv()); // 暈眩耐性（隊友）
                                        //player.getResistance().addDESPERADO(10 + player.getGraceLv()); // 恐怖耐性（隊友）
                                        //player.sendPackets(new S_NewSkillIcon(GRACE_AVATAR, true, 15));
                                        player.setSkillEffect(GRACE_AVATAR, 15 * 1000);
                                        player.sendPackets(new S_ServerMessage(4734));
                                    }
                                }
                            }
                        }
                    } else if (this._skillId == SOUL_BARRIER) { // 精靈新技能 魔力護盾
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.setSkillEffect(SOUL_BARRIER, 600 * 1000);
                            //pc.sendPackets(new S_NewSkillIcon(SOUL_BARRIER, true, 600));
                        }
                    } else if (this._skillId == DESTROY) { // 龍騎士新技能 撕裂護甲
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.setSkillEffect(DESTROY, 30 * 1000);
                            //pc.sendPackets(new S_NewSkillIcon(DESTROY, true, 30));
                        }
                    } else if (this._skillId == IMPACT) { // 幻術師新技能 衝突強化
                        if (_user instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) _user;
                            if (_target instanceof L1PcInstance) {
                                L1PcInstance target = (L1PcInstance) _target;
                                target.setSkillEffect(IMPACT, 15 * 1000);
                                //target.sendPackets(new S_NewSkillIcon(IMPACT, true, 15));
                                target.sendPackets(new S_SkillSound(target.getId(), 14513));
                                Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 14513));
                                int upskill = pc.getLevel() - 80;
                                if (upskill >= 5) {
                                    upskill = 5;
                                }
                                target.setImpactUp(5 + upskill);
                            }
                        }
                    } else if (this._skillId == TITANL_RISING) { // 狂戰士新技能 泰坦狂暴
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.setSkillEffect(TITANL_RISING, 2400 * 1000);
                            //pc.sendPackets(new S_NewSkillIcon(TITANL_RISING, true, 2400));
                            int upHP = pc.getLevel() - 80;
                            if (upHP >= 5) {
                                upHP = 5;
                            }
                            pc.setRisingUp(5 + upHP);
                        }
                    } else if (this._skillId == SHIELD) { // 保護罩
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addAc(-2);
                        pc.sendPackets(new S_SkillIconShield(5, this._getBuffIconDuration));
                    } else if (this._skillId == DRESS_DEXTERITY) { // 敏捷提升
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDex((byte) 3);
                        pc.sendPackets(new S_Dexup(pc, 3, this._getBuffIconDuration));
                    } else if (this._skillId == DRESS_MIGHTY) { // 力量提升
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addStr((byte) 3);
                        pc.sendPackets(new S_Strup(pc, 3, this._getBuffIconDuration));
                    } else if (this._skillId == SHADOW_FANG) { // 暗影之牙
                        final L1PcInstance pc = (L1PcInstance) cha;
                        final L1ItemInstance item = pc.getInventory().getItem(this._itemobjid);
                        if ((item != null) && (item.getItem().getType2() == 1)) {
                            pc.sendPackets(new S_PacketBoxWeapon(_skill.getCastGfx(), this._skill.getBuffDuration()));
                            item.setSkillWeaponEnchant(pc, this._skillId, this._skill.getBuffDuration() * 1000);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                        //					} else if (this._skillId == ENCHANT_WEAPON) { // 擬似魔法武器
                        //						final L1PcInstance pc = (L1PcInstance) cha;
                        //						final L1ItemInstance item = pc.getInventory().getItem(this._itemobjid);
                        if ((item != null) && (item.getItem().getType2() == 1)) {
                            pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
                            pc.sendPackets(new S_PacketBoxWeapon(_skill.getCastGfx(), this._skill.getBuffDuration()));
                            item.setSkillWeaponEnchant(pc, this._skillId, this._skill.getBuffDuration() * 1000);
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                    } else if (/*(this._skillId == HOLY_WEAPON) // 神聖武器
							|| */(this._skillId == BLESS_WEAPON)) { // 祝福魔法武器
                        if (!(cha instanceof L1PcInstance)) {
                            return;
                        }
                        final L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.getWeapon() == null) {
                            pc.sendPackets(new S_ServerMessage(79));
                            return;
                        }
                        for (final L1ItemInstance item : pc.getInventory().getItems()) {
                            if (pc.getWeapon().equals(item)) {
                                pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
                                pc.sendPackets(new S_PacketBoxWeapon(_skill.getCastGfx(), this._skill.getBuffDuration()));
                                item.setSkillWeaponEnchant(pc, this._skillId, this._skill.getBuffDuration() * 1000);
                                return;
                            }
                        }
                    } else if (this._skillId == BLESSED_ARMOR) { // 鎧甲護持
                        final L1PcInstance pc = (L1PcInstance) cha;
                        final L1ItemInstance item = pc.getInventory().getItem(this._itemobjid);
                        if ((item != null) && (item.getItem().getType2() == 2) && (item.getItem().getType() == 2)) {
                            pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
                            item.setSkillArmorEnchant(pc, this._skillId, this._skill.getBuffDuration() * 1000);
                            if (item.isEquipped()) {
                                pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_OTHER3, _skill.getBuffDuration(), _skill.getCastGfx(), 0));
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                    } else if (this._skillId == EARTH_BLESS) { // 大地的護衛
                        final L1PcInstance pc = (L1PcInstance) cha;
                        // pc.addAc(-7);
                        pc.sendPackets(new S_SkillIconShield(7, this._getBuffIconDuration));
                    } else if (this._skillId == RESIST_MAGIC) { // 魔法防禦
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addMr(10);
                        pc.sendPackets(new S_SPMR(pc));
                    } else if (this._skillId == CLEAR_MIND) { // 淨化精神
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addWis((byte) 3);
                        pc.resetBaseMr();
                    } else if (this._skillId == RESIST_ELEMENTAL) { // 屬性防禦
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addWind(10);
                        pc.addWater(10);
                        pc.addFire(10);
                        pc.addEarth(10);
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                    } else if (this._skillId == ELEMENTAL_PROTECTION) { // 單屬性防禦
                        final L1PcInstance pc = (L1PcInstance) cha;
                        final int attr = pc.getElfAttr();
                        if (attr == 1) {
                            pc.addEarth(50);
                        } else if (attr == 2) {
                            pc.addFire(50);
                        } else if (attr == 4) {
                            pc.addWater(50);
                        } else if (attr == 8) {
                            pc.addWind(50);
                        }
                    } else if ((this._skillId == INVISIBILITY) || (this._skillId == BLIND_HIDING)) { // 隱身術、暗隱術
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.sendPackets(new S_Invis(pc.getId(), 1));
                        pc.broadcastPacketAll(new S_RemoveObject(pc));
                        if (this._skillId == BLIND_HIDING) {
                            pc.setSkillEffect(L1SkillId.BLIND_HIDING, 18000); // 60秒
                        }
                    } else if (this._skillId == IRON_SKIN) { // 鋼鐵防護
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addAc(-10);
                        pc.sendPackets(new S_SkillIconShield(10, this._getBuffIconDuration));
                    } else if (this._skillId == EARTH_SKIN) { // 大地防護
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addAc(-6);
                        pc.sendPackets(new S_SkillIconShield(6, this._getBuffIconDuration));
                    } else if (this._skillId == PHYSICAL_ENCHANT_STR) { // 體魄強健術：STR
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addStr((byte) 5);
                        pc.sendPackets(new S_Strup(pc, 5, this._getBuffIconDuration));
                    } else if (this._skillId == PHYSICAL_ENCHANT_DEX) { // 通暢氣脈術：DEX
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDex((byte) 5);
                        pc.sendPackets(new S_Dexup(pc, 5, this._getBuffIconDuration));
                    } else if (this._skillId == PHYSICAL_ENCHANT_INT_DEX_STR) {
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addInt((byte) 1);
                        pc.addDex((byte) 1);
                        pc.addStr((byte) 1);
                        //						pc.sendPackets(new S_InventoryIcon(10534, true, 2747, this._getBuffIconDuration));
                    } else if (this._skillId == FIRE_WEAPON) { // 火焰武器
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(4);
                        pc.sendPackets(new S_PacketBoxIconAura(147, this._getBuffIconDuration));
                    } else if (this._skillId == FIRE_BLESS) { // 舞躍之火
                        // final L1PcInstance pc = (L1PcInstance) cha;
                        // pc.addDmgup(4);
                        /*
                         * pc.sendPackets(new S_PacketBoxIconAura(154,
                         * this._getBuffIconDuration));
                         */
                    } else if (this._skillId == BURNING_WEAPON) { // 烈炎武器
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(6);
                        pc.addHitup(3);
                        pc.sendPackets(new S_PacketBoxIconAura(162, this._getBuffIconDuration));
                    } else if (this._skillId == WIND_SHOT) { // ウィンド ショット
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addBowHitup(6);
                        pc.sendPackets(new S_PacketBoxIconAura(148, this._getBuffIconDuration));
                    } else if (this._skillId == STORM_EYE) { // ストーム アイ
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addBowHitup(2);
                        pc.addBowDmgup(3);
                        pc.sendPackets(new S_PacketBoxIconAura(155, this._getBuffIconDuration));
                    } else if (this._skillId == STORM_SHOT) { // ストーム ショット
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addBowDmgup(5);
                        pc.addBowHitup(-1);
                        pc.sendPackets(new S_PacketBoxIconAura(165, this._getBuffIconDuration));

                    } else if (this._skillId == BERSERKERS) { // 狂暴術
                        final L1PcInstance pc = (L1PcInstance) cha;
                        // 取得玩家狂暴術的技能等級
                        int bookLevel = pc.getSkillLevel(BERSERKERS);
                        // 從 SkillEnhanceTable 讀取狂暴術的強化資料
                        L1SkillEnhance enhanceData = SkillEnhanceTable.get().getEnhanceData(BERSERKERS, bookLevel);
                        // 預設加成數值
                        int addAc = 10;
                        int addDmgup = 5;
                        // 如果存在強化資料，則取設定值
                        if (enhanceData != null) {
                            addAc = enhanceData.getSetting1();
                            addDmgup = enhanceData.getSetting2();
                        }
                        pc.addAc(addAc);
                        pc.addDmgup(addDmgup);
                        pc.stopHpRegeneration();
                        // 準備提示訊息
                        String msg = "技能等級：lv." + bookLevel + "，額外增加：AC +" + addAc + "，傷害加成 +" + addDmgup;
                        // 提示玩家：送出系統訊息與/或提示音效
                        pc.sendPackets(new S_SystemMessage(msg));

                    } else if (this._skillId == GREATER_HASTE) { // グレーター ヘイスト
                        final L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.getHasteItemEquipped() > 0) {
                            continue;
                        }
                        if (pc.getMoveSpeed() != 2) { // スロー中以外
                            pc.setDrink(false);
                            pc.setMoveSpeed(1);
                            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, this._getBuffIconDuration));
                            pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
                        } else { // スロー中
                            int skillNum = 0;
                            if (pc.hasSkillEffect(SLOW)) {
                                skillNum = SLOW;
                            } /*else if (pc.hasSkillEffect(MASS_SLOW)) {
								skillNum = MASS_SLOW;
							}*/ else if (pc.hasSkillEffect(ENTANGLE)) {
                                skillNum = ENTANGLE;
                            }
                            if (skillNum != 0) {
                                pc.removeSkillEffect(skillNum);
                                pc.removeSkillEffect(GREATER_HASTE);
                                pc.setMoveSpeed(0);
                                continue;
                            }
                        }
                    } else if (this._skillId == HOLY_WALK) { // ホーリーウォーク
                        final L1PcInstance pc = (L1PcInstance) cha;
                        if (pc.hasSkillEffect(L1SkillId.STATUS_BRAVE3)) {
                        }
                        if (pc.isHOLY_WALK2()) { // 神聖迅猛
                            L1BuffUtil.braveStart(pc); // 先清理既有勇敢/疾走類狀態
                            pc.setSkillEffect(L1SkillId.STATUS_BRAVE, this._getBuffIconDuration * 1000);
                            pc.setBraveSpeed(1); // 勇水速度
                            pc.sendPackets(new S_SkillBrave(pc.getId(), 1, this._getBuffIconDuration));
                            pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 1, 0));
                        } else {
                            pc.setSkillEffect(L1SkillId.HOLY_WALK, this._getBuffIconDuration * 1000);
                            pc.setBraveSpeed(4);
                            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, this._getBuffIconDuration));
                            pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 4, 0));
                        }
                    } else if ((this._skillId == MOVING_ACCELERATION) || (this._skillId == WIND_WALK)) { // ホーリーウォーク、ムービングアクセレーション、ウィンドウォーク
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.setBraveSpeed(4);
                        pc.sendPackets(new S_SkillBrave(pc.getId(), 4, this._getBuffIconDuration));
                        pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 4, 0));
                    } else if (this._skillId == ILLUSION_OGRE) { // イリュージョン：オーガ
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addDmgup(4);
                        pc.addHitup(4);
                    } else if (this._skillId == ILLUSION_DIA_GOLEM) { // イリュージョン：ダイアモンドゴーレム
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.addAc(-8);
                    } /*else if (_skillId == GIGANTIC) {
						final L1PcInstance pc = (L1PcInstance) cha;
						final int HP = (pc.getBaseMaxHp() / 100) * (pc.getLevel() / 2);
						pc.setGiganticHp(HP);
						pc.addMaxHp(pc.getGiganticHp());
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
						if (pc.isInParty()) {
							pc.getParty().updateMiniHP(pc);
						}
					}*/
                }
                /** 對NPC使用技能的情況 */
                if ((_calcType == PC_NPC) || (_calcType == NPC_NPC)) {
                    // ★★★ ペット系スキル ★★★ 寵物使用的技能
                    if ((_skillId == TAMING_MONSTER) && ((L1MonsterInstance) cha).getNpcTemplate().isTamable()) { // テイミングモンスター
                        int petcost = 0;
                        final Object[] petlist = _user.getPetList().values().toArray();
                        for (final Object pet : petlist) {
                            // 現在のペットコスト
                            petcost += ((L1NpcInstance) pet).getPetcost();
                        }
                        int charisma = _user.getCha();
                        if (_player.isElf()) { // エルフ
                            charisma += 12;
                        } else if (_player.isWizard()) { // ウィザード
                            charisma += 6;
                        }
                        charisma -= petcost;
                        if (charisma >= 6) { // ペットコストの確認
                            final L1SummonInstance summon = new L1SummonInstance(this._targetNpc, this._user, false);
                            this._target = summon; // ターゲット入替え
                        } else {
                            this._player.sendPackets(new S_ServerMessage(319)); // \f1これ以上のモンスターを操ることはできません。
                        }
                    } else if (this._skillId == CREATE_ZOMBIE) { // クリエイトゾンビ
                        int petcost = 0;
                        final Object[] petlist = this._user.getPetList().values().toArray();
                        for (final Object pet : petlist) {
                            // 現在のペットコスト
                            petcost += ((L1NpcInstance) pet).getPetcost();
                        }
                        int charisma = this._user.getCha();
                        if (this._player.isElf()) { // エルフ
                            charisma += 12;
                        } else if (this._player.isWizard()) { // ウィザード
                            charisma += 6;
                        }
                        charisma -= petcost;
                        if (charisma >= 6) { // ペットコストの確認
                            final L1SummonInstance summon = new L1SummonInstance(this._targetNpc, this._user, true);
                            this._target = summon; // ターゲット入替え
                        } else {
                            this._player.sendPackets(new S_ServerMessage(319)); // \f1これ以上のモンスターを操ることはできません。
                        }
                    } else if (this._skillId == WEAK_ELEMENTAL) { // ウィーク エレメンタル
                        if (cha instanceof L1MonsterInstance) {
                            final L1Npc npcTemp = ((L1MonsterInstance) cha).getNpcTemplate();
                            final int weakAttr = npcTemp.get_weakAttr();
                            if ((weakAttr & 1) == 1) { // 地
                                cha.broadcastPacketAll(new S_SkillSound(cha.getId(), 2169));
                            }
                            if ((weakAttr & 2) == 2) { // 火
                                cha.broadcastPacketAll(new S_SkillSound(cha.getId(), 2167));
                            }
                            if ((weakAttr & 4) == 4) { // 水
                                cha.broadcastPacketAll(new S_SkillSound(cha.getId(), 2166));
                            }
                            if ((weakAttr & 8) == 8) { // 風
                                cha.broadcastPacketAll(new S_SkillSound(cha.getId(), 2168));
                            }
                        }
                    } else if (this._skillId == RETURN_TO_NATURE) { //釋放元素145
                        if (cha instanceof L1SummonInstance) {
                            final L1SummonInstance summon = (L1SummonInstance) cha;
                            summon.broadcastPacketAll(new S_SkillSound(summon.getId(), 2245));
                            summon.returnToNature();
                        } else {
                            if (this._user instanceof L1PcInstance) {
                                this._player.sendPackets(new S_ServerMessage(79));
                            }
                        }
                    }
                }
                // ■■■■ 個別處理ここまで ■■■■
                // 治愈性魔法攻擊不死系的怪物。
                if ((this._skill.getType() == L1Skills.TYPE_HEAL) && (this._calcType == PC_NPC) && (undeadType == 1)) {// 不死系
                    this._dmg = _heal; // もし、アンデットで回覆系スキルならばダメージになる。
                }
                // 治愈性魔法無法對此不死系起作用
                if ((this._skill.getType() == L1Skills.TYPE_HEAL) && (this._calcType == PC_NPC) && (undeadType == 4)) {// 不死系(治愈術無傷害)
                    this._heal = 0; // もし、アンデット系ボスで回覆系スキルならば無效
                }
                // 無法對城門、守護塔補血
                if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) && (this._heal < 0)) { // ガーディアンタワー、ドアにヒールを使用
                    this._heal = 0;
                }
                // 執行傷害及吸魔計算
                if ((this._dmg != 0) || (drainMana != 0)) {
                    // System.out.println("結果質2:(HP) " + this._dmg);
                    try {
                        if (this._dmg >= 500 && (cha.getName() == "秋海棠" || cha.getName() == "低調" || cha.getName().startsWith("進擊") || _user.getName() == "秋海棠" || _user.getName() == "低調" || _user.getName().startsWith("進擊"))) {
                            L1Character.writeReceivelog(_user, cha, "技能編號:" + _skillId + ",技能名稱: " + _skill.getName(), this._dmg, cha.getCurrentHp());
                        }
                    } catch (Exception e) {
                        System.out.println("傷害記錄LOG發生錯誤" + e.getMessage() + "," + Arrays.toString(e.getStackTrace()));
                    }
                    magic.commit(this._dmg, drainMana); // ダメージ系、回覆系の值をターゲットにコミットする。
                }
                // 補血判斷
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_heal != 0) && (undeadType != 1)) {
                    cha.setCurrentHp(_heal + cha.getCurrentHp());
                }
                // 非治愈性魔法補血判斷(寒戰、吸吻等)
                if (heal > 0) {
                    _user.setCurrentHp(heal + _user.getCurrentHp());
                }
                if (cha instanceof L1PcInstance) { // ターゲットがPCならば、ACとステータスを送信
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    sendHappenMessage(pc); // ターゲットにメッセージを送信
                }
                addMagicList(cha, false); // 更新右上角狀態圖示
            }
            if ((_skillId == DETECTION) || (_skillId == COUNTER_DETECTION) || (_skillId == FREEZING_BREATH) || (_skillId == ARM_BREAKER)) { // 無所類型技能
                if (_user instanceof L1NpcInstance) {
                    final L1NpcInstance npc = (L1NpcInstance) _user;
                    _log.warn("NPC使用了玩家專用技能(無所遁形類): NPCID=" + npc.getNpcId() + " Name=" + npc.getName() + " SkillId=" + _skillId + " MapId=" + npc.getMapId() + " Loc=" + npc.getX() + "," + npc.getY());
                }
                detection(_user);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void detection(final L1Character cha) {
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            if (!pc.isGmInvis() && pc.isInvisble()) { // 自分
                pc.delInvis();
                pc.beginInvisTimer();
            }
        }
        for (final L1PcInstance tgt : World.get().getVisiblePlayer(cha)) {
            if (!tgt.isGmInvis() && tgt.isInvisble()) {
                tgt.delInvis();
            }
        }
        // 偵測陷阱的處理
        if (cha instanceof L1PcInstance) {
            WorldTrap.get().onDetection((L1PcInstance) cha);
        }
    }

    /**
     * 群體魔法目標判定
     *
     */
    private boolean isTargetCalc(final L1Character cha) {
        // 攻擊魔法のNon?PvP判定
        if (this._skill.getTarget().equals("attack") && (this._skillId != TURN_UNDEAD)) { // 攻擊魔法
            if (this.isPcSummonPet(cha)) { // 對像がPC、サモン、ペット
                if (this._player.isSafetyZone() || cha.isSafetyZone() || // 攻擊する側または攻擊される側がセーフティーゾーン
                        this._player.checkNonPvP(this._player, cha)) { // Non-PvP設定
                    return false;
                }
            }
        }
        switch (this._skillId) {
            // 沉睡之霧 (魔法大師) - 改為自我施放的BUFF技能
            /*case FOG_OF_SLEEPING:
                if (this._user.getId() == cha.getId()) {
                    return false;
                }
                break;*/
            // 集體緩速術
            //		case MASS_SLOW:
            //			if (this._user.getId() == cha.getId()) {
            //				return false;
            //			}
            //
            //			if (cha instanceof L1SummonInstance) {
            //				final L1SummonInstance summon = (L1SummonInstance) cha;
            //				if (this._user.getId() == summon.getMaster().getId()) {
            //					return false;
            //				}
            //
            //			} else if (cha instanceof L1PetInstance) {
            //				final L1PetInstance pet = (L1PetInstance) cha;
            //				if (this._user.getId() == pet.getMaster().getId()) {
            //					return false;
            //				}
            //			}
            //			break;
            // 集體傳送術
            //case MASS_TELEPORT:
            //    if (this._user.getId() != cha.getId()) {
            //        return false;
            //    }
            //    break;
        }
        return true;
    }

    @SuppressWarnings("unused")
    private int calculateNewCoordinate(final int x, final int y, final int ref) {
        if (ref > 0) {
            return x - ref; // 推離原點
        } else if (ref < 0) {
            return x + ref; // 推向原點
        }
        return x; // 若 ref 為 0，保持不變
    }

    /**
     * 目標對像 是否為寵物 召喚獸 虛擬人物
     *
     */
    private boolean isPcSummonPet(final L1Character cha) {
        // PC 對 PC
        switch (this._calcType) {
            case PC_PC:
                return true;
            // PC 對 NPC
            case PC_NPC:
                // 目標對像為召喚獸
                if (cha instanceof L1SummonInstance) {
                    final L1SummonInstance summon = (L1SummonInstance) cha;
                    // 目標對像具有主人
                    if (summon.isExsistMaster()) {
                        return true;
                    }
                }
                // 目標對像為寵物
                if (cha instanceof L1PetInstance) {
                    return true;
                }
                if (cha instanceof L1DeInstance) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * 檢查是否為不合法的目標
     *
     * @return true:不合法 false:合法
     */
    private boolean isTargetFailure(final L1Character cha) {
        boolean isTU = false; // 是否可以使用驅散亡靈技能
        boolean isErase = false; // 是否可以使用驅散魔法技能
        boolean isManaDrain = false; // 是否可以使用吸取魔法技能
        int undeadType = 0; // 記錄亡靈類型
        // 如果目標是守衛塔或門，則機率系技能無效
        if ((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) {
            return true;
        }
        // 如果目標是玩家
        if (cha instanceof L1PcInstance) {
            if ((this._calcType == PC_PC) && this._player.checkNonPvP(this._player, cha)) { // 如果是非PvP設定
                final L1PcInstance pc = (L1PcInstance) cha;
                if ((this._player.getId() == pc.getId()) || ((pc.getClanid() != 0) && (this._player.getClanid() == pc.getClanid()))) {
                    return false; // 如果是同一個玩家或同一個公會的玩家，返回false
                }
                return true; // 否則返回true
            }
            return false; // 如果不是PvP情況，返回false
        }
        // 如果目標是怪物，檢查是否可以使用驅散亡靈技能
        if (cha instanceof L1MonsterInstance) {
            isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU(); // 取得是否可以驅散亡靈
        }
        // 如果目標是怪物，檢查是否可以使用驅散魔法技能
        if (cha instanceof L1MonsterInstance) {
            isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase(); // 取得是否可以驅散魔法
        }
        // 如果目標是怪物，判斷是否爲亡靈
        if (cha instanceof L1MonsterInstance) {
            undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead(); // 取得亡靈類型
        }
        // 如果目標是怪物，判斷是否可以吸取魔法
        if (cha instanceof L1MonsterInstance) {
            isManaDrain = true; // 如果目標是怪物，設爲可以吸取魔法
        }
        /*
         * 成功除外條件：
         * 1. 驅散亡靈技能成功，但目標不是亡靈。
         * 2. 驅散亡靈技能成功，但目標無法被驅散。
         * 3. 以下技能無效：減速、群體減速、吸取魔法、纏繞、驅散魔法、風縛。
         * 4. 吸取魔法成功，但目標不是怪物。
         */
        if (((this._skillId == TURN_UNDEAD) && ((undeadType == 0) || (undeadType == 2))) ||
                ((this._skillId == TURN_UNDEAD) && (!isTU)) ||
                (((this._skillId == ERASE_MAGIC) || (this._skillId == SLOW) || (this._skillId == MANA_DRAIN)
                        /*|| (this._skillId == MASS_SLOW)*/ || (this._skillId == ENTANGLE) || (this._skillId == WIND_SHACKLE)) && (!isErase)) ||
                ((this._skillId == MANA_DRAIN) && (!isManaDrain))) {
            return true; // 如果滿足以上任何一個條件，返回true，表示失敗
        }

        return false; // 如果沒有任何條件滿足，返回false，表示成功
    }

    /**
     * 是否使用了魔法屏障
     */
    private boolean isUseCounterMagic(final L1Character cha) {
        // カウンターマジック有效なスキルでカウンターマジック中
        if (this._isCounterMagic && cha.hasSkillEffect(COUNTER_MAGIC)) {
            cha.removeSkillEffect(COUNTER_MAGIC);
            final int castgfx2 = SkillsTable.get().getTemplate(COUNTER_MAGIC).getCastGfx2();
            cha.broadcastPacketAll(new S_SkillSound(cha.getId(), castgfx2));
            if (cha instanceof L1PcInstance) {
                final L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_SkillSound(pc.getId(), castgfx2));
            }
            return true;
        }
        return false;
    }

    /**
     * 生成毒霧區域
     *
     */
    @SuppressWarnings("unused")
    private void SpawnPoisonArea(L1Location baseloc) {
        int locX = 0;
        int locY = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                switch (_user.getHeading()) {
                    case 0:
                        locX = (-1 + j);
                        locY = -1 * (-3 + i);
                        break;
                    case 1:
                        locX = -1 * (2 + j - i);
                        locY = -1 * (-4 + j + i);
                        break;
                    case 2:
                        locX = -1 * (3 - i);
                        locY = (-1 + j);
                        break;
                    case 3:
                        locX = -1 * (4 - j - i);
                        locY = -1 * (2 + j - i);
                        break;
                    case 4:
                        locX = (1 - j);
                        locY = -1 * (3 - i);
                        break;
                    case 5:
                        locX = -1 * (-2 - j + i);
                        locY = -1 * (4 - j - i);
                        break;
                    case 6:
                        locX = -1 * (-3 + i);
                        locY = (1 - j);
                        break;
                    case 7:
                        locX = -1 * (-4 + j + i);
                        locY = -1 * (-2 - j + i);
                        break;
                }
                L1EffectInstance effect = L1SpawnUtil.spawnEffect(86125, 3, baseloc.getX() - locX, baseloc.getY() - locY, _user.getMapId(), _user, 20011);
                effect.broadcastPacketAll(new S_SkillSound(effect.getId(), 1263));// 毒霧特效
            }
        }
    }

    public void removeNewIcon(L1PcInstance pc, int skillid) {
        switch (skillid) {
            case ABSOLUTE_BLADE:
            case ASSASSIN:
            case SOUL_BARRIER:
            case DESTROY:
            case IMPACT:
            case TITANL_RISING:
            case BLAZING_SPIRITS:
            case DEATH_HEAL:
            case GRACE_AVATAR:
                pc.sendPackets(new S_NewSkillIcon(skillid, false, -1));
                pc.removeSkillEffect(skillid);
                break;
            default:
                break;
        }
    }
}
