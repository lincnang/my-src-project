package com.lineage.server.utils;

import com.google.protobuf.ByteString;
import com.lineage.config.Config;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LineageUtil {
    private static Logger _log = Logger.getLogger(LineageUtil.class.getName());

    public static ByteString getByteString(final String s) {
        ByteString result = ByteString.EMPTY;
        try {
            result = ByteString.copyFrom(s.getBytes(Config.CLIENT_LANGUAGE_CODE));
        } catch (final UnsupportedEncodingException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return result;
    }
}