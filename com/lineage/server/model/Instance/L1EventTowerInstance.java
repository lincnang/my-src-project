package com.lineage.server.model.Instance;

import com.lineage.data.event.MiniGame.MiniSiege;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;

/**
 * 底比斯大戰遊戲
 */
public class L1EventTowerInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private int _crackStatus;

    public L1EventTowerInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance player) {
        if (getCurrentHp() > 0 && !isDead()) {
            final L1AttackMode attack = new L1AttackPc(player, this);
            if (attack.calcHit()) {
                attack.calcDamage();
                //attack.addPcPoisonAttack(player, this);
            }
            attack.action();
            attack.commit();
        }
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        if (MiniSiege.getInstance().getStage() == 0) {
            ((L1PcInstance) attacker).sendPackets(new S_SystemMessage("還在召集參賽者，請稍後。"));
            return;
        }
        int towerid = getNpcTemplate().get_npcId();
        if (attacker instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) attacker;
            for (int i = 0; i < 9; i++) {
                System.out.print(i + "第タワー：" + MiniSiege.getInstance().isDestory(i) + "\t");
                if (i == 8) {
                    System.out.println("");
                }
            }
            switch (towerid) {
                case 4205:
                    if (!MiniSiege.getInstance().isDestory(0)) { //Aチーム守護塔
                        pc.sendPackets(new S_SystemMessage("請先把前一個塔摧毀。"));
                        return;
                    }
                    break;
                case 4206:
                    if (!MiniSiege.getInstance().isDestory(1)) {
                        pc.sendPackets(new S_SystemMessage("請先把前一個塔摧毀。"));
                        return;
                    }
                    break;
                case 4207:
                    if (!MiniSiege.getInstance().isDestory(2)) {
                        pc.sendPackets(new S_SystemMessage("請先把前一個塔摧毀。"));
                        return;
                    }
                    break;
                case 4209:
                    if (!MiniSiege.getInstance().isDestory(3)) {//Aチーム中トップ
                        pc.sendPackets(new S_SystemMessage("請先把前一個塔摧毀。"));
                        return;
                    }
                    break;
                case 4210:
                    if (!MiniSiege.getInstance().isDestory(4)) {
                        pc.sendPackets(new S_SystemMessage("請先把前一個塔摧毀。"));
                        return;
                    }
                    break;
                case 4211:
                    if (!MiniSiege.getInstance().isDestory(5)) { //Cチーム中トップ
                        pc.sendPackets(new S_SystemMessage("請先把前一個塔摧毀。"));
                        return;
                    }
                    break;
            }
            int newhp = getCurrentHp() - damage;
            if (newhp < 0) {
                _crackStatus = 0;
                setStatus(ActionCodes.ACTION_TowerDie);
                setCurrentHp(0);
                getMap().setPassable(getLocation(), true);
                System.out.println("TOWERID : " + towerid);
                switch (towerid) {
                    case 4201:
                        MiniSiege.getInstance().setDestroy(0);
                        MiniSiege.getInstance().GiveReward(1, pc.getTeam());
                        break;
                    case 4202:
                        MiniSiege.getInstance().setDestroy(1);
                        MiniSiege.getInstance().GiveReward(1, pc.getTeam());
                        break;
                    case 4203:
                        MiniSiege.getInstance().setDestroy(2);
                        System.out.println("TEam : " + pc.getTeam());
                        MiniSiege.getInstance().GiveReward(1, pc.getTeam());
                        System.out.println("獎勵支付完畢。");
                        break;
                    case 4205:
                        MiniSiege.getInstance().setDestroy(3);
                        MiniSiege.getInstance().GiveReward(2, pc.getTeam());
                        break;
                    case 4206:
                        MiniSiege.getInstance().setDestroy(4);
                        MiniSiege.getInstance().GiveReward(2, pc.getTeam());
                        break;
                    case 4207:
                        MiniSiege.getInstance().setDestroy(5);
                        MiniSiege.getInstance().GiveReward(2, pc.getTeam());
                        break;
                    case 4209:
                        MiniSiege.getInstance().setDestroy(6);
                        break;
                    case 4210:
                        MiniSiege.getInstance().setDestroy(7);
                        break;
                    case 4211:
                        MiniSiege.getInstance().setDestroy(8);
                        break;
                }
                deleteMe();
            } else {
                setCurrentHp(newhp);
                if ((getMaxHp() * 1 / 4) > getCurrentHp()) {
                    if (_crackStatus != 3) {
                        broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_TowerCrack3));
                        setStatus(ActionCodes.ACTION_TowerCrack3);
                        _crackStatus = 3;
                    }
                } else if ((getMaxHp() * 2 / 4) > getCurrentHp()) {
                    if (_crackStatus != 2) {
                        broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_TowerCrack2));
                        setStatus(ActionCodes.ACTION_TowerCrack2);
                        _crackStatus = 2;
                    }
                } else if ((getMaxHp() * 3 / 4) > getCurrentHp()) {
                    if (_crackStatus != 1) {
                        broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_TowerCrack1));
                        setStatus(ActionCodes.ACTION_TowerCrack1);
                        _crackStatus = 1;
                    }
                }
            }
        }
    }
}
