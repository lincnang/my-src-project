package william;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_AllChannelsChat;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.world.World;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 即時獎勵系統
 */
public class L1WilliamLimitedReward {
    private static final int _int8 = 128;
    private static final int _int7 = 64;
    private static final int _int6 = 32;
    private static final int _int5 = 16;
    private static final int _int4 = 8;
    private static final int _int3 = 4;
    private static final int _int2 = 2;
    private static final int _int1 = 1;
    private static Logger _log = Logger.getLogger(L1WilliamLimitedReward.class.getName());
    private final int _id;
    private final int _check_classId;
    private final int _check_level;
    private final int _check_item;
    private final int _check_itemCount;
    private final String _surplus_msg;
    private final int _surplus_msg_color;
    private final String _itemId;
    private final String _count;
    private final String _enchantlvl;
    private final int _totalCount;
    private final int _quest_id;
    private final int _quest_step;
    private final String _message;
    private final String _message_end;
    private boolean _activity;
    private int _appearCount;
    private boolean _isCrown;
    private boolean _isKnight;
    private boolean _isElf;
    private boolean _isWizard;
    private boolean _isDarkelf;
    private boolean _isDragonKnight;
    private boolean _isIllusionist;
    private boolean _isWarrior;
    private int _use_type = 255;

    public L1WilliamLimitedReward(final int id, final boolean activity, final int check_classId, final int check_level, final int check_item, final int check_itemCount, final String surplus_msg, final int surplus_msg_color, final String itemId, final String count, final String enchantlvl, final int totalCount, final int appearCount, final int quest_id, final int quest_step, final String message, final String message_end) {
        _id = id;
        _activity = activity;
        _check_classId = check_classId;
        _check_level = check_level;
        _check_item = check_item;
        _check_itemCount = check_itemCount;
        _surplus_msg = surplus_msg;
        _surplus_msg_color = surplus_msg_color;
        _itemId = itemId;
        _count = count;
        _enchantlvl = enchantlvl;
        _totalCount = totalCount;
        _appearCount = appearCount;
        _quest_id = quest_id;
        _quest_step = quest_step;
        _message = message;
        _message_end = message_end;
    }

    public static void check_Task_For_Item(final L1PcInstance pc, final int itemId, final int count) {
        final L1WilliamLimitedReward limitedReward = LimitedReward.getInstance().getTemplate1(itemId);
        if (limitedReward == null) {
            return;
        }
        if (!limitedReward.isActivity()) {
            return;
        }
        if (limitedReward.getTotalCount() <= limitedReward.get_appearCount()) {
            return;
        }
        if (limitedReward.get_quest_id() != 0 && pc.getQuest().get_step(limitedReward.get_quest_id()) == limitedReward.get_quest_step()) {
            return;
        }
        limitedReward.set_use_type(limitedReward.get_check_classId());
        if (!limitedReward.is_use(pc)) {
            return;
        }
        int codes = 0;
        if (limitedReward.get_surplus_msg_color() != -1) {
            codes = limitedReward.get_surplus_msg_color();
        } else {
            final int[] _codes = {14, 7, 24, 13, 47, 4, 3, 53, 10, 1, 2, 11};
            codes = _codes[RandomArrayList.getInt(_codes.length)];
        }
        if (pc.getInventory().consumeItem(limitedReward.get_check_item(), limitedReward.get_check_itemCount())) {
            if ((limitedReward.get_itemId() != null) && (!limitedReward.get_itemId().equals(""))) {
                final int[] itemGive = (int[]) getArray(limitedReward.get_itemId(), ",", 1);
                final int[] itemCount = (int[]) getArray(limitedReward.get_count(), ",", 1);
                final int[] itemEnchantlvl = (int[]) getArray(limitedReward.get_enchantlvl(), ",", 1);
                for (int j = 0; j < itemGive.length; j++) {
                    CreateNewItem.createNewItem_LV(pc, itemGive[j], itemCount[j], itemEnchantlvl[j]);
                }
            }
            if (limitedReward.get_quest_id() != 0) {
                pc.getQuest().set_step(limitedReward.get_quest_id(), limitedReward.get_quest_step());
            }
            if (limitedReward.get_message() != null && !limitedReward.get_message().equals("")) {
                final int qq = limitedReward.getTotalCount() - limitedReward.get_appearCount() - 1;
                if (qq <= 0) {
                    World.get().broadcastPacketToAll(new S_AllChannelsChat(limitedReward.get_message_end(), codes));
                } else {
                    //World.get().broadcastPacketToAll(new S_AllChannelsChat(String.format(limitedReward.get_message(), new Object[] { Integer.valueOf(qq) }), codes));
                    World.get().broadcastPacketToAll(new S_AllChannelsChat(String.format(limitedReward.get_message(), qq), codes));
                }
            }
            limitedReward.set_appearCount(limitedReward.get_appearCount() + 1);
            LimitedReward.limitedRewardToList(limitedReward.get_id(), limitedReward.get_appearCount());
        } else if (limitedReward.get_surplus_msg() != null && !limitedReward.get_surplus_msg().equals("")) {
            final int qq = (int) (limitedReward.get_check_itemCount() - pc.getInventory().countItems(limitedReward.get_check_item()));
            //pc.sendPackets(new S_AllChannelsChat(String.format(limitedReward.get_surplus_msg(), new Object[] { Integer.valueOf(qq) }), codes));
            pc.sendPackets(new S_AllChannelsChat(String.format(limitedReward.get_surplus_msg(), qq), codes));
        }
    }

