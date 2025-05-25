package com.add.MJBookQuestSystem;

import com.add.MJBookQuestSystem.Compensator.NormalQuestCompensator;
import com.add.MJBookQuestSystem.Loader.MonsterBookCompensateLoader;
import com.add.MJBookQuestSystem.Loader.MonsterBookLoader;
import com.add.MJBookQuestSystem.Templates.UserMonsterBookProgress;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Item;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 怪物圖鑑 使用
 */
public class UserMonsterBook {
    private HashMap<Integer, UserMonsterBookProgress> _mb;
    private L1PcInstance _owner;
    private Object _lock;

    public UserMonsterBook(L1PcInstance pc) {
        _owner = pc;
        _mb = new HashMap<Integer, UserMonsterBookProgress>(10);
        _lock = new Object();
    }

    public static int bookIdCalculator(int i) {
        int result = (i / 3) + (i % 3 == 0 ? 0 : 1);
        return result;
    }

    public void setMonsterBook(int bookid, UserMonsterBookProgress progress) {
        _mb.put(bookid, progress);
    }

    public ArrayList<UserMonsterBookProgress> getProgressList() {
        ArrayList<UserMonsterBookProgress> list = new ArrayList<UserMonsterBookProgress>(_mb.size());
        list.addAll(_mb.values());
        return list;
    }

    public void addMonster(MonsterBook book) {
        if (book == null) {
            return;
        }
        int bookId = book.getBookId();
        synchronized (_lock) {
            UserMonsterBookProgress progress = _mb.get(bookId);
            if (progress == null) {
                progress = new UserMonsterBookProgress(bookId, 1, 1, 0);
                setMonsterBook(bookId, progress);
                _owner.sendPackets(new S_MonsterBook(S_MonsterBook.MB_ADD, bookId, progress.getStep()));
            } else {
                progress.addStep(1);
                _owner.sendPackets(new S_MonsterBook(S_MonsterBook.MB_ADD, bookId, progress.getStep()));
                // 每個任務完成的時候
                // if(progress.getStep() == book.getStepThird() ||
                // progress.getStep() == book.getStepSecond() ||
                // progress.getStep() == book.getStepFirst()){
                if (progress.getStep() >= book.getStepThird() && progress.getCompleted() != 3 || progress.getStep() >= book.getStepSecond() && progress.getCompleted() != 2 || progress.getStep() >= book.getStepFirst() && progress.getCompleted() != 1) {
                    _owner.sendPackets(new S_MonsterBook(S_MonsterBook.MB_NEXT, bookId, progress.getLevel() + 4));
                    progress.setLevel(progress.getLevel() + 1);
                }
            }
        }
    }

    public void teleport(int bookId) {
        MonsterBook book = MonsterBookLoader.getInstance().getTemplate(bookId);
        if (book == null) {
            return;
        }
        if (!_owner.getMap().isEscapable()) {
            _owner.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); // 解除傳送鎖定
            _owner.sendPackets(new S_ServerMessage(4726)); // 怪物圖鑒：在該地區無法使用
            return;
        }
        final L1Item item = ItemTable.get().getTemplate(book.getMarterial());
        // if(!_owner.getInventory().checkItem(140100, 1)){
        if (!_owner.getInventory().checkItem(book.getMarterial(), 1)) {
            // _owner.sendPackets(new
            // S_MonsterBook(S_MonsterBook.MB_TELEPORT_FAIL));
            _owner.sendPackets(new S_ServerMessage(4692, item.getName()));
            return;
        }
        L1Location baseloc = new L1Location(book.getTelX(), book.getTelY(), book.getTelMapId());
        L1Location loc = L1Location.randomLocation(baseloc, 0, 5, true);
        // _owner.getInventory().consumeItem(140100, 1);
        _owner.getInventory().consumeItem(book.getMarterial(), 1);
        L1Teleport.teleport(_owner, loc, _owner.getHeading(), true);
    }

    public void complete(int bookId) {
        UserMonsterBookProgress progress = _mb.get(bookId);
        if (progress == null) {
            return;
        }
        MonsterBook book = MonsterBookLoader.getInstance().getTemplate(progress.getBookId());
        if (book == null) {
            return;
        }
        if (progress.getCompleted() >= 3) {
            return;
        }
        switch (progress.getCompleted()) {
            case 0:
                if (book.getStepFirst() > progress.getStep()) {
                    return;
                }
                break;
            case 1:
                if (book.getStepSecond() > progress.getStep()) {
                    return;
                }
                break;
            case 2:
                if (book.getStepThird() > progress.getStep()) {
                    return;
                }
                break;
        }
        _owner.sendPackets(new S_MonsterBook(S_MonsterBook.MB_NEXT, progress.getBookId(), 0x05 + progress.getCompleted()));
        _owner.sendPackets(new S_MonsterBook(S_MonsterBook.MB_USE, book.getClearNum() + progress.getCompleted(), 0));
        progress.setCompleted(progress.getCompleted() + 1);
        ArrayList<NormalQuestCompensator> list = MonsterBookCompensateLoader.getInstance().getNormalCompensators(progress.getCompleted());
        int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            list.get(i).compensate(_owner);
        }
        _owner.sendPackets(new S_SkillSound(_owner.getId(), 3944));
    }

    public void sendList() {
        UserMonsterBookProgress progress = null;
        ArrayList<UserMonsterBookProgress> list = getProgressList();
        MonsterBook book = null;
        int size = list.size();
        int level = 0;
        int clearNum = 0;
        S_MonsterBook s_book = new S_MonsterBook(S_MonsterBook.MB_LOGIN_LIST);
        _owner.sendPackets(new S_MonsterBook(S_MonsterBook.MB_LOGIN_SYN));
        s_book.addH(0x08);
        s_book.addH(0x10);
        DelaySender ds = new DelaySender(_owner);
        for (int i = 0; i < size; i++) {
            progress = list.get(i);
            level = progress.getLevel();
            book = MonsterBookLoader.getInstance().getTemplate(progress.getBookId());
            if (book == null) {
                continue;
            }
            clearNum = book.getClearNum();
            s_book.addC(0x1A);
            s_book.addC(s_book.getSize7B(progress.getBookId()) + s_book.getSize7B(progress.getStep()) + 2);
            s_book.addC(0x08);
            s_book.add4Bit(progress.getBookId());
            s_book.addC(0x10);
            s_book.add4Bit(progress.getStep());
            for (int j = 0; j < level - 1; j++) {
                ds.add(new S_MonsterBook(S_MonsterBook.MB_NEXT, progress.getBookId(), 0x05 + j));
                if (progress.isCompleted(j + 1)) {
                    ds.add(new S_MonsterBook(S_MonsterBook.MB_USE, clearNum + j, 0));
                }
            }
        }
        s_book.addH(0x00);
        _owner.sendPackets(s_book);
        ds.run();
    }

    static class DelaySender {
        private ArrayList<ServerBasePacket> _pck;
        private L1PcInstance _pc;

        public DelaySender(L1PcInstance pc) {
            _pck = new ArrayList<ServerBasePacket>(6);
            _pc = pc;
        }

        public void add(ServerBasePacket p) {
            _pck.add(p);
        }

        public void run() {
            int size = _pck.size();
            for (int i = 0; i < size; i++) {
                _pc.sendPackets(_pck.get(i));
            }
        }
    }
}
