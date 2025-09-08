package com.lineage.server;

import com.add.*;
import com.add.BigHot.BigHotblingLock;
import com.add.BigHot.T_BigHotbling;
import com.add.Crack.L1CrackStart;
import com.add.MJBookQuestSystem.Loader.MonsterBookCompensateLoader;
import com.add.MJBookQuestSystem.Loader.MonsterBookLoader;
import com.add.MJBookQuestSystem.Templates.WeekQuestDateCalculator;
import com.add.Mobbling.IdLoad;
import com.add.Mobbling.MobblingLock;
import com.add.Mobbling.T_Mobbling;
import com.add.Tsai.*;
import com.add.Tsai.Astrology.Astrology1Table;
import com.add.Tsai.Astrology.AttonAstrologyTable;
import com.add.system.L1BlendTable;
import com.add.system.L1FireSmithCrystalTable;
import com.eric.RandomMobTable;
import com.eric.StartCheckWarTime;
import com.eric.gui.J_Main;
import com.lineage.config.*;
import com.lineage.data.event.MiniGame.MiniSiegeNpcStart;
import com.lineage.data.event.SubItemSet;
import com.lineage.data.event.ice.IceQueenSystem;
import com.lineage.data.item_weapon.proficiency.CharacterWeaponProficiencyTable;
import com.lineage.data.item_weapon.proficiency.WeaponProficiencyTable;
import com.lineage.list.BadNamesList;
import com.lineage.managerUI.Eva;
import com.lineage.server.Controller.*;
import com.lineage.server.Manly.WenYangJiLuTable;
import com.lineage.server.Manly.WenYangTable;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.*;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.datatables.sql.ServerGmCommandTable;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1ItemPower;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1AttackList;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.BraveavatarController;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.thread.*;
import com.lineage.server.timecontroller.*;
import com.lineage.server.timecontroller.event.ranking.RankingHeroTimer;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.world.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.*;

