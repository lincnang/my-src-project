package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Master;
import com.lineage.server.world.WorldClan;

import java.util.ArrayList;

public class S_PacketBox extends ServerBasePacket {
    /**
     * C(id) H(?): %sの攻城戦が始まりました。
     */
    public static final int MSG_WAR_BEGIN = 0;
    /**
     * C(id) H(?): %sの攻城戦が終了しました。
     */
    public static final int MSG_WAR_END = 1;
    /**
     * C(id) H(?): %sの攻城戦が進行中です。
     */
    public static final int MSG_WAR_GOING = 2;
    /**
     * -: 城の主導権を握りました。 (音楽が変わる)
     */
    public static final int MSG_WAR_INITIATIVE = 3;
    /**
     * -: 城を占拠しました。
     */
    public static final int MSG_WAR_OCCUPY = 4;
    /**
     * ?: 決闘が終りました。 (音楽が変わる)
     */
    public static final int MSG_DUEL = 5;
    /**
     * C(count): SMSの送信に失敗しました。 / 全部で%d件送信されました。
     */
    public static final int MSG_SMS_SENT = 6;
    /**
     * -: 祝福の中、2人は夫婦として結ばれました。 (音楽が変わる)
     */
    public static final int MSG_MARRIED = 9;
    /**
     * C(weight): 重量(30段階)
     */
    public static final int WEIGHT = 10;
    /**
     * C(food): 満腹度(30段階)
     */
    public static final int FOOD = 11;
    /**
     * C(0) C(level): このアイテムは%dレベル以下のみ使用できます。 (0~49以外は表示されない)
     */
    public static final int MSG_LEVEL_OVER = 12;
    /**
     * UB情報HTML
     */
    public static final int HTML_UB = 14;
    /**
     * C(id)<br>
     * 1:身に込められていた精霊の力が空気の中に溶けて行くのを感じました。<br>
     * 2:体の隅々に火の精霊力が染みこんできます。<br>
     * 3:体の隅々に水の精霊力が染みこんできます。<br>
     * 4:体の隅々に風の精霊力が染みこんできます。<br>
     * 5:体の隅々に地の精霊力が染みこんできます。<br>
     */
    public static final int MSG_ELF = 15;
    /**
     * C(count) S(name)...: 遮断リスト複数追加
     */
    //public static final int ADD_EXCLUDE2 = 17;
    public static final int SHOW_LIST_EXCLUDE = 17;
    /**
     * S(name): 遮断リスト追加
     */
    public static final int ADD_EXCLUDE = 18;
    /**
     * S(name): 遮断解除
     */
    public static final int REM_EXCLUDE = 19;
    /**
     * S_OPCODE_ACTIVESPELLS
     */
    public static final int ICON_ACTIVESPELLS = 20;
    /**
     * スキルアイコン
     */
    public static final int ICONS2 = 21;
    /**
     * S_SkillIconAura
     */
    public static final int ICON_AURA = 22;
    /**
     * S(name): タウンリーダーに%sが選ばれました。
     */
    public static final int MSG_TOWN_LEADER = 23;
    /**
     * 血盟推薦 接受玩家加入
     */
    public static final int HTML_PLEDGE_RECOMMENDATION_ACCEPT = 25;
    /**
     * C(id): あなたのランクが%sに変更されました。<br>
     * id - 1:見習い 2:一般 3:ガーディアン
     */
    public static final int MSG_RANK_CHANGED = 27;
    /**
     * D(?) S(name) S(clanname): %s血盟の%sがラスタバド軍を退けました。
     */
    public static final int MSG_WIN_LASTAVARD = 30;
    /**
     * -: \f1気分が良くなりました。
     */
    public static final int MSG_FEEL_GOOD = 31;
    /**
     * 不明。C_30パケットが飛ぶ
     */
    public static final int SOMETHING1 = 33;
    /**
     * H(time): ブルーポーションのアイコンが表示される。
     */
    public static final int ICON_BLUEPOTION = 34;
    /**
     * H(time): 変身のアイコンが表示される。
     */
    public static final int ICON_POLYMORPH = 35;
    /**
     * H(time): チャット禁止のアイコンが表示される。
     */
    public static final int ICON_CHATBAN = 36;
    /**
     * S_PetEquipment
     */
    public static final int GUI_PET_EQUITMENT = 37;
    /**
     * 血盟情報のHTMLが表示される
     */
    public static final int HTML_CLAN1 = 38;
    /**
     * H(time): イミュのアイコンが表示される
     */
    public static final int ICON_I2H = 40;
    /**
     * キャラクターのゲームオプション、ショートカット情報などを送る
     */
    public static final int CHARACTER_CONFIG = 41;
    /**
     * キャラクター選択画面に戻る
     */
    public static final int LOGOUT = 42;
    /**
     * 戦闘中に再始動することはできません。
     */
    public static final int MSG_CANT_LOGOUT = 43;
    /**
     * 風之枷鎖狀態圖示
     */
    public static final int ICON_WIND_SHACKLE = 44;
    /**
     * C(count) D(time) S(name) S(info):<br>
     * [CALL] ボタンのついたウィンドウが表示される。これはBOTなどの不正者チェックに
     * 使われる機能らしい。名前をダブルクリックするとC_RequestWhoが飛び、クライアントの
     * フォルダにbot_list.txtが生成される。名前を選択して+キーを押すと新しいウィンドウが開く。
     */
    public static final int CALL_SOMETHING = 45;
    /**
     * C(id): バトル コロシアム、カオス大戦がー<br>
     * id - 1:開始します 2:取り消されました 3:終了します
     */
    public static final int MSG_COLOSSEUM = 49;
    /**
     * 血盟情報のHTML
     */
    public static final int HTML_CLAN2 = 51;
    /**
     * 料理ウィンドウを開く
     */
    public static final int COOK_WINDOW = 52;
    /**
     * C(type) H(time): 料理アイコンが表示される
     */
    public static final int ICON_COOKING = 53;
    /**
     * 魚がかかったグラフィックが表示される
     */
    public static final int FISHING = 55;
    /**
     * 魔法娃娃狀態圖示
     */
    public static final int DOLL = 56;
    /**
     * 慎重藥水狀態圖示
     */
    public static final int ICON_WIS_POTION = 57;
    /**
     * 水之元氣狀態圖示取消
     */
    public static final int ICON_WATER_LIFE_CANCEL = 59;
    // 58 : D(objid) H(gfxid) item invgfx change
    /**
     * 三段加速狀態圖示
     */
    public static final int ICON_THIRD_SPEED = 60;
    /**
     * HTML 同盟目錄
     */
    public static final int PLEDGE_UNION = 62;
    /**
     * 小遊戲玩家名單並倒數5秒
     */
    public static final int MINIGAME_GAMESTART = 64;
    /**
     * 小遊戲開始累積時間
     */
    public static final int MINIGAME_COUNTDOWN_1 = 65;
    /**
     * 小遊戲更新玩家名單
     */
    public static final int MINIGAME_PLAYERINFO = 66;
    /**
     * 小遊戲圈數
     */
    public static final int MINIGAME_LAP = 67;
    /**
     * 小遊戲勝利提示
     */
    public static final int MINIGAME_WINNER = 68;
    /**
     * 小遊戲結束提示
     */
    public static final int MINIGAME_GAMEOVER = 69;
    /**
     * 小遊戲關閉累計時間
     */
    public static final int MINIGAME_COUNTDOWN_1_END = 70;
    /**
     * 小遊戲開始倒數時間
     */
    public static final int MINIGAME_COUNTDOWN_2 = 71;
    /**
     * 小遊戲關閉倒數時間
     */
    public static final int MINIGAME_COUNTDOWN_2_END = 72;
    /**
     * 武器破壞者圖示
     */
    public static final int ICON_WEAPON_BREAKER = 74;
    // 61 遊戲時間初始化 + C(0)
    // L1JJP: S_PlayTime()
    /**
     * 弱點曝光
     */
    public static final int ICON_WEAKNESS = 75;
    public static final int MSG_WAR_BEGIN1 = 78;
    public static final int MSG_WAR_END1 = 79;
    /**
     * 攻城戰進行中
     */
    public static final int MSG_WAR_GOING1 = 80; // TODO
    /**
     * 殷海薩的祝福
     */
    public static final int ICON_EINHASADS_BLESS = 82;
    /**
     * 螢幕上的效果
     */
    public static final int GUI_VISUAL_EFFECT = 83;
    /**
     * 螢幕上的字
     */
    public static final int MSG_COLOR_MESSAGE = 84;
    // 1:拍照 2:螢幕搖 3:煙火 4:閃電然後螢幕變黑-->在變亮
    // 5:飽食度特效 6~8:螢幕標框特效
    /**
     * 友好度 S_Karma
     */
    public static final int GUI_KARMA = 87;
    // 1:取消頭上的字 2:頭上有字 3:取消得分框框 4:左上角有得分框框
    /**
     * 閃避率 正
     */
    public static final int DODGE_RATE_PLUS = 88;
    /**
     * 血痕
     */
    public static final int ICON_BLOOD_STAIN = 100;
    /**
     * 閃避率 負
     */
    public static final int DODGE_RATE_MINUS = 101;
    // 82:安塔瑞斯的血痕。 85:法利昂的血痕。 88:林德拜爾的血痕。 91:巴拉卡斯的血痕。
    /**
     * 龍之棲息地 S_DragonGate
     */
    public static final int GUI_DRAGON_GATE = 102;
    /**
     * 組隊系統-新加入隊伍的玩家
     */
    public static final int PARTY_ADD_NEWMEMBER = 104;
    /**
     * 組隊系統-舊的隊員
     */
    public static final int PARTY_OLD_MEMBER = 105;
    /**
     * 組隊系統-隊長委任
     */
    public static final int PARTY_CHANGE_LEADER = 106;
    /**
     * 組隊系統-更新死亡隊員名稱UI顏色
     */
    public static final int PARTY_DEATH_REFRESHNAME = 108;
    /**
     * 組隊系統-更新隊伍
     */
    public static final int PARTY_REFRESH = 110;
    /**
     * XXX發送了座標情報 S_SendLocation
     */
    public static final int MSG_LOCATION = 111;
    /**
     * 戰鬥特化 S_Fight
     */
    public static final int ICON_FIGHT = 114;
    /**
     * 3.8血盟倉庫使用紀錄
     */
    public static final int HTML_CLAN_WARHOUSE_RECORD = 117;
    /**
     * 活動圖示
     */
    public static final int ICON_SECURITY_SERVICES = 125;
    // 120 MSG 畫面中央訊息 用法不明
    /**
     * 活動圖示2
     */
    public static final int ICON_OTHER2 = 127;
    // OS的資安水準是最新狀態. 因此身體和心理上可以感覺變強。
    /**
     * 迴避率更新
     */
    public static final int UPDATE_ER = 132;
    // 物理攻擊+1、魔法攻擊+1、傷害減免+3、體力上限+30、魔力上限+30。
    // 131 ICON 某些狀態圖示會不見
    /**
     * 武器損壞
     */
    public static final int WEAPON_DURABILITY = 138;
    /**
     * 擴張最大記憶空間10個 (0~127)
     */
    public static final int MSG_BOOKMARK_SPACE = 141;
    /**
     * 守護之魂圖示
     */
    public static final int ICON_SOUL_GUARDIAN = 144;
    /**
     * 師徒系統
     */
    public static final int HTML_MASTER = 146;
    /**
     * 常駐型圖示
     */
    public static final int ICON_NO_TIMER = 147;
    /**
     * 道具封印
     */
    public static final int ITEM_STATUS = 149;
    // 147 ICON 常駐型圖示
    // C() //0:不顯示 1:顯示
    // C() 如下
    // 0: 沒有與師父組隊 一名徒弟 -->防禦-1
    // 1: 沒有與師父組隊 二名徒弟 -->防禦-1、魔防+1
    // 2: 沒有與師父組隊 三名徒弟 -->防禦-1、魔防+1、四屬性防禦+2
    // 3: 沒有與師父組隊 四名徒弟 -->防禦-1、魔防+1、四屬性防禦+2、迴避+1
    // 4: 有與師父組隊 一名徒弟 ---->防禦-3
    // 5: 有與師父組隊 二名徒弟 ---->防禦-3、魔防+3
    // 6: 有與師父組隊 三名徒弟 ---->防禦-3、魔防+3、四屬性防禦+6
    // 7: 有與師父組隊 四名徒弟 ---->防禦-3、魔防+3、四屬性防禦+6、迴避+3
    // 8: 感受到憤怒的氣息。
    // 9: STR_STATUS_EFFECT_RED_WINE
    // 10:近距離附加打擊/命中、魔法防禦、各屬性抵抗、體力/魔力恢復、傷害減免、經驗值獎勵
    // 13:傷害減免、經驗值獎勵
    // 14:STR_COOK_BENTO_OF_RAFONS
    // 15:STR_COOK_WINE_OF_RAFONS
    // 16:防禦-3。
    // 17:殷海薩的祝福 (紫色ICON)
    // 18:隱暗術
    // 19:大地屏障
    // 20:擬武
    // 21:地面障礙
    // 22:封印禁地
    // 23:暗盲
    // 24:黑闇之影
    // 25:暗黑盲咒
    // 26:冰矛圍籬
    // 27:木乃伊的詛咒 (被麻痺了)
    // 28:沉睡之霧
    // 29:立方：和諧 (每5秒魔力恢復量+5、體力-25)
    // 30:立方：燃燒(火屬性抵抗+30)
    // 31:立方：地裂(地屬性抵抗+30)
    // 32:毒咒
    // 33:暗影之牙
    // 34:魔法屏障
    // 35:鎧甲護持
    // 36:木乃伊的詛咒 (身體完全硬化)
    // 37:隱身術
    // 38:獲得狩獵經驗+20%的效果。
    // 39:額外攻擊點數+2、攻擊命中率+2
    // 40:感覺到了美味粽子的氣息
    // 41:魔力回復+5、體力回復+5
    // 42:傷害減免+5
    // 43:體力上限+100、魔力上限+100
    // 45:魔力增加的咒文纏繞武器。(大地屏障圖示) 可併存
    // 46:魔力增加的咒文纏繞武器。(大地屏障圖示) 可併存
    // 47:大地屏障 可併存
    // 49:沉睡之霧 可併存
    // 50:近距離物理傷害 +58%
    // 51~60: 0~9階 龍印魔石 (鬥士)
    // 61~70: 0~9階 龍印魔石 (弓手)
    // 71~80: 0~9階 龍印魔石 (賢者)
    // 81~90: 0~9階 龍印魔石 (衝鋒)
    // 91:經驗值獎勵+20%(龍頭圖示)
    // 92:藥水霜化術
    // 93:接受初心者小幫手的幫助，恢復力提升。
    // 94:感受到中秋月兔的力量，體力上限+30、魔力上限+15。
    // 95:敢受魔法的力量 MP+30、魔力恢復+4、魔法攻擊+1
    // 96:感受到月餅的效果，魔力上限+80。
    // 97:感受到牛肉月餅的效果，體力上限+120。
    // 98:感受到神聖的力量。
    // 99:丹特斯的氣息：近距離、遠距離傷害+2、魔攻+1，魔力恢復量+2。
    // 100: 狩獵的經驗值將會增加。
    // 101: 黑蛇的祝福：體力上限+20，魔力上限+13，防禦-2，傷害減免+3，黑暗耐性+10。
    // 102: 靈魂昇華
    // 103: 生命之泉
    // 104: 鋼鐵防護
    // 105: 增加體力上限、魔力上限、防禦力提升。
    // 106: 勇猛意志
    // 107: 灼熱武器
    // 108: [日本] EXP+5%, 減少傷害+5
    // 109: 體力上限+25、魔力上限+20、傷害減免+2。
    // 110: 等級提升獎勵: EXP +123%
    // 111: dummy3400 (很帥的圖示)
    // 112: (迴避提升圖示) 遠距離攻擊+2、近距離攻擊+2、魔法攻擊+1。
    // 113: 體力上限+50、魔力上限+30、魔法防禦+10、傷害減少+3。
    // 114: 龍語: 因為龍之血痕無法入場
    // 115: 狩獵時取得經驗值些微增加並且有近距離、遠距離、魔法命中率+3的效果。
    // 116: 料理：可提升攻擊力，防禦力，恢復值。
    // 117: 慎重藥水：魔法攻擊+2、魔力恢復+2。福利慎重藥水： 魔法攻擊+2、魔力恢復+4。
    // 118~120: 王者加護圖示
    // 121: 降低負重
    // 143: 全身都能感受到死亡騎士的憤怒
    // 144: 無法感受到死亡騎士的憤怒
    // 187: 在夢中不會受到死亡逞罰(日出之國圖示)
    // 235: 神聖的丸子圖示
    // 244: 巧克力
    // 254: 神秘的力量從內心深處湧出
    /**
     * 3.8 地圖倒數計時器
     */
    public static final int MAP_TIMER = 153;
    /**
     * 圖示同147 H(time)
     */
    public static final int ICON_OTHER3 = 154;
    /**
     * 左上角顯示：回合0/6.
     */
    public static final int ROUND_NUMBER = 156;
    /**
     * 3.8 地圖剩餘時間
     */
    public static final int MAP_TIME = 159;
    public static final int WEAPON_RANGE = 160;
    // 160 正服出現在商店變身..用處不明?
    // 0000: fa a0 01 39 00 02 30 27
    // 161 中毒圖示 1綠毒 2麻痺
    public static final int POISON_ICON = 161;
    // 165 ICON 經驗值20%
    public static final int EXPBOUNS = 165;
    /**
     * 3.8 血盟查詢盟友 (顯示公告)
     */
    public static final int HTML_PLEDGE_ANNOUNCE = 167;
    /**
     * 3.8 血盟查詢盟友 (寫入公告)
     */
    public static final int HTML_PLEDGE_REALEASE_ANNOUNCE = 168;
    /**
     * 3.8 血盟查詢盟友 (寫入備註)
     */
    public static final int HTML_PLEDGE_WRITE_NOTES = 169;
    /**
     * 3.8 血盟查詢盟友 (顯示盟友)
     */
    public static final int HTML_PLEDGE_MEMBERS = 170;
    /**
     * 3.8 血盟查詢盟友 (顯示上線盟友)
     */
    public static final int HTML_PLEDGE_ONLINE_MEMBERS = 171;
    /**
     * 3.8 血盟 識別盟徽狀態
     */
    public static final int PLEDGE_EMBLEM_STATUS = 173;
    /**
     * 3.8 村莊便利傳送
     */
    public static final int TOWN_TELEPORT = 176;
    // 龍語 179
    public static final int DRAGON_STUS = 179;
    public static final int TOMAHAWK = 180;
    public static final int NONE_TIME_ICON = 180;
    // 變身特化
    public static final int SPECIAL_POLY = 185;
    // 護甲身軀
    public static final int ARMOR_BODY = 186;
    /**
     * 精靈的祝賀禮物
     */
    public static final int GIFT_512 = 188;
    // 裝備切換
    public static final int INVENTORY_SAVE = 189;
    // 紅騎士軍團力量
    public static final int RED_KNIGHT_EFFECT = 191;
    public static final int USER_BACK_STAB = 193;
    // 左上角顯示時間 開始
    public static final int LEFT_TIMER_BEG = 195;
    // 左上角顯示時間 結束
    public static final int LEFT_TIMER_END = 196;
    /**
     * 噬魂塔下層開始
     */
    public static final int SOULTOWERSTART = 195;
    // public static final int TRUETARGET=194;
    /**
     * 噬魂塔下層結束
     */
    public static final int SOULTOWEREND = 196;
    /**
     * 8.1連擊系統
     */
    public static final int ICON_COMBO_BUFF = 204;
    private byte[] _byte = null;

