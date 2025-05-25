package com.add.MJBookQuestSystem.Templates;

import com.add.MJBookQuestSystem.Loader.MonsterBookLoader;
import com.add.MJBookQuestSystem.MonsterBook;

public class UserMonsterBookProgress {
    /**
     * 圖鑒 id
     **/
    private int _bookId;
    /**
     * 當前進度級別
     **/
    private int _level;
    /**
     * 當前的數量
     **/
    private int _step;
    /**
     * 得到補償的階段
     **/
    private int _completed;

    public UserMonsterBookProgress(int bookId, int level, int step, int completed) {
        _bookId = bookId;
        _level = level;
        _step = step;
        _completed = completed;
    }

    public void addLevel(int i) {
        setLevel(getLevel() + i);
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

    public int getLevel() {
        return _level;
    }

    public void setLevel(int i) {
        _level = i;
    }

    public int getStep() {
        return _step;
    }

    public void setStep(int i) {
        _step = i;
        MonsterBook book = MonsterBookLoader.getInstance().getTemplate(getBookId());
        if (book == null) {
            return;
        }
        if (book.getStepThird() <= _step) {
            setLevel(3);
        } else if (book.getStepSecond() <= _step) {
            setLevel(2);
        } else {
            setLevel(1);
        }
    }

    public int getCompleted() {
        return _completed;
    }

    public void setCompleted(int i) {
        _completed = i;
    }

    public boolean isCompleted(int level) {
        if (_completed - level >= 0) {
            return true;
        }
        return false;
    }
}
