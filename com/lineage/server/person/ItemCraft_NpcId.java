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
public final class ItemCraft_NpcId {
    private static Descriptors.FileDescriptor descriptor;
    private static final Descriptors.Descriptor sendCraftNpcCraftList_descriptor = getDescriptor().getMessageTypes().get(23);
    private static final GeneratedMessageV3.FieldAccessorTable sendCraftNpcCraftList_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(sendCraftNpcCraftList_descriptor, new String[]{"Unknown1", "List", "Unknown2"});
    private static final Descriptors.Descriptor sendCraftNpcCraftList_List_descriptor = sendCraftNpcCraftList_descriptor.getNestedTypes().get(0);
    private static final GeneratedMessageV3.FieldAccessorTable sendCraftNpcCraftList_List_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(sendCraftNpcCraftList_List_descriptor, new String[]{"ActionId", "Unknown1", "Unknown2"});
    private static final Descriptors.Descriptor readCraftNpcId_descriptor = getDescriptor().getMessageTypes().get(24);
    private static final GeneratedMessageV3.FieldAccessorTable readCraftNpcId_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(readCraftNpcId_descriptor, new String[]{"Npcid"});

    static {
        String[] descriptorData = {"\n\fPerson.proto\022\021l1j.server.server\"¢\001\n\021sendEquipmentInfo\022\f\n\004type\030\001 \002(\005\022<\n\004item\030\002 \003(\0132..l1j.server.server.sendEquipmentInfo.Equipment\022\n\n\002un\030\003 \002(\005\022\013\n\003un1\030\004 \002(\005\032(\n\tEquipment\022\f\n\004eqid\030\001 \002(\005\022\r\n\005objid\030\002 \003(\005\"\002\n\023sendSoulTowerRanked\022K\n\007newRank\030\001 \003(\0132:.l1j.server.server.sendSoulTowerRanked.soulTowerRankedInfo\022K\n\007oldRank\030\002 \003(\0132:.l1j.server.server.sendSoulTowerRanked.soulTowerRankedInfo\032n\n\023soulTowerRankedInfo\022", "\f\n\004name\030\001 \002(\f\022\020\n\busertype\030\002 \002(\005\022\017\n\007maptime\030\003 \002(\005\022\016\n\006gotime\030\004 \002(\005\022\026\n\016soulTowerClass\030\005 \002(\005\"³\002\n\fsendTaskInfo\022\f\n\004type\030\001 \002(\005\022\r\n\005type1\030\002 \002(\005\0226\n\004info\030\003 \003(\0132(.l1j.server.server.sendTaskInfo.TaskInfo\032Í\001\n\bTaskInfo\022\016\n\006taskid\030\001 \002(\005\022\013\n\003url\030\003 \002(\f\022\r\n\005title\030\004 \001(\f\022\021\n\tstarttime\030\005 \001(\005\022\017\n\007endtime\030\006 \001(\005\022?\n\004boss\030\007 \001(\01321.l1j.server.server.sendTaskInfo.TaskInfo.bossInfo\0320\n\bbossInfo\022\020\n\bstringid\030\001 \002(\f\022\022\n\nteleportId\030\002 \002(\005\"í", "\001\n\022sendClanMemberInfo\022\f\n\004type\030\001 \002(\005\022D\n\nmemberInfo\030\002 \003(\01320.l1j.server.server.sendClanMemberInfo.MemberInfo\032\001\n\nMemberInfo\022\r\n\005objid\030\001 \002(\005\022\020\n\bclanName\030\002 \002(\f\022\020\n\bobjectId\030\003 \002(\005\022\020\n\bcharName\030\004 \002(\f\022\f\n\004note\030\005 \002(\f\022\020\n\bisonline\030\006 \002(\005\022\017\n\007jobtype\030\007 \002(\005\"\001\n\023sendMonsterKillInfo\022\f\n\004type\030\001 \002(\005\022\017\n\007unknow2\030\002 \002(\005\022=\n\004info\030\003 \003(\0132/.l1j.server.server.sendMonsterKillInfo.KillInfo\032&\n\bKillInfo\022\013\n\003num\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\"¶\001\n", "\017sendMonsterList\022\017\n\007unknow1\030\001 \002(\005\022\017\n\007unknow2\030\002 \002(\005\022<\n\004info\030\003 \003(\0132..l1j.server.server.sendMonsterList.MonsterInfo\032C\n\013MonsterInfo\022\020\n\bquestNum\030\001 \002(\005\022\023\n\013monsterTime\030\002 \002(\005\022\r\n\005value\030\003 \002(\005\"2\n\fsendClanInfo\022\020\n\bclanName\030\001 \002(\f\022\020\n\bclanRank\030\002 \002(\005\"¨\002\n\023sendTelePortMapInfo\022\020\n\bnpcObjId\030\001 \002(\005\022D\n\004info\030\002 \003(\01326.l1j.server.server.sendTelePortMapInfo.TelePortMapInfo\032¸\001\n\017TelePortMapInfo\022\024\n\ftelePortName\030\001 \002(\f\022O\n\007mapInfo\030\002", " \001(\0132>.l1j.server.server.sendTelePortMapInfo.TelePortMapInfo.MapInfo\032>\n\007MapInfo\022\017\n\007mapType\030\001 \002(\005\022\021\n\tteleMoney\030\002 \002(\005\022\017\n\007unknown\030\003 \002(\005\"\001\n\rsendBuddyList\022\f\n\004type\030\001 \002(\005\022=\n\tbuddyList\030\002 \003(\0132*.l1j.server.server.sendBuddyList.buddyInfo\032<\n\tbuddyInfo\022\f\n\004name\030\001 \002(\f\022\020\n\bisOnline\030\002 \002(\005\022\017\n\007unknown\030\003 \002(\f\";\n\fsendFishTime\022\f\n\004type\030\001 \002(\005\022\f\n\004time\030\002 \001(\005\022\017\n\007isQuick\030\003 \001(\005\"\002\n\013sendTAMPage\0226\n\004user\030\001 \003(\0132(.l1j.server.server", ".sendTAMPage.UserModel\022\020\n\bunknown1\030\002 \002(\005\022\020\n\bunknown2\030\003 \002(\005\022\020\n\bunknown3\030\004 \002(\005\032\001\n\tUserModel\022\020\n\bserverNo\030\001 \002(\005\022\r\n\005objid\030\002 \002(\005\022\f\n\004time\030\003 \002(\005\022\017\n\007tamwait\030\004 \002(\005\022\f\n\004name\030\005 \002(\f\022\r\n\005level\030\006 \002(\005\022\021\n\ttypeClass\030\007 \002(\005\022\013\n\003sex\030\b \002(\005\"\007\n\016sendAttrReward\0227\n\003str\030\001 \002(\0132*.l1j.server.server.sendAttrReward.strModel\0229\n\005intel\030\002 \002(\0132*.l1j.server.server.sendAttrReward.intModel\0227\n\003wis\030\003 \002(\0132*.l1j.server.server.sendAttrReward.w", "isModel\0227\n\003dex\030\004 \002(\0132*.l1j.server.server.sendAttrReward.dexModel\0227\n\003con\030\005 \002(\0132*.l1j.server.server.sendAttrReward.conModel\0227\n\003cha\030\006 \002(\0132*.l1j.server.server.sendAttrReward.chaModel\032R\n\bstrModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003dmg\030\002 \001(\005\022\013\n\003hit\030\003 \001(\005\022\032\n\022criticalStrikeRate\030\004 \001(\005\032a\n\bintModel\022\020\n\battValue\030\001 \001(\005\022\020\n\bmagicDmg\030\002 \001(\005\022\020\n\bmagicHit\030\003 \001(\005\022\037\n\027magicCriticalStrikeRate\030\004 \001(\005\032\001\n\bwisModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003mpr\030", "\002 \001(\005\022\023\n\013MPCureBonus\030\003 \001(\005\022\n\n\002mr\030\004 \001(\005\022\024\n\flvUpAddMinMp\030\005 \001(\005\022\024\n\flvUpAddMaxMp\030\006 \001(\005\022\r\n\005mpMax\030\007 \001(\005\032[\n\bdexModel\022\020\n\battValue\030\001 \001(\005\022\016\n\006bowDmg\030\002 \001(\005\022\016\n\006bowHit\030\003 \001(\005\022\035\n\025bowcriticalStrikeRate\030\004 \001(\005\032s\n\bconModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003hpr\030\002 \001(\005\022\023\n\013HPCureBonus\030\003 \001(\005\022\016\n\006weight\030\004 \001(\005\022\024\n\flvUpAddMaxHp\030\005 \001(\005\022\r\n\005hpMax\030\006 \001(\005\032\034\n\bchaModel\022\020\n\battValue\030\001 \001(\005\"b\n\020sendUserBaseAttr\022\013\n\003str\030\001 \002(\005\022\r\n\005intel\030\002 \002(\005\022\013\n\003wis\030\003 \002(\005\022\013", "\n\003dex\030\004 \002(\005\022\013\n\003con\030\005 \002(\005\022\013\n\003cha\030\006 \002(\005\"A\n\013readBanInfo\022\023\n\013excludeType\030\001 \002(\005\022\017\n\007subType\030\002 \002(\005\022\f\n\004name\030\003 \003(\f\"\026\n\007readSha\022\013\n\003sha\030\001 \002(\f\"D\n\020readCustomPacket\022\016\n\006custom\030\001 \001(\005\022\017\n\007custom1\030\002 \001(\005\022\017\n\007custom2\030\003 \001(\005\"Y\n\021readCustomPacket1\022\016\n\006custom\030\001 \001(\005\0224\n\007custom1\030\002 \001(\0132#.l1j.server.server.readCustomPacket\"c\n\020sendCustomPacket\022\f\n\004type\030\001 \001(\005\022\016\n\006custom\030\002 \001(\005\022\017\n\007custom1\030\003 \001(\005\022\017\n\007custom2\030\004 \001(\005\022\017\n\007custom3\030\005 \001(\005\"H\n\021sendCu", "stomPacket1\0223\n\006custom\030\001 \001(\0132#.l1j.server.server.sendCustomPacket\"Z\n\016sendClanConfig\022\f\n\004type\030\001 \002(\005\022\025\n\rjoinOpenState\030\002 \002(\005\022\021\n\tjoinState\030\003 \002(\005\022\020\n\bpassword\030\004 \002(\t\"È\001\n\017sendCustomSkill\022\021\n\tskillType\030\001 \002(\005\022\017\n\007skillId\030\002 \002(\005\022\f\n\004time\030\003 \002(\005\022\020\n\btimetype\030\004 \002(\005\022\f\n\004icon\030\005 \002(\005\022\020\n\bunknown1\030\006 \002(\005\022\013\n\003seq\030\007 \002(\005\022\020\n\bskillMsg\030\b \002(\005\022\020\n\bstartMsg\030\t \002(\005\022\016\n\006endMsg\030\n \002(\005\022\020\n\bunknown2\030\013 \002(\005\"B\n\021sendCastleWarTime\022\r\n\005isatt\030\001 \002(\005\022\f\n\004t", "ime\030\002 \002(\005\022\020\n\bclanName\030\003 \001(\f\"\001\n\021sendCastleTaxRate\022\020\n\bcastleId\030\001 \002(\005\022\020\n\bclanName\030\002 \002(\f\022\022\n\nleaderName\030\003 \002(\f\022\020\n\bunknown1\030\004 \002(\005\022\020\n\bunknown2\030\005 \002(\005\022\020\n\bunknown3\030\006 \002(\005\022\023\n\013publicMoney\030\007 \002(\005\"¶\001\n\025sendCraftNpcCraftList\022\020\n\bunknown1\030\001 \002(\005\022;\n\004list\030\002 \003(\0132-.l1j.server.server.sendCraftNpcCraftList.List\022\020\n\bunknown2\030\003 \002(\005\032<\n\004List\022\020\n\bactionId\030\001 \002(\005\022\020\n\bunknown1\030\002 \002(\005\022\020\n\bunknown2\030\003 \002(\005\"\037\n\016readCraftNpcId\022\r\n\005npcid\030\001 \002(\005\"0", "\n\rSendCraftType\022\f\n\004type\030\001 \002(\005\022\021\n\ttimeIndex\030\002 \002(\005\"Y\n\fReadUserChat\022\021\n\tchatIndex\030\001 \002(\005\022\020\n\bchatType\030\002 \002(\005\022\020\n\bchatText\030\003 \002(\f\022\022\n\ntargetName\030\005 \001(\f\"­\001\n\007MapInfo\022\r\n\005mapid\030\001 \002(\005\022\020\n\bserverId\030\002 \002(\005\022\024\n\fisUnderwater\030\003 \002(\005\022\020\n\bunknown1\030\004 \002(\005\022\020\n\bunknown2\030\005 \002(\005\022\020\n\bunknown3\030\006 \002(\005\0225\n\bunknown4\030\007 \001(\0132#.l1j.server.server.sendCustomPacket\"\b\n\027CharAbilityDetailedInfo\022\f\n\004type\030\001 \002(\005\022G\n\tstrPacket\030\002 \001(\01324.l1j.server.server.Cha", "rAbilityDetailedInfo.StrPacket\022G\n\tintPacket\030\003 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.IntPacket\022G\n\twisPacket\030\004 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.WisPacket\022G\n\tdexPacket\030\005 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.DexPacket\022G\n\tconPacket\030\006 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.ConPacket\022G\n\tchaPacket\030\007 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.ChaPacket\032Q\n\tS", "trPacket\022\013\n\003dmg\030\001 \002(\005\022\013\n\003hit\030\002 \002(\005\022\032\n\022criticalStrikeRate\030\003 \002(\005\022\016\n\006weight\030\004 \002(\005\032b\n\tDexPacket\022\016\n\006bowDmg\030\001 \002(\005\022\016\n\006bowHit\030\002 \002(\005\022\035\n\025bowcriticalStrikeRate\030\003 \002(\005\022\n\n\002ac\030\004 \002(\005\022\n\n\002er\030\005 \002(\005\032}\n\tIntPacket\022\020\n\bmagicDmg\030\001 \002(\005\022\020\n\bmagicHit\030\002 \002(\005\022\037\n\027magicCriticalStrikeRate\030\003 \002(\005\022\022\n\nmagicBonus\030\004 \002(\005\022\027\n\017MPConsumeReduce\030\005 \002(\005\032i\n\tConPacket\022\013\n\003hpr\030\001 \002(\005\022\023\n\013HPCureBonus\030\002 \002(\005\022\016\n\006weight\030\003 \002(\005\022\024\n\flvUpAddMinHp\030\004 \002(\005\022\024\n\flvUpAd", "dMaxHp\030\005 \002(\005\032e\n\tWisPacket\022\013\n\003mpr\030\001 \002(\005\022\023\n\013MPCureBonus\030\002 \002(\005\022\n\n\002mr\030\003 \002(\005\022\024\n\flvUpAddMinMp\030\004 \002(\005\022\024\n\flvUpAddMaxMp\030\005 \002(\005\032\034\n\tChaPacket\022\017\n\007unknown\030\001 \002(\005\"µ\001\n\023ReadAddCharAbillity\022\r\n\005level\030\001 \002(\005\022\021\n\tclassType\030\002 \002(\005\022\f\n\004type\030\003 \002(\005\022\f\n\004mode\030\004 \001(\005\022\020\n\bstrOrCon\030\005 \001(\005\022\013\n\003str\030\006 \001(\005\022\r\n\005intel\030\007 \001(\005\022\013\n\003wis\030\b \001(\005\022\013\n\003dex\030\t \001(\005\022\013\n\003con\030\n \001(\005\022\013\n\003cha\030\013 \001(\005\"3\n\017DollComposeItem\022\021\n\titemObjId\030\001 \002(\005\022\r\n\005gfxid\030\002 \002(\005\"Ü\001\n\020LotteryInvent", "ory\022\f\n\004type\030\001 \002(\005\022\r\n\005type1\030\002 \002(\005\022F\n\004item\030\003 \003(\01328.l1j.server.server.LotteryInventory.LotteryInventoryItem\032c\n\024LotteryInventoryItem\022\016\n\006descid\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\r\n\005gfxid\030\003 \002(\005\022\017\n\007indexId\030\004 \002(\005\022\f\n\004name\030\005 \002(\f\"3\n\024readLotteryInventory\022\f\n\004type\030\001 \002(\005\022\r\n\005index\030\002 \003(\005\"ì\001\n\rItemCraftRead\022\020\n\bnpcObjId\030\001 \002(\005\022\020\n\bactionId\030\002 \002(\005\022\r\n\005count\030\003 \002(\005\022<\n\bitemlist\030\004 \003(\0132*.l1j.server.server.ItemCraftRead.CraftItem\022\021\n\tcrefCou", "nt\030\005 \001(\005\032W\n\tCraftItem\022\r\n\005index\030\001 \002(\005\022\016\n\006descid\030\002 \002(\005\022\021\n\titemcount\030\003 \001(\005\022\030\n\020itemEnchantLevel\030\004 \001(\005\"Ð\004\n\fPCAndNpcPack\022\013\n\003loc\030\001 \002(\004\022\n\n\002id\030\002 \002(\005\022\r\n\005gfxId\030\003 \002(\005\022\016\n\006status\030\004 \002(\005\022\017\n\007heading\030\005 \002(\005\022\024\n\fownLightSize\030\006 \002(\005\022\021\n\tlightSize\030\007 \002(\005\022\016\n\006lawful\030\b \002(\005\022\f\n\004name\030\t \002(\f\022\r\n\005title\030\n \002(\f\022\021\n\tmoveSpeed\030\013 \002(\005\022\022\n\nbraveSpeed\030\f \002(\005\022\024\n\fisThreeSpeed\030\r \002(\005\022\017\n\007isGhost\030\016 \002(\005\022\023\n\013isParalyzed\030\017 \002(\005\022\020\n\bviewName\030\020 \002(\005\022\017\n\007isInvi", "s\030\021 \002(\005\022\016\n\006posion\030\022 \002(\005\022\016\n\006clanId\030\023 \002(\005\022\020\n\bclanName\030\024 \002(\f\022\016\n\006master\030\025 \002(\f\022\r\n\005state\030\026 \002(\005\022\017\n\007HPMeter\030\027 \002(\005\022\r\n\005level\030\030 \002(\005\022\030\n\020privateShopTitle\030\031 \001(\f\022\020\n\bunknown7\030\032 \002(\005\022\020\n\bunknown8\030\033 \002(\005\022\020\n\bunknown9\030\034 \002(\005\022\021\n\tunknown10\030\035 \001(\005\022\017\n\007MPMeter\030\036 \002(\005\022\021\n\tunknown11\030\037 \001(\005\022\020\n\bServerNo\030  \001(\005\022\021\n\tunknown14\030\" \001(\005\"Y\n\rSendItemCraft\022\024\n\fisSendPacket\030\001 \002(\005\0222\n\004list\030\002 \003(\0132$.l1j.server.server.SendItemCraftList\"\024\n\021SendItemCraf", "tList\022\020\n\bactionid\030\001 \002(\005\022J\n\tcondition\030\002 \002(\01327.l1j.server.server.SendItemCraftList.CraftItemCondition\022\020\n\bunknown1\030\003 \002(\005\022;\n\005quest\030\004 \002(\0132,.l1j.server.server.SendItemCraftList.unQuest\022<\n\004poly\030\005 \002(\0132..l1j.server.server.SendItemCraftList.PolyModel\022D\n\bunknown2\030\006 \002(\01322.l1j.server.server.SendItemCraftList.unknownModel2\022?\n\bmaterial\030\007 \002(\0132-.l1j.server.server.SendItemCraftList.Material\022B\n\007results\030\b \002(\01321.l1j.s", "erver.server.SendItemCraftList.CraftResults\022\026\n\016craftDelayTime\030\t \002(\005\032Ì\001\n\022CraftItemCondition\022\016\n\006nameId\030\001 \002(\005\022\020\n\bminLevel\030\002 \002(\005\022\020\n\bmaxLevel\030\003 \002(\005\022\020\n\bunknown1\030\004 \002(\005\022\021\n\tminLawful\030\005 \002(\005\022\021\n\tMaxLawful\030\006 \002(\005\022\020\n\bminKarma\030\007 \002(\005\022\020\n\bmaxKarma\030\b \002(\005\022\020\n\bmaxCount\030\t \002(\005\022\024\n\fisShowChance\030\n \001(\005\0322\n\nQuestModel\022\017\n\007questId\030\001 \002(\005\022\023\n\013questStepId\030\002 \002(\005\032m\n\007unQuest\022\020\n\bunknown1\030\001 \002(\005\022\020\n\bunknown2\030\002 \002(\005\022>\n\005quest\030\003 \002(\0132/.l1j.serve", "r.server.SendItemCraftList.QuestModel\0320\n\tPolyModel\022\023\n\013pcountcount\030\001 \002(\005\022\016\n\006polyId\030\002 \003(\005\0321\n\runknownModel1\022\017\n\007unknow1\030\001 \002(\003\022\017\n\007unknow2\030\002 \002(\003\032è\002\n\bMaterial\022M\n\bmaterial\030\001 \003(\0132;.l1j.server.server.SendItemCraftList.Material.MaterialModel\022P\n\013addMaterial\030\002 \003(\0132;.l1j.server.server.SendItemCraftList.Material.MaterialModel\032º\001\n\rMaterialModel\022\022\n\nitemDescId\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\021\n\twindowNum\030\003 \002(\005\022\024\n\fenchantLevel", "\030\004 \002(\005\022\r\n\005bless\030\005 \002(\005\022\f\n\004name\030\006 \002(\f\022\r\n\005gfxId\030\007 \002(\005\022\017\n\007unknow1\030\b \002(\005\022\017\n\007unknow2\030\t \002(\005\022\017\n\007unknow3\030\n \002(\005\032\007\n\013CraftResult\022D\n\bunknown1\030\001 \001(\01322.l1j.server.server.SendItemCraftList.unknownModel1\022D\n\bunknown2\030\002 \001(\01322.l1j.server.server.SendItemCraftList.unknownModel1\022\020\n\bitemSize\030\003 \001(\005\022\020\n\bitemtype\030\004 \001(\005\022Z\n\017singleCraftItem\030\006 \003(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\022Z\n\017randomCraft", "Item\030\005 \003(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\022M\n\tgreatItem\030\007 \001(\0132:.l1j.server.server.SendItemCraftList.CraftResult.GreatItem\032º\002\n\020CraftResultModel\022\022\n\nitemDescId\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\020\n\bunknown1\030\003 \002(\003\022\024\n\fenchantLevel\030\004 \002(\005\022\r\n\005bless\030\005 \002(\005\022\020\n\bunknown2\030\006 \002(\005\022\020\n\bunknown3\030\007 \002(\005\022\f\n\004name\030\b \002(\f\022\020\n\bunknown4\030\t \002(\005\022\020\n\bunknown5\030\n \002(\005\022\r\n\005gfxId\030\013 \002(\005\022\025\n\rsuccedMessage\030\f \002(\f\022\027\n\017itemS", "tatusBytes\030\r \002(\f\022\020\n\bunknown6\030\016 \001(\005\022\020\n\bunknown7\030\017 \001(\005\022\023\n\013isGreatItem\030\020 \001(\005\032\001\n\tGreatItem\022\n\n\002un\030\001 \002(\005\022\013\n\003un1\030\002 \002(\005\022\013\n\003un2\030\003 \002(\005\022O\n\004Item\030\004 \002(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\032¯\001\n\fCraftResults\022E\n\013succeedList\030\001 \002(\01320.l1j.server.server.SendItemCraftList.CraftResult\022B\n\bfailList\030\002 \002(\01320.l1j.server.server.SendItemCraftList.CraftResult\022\024\n\fsuccessRatio\030\003 \002(\005\032¬\001\n\runknownMode", "l2\022\f\n\004type\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022G\n\002un\030\003 \003(\0132;.l1j.server.server.SendItemCraftList.unknownModel2.ItemInfo\0325\n\bItemInfo\022\016\n\006descid\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\n\n\002un\030\003 \002(\005\"ò\005\n\rsendDollCraft\022\024\n\fisSendPacket\030\001 \002(\005\022;\n\bmaterial\030\002 \003(\0132).l1j.server.server.sendDollCraft.ItemList\0229\n\006result\030\003 \003(\0132).l1j.server.server.sendDollCraft.ItemList\0228\n\006config\030\004 \003(\0132(.l1j.server.server.sendDollCraft.Configs\032\001\n\bItemList\022\016\n\006statge", "\030\001 \002(\005\022@\n\004item\030\002 \003(\01322.l1j.server.server.sendDollCraft.ItemList.ItemInfo\0321\n\bItemInfo\022\022\n\nitemDescId\030\001 \002(\005\022\021\n\titemGfxId\030\002 \001(\005\032\003\n\007Configs\022\r\n\005stage\030\001 \002(\005\022E\n\006config\030\002 \003(\01325.l1j.server.server.sendDollCraft.Configs.ColumnConfig\022A\n\007config1\030\003 \003(\01320.l1j.server.server.sendDollCraft.Configs.Config1\032B\n\fColumnConfig\022\021\n\tcolumnNum\030\001 \002(\005\022\020\n\bunknown1\030\002 \002(\005\022\r\n\005stage\030\003 \002(\005\032\001\n\007Config1\022\r\n\005stage\030\001 \003(\005\022\f\n\004type\030\002 \002(\005\022I\n", "\007unknow1\030\003 \001(\01328.l1j.server.server.sendDollCraft.Configs.Config1.Config2\032*\n\007Config2\022\017\n\007unknow1\030\001 \001(\005\022\016\n\006stage1\030\002 \001(\005\"\001\n\021sendDollCraftType\022\f\n\004type\030\001 \002(\005\022?\n\bdollItem\030\002 \002(\0132-.l1j.server.server.sendDollCraftType.DollItem\032(\n\bDollItem\022\r\n\005objid\030\001 \002(\005\022\r\n\005gfxId\030\002 \002(\005\"\001\n\rreadDollCraft\022\r\n\005stage\030\001 \002(\005\0227\n\004item\030\002 \003(\0132).l1j.server.server.readDollCraft.ItemInfo\032<\n\bItemInfo\022\r\n\005index\030\001 \002(\005\022\016\n\006descId\030\002 \002(\005\022\021\n\titem", "ObjId\030\003 \002(\005"};
        Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = root -> {
            ItemCraft_NpcId.descriptor = root;
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

    public static abstract interface readCraftNpcIdOrBuilder extends MessageOrBuilder {
        public abstract boolean hasNpcid();

        public abstract int getNpcid();
    }

    public static abstract interface sendCraftNpcCraftListOrBuilder extends MessageOrBuilder {
        public abstract boolean hasUnknown1();

        public abstract int getUnknown1();

        public abstract List<ItemCraft_NpcId.sendCraftNpcCraftList.List> getListList();

        public abstract ItemCraft_NpcId.sendCraftNpcCraftList.List getList(int paramInt);

        public abstract int getListCount();

        public abstract List<? extends ItemCraft_NpcId.sendCraftNpcCraftList.ListOrBuilder> getListOrBuilderList();

        public abstract ItemCraft_NpcId.sendCraftNpcCraftList.ListOrBuilder getListOrBuilder(int paramInt);

        public abstract boolean hasUnknown2();

        public abstract int getUnknown2();
    }

    public static final class readCraftNpcId extends GeneratedMessageV3 implements ItemCraft_NpcId.readCraftNpcIdOrBuilder {
        public static final int NPCID_FIELD_NUMBER = 1;
        @Deprecated
        public static final Parser<readCraftNpcId> PARSER = new AbstractParser<readCraftNpcId>() {
            public ItemCraft_NpcId.readCraftNpcId parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new ItemCraft_NpcId.readCraftNpcId(input, extensionRegistry);
            }
        };
        private static final long serialVersionUID = 0L;
        private static final readCraftNpcId DEFAULT_INSTANCE = new readCraftNpcId();
        private int bitField0_;
        private int npcid_;
        private byte memoizedIsInitialized = -1;

        private readCraftNpcId(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private readCraftNpcId() {
            this.npcid_ = 0;
        }

        private readCraftNpcId(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.npcid_ = input.readInt32();
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
            return ItemCraft_NpcId.readCraftNpcId_descriptor;
        }

        public static readCraftNpcId parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (readCraftNpcId) PARSER.parseFrom(data);
        }

        public static readCraftNpcId parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (readCraftNpcId) PARSER.parseFrom(data, extensionRegistry);
        }

        public static readCraftNpcId parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (readCraftNpcId) PARSER.parseFrom(data);
        }