    public static void check_Task_For_Level(final L1PcInstance pc) {
        L1WilliamLimitedReward limitedReward = null;
        final L1WilliamLimitedReward[] limitedRewardSize = LimitedReward.getInstance().getLimitedRewardList();
        for (int i = 0; i < limitedRewardSize.length; i++) {
            limitedReward = LimitedReward.getInstance().getTemplate(i);
            if (limitedReward.isActivity()) {
                if (limitedReward.getTotalCount() > limitedReward.get_appearCount()) {
                    if (limitedReward.get_quest_id() == 0 || pc.getQuest().get_step(limitedReward.get_quest_id()) != limitedReward.get_quest_step()) {
                        if (limitedReward.get_check_level() <= pc.getLevel()) {
                            limitedReward.set_use_type(limitedReward.get_check_classId());
                            if (limitedReward.is_use(pc)) {
                                int codes = 0;
                                if (limitedReward.get_surplus_msg_color() != -1) {
                                    codes = limitedReward.get_surplus_msg_color();
                                } else {
                                    final int[] _codes = {14, 7, 24, 13, 47, 4, 3, 53, 10, 1, 2, 11};
                                    codes = _codes[RandomArrayList.getInt(_codes.length)];
                                }
                                if (limitedReward.get_itemId() != null && !limitedReward.get_itemId().equals("")) {
                                    final int[] itemGive = (int[]) getArray(limitedReward.get_itemId(), ",", 1);
                                    final int[] itemCount = (int[]) getArray(limitedReward.get_count(), ",", 1);
                                    final int[] itemEnchantlvl = (int[]) getArray(limitedReward.get_enchantlvl(), ",", 1);
                                    for (int j = 0; j < itemGive.length; j++) {
                                        CreateNewItem.createNewItem_LV(pc, itemGive[j], itemCount[j], itemEnchantlvl[j]);
                                    }
                                }
                                if (limitedReward.get_quest_id() != 0) {
                                    pc.getQuest().set_step(limitedReward.get_quest_id(), limitedReward.get_quest_step());
                                }
                                if (limitedReward.get_message() != null && !limitedReward.get_message().equals("")) {
                                    final int qq = limitedReward.getTotalCount() - limitedReward.get_appearCount() - 1;
                                    if (qq <= 0) {
                                        limitedReward.set_Activity(false);
                                        LimitedReward.limitedRewardToList_isOver(i, 0);
                                        World.get().broadcastPacketToAll(new S_AllChannelsChat(limitedReward.get_message_end(), codes));
                                    } else {
                                        limitedReward.set_appearCount(limitedReward.get_appearCount() + 1);
                                        LimitedReward.limitedRewardToList(i, limitedReward.get_appearCount());
                                        //World.get().broadcastPacketToAll(new S_AllChannelsChat(String.format(limitedReward.get_message(), new Object[] { Integer.valueOf(qq) }), codes));
                                        World.get().broadcastPacketToAll(new S_AllChannelsChat(String.format(limitedReward.get_message(), qq), codes));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static Object getArray(final String s, final String sToken, final int iType) {
        final StringTokenizer st = new StringTokenizer(s, sToken);
        final int iSize = st.countTokens();
        String sTemp = null;
        if (iType == 1) {
            final int[] iReturn = new int[iSize];
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                iReturn[i] = Integer.parseInt(sTemp);
            }
            return iReturn;
        }
        if (iType == 2) {
            final String[] sReturn = new String[iSize];
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                sReturn[i] = sTemp;
            }
            return sReturn;
        }
        if (iType == 3) {
            String sReturn = null;
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                sReturn = sTemp;
            }
            return sReturn;
        }
        if (iType == 4) {
            final short[] iReturn = new short[iSize];
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                iReturn[i] = Short.parseShort(sTemp);
            }
            return iReturn;
        }
        return null;
    }

    public int get_id() {
        return _id;
    }

    public boolean isActivity() {
        return _activity;
    }

    public void set_Activity(final boolean i) {
        _activity = i;
    }

    public int get_check_classId() {
        return _check_classId;
    }

    public int get_check_level() {
        return _check_level;
    }

    public int get_check_item() {
        return _check_item;
    }

    public int get_check_itemCount() {
        return _check_itemCount;
    }

    public String get_surplus_msg() {
        return _surplus_msg;
    }

    public int get_surplus_msg_color() {
        return _surplus_msg_color;
    }

    public String get_itemId() {
        return _itemId;
    }

    public String get_count() {
        return _count;
    }

    public String get_enchantlvl() {
        return _enchantlvl;
    }

    public int getTotalCount() {
        return _totalCount;
    }

    public int get_appearCount() {
        return _appearCount;
    }

    public void set_appearCount(final int i) {
        _appearCount = i;
    }

    public int get_quest_id() {
        return _quest_id;
    }

    public int get_quest_step() {
        return _quest_step;
    }

    public String get_message() {
        return _message;
    }

    public String get_message_end() {
        return _message_end;
    }

    public int get_use_type() {
        return _use_type;
    }

    public void set_use_type(int use_type) {
        _use_type = use_type;
        if (use_type >= _int8) {
            use_type -= _int8;
            _isWarrior = true;
        }
        if (use_type >= _int7) {
            use_type -= _int7;
            _isIllusionist = true;
        }
        if (use_type >= _int6) {
            use_type -= _int6;
            _isDragonKnight = true;
        }
        if (use_type >= _int5) {
            use_type -= _int5;
            _isDarkelf = true;
        }
        if (use_type >= _int4) {
            use_type -= _int4;
            _isWizard = true;
        }
        if (use_type >= _int3) {
            use_type -= _int3;
            _isElf = true;
        }
        if (use_type >= _int2) {
            use_type -= _int2;
            _isKnight = true;
        }
        if (use_type >= _int1) {
            use_type--;
            _isCrown = true;
        }
    }

    public boolean is_use(final L1PcInstance pc) {
        try {
            if (pc.isCrown() && _isCrown) {
                return true;
            }
            if (pc.isKnight() && _isKnight) {
                return true;
            }
            if (pc.isElf() && _isElf) {
                return true;
            }
            if (pc.isWizard() && _isWizard) {
                return true;
            }
            if (pc.isDarkelf() && _isDarkelf) {
                return true;
            }
            if (pc.isDragonKnight() && _isDragonKnight) {
                return true;
            }
            if (pc.isIllusionist() && _isIllusionist) {
                return true;
            }
            if (pc.isWarrior() && _isWarrior) {
                return true;
            }
        } catch (final Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return false;
    }
}
