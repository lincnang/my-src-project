package com.lineage.server.person;

import com.google.protobuf.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * 樂天堂火神製作(DB化)
 */
public final class ItemCraft_Type {
    private static Descriptors.FileDescriptor descriptor;
    private static final Descriptors.Descriptor SendCraftType_descriptor = getDescriptor().getMessageTypes().get(25);
    private static final GeneratedMessageV3.FieldAccessorTable SendCraftType_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendCraftType_descriptor, new String[]{"Type", "TimeIndex"});

    static {
        String[] descriptorData = {"\n\fPerson.proto\022\021l1j.server.server\"¢\001\n\021sendEquipmentInfo\022\f\n\004type\030\001 \002(\005\022<\n\004item\030\002 \003(\0132..l1j.server.server.sendEquipmentInfo.Equipment\022\n\n\002un\030\003 \002(\005\022\013\n\003un1\030\004 \002(\005\032(\n\tEquipment\022\f\n\004eqid\030\001 \002(\005\022\r\n\005objid\030\002 \003(\005\"\002\n\023sendSoulTowerRanked\022K\n\007newRank\030\001 \003(\0132:.l1j.server.server.sendSoulTowerRanked.soulTowerRankedInfo\022K\n\007oldRank\030\002 \003(\0132:.l1j.server.server.sendSoulTowerRanked.soulTowerRankedInfo\032n\n\023soulTowerRankedInfo\022", "\f\n\004name\030\001 \002(\f\022\020\n\busertype\030\002 \002(\005\022\017\n\007maptime\030\003 \002(\005\022\016\n\006gotime\030\004 \002(\005\022\026\n\016soulTowerClass\030\005 \002(\005\"³\002\n\fsendTaskInfo\022\f\n\004type\030\001 \002(\005\022\r\n\005type1\030\002 \002(\005\0226\n\004info\030\003 \003(\0132(.l1j.server.server.sendTaskInfo.TaskInfo\032Í\001\n\bTaskInfo\022\016\n\006taskid\030\001 \002(\005\022\013\n\003url\030\003 \002(\f\022\r\n\005title\030\004 \001(\f\022\021\n\tstarttime\030\005 \001(\005\022\017\n\007endtime\030\006 \001(\005\022?\n\004boss\030\007 \001(\01321.l1j.server.server.sendTaskInfo.TaskInfo.bossInfo\0320\n\bbossInfo\022\020\n\bstringid\030\001 \002(\f\022\022\n\nteleportId\030\002 \002(\005\"í", "\001\n\022sendClanMemberInfo\022\f\n\004type\030\001 \002(\005\022D\n\nmemberInfo\030\002 \003(\01320.l1j.server.server.sendClanMemberInfo.MemberInfo\032\001\n\nMemberInfo\022\r\n\005objid\030\001 \002(\005\022\020\n\bclanName\030\002 \002(\f\022\020\n\bobjectId\030\003 \002(\005\022\020\n\bcharName\030\004 \002(\f\022\f\n\004note\030\005 \002(\f\022\020\n\bisonline\030\006 \002(\005\022\017\n\007jobtype\030\007 \002(\005\"\001\n\023sendMonsterKillInfo\022\f\n\004type\030\001 \002(\005\022\017\n\007unknow2\030\002 \002(\005\022=\n\004info\030\003 \003(\0132/.l1j.server.server.sendMonsterKillInfo.KillInfo\032&\n\bKillInfo\022\013\n\003num\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\"¶\001\n", "\017sendMonsterList\022\017\n\007unknow1\030\001 \002(\005\022\017\n\007unknow2\030\002 \002(\005\022<\n\004info\030\003 \003(\0132..l1j.server.server.sendMonsterList.MonsterInfo\032C\n\013MonsterInfo\022\020\n\bquestNum\030\001 \002(\005\022\023\n\013monsterTime\030\002 \002(\005\022\r\n\005value\030\003 \002(\005\"2\n\fsendClanInfo\022\020\n\bclanName\030\001 \002(\f\022\020\n\bclanRank\030\002 \002(\005\"¨\002\n\023sendTelePortMapInfo\022\020\n\bnpcObjId\030\001 \002(\005\022D\n\004info\030\002 \003(\01326.l1j.server.server.sendTelePortMapInfo.TelePortMapInfo\032¸\001\n\017TelePortMapInfo\022\024\n\ftelePortName\030\001 \002(\f\022O\n\007mapInfo\030\002", " \001(\0132>.l1j.server.server.sendTelePortMapInfo.TelePortMapInfo.MapInfo\032>\n\007MapInfo\022\017\n\007mapType\030\001 \002(\005\022\021\n\tteleMoney\030\002 \002(\005\022\017\n\007unknown\030\003 \002(\005\"\001\n\rsendBuddyList\022\f\n\004type\030\001 \002(\005\022=\n\tbuddyList\030\002 \003(\0132*.l1j.server.server.sendBuddyList.buddyInfo\032<\n\tbuddyInfo\022\f\n\004name\030\001 \002(\f\022\020\n\bisOnline\030\002 \002(\005\022\017\n\007unknown\030\003 \002(\f\";\n\fsendFishTime\022\f\n\004type\030\001 \002(\005\022\f\n\004time\030\002 \001(\005\022\017\n\007isQuick\030\003 \001(\005\"\002\n\013sendTAMPage\0226\n\004user\030\001 \003(\0132(.l1j.server.server", ".sendTAMPage.UserModel\022\020\n\bunknown1\030\002 \002(\005\022\020\n\bunknown2\030\003 \002(\005\022\020\n\bunknown3\030\004 \002(\005\032\001\n\tUserModel\022\020\n\bserverNo\030\001 \002(\005\022\r\n\005objid\030\002 \002(\005\022\f\n\004time\030\003 \002(\005\022\017\n\007tamwait\030\004 \002(\005\022\f\n\004name\030\005 \002(\f\022\r\n\005level\030\006 \002(\005\022\021\n\ttypeClass\030\007 \002(\005\022\013\n\003sex\030\b \002(\005\"\007\n\016sendAttrReward\0227\n\003str\030\001 \002(\0132*.l1j.server.server.sendAttrReward.strModel\0229\n\005intel\030\002 \002(\0132*.l1j.server.server.sendAttrReward.intModel\0227\n\003wis\030\003 \002(\0132*.l1j.server.server.sendAttrReward.w", "isModel\0227\n\003dex\030\004 \002(\0132*.l1j.server.server.sendAttrReward.dexModel\0227\n\003con\030\005 \002(\0132*.l1j.server.server.sendAttrReward.conModel\0227\n\003cha\030\006 \002(\0132*.l1j.server.server.sendAttrReward.chaModel\032R\n\bstrModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003dmg\030\002 \001(\005\022\013\n\003hit\030\003 \001(\005\022\032\n\022criticalStrikeRate\030\004 \001(\005\032a\n\bintModel\022\020\n\battValue\030\001 \001(\005\022\020\n\bmagicDmg\030\002 \001(\005\022\020\n\bmagicHit\030\003 \001(\005\022\037\n\027magicCriticalStrikeRate\030\004 \001(\005\032\001\n\bwisModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003mpr\030", "\002 \001(\005\022\023\n\013MPCureBonus\030\003 \001(\005\022\n\n\002mr\030\004 \001(\005\022\024\n\flvUpAddMinMp\030\005 \001(\005\022\024\n\flvUpAddMaxMp\030\006 \001(\005\022\r\n\005mpMax\030\007 \001(\005\032[\n\bdexModel\022\020\n\battValue\030\001 \001(\005\022\016\n\006bowDmg\030\002 \001(\005\022\016\n\006bowHit\030\003 \001(\005\022\035\n\025bowcriticalStrikeRate\030\004 \001(\005\032s\n\bconModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003hpr\030\002 \001(\005\022\023\n\013HPCureBonus\030\003 \001(\005\022\016\n\006weight\030\004 \001(\005\022\024\n\flvUpAddMaxHp\030\005 \001(\005\022\r\n\005hpMax\030\006 \001(\005\032\034\n\bchaModel\022\020\n\battValue\030\001 \001(\005\"b\n\020sendUserBaseAttr\022\013\n\003str\030\001 \002(\005\022\r\n\005intel\030\002 \002(\005\022\013\n\003wis\030\003 \002(\005\022\013", "\n\003dex\030\004 \002(\005\022\013\n\003con\030\005 \002(\005\022\013\n\003cha\030\006 \002(\005\"A\n\013readBanInfo\022\023\n\013excludeType\030\001 \002(\005\022\017\n\007subType\030\002 \002(\005\022\f\n\004name\030\003 \003(\f\"\026\n\007readSha\022\013\n\003sha\030\001 \002(\f\"D\n\020readCustomPacket\022\016\n\006custom\030\001 \001(\005\022\017\n\007custom1\030\002 \001(\005\022\017\n\007custom2\030\003 \001(\005\"Y\n\021readCustomPacket1\022\016\n\006custom\030\001 \001(\005\0224\n\007custom1\030\002 \001(\0132#.l1j.server.server.readCustomPacket\"c\n\020sendCustomPacket\022\f\n\004type\030\001 \001(\005\022\016\n\006custom\030\002 \001(\005\022\017\n\007custom1\030\003 \001(\005\022\017\n\007custom2\030\004 \001(\005\022\017\n\007custom3\030\005 \001(\005\"H\n\021sendCu", "stomPacket1\0223\n\006custom\030\001 \001(\0132#.l1j.server.server.sendCustomPacket\"Z\n\016sendClanConfig\022\f\n\004type\030\001 \002(\005\022\025\n\rjoinOpenState\030\002 \002(\005\022\021\n\tjoinState\030\003 \002(\005\022\020\n\bpassword\030\004 \002(\t\"È\001\n\017sendCustomSkill\022\021\n\tskillType\030\001 \002(\005\022\017\n\007skillId\030\002 \002(\005\022\f\n\004time\030\003 \002(\005\022\020\n\btimetype\030\004 \002(\005\022\f\n\004icon\030\005 \002(\005\022\020\n\bunknown1\030\006 \002(\005\022\013\n\003seq\030\007 \002(\005\022\020\n\bskillMsg\030\b \002(\005\022\020\n\bstartMsg\030\t \002(\005\022\016\n\006endMsg\030\n \002(\005\022\020\n\bunknown2\030\013 \002(\005\"B\n\021sendCastleWarTime\022\r\n\005isatt\030\001 \002(\005\022\f\n\004t", "ime\030\002 \002(\005\022\020\n\bclanName\030\003 \001(\f\"\001\n\021sendCastleTaxRate\022\020\n\bcastleId\030\001 \002(\005\022\020\n\bclanName\030\002 \002(\f\022\022\n\nleaderName\030\003 \002(\f\022\020\n\bunknown1\030\004 \002(\005\022\020\n\bunknown2\030\005 \002(\005\022\020\n\bunknown3\030\006 \002(\005\022\023\n\013publicMoney\030\007 \002(\005\"¶\001\n\025sendCraftNpcCraftList\022\020\n\bunknown1\030\001 \002(\005\022;\n\004list\030\002 \003(\0132-.l1j.server.server.sendCraftNpcCraftList.List\022\020\n\bunknown2\030\003 \002(\005\032<\n\004List\022\020\n\bactionId\030\001 \002(\005\022\020\n\bunknown1\030\002 \002(\005\022\020\n\bunknown2\030\003 \002(\005\"\037\n\016readCraftNpcId\022\r\n\005npcid\030\001 \002(\005\"0", "\n\rSendCraftType\022\f\n\004type\030\001 \002(\005\022\021\n\ttimeIndex\030\002 \002(\005\"Y\n\fReadUserChat\022\021\n\tchatIndex\030\001 \002(\005\022\020\n\bchatType\030\002 \002(\005\022\020\n\bchatText\030\003 \002(\f\022\022\n\ntargetName\030\005 \001(\f\"­\001\n\007MapInfo\022\r\n\005mapid\030\001 \002(\005\022\020\n\bserverId\030\002 \002(\005\022\024\n\fisUnderwater\030\003 \002(\005\022\020\n\bunknown1\030\004 \002(\005\022\020\n\bunknown2\030\005 \002(\005\022\020\n\bunknown3\030\006 \002(\005\0225\n\bunknown4\030\007 \001(\0132#.l1j.server.server.sendCustomPacket\"\b\n\027CharAbilityDetailedInfo\022\f\n\004type\030\001 \002(\005\022G\n\tstrPacket\030\002 \001(\01324.l1j.server.server.Cha", "rAbilityDetailedInfo.StrPacket\022G\n\tintPacket\030\003 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.IntPacket\022G\n\twisPacket\030\004 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.WisPacket\022G\n\tdexPacket\030\005 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.DexPacket\022G\n\tconPacket\030\006 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.ConPacket\022G\n\tchaPacket\030\007 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.ChaPacket\032Q\n\tS", "trPacket\022\013\n\003dmg\030\001 \002(\005\022\013\n\003hit\030\002 \002(\005\022\032\n\022criticalStrikeRate\030\003 \002(\005\022\016\n\006weight\030\004 \002(\005\032b\n\tDexPacket\022\016\n\006bowDmg\030\001 \002(\005\022\016\n\006bowHit\030\002 \002(\005\022\035\n\025bowcriticalStrikeRate\030\003 \002(\005\022\n\n\002ac\030\004 \002(\005\022\n\n\002er\030\005 \002(\005\032}\n\tIntPacket\022\020\n\bmagicDmg\030\001 \002(\005\022\020\n\bmagicHit\030\002 \002(\005\022\037\n\027magicCriticalStrikeRate\030\003 \002(\005\022\022\n\nmagicBonus\030\004 \002(\005\022\027\n\017MPConsumeReduce\030\005 \002(\005\032i\n\tConPacket\022\013\n\003hpr\030\001 \002(\005\022\023\n\013HPCureBonus\030\002 \002(\005\022\016\n\006weight\030\003 \002(\005\022\024\n\flvUpAddMinHp\030\004 \002(\005\022\024\n\flvUpAd", "dMaxHp\030\005 \002(\005\032e\n\tWisPacket\022\013\n\003mpr\030\001 \002(\005\022\023\n\013MPCureBonus\030\002 \002(\005\022\n\n\002mr\030\003 \002(\005\022\024\n\flvUpAddMinMp\030\004 \002(\005\022\024\n\flvUpAddMaxMp\030\005 \002(\005\032\034\n\tChaPacket\022\017\n\007unknown\030\001 \002(\005\"µ\001\n\023ReadAddCharAbillity\022\r\n\005level\030\001 \002(\005\022\021\n\tclassType\030\002 \002(\005\022\f\n\004type\030\003 \002(\005\022\f\n\004mode\030\004 \001(\005\022\020\n\bstrOrCon\030\005 \001(\005\022\013\n\003str\030\006 \001(\005\022\r\n\005intel\030\007 \001(\005\022\013\n\003wis\030\b \001(\005\022\013\n\003dex\030\t \001(\005\022\013\n\003con\030\n \001(\005\022\013\n\003cha\030\013 \001(\005\"3\n\017DollComposeItem\022\021\n\titemObjId\030\001 \002(\005\022\r\n\005gfxid\030\002 \002(\005\"Ü\001\n\020LotteryInvent", "ory\022\f\n\004type\030\001 \002(\005\022\r\n\005type1\030\002 \002(\005\022F\n\004item\030\003 \003(\01328.l1j.server.server.LotteryInventory.LotteryInventoryItem\032c\n\024LotteryInventoryItem\022\016\n\006descid\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\r\n\005gfxid\030\003 \002(\005\022\017\n\007indexId\030\004 \002(\005\022\f\n\004name\030\005 \002(\f\"3\n\024readLotteryInventory\022\f\n\004type\030\001 \002(\005\022\r\n\005index\030\002 \003(\005\"ì\001\n\rItemCraftRead\022\020\n\bnpcObjId\030\001 \002(\005\022\020\n\bactionId\030\002 \002(\005\022\r\n\005count\030\003 \002(\005\022<\n\bitemlist\030\004 \003(\0132*.l1j.server.server.ItemCraftRead.CraftItem\022\021\n\tcrefCou", "nt\030\005 \001(\005\032W\n\tCraftItem\022\r\n\005index\030\001 \002(\005\022\016\n\006descid\030\002 \002(\005\022\021\n\titemcount\030\003 \001(\005\022\030\n\020itemEnchantLevel\030\004 \001(\005\"Ð\004\n\fPCAndNpcPack\022\013\n\003loc\030\001 \002(\004\022\n\n\002id\030\002 \002(\005\022\r\n\005gfxId\030\003 \002(\005\022\016\n\006status\030\004 \002(\005\022\017\n\007heading\030\005 \002(\005\022\024\n\fownLightSize\030\006 \002(\005\022\021\n\tlightSize\030\007 \002(\005\022\016\n\006lawful\030\b \002(\005\022\f\n\004name\030\t \002(\f\022\r\n\005title\030\n \002(\f\022\021\n\tmoveSpeed\030\013 \002(\005\022\022\n\nbraveSpeed\030\f \002(\005\022\024\n\fisThreeSpeed\030\r \002(\005\022\017\n\007isGhost\030\016 \002(\005\022\023\n\013isParalyzed\030\017 \002(\005\022\020\n\bviewName\030\020 \002(\005\022\017\n\007isInvi", "s\030\021 \002(\005\022\016\n\006posion\030\022 \002(\005\022\016\n\006clanId\030\023 \002(\005\022\020\n\bclanName\030\024 \002(\f\022\016\n\006master\030\025 \002(\f\022\r\n\005state\030\026 \002(\005\022\017\n\007HPMeter\030\027 \002(\005\022\r\n\005level\030\030 \002(\005\022\030\n\020privateShopTitle\030\031 \001(\f\022\020\n\bunknown7\030\032 \002(\005\022\020\n\bunknown8\030\033 \002(\005\022\020\n\bunknown9\030\034 \002(\005\022\021\n\tunknown10\030\035 \001(\005\022\017\n\007MPMeter\030\036 \002(\005\022\021\n\tunknown11\030\037 \001(\005\022\020\n\bServerNo\030  \001(\005\022\021\n\tunknown14\030\" \001(\005\"Y\n\rSendItemCraft\022\024\n\fisSendPacket\030\001 \002(\005\0222\n\004list\030\002 \003(\0132$.l1j.server.server.SendItemCraftList\"\024\n\021SendItemCraf", "tList\022\020\n\bactionid\030\001 \002(\005\022J\n\tcondition\030\002 \002(\01327.l1j.server.server.SendItemCraftList.CraftItemCondition\022\020\n\bunknown1\030\003 \002(\005\022;\n\005quest\030\004 \002(\0132,.l1j.server.server.SendItemCraftList.unQuest\022<\n\004poly\030\005 \002(\0132..l1j.server.server.SendItemCraftList.PolyModel\022D\n\bunknown2\030\006 \002(\01322.l1j.server.server.SendItemCraftList.unknownModel2\022?\n\bmaterial\030\007 \002(\0132-.l1j.server.server.SendItemCraftList.Material\022B\n\007results\030\b \002(\01321.l1j.s", "erver.server.SendItemCraftList.CraftResults\022\026\n\016craftDelayTime\030\t \002(\005\032Ì\001\n\022CraftItemCondition\022\016\n\006nameId\030\001 \002(\005\022\020\n\bminLevel\030\002 \002(\005\022\020\n\bmaxLevel\030\003 \002(\005\022\020\n\bunknown1\030\004 \002(\005\022\021\n\tminLawful\030\005 \002(\005\022\021\n\tMaxLawful\030\006 \002(\005\022\020\n\bminKarma\030\007 \002(\005\022\020\n\bmaxKarma\030\b \002(\005\022\020\n\bmaxCount\030\t \002(\005\022\024\n\fisShowChance\030\n \001(\005\0322\n\nQuestModel\022\017\n\007questId\030\001 \002(\005\022\023\n\013questStepId\030\002 \002(\005\032m\n\007unQuest\022\020\n\bunknown1\030\001 \002(\005\022\020\n\bunknown2\030\002 \002(\005\022>\n\005quest\030\003 \002(\0132/.l1j.serve", "r.server.SendItemCraftList.QuestModel\0320\n\tPolyModel\022\023\n\013pcountcount\030\001 \002(\005\022\016\n\006polyId\030\002 \003(\005\0321\n\runknownModel1\022\017\n\007unknow1\030\001 \002(\003\022\017\n\007unknow2\030\002 \002(\003\032è\002\n\bMaterial\022M\n\bmaterial\030\001 \003(\0132;.l1j.server.server.SendItemCraftList.Material.MaterialModel\022P\n\013addMaterial\030\002 \003(\0132;.l1j.server.server.SendItemCraftList.Material.MaterialModel\032º\001\n\rMaterialModel\022\022\n\nitemDescId\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\021\n\twindowNum\030\003 \002(\005\022\024\n\fenchantLevel", "\030\004 \002(\005\022\r\n\005bless\030\005 \002(\005\022\f\n\004name\030\006 \002(\f\022\r\n\005gfxId\030\007 \002(\005\022\017\n\007unknow1\030\b \002(\005\022\017\n\007unknow2\030\t \002(\005\022\017\n\007unknow3\030\n \002(\005\032\007\n\013CraftResult\022D\n\bunknown1\030\001 \001(\01322.l1j.server.server.SendItemCraftList.unknownModel1\022D\n\bunknown2\030\002 \001(\01322.l1j.server.server.SendItemCraftList.unknownModel1\022\020\n\bitemSize\030\003 \001(\005\022\020\n\bitemtype\030\004 \001(\005\022Z\n\017singleCraftItem\030\006 \003(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\022Z\n\017randomCraft", "Item\030\005 \003(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\022M\n\tgreatItem\030\007 \001(\0132:.l1j.server.server.SendItemCraftList.CraftResult.GreatItem\032º\002\n\020CraftResultModel\022\022\n\nitemDescId\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\020\n\bunknown1\030\003 \002(\003\022\024\n\fenchantLevel\030\004 \002(\005\022\r\n\005bless\030\005 \002(\005\022\020\n\bunknown2\030\006 \002(\005\022\020\n\bunknown3\030\007 \002(\005\022\f\n\004name\030\b \002(\f\022\020\n\bunknown4\030\t \002(\005\022\020\n\bunknown5\030\n \002(\005\022\r\n\005gfxId\030\013 \002(\005\022\025\n\rsuccedMessage\030\f \002(\f\022\027\n\017itemS", "tatusBytes\030\r \002(\f\022\020\n\bunknown6\030\016 \001(\005\022\020\n\bunknown7\030\017 \001(\005\022\023\n\013isGreatItem\030\020 \001(\005\032\001\n\tGreatItem\022\n\n\002un\030\001 \002(\005\022\013\n\003un1\030\002 \002(\005\022\013\n\003un2\030\003 \002(\005\022O\n\004Item\030\004 \002(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\032¯\001\n\fCraftResults\022E\n\013succeedList\030\001 \002(\01320.l1j.server.server.SendItemCraftList.CraftResult\022B\n\bfailList\030\002 \002(\01320.l1j.server.server.SendItemCraftList.CraftResult\022\024\n\fsuccessRatio\030\003 \002(\005\032¬\001\n\runknownMode", "l2\022\f\n\004type\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022G\n\002un\030\003 \003(\0132;.l1j.server.server.SendItemCraftList.unknownModel2.ItemInfo\0325\n\bItemInfo\022\016\n\006descid\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\n\n\002un\030\003 \002(\005\"ò\005\n\rsendDollCraft\022\024\n\fisSendPacket\030\001 \002(\005\022;\n\bmaterial\030\002 \003(\0132).l1j.server.server.sendDollCraft.ItemList\0229\n\006result\030\003 \003(\0132).l1j.server.server.sendDollCraft.ItemList\0228\n\006config\030\004 \003(\0132(.l1j.server.server.sendDollCraft.Configs\032\001\n\bItemList\022\016\n\006statge", "\030\001 \002(\005\022@\n\004item\030\002 \003(\01322.l1j.server.server.sendDollCraft.ItemList.ItemInfo\0321\n\bItemInfo\022\022\n\nitemDescId\030\001 \002(\005\022\021\n\titemGfxId\030\002 \001(\005\032\003\n\007Configs\022\r\n\005stage\030\001 \002(\005\022E\n\006config\030\002 \003(\01325.l1j.server.server.sendDollCraft.Configs.ColumnConfig\022A\n\007config1\030\003 \003(\01320.l1j.server.server.sendDollCraft.Configs.Config1\032B\n\fColumnConfig\022\021\n\tcolumnNum\030\001 \002(\005\022\020\n\bunknown1\030\002 \002(\005\022\r\n\005stage\030\003 \002(\005\032\001\n\007Config1\022\r\n\005stage\030\001 \003(\005\022\f\n\004type\030\002 \002(\005\022I\n", "\007unknow1\030\003 \001(\01328.l1j.server.server.sendDollCraft.Configs.Config1.Config2\032*\n\007Config2\022\017\n\007unknow1\030\001 \001(\005\022\016\n\006stage1\030\002 \001(\005\"\001\n\021sendDollCraftType\022\f\n\004type\030\001 \002(\005\022?\n\bdollItem\030\002 \002(\0132-.l1j.server.server.sendDollCraftType.DollItem\032(\n\bDollItem\022\r\n\005objid\030\001 \002(\005\022\r\n\005gfxId\030\002 \002(\005\"\001\n\rreadDollCraft\022\r\n\005stage\030\001 \002(\005\0227\n\004item\030\002 \003(\0132).l1j.server.server.readDollCraft.ItemInfo\032<\n\bItemInfo\022\r\n\005index\030\001 \002(\005\022\016\n\006descId\030\002 \002(\005\022\021\n\titem", "ObjId\030\003 \002(\005"};
        Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = root -> {
            ItemCraft_Type.descriptor = root;
            return null;
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        registerAllExtensions(registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    public static abstract interface SendCraftTypeOrBuilder extends MessageOrBuilder {
        public abstract boolean hasType();

        public abstract int getType();

        public abstract boolean hasTimeIndex();

        public abstract int getTimeIndex();
    }

    public static final class SendCraftType extends GeneratedMessageV3 implements ItemCraft_Type.SendCraftTypeOrBuilder {
        public static final int TYPE_FIELD_NUMBER = 1;
        public static final int TIMEINDEX_FIELD_NUMBER = 2;
        @Deprecated
        public static final Parser<SendCraftType> PARSER = new AbstractParser<SendCraftType>() {
            public ItemCraft_Type.SendCraftType parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new ItemCraft_Type.SendCraftType(input, extensionRegistry);
            }
        };
        private static final long serialVersionUID = 0L;
        private static final SendCraftType DEFAULT_INSTANCE = new SendCraftType();
        private int bitField0_;
        private int type_;
        private int timeIndex_;
        private byte memoizedIsInitialized = -1;

        private SendCraftType(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private SendCraftType() {
            this.type_ = 0;
            this.timeIndex_ = 0;
        }

        private SendCraftType(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            @SuppressWarnings("unused") int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default:
                            if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        case 8:
                            this.bitField0_ |= 1;
                            this.type_ = input.readInt32();
                            break;
                        case 16:
                            this.bitField0_ |= 2;
                            this.timeIndex_ = input.readInt32();
                            break;
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (IOException e) {
                throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
            } finally {
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static Descriptors.Descriptor getDescriptor() {
            return ItemCraft_Type.SendCraftType_descriptor;
        }

        public static SendCraftType parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (SendCraftType) PARSER.parseFrom(data);
        }

        public static SendCraftType parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SendCraftType) PARSER.parseFrom(data, extensionRegistry);
        }

        public static SendCraftType parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (SendCraftType) PARSER.parseFrom(data);
        }

        public static SendCraftType parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SendCraftType) PARSER.parseFrom(data, extensionRegistry);
        }

        public static SendCraftType parseFrom(InputStream input) throws IOException {
            return (SendCraftType) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static SendCraftType parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SendCraftType) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static SendCraftType parseDelimitedFrom(InputStream input) throws IOException {
            return (SendCraftType) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static SendCraftType parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SendCraftType) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static SendCraftType parseFrom(CodedInputStream input) throws IOException {
            return (SendCraftType) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static SendCraftType parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SendCraftType) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(SendCraftType prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static SendCraftType getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SendCraftType> parser() {
            return PARSER;
        }

        public UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return ItemCraft_Type.SendCraftType_fieldAccessorTable.ensureFieldAccessorsInitialized(SendCraftType.class, Builder.class);
        }

        public boolean hasType() {
            return (this.bitField0_ & 0x1) == 1;
        }

        public int getType() {
            return this.type_;
        }

        public boolean hasTimeIndex() {
            return (this.bitField0_ & 0x2) == 2;
        }

        public int getTimeIndex() {
            return this.timeIndex_;
        }

        public boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasType()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!hasTimeIndex()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 0x1) == 1) {
                output.writeInt32(1, this.type_);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                output.writeInt32(2, this.timeIndex_);
            }
            this.unknownFields.writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 1) {
                size = size + CodedOutputStream.computeInt32Size(1, this.type_);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                size = size + CodedOutputStream.computeInt32Size(2, this.timeIndex_);
            }
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SendCraftType)) {
                return super.equals(obj);
            }
            SendCraftType other = (SendCraftType) obj;
            boolean result = true;
            result = (result) && (hasType() == other.hasType());
            if (hasType()) {
                result = (result) && (getType() == other.getType());
            }
            result = (result) && (hasTimeIndex() == other.hasTimeIndex());
            if (hasTimeIndex()) {
                result = (result) && (getTimeIndex() == other.getTimeIndex());
            }
            result = (result) && (this.unknownFields.equals(other.unknownFields));
            return result;
        }

        @SuppressWarnings("unchecked")
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = 41;
            hash = 19 * hash + getDescriptorForType().hashCode();
            if (hasType()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + getType();
            }
            if (hasTimeIndex()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + getTimeIndex();
            }
            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder(null) : new Builder(null).mergeFrom(this);
        }

        protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public Parser<SendCraftType> getParserForType() {
            return PARSER;
        }

        public SendCraftType getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Type.SendCraftTypeOrBuilder {
            private int bitField0_;
            private int type_;
            private int timeIndex_;

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Type.SendCraftType_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Type.SendCraftType_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Type.SendCraftType.class, Builder.class);
            }

            private void maybeForceBuilderInitialization() {
            }

            public Builder clear() {
                super.clear();
                this.type_ = 0;
                this.bitField0_ &= -2;
                this.timeIndex_ = 0;
                this.bitField0_ &= -3;
                return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return ItemCraft_Type.SendCraftType_descriptor;
            }

            public ItemCraft_Type.SendCraftType getDefaultInstanceForType() {
                return ItemCraft_Type.SendCraftType.getDefaultInstance();
            }

            public ItemCraft_Type.SendCraftType build() {
                ItemCraft_Type.SendCraftType result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public ItemCraft_Type.SendCraftType buildPartial() {
                ItemCraft_Type.SendCraftType result = new ItemCraft_Type.SendCraftType(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 1) {
                    to_bitField0_ |= 1;
                }
                result.type_ = this.type_;
                if ((from_bitField0_ & 0x2) == 2) {
                    to_bitField0_ |= 2;
                }
                result.timeIndex_ = this.timeIndex_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }

            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            public Builder mergeFrom(Message other) {
                if ((other instanceof ItemCraft_Type.SendCraftType)) {
                    return mergeFrom((ItemCraft_Type.SendCraftType) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(ItemCraft_Type.SendCraftType other) {
                if (other == ItemCraft_Type.SendCraftType.getDefaultInstance()) {
                    return this;
                }
                if (other.hasType()) {
                    setType(other.getType());
                }
                if (other.hasTimeIndex()) {
                    setTimeIndex(other.getTimeIndex());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            public boolean isInitialized() {
                if (!hasType()) {
                    return false;
                }
                if (!hasTimeIndex()) {
                    return false;
                }
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                ItemCraft_Type.SendCraftType parsedMessage = null;
                try {
                    parsedMessage = (ItemCraft_Type.SendCraftType) ItemCraft_Type.SendCraftType.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ItemCraft_Type.SendCraftType) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            public boolean hasType() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getType() {
                return this.type_;
            }

            public Builder setType(int value) {
                this.bitField0_ |= 1;
                this.type_ = value;
                onChanged();
                return this;
            }

            public Builder clearType() {
                this.bitField0_ &= -2;
                this.type_ = 0;
                onChanged();
                return this;
            }

            public boolean hasTimeIndex() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public int getTimeIndex() {
                return this.timeIndex_;
            }

            public Builder setTimeIndex(int value) {
                this.bitField0_ |= 2;
                this.timeIndex_ = value;
                onChanged();
                return this;
            }

            public Builder clearTimeIndex() {
                this.bitField0_ &= -3;
                this.timeIndex_ = 0;
                onChanged();
                return this;
            }

            public Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            public Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }
    }
}
