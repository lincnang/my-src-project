package com.lineage.server.model.Instance;

import com.lineage.server.model.L1Object;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;

/**
 * 中央寺廟NPC
 *
 * @author sudawei
 */
public class L1DotaInstance extends L1MonsterInstance {
    private static final long serialVersionUID = 1L;

    public L1DotaInstance(L1Npc template) {
        super(template);
        set_storeDroped(false);
        //setAtkspeed(SprTable.get().getSprSpeed(getTempCharGfx(), ActionCodes.ACTION_SkillAttack));
        //setPassispeed(SprTable.get().getSprSpeed(getTempCharGfx(), ActionCodes.ACTION_Walk));
        //System.out.println("npcid:"+getNpcId()+",atkspeed:"+getAtkspeed()+",passispeed:"+getPassispeed());
        //int sleepTime=SprTable.get().getSprSpeed(getTempCharGfx(), ActionCodes.ACTION_SkillAttack);
        //npcSleepTime(sleepTime,ActionCodes.ACTION_Damage);
        //setActivated(true);
        onNpcAI();
    }

    @Override
    public void searchTarget() {
        //for (L1Object object : World.get().getVisibleObjects(this,1000)) {
        for (L1Object object : World.get().getVisibleObjects(this, -1)) {
            //for (L1Object object : World.get().getVisibleObjects(1936).values()) {
            if (object instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) object;
                if (npc.getNpcId() == 190114) {
                    if (getCurrentHp() > 2000) {
                        _hateList.add(npc, 0);
                    } else {
                        _hateList.add(npc, getCurrentHp());
                    }
                    _target = npc;
                    return;
                }
            }
        }
    }
    //	@Override
    //	public void onTarget() {
    //		setActivated(true);
    //		_targetItemList.clear();
    //		_targetItem = null;
    //		final L1Character target = _target;
    //		if (isAttackPosition(target.getX(), target.getY(), get_ranged())) {// 已經到達可以攻擊的距離
    //			if (_mobSkill.skillUse(target, true)) { // スキル使用(mobskill.sqlのTriRndに從う)
    //				setSleepTime(calcSleepTime(_mobSkill.getSleepTime(), MAGIC_SPEED));
    //
    //			} else { // スキル使用が失敗したら物理攻擊
    //				setHeading(targetDirection(target.getX(), target.getY()));
    //				attackTarget(target);
    //			}
    //		}else{
    //			if (glanceCheck(_target.getX(), _target.getY())) {
    //			if (_mobSkill.skillUse(target, false)) {
    //				// スキル使用(mobskill.sqlのTriRndに從わず、■動確率は100%。ただしサモン、強制變身は常にTriRndに從う。)
    //				setSleepTime(calcSleepTime(_mobSkill.getSleepTime(), MAGIC_SPEED));
    //				return;
    //			}
    //		}
    //			if(_npcMove==null){
    //				_npcMove=new NpcMove(this);
    //			}
    //			int dir=_npcMove.moveDirection(target.getX(),target.getY());
    //			if(dir==-1){
    //				//targetDirection(target.getX(),target.getY());
    //				//setSleepTime(3000);
    //				//this.deleteMe();
    //				if(this.getMapId()>=1936&&this.getMapId()<=1986){
    //					this.removeAllKnownObjects();
    //				}else{
    //					tagertClear();
    //				}
    //			}else{
    //				//npcSleepTime(sleepTime,ActionCodes.ACTION_Walk);
    //				_npcMove.setDirectionMove(dir);
    //			}
    //		}
    //		try {
    //			TimeUnit.MILLISECONDS.sleep(300);
    //		} catch (InterruptedException e) {
    //			// TODO 自動生成的 catch 塊
    //			//e.printStackTrace();
    //		}
    //	}
    //	private int moveDirection(int x,int y){
    //		double distance=getLocation().getLineDistance(x,y);
    //		if(hasSkillEffect(40)&&distance>=2D){
    //			return -1;
    //		}
    //		if(distance>30D){
    //			return -1;
    //		}
    //		int dir;
    //		if(distance>L1NpcInstance.DISTANCE){
    //			_npcMove._targetDirection();
    //		}
    //
    //	}
}
