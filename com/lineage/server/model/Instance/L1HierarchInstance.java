package com.lineage.server.model.Instance;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcHierarchTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Hierarch;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

/**
 * 魔法祭司-控制項
 *
 * @author admin
 */
public class L1HierarchInstance extends L1NpcInstance {
    private static final Log _log = LogFactory.getLog(L1HierarchInstance.class);
    private static final long serialVersionUID = 1L;
    private static Random _random = new Random();
    private int _itemObjId;
    private int _time = 10800;
    private int[] _skill_id = null;
    private int[] _skill_mp = null;
    private int _srcMoveSpeed;
    private int _srcBraveSpeed;

    public L1HierarchInstance(L1Npc template, L1Character master, int itemObjId) {
        super(template);
        try {
            setId(IdFactoryNpc.get().nextId());
            set_showId(master.get_showId());
            setItemObjId(itemObjId);
            setMaster(master);
            setX(master.getX() + _random.nextInt(5) - 2);
            setY(master.getY() + _random.nextInt(5) - 2);
            setMap(master.getMapId());
            setHeading(5);
            setLightSize(template.getLightSize());
            World.get().storeObject(this);
            World.get().addVisibleObject(this);
            startAI();
            for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
                onPerceive(pc);
            }
            master.addHierarch(this);
            broadcastPacketX8(new S_SkillSound(getId(), 5935));
            L1Hierarch hierarch = NpcHierarchTable.get().getHierarch(getNpcId());
            if (hierarch != null) {
                this._skill_id = hierarch.getSkillId();
                this._skill_mp = hierarch.getSkillMp();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean noTarget() {
        if ((this._master == null) || (this._master.isInvisble()) || (this._master.isDead())) {
            deleteHierarch();
            return true;
        }
        if (this._master.getMapId() != getMapId()) {
            return true;
        }
        if ((getHierarch() == 1) && ((this._master instanceof L1PcInstance))) {
            L1PcInstance pc = (L1PcInstance) this._master;
            if ((this._skill_id != null) && (this._skill_mp != null)) {
                for (int i = 0; i < this._skill_id.length; i++) {
                    int _skillid = this._skill_id[i];
                    int _skillmp = this._skill_mp[i];
                    if ((!pc.hasSkillEffect(_skillid)) && (getCurrentMp() > _skillmp)) {
                        setCurrentMp(getCurrentMp() - _skillmp);
                        L1Skills _skill = SkillsTable.get().getTemplate(_skillid);
                        L1SkillUse skillUse = new L1SkillUse();
                        skillUse.handleCommands(pc, _skillid, pc.getId(), pc.getX(), pc.getX(), _skill.getBuffDuration(), 4);
                        setSleepTime(calcSleepTime(getAtkspeed(), 1));
                        break;
                        // }
                        //  }
                    }
                }
            }
            if ((getCurrentMp() > 15) && (pc.getCurrentHp() != pc.getMaxHp()) && (pc.getCurrentHp() < pc.getMaxHp() * pc.getHierarch() / 10)) {
                int chance = _random.nextInt(75) + 1;
                pc.sendPacketsX8(new S_SkillSound(pc.getId(), 830));
                pc.setCurrentHp(pc.getCurrentHp() + (getLawful() + chance));
                setCurrentMp(getCurrentMp() - 15);
                setSleepTime(calcSleepTime(getAtkspeed(), 1));
            }
        }
        if ((getLocation().getTileLineDistance(this._master.getLocation()) > 2) && (this._npcMove != null)) {
            this._npcMove.setDirectionMove(this._npcMove.moveDirection(this._master.getX(), this._master.getY()));
            setSleepTime(calcSleepTime(getPassispeed(), 0));
        }
        return false;
    }

    public void deleteHierarch() {
        try {
            broadcastPacketX8(new S_SkillSound(getId(), 5936));
            this._master.removeHierarch();
            deleteMe();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onAction(L1PcInstance player) {
        if (isDead()) {
            return;
        }
        if (this._master.equals(player)) {
            String msg0 = "跟隨";
            if (getHierarch() == 1) {
                msg0 = "輔助";
            }
            String msg1 = String.valueOf(player.getHierarch() * 10);
            String[] htmldata = {getName(), String.valueOf(getCurrentMp()), String.valueOf(getMaxMp()), msg0, msg1};
            player.sendPackets(new S_NPCTalkReturn(getId(), "Hierarch", htmldata));
        }
    }

    public void onTalkAction(L1PcInstance pc) {
        if (isDead()) {
            return;
        }
        if (this._master.equals(pc)) {
            String msg0 = "跟隨";
            if (getHierarch() == 1) {
                msg0 = "輔助";
            }
            String msg1 = String.valueOf(pc.getHierarch() * 10);
            String[] htmldata = {getName(), String.valueOf(getCurrentMp()), String.valueOf(getMaxMp()), msg0, msg1};
            pc.sendPackets(new S_NPCTalkReturn(getId(), "Hierarch", htmldata));
        }
    }

    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_NPCPack_Hierarch(this));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void setCurrentMp(int i) {
        int currentMp = i;
        if (currentMp >= getMaxMp()) {
            currentMp = getMaxMp();
        }
        setCurrentMpDirect(currentMp);
        broadcastPacketX8(new S_SkillSound(getId(), 6321));
    }

    public void setNpcMoveSpeed() {
        try {
            if ((this._master != null) && (this._master.isInvisble())) {
                deleteHierarch();
                return;
            }
            if (this._master.isDead()) {
                deleteHierarch();
                return;
            }
            if (this._master.getMoveSpeed() != this._srcMoveSpeed) {
                set_srcMoveSpeed(this._master.getMoveSpeed());
                setMoveSpeed(this._srcMoveSpeed);
            }
            if (this._master.getBraveSpeed() != this._srcBraveSpeed) {
                set_srcBraveSpeed(this._master.getBraveSpeed());
                setBraveSpeed(this._srcBraveSpeed);
            }
            if ((this._master != null) && (this._master.getMapId() == getMapId())) {
                if (getLocation().getTileLineDistance(this._master.getLocation()) > 2) {
                    int dir = targetDirection(this._master.getX(), this._master.getY());
                    setDirectionMoveSrc(dir);
                    broadcastPacketAll(new S_MoveCharPacket(this));
                }
            } else {
                deleteHierarch();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void set_srcMoveSpeed(int srcMoveSpeed) {
        try {
            this._srcMoveSpeed = srcMoveSpeed;
            broadcastPacketAll(new S_SkillHaste(getId(), this._srcMoveSpeed, 0));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void set_srcBraveSpeed(int srcBraveSpeed) {
        try {
            this._srcBraveSpeed = srcBraveSpeed;
            broadcastPacketAll(new S_SkillBrave(getId(), this._srcBraveSpeed, 0));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int getItemObjId() {
        return this._itemObjId;
    }

    public void setItemObjId(int i) {
        this._itemObjId = i;
    }

    public int get_time() {
        return this._time;
    }

    public void set_time(int time) {
        this._time = time;
    }
}
