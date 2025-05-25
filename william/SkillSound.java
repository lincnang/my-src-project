package william;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkillSound extends TimerTask {
    private static Logger _log = Logger.getLogger(SkillSound.class.getName());
    private final L1PcInstance _pc;

    public SkillSound(L1PcInstance pc) {
        _pc = pc;
    }

    /**
     *
     */
    public void run() {
        try {
            if (_pc.isDead()) {
                return;
            }
            if (_pc.getNETimeGfx() != 0) {
                _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), _pc.getNETimeGfx()));
                _pc.broadcastPacketX8(new S_SkillSound(_pc.getId(), _pc.getNETimeGfx()));
            }
            if (_pc.getNETimeGfx2() != 0) {
                _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), _pc.getNETimeGfx2()));
                _pc.broadcastPacketX8(new S_SkillSound(_pc.getId(), _pc.getNETimeGfx2()));
            }
        } catch (Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}