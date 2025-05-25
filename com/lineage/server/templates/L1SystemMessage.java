package com.lineage.server.templates;

import com.lineage.server.datatables.SystemMessageTable;

public class L1SystemMessage {
    private int _id;
    private String _message;

    public L1SystemMessage(final int id, final String message) {
        this._id = id;
        this._message = message;
    }

    public static String ShowMessage(final int id) {
        final L1SystemMessage System_Message = SystemMessageTable.getInstance().getTemplate(id);
        if (System_Message == null) {
            return "";
        }
        final String Message = System_Message.getMessage();
        return Message;
    }

    public int getId() {
        return this._id;
    }

    public String getMessage() {
        return this._message;
    }
}
