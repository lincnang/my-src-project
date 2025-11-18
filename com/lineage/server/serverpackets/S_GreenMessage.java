/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lineage.server.serverpackets;

public class S_GreenMessage extends ServerBasePacket {
    private static final String S_GREENMESSAGE = "[S] S_GreenMessage";

    public S_GreenMessage(String s) {
        writeC(S_OPCODE_GREENMESSAGE);
        writeC(0x54);
        writeC(0x02);
        writeS(sanitizeForClient(s));
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return S_GREENMESSAGE;
    }

    // 與 S_ServerMessage 相同的最小清理策略
    private static String sanitizeForClient(String input) {
        if (input == null) return "";
        String s = input
                .replace('%', '％')
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ');
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0x20 && c != 0x7F) {
                sb.append(c);
            } else {
                sb.append(' ');
            }
        }
        final int MAX_LEN = 200;
        if (sb.length() > MAX_LEN) return sb.substring(0, MAX_LEN);
        return sb.toString();
    }
}