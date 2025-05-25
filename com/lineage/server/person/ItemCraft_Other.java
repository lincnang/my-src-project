package com.lineage.server.person;

import com.google.protobuf.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 樂天堂火神製作(DB化)
 */
public final class ItemCraft_Other {
    private static Descriptors.FileDescriptor descriptor;
    private static final Descriptors.Descriptor SendItemCraft_descriptor = getDescriptor().getMessageTypes().get(35);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraft_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraft_descriptor, new String[]{"IsSendPacket", "List"});
    private static final Descriptors.Descriptor SendItemCraftList_descriptor = getDescriptor().getMessageTypes().get(36);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_descriptor, new String[]{"Actionid", "Condition", "Unknown1", "Quest", "Poly", "Unknown2", "Material", "Results", "CraftDelayTime"});
    private static final Descriptors.Descriptor SendItemCraftList_CraftItemCondition_descriptor = SendItemCraftList_descriptor.getNestedTypes().get(0);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_CraftItemCondition_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_CraftItemCondition_descriptor, new String[]{"NameId", "MinLevel", "MaxLevel", "Unknown1", "MinLawful", "MaxLawful", "MinKarma", "MaxKarma", "MaxCount", "IsShowChance"});
    private static final Descriptors.Descriptor SendItemCraftList_QuestModel_descriptor = SendItemCraftList_descriptor.getNestedTypes().get(1);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_QuestModel_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_QuestModel_descriptor, new String[]{"QuestId", "QuestStepId"});
    private static final Descriptors.Descriptor SendItemCraftList_unQuest_descriptor = SendItemCraftList_descriptor.getNestedTypes().get(2);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_unQuest_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_unQuest_descriptor, new String[]{"Unknown1", "Unknown2", "Quest"});
    private static final Descriptors.Descriptor SendItemCraftList_PolyModel_descriptor = SendItemCraftList_descriptor.getNestedTypes().get(3);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_PolyModel_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_PolyModel_descriptor, new String[]{"Pcountcount", "PolyId"});
    private static final Descriptors.Descriptor SendItemCraftList_unknownModel1_descriptor = SendItemCraftList_descriptor.getNestedTypes().get(4);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_unknownModel1_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_unknownModel1_descriptor, new String[]{"Unknow1", "Unknow2"});
    private static final Descriptors.Descriptor SendItemCraftList_Material_descriptor = SendItemCraftList_descriptor.getNestedTypes().get(5);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_Material_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_Material_descriptor, new String[]{"Material", "AddMaterial"});
    private static final Descriptors.Descriptor SendItemCraftList_Material_MaterialModel_descriptor = SendItemCraftList_Material_descriptor.getNestedTypes().get(0);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_Material_MaterialModel_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_Material_MaterialModel_descriptor, new String[]{"ItemDescId", "Count", "WindowNum", "EnchantLevel", "Bless", "Name", "GfxId", "Unknow1", "Unknow2", "Unknow3"});
    private static final Descriptors.Descriptor SendItemCraftList_CraftResult_descriptor = SendItemCraftList_descriptor.getNestedTypes().get(6);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_CraftResult_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_CraftResult_descriptor, new String[]{"Unknown1", "Unknown2", "ItemSize", "Itemtype", "SingleCraftItem", "RandomCraftItem", "GreatItem"});
    private static final Descriptors.Descriptor SendItemCraftList_CraftResult_CraftResultModel_descriptor = SendItemCraftList_CraftResult_descriptor.getNestedTypes().get(0);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_CraftResult_CraftResultModel_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_CraftResult_CraftResultModel_descriptor, new String[]{"ItemDescId", "Count", "Unknown1", "EnchantLevel", "Bless", "Unknown2", "Unknown3", "Name", "Unknown4", "Unknown5", "GfxId", "SuccedMessage", "ItemStatusBytes", "Unknown6", "Unknown7", "IsGreatItem"});
    private static final Descriptors.Descriptor SendItemCraftList_CraftResult_GreatItem_descriptor = SendItemCraftList_CraftResult_descriptor.getNestedTypes().get(1);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_CraftResult_GreatItem_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_CraftResult_GreatItem_descriptor, new String[]{"Un", "Un1", "Un2", "Item"});
    private static final Descriptors.Descriptor SendItemCraftList_CraftResults_descriptor = SendItemCraftList_descriptor.getNestedTypes().get(7);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_CraftResults_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_CraftResults_descriptor, new String[]{"SucceedList", "FailList", "SuccessRatio"});
    private static final Descriptors.Descriptor SendItemCraftList_unknownModel2_descriptor = SendItemCraftList_descriptor.getNestedTypes().get(8);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_unknownModel2_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_unknownModel2_descriptor, new String[]{"Type", "Count", "Un"});
    private static final Descriptors.Descriptor SendItemCraftList_unknownModel2_ItemInfo_descriptor = SendItemCraftList_unknownModel2_descriptor.getNestedTypes().get(0);
    private static final GeneratedMessageV3.FieldAccessorTable SendItemCraftList_unknownModel2_ItemInfo_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(SendItemCraftList_unknownModel2_ItemInfo_descriptor, new String[]{"Descid", "Count", "Un"});

    static {
        String[] descriptorData = {"\n\fPerson.proto\022\021l1j.server.server\"￠\001\n\021sendEquipmentInfo\022\f\n\004type\030\001 \002(\005\022<\n\004item\030\002 \003(\0132..l1j.server.server.sendEquipmentInfo.Equipment\022\n\n\002un\030\003 \002(\005\022\013\n\003un1\030\004 \002(\005\032(\n\tEquipment\022\f\n\004eqid\030\001 \002(\005\022\r\n\005objid\030\002 \003(\005\"?\002\n\023sendSoulTowerRanked\022K\n\007newRank\030\001 \003(\0132:.l1j.server.server.sendSoulTowerRanked.soulTowerRankedInfo\022K\n\007oldRank\030\002 \003(\0132:.l1j.server.server.sendSoulTowerRanked.soulTowerRankedInfo\032n\n\023soulTowerRankedInfo\022", "\f\n\004name\030\001 \002(\f\022\020\n\busertype\030\002 \002(\005\022\017\n\007maptime\030\003 \002(\005\022\016\n\006gotime\030\004 \002(\005\022\026\n\016soulTowerClass\030\005 \002(\005\"3\002\n\fsendTaskInfo\022\f\n\004type\030\001 \002(\005\022\r\n\005type1\030\002 \002(\005\0226\n\004info\030\003 \003(\0132(.l1j.server.server.sendTaskInfo.TaskInfo\032í\001\n\bTaskInfo\022\016\n\006taskid\030\001 \002(\005\022\013\n\003url\030\003 \002(\f\022\r\n\005title\030\004 \001(\f\022\021\n\tstarttime\030\005 \001(\005\022\017\n\007endtime\030\006 \001(\005\022?\n\004boss\030\007 \001(\01321.l1j.server.server.sendTaskInfo.TaskInfo.bossInfo\0320\n\bbossInfo\022\020\n\bstringid\030\001 \002(\f\022\022\n\nteleportId\030\002 \002(\005\"í", "\001\n\022sendClanMemberInfo\022\f\n\004type\030\001 \002(\005\022D\n\nmemberInfo\030\002 \003(\01320.l1j.server.server.sendClanMemberInfo.MemberInfo\032?\001\n\nMemberInfo\022\r\n\005objid\030\001 \002(\005\022\020\n\bclanName\030\002 \002(\f\022\020\n\bobjectId\030\003 \002(\005\022\020\n\bcharName\030\004 \002(\f\022\f\n\004note\030\005 \002(\f\022\020\n\bisonline\030\006 \002(\005\022\017\n\007jobtype\030\007 \002(\005\"?\001\n\023sendMonsterKillInfo\022\f\n\004type\030\001 \002(\005\022\017\n\007unknow2\030\002 \002(\005\022=\n\004info\030\003 \003(\0132/.l1j.server.server.sendMonsterKillInfo.KillInfo\032&\n\bKillInfo\022\013\n\003num\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\"?\001\n", "\017sendMonsterList\022\017\n\007unknow1\030\001 \002(\005\022\017\n\007unknow2\030\002 \002(\005\022<\n\004info\030\003 \003(\0132..l1j.server.server.sendMonsterList.MonsterInfo\032C\n\013MonsterInfo\022\020\n\bquestNum\030\001 \002(\005\022\023\n\013monsterTime\030\002 \002(\005\022\r\n\005value\030\003 \002(\005\"2\n\fsendClanInfo\022\020\n\bclanName\030\001 \002(\f\022\020\n\bclanRank\030\002 \002(\005\"¨\002\n\023sendTelePortMapInfo\022\020\n\bnpcObjId\030\001 \002(\005\022D\n\004info\030\002 \003(\01326.l1j.server.server.sendTelePortMapInfo.TelePortMapInfo\032?\001\n\017TelePortMapInfo\022\024\n\ftelePortName\030\001 \002(\f\022O\n\007mapInfo\030\002", " \001(\0132>.l1j.server.server.sendTelePortMapInfo.TelePortMapInfo.MapInfo\032>\n\007MapInfo\022\017\n\007mapType\030\001 \002(\005\022\021\n\tteleMoney\030\002 \002(\005\022\017\n\007unknown\030\003 \002(\005\"?\001\n\rsendBuddyList\022\f\n\004type\030\001 \002(\005\022=\n\tbuddyList\030\002 \003(\0132*.l1j.server.server.sendBuddyList.buddyInfo\032<\n\tbuddyInfo\022\f\n\004name\030\001 \002(\f\022\020\n\bisOnline\030\002 \002(\005\022\017\n\007unknown\030\003 \002(\f\";\n\fsendFishTime\022\f\n\004type\030\001 \002(\005\022\f\n\004time\030\002 \001(\005\022\017\n\007isQuick\030\003 \001(\005\"?\002\n\013sendTAMPage\0226\n\004user\030\001 \003(\0132(.l1j.server.server", ".sendTAMPage.UserModel\022\020\n\bunknown1\030\002 \002(\005\022\020\n\bunknown2\030\003 \002(\005\022\020\n\bunknown3\030\004 \002(\005\032?\001\n\tUserModel\022\020\n\bserverNo\030\001 \002(\005\022\r\n\005objid\030\002 \002(\005\022\f\n\004time\030\003 \002(\005\022\017\n\007tamwait\030\004 \002(\005\022\f\n\004name\030\005 \002(\f\022\r\n\005level\030\006 \002(\005\022\021\n\ttypeClass\030\007 \002(\005\022\013\n\003sex\030\b \002(\005\"?\007\n\016sendAttrReward\0227\n\003str\030\001 \002(\0132*.l1j.server.server.sendAttrReward.strModel\0229\n\005intel\030\002 \002(\0132*.l1j.server.server.sendAttrReward.intModel\0227\n\003wis\030\003 \002(\0132*.l1j.server.server.sendAttrReward.w", "isModel\0227\n\003dex\030\004 \002(\0132*.l1j.server.server.sendAttrReward.dexModel\0227\n\003con\030\005 \002(\0132*.l1j.server.server.sendAttrReward.conModel\0227\n\003cha\030\006 \002(\0132*.l1j.server.server.sendAttrReward.chaModel\032R\n\bstrModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003dmg\030\002 \001(\005\022\013\n\003hit\030\003 \001(\005\022\032\n\022criticalStrikeRate\030\004 \001(\005\032a\n\bintModel\022\020\n\battValue\030\001 \001(\005\022\020\n\bmagicDmg\030\002 \001(\005\022\020\n\bmagicHit\030\003 \001(\005\022\037\n\027magicCriticalStrikeRate\030\004 \001(\005\032?\001\n\bwisModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003mpr\030", "\002 \001(\005\022\023\n\013MPCureBonus\030\003 \001(\005\022\n\n\002mr\030\004 \001(\005\022\024\n\flvUpAddMinMp\030\005 \001(\005\022\024\n\flvUpAddMaxMp\030\006 \001(\005\022\r\n\005mpMax\030\007 \001(\005\032[\n\bdexModel\022\020\n\battValue\030\001 \001(\005\022\016\n\006bowDmg\030\002 \001(\005\022\016\n\006bowHit\030\003 \001(\005\022\035\n\025bowcriticalStrikeRate\030\004 \001(\005\032s\n\bconModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003hpr\030\002 \001(\005\022\023\n\013HPCureBonus\030\003 \001(\005\022\016\n\006weight\030\004 \001(\005\022\024\n\flvUpAddMaxHp\030\005 \001(\005\022\r\n\005hpMax\030\006 \001(\005\032\034\n\bchaModel\022\020\n\battValue\030\001 \001(\005\"b\n\020sendUserBaseAttr\022\013\n\003str\030\001 \002(\005\022\r\n\005intel\030\002 \002(\005\022\013\n\003wis\030\003 \002(\005\022\013", "\n\003dex\030\004 \002(\005\022\013\n\003con\030\005 \002(\005\022\013\n\003cha\030\006 \002(\005\"A\n\013readBanInfo\022\023\n\013excludeType\030\001 \002(\005\022\017\n\007subType\030\002 \002(\005\022\f\n\004name\030\003 \003(\f\"\026\n\007readSha\022\013\n\003sha\030\001 \002(\f\"D\n\020readCustomPacket\022\016\n\006custom\030\001 \001(\005\022\017\n\007custom1\030\002 \001(\005\022\017\n\007custom2\030\003 \001(\005\"Y\n\021readCustomPacket1\022\016\n\006custom\030\001 \001(\005\0224\n\007custom1\030\002 \001(\0132#.l1j.server.server.readCustomPacket\"c\n\020sendCustomPacket\022\f\n\004type\030\001 \001(\005\022\016\n\006custom\030\002 \001(\005\022\017\n\007custom1\030\003 \001(\005\022\017\n\007custom2\030\004 \001(\005\022\017\n\007custom3\030\005 \001(\005\"H\n\021sendCu", "stomPacket1\0223\n\006custom\030\001 \001(\0132#.l1j.server.server.sendCustomPacket\"Z\n\016sendClanConfig\022\f\n\004type\030\001 \002(\005\022\025\n\rjoinOpenState\030\002 \002(\005\022\021\n\tjoinState\030\003 \002(\005\022\020\n\bpassword\030\004 \002(\t\"è\001\n\017sendCustomSkill\022\021\n\tskillType\030\001 \002(\005\022\017\n\007skillId\030\002 \002(\005\022\f\n\004time\030\003 \002(\005\022\020\n\btimetype\030\004 \002(\005\022\f\n\004icon\030\005 \002(\005\022\020\n\bunknown1\030\006 \002(\005\022\013\n\003seq\030\007 \002(\005\022\020\n\bskillMsg\030\b \002(\005\022\020\n\bstartMsg\030\t \002(\005\022\016\n\006endMsg\030\n \002(\005\022\020\n\bunknown2\030\013 \002(\005\"B\n\021sendCastleWarTime\022\r\n\005isatt\030\001 \002(\005\022\f\n\004t", "ime\030\002 \002(\005\022\020\n\bclanName\030\003 \001(\f\"?\001\n\021sendCastleTaxRate\022\020\n\bcastleId\030\001 \002(\005\022\020\n\bclanName\030\002 \002(\f\022\022\n\nleaderName\030\003 \002(\f\022\020\n\bunknown1\030\004 \002(\005\022\020\n\bunknown2\030\005 \002(\005\022\020\n\bunknown3\030\006 \002(\005\022\023\n\013publicMoney\030\007 \002(\005\"?\001\n\025sendCraftNpcCraftList\022\020\n\bunknown1\030\001 \002(\005\022;\n\004list\030\002 \003(\0132-.l1j.server.server.sendCraftNpcCraftList.List\022\020\n\bunknown2\030\003 \002(\005\032<\n\004List\022\020\n\bactionId\030\001 \002(\005\022\020\n\bunknown1\030\002 \002(\005\022\020\n\bunknown2\030\003 \002(\005\"\037\n\016readCraftNpcId\022\r\n\005npcid\030\001 \002(\005\"0", "\n\rSendCraftType\022\f\n\004type\030\001 \002(\005\022\021\n\ttimeIndex\030\002 \002(\005\"Y\n\fReadUserChat\022\021\n\tchatIndex\030\001 \002(\005\022\020\n\bchatType\030\002 \002(\005\022\020\n\bchatText\030\003 \002(\f\022\022\n\ntargetName\030\005 \001(\f\"-\001\n\007MapInfo\022\r\n\005mapid\030\001 \002(\005\022\020\n\bserverId\030\002 \002(\005\022\024\n\fisUnderwater\030\003 \002(\005\022\020\n\bunknown1\030\004 \002(\005\022\020\n\bunknown2\030\005 \002(\005\022\020\n\bunknown3\030\006 \002(\005\0225\n\bunknown4\030\007 \001(\0132#.l1j.server.server.sendCustomPacket\"?\b\n\027CharAbilityDetailedInfo\022\f\n\004type\030\001 \002(\005\022G\n\tstrPacket\030\002 \001(\01324.l1j.server.server.Cha", "rAbilityDetailedInfo.StrPacket\022G\n\tintPacket\030\003 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.IntPacket\022G\n\twisPacket\030\004 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.WisPacket\022G\n\tdexPacket\030\005 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.DexPacket\022G\n\tconPacket\030\006 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.ConPacket\022G\n\tchaPacket\030\007 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.ChaPacket\032Q\n\tS", "trPacket\022\013\n\003dmg\030\001 \002(\005\022\013\n\003hit\030\002 \002(\005\022\032\n\022criticalStrikeRate\030\003 \002(\005\022\016\n\006weight\030\004 \002(\005\032b\n\tDexPacket\022\016\n\006bowDmg\030\001 \002(\005\022\016\n\006bowHit\030\002 \002(\005\022\035\n\025bowcriticalStrikeRate\030\003 \002(\005\022\n\n\002ac\030\004 \002(\005\022\n\n\002er\030\005 \002(\005\032}\n\tIntPacket\022\020\n\bmagicDmg\030\001 \002(\005\022\020\n\bmagicHit\030\002 \002(\005\022\037\n\027magicCriticalStrikeRate\030\003 \002(\005\022\022\n\nmagicBonus\030\004 \002(\005\022\027\n\017MPConsumeReduce\030\005 \002(\005\032i\n\tConPacket\022\013\n\003hpr\030\001 \002(\005\022\023\n\013HPCureBonus\030\002 \002(\005\022\016\n\006weight\030\003 \002(\005\022\024\n\flvUpAddMinHp\030\004 \002(\005\022\024\n\flvUpAd", "dMaxHp\030\005 \002(\005\032e\n\tWisPacket\022\013\n\003mpr\030\001 \002(\005\022\023\n\013MPCureBonus\030\002 \002(\005\022\n\n\002mr\030\003 \002(\005\022\024\n\flvUpAddMinMp\030\004 \002(\005\022\024\n\flvUpAddMaxMp\030\005 \002(\005\032\034\n\tChaPacket\022\017\n\007unknown\030\001 \002(\005\"μ\001\n\023ReadAddCharAbillity\022\r\n\005level\030\001 \002(\005\022\021\n\tclassType\030\002 \002(\005\022\f\n\004type\030\003 \002(\005\022\f\n\004mode\030\004 \001(\005\022\020\n\bstrOrCon\030\005 \001(\005\022\013\n\003str\030\006 \001(\005\022\r\n\005intel\030\007 \001(\005\022\013\n\003wis\030\b \001(\005\022\013\n\003dex\030\t \001(\005\022\013\n\003con\030\n \001(\005\022\013\n\003cha\030\013 \001(\005\"3\n\017DollComposeItem\022\021\n\titemObjId\030\001 \002(\005\022\r\n\005gfxid\030\002 \002(\005\"ü\001\n\020LotteryInvent", "ory\022\f\n\004type\030\001 \002(\005\022\r\n\005type1\030\002 \002(\005\022F\n\004item\030\003 \003(\01328.l1j.server.server.LotteryInventory.LotteryInventoryItem\032c\n\024LotteryInventoryItem\022\016\n\006descid\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\r\n\005gfxid\030\003 \002(\005\022\017\n\007indexId\030\004 \002(\005\022\f\n\004name\030\005 \002(\f\"3\n\024readLotteryInventory\022\f\n\004type\030\001 \002(\005\022\r\n\005index\030\002 \003(\005\"ì\001\n\rItemCraftRead\022\020\n\bnpcObjId\030\001 \002(\005\022\020\n\bactionId\030\002 \002(\005\022\r\n\005count\030\003 \002(\005\022<\n\bitemlist\030\004 \003(\0132*.l1j.server.server.ItemCraftRead.CraftItem\022\021\n\tcrefCou", "nt\030\005 \001(\005\032W\n\tCraftItem\022\r\n\005index\030\001 \002(\005\022\016\n\006descid\030\002 \002(\005\022\021\n\titemcount\030\003 \001(\005\022\030\n\020itemEnchantLevel\030\004 \001(\005\"D\004\n\fPCAndNpcPack\022\013\n\003loc\030\001 \002(\004\022\n\n\002id\030\002 \002(\005\022\r\n\005gfxId\030\003 \002(\005\022\016\n\006status\030\004 \002(\005\022\017\n\007heading\030\005 \002(\005\022\024\n\fownLightSize\030\006 \002(\005\022\021\n\tlightSize\030\007 \002(\005\022\016\n\006lawful\030\b \002(\005\022\f\n\004name\030\t \002(\f\022\r\n\005title\030\n \002(\f\022\021\n\tmoveSpeed\030\013 \002(\005\022\022\n\nbraveSpeed\030\f \002(\005\022\024\n\fisThreeSpeed\030\r \002(\005\022\017\n\007isGhost\030\016 \002(\005\022\023\n\013isParalyzed\030\017 \002(\005\022\020\n\bviewName\030\020 \002(\005\022\017\n\007isInvi", "s\030\021 \002(\005\022\016\n\006posion\030\022 \002(\005\022\016\n\006clanId\030\023 \002(\005\022\020\n\bclanName\030\024 \002(\f\022\016\n\006master\030\025 \002(\f\022\r\n\005state\030\026 \002(\005\022\017\n\007HPMeter\030\027 \002(\005\022\r\n\005level\030\030 \002(\005\022\030\n\020privateShopTitle\030\031 \001(\f\022\020\n\bunknown7\030\032 \002(\005\022\020\n\bunknown8\030\033 \002(\005\022\020\n\bunknown9\030\034 \002(\005\022\021\n\tunknown10\030\035 \001(\005\022\017\n\007MPMeter\030\036 \002(\005\022\021\n\tunknown11\030\037 \001(\005\022\020\n\bServerNo\030  \001(\005\022\021\n\tunknown14\030\" \001(\005\"Y\n\rSendItemCraft\022\024\n\fisSendPacket\030\001 \002(\005\0222\n\004list\030\002 \003(\0132$.l1j.server.server.SendItemCraftList\"?\024\n\021SendItemCraf", "tList\022\020\n\bactionid\030\001 \002(\005\022J\n\tcondition\030\002 \002(\01327.l1j.server.server.SendItemCraftList.CraftItemCondition\022\020\n\bunknown1\030\003 \002(\005\022;\n\005quest\030\004 \002(\0132,.l1j.server.server.SendItemCraftList.unQuest\022<\n\004poly\030\005 \002(\0132..l1j.server.server.SendItemCraftList.PolyModel\022D\n\bunknown2\030\006 \002(\01322.l1j.server.server.SendItemCraftList.unknownModel2\022?\n\bmaterial\030\007 \002(\0132-.l1j.server.server.SendItemCraftList.Material\022B\n\007results\030\b \002(\01321.l1j.s", "erver.server.SendItemCraftList.CraftResults\022\026\n\016craftDelayTime\030\t \002(\005\032ì\001\n\022CraftItemCondition\022\016\n\006nameId\030\001 \002(\005\022\020\n\bminLevel\030\002 \002(\005\022\020\n\bmaxLevel\030\003 \002(\005\022\020\n\bunknown1\030\004 \002(\005\022\021\n\tminLawful\030\005 \002(\005\022\021\n\tMaxLawful\030\006 \002(\005\022\020\n\bminKarma\030\007 \002(\005\022\020\n\bmaxKarma\030\b \002(\005\022\020\n\bmaxCount\030\t \002(\005\022\024\n\fisShowChance\030\n \001(\005\0322\n\nQuestModel\022\017\n\007questId\030\001 \002(\005\022\023\n\013questStepId\030\002 \002(\005\032m\n\007unQuest\022\020\n\bunknown1\030\001 \002(\005\022\020\n\bunknown2\030\002 \002(\005\022>\n\005quest\030\003 \002(\0132/.l1j.serve", "r.server.SendItemCraftList.QuestModel\0320\n\tPolyModel\022\023\n\013pcountcount\030\001 \002(\005\022\016\n\006polyId\030\002 \003(\005\0321\n\runknownModel1\022\017\n\007unknow1\030\001 \002(\003\022\017\n\007unknow2\030\002 \002(\003\032è\002\n\bMaterial\022M\n\bmaterial\030\001 \003(\0132;.l1j.server.server.SendItemCraftList.Material.MaterialModel\022P\n\013addMaterial\030\002 \003(\0132;.l1j.server.server.SendItemCraftList.Material.MaterialModel\032o\001\n\rMaterialModel\022\022\n\nitemDescId\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\021\n\twindowNum\030\003 \002(\005\022\024\n\fenchantLevel", "\030\004 \002(\005\022\r\n\005bless\030\005 \002(\005\022\f\n\004name\030\006 \002(\f\022\r\n\005gfxId\030\007 \002(\005\022\017\n\007unknow1\030\b \002(\005\022\017\n\007unknow2\030\t \002(\005\022\017\n\007unknow3\030\n \002(\005\032?\007\n\013CraftResult\022D\n\bunknown1\030\001 \001(\01322.l1j.server.server.SendItemCraftList.unknownModel1\022D\n\bunknown2\030\002 \001(\01322.l1j.server.server.SendItemCraftList.unknownModel1\022\020\n\bitemSize\030\003 \001(\005\022\020\n\bitemtype\030\004 \001(\005\022Z\n\017singleCraftItem\030\006 \003(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\022Z\n\017randomCraft", "Item\030\005 \003(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\022M\n\tgreatItem\030\007 \001(\0132:.l1j.server.server.SendItemCraftList.CraftResult.GreatItem\032o\002\n\020CraftResultModel\022\022\n\nitemDescId\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\020\n\bunknown1\030\003 \002(\003\022\024\n\fenchantLevel\030\004 \002(\005\022\r\n\005bless\030\005 \002(\005\022\020\n\bunknown2\030\006 \002(\005\022\020\n\bunknown3\030\007 \002(\005\022\f\n\004name\030\b \002(\f\022\020\n\bunknown4\030\t \002(\005\022\020\n\bunknown5\030\n \002(\005\022\r\n\005gfxId\030\013 \002(\005\022\025\n\rsuccedMessage\030\f \002(\f\022\027\n\017itemS", "tatusBytes\030\r \002(\f\022\020\n\bunknown6\030\016 \001(\005\022\020\n\bunknown7\030\017 \001(\005\022\023\n\013isGreatItem\030\020 \001(\005\032?\001\n\tGreatItem\022\n\n\002un\030\001 \002(\005\022\013\n\003un1\030\002 \002(\005\022\013\n\003un2\030\003 \002(\005\022O\n\004Item\030\004 \002(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\032ˉ\001\n\fCraftResults\022E\n\013succeedList\030\001 \002(\01320.l1j.server.server.SendItemCraftList.CraftResult\022B\n\bfailList\030\002 \002(\01320.l1j.server.server.SendItemCraftList.CraftResult\022\024\n\fsuccessRatio\030\003 \002(\005\032?\001\n\runknownMode", "l2\022\f\n\004type\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022G\n\002un\030\003 \003(\0132;.l1j.server.server.SendItemCraftList.unknownModel2.ItemInfo\0325\n\bItemInfo\022\016\n\006descid\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\n\n\002un\030\003 \002(\005\"ò\005\n\rsendDollCraft\022\024\n\fisSendPacket\030\001 \002(\005\022;\n\bmaterial\030\002 \003(\0132).l1j.server.server.sendDollCraft.ItemList\0229\n\006result\030\003 \003(\0132).l1j.server.server.sendDollCraft.ItemList\0228\n\006config\030\004 \003(\0132(.l1j.server.server.sendDollCraft.Configs\032?\001\n\bItemList\022\016\n\006statge", "\030\001 \002(\005\022@\n\004item\030\002 \003(\01322.l1j.server.server.sendDollCraft.ItemList.ItemInfo\0321\n\bItemInfo\022\022\n\nitemDescId\030\001 \002(\005\022\021\n\titemGfxId\030\002 \001(\005\032?\003\n\007Configs\022\r\n\005stage\030\001 \002(\005\022E\n\006config\030\002 \003(\01325.l1j.server.server.sendDollCraft.Configs.ColumnConfig\022A\n\007config1\030\003 \003(\01320.l1j.server.server.sendDollCraft.Configs.Config1\032B\n\fColumnConfig\022\021\n\tcolumnNum\030\001 \002(\005\022\020\n\bunknown1\030\002 \002(\005\022\r\n\005stage\030\003 \002(\005\032?\001\n\007Config1\022\r\n\005stage\030\001 \003(\005\022\f\n\004type\030\002 \002(\005\022I\n", "\007unknow1\030\003 \001(\01328.l1j.server.server.sendDollCraft.Configs.Config1.Config2\032*\n\007Config2\022\017\n\007unknow1\030\001 \001(\005\022\016\n\006stage1\030\002 \001(\005\"?\001\n\021sendDollCraftType\022\f\n\004type\030\001 \002(\005\022?\n\bdollItem\030\002 \002(\0132-.l1j.server.server.sendDollCraftType.DollItem\032(\n\bDollItem\022\r\n\005objid\030\001 \002(\005\022\r\n\005gfxId\030\002 \002(\005\"?\001\n\rreadDollCraft\022\r\n\005stage\030\001 \002(\005\0227\n\004item\030\002 \003(\0132).l1j.server.server.readDollCraft.ItemInfo\032<\n\bItemInfo\022\r\n\005index\030\001 \002(\005\022\016\n\006descId\030\002 \002(\005\022\021\n\titem", "ObjId\030\003 \002(\005"};
        Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            public ExtensionRegistry assignDescriptors(Descriptors.FileDescriptor root) {
                ItemCraft_Other.descriptor = root;
                return null;
            }
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

    public static abstract interface SendItemCraftListOrBuilder extends MessageOrBuilder {
        public abstract boolean hasActionid();

        public abstract int getActionid();

        public abstract boolean hasCondition();

        public abstract ItemCraft_Other.SendItemCraftList.CraftItemCondition getCondition();

        public abstract ItemCraft_Other.SendItemCraftList.CraftItemConditionOrBuilder getConditionOrBuilder();

        public abstract boolean hasUnknown1();

        public abstract int getUnknown1();

        public abstract boolean hasQuest();

        public abstract ItemCraft_Other.SendItemCraftList.unQuest getQuest();

        public abstract ItemCraft_Other.SendItemCraftList.unQuestOrBuilder getQuestOrBuilder();

        public abstract boolean hasPoly();

        public abstract ItemCraft_Other.SendItemCraftList.PolyModel getPoly();

        public abstract ItemCraft_Other.SendItemCraftList.PolyModelOrBuilder getPolyOrBuilder();

        public abstract boolean hasUnknown2();

        public abstract ItemCraft_Other.SendItemCraftList.unknownModel2 getUnknown2();

        public abstract ItemCraft_Other.SendItemCraftList.unknownModel2OrBuilder getUnknown2OrBuilder();

        public abstract boolean hasMaterial();

        public abstract ItemCraft_Other.SendItemCraftList.Material getMaterial();

        public abstract ItemCraft_Other.SendItemCraftList.MaterialOrBuilder getMaterialOrBuilder();

        public abstract boolean hasResults();

        public abstract ItemCraft_Other.SendItemCraftList.CraftResults getResults();

        public abstract ItemCraft_Other.SendItemCraftList.CraftResultsOrBuilder getResultsOrBuilder();

        public abstract boolean hasCraftDelayTime();

        public abstract int getCraftDelayTime();
    }

    public static abstract interface SendItemCraftOrBuilder extends MessageOrBuilder {
        public abstract boolean hasIsSendPacket();

        public abstract int getIsSendPacket();

        public abstract List<ItemCraft_Other.SendItemCraftList> getListList();

        public abstract ItemCraft_Other.SendItemCraftList getList(int paramInt);

        public abstract int getListCount();

        public abstract List<? extends ItemCraft_Other.SendItemCraftListOrBuilder> getListOrBuilderList();

        public abstract ItemCraft_Other.SendItemCraftListOrBuilder getListOrBuilder(int paramInt);
    }

    public static final class SendItemCraft extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftOrBuilder {
        public static final int ISSENDPACKET_FIELD_NUMBER = 1;
        public static final int LIST_FIELD_NUMBER = 2;
        @Deprecated
        public static final Parser<SendItemCraft> PARSER = new AbstractParser<SendItemCraft>() {
            public ItemCraft_Other.SendItemCraft parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new ItemCraft_Other.SendItemCraft(input, extensionRegistry);
            }
        };
        private static final long serialVersionUID = 0L;
        private static final SendItemCraft DEFAULT_INSTANCE = new SendItemCraft();
        private int bitField0_;
        private int isSendPacket_;
        private List<ItemCraft_Other.SendItemCraftList> list_;
        private byte memoizedIsInitialized = -1;

        private SendItemCraft(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private SendItemCraft() {
            this.isSendPacket_ = 0;
            this.list_ = Collections.emptyList();
        }

        private SendItemCraft(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
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
                            this.isSendPacket_ = input.readInt32();
                            break;
                        case 18:
                            if ((mutable_bitField0_ & 0x2) != 2) {
                                this.list_ = new ArrayList<SendItemCraftList>();
                                mutable_bitField0_ |= 2;
                            }
                            this.list_.add((ItemCraft_Other.SendItemCraftList) input.readMessage(ItemCraft_Other.SendItemCraftList.PARSER, extensionRegistry));
                            break;
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (IOException e) {
                throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
            } finally {
                if ((mutable_bitField0_ & 0x2) == 2) {
                    this.list_ = Collections.unmodifiableList(this.list_);
                }
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return ItemCraft_Other.SendItemCraft_descriptor;
        }

        public static SendItemCraft parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (SendItemCraft) PARSER.parseFrom(data);
        }

        public static SendItemCraft parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SendItemCraft) PARSER.parseFrom(data, extensionRegistry);
        }

        public static SendItemCraft parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (SendItemCraft) PARSER.parseFrom(data);
        }

        public static SendItemCraft parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SendItemCraft) PARSER.parseFrom(data, extensionRegistry);
        }

        public static SendItemCraft parseFrom(InputStream input) throws IOException {
            return (SendItemCraft) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static SendItemCraft parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SendItemCraft) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static SendItemCraft parseDelimitedFrom(InputStream input) throws IOException {
            return (SendItemCraft) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static SendItemCraft parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SendItemCraft) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static SendItemCraft parseFrom(CodedInputStream input) throws IOException {
            return (SendItemCraft) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static SendItemCraft parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SendItemCraft) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(SendItemCraft prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static SendItemCraft getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SendItemCraft> parser() {
            return PARSER;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return ItemCraft_Other.SendItemCraft_fieldAccessorTable.ensureFieldAccessorsInitialized(SendItemCraft.class, Builder.class);
        }

        public boolean hasIsSendPacket() {
            return (this.bitField0_ & 0x1) == 1;
        }

        public int getIsSendPacket() {
            return this.isSendPacket_;
        }

        public List<ItemCraft_Other.SendItemCraftList> getListList() {
            return this.list_;
        }

        public List<? extends ItemCraft_Other.SendItemCraftListOrBuilder> getListOrBuilderList() {
            return this.list_;
        }

        public int getListCount() {
            return this.list_.size();
        }

        public ItemCraft_Other.SendItemCraftList getList(int index) {
            return (ItemCraft_Other.SendItemCraftList) this.list_.get(index);
        }

        public ItemCraft_Other.SendItemCraftListOrBuilder getListOrBuilder(int index) {
            return (ItemCraft_Other.SendItemCraftListOrBuilder) this.list_.get(index);
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasIsSendPacket()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            for (int i = 0; i < getListCount(); i++) {
                if (!getList(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 0x1) == 1) {
                output.writeInt32(1, this.isSendPacket_);
            }
            for (int i = 0; i < this.list_.size(); i++) {
                output.writeMessage(2, (MessageLite) this.list_.get(i));
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
                size = size + CodedOutputStream.computeInt32Size(1, this.isSendPacket_);
            }
            for (int i = 0; i < this.list_.size(); i++) {
                size = size + CodedOutputStream.computeMessageSize(2, (MessageLite) this.list_.get(i));
            }
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SendItemCraft)) {
                return super.equals(obj);
            }
            SendItemCraft other = (SendItemCraft) obj;
            boolean result = true;
            result = (result) && (hasIsSendPacket() == other.hasIsSendPacket());
            if (hasIsSendPacket()) {
                result = (result) && (getIsSendPacket() == other.getIsSendPacket());
            }
            result = (result) && (getListList().equals(other.getListList()));
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
            if (hasIsSendPacket()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + getIsSendPacket();
            }
            if (getListCount() > 0) {
                hash = 37 * hash + 2;
                hash = 53 * hash + getListList().hashCode();
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

        public Parser<SendItemCraft> getParserForType() {
            return PARSER;
        }

        public SendItemCraft getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftOrBuilder {
            private int bitField0_;
            private int isSendPacket_;
            private List<ItemCraft_Other.SendItemCraftList> list_ = Collections.emptyList();
            private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList, ItemCraft_Other.SendItemCraftList.Builder, ItemCraft_Other.SendItemCraftListOrBuilder> listBuilder_;

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraft_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraft_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraft.class, Builder.class);
            }

            private void maybeForceBuilderInitialization() {
                if (ItemCraft_Other.SendItemCraft.alwaysUseFieldBuilders) {
                    getListFieldBuilder();
                }
            }

            public Builder clear() {
                super.clear();
                this.isSendPacket_ = 0;
                this.bitField0_ &= -2;
                if (this.listBuilder_ == null) {
                    this.list_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                } else {
                    this.listBuilder_.clear();
                }
                return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return ItemCraft_Other.SendItemCraft_descriptor;
            }

            public ItemCraft_Other.SendItemCraft getDefaultInstanceForType() {
                return ItemCraft_Other.SendItemCraft.getDefaultInstance();
            }

            public ItemCraft_Other.SendItemCraft build() {
                ItemCraft_Other.SendItemCraft result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public ItemCraft_Other.SendItemCraft buildPartial() {
                ItemCraft_Other.SendItemCraft result = new ItemCraft_Other.SendItemCraft(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 1) {
                    to_bitField0_ |= 1;
                }
                result.isSendPacket_ = this.isSendPacket_;
                if (this.listBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 2) {
                        this.list_ = Collections.unmodifiableList(this.list_);
                        this.bitField0_ &= -3;
                    }
                    result.list_ = this.list_;
                } else {
                    result.list_ = this.listBuilder_.build();
                }
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
                if ((other instanceof ItemCraft_Other.SendItemCraft)) {
                    return mergeFrom((ItemCraft_Other.SendItemCraft) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(ItemCraft_Other.SendItemCraft other) {
                if (other == ItemCraft_Other.SendItemCraft.getDefaultInstance()) {
                    return this;
                }
                if (other.hasIsSendPacket()) {
                    setIsSendPacket(other.getIsSendPacket());
                }
                if (this.listBuilder_ == null) {
                    if (!other.list_.isEmpty()) {
                        if (this.list_.isEmpty()) {
                            this.list_ = other.list_;
                            this.bitField0_ &= -3;
                        } else {
                            ensureListIsMutable();
                            this.list_.addAll(other.list_);
                        }
                        onChanged();
                    }
                } else if (!other.list_.isEmpty()) {
                    if (this.listBuilder_.isEmpty()) {
                        this.listBuilder_.dispose();
                        this.listBuilder_ = null;
                        this.list_ = other.list_;
                        this.bitField0_ &= -3;
                        this.listBuilder_ = (ItemCraft_Other.SendItemCraft.alwaysUseFieldBuilders ? getListFieldBuilder() : null);
                    } else {
                        this.listBuilder_.addAllMessages(other.list_);
                    }
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                if (!hasIsSendPacket()) {
                    return false;
                }
                for (int i = 0; i < getListCount(); i++) {
                    if (!getList(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                ItemCraft_Other.SendItemCraft parsedMessage = null;
                try {
                    parsedMessage = (ItemCraft_Other.SendItemCraft) ItemCraft_Other.SendItemCraft.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ItemCraft_Other.SendItemCraft) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            public boolean hasIsSendPacket() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getIsSendPacket() {
                return this.isSendPacket_;
            }

            public Builder setIsSendPacket(int value) {
                this.bitField0_ |= 1;
                this.isSendPacket_ = value;
                onChanged();
                return this;
            }

            public Builder clearIsSendPacket() {
                this.bitField0_ &= -2;
                this.isSendPacket_ = 0;
                onChanged();
                return this;
            }

            private void ensureListIsMutable() {
                if ((this.bitField0_ & 0x2) != 2) {
                    this.list_ = new ArrayList<SendItemCraftList>(this.list_);
                    this.bitField0_ |= 2;
                }
            }

            public List<ItemCraft_Other.SendItemCraftList> getListList() {
                if (this.listBuilder_ == null) {
                    return Collections.unmodifiableList(this.list_);
                }
                return this.listBuilder_.getMessageList();
            }

            public int getListCount() {
                if (this.listBuilder_ == null) {
                    return this.list_.size();
                }
                return this.listBuilder_.getCount();
            }

            public ItemCraft_Other.SendItemCraftList getList(int index) {
                if (this.listBuilder_ == null) {
                    return (ItemCraft_Other.SendItemCraftList) this.list_.get(index);
                }
                return (ItemCraft_Other.SendItemCraftList) this.listBuilder_.getMessage(index);
            }

            public Builder setList(int index, ItemCraft_Other.SendItemCraftList value) {
                if (this.listBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureListIsMutable();
                    this.list_.set(index, value);
                    onChanged();
                } else {
                    this.listBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setList(int index, ItemCraft_Other.SendItemCraftList.Builder builderForValue) {
                if (this.listBuilder_ == null) {
                    ensureListIsMutable();
                    this.list_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    this.listBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addList(ItemCraft_Other.SendItemCraftList value) {
                if (this.listBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureListIsMutable();
                    this.list_.add(value);
                    onChanged();
                } else {
                    this.listBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addList(int index, ItemCraft_Other.SendItemCraftList value) {
                if (this.listBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureListIsMutable();
                    this.list_.add(index, value);
                    onChanged();
                } else {
                    this.listBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addList(ItemCraft_Other.SendItemCraftList.Builder builderForValue) {
                if (this.listBuilder_ == null) {
                    ensureListIsMutable();
                    this.list_.add(builderForValue.build());
                    onChanged();
                } else {
                    this.listBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addList(int index, ItemCraft_Other.SendItemCraftList.Builder builderForValue) {
                if (this.listBuilder_ == null) {
                    ensureListIsMutable();
                    this.list_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    this.listBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllList(Iterable<? extends ItemCraft_Other.SendItemCraftList> values) {
                if (this.listBuilder_ == null) {
                    ensureListIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.list_);
                    onChanged();
                } else {
                    this.listBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearList() {
                if (this.listBuilder_ == null) {
                    this.list_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                    onChanged();
                } else {
                    this.listBuilder_.clear();
                }
                return this;
            }

            public Builder removeList(int index) {
                if (this.listBuilder_ == null) {
                    ensureListIsMutable();
                    this.list_.remove(index);
                    onChanged();
                } else {
                    this.listBuilder_.remove(index);
                }
                return this;
            }

            public ItemCraft_Other.SendItemCraftList.Builder getListBuilder(int index) {
                return (ItemCraft_Other.SendItemCraftList.Builder) getListFieldBuilder().getBuilder(index);
            }

            public ItemCraft_Other.SendItemCraftListOrBuilder getListOrBuilder(int index) {
                if (this.listBuilder_ == null) {
                    return (ItemCraft_Other.SendItemCraftListOrBuilder) this.list_.get(index);
                }
                return (ItemCraft_Other.SendItemCraftListOrBuilder) this.listBuilder_.getMessageOrBuilder(index);
            }

            public List<? extends ItemCraft_Other.SendItemCraftListOrBuilder> getListOrBuilderList() {
                if (this.listBuilder_ != null) {
                    return this.listBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.list_);
            }

            public ItemCraft_Other.SendItemCraftList.Builder addListBuilder() {
                return (ItemCraft_Other.SendItemCraftList.Builder) getListFieldBuilder().addBuilder(ItemCraft_Other.SendItemCraftList.getDefaultInstance());
            }

            public ItemCraft_Other.SendItemCraftList.Builder addListBuilder(int index) {
                return (ItemCraft_Other.SendItemCraftList.Builder) getListFieldBuilder().addBuilder(index, ItemCraft_Other.SendItemCraftList.getDefaultInstance());
            }

            public List<ItemCraft_Other.SendItemCraftList.Builder> getListBuilderList() {
                return getListFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList, ItemCraft_Other.SendItemCraftList.Builder, ItemCraft_Other.SendItemCraftListOrBuilder> getListFieldBuilder() {
                if (this.listBuilder_ == null) {
                    this.listBuilder_ = new RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList, ItemCraft_Other.SendItemCraftList.Builder, ItemCraft_Other.SendItemCraftListOrBuilder>(this.list_, (this.bitField0_ & 0x2) == 2, getParentForChildren(), isClean());
                    this.list_ = null;
                }
                return this.listBuilder_;
            }

            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }
    }

    public static final class SendItemCraftList extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftListOrBuilder {
        public static final int ACTIONID_FIELD_NUMBER = 1;
        public static final int CONDITION_FIELD_NUMBER = 2;
        public static final int UNKNOWN1_FIELD_NUMBER = 3;
        public static final int QUEST_FIELD_NUMBER = 4;
        public static final int POLY_FIELD_NUMBER = 5;
        public static final int UNKNOWN2_FIELD_NUMBER = 6;
        public static final int MATERIAL_FIELD_NUMBER = 7;
        public static final int RESULTS_FIELD_NUMBER = 8;
        public static final int CRAFTDELAYTIME_FIELD_NUMBER = 9;
        @Deprecated
        public static final Parser<SendItemCraftList> PARSER = new AbstractParser<SendItemCraftList>() {
            public ItemCraft_Other.SendItemCraftList parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new ItemCraft_Other.SendItemCraftList(input, extensionRegistry);
            }
        };
        private static final long serialVersionUID = 0L;
        private static final SendItemCraftList DEFAULT_INSTANCE = new SendItemCraftList();
        private int bitField0_;
        private int actionid_;
        private CraftItemCondition condition_;
        private int unknown1_;
        private unQuest quest_;
        private PolyModel poly_;
        private unknownModel2 unknown2_;
        private Material material_;
        private CraftResults results_;
        private int craftDelayTime_;
        private byte memoizedIsInitialized = -1;

        private SendItemCraftList(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private SendItemCraftList() {
            this.actionid_ = 0;
            this.unknown1_ = 0;
            this.craftDelayTime_ = 0;
        }

        private SendItemCraftList(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.actionid_ = input.readInt32();
                            break;
                        case 18:
                            ItemCraft_Other.SendItemCraftList.CraftItemCondition.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x2) == 2) {
                                subBuilder = this.condition_.toBuilder();
                            }
                            this.condition_ = ((CraftItemCondition) input.readMessage(CraftItemCondition.PARSER, extensionRegistry));
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.condition_);
                                this.condition_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 2;
                            break;
                        case 24:
                            this.bitField0_ |= 4;
                            this.unknown1_ = input.readInt32();
                            break;
                        case 34:
                            ItemCraft_Other.SendItemCraftList.unQuest.Builder subBuilder1 = null;
                            if ((this.bitField0_ & 0x8) == 8) {
                                subBuilder1 = this.quest_.toBuilder();
                            }
                            this.quest_ = ((unQuest) input.readMessage(unQuest.PARSER, extensionRegistry));
                            if (subBuilder1 != null) {
                                subBuilder1.mergeFrom(this.quest_);
                                this.quest_ = subBuilder1.buildPartial();
                            }
                            this.bitField0_ |= 8;
                            break;
                        case 42:
                            ItemCraft_Other.SendItemCraftList.PolyModel.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x10) == 16) {
                                subBuilder2 = this.poly_.toBuilder();
                            }
                            this.poly_ = ((PolyModel) input.readMessage(PolyModel.PARSER, extensionRegistry));
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.poly_);
                                this.poly_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 16;
                            break;
                        case 50:
                            ItemCraft_Other.SendItemCraftList.unknownModel2.Builder subBuilder3 = null;
                            if ((this.bitField0_ & 0x20) == 32) {
                                subBuilder3 = this.unknown2_.toBuilder();
                            }
                            this.unknown2_ = ((unknownModel2) input.readMessage(unknownModel2.PARSER, extensionRegistry));
                            if (subBuilder3 != null) {
                                subBuilder3.mergeFrom(this.unknown2_);
                                this.unknown2_ = subBuilder3.buildPartial();
                            }
                            this.bitField0_ |= 32;
                            break;
                        case 58:
                            ItemCraft_Other.SendItemCraftList.Material.Builder subBuilder4 = null;
                            if ((this.bitField0_ & 0x40) == 64) {
                                subBuilder4 = this.material_.toBuilder();
                            }
                            this.material_ = ((Material) input.readMessage(Material.PARSER, extensionRegistry));
                            if (subBuilder4 != null) {
                                subBuilder4.mergeFrom(this.material_);
                                this.material_ = subBuilder4.buildPartial();
                            }
                            this.bitField0_ |= 64;
                            break;
                        case 66:
                            ItemCraft_Other.SendItemCraftList.CraftResults.Builder subBuilder5 = null;
                            if ((this.bitField0_ & 0x80) == 128) {
                                subBuilder5 = this.results_.toBuilder();
                            }
                            this.results_ = ((CraftResults) input.readMessage(CraftResults.PARSER, extensionRegistry));
                            if (subBuilder5 != null) {
                                subBuilder5.mergeFrom(this.results_);
                                this.results_ = subBuilder5.buildPartial();
                            }
                            this.bitField0_ |= 128;
                            break;
                        case 72:
                            this.bitField0_ |= 256;
                            this.craftDelayTime_ = input.readInt32();
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

        public static final Descriptors.Descriptor getDescriptor() {
            return ItemCraft_Other.SendItemCraftList_descriptor;
        }

        public static SendItemCraftList parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (SendItemCraftList) PARSER.parseFrom(data);
        }

        public static SendItemCraftList parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SendItemCraftList) PARSER.parseFrom(data, extensionRegistry);
        }

        public static SendItemCraftList parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (SendItemCraftList) PARSER.parseFrom(data);
        }

        public static SendItemCraftList parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SendItemCraftList) PARSER.parseFrom(data, extensionRegistry);
        }

        public static SendItemCraftList parseFrom(InputStream input) throws IOException {
            return (SendItemCraftList) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static SendItemCraftList parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SendItemCraftList) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static SendItemCraftList parseDelimitedFrom(InputStream input) throws IOException {
            return (SendItemCraftList) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static SendItemCraftList parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SendItemCraftList) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static SendItemCraftList parseFrom(CodedInputStream input) throws IOException {
            return (SendItemCraftList) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static SendItemCraftList parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SendItemCraftList) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(SendItemCraftList prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static SendItemCraftList getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SendItemCraftList> parser() {
            return PARSER;
        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return ItemCraft_Other.SendItemCraftList_fieldAccessorTable.ensureFieldAccessorsInitialized(SendItemCraftList.class, Builder.class);
        }

        public boolean hasActionid() {
            return (this.bitField0_ & 0x1) == 1;
        }

        public int getActionid() {
            return this.actionid_;
        }

        public boolean hasCondition() {
            return (this.bitField0_ & 0x2) == 2;
        }

        public CraftItemCondition getCondition() {
            return this.condition_ == null ? CraftItemCondition.getDefaultInstance() : this.condition_;
        }

        public CraftItemConditionOrBuilder getConditionOrBuilder() {
            return this.condition_ == null ? CraftItemCondition.getDefaultInstance() : this.condition_;
        }

        public boolean hasUnknown1() {
            return (this.bitField0_ & 0x4) == 4;
        }

        public int getUnknown1() {
            return this.unknown1_;
        }

        public boolean hasQuest() {
            return (this.bitField0_ & 0x8) == 8;
        }

        public unQuest getQuest() {
            return this.quest_ == null ? unQuest.getDefaultInstance() : this.quest_;
        }

        public unQuestOrBuilder getQuestOrBuilder() {
            return this.quest_ == null ? unQuest.getDefaultInstance() : this.quest_;
        }

        public boolean hasPoly() {
            return (this.bitField0_ & 0x10) == 16;
        }

        public PolyModel getPoly() {
            return this.poly_ == null ? PolyModel.getDefaultInstance() : this.poly_;
        }

        public PolyModelOrBuilder getPolyOrBuilder() {
            return this.poly_ == null ? PolyModel.getDefaultInstance() : this.poly_;
        }

        public boolean hasUnknown2() {
            return (this.bitField0_ & 0x20) == 32;
        }

        public unknownModel2 getUnknown2() {
            return this.unknown2_ == null ? unknownModel2.getDefaultInstance() : this.unknown2_;
        }

        public unknownModel2OrBuilder getUnknown2OrBuilder() {
            return this.unknown2_ == null ? unknownModel2.getDefaultInstance() : this.unknown2_;
        }

        public boolean hasMaterial() {
            return (this.bitField0_ & 0x40) == 64;
        }

        public Material getMaterial() {
            return this.material_ == null ? Material.getDefaultInstance() : this.material_;
        }

        public MaterialOrBuilder getMaterialOrBuilder() {
            return this.material_ == null ? Material.getDefaultInstance() : this.material_;
        }

        public boolean hasResults() {
            return (this.bitField0_ & 0x80) == 128;
        }

        public CraftResults getResults() {
            return this.results_ == null ? CraftResults.getDefaultInstance() : this.results_;
        }

        public CraftResultsOrBuilder getResultsOrBuilder() {
            return this.results_ == null ? CraftResults.getDefaultInstance() : this.results_;
        }

        public boolean hasCraftDelayTime() {
            return (this.bitField0_ & 0x100) == 256;
        }

        public int getCraftDelayTime() {
            return this.craftDelayTime_;
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasActionid()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!hasCondition()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!hasUnknown1()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!hasQuest()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!hasPoly()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!hasUnknown2()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!hasMaterial()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!hasResults()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!hasCraftDelayTime()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!getCondition().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!getQuest().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!getPoly().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!getUnknown2().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!getMaterial().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!getResults().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 0x1) == 1) {
                output.writeInt32(1, this.actionid_);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                output.writeMessage(2, getCondition());
            }
            if ((this.bitField0_ & 0x4) == 4) {
                output.writeInt32(3, this.unknown1_);
            }
            if ((this.bitField0_ & 0x8) == 8) {
                output.writeMessage(4, getQuest());
            }
            if ((this.bitField0_ & 0x10) == 16) {
                output.writeMessage(5, getPoly());
            }
            if ((this.bitField0_ & 0x20) == 32) {
                output.writeMessage(6, getUnknown2());
            }
            if ((this.bitField0_ & 0x40) == 64) {
                output.writeMessage(7, getMaterial());
            }
            if ((this.bitField0_ & 0x80) == 128) {
                output.writeMessage(8, getResults());
            }
            if ((this.bitField0_ & 0x100) == 256) {
                output.writeInt32(9, this.craftDelayTime_);
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
                size = size + CodedOutputStream.computeInt32Size(1, this.actionid_);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                size = size + CodedOutputStream.computeMessageSize(2, getCondition());
            }
            if ((this.bitField0_ & 0x4) == 4) {
                size = size + CodedOutputStream.computeInt32Size(3, this.unknown1_);
            }
            if ((this.bitField0_ & 0x8) == 8) {
                size = size + CodedOutputStream.computeMessageSize(4, getQuest());
            }
            if ((this.bitField0_ & 0x10) == 16) {
                size = size + CodedOutputStream.computeMessageSize(5, getPoly());
            }
            if ((this.bitField0_ & 0x20) == 32) {
                size = size + CodedOutputStream.computeMessageSize(6, getUnknown2());
            }
            if ((this.bitField0_ & 0x40) == 64) {
                size = size + CodedOutputStream.computeMessageSize(7, getMaterial());
            }
            if ((this.bitField0_ & 0x80) == 128) {
                size = size + CodedOutputStream.computeMessageSize(8, getResults());
            }
            if ((this.bitField0_ & 0x100) == 256) {
                size = size + CodedOutputStream.computeInt32Size(9, this.craftDelayTime_);
            }
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SendItemCraftList)) {
                return super.equals(obj);
            }
            SendItemCraftList other = (SendItemCraftList) obj;
            boolean result = true;
            result = (result) && (hasActionid() == other.hasActionid());
            if (hasActionid()) {
                result = (result) && (getActionid() == other.getActionid());
            }
            result = (result) && (hasCondition() == other.hasCondition());
            if (hasCondition()) {
                result = (result) && (getCondition().equals(other.getCondition()));
            }
            result = (result) && (hasUnknown1() == other.hasUnknown1());
            if (hasUnknown1()) {
                result = (result) && (getUnknown1() == other.getUnknown1());
            }
            result = (result) && (hasQuest() == other.hasQuest());
            if (hasQuest()) {
                result = (result) && (getQuest().equals(other.getQuest()));
            }
            result = (result) && (hasPoly() == other.hasPoly());
            if (hasPoly()) {
                result = (result) && (getPoly().equals(other.getPoly()));
            }
            result = (result) && (hasUnknown2() == other.hasUnknown2());
            if (hasUnknown2()) {
                result = (result) && (getUnknown2().equals(other.getUnknown2()));
            }
            result = (result) && (hasMaterial() == other.hasMaterial());
            if (hasMaterial()) {
                result = (result) && (getMaterial().equals(other.getMaterial()));
            }
            result = (result) && (hasResults() == other.hasResults());
            if (hasResults()) {
                result = (result) && (getResults().equals(other.getResults()));
            }
            result = (result) && (hasCraftDelayTime() == other.hasCraftDelayTime());
            if (hasCraftDelayTime()) {
                result = (result) && (getCraftDelayTime() == other.getCraftDelayTime());
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
            if (hasActionid()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + getActionid();
            }
            if (hasCondition()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + getCondition().hashCode();
            }
            if (hasUnknown1()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + getUnknown1();
            }
            if (hasQuest()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + getQuest().hashCode();
            }
            if (hasPoly()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + getPoly().hashCode();
            }
            if (hasUnknown2()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + getUnknown2().hashCode();
            }
            if (hasMaterial()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + getMaterial().hashCode();
            }
            if (hasResults()) {
                hash = 37 * hash + 8;
                hash = 53 * hash + getResults().hashCode();
            }
            if (hasCraftDelayTime()) {
                hash = 37 * hash + 9;
                hash = 53 * hash + getCraftDelayTime();
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

        public Parser<SendItemCraftList> getParserForType() {
            return PARSER;
        }

        public SendItemCraftList getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static abstract interface CraftItemConditionOrBuilder extends MessageOrBuilder {
            public abstract boolean hasNameId();

            public abstract int getNameId();

            public abstract boolean hasMinLevel();

            public abstract int getMinLevel();

            public abstract boolean hasMaxLevel();

            public abstract int getMaxLevel();

            public abstract boolean hasUnknown1();

            public abstract int getUnknown1();

            public abstract boolean hasMinLawful();

            public abstract int getMinLawful();

            public abstract boolean hasMaxLawful();

            public abstract int getMaxLawful();

            public abstract boolean hasMinKarma();

            public abstract int getMinKarma();

            public abstract boolean hasMaxKarma();

            public abstract int getMaxKarma();

            public abstract boolean hasMaxCount();

            public abstract int getMaxCount();

            public abstract boolean hasIsShowChance();

            public abstract int getIsShowChance();
        }

        public static abstract interface CraftResultOrBuilder extends MessageOrBuilder {
            public abstract boolean hasUnknown1();

            public abstract ItemCraft_Other.SendItemCraftList.unknownModel1 getUnknown1();

            public abstract ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder getUnknown1OrBuilder();

            public abstract boolean hasUnknown2();

            public abstract ItemCraft_Other.SendItemCraftList.unknownModel1 getUnknown2();

            public abstract ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder getUnknown2OrBuilder();

            public abstract boolean hasItemSize();

            public abstract int getItemSize();

            public abstract boolean hasItemtype();

            public abstract int getItemtype();

            public abstract List<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel> getSingleCraftItemList();

            public abstract ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel getSingleCraftItem(int paramInt);

            public abstract int getSingleCraftItemCount();

            public abstract List<? extends ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder> getSingleCraftItemOrBuilderList();

            public abstract ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder getSingleCraftItemOrBuilder(int paramInt);

            public abstract List<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel> getRandomCraftItemList();

            public abstract ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel getRandomCraftItem(int paramInt);

            public abstract int getRandomCraftItemCount();

            public abstract List<? extends ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder> getRandomCraftItemOrBuilderList();

            public abstract ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder getRandomCraftItemOrBuilder(int paramInt);

            public abstract boolean hasGreatItem();

            public abstract ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem getGreatItem();

            public abstract ItemCraft_Other.SendItemCraftList.CraftResult.GreatItemOrBuilder getGreatItemOrBuilder();
        }

        public static abstract interface CraftResultsOrBuilder extends MessageOrBuilder {
            public abstract boolean hasSucceedList();

            public abstract ItemCraft_Other.SendItemCraftList.CraftResult getSucceedList();

            public abstract ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder getSucceedListOrBuilder();

            public abstract boolean hasFailList();

            public abstract ItemCraft_Other.SendItemCraftList.CraftResult getFailList();

            public abstract ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder getFailListOrBuilder();

            public abstract boolean hasSuccessRatio();

            public abstract int getSuccessRatio();
        }

        public static abstract interface MaterialOrBuilder extends MessageOrBuilder {
            public abstract List<ItemCraft_Other.SendItemCraftList.Material.MaterialModel> getMaterialList();

            public abstract ItemCraft_Other.SendItemCraftList.Material.MaterialModel getMaterial(int paramInt);

            public abstract int getMaterialCount();

            public abstract List<? extends ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder> getMaterialOrBuilderList();

            public abstract ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder getMaterialOrBuilder(int paramInt);

            public abstract List<ItemCraft_Other.SendItemCraftList.Material.MaterialModel> getAddMaterialList();

            public abstract ItemCraft_Other.SendItemCraftList.Material.MaterialModel getAddMaterial(int paramInt);

            public abstract int getAddMaterialCount();

            public abstract List<? extends ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder> getAddMaterialOrBuilderList();

            public abstract ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder getAddMaterialOrBuilder(int paramInt);
        }

        public static abstract interface PolyModelOrBuilder extends MessageOrBuilder {
            public abstract boolean hasPcountcount();

            public abstract int getPcountcount();

            public abstract List<Integer> getPolyIdList();

            public abstract int getPolyIdCount();

            public abstract int getPolyId(int paramInt);
        }

        public static abstract interface QuestModelOrBuilder extends MessageOrBuilder {
            public abstract boolean hasQuestId();

            public abstract int getQuestId();

            public abstract boolean hasQuestStepId();

            public abstract int getQuestStepId();
        }

        public static abstract interface unQuestOrBuilder extends MessageOrBuilder {
            public abstract boolean hasUnknown1();

            public abstract int getUnknown1();

            public abstract boolean hasUnknown2();

            public abstract int getUnknown2();

            public abstract boolean hasQuest();

            public abstract ItemCraft_Other.SendItemCraftList.QuestModel getQuest();

            public abstract ItemCraft_Other.SendItemCraftList.QuestModelOrBuilder getQuestOrBuilder();
        }

        public static abstract interface unknownModel1OrBuilder extends MessageOrBuilder {
            public abstract boolean hasUnknow1();

            public abstract long getUnknow1();

            public abstract boolean hasUnknow2();

            public abstract long getUnknow2();
        }

        public static abstract interface unknownModel2OrBuilder extends MessageOrBuilder {
            public abstract boolean hasType();

            public abstract int getType();

            public abstract boolean hasCount();

            public abstract int getCount();

            public abstract List<ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo> getUnList();

            public abstract ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo getUn(int paramInt);

            public abstract int getUnCount();

            public abstract List<? extends ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder> getUnOrBuilderList();

            public abstract ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder getUnOrBuilder(int paramInt);
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftListOrBuilder {
            private int bitField0_;
            private int actionid_;
            private ItemCraft_Other.SendItemCraftList.CraftItemCondition condition_ = null;
            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftItemCondition, ItemCraft_Other.SendItemCraftList.CraftItemCondition.Builder, ItemCraft_Other.SendItemCraftList.CraftItemConditionOrBuilder> conditionBuilder_;
            private int unknown1_;
            private ItemCraft_Other.SendItemCraftList.unQuest quest_ = null;
            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unQuest, ItemCraft_Other.SendItemCraftList.unQuest.Builder, ItemCraft_Other.SendItemCraftList.unQuestOrBuilder> questBuilder_;
            private ItemCraft_Other.SendItemCraftList.PolyModel poly_ = null;
            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.PolyModel, ItemCraft_Other.SendItemCraftList.PolyModel.Builder, ItemCraft_Other.SendItemCraftList.PolyModelOrBuilder> polyBuilder_;
            private ItemCraft_Other.SendItemCraftList.unknownModel2 unknown2_ = null;
            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel2, ItemCraft_Other.SendItemCraftList.unknownModel2.Builder, ItemCraft_Other.SendItemCraftList.unknownModel2OrBuilder> unknown2Builder_;
            private ItemCraft_Other.SendItemCraftList.Material material_ = null;
            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.Material, ItemCraft_Other.SendItemCraftList.Material.Builder, ItemCraft_Other.SendItemCraftList.MaterialOrBuilder> materialBuilder_;
            private ItemCraft_Other.SendItemCraftList.CraftResults results_ = null;
            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResults, ItemCraft_Other.SendItemCraftList.CraftResults.Builder, ItemCraft_Other.SendItemCraftList.CraftResultsOrBuilder> resultsBuilder_;
            private int craftDelayTime_;

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraftList_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraftList_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.class, Builder.class);
            }

            private void maybeForceBuilderInitialization() {
                if (ItemCraft_Other.SendItemCraftList.alwaysUseFieldBuilders) {
                    getConditionFieldBuilder();
                    getQuestFieldBuilder();
                    getPolyFieldBuilder();
                    getUnknown2FieldBuilder();
                    getMaterialFieldBuilder();
                    getResultsFieldBuilder();
                }
            }

            public Builder clear() {
                super.clear();
                this.actionid_ = 0;
                this.bitField0_ &= -2;
                if (this.conditionBuilder_ == null) {
                    this.condition_ = null;
                } else {
                    this.conditionBuilder_.clear();
                }
                this.bitField0_ &= -3;
                this.unknown1_ = 0;
                this.bitField0_ &= -5;
                if (this.questBuilder_ == null) {
                    this.quest_ = null;
                } else {
                    this.questBuilder_.clear();
                }
                this.bitField0_ &= -9;
                if (this.polyBuilder_ == null) {
                    this.poly_ = null;
                } else {
                    this.polyBuilder_.clear();
                }
                this.bitField0_ &= -17;
                if (this.unknown2Builder_ == null) {
                    this.unknown2_ = null;
                } else {
                    this.unknown2Builder_.clear();
                }
                this.bitField0_ &= -33;
                if (this.materialBuilder_ == null) {
                    this.material_ = null;
                } else {
                    this.materialBuilder_.clear();
                }
                this.bitField0_ &= -65;
                if (this.resultsBuilder_ == null) {
                    this.results_ = null;
                } else {
                    this.resultsBuilder_.clear();
                }
                this.bitField0_ &= -129;
                this.craftDelayTime_ = 0;
                this.bitField0_ &= -257;
                return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return ItemCraft_Other.SendItemCraftList_descriptor;
            }

            public ItemCraft_Other.SendItemCraftList getDefaultInstanceForType() {
                return ItemCraft_Other.SendItemCraftList.getDefaultInstance();
            }

            public ItemCraft_Other.SendItemCraftList build() {
                ItemCraft_Other.SendItemCraftList result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public ItemCraft_Other.SendItemCraftList buildPartial() {
                ItemCraft_Other.SendItemCraftList result = new ItemCraft_Other.SendItemCraftList(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 1) {
                    to_bitField0_ |= 1;
                }
                result.actionid_ = this.actionid_;
                if ((from_bitField0_ & 0x2) == 2) {
                    to_bitField0_ |= 2;
                }
                if (this.conditionBuilder_ == null) {
                    result.condition_ = this.condition_;
                } else {
                    result.condition_ = ((ItemCraft_Other.SendItemCraftList.CraftItemCondition) this.conditionBuilder_.build());
                }
                if ((from_bitField0_ & 0x4) == 4) {
                    to_bitField0_ |= 4;
                }
                result.unknown1_ = this.unknown1_;
                if ((from_bitField0_ & 0x8) == 8) {
                    to_bitField0_ |= 8;
                }
                if (this.questBuilder_ == null) {
                    result.quest_ = this.quest_;
                } else {
                    result.quest_ = ((ItemCraft_Other.SendItemCraftList.unQuest) this.questBuilder_.build());
                }
                if ((from_bitField0_ & 0x10) == 16) {
                    to_bitField0_ |= 16;
                }
                if (this.polyBuilder_ == null) {
                    result.poly_ = this.poly_;
                } else {
                    result.poly_ = ((ItemCraft_Other.SendItemCraftList.PolyModel) this.polyBuilder_.build());
                }
                if ((from_bitField0_ & 0x20) == 32) {
                    to_bitField0_ |= 32;
                }
                if (this.unknown2Builder_ == null) {
                    result.unknown2_ = this.unknown2_;
                } else {
                    result.unknown2_ = ((ItemCraft_Other.SendItemCraftList.unknownModel2) this.unknown2Builder_.build());
                }
                if ((from_bitField0_ & 0x40) == 64) {
                    to_bitField0_ |= 64;
                }
                if (this.materialBuilder_ == null) {
                    result.material_ = this.material_;
                } else {
                    result.material_ = ((ItemCraft_Other.SendItemCraftList.Material) this.materialBuilder_.build());
                }
                if ((from_bitField0_ & 0x80) == 128) {
                    to_bitField0_ |= 128;
                }
                if (this.resultsBuilder_ == null) {
                    result.results_ = this.results_;
                } else {
                    result.results_ = ((ItemCraft_Other.SendItemCraftList.CraftResults) this.resultsBuilder_.build());
                }
                if ((from_bitField0_ & 0x100) == 256) {
                    to_bitField0_ |= 256;
                }
                result.craftDelayTime_ = this.craftDelayTime_;
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
                if ((other instanceof ItemCraft_Other.SendItemCraftList)) {
                    return mergeFrom((ItemCraft_Other.SendItemCraftList) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(ItemCraft_Other.SendItemCraftList other) {
                if (other == ItemCraft_Other.SendItemCraftList.getDefaultInstance()) {
                    return this;
                }
                if (other.hasActionid()) {
                    setActionid(other.getActionid());
                }
                if (other.hasCondition()) {
                    mergeCondition(other.getCondition());
                }
                if (other.hasUnknown1()) {
                    setUnknown1(other.getUnknown1());
                }
                if (other.hasQuest()) {
                    mergeQuest(other.getQuest());
                }
                if (other.hasPoly()) {
                    mergePoly(other.getPoly());
                }
                if (other.hasUnknown2()) {
                    mergeUnknown2(other.getUnknown2());
                }
                if (other.hasMaterial()) {
                    mergeMaterial(other.getMaterial());
                }
                if (other.hasResults()) {
                    mergeResults(other.getResults());
                }
                if (other.hasCraftDelayTime()) {
                    setCraftDelayTime(other.getCraftDelayTime());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                if (!hasActionid()) {
                    return false;
                }
                if (!hasCondition()) {
                    return false;
                }
                if (!hasUnknown1()) {
                    return false;
                }
                if (!hasQuest()) {
                    return false;
                }
                if (!hasPoly()) {
                    return false;
                }
                if (!hasUnknown2()) {
                    return false;
                }
                if (!hasMaterial()) {
                    return false;
                }
                if (!hasResults()) {
                    return false;
                }
                if (!hasCraftDelayTime()) {
                    return false;
                }
                if (!getCondition().isInitialized()) {
                    return false;
                }
                if (!getQuest().isInitialized()) {
                    return false;
                }
                if (!getPoly().isInitialized()) {
                    return false;
                }
                if (!getUnknown2().isInitialized()) {
                    return false;
                }
                if (!getMaterial().isInitialized()) {
                    return false;
                }
                if (!getResults().isInitialized()) {
                    return false;
                }
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                ItemCraft_Other.SendItemCraftList parsedMessage = null;
                try {
                    parsedMessage = (ItemCraft_Other.SendItemCraftList) ItemCraft_Other.SendItemCraftList.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ItemCraft_Other.SendItemCraftList) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            public boolean hasActionid() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getActionid() {
                return this.actionid_;
            }

            public Builder setActionid(int value) {
                this.bitField0_ |= 1;
                this.actionid_ = value;
                onChanged();
                return this;
            }

            public Builder clearActionid() {
                this.bitField0_ &= -2;
                this.actionid_ = 0;
                onChanged();
                return this;
            }

            public boolean hasCondition() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public ItemCraft_Other.SendItemCraftList.CraftItemCondition getCondition() {
                if (this.conditionBuilder_ == null) {
                    return this.condition_ == null ? ItemCraft_Other.SendItemCraftList.CraftItemCondition.getDefaultInstance() : this.condition_;
                }
                return (ItemCraft_Other.SendItemCraftList.CraftItemCondition) this.conditionBuilder_.getMessage();
            }

            public Builder setCondition(ItemCraft_Other.SendItemCraftList.CraftItemCondition value) {
                if (this.conditionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.condition_ = value;
                    onChanged();
                } else {
                    this.conditionBuilder_.setMessage(value);
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder setCondition(ItemCraft_Other.SendItemCraftList.CraftItemCondition.Builder builderForValue) {
                if (this.conditionBuilder_ == null) {
                    this.condition_ = builderForValue.build();
                    onChanged();
                } else {
                    this.conditionBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder mergeCondition(ItemCraft_Other.SendItemCraftList.CraftItemCondition value) {
                if (this.conditionBuilder_ == null) {
                    if (((this.bitField0_ & 0x2) == 2) && (this.condition_ != null) && (this.condition_ != ItemCraft_Other.SendItemCraftList.CraftItemCondition.getDefaultInstance())) {
                        this.condition_ = ItemCraft_Other.SendItemCraftList.CraftItemCondition.newBuilder(this.condition_).mergeFrom(value).buildPartial();
                    } else {
                        this.condition_ = value;
                    }
                    onChanged();
                } else {
                    this.conditionBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder clearCondition() {
                if (this.conditionBuilder_ == null) {
                    this.condition_ = null;
                    onChanged();
                } else {
                    this.conditionBuilder_.clear();
                }
                this.bitField0_ &= -3;
                return this;
            }

            public ItemCraft_Other.SendItemCraftList.CraftItemCondition.Builder getConditionBuilder() {
                this.bitField0_ |= 2;
                onChanged();
                return (ItemCraft_Other.SendItemCraftList.CraftItemCondition.Builder) getConditionFieldBuilder().getBuilder();
            }

            public ItemCraft_Other.SendItemCraftList.CraftItemConditionOrBuilder getConditionOrBuilder() {
                if (this.conditionBuilder_ != null) {
                    return (ItemCraft_Other.SendItemCraftList.CraftItemConditionOrBuilder) this.conditionBuilder_.getMessageOrBuilder();
                }
                return this.condition_ == null ? ItemCraft_Other.SendItemCraftList.CraftItemCondition.getDefaultInstance() : this.condition_;
            }

            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftItemCondition, ItemCraft_Other.SendItemCraftList.CraftItemCondition.Builder, ItemCraft_Other.SendItemCraftList.CraftItemConditionOrBuilder> getConditionFieldBuilder() {
                if (this.conditionBuilder_ == null) {
                    this.conditionBuilder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftItemCondition, ItemCraft_Other.SendItemCraftList.CraftItemCondition.Builder, ItemCraft_Other.SendItemCraftList.CraftItemConditionOrBuilder>(getCondition(), getParentForChildren(), isClean());
                    this.condition_ = null;
                }
                return this.conditionBuilder_;
            }

            public boolean hasUnknown1() {
                return (this.bitField0_ & 0x4) == 4;
            }

            public int getUnknown1() {
                return this.unknown1_;
            }

            public Builder setUnknown1(int value) {
                this.bitField0_ |= 4;
                this.unknown1_ = value;
                onChanged();
                return this;
            }

            public Builder clearUnknown1() {
                this.bitField0_ &= -5;
                this.unknown1_ = 0;
                onChanged();
                return this;
            }

            public boolean hasQuest() {
                return (this.bitField0_ & 0x8) == 8;
            }

            public ItemCraft_Other.SendItemCraftList.unQuest getQuest() {
                if (this.questBuilder_ == null) {
                    return this.quest_ == null ? ItemCraft_Other.SendItemCraftList.unQuest.getDefaultInstance() : this.quest_;
                }
                return (ItemCraft_Other.SendItemCraftList.unQuest) this.questBuilder_.getMessage();
            }

            public Builder setQuest(ItemCraft_Other.SendItemCraftList.unQuest value) {
                if (this.questBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.quest_ = value;
                    onChanged();
                } else {
                    this.questBuilder_.setMessage(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder setQuest(ItemCraft_Other.SendItemCraftList.unQuest.Builder builderForValue) {
                if (this.questBuilder_ == null) {
                    this.quest_ = builderForValue.build();
                    onChanged();
                } else {
                    this.questBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder mergeQuest(ItemCraft_Other.SendItemCraftList.unQuest value) {
                if (this.questBuilder_ == null) {
                    if (((this.bitField0_ & 0x8) == 8) && (this.quest_ != null) && (this.quest_ != ItemCraft_Other.SendItemCraftList.unQuest.getDefaultInstance())) {
                        this.quest_ = ItemCraft_Other.SendItemCraftList.unQuest.newBuilder(this.quest_).mergeFrom(value).buildPartial();
                    } else {
                        this.quest_ = value;
                    }
                    onChanged();
                } else {
                    this.questBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder clearQuest() {
                if (this.questBuilder_ == null) {
                    this.quest_ = null;
                    onChanged();
                } else {
                    this.questBuilder_.clear();
                }
                this.bitField0_ &= -9;
                return this;
            }

            public ItemCraft_Other.SendItemCraftList.unQuest.Builder getQuestBuilder() {
                this.bitField0_ |= 8;
                onChanged();
                return (ItemCraft_Other.SendItemCraftList.unQuest.Builder) getQuestFieldBuilder().getBuilder();
            }

            public ItemCraft_Other.SendItemCraftList.unQuestOrBuilder getQuestOrBuilder() {
                if (this.questBuilder_ != null) {
                    return (ItemCraft_Other.SendItemCraftList.unQuestOrBuilder) this.questBuilder_.getMessageOrBuilder();
                }
                return this.quest_ == null ? ItemCraft_Other.SendItemCraftList.unQuest.getDefaultInstance() : this.quest_;
            }

            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unQuest, ItemCraft_Other.SendItemCraftList.unQuest.Builder, ItemCraft_Other.SendItemCraftList.unQuestOrBuilder> getQuestFieldBuilder() {
                if (this.questBuilder_ == null) {
                    this.questBuilder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unQuest, ItemCraft_Other.SendItemCraftList.unQuest.Builder, ItemCraft_Other.SendItemCraftList.unQuestOrBuilder>(getQuest(), getParentForChildren(), isClean());
                    this.quest_ = null;
                }
                return this.questBuilder_;
            }

            public boolean hasPoly() {
                return (this.bitField0_ & 0x10) == 16;
            }

            public ItemCraft_Other.SendItemCraftList.PolyModel getPoly() {
                if (this.polyBuilder_ == null) {
                    return this.poly_ == null ? ItemCraft_Other.SendItemCraftList.PolyModel.getDefaultInstance() : this.poly_;
                }
                return (ItemCraft_Other.SendItemCraftList.PolyModel) this.polyBuilder_.getMessage();
            }

            public Builder setPoly(ItemCraft_Other.SendItemCraftList.PolyModel value) {
                if (this.polyBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.poly_ = value;
                    onChanged();
                } else {
                    this.polyBuilder_.setMessage(value);
                }
                this.bitField0_ |= 16;
                return this;
            }

            public Builder setPoly(ItemCraft_Other.SendItemCraftList.PolyModel.Builder builderForValue) {
                if (this.polyBuilder_ == null) {
                    this.poly_ = builderForValue.build();
                    onChanged();
                } else {
                    this.polyBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 16;
                return this;
            }

            public Builder mergePoly(ItemCraft_Other.SendItemCraftList.PolyModel value) {
                if (this.polyBuilder_ == null) {
                    if (((this.bitField0_ & 0x10) == 16) && (this.poly_ != null) && (this.poly_ != ItemCraft_Other.SendItemCraftList.PolyModel.getDefaultInstance())) {
                        this.poly_ = ItemCraft_Other.SendItemCraftList.PolyModel.newBuilder(this.poly_).mergeFrom(value).buildPartial();
                    } else {
                        this.poly_ = value;
                    }
                    onChanged();
                } else {
                    this.polyBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 16;
                return this;
            }

            public Builder clearPoly() {
                if (this.polyBuilder_ == null) {
                    this.poly_ = null;
                    onChanged();
                } else {
                    this.polyBuilder_.clear();
                }
                this.bitField0_ &= -17;
                return this;
            }

            public ItemCraft_Other.SendItemCraftList.PolyModel.Builder getPolyBuilder() {
                this.bitField0_ |= 16;
                onChanged();
                return (ItemCraft_Other.SendItemCraftList.PolyModel.Builder) getPolyFieldBuilder().getBuilder();
            }

            public ItemCraft_Other.SendItemCraftList.PolyModelOrBuilder getPolyOrBuilder() {
                if (this.polyBuilder_ != null) {
                    return (ItemCraft_Other.SendItemCraftList.PolyModelOrBuilder) this.polyBuilder_.getMessageOrBuilder();
                }
                return this.poly_ == null ? ItemCraft_Other.SendItemCraftList.PolyModel.getDefaultInstance() : this.poly_;
            }

            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.PolyModel, ItemCraft_Other.SendItemCraftList.PolyModel.Builder, ItemCraft_Other.SendItemCraftList.PolyModelOrBuilder> getPolyFieldBuilder() {
                if (this.polyBuilder_ == null) {
                    this.polyBuilder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.PolyModel, ItemCraft_Other.SendItemCraftList.PolyModel.Builder, ItemCraft_Other.SendItemCraftList.PolyModelOrBuilder>(getPoly(), getParentForChildren(), isClean());
                    this.poly_ = null;
                }
                return this.polyBuilder_;
            }

            public boolean hasUnknown2() {
                return (this.bitField0_ & 0x20) == 32;
            }

            public ItemCraft_Other.SendItemCraftList.unknownModel2 getUnknown2() {
                if (this.unknown2Builder_ == null) {
                    return this.unknown2_ == null ? ItemCraft_Other.SendItemCraftList.unknownModel2.getDefaultInstance() : this.unknown2_;
                }
                return (ItemCraft_Other.SendItemCraftList.unknownModel2) this.unknown2Builder_.getMessage();
            }

            public Builder setUnknown2(ItemCraft_Other.SendItemCraftList.unknownModel2 value) {
                if (this.unknown2Builder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.unknown2_ = value;
                    onChanged();
                } else {
                    this.unknown2Builder_.setMessage(value);
                }
                this.bitField0_ |= 32;
                return this;
            }

            public Builder setUnknown2(ItemCraft_Other.SendItemCraftList.unknownModel2.Builder builderForValue) {
                if (this.unknown2Builder_ == null) {
                    this.unknown2_ = builderForValue.build();
                    onChanged();
                } else {
                    this.unknown2Builder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 32;
                return this;
            }

            public Builder mergeUnknown2(ItemCraft_Other.SendItemCraftList.unknownModel2 value) {
                if (this.unknown2Builder_ == null) {
                    if (((this.bitField0_ & 0x20) == 32) && (this.unknown2_ != null) && (this.unknown2_ != ItemCraft_Other.SendItemCraftList.unknownModel2.getDefaultInstance())) {
                        this.unknown2_ = ItemCraft_Other.SendItemCraftList.unknownModel2.newBuilder(this.unknown2_).mergeFrom(value).buildPartial();
                    } else {
                        this.unknown2_ = value;
                    }
                    onChanged();
                } else {
                    this.unknown2Builder_.mergeFrom(value);
                }
                this.bitField0_ |= 32;
                return this;
            }

            public Builder clearUnknown2() {
                if (this.unknown2Builder_ == null) {
                    this.unknown2_ = null;
                    onChanged();
                } else {
                    this.unknown2Builder_.clear();
                }
                this.bitField0_ &= -33;
                return this;
            }

            public ItemCraft_Other.SendItemCraftList.unknownModel2.Builder getUnknown2Builder() {
                this.bitField0_ |= 32;
                onChanged();
                return (ItemCraft_Other.SendItemCraftList.unknownModel2.Builder) getUnknown2FieldBuilder().getBuilder();
            }

            public ItemCraft_Other.SendItemCraftList.unknownModel2OrBuilder getUnknown2OrBuilder() {
                if (this.unknown2Builder_ != null) {
                    return (ItemCraft_Other.SendItemCraftList.unknownModel2OrBuilder) this.unknown2Builder_.getMessageOrBuilder();
                }
                return this.unknown2_ == null ? ItemCraft_Other.SendItemCraftList.unknownModel2.getDefaultInstance() : this.unknown2_;
            }

            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel2, ItemCraft_Other.SendItemCraftList.unknownModel2.Builder, ItemCraft_Other.SendItemCraftList.unknownModel2OrBuilder> getUnknown2FieldBuilder() {
                if (this.unknown2Builder_ == null) {
                    this.unknown2Builder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel2, ItemCraft_Other.SendItemCraftList.unknownModel2.Builder, ItemCraft_Other.SendItemCraftList.unknownModel2OrBuilder>(getUnknown2(), getParentForChildren(), isClean());
                    this.unknown2_ = null;
                }
                return this.unknown2Builder_;
            }

            public boolean hasMaterial() {
                return (this.bitField0_ & 0x40) == 64;
            }

            public ItemCraft_Other.SendItemCraftList.Material getMaterial() {
                if (this.materialBuilder_ == null) {
                    return this.material_ == null ? ItemCraft_Other.SendItemCraftList.Material.getDefaultInstance() : this.material_;
                }
                return (ItemCraft_Other.SendItemCraftList.Material) this.materialBuilder_.getMessage();
            }

            public Builder setMaterial(ItemCraft_Other.SendItemCraftList.Material value) {
                if (this.materialBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.material_ = value;
                    onChanged();
                } else {
                    this.materialBuilder_.setMessage(value);
                }
                this.bitField0_ |= 64;
                return this;
            }

            public Builder setMaterial(ItemCraft_Other.SendItemCraftList.Material.Builder builderForValue) {
                if (this.materialBuilder_ == null) {
                    this.material_ = builderForValue.build();
                    onChanged();
                } else {
                    this.materialBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 64;
                return this;
            }

            public Builder mergeMaterial(ItemCraft_Other.SendItemCraftList.Material value) {
                if (this.materialBuilder_ == null) {
                    if (((this.bitField0_ & 0x40) == 64) && (this.material_ != null) && (this.material_ != ItemCraft_Other.SendItemCraftList.Material.getDefaultInstance())) {
                        this.material_ = ItemCraft_Other.SendItemCraftList.Material.newBuilder(this.material_).mergeFrom(value).buildPartial();
                    } else {
                        this.material_ = value;
                    }
                    onChanged();
                } else {
                    this.materialBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 64;
                return this;
            }

            public Builder clearMaterial() {
                if (this.materialBuilder_ == null) {
                    this.material_ = null;
                    onChanged();
                } else {
                    this.materialBuilder_.clear();
                }
                this.bitField0_ &= -65;
                return this;
            }

            public ItemCraft_Other.SendItemCraftList.Material.Builder getMaterialBuilder() {
                this.bitField0_ |= 64;
                onChanged();
                return (ItemCraft_Other.SendItemCraftList.Material.Builder) getMaterialFieldBuilder().getBuilder();
            }

            public ItemCraft_Other.SendItemCraftList.MaterialOrBuilder getMaterialOrBuilder() {
                if (this.materialBuilder_ != null) {
                    return (ItemCraft_Other.SendItemCraftList.MaterialOrBuilder) this.materialBuilder_.getMessageOrBuilder();
                }
                return this.material_ == null ? ItemCraft_Other.SendItemCraftList.Material.getDefaultInstance() : this.material_;
            }

            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.Material, ItemCraft_Other.SendItemCraftList.Material.Builder, ItemCraft_Other.SendItemCraftList.MaterialOrBuilder> getMaterialFieldBuilder() {
                if (this.materialBuilder_ == null) {
                    this.materialBuilder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.Material, ItemCraft_Other.SendItemCraftList.Material.Builder, ItemCraft_Other.SendItemCraftList.MaterialOrBuilder>(getMaterial(), getParentForChildren(), isClean());
                    this.material_ = null;
                }
                return this.materialBuilder_;
            }

            public boolean hasResults() {
                return (this.bitField0_ & 0x80) == 128;
            }

            public ItemCraft_Other.SendItemCraftList.CraftResults getResults() {
                if (this.resultsBuilder_ == null) {
                    return this.results_ == null ? ItemCraft_Other.SendItemCraftList.CraftResults.getDefaultInstance() : this.results_;
                }
                return (ItemCraft_Other.SendItemCraftList.CraftResults) this.resultsBuilder_.getMessage();
            }

            public Builder setResults(ItemCraft_Other.SendItemCraftList.CraftResults value) {
                if (this.resultsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.results_ = value;
                    onChanged();
                } else {
                    this.resultsBuilder_.setMessage(value);
                }
                this.bitField0_ |= 128;
                return this;
            }

            public Builder setResults(ItemCraft_Other.SendItemCraftList.CraftResults.Builder builderForValue) {
                if (this.resultsBuilder_ == null) {
                    this.results_ = builderForValue.build();
                    onChanged();
                } else {
                    this.resultsBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 128;
                return this;
            }

            public Builder mergeResults(ItemCraft_Other.SendItemCraftList.CraftResults value) {
                if (this.resultsBuilder_ == null) {
                    if (((this.bitField0_ & 0x80) == 128) && (this.results_ != null) && (this.results_ != ItemCraft_Other.SendItemCraftList.CraftResults.getDefaultInstance())) {
                        this.results_ = ItemCraft_Other.SendItemCraftList.CraftResults.newBuilder(this.results_).mergeFrom(value).buildPartial();
                    } else {
                        this.results_ = value;
                    }
                    onChanged();
                } else {
                    this.resultsBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 128;
                return this;
            }

            public Builder clearResults() {
                if (this.resultsBuilder_ == null) {
                    this.results_ = null;
                    onChanged();
                } else {
                    this.resultsBuilder_.clear();
                }
                this.bitField0_ &= -129;
                return this;
            }

            public ItemCraft_Other.SendItemCraftList.CraftResults.Builder getResultsBuilder() {
                this.bitField0_ |= 128;
                onChanged();
                return (ItemCraft_Other.SendItemCraftList.CraftResults.Builder) getResultsFieldBuilder().getBuilder();
            }

            public ItemCraft_Other.SendItemCraftList.CraftResultsOrBuilder getResultsOrBuilder() {
                if (this.resultsBuilder_ != null) {
                    return (ItemCraft_Other.SendItemCraftList.CraftResultsOrBuilder) this.resultsBuilder_.getMessageOrBuilder();
                }
                return this.results_ == null ? ItemCraft_Other.SendItemCraftList.CraftResults.getDefaultInstance() : this.results_;
            }

            private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResults, ItemCraft_Other.SendItemCraftList.CraftResults.Builder, ItemCraft_Other.SendItemCraftList.CraftResultsOrBuilder> getResultsFieldBuilder() {
                if (this.resultsBuilder_ == null) {
                    this.resultsBuilder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResults, ItemCraft_Other.SendItemCraftList.CraftResults.Builder, ItemCraft_Other.SendItemCraftList.CraftResultsOrBuilder>(getResults(), getParentForChildren(), isClean());
                    this.results_ = null;
                }
                return this.resultsBuilder_;
            }

            public boolean hasCraftDelayTime() {
                return (this.bitField0_ & 0x100) == 256;
            }

            public int getCraftDelayTime() {
                return this.craftDelayTime_;
            }

            public Builder setCraftDelayTime(int value) {
                this.bitField0_ |= 256;
                this.craftDelayTime_ = value;
                onChanged();
                return this;
            }

            public Builder clearCraftDelayTime() {
                this.bitField0_ &= -257;
                this.craftDelayTime_ = 0;
                onChanged();
                return this;
            }

            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static final class CraftItemCondition extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.CraftItemConditionOrBuilder {
            public static final int NAMEID_FIELD_NUMBER = 1;
            public static final int MINLEVEL_FIELD_NUMBER = 2;
            public static final int MAXLEVEL_FIELD_NUMBER = 3;
            public static final int UNKNOWN1_FIELD_NUMBER = 4;
            public static final int MINLAWFUL_FIELD_NUMBER = 5;
            public static final int MAXLAWFUL_FIELD_NUMBER = 6;
            public static final int MINKARMA_FIELD_NUMBER = 7;
            public static final int MAXKARMA_FIELD_NUMBER = 8;
            public static final int MAXCOUNT_FIELD_NUMBER = 9;
            public static final int ISSHOWCHANCE_FIELD_NUMBER = 10;
            @Deprecated
            public static final Parser<CraftItemCondition> PARSER = new AbstractParser<CraftItemCondition>() {
                public ItemCraft_Other.SendItemCraftList.CraftItemCondition parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ItemCraft_Other.SendItemCraftList.CraftItemCondition(input, extensionRegistry);
                }
            };
            private static final long serialVersionUID = 0L;
            private static final CraftItemCondition DEFAULT_INSTANCE = new CraftItemCondition();
            private int bitField0_;
            private int nameId_;
            private int minLevel_;
            private int maxLevel_;
            private int unknown1_;
            private int minLawful_;
            private int maxLawful_;
            private int minKarma_;
            private int maxKarma_;
            private int maxCount_;
            private int isShowChance_;
            private byte memoizedIsInitialized = -1;

            private CraftItemCondition(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private CraftItemCondition() {
                this.nameId_ = 0;
                this.minLevel_ = 0;
                this.maxLevel_ = 0;
                this.unknown1_ = 0;
                this.minLawful_ = 0;
                this.maxLawful_ = 0;
                this.minKarma_ = 0;
                this.maxKarma_ = 0;
                this.maxCount_ = 0;
                this.isShowChance_ = 0;
            }

            private CraftItemCondition(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.nameId_ = input.readInt32();
                                break;
                            case 16:
                                this.bitField0_ |= 2;
                                this.minLevel_ = input.readInt32();
                                break;
                            case 24:
                                this.bitField0_ |= 4;
                                this.maxLevel_ = input.readInt32();
                                break;
                            case 32:
                                this.bitField0_ |= 8;
                                this.unknown1_ = input.readInt32();
                                break;
                            case 40:
                                this.bitField0_ |= 16;
                                this.minLawful_ = input.readInt32();
                                break;
                            case 48:
                                this.bitField0_ |= 32;
                                this.maxLawful_ = input.readInt32();
                                break;
                            case 56:
                                this.bitField0_ |= 64;
                                this.minKarma_ = input.readInt32();
                                break;
                            case 64:
                                this.bitField0_ |= 128;
                                this.maxKarma_ = input.readInt32();
                                break;
                            case 72:
                                this.bitField0_ |= 256;
                                this.maxCount_ = input.readInt32();
                                break;
                            case 80:
                                this.bitField0_ |= 512;
                                this.isShowChance_ = input.readInt32();
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

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraftList_CraftItemCondition_descriptor;
            }

            public static CraftItemCondition parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return (CraftItemCondition) PARSER.parseFrom(data);
            }

            public static CraftItemCondition parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (CraftItemCondition) PARSER.parseFrom(data, extensionRegistry);
            }

            public static CraftItemCondition parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return (CraftItemCondition) PARSER.parseFrom(data);
            }

            public static CraftItemCondition parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (CraftItemCondition) PARSER.parseFrom(data, extensionRegistry);
            }

            public static CraftItemCondition parseFrom(InputStream input) throws IOException {
                return (CraftItemCondition) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static CraftItemCondition parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (CraftItemCondition) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static CraftItemCondition parseDelimitedFrom(InputStream input) throws IOException {
                return (CraftItemCondition) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static CraftItemCondition parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (CraftItemCondition) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static CraftItemCondition parseFrom(CodedInputStream input) throws IOException {
                return (CraftItemCondition) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static CraftItemCondition parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (CraftItemCondition) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(CraftItemCondition prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            public static CraftItemCondition getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<CraftItemCondition> parser() {
                return PARSER;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraftList_CraftItemCondition_fieldAccessorTable.ensureFieldAccessorsInitialized(CraftItemCondition.class, Builder.class);
            }

            public boolean hasNameId() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getNameId() {
                return this.nameId_;
            }

            public boolean hasMinLevel() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public int getMinLevel() {
                return this.minLevel_;
            }

            public boolean hasMaxLevel() {
                return (this.bitField0_ & 0x4) == 4;
            }

            public int getMaxLevel() {
                return this.maxLevel_;
            }

            public boolean hasUnknown1() {
                return (this.bitField0_ & 0x8) == 8;
            }

            public int getUnknown1() {
                return this.unknown1_;
            }

            public boolean hasMinLawful() {
                return (this.bitField0_ & 0x10) == 16;
            }

            public int getMinLawful() {
                return this.minLawful_;
            }

            public boolean hasMaxLawful() {
                return (this.bitField0_ & 0x20) == 32;
            }

            public int getMaxLawful() {
                return this.maxLawful_;
            }

            public boolean hasMinKarma() {
                return (this.bitField0_ & 0x40) == 64;
            }

            public int getMinKarma() {
                return this.minKarma_;
            }

            public boolean hasMaxKarma() {
                return (this.bitField0_ & 0x80) == 128;
            }

            public int getMaxKarma() {
                return this.maxKarma_;
            }

            public boolean hasMaxCount() {
                return (this.bitField0_ & 0x100) == 256;
            }

            public int getMaxCount() {
                return this.maxCount_;
            }

            public boolean hasIsShowChance() {
                return (this.bitField0_ & 0x200) == 512;
            }

            public int getIsShowChance() {
                return this.isShowChance_;
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                if (!hasNameId()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasMinLevel()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasMaxLevel()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasUnknown1()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasMinLawful()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasMaxLawful()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasMinKarma()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasMaxKarma()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasMaxCount()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 0x1) == 1) {
                    output.writeInt32(1, this.nameId_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    output.writeInt32(2, this.minLevel_);
                }
                if ((this.bitField0_ & 0x4) == 4) {
                    output.writeInt32(3, this.maxLevel_);
                }
                if ((this.bitField0_ & 0x8) == 8) {
                    output.writeInt32(4, this.unknown1_);
                }
                if ((this.bitField0_ & 0x10) == 16) {
                    output.writeInt32(5, this.minLawful_);
                }
                if ((this.bitField0_ & 0x20) == 32) {
                    output.writeInt32(6, this.maxLawful_);
                }
                if ((this.bitField0_ & 0x40) == 64) {
                    output.writeInt32(7, this.minKarma_);
                }
                if ((this.bitField0_ & 0x80) == 128) {
                    output.writeInt32(8, this.maxKarma_);
                }
                if ((this.bitField0_ & 0x100) == 256) {
                    output.writeInt32(9, this.maxCount_);
                }
                if ((this.bitField0_ & 0x200) == 512) {
                    output.writeInt32(10, this.isShowChance_);
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
                    size = size + CodedOutputStream.computeInt32Size(1, this.nameId_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    size = size + CodedOutputStream.computeInt32Size(2, this.minLevel_);
                }
                if ((this.bitField0_ & 0x4) == 4) {
                    size = size + CodedOutputStream.computeInt32Size(3, this.maxLevel_);
                }
                if ((this.bitField0_ & 0x8) == 8) {
                    size = size + CodedOutputStream.computeInt32Size(4, this.unknown1_);
                }
                if ((this.bitField0_ & 0x10) == 16) {
                    size = size + CodedOutputStream.computeInt32Size(5, this.minLawful_);
                }
                if ((this.bitField0_ & 0x20) == 32) {
                    size = size + CodedOutputStream.computeInt32Size(6, this.maxLawful_);
                }
                if ((this.bitField0_ & 0x40) == 64) {
                    size = size + CodedOutputStream.computeInt32Size(7, this.minKarma_);
                }
                if ((this.bitField0_ & 0x80) == 128) {
                    size = size + CodedOutputStream.computeInt32Size(8, this.maxKarma_);
                }
                if ((this.bitField0_ & 0x100) == 256) {
                    size = size + CodedOutputStream.computeInt32Size(9, this.maxCount_);
                }
                if ((this.bitField0_ & 0x200) == 512) {
                    size = size + CodedOutputStream.computeInt32Size(10, this.isShowChance_);
                }
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof CraftItemCondition)) {
                    return super.equals(obj);
                }
                CraftItemCondition other = (CraftItemCondition) obj;
                boolean result = true;
                result = (result) && (hasNameId() == other.hasNameId());
                if (hasNameId()) {
                    result = (result) && (getNameId() == other.getNameId());
                }
                result = (result) && (hasMinLevel() == other.hasMinLevel());
                if (hasMinLevel()) {
                    result = (result) && (getMinLevel() == other.getMinLevel());
                }
                result = (result) && (hasMaxLevel() == other.hasMaxLevel());
                if (hasMaxLevel()) {
                    result = (result) && (getMaxLevel() == other.getMaxLevel());
                }
                result = (result) && (hasUnknown1() == other.hasUnknown1());
                if (hasUnknown1()) {
                    result = (result) && (getUnknown1() == other.getUnknown1());
                }
                result = (result) && (hasMinLawful() == other.hasMinLawful());
                if (hasMinLawful()) {
                    result = (result) && (getMinLawful() == other.getMinLawful());
                }
                result = (result) && (hasMaxLawful() == other.hasMaxLawful());
                if (hasMaxLawful()) {
                    result = (result) && (getMaxLawful() == other.getMaxLawful());
                }
                result = (result) && (hasMinKarma() == other.hasMinKarma());
                if (hasMinKarma()) {
                    result = (result) && (getMinKarma() == other.getMinKarma());
                }
                result = (result) && (hasMaxKarma() == other.hasMaxKarma());
                if (hasMaxKarma()) {
                    result = (result) && (getMaxKarma() == other.getMaxKarma());
                }
                result = (result) && (hasMaxCount() == other.hasMaxCount());
                if (hasMaxCount()) {
                    result = (result) && (getMaxCount() == other.getMaxCount());
                }
                result = (result) && (hasIsShowChance() == other.hasIsShowChance());
                if (hasIsShowChance()) {
                    result = (result) && (getIsShowChance() == other.getIsShowChance());
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
                if (hasNameId()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + getNameId();
                }
                if (hasMinLevel()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + getMinLevel();
                }
                if (hasMaxLevel()) {
                    hash = 37 * hash + 3;
                    hash = 53 * hash + getMaxLevel();
                }
                if (hasUnknown1()) {
                    hash = 37 * hash + 4;
                    hash = 53 * hash + getUnknown1();
                }
                if (hasMinLawful()) {
                    hash = 37 * hash + 5;
                    hash = 53 * hash + getMinLawful();
                }
                if (hasMaxLawful()) {
                    hash = 37 * hash + 6;
                    hash = 53 * hash + getMaxLawful();
                }
                if (hasMinKarma()) {
                    hash = 37 * hash + 7;
                    hash = 53 * hash + getMinKarma();
                }
                if (hasMaxKarma()) {
                    hash = 37 * hash + 8;
                    hash = 53 * hash + getMaxKarma();
                }
                if (hasMaxCount()) {
                    hash = 37 * hash + 9;
                    hash = 53 * hash + getMaxCount();
                }
                if (hasIsShowChance()) {
                    hash = 37 * hash + 10;
                    hash = 53 * hash + getIsShowChance();
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

            public Parser<CraftItemCondition> getParserForType() {
                return PARSER;
            }

            public CraftItemCondition getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.CraftItemConditionOrBuilder {
                private int bitField0_;
                private int nameId_;
                private int minLevel_;
                private int maxLevel_;
                private int unknown1_;
                private int minLawful_;
                private int maxLawful_;
                private int minKarma_;
                private int maxKarma_;
                private int maxCount_;
                private int isShowChance_;

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_CraftItemCondition_descriptor;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_CraftItemCondition_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.CraftItemCondition.class, Builder.class);
                }

                private void maybeForceBuilderInitialization() {
                }

                public Builder clear() {
                    super.clear();
                    this.nameId_ = 0;
                    this.bitField0_ &= -2;
                    this.minLevel_ = 0;
                    this.bitField0_ &= -3;
                    this.maxLevel_ = 0;
                    this.bitField0_ &= -5;
                    this.unknown1_ = 0;
                    this.bitField0_ &= -9;
                    this.minLawful_ = 0;
                    this.bitField0_ &= -17;
                    this.maxLawful_ = 0;
                    this.bitField0_ &= -33;
                    this.minKarma_ = 0;
                    this.bitField0_ &= -65;
                    this.maxKarma_ = 0;
                    this.bitField0_ &= -129;
                    this.maxCount_ = 0;
                    this.bitField0_ &= -257;
                    this.isShowChance_ = 0;
                    this.bitField0_ &= -513;
                    return this;
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return ItemCraft_Other.SendItemCraftList_CraftItemCondition_descriptor;
                }

                public ItemCraft_Other.SendItemCraftList.CraftItemCondition getDefaultInstanceForType() {
                    return ItemCraft_Other.SendItemCraftList.CraftItemCondition.getDefaultInstance();
                }

                public ItemCraft_Other.SendItemCraftList.CraftItemCondition build() {
                    ItemCraft_Other.SendItemCraftList.CraftItemCondition result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public ItemCraft_Other.SendItemCraftList.CraftItemCondition buildPartial() {
                    ItemCraft_Other.SendItemCraftList.CraftItemCondition result = new ItemCraft_Other.SendItemCraftList.CraftItemCondition(this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 1) {
                        to_bitField0_ |= 1;
                    }
                    result.nameId_ = this.nameId_;
                    if ((from_bitField0_ & 0x2) == 2) {
                        to_bitField0_ |= 2;
                    }
                    result.minLevel_ = this.minLevel_;
                    if ((from_bitField0_ & 0x4) == 4) {
                        to_bitField0_ |= 4;
                    }
                    result.maxLevel_ = this.maxLevel_;
                    if ((from_bitField0_ & 0x8) == 8) {
                        to_bitField0_ |= 8;
                    }
                    result.unknown1_ = this.unknown1_;
                    if ((from_bitField0_ & 0x10) == 16) {
                        to_bitField0_ |= 16;
                    }
                    result.minLawful_ = this.minLawful_;
                    if ((from_bitField0_ & 0x20) == 32) {
                        to_bitField0_ |= 32;
                    }
                    result.maxLawful_ = this.maxLawful_;
                    if ((from_bitField0_ & 0x40) == 64) {
                        to_bitField0_ |= 64;
                    }
                    result.minKarma_ = this.minKarma_;
                    if ((from_bitField0_ & 0x80) == 128) {
                        to_bitField0_ |= 128;
                    }
                    result.maxKarma_ = this.maxKarma_;
                    if ((from_bitField0_ & 0x100) == 256) {
                        to_bitField0_ |= 256;
                    }
                    result.maxCount_ = this.maxCount_;
                    if ((from_bitField0_ & 0x200) == 512) {
                        to_bitField0_ |= 512;
                    }
                    result.isShowChance_ = this.isShowChance_;
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
                    if ((other instanceof ItemCraft_Other.SendItemCraftList.CraftItemCondition)) {
                        return mergeFrom((ItemCraft_Other.SendItemCraftList.CraftItemCondition) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.CraftItemCondition other) {
                    if (other == ItemCraft_Other.SendItemCraftList.CraftItemCondition.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasNameId()) {
                        setNameId(other.getNameId());
                    }
                    if (other.hasMinLevel()) {
                        setMinLevel(other.getMinLevel());
                    }
                    if (other.hasMaxLevel()) {
                        setMaxLevel(other.getMaxLevel());
                    }
                    if (other.hasUnknown1()) {
                        setUnknown1(other.getUnknown1());
                    }
                    if (other.hasMinLawful()) {
                        setMinLawful(other.getMinLawful());
                    }
                    if (other.hasMaxLawful()) {
                        setMaxLawful(other.getMaxLawful());
                    }
                    if (other.hasMinKarma()) {
                        setMinKarma(other.getMinKarma());
                    }
                    if (other.hasMaxKarma()) {
                        setMaxKarma(other.getMaxKarma());
                    }
                    if (other.hasMaxCount()) {
                        setMaxCount(other.getMaxCount());
                    }
                    if (other.hasIsShowChance()) {
                        setIsShowChance(other.getIsShowChance());
                    }
                    mergeUnknownFields(other.unknownFields);
                    onChanged();
                    return this;
                }

                public final boolean isInitialized() {
                    if (!hasNameId()) {
                        return false;
                    }
                    if (!hasMinLevel()) {
                        return false;
                    }
                    if (!hasMaxLevel()) {
                        return false;
                    }
                    if (!hasUnknown1()) {
                        return false;
                    }
                    if (!hasMinLawful()) {
                        return false;
                    }
                    if (!hasMaxLawful()) {
                        return false;
                    }
                    if (!hasMinKarma()) {
                        return false;
                    }
                    if (!hasMaxKarma()) {
                        return false;
                    }
                    if (!hasMaxCount()) {
                        return false;
                    }
                    return true;
                }

                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    ItemCraft_Other.SendItemCraftList.CraftItemCondition parsedMessage = null;
                    try {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.CraftItemCondition) ItemCraft_Other.SendItemCraftList.CraftItemCondition.PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.CraftItemCondition) e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    } finally {
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }

                public boolean hasNameId() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public int getNameId() {
                    return this.nameId_;
                }

                public Builder setNameId(int value) {
                    this.bitField0_ |= 1;
                    this.nameId_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearNameId() {
                    this.bitField0_ &= -2;
                    this.nameId_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasMinLevel() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public int getMinLevel() {
                    return this.minLevel_;
                }

                public Builder setMinLevel(int value) {
                    this.bitField0_ |= 2;
                    this.minLevel_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearMinLevel() {
                    this.bitField0_ &= -3;
                    this.minLevel_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasMaxLevel() {
                    return (this.bitField0_ & 0x4) == 4;
                }

                public int getMaxLevel() {
                    return this.maxLevel_;
                }

                public Builder setMaxLevel(int value) {
                    this.bitField0_ |= 4;
                    this.maxLevel_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearMaxLevel() {
                    this.bitField0_ &= -5;
                    this.maxLevel_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasUnknown1() {
                    return (this.bitField0_ & 0x8) == 8;
                }

                public int getUnknown1() {
                    return this.unknown1_;
                }

                public Builder setUnknown1(int value) {
                    this.bitField0_ |= 8;
                    this.unknown1_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearUnknown1() {
                    this.bitField0_ &= -9;
                    this.unknown1_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasMinLawful() {
                    return (this.bitField0_ & 0x10) == 16;
                }

                public int getMinLawful() {
                    return this.minLawful_;
                }

                public Builder setMinLawful(int value) {
                    this.bitField0_ |= 16;
                    this.minLawful_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearMinLawful() {
                    this.bitField0_ &= -17;
                    this.minLawful_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasMaxLawful() {
                    return (this.bitField0_ & 0x20) == 32;
                }

                public int getMaxLawful() {
                    return this.maxLawful_;
                }

                public Builder setMaxLawful(int value) {
                    this.bitField0_ |= 32;
                    this.maxLawful_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearMaxLawful() {
                    this.bitField0_ &= -33;
                    this.maxLawful_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasMinKarma() {
                    return (this.bitField0_ & 0x40) == 64;
                }

                public int getMinKarma() {
                    return this.minKarma_;
                }

                public Builder setMinKarma(int value) {
                    this.bitField0_ |= 64;
                    this.minKarma_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearMinKarma() {
                    this.bitField0_ &= -65;
                    this.minKarma_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasMaxKarma() {
                    return (this.bitField0_ & 0x80) == 128;
                }

                public int getMaxKarma() {
                    return this.maxKarma_;
                }

                public Builder setMaxKarma(int value) {
                    this.bitField0_ |= 128;
                    this.maxKarma_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearMaxKarma() {
                    this.bitField0_ &= -129;
                    this.maxKarma_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasMaxCount() {
                    return (this.bitField0_ & 0x100) == 256;
                }

                public int getMaxCount() {
                    return this.maxCount_;
                }

                public Builder setMaxCount(int value) {
                    this.bitField0_ |= 256;
                    this.maxCount_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearMaxCount() {
                    this.bitField0_ &= -257;
                    this.maxCount_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasIsShowChance() {
                    return (this.bitField0_ & 0x200) == 512;
                }

                public int getIsShowChance() {
                    return this.isShowChance_;
                }

                public Builder setIsShowChance(int value) {
                    this.bitField0_ |= 512;
                    this.isShowChance_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearIsShowChance() {
                    this.bitField0_ &= -513;
                    this.isShowChance_ = 0;
                    onChanged();
                    return this;
                }

                public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.setUnknownFields(unknownFields);
                }

                public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.mergeUnknownFields(unknownFields);
                }
            }
        }

        public static final class CraftResult extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder {
            public static final int UNKNOWN1_FIELD_NUMBER = 1;
            public static final int UNKNOWN2_FIELD_NUMBER = 2;
            public static final int ITEMSIZE_FIELD_NUMBER = 3;
            public static final int ITEMTYPE_FIELD_NUMBER = 4;
            public static final int SINGLECRAFTITEM_FIELD_NUMBER = 6;
            public static final int RANDOMCRAFTITEM_FIELD_NUMBER = 5;
            public static final int GREATITEM_FIELD_NUMBER = 7;
            @Deprecated
            public static final Parser<CraftResult> PARSER = new AbstractParser<CraftResult>() {
                public ItemCraft_Other.SendItemCraftList.CraftResult parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ItemCraft_Other.SendItemCraftList.CraftResult(input, extensionRegistry);
                }
            };
            private static final long serialVersionUID = 0L;
            private static final CraftResult DEFAULT_INSTANCE = new CraftResult();
            private int bitField0_;
            private ItemCraft_Other.SendItemCraftList.unknownModel1 unknown1_;
            private ItemCraft_Other.SendItemCraftList.unknownModel1 unknown2_;
            private int itemSize_;
            private int itemtype_;
            private List<CraftResultModel> singleCraftItem_;
            private List<CraftResultModel> randomCraftItem_;
            private GreatItem greatItem_;
            private byte memoizedIsInitialized = -1;

            private CraftResult(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private CraftResult() {
                this.itemSize_ = 0;
                this.itemtype_ = 0;
                this.singleCraftItem_ = Collections.emptyList();
                this.randomCraftItem_ = Collections.emptyList();
            }

            private CraftResult(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this();
                int mutable_bitField0_ = 0;
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
                            case 10:
                                ItemCraft_Other.SendItemCraftList.unknownModel1.Builder subBuilder = null;
                                if ((this.bitField0_ & 0x1) == 1) {
                                    subBuilder = this.unknown1_.toBuilder();
                                }
                                this.unknown1_ = ((ItemCraft_Other.SendItemCraftList.unknownModel1) input.readMessage(ItemCraft_Other.SendItemCraftList.unknownModel1.PARSER, extensionRegistry));
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom(this.unknown1_);
                                    this.unknown1_ = subBuilder.buildPartial();
                                }
                                this.bitField0_ |= 1;
                                break;
                            case 18:
                                ItemCraft_Other.SendItemCraftList.unknownModel1.Builder subBuilder1 = null;
                                if ((this.bitField0_ & 0x2) == 2) {
                                    subBuilder1 = this.unknown2_.toBuilder();
                                }
                                this.unknown2_ = ((ItemCraft_Other.SendItemCraftList.unknownModel1) input.readMessage(ItemCraft_Other.SendItemCraftList.unknownModel1.PARSER, extensionRegistry));
                                if (subBuilder1 != null) {
                                    subBuilder1.mergeFrom(this.unknown2_);
                                    this.unknown2_ = subBuilder1.buildPartial();
                                }
                                this.bitField0_ |= 2;
                                break;
                            case 24:
                                this.bitField0_ |= 4;
                                this.itemSize_ = input.readInt32();
                                break;
                            case 32:
                                this.bitField0_ |= 8;
                                this.itemtype_ = input.readInt32();
                                break;
                            case 42:
                                if ((mutable_bitField0_ & 0x20) != 32) {
                                    this.randomCraftItem_ = new ArrayList<CraftResultModel>();
                                    mutable_bitField0_ |= 32;
                                }
                                this.randomCraftItem_.add((CraftResultModel) input.readMessage(CraftResultModel.PARSER, extensionRegistry));
                                break;
                            case 50:
                                if ((mutable_bitField0_ & 0x10) != 16) {
                                    this.singleCraftItem_ = new ArrayList<CraftResultModel>();
                                    mutable_bitField0_ |= 16;
                                }
                                this.singleCraftItem_.add((CraftResultModel) input.readMessage(CraftResultModel.PARSER, extensionRegistry));
                                break;
                            case 58:
                                ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.Builder subBuilder2 = null;
                                if ((this.bitField0_ & 0x10) == 16) {
                                    subBuilder2 = this.greatItem_.toBuilder();
                                }
                                this.greatItem_ = ((GreatItem) input.readMessage(GreatItem.PARSER, extensionRegistry));
                                if (subBuilder2 != null) {
                                    subBuilder2.mergeFrom(this.greatItem_);
                                    this.greatItem_ = subBuilder2.buildPartial();
                                }
                                this.bitField0_ |= 16;
                                break;
                        }
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e) {
                    throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
                } finally {
                    if ((mutable_bitField0_ & 0x20) == 32) {
                        this.randomCraftItem_ = Collections.unmodifiableList(this.randomCraftItem_);
                    }
                    if ((mutable_bitField0_ & 0x10) == 16) {
                        this.singleCraftItem_ = Collections.unmodifiableList(this.singleCraftItem_);
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraftList_CraftResult_descriptor;
            }

            public static CraftResult parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return (CraftResult) PARSER.parseFrom(data);
            }

            public static CraftResult parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (CraftResult) PARSER.parseFrom(data, extensionRegistry);
            }

            public static CraftResult parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return (CraftResult) PARSER.parseFrom(data);
            }

            public static CraftResult parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (CraftResult) PARSER.parseFrom(data, extensionRegistry);
            }

            public static CraftResult parseFrom(InputStream input) throws IOException {
                return (CraftResult) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static CraftResult parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (CraftResult) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static CraftResult parseDelimitedFrom(InputStream input) throws IOException {
                return (CraftResult) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static CraftResult parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (CraftResult) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static CraftResult parseFrom(CodedInputStream input) throws IOException {
                return (CraftResult) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static CraftResult parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (CraftResult) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(CraftResult prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            public static CraftResult getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<CraftResult> parser() {
                return PARSER;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraftList_CraftResult_fieldAccessorTable.ensureFieldAccessorsInitialized(CraftResult.class, Builder.class);
            }

            public boolean hasUnknown1() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public ItemCraft_Other.SendItemCraftList.unknownModel1 getUnknown1() {
                return this.unknown1_ == null ? ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance() : this.unknown1_;
            }

            public ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder getUnknown1OrBuilder() {
                return this.unknown1_ == null ? ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance() : this.unknown1_;
            }

            public boolean hasUnknown2() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public ItemCraft_Other.SendItemCraftList.unknownModel1 getUnknown2() {
                return this.unknown2_ == null ? ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance() : this.unknown2_;
            }

            public ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder getUnknown2OrBuilder() {
                return this.unknown2_ == null ? ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance() : this.unknown2_;
            }

            public boolean hasItemSize() {
                return (this.bitField0_ & 0x4) == 4;
            }

            public int getItemSize() {
                return this.itemSize_;
            }

            public boolean hasItemtype() {
                return (this.bitField0_ & 0x8) == 8;
            }

            public int getItemtype() {
                return this.itemtype_;
            }

            public List<CraftResultModel> getSingleCraftItemList() {
                return this.singleCraftItem_;
            }

            public List<? extends CraftResultModelOrBuilder> getSingleCraftItemOrBuilderList() {
                return this.singleCraftItem_;
            }

            public int getSingleCraftItemCount() {
                return this.singleCraftItem_.size();
            }

            public CraftResultModel getSingleCraftItem(int index) {
                return (CraftResultModel) this.singleCraftItem_.get(index);
            }

            public CraftResultModelOrBuilder getSingleCraftItemOrBuilder(int index) {
                return (CraftResultModelOrBuilder) this.singleCraftItem_.get(index);
            }

            public List<CraftResultModel> getRandomCraftItemList() {
                return this.randomCraftItem_;
            }

            public List<? extends CraftResultModelOrBuilder> getRandomCraftItemOrBuilderList() {
                return this.randomCraftItem_;
            }

            public int getRandomCraftItemCount() {
                return this.randomCraftItem_.size();
            }

            public CraftResultModel getRandomCraftItem(int index) {
                return (CraftResultModel) this.randomCraftItem_.get(index);
            }

            public CraftResultModelOrBuilder getRandomCraftItemOrBuilder(int index) {
                return (CraftResultModelOrBuilder) this.randomCraftItem_.get(index);
            }

            public boolean hasGreatItem() {
                return (this.bitField0_ & 0x10) == 16;
            }

            public GreatItem getGreatItem() {
                return this.greatItem_ == null ? GreatItem.getDefaultInstance() : this.greatItem_;
            }

            public GreatItemOrBuilder getGreatItemOrBuilder() {
                return this.greatItem_ == null ? GreatItem.getDefaultInstance() : this.greatItem_;
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                if ((hasUnknown1()) && (!getUnknown1().isInitialized())) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if ((hasUnknown2()) && (!getUnknown2().isInitialized())) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                for (int i = 0; i < getSingleCraftItemCount(); i++) {
                    if (!getSingleCraftItem(i).isInitialized()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                }
                for (int i = 0; i < getRandomCraftItemCount(); i++) {
                    if (!getRandomCraftItem(i).isInitialized()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                }
                if ((hasGreatItem()) && (!getGreatItem().isInitialized())) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 0x1) == 1) {
                    output.writeMessage(1, getUnknown1());
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    output.writeMessage(2, getUnknown2());
                }
                if ((this.bitField0_ & 0x4) == 4) {
                    output.writeInt32(3, this.itemSize_);
                }
                if ((this.bitField0_ & 0x8) == 8) {
                    output.writeInt32(4, this.itemtype_);
                }
                for (int i = 0; i < this.randomCraftItem_.size(); i++) {
                    output.writeMessage(5, (MessageLite) this.randomCraftItem_.get(i));
                }
                for (int i = 0; i < this.singleCraftItem_.size(); i++) {
                    output.writeMessage(6, (MessageLite) this.singleCraftItem_.get(i));
                }
                if ((this.bitField0_ & 0x10) == 16) {
                    output.writeMessage(7, getGreatItem());
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
                    size = size + CodedOutputStream.computeMessageSize(1, getUnknown1());
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    size = size + CodedOutputStream.computeMessageSize(2, getUnknown2());
                }
                if ((this.bitField0_ & 0x4) == 4) {
                    size = size + CodedOutputStream.computeInt32Size(3, this.itemSize_);
                }
                if ((this.bitField0_ & 0x8) == 8) {
                    size = size + CodedOutputStream.computeInt32Size(4, this.itemtype_);
                }
                for (int i = 0; i < this.randomCraftItem_.size(); i++) {
                    size = size + CodedOutputStream.computeMessageSize(5, (MessageLite) this.randomCraftItem_.get(i));
                }
                for (int i = 0; i < this.singleCraftItem_.size(); i++) {
                    size = size + CodedOutputStream.computeMessageSize(6, (MessageLite) this.singleCraftItem_.get(i));
                }
                if ((this.bitField0_ & 0x10) == 16) {
                    size = size + CodedOutputStream.computeMessageSize(7, getGreatItem());
                }
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof CraftResult)) {
                    return super.equals(obj);
                }
                CraftResult other = (CraftResult) obj;
                boolean result = true;
                result = (result) && (hasUnknown1() == other.hasUnknown1());
                if (hasUnknown1()) {
                    result = (result) && (getUnknown1().equals(other.getUnknown1()));
                }
                result = (result) && (hasUnknown2() == other.hasUnknown2());
                if (hasUnknown2()) {
                    result = (result) && (getUnknown2().equals(other.getUnknown2()));
                }
                result = (result) && (hasItemSize() == other.hasItemSize());
                if (hasItemSize()) {
                    result = (result) && (getItemSize() == other.getItemSize());
                }
                result = (result) && (hasItemtype() == other.hasItemtype());
                if (hasItemtype()) {
                    result = (result) && (getItemtype() == other.getItemtype());
                }
                result = (result) && (getSingleCraftItemList().equals(other.getSingleCraftItemList()));
                result = (result) && (getRandomCraftItemList().equals(other.getRandomCraftItemList()));
                result = (result) && (hasGreatItem() == other.hasGreatItem());
                if (hasGreatItem()) {
                    result = (result) && (getGreatItem().equals(other.getGreatItem()));
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
                if (hasUnknown1()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + getUnknown1().hashCode();
                }
                if (hasUnknown2()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + getUnknown2().hashCode();
                }
                if (hasItemSize()) {
                    hash = 37 * hash + 3;
                    hash = 53 * hash + getItemSize();
                }
                if (hasItemtype()) {
                    hash = 37 * hash + 4;
                    hash = 53 * hash + getItemtype();
                }
                if (getSingleCraftItemCount() > 0) {
                    hash = 37 * hash + 6;
                    hash = 53 * hash + getSingleCraftItemList().hashCode();
                }
                if (getRandomCraftItemCount() > 0) {
                    hash = 37 * hash + 5;
                    hash = 53 * hash + getRandomCraftItemList().hashCode();
                }
                if (hasGreatItem()) {
                    hash = 37 * hash + 7;
                    hash = 53 * hash + getGreatItem().hashCode();
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

            public Parser<CraftResult> getParserForType() {
                return PARSER;
            }

            public CraftResult getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static abstract interface CraftResultModelOrBuilder extends MessageOrBuilder {
                public abstract boolean hasItemDescId();

                public abstract int getItemDescId();

                public abstract boolean hasCount();

                public abstract int getCount();

                public abstract boolean hasUnknown1();

                public abstract long getUnknown1();

                public abstract boolean hasEnchantLevel();

                public abstract int getEnchantLevel();

                public abstract boolean hasBless();

                public abstract int getBless();

                public abstract boolean hasUnknown2();

                public abstract int getUnknown2();

                public abstract boolean hasUnknown3();

                public abstract int getUnknown3();

                public abstract boolean hasName();

                public abstract ByteString getName();

                public abstract boolean hasUnknown4();

                public abstract int getUnknown4();

                public abstract boolean hasUnknown5();

                public abstract int getUnknown5();

                public abstract boolean hasGfxId();

                public abstract int getGfxId();

                public abstract boolean hasSuccedMessage();

                public abstract ByteString getSuccedMessage();

                public abstract boolean hasItemStatusBytes();

                public abstract ByteString getItemStatusBytes();

                public abstract boolean hasUnknown6();

                public abstract int getUnknown6();

                public abstract boolean hasUnknown7();

                public abstract int getUnknown7();

                public abstract boolean hasIsGreatItem();

                public abstract int getIsGreatItem();
            }

            public static abstract interface GreatItemOrBuilder extends MessageOrBuilder {
                public abstract boolean hasUn();

                public abstract int getUn();

                public abstract boolean hasUn1();

                public abstract int getUn1();

                public abstract boolean hasUn2();

                public abstract int getUn2();

                public abstract boolean hasItem();

                public abstract ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel getItem();

                public abstract ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder getItemOrBuilder();
            }

            public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder {
                private int bitField0_;
                private ItemCraft_Other.SendItemCraftList.unknownModel1 unknown1_ = null;
                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel1, ItemCraft_Other.SendItemCraftList.unknownModel1.Builder, ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder> unknown1Builder_;
                private ItemCraft_Other.SendItemCraftList.unknownModel1 unknown2_ = null;
                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel1, ItemCraft_Other.SendItemCraftList.unknownModel1.Builder, ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder> unknown2Builder_;
                private int itemSize_;
                private int itemtype_;
                private List<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel> singleCraftItem_ = Collections.emptyList();
                private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder> singleCraftItemBuilder_;
                private List<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel> randomCraftItem_ = Collections.emptyList();
                private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder> randomCraftItemBuilder_;
                private ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem greatItem_ = null;
                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem, ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.GreatItemOrBuilder> greatItemBuilder_;

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_CraftResult_descriptor;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_CraftResult_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.CraftResult.class, Builder.class);
                }

                private void maybeForceBuilderInitialization() {
                    if (ItemCraft_Other.SendItemCraftList.CraftResult.alwaysUseFieldBuilders) {
                        getUnknown1FieldBuilder();
                        getUnknown2FieldBuilder();
                        getSingleCraftItemFieldBuilder();
                        getRandomCraftItemFieldBuilder();
                        getGreatItemFieldBuilder();
                    }
                }

                public Builder clear() {
                    super.clear();
                    if (this.unknown1Builder_ == null) {
                        this.unknown1_ = null;
                    } else {
                        this.unknown1Builder_.clear();
                    }
                    this.bitField0_ &= -2;
                    if (this.unknown2Builder_ == null) {
                        this.unknown2_ = null;
                    } else {
                        this.unknown2Builder_.clear();
                    }
                    this.bitField0_ &= -3;
                    this.itemSize_ = 0;
                    this.bitField0_ &= -5;
                    this.itemtype_ = 0;
                    this.bitField0_ &= -9;
                    if (this.singleCraftItemBuilder_ == null) {
                        this.singleCraftItem_ = Collections.emptyList();
                        this.bitField0_ &= -17;
                    } else {
                        this.singleCraftItemBuilder_.clear();
                    }
                    if (this.randomCraftItemBuilder_ == null) {
                        this.randomCraftItem_ = Collections.emptyList();
                        this.bitField0_ &= -33;
                    } else {
                        this.randomCraftItemBuilder_.clear();
                    }
                    if (this.greatItemBuilder_ == null) {
                        this.greatItem_ = null;
                    } else {
                        this.greatItemBuilder_.clear();
                    }
                    this.bitField0_ &= -65;
                    return this;
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return ItemCraft_Other.SendItemCraftList_CraftResult_descriptor;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult getDefaultInstanceForType() {
                    return ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance();
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult build() {
                    ItemCraft_Other.SendItemCraftList.CraftResult result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult buildPartial() {
                    ItemCraft_Other.SendItemCraftList.CraftResult result = new ItemCraft_Other.SendItemCraftList.CraftResult(this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 1) {
                        to_bitField0_ |= 1;
                    }
                    if (this.unknown1Builder_ == null) {
                        result.unknown1_ = this.unknown1_;
                    } else {
                        result.unknown1_ = ((ItemCraft_Other.SendItemCraftList.unknownModel1) this.unknown1Builder_.build());
                    }
                    if ((from_bitField0_ & 0x2) == 2) {
                        to_bitField0_ |= 2;
                    }
                    if (this.unknown2Builder_ == null) {
                        result.unknown2_ = this.unknown2_;
                    } else {
                        result.unknown2_ = ((ItemCraft_Other.SendItemCraftList.unknownModel1) this.unknown2Builder_.build());
                    }
                    if ((from_bitField0_ & 0x4) == 4) {
                        to_bitField0_ |= 4;
                    }
                    result.itemSize_ = this.itemSize_;
                    if ((from_bitField0_ & 0x8) == 8) {
                        to_bitField0_ |= 8;
                    }
                    result.itemtype_ = this.itemtype_;
                    if (this.singleCraftItemBuilder_ == null) {
                        if ((this.bitField0_ & 0x10) == 16) {
                            this.singleCraftItem_ = Collections.unmodifiableList(this.singleCraftItem_);
                            this.bitField0_ &= -17;
                        }
                        result.singleCraftItem_ = this.singleCraftItem_;
                    } else {
                        result.singleCraftItem_ = this.singleCraftItemBuilder_.build();
                    }
                    if (this.randomCraftItemBuilder_ == null) {
                        if ((this.bitField0_ & 0x20) == 32) {
                            this.randomCraftItem_ = Collections.unmodifiableList(this.randomCraftItem_);
                            this.bitField0_ &= -33;
                        }
                        result.randomCraftItem_ = this.randomCraftItem_;
                    } else {
                        result.randomCraftItem_ = this.randomCraftItemBuilder_.build();
                    }
                    if ((from_bitField0_ & 0x40) == 64) {
                        to_bitField0_ |= 16;
                    }
                    if (this.greatItemBuilder_ == null) {
                        result.greatItem_ = this.greatItem_;
                    } else {
                        result.greatItem_ = ((ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem) this.greatItemBuilder_.build());
                    }
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
                    if ((other instanceof ItemCraft_Other.SendItemCraftList.CraftResult)) {
                        return mergeFrom((ItemCraft_Other.SendItemCraftList.CraftResult) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.CraftResult other) {
                    if (other == ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasUnknown1()) {
                        mergeUnknown1(other.getUnknown1());
                    }
                    if (other.hasUnknown2()) {
                        mergeUnknown2(other.getUnknown2());
                    }
                    if (other.hasItemSize()) {
                        setItemSize(other.getItemSize());
                    }
                    if (other.hasItemtype()) {
                        setItemtype(other.getItemtype());
                    }
                    if (this.singleCraftItemBuilder_ == null) {
                        if (!other.singleCraftItem_.isEmpty()) {
                            if (this.singleCraftItem_.isEmpty()) {
                                this.singleCraftItem_ = other.singleCraftItem_;
                                this.bitField0_ &= -17;
                            } else {
                                ensureSingleCraftItemIsMutable();
                                this.singleCraftItem_.addAll(other.singleCraftItem_);
                            }
                            onChanged();
                        }
                    } else if (!other.singleCraftItem_.isEmpty()) {
                        if (this.singleCraftItemBuilder_.isEmpty()) {
                            this.singleCraftItemBuilder_.dispose();
                            this.singleCraftItemBuilder_ = null;
                            this.singleCraftItem_ = other.singleCraftItem_;
                            this.bitField0_ &= -17;
                            this.singleCraftItemBuilder_ = (ItemCraft_Other.SendItemCraftList.CraftResult.alwaysUseFieldBuilders ? getSingleCraftItemFieldBuilder() : null);
                        } else {
                            this.singleCraftItemBuilder_.addAllMessages(other.singleCraftItem_);
                        }
                    }
                    if (this.randomCraftItemBuilder_ == null) {
                        if (!other.randomCraftItem_.isEmpty()) {
                            if (this.randomCraftItem_.isEmpty()) {
                                this.randomCraftItem_ = other.randomCraftItem_;
                                this.bitField0_ &= -33;
                            } else {
                                ensureRandomCraftItemIsMutable();
                                this.randomCraftItem_.addAll(other.randomCraftItem_);
                            }
                            onChanged();
                        }
                    } else if (!other.randomCraftItem_.isEmpty()) {
                        if (this.randomCraftItemBuilder_.isEmpty()) {
                            this.randomCraftItemBuilder_.dispose();
                            this.randomCraftItemBuilder_ = null;
                            this.randomCraftItem_ = other.randomCraftItem_;
                            this.bitField0_ &= -33;
                            this.randomCraftItemBuilder_ = (ItemCraft_Other.SendItemCraftList.CraftResult.alwaysUseFieldBuilders ? getRandomCraftItemFieldBuilder() : null);
                        } else {
                            this.randomCraftItemBuilder_.addAllMessages(other.randomCraftItem_);
                        }
                    }
                    if (other.hasGreatItem()) {
                        mergeGreatItem(other.getGreatItem());
                    }
                    mergeUnknownFields(other.unknownFields);
                    onChanged();
                    return this;
                }

                public final boolean isInitialized() {
                    if ((hasUnknown1()) && (!getUnknown1().isInitialized())) {
                        return false;
                    }
                    if ((hasUnknown2()) && (!getUnknown2().isInitialized())) {
                        return false;
                    }
                    for (int i = 0; i < getSingleCraftItemCount(); i++) {
                        if (!getSingleCraftItem(i).isInitialized()) {
                            return false;
                        }
                    }
                    for (int i = 0; i < getRandomCraftItemCount(); i++) {
                        if (!getRandomCraftItem(i).isInitialized()) {
                            return false;
                        }
                    }
                    if ((hasGreatItem()) && (!getGreatItem().isInitialized())) {
                        return false;
                    }
                    return true;
                }

                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    ItemCraft_Other.SendItemCraftList.CraftResult parsedMessage = null;
                    try {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.CraftResult) ItemCraft_Other.SendItemCraftList.CraftResult.PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.CraftResult) e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    } finally {
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }

                public boolean hasUnknown1() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel1 getUnknown1() {
                    if (this.unknown1Builder_ == null) {
                        return this.unknown1_ == null ? ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance() : this.unknown1_;
                    }
                    return (ItemCraft_Other.SendItemCraftList.unknownModel1) this.unknown1Builder_.getMessage();
                }

                public Builder setUnknown1(ItemCraft_Other.SendItemCraftList.unknownModel1 value) {
                    if (this.unknown1Builder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.unknown1_ = value;
                        onChanged();
                    } else {
                        this.unknown1Builder_.setMessage(value);
                    }
                    this.bitField0_ |= 1;
                    return this;
                }

                public Builder setUnknown1(ItemCraft_Other.SendItemCraftList.unknownModel1.Builder builderForValue) {
                    if (this.unknown1Builder_ == null) {
                        this.unknown1_ = builderForValue.build();
                        onChanged();
                    } else {
                        this.unknown1Builder_.setMessage(builderForValue.build());
                    }
                    this.bitField0_ |= 1;
                    return this;
                }

                public Builder mergeUnknown1(ItemCraft_Other.SendItemCraftList.unknownModel1 value) {
                    if (this.unknown1Builder_ == null) {
                        if (((this.bitField0_ & 0x1) == 1) && (this.unknown1_ != null) && (this.unknown1_ != ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance())) {
                            this.unknown1_ = ItemCraft_Other.SendItemCraftList.unknownModel1.newBuilder(this.unknown1_).mergeFrom(value).buildPartial();
                        } else {
                            this.unknown1_ = value;
                        }
                        onChanged();
                    } else {
                        this.unknown1Builder_.mergeFrom(value);
                    }
                    this.bitField0_ |= 1;
                    return this;
                }

                public Builder clearUnknown1() {
                    if (this.unknown1Builder_ == null) {
                        this.unknown1_ = null;
                        onChanged();
                    } else {
                        this.unknown1Builder_.clear();
                    }
                    this.bitField0_ &= -2;
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel1.Builder getUnknown1Builder() {
                    this.bitField0_ |= 1;
                    onChanged();
                    return (ItemCraft_Other.SendItemCraftList.unknownModel1.Builder) getUnknown1FieldBuilder().getBuilder();
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder getUnknown1OrBuilder() {
                    if (this.unknown1Builder_ != null) {
                        return (ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder) this.unknown1Builder_.getMessageOrBuilder();
                    }
                    return this.unknown1_ == null ? ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance() : this.unknown1_;
                }

                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel1, ItemCraft_Other.SendItemCraftList.unknownModel1.Builder, ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder> getUnknown1FieldBuilder() {
                    if (this.unknown1Builder_ == null) {
                        this.unknown1Builder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel1, ItemCraft_Other.SendItemCraftList.unknownModel1.Builder, ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder>(getUnknown1(), getParentForChildren(), isClean());
                        this.unknown1_ = null;
                    }
                    return this.unknown1Builder_;
                }

                public boolean hasUnknown2() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel1 getUnknown2() {
                    if (this.unknown2Builder_ == null) {
                        return this.unknown2_ == null ? ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance() : this.unknown2_;
                    }
                    return (ItemCraft_Other.SendItemCraftList.unknownModel1) this.unknown2Builder_.getMessage();
                }

                public Builder setUnknown2(ItemCraft_Other.SendItemCraftList.unknownModel1 value) {
                    if (this.unknown2Builder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.unknown2_ = value;
                        onChanged();
                    } else {
                        this.unknown2Builder_.setMessage(value);
                    }
                    this.bitField0_ |= 2;
                    return this;
                }

                public Builder setUnknown2(ItemCraft_Other.SendItemCraftList.unknownModel1.Builder builderForValue) {
                    if (this.unknown2Builder_ == null) {
                        this.unknown2_ = builderForValue.build();
                        onChanged();
                    } else {
                        this.unknown2Builder_.setMessage(builderForValue.build());
                    }
                    this.bitField0_ |= 2;
                    return this;
                }

                public Builder mergeUnknown2(ItemCraft_Other.SendItemCraftList.unknownModel1 value) {
                    if (this.unknown2Builder_ == null) {
                        if (((this.bitField0_ & 0x2) == 2) && (this.unknown2_ != null) && (this.unknown2_ != ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance())) {
                            this.unknown2_ = ItemCraft_Other.SendItemCraftList.unknownModel1.newBuilder(this.unknown2_).mergeFrom(value).buildPartial();
                        } else {
                            this.unknown2_ = value;
                        }
                        onChanged();
                    } else {
                        this.unknown2Builder_.mergeFrom(value);
                    }
                    this.bitField0_ |= 2;
                    return this;
                }

                public Builder clearUnknown2() {
                    if (this.unknown2Builder_ == null) {
                        this.unknown2_ = null;
                        onChanged();
                    } else {
                        this.unknown2Builder_.clear();
                    }
                    this.bitField0_ &= -3;
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel1.Builder getUnknown2Builder() {
                    this.bitField0_ |= 2;
                    onChanged();
                    return (ItemCraft_Other.SendItemCraftList.unknownModel1.Builder) getUnknown2FieldBuilder().getBuilder();
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder getUnknown2OrBuilder() {
                    if (this.unknown2Builder_ != null) {
                        return (ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder) this.unknown2Builder_.getMessageOrBuilder();
                    }
                    return this.unknown2_ == null ? ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance() : this.unknown2_;
                }

                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel1, ItemCraft_Other.SendItemCraftList.unknownModel1.Builder, ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder> getUnknown2FieldBuilder() {
                    if (this.unknown2Builder_ == null) {
                        this.unknown2Builder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel1, ItemCraft_Other.SendItemCraftList.unknownModel1.Builder, ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder>(getUnknown2(), getParentForChildren(), isClean());
                        this.unknown2_ = null;
                    }
                    return this.unknown2Builder_;
                }

                public boolean hasItemSize() {
                    return (this.bitField0_ & 0x4) == 4;
                }

                public int getItemSize() {
                    return this.itemSize_;
                }

                public Builder setItemSize(int value) {
                    this.bitField0_ |= 4;
                    this.itemSize_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearItemSize() {
                    this.bitField0_ &= -5;
                    this.itemSize_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasItemtype() {
                    return (this.bitField0_ & 0x8) == 8;
                }

                public int getItemtype() {
                    return this.itemtype_;
                }

                public Builder setItemtype(int value) {
                    this.bitField0_ |= 8;
                    this.itemtype_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearItemtype() {
                    this.bitField0_ &= -9;
                    this.itemtype_ = 0;
                    onChanged();
                    return this;
                }

                private void ensureSingleCraftItemIsMutable() {
                    if ((this.bitField0_ & 0x10) != 16) {
                        this.singleCraftItem_ = new ArrayList<CraftResultModel>(this.singleCraftItem_);
                        this.bitField0_ |= 16;
                    }
                }

                public List<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel> getSingleCraftItemList() {
                    if (this.singleCraftItemBuilder_ == null) {
                        return Collections.unmodifiableList(this.singleCraftItem_);
                    }
                    return this.singleCraftItemBuilder_.getMessageList();
                }

                public int getSingleCraftItemCount() {
                    if (this.singleCraftItemBuilder_ == null) {
                        return this.singleCraftItem_.size();
                    }
                    return this.singleCraftItemBuilder_.getCount();
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel getSingleCraftItem(int index) {
                    if (this.singleCraftItemBuilder_ == null) {
                        return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel) this.singleCraftItem_.get(index);
                    }
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel) this.singleCraftItemBuilder_.getMessage(index);
                }

                public Builder setSingleCraftItem(int index, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel value) {
                    if (this.singleCraftItemBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureSingleCraftItemIsMutable();
                        this.singleCraftItem_.set(index, value);
                        onChanged();
                    } else {
                        this.singleCraftItemBuilder_.setMessage(index, value);
                    }
                    return this;
                }

                public Builder setSingleCraftItem(int index, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder builderForValue) {
                    if (this.singleCraftItemBuilder_ == null) {
                        ensureSingleCraftItemIsMutable();
                        this.singleCraftItem_.set(index, builderForValue.build());
                        onChanged();
                    } else {
                        this.singleCraftItemBuilder_.setMessage(index, builderForValue.build());
                    }
                    return this;
                }

                public Builder addSingleCraftItem(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel value) {
                    if (this.singleCraftItemBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureSingleCraftItemIsMutable();
                        this.singleCraftItem_.add(value);
                        onChanged();
                    } else {
                        this.singleCraftItemBuilder_.addMessage(value);
                    }
                    return this;
                }

                public Builder addSingleCraftItem(int index, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel value) {
                    if (this.singleCraftItemBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureSingleCraftItemIsMutable();
                        this.singleCraftItem_.add(index, value);
                        onChanged();
                    } else {
                        this.singleCraftItemBuilder_.addMessage(index, value);
                    }
                    return this;
                }

                public Builder addSingleCraftItem(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder builderForValue) {
                    if (this.singleCraftItemBuilder_ == null) {
                        ensureSingleCraftItemIsMutable();
                        this.singleCraftItem_.add(builderForValue.build());
                        onChanged();
                    } else {
                        this.singleCraftItemBuilder_.addMessage(builderForValue.build());
                    }
                    return this;
                }

                public Builder addSingleCraftItem(int index, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder builderForValue) {
                    if (this.singleCraftItemBuilder_ == null) {
                        ensureSingleCraftItemIsMutable();
                        this.singleCraftItem_.add(index, builderForValue.build());
                        onChanged();
                    } else {
                        this.singleCraftItemBuilder_.addMessage(index, builderForValue.build());
                    }
                    return this;
                }

                public Builder addAllSingleCraftItem(Iterable<? extends ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel> values) {
                    if (this.singleCraftItemBuilder_ == null) {
                        ensureSingleCraftItemIsMutable();
                        AbstractMessageLite.Builder.addAll(values, this.singleCraftItem_);
                        onChanged();
                    } else {
                        this.singleCraftItemBuilder_.addAllMessages(values);
                    }
                    return this;
                }

                public Builder clearSingleCraftItem() {
                    if (this.singleCraftItemBuilder_ == null) {
                        this.singleCraftItem_ = Collections.emptyList();
                        this.bitField0_ &= -17;
                        onChanged();
                    } else {
                        this.singleCraftItemBuilder_.clear();
                    }
                    return this;
                }

                public Builder removeSingleCraftItem(int index) {
                    if (this.singleCraftItemBuilder_ == null) {
                        ensureSingleCraftItemIsMutable();
                        this.singleCraftItem_.remove(index);
                        onChanged();
                    } else {
                        this.singleCraftItemBuilder_.remove(index);
                    }
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder getSingleCraftItemBuilder(int index) {
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder) getSingleCraftItemFieldBuilder().getBuilder(index);
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder getSingleCraftItemOrBuilder(int index) {
                    if (this.singleCraftItemBuilder_ == null) {
                        return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder) this.singleCraftItem_.get(index);
                    }
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder) this.singleCraftItemBuilder_.getMessageOrBuilder(index);
                }

                public List<? extends ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder> getSingleCraftItemOrBuilderList() {
                    if (this.singleCraftItemBuilder_ != null) {
                        return this.singleCraftItemBuilder_.getMessageOrBuilderList();
                    }
                    return Collections.unmodifiableList(this.singleCraftItem_);
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder addSingleCraftItemBuilder() {
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder) getSingleCraftItemFieldBuilder().addBuilder(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance());
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder addSingleCraftItemBuilder(int index) {
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder) getSingleCraftItemFieldBuilder().addBuilder(index, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance());
                }

                public List<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder> getSingleCraftItemBuilderList() {
                    return getSingleCraftItemFieldBuilder().getBuilderList();
                }

                private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder> getSingleCraftItemFieldBuilder() {
                    if (this.singleCraftItemBuilder_ == null) {
                        this.singleCraftItemBuilder_ = new RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder>(this.singleCraftItem_, (this.bitField0_ & 0x10) == 16, getParentForChildren(), isClean());
                        this.singleCraftItem_ = null;
                    }
                    return this.singleCraftItemBuilder_;
                }

                private void ensureRandomCraftItemIsMutable() {
                    if ((this.bitField0_ & 0x20) != 32) {
                        this.randomCraftItem_ = new ArrayList<CraftResultModel>(this.randomCraftItem_);
                        this.bitField0_ |= 32;
                    }
                }

                public List<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel> getRandomCraftItemList() {
                    if (this.randomCraftItemBuilder_ == null) {
                        return Collections.unmodifiableList(this.randomCraftItem_);
                    }
                    return this.randomCraftItemBuilder_.getMessageList();
                }

                public int getRandomCraftItemCount() {
                    if (this.randomCraftItemBuilder_ == null) {
                        return this.randomCraftItem_.size();
                    }
                    return this.randomCraftItemBuilder_.getCount();
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel getRandomCraftItem(int index) {
                    if (this.randomCraftItemBuilder_ == null) {
                        return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel) this.randomCraftItem_.get(index);
                    }
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel) this.randomCraftItemBuilder_.getMessage(index);
                }

                public Builder setRandomCraftItem(int index, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel value) {
                    if (this.randomCraftItemBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureRandomCraftItemIsMutable();
                        this.randomCraftItem_.set(index, value);
                        onChanged();
                    } else {
                        this.randomCraftItemBuilder_.setMessage(index, value);
                    }
                    return this;
                }

                public Builder setRandomCraftItem(int index, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder builderForValue) {
                    if (this.randomCraftItemBuilder_ == null) {
                        ensureRandomCraftItemIsMutable();
                        this.randomCraftItem_.set(index, builderForValue.build());
                        onChanged();
                    } else {
                        this.randomCraftItemBuilder_.setMessage(index, builderForValue.build());
                    }
                    return this;
                }

                public Builder addRandomCraftItem(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel value) {
                    if (this.randomCraftItemBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureRandomCraftItemIsMutable();
                        this.randomCraftItem_.add(value);
                        onChanged();
                    } else {
                        this.randomCraftItemBuilder_.addMessage(value);
                    }
                    return this;
                }

                public Builder addRandomCraftItem(int index, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel value) {
                    if (this.randomCraftItemBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureRandomCraftItemIsMutable();
                        this.randomCraftItem_.add(index, value);
                        onChanged();
                    } else {
                        this.randomCraftItemBuilder_.addMessage(index, value);
                    }
                    return this;
                }

                public Builder addRandomCraftItem(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder builderForValue) {
                    if (this.randomCraftItemBuilder_ == null) {
                        ensureRandomCraftItemIsMutable();
                        this.randomCraftItem_.add(builderForValue.build());
                        onChanged();
                    } else {
                        this.randomCraftItemBuilder_.addMessage(builderForValue.build());
                    }
                    return this;
                }

                public Builder addRandomCraftItem(int index, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder builderForValue) {
                    if (this.randomCraftItemBuilder_ == null) {
                        ensureRandomCraftItemIsMutable();
                        this.randomCraftItem_.add(index, builderForValue.build());
                        onChanged();
                    } else {
                        this.randomCraftItemBuilder_.addMessage(index, builderForValue.build());
                    }
                    return this;
                }

                public Builder addAllRandomCraftItem(Iterable<? extends ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel> values) {
                    if (this.randomCraftItemBuilder_ == null) {
                        ensureRandomCraftItemIsMutable();
                        AbstractMessageLite.Builder.addAll(values, this.randomCraftItem_);
                        onChanged();
                    } else {
                        this.randomCraftItemBuilder_.addAllMessages(values);
                    }
                    return this;
                }

                public Builder clearRandomCraftItem() {
                    if (this.randomCraftItemBuilder_ == null) {
                        this.randomCraftItem_ = Collections.emptyList();
                        this.bitField0_ &= -33;
                        onChanged();
                    } else {
                        this.randomCraftItemBuilder_.clear();
                    }
                    return this;
                }

                public Builder removeRandomCraftItem(int index) {
                    if (this.randomCraftItemBuilder_ == null) {
                        ensureRandomCraftItemIsMutable();
                        this.randomCraftItem_.remove(index);
                        onChanged();
                    } else {
                        this.randomCraftItemBuilder_.remove(index);
                    }
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder getRandomCraftItemBuilder(int index) {
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder) getRandomCraftItemFieldBuilder().getBuilder(index);
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder getRandomCraftItemOrBuilder(int index) {
                    if (this.randomCraftItemBuilder_ == null) {
                        return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder) this.randomCraftItem_.get(index);
                    }
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder) this.randomCraftItemBuilder_.getMessageOrBuilder(index);
                }

                public List<? extends ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder> getRandomCraftItemOrBuilderList() {
                    if (this.randomCraftItemBuilder_ != null) {
                        return this.randomCraftItemBuilder_.getMessageOrBuilderList();
                    }
                    return Collections.unmodifiableList(this.randomCraftItem_);
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder addRandomCraftItemBuilder() {
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder) getRandomCraftItemFieldBuilder().addBuilder(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance());
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder addRandomCraftItemBuilder(int index) {
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder) getRandomCraftItemFieldBuilder().addBuilder(index, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance());
                }

                public List<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder> getRandomCraftItemBuilderList() {
                    return getRandomCraftItemFieldBuilder().getBuilderList();
                }

                private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder> getRandomCraftItemFieldBuilder() {
                    if (this.randomCraftItemBuilder_ == null) {
                        this.randomCraftItemBuilder_ = new RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder>(this.randomCraftItem_, (this.bitField0_ & 0x20) == 32, getParentForChildren(), isClean());
                        this.randomCraftItem_ = null;
                    }
                    return this.randomCraftItemBuilder_;
                }

                public boolean hasGreatItem() {
                    return (this.bitField0_ & 0x40) == 64;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem getGreatItem() {
                    if (this.greatItemBuilder_ == null) {
                        return this.greatItem_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.getDefaultInstance() : this.greatItem_;
                    }
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem) this.greatItemBuilder_.getMessage();
                }

                public Builder setGreatItem(ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem value) {
                    if (this.greatItemBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.greatItem_ = value;
                        onChanged();
                    } else {
                        this.greatItemBuilder_.setMessage(value);
                    }
                    this.bitField0_ |= 64;
                    return this;
                }

                public Builder setGreatItem(ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.Builder builderForValue) {
                    if (this.greatItemBuilder_ == null) {
                        this.greatItem_ = builderForValue.build();
                        onChanged();
                    } else {
                        this.greatItemBuilder_.setMessage(builderForValue.build());
                    }
                    this.bitField0_ |= 64;
                    return this;
                }

                public Builder mergeGreatItem(ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem value) {
                    if (this.greatItemBuilder_ == null) {
                        if (((this.bitField0_ & 0x40) == 64) && (this.greatItem_ != null) && (this.greatItem_ != ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.getDefaultInstance())) {
                            this.greatItem_ = ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.newBuilder(this.greatItem_).mergeFrom(value).buildPartial();
                        } else {
                            this.greatItem_ = value;
                        }
                        onChanged();
                    } else {
                        this.greatItemBuilder_.mergeFrom(value);
                    }
                    this.bitField0_ |= 64;
                    return this;
                }

                public Builder clearGreatItem() {
                    if (this.greatItemBuilder_ == null) {
                        this.greatItem_ = null;
                        onChanged();
                    } else {
                        this.greatItemBuilder_.clear();
                    }
                    this.bitField0_ &= -65;
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.Builder getGreatItemBuilder() {
                    this.bitField0_ |= 64;
                    onChanged();
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.Builder) getGreatItemFieldBuilder().getBuilder();
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.GreatItemOrBuilder getGreatItemOrBuilder() {
                    if (this.greatItemBuilder_ != null) {
                        return (ItemCraft_Other.SendItemCraftList.CraftResult.GreatItemOrBuilder) this.greatItemBuilder_.getMessageOrBuilder();
                    }
                    return this.greatItem_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.getDefaultInstance() : this.greatItem_;
                }

                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem, ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.GreatItemOrBuilder> getGreatItemFieldBuilder() {
                    if (this.greatItemBuilder_ == null) {
                        this.greatItemBuilder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem, ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.GreatItemOrBuilder>(getGreatItem(), getParentForChildren(), isClean());
                        this.greatItem_ = null;
                    }
                    return this.greatItemBuilder_;
                }

                public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.setUnknownFields(unknownFields);
                }

                public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.mergeUnknownFields(unknownFields);
                }
            }

            public static final class CraftResultModel extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder {
                public static final int ITEMDESCID_FIELD_NUMBER = 1;
                public static final int COUNT_FIELD_NUMBER = 2;
                public static final int UNKNOWN1_FIELD_NUMBER = 3;
                public static final int ENCHANTLEVEL_FIELD_NUMBER = 4;
                public static final int BLESS_FIELD_NUMBER = 5;
                public static final int UNKNOWN2_FIELD_NUMBER = 6;
                public static final int UNKNOWN3_FIELD_NUMBER = 7;
                public static final int NAME_FIELD_NUMBER = 8;
                public static final int UNKNOWN4_FIELD_NUMBER = 9;
                public static final int UNKNOWN5_FIELD_NUMBER = 10;
                public static final int GFXID_FIELD_NUMBER = 11;
                public static final int SUCCEDMESSAGE_FIELD_NUMBER = 12;
                public static final int ITEMSTATUSBYTES_FIELD_NUMBER = 13;
                public static final int UNKNOWN6_FIELD_NUMBER = 14;
                public static final int UNKNOWN7_FIELD_NUMBER = 15;
                public static final int ISGREATITEM_FIELD_NUMBER = 16;
                @Deprecated
                public static final Parser<CraftResultModel> PARSER = new AbstractParser<CraftResultModel>() {
                    public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                        return new ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel(input, extensionRegistry);
                    }
                };
                private static final long serialVersionUID = 0L;
                private static final CraftResultModel DEFAULT_INSTANCE = new CraftResultModel();
                private int bitField0_;
                private int itemDescId_;
                private int count_;
                private long unknown1_;
                private int enchantLevel_;
                private int bless_;
                private int unknown2_;
                private int unknown3_;
                private ByteString name_;
                private int unknown4_;
                private int unknown5_;
                private int gfxId_;
                private ByteString succedMessage_;
                private ByteString itemStatusBytes_;
                private int unknown6_;
                private int unknown7_;
                private int isGreatItem_;
                private byte memoizedIsInitialized = -1;

                private CraftResultModel(GeneratedMessageV3.Builder<?> builder) {
                    super(builder);
                }

                private CraftResultModel() {
                    this.itemDescId_ = 0;
                    this.count_ = 0;
                    this.unknown1_ = 0L;
                    this.enchantLevel_ = 0;
                    this.bless_ = 0;
                    this.unknown2_ = 0;
                    this.unknown3_ = 0;
                    this.name_ = ByteString.EMPTY;
                    this.unknown4_ = 0;
                    this.unknown5_ = 0;
                    this.gfxId_ = 0;
                    this.succedMessage_ = ByteString.EMPTY;
                    this.itemStatusBytes_ = ByteString.EMPTY;
                    this.unknown6_ = 0;
                    this.unknown7_ = 0;
                    this.isGreatItem_ = 0;
                }

                private CraftResultModel(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                    this.itemDescId_ = input.readInt32();
                                    break;
                                case 16:
                                    this.bitField0_ |= 2;
                                    this.count_ = input.readInt32();
                                    break;
                                case 24:
                                    this.bitField0_ |= 4;
                                    this.unknown1_ = input.readInt64();
                                    break;
                                case 32:
                                    this.bitField0_ |= 8;
                                    this.enchantLevel_ = input.readInt32();
                                    break;
                                case 40:
                                    this.bitField0_ |= 16;
                                    this.bless_ = input.readInt32();
                                    break;
                                case 48:
                                    this.bitField0_ |= 32;
                                    this.unknown2_ = input.readInt32();
                                    break;
                                case 56:
                                    this.bitField0_ |= 64;
                                    this.unknown3_ = input.readInt32();
                                    break;
                                case 66:
                                    this.bitField0_ |= 128;
                                    this.name_ = input.readBytes();
                                    break;
                                case 72:
                                    this.bitField0_ |= 256;
                                    this.unknown4_ = input.readInt32();
                                    break;
                                case 80:
                                    this.bitField0_ |= 512;
                                    this.unknown5_ = input.readInt32();
                                    break;
                                case 88:
                                    this.bitField0_ |= 1024;
                                    this.gfxId_ = input.readInt32();
                                    break;
                                case 98:
                                    this.bitField0_ |= 2048;
                                    this.succedMessage_ = input.readBytes();
                                    break;
                                case 106:
                                    this.bitField0_ |= 4096;
                                    this.itemStatusBytes_ = input.readBytes();
                                    break;
                                case 112:
                                    this.bitField0_ |= 8192;
                                    this.unknown6_ = input.readInt32();
                                    break;
                                case 120:
                                    this.bitField0_ |= 16384;
                                    this.unknown7_ = input.readInt32();
                                    break;
                                case 128:
                                    this.bitField0_ |= 32768;
                                    this.isGreatItem_ = input.readInt32();
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

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_CraftResult_CraftResultModel_descriptor;
                }

                public static CraftResultModel parseFrom(ByteString data) throws InvalidProtocolBufferException {
                    return (CraftResultModel) PARSER.parseFrom(data);
                }

                public static CraftResultModel parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return (CraftResultModel) PARSER.parseFrom(data, extensionRegistry);
                }

                public static CraftResultModel parseFrom(byte[] data) throws InvalidProtocolBufferException {
                    return (CraftResultModel) PARSER.parseFrom(data);
                }

                public static CraftResultModel parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return (CraftResultModel) PARSER.parseFrom(data, extensionRegistry);
                }

                public static CraftResultModel parseFrom(InputStream input) throws IOException {
                    return (CraftResultModel) GeneratedMessageV3.parseWithIOException(PARSER, input);
                }

                public static CraftResultModel parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (CraftResultModel) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
                }

                public static CraftResultModel parseDelimitedFrom(InputStream input) throws IOException {
                    return (CraftResultModel) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
                }

                public static CraftResultModel parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (CraftResultModel) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
                }

                public static CraftResultModel parseFrom(CodedInputStream input) throws IOException {
                    return (CraftResultModel) GeneratedMessageV3.parseWithIOException(PARSER, input);
                }

                public static CraftResultModel parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (CraftResultModel) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
                }

                public static Builder newBuilder() {
                    return DEFAULT_INSTANCE.toBuilder();
                }

                public static Builder newBuilder(CraftResultModel prototype) {
                    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
                }

                public static CraftResultModel getDefaultInstance() {
                    return DEFAULT_INSTANCE;
                }

                public static Parser<CraftResultModel> parser() {
                    return PARSER;
                }

                public final UnknownFieldSet getUnknownFields() {
                    return this.unknownFields;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_CraftResult_CraftResultModel_fieldAccessorTable.ensureFieldAccessorsInitialized(CraftResultModel.class, Builder.class);
                }

                public boolean hasItemDescId() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public int getItemDescId() {
                    return this.itemDescId_;
                }

                public boolean hasCount() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public int getCount() {
                    return this.count_;
                }

                public boolean hasUnknown1() {
                    return (this.bitField0_ & 0x4) == 4;
                }

                public long getUnknown1() {
                    return this.unknown1_;
                }

                public boolean hasEnchantLevel() {
                    return (this.bitField0_ & 0x8) == 8;
                }

                public int getEnchantLevel() {
                    return this.enchantLevel_;
                }

                public boolean hasBless() {
                    return (this.bitField0_ & 0x10) == 16;
                }

                public int getBless() {
                    return this.bless_;
                }

                public boolean hasUnknown2() {
                    return (this.bitField0_ & 0x20) == 32;
                }

                public int getUnknown2() {
                    return this.unknown2_;
                }

                public boolean hasUnknown3() {
                    return (this.bitField0_ & 0x40) == 64;
                }

                public int getUnknown3() {
                    return this.unknown3_;
                }

                public boolean hasName() {
                    return (this.bitField0_ & 0x80) == 128;
                }

                public ByteString getName() {
                    return this.name_;
                }

                public boolean hasUnknown4() {
                    return (this.bitField0_ & 0x100) == 256;
                }

                public int getUnknown4() {
                    return this.unknown4_;
                }

                public boolean hasUnknown5() {
                    return (this.bitField0_ & 0x200) == 512;
                }

                public int getUnknown5() {
                    return this.unknown5_;
                }

                public boolean hasGfxId() {
                    return (this.bitField0_ & 0x400) == 1024;
                }

                public int getGfxId() {
                    return this.gfxId_;
                }

                public boolean hasSuccedMessage() {
                    return (this.bitField0_ & 0x800) == 2048;
                }

                public ByteString getSuccedMessage() {
                    return this.succedMessage_;
                }

                public boolean hasItemStatusBytes() {
                    return (this.bitField0_ & 0x1000) == 4096;
                }

                public ByteString getItemStatusBytes() {
                    return this.itemStatusBytes_;
                }

                public boolean hasUnknown6() {
                    return (this.bitField0_ & 0x2000) == 8192;
                }

                public int getUnknown6() {
                    return this.unknown6_;
                }

                public boolean hasUnknown7() {
                    return (this.bitField0_ & 0x4000) == 16384;
                }

                public int getUnknown7() {
                    return this.unknown7_;
                }

                public boolean hasIsGreatItem() {
                    return (this.bitField0_ & 0x8000) == 32768;
                }

                public int getIsGreatItem() {
                    return this.isGreatItem_;
                }

                public final boolean isInitialized() {
                    byte isInitialized = this.memoizedIsInitialized;
                    if (isInitialized == 1) {
                        return true;
                    }
                    if (isInitialized == 0) {
                        return false;
                    }
                    if (!hasItemDescId()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasCount()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUnknown1()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasEnchantLevel()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasBless()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUnknown2()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUnknown3()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasName()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUnknown4()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUnknown5()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasGfxId()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasSuccedMessage()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasItemStatusBytes()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    this.memoizedIsInitialized = 1;
                    return true;
                }

                public void writeTo(CodedOutputStream output) throws IOException {
                    if ((this.bitField0_ & 0x1) == 1) {
                        output.writeInt32(1, this.itemDescId_);
                    }
                    if ((this.bitField0_ & 0x2) == 2) {
                        output.writeInt32(2, this.count_);
                    }
                    if ((this.bitField0_ & 0x4) == 4) {
                        output.writeInt64(3, this.unknown1_);
                    }
                    if ((this.bitField0_ & 0x8) == 8) {
                        output.writeInt32(4, this.enchantLevel_);
                    }
                    if ((this.bitField0_ & 0x10) == 16) {
                        output.writeInt32(5, this.bless_);
                    }
                    if ((this.bitField0_ & 0x20) == 32) {
                        output.writeInt32(6, this.unknown2_);
                    }
                    if ((this.bitField0_ & 0x40) == 64) {
                        output.writeInt32(7, this.unknown3_);
                    }
                    if ((this.bitField0_ & 0x80) == 128) {
                        output.writeBytes(8, this.name_);
                    }
                    if ((this.bitField0_ & 0x100) == 256) {
                        output.writeInt32(9, this.unknown4_);
                    }
                    if ((this.bitField0_ & 0x200) == 512) {
                        output.writeInt32(10, this.unknown5_);
                    }
                    if ((this.bitField0_ & 0x400) == 1024) {
                        output.writeInt32(11, this.gfxId_);
                    }
                    if ((this.bitField0_ & 0x800) == 2048) {
                        output.writeBytes(12, this.succedMessage_);
                    }
                    if ((this.bitField0_ & 0x1000) == 4096) {
                        output.writeBytes(13, this.itemStatusBytes_);
                    }
                    if ((this.bitField0_ & 0x2000) == 8192) {
                        output.writeInt32(14, this.unknown6_);
                    }
                    if ((this.bitField0_ & 0x4000) == 16384) {
                        output.writeInt32(15, this.unknown7_);
                    }
                    if ((this.bitField0_ & 0x8000) == 32768) {
                        output.writeInt32(16, this.isGreatItem_);
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
                        size = size + CodedOutputStream.computeInt32Size(1, this.itemDescId_);
                    }
                    if ((this.bitField0_ & 0x2) == 2) {
                        size = size + CodedOutputStream.computeInt32Size(2, this.count_);
                    }
                    if ((this.bitField0_ & 0x4) == 4) {
                        size = size + CodedOutputStream.computeInt64Size(3, this.unknown1_);
                    }
                    if ((this.bitField0_ & 0x8) == 8) {
                        size = size + CodedOutputStream.computeInt32Size(4, this.enchantLevel_);
                    }
                    if ((this.bitField0_ & 0x10) == 16) {
                        size = size + CodedOutputStream.computeInt32Size(5, this.bless_);
                    }
                    if ((this.bitField0_ & 0x20) == 32) {
                        size = size + CodedOutputStream.computeInt32Size(6, this.unknown2_);
                    }
                    if ((this.bitField0_ & 0x40) == 64) {
                        size = size + CodedOutputStream.computeInt32Size(7, this.unknown3_);
                    }
                    if ((this.bitField0_ & 0x80) == 128) {
                        size = size + CodedOutputStream.computeBytesSize(8, this.name_);
                    }
                    if ((this.bitField0_ & 0x100) == 256) {
                        size = size + CodedOutputStream.computeInt32Size(9, this.unknown4_);
                    }
                    if ((this.bitField0_ & 0x200) == 512) {
                        size = size + CodedOutputStream.computeInt32Size(10, this.unknown5_);
                    }
                    if ((this.bitField0_ & 0x400) == 1024) {
                        size = size + CodedOutputStream.computeInt32Size(11, this.gfxId_);
                    }
                    if ((this.bitField0_ & 0x800) == 2048) {
                        size = size + CodedOutputStream.computeBytesSize(12, this.succedMessage_);
                    }
                    if ((this.bitField0_ & 0x1000) == 4096) {
                        size = size + CodedOutputStream.computeBytesSize(13, this.itemStatusBytes_);
                    }
                    if ((this.bitField0_ & 0x2000) == 8192) {
                        size = size + CodedOutputStream.computeInt32Size(14, this.unknown6_);
                    }
                    if ((this.bitField0_ & 0x4000) == 16384) {
                        size = size + CodedOutputStream.computeInt32Size(15, this.unknown7_);
                    }
                    if ((this.bitField0_ & 0x8000) == 32768) {
                        size = size + CodedOutputStream.computeInt32Size(16, this.isGreatItem_);
                    }
                    size += this.unknownFields.getSerializedSize();
                    this.memoizedSize = size;
                    return size;
                }

                public boolean equals(Object obj) {
                    if (obj == this) {
                        return true;
                    }
                    if (!(obj instanceof CraftResultModel)) {
                        return super.equals(obj);
                    }
                    CraftResultModel other = (CraftResultModel) obj;
                    boolean result = true;
                    result = (result) && (hasItemDescId() == other.hasItemDescId());
                    if (hasItemDescId()) {
                        result = (result) && (getItemDescId() == other.getItemDescId());
                    }
                    result = (result) && (hasCount() == other.hasCount());
                    if (hasCount()) {
                        result = (result) && (getCount() == other.getCount());
                    }
                    result = (result) && (hasUnknown1() == other.hasUnknown1());
                    if (hasUnknown1()) {
                        result = (result) && (getUnknown1() == other.getUnknown1());
                    }
                    result = (result) && (hasEnchantLevel() == other.hasEnchantLevel());
                    if (hasEnchantLevel()) {
                        result = (result) && (getEnchantLevel() == other.getEnchantLevel());
                    }
                    result = (result) && (hasBless() == other.hasBless());
                    if (hasBless()) {
                        result = (result) && (getBless() == other.getBless());
                    }
                    result = (result) && (hasUnknown2() == other.hasUnknown2());
                    if (hasUnknown2()) {
                        result = (result) && (getUnknown2() == other.getUnknown2());
                    }
                    result = (result) && (hasUnknown3() == other.hasUnknown3());
                    if (hasUnknown3()) {
                        result = (result) && (getUnknown3() == other.getUnknown3());
                    }
                    result = (result) && (hasName() == other.hasName());
                    if (hasName()) {
                        result = (result) && (getName().equals(other.getName()));
                    }
                    result = (result) && (hasUnknown4() == other.hasUnknown4());
                    if (hasUnknown4()) {
                        result = (result) && (getUnknown4() == other.getUnknown4());
                    }
                    result = (result) && (hasUnknown5() == other.hasUnknown5());
                    if (hasUnknown5()) {
                        result = (result) && (getUnknown5() == other.getUnknown5());
                    }
                    result = (result) && (hasGfxId() == other.hasGfxId());
                    if (hasGfxId()) {
                        result = (result) && (getGfxId() == other.getGfxId());
                    }
                    result = (result) && (hasSuccedMessage() == other.hasSuccedMessage());
                    if (hasSuccedMessage()) {
                        result = (result) && (getSuccedMessage().equals(other.getSuccedMessage()));
                    }
                    result = (result) && (hasItemStatusBytes() == other.hasItemStatusBytes());
                    if (hasItemStatusBytes()) {
                        result = (result) && (getItemStatusBytes().equals(other.getItemStatusBytes()));
                    }
                    result = (result) && (hasUnknown6() == other.hasUnknown6());
                    if (hasUnknown6()) {
                        result = (result) && (getUnknown6() == other.getUnknown6());
                    }
                    result = (result) && (hasUnknown7() == other.hasUnknown7());
                    if (hasUnknown7()) {
                        result = (result) && (getUnknown7() == other.getUnknown7());
                    }
                    result = (result) && (hasIsGreatItem() == other.hasIsGreatItem());
                    if (hasIsGreatItem()) {
                        result = (result) && (getIsGreatItem() == other.getIsGreatItem());
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
                    if (hasItemDescId()) {
                        hash = 37 * hash + 1;
                        hash = 53 * hash + getItemDescId();
                    }
                    if (hasCount()) {
                        hash = 37 * hash + 2;
                        hash = 53 * hash + getCount();
                    }
                    if (hasUnknown1()) {
                        hash = 37 * hash + 3;
                        hash = 53 * hash + Internal.hashLong(getUnknown1());
                    }
                    if (hasEnchantLevel()) {
                        hash = 37 * hash + 4;
                        hash = 53 * hash + getEnchantLevel();
                    }
                    if (hasBless()) {
                        hash = 37 * hash + 5;
                        hash = 53 * hash + getBless();
                    }
                    if (hasUnknown2()) {
                        hash = 37 * hash + 6;
                        hash = 53 * hash + getUnknown2();
                    }
                    if (hasUnknown3()) {
                        hash = 37 * hash + 7;
                        hash = 53 * hash + getUnknown3();
                    }
                    if (hasName()) {
                        hash = 37 * hash + 8;
                        hash = 53 * hash + getName().hashCode();
                    }
                    if (hasUnknown4()) {
                        hash = 37 * hash + 9;
                        hash = 53 * hash + getUnknown4();
                    }
                    if (hasUnknown5()) {
                        hash = 37 * hash + 10;
                        hash = 53 * hash + getUnknown5();
                    }
                    if (hasGfxId()) {
                        hash = 37 * hash + 11;
                        hash = 53 * hash + getGfxId();
                    }
                    if (hasSuccedMessage()) {
                        hash = 37 * hash + 12;
                        hash = 53 * hash + getSuccedMessage().hashCode();
                    }
                    if (hasItemStatusBytes()) {
                        hash = 37 * hash + 13;
                        hash = 53 * hash + getItemStatusBytes().hashCode();
                    }
                    if (hasUnknown6()) {
                        hash = 37 * hash + 14;
                        hash = 53 * hash + getUnknown6();
                    }
                    if (hasUnknown7()) {
                        hash = 37 * hash + 15;
                        hash = 53 * hash + getUnknown7();
                    }
                    if (hasIsGreatItem()) {
                        hash = 37 * hash + 16;
                        hash = 53 * hash + getIsGreatItem();
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

                public Parser<CraftResultModel> getParserForType() {
                    return PARSER;
                }

                public CraftResultModel getDefaultInstanceForType() {
                    return DEFAULT_INSTANCE;
                }

                public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder {
                    private int bitField0_;
                    private int itemDescId_;
                    private int count_;
                    private long unknown1_;
                    private int enchantLevel_;
                    private int bless_;
                    private int unknown2_;
                    private int unknown3_;
                    private ByteString name_ = ByteString.EMPTY;
                    private int unknown4_;
                    private int unknown5_;
                    private int gfxId_;
                    private ByteString succedMessage_ = ByteString.EMPTY;
                    private ByteString itemStatusBytes_ = ByteString.EMPTY;
                    private int unknown6_;
                    private int unknown7_;
                    private int isGreatItem_;

                    private Builder() {
                        maybeForceBuilderInitialization();
                    }

                    private Builder(GeneratedMessageV3.BuilderParent parent) {
                        super(parent);
                        maybeForceBuilderInitialization();
                    }

                    public static final Descriptors.Descriptor getDescriptor() {
                        return ItemCraft_Other.SendItemCraftList_CraftResult_CraftResultModel_descriptor;
                    }

                    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                        return ItemCraft_Other.SendItemCraftList_CraftResult_CraftResultModel_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.class, Builder.class);
                    }

                    private void maybeForceBuilderInitialization() {
                    }

                    public Builder clear() {
                        super.clear();
                        this.itemDescId_ = 0;
                        this.bitField0_ &= -2;
                        this.count_ = 0;
                        this.bitField0_ &= -3;
                        this.unknown1_ = 0L;
                        this.bitField0_ &= -5;
                        this.enchantLevel_ = 0;
                        this.bitField0_ &= -9;
                        this.bless_ = 0;
                        this.bitField0_ &= -17;
                        this.unknown2_ = 0;
                        this.bitField0_ &= -33;
                        this.unknown3_ = 0;
                        this.bitField0_ &= -65;
                        this.name_ = ByteString.EMPTY;
                        this.bitField0_ &= -129;
                        this.unknown4_ = 0;
                        this.bitField0_ &= -257;
                        this.unknown5_ = 0;
                        this.bitField0_ &= -513;
                        this.gfxId_ = 0;
                        this.bitField0_ &= -1025;
                        this.succedMessage_ = ByteString.EMPTY;
                        this.bitField0_ &= -2049;
                        this.itemStatusBytes_ = ByteString.EMPTY;
                        this.bitField0_ &= -4097;
                        this.unknown6_ = 0;
                        this.bitField0_ &= -8193;
                        this.unknown7_ = 0;
                        this.bitField0_ &= -16385;
                        this.isGreatItem_ = 0;
                        this.bitField0_ &= -32769;
                        return this;
                    }

                    public Descriptors.Descriptor getDescriptorForType() {
                        return ItemCraft_Other.SendItemCraftList_CraftResult_CraftResultModel_descriptor;
                    }

                    public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel getDefaultInstanceForType() {
                        return ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance();
                    }

                    public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel build() {
                        ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel result = buildPartial();
                        if (!result.isInitialized()) {
                            throw newUninitializedMessageException(result);
                        }
                        return result;
                    }

                    public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel buildPartial() {
                        ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel result = new ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel(this);
                        int from_bitField0_ = this.bitField0_;
                        int to_bitField0_ = 0;
                        if ((from_bitField0_ & 0x1) == 1) {
                            to_bitField0_ |= 1;
                        }
                        result.itemDescId_ = this.itemDescId_;
                        if ((from_bitField0_ & 0x2) == 2) {
                            to_bitField0_ |= 2;
                        }
                        result.count_ = this.count_;
                        if ((from_bitField0_ & 0x4) == 4) {
                            to_bitField0_ |= 4;
                        }
                        result.unknown1_ = this.unknown1_;
                        if ((from_bitField0_ & 0x8) == 8) {
                            to_bitField0_ |= 8;
                        }
                        result.enchantLevel_ = this.enchantLevel_;
                        if ((from_bitField0_ & 0x10) == 16) {
                            to_bitField0_ |= 16;
                        }
                        result.bless_ = this.bless_;
                        if ((from_bitField0_ & 0x20) == 32) {
                            to_bitField0_ |= 32;
                        }
                        result.unknown2_ = this.unknown2_;
                        if ((from_bitField0_ & 0x40) == 64) {
                            to_bitField0_ |= 64;
                        }
                        result.unknown3_ = this.unknown3_;
                        if ((from_bitField0_ & 0x80) == 128) {
                            to_bitField0_ |= 128;
                        }
                        result.name_ = this.name_;
                        if ((from_bitField0_ & 0x100) == 256) {
                            to_bitField0_ |= 256;
                        }
                        result.unknown4_ = this.unknown4_;
                        if ((from_bitField0_ & 0x200) == 512) {
                            to_bitField0_ |= 512;
                        }
                        result.unknown5_ = this.unknown5_;
                        if ((from_bitField0_ & 0x400) == 1024) {
                            to_bitField0_ |= 1024;
                        }
                        result.gfxId_ = this.gfxId_;
                        if ((from_bitField0_ & 0x800) == 2048) {
                            to_bitField0_ |= 2048;
                        }
                        result.succedMessage_ = this.succedMessage_;
                        if ((from_bitField0_ & 0x1000) == 4096) {
                            to_bitField0_ |= 4096;
                        }
                        result.itemStatusBytes_ = this.itemStatusBytes_;
                        if ((from_bitField0_ & 0x2000) == 8192) {
                            to_bitField0_ |= 8192;
                        }
                        result.unknown6_ = this.unknown6_;
                        if ((from_bitField0_ & 0x4000) == 16384) {
                            to_bitField0_ |= 16384;
                        }
                        result.unknown7_ = this.unknown7_;
                        if ((from_bitField0_ & 0x8000) == 32768) {
                            to_bitField0_ |= 32768;
                        }
                        result.isGreatItem_ = this.isGreatItem_;
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
                        if ((other instanceof ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel)) {
                            return mergeFrom((ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel) other);
                        }
                        super.mergeFrom(other);
                        return this;
                    }

                    public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel other) {
                        if (other == ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance()) {
                            return this;
                        }
                        if (other.hasItemDescId()) {
                            setItemDescId(other.getItemDescId());
                        }
                        if (other.hasCount()) {
                            setCount(other.getCount());
                        }
                        if (other.hasUnknown1()) {
                            setUnknown1(other.getUnknown1());
                        }
                        if (other.hasEnchantLevel()) {
                            setEnchantLevel(other.getEnchantLevel());
                        }
                        if (other.hasBless()) {
                            setBless(other.getBless());
                        }
                        if (other.hasUnknown2()) {
                            setUnknown2(other.getUnknown2());
                        }
                        if (other.hasUnknown3()) {
                            setUnknown3(other.getUnknown3());
                        }
                        if (other.hasName()) {
                            setName(other.getName());
                        }
                        if (other.hasUnknown4()) {
                            setUnknown4(other.getUnknown4());
                        }
                        if (other.hasUnknown5()) {
                            setUnknown5(other.getUnknown5());
                        }
                        if (other.hasGfxId()) {
                            setGfxId(other.getGfxId());
                        }
                        if (other.hasSuccedMessage()) {
                            setSuccedMessage(other.getSuccedMessage());
                        }
                        if (other.hasItemStatusBytes()) {
                            setItemStatusBytes(other.getItemStatusBytes());
                        }
                        if (other.hasUnknown6()) {
                            setUnknown6(other.getUnknown6());
                        }
                        if (other.hasUnknown7()) {
                            setUnknown7(other.getUnknown7());
                        }
                        if (other.hasIsGreatItem()) {
                            setIsGreatItem(other.getIsGreatItem());
                        }
                        mergeUnknownFields(other.unknownFields);
                        onChanged();
                        return this;
                    }

                    public final boolean isInitialized() {
                        if (!hasItemDescId()) {
                            return false;
                        }
                        if (!hasCount()) {
                            return false;
                        }
                        if (!hasUnknown1()) {
                            return false;
                        }
                        if (!hasEnchantLevel()) {
                            return false;
                        }
                        if (!hasBless()) {
                            return false;
                        }
                        if (!hasUnknown2()) {
                            return false;
                        }
                        if (!hasUnknown3()) {
                            return false;
                        }
                        if (!hasName()) {
                            return false;
                        }
                        if (!hasUnknown4()) {
                            return false;
                        }
                        if (!hasUnknown5()) {
                            return false;
                        }
                        if (!hasGfxId()) {
                            return false;
                        }
                        if (!hasSuccedMessage()) {
                            return false;
                        }
                        if (!hasItemStatusBytes()) {
                            return false;
                        }
                        return true;
                    }

                    public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                        ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel parsedMessage = null;
                        try {
                            parsedMessage = (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel) ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.PARSER.parsePartialFrom(input, extensionRegistry);
                        } catch (InvalidProtocolBufferException e) {
                            parsedMessage = (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel) e.getUnfinishedMessage();
                            throw e.unwrapIOException();
                        } finally {
                            if (parsedMessage != null) {
                                mergeFrom(parsedMessage);
                            }
                        }
                        return this;
                    }

                    public boolean hasItemDescId() {
                        return (this.bitField0_ & 0x1) == 1;
                    }

                    public int getItemDescId() {
                        return this.itemDescId_;
                    }

                    public Builder setItemDescId(int value) {
                        this.bitField0_ |= 1;
                        this.itemDescId_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearItemDescId() {
                        this.bitField0_ &= -2;
                        this.itemDescId_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasCount() {
                        return (this.bitField0_ & 0x2) == 2;
                    }

                    public int getCount() {
                        return this.count_;
                    }

                    public Builder setCount(int value) {
                        this.bitField0_ |= 2;
                        this.count_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearCount() {
                        this.bitField0_ &= -3;
                        this.count_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUnknown1() {
                        return (this.bitField0_ & 0x4) == 4;
                    }

                    public long getUnknown1() {
                        return this.unknown1_;
                    }

                    public Builder setUnknown1(long value) {
                        this.bitField0_ |= 4;
                        this.unknown1_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUnknown1() {
                        this.bitField0_ &= -5;
                        this.unknown1_ = 0L;
                        onChanged();
                        return this;
                    }

                    public boolean hasEnchantLevel() {
                        return (this.bitField0_ & 0x8) == 8;
                    }

                    public int getEnchantLevel() {
                        return this.enchantLevel_;
                    }

                    public Builder setEnchantLevel(int value) {
                        this.bitField0_ |= 8;
                        this.enchantLevel_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearEnchantLevel() {
                        this.bitField0_ &= -9;
                        this.enchantLevel_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasBless() {
                        return (this.bitField0_ & 0x10) == 16;
                    }

                    public int getBless() {
                        return this.bless_;
                    }

                    public Builder setBless(int value) {
                        this.bitField0_ |= 16;
                        this.bless_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearBless() {
                        this.bitField0_ &= -17;
                        this.bless_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUnknown2() {
                        return (this.bitField0_ & 0x20) == 32;
                    }

                    public int getUnknown2() {
                        return this.unknown2_;
                    }

                    public Builder setUnknown2(int value) {
                        this.bitField0_ |= 32;
                        this.unknown2_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUnknown2() {
                        this.bitField0_ &= -33;
                        this.unknown2_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUnknown3() {
                        return (this.bitField0_ & 0x40) == 64;
                    }

                    public int getUnknown3() {
                        return this.unknown3_;
                    }

                    public Builder setUnknown3(int value) {
                        this.bitField0_ |= 64;
                        this.unknown3_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUnknown3() {
                        this.bitField0_ &= -65;
                        this.unknown3_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasName() {
                        return (this.bitField0_ & 0x80) == 128;
                    }

                    public ByteString getName() {
                        return this.name_;
                    }

                    public Builder setName(ByteString value) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.bitField0_ |= 128;
                        this.name_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearName() {
                        this.bitField0_ &= -129;
                        this.name_ = ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance().getName();
                        onChanged();
                        return this;
                    }

                    public boolean hasUnknown4() {
                        return (this.bitField0_ & 0x100) == 256;
                    }

                    public int getUnknown4() {
                        return this.unknown4_;
                    }

                    public Builder setUnknown4(int value) {
                        this.bitField0_ |= 256;
                        this.unknown4_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUnknown4() {
                        this.bitField0_ &= -257;
                        this.unknown4_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUnknown5() {
                        return (this.bitField0_ & 0x200) == 512;
                    }

                    public int getUnknown5() {
                        return this.unknown5_;
                    }

                    public Builder setUnknown5(int value) {
                        this.bitField0_ |= 512;
                        this.unknown5_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUnknown5() {
                        this.bitField0_ &= -513;
                        this.unknown5_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasGfxId() {
                        return (this.bitField0_ & 0x400) == 1024;
                    }

                    public int getGfxId() {
                        return this.gfxId_;
                    }

                    public Builder setGfxId(int value) {
                        this.bitField0_ |= 1024;
                        this.gfxId_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearGfxId() {
                        this.bitField0_ &= -1025;
                        this.gfxId_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasSuccedMessage() {
                        return (this.bitField0_ & 0x800) == 2048;
                    }

                    public ByteString getSuccedMessage() {
                        return this.succedMessage_;
                    }

                    public Builder setSuccedMessage(ByteString value) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.bitField0_ |= 2048;
                        this.succedMessage_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearSuccedMessage() {
                        this.bitField0_ &= -2049;
                        this.succedMessage_ = ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance().getSuccedMessage();
                        onChanged();
                        return this;
                    }

                    public boolean hasItemStatusBytes() {
                        return (this.bitField0_ & 0x1000) == 4096;
                    }

                    public ByteString getItemStatusBytes() {
                        return this.itemStatusBytes_;
                    }

                    public Builder setItemStatusBytes(ByteString value) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.bitField0_ |= 4096;
                        this.itemStatusBytes_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearItemStatusBytes() {
                        this.bitField0_ &= -4097;
                        this.itemStatusBytes_ = ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance().getItemStatusBytes();
                        onChanged();
                        return this;
                    }

                    public boolean hasUnknown6() {
                        return (this.bitField0_ & 0x2000) == 8192;
                    }

                    public int getUnknown6() {
                        return this.unknown6_;
                    }

                    public Builder setUnknown6(int value) {
                        this.bitField0_ |= 8192;
                        this.unknown6_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUnknown6() {
                        this.bitField0_ &= -8193;
                        this.unknown6_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUnknown7() {
                        return (this.bitField0_ & 0x4000) == 16384;
                    }

                    public int getUnknown7() {
                        return this.unknown7_;
                    }

                    public Builder setUnknown7(int value) {
                        this.bitField0_ |= 16384;
                        this.unknown7_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUnknown7() {
                        this.bitField0_ &= -16385;
                        this.unknown7_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasIsGreatItem() {
                        return (this.bitField0_ & 0x8000) == 32768;
                    }

                    public int getIsGreatItem() {
                        return this.isGreatItem_;
                    }

                    public Builder setIsGreatItem(int value) {
                        this.bitField0_ |= 32768;
                        this.isGreatItem_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearIsGreatItem() {
                        this.bitField0_ &= -32769;
                        this.isGreatItem_ = 0;
                        onChanged();
                        return this;
                    }

                    public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                        return (Builder) super.setUnknownFields(unknownFields);
                    }

                    public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                        return (Builder) super.mergeUnknownFields(unknownFields);
                    }
                }
            }

            public static final class GreatItem extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.CraftResult.GreatItemOrBuilder {
                public static final int UN_FIELD_NUMBER = 1;
                public static final int UN1_FIELD_NUMBER = 2;
                public static final int UN2_FIELD_NUMBER = 3;
                public static final int ITEM_FIELD_NUMBER = 4;
                @Deprecated
                public static final Parser<GreatItem> PARSER = new AbstractParser<GreatItem>() {
                    public ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                        return new ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem(input, extensionRegistry);
                    }
                };
                private static final long serialVersionUID = 0L;
                private static final GreatItem DEFAULT_INSTANCE = new GreatItem();
                private int bitField0_;
                private int un_;
                private int un1_;
                private int un2_;
                private ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel item_;
                private byte memoizedIsInitialized = -1;

                private GreatItem(GeneratedMessageV3.Builder<?> builder) {
                    super(builder);
                }

                private GreatItem() {
                    this.un_ = 0;
                    this.un1_ = 0;
                    this.un2_ = 0;
                }

                private GreatItem(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                    this.un_ = input.readInt32();
                                    break;
                                case 16:
                                    this.bitField0_ |= 2;
                                    this.un1_ = input.readInt32();
                                    break;
                                case 24:
                                    this.bitField0_ |= 4;
                                    this.un2_ = input.readInt32();
                                    break;
                                case 34:
                                    ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder subBuilder = null;
                                    if ((this.bitField0_ & 0x8) == 8) {
                                        subBuilder = this.item_.toBuilder();
                                    }
                                    this.item_ = ((ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel) input.readMessage(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.PARSER, extensionRegistry));
                                    if (subBuilder != null) {
                                        subBuilder.mergeFrom(this.item_);
                                        this.item_ = subBuilder.buildPartial();
                                    }
                                    this.bitField0_ |= 8;
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

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_CraftResult_GreatItem_descriptor;
                }

                public static GreatItem parseFrom(ByteString data) throws InvalidProtocolBufferException {
                    return (GreatItem) PARSER.parseFrom(data);
                }

                public static GreatItem parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return (GreatItem) PARSER.parseFrom(data, extensionRegistry);
                }

                public static GreatItem parseFrom(byte[] data) throws InvalidProtocolBufferException {
                    return (GreatItem) PARSER.parseFrom(data);
                }

                public static GreatItem parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return (GreatItem) PARSER.parseFrom(data, extensionRegistry);
                }

                public static GreatItem parseFrom(InputStream input) throws IOException {
                    return (GreatItem) GeneratedMessageV3.parseWithIOException(PARSER, input);
                }

                public static GreatItem parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (GreatItem) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
                }

                public static GreatItem parseDelimitedFrom(InputStream input) throws IOException {
                    return (GreatItem) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
                }

                public static GreatItem parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (GreatItem) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
                }

                public static GreatItem parseFrom(CodedInputStream input) throws IOException {
                    return (GreatItem) GeneratedMessageV3.parseWithIOException(PARSER, input);
                }

                public static GreatItem parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (GreatItem) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
                }

                public static Builder newBuilder() {
                    return DEFAULT_INSTANCE.toBuilder();
                }

                public static Builder newBuilder(GreatItem prototype) {
                    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
                }

                public static GreatItem getDefaultInstance() {
                    return DEFAULT_INSTANCE;
                }

                public static Parser<GreatItem> parser() {
                    return PARSER;
                }

                public final UnknownFieldSet getUnknownFields() {
                    return this.unknownFields;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_CraftResult_GreatItem_fieldAccessorTable.ensureFieldAccessorsInitialized(GreatItem.class, Builder.class);
                }

                public boolean hasUn() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public int getUn() {
                    return this.un_;
                }

                public boolean hasUn1() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public int getUn1() {
                    return this.un1_;
                }

                public boolean hasUn2() {
                    return (this.bitField0_ & 0x4) == 4;
                }

                public int getUn2() {
                    return this.un2_;
                }

                public boolean hasItem() {
                    return (this.bitField0_ & 0x8) == 8;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel getItem() {
                    return this.item_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance() : this.item_;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder getItemOrBuilder() {
                    return this.item_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance() : this.item_;
                }

                public final boolean isInitialized() {
                    byte isInitialized = this.memoizedIsInitialized;
                    if (isInitialized == 1) {
                        return true;
                    }
                    if (isInitialized == 0) {
                        return false;
                    }
                    if (!hasUn()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUn1()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUn2()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasItem()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!getItem().isInitialized()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    this.memoizedIsInitialized = 1;
                    return true;
                }

                public void writeTo(CodedOutputStream output) throws IOException {
                    if ((this.bitField0_ & 0x1) == 1) {
                        output.writeInt32(1, this.un_);
                    }
                    if ((this.bitField0_ & 0x2) == 2) {
                        output.writeInt32(2, this.un1_);
                    }
                    if ((this.bitField0_ & 0x4) == 4) {
                        output.writeInt32(3, this.un2_);
                    }
                    if ((this.bitField0_ & 0x8) == 8) {
                        output.writeMessage(4, getItem());
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
                        size = size + CodedOutputStream.computeInt32Size(1, this.un_);
                    }
                    if ((this.bitField0_ & 0x2) == 2) {
                        size = size + CodedOutputStream.computeInt32Size(2, this.un1_);
                    }
                    if ((this.bitField0_ & 0x4) == 4) {
                        size = size + CodedOutputStream.computeInt32Size(3, this.un2_);
                    }
                    if ((this.bitField0_ & 0x8) == 8) {
                        size = size + CodedOutputStream.computeMessageSize(4, getItem());
                    }
                    size += this.unknownFields.getSerializedSize();
                    this.memoizedSize = size;
                    return size;
                }

                public boolean equals(Object obj) {
                    if (obj == this) {
                        return true;
                    }
                    if (!(obj instanceof GreatItem)) {
                        return super.equals(obj);
                    }
                    GreatItem other = (GreatItem) obj;
                    boolean result = true;
                    result = (result) && (hasUn() == other.hasUn());
                    if (hasUn()) {
                        result = (result) && (getUn() == other.getUn());
                    }
                    result = (result) && (hasUn1() == other.hasUn1());
                    if (hasUn1()) {
                        result = (result) && (getUn1() == other.getUn1());
                    }
                    result = (result) && (hasUn2() == other.hasUn2());
                    if (hasUn2()) {
                        result = (result) && (getUn2() == other.getUn2());
                    }
                    result = (result) && (hasItem() == other.hasItem());
                    if (hasItem()) {
                        result = (result) && (getItem().equals(other.getItem()));
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
                    if (hasUn()) {
                        hash = 37 * hash + 1;
                        hash = 53 * hash + getUn();
                    }
                    if (hasUn1()) {
                        hash = 37 * hash + 2;
                        hash = 53 * hash + getUn1();
                    }
                    if (hasUn2()) {
                        hash = 37 * hash + 3;
                        hash = 53 * hash + getUn2();
                    }
                    if (hasItem()) {
                        hash = 37 * hash + 4;
                        hash = 53 * hash + getItem().hashCode();
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

                public Parser<GreatItem> getParserForType() {
                    return PARSER;
                }

                public GreatItem getDefaultInstanceForType() {
                    return DEFAULT_INSTANCE;
                }

                public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.CraftResult.GreatItemOrBuilder {
                    private int bitField0_;
                    private int un_;
                    private int un1_;
                    private int un2_;
                    private ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel item_ = null;
                    private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder> itemBuilder_;

                    private Builder() {
                        maybeForceBuilderInitialization();
                    }

                    private Builder(GeneratedMessageV3.BuilderParent parent) {
                        super(parent);
                        maybeForceBuilderInitialization();
                    }

                    public static final Descriptors.Descriptor getDescriptor() {
                        return ItemCraft_Other.SendItemCraftList_CraftResult_GreatItem_descriptor;
                    }

                    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                        return ItemCraft_Other.SendItemCraftList_CraftResult_GreatItem_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.class, Builder.class);
                    }

                    private void maybeForceBuilderInitialization() {
                        if (ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.alwaysUseFieldBuilders) {
                            getItemFieldBuilder();
                        }
                    }

                    public Builder clear() {
                        super.clear();
                        this.un_ = 0;
                        this.bitField0_ &= -2;
                        this.un1_ = 0;
                        this.bitField0_ &= -3;
                        this.un2_ = 0;
                        this.bitField0_ &= -5;
                        if (this.itemBuilder_ == null) {
                            this.item_ = null;
                        } else {
                            this.itemBuilder_.clear();
                        }
                        this.bitField0_ &= -9;
                        return this;
                    }

                    public Descriptors.Descriptor getDescriptorForType() {
                        return ItemCraft_Other.SendItemCraftList_CraftResult_GreatItem_descriptor;
                    }

                    public ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem getDefaultInstanceForType() {
                        return ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.getDefaultInstance();
                    }

                    public ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem build() {
                        ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem result = buildPartial();
                        if (!result.isInitialized()) {
                            throw newUninitializedMessageException(result);
                        }
                        return result;
                    }

                    public ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem buildPartial() {
                        ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem result = new ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem(this);
                        int from_bitField0_ = this.bitField0_;
                        int to_bitField0_ = 0;
                        if ((from_bitField0_ & 0x1) == 1) {
                            to_bitField0_ |= 1;
                        }
                        result.un_ = this.un_;
                        if ((from_bitField0_ & 0x2) == 2) {
                            to_bitField0_ |= 2;
                        }
                        result.un1_ = this.un1_;
                        if ((from_bitField0_ & 0x4) == 4) {
                            to_bitField0_ |= 4;
                        }
                        result.un2_ = this.un2_;
                        if ((from_bitField0_ & 0x8) == 8) {
                            to_bitField0_ |= 8;
                        }
                        if (this.itemBuilder_ == null) {
                            result.item_ = this.item_;
                        } else {
                            result.item_ = ((ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel) this.itemBuilder_.build());
                        }
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
                        if ((other instanceof ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem)) {
                            return mergeFrom((ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem) other);
                        }
                        super.mergeFrom(other);
                        return this;
                    }

                    public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem other) {
                        if (other == ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.getDefaultInstance()) {
                            return this;
                        }
                        if (other.hasUn()) {
                            setUn(other.getUn());
                        }
                        if (other.hasUn1()) {
                            setUn1(other.getUn1());
                        }
                        if (other.hasUn2()) {
                            setUn2(other.getUn2());
                        }
                        if (other.hasItem()) {
                            mergeItem(other.getItem());
                        }
                        mergeUnknownFields(other.unknownFields);
                        onChanged();
                        return this;
                    }

                    public final boolean isInitialized() {
                        if (!hasUn()) {
                            return false;
                        }
                        if (!hasUn1()) {
                            return false;
                        }
                        if (!hasUn2()) {
                            return false;
                        }
                        if (!hasItem()) {
                            return false;
                        }
                        if (!getItem().isInitialized()) {
                            return false;
                        }
                        return true;
                    }

                    public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                        ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem parsedMessage = null;
                        try {
                            parsedMessage = (ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem) ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.PARSER.parsePartialFrom(input, extensionRegistry);
                        } catch (InvalidProtocolBufferException e) {
                            parsedMessage = (ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem) e.getUnfinishedMessage();
                            throw e.unwrapIOException();
                        } finally {
                            if (parsedMessage != null) {
                                mergeFrom(parsedMessage);
                            }
                        }
                        return this;
                    }

                    public boolean hasUn() {
                        return (this.bitField0_ & 0x1) == 1;
                    }

                    public int getUn() {
                        return this.un_;
                    }

                    public Builder setUn(int value) {
                        this.bitField0_ |= 1;
                        this.un_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUn() {
                        this.bitField0_ &= -2;
                        this.un_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUn1() {
                        return (this.bitField0_ & 0x2) == 2;
                    }

                    public int getUn1() {
                        return this.un1_;
                    }

                    public Builder setUn1(int value) {
                        this.bitField0_ |= 2;
                        this.un1_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUn1() {
                        this.bitField0_ &= -3;
                        this.un1_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUn2() {
                        return (this.bitField0_ & 0x4) == 4;
                    }

                    public int getUn2() {
                        return this.un2_;
                    }

                    public Builder setUn2(int value) {
                        this.bitField0_ |= 4;
                        this.un2_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUn2() {
                        this.bitField0_ &= -5;
                        this.un2_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasItem() {
                        return (this.bitField0_ & 0x8) == 8;
                    }

                    public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel getItem() {
                        if (this.itemBuilder_ == null) {
                            return this.item_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance() : this.item_;
                        }
                        return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel) this.itemBuilder_.getMessage();
                    }

                    public Builder setItem(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel value) {
                        if (this.itemBuilder_ == null) {
                            if (value == null) {
                                throw new NullPointerException();
                            }
                            this.item_ = value;
                            onChanged();
                        } else {
                            this.itemBuilder_.setMessage(value);
                        }
                        this.bitField0_ |= 8;
                        return this;
                    }

                    public Builder setItem(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder builderForValue) {
                        if (this.itemBuilder_ == null) {
                            this.item_ = builderForValue.build();
                            onChanged();
                        } else {
                            this.itemBuilder_.setMessage(builderForValue.build());
                        }
                        this.bitField0_ |= 8;
                        return this;
                    }

                    public Builder mergeItem(ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel value) {
                        if (this.itemBuilder_ == null) {
                            if (((this.bitField0_ & 0x8) == 8) && (this.item_ != null) && (this.item_ != ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance())) {
                                this.item_ = ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.newBuilder(this.item_).mergeFrom(value).buildPartial();
                            } else {
                                this.item_ = value;
                            }
                            onChanged();
                        } else {
                            this.itemBuilder_.mergeFrom(value);
                        }
                        this.bitField0_ |= 8;
                        return this;
                    }

                    public Builder clearItem() {
                        if (this.itemBuilder_ == null) {
                            this.item_ = null;
                            onChanged();
                        } else {
                            this.itemBuilder_.clear();
                        }
                        this.bitField0_ &= -9;
                        return this;
                    }

                    public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder getItemBuilder() {
                        this.bitField0_ |= 8;
                        onChanged();
                        return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder) getItemFieldBuilder().getBuilder();
                    }

                    public ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder getItemOrBuilder() {
                        if (this.itemBuilder_ != null) {
                            return (ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder) this.itemBuilder_.getMessageOrBuilder();
                        }
                        return this.item_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.getDefaultInstance() : this.item_;
                    }

                    private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder> getItemFieldBuilder() {
                        if (this.itemBuilder_ == null) {
                            this.itemBuilder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder, ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModelOrBuilder>(getItem(), getParentForChildren(), isClean());
                            this.item_ = null;
                        }
                        return this.itemBuilder_;
                    }

                    public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                        return (Builder) super.setUnknownFields(unknownFields);
                    }

                    public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                        return (Builder) super.mergeUnknownFields(unknownFields);
                    }
                }
            }
        }

        public static final class CraftResults extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.CraftResultsOrBuilder {
            public static final int SUCCEEDLIST_FIELD_NUMBER = 1;
            public static final int FAILLIST_FIELD_NUMBER = 2;
            public static final int SUCCESSRATIO_FIELD_NUMBER = 3;
            @Deprecated
            public static final Parser<CraftResults> PARSER = new AbstractParser<CraftResults>() {
                public ItemCraft_Other.SendItemCraftList.CraftResults parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ItemCraft_Other.SendItemCraftList.CraftResults(input, extensionRegistry);
                }
            };
            private static final long serialVersionUID = 0L;
            private static final CraftResults DEFAULT_INSTANCE = new CraftResults();
            private int bitField0_;
            private ItemCraft_Other.SendItemCraftList.CraftResult succeedList_;
            private ItemCraft_Other.SendItemCraftList.CraftResult failList_;
            private int successRatio_;
            private byte memoizedIsInitialized = -1;

            private CraftResults(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private CraftResults() {
                this.successRatio_ = 0;
            }

            private CraftResults(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            case 10:
                                ItemCraft_Other.SendItemCraftList.CraftResult.Builder subBuilder = null;
                                if ((this.bitField0_ & 0x1) == 1) {
                                    subBuilder = this.succeedList_.toBuilder();
                                }
                                this.succeedList_ = ((ItemCraft_Other.SendItemCraftList.CraftResult) input.readMessage(ItemCraft_Other.SendItemCraftList.CraftResult.PARSER, extensionRegistry));
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom(this.succeedList_);
                                    this.succeedList_ = subBuilder.buildPartial();
                                }
                                this.bitField0_ |= 1;
                                break;
                            case 18:
                                ItemCraft_Other.SendItemCraftList.CraftResult.Builder subBuilder1 = null;
                                if ((this.bitField0_ & 0x2) == 2) {
                                    subBuilder1 = this.failList_.toBuilder();
                                }
                                this.failList_ = ((ItemCraft_Other.SendItemCraftList.CraftResult) input.readMessage(ItemCraft_Other.SendItemCraftList.CraftResult.PARSER, extensionRegistry));
                                if (subBuilder1 != null) {
                                    subBuilder1.mergeFrom(this.failList_);
                                    this.failList_ = subBuilder1.buildPartial();
                                }
                                this.bitField0_ |= 2;
                                break;
                            case 24:
                                this.bitField0_ |= 4;
                                this.successRatio_ = input.readInt32();
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

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraftList_CraftResults_descriptor;
            }

            public static CraftResults parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return (CraftResults) PARSER.parseFrom(data);
            }

            public static CraftResults parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (CraftResults) PARSER.parseFrom(data, extensionRegistry);
            }

            public static CraftResults parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return (CraftResults) PARSER.parseFrom(data);
            }

            public static CraftResults parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (CraftResults) PARSER.parseFrom(data, extensionRegistry);
            }

            public static CraftResults parseFrom(InputStream input) throws IOException {
                return (CraftResults) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static CraftResults parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (CraftResults) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static CraftResults parseDelimitedFrom(InputStream input) throws IOException {
                return (CraftResults) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static CraftResults parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (CraftResults) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static CraftResults parseFrom(CodedInputStream input) throws IOException {
                return (CraftResults) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static CraftResults parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (CraftResults) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(CraftResults prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            public static CraftResults getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<CraftResults> parser() {
                return PARSER;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraftList_CraftResults_fieldAccessorTable.ensureFieldAccessorsInitialized(CraftResults.class, Builder.class);
            }

            public boolean hasSucceedList() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public ItemCraft_Other.SendItemCraftList.CraftResult getSucceedList() {
                return this.succeedList_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance() : this.succeedList_;
            }

            public ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder getSucceedListOrBuilder() {
                return this.succeedList_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance() : this.succeedList_;
            }

            public boolean hasFailList() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public ItemCraft_Other.SendItemCraftList.CraftResult getFailList() {
                return this.failList_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance() : this.failList_;
            }

            public ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder getFailListOrBuilder() {
                return this.failList_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance() : this.failList_;
            }

            public boolean hasSuccessRatio() {
                return (this.bitField0_ & 0x4) == 4;
            }

            public int getSuccessRatio() {
                return this.successRatio_;
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                if (!hasSucceedList()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasFailList()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasSuccessRatio()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!getSucceedList().isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!getFailList().isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 0x1) == 1) {
                    output.writeMessage(1, getSucceedList());
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    output.writeMessage(2, getFailList());
                }
                if ((this.bitField0_ & 0x4) == 4) {
                    output.writeInt32(3, this.successRatio_);
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
                    size = size + CodedOutputStream.computeMessageSize(1, getSucceedList());
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    size = size + CodedOutputStream.computeMessageSize(2, getFailList());
                }
                if ((this.bitField0_ & 0x4) == 4) {
                    size = size + CodedOutputStream.computeInt32Size(3, this.successRatio_);
                }
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof CraftResults)) {
                    return super.equals(obj);
                }
                CraftResults other = (CraftResults) obj;
                boolean result = true;
                result = (result) && (hasSucceedList() == other.hasSucceedList());
                if (hasSucceedList()) {
                    result = (result) && (getSucceedList().equals(other.getSucceedList()));
                }
                result = (result) && (hasFailList() == other.hasFailList());
                if (hasFailList()) {
                    result = (result) && (getFailList().equals(other.getFailList()));
                }
                result = (result) && (hasSuccessRatio() == other.hasSuccessRatio());
                if (hasSuccessRatio()) {
                    result = (result) && (getSuccessRatio() == other.getSuccessRatio());
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
                if (hasSucceedList()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + getSucceedList().hashCode();
                }
                if (hasFailList()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + getFailList().hashCode();
                }
                if (hasSuccessRatio()) {
                    hash = 37 * hash + 3;
                    hash = 53 * hash + getSuccessRatio();
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

            public Parser<CraftResults> getParserForType() {
                return PARSER;
            }

            public CraftResults getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.CraftResultsOrBuilder {
                private int bitField0_;
                private ItemCraft_Other.SendItemCraftList.CraftResult succeedList_ = null;
                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult, ItemCraft_Other.SendItemCraftList.CraftResult.Builder, ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder> succeedListBuilder_;
                private ItemCraft_Other.SendItemCraftList.CraftResult failList_ = null;
                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult, ItemCraft_Other.SendItemCraftList.CraftResult.Builder, ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder> failListBuilder_;
                private int successRatio_;

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_CraftResults_descriptor;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_CraftResults_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.CraftResults.class, Builder.class);
                }

                private void maybeForceBuilderInitialization() {
                    if (ItemCraft_Other.SendItemCraftList.CraftResults.alwaysUseFieldBuilders) {
                        getSucceedListFieldBuilder();
                        getFailListFieldBuilder();
                    }
                }

                public Builder clear() {
                    super.clear();
                    if (this.succeedListBuilder_ == null) {
                        this.succeedList_ = null;
                    } else {
                        this.succeedListBuilder_.clear();
                    }
                    this.bitField0_ &= -2;
                    if (this.failListBuilder_ == null) {
                        this.failList_ = null;
                    } else {
                        this.failListBuilder_.clear();
                    }
                    this.bitField0_ &= -3;
                    this.successRatio_ = 0;
                    this.bitField0_ &= -5;
                    return this;
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return ItemCraft_Other.SendItemCraftList_CraftResults_descriptor;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResults getDefaultInstanceForType() {
                    return ItemCraft_Other.SendItemCraftList.CraftResults.getDefaultInstance();
                }

                public ItemCraft_Other.SendItemCraftList.CraftResults build() {
                    ItemCraft_Other.SendItemCraftList.CraftResults result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResults buildPartial() {
                    ItemCraft_Other.SendItemCraftList.CraftResults result = new ItemCraft_Other.SendItemCraftList.CraftResults(this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 1) {
                        to_bitField0_ |= 1;
                    }
                    if (this.succeedListBuilder_ == null) {
                        result.succeedList_ = this.succeedList_;
                    } else {
                        result.succeedList_ = ((ItemCraft_Other.SendItemCraftList.CraftResult) this.succeedListBuilder_.build());
                    }
                    if ((from_bitField0_ & 0x2) == 2) {
                        to_bitField0_ |= 2;
                    }
                    if (this.failListBuilder_ == null) {
                        result.failList_ = this.failList_;
                    } else {
                        result.failList_ = ((ItemCraft_Other.SendItemCraftList.CraftResult) this.failListBuilder_.build());
                    }
                    if ((from_bitField0_ & 0x4) == 4) {
                        to_bitField0_ |= 4;
                    }
                    result.successRatio_ = this.successRatio_;
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
                    if ((other instanceof ItemCraft_Other.SendItemCraftList.CraftResults)) {
                        return mergeFrom((ItemCraft_Other.SendItemCraftList.CraftResults) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.CraftResults other) {
                    if (other == ItemCraft_Other.SendItemCraftList.CraftResults.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasSucceedList()) {
                        mergeSucceedList(other.getSucceedList());
                    }
                    if (other.hasFailList()) {
                        mergeFailList(other.getFailList());
                    }
                    if (other.hasSuccessRatio()) {
                        setSuccessRatio(other.getSuccessRatio());
                    }
                    mergeUnknownFields(other.unknownFields);
                    onChanged();
                    return this;
                }

                public final boolean isInitialized() {
                    if (!hasSucceedList()) {
                        return false;
                    }
                    if (!hasFailList()) {
                        return false;
                    }
                    if (!hasSuccessRatio()) {
                        return false;
                    }
                    if (!getSucceedList().isInitialized()) {
                        return false;
                    }
                    if (!getFailList().isInitialized()) {
                        return false;
                    }
                    return true;
                }

                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    ItemCraft_Other.SendItemCraftList.CraftResults parsedMessage = null;
                    try {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.CraftResults) ItemCraft_Other.SendItemCraftList.CraftResults.PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.CraftResults) e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    } finally {
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }

                public boolean hasSucceedList() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult getSucceedList() {
                    if (this.succeedListBuilder_ == null) {
                        return this.succeedList_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance() : this.succeedList_;
                    }
                    return (ItemCraft_Other.SendItemCraftList.CraftResult) this.succeedListBuilder_.getMessage();
                }

                public Builder setSucceedList(ItemCraft_Other.SendItemCraftList.CraftResult value) {
                    if (this.succeedListBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.succeedList_ = value;
                        onChanged();
                    } else {
                        this.succeedListBuilder_.setMessage(value);
                    }
                    this.bitField0_ |= 1;
                    return this;
                }

                public Builder setSucceedList(ItemCraft_Other.SendItemCraftList.CraftResult.Builder builderForValue) {
                    if (this.succeedListBuilder_ == null) {
                        this.succeedList_ = builderForValue.build();
                        onChanged();
                    } else {
                        this.succeedListBuilder_.setMessage(builderForValue.build());
                    }
                    this.bitField0_ |= 1;
                    return this;
                }

                public Builder mergeSucceedList(ItemCraft_Other.SendItemCraftList.CraftResult value) {
                    if (this.succeedListBuilder_ == null) {
                        if (((this.bitField0_ & 0x1) == 1) && (this.succeedList_ != null) && (this.succeedList_ != ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance())) {
                            this.succeedList_ = ItemCraft_Other.SendItemCraftList.CraftResult.newBuilder(this.succeedList_).mergeFrom(value).buildPartial();
                        } else {
                            this.succeedList_ = value;
                        }
                        onChanged();
                    } else {
                        this.succeedListBuilder_.mergeFrom(value);
                    }
                    this.bitField0_ |= 1;
                    return this;
                }

                public Builder clearSucceedList() {
                    if (this.succeedListBuilder_ == null) {
                        this.succeedList_ = null;
                        onChanged();
                    } else {
                        this.succeedListBuilder_.clear();
                    }
                    this.bitField0_ &= -2;
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.Builder getSucceedListBuilder() {
                    this.bitField0_ |= 1;
                    onChanged();
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.Builder) getSucceedListFieldBuilder().getBuilder();
                }

                public ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder getSucceedListOrBuilder() {
                    if (this.succeedListBuilder_ != null) {
                        return (ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder) this.succeedListBuilder_.getMessageOrBuilder();
                    }
                    return this.succeedList_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance() : this.succeedList_;
                }

                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult, ItemCraft_Other.SendItemCraftList.CraftResult.Builder, ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder> getSucceedListFieldBuilder() {
                    if (this.succeedListBuilder_ == null) {
                        this.succeedListBuilder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult, ItemCraft_Other.SendItemCraftList.CraftResult.Builder, ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder>(getSucceedList(), getParentForChildren(), isClean());
                        this.succeedList_ = null;
                    }
                    return this.succeedListBuilder_;
                }

                public boolean hasFailList() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult getFailList() {
                    if (this.failListBuilder_ == null) {
                        return this.failList_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance() : this.failList_;
                    }
                    return (ItemCraft_Other.SendItemCraftList.CraftResult) this.failListBuilder_.getMessage();
                }

                public Builder setFailList(ItemCraft_Other.SendItemCraftList.CraftResult value) {
                    if (this.failListBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.failList_ = value;
                        onChanged();
                    } else {
                        this.failListBuilder_.setMessage(value);
                    }
                    this.bitField0_ |= 2;
                    return this;
                }

                public Builder setFailList(ItemCraft_Other.SendItemCraftList.CraftResult.Builder builderForValue) {
                    if (this.failListBuilder_ == null) {
                        this.failList_ = builderForValue.build();
                        onChanged();
                    } else {
                        this.failListBuilder_.setMessage(builderForValue.build());
                    }
                    this.bitField0_ |= 2;
                    return this;
                }

                public Builder mergeFailList(ItemCraft_Other.SendItemCraftList.CraftResult value) {
                    if (this.failListBuilder_ == null) {
                        if (((this.bitField0_ & 0x2) == 2) && (this.failList_ != null) && (this.failList_ != ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance())) {
                            this.failList_ = ItemCraft_Other.SendItemCraftList.CraftResult.newBuilder(this.failList_).mergeFrom(value).buildPartial();
                        } else {
                            this.failList_ = value;
                        }
                        onChanged();
                    } else {
                        this.failListBuilder_.mergeFrom(value);
                    }
                    this.bitField0_ |= 2;
                    return this;
                }

                public Builder clearFailList() {
                    if (this.failListBuilder_ == null) {
                        this.failList_ = null;
                        onChanged();
                    } else {
                        this.failListBuilder_.clear();
                    }
                    this.bitField0_ &= -3;
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.CraftResult.Builder getFailListBuilder() {
                    this.bitField0_ |= 2;
                    onChanged();
                    return (ItemCraft_Other.SendItemCraftList.CraftResult.Builder) getFailListFieldBuilder().getBuilder();
                }

                public ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder getFailListOrBuilder() {
                    if (this.failListBuilder_ != null) {
                        return (ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder) this.failListBuilder_.getMessageOrBuilder();
                    }
                    return this.failList_ == null ? ItemCraft_Other.SendItemCraftList.CraftResult.getDefaultInstance() : this.failList_;
                }

                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult, ItemCraft_Other.SendItemCraftList.CraftResult.Builder, ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder> getFailListFieldBuilder() {
                    if (this.failListBuilder_ == null) {
                        this.failListBuilder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.CraftResult, ItemCraft_Other.SendItemCraftList.CraftResult.Builder, ItemCraft_Other.SendItemCraftList.CraftResultOrBuilder>(getFailList(), getParentForChildren(), isClean());
                        this.failList_ = null;
                    }
                    return this.failListBuilder_;
                }

                public boolean hasSuccessRatio() {
                    return (this.bitField0_ & 0x4) == 4;
                }

                public int getSuccessRatio() {
                    return this.successRatio_;
                }

                public Builder setSuccessRatio(int value) {
                    this.bitField0_ |= 4;
                    this.successRatio_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearSuccessRatio() {
                    this.bitField0_ &= -5;
                    this.successRatio_ = 0;
                    onChanged();
                    return this;
                }

                public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.setUnknownFields(unknownFields);
                }

                public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.mergeUnknownFields(unknownFields);
                }
            }
        }

        public static final class Material extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.MaterialOrBuilder {
            public static final int MATERIAL_FIELD_NUMBER = 1;
            public static final int ADDMATERIAL_FIELD_NUMBER = 2;
            @Deprecated
            public static final Parser<Material> PARSER = new AbstractParser<Material>() {
                public ItemCraft_Other.SendItemCraftList.Material parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ItemCraft_Other.SendItemCraftList.Material(input, extensionRegistry);
                }
            };
            private static final long serialVersionUID = 0L;
            private static final Material DEFAULT_INSTANCE = new Material();
            private List<MaterialModel> material_;
            private List<MaterialModel> addMaterial_;
            private byte memoizedIsInitialized = -1;

            private Material(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private Material() {
                this.material_ = Collections.emptyList();
                this.addMaterial_ = Collections.emptyList();
            }

            private Material(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this();
                int mutable_bitField0_ = 0;
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
                            case 10:
                                if ((mutable_bitField0_ & 0x1) != 1) {
                                    this.material_ = new ArrayList<MaterialModel>();
                                    mutable_bitField0_ |= 1;
                                }
                                this.material_.add((MaterialModel) input.readMessage(MaterialModel.PARSER, extensionRegistry));
                                break;
                            case 18:
                                if ((mutable_bitField0_ & 0x2) != 2) {
                                    this.addMaterial_ = new ArrayList<MaterialModel>();
                                    mutable_bitField0_ |= 2;
                                }
                                this.addMaterial_.add((MaterialModel) input.readMessage(MaterialModel.PARSER, extensionRegistry));
                                break;
                        }
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e) {
                    throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
                } finally {
                    if ((mutable_bitField0_ & 0x1) == 1) {
                        this.material_ = Collections.unmodifiableList(this.material_);
                    }
                    if ((mutable_bitField0_ & 0x2) == 2) {
                        this.addMaterial_ = Collections.unmodifiableList(this.addMaterial_);
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraftList_Material_descriptor;
            }

            public static Material parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return (Material) PARSER.parseFrom(data);
            }

            public static Material parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (Material) PARSER.parseFrom(data, extensionRegistry);
            }

            public static Material parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return (Material) PARSER.parseFrom(data);
            }

            public static Material parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (Material) PARSER.parseFrom(data, extensionRegistry);
            }

            public static Material parseFrom(InputStream input) throws IOException {
                return (Material) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static Material parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (Material) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Material parseDelimitedFrom(InputStream input) throws IOException {
                return (Material) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static Material parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (Material) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static Material parseFrom(CodedInputStream input) throws IOException {
                return (Material) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static Material parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (Material) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(Material prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            public static Material getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<Material> parser() {
                return PARSER;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraftList_Material_fieldAccessorTable.ensureFieldAccessorsInitialized(Material.class, Builder.class);
            }

            public List<MaterialModel> getMaterialList() {
                return this.material_;
            }

            public List<? extends MaterialModelOrBuilder> getMaterialOrBuilderList() {
                return this.material_;
            }

            public int getMaterialCount() {
                return this.material_.size();
            }

            public MaterialModel getMaterial(int index) {
                return (MaterialModel) this.material_.get(index);
            }

            public MaterialModelOrBuilder getMaterialOrBuilder(int index) {
                return (MaterialModelOrBuilder) this.material_.get(index);
            }

            public List<MaterialModel> getAddMaterialList() {
                return this.addMaterial_;
            }

            public List<? extends MaterialModelOrBuilder> getAddMaterialOrBuilderList() {
                return this.addMaterial_;
            }

            public int getAddMaterialCount() {
                return this.addMaterial_.size();
            }

            public MaterialModel getAddMaterial(int index) {
                return (MaterialModel) this.addMaterial_.get(index);
            }

            public MaterialModelOrBuilder getAddMaterialOrBuilder(int index) {
                return (MaterialModelOrBuilder) this.addMaterial_.get(index);
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                for (int i = 0; i < getMaterialCount(); i++) {
                    if (!getMaterial(i).isInitialized()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                }
                for (int i = 0; i < getAddMaterialCount(); i++) {
                    if (!getAddMaterial(i).isInitialized()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                for (int i = 0; i < this.material_.size(); i++) {
                    output.writeMessage(1, (MessageLite) this.material_.get(i));
                }
                for (int i = 0; i < this.addMaterial_.size(); i++) {
                    output.writeMessage(2, (MessageLite) this.addMaterial_.get(i));
                }
                this.unknownFields.writeTo(output);
            }

            public int getSerializedSize() {
                int size = this.memoizedSize;
                if (size != -1) {
                    return size;
                }
                size = 0;
                for (int i = 0; i < this.material_.size(); i++) {
                    size = size + CodedOutputStream.computeMessageSize(1, (MessageLite) this.material_.get(i));
                }
                for (int i = 0; i < this.addMaterial_.size(); i++) {
                    size = size + CodedOutputStream.computeMessageSize(2, (MessageLite) this.addMaterial_.get(i));
                }
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof Material)) {
                    return super.equals(obj);
                }
                Material other = (Material) obj;
                boolean result = true;
                result = (result) && (getMaterialList().equals(other.getMaterialList()));
                result = (result) && (getAddMaterialList().equals(other.getAddMaterialList()));
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
                if (getMaterialCount() > 0) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + getMaterialList().hashCode();
                }
                if (getAddMaterialCount() > 0) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + getAddMaterialList().hashCode();
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

            public Parser<Material> getParserForType() {
                return PARSER;
            }

            public Material getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static abstract interface MaterialModelOrBuilder extends MessageOrBuilder {
                public abstract boolean hasItemDescId();

                public abstract int getItemDescId();

                public abstract boolean hasCount();

                public abstract int getCount();

                public abstract boolean hasWindowNum();

                public abstract int getWindowNum();

                public abstract boolean hasEnchantLevel();

                public abstract int getEnchantLevel();

                public abstract boolean hasBless();

                public abstract int getBless();

                public abstract boolean hasName();

                public abstract ByteString getName();

                public abstract boolean hasGfxId();

                public abstract int getGfxId();

                public abstract boolean hasUnknow1();

                public abstract int getUnknow1();

                public abstract boolean hasUnknow2();

                public abstract int getUnknow2();

                public abstract boolean hasUnknow3();

                public abstract int getUnknow3();
            }

            public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.MaterialOrBuilder {
                private int bitField0_;
                private List<ItemCraft_Other.SendItemCraftList.Material.MaterialModel> material_ = Collections.emptyList();
                private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.Material.MaterialModel, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder, ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder> materialBuilder_;
                private List<ItemCraft_Other.SendItemCraftList.Material.MaterialModel> addMaterial_ = Collections.emptyList();
                private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.Material.MaterialModel, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder, ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder> addMaterialBuilder_;

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_Material_descriptor;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_Material_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.Material.class, Builder.class);
                }

                private void maybeForceBuilderInitialization() {
                    if (ItemCraft_Other.SendItemCraftList.Material.alwaysUseFieldBuilders) {
                        getMaterialFieldBuilder();
                        getAddMaterialFieldBuilder();
                    }
                }

                public Builder clear() {
                    super.clear();
                    if (this.materialBuilder_ == null) {
                        this.material_ = Collections.emptyList();
                        this.bitField0_ &= -2;
                    } else {
                        this.materialBuilder_.clear();
                    }
                    if (this.addMaterialBuilder_ == null) {
                        this.addMaterial_ = Collections.emptyList();
                        this.bitField0_ &= -3;
                    } else {
                        this.addMaterialBuilder_.clear();
                    }
                    return this;
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return ItemCraft_Other.SendItemCraftList_Material_descriptor;
                }

                public ItemCraft_Other.SendItemCraftList.Material getDefaultInstanceForType() {
                    return ItemCraft_Other.SendItemCraftList.Material.getDefaultInstance();
                }

                public ItemCraft_Other.SendItemCraftList.Material build() {
                    ItemCraft_Other.SendItemCraftList.Material result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public ItemCraft_Other.SendItemCraftList.Material buildPartial() {
                    ItemCraft_Other.SendItemCraftList.Material result = new ItemCraft_Other.SendItemCraftList.Material(this);
                    @SuppressWarnings("unused") int from_bitField0_ = this.bitField0_;
                    if (this.materialBuilder_ == null) {
                        if ((this.bitField0_ & 0x1) == 1) {
                            this.material_ = Collections.unmodifiableList(this.material_);
                            this.bitField0_ &= -2;
                        }
                        result.material_ = this.material_;
                    } else {
                        result.material_ = this.materialBuilder_.build();
                    }
                    if (this.addMaterialBuilder_ == null) {
                        if ((this.bitField0_ & 0x2) == 2) {
                            this.addMaterial_ = Collections.unmodifiableList(this.addMaterial_);
                            this.bitField0_ &= -3;
                        }
                        result.addMaterial_ = this.addMaterial_;
                    } else {
                        result.addMaterial_ = this.addMaterialBuilder_.build();
                    }
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
                    if ((other instanceof ItemCraft_Other.SendItemCraftList.Material)) {
                        return mergeFrom((ItemCraft_Other.SendItemCraftList.Material) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.Material other) {
                    if (other == ItemCraft_Other.SendItemCraftList.Material.getDefaultInstance()) {
                        return this;
                    }
                    if (this.materialBuilder_ == null) {
                        if (!other.material_.isEmpty()) {
                            if (this.material_.isEmpty()) {
                                this.material_ = other.material_;
                                this.bitField0_ &= -2;
                            } else {
                                ensureMaterialIsMutable();
                                this.material_.addAll(other.material_);
                            }
                            onChanged();
                        }
                    } else if (!other.material_.isEmpty()) {
                        if (this.materialBuilder_.isEmpty()) {
                            this.materialBuilder_.dispose();
                            this.materialBuilder_ = null;
                            this.material_ = other.material_;
                            this.bitField0_ &= -2;
                            this.materialBuilder_ = (ItemCraft_Other.SendItemCraftList.Material.alwaysUseFieldBuilders ? getMaterialFieldBuilder() : null);
                        } else {
                            this.materialBuilder_.addAllMessages(other.material_);
                        }
                    }
                    if (this.addMaterialBuilder_ == null) {
                        if (!other.addMaterial_.isEmpty()) {
                            if (this.addMaterial_.isEmpty()) {
                                this.addMaterial_ = other.addMaterial_;
                                this.bitField0_ &= -3;
                            } else {
                                ensureAddMaterialIsMutable();
                                this.addMaterial_.addAll(other.addMaterial_);
                            }
                            onChanged();
                        }
                    } else if (!other.addMaterial_.isEmpty()) {
                        if (this.addMaterialBuilder_.isEmpty()) {
                            this.addMaterialBuilder_.dispose();
                            this.addMaterialBuilder_ = null;
                            this.addMaterial_ = other.addMaterial_;
                            this.bitField0_ &= -3;
                            this.addMaterialBuilder_ = (ItemCraft_Other.SendItemCraftList.Material.alwaysUseFieldBuilders ? getAddMaterialFieldBuilder() : null);
                        } else {
                            this.addMaterialBuilder_.addAllMessages(other.addMaterial_);
                        }
                    }
                    mergeUnknownFields(other.unknownFields);
                    onChanged();
                    return this;
                }

                public final boolean isInitialized() {
                    for (int i = 0; i < getMaterialCount(); i++) {
                        if (!getMaterial(i).isInitialized()) {
                            return false;
                        }
                    }
                    for (int i = 0; i < getAddMaterialCount(); i++) {
                        if (!getAddMaterial(i).isInitialized()) {
                            return false;
                        }
                    }
                    return true;
                }

                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    ItemCraft_Other.SendItemCraftList.Material parsedMessage = null;
                    try {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.Material) ItemCraft_Other.SendItemCraftList.Material.PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.Material) e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    } finally {
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }

                private void ensureMaterialIsMutable() {
                    if ((this.bitField0_ & 0x1) != 1) {
                        this.material_ = new ArrayList<MaterialModel>(this.material_);
                        this.bitField0_ |= 1;
                    }
                }

                public List<ItemCraft_Other.SendItemCraftList.Material.MaterialModel> getMaterialList() {
                    if (this.materialBuilder_ == null) {
                        return Collections.unmodifiableList(this.material_);
                    }
                    return this.materialBuilder_.getMessageList();
                }

                public int getMaterialCount() {
                    if (this.materialBuilder_ == null) {
                        return this.material_.size();
                    }
                    return this.materialBuilder_.getCount();
                }

                public ItemCraft_Other.SendItemCraftList.Material.MaterialModel getMaterial(int index) {
                    if (this.materialBuilder_ == null) {
                        return (ItemCraft_Other.SendItemCraftList.Material.MaterialModel) this.material_.get(index);
                    }
                    return (ItemCraft_Other.SendItemCraftList.Material.MaterialModel) this.materialBuilder_.getMessage(index);
                }

                public Builder setMaterial(int index, ItemCraft_Other.SendItemCraftList.Material.MaterialModel value) {
                    if (this.materialBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureMaterialIsMutable();
                        this.material_.set(index, value);
                        onChanged();
                    } else {
                        this.materialBuilder_.setMessage(index, value);
                    }
                    return this;
                }

                public Builder setMaterial(int index, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder builderForValue) {
                    if (this.materialBuilder_ == null) {
                        ensureMaterialIsMutable();
                        this.material_.set(index, builderForValue.build());
                        onChanged();
                    } else {
                        this.materialBuilder_.setMessage(index, builderForValue.build());
                    }
                    return this;
                }

                public Builder addMaterial(ItemCraft_Other.SendItemCraftList.Material.MaterialModel value) {
                    if (this.materialBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureMaterialIsMutable();
                        this.material_.add(value);
                        onChanged();
                    } else {
                        this.materialBuilder_.addMessage(value);
                    }
                    return this;
                }

                public Builder addMaterial(int index, ItemCraft_Other.SendItemCraftList.Material.MaterialModel value) {
                    if (this.materialBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureMaterialIsMutable();
                        this.material_.add(index, value);
                        onChanged();
                    } else {
                        this.materialBuilder_.addMessage(index, value);
                    }
                    return this;
                }

                public Builder addMaterial(ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder builderForValue) {
                    if (this.materialBuilder_ == null) {
                        ensureMaterialIsMutable();
                        this.material_.add(builderForValue.build());
                        onChanged();
                    } else {
                        this.materialBuilder_.addMessage(builderForValue.build());
                    }
                    return this;
                }

                public Builder addMaterial(int index, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder builderForValue) {
                    if (this.materialBuilder_ == null) {
                        ensureMaterialIsMutable();
                        this.material_.add(index, builderForValue.build());
                        onChanged();
                    } else {
                        this.materialBuilder_.addMessage(index, builderForValue.build());
                    }
                    return this;
                }

                public Builder addAllMaterial(Iterable<? extends ItemCraft_Other.SendItemCraftList.Material.MaterialModel> values) {
                    if (this.materialBuilder_ == null) {
                        ensureMaterialIsMutable();
                        AbstractMessageLite.Builder.addAll(values, this.material_);
                        onChanged();
                    } else {
                        this.materialBuilder_.addAllMessages(values);
                    }
                    return this;
                }

                public Builder clearMaterial() {
                    if (this.materialBuilder_ == null) {
                        this.material_ = Collections.emptyList();
                        this.bitField0_ &= -2;
                        onChanged();
                    } else {
                        this.materialBuilder_.clear();
                    }
                    return this;
                }

                public Builder removeMaterial(int index) {
                    if (this.materialBuilder_ == null) {
                        ensureMaterialIsMutable();
                        this.material_.remove(index);
                        onChanged();
                    } else {
                        this.materialBuilder_.remove(index);
                    }
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder getMaterialBuilder(int index) {
                    return (ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder) getMaterialFieldBuilder().getBuilder(index);
                }

                public ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder getMaterialOrBuilder(int index) {
                    if (this.materialBuilder_ == null) {
                        return (ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder) this.material_.get(index);
                    }
                    return (ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder) this.materialBuilder_.getMessageOrBuilder(index);
                }

                public List<? extends ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder> getMaterialOrBuilderList() {
                    if (this.materialBuilder_ != null) {
                        return this.materialBuilder_.getMessageOrBuilderList();
                    }
                    return Collections.unmodifiableList(this.material_);
                }

                public ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder addMaterialBuilder() {
                    return (ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder) getMaterialFieldBuilder().addBuilder(ItemCraft_Other.SendItemCraftList.Material.MaterialModel.getDefaultInstance());
                }

                public ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder addMaterialBuilder(int index) {
                    return (ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder) getMaterialFieldBuilder().addBuilder(index, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.getDefaultInstance());
                }

                public List<ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder> getMaterialBuilderList() {
                    return getMaterialFieldBuilder().getBuilderList();
                }

                private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.Material.MaterialModel, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder, ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder> getMaterialFieldBuilder() {
                    if (this.materialBuilder_ == null) {
                        this.materialBuilder_ = new RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.Material.MaterialModel, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder, ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder>(this.material_, (this.bitField0_ & 0x1) == 1, getParentForChildren(), isClean());
                        this.material_ = null;
                    }
                    return this.materialBuilder_;
                }

                private void ensureAddMaterialIsMutable() {
                    if ((this.bitField0_ & 0x2) != 2) {
                        this.addMaterial_ = new ArrayList<MaterialModel>(this.addMaterial_);
                        this.bitField0_ |= 2;
                    }
                }

                public List<ItemCraft_Other.SendItemCraftList.Material.MaterialModel> getAddMaterialList() {
                    if (this.addMaterialBuilder_ == null) {
                        return Collections.unmodifiableList(this.addMaterial_);
                    }
                    return this.addMaterialBuilder_.getMessageList();
                }

                public int getAddMaterialCount() {
                    if (this.addMaterialBuilder_ == null) {
                        return this.addMaterial_.size();
                    }
                    return this.addMaterialBuilder_.getCount();
                }

                public ItemCraft_Other.SendItemCraftList.Material.MaterialModel getAddMaterial(int index) {
                    if (this.addMaterialBuilder_ == null) {
                        return (ItemCraft_Other.SendItemCraftList.Material.MaterialModel) this.addMaterial_.get(index);
                    }
                    return (ItemCraft_Other.SendItemCraftList.Material.MaterialModel) this.addMaterialBuilder_.getMessage(index);
                }

                public Builder setAddMaterial(int index, ItemCraft_Other.SendItemCraftList.Material.MaterialModel value) {
                    if (this.addMaterialBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureAddMaterialIsMutable();
                        this.addMaterial_.set(index, value);
                        onChanged();
                    } else {
                        this.addMaterialBuilder_.setMessage(index, value);
                    }
                    return this;
                }

                public Builder setAddMaterial(int index, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder builderForValue) {
                    if (this.addMaterialBuilder_ == null) {
                        ensureAddMaterialIsMutable();
                        this.addMaterial_.set(index, builderForValue.build());
                        onChanged();
                    } else {
                        this.addMaterialBuilder_.setMessage(index, builderForValue.build());
                    }
                    return this;
                }

                public Builder addAddMaterial(ItemCraft_Other.SendItemCraftList.Material.MaterialModel value) {
                    if (this.addMaterialBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureAddMaterialIsMutable();
                        this.addMaterial_.add(value);
                        onChanged();
                    } else {
                        this.addMaterialBuilder_.addMessage(value);
                    }
                    return this;
                }

                public Builder addAddMaterial(int index, ItemCraft_Other.SendItemCraftList.Material.MaterialModel value) {
                    if (this.addMaterialBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureAddMaterialIsMutable();
                        this.addMaterial_.add(index, value);
                        onChanged();
                    } else {
                        this.addMaterialBuilder_.addMessage(index, value);
                    }
                    return this;
                }

                public Builder addAddMaterial(ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder builderForValue) {
                    if (this.addMaterialBuilder_ == null) {
                        ensureAddMaterialIsMutable();
                        this.addMaterial_.add(builderForValue.build());
                        onChanged();
                    } else {
                        this.addMaterialBuilder_.addMessage(builderForValue.build());
                    }
                    return this;
                }

                public Builder addAddMaterial(int index, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder builderForValue) {
                    if (this.addMaterialBuilder_ == null) {
                        ensureAddMaterialIsMutable();
                        this.addMaterial_.add(index, builderForValue.build());
                        onChanged();
                    } else {
                        this.addMaterialBuilder_.addMessage(index, builderForValue.build());
                    }
                    return this;
                }

                public Builder addAllAddMaterial(Iterable<? extends ItemCraft_Other.SendItemCraftList.Material.MaterialModel> values) {
                    if (this.addMaterialBuilder_ == null) {
                        ensureAddMaterialIsMutable();
                        AbstractMessageLite.Builder.addAll(values, this.addMaterial_);
                        onChanged();
                    } else {
                        this.addMaterialBuilder_.addAllMessages(values);
                    }
                    return this;
                }

                public Builder clearAddMaterial() {
                    if (this.addMaterialBuilder_ == null) {
                        this.addMaterial_ = Collections.emptyList();
                        this.bitField0_ &= -3;
                        onChanged();
                    } else {
                        this.addMaterialBuilder_.clear();
                    }
                    return this;
                }

                public Builder removeAddMaterial(int index) {
                    if (this.addMaterialBuilder_ == null) {
                        ensureAddMaterialIsMutable();
                        this.addMaterial_.remove(index);
                        onChanged();
                    } else {
                        this.addMaterialBuilder_.remove(index);
                    }
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder getAddMaterialBuilder(int index) {
                    return (ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder) getAddMaterialFieldBuilder().getBuilder(index);
                }

                public ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder getAddMaterialOrBuilder(int index) {
                    if (this.addMaterialBuilder_ == null) {
                        return (ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder) this.addMaterial_.get(index);
                    }
                    return (ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder) this.addMaterialBuilder_.getMessageOrBuilder(index);
                }

                public List<? extends ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder> getAddMaterialOrBuilderList() {
                    if (this.addMaterialBuilder_ != null) {
                        return this.addMaterialBuilder_.getMessageOrBuilderList();
                    }
                    return Collections.unmodifiableList(this.addMaterial_);
                }

                public ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder addAddMaterialBuilder() {
                    return (ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder) getAddMaterialFieldBuilder().addBuilder(ItemCraft_Other.SendItemCraftList.Material.MaterialModel.getDefaultInstance());
                }

                public ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder addAddMaterialBuilder(int index) {
                    return (ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder) getAddMaterialFieldBuilder().addBuilder(index, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.getDefaultInstance());
                }

                public List<ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder> getAddMaterialBuilderList() {
                    return getAddMaterialFieldBuilder().getBuilderList();
                }

                private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.Material.MaterialModel, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder, ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder> getAddMaterialFieldBuilder() {
                    if (this.addMaterialBuilder_ == null) {
                        this.addMaterialBuilder_ = new RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.Material.MaterialModel, ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder, ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder>(this.addMaterial_, (this.bitField0_ & 0x2) == 2, getParentForChildren(), isClean());
                        this.addMaterial_ = null;
                    }
                    return this.addMaterialBuilder_;
                }

                public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.setUnknownFields(unknownFields);
                }

                public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.mergeUnknownFields(unknownFields);
                }
            }

            public static final class MaterialModel extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder {
                public static final int ITEMDESCID_FIELD_NUMBER = 1;
                public static final int COUNT_FIELD_NUMBER = 2;
                public static final int WINDOWNUM_FIELD_NUMBER = 3;
                public static final int ENCHANTLEVEL_FIELD_NUMBER = 4;
                public static final int BLESS_FIELD_NUMBER = 5;
                public static final int NAME_FIELD_NUMBER = 6;
                public static final int GFXID_FIELD_NUMBER = 7;
                public static final int UNKNOW1_FIELD_NUMBER = 8;
                public static final int UNKNOW2_FIELD_NUMBER = 9;
                public static final int UNKNOW3_FIELD_NUMBER = 10;
                @Deprecated
                public static final Parser<MaterialModel> PARSER = new AbstractParser<MaterialModel>() {
                    public ItemCraft_Other.SendItemCraftList.Material.MaterialModel parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                        return new ItemCraft_Other.SendItemCraftList.Material.MaterialModel(input, extensionRegistry);
                    }
                };
                private static final long serialVersionUID = 0L;
                private static final MaterialModel DEFAULT_INSTANCE = new MaterialModel();
                private int bitField0_;
                private int itemDescId_;
                private int count_;
                private int windowNum_;
                private int enchantLevel_;
                private int bless_;
                private ByteString name_;
                private int gfxId_;
                private int unknow1_;
                private int unknow2_;
                private int unknow3_;
                private byte memoizedIsInitialized = -1;

                private MaterialModel(GeneratedMessageV3.Builder<?> builder) {
                    super(builder);
                }

                private MaterialModel() {
                    this.itemDescId_ = 0;
                    this.count_ = 0;
                    this.windowNum_ = 0;
                    this.enchantLevel_ = 0;
                    this.bless_ = 0;
                    this.name_ = ByteString.EMPTY;
                    this.gfxId_ = 0;
                    this.unknow1_ = 0;
                    this.unknow2_ = 0;
                    this.unknow3_ = 0;
                }

                private MaterialModel(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                    this.itemDescId_ = input.readInt32();
                                    break;
                                case 16:
                                    this.bitField0_ |= 2;
                                    this.count_ = input.readInt32();
                                    break;
                                case 24:
                                    this.bitField0_ |= 4;
                                    this.windowNum_ = input.readInt32();
                                    break;
                                case 32:
                                    this.bitField0_ |= 8;
                                    this.enchantLevel_ = input.readInt32();
                                    break;
                                case 40:
                                    this.bitField0_ |= 16;
                                    this.bless_ = input.readInt32();
                                    break;
                                case 50:
                                    this.bitField0_ |= 32;
                                    this.name_ = input.readBytes();
                                    break;
                                case 56:
                                    this.bitField0_ |= 64;
                                    this.gfxId_ = input.readInt32();
                                    break;
                                case 64:
                                    this.bitField0_ |= 128;
                                    this.unknow1_ = input.readInt32();
                                    break;
                                case 72:
                                    this.bitField0_ |= 256;
                                    this.unknow2_ = input.readInt32();
                                    break;
                                case 80:
                                    this.bitField0_ |= 512;
                                    this.unknow3_ = input.readInt32();
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

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_Material_MaterialModel_descriptor;
                }

                public static MaterialModel parseFrom(ByteString data) throws InvalidProtocolBufferException {
                    return (MaterialModel) PARSER.parseFrom(data);
                }

                public static MaterialModel parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return (MaterialModel) PARSER.parseFrom(data, extensionRegistry);
                }

                public static MaterialModel parseFrom(byte[] data) throws InvalidProtocolBufferException {
                    return (MaterialModel) PARSER.parseFrom(data);
                }

                public static MaterialModel parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return (MaterialModel) PARSER.parseFrom(data, extensionRegistry);
                }

                public static MaterialModel parseFrom(InputStream input) throws IOException {
                    return (MaterialModel) GeneratedMessageV3.parseWithIOException(PARSER, input);
                }

                public static MaterialModel parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (MaterialModel) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
                }

                public static MaterialModel parseDelimitedFrom(InputStream input) throws IOException {
                    return (MaterialModel) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
                }

                public static MaterialModel parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (MaterialModel) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
                }

                public static MaterialModel parseFrom(CodedInputStream input) throws IOException {
                    return (MaterialModel) GeneratedMessageV3.parseWithIOException(PARSER, input);
                }

                public static MaterialModel parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (MaterialModel) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
                }

                public static Builder newBuilder() {
                    return DEFAULT_INSTANCE.toBuilder();
                }

                public static Builder newBuilder(MaterialModel prototype) {
                    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
                }

                public static MaterialModel getDefaultInstance() {
                    return DEFAULT_INSTANCE;
                }

                public static Parser<MaterialModel> parser() {
                    return PARSER;
                }

                public final UnknownFieldSet getUnknownFields() {
                    return this.unknownFields;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_Material_MaterialModel_fieldAccessorTable.ensureFieldAccessorsInitialized(MaterialModel.class, Builder.class);
                }

                public boolean hasItemDescId() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public int getItemDescId() {
                    return this.itemDescId_;
                }

                public boolean hasCount() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public int getCount() {
                    return this.count_;
                }

                public boolean hasWindowNum() {
                    return (this.bitField0_ & 0x4) == 4;
                }

                public int getWindowNum() {
                    return this.windowNum_;
                }

                public boolean hasEnchantLevel() {
                    return (this.bitField0_ & 0x8) == 8;
                }

                public int getEnchantLevel() {
                    return this.enchantLevel_;
                }

                public boolean hasBless() {
                    return (this.bitField0_ & 0x10) == 16;
                }

                public int getBless() {
                    return this.bless_;
                }

                public boolean hasName() {
                    return (this.bitField0_ & 0x20) == 32;
                }

                public ByteString getName() {
                    return this.name_;
                }

                public boolean hasGfxId() {
                    return (this.bitField0_ & 0x40) == 64;
                }

                public int getGfxId() {
                    return this.gfxId_;
                }

                public boolean hasUnknow1() {
                    return (this.bitField0_ & 0x80) == 128;
                }

                public int getUnknow1() {
                    return this.unknow1_;
                }

                public boolean hasUnknow2() {
                    return (this.bitField0_ & 0x100) == 256;
                }

                public int getUnknow2() {
                    return this.unknow2_;
                }

                public boolean hasUnknow3() {
                    return (this.bitField0_ & 0x200) == 512;
                }

                public int getUnknow3() {
                    return this.unknow3_;
                }

                public final boolean isInitialized() {
                    byte isInitialized = this.memoizedIsInitialized;
                    if (isInitialized == 1) {
                        return true;
                    }
                    if (isInitialized == 0) {
                        return false;
                    }
                    if (!hasItemDescId()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasCount()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasWindowNum()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasEnchantLevel()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasBless()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasName()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasGfxId()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUnknow1()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUnknow2()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUnknow3()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    this.memoizedIsInitialized = 1;
                    return true;
                }

                public void writeTo(CodedOutputStream output) throws IOException {
                    if ((this.bitField0_ & 0x1) == 1) {
                        output.writeInt32(1, this.itemDescId_);
                    }
                    if ((this.bitField0_ & 0x2) == 2) {
                        output.writeInt32(2, this.count_);
                    }
                    if ((this.bitField0_ & 0x4) == 4) {
                        output.writeInt32(3, this.windowNum_);
                    }
                    if ((this.bitField0_ & 0x8) == 8) {
                        output.writeInt32(4, this.enchantLevel_);
                    }
                    if ((this.bitField0_ & 0x10) == 16) {
                        output.writeInt32(5, this.bless_);
                    }
                    if ((this.bitField0_ & 0x20) == 32) {
                        output.writeBytes(6, this.name_);
                    }
                    if ((this.bitField0_ & 0x40) == 64) {
                        output.writeInt32(7, this.gfxId_);
                    }
                    if ((this.bitField0_ & 0x80) == 128) {
                        output.writeInt32(8, this.unknow1_);
                    }
                    if ((this.bitField0_ & 0x100) == 256) {
                        output.writeInt32(9, this.unknow2_);
                    }
                    if ((this.bitField0_ & 0x200) == 512) {
                        output.writeInt32(10, this.unknow3_);
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
                        size = size + CodedOutputStream.computeInt32Size(1, this.itemDescId_);
                    }
                    if ((this.bitField0_ & 0x2) == 2) {
                        size = size + CodedOutputStream.computeInt32Size(2, this.count_);
                    }
                    if ((this.bitField0_ & 0x4) == 4) {
                        size = size + CodedOutputStream.computeInt32Size(3, this.windowNum_);
                    }
                    if ((this.bitField0_ & 0x8) == 8) {
                        size = size + CodedOutputStream.computeInt32Size(4, this.enchantLevel_);
                    }
                    if ((this.bitField0_ & 0x10) == 16) {
                        size = size + CodedOutputStream.computeInt32Size(5, this.bless_);
                    }
                    if ((this.bitField0_ & 0x20) == 32) {
                        size = size + CodedOutputStream.computeBytesSize(6, this.name_);
                    }
                    if ((this.bitField0_ & 0x40) == 64) {
                        size = size + CodedOutputStream.computeInt32Size(7, this.gfxId_);
                    }
                    if ((this.bitField0_ & 0x80) == 128) {
                        size = size + CodedOutputStream.computeInt32Size(8, this.unknow1_);
                    }
                    if ((this.bitField0_ & 0x100) == 256) {
                        size = size + CodedOutputStream.computeInt32Size(9, this.unknow2_);
                    }
                    if ((this.bitField0_ & 0x200) == 512) {
                        size = size + CodedOutputStream.computeInt32Size(10, this.unknow3_);
                    }
                    size += this.unknownFields.getSerializedSize();
                    this.memoizedSize = size;
                    return size;
                }

                public boolean equals(Object obj) {
                    if (obj == this) {
                        return true;
                    }
                    if (!(obj instanceof MaterialModel)) {
                        return super.equals(obj);
                    }
                    MaterialModel other = (MaterialModel) obj;
                    boolean result = true;
                    result = (result) && (hasItemDescId() == other.hasItemDescId());
                    if (hasItemDescId()) {
                        result = (result) && (getItemDescId() == other.getItemDescId());
                    }
                    result = (result) && (hasCount() == other.hasCount());
                    if (hasCount()) {
                        result = (result) && (getCount() == other.getCount());
                    }
                    result = (result) && (hasWindowNum() == other.hasWindowNum());
                    if (hasWindowNum()) {
                        result = (result) && (getWindowNum() == other.getWindowNum());
                    }
                    result = (result) && (hasEnchantLevel() == other.hasEnchantLevel());
                    if (hasEnchantLevel()) {
                        result = (result) && (getEnchantLevel() == other.getEnchantLevel());
                    }
                    result = (result) && (hasBless() == other.hasBless());
                    if (hasBless()) {
                        result = (result) && (getBless() == other.getBless());
                    }
                    result = (result) && (hasName() == other.hasName());
                    if (hasName()) {
                        result = (result) && (getName().equals(other.getName()));
                    }
                    result = (result) && (hasGfxId() == other.hasGfxId());
                    if (hasGfxId()) {
                        result = (result) && (getGfxId() == other.getGfxId());
                    }
                    result = (result) && (hasUnknow1() == other.hasUnknow1());
                    if (hasUnknow1()) {
                        result = (result) && (getUnknow1() == other.getUnknow1());
                    }
                    result = (result) && (hasUnknow2() == other.hasUnknow2());
                    if (hasUnknow2()) {
                        result = (result) && (getUnknow2() == other.getUnknow2());
                    }
                    result = (result) && (hasUnknow3() == other.hasUnknow3());
                    if (hasUnknow3()) {
                        result = (result) && (getUnknow3() == other.getUnknow3());
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
                    if (hasItemDescId()) {
                        hash = 37 * hash + 1;
                        hash = 53 * hash + getItemDescId();
                    }
                    if (hasCount()) {
                        hash = 37 * hash + 2;
                        hash = 53 * hash + getCount();
                    }
                    if (hasWindowNum()) {
                        hash = 37 * hash + 3;
                        hash = 53 * hash + getWindowNum();
                    }
                    if (hasEnchantLevel()) {
                        hash = 37 * hash + 4;
                        hash = 53 * hash + getEnchantLevel();
                    }
                    if (hasBless()) {
                        hash = 37 * hash + 5;
                        hash = 53 * hash + getBless();
                    }
                    if (hasName()) {
                        hash = 37 * hash + 6;
                        hash = 53 * hash + getName().hashCode();
                    }
                    if (hasGfxId()) {
                        hash = 37 * hash + 7;
                        hash = 53 * hash + getGfxId();
                    }
                    if (hasUnknow1()) {
                        hash = 37 * hash + 8;
                        hash = 53 * hash + getUnknow1();
                    }
                    if (hasUnknow2()) {
                        hash = 37 * hash + 9;
                        hash = 53 * hash + getUnknow2();
                    }
                    if (hasUnknow3()) {
                        hash = 37 * hash + 10;
                        hash = 53 * hash + getUnknow3();
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

                public Parser<MaterialModel> getParserForType() {
                    return PARSER;
                }

                public MaterialModel getDefaultInstanceForType() {
                    return DEFAULT_INSTANCE;
                }

                public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.Material.MaterialModelOrBuilder {
                    private int bitField0_;
                    private int itemDescId_;
                    private int count_;
                    private int windowNum_;
                    private int enchantLevel_;
                    private int bless_;
                    private ByteString name_ = ByteString.EMPTY;
                    private int gfxId_;
                    private int unknow1_;
                    private int unknow2_;
                    private int unknow3_;

                    private Builder() {
                        maybeForceBuilderInitialization();
                    }

                    private Builder(GeneratedMessageV3.BuilderParent parent) {
                        super(parent);
                        maybeForceBuilderInitialization();
                    }

                    public static final Descriptors.Descriptor getDescriptor() {
                        return ItemCraft_Other.SendItemCraftList_Material_MaterialModel_descriptor;
                    }

                    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                        return ItemCraft_Other.SendItemCraftList_Material_MaterialModel_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.Material.MaterialModel.class, Builder.class);
                    }

                    private void maybeForceBuilderInitialization() {
                    }

                    public Builder clear() {
                        super.clear();
                        this.itemDescId_ = 0;
                        this.bitField0_ &= -2;
                        this.count_ = 0;
                        this.bitField0_ &= -3;
                        this.windowNum_ = 0;
                        this.bitField0_ &= -5;
                        this.enchantLevel_ = 0;
                        this.bitField0_ &= -9;
                        this.bless_ = 0;
                        this.bitField0_ &= -17;
                        this.name_ = ByteString.EMPTY;
                        this.bitField0_ &= -33;
                        this.gfxId_ = 0;
                        this.bitField0_ &= -65;
                        this.unknow1_ = 0;
                        this.bitField0_ &= -129;
                        this.unknow2_ = 0;
                        this.bitField0_ &= -257;
                        this.unknow3_ = 0;
                        this.bitField0_ &= -513;
                        return this;
                    }

                    public Descriptors.Descriptor getDescriptorForType() {
                        return ItemCraft_Other.SendItemCraftList_Material_MaterialModel_descriptor;
                    }

                    public ItemCraft_Other.SendItemCraftList.Material.MaterialModel getDefaultInstanceForType() {
                        return ItemCraft_Other.SendItemCraftList.Material.MaterialModel.getDefaultInstance();
                    }

                    public ItemCraft_Other.SendItemCraftList.Material.MaterialModel build() {
                        ItemCraft_Other.SendItemCraftList.Material.MaterialModel result = buildPartial();
                        if (!result.isInitialized()) {
                            throw newUninitializedMessageException(result);
                        }
                        return result;
                    }

                    public ItemCraft_Other.SendItemCraftList.Material.MaterialModel buildPartial() {
                        ItemCraft_Other.SendItemCraftList.Material.MaterialModel result = new ItemCraft_Other.SendItemCraftList.Material.MaterialModel(this);
                        int from_bitField0_ = this.bitField0_;
                        int to_bitField0_ = 0;
                        if ((from_bitField0_ & 0x1) == 1) {
                            to_bitField0_ |= 1;
                        }
                        result.itemDescId_ = this.itemDescId_;
                        if ((from_bitField0_ & 0x2) == 2) {
                            to_bitField0_ |= 2;
                        }
                        result.count_ = this.count_;
                        if ((from_bitField0_ & 0x4) == 4) {
                            to_bitField0_ |= 4;
                        }
                        result.windowNum_ = this.windowNum_;
                        if ((from_bitField0_ & 0x8) == 8) {
                            to_bitField0_ |= 8;
                        }
                        result.enchantLevel_ = this.enchantLevel_;
                        if ((from_bitField0_ & 0x10) == 16) {
                            to_bitField0_ |= 16;
                        }
                        result.bless_ = this.bless_;
                        if ((from_bitField0_ & 0x20) == 32) {
                            to_bitField0_ |= 32;
                        }
                        result.name_ = this.name_;
                        if ((from_bitField0_ & 0x40) == 64) {
                            to_bitField0_ |= 64;
                        }
                        result.gfxId_ = this.gfxId_;
                        if ((from_bitField0_ & 0x80) == 128) {
                            to_bitField0_ |= 128;
                        }
                        result.unknow1_ = this.unknow1_;
                        if ((from_bitField0_ & 0x100) == 256) {
                            to_bitField0_ |= 256;
                        }
                        result.unknow2_ = this.unknow2_;
                        if ((from_bitField0_ & 0x200) == 512) {
                            to_bitField0_ |= 512;
                        }
                        result.unknow3_ = this.unknow3_;
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
                        if ((other instanceof ItemCraft_Other.SendItemCraftList.Material.MaterialModel)) {
                            return mergeFrom((ItemCraft_Other.SendItemCraftList.Material.MaterialModel) other);
                        }
                        super.mergeFrom(other);
                        return this;
                    }

                    public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.Material.MaterialModel other) {
                        if (other == ItemCraft_Other.SendItemCraftList.Material.MaterialModel.getDefaultInstance()) {
                            return this;
                        }
                        if (other.hasItemDescId()) {
                            setItemDescId(other.getItemDescId());
                        }
                        if (other.hasCount()) {
                            setCount(other.getCount());
                        }
                        if (other.hasWindowNum()) {
                            setWindowNum(other.getWindowNum());
                        }
                        if (other.hasEnchantLevel()) {
                            setEnchantLevel(other.getEnchantLevel());
                        }
                        if (other.hasBless()) {
                            setBless(other.getBless());
                        }
                        if (other.hasName()) {
                            setName(other.getName());
                        }
                        if (other.hasGfxId()) {
                            setGfxId(other.getGfxId());
                        }
                        if (other.hasUnknow1()) {
                            setUnknow1(other.getUnknow1());
                        }
                        if (other.hasUnknow2()) {
                            setUnknow2(other.getUnknow2());
                        }
                        if (other.hasUnknow3()) {
                            setUnknow3(other.getUnknow3());
                        }
                        mergeUnknownFields(other.unknownFields);
                        onChanged();
                        return this;
                    }

                    public final boolean isInitialized() {
                        if (!hasItemDescId()) {
                            return false;
                        }
                        if (!hasCount()) {
                            return false;
                        }
                        if (!hasWindowNum()) {
                            return false;
                        }
                        if (!hasEnchantLevel()) {
                            return false;
                        }
                        if (!hasBless()) {
                            return false;
                        }
                        if (!hasName()) {
                            return false;
                        }
                        if (!hasGfxId()) {
                            return false;
                        }
                        if (!hasUnknow1()) {
                            return false;
                        }
                        if (!hasUnknow2()) {
                            return false;
                        }
                        if (!hasUnknow3()) {
                            return false;
                        }
                        return true;
                    }

                    public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                        ItemCraft_Other.SendItemCraftList.Material.MaterialModel parsedMessage = null;
                        try {
                            parsedMessage = (ItemCraft_Other.SendItemCraftList.Material.MaterialModel) ItemCraft_Other.SendItemCraftList.Material.MaterialModel.PARSER.parsePartialFrom(input, extensionRegistry);
                        } catch (InvalidProtocolBufferException e) {
                            parsedMessage = (ItemCraft_Other.SendItemCraftList.Material.MaterialModel) e.getUnfinishedMessage();
                            throw e.unwrapIOException();
                        } finally {
                            if (parsedMessage != null) {
                                mergeFrom(parsedMessage);
                            }
                        }
                        return this;
                    }

                    public boolean hasItemDescId() {
                        return (this.bitField0_ & 0x1) == 1;
                    }

                    public int getItemDescId() {
                        return this.itemDescId_;
                    }

                    public Builder setItemDescId(int value) {
                        this.bitField0_ |= 1;
                        this.itemDescId_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearItemDescId() {
                        this.bitField0_ &= -2;
                        this.itemDescId_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasCount() {
                        return (this.bitField0_ & 0x2) == 2;
                    }

                    public int getCount() {
                        return this.count_;
                    }

                    public Builder setCount(int value) {
                        this.bitField0_ |= 2;
                        this.count_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearCount() {
                        this.bitField0_ &= -3;
                        this.count_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasWindowNum() {
                        return (this.bitField0_ & 0x4) == 4;
                    }

                    public int getWindowNum() {
                        return this.windowNum_;
                    }

                    public Builder setWindowNum(int value) {
                        this.bitField0_ |= 4;
                        this.windowNum_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearWindowNum() {
                        this.bitField0_ &= -5;
                        this.windowNum_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasEnchantLevel() {
                        return (this.bitField0_ & 0x8) == 8;
                    }

                    public int getEnchantLevel() {
                        return this.enchantLevel_;
                    }

                    public Builder setEnchantLevel(int value) {
                        this.bitField0_ |= 8;
                        this.enchantLevel_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearEnchantLevel() {
                        this.bitField0_ &= -9;
                        this.enchantLevel_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasBless() {
                        return (this.bitField0_ & 0x10) == 16;
                    }

                    public int getBless() {
                        return this.bless_;
                    }

                    public Builder setBless(int value) {
                        this.bitField0_ |= 16;
                        this.bless_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearBless() {
                        this.bitField0_ &= -17;
                        this.bless_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasName() {
                        return (this.bitField0_ & 0x20) == 32;
                    }

                    public ByteString getName() {
                        return this.name_;
                    }

                    public Builder setName(ByteString value) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.bitField0_ |= 32;
                        this.name_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearName() {
                        this.bitField0_ &= -33;
                        this.name_ = ItemCraft_Other.SendItemCraftList.Material.MaterialModel.getDefaultInstance().getName();
                        onChanged();
                        return this;
                    }

                    public boolean hasGfxId() {
                        return (this.bitField0_ & 0x40) == 64;
                    }

                    public int getGfxId() {
                        return this.gfxId_;
                    }

                    public Builder setGfxId(int value) {
                        this.bitField0_ |= 64;
                        this.gfxId_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearGfxId() {
                        this.bitField0_ &= -65;
                        this.gfxId_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUnknow1() {
                        return (this.bitField0_ & 0x80) == 128;
                    }

                    public int getUnknow1() {
                        return this.unknow1_;
                    }

                    public Builder setUnknow1(int value) {
                        this.bitField0_ |= 128;
                        this.unknow1_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUnknow1() {
                        this.bitField0_ &= -129;
                        this.unknow1_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUnknow2() {
                        return (this.bitField0_ & 0x100) == 256;
                    }

                    public int getUnknow2() {
                        return this.unknow2_;
                    }

                    public Builder setUnknow2(int value) {
                        this.bitField0_ |= 256;
                        this.unknow2_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUnknow2() {
                        this.bitField0_ &= -257;
                        this.unknow2_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUnknow3() {
                        return (this.bitField0_ & 0x200) == 512;
                    }

                    public int getUnknow3() {
                        return this.unknow3_;
                    }

                    public Builder setUnknow3(int value) {
                        this.bitField0_ |= 512;
                        this.unknow3_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUnknow3() {
                        this.bitField0_ &= -513;
                        this.unknow3_ = 0;
                        onChanged();
                        return this;
                    }

                    public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                        return (Builder) super.setUnknownFields(unknownFields);
                    }

                    public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                        return (Builder) super.mergeUnknownFields(unknownFields);
                    }
                }
            }
        }

        public static final class PolyModel extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.PolyModelOrBuilder {
            public static final int PCOUNTCOUNT_FIELD_NUMBER = 1;
            public static final int POLYID_FIELD_NUMBER = 2;
            @Deprecated
            public static final Parser<PolyModel> PARSER = new AbstractParser<PolyModel>() {
                public ItemCraft_Other.SendItemCraftList.PolyModel parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ItemCraft_Other.SendItemCraftList.PolyModel(input, extensionRegistry);
                }
            };
            private static final long serialVersionUID = 0L;
            private static final PolyModel DEFAULT_INSTANCE = new PolyModel();
            private int bitField0_;
            private int pcountcount_;
            private List<Integer> polyId_;
            private byte memoizedIsInitialized = -1;

            private PolyModel(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private PolyModel() {
                this.pcountcount_ = 0;
                this.polyId_ = Collections.emptyList();
            }

            private PolyModel(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this();
                int mutable_bitField0_ = 0;
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
                                this.pcountcount_ = input.readInt32();
                                break;
                            case 16:
                                if ((mutable_bitField0_ & 0x2) != 2) {
                                    this.polyId_ = new ArrayList<Integer>();
                                    mutable_bitField0_ |= 2;
                                }
                                this.polyId_.add(Integer.valueOf(input.readInt32()));
                                break;
                            case 18:
                                int length = input.readRawVarint32();
                                int limit = input.pushLimit(length);
                                if (((mutable_bitField0_ & 0x2) != 2) && (input.getBytesUntilLimit() > 0)) {
                                    this.polyId_ = new ArrayList<Integer>();
                                    mutable_bitField0_ |= 2;
                                }
                                while (input.getBytesUntilLimit() > 0) {
                                    this.polyId_.add(Integer.valueOf(input.readInt32()));
                                }
                                input.popLimit(limit);
                                break;
                        }
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e) {
                    throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
                } finally {
                    if ((mutable_bitField0_ & 0x2) == 2) {
                        this.polyId_ = Collections.unmodifiableList(this.polyId_);
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraftList_PolyModel_descriptor;
            }

            public static PolyModel parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return (PolyModel) PARSER.parseFrom(data);
            }

            public static PolyModel parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (PolyModel) PARSER.parseFrom(data, extensionRegistry);
            }

            public static PolyModel parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return (PolyModel) PARSER.parseFrom(data);
            }

            public static PolyModel parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (PolyModel) PARSER.parseFrom(data, extensionRegistry);
            }

            public static PolyModel parseFrom(InputStream input) throws IOException {
                return (PolyModel) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static PolyModel parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (PolyModel) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static PolyModel parseDelimitedFrom(InputStream input) throws IOException {
                return (PolyModel) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static PolyModel parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (PolyModel) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static PolyModel parseFrom(CodedInputStream input) throws IOException {
                return (PolyModel) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static PolyModel parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (PolyModel) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(PolyModel prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            public static PolyModel getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<PolyModel> parser() {
                return PARSER;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraftList_PolyModel_fieldAccessorTable.ensureFieldAccessorsInitialized(PolyModel.class, Builder.class);
            }

            public boolean hasPcountcount() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getPcountcount() {
                return this.pcountcount_;
            }

            public List<Integer> getPolyIdList() {
                return this.polyId_;
            }

            public int getPolyIdCount() {
                return this.polyId_.size();
            }

            public int getPolyId(int index) {
                return ((Integer) this.polyId_.get(index)).intValue();
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                if (!hasPcountcount()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 0x1) == 1) {
                    output.writeInt32(1, this.pcountcount_);
                }
                for (int i = 0; i < this.polyId_.size(); i++) {
                    output.writeInt32(2, ((Integer) this.polyId_.get(i)).intValue());
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
                    size = size + CodedOutputStream.computeInt32Size(1, this.pcountcount_);
                }
                int dataSize = 0;
                for (int i = 0; i < this.polyId_.size(); i++) {
                    dataSize = dataSize + CodedOutputStream.computeInt32SizeNoTag(((Integer) this.polyId_.get(i)).intValue());
                }
                size += dataSize;
                size += 1 * getPolyIdList().size();
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof PolyModel)) {
                    return super.equals(obj);
                }
                PolyModel other = (PolyModel) obj;
                boolean result = true;
                result = (result) && (hasPcountcount() == other.hasPcountcount());
                if (hasPcountcount()) {
                    result = (result) && (getPcountcount() == other.getPcountcount());
                }
                result = (result) && (getPolyIdList().equals(other.getPolyIdList()));
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
                if (hasPcountcount()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + getPcountcount();
                }
                if (getPolyIdCount() > 0) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + getPolyIdList().hashCode();
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

            public Parser<PolyModel> getParserForType() {
                return PARSER;
            }

            public PolyModel getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.PolyModelOrBuilder {
                private int bitField0_;
                private int pcountcount_;
                private List<Integer> polyId_ = Collections.emptyList();

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_PolyModel_descriptor;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_PolyModel_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.PolyModel.class, Builder.class);
                }

                private void maybeForceBuilderInitialization() {
                }

                public Builder clear() {
                    super.clear();
                    this.pcountcount_ = 0;
                    this.bitField0_ &= -2;
                    this.polyId_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                    return this;
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return ItemCraft_Other.SendItemCraftList_PolyModel_descriptor;
                }

                public ItemCraft_Other.SendItemCraftList.PolyModel getDefaultInstanceForType() {
                    return ItemCraft_Other.SendItemCraftList.PolyModel.getDefaultInstance();
                }

                public ItemCraft_Other.SendItemCraftList.PolyModel build() {
                    ItemCraft_Other.SendItemCraftList.PolyModel result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public ItemCraft_Other.SendItemCraftList.PolyModel buildPartial() {
                    ItemCraft_Other.SendItemCraftList.PolyModel result = new ItemCraft_Other.SendItemCraftList.PolyModel(this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 1) {
                        to_bitField0_ |= 1;
                    }
                    result.pcountcount_ = this.pcountcount_;
                    if ((this.bitField0_ & 0x2) == 2) {
                        this.polyId_ = Collections.unmodifiableList(this.polyId_);
                        this.bitField0_ &= -3;
                    }
                    result.polyId_ = this.polyId_;
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
                    if ((other instanceof ItemCraft_Other.SendItemCraftList.PolyModel)) {
                        return mergeFrom((ItemCraft_Other.SendItemCraftList.PolyModel) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.PolyModel other) {
                    if (other == ItemCraft_Other.SendItemCraftList.PolyModel.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasPcountcount()) {
                        setPcountcount(other.getPcountcount());
                    }
                    if (!other.polyId_.isEmpty()) {
                        if (this.polyId_.isEmpty()) {
                            this.polyId_ = other.polyId_;
                            this.bitField0_ &= -3;
                        } else {
                            ensurePolyIdIsMutable();
                            this.polyId_.addAll(other.polyId_);
                        }
                        onChanged();
                    }
                    mergeUnknownFields(other.unknownFields);
                    onChanged();
                    return this;
                }

                public final boolean isInitialized() {
                    if (!hasPcountcount()) {
                        return false;
                    }
                    return true;
                }

                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    ItemCraft_Other.SendItemCraftList.PolyModel parsedMessage = null;
                    try {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.PolyModel) ItemCraft_Other.SendItemCraftList.PolyModel.PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.PolyModel) e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    } finally {
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }

                public boolean hasPcountcount() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public int getPcountcount() {
                    return this.pcountcount_;
                }

                public Builder setPcountcount(int value) {
                    this.bitField0_ |= 1;
                    this.pcountcount_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearPcountcount() {
                    this.bitField0_ &= -2;
                    this.pcountcount_ = 0;
                    onChanged();
                    return this;
                }

                private void ensurePolyIdIsMutable() {
                    if ((this.bitField0_ & 0x2) != 2) {
                        this.polyId_ = new ArrayList<Integer>(this.polyId_);
                        this.bitField0_ |= 2;
                    }
                }

                public List<Integer> getPolyIdList() {
                    return Collections.unmodifiableList(this.polyId_);
                }

                public int getPolyIdCount() {
                    return this.polyId_.size();
                }

                public int getPolyId(int index) {
                    return ((Integer) this.polyId_.get(index)).intValue();
                }

                public Builder setPolyId(int index, int value) {
                    ensurePolyIdIsMutable();
                    this.polyId_.set(index, Integer.valueOf(value));
                    onChanged();
                    return this;
                }

                public Builder addPolyId(int value) {
                    ensurePolyIdIsMutable();
                    this.polyId_.add(Integer.valueOf(value));
                    onChanged();
                    return this;
                }

                public Builder addAllPolyId(Iterable<? extends Integer> values) {
                    ensurePolyIdIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.polyId_);
                    onChanged();
                    return this;
                }

                public Builder clearPolyId() {
                    this.polyId_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                    onChanged();
                    return this;
                }

                public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.setUnknownFields(unknownFields);
                }

                public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.mergeUnknownFields(unknownFields);
                }
            }
        }

        public static final class QuestModel extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.QuestModelOrBuilder {
            public static final int QUESTID_FIELD_NUMBER = 1;
            public static final int QUESTSTEPID_FIELD_NUMBER = 2;
            @Deprecated
            public static final Parser<QuestModel> PARSER = new AbstractParser<QuestModel>() {
                public ItemCraft_Other.SendItemCraftList.QuestModel parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ItemCraft_Other.SendItemCraftList.QuestModel(input, extensionRegistry);
                }
            };
            private static final long serialVersionUID = 0L;
            private static final QuestModel DEFAULT_INSTANCE = new QuestModel();
            private int bitField0_;
            private int questId_;
            private int questStepId_;
            private byte memoizedIsInitialized = -1;

            private QuestModel(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private QuestModel() {
                this.questId_ = 0;
                this.questStepId_ = 0;
            }

            private QuestModel(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.questId_ = input.readInt32();
                                break;
                            case 16:
                                this.bitField0_ |= 2;
                                this.questStepId_ = input.readInt32();
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

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraftList_QuestModel_descriptor;
            }

            public static QuestModel parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return (QuestModel) PARSER.parseFrom(data);
            }

            public static QuestModel parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (QuestModel) PARSER.parseFrom(data, extensionRegistry);
            }

            public static QuestModel parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return (QuestModel) PARSER.parseFrom(data);
            }

            public static QuestModel parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (QuestModel) PARSER.parseFrom(data, extensionRegistry);
            }

            public static QuestModel parseFrom(InputStream input) throws IOException {
                return (QuestModel) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static QuestModel parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (QuestModel) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static QuestModel parseDelimitedFrom(InputStream input) throws IOException {
                return (QuestModel) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static QuestModel parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (QuestModel) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static QuestModel parseFrom(CodedInputStream input) throws IOException {
                return (QuestModel) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static QuestModel parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (QuestModel) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(QuestModel prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            public static QuestModel getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<QuestModel> parser() {
                return PARSER;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraftList_QuestModel_fieldAccessorTable.ensureFieldAccessorsInitialized(QuestModel.class, Builder.class);
            }

            public boolean hasQuestId() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getQuestId() {
                return this.questId_;
            }

            public boolean hasQuestStepId() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public int getQuestStepId() {
                return this.questStepId_;
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                if (!hasQuestId()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasQuestStepId()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 0x1) == 1) {
                    output.writeInt32(1, this.questId_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    output.writeInt32(2, this.questStepId_);
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
                    size = size + CodedOutputStream.computeInt32Size(1, this.questId_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    size = size + CodedOutputStream.computeInt32Size(2, this.questStepId_);
                }
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof QuestModel)) {
                    return super.equals(obj);
                }
                QuestModel other = (QuestModel) obj;
                boolean result = true;
                result = (result) && (hasQuestId() == other.hasQuestId());
                if (hasQuestId()) {
                    result = (result) && (getQuestId() == other.getQuestId());
                }
                result = (result) && (hasQuestStepId() == other.hasQuestStepId());
                if (hasQuestStepId()) {
                    result = (result) && (getQuestStepId() == other.getQuestStepId());
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
                if (hasQuestId()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + getQuestId();
                }
                if (hasQuestStepId()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + getQuestStepId();
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

            public Parser<QuestModel> getParserForType() {
                return PARSER;
            }

            public QuestModel getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.QuestModelOrBuilder {
                private int bitField0_;
                private int questId_;
                private int questStepId_;

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_QuestModel_descriptor;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_QuestModel_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.QuestModel.class, Builder.class);
                }

                private void maybeForceBuilderInitialization() {
                }

                public Builder clear() {
                    super.clear();
                    this.questId_ = 0;
                    this.bitField0_ &= -2;
                    this.questStepId_ = 0;
                    this.bitField0_ &= -3;
                    return this;
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return ItemCraft_Other.SendItemCraftList_QuestModel_descriptor;
                }

                public ItemCraft_Other.SendItemCraftList.QuestModel getDefaultInstanceForType() {
                    return ItemCraft_Other.SendItemCraftList.QuestModel.getDefaultInstance();
                }

                public ItemCraft_Other.SendItemCraftList.QuestModel build() {
                    ItemCraft_Other.SendItemCraftList.QuestModel result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public ItemCraft_Other.SendItemCraftList.QuestModel buildPartial() {
                    ItemCraft_Other.SendItemCraftList.QuestModel result = new ItemCraft_Other.SendItemCraftList.QuestModel(this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 1) {
                        to_bitField0_ |= 1;
                    }
                    result.questId_ = this.questId_;
                    if ((from_bitField0_ & 0x2) == 2) {
                        to_bitField0_ |= 2;
                    }
                    result.questStepId_ = this.questStepId_;
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
                    if ((other instanceof ItemCraft_Other.SendItemCraftList.QuestModel)) {
                        return mergeFrom((ItemCraft_Other.SendItemCraftList.QuestModel) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.QuestModel other) {
                    if (other == ItemCraft_Other.SendItemCraftList.QuestModel.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasQuestId()) {
                        setQuestId(other.getQuestId());
                    }
                    if (other.hasQuestStepId()) {
                        setQuestStepId(other.getQuestStepId());
                    }
                    mergeUnknownFields(other.unknownFields);
                    onChanged();
                    return this;
                }

                public final boolean isInitialized() {
                    if (!hasQuestId()) {
                        return false;
                    }
                    if (!hasQuestStepId()) {
                        return false;
                    }
                    return true;
                }

                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    ItemCraft_Other.SendItemCraftList.QuestModel parsedMessage = null;
                    try {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.QuestModel) ItemCraft_Other.SendItemCraftList.QuestModel.PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.QuestModel) e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    } finally {
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }

                public boolean hasQuestId() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public int getQuestId() {
                    return this.questId_;
                }

                public Builder setQuestId(int value) {
                    this.bitField0_ |= 1;
                    this.questId_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearQuestId() {
                    this.bitField0_ &= -2;
                    this.questId_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasQuestStepId() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public int getQuestStepId() {
                    return this.questStepId_;
                }

                public Builder setQuestStepId(int value) {
                    this.bitField0_ |= 2;
                    this.questStepId_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearQuestStepId() {
                    this.bitField0_ &= -3;
                    this.questStepId_ = 0;
                    onChanged();
                    return this;
                }

                public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.setUnknownFields(unknownFields);
                }

                public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.mergeUnknownFields(unknownFields);
                }
            }
        }

        public static final class unQuest extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.unQuestOrBuilder {
            public static final int UNKNOWN1_FIELD_NUMBER = 1;
            public static final int UNKNOWN2_FIELD_NUMBER = 2;
            public static final int QUEST_FIELD_NUMBER = 3;
            @Deprecated
            public static final Parser<unQuest> PARSER = new AbstractParser<unQuest>() {
                public ItemCraft_Other.SendItemCraftList.unQuest parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ItemCraft_Other.SendItemCraftList.unQuest(input, extensionRegistry);
                }
            };
            private static final long serialVersionUID = 0L;
            private static final unQuest DEFAULT_INSTANCE = new unQuest();
            private int bitField0_;
            private int unknown1_;
            private int unknown2_;
            private ItemCraft_Other.SendItemCraftList.QuestModel quest_;
            private byte memoizedIsInitialized = -1;

            private unQuest(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private unQuest() {
                this.unknown1_ = 0;
                this.unknown2_ = 0;
            }

            private unQuest(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.unknown1_ = input.readInt32();
                                break;
                            case 16:
                                this.bitField0_ |= 2;
                                this.unknown2_ = input.readInt32();
                                break;
                            case 26:
                                ItemCraft_Other.SendItemCraftList.QuestModel.Builder subBuilder = null;
                                if ((this.bitField0_ & 0x4) == 4) {
                                    subBuilder = this.quest_.toBuilder();
                                }
                                this.quest_ = ((ItemCraft_Other.SendItemCraftList.QuestModel) input.readMessage(ItemCraft_Other.SendItemCraftList.QuestModel.PARSER, extensionRegistry));
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom(this.quest_);
                                    this.quest_ = subBuilder.buildPartial();
                                }
                                this.bitField0_ |= 4;
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

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraftList_unQuest_descriptor;
            }

            public static unQuest parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return (unQuest) PARSER.parseFrom(data);
            }

            public static unQuest parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (unQuest) PARSER.parseFrom(data, extensionRegistry);
            }

            public static unQuest parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return (unQuest) PARSER.parseFrom(data);
            }

            public static unQuest parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (unQuest) PARSER.parseFrom(data, extensionRegistry);
            }

            public static unQuest parseFrom(InputStream input) throws IOException {
                return (unQuest) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static unQuest parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (unQuest) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static unQuest parseDelimitedFrom(InputStream input) throws IOException {
                return (unQuest) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static unQuest parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (unQuest) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static unQuest parseFrom(CodedInputStream input) throws IOException {
                return (unQuest) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static unQuest parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (unQuest) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(unQuest prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            public static unQuest getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<unQuest> parser() {
                return PARSER;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraftList_unQuest_fieldAccessorTable.ensureFieldAccessorsInitialized(unQuest.class, Builder.class);
            }

            public boolean hasUnknown1() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getUnknown1() {
                return this.unknown1_;
            }

            public boolean hasUnknown2() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public int getUnknown2() {
                return this.unknown2_;
            }

            public boolean hasQuest() {
                return (this.bitField0_ & 0x4) == 4;
            }

            public ItemCraft_Other.SendItemCraftList.QuestModel getQuest() {
                return this.quest_ == null ? ItemCraft_Other.SendItemCraftList.QuestModel.getDefaultInstance() : this.quest_;
            }

            public ItemCraft_Other.SendItemCraftList.QuestModelOrBuilder getQuestOrBuilder() {
                return this.quest_ == null ? ItemCraft_Other.SendItemCraftList.QuestModel.getDefaultInstance() : this.quest_;
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                if (!hasUnknown1()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasUnknown2()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasQuest()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!getQuest().isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 0x1) == 1) {
                    output.writeInt32(1, this.unknown1_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    output.writeInt32(2, this.unknown2_);
                }
                if ((this.bitField0_ & 0x4) == 4) {
                    output.writeMessage(3, getQuest());
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
                    size = size + CodedOutputStream.computeInt32Size(1, this.unknown1_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    size = size + CodedOutputStream.computeInt32Size(2, this.unknown2_);
                }
                if ((this.bitField0_ & 0x4) == 4) {
                    size = size + CodedOutputStream.computeMessageSize(3, getQuest());
                }
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof unQuest)) {
                    return super.equals(obj);
                }
                unQuest other = (unQuest) obj;
                boolean result = true;
                result = (result) && (hasUnknown1() == other.hasUnknown1());
                if (hasUnknown1()) {
                    result = (result) && (getUnknown1() == other.getUnknown1());
                }
                result = (result) && (hasUnknown2() == other.hasUnknown2());
                if (hasUnknown2()) {
                    result = (result) && (getUnknown2() == other.getUnknown2());
                }
                result = (result) && (hasQuest() == other.hasQuest());
                if (hasQuest()) {
                    result = (result) && (getQuest().equals(other.getQuest()));
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
                if (hasUnknown1()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + getUnknown1();
                }
                if (hasUnknown2()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + getUnknown2();
                }
                if (hasQuest()) {
                    hash = 37 * hash + 3;
                    hash = 53 * hash + getQuest().hashCode();
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

            public Parser<unQuest> getParserForType() {
                return PARSER;
            }

            public unQuest getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.unQuestOrBuilder {
                private int bitField0_;
                private int unknown1_;
                private int unknown2_;
                private ItemCraft_Other.SendItemCraftList.QuestModel quest_ = null;
                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.QuestModel, ItemCraft_Other.SendItemCraftList.QuestModel.Builder, ItemCraft_Other.SendItemCraftList.QuestModelOrBuilder> questBuilder_;

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_unQuest_descriptor;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_unQuest_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.unQuest.class, Builder.class);
                }

                private void maybeForceBuilderInitialization() {
                    if (ItemCraft_Other.SendItemCraftList.unQuest.alwaysUseFieldBuilders) {
                        getQuestFieldBuilder();
                    }
                }

                public Builder clear() {
                    super.clear();
                    this.unknown1_ = 0;
                    this.bitField0_ &= -2;
                    this.unknown2_ = 0;
                    this.bitField0_ &= -3;
                    if (this.questBuilder_ == null) {
                        this.quest_ = null;
                    } else {
                        this.questBuilder_.clear();
                    }
                    this.bitField0_ &= -5;
                    return this;
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return ItemCraft_Other.SendItemCraftList_unQuest_descriptor;
                }

                public ItemCraft_Other.SendItemCraftList.unQuest getDefaultInstanceForType() {
                    return ItemCraft_Other.SendItemCraftList.unQuest.getDefaultInstance();
                }

                public ItemCraft_Other.SendItemCraftList.unQuest build() {
                    ItemCraft_Other.SendItemCraftList.unQuest result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public ItemCraft_Other.SendItemCraftList.unQuest buildPartial() {
                    ItemCraft_Other.SendItemCraftList.unQuest result = new ItemCraft_Other.SendItemCraftList.unQuest(this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 1) {
                        to_bitField0_ |= 1;
                    }
                    result.unknown1_ = this.unknown1_;
                    if ((from_bitField0_ & 0x2) == 2) {
                        to_bitField0_ |= 2;
                    }
                    result.unknown2_ = this.unknown2_;
                    if ((from_bitField0_ & 0x4) == 4) {
                        to_bitField0_ |= 4;
                    }
                    if (this.questBuilder_ == null) {
                        result.quest_ = this.quest_;
                    } else {
                        result.quest_ = ((ItemCraft_Other.SendItemCraftList.QuestModel) this.questBuilder_.build());
                    }
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
                    if ((other instanceof ItemCraft_Other.SendItemCraftList.unQuest)) {
                        return mergeFrom((ItemCraft_Other.SendItemCraftList.unQuest) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.unQuest other) {
                    if (other == ItemCraft_Other.SendItemCraftList.unQuest.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasUnknown1()) {
                        setUnknown1(other.getUnknown1());
                    }
                    if (other.hasUnknown2()) {
                        setUnknown2(other.getUnknown2());
                    }
                    if (other.hasQuest()) {
                        mergeQuest(other.getQuest());
                    }
                    mergeUnknownFields(other.unknownFields);
                    onChanged();
                    return this;
                }

                public final boolean isInitialized() {
                    if (!hasUnknown1()) {
                        return false;
                    }
                    if (!hasUnknown2()) {
                        return false;
                    }
                    if (!hasQuest()) {
                        return false;
                    }
                    if (!getQuest().isInitialized()) {
                        return false;
                    }
                    return true;
                }

                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    ItemCraft_Other.SendItemCraftList.unQuest parsedMessage = null;
                    try {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.unQuest) ItemCraft_Other.SendItemCraftList.unQuest.PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.unQuest) e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    } finally {
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }

                public boolean hasUnknown1() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public int getUnknown1() {
                    return this.unknown1_;
                }

                public Builder setUnknown1(int value) {
                    this.bitField0_ |= 1;
                    this.unknown1_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearUnknown1() {
                    this.bitField0_ &= -2;
                    this.unknown1_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasUnknown2() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public int getUnknown2() {
                    return this.unknown2_;
                }

                public Builder setUnknown2(int value) {
                    this.bitField0_ |= 2;
                    this.unknown2_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearUnknown2() {
                    this.bitField0_ &= -3;
                    this.unknown2_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasQuest() {
                    return (this.bitField0_ & 0x4) == 4;
                }

                public ItemCraft_Other.SendItemCraftList.QuestModel getQuest() {
                    if (this.questBuilder_ == null) {
                        return this.quest_ == null ? ItemCraft_Other.SendItemCraftList.QuestModel.getDefaultInstance() : this.quest_;
                    }
                    return (ItemCraft_Other.SendItemCraftList.QuestModel) this.questBuilder_.getMessage();
                }

                public Builder setQuest(ItemCraft_Other.SendItemCraftList.QuestModel value) {
                    if (this.questBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.quest_ = value;
                        onChanged();
                    } else {
                        this.questBuilder_.setMessage(value);
                    }
                    this.bitField0_ |= 4;
                    return this;
                }

                public Builder setQuest(ItemCraft_Other.SendItemCraftList.QuestModel.Builder builderForValue) {
                    if (this.questBuilder_ == null) {
                        this.quest_ = builderForValue.build();
                        onChanged();
                    } else {
                        this.questBuilder_.setMessage(builderForValue.build());
                    }
                    this.bitField0_ |= 4;
                    return this;
                }

                public Builder mergeQuest(ItemCraft_Other.SendItemCraftList.QuestModel value) {
                    if (this.questBuilder_ == null) {
                        if (((this.bitField0_ & 0x4) == 4) && (this.quest_ != null) && (this.quest_ != ItemCraft_Other.SendItemCraftList.QuestModel.getDefaultInstance())) {
                            this.quest_ = ItemCraft_Other.SendItemCraftList.QuestModel.newBuilder(this.quest_).mergeFrom(value).buildPartial();
                        } else {
                            this.quest_ = value;
                        }
                        onChanged();
                    } else {
                        this.questBuilder_.mergeFrom(value);
                    }
                    this.bitField0_ |= 4;
                    return this;
                }

                public Builder clearQuest() {
                    if (this.questBuilder_ == null) {
                        this.quest_ = null;
                        onChanged();
                    } else {
                        this.questBuilder_.clear();
                    }
                    this.bitField0_ &= -5;
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.QuestModel.Builder getQuestBuilder() {
                    this.bitField0_ |= 4;
                    onChanged();
                    return (ItemCraft_Other.SendItemCraftList.QuestModel.Builder) getQuestFieldBuilder().getBuilder();
                }

                public ItemCraft_Other.SendItemCraftList.QuestModelOrBuilder getQuestOrBuilder() {
                    if (this.questBuilder_ != null) {
                        return (ItemCraft_Other.SendItemCraftList.QuestModelOrBuilder) this.questBuilder_.getMessageOrBuilder();
                    }
                    return this.quest_ == null ? ItemCraft_Other.SendItemCraftList.QuestModel.getDefaultInstance() : this.quest_;
                }

                private SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.QuestModel, ItemCraft_Other.SendItemCraftList.QuestModel.Builder, ItemCraft_Other.SendItemCraftList.QuestModelOrBuilder> getQuestFieldBuilder() {
                    if (this.questBuilder_ == null) {
                        this.questBuilder_ = new SingleFieldBuilderV3<ItemCraft_Other.SendItemCraftList.QuestModel, ItemCraft_Other.SendItemCraftList.QuestModel.Builder, ItemCraft_Other.SendItemCraftList.QuestModelOrBuilder>(getQuest(), getParentForChildren(), isClean());
                        this.quest_ = null;
                    }
                    return this.questBuilder_;
                }

                public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.setUnknownFields(unknownFields);
                }

                public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.mergeUnknownFields(unknownFields);
                }
            }
        }

        public static final class unknownModel1 extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder {
            public static final int UNKNOW1_FIELD_NUMBER = 1;
            public static final int UNKNOW2_FIELD_NUMBER = 2;
            @Deprecated
            public static final Parser<unknownModel1> PARSER = new AbstractParser<unknownModel1>() {
                public ItemCraft_Other.SendItemCraftList.unknownModel1 parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ItemCraft_Other.SendItemCraftList.unknownModel1(input, extensionRegistry);
                }
            };
            private static final long serialVersionUID = 0L;
            private static final unknownModel1 DEFAULT_INSTANCE = new unknownModel1();
            private int bitField0_;
            private long unknow1_;
            private long unknow2_;
            private byte memoizedIsInitialized = -1;

            private unknownModel1(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private unknownModel1() {
                this.unknow1_ = 0L;
                this.unknow2_ = 0L;
            }

            private unknownModel1(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.unknow1_ = input.readInt64();
                                break;
                            case 16:
                                this.bitField0_ |= 2;
                                this.unknow2_ = input.readInt64();
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

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraftList_unknownModel1_descriptor;
            }

            public static unknownModel1 parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return (unknownModel1) PARSER.parseFrom(data);
            }

            public static unknownModel1 parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (unknownModel1) PARSER.parseFrom(data, extensionRegistry);
            }

            public static unknownModel1 parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return (unknownModel1) PARSER.parseFrom(data);
            }

            public static unknownModel1 parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (unknownModel1) PARSER.parseFrom(data, extensionRegistry);
            }

            public static unknownModel1 parseFrom(InputStream input) throws IOException {
                return (unknownModel1) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static unknownModel1 parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (unknownModel1) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static unknownModel1 parseDelimitedFrom(InputStream input) throws IOException {
                return (unknownModel1) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static unknownModel1 parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (unknownModel1) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static unknownModel1 parseFrom(CodedInputStream input) throws IOException {
                return (unknownModel1) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static unknownModel1 parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (unknownModel1) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(unknownModel1 prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            public static unknownModel1 getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<unknownModel1> parser() {
                return PARSER;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraftList_unknownModel1_fieldAccessorTable.ensureFieldAccessorsInitialized(unknownModel1.class, Builder.class);
            }

            public boolean hasUnknow1() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public long getUnknow1() {
                return this.unknow1_;
            }

            public boolean hasUnknow2() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public long getUnknow2() {
                return this.unknow2_;
            }

            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                if (!hasUnknow1()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!hasUnknow2()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 0x1) == 1) {
                    output.writeInt64(1, this.unknow1_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    output.writeInt64(2, this.unknow2_);
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
                    size = size + CodedOutputStream.computeInt64Size(1, this.unknow1_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    size = size + CodedOutputStream.computeInt64Size(2, this.unknow2_);
                }
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof unknownModel1)) {
                    return super.equals(obj);
                }
                unknownModel1 other = (unknownModel1) obj;
                boolean result = true;
                result = (result) && (hasUnknow1() == other.hasUnknow1());
                if (hasUnknow1()) {
                    result = (result) && (getUnknow1() == other.getUnknow1());
                }
                result = (result) && (hasUnknow2() == other.hasUnknow2());
                if (hasUnknow2()) {
                    result = (result) && (getUnknow2() == other.getUnknow2());
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
                if (hasUnknow1()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + Internal.hashLong(getUnknow1());
                }
                if (hasUnknow2()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + Internal.hashLong(getUnknow2());
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

            public Parser<unknownModel1> getParserForType() {
                return PARSER;
            }

            public unknownModel1 getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.unknownModel1OrBuilder {
                private int bitField0_;
                private long unknow1_;
                private long unknow2_;

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_unknownModel1_descriptor;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_unknownModel1_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.unknownModel1.class, Builder.class);
                }

                private void maybeForceBuilderInitialization() {
                }

                public Builder clear() {
                    super.clear();
                    this.unknow1_ = 0L;
                    this.bitField0_ &= -2;
                    this.unknow2_ = 0L;
                    this.bitField0_ &= -3;
                    return this;
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return ItemCraft_Other.SendItemCraftList_unknownModel1_descriptor;
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel1 getDefaultInstanceForType() {
                    return ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance();
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel1 build() {
                    ItemCraft_Other.SendItemCraftList.unknownModel1 result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel1 buildPartial() {
                    ItemCraft_Other.SendItemCraftList.unknownModel1 result = new ItemCraft_Other.SendItemCraftList.unknownModel1(this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 1) {
                        to_bitField0_ |= 1;
                    }
                    result.unknow1_ = this.unknow1_;
                    if ((from_bitField0_ & 0x2) == 2) {
                        to_bitField0_ |= 2;
                    }
                    result.unknow2_ = this.unknow2_;
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
                    if ((other instanceof ItemCraft_Other.SendItemCraftList.unknownModel1)) {
                        return mergeFrom((ItemCraft_Other.SendItemCraftList.unknownModel1) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.unknownModel1 other) {
                    if (other == ItemCraft_Other.SendItemCraftList.unknownModel1.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasUnknow1()) {
                        setUnknow1(other.getUnknow1());
                    }
                    if (other.hasUnknow2()) {
                        setUnknow2(other.getUnknow2());
                    }
                    mergeUnknownFields(other.unknownFields);
                    onChanged();
                    return this;
                }

                public final boolean isInitialized() {
                    if (!hasUnknow1()) {
                        return false;
                    }
                    if (!hasUnknow2()) {
                        return false;
                    }
                    return true;
                }

                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    ItemCraft_Other.SendItemCraftList.unknownModel1 parsedMessage = null;
                    try {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.unknownModel1) ItemCraft_Other.SendItemCraftList.unknownModel1.PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.unknownModel1) e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    } finally {
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }

                public boolean hasUnknow1() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public long getUnknow1() {
                    return this.unknow1_;
                }

                public Builder setUnknow1(long value) {
                    this.bitField0_ |= 1;
                    this.unknow1_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearUnknow1() {
                    this.bitField0_ &= -2;
                    this.unknow1_ = 0L;
                    onChanged();
                    return this;
                }

                public boolean hasUnknow2() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public long getUnknow2() {
                    return this.unknow2_;
                }

                public Builder setUnknow2(long value) {
                    this.bitField0_ |= 2;
                    this.unknow2_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearUnknow2() {
                    this.bitField0_ &= -3;
                    this.unknow2_ = 0L;
                    onChanged();
                    return this;
                }

                public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.setUnknownFields(unknownFields);
                }

                public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.mergeUnknownFields(unknownFields);
                }
            }
        }

        public static final class unknownModel2 extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.unknownModel2OrBuilder {
            public static final int TYPE_FIELD_NUMBER = 1;
            public static final int COUNT_FIELD_NUMBER = 2;
            public static final int UN_FIELD_NUMBER = 3;
            @Deprecated
            public static final Parser<unknownModel2> PARSER = new AbstractParser<unknownModel2>() {
                public ItemCraft_Other.SendItemCraftList.unknownModel2 parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ItemCraft_Other.SendItemCraftList.unknownModel2(input, extensionRegistry);
                }
            };
            private static final long serialVersionUID = 0L;
            private static final unknownModel2 DEFAULT_INSTANCE = new unknownModel2();
            private int bitField0_;
            private int type_;
            private int count_;
            private List<ItemInfo> un_;
            private byte memoizedIsInitialized = -1;

            private unknownModel2(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private unknownModel2() {
                this.type_ = 0;
                this.count_ = 0;
                this.un_ = Collections.emptyList();
            }

            private unknownModel2(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this();
                int mutable_bitField0_ = 0;
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
                                this.count_ = input.readInt32();
                                break;
                            case 26:
                                if ((mutable_bitField0_ & 0x4) != 4) {
                                    this.un_ = new ArrayList<ItemInfo>();
                                    mutable_bitField0_ |= 4;
                                }
                                this.un_.add((ItemInfo) input.readMessage(ItemInfo.PARSER, extensionRegistry));
                                break;
                        }
                    }
                } catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                } catch (IOException e) {
                    throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
                } finally {
                    if ((mutable_bitField0_ & 0x4) == 4) {
                        this.un_ = Collections.unmodifiableList(this.un_);
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return ItemCraft_Other.SendItemCraftList_unknownModel2_descriptor;
            }

            public static unknownModel2 parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return (unknownModel2) PARSER.parseFrom(data);
            }

            public static unknownModel2 parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (unknownModel2) PARSER.parseFrom(data, extensionRegistry);
            }

            public static unknownModel2 parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return (unknownModel2) PARSER.parseFrom(data);
            }

            public static unknownModel2 parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (unknownModel2) PARSER.parseFrom(data, extensionRegistry);
            }

            public static unknownModel2 parseFrom(InputStream input) throws IOException {
                return (unknownModel2) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static unknownModel2 parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (unknownModel2) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static unknownModel2 parseDelimitedFrom(InputStream input) throws IOException {
                return (unknownModel2) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static unknownModel2 parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (unknownModel2) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static unknownModel2 parseFrom(CodedInputStream input) throws IOException {
                return (unknownModel2) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static unknownModel2 parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (unknownModel2) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(unknownModel2 prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            public static unknownModel2 getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<unknownModel2> parser() {
                return PARSER;
            }

            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_Other.SendItemCraftList_unknownModel2_fieldAccessorTable.ensureFieldAccessorsInitialized(unknownModel2.class, Builder.class);
            }

            public boolean hasType() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getType() {
                return this.type_;
            }

            public boolean hasCount() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public int getCount() {
                return this.count_;
            }

            public List<ItemInfo> getUnList() {
                return this.un_;
            }

            public List<? extends ItemInfoOrBuilder> getUnOrBuilderList() {
                return this.un_;
            }

            public int getUnCount() {
                return this.un_.size();
            }

            public ItemInfo getUn(int index) {
                return (ItemInfo) this.un_.get(index);
            }

            public ItemInfoOrBuilder getUnOrBuilder(int index) {
                return (ItemInfoOrBuilder) this.un_.get(index);
            }

            public final boolean isInitialized() {
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
                if (!hasCount()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                for (int i = 0; i < getUnCount(); i++) {
                    if (!getUn(i).isInitialized()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 0x1) == 1) {
                    output.writeInt32(1, this.type_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    output.writeInt32(2, this.count_);
                }
                for (int i = 0; i < this.un_.size(); i++) {
                    output.writeMessage(3, (MessageLite) this.un_.get(i));
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
                    size = size + CodedOutputStream.computeInt32Size(2, this.count_);
                }
                for (int i = 0; i < this.un_.size(); i++) {
                    size = size + CodedOutputStream.computeMessageSize(3, (MessageLite) this.un_.get(i));
                }
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof unknownModel2)) {
                    return super.equals(obj);
                }
                unknownModel2 other = (unknownModel2) obj;
                boolean result = true;
                result = (result) && (hasType() == other.hasType());
                if (hasType()) {
                    result = (result) && (getType() == other.getType());
                }
                result = (result) && (hasCount() == other.hasCount());
                if (hasCount()) {
                    result = (result) && (getCount() == other.getCount());
                }
                result = (result) && (getUnList().equals(other.getUnList()));
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
                if (hasCount()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + getCount();
                }
                if (getUnCount() > 0) {
                    hash = 37 * hash + 3;
                    hash = 53 * hash + getUnList().hashCode();
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

            public Parser<unknownModel2> getParserForType() {
                return PARSER;
            }

            public unknownModel2 getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static abstract interface ItemInfoOrBuilder extends MessageOrBuilder {
                public abstract boolean hasDescid();

                public abstract int getDescid();

                public abstract boolean hasCount();

                public abstract int getCount();

                public abstract boolean hasUn();

                public abstract int getUn();
            }

            public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.unknownModel2OrBuilder {
                private int bitField0_;
                private int type_;
                private int count_;
                private List<ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo> un_ = Collections.emptyList();
                private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder> unBuilder_;

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_unknownModel2_descriptor;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_unknownModel2_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.unknownModel2.class, Builder.class);
                }

                private void maybeForceBuilderInitialization() {
                    if (ItemCraft_Other.SendItemCraftList.unknownModel2.alwaysUseFieldBuilders) {
                        getUnFieldBuilder();
                    }
                }

                public Builder clear() {
                    super.clear();
                    this.type_ = 0;
                    this.bitField0_ &= -2;
                    this.count_ = 0;
                    this.bitField0_ &= -3;
                    if (this.unBuilder_ == null) {
                        this.un_ = Collections.emptyList();
                        this.bitField0_ &= -5;
                    } else {
                        this.unBuilder_.clear();
                    }
                    return this;
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return ItemCraft_Other.SendItemCraftList_unknownModel2_descriptor;
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel2 getDefaultInstanceForType() {
                    return ItemCraft_Other.SendItemCraftList.unknownModel2.getDefaultInstance();
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel2 build() {
                    ItemCraft_Other.SendItemCraftList.unknownModel2 result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel2 buildPartial() {
                    ItemCraft_Other.SendItemCraftList.unknownModel2 result = new ItemCraft_Other.SendItemCraftList.unknownModel2(this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 1) {
                        to_bitField0_ |= 1;
                    }
                    result.type_ = this.type_;
                    if ((from_bitField0_ & 0x2) == 2) {
                        to_bitField0_ |= 2;
                    }
                    result.count_ = this.count_;
                    if (this.unBuilder_ == null) {
                        if ((this.bitField0_ & 0x4) == 4) {
                            this.un_ = Collections.unmodifiableList(this.un_);
                            this.bitField0_ &= -5;
                        }
                        result.un_ = this.un_;
                    } else {
                        result.un_ = this.unBuilder_.build();
                    }
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
                    if ((other instanceof ItemCraft_Other.SendItemCraftList.unknownModel2)) {
                        return mergeFrom((ItemCraft_Other.SendItemCraftList.unknownModel2) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.unknownModel2 other) {
                    if (other == ItemCraft_Other.SendItemCraftList.unknownModel2.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasType()) {
                        setType(other.getType());
                    }
                    if (other.hasCount()) {
                        setCount(other.getCount());
                    }
                    if (this.unBuilder_ == null) {
                        if (!other.un_.isEmpty()) {
                            if (this.un_.isEmpty()) {
                                this.un_ = other.un_;
                                this.bitField0_ &= -5;
                            } else {
                                ensureUnIsMutable();
                                this.un_.addAll(other.un_);
                            }
                            onChanged();
                        }
                    } else if (!other.un_.isEmpty()) {
                        if (this.unBuilder_.isEmpty()) {
                            this.unBuilder_.dispose();
                            this.unBuilder_ = null;
                            this.un_ = other.un_;
                            this.bitField0_ &= -5;
                            this.unBuilder_ = (ItemCraft_Other.SendItemCraftList.unknownModel2.alwaysUseFieldBuilders ? getUnFieldBuilder() : null);
                        } else {
                            this.unBuilder_.addAllMessages(other.un_);
                        }
                    }
                    mergeUnknownFields(other.unknownFields);
                    onChanged();
                    return this;
                }

                public final boolean isInitialized() {
                    if (!hasType()) {
                        return false;
                    }
                    if (!hasCount()) {
                        return false;
                    }
                    for (int i = 0; i < getUnCount(); i++) {
                        if (!getUn(i).isInitialized()) {
                            return false;
                        }
                    }
                    return true;
                }

                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    ItemCraft_Other.SendItemCraftList.unknownModel2 parsedMessage = null;
                    try {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.unknownModel2) ItemCraft_Other.SendItemCraftList.unknownModel2.PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ItemCraft_Other.SendItemCraftList.unknownModel2) e.getUnfinishedMessage();
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

                public boolean hasCount() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public int getCount() {
                    return this.count_;
                }

                public Builder setCount(int value) {
                    this.bitField0_ |= 2;
                    this.count_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearCount() {
                    this.bitField0_ &= -3;
                    this.count_ = 0;
                    onChanged();
                    return this;
                }

                private void ensureUnIsMutable() {
                    if ((this.bitField0_ & 0x4) != 4) {
                        this.un_ = new ArrayList<ItemInfo>(this.un_);
                        this.bitField0_ |= 4;
                    }
                }

                public List<ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo> getUnList() {
                    if (this.unBuilder_ == null) {
                        return Collections.unmodifiableList(this.un_);
                    }
                    return this.unBuilder_.getMessageList();
                }

                public int getUnCount() {
                    if (this.unBuilder_ == null) {
                        return this.un_.size();
                    }
                    return this.unBuilder_.getCount();
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo getUn(int index) {
                    if (this.unBuilder_ == null) {
                        return (ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo) this.un_.get(index);
                    }
                    return (ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo) this.unBuilder_.getMessage(index);
                }

                public Builder setUn(int index, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo value) {
                    if (this.unBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureUnIsMutable();
                        this.un_.set(index, value);
                        onChanged();
                    } else {
                        this.unBuilder_.setMessage(index, value);
                    }
                    return this;
                }

                public Builder setUn(int index, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder builderForValue) {
                    if (this.unBuilder_ == null) {
                        ensureUnIsMutable();
                        this.un_.set(index, builderForValue.build());
                        onChanged();
                    } else {
                        this.unBuilder_.setMessage(index, builderForValue.build());
                    }
                    return this;
                }

                public Builder addUn(ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo value) {
                    if (this.unBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureUnIsMutable();
                        this.un_.add(value);
                        onChanged();
                    } else {
                        this.unBuilder_.addMessage(value);
                    }
                    return this;
                }

                public Builder addUn(int index, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo value) {
                    if (this.unBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        ensureUnIsMutable();
                        this.un_.add(index, value);
                        onChanged();
                    } else {
                        this.unBuilder_.addMessage(index, value);
                    }
                    return this;
                }

                public Builder addUn(ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder builderForValue) {
                    if (this.unBuilder_ == null) {
                        ensureUnIsMutable();
                        this.un_.add(builderForValue.build());
                        onChanged();
                    } else {
                        this.unBuilder_.addMessage(builderForValue.build());
                    }
                    return this;
                }

                public Builder addUn(int index, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder builderForValue) {
                    if (this.unBuilder_ == null) {
                        ensureUnIsMutable();
                        this.un_.add(index, builderForValue.build());
                        onChanged();
                    } else {
                        this.unBuilder_.addMessage(index, builderForValue.build());
                    }
                    return this;
                }

                public Builder addAllUn(Iterable<? extends ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo> values) {
                    if (this.unBuilder_ == null) {
                        ensureUnIsMutable();
                        AbstractMessageLite.Builder.addAll(values, this.un_);
                        onChanged();
                    } else {
                        this.unBuilder_.addAllMessages(values);
                    }
                    return this;
                }

                public Builder clearUn() {
                    if (this.unBuilder_ == null) {
                        this.un_ = Collections.emptyList();
                        this.bitField0_ &= -5;
                        onChanged();
                    } else {
                        this.unBuilder_.clear();
                    }
                    return this;
                }

                public Builder removeUn(int index) {
                    if (this.unBuilder_ == null) {
                        ensureUnIsMutable();
                        this.un_.remove(index);
                        onChanged();
                    } else {
                        this.unBuilder_.remove(index);
                    }
                    return this;
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder getUnBuilder(int index) {
                    return (ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder) getUnFieldBuilder().getBuilder(index);
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder getUnOrBuilder(int index) {
                    if (this.unBuilder_ == null) {
                        return (ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder) this.un_.get(index);
                    }
                    return (ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder) this.unBuilder_.getMessageOrBuilder(index);
                }

                public List<? extends ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder> getUnOrBuilderList() {
                    if (this.unBuilder_ != null) {
                        return this.unBuilder_.getMessageOrBuilderList();
                    }
                    return Collections.unmodifiableList(this.un_);
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder addUnBuilder() {
                    return (ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder) getUnFieldBuilder().addBuilder(ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.getDefaultInstance());
                }

                public ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder addUnBuilder(int index) {
                    return (ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder) getUnFieldBuilder().addBuilder(index, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.getDefaultInstance());
                }

                public List<ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder> getUnBuilderList() {
                    return getUnFieldBuilder().getBuilderList();
                }

                private RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder> getUnFieldBuilder() {
                    if (this.unBuilder_ == null) {
                        this.unBuilder_ = new RepeatedFieldBuilderV3<ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.Builder, ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder>(this.un_, (this.bitField0_ & 0x4) == 4, getParentForChildren(), isClean());
                        this.un_ = null;
                    }
                    return this.unBuilder_;
                }

                public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.setUnknownFields(unknownFields);
                }

                public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder) super.mergeUnknownFields(unknownFields);
                }
            }

            public static final class ItemInfo extends GeneratedMessageV3 implements ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder {
                public static final int DESCID_FIELD_NUMBER = 1;
                public static final int COUNT_FIELD_NUMBER = 2;
                public static final int UN_FIELD_NUMBER = 3;
                @Deprecated
                public static final Parser<ItemInfo> PARSER = new AbstractParser<ItemInfo>() {
                    public ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                        return new ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo(input, extensionRegistry);
                    }
                };
                private static final long serialVersionUID = 0L;
                private static final ItemInfo DEFAULT_INSTANCE = new ItemInfo();
                private int bitField0_;
                private int descid_;
                private int count_;
                private int un_;
                private byte memoizedIsInitialized = -1;

                private ItemInfo(GeneratedMessageV3.Builder<?> builder) {
                    super(builder);
                }

                private ItemInfo() {
                    this.descid_ = 0;
                    this.count_ = 0;
                    this.un_ = 0;
                }

                private ItemInfo(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                    this.descid_ = input.readInt32();
                                    break;
                                case 16:
                                    this.bitField0_ |= 2;
                                    this.count_ = input.readInt32();
                                    break;
                                case 24:
                                    this.bitField0_ |= 4;
                                    this.un_ = input.readInt32();
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

                public static final Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_Other.SendItemCraftList_unknownModel2_ItemInfo_descriptor;
                }

                public static ItemInfo parseFrom(ByteString data) throws InvalidProtocolBufferException {
                    return (ItemInfo) PARSER.parseFrom(data);
                }

                public static ItemInfo parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return (ItemInfo) PARSER.parseFrom(data, extensionRegistry);
                }

                public static ItemInfo parseFrom(byte[] data) throws InvalidProtocolBufferException {
                    return (ItemInfo) PARSER.parseFrom(data);
                }

                public static ItemInfo parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return (ItemInfo) PARSER.parseFrom(data, extensionRegistry);
                }

                public static ItemInfo parseFrom(InputStream input) throws IOException {
                    return (ItemInfo) GeneratedMessageV3.parseWithIOException(PARSER, input);
                }

                public static ItemInfo parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (ItemInfo) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
                }

                public static ItemInfo parseDelimitedFrom(InputStream input) throws IOException {
                    return (ItemInfo) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
                }

                public static ItemInfo parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (ItemInfo) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
                }

                public static ItemInfo parseFrom(CodedInputStream input) throws IOException {
                    return (ItemInfo) GeneratedMessageV3.parseWithIOException(PARSER, input);
                }

                public static ItemInfo parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    return (ItemInfo) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
                }

                public static Builder newBuilder() {
                    return DEFAULT_INSTANCE.toBuilder();
                }

                public static Builder newBuilder(ItemInfo prototype) {
                    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
                }

                public static ItemInfo getDefaultInstance() {
                    return DEFAULT_INSTANCE;
                }

                public static Parser<ItemInfo> parser() {
                    return PARSER;
                }

                public final UnknownFieldSet getUnknownFields() {
                    return this.unknownFields;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_Other.SendItemCraftList_unknownModel2_ItemInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemInfo.class, Builder.class);
                }

                public boolean hasDescid() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public int getDescid() {
                    return this.descid_;
                }

                public boolean hasCount() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public int getCount() {
                    return this.count_;
                }

                public boolean hasUn() {
                    return (this.bitField0_ & 0x4) == 4;
                }

                public int getUn() {
                    return this.un_;
                }

                public final boolean isInitialized() {
                    byte isInitialized = this.memoizedIsInitialized;
                    if (isInitialized == 1) {
                        return true;
                    }
                    if (isInitialized == 0) {
                        return false;
                    }
                    if (!hasDescid()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasCount()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    if (!hasUn()) {
                        this.memoizedIsInitialized = 0;
                        return false;
                    }
                    this.memoizedIsInitialized = 1;
                    return true;
                }

                public void writeTo(CodedOutputStream output) throws IOException {
                    if ((this.bitField0_ & 0x1) == 1) {
                        output.writeInt32(1, this.descid_);
                    }
                    if ((this.bitField0_ & 0x2) == 2) {
                        output.writeInt32(2, this.count_);
                    }
                    if ((this.bitField0_ & 0x4) == 4) {
                        output.writeInt32(3, this.un_);
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
                        size = size + CodedOutputStream.computeInt32Size(1, this.descid_);
                    }
                    if ((this.bitField0_ & 0x2) == 2) {
                        size = size + CodedOutputStream.computeInt32Size(2, this.count_);
                    }
                    if ((this.bitField0_ & 0x4) == 4) {
                        size = size + CodedOutputStream.computeInt32Size(3, this.un_);
                    }
                    size += this.unknownFields.getSerializedSize();
                    this.memoizedSize = size;
                    return size;
                }

                public boolean equals(Object obj) {
                    if (obj == this) {
                        return true;
                    }
                    if (!(obj instanceof ItemInfo)) {
                        return super.equals(obj);
                    }
                    ItemInfo other = (ItemInfo) obj;
                    boolean result = true;
                    result = (result) && (hasDescid() == other.hasDescid());
                    if (hasDescid()) {
                        result = (result) && (getDescid() == other.getDescid());
                    }
                    result = (result) && (hasCount() == other.hasCount());
                    if (hasCount()) {
                        result = (result) && (getCount() == other.getCount());
                    }
                    result = (result) && (hasUn() == other.hasUn());
                    if (hasUn()) {
                        result = (result) && (getUn() == other.getUn());
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
                    if (hasDescid()) {
                        hash = 37 * hash + 1;
                        hash = 53 * hash + getDescid();
                    }
                    if (hasCount()) {
                        hash = 37 * hash + 2;
                        hash = 53 * hash + getCount();
                    }
                    if (hasUn()) {
                        hash = 37 * hash + 3;
                        hash = 53 * hash + getUn();
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

                public Parser<ItemInfo> getParserForType() {
                    return PARSER;
                }

                public ItemInfo getDefaultInstanceForType() {
                    return DEFAULT_INSTANCE;
                }

                public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfoOrBuilder {
                    private int bitField0_;
                    private int descid_;
                    private int count_;
                    private int un_;

                    private Builder() {
                        maybeForceBuilderInitialization();
                    }

                    private Builder(GeneratedMessageV3.BuilderParent parent) {
                        super(parent);
                        maybeForceBuilderInitialization();
                    }

                    public static final Descriptors.Descriptor getDescriptor() {
                        return ItemCraft_Other.SendItemCraftList_unknownModel2_ItemInfo_descriptor;
                    }

                    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                        return ItemCraft_Other.SendItemCraftList_unknownModel2_ItemInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.class, Builder.class);
                    }

                    private void maybeForceBuilderInitialization() {
                    }

                    public Builder clear() {
                        super.clear();
                        this.descid_ = 0;
                        this.bitField0_ &= -2;
                        this.count_ = 0;
                        this.bitField0_ &= -3;
                        this.un_ = 0;
                        this.bitField0_ &= -5;
                        return this;
                    }

                    public Descriptors.Descriptor getDescriptorForType() {
                        return ItemCraft_Other.SendItemCraftList_unknownModel2_ItemInfo_descriptor;
                    }

                    public ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo getDefaultInstanceForType() {
                        return ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.getDefaultInstance();
                    }

                    public ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo build() {
                        ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo result = buildPartial();
                        if (!result.isInitialized()) {
                            throw newUninitializedMessageException(result);
                        }
                        return result;
                    }

                    public ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo buildPartial() {
                        ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo result = new ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo(this);
                        int from_bitField0_ = this.bitField0_;
                        int to_bitField0_ = 0;
                        if ((from_bitField0_ & 0x1) == 1) {
                            to_bitField0_ |= 1;
                        }
                        result.descid_ = this.descid_;
                        if ((from_bitField0_ & 0x2) == 2) {
                            to_bitField0_ |= 2;
                        }
                        result.count_ = this.count_;
                        if ((from_bitField0_ & 0x4) == 4) {
                            to_bitField0_ |= 4;
                        }
                        result.un_ = this.un_;
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
                        if ((other instanceof ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo)) {
                            return mergeFrom((ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo) other);
                        }
                        super.mergeFrom(other);
                        return this;
                    }

                    public Builder mergeFrom(ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo other) {
                        if (other == ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.getDefaultInstance()) {
                            return this;
                        }
                        if (other.hasDescid()) {
                            setDescid(other.getDescid());
                        }
                        if (other.hasCount()) {
                            setCount(other.getCount());
                        }
                        if (other.hasUn()) {
                            setUn(other.getUn());
                        }
                        mergeUnknownFields(other.unknownFields);
                        onChanged();
                        return this;
                    }

                    public final boolean isInitialized() {
                        if (!hasDescid()) {
                            return false;
                        }
                        if (!hasCount()) {
                            return false;
                        }
                        if (!hasUn()) {
                            return false;
                        }
                        return true;
                    }

                    public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                        ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo parsedMessage = null;
                        try {
                            parsedMessage = (ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo) ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo.PARSER.parsePartialFrom(input, extensionRegistry);
                        } catch (InvalidProtocolBufferException e) {
                            parsedMessage = (ItemCraft_Other.SendItemCraftList.unknownModel2.ItemInfo) e.getUnfinishedMessage();
                            throw e.unwrapIOException();
                        } finally {
                            if (parsedMessage != null) {
                                mergeFrom(parsedMessage);
                            }
                        }
                        return this;
                    }

                    public boolean hasDescid() {
                        return (this.bitField0_ & 0x1) == 1;
                    }

                    public int getDescid() {
                        return this.descid_;
                    }

                    public Builder setDescid(int value) {
                        this.bitField0_ |= 1;
                        this.descid_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearDescid() {
                        this.bitField0_ &= -2;
                        this.descid_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasCount() {
                        return (this.bitField0_ & 0x2) == 2;
                    }

                    public int getCount() {
                        return this.count_;
                    }

                    public Builder setCount(int value) {
                        this.bitField0_ |= 2;
                        this.count_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearCount() {
                        this.bitField0_ &= -3;
                        this.count_ = 0;
                        onChanged();
                        return this;
                    }

                    public boolean hasUn() {
                        return (this.bitField0_ & 0x4) == 4;
                    }

                    public int getUn() {
                        return this.un_;
                    }

                    public Builder setUn(int value) {
                        this.bitField0_ |= 4;
                        this.un_ = value;
                        onChanged();
                        return this;
                    }

                    public Builder clearUn() {
                        this.bitField0_ &= -5;
                        this.un_ = 0;
                        onChanged();
                        return this;
                    }

                    public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                        return (Builder) super.setUnknownFields(unknownFields);
                    }

                    public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                        return (Builder) super.mergeUnknownFields(unknownFields);
                    }
                }
            }
        }
    }
}