    public S_PacketBox(int subCode, L1PcInstance pc) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case TOWN_TELEPORT:
                writeC(0x01);
                writeH(pc.getX());
                writeH(pc.getY());
                break;
            case USER_BACK_STAB: // 193
                // 原地順移並更新Client端畫面
                pc.setTeleportX(pc.getX());
                pc.setTeleportY(pc.getY());
                pc.setTeleportMapId(pc.getMapId());
                pc.setTeleportHeading(5);
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 169));
                pc.sendPackets(new S_Teleport(pc));
                pc.sendPackets(new S_ServerMessage("\\fR解除人物錯位！"));
                break;
        }
    }

    public S_PacketBox(int subCode, int value) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case ICON_COMBO_BUFF: // 8.1連擊系統
                writeH(value);
                break;
            case 74:
            case 75:
                int time = value >> 2;
                writeC(time);
                break;
            case SOULTOWERSTART:// 噬魂塔下層開始
                writeD(value); // time
                writeH(0x34dc); // 定值不用改
                writeH(0x00);
                break;
            case SOULTOWEREND:// 噬魂塔下層結束
                writeD(0);
                writeH(value);// end time
                writeH(0x00);
                break;
            case 34:
            case 35:
            case 36:
            case ICON_I2H:
            case MAP_TIMER:
                writeH(value);
                break;
            case 57:
                writeC(44);
                writeH(value);
                break;
            case 6:
            case 10:
            case 11:
            case ICON_SOUL_GUARDIAN:
                writeC(value);
                break;
            case ICONS2: // 狀態圖示
                writeC(0x00);
                writeC(0x00);
                writeC(0x00);
                writeC(value); // 閃避圖示 (幻術:鏡像、黑妖:闇影閃避)
                break;
            case 15:
            case 49:
            case MSG_BOOKMARK_SPACE: // 日版記憶座標
                writeC(value);
                break;
            case PLEDGE_EMBLEM_STATUS:
                writeC(1);
                if (value == 0) { // 0:關閉 1:開啟
                    writeC(0);
                } else if (value == 1) {
                    writeC(1);
                }
                writeD(0x00);
                break;
            case DOLL:
            case UPDATE_ER:
                writeH(value);
                break;
            case GUI_VISUAL_EFFECT:
                writeC(value);
                break;
            case HTML_MASTER:
                ArrayList<L1PcInstance> list = L1Master.getInstance().getDiscipleList(value);
                if (list != null) {
                    writeC(list.size()); // 弟子數量
                    for (L1PcInstance disciple : list) {
                        writeS(disciple.getName());// disciple name
                        writeC(disciple.getLevel());// lv
                        writeC(disciple.getType());// getType
                    }
                }
                // master name
                writeS(L1Master.getInstance().getMasterName(value));
                break;
        }
    }

    public S_PacketBox(int subCode, int type, int time) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case ROUND_NUMBER:
                writeD(type);
                writeD(time);
                writeH(0);
                break;
            case MSG_DUEL:
                writeD(type);
                writeD(time);
                break;
            case ICON_BLOOD_STAIN:
                writeC(type);// 82:安塔瑞斯的血痕。 85:法利昂的血痕。 88:林德拜爾的血痕。 91:巴拉卡斯的血痕。
                writeH(time);
                break;
            case ICON_NO_TIMER:
                writeC(type);// 是否顯示0/1
                writeC(time);// 圖示編號
                break;
            case ICON_OTHER3:
                writeH(type);// 時間
                writeC(time);// 圖示編號
                break;
		/*case ICON_NO_TIMER:
			   writeC(time); // 0:不顯示 1:顯示
			   writeC(type);
	    break;*/
            case NONE_TIME_ICON: // 常駐型
                writeC(type);
                writeD(time);
                writeD(0x00000D67);
                writeH(0x00);
                break;
        }
    }

    public S_PacketBox(L1PcInstance master, ArrayList<L1PcInstance> totalList) {
        writeC(S_EVENT);
        writeC(HTML_MASTER);
        writeC(totalList.size());
        for (L1PcInstance l1Char : totalList) {
            writeS(l1Char.getName());
            writeC(l1Char.getLevel());
            writeC(l1Char.getType());
        }
        writeS(master.getName());
        writeC(master.getLevel());
        writeC(master.getType());
    }

    public S_PacketBox(int subCode, String name) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case MSG_COLOR_MESSAGE:
                writeC(2);
                writeS(name);
                break;
            //case ADD_EXCLUDE:
            //case REM_EXCLUDE:
            case MSG_TOWN_LEADER:
                writeS(name);
                break;
            case HTML_PLEDGE_REALEASE_ANNOUNCE:
                writeS(name);
                break;
        }
    }

    public S_PacketBox(int subCode, String name, int type) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case MSG_COLOR_MESSAGE:
                writeC(type);
                writeS(name);
                break;
            case MINIGAME_GAMESTART:
			/*writeC(5);
			writeC(129);
			writeC(252);
			writeC(125);
			writeC(110);
			writeC(17);*/
                break;
            case MINIGAME_PLAYERINFO:
                this.writeC(1);// 顯示筆數
                this.writeC(0x00);//
                this.writeC(0x00);//
                this.writeC(0x00);//
                this.writeS(name);
                break;
        }
    }
    /*
     * public S_PacketBox(int subCode, int rank, String name) {
     * writeC(S_OPCODE_PACKETBOX); writeC(subCode);
     *
     * switch (subCode) { case MSG_RANK_CHANGED: // 你的階級變更為%s writeC(rank);
     * writeS(name); break; }
     *
     * }
     */

    /**
     * 顯示限時地圖左上角計時器剩餘時間[單位:秒].
     */
    public S_PacketBox(final int subCode, final int value, final String msg) {
        this.writeC(S_EVENT);
        this.writeC(subCode);
        switch (subCode) {
            case MAP_TIMER:
                this.writeD(value);
                break;
            case MSG_COLOR_MESSAGE:
                writeC(value);
                writeS(msg);
                break;
            case MSG_RANK_CHANGED:
                writeC(value);
                writeS(msg);
                break;
            default:
                switch (subCode) {
                    case ADD_EXCLUDE:
                    case REM_EXCLUDE:
                        writeS(msg);
                        writeC(value);
                        break;
                }
                //_log.error("未知PACKETBOX的TYPE:" + subCode);
                break;
        }
    }

    public S_PacketBox(int subCode, String[] names, int type) {
        writeC(S_EVENT);
        writeC(subCode);
        writeC(0);
        switch (subCode) {
            case SHOW_LIST_EXCLUDE:
                writeC(type);
                writeC(names.length);
                for (String name : names) {
                    writeS(name);
                }
                writeH(0);
                break;
        }
    }

    public S_PacketBox(int subCode, Object[] names) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
		/*case SHOW_LIST_EXCLUDE:
			writeC(names.length);
			for (Object name : names) {
				writeS(name.toString());
			}
			break;*/
            case HTML_PLEDGE_ONLINE_MEMBERS:
                writeH(names.length);
                for (Object name : names) {
                    L1PcInstance pc = (L1PcInstance) name;
                    writeS(pc.getName());
                    writeC(0); // 3.80 新增
                }
                break;
            default:
                break;
        }
    }

    public S_PacketBox(final int subCode) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case 32:
                writeC(16);
                writeD(0);
                writeD(0);
                writeD(0);
                writeC(0);
                writeC(32);
                writeH(0);
                break;
            // case MSG_WAR_INITIATIVE:
            // case MSG_WAR_OCCUPY:
            case MSG_MARRIED:
            case MSG_FEEL_GOOD:
            case MSG_CANT_LOGOUT:
            case LOGOUT:
            case FISHING:
            case ICON_SECURITY_SERVICES:
                break;
            case INVENTORY_SAVE: // 裝備切換
                writeD(0x0d);
                break;
            default:
                break;
        }
    }

    public S_PacketBox(final int subCode, final int range, final int type, final boolean check) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case WEAPON_RANGE:
                writeC(range);
                writeC(type);
                writeC(check ? 1 : 0);
                writeH(0);
                break;
        }
    }

    public S_PacketBox(final int subCode, final int value, final boolean show) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case TOMAHAWK:
                writeC(show ? 0x01 : 0x00); // On Off
                writeD(value);
                break;
        }
    }

    public S_PacketBox(L1PcInstance pc, int subCode) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case PLEDGE_UNION:
                StringBuffer sb = new StringBuffer();
                for (int i : pc.getClan().Alliance()) {
                    if (i == 0) {
                        continue;
                    }
                    L1Clan c = WorldClan.get().getClan(i);
                    if (c == null) {
                        continue;
                    }
                    sb.append(c.getClanName() + " ");
                }
                writeS(sb.toString());
                break;
            default:
                break;
        }
    }

    public S_PacketBox(int subCode, L1ItemInstance item, int type) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case ITEM_STATUS:
                writeD(item.getId());
                writeH(type);
                break;
        }
    }

    /**
     * 升級經驗獎勵狀態
     *
     */
    public S_PacketBox(int time, boolean ck, boolean ck2) {
        writeC(S_EVENT);
        writeC(0x56);
        writeC(0xAA);
        writeC(0x01);
        writeH(time / 16);
        writeH(0x00);
    }

    /**
     * 鎧甲護持
     *
     */
    public S_PacketBox(int subCode, int time, int gfxid, int type) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case ICON_OTHER3:
                writeH(time); // 時間
                writeD(gfxid); // 圖標
                writeC(type); // 類型
                writeC(0x00);
                break;
        }
    }

    /**
     * 精靈的祝賀禮物
     *
     */
    public S_PacketBox(final int subCode, final byte[] b) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case GIFT_512:
                writeByte(b);
                break;
        }
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
