package com.lineage.server.model.Instance;

import com.lineage.server.serverpackets.S_NPCPack_Skin;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1SkinInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1SkinInstance.class);
    private int _srcMoveSpeed;
    private int _srcBraveSpeed;
    private int _move_type = 0;

    public L1SkinInstance(L1Npc template) {
        super(template);
    }

    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_NPCPack_Skin(this));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onAction(L1PcInstance actionFrom) {
        super.onAction(actionFrom);
    }

    public void deleteMe() {
        try {
            this._destroyed = true;
            if (getInventory() != null) {
                getInventory().clearItems();
            }
            allTargetClear();
            this._master = null;
            World.get().removeVisibleObject(this);
            World.get().removeObject(this);
            for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
                pc.removeKnownObject(this);
                pc.sendPackets(new S_RemoveObject(this));
            }
            removeAllKnownObjects();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void setNpcMoveSpeed() {
        if (this._master == null) {
            deleteMe();
            return;
        }
        try {
            if (this._master.isInvisble()) {
                deleteMe();
                return;
            }
            if (this._master.isDead()) {
                deleteMe();
                return;
            }
            //if (this._master.getMoveSpeed() != this._srcMoveSpeed) {
            set_srcMoveSpeed(this._master.getMoveSpeed());
            setMoveSpeed(this._srcMoveSpeed);
            //}
            //if (this._master.getBraveSpeed() != this._srcBraveSpeed) {
            set_srcBraveSpeed(this._master.getBraveSpeed());
            setBraveSpeed(this._srcBraveSpeed);
            //}
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

    public int getMoveType() {
        return this._move_type;
    }

    public void setMoveType(int i) {
        this._move_type = i;
    }
}