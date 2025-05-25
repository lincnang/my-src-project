package com.lineage.server.serverpackets;

import com.lineage.server.datatables.SoulTowerTable.SoulTowerRank;
import com.lineage.server.person.Fish_Time;
import com.lineage.server.utils.LineageUtil;
import l1j.server.server.datas.protobuf.PBMessageALL4;
import l1j.server.server.datas.protobuf.PBMessageALL8;

public class S_ProtoBuffers extends ServerBasePacket {
    //private static Logger _log = Logger.getLogger(S_ProtoBuffers.class.getName());
    // 道具製作
    public static final int CRAFT_LIST = 55;
    // 取得道具製作明細
    public static final int CRAFT_GET = 57;
    // 道具製作結果:用途是扣掉"一個單位"的製做道具的資訊
    public static final int CRAFT_RESULT = 59;
    // 釣魚時間(光棒)
    public static final int FISHING_TIME = 63;
    public static final int WAR_EMBLEM = 65;
    // [MSG_0] 1= int= 764471
    // [MSG_0] 2= int= 1
    // XXX 已佔領城堡的血盟如下
    public static final int WAR_UNKNOW = 68;
    // 攻/守城資訊
    public static final int WAR_INFO = 76;
    // 道具製作時間
    public static final int CRAFT_TIME = 93;
    // 0000: 18 5d 00 08 00 10 1d 24 80
    // 潘朵拉的抽抽樂
    public static final int LUCKY_DRAW = 101;
    // 特羅斯廣播
    public static final int DEPOROJU_TALK2 = 102;
    // 帶圖片的訊息
    public static final int DEPOROJU_TALK1 = 103;// 無%s
    // XXX 被指定的血盟名可攻擊
    public static final int WAR_ATTACK_CLAN = 104;
    // 新的活動圖示
    public static final int NEW_ICON = 110;
    public static final int SHIFT_SERVER = 113; // 從原本伺服器轉至活動伺服器
    public static final int SHIFT_LOGIN_LOCK = 116; // 轉換伺服器的鎖定畫面 (等待人物轉換成功登入)
    public static final int MAPID = 118;
    public static final int OBJ_PACK = 119;
    public static final int WHO = 120; // XXX 0000: a6 78 00 10 e5 18 00 02
    // 魔法娃娃合成
    public static final int PROMOTE_DOLL = 123;
    public static final int PROMOTE_DOLL_RESULT = 125;
    public static final int PROMOTE_DOLL_BEGIN = 128;
    public static final int THEBES_RANK = 133; // 底比斯排名
    // 活動通知
    public static final int ACTIVITY_NEWS = 141;
    public static final int HTML_TELEPORT_LOCK = 145; // 用於傳送師傳送時顯示傳送特效
    public static final int CASTLE_INFO = 318;
    // 表情符號
    public static final int EMOTICONS = 320;
    // 血盟介面 ?
    public static final int PLEDGE_SETTING3 = 325;
    // 血盟招募-變更的結果
    public static final int PLEDGE_SETTING2 = 327;
    // 血盟招募-變更
    public static final int PLEDGE_SETTING = 333;
    public static final int SOUL_TOEWR_RANK = 335;
    // 好友名單
    public static final int QUERY_BUDDY = 337;
    // 組隊標記
    public static final int PARTY_MARK = 339;
    // 18 53 01 08 dd f9 1a 10 05 1d b0
    // 戰士被動(登入)
    public static final int ADDSKILL_LOGIN = 401;
    // 戰士被動
    public static final int ADDSKILL_NEW = 402;
    // tam點數
    public static final int TAM_POINT = 450;
    // 手遊資訊
    public static final int TAM_INFO = 461;
    // 新的圖示
    public static final int NEW_ICON2 = 463;
    public static final int ABILITY_INFO = 483; // 初始能力資料
    public static final int WEIGHT = 485; // 負重能力
    public static final int BASE_ABILITY_GUIDE = 487; // 基礎能力附加效果
    public static final int ELIXIR_COUNT = 489; // 萬能藥使用數量
    public static final int BASE_ABILITY = 490; // 基礎能力
    public static final int CHAT_SELF = 515;
    public static final int CHAT = 516;
    public static final int QUEST_BEGIN = 518; // 任務開始
    public static final int QUEST_UPDATE = 519; // 任務更新
    public static final int QUEST_GUIDE = 521; // 任務指引
    public static final int QUEST_END = 525; // 任務結束
    public static final int CLAN_NAME = 537;
    public static final int PARTY_ADD_MEMBER = 539;
    public static final int COUNT_DOWN = 540; // 左上角倒數計時+DESC(原本用於底比斯大戰)
    public static final int MONSTER_LIST1 = 559; // 怪物圖鑑1-獎勵狀態
    public static final int MONSTER_LIST2 = 560; // 怪物圖鑑2
    public static final int MONSTER_LIST_STAGE_OK = 564; // 怪物圖鑑階段領取
    public static final int MONSTER_LIST_ADD = 567; // 怪物圖鑑ADD
    public static final int MONSTER_LIST_STAGE = 568; // 怪物圖鑑階段
    // S_EXTENDED_PROTOBUF-568 (71:14) 2016.12.11 21:30:02
    // 0000: 47 38 02 08 ea 03 10 9c a9 b5 c2 05 34 20
    // [MSG_0] 1= int= 490 一個編號3個階段
    // [MSG_0] 2= int= 1481462940
    public static final int ADEN_TEL = 579; // 傳送師地圖
    public static final int EQUIP_CHANGE = 800; // 裝備切換
    public static final int LIMIT_MAP = 803; // 地圖剩餘時間
    public static final int MONSTER_LIST_WEEK = 810;
    public static final int MONSTER_LIST_WEEK_COUNT = 813;
    public static final int MONSTER_LIST_WEEK_STAGE = 814;

