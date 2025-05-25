package com.lineage.server.serverpackets;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Account;
import com.lineage.server.utils.SQLUtil;

import java.sql.*;

/**
 * 成長果實系統(Tam幣)
 */
public class S_TamWindow extends ServerBasePacket {
    public static final int ACCOUNT_TAM = 0xCD;
    public static final int TAM_POINT = 0x1c2;
    public static final int BUFF_WINDOW = 0x6e;
    public static final int[] hextable = {0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x8b, 0x8c, 0x8d, 0x8e, 0x8f, 0x90, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0x9b, 0x9c, 0x9d, 0x9e, 0x9f, 0xa0, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, 0xad, 0xae, 0xaf, 0xb0, 0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf, 0xc0, 0xc1, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xcb, 0xcc, 0xcd, 0xce, 0xcf, 0xd0, 0xd1, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xdb, 0xdc, 0xdd, 0xde, 0xdf, 0xe0, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xeb, 0xec, 0xed, 0xee, 0xef, 0xf0, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe, 0xff};
    private static final String S_TAMWINDOW = "S_TamWindow";
    private byte[] _byte = null;

    /**
     * 帳號顯示TAM幣
     *
     * @param pc
     */
    public S_TamWindow(final L1PcInstance pc) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(TAM_POINT);
        writeC(0x08);
        L1Account account = pc.getNetConnection().getAccount();
        int value = account.get_tam_point();
        if (value <= 0) {
            writeC(0x00);
        }
        byteWrite(value);
        writeH(0);
    }

    /**
     * TAM成長階段狀態圖示
     *
     * @param iconid   狀態圖示編號
     * @param on       開關true/false
     * @param stringid string.tbl訊息編號
     * @param time     時間
     */
    public S_TamWindow(int iconid, boolean on, int stringid, long time) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(BUFF_WINDOW);
        writeC(0x08);
        writeC(on ? 2 : 3); // 開關
        writeC(0x10);
        writeBit(iconid); // 狀態圖示編號
        writeC(0x18);
        writeBit(time / 1000); // 時間
        writeC(0x20);
        writeC(0x08);
        writeC(0x28);
        writeBit(iconid); // 狀態圖示編號
        writeH(0x30);
        writeC(0x38);
        writeC(0x03);
        writeC(0x40);
        writeBit(stringid); // 狀態圖示string.tbl訊息內容編號
        writeC(0x48);
        writeC(0x00);
        writeH(0x0050);
        writeC(0x58);
        writeC(0x01);
        writeC(0x60);
        writeC(0x00);
        writeC(0x68);
        writeC(0x00);
        writeC(0x70);
        writeC(0x00);
        writeH(0x00);
    }

    public S_TamWindow(int id, int time, int type) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(BUFF_WINDOW);
        writeC(0x08);
        writeC(2);
        writeC(0x10);
        write7B(id);
        writeC(0x18);
        write7B(time);
        writeC(0x20);
        writeC(8);
        writeC(0x28);
        write7B(type == 1 ? 6100 : type == 2 ? 6546 : 6547);
        writeC(0x30);
        writeC(0);
        writeC(0x38);
        writeC(1);
        writeC(0x40);
        write7B(type + 4180); // string
        writeC(0x48);
        writeH(0x20d5);
        writeC(0x50);
        writeC(0);
        writeC(0x58);
        writeC(1);
        writeH(0);
    }

    public S_TamWindow(String account) {
        writeC(S_EXTENDED_PROTOBUF);
        writeC(ACCOUNT_TAM);
        writeC(0x01);//?
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int _level = 0;
        int _class = 0;
        int _sex = 0;
        String _Name = null;
        Timestamp tamtime = null;
        int _objid = 0;
        int objidcount;
        long time = 0;
        long sysTime = System.currentTimeMillis();
        int tamcount;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT objid, TamEndTime, level, char_name, Type,Sex FROM characters WHERE account_name=? ORDER BY `TamEndTime` DESC, `EXP` DESC");
            pstm.setString(1, account);
            rs = pstm.executeQuery();
            while (rs.next()) {
                tamcount = 0;
                objidcount = 0;
                time = 0;
                tamtime = null;
                _objid = rs.getInt("objid");
                _level = rs.getInt("level");
                _class = rs.getInt("Type");
                _sex = rs.getInt("Sex");
                _Name = rs.getString("char_name");
                tamtime = rs.getTimestamp("TamEndTime");
                if (tamtime != null) {
                    if (sysTime < tamtime.getTime()) {
                        time = tamtime.getTime() - sysTime;
                    }
                }
                if (time == 0) {
                    tamcount = 1;
                } else {
                    tamcount = byteWriteCount(time / 1000);
                }
                objidcount = byteWriteCount(_objid);
                writeC(0x0a);//
                // System.out.println(objidcount);
                /* addlen */
                //writeC(_Name.getBytes().length + 14 + objidcount + tamcount);// パケット全体の長さ
                writeBit(_Name.getBytes().length + 14 + objidcount + tamcount);
                writeC(0x08);//
                writeC(0x00);// 変化（サーバー異なる変更）
                writeC(0x10);//
                byteWrite(_objid);// 乗車いる時上がる。
                //write4bit(_objid);//テスト
                writeC(0x18);
                if (time == 0) {
                    writeC(0);// 乗車
                } else {
                    byteWrite(time / 1000);// 乗車いる時上がる。
                    //write4bit(time/1000);
                }
                writeC(0x20);
                //writeC(tamwaitcount(_objid));
                writeBit(tamwaitcount(_objid));
                writeC(0x2a);//
                //writeC(_Name.getBytes().length);// 名前の長さ
                writeBit(_Name.getBytes().length);// 名前の長さ
                writeByte(_Name.getBytes());// 名前の後に0ない名前。
                writeC(0x30);//
                writeC(_level);// レベル
                writeC(0x38);//
                writeC(_class);// クラス番号
                writeC(0x40);//
                writeC(_sex);// _sex);//男女性？
            }
            writeC(0x10);
            writeC(0x03);
            writeC(0x18);
            writeC(0x00);
            writeC(0x20);
            writeC(0x00);
            writeH(0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void byteWrite(long value) {
        long temp = value / 128;
        if (temp > 0) {
            writeC(hextable[(int) value % 128]);
            while (temp >= 128) {
                writeC(hextable[(int) temp % 128]);
                temp = temp / 128;
            }
            if (temp > 0) {
                writeC((int) temp);
            }
        } else {
            if (value == 0) {
                writeC(0);
            } else {
                writeC(hextable[(int) value]);
                writeC(0);
            }
        }
    }

    private int byteWriteCount(long value) {
        long temp = value / 128;
        int count = 0;
        if (temp > 0) {
            count++;
            while (temp >= 128) {
                count++;
                temp = temp / 128;
            }
            if (temp > 0) {
                count++;
            }
        } else {
            if (value == 0) {
                count++;
            } else {
                count += 2;
            }
        }
        return count;
    }

    private int tamwaitcount(int obj) {
        int count = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM tam WHERE objid = ?");
            pstm.setInt(1, obj);
            rs = pstm.executeQuery();
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return count;
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    public String getType() {
        return S_TAMWINDOW;
    }
}
