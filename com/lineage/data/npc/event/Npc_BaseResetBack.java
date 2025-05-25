package com.lineage.data.npc.event;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_GmMessage;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 72000 補點數專用
 *
 * @author loli
 */
public class Npc_BaseResetBack extends NpcExecutor {
    // 各職業初始化屬性(王族, 騎士, 精靈, 法師, 黑妖, 龍騎士, 幻術師, 戰士)
    public static final int[] ORIGINAL_STR = new int[]{13, 16, 11, 8, 12, 13, 11, 16};
    public static final int[] ORIGINAL_DEX = new int[]{10, 12, 12, 7, 15, 11, 10, 13};
    public static final int[] ORIGINAL_CON = new int[]{10, 14, 12, 12, 8, 14, 12, 16};
    public static final int[] ORIGINAL_WIS = new int[]{11, 9, 12, 12, 10, 12, 12, 7};
    public static final int[] ORIGINAL_CHA = new int[]{13, 12, 9, 8, 9, 8, 8, 9};
    public static final int[] ORIGINAL_INT = new int[]{10, 8, 12, 12, 11, 11, 12, 10};
    private static final Log _log = LogFactory.getLog(Npc_BaseResetBack.class);

    private Npc_BaseResetBack() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_BaseResetBack();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        try {
            //final String otherStatus = String .valueOf(pc.getOtherStats());
            final String addPoint = String.valueOf(pc.getAddPoint());
            final String delPoint = String.valueOf(pc.getDelPoint());
            final String basestr = String.valueOf(pc.getBaseStr());
            final String basedex = String.valueOf(pc.getBaseDex());
            final String baseint = String.valueOf(pc.getBaseInt());
            final String basecha = String.valueOf(pc.getBaseCha());
            final String[] info = new String[]{addPoint, delPoint, basestr, basedex, baseint, basecha};
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "baseBack", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        try {
            boolean change = false;
            _log.info(pc.getName() + "補點數開始");
            _log.info(pc.getName() + "執行" + cmd.toString());
            //---------------------扣除---------------------
            // 扣str
            if (cmd.equalsIgnoreCase("del1")) {
                if (checkStatus(pc, "baseStr-")) {
                    int value = pc.getBaseStr() - 1;
                    pc.addBaseStr((byte) (value - pc.getBaseStr()));
                    pc.setDelPoint(pc.getDelPoint() + 1);
                    change = true;
                }
                // 扣dex
            } else if (cmd.equalsIgnoreCase("del2")) {
                if (checkStatus(pc, "baseDex-")) {
                    int value = pc.getBaseDex() - 1;
                    pc.addBaseDex((byte) (value - pc.getBaseDex()));
                    pc.setDelPoint(pc.getDelPoint() + 1);
                    change = true;
                }
                // 扣int
            } else if (cmd.equalsIgnoreCase("del3")) {
                if (checkStatus(pc, "baseInt-")) {
                    int value = pc.getBaseInt() - 1;
                    pc.addBaseInt((byte) (value - pc.getBaseInt()));
                    pc.setDelPoint(pc.getDelPoint() + 1);
                    change = true;
                }
                // 扣cha
            } else if (cmd.equalsIgnoreCase("del4")) {
                if (checkStatus(pc, "baseCha-")) {
                    int value = pc.getBaseCha() - 1;
                    pc.addBaseCha((byte) (value - pc.getBaseCha()));
                    pc.setDelPoint(pc.getDelPoint() + 1);
                    change = true;
                }
            }
            //---------------------新增---------------------
            else
                // 補str
                if (cmd.equalsIgnoreCase("add1")) {
                    if (checkStatus(pc, "baseStr+")) {
                        int value = pc.getBaseStr() + 1;
                        pc.addBaseStr((byte) (value - pc.getBaseStr()));
                        pc.setDelPoint(pc.getDelPoint() - 1);
                        pc.setAddPoint(pc.getAddPoint() + 1);
                        change = true;
                    }
                    // 補dex
                } else if (cmd.equalsIgnoreCase("add2")) {
                    if (checkStatus(pc, "baseDex+")) {
                        int value = pc.getBaseDex() + 1;
                        pc.addBaseDex((byte) (value - pc.getBaseDex()));
                        pc.setDelPoint(pc.getDelPoint() - 1);
                        pc.setAddPoint(pc.getAddPoint() + 1);
                        change = true;
                    }
                    // 補int
                } else if (cmd.equalsIgnoreCase("add3")) {
                    if (checkStatus(pc, "baseInt+")) {
                        int value = pc.getBaseInt() + 1;
                        pc.addBaseInt((byte) (value - pc.getBaseInt()));
                        pc.setDelPoint(pc.getDelPoint() - 1);
                        pc.setAddPoint(pc.getAddPoint() + 1);
                        change = true;
                    }
                    // 補cha
                } else if (cmd.equalsIgnoreCase("add4")) {
                    if (checkStatus(pc, "baseCha+")) {
                        int value = pc.getBaseCha() + 1;
                        pc.addBaseCha((byte) (value - pc.getBaseCha()));
                        pc.setDelPoint(pc.getDelPoint() - 1);
                        pc.setAddPoint(pc.getAddPoint() + 1);
                        change = true;
                    }
                }
                // 補CON
                else if (cmd.equalsIgnoreCase("add5")) {
                    if (checkStatus(pc, "baseCon+")) {
                        int value = pc.getBaseCon() + 1;
                        pc.addBaseCon((byte) (value - pc.getBaseCon()));
                        pc.setDelPoint(pc.getDelPoint() - 1);
                        pc.setAddPoint(pc.getAddPoint() + 1);
                        change = true;
                    }
                }
                // 補WIS
                else if (cmd.equalsIgnoreCase("add6")) {
                    if (checkStatus(pc, "baseWis+")) {
                        int value = pc.getBaseWis() + 1;
                        pc.addBaseWis((byte) (value - pc.getBaseWis()));
                        pc.setDelPoint(pc.getDelPoint() - 1);
                        pc.setAddPoint(pc.getAddPoint() + 1);
                        change = true;
                    }
                }
            pc.sendPackets(new S_CloseList(pc.getId()));
            if (change) {
                pc.save(); // 資料存檔
                pc.sendPackets(new S_OwnCharStatus(pc));// 更新顯示
            }
            _log.info(pc.getName() + "補點數完成");
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean checkStatus(final L1PcInstance pc, String type) {
        int baseStr = ORIGINAL_STR[pc.getType()];
        int baseDex = ORIGINAL_DEX[pc.getType()];
        int baseInt = ORIGINAL_INT[pc.getType()];
        int baseCha = ORIGINAL_CHA[pc.getType()];
        int otherStatus = pc.getOtherStats();
        //        int addpoint=pc.getAddPoint();
        int delpoint = pc.getDelPoint();
        if (otherStatus == 0) {
            error(pc, "請先使用回憶蠟燭");
            return false;
        }
        if (type.contains("+") && delpoint <= 0) {
            error(pc, "請先執行扣除點數");
            return false;
        }
        //        if(type.contains("-") && delpoint+addpoint>=otherStatus){
        //        	error(pc,"已經扣完點數");
        //        	return false;
        //        }
        if (type.equals("baseStr-")) {
            if (pc.getBaseStr() - 1 < baseStr) {
                msg1(pc);
                return false;
            }
        } else if (type.equals("baseDex-")) {
            if (pc.getBaseDex() - 1 < baseDex) {
                msg1(pc);
                return false;
            }
        } else if (type.equals("baseInt-")) {
            if (pc.getBaseInt() - 1 < baseInt) {
                msg1(pc);
                return false;
            }
        } else if (type.equals("baseCha-")) {
            if (pc.getBaseCha() - 1 < baseCha) {
                msg1(pc);
                return false;
            }
        } else if (type.equals("baseStr+")) {
            if (pc.getBaseStr() + 1 > 45) {
                msg2(pc);
                return false;
            }
        } else if (type.equals("baseDex+")) {
            if (pc.getBaseDex() + 1 > 45) {
                msg2(pc);
                return false;
            }
        } else if (type.equals("baseInt+")) {
            if (pc.getBaseInt() + 1 > 45) {
                msg2(pc);
                return false;
            }
        } else if (type.equals("baseCha+")) {
            if (pc.getBaseCha() + 1 > 45) {
                msg2(pc);
                return false;
            }
        } else if (type.equals("baseCon+")) {
            if (pc.getBaseCon() + 1 > 45) {
                msg2(pc);
                return false;
            }
        } else if (type.equals("baseWis+")) {
            if (pc.getBaseWis() + 1 > 45) {
                msg2(pc);
                return false;
            }
        }
        ok(pc, "變更成功");
        return true;
    }

    public void msg1(final L1PcInstance pc) {
        pc.sendPackets(new S_GmMessage("不能扣到比出生素質低唷。"));
        pc.sendPackets(new S_CloseList(pc.getId()));
    }

    public void msg2(final L1PcInstance pc) {
        pc.sendPackets(new S_GmMessage("不能超過45唷。"));
        pc.sendPackets(new S_CloseList(pc.getId()));
    }

    public void error(final L1PcInstance pc, String msg) {
        pc.sendPackets(new S_GmMessage(msg));
    }

    public void ok(final L1PcInstance pc, String msg) {
        pc.sendPackets(new S_GmMessage(msg));
    }
}