    // 屍魂塔排名
    public S_ProtoBuffers(final SoulTowerRank... ranks) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(SOUL_TOEWR_RANK);
        final PBMessageALL4.type15.Builder builder15 = PBMessageALL4.type15.newBuilder();
        for (final SoulTowerRank rank : ranks) {
            final PBMessageALL8.typeSoulTower.Builder builder8 = PBMessageALL8.typeSoulTower.newBuilder();
            builder8.setArray1(LineageUtil.getByteString(rank.name)); // name
            builder8.setValue2(rank.classType);// class
            builder8.setValue3(rank.time);// clear time (s)
            builder8.setValue4((int) (rank.date / 1000));// date
            builder8.setValue5(1); // bool 0:clear 1:show
            // 歷史紀錄
            if (builder15.getArray1Count() < 3) {
                builder15.addArray1(builder8.build().toByteString());
            }
            // 目前
            if (builder15.getArray2Count() < 10) {
                builder15.addArray2(builder8.build().toByteString());
            }
        }
        writeByte(builder15.build().toByteArray());
        writeH(0);
    }

    public S_ProtoBuffers(int type, int i, boolean ck, long time) {
        writeC(223);
        writeH(type);
        switch (type) {
            case FISHING_TIME:
                final Fish_Time.sendFishTime.Builder fish = Fish_Time.sendFishTime.newBuilder();
                //PBMessageALL.type1.Builder builder = PBMessageALL.type1.newBuilder();
                if (1 == i) {
                    fish.setType(i); // 2:?? 正服結束時會出現
                    fish.setTime((int) time);// sint
                    fish.setIsQuick(ck ? 2 : 1); // 1:未安裝線軸 2:安裝線軸
                } else {
                    fish.setType(i);
                }
                writeByte(fish.build().toByteArray());
                break;
            default:
                System.out.println("S_NewCreateItem未知32OP:" + type);
        }
        writeH(0);
    }

    //新手任務
		/*public S_ProtoBuffers(final int type, final L1QuestNew qn) {
			writeC(S_EXTENDED_PROTOBUF);
			writeH(type);
			final PBMessageALL5.type16.Builder builder16 = PBMessageALL5.type16.newBuilder();
			builder16.setValue1(qn.getId());
			builder16.setValue2(qn.getOwner().getId());
			////
			final PBMessageALL.type1.Builder builder1 = PBMessageALL.type1.newBuilder();
			int index = 1;
			if (qn.getRequireLevel() > 0) {
				builder1.setValue1(index++);
				builder1.setValue2(qn.getCurrentLevel());
				builder1.setValue3(qn.getRequireLevel());
				builder16.addArray4(builder1.build().toByteString());
			}
			if (qn.getRequireNpcid().length > 0) {
				for (int i = 0; i < qn.getRequireNpcid().length; i++) {
					builder1.setValue1(index++);
					builder1.setValue2(qn.getCurrentNpcCount()[i]);
					builder1.setValue3(qn.getRequireNpcCount()[i]);
					builder16.addArray4(builder1.build().toByteString());
				}
			}
			if (qn.getRequireItemid().length > 0) {
				for (int i = 0; i < qn.getRequireItemid().length; i++) {
					builder1.setValue1(index++);
					builder1.setValue2(qn.getCurrentItemCount()[i]);
					builder1.setValue3(qn.getRequireItemCount()[i]);
					builder16.addArray4(builder1.build().toByteString());
				}
			}
			if (qn.getRequireUseItemid().length > 0) {
				for (int i = 0; i < qn.getRequireUseItemid().length; i++) {
					builder1.setValue1(index++);
					builder1.setValue2(qn.getCurrentUseItemCount()[i]);
					builder1.setValue3(qn.getRequireUseItemCount()[i]);
					builder16.addArray4(builder1.build().toByteString());
				}
			}
			////
			builder16.setValue5(1); // unknow
			final PBMessageALL5.type17.Builder builder = PBMessageALL5.type17.newBuilder();
			builder.setArray1(builder16.build().toByteString());
			writeByte(builder.build().toByteArray());
			writeH(0x00);
		}*/
    @Override
    public byte[] getContent() {
        return _bao.toByteArray();
    }

    @Override
    public String getType() {
        return "S_ProtoBuffers";
    }
}
