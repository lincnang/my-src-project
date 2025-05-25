package william;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SustainEffect extends TimerTask {
    private static Logger _log = Logger.getLogger(SustainEffect.class.getName());
    private final L1PcInstance Active_pc;
    private final int effect_id;

    public SustainEffect(L1PcInstance pc, int effect) {
        Active_pc = pc;
        effect_id = effect;
    }

    @Override
    public void run() {
        try {
            if (Active_pc.isDead()) {
                return;
            }
            Active_pc.sendPacketsArmorYN(new S_SkillSound(Active_pc.getId(), effect_id));
            // Active_pc.sendPackets(new S_SkillSound(Active_pc.getId(),
            // effect_id));
        } catch (Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}
