package com.add.MJBookQuestSystem.Templates;

import com.add.MJBookQuestSystem.Loader.MonsterBookLoader;
import com.add.MJBookQuestSystem.MonsterBook;
import com.lineage.server.utils.MJBytesOutputStream;

import java.sql.Timestamp;

public class UserWeekQuestProgress {
    private int _bookId;
    private int _difficulty;
    private int _section;
    private int _step;
    private Timestamp _stamp;
    private boolean _iscompleted;

    public UserWeekQuestProgress(int bookId, int difficulty, int section, int step, Timestamp stamp, boolean iscompleted) {
        _bookId = bookId;
        _difficulty = difficulty;
        _section = section;
        _step = step;
        _stamp = stamp;
        _iscompleted = iscompleted;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("book id\t: ").append(_bookId).append("\r\n");
        sb.append("difficulty\t: ").append(_difficulty).append("\r\n");
        sb.append("section\t: ").append(_section).append("\r\n");
        sb.append("step\t: ").append(_step).append("\r\n");
        sb.append("stamp\t: ").append(_stamp.toString()).append("\r\n");
        sb.append("isCompleted\t: ").append(_iscompleted).append("\r\n\r\n");
        return sb.toString();
    }

    public void addStep(int i) {
        setStep(getStep() + i);
    }

    public int getBookId() {
        return _bookId;
    }

    public void setBookId(int i) {
        _bookId = i;
    }

    public int getDifficulty() {
        return _difficulty;
    }

    public void setDifficulty(int i) {
        _difficulty = i;
    }

    public int getSection() {
        return _section;
    }

    public void setSection(int i) {
        _section = i;
    }

    public int getStep() {
        return _step;
    }

    public void setStep(int i) {
        _step = i;
    }

    public Timestamp getStamp() {
        return _stamp;
    }

    public void setStamp(Timestamp ts) {
        _stamp = ts;
    }

    public boolean isCompleted() {
        return _iscompleted;
    }

    public void setCompleted(boolean b) {
        _iscompleted = b;
    }

    public boolean isClear() {
        MonsterBook book = MonsterBookLoader.getInstance().getTemplate(_bookId);
        if (book == null) {
            return false;
        }
        return isClear(book);
    }

    public boolean isClear(MonsterBook book) {
        if (book.getWeekSuccessCount() <= _step) {
            return true;
        }
        return false;
    }

    public byte[] getSerialize() throws Exception {
        MonsterBook book = MonsterBookLoader.getInstance().getTemplate(_bookId);
        if (book == null) {
            return null;
        }
        @SuppressWarnings("resource") MJBytesOutputStream mbos = new MJBytesOutputStream();
        mbos.write(0x22);
        mbos.write(0x00);
        mbos.write(0x08);
        mbos.write(getSection());
        mbos.write(0x10);
        mbos.writeBit(book.getWeekSuccessCount()); // 這裡
        mbos.write(0x18);
        mbos.writeBit(book.getClearNum());
        mbos.write(0x20);
        mbos.writeBit(book.getBookId());
        mbos.write(0x28);
        mbos.writeBit(getStep()); // 這裡
        byte[] b = mbos.toArray();
        int size = b.length - 2;
        b[1] = (byte) (size & 0xff);
        return b;
    }
}
