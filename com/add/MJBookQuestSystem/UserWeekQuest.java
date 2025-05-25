package com.add.MJBookQuestSystem;

import com.add.MJBookQuestSystem.Compensator.WeekQuestCompensator;
import com.add.MJBookQuestSystem.Loader.MonsterBookCompensateLoader;
import com.add.MJBookQuestSystem.Loader.WeekQuestLoader;
import com.add.MJBookQuestSystem.Templates.UserWeekQuestProgress;
import com.add.MJBookQuestSystem.Templates.WeekQuestDateCalculator;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_WeekQuest;
import com.lineage.server.utils.MJBytesOutputStream;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

public class UserWeekQuest {
    private static final S_SystemMessage _updateMessage = new S_SystemMessage("每週任務都被更新了，重新登入並獲得獎勵。");
    private L1PcInstance _owner;
    private UserWeekQuestProgress[][] _wq;
    private Object _lock;

    public UserWeekQuest(L1PcInstance pc) {
        _owner = pc;
        _wq = new UserWeekQuestProgress[][]{{null, null, null}, {null, null, null}, {null, null, null},};
        _lock = new Object();
    }

    /**
     * 映射每周任務信息
     **/
    public void setWeekQuestInformation(ResultSet rs) throws Exception {
        int bookId;
        int difficulty;
        int section;
        int step;
        Timestamp stamp;
        boolean isCompleted;
        // 首先我們劃分數據庫信息.
        while (rs.next()) {
            bookId = rs.getInt("bookId");
            difficulty = rs.getInt("difficulty");
            section = rs.getInt("section");
            step = rs.getInt("step");
            stamp = rs.getTimestamp("stamp");
            isCompleted = rs.getBoolean("completed");
            _wq[difficulty - 1][section] = new UserWeekQuestProgress(bookId, difficulty, section, step, stamp, isCompleted);
        }
        MonsterBook book = null;
        WeekQuestDateCalculator wqcal = WeekQuestDateCalculator.getInstance();
        WeekQuestLoader wqLoader = WeekQuestLoader.getInstance();
        // 檢查載入的怪物圖鑒
        for (int i = 0; i < 3; i++) {
            // 如果沒有信息加載，就會分配一個新的
            if (_wq[i][0] == null) {
                for (int j = 0; j < 3; j++) {
                    _wq[i][j] = new UserWeekQuestProgress(0, 0, 0, 0, null, false);
                }
            }
            // 如果每個難度級別應該根據0進行更新，就會更新每周任務
            if (wqcal.isUpdateWeekQuest(_wq[i][0].getStamp())) {
                for (int j = 0; j < 3; j++) {
                    book = wqLoader.getBook(i, j);
                    // 更新資訊
                    _wq[i][j].setBookId(book.getBookId());
                    _wq[i][j].setDifficulty(i + 1);
                    _wq[i][j].setSection(j);
                    _wq[i][j].setStamp(wqcal.getUpdateStamp());
                    _wq[i][j].setStep(0);
                    _wq[i][j].setCompleted(false);
                }
            }
        }
    }

    public byte[] getSerialize() throws Exception {
        @SuppressWarnings("resource") MJBytesOutputStream mbos = new MJBytesOutputStream();
        byte[] section1 = null;
        byte[] section2 = null;
        byte[] section3 = null;
        int successfully = 0;
        mbos.write(0x20);
        mbos.write(0x37);
        for (int i = 0; i < 3; i++) {
            successfully = 1;
            section1 = _wq[i][0].getSerialize();
            section2 = _wq[i][1].getSerialize();
            section3 = _wq[i][2].getSerialize();
            mbos.write(0x12);
            mbos.write(section1.length + section2.length + section3.length + 4);
            mbos.write(0x08);
            mbos.write(i);
            mbos.write(0x18);
            // 如果線路清除，請激活獎勵按鈕
            if (isLineClear(i)) {
                successfully = 3;
                // 如果已經得到了獎勵，那就會成為曲線狀態
                if (isLineCompleted(i)) {
                    successfully = 5;
                }
            }
            mbos.write(successfully);
            mbos.write(section1);
            mbos.write(section2);
            mbos.write(section3);
        }
        return mbos.toArray();
    }