import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GameServer {
    private static final Log _log = LogFactory.getLog(GameServer.class);
    private static GameServer _instance;

    public static GameServer getInstance() {
        if (_instance == null) {
            _instance = new GameServer();
        }
        return _instance;
    }

    /**
     * 獲取網絡文件的大小
     *
     * @param URLName 地址
     */
    public static void getUrlImg(String URLName) throws Exception {
        int HttpResult = 0; // 服務器返回的狀態
        URL url = new URL(URLName); // 創建URL
        URLConnection urlconn = url.openConnection(); // 試圖連接並取得返回狀態碼urlconn.connect();
        HttpURLConnection httpconn = (HttpURLConnection) urlconn;
        HttpResult = httpconn.getResponseCode();
        if (HttpResult != HttpURLConnection.HTTP_OK) { // 不等於HTTP_OK說明連接不成功
            System.out.print("授權失敗，請聯繫台灣JAVA技術老爹 QQ:#### LINE:#####");
            System.exit(0);
        } else {
            System.out.println("授權成功");
        }
    }

    public void initialize() throws Exception {
        PerformanceTimer timer = new PerformanceTimer();
        try {
            _log.info("\n\r--------------------------------------------------\n\r" + "\n\r       外部設置：經驗倍率: " + ConfigRate.RATE_XP + "\n\r       外部設置：正義質倍率: " + ConfigRate.RATE_LA + "\n\r       外部設置：友好度倍率: " + ConfigRate.RATE_KARMA + "\n\r       外部設置：物品掉落倍率: " + ConfigRate.RATE_DROP_ITEMS + "\n\r       外部設置：金幣掉落倍率: " + ConfigRate.RATE_DROP_ADENA + "\n\r       外部設置：廣播等級限制: " + ConfigAlt.GLOBAL_CHAT_LEVEL + "\n\r       外部設置：PK設置: " + (ConfigAlt.ALT_NONPVP ? "允許" : "不允許") + "\n\r       外部設置：最大連線設置: " + Config.MAX_ONLINE_USERS + "\n\r--------------------------------------------------");
            ServerReading.get().load(); //src016
            IdFactory.get().load();
            IdLoad.getInstance();
            CharObjidTable.get().load();
            CharacterConfigReading.get().load();
            // 人物好友紀錄
            BuddyTable.getInstance(); // 7.6
            /* 新手任務 */
            //QuestNewTable.get();
            AccountReading.get().load();
            GeneralThreadPool.get();
            PcOtherThreadPool.get();
            NpcAiThreadPool.get();
            DeAiThreadPool.get();
            L1SystemMessageTable.get().loadSystemMessage();// DB化系統設定
            SystemMessage.getInstance(); // DB化訊息
            ExpTable.get().load();
            SprTable.get().load();
            MapsTable.get().load();
            MapExpTable.get().load();
            MapLevelTable.get().load();
            ItemTimeTable.get().load();
            L1WorldMap.get().load();
            L1GameTimeClock.init();
            NpcTable.get().load();
            NpcScoreTable.get().load();
            CharacterTable.loadAllCharName();
            CharacterTable.clearOnlineStatus();
            CharacterTable.clearSpeedError();// 加速器檢測 逾時錯誤歸0
            CharacterTable.clearBanError();// 定時外掛驗證 逾時錯誤歸0
            CharacterTable.clearInputBanError();// 定時外掛驗證 輸入錯誤歸0
            World.get();
            WorldCrown.get();
            WorldKnight.get();
            WorldElf.get();
            WorldWizard.get();
            WorldDarkelf.get();
            WorldDragonKnight.get();
            WorldIllusionist.get();
            WorldWarrior.get();
            WorldPet.get();
            WorldSummons.get();
            TrapTable.get().load();
            TrapsSpawn.get().load();
            ItemTable.get().load();
            DropTable.get().load();
            DropMapTable.get().load();
            DropItemTable.get().load();
            DropItemEnchantTable.get().load();// 掉落道具強化值
            //            ArrowGfxid.get().load();
            DropMobTable.get(); // 全怪物掉落物品
            SkillsTable.get().load();
            SkillsProbabilityTable.get(); // 負面技能幾率設置DB化
            DragonExp.get().load();//龍之祝福系統 By 台灣JAVA技術老爹
            SkillsItemTable.get().load();
            MobGroupTable.get().load();
            NPCTalkDataTable.get().load();
            NpcActionTable.load();
            SpawnTimeTable.getInstance();
            SpawnTable.get().load();
            PolyTable.get().load();
            ShopTable.get().load();
            ShopCnTable.get().load();
            ShopAutoHpTable.get().load(); // 特殊商店 -> 購買自動喝水補魔道具
            CharRemoveItemReading.get().load(); // 特殊商店 -> 添加刪除物品
            DungeonTable.get().load();
            DungeonRTable.get().load();
            NpcSpawnTable.get().load();
            DwarfForClanReading.get().load();
            // 血盟同盟資料 by terry0412
            ClanAllianceReading.get().load();// 7.6
            ClanReading.get().load();
            ActivityNoticeTable.get().load(); // 活動
            RewardClanSkillsTable.get();
            CharOtherReading1.get().load();
            if (com.lineage.data.event.ClanSkillDBSet.START) {
                com.lineage.server.datatables.lock.ClanReading.get().load();
            }
            ClanEmblemReading.get().load();
            //L1ClanMatching.getInstance().loadClanMatching();// 血盟推薦
            ReincarnationSkill.getInstance(); // 轉生天賦
            MonsterBookLoader.getInstance();
            MonsterBookCompensateLoader.getInstance();
            //技能強化
            SkillEnhanceTable.load();
            GiveBack.load();
            if (ConfigLIN.Week_Quest) {
                WeekQuestDateCalculator.getInstance().run();
            }
            InvSwapTable.getInstance(); // 裝備切換
            /** [原碼] 怪物對戰系統 */
            if (L1Config._2151) {
                MobblingLock.create().load();
                T_Mobbling.getStart();
            }
            /** [原碼] 大樂透系統 */
            if (L1Config._2161) {
                BigHotblingLock.create().load();
                T_BigHotbling.getStart();
            }
            /** [原碼] 指定地圖隨機產生指定怪物 */
            RandomMobTable.getInstance().startRandomMob();
            /** [原碼] 修正攻城逾時不更新 */
            StartCheckWarTime.getInstance();
            /** [原碼] 時空裂痕 */
            //L1CrackTime.getStart();
            L1CrackStart.getInstance();
            /** [源碼] 道具自訂義訊息*/
            william.WilliamItemMessage.getData();
            /** [源碼] 武器防具煉化能力*/
            A_ResolventTable.get().load();
            /** [新增] NPC威望積分設置*/
            NpcHonorTable.get().load();
            Honor.getInstance(); //
            CastleReading.get().load();
            L1CastleLocation.setCastleTaxRate();
            GetBackRestartTable.get().load();
            DoorSpawnTable.get().load();
            WeaponSkillTable.get().load();
            WeaponSkillPowerTable.get().load();
            GetbackTable.loadGetBack();
            PetTypeTable.load();
            PetItemTable.get().load();
            ItemBoxTable.get().load();
            ResolventTable.get().load();
            NpcTeleportTable.get().load();
            NpcTeleportOutTable.get().load(); // 指定地圖指定時間傳走玩家
            NpcChatTable.get().load();
            ArmorSetTable.get().load();
            ItemTeleportTable.get().load();
            ItemPowerUpdateTable.get().load();
            CommandsTable.get().load();
            BeginnerTable.get().load();
            ItemRestrictionsTable.get().load();
            ServerGmCommandTable.get().load();
            SpawnBossReading.get().load();
            HouseReading.get().load();
            IpReading.get().load();
            TownReading.get().load();
            //MailTable.get().load();
            MailReading.get().load();// 7.6信件資料
            AuctionBoardReading.get().load();
            BoardReading.get().load();
            CharBuffReading.get().load();
            CharSkillReading.get().load();
            CharOtherReading.get().load();
            CharacterQuestReading.get().load();
            DollQuestReading.get().load();
            CardQuestReading.get().load();
            BadNamesList.get().load();
            SceneryTable.get().load();
            L1SkillMode.get().load();
            L1AttackList.load();
            L1ItemPower.load();
            // 加載連續魔法減低損傷資料
            L1PcInstance.load();
            CharItemsReading.get().load();
            DwarfReading.get().load();
            DwarfForChaReading.get().load();
            DwarfForElfReading.get().load();
            DollPowerTable.get().load();
            PetReading.get().load();
            CharItemsTimeReading.get().load();
            L1PcOther.load();
            EventTable.get().load();
            if (EventTable.get().size() > 0) {
                EventSpawnTable.get().load();
            }
            QuestMapTable.get().load();
            FurnitureSpawnReading.get().load();
            ItemMsgTable.get().load();
            WeaponPowerTable.get().load();
            FishingTable.get().load();
            CastleWarGiftTable.get().load();
            // CharApprenticeTable.getInstance().load();
            //BoardOrimReading.get().load();
            /** 重置限時地監 */
            L1ResetMapTime.get().ResetTimingMap();
            // 新增爵位系統每日重置邏輯
            for (L1PcInstance pc : World.get().getAllPlayers()) {
                Honor.getInstance().resetHonorIfNotCompleted(pc, false); // 線上角色專用
            }
            // 管理視窗
            switch (Config.UI_MODE) {
                case 0:
                    System.out.println("UI已關閉");
                    break;
                case 1:
                    J_Main.getInstance();
                    break;
                case 2:
                    Eva.getInstance();
                    break;
                default:
                    System.out.println("未知UI模式，已自動關閉介面");
            }
            _log.info("已完成爵位每日任務與地監同步重置。");
            /** 載入等級排行榜資料 */
            RankingHeroTimer.load();
            /** 道具製造DB化 */
            L1BlendTable.getInstance().loadBlendTable();
            /** 火神融煉道具 */
            L1FireSmithCrystalTable.get().load();
            // PC檢查時間軸 by terry0412
            CheckTimeController.getInstance().start();
            // 建立資料 [地圖群組設置資料 (入場時間限制)] by terry0412
            MapsGroupTable.get().load();
            // 載入資料 [地圖入場時間紀錄] by terry0412
            CharMapTimeReading.get().load();
            // 載入資料 [人物物品凹槽資料] by terry0412
            CharItemPowerReading.get().load();
            CharItemBlessReading.get().load();
            CharItemPowerTable.get().load(); // 強化擴充能力
            ItemPowerTable.get().load();
            /** [原碼] 底比斯大戰遊戲 */
            if (ConfigThebes.Mini_Siege) {
                MiniSiegeNpcStart.getInstance();
            }
            ServerTimerController.getInstance(); // 各種時間控制
            _log.info("------------------------------------------------------------");
            _log.info("       潘朵拉系列");
            _log.info("------------------------------------------------------------");
            CharacterGiftTable.getInstance(); // 精靈的祝賀禮物
            /** [原碼] 潘朵拉抽抽樂 */
            //LuckyLotteryTable.getInstance().loadData();
            LuckyLotteryTable.getInstance();
            T_OnlineGiftTable.get();//6.2在線獎勵
            T_RankTable.get();//6.2排行榜封包
            T_GameMallTable.get();//潘朵拉商城
            T_CraftConfigTable.get();
            VipSetsTable.get();//VIP能力資料
            ItemUpgradeTable.getInstance(); // 火神合成系統
            RefineTable.getInstance(); // 火神精煉
            _log.info("------------------------------------------------------------");
            _log.info("       老爹系列");
            _log.info("------------------------------------------------------------");

            /** [新增] 系統_強化智力/精神/體質 設置 */
            com.lineage.server.datatables.IntSettingTable.getInstance();
            com.lineage.server.datatables.WisSettingTable.getInstance();
            com.lineage.server.datatables.ConSettingTable.getInstance();

            // 可選：登入後或定時計畫觸發 reapply 由屬性setter保證動態回收/套用

            ExtraCriticalHitStoneTable.getInstance().load();// 爆擊
            ItemIntegrationTable.get();
            ExtraMeteAbilityTable.getInstance().load();
            ExtraMagicWeaponTable.getInstance().load();// 武器魔法DIY
            CheckItemPowerTable.get(); // 身上持有道具給予能力系統
            ArmorKitPowerTable.getInstance().load();
            ExtraBossWeaponTable.getInstance().load();// boss武器
            ExtraItemStealTable.getInstance().load();// 死亡奪取
            WilliamBuff.load(); // NPC魔法輔助DB化系統
            AbilityOrginal.getInstance(); // 強化擴充能力
            WeaponSoul.getInstance(); // 武器劍靈系統
            BlendTable.getInstance().load();// 載入資料 [物品融合系統(DB自製)] by terry0412
            LimitedReward.getInstance(); // 即時獎勵系統
            William_Online_Reward.getInstance(); // 線上抽獎系統
            MobItemTable.get(); // 特定怪物死亡掉落道具或給予狀態系統
            SuperRuneTable.getInstance().load();// 超能
            ExtraPolyPowerTable.getInstance().load();// 變身附加能力
            ExpMeteUpTable.get().load();// 轉生經驗減少
            ItemHtmlTable.get(); // 自訂道具對話系統
            NpcBoxTable.get().load();// 載入資料 [NPC寶箱資料] by terry0412
            // CharApprenticeTable.getInstance().load();
            ExtraAttrWeaponTable.getInstance().load();
            ItemVIPTable.get(); // VIP道具加值
            // 載入變身增加能力值
            ItemSublimationTable.get();// 昇華系統
            RewardAcTable.get();
            DollHeChengTable.getInstance().load();//娃娃合成調用圖片
            polyHeChengTable.getInstance().load();//變身卡合成調用圖片
            MagicHeChengTable.getInstance().load();//魔法合成調用圖片
            ACardTable.get().load();//卡冊
            CardSetTable.get().load();//組合卡冊
            ServerGcTimePool.get();// 線程工廠設置
            DeathThreadPool.get();// 線程工廠設置
            Day_Signature.get().load();//每日領取
            Day_Signature_New.get().load();//每日領取(新)
            NewEnchantSystem.get().load();//強化武器加成系統
            ArmorEnchantSystem.get().load();//強化防具加成系統
            dollTable.get().load();//娃娃卡冊
            dollSetTable.get().load();//娃娃卡冊
            holyTable.get().load();//聖物
            holySetTable.get().load();//聖物套組
            NewAutoPractice.get().load();//掛機仇人名單
            NewAutoPractice.get().load2();//掛機被殺名單
            WenYangJiLuTable.getInstance().loadWenYangTable();// 紋樣系統
            WenYangTable.getInstance(); // 紋樣系統
            CharacterAdenaTradeReading.get().load();//金幣交易系統
            CharaterTradeReading.get().load();//角色買賣系統
            RevertHpMp.get().load();//地圖回復血魔
            Drop_limit.get(); //掉寶限制 2023 11 11
            Zhufu.getInstance();//祝福化系統
            collectTable.get().load();//收藏
            collectSetTable.get().load();//收藏卡冊
            MonsterEnhanceTable.getInstance().load(); // 怪物強化系統
            JiezEnchant.get().load();//戒指強化加成系統 2023 12 17
            Astrology1Table.get().load();//守護星盤
            AttonAstrologyTable.get().load();  // 阿頓星盤資料
            com.add.Tsai.Astrology.SilianAstrologyTable.get().load(); // 絲莉安星盤資料
            AstrologyQuestReading.get().load();//星盤任務
            //TODO 玩家武器熟練度
            WeaponProficiencyTable.getInstance().loadProficiency();
            CharacterWeaponProficiencyTable.getInstance().load();
            if (SubItemSet.START) {
                CharItemSublimationReading.get().load(true);
            }
            // 物品特殊屬性設定資料表(炫色)
            //ItemSpecialAttributeTable.get().load();
            // 人物物品特殊屬性資料表(炫色)
            //ItemSpecialAttributeCharTable.get().load();
            EquipCollectTable.getInstance().loadEquipCollectTable();// 成就系統
            //*getUrlImg("http://www.ttang1.xyz/list.txt22");//填寫自己的網址文件
            System.out.println("伺服器啟動完畢。");
            System.out.println("此版本由台灣JAVA技術老爹製作");
            System.out.println("聯繫QQ：##### LINE：####");
            System.out.println("此版本為合作專用！！！！");
            System.out.println("請勿作為商業化使用否則後果自負！");
            System.out.println("伺服器啟動完畢。");
            CheckFightTimeController.getInstance().start();
            StartTimer_Server startTimer = new StartTimer_Server();
            startTimer.start();
            StartTimer_Pc pcTimer = new StartTimer_Pc();
            pcTimer.start();
            StartTimer_Npc npcTimer = new StartTimer_Npc();
            npcTimer.start();
            StartTimer_Pet petTimer = new StartTimer_Pet();
            petTimer.start();
            StartTimer_Skill skillTimer = new StartTimer_Skill();
            skillTimer.start();

            // 新增：黑妖暗隱術MP消耗Timer
            new com.lineage.server.timecontroller.pc.BlindHidingMpTimer().start();

            // 活動專用時間軸
            final StartTimer_Event eventTimer = new StartTimer_Event();
            eventTimer.start();
            // 指定時間召怪召物系統
            ComesMonsterController comesMonsterController = ComesMonsterController.getInstance();
            GeneralThreadPool.get().execute(comesMonsterController);
            // 假人
            FakeNpcController fakeNpcController = FakeNpcController.getInstance();
            GeneralThreadPool.get().execute(fakeNpcController);
            Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
            DeNameTable.get().load();
            DeClanTable.get().loadIcon();// 虛擬血盟盟徽
            DeClanTable.get().load();
            DeTitleTable.get().load();
            DeShopChatTable.get().load();
            DeGlobalChatTable.get().load();
            DeShopItemTable.get().load();
            EchoServerTimer.get().start();
            IceQueenSystem.getInstance().load();
            // ValakasRoomSystem.getInstance().load();
            L1DoorInstance.openDoor();
            BraveavatarController braveavatarController = BraveavatarController.getInstance();
            GeneralThreadPool.get().execute(braveavatarController);
            // 成長果實系統(Tam幣)
            TamController tamController = TamController.getInstance();
            GeneralThreadPool.get().scheduleAtFixedRate(tamController, 0, TamController.SLEEP_TIME);
            // 日版釣魚
            GeneralThreadPool.get().schedule(FishingTimeController.getInstance(), 300);
            // System.out.println("test");
            // C_ExportXml e =new C_ExportXml();
            // e.export();
            // NewMapUtil.load("./data/map/");
            // DummyFishingTable.get();// 假人釣魚
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            final String osname = System.getProperties().getProperty("os.name");
            final String username = System.getProperties().getProperty("user.name");
            String ver = "\n\r--------------------------------------------------" + "\n\r       主機位置: " + Config.GAME_SERVER_HOST_NAME + "\n\r       監聽端口: " + Config.GAME_SERVER_PORT + "\n\r       伺服器作業系統: " + osname + "\n\r       伺服器使用者: " + username + "\n\r       使用者名稱資料庫: " + ConfigSQL.DB_URL2_LOGIN + "\n\r       伺服器檔案資料庫: " + ConfigSQL.DB_URL2 + "\n\r       綁定登入器設置: " + Config.LOGINS_TO_AUTOENTICATION + "\n\r--------------------------------------------------" + "\n\r       請勿作為其他商業性用途如發生問題自行請負責" + "\n\r       此研究用模擬器使用上如有問題請洽" + "\n\r       只有此唯一聯絡帳號!!!!!" + "\n\r       『" + Config.SERVERNAME + "』 ：專屬版本" + "\n\r--------------------------------------------------";
            _log.info(ver);
            // 加載管理器
            EventQueue.invokeLater(() -> {
            });
            CmdEcho cmdEcho = new CmdEcho(timer.get());
            cmdEcho.runCmd();
        }
    }
}