        public static readCraftNpcId parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (readCraftNpcId) PARSER.parseFrom(data, extensionRegistry);
        }

        public static readCraftNpcId parseFrom(InputStream input) throws IOException {
            return (readCraftNpcId) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static readCraftNpcId parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (readCraftNpcId) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static readCraftNpcId parseDelimitedFrom(InputStream input) throws IOException {
            return (readCraftNpcId) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static readCraftNpcId parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (readCraftNpcId) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static readCraftNpcId parseFrom(CodedInputStream input) throws IOException {
            return (readCraftNpcId) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static readCraftNpcId parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (readCraftNpcId) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(readCraftNpcId prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static readCraftNpcId getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<readCraftNpcId> parser() {
            return PARSER;
        }

        public UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return ItemCraft_NpcId.readCraftNpcId_fieldAccessorTable.ensureFieldAccessorsInitialized(readCraftNpcId.class, Builder.class);
        }

        public boolean hasNpcid() {
            return (this.bitField0_ & 0x1) == 1;
        }

        public int getNpcid() {
            return this.npcid_;
        }

        public boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasNpcid()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 0x1) == 1) {
                output.writeInt32(1, this.npcid_);
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
                size = size + CodedOutputStream.computeInt32Size(1, this.npcid_);
            }
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof readCraftNpcId)) {
                return super.equals(obj);
            }
            readCraftNpcId other = (readCraftNpcId) obj;
            boolean result = true;
            result = (result) && (hasNpcid() == other.hasNpcid());
            if (hasNpcid()) {
                result = (result) && (getNpcid() == other.getNpcid());
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
            if (hasNpcid()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + getNpcid();
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

        public Parser<readCraftNpcId> getParserForType() {
            return PARSER;
        }

        public readCraftNpcId getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_NpcId.readCraftNpcIdOrBuilder {
            private int bitField0_;
            private int npcid_;

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static Descriptors.Descriptor getDescriptor() {
                return ItemCraft_NpcId.readCraftNpcId_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_NpcId.readCraftNpcId_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_NpcId.readCraftNpcId.class, Builder.class);
            }

            private void maybeForceBuilderInitialization() {
            }

            public Builder clear() {
                super.clear();
                this.npcid_ = 0;
                this.bitField0_ &= -2;
                return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return ItemCraft_NpcId.readCraftNpcId_descriptor;
            }

            public ItemCraft_NpcId.readCraftNpcId getDefaultInstanceForType() {
                return ItemCraft_NpcId.readCraftNpcId.getDefaultInstance();
            }

            public ItemCraft_NpcId.readCraftNpcId build() {
                ItemCraft_NpcId.readCraftNpcId result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public ItemCraft_NpcId.readCraftNpcId buildPartial() {
                ItemCraft_NpcId.readCraftNpcId result = new ItemCraft_NpcId.readCraftNpcId(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 1) {
                    to_bitField0_ |= 1;
                }
                result.npcid_ = this.npcid_;
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
                if ((other instanceof ItemCraft_NpcId.readCraftNpcId)) {
                    return mergeFrom((ItemCraft_NpcId.readCraftNpcId) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(ItemCraft_NpcId.readCraftNpcId other) {
                if (other == ItemCraft_NpcId.readCraftNpcId.getDefaultInstance()) {
                    return this;
                }
                if (other.hasNpcid()) {
                    setNpcid(other.getNpcid());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            public boolean isInitialized() {
                if (!hasNpcid()) {
                    return false;
                }
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                ItemCraft_NpcId.readCraftNpcId parsedMessage = null;
                try {
                    parsedMessage = (ItemCraft_NpcId.readCraftNpcId) ItemCraft_NpcId.readCraftNpcId.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ItemCraft_NpcId.readCraftNpcId) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            public boolean hasNpcid() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getNpcid() {
                return this.npcid_;
            }

            public Builder setNpcid(int value) {
                this.bitField0_ |= 1;
                this.npcid_ = value;
                onChanged();
                return this;
            }

            public Builder clearNpcid() {
                this.bitField0_ &= -2;
                this.npcid_ = 0;
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

    public static final class sendCraftNpcCraftList extends GeneratedMessageV3 implements ItemCraft_NpcId.sendCraftNpcCraftListOrBuilder {
        public static final int UNKNOWN1_FIELD_NUMBER = 1;
        public static final int LIST_FIELD_NUMBER = 2;
        public static final int UNKNOWN2_FIELD_NUMBER = 3;
        @Deprecated
        public static final Parser<sendCraftNpcCraftList> PARSER = new AbstractParser<sendCraftNpcCraftList>() {
            public ItemCraft_NpcId.sendCraftNpcCraftList parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new ItemCraft_NpcId.sendCraftNpcCraftList(input, extensionRegistry);
            }
        };
        private static final long serialVersionUID = 0L;
        private static final sendCraftNpcCraftList DEFAULT_INSTANCE = new sendCraftNpcCraftList();
        private int bitField0_;
        private int unknown1_;
        //private List<List> list_;
        private java.util.List<List> list_; // 修復
        private int unknown2_;
        private byte memoizedIsInitialized = -1;

        private sendCraftNpcCraftList(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private sendCraftNpcCraftList() {
            this.unknown1_ = 0;
            this.list_ = Collections.emptyList();
            this.unknown2_ = 0;
        }

        private sendCraftNpcCraftList(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.unknown1_ = input.readInt32();
                            break;
                        case 18:
                            if ((mutable_bitField0_ & 0x2) != 2) {
                                this.list_ = new ArrayList<>();
                                mutable_bitField0_ |= 2;
                            }
                            this.list_.add((List) input.readMessage(List.PARSER, extensionRegistry));
                            break;
                        case 24:
                            this.bitField0_ |= 2;
                            this.unknown2_ = input.readInt32();
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

        public static Descriptors.Descriptor getDescriptor() {
            return ItemCraft_NpcId.sendCraftNpcCraftList_descriptor;
        }

        public static sendCraftNpcCraftList parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (sendCraftNpcCraftList) PARSER.parseFrom(data);
        }

        public static sendCraftNpcCraftList parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (sendCraftNpcCraftList) PARSER.parseFrom(data, extensionRegistry);
        }

        public static sendCraftNpcCraftList parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (sendCraftNpcCraftList) PARSER.parseFrom(data);
        }

        public static sendCraftNpcCraftList parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (sendCraftNpcCraftList) PARSER.parseFrom(data, extensionRegistry);
        }

        public static sendCraftNpcCraftList parseFrom(InputStream input) throws IOException {
            return (sendCraftNpcCraftList) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static sendCraftNpcCraftList parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendCraftNpcCraftList) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static sendCraftNpcCraftList parseDelimitedFrom(InputStream input) throws IOException {
            return (sendCraftNpcCraftList) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static sendCraftNpcCraftList parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendCraftNpcCraftList) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static sendCraftNpcCraftList parseFrom(CodedInputStream input) throws IOException {
            return (sendCraftNpcCraftList) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static sendCraftNpcCraftList parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendCraftNpcCraftList) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(sendCraftNpcCraftList prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static sendCraftNpcCraftList getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<sendCraftNpcCraftList> parser() {
            return PARSER;
        }

        public UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return ItemCraft_NpcId.sendCraftNpcCraftList_fieldAccessorTable.ensureFieldAccessorsInitialized(sendCraftNpcCraftList.class, Builder.class);
        }

        public boolean hasUnknown1() {
            return (this.bitField0_ & 0x1) == 1;
        }

        public int getUnknown1() {
            return this.unknown1_;
        }

        //public List<List> getListList() {
        public java.util.List<List> getListList() { // 修復
            return this.list_;
        }

        //public List<? extends ListOrBuilder> getListOrBuilderList() {
        public java.util.List<? extends ListOrBuilder> getListOrBuilderList() { // 修復
            return this.list_;
        }

        public int getListCount() {
            return this.list_.size();
        }

        public List getList(int index) {
            return (List) this.list_.get(index);
        }

        public ListOrBuilder getListOrBuilder(int index) {
            return (ListOrBuilder) this.list_.get(index);
        }

        public boolean hasUnknown2() {
            return (this.bitField0_ & 0x2) == 2;
        }

        public int getUnknown2() {
            return this.unknown2_;
        }

        public boolean isInitialized() {
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
                output.writeInt32(1, this.unknown1_);
            }
            for (List list : this.list_) {
                output.writeMessage(2, (MessageLite) list);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                output.writeInt32(3, this.unknown2_);
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
            for (List list : this.list_) {
                size = size + CodedOutputStream.computeMessageSize(2, (MessageLite) list);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                size = size + CodedOutputStream.computeInt32Size(3, this.unknown2_);
            }
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof sendCraftNpcCraftList)) {
                return super.equals(obj);
            }
            sendCraftNpcCraftList other = (sendCraftNpcCraftList) obj;
            boolean result = true;
            result = (result) && (hasUnknown1() == other.hasUnknown1());
            if (hasUnknown1()) {
                result = (result) && (getUnknown1() == other.getUnknown1());
            }
            result = (result) && (getListList().equals(other.getListList()));
            result = (result) && (hasUnknown2() == other.hasUnknown2());
            if (hasUnknown2()) {
                result = (result) && (getUnknown2() == other.getUnknown2());
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
            if (getListCount() > 0) {
                hash = 37 * hash + 2;
                hash = 53 * hash + getListList().hashCode();
            }
            if (hasUnknown2()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + getUnknown2();
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

        public Parser<sendCraftNpcCraftList> getParserForType() {
            return PARSER;
        }

        public sendCraftNpcCraftList getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static abstract interface ListOrBuilder extends MessageOrBuilder {
            public abstract boolean hasActionId();

            public abstract int getActionId();

            public abstract boolean hasUnknown1();

            public abstract int getUnknown1();

            public abstract boolean hasUnknown2();

            public abstract int getUnknown2();
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_NpcId.sendCraftNpcCraftListOrBuilder {
            private int bitField0_;
            private int unknown1_;
            //private List<Person.sendCraftNpcCraftList.List> list_ = Collections.emptyList();
            private java.util.List<ItemCraft_NpcId.sendCraftNpcCraftList.List> list_ = Collections.emptyList(); // 修復
            //private java.util.List<List> list_ = Collections.emptyList(); // 修復
            private RepeatedFieldBuilderV3<ItemCraft_NpcId.sendCraftNpcCraftList.List, ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder, ItemCraft_NpcId.sendCraftNpcCraftList.ListOrBuilder> listBuilder_;
            private int unknown2_;

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static Descriptors.Descriptor getDescriptor() {
                return ItemCraft_NpcId.sendCraftNpcCraftList_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_NpcId.sendCraftNpcCraftList_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_NpcId.sendCraftNpcCraftList.class, Builder.class);
            }

            private void maybeForceBuilderInitialization() {
                if (ItemCraft_NpcId.sendCraftNpcCraftList.alwaysUseFieldBuilders) {
                    getListFieldBuilder();
                }
            }

            public Builder clear() {
                super.clear();
                this.unknown1_ = 0;
                this.bitField0_ &= -2;
                if (this.listBuilder_ == null) {
                    this.list_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                } else {
                    this.listBuilder_.clear();
                }
                this.unknown2_ = 0;
                this.bitField0_ &= -5;
                return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return ItemCraft_NpcId.sendCraftNpcCraftList_descriptor;
            }

            public ItemCraft_NpcId.sendCraftNpcCraftList getDefaultInstanceForType() {
                return ItemCraft_NpcId.sendCraftNpcCraftList.getDefaultInstance();
            }

            public ItemCraft_NpcId.sendCraftNpcCraftList build() {
                ItemCraft_NpcId.sendCraftNpcCraftList result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public ItemCraft_NpcId.sendCraftNpcCraftList buildPartial() {
                ItemCraft_NpcId.sendCraftNpcCraftList result = new ItemCraft_NpcId.sendCraftNpcCraftList(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 1) {
                    to_bitField0_ |= 1;
                }
                result.unknown1_ = this.unknown1_;
                if (this.listBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 2) {
                        this.list_ = Collections.unmodifiableList(this.list_);
                        this.bitField0_ &= -3;
                    }
                    result.list_ = this.list_;
                } else {
                    result.list_ = this.listBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 4) {
                    to_bitField0_ |= 2;
                }
                result.unknown2_ = this.unknown2_;
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
                if ((other instanceof ItemCraft_NpcId.sendCraftNpcCraftList)) {
                    return mergeFrom((ItemCraft_NpcId.sendCraftNpcCraftList) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(ItemCraft_NpcId.sendCraftNpcCraftList other) {
                if (other == ItemCraft_NpcId.sendCraftNpcCraftList.getDefaultInstance()) {
                    return this;
                }
                if (other.hasUnknown1()) {
                    setUnknown1(other.getUnknown1());
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
                        this.listBuilder_ = (ItemCraft_NpcId.sendCraftNpcCraftList.alwaysUseFieldBuilders ? getListFieldBuilder() : null);
                    } else {
                        this.listBuilder_.addAllMessages(other.list_);
                    }
                }
                if (other.hasUnknown2()) {
                    setUnknown2(other.getUnknown2());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            public boolean isInitialized() {
                if (!hasUnknown1()) {
                    return false;
                }
                if (!hasUnknown2()) {
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
                ItemCraft_NpcId.sendCraftNpcCraftList parsedMessage = null;
                try {
                    parsedMessage = (ItemCraft_NpcId.sendCraftNpcCraftList) ItemCraft_NpcId.sendCraftNpcCraftList.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ItemCraft_NpcId.sendCraftNpcCraftList) e.getUnfinishedMessage();
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

            private void ensureListIsMutable() {
                if ((this.bitField0_ & 0x2) != 2) {
                    this.list_ = new ArrayList<>(this.list_);
                    this.bitField0_ |= 2;
                }
            }

            //public List<Person.sendCraftNpcCraftList.List> getListList() {
            public java.util.List<ItemCraft_NpcId.sendCraftNpcCraftList.List> getListList() { // 修復
                //public java.util.List<List> getListList() { // 修復
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

            public ItemCraft_NpcId.sendCraftNpcCraftList.List getList(int index) {
                if (this.listBuilder_ == null) {
                    return (ItemCraft_NpcId.sendCraftNpcCraftList.List) this.list_.get(index);
                }
                return (ItemCraft_NpcId.sendCraftNpcCraftList.List) this.listBuilder_.getMessage(index);
            }

            public Builder setList(int index, ItemCraft_NpcId.sendCraftNpcCraftList.List value) {
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

            public Builder setList(int index, ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder builderForValue) {
                if (this.listBuilder_ == null) {
                    ensureListIsMutable();
                    this.list_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    this.listBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addList(ItemCraft_NpcId.sendCraftNpcCraftList.List value) {
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

            public Builder addList(int index, ItemCraft_NpcId.sendCraftNpcCraftList.List value) {
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

            public Builder addList(ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder builderForValue) {
                if (this.listBuilder_ == null) {
                    ensureListIsMutable();
                    this.list_.add(builderForValue.build());
                    onChanged();
                } else {
                    this.listBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addList(int index, ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder builderForValue) {
                if (this.listBuilder_ == null) {
                    ensureListIsMutable();
                    this.list_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    this.listBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllList(Iterable<? extends ItemCraft_NpcId.sendCraftNpcCraftList.List> values) {
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

            public ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder getListBuilder(int index) {
                return (ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder) getListFieldBuilder().getBuilder(index);
            }

            public ItemCraft_NpcId.sendCraftNpcCraftList.ListOrBuilder getListOrBuilder(int index) {
                if (this.listBuilder_ == null) {
                    return (ItemCraft_NpcId.sendCraftNpcCraftList.ListOrBuilder) this.list_.get(index);
                }
                return (ItemCraft_NpcId.sendCraftNpcCraftList.ListOrBuilder) this.listBuilder_.getMessageOrBuilder(index);
            }

            //public List<? extends Person.sendCraftNpcCraftList.ListOrBuilder> getListOrBuilderList() {
            public java.util.List<? extends ItemCraft_NpcId.sendCraftNpcCraftList.ListOrBuilder> getListOrBuilderList() { // 修復
                //public java.util.List<? extends ListOrBuilder> getListOrBuilderList() { // 修復
                if (this.listBuilder_ != null) {
                    return this.listBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.list_);
            }

            public ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder addListBuilder() {
                return (ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder) getListFieldBuilder().addBuilder(ItemCraft_NpcId.sendCraftNpcCraftList.List.getDefaultInstance());
            }

            public ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder addListBuilder(int index) {
                return (ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder) getListFieldBuilder().addBuilder(index, ItemCraft_NpcId.sendCraftNpcCraftList.List.getDefaultInstance());
            }

            //public List<Person.sendCraftNpcCraftList.List.Builder> getListBuilderList() {
            public java.util.List<ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder> getListBuilderList() { // 修復
                //public java.util.List<List.Builder> getListBuilderList() { // 修復
                return getListFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<ItemCraft_NpcId.sendCraftNpcCraftList.List, ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder, ItemCraft_NpcId.sendCraftNpcCraftList.ListOrBuilder> getListFieldBuilder() {
                if (this.listBuilder_ == null) {
                    this.listBuilder_ = new RepeatedFieldBuilderV3<>(this.list_, (this.bitField0_ & 0x2) == 2, getParentForChildren(), isClean());
                    this.list_ = null;
                }
                return this.listBuilder_;
            }

            public boolean hasUnknown2() {
                return (this.bitField0_ & 0x4) == 4;
            }

            public int getUnknown2() {
                return this.unknown2_;
            }

            public Builder setUnknown2(int value) {
                this.bitField0_ |= 4;
                this.unknown2_ = value;
                onChanged();
                return this;
            }

            public Builder clearUnknown2() {
                this.bitField0_ &= -5;
                this.unknown2_ = 0;
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

        public static final class List extends GeneratedMessageV3 implements ItemCraft_NpcId.sendCraftNpcCraftList.ListOrBuilder {
            public static final int ACTIONID_FIELD_NUMBER = 1;
            public static final int UNKNOWN1_FIELD_NUMBER = 2;
            public static final int UNKNOWN2_FIELD_NUMBER = 3;
            @Deprecated
            public static final Parser<List> PARSER = new AbstractParser<List>() {
                public ItemCraft_NpcId.sendCraftNpcCraftList.List parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ItemCraft_NpcId.sendCraftNpcCraftList.List(input, extensionRegistry);
                }
            };
            private static final long serialVersionUID = 0L;
            private static final List DEFAULT_INSTANCE = new List();
            private int bitField0_;
            private int actionId_;
            private int unknown1_;
            private int unknown2_;
            private byte memoizedIsInitialized = -1;

            private List(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private List() {
                this.actionId_ = 0;
                this.unknown1_ = 0;
                this.unknown2_ = 0;
            }

            private List(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.actionId_ = input.readInt32();
                                break;
                            case 16:
                                this.bitField0_ |= 2;
                                this.unknown1_ = input.readInt32();
                                break;
                            case 24:
                                this.bitField0_ |= 4;
                                this.unknown2_ = input.readInt32();
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
                return ItemCraft_NpcId.sendCraftNpcCraftList_List_descriptor;
            }

            public static List parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return (List) PARSER.parseFrom(data);
            }

            public static List parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (List) PARSER.parseFrom(data, extensionRegistry);
            }

            public static List parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return (List) PARSER.parseFrom(data);
            }

            public static List parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return (List) PARSER.parseFrom(data, extensionRegistry);
            }

            public static List parseFrom(InputStream input) throws IOException {
                return (List) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static List parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (List) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static List parseDelimitedFrom(InputStream input) throws IOException {
                return (List) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static List parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (List) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static List parseFrom(CodedInputStream input) throws IOException {
                return (List) GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static List parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return (List) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(List prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            public static List getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<List> parser() {
                return PARSER;
            }

            public UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return ItemCraft_NpcId.sendCraftNpcCraftList_List_fieldAccessorTable.ensureFieldAccessorsInitialized(List.class, Builder.class);
            }

            public boolean hasActionId() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getActionId() {
                return this.actionId_;
            }

            public boolean hasUnknown1() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public int getUnknown1() {
                return this.unknown1_;
            }

            public boolean hasUnknown2() {
                return (this.bitField0_ & 0x4) == 4;
            }

            public int getUnknown2() {
                return this.unknown2_;
            }

            public boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                if (!hasActionId()) {
                    this.memoizedIsInitialized = 0;
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
                this.memoizedIsInitialized = 1;
                return true;
            }

            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 0x1) == 1) {
                    output.writeInt32(1, this.actionId_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    output.writeInt32(2, this.unknown1_);
                }
                if ((this.bitField0_ & 0x4) == 4) {
                    output.writeInt32(3, this.unknown2_);
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
                    size = size + CodedOutputStream.computeInt32Size(1, this.actionId_);
                }
                if ((this.bitField0_ & 0x2) == 2) {
                    size = size + CodedOutputStream.computeInt32Size(2, this.unknown1_);
                }
                if ((this.bitField0_ & 0x4) == 4) {
                    size = size + CodedOutputStream.computeInt32Size(3, this.unknown2_);
                }
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof List)) {
                    return super.equals(obj);
                }
                List other = (List) obj;
                boolean result = true;
                result = (result) && (hasActionId() == other.hasActionId());
                if (hasActionId()) {
                    result = (result) && (getActionId() == other.getActionId());
                }
                result = (result) && (hasUnknown1() == other.hasUnknown1());
                if (hasUnknown1()) {
                    result = (result) && (getUnknown1() == other.getUnknown1());
                }
                result = (result) && (hasUnknown2() == other.hasUnknown2());
                if (hasUnknown2()) {
                    result = (result) && (getUnknown2() == other.getUnknown2());
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
                if (hasActionId()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + getActionId();
                }
                if (hasUnknown1()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + getUnknown1();
                }
                if (hasUnknown2()) {
                    hash = 37 * hash + 3;
                    hash = 53 * hash + getUnknown2();
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

            public Parser<List> getParserForType() {
                return PARSER;
            }

            public List getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ItemCraft_NpcId.sendCraftNpcCraftList.ListOrBuilder {
                private int bitField0_;
                private int actionId_;
                private int unknown1_;
                private int unknown2_;

                private Builder() {
                    maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    maybeForceBuilderInitialization();
                }

                public static Descriptors.Descriptor getDescriptor() {
                    return ItemCraft_NpcId.sendCraftNpcCraftList_List_descriptor;
                }

                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return ItemCraft_NpcId.sendCraftNpcCraftList_List_fieldAccessorTable.ensureFieldAccessorsInitialized(ItemCraft_NpcId.sendCraftNpcCraftList.List.class, Builder.class);
                }

                private void maybeForceBuilderInitialization() {
                }

                public Builder clear() {
                    super.clear();
                    this.actionId_ = 0;
                    this.bitField0_ &= -2;
                    this.unknown1_ = 0;
                    this.bitField0_ &= -3;
                    this.unknown2_ = 0;
                    this.bitField0_ &= -5;
                    return this;
                }

                public Descriptors.Descriptor getDescriptorForType() {
                    return ItemCraft_NpcId.sendCraftNpcCraftList_List_descriptor;
                }

                public ItemCraft_NpcId.sendCraftNpcCraftList.List getDefaultInstanceForType() {
                    return ItemCraft_NpcId.sendCraftNpcCraftList.List.getDefaultInstance();
                }

                public ItemCraft_NpcId.sendCraftNpcCraftList.List build() {
                    ItemCraft_NpcId.sendCraftNpcCraftList.List result = buildPartial();
                    if (!result.isInitialized()) {
                        throw newUninitializedMessageException(result);
                    }
                    return result;
                }

                public ItemCraft_NpcId.sendCraftNpcCraftList.List buildPartial() {
                    ItemCraft_NpcId.sendCraftNpcCraftList.List result = new ItemCraft_NpcId.sendCraftNpcCraftList.List(this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 1) {
                        to_bitField0_ |= 1;
                    }
                    result.actionId_ = this.actionId_;
                    if ((from_bitField0_ & 0x2) == 2) {
                        to_bitField0_ |= 2;
                    }
                    result.unknown1_ = this.unknown1_;
                    if ((from_bitField0_ & 0x4) == 4) {
                        to_bitField0_ |= 4;
                    }
                    result.unknown2_ = this.unknown2_;
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
                    if ((other instanceof ItemCraft_NpcId.sendCraftNpcCraftList.List)) {
                        return mergeFrom((ItemCraft_NpcId.sendCraftNpcCraftList.List) other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(ItemCraft_NpcId.sendCraftNpcCraftList.List other) {
                    if (other == ItemCraft_NpcId.sendCraftNpcCraftList.List.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasActionId()) {
                        setActionId(other.getActionId());
                    }
                    if (other.hasUnknown1()) {
                        setUnknown1(other.getUnknown1());
                    }
                    if (other.hasUnknown2()) {
                        setUnknown2(other.getUnknown2());
                    }
                    mergeUnknownFields(other.unknownFields);
                    onChanged();
                    return this;
                }

                public boolean isInitialized() {
                    if (!hasActionId()) {
                        return false;
                    }
                    if (!hasUnknown1()) {
                        return false;
                    }
                    if (!hasUnknown2()) {
                        return false;
                    }
                    return true;
                }

                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    ItemCraft_NpcId.sendCraftNpcCraftList.List parsedMessage = null;
                    try {
                        parsedMessage = (ItemCraft_NpcId.sendCraftNpcCraftList.List) ItemCraft_NpcId.sendCraftNpcCraftList.List.PARSER.parsePartialFrom(input, extensionRegistry);
                    } catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ItemCraft_NpcId.sendCraftNpcCraftList.List) e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    } finally {
                        if (parsedMessage != null) {
                            mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }

                public boolean hasActionId() {
                    return (this.bitField0_ & 0x1) == 1;
                }

                public int getActionId() {
                    return this.actionId_;
                }

                public Builder setActionId(int value) {
                    this.bitField0_ |= 1;
                    this.actionId_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearActionId() {
                    this.bitField0_ &= -2;
                    this.actionId_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasUnknown1() {
                    return (this.bitField0_ & 0x2) == 2;
                }

                public int getUnknown1() {
                    return this.unknown1_;
                }

                public Builder setUnknown1(int value) {
                    this.bitField0_ |= 2;
                    this.unknown1_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearUnknown1() {
                    this.bitField0_ &= -3;
                    this.unknown1_ = 0;
                    onChanged();
                    return this;
                }

                public boolean hasUnknown2() {
                    return (this.bitField0_ & 0x4) == 4;
                }

                public int getUnknown2() {
                    return this.unknown2_;
                }

                public Builder setUnknown2(int value) {
                    this.bitField0_ |= 4;
                    this.unknown2_ = value;
                    onChanged();
                    return this;
                }

                public Builder clearUnknown2() {
                    this.bitField0_ &= -5;
                    this.unknown2_ = 0;
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
}
