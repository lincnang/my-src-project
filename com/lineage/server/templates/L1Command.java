package com.lineage.server.templates;

public class L1Command {
    private final String _name;
    private final boolean _system;
    private final int _level;
    private final String _executorClassName;
    private final String _note;

    public L1Command(String name, boolean system, int level, String executorClassName, String note) {
        _name = name;
        _system = system;
        _level = level;
        _executorClassName = executorClassName;
        _note = note;
    }

    public String getName() {
        return _name;
    }

    public boolean isSystem() {
        return _system;
    }

    public int getLevel() {
        return _level;
    }

    public String getExecutorClassName() {
        return _executorClassName;
    }

    public String get_note() {
        return _note;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Command JD-Core Version: 0.6.2
 */