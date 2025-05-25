package com.lineage.server.model.npc;

public class L1NpcHtml {
    public static final L1NpcHtml HTML_CLOSE = new L1NpcHtml("");
    private final String _name;
    private final String[] _args;

    public L1NpcHtml(String name) {
        this(name, new String[0]);
    }

    public L1NpcHtml(String name, String[] args) {
        if ((name == null) || (args == null)) {
            throw new NullPointerException();
        }
        _name = name;
        _args = args;
    }

    public String getName() {
        return _name;
    }

    public String[] getArgs() {
        return _args;
    }
}
