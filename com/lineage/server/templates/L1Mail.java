package com.lineage.server.templates;

import java.sql.Timestamp;

public final class L1Mail { // repaired by terry0412
    private int _id;
    private int _type;
    private String _senderName;
    private String _receiverName;
    /**
     * 發信日期 (修正 by terry0412)
     */
    private Timestamp _date;
    private int _readStatus;
    private byte[] _subject;
    private byte[] _content;
    /**
     * 信件發送類型 (0:發信 1:收信) by terry0412
     */
    private int _send_type;

    public L1Mail() {
    }

    public int getId() {
        return _id;
    }

    public void setId(final int i) {
        _id = i;
    }

    public int getType() {
        return _type;
    }

    public void setType(final int i) {
        _type = i;
    }

    public String getSenderName() {
        return _senderName;
    }

    public void setSenderName(final String s) {
        _senderName = s;
    }

    public String getReceiverName() {
        return _receiverName;
    }

    public void setReceiverName(final String s) {
        _receiverName = s;
    }

    public Timestamp getDate() {
        return _date;
    }

    public void setDate(final Timestamp date) {
        _date = date;
    }

    public int getReadStatus() {
        return _readStatus;
    }

    public void setReadStatus(final int i) {
        _readStatus = i;
    }

    public byte[] getSubject() {
        return _subject;
    }

    public void setSubject(final byte[] arg) {
        _subject = arg;
    }

    public byte[] getContent() {
        return _content;
    }

    public void setContent(final byte[] arg) {
        _content = arg;
    }

    public int getSendType() {
        return _send_type;
    }

    public void setSendType(final int i) {
        _send_type = i;
    }
}