    /**
     * 如果每節有多個bookids，則為true，否則為false
     **/
    @SuppressWarnings("unused")
    private boolean checkDuplicateBookId(MonsterBook book, int difficulty, int section) throws Exception {
        if (difficulty < 0 || difficulty > 2) {
            throw new Exception("invalid difficulty " + difficulty);
        }
        for (int i = section - 1; i >= 0; i--) {
            if (_wq[difficulty][i].getBookId() == book.getBookId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回特定難度任務的列表
     **/
    public UserWeekQuestProgress[] getProgressList(int difficulty) {
        UserWeekQuestProgress[] progresses = new UserWeekQuestProgress[3];
        progresses[0] = _wq[difficulty][0];
        progresses[1] = _wq[difficulty][1];
        progresses[2] = _wq[difficulty][2];
        return progresses;
    }

    /**
     * 返回所有主列表
     **/
    public ArrayList<UserWeekQuestProgress> getProgressList() {
        ArrayList<UserWeekQuestProgress> list = new ArrayList<UserWeekQuestProgress>(9);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                list.add(_wq[i][j]);
            }
        }
        return list;
    }

    /**
     * 發送主列表
     **/
    public void sendList() {
        // 發送周任務列表
        S_WeekQuest wq = new S_WeekQuest();
        wq.writeWQList(_owner);
        _owner.sendPackets(wq);
    }

    /**
     * 瞬間移動請求響應
     **/
    public void teleport(int difficulty, int section) {
        if (difficulty < 0 || difficulty > 2) {
            return;
        }
        _owner.getMonsterBook().teleport(_wq[difficulty][section].getBookId());
    }

    /**
     * 狩獵怪物
     **/
    public void addMonster(MonsterBook book) {
        if (book == null) {
            return;
        }
        UserWeekQuestProgress progress = null;
        int difficulty = book.getWeekDifficulty();
        if (difficulty < 0 || difficulty > 2) {
            return;
        }
        if (WeekQuestDateCalculator.getInstance().isUpdateWeekQuest(_wq[difficulty][0].getStamp())) {
            _owner.sendPackets(_updateMessage);
            return;
        }
        // 如果已清除或已獲得獎勵, 則返回
        if (isLineClear(difficulty) || isLineCompleted(difficulty)) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            progress = _wq[difficulty][i];
            if (progress.getBookId() != book.getBookId()) {
                continue;
            }
            synchronized (_lock) {
                progress.addStep(1);
                // 發送更新的任務狀態
                S_WeekQuest wq = new S_WeekQuest();
                wq.writeWQUpdate(difficulty, i, progress.getStep());
                _owner.sendPackets(wq);
                // 如果任務已經完成，則會發送完成消息
                if (isLineClear(difficulty)) {
                    wq = new S_WeekQuest();
                    wq.writeWQLineClear(difficulty, 3);
                    _owner.sendPackets(wq);
                }
                return;
            }
        }
    }

    /**
     * 執行獎勵
     **/
    public void complete(int difficulty, int section) {
        synchronized (_lock) {
            if (difficulty < 0 || difficulty > 2) {
                return;
            }
            if (WeekQuestDateCalculator.getInstance().isUpdateWeekQuest(_wq[difficulty][0].getStamp())) {
                _owner.sendPackets(_updateMessage);
                return;
            }
            // 如果線路被補償, 則該行未清除或不處理
            if (!isLineClear(difficulty) || isLineCompleted(difficulty)) {
                StringBuilder sb = new StringBuilder(128);
                sb.append("已經收到每周任務獎勵的玩家 : ").append(_owner.getName()).append(" 將試圖獎勵。");
                System.out.println(sb.toString());
                return;
            }
            _wq[difficulty][0].setCompleted(true);
            _wq[difficulty][1].setCompleted(true);
            _wq[difficulty][2].setCompleted(true);
            S_WeekQuest wq = new S_WeekQuest();
            wq.writeWQLineClear(difficulty, 5);
            _owner.sendPackets(wq);
            WeekQuestCompensator compensator = MonsterBookCompensateLoader.getInstance().getWeekCompensator(section);
            compensator.compensate(_owner);
        }
    }

    /**
     * 所有行都被清除
     **/
    public boolean isLineClear(int difficulty) {
        if (_wq[difficulty][0].isClear() && _wq[difficulty][1].isClear() && _wq[difficulty][2].isClear()) {
            return true;
        }
        return false;
    }

    /**
     * 收到的行獎勵
     **/
    public boolean isLineCompleted(int difficulty) {
        if (_wq[difficulty][0].isCompleted() || _wq[difficulty][1].isCompleted() || _wq[difficulty][2].isCompleted()) {
            return true;
        }
        return false;
    }
}
