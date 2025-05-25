package com.lineage.server.person;

import com.google.protobuf.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * 樂天堂火神製作(DB化)
 */
public final class Custom_Packet {
    private static Descriptors.FileDescriptor descriptor;
    private static final Descriptors.Descriptor readCustomPacket_descriptor = getDescriptor().getMessageTypes().get(15);
    private static final GeneratedMessageV3.FieldAccessorTable readCustomPacket_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(readCustomPacket_descriptor, new String[]{"Custom", "Custom1", "Custom2"});
    private static final Descriptors.Descriptor readCustomPacket1_descriptor = getDescriptor().getMessageTypes().get(16);
    private static final GeneratedMessageV3.FieldAccessorTable readCustomPacket1_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(readCustomPacket1_descriptor, new String[]{"Custom", "Custom1"});
    private static final Descriptors.Descriptor sendCustomPacket_descriptor = getDescriptor().getMessageTypes().get(17);
    private static final GeneratedMessageV3.FieldAccessorTable sendCustomPacket_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(sendCustomPacket_descriptor, new String[]{"Type", "Custom", "Custom1", "Custom2", "Custom3"});
    private static final Descriptors.Descriptor sendCustomPacket1_descriptor = getDescriptor().getMessageTypes().get(18);
    private static final GeneratedMessageV3.FieldAccessorTable sendCustomPacket1_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(sendCustomPacket1_descriptor, new String[]{"Custom"});

    static {
        String[] descriptorData = {"\n\fPerson.proto\022\021l1j.server.server\"¢\001\n\021sendEquipmentInfo\022\f\n\004type\030\001 \002(\005\022<\n\004item\030\002 \003(\0132..l1j.server.server.sendEquipmentInfo.Equipment\022\n\n\002un\030\003 \002(\005\022\013\n\003un1\030\004 \002(\005\032(\n\tEquipment\022\f\n\004eqid\030\001 \002(\005\022\r\n\005objid\030\002 \003(\005\"\002\n\023sendSoulTowerRanked\022K\n\007newRank\030\001 \003(\0132:.l1j.server.server.sendSoulTowerRanked.soulTowerRankedInfo\022K\n\007oldRank\030\002 \003(\0132:.l1j.server.server.sendSoulTowerRanked.soulTowerRankedInfo\032n\n\023soulTowerRankedInfo\022", "\f\n\004name\030\001 \002(\f\022\020\n\busertype\030\002 \002(\005\022\017\n\007maptime\030\003 \002(\005\022\016\n\006gotime\030\004 \002(\005\022\026\n\016soulTowerClass\030\005 \002(\005\"³\002\n\fsendTaskInfo\022\f\n\004type\030\001 \002(\005\022\r\n\005type1\030\002 \002(\005\0226\n\004info\030\003 \003(\0132(.l1j.server.server.sendTaskInfo.TaskInfo\032Í\001\n\bTaskInfo\022\016\n\006taskid\030\001 \002(\005\022\013\n\003url\030\003 \002(\f\022\r\n\005title\030\004 \001(\f\022\021\n\tstarttime\030\005 \001(\005\022\017\n\007endtime\030\006 \001(\005\022?\n\004boss\030\007 \001(\01321.l1j.server.server.sendTaskInfo.TaskInfo.bossInfo\0320\n\bbossInfo\022\020\n\bstringid\030\001 \002(\f\022\022\n\nteleportId\030\002 \002(\005\"í", "\001\n\022sendClanMemberInfo\022\f\n\004type\030\001 \002(\005\022D\n\nmemberInfo\030\002 \003(\01320.l1j.server.server.sendClanMemberInfo.MemberInfo\032\001\n\nMemberInfo\022\r\n\005objid\030\001 \002(\005\022\020\n\bclanName\030\002 \002(\f\022\020\n\bobjectId\030\003 \002(\005\022\020\n\bcharName\030\004 \002(\f\022\f\n\004note\030\005 \002(\f\022\020\n\bisonline\030\006 \002(\005\022\017\n\007jobtype\030\007 \002(\005\"\001\n\023sendMonsterKillInfo\022\f\n\004type\030\001 \002(\005\022\017\n\007unknow2\030\002 \002(\005\022=\n\004info\030\003 \003(\0132/.l1j.server.server.sendMonsterKillInfo.KillInfo\032&\n\bKillInfo\022\013\n\003num\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\"¶\001\n", "\017sendMonsterList\022\017\n\007unknow1\030\001 \002(\005\022\017\n\007unknow2\030\002 \002(\005\022<\n\004info\030\003 \003(\0132..l1j.server.server.sendMonsterList.MonsterInfo\032C\n\013MonsterInfo\022\020\n\bquestNum\030\001 \002(\005\022\023\n\013monsterTime\030\002 \002(\005\022\r\n\005value\030\003 \002(\005\"2\n\fsendClanInfo\022\020\n\bclanName\030\001 \002(\f\022\020\n\bclanRank\030\002 \002(\005\"¨\002\n\023sendTelePortMapInfo\022\020\n\bnpcObjId\030\001 \002(\005\022D\n\004info\030\002 \003(\01326.l1j.server.server.sendTelePortMapInfo.TelePortMapInfo\032¸\001\n\017TelePortMapInfo\022\024\n\ftelePortName\030\001 \002(\f\022O\n\007mapInfo\030\002", " \001(\0132>.l1j.server.server.sendTelePortMapInfo.TelePortMapInfo.MapInfo\032>\n\007MapInfo\022\017\n\007mapType\030\001 \002(\005\022\021\n\tteleMoney\030\002 \002(\005\022\017\n\007unknown\030\003 \002(\005\"\001\n\rsendBuddyList\022\f\n\004type\030\001 \002(\005\022=\n\tbuddyList\030\002 \003(\0132*.l1j.server.server.sendBuddyList.buddyInfo\032<\n\tbuddyInfo\022\f\n\004name\030\001 \002(\f\022\020\n\bisOnline\030\002 \002(\005\022\017\n\007unknown\030\003 \002(\f\";\n\fsendFishTime\022\f\n\004type\030\001 \002(\005\022\f\n\004time\030\002 \001(\005\022\017\n\007isQuick\030\003 \001(\005\"\002\n\013sendTAMPage\0226\n\004user\030\001 \003(\0132(.l1j.server.server", ".sendTAMPage.UserModel\022\020\n\bunknown1\030\002 \002(\005\022\020\n\bunknown2\030\003 \002(\005\022\020\n\bunknown3\030\004 \002(\005\032\001\n\tUserModel\022\020\n\bserverNo\030\001 \002(\005\022\r\n\005objid\030\002 \002(\005\022\f\n\004time\030\003 \002(\005\022\017\n\007tamwait\030\004 \002(\005\022\f\n\004name\030\005 \002(\f\022\r\n\005level\030\006 \002(\005\022\021\n\ttypeClass\030\007 \002(\005\022\013\n\003sex\030\b \002(\005\"\007\n\016sendAttrReward\0227\n\003str\030\001 \002(\0132*.l1j.server.server.sendAttrReward.strModel\0229\n\005intel\030\002 \002(\0132*.l1j.server.server.sendAttrReward.intModel\0227\n\003wis\030\003 \002(\0132*.l1j.server.server.sendAttrReward.w", "isModel\0227\n\003dex\030\004 \002(\0132*.l1j.server.server.sendAttrReward.dexModel\0227\n\003con\030\005 \002(\0132*.l1j.server.server.sendAttrReward.conModel\0227\n\003cha\030\006 \002(\0132*.l1j.server.server.sendAttrReward.chaModel\032R\n\bstrModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003dmg\030\002 \001(\005\022\013\n\003hit\030\003 \001(\005\022\032\n\022criticalStrikeRate\030\004 \001(\005\032a\n\bintModel\022\020\n\battValue\030\001 \001(\005\022\020\n\bmagicDmg\030\002 \001(\005\022\020\n\bmagicHit\030\003 \001(\005\022\037\n\027magicCriticalStrikeRate\030\004 \001(\005\032\001\n\bwisModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003mpr\030", "\002 \001(\005\022\023\n\013MPCureBonus\030\003 \001(\005\022\n\n\002mr\030\004 \001(\005\022\024\n\flvUpAddMinMp\030\005 \001(\005\022\024\n\flvUpAddMaxMp\030\006 \001(\005\022\r\n\005mpMax\030\007 \001(\005\032[\n\bdexModel\022\020\n\battValue\030\001 \001(\005\022\016\n\006bowDmg\030\002 \001(\005\022\016\n\006bowHit\030\003 \001(\005\022\035\n\025bowcriticalStrikeRate\030\004 \001(\005\032s\n\bconModel\022\020\n\battValue\030\001 \001(\005\022\013\n\003hpr\030\002 \001(\005\022\023\n\013HPCureBonus\030\003 \001(\005\022\016\n\006weight\030\004 \001(\005\022\024\n\flvUpAddMaxHp\030\005 \001(\005\022\r\n\005hpMax\030\006 \001(\005\032\034\n\bchaModel\022\020\n\battValue\030\001 \001(\005\"b\n\020sendUserBaseAttr\022\013\n\003str\030\001 \002(\005\022\r\n\005intel\030\002 \002(\005\022\013\n\003wis\030\003 \002(\005\022\013", "\n\003dex\030\004 \002(\005\022\013\n\003con\030\005 \002(\005\022\013\n\003cha\030\006 \002(\005\"A\n\013readBanInfo\022\023\n\013excludeType\030\001 \002(\005\022\017\n\007subType\030\002 \002(\005\022\f\n\004name\030\003 \003(\f\"\026\n\007readSha\022\013\n\003sha\030\001 \002(\f\"D\n\020readCustomPacket\022\016\n\006custom\030\001 \001(\005\022\017\n\007custom1\030\002 \001(\005\022\017\n\007custom2\030\003 \001(\005\"Y\n\021readCustomPacket1\022\016\n\006custom\030\001 \001(\005\0224\n\007custom1\030\002 \001(\0132#.l1j.server.server.readCustomPacket\"c\n\020sendCustomPacket\022\f\n\004type\030\001 \001(\005\022\016\n\006custom\030\002 \001(\005\022\017\n\007custom1\030\003 \001(\005\022\017\n\007custom2\030\004 \001(\005\022\017\n\007custom3\030\005 \001(\005\"H\n\021sendCu", "stomPacket1\0223\n\006custom\030\001 \001(\0132#.l1j.server.server.sendCustomPacket\"Z\n\016sendClanConfig\022\f\n\004type\030\001 \002(\005\022\025\n\rjoinOpenState\030\002 \002(\005\022\021\n\tjoinState\030\003 \002(\005\022\020\n\bpassword\030\004 \002(\t\"È\001\n\017sendCustomSkill\022\021\n\tskillType\030\001 \002(\005\022\017\n\007skillId\030\002 \002(\005\022\f\n\004time\030\003 \002(\005\022\020\n\btimetype\030\004 \002(\005\022\f\n\004icon\030\005 \002(\005\022\020\n\bunknown1\030\006 \002(\005\022\013\n\003seq\030\007 \002(\005\022\020\n\bskillMsg\030\b \002(\005\022\020\n\bstartMsg\030\t \002(\005\022\016\n\006endMsg\030\n \002(\005\022\020\n\bunknown2\030\013 \002(\005\"B\n\021sendCastleWarTime\022\r\n\005isatt\030\001 \002(\005\022\f\n\004t", "ime\030\002 \002(\005\022\020\n\bclanName\030\003 \001(\f\"\001\n\021sendCastleTaxRate\022\020\n\bcastleId\030\001 \002(\005\022\020\n\bclanName\030\002 \002(\f\022\022\n\nleaderName\030\003 \002(\f\022\020\n\bunknown1\030\004 \002(\005\022\020\n\bunknown2\030\005 \002(\005\022\020\n\bunknown3\030\006 \002(\005\022\023\n\013publicMoney\030\007 \002(\005\"¶\001\n\025sendCraftNpcCraftList\022\020\n\bunknown1\030\001 \002(\005\022;\n\004list\030\002 \003(\0132-.l1j.server.server.sendCraftNpcCraftList.List\022\020\n\bunknown2\030\003 \002(\005\032<\n\004List\022\020\n\bactionId\030\001 \002(\005\022\020\n\bunknown1\030\002 \002(\005\022\020\n\bunknown2\030\003 \002(\005\"\037\n\016readCraftNpcId\022\r\n\005npcid\030\001 \002(\005\"0", "\n\rSendCraftType\022\f\n\004type\030\001 \002(\005\022\021\n\ttimeIndex\030\002 \002(\005\"Y\n\fReadUserChat\022\021\n\tchatIndex\030\001 \002(\005\022\020\n\bchatType\030\002 \002(\005\022\020\n\bchatText\030\003 \002(\f\022\022\n\ntargetName\030\005 \001(\f\"­\001\n\007MapInfo\022\r\n\005mapid\030\001 \002(\005\022\020\n\bserverId\030\002 \002(\005\022\024\n\fisUnderwater\030\003 \002(\005\022\020\n\bunknown1\030\004 \002(\005\022\020\n\bunknown2\030\005 \002(\005\022\020\n\bunknown3\030\006 \002(\005\0225\n\bunknown4\030\007 \001(\0132#.l1j.server.server.sendCustomPacket\"\b\n\027CharAbilityDetailedInfo\022\f\n\004type\030\001 \002(\005\022G\n\tstrPacket\030\002 \001(\01324.l1j.server.server.Cha", "rAbilityDetailedInfo.StrPacket\022G\n\tintPacket\030\003 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.IntPacket\022G\n\twisPacket\030\004 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.WisPacket\022G\n\tdexPacket\030\005 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.DexPacket\022G\n\tconPacket\030\006 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.ConPacket\022G\n\tchaPacket\030\007 \001(\01324.l1j.server.server.CharAbilityDetailedInfo.ChaPacket\032Q\n\tS", "trPacket\022\013\n\003dmg\030\001 \002(\005\022\013\n\003hit\030\002 \002(\005\022\032\n\022criticalStrikeRate\030\003 \002(\005\022\016\n\006weight\030\004 \002(\005\032b\n\tDexPacket\022\016\n\006bowDmg\030\001 \002(\005\022\016\n\006bowHit\030\002 \002(\005\022\035\n\025bowcriticalStrikeRate\030\003 \002(\005\022\n\n\002ac\030\004 \002(\005\022\n\n\002er\030\005 \002(\005\032}\n\tIntPacket\022\020\n\bmagicDmg\030\001 \002(\005\022\020\n\bmagicHit\030\002 \002(\005\022\037\n\027magicCriticalStrikeRate\030\003 \002(\005\022\022\n\nmagicBonus\030\004 \002(\005\022\027\n\017MPConsumeReduce\030\005 \002(\005\032i\n\tConPacket\022\013\n\003hpr\030\001 \002(\005\022\023\n\013HPCureBonus\030\002 \002(\005\022\016\n\006weight\030\003 \002(\005\022\024\n\flvUpAddMinHp\030\004 \002(\005\022\024\n\flvUpAd", "dMaxHp\030\005 \002(\005\032e\n\tWisPacket\022\013\n\003mpr\030\001 \002(\005\022\023\n\013MPCureBonus\030\002 \002(\005\022\n\n\002mr\030\003 \002(\005\022\024\n\flvUpAddMinMp\030\004 \002(\005\022\024\n\flvUpAddMaxMp\030\005 \002(\005\032\034\n\tChaPacket\022\017\n\007unknown\030\001 \002(\005\"µ\001\n\023ReadAddCharAbillity\022\r\n\005level\030\001 \002(\005\022\021\n\tclassType\030\002 \002(\005\022\f\n\004type\030\003 \002(\005\022\f\n\004mode\030\004 \001(\005\022\020\n\bstrOrCon\030\005 \001(\005\022\013\n\003str\030\006 \001(\005\022\r\n\005intel\030\007 \001(\005\022\013\n\003wis\030\b \001(\005\022\013\n\003dex\030\t \001(\005\022\013\n\003con\030\n \001(\005\022\013\n\003cha\030\013 \001(\005\"3\n\017DollComposeItem\022\021\n\titemObjId\030\001 \002(\005\022\r\n\005gfxid\030\002 \002(\005\"Ü\001\n\020LotteryInvent", "ory\022\f\n\004type\030\001 \002(\005\022\r\n\005type1\030\002 \002(\005\022F\n\004item\030\003 \003(\01328.l1j.server.server.LotteryInventory.LotteryInventoryItem\032c\n\024LotteryInventoryItem\022\016\n\006descid\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\r\n\005gfxid\030\003 \002(\005\022\017\n\007indexId\030\004 \002(\005\022\f\n\004name\030\005 \002(\f\"3\n\024readLotteryInventory\022\f\n\004type\030\001 \002(\005\022\r\n\005index\030\002 \003(\005\"ì\001\n\rItemCraftRead\022\020\n\bnpcObjId\030\001 \002(\005\022\020\n\bactionId\030\002 \002(\005\022\r\n\005count\030\003 \002(\005\022<\n\bitemlist\030\004 \003(\0132*.l1j.server.server.ItemCraftRead.CraftItem\022\021\n\tcrefCou", "nt\030\005 \001(\005\032W\n\tCraftItem\022\r\n\005index\030\001 \002(\005\022\016\n\006descid\030\002 \002(\005\022\021\n\titemcount\030\003 \001(\005\022\030\n\020itemEnchantLevel\030\004 \001(\005\"Ð\004\n\fPCAndNpcPack\022\013\n\003loc\030\001 \002(\004\022\n\n\002id\030\002 \002(\005\022\r\n\005gfxId\030\003 \002(\005\022\016\n\006status\030\004 \002(\005\022\017\n\007heading\030\005 \002(\005\022\024\n\fownLightSize\030\006 \002(\005\022\021\n\tlightSize\030\007 \002(\005\022\016\n\006lawful\030\b \002(\005\022\f\n\004name\030\t \002(\f\022\r\n\005title\030\n \002(\f\022\021\n\tmoveSpeed\030\013 \002(\005\022\022\n\nbraveSpeed\030\f \002(\005\022\024\n\fisThreeSpeed\030\r \002(\005\022\017\n\007isGhost\030\016 \002(\005\022\023\n\013isParalyzed\030\017 \002(\005\022\020\n\bviewName\030\020 \002(\005\022\017\n\007isInvi", "s\030\021 \002(\005\022\016\n\006posion\030\022 \002(\005\022\016\n\006clanId\030\023 \002(\005\022\020\n\bclanName\030\024 \002(\f\022\016\n\006master\030\025 \002(\f\022\r\n\005state\030\026 \002(\005\022\017\n\007HPMeter\030\027 \002(\005\022\r\n\005level\030\030 \002(\005\022\030\n\020privateShopTitle\030\031 \001(\f\022\020\n\bunknown7\030\032 \002(\005\022\020\n\bunknown8\030\033 \002(\005\022\020\n\bunknown9\030\034 \002(\005\022\021\n\tunknown10\030\035 \001(\005\022\017\n\007MPMeter\030\036 \002(\005\022\021\n\tunknown11\030\037 \001(\005\022\020\n\bServerNo\030  \001(\005\022\021\n\tunknown14\030\" \001(\005\"Y\n\rSendItemCraft\022\024\n\fisSendPacket\030\001 \002(\005\0222\n\004list\030\002 \003(\0132$.l1j.server.server.SendItemCraftList\"\024\n\021SendItemCraf", "tList\022\020\n\bactionid\030\001 \002(\005\022J\n\tcondition\030\002 \002(\01327.l1j.server.server.SendItemCraftList.CraftItemCondition\022\020\n\bunknown1\030\003 \002(\005\022;\n\005quest\030\004 \002(\0132,.l1j.server.server.SendItemCraftList.unQuest\022<\n\004poly\030\005 \002(\0132..l1j.server.server.SendItemCraftList.PolyModel\022D\n\bunknown2\030\006 \002(\01322.l1j.server.server.SendItemCraftList.unknownModel2\022?\n\bmaterial\030\007 \002(\0132-.l1j.server.server.SendItemCraftList.Material\022B\n\007results\030\b \002(\01321.l1j.s", "erver.server.SendItemCraftList.CraftResults\022\026\n\016craftDelayTime\030\t \002(\005\032Ì\001\n\022CraftItemCondition\022\016\n\006nameId\030\001 \002(\005\022\020\n\bminLevel\030\002 \002(\005\022\020\n\bmaxLevel\030\003 \002(\005\022\020\n\bunknown1\030\004 \002(\005\022\021\n\tminLawful\030\005 \002(\005\022\021\n\tMaxLawful\030\006 \002(\005\022\020\n\bminKarma\030\007 \002(\005\022\020\n\bmaxKarma\030\b \002(\005\022\020\n\bmaxCount\030\t \002(\005\022\024\n\fisShowChance\030\n \001(\005\0322\n\nQuestModel\022\017\n\007questId\030\001 \002(\005\022\023\n\013questStepId\030\002 \002(\005\032m\n\007unQuest\022\020\n\bunknown1\030\001 \002(\005\022\020\n\bunknown2\030\002 \002(\005\022>\n\005quest\030\003 \002(\0132/.l1j.serve", "r.server.SendItemCraftList.QuestModel\0320\n\tPolyModel\022\023\n\013pcountcount\030\001 \002(\005\022\016\n\006polyId\030\002 \003(\005\0321\n\runknownModel1\022\017\n\007unknow1\030\001 \002(\003\022\017\n\007unknow2\030\002 \002(\003\032è\002\n\bMaterial\022M\n\bmaterial\030\001 \003(\0132;.l1j.server.server.SendItemCraftList.Material.MaterialModel\022P\n\013addMaterial\030\002 \003(\0132;.l1j.server.server.SendItemCraftList.Material.MaterialModel\032º\001\n\rMaterialModel\022\022\n\nitemDescId\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\021\n\twindowNum\030\003 \002(\005\022\024\n\fenchantLevel", "\030\004 \002(\005\022\r\n\005bless\030\005 \002(\005\022\f\n\004name\030\006 \002(\f\022\r\n\005gfxId\030\007 \002(\005\022\017\n\007unknow1\030\b \002(\005\022\017\n\007unknow2\030\t \002(\005\022\017\n\007unknow3\030\n \002(\005\032\007\n\013CraftResult\022D\n\bunknown1\030\001 \001(\01322.l1j.server.server.SendItemCraftList.unknownModel1\022D\n\bunknown2\030\002 \001(\01322.l1j.server.server.SendItemCraftList.unknownModel1\022\020\n\bitemSize\030\003 \001(\005\022\020\n\bitemtype\030\004 \001(\005\022Z\n\017singleCraftItem\030\006 \003(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\022Z\n\017randomCraft", "Item\030\005 \003(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\022M\n\tgreatItem\030\007 \001(\0132:.l1j.server.server.SendItemCraftList.CraftResult.GreatItem\032º\002\n\020CraftResultModel\022\022\n\nitemDescId\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\020\n\bunknown1\030\003 \002(\003\022\024\n\fenchantLevel\030\004 \002(\005\022\r\n\005bless\030\005 \002(\005\022\020\n\bunknown2\030\006 \002(\005\022\020\n\bunknown3\030\007 \002(\005\022\f\n\004name\030\b \002(\f\022\020\n\bunknown4\030\t \002(\005\022\020\n\bunknown5\030\n \002(\005\022\r\n\005gfxId\030\013 \002(\005\022\025\n\rsuccedMessage\030\f \002(\f\022\027\n\017itemS", "tatusBytes\030\r \002(\f\022\020\n\bunknown6\030\016 \001(\005\022\020\n\bunknown7\030\017 \001(\005\022\023\n\013isGreatItem\030\020 \001(\005\032\001\n\tGreatItem\022\n\n\002un\030\001 \002(\005\022\013\n\003un1\030\002 \002(\005\022\013\n\003un2\030\003 \002(\005\022O\n\004Item\030\004 \002(\0132A.l1j.server.server.SendItemCraftList.CraftResult.CraftResultModel\032¯\001\n\fCraftResults\022E\n\013succeedList\030\001 \002(\01320.l1j.server.server.SendItemCraftList.CraftResult\022B\n\bfailList\030\002 \002(\01320.l1j.server.server.SendItemCraftList.CraftResult\022\024\n\fsuccessRatio\030\003 \002(\005\032¬\001\n\runknownMode", "l2\022\f\n\004type\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022G\n\002un\030\003 \003(\0132;.l1j.server.server.SendItemCraftList.unknownModel2.ItemInfo\0325\n\bItemInfo\022\016\n\006descid\030\001 \002(\005\022\r\n\005count\030\002 \002(\005\022\n\n\002un\030\003 \002(\005\"ò\005\n\rsendDollCraft\022\024\n\fisSendPacket\030\001 \002(\005\022;\n\bmaterial\030\002 \003(\0132).l1j.server.server.sendDollCraft.ItemList\0229\n\006result\030\003 \003(\0132).l1j.server.server.sendDollCraft.ItemList\0228\n\006config\030\004 \003(\0132(.l1j.server.server.sendDollCraft.Configs\032\001\n\bItemList\022\016\n\006statge", "\030\001 \002(\005\022@\n\004item\030\002 \003(\01322.l1j.server.server.sendDollCraft.ItemList.ItemInfo\0321\n\bItemInfo\022\022\n\nitemDescId\030\001 \002(\005\022\021\n\titemGfxId\030\002 \001(\005\032\003\n\007Configs\022\r\n\005stage\030\001 \002(\005\022E\n\006config\030\002 \003(\01325.l1j.server.server.sendDollCraft.Configs.ColumnConfig\022A\n\007config1\030\003 \003(\01320.l1j.server.server.sendDollCraft.Configs.Config1\032B\n\fColumnConfig\022\021\n\tcolumnNum\030\001 \002(\005\022\020\n\bunknown1\030\002 \002(\005\022\r\n\005stage\030\003 \002(\005\032\001\n\007Config1\022\r\n\005stage\030\001 \003(\005\022\f\n\004type\030\002 \002(\005\022I\n", "\007unknow1\030\003 \001(\01328.l1j.server.server.sendDollCraft.Configs.Config1.Config2\032*\n\007Config2\022\017\n\007unknow1\030\001 \001(\005\022\016\n\006stage1\030\002 \001(\005\"\001\n\021sendDollCraftType\022\f\n\004type\030\001 \002(\005\022?\n\bdollItem\030\002 \002(\0132-.l1j.server.server.sendDollCraftType.DollItem\032(\n\bDollItem\022\r\n\005objid\030\001 \002(\005\022\r\n\005gfxId\030\002 \002(\005\"\001\n\rreadDollCraft\022\r\n\005stage\030\001 \002(\005\0227\n\004item\030\002 \003(\0132).l1j.server.server.readDollCraft.ItemInfo\032<\n\bItemInfo\022\r\n\005index\030\001 \002(\005\022\016\n\006descId\030\002 \002(\005\022\021\n\titem", "ObjId\030\003 \002(\005"};
        Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = root -> {
            Custom_Packet.descriptor = root;
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

    public static abstract interface sendCustomPacket1OrBuilder extends MessageOrBuilder {
        public abstract boolean hasCustom();

        public abstract Custom_Packet.sendCustomPacket getCustom();

        public abstract Custom_Packet.sendCustomPacketOrBuilder getCustomOrBuilder();
    }

    public static abstract interface sendCustomPacketOrBuilder extends MessageOrBuilder {
        public abstract boolean hasType();

        public abstract int getType();

        public abstract boolean hasCustom();

        public abstract int getCustom();

        public abstract boolean hasCustom1();

        public abstract int getCustom1();

        public abstract boolean hasCustom2();

        public abstract int getCustom2();

        public abstract boolean hasCustom3();

        public abstract int getCustom3();
    }

    public static abstract interface readCustomPacket1OrBuilder extends MessageOrBuilder {
        public abstract boolean hasCustom();

        public abstract int getCustom();

        public abstract boolean hasCustom1();

        public abstract Custom_Packet.readCustomPacket getCustom1();

        public abstract Custom_Packet.readCustomPacketOrBuilder getCustom1OrBuilder();
    }

    public static abstract interface readCustomPacketOrBuilder extends MessageOrBuilder {
        public abstract boolean hasCustom();

        public abstract int getCustom();

        public abstract boolean hasCustom1();

        public abstract int getCustom1();

        public abstract boolean hasCustom2();

        public abstract int getCustom2();
    }

    public static final class sendCustomPacket extends GeneratedMessageV3 implements Custom_Packet.sendCustomPacketOrBuilder {
        public static final int TYPE_FIELD_NUMBER = 1;
        public static final int CUSTOM_FIELD_NUMBER = 2;
        public static final int CUSTOM1_FIELD_NUMBER = 3;
        public static final int CUSTOM2_FIELD_NUMBER = 4;
        public static final int CUSTOM3_FIELD_NUMBER = 5;
        @Deprecated
        public static final Parser<sendCustomPacket> PARSER = new AbstractParser<sendCustomPacket>() {
            public Custom_Packet.sendCustomPacket parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Custom_Packet.sendCustomPacket(input, extensionRegistry);
            }
        };
        private static final long serialVersionUID = 0L;
        private static final sendCustomPacket DEFAULT_INSTANCE = new sendCustomPacket();
        private int bitField0_;
        private int type_;
        private int custom_;
        private int custom1_;
        private int custom2_;
        private int custom3_;
        private byte memoizedIsInitialized = -1;

        private sendCustomPacket(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private sendCustomPacket() {
            this.type_ = 0;
            this.custom_ = 0;
            this.custom1_ = 0;
            this.custom2_ = 0;
            this.custom3_ = 0;
        }

        private sendCustomPacket(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.custom_ = input.readInt32();
                            break;
                        case 24:
                            this.bitField0_ |= 4;
                            this.custom1_ = input.readInt32();
                            break;
                        case 32:
                            this.bitField0_ |= 8;
                            this.custom2_ = input.readInt32();
                            break;
                        case 40:
                            this.bitField0_ |= 16;
                            this.custom3_ = input.readInt32();
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
            return Custom_Packet.sendCustomPacket_descriptor;
        }

        public static sendCustomPacket parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (sendCustomPacket) PARSER.parseFrom(data);
        }

        public static sendCustomPacket parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (sendCustomPacket) PARSER.parseFrom(data, extensionRegistry);
        }

        public static sendCustomPacket parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (sendCustomPacket) PARSER.parseFrom(data);
        }

        public static sendCustomPacket parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (sendCustomPacket) PARSER.parseFrom(data, extensionRegistry);
        }

        public static sendCustomPacket parseFrom(InputStream input) throws IOException {
            return (sendCustomPacket) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static sendCustomPacket parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendCustomPacket) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static sendCustomPacket parseDelimitedFrom(InputStream input) throws IOException {
            return (sendCustomPacket) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static sendCustomPacket parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendCustomPacket) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static sendCustomPacket parseFrom(CodedInputStream input) throws IOException {
            return (sendCustomPacket) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static sendCustomPacket parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendCustomPacket) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(sendCustomPacket prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static sendCustomPacket getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<sendCustomPacket> parser() {
            return PARSER;
        }

        public UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Custom_Packet.sendCustomPacket_fieldAccessorTable.ensureFieldAccessorsInitialized(sendCustomPacket.class, Builder.class);
        }

        public boolean hasType() {
            return (this.bitField0_ & 0x1) == 1;
        }

        public int getType() {
            return this.type_;
        }

        public boolean hasCustom() {
            return (this.bitField0_ & 0x2) == 2;
        }

        public int getCustom() {
            return this.custom_;
        }

        public boolean hasCustom1() {
            return (this.bitField0_ & 0x4) == 4;
        }

        public int getCustom1() {
            return this.custom1_;
        }

        public boolean hasCustom2() {
            return (this.bitField0_ & 0x8) == 8;
        }

        public int getCustom2() {
            return this.custom2_;
        }

        public boolean hasCustom3() {
            return (this.bitField0_ & 0x10) == 16;
        }

        public int getCustom3() {
            return this.custom3_;
        }

        public boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
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
                output.writeInt32(2, this.custom_);
            }
            if ((this.bitField0_ & 0x4) == 4) {
                output.writeInt32(3, this.custom1_);
            }
            if ((this.bitField0_ & 0x8) == 8) {
                output.writeInt32(4, this.custom2_);
            }
            if ((this.bitField0_ & 0x10) == 16) {
                output.writeInt32(5, this.custom3_);
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
                size = size + CodedOutputStream.computeInt32Size(2, this.custom_);
            }
            if ((this.bitField0_ & 0x4) == 4) {
                size = size + CodedOutputStream.computeInt32Size(3, this.custom1_);
            }
            if ((this.bitField0_ & 0x8) == 8) {
                size = size + CodedOutputStream.computeInt32Size(4, this.custom2_);
            }
            if ((this.bitField0_ & 0x10) == 16) {
                size = size + CodedOutputStream.computeInt32Size(5, this.custom3_);
            }
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof sendCustomPacket)) {
                return super.equals(obj);
            }
            sendCustomPacket other = (sendCustomPacket) obj;
            boolean result = true;
            result = (result) && (hasType() == other.hasType());
            if (hasType()) {
                result = (result) && (getType() == other.getType());
            }
            result = (result) && (hasCustom() == other.hasCustom());
            if (hasCustom()) {
                result = (result) && (getCustom() == other.getCustom());
            }
            result = (result) && (hasCustom1() == other.hasCustom1());
            if (hasCustom1()) {
                result = (result) && (getCustom1() == other.getCustom1());
            }
            result = (result) && (hasCustom2() == other.hasCustom2());
            if (hasCustom2()) {
                result = (result) && (getCustom2() == other.getCustom2());
            }
            result = (result) && (hasCustom3() == other.hasCustom3());
            if (hasCustom3()) {
                result = (result) && (getCustom3() == other.getCustom3());
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
            if (hasCustom()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + getCustom();
            }
            if (hasCustom1()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + getCustom1();
            }
            if (hasCustom2()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + getCustom2();
            }
            if (hasCustom3()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + getCustom3();
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

        public Parser<sendCustomPacket> getParserForType() {
            return PARSER;
        }

        public sendCustomPacket getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Custom_Packet.sendCustomPacketOrBuilder {
            private int bitField0_;
            private int type_;
            private int custom_;
            private int custom1_;
            private int custom2_;
            private int custom3_;

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static Descriptors.Descriptor getDescriptor() {
                return Custom_Packet.sendCustomPacket_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Custom_Packet.sendCustomPacket_fieldAccessorTable.ensureFieldAccessorsInitialized(Custom_Packet.sendCustomPacket.class, Builder.class);
            }

            private void maybeForceBuilderInitialization() {
            }

            public Builder clear() {
                super.clear();
                this.type_ = 0;
                this.bitField0_ &= -2;
                this.custom_ = 0;
                this.bitField0_ &= -3;
                this.custom1_ = 0;
                this.bitField0_ &= -5;
                this.custom2_ = 0;
                this.bitField0_ &= -9;
                this.custom3_ = 0;
                this.bitField0_ &= -17;
                return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return Custom_Packet.sendCustomPacket_descriptor;
            }

            public Custom_Packet.sendCustomPacket getDefaultInstanceForType() {
                return Custom_Packet.sendCustomPacket.getDefaultInstance();
            }

            public Custom_Packet.sendCustomPacket build() {
                Custom_Packet.sendCustomPacket result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public Custom_Packet.sendCustomPacket buildPartial() {
                Custom_Packet.sendCustomPacket result = new Custom_Packet.sendCustomPacket(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 1) {
                    to_bitField0_ |= 1;
                }
                result.type_ = this.type_;
                if ((from_bitField0_ & 0x2) == 2) {
                    to_bitField0_ |= 2;
                }
                result.custom_ = this.custom_;
                if ((from_bitField0_ & 0x4) == 4) {
                    to_bitField0_ |= 4;
                }
                result.custom1_ = this.custom1_;
                if ((from_bitField0_ & 0x8) == 8) {
                    to_bitField0_ |= 8;
                }
                result.custom2_ = this.custom2_;
                if ((from_bitField0_ & 0x10) == 16) {
                    to_bitField0_ |= 16;
                }
                result.custom3_ = this.custom3_;
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
                if ((other instanceof Custom_Packet.sendCustomPacket)) {
                    return mergeFrom((Custom_Packet.sendCustomPacket) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Custom_Packet.sendCustomPacket other) {
                if (other == Custom_Packet.sendCustomPacket.getDefaultInstance()) {
                    return this;
                }
                if (other.hasType()) {
                    setType(other.getType());
                }
                if (other.hasCustom()) {
                    setCustom(other.getCustom());
                }
                if (other.hasCustom1()) {
                    setCustom1(other.getCustom1());
                }
                if (other.hasCustom2()) {
                    setCustom2(other.getCustom2());
                }
                if (other.hasCustom3()) {
                    setCustom3(other.getCustom3());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            public boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Custom_Packet.sendCustomPacket parsedMessage = null;
                try {
                    parsedMessage = (Custom_Packet.sendCustomPacket) Custom_Packet.sendCustomPacket.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Custom_Packet.sendCustomPacket) e.getUnfinishedMessage();
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

            public boolean hasCustom() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public int getCustom() {
                return this.custom_;
            }

            public Builder setCustom(int value) {
                this.bitField0_ |= 2;
                this.custom_ = value;
                onChanged();
                return this;
            }

            public Builder clearCustom() {
                this.bitField0_ &= -3;
                this.custom_ = 0;
                onChanged();
                return this;
            }

            public boolean hasCustom1() {
                return (this.bitField0_ & 0x4) == 4;
            }

            public int getCustom1() {
                return this.custom1_;
            }

            public Builder setCustom1(int value) {
                this.bitField0_ |= 4;
                this.custom1_ = value;
                onChanged();
                return this;
            }

            public Builder clearCustom1() {
                this.bitField0_ &= -5;
                this.custom1_ = 0;
                onChanged();
                return this;
            }

            public boolean hasCustom2() {
                return (this.bitField0_ & 0x8) == 8;
            }

            public int getCustom2() {
                return this.custom2_;
            }

            public Builder setCustom2(int value) {
                this.bitField0_ |= 8;
                this.custom2_ = value;
                onChanged();
                return this;
            }

            public Builder clearCustom2() {
                this.bitField0_ &= -9;
                this.custom2_ = 0;
                onChanged();
                return this;
            }

            public boolean hasCustom3() {
                return (this.bitField0_ & 0x10) == 16;
            }

            public int getCustom3() {
                return this.custom3_;
            }

            public Builder setCustom3(int value) {
                this.bitField0_ |= 16;
                this.custom3_ = value;
                onChanged();
                return this;
            }

            public Builder clearCustom3() {
                this.bitField0_ &= -17;
                this.custom3_ = 0;
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

    public static final class sendCustomPacket1 extends GeneratedMessageV3 implements Custom_Packet.sendCustomPacket1OrBuilder {
        public static final int CUSTOM_FIELD_NUMBER = 1;
        @Deprecated
        public static final Parser<sendCustomPacket1> PARSER = new AbstractParser<sendCustomPacket1>() {
            public Custom_Packet.sendCustomPacket1 parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Custom_Packet.sendCustomPacket1(input, extensionRegistry);
            }
        };
        private static final long serialVersionUID = 0L;
        private static final sendCustomPacket1 DEFAULT_INSTANCE = new sendCustomPacket1();
        private int bitField0_;
        private Custom_Packet.sendCustomPacket custom_;
        private byte memoizedIsInitialized = -1;

        private sendCustomPacket1(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private sendCustomPacket1() {
        }

        private sendCustomPacket1(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            Custom_Packet.sendCustomPacket.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 1) {
                                subBuilder = this.custom_.toBuilder();
                            }
                            this.custom_ = ((Custom_Packet.sendCustomPacket) input.readMessage(Custom_Packet.sendCustomPacket.PARSER, extensionRegistry));
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.custom_);
                                this.custom_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 1;
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
            return Custom_Packet.sendCustomPacket1_descriptor;
        }

        public static sendCustomPacket1 parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (sendCustomPacket1) PARSER.parseFrom(data);
        }

        public static sendCustomPacket1 parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (sendCustomPacket1) PARSER.parseFrom(data, extensionRegistry);
        }

        public static sendCustomPacket1 parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (sendCustomPacket1) PARSER.parseFrom(data);
        }

        public static sendCustomPacket1 parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (sendCustomPacket1) PARSER.parseFrom(data, extensionRegistry);
        }

        public static sendCustomPacket1 parseFrom(InputStream input) throws IOException {
            return (sendCustomPacket1) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static sendCustomPacket1 parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendCustomPacket1) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static sendCustomPacket1 parseDelimitedFrom(InputStream input) throws IOException {
            return (sendCustomPacket1) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static sendCustomPacket1 parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendCustomPacket1) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static sendCustomPacket1 parseFrom(CodedInputStream input) throws IOException {
            return (sendCustomPacket1) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static sendCustomPacket1 parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (sendCustomPacket1) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(sendCustomPacket1 prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static sendCustomPacket1 getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<sendCustomPacket1> parser() {
            return PARSER;
        }

        public UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Custom_Packet.sendCustomPacket1_fieldAccessorTable.ensureFieldAccessorsInitialized(sendCustomPacket1.class, Builder.class);
        }

        public boolean hasCustom() {
            return (this.bitField0_ & 0x1) == 1;
        }

        public Custom_Packet.sendCustomPacket getCustom() {
            return this.custom_ == null ? Custom_Packet.sendCustomPacket.getDefaultInstance() : this.custom_;
        }

        public Custom_Packet.sendCustomPacketOrBuilder getCustomOrBuilder() {
            return this.custom_ == null ? Custom_Packet.sendCustomPacket.getDefaultInstance() : this.custom_;
        }

        public boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 0x1) == 1) {
                output.writeMessage(1, getCustom());
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
                size = size + CodedOutputStream.computeMessageSize(1, getCustom());
            }
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof sendCustomPacket1)) {
                return super.equals(obj);
            }
            sendCustomPacket1 other = (sendCustomPacket1) obj;
            boolean result = true;
            result = (result) && (hasCustom() == other.hasCustom());
            if (hasCustom()) {
                result = (result) && (getCustom().equals(other.getCustom()));
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
            if (hasCustom()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + getCustom().hashCode();
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

        public Parser<sendCustomPacket1> getParserForType() {
            return PARSER;
        }

        public sendCustomPacket1 getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Custom_Packet.sendCustomPacket1OrBuilder {
            private int bitField0_;
            private Custom_Packet.sendCustomPacket custom_ = null;
            private SingleFieldBuilderV3<Custom_Packet.sendCustomPacket, Custom_Packet.sendCustomPacket.Builder, Custom_Packet.sendCustomPacketOrBuilder> customBuilder_;

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static Descriptors.Descriptor getDescriptor() {
                return Custom_Packet.sendCustomPacket1_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Custom_Packet.sendCustomPacket1_fieldAccessorTable.ensureFieldAccessorsInitialized(Custom_Packet.sendCustomPacket1.class, Builder.class);
            }

            private void maybeForceBuilderInitialization() {
                if (Custom_Packet.sendCustomPacket1.alwaysUseFieldBuilders) {
                    getCustomFieldBuilder();
                }
            }

            public Builder clear() {
                super.clear();
                if (this.customBuilder_ == null) {
                    this.custom_ = null;
                } else {
                    this.customBuilder_.clear();
                }
                this.bitField0_ &= -2;
                return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return Custom_Packet.sendCustomPacket1_descriptor;
            }

            public Custom_Packet.sendCustomPacket1 getDefaultInstanceForType() {
                return Custom_Packet.sendCustomPacket1.getDefaultInstance();
            }

            public Custom_Packet.sendCustomPacket1 build() {
                Custom_Packet.sendCustomPacket1 result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public Custom_Packet.sendCustomPacket1 buildPartial() {
                Custom_Packet.sendCustomPacket1 result = new Custom_Packet.sendCustomPacket1(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 1) {
                    to_bitField0_ |= 1;
                }
                if (this.customBuilder_ == null) {
                    result.custom_ = this.custom_;
                } else {
                    result.custom_ = ((Custom_Packet.sendCustomPacket) this.customBuilder_.build());
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
                if ((other instanceof Custom_Packet.sendCustomPacket1)) {
                    return mergeFrom((Custom_Packet.sendCustomPacket1) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Custom_Packet.sendCustomPacket1 other) {
                if (other == Custom_Packet.sendCustomPacket1.getDefaultInstance()) {
                    return this;
                }
                if (other.hasCustom()) {
                    mergeCustom(other.getCustom());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            public boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Custom_Packet.sendCustomPacket1 parsedMessage = null;
                try {
                    parsedMessage = (Custom_Packet.sendCustomPacket1) Custom_Packet.sendCustomPacket1.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Custom_Packet.sendCustomPacket1) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            public boolean hasCustom() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public Custom_Packet.sendCustomPacket getCustom() {
                if (this.customBuilder_ == null) {
                    return this.custom_ == null ? Custom_Packet.sendCustomPacket.getDefaultInstance() : this.custom_;
                }
                return (Custom_Packet.sendCustomPacket) this.customBuilder_.getMessage();
            }

            public Builder setCustom(Custom_Packet.sendCustomPacket value) {
                if (this.customBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.custom_ = value;
                    onChanged();
                } else {
                    this.customBuilder_.setMessage(value);
                }
                this.bitField0_ |= 1;
                return this;
            }

            public Builder setCustom(Custom_Packet.sendCustomPacket.Builder builderForValue) {
                if (this.customBuilder_ == null) {
                    this.custom_ = builderForValue.build();
                    onChanged();
                } else {
                    this.customBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 1;
                return this;
            }

            public Builder mergeCustom(Custom_Packet.sendCustomPacket value) {
                if (this.customBuilder_ == null) {
                    if (((this.bitField0_ & 0x1) == 1) && (this.custom_ != null) && (this.custom_ != Custom_Packet.sendCustomPacket.getDefaultInstance())) {
                        this.custom_ = Custom_Packet.sendCustomPacket.newBuilder(this.custom_).mergeFrom(value).buildPartial();
                    } else {
                        this.custom_ = value;
                    }
                    onChanged();
                } else {
                    this.customBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 1;
                return this;
            }

            public Builder clearCustom() {
                if (this.customBuilder_ == null) {
                    this.custom_ = null;
                    onChanged();
                } else {
                    this.customBuilder_.clear();
                }
                this.bitField0_ &= -2;
                return this;
            }

            public Custom_Packet.sendCustomPacket.Builder getCustomBuilder() {
                this.bitField0_ |= 1;
                onChanged();
                return (Custom_Packet.sendCustomPacket.Builder) getCustomFieldBuilder().getBuilder();
            }

            public Custom_Packet.sendCustomPacketOrBuilder getCustomOrBuilder() {
                if (this.customBuilder_ != null) {
                    return (Custom_Packet.sendCustomPacketOrBuilder) this.customBuilder_.getMessageOrBuilder();
                }
                return this.custom_ == null ? Custom_Packet.sendCustomPacket.getDefaultInstance() : this.custom_;
            }

            private SingleFieldBuilderV3<Custom_Packet.sendCustomPacket, Custom_Packet.sendCustomPacket.Builder, Custom_Packet.sendCustomPacketOrBuilder> getCustomFieldBuilder() {
                if (this.customBuilder_ == null) {
                    this.customBuilder_ = new SingleFieldBuilderV3<>(getCustom(), getParentForChildren(), isClean());
                    this.custom_ = null;
                }
                return this.customBuilder_;
            }

            public Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            public Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }
    }

    public static final class readCustomPacket extends GeneratedMessageV3 implements Custom_Packet.readCustomPacketOrBuilder {
        public static final int CUSTOM_FIELD_NUMBER = 1;
        public static final int CUSTOM1_FIELD_NUMBER = 2;
        public static final int CUSTOM2_FIELD_NUMBER = 3;
        @Deprecated
        public static final Parser<readCustomPacket> PARSER = new AbstractParser<readCustomPacket>() {
            public Custom_Packet.readCustomPacket parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Custom_Packet.readCustomPacket(input, extensionRegistry);
            }
        };
        private static final long serialVersionUID = 0L;
        private static final readCustomPacket DEFAULT_INSTANCE = new readCustomPacket();
        private int bitField0_;
        private int custom_;
        private int custom1_;
        private int custom2_;
        private byte memoizedIsInitialized = -1;

        private readCustomPacket(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private readCustomPacket() {
            this.custom_ = 0;
            this.custom1_ = 0;
            this.custom2_ = 0;
        }

        private readCustomPacket(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.custom_ = input.readInt32();
                            break;
                        case 16:
                            this.bitField0_ |= 2;
                            this.custom1_ = input.readInt32();
                            break;
                        case 24:
                            this.bitField0_ |= 4;
                            this.custom2_ = input.readInt32();
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
            return Custom_Packet.readCustomPacket_descriptor;
        }

        public static readCustomPacket parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (readCustomPacket) PARSER.parseFrom(data);
        }

        public static readCustomPacket parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (readCustomPacket) PARSER.parseFrom(data, extensionRegistry);
        }

        public static readCustomPacket parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (readCustomPacket) PARSER.parseFrom(data);
        }

        public static readCustomPacket parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (readCustomPacket) PARSER.parseFrom(data, extensionRegistry);
        }

        public static readCustomPacket parseFrom(InputStream input) throws IOException {
            return (readCustomPacket) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static readCustomPacket parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (readCustomPacket) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static readCustomPacket parseDelimitedFrom(InputStream input) throws IOException {
            return (readCustomPacket) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static readCustomPacket parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (readCustomPacket) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static readCustomPacket parseFrom(CodedInputStream input) throws IOException {
            return (readCustomPacket) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static readCustomPacket parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (readCustomPacket) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(readCustomPacket prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static readCustomPacket getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<readCustomPacket> parser() {
            return PARSER;
        }

        public UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Custom_Packet.readCustomPacket_fieldAccessorTable.ensureFieldAccessorsInitialized(readCustomPacket.class, Builder.class);
        }

        public boolean hasCustom() {
            return (this.bitField0_ & 0x1) == 1;
        }

        public int getCustom() {
            return this.custom_;
        }

        public boolean hasCustom1() {
            return (this.bitField0_ & 0x2) == 2;
        }

        public int getCustom1() {
            return this.custom1_;
        }

        public boolean hasCustom2() {
            return (this.bitField0_ & 0x4) == 4;
        }

        public int getCustom2() {
            return this.custom2_;
        }

        public boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 0x1) == 1) {
                output.writeInt32(1, this.custom_);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                output.writeInt32(2, this.custom1_);
            }
            if ((this.bitField0_ & 0x4) == 4) {
                output.writeInt32(3, this.custom2_);
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
                size = size + CodedOutputStream.computeInt32Size(1, this.custom_);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                size = size + CodedOutputStream.computeInt32Size(2, this.custom1_);
            }
            if ((this.bitField0_ & 0x4) == 4) {
                size = size + CodedOutputStream.computeInt32Size(3, this.custom2_);
            }
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof readCustomPacket)) {
                return super.equals(obj);
            }
            readCustomPacket other = (readCustomPacket) obj;
            boolean result = true;
            result = (result) && (hasCustom() == other.hasCustom());
            if (hasCustom()) {
                result = (result) && (getCustom() == other.getCustom());
            }
            result = (result) && (hasCustom1() == other.hasCustom1());
            if (hasCustom1()) {
                result = (result) && (getCustom1() == other.getCustom1());
            }
            result = (result) && (hasCustom2() == other.hasCustom2());
            if (hasCustom2()) {
                result = (result) && (getCustom2() == other.getCustom2());
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
            if (hasCustom()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + getCustom();
            }
            if (hasCustom1()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + getCustom1();
            }
            if (hasCustom2()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + getCustom2();
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

        public Parser<readCustomPacket> getParserForType() {
            return PARSER;
        }

        public readCustomPacket getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Custom_Packet.readCustomPacketOrBuilder {
            private int bitField0_;
            private int custom_;
            private int custom1_;
            private int custom2_;

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static Descriptors.Descriptor getDescriptor() {
                return Custom_Packet.readCustomPacket_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Custom_Packet.readCustomPacket_fieldAccessorTable.ensureFieldAccessorsInitialized(Custom_Packet.readCustomPacket.class, Builder.class);
            }

            private void maybeForceBuilderInitialization() {
            }

            public Builder clear() {
                super.clear();
                this.custom_ = 0;
                this.bitField0_ &= -2;
                this.custom1_ = 0;
                this.bitField0_ &= -3;
                this.custom2_ = 0;
                this.bitField0_ &= -5;
                return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return Custom_Packet.readCustomPacket_descriptor;
            }

            public Custom_Packet.readCustomPacket getDefaultInstanceForType() {
                return Custom_Packet.readCustomPacket.getDefaultInstance();
            }

            public Custom_Packet.readCustomPacket build() {
                Custom_Packet.readCustomPacket result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public Custom_Packet.readCustomPacket buildPartial() {
                Custom_Packet.readCustomPacket result = new Custom_Packet.readCustomPacket(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 1) {
                    to_bitField0_ |= 1;
                }
                result.custom_ = this.custom_;
                if ((from_bitField0_ & 0x2) == 2) {
                    to_bitField0_ |= 2;
                }
                result.custom1_ = this.custom1_;
                if ((from_bitField0_ & 0x4) == 4) {
                    to_bitField0_ |= 4;
                }
                result.custom2_ = this.custom2_;
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
                if ((other instanceof Custom_Packet.readCustomPacket)) {
                    return mergeFrom((Custom_Packet.readCustomPacket) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Custom_Packet.readCustomPacket other) {
                if (other == Custom_Packet.readCustomPacket.getDefaultInstance()) {
                    return this;
                }
                if (other.hasCustom()) {
                    setCustom(other.getCustom());
                }
                if (other.hasCustom1()) {
                    setCustom1(other.getCustom1());
                }
                if (other.hasCustom2()) {
                    setCustom2(other.getCustom2());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            public boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Custom_Packet.readCustomPacket parsedMessage = null;
                try {
                    parsedMessage = (Custom_Packet.readCustomPacket) Custom_Packet.readCustomPacket.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Custom_Packet.readCustomPacket) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            public boolean hasCustom() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getCustom() {
                return this.custom_;
            }

            public Builder setCustom(int value) {
                this.bitField0_ |= 1;
                this.custom_ = value;
                onChanged();
                return this;
            }

            public Builder clearCustom() {
                this.bitField0_ &= -2;
                this.custom_ = 0;
                onChanged();
                return this;
            }

            public boolean hasCustom1() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public int getCustom1() {
                return this.custom1_;
            }

            public Builder setCustom1(int value) {
                this.bitField0_ |= 2;
                this.custom1_ = value;
                onChanged();
                return this;
            }

            public Builder clearCustom1() {
                this.bitField0_ &= -3;
                this.custom1_ = 0;
                onChanged();
                return this;
            }

            public boolean hasCustom2() {
                return (this.bitField0_ & 0x4) == 4;
            }

            public int getCustom2() {
                return this.custom2_;
            }

            public Builder setCustom2(int value) {
                this.bitField0_ |= 4;
                this.custom2_ = value;
                onChanged();
                return this;
            }

            public Builder clearCustom2() {
                this.bitField0_ &= -5;
                this.custom2_ = 0;
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

    public static final class readCustomPacket1 extends GeneratedMessageV3 implements Custom_Packet.readCustomPacket1OrBuilder {
        public static final int CUSTOM_FIELD_NUMBER = 1;
        public static final int CUSTOM1_FIELD_NUMBER = 2;
        @Deprecated
        public static final Parser<readCustomPacket1> PARSER = new AbstractParser<readCustomPacket1>() {
            public Custom_Packet.readCustomPacket1 parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Custom_Packet.readCustomPacket1(input, extensionRegistry);
            }
        };
        private static final long serialVersionUID = 0L;
        private static final readCustomPacket1 DEFAULT_INSTANCE = new readCustomPacket1();
        private int bitField0_;
        private int custom_;
        private Custom_Packet.readCustomPacket custom1_;
        private byte memoizedIsInitialized = -1;

        private readCustomPacket1(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private readCustomPacket1() {
            this.custom_ = 0;
        }

        private readCustomPacket1(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.custom_ = input.readInt32();
                            break;
                        case 18:
                            Custom_Packet.readCustomPacket.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x2) == 2) {
                                subBuilder = this.custom1_.toBuilder();
                            }
                            this.custom1_ = ((Custom_Packet.readCustomPacket) input.readMessage(Custom_Packet.readCustomPacket.PARSER, extensionRegistry));
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.custom1_);
                                this.custom1_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 2;
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
            return Custom_Packet.readCustomPacket1_descriptor;
        }

        public static readCustomPacket1 parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (readCustomPacket1) PARSER.parseFrom(data);
        }

        public static readCustomPacket1 parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (readCustomPacket1) PARSER.parseFrom(data, extensionRegistry);
        }

        public static readCustomPacket1 parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (readCustomPacket1) PARSER.parseFrom(data);
        }

        public static readCustomPacket1 parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (readCustomPacket1) PARSER.parseFrom(data, extensionRegistry);
        }

        public static readCustomPacket1 parseFrom(InputStream input) throws IOException {
            return (readCustomPacket1) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static readCustomPacket1 parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (readCustomPacket1) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static readCustomPacket1 parseDelimitedFrom(InputStream input) throws IOException {
            return (readCustomPacket1) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static readCustomPacket1 parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (readCustomPacket1) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static readCustomPacket1 parseFrom(CodedInputStream input) throws IOException {
            return (readCustomPacket1) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static readCustomPacket1 parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (readCustomPacket1) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(readCustomPacket1 prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static readCustomPacket1 getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<readCustomPacket1> parser() {
            return PARSER;
        }

        public UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Custom_Packet.readCustomPacket1_fieldAccessorTable.ensureFieldAccessorsInitialized(readCustomPacket1.class, Builder.class);
        }

        public boolean hasCustom() {
            return (this.bitField0_ & 0x1) == 1;
        }

        public int getCustom() {
            return this.custom_;
        }

        public boolean hasCustom1() {
            return (this.bitField0_ & 0x2) == 2;
        }

        public Custom_Packet.readCustomPacket getCustom1() {
            return this.custom1_ == null ? Custom_Packet.readCustomPacket.getDefaultInstance() : this.custom1_;
        }

        public Custom_Packet.readCustomPacketOrBuilder getCustom1OrBuilder() {
            return this.custom1_ == null ? Custom_Packet.readCustomPacket.getDefaultInstance() : this.custom1_;
        }

        public boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 0x1) == 1) {
                output.writeInt32(1, this.custom_);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                output.writeMessage(2, getCustom1());
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
                size = size + CodedOutputStream.computeInt32Size(1, this.custom_);
            }
            if ((this.bitField0_ & 0x2) == 2) {
                size = size + CodedOutputStream.computeMessageSize(2, getCustom1());
            }
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof readCustomPacket1)) {
                return super.equals(obj);
            }
            readCustomPacket1 other = (readCustomPacket1) obj;
            boolean result = true;
            result = (result) && (hasCustom() == other.hasCustom());
            if (hasCustom()) {
                result = (result) && (getCustom() == other.getCustom());
            }
            result = (result) && (hasCustom1() == other.hasCustom1());
            if (hasCustom1()) {
                result = (result) && (getCustom1().equals(other.getCustom1()));
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
            if (hasCustom()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + getCustom();
            }
            if (hasCustom1()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + getCustom1().hashCode();
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

        public Parser<readCustomPacket1> getParserForType() {
            return PARSER;
        }

        public readCustomPacket1 getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Custom_Packet.readCustomPacket1OrBuilder {
            private int bitField0_;
            private int custom_;
            private Custom_Packet.readCustomPacket custom1_ = null;
            private SingleFieldBuilderV3<Custom_Packet.readCustomPacket, Custom_Packet.readCustomPacket.Builder, Custom_Packet.readCustomPacketOrBuilder> custom1Builder_;

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static Descriptors.Descriptor getDescriptor() {
                return Custom_Packet.readCustomPacket1_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Custom_Packet.readCustomPacket1_fieldAccessorTable.ensureFieldAccessorsInitialized(Custom_Packet.readCustomPacket1.class, Builder.class);
            }

            private void maybeForceBuilderInitialization() {
                if (Custom_Packet.readCustomPacket1.alwaysUseFieldBuilders) {
                    getCustom1FieldBuilder();
                }
            }

            public Builder clear() {
                super.clear();
                this.custom_ = 0;
                this.bitField0_ &= -2;
                if (this.custom1Builder_ == null) {
                    this.custom1_ = null;
                } else {
                    this.custom1Builder_.clear();
                }
                this.bitField0_ &= -3;
                return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
                return Custom_Packet.readCustomPacket1_descriptor;
            }

            public Custom_Packet.readCustomPacket1 getDefaultInstanceForType() {
                return Custom_Packet.readCustomPacket1.getDefaultInstance();
            }

            public Custom_Packet.readCustomPacket1 build() {
                Custom_Packet.readCustomPacket1 result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public Custom_Packet.readCustomPacket1 buildPartial() {
                Custom_Packet.readCustomPacket1 result = new Custom_Packet.readCustomPacket1(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 1) {
                    to_bitField0_ |= 1;
                }
                result.custom_ = this.custom_;
                if ((from_bitField0_ & 0x2) == 2) {
                    to_bitField0_ |= 2;
                }
                if (this.custom1Builder_ == null) {
                    result.custom1_ = this.custom1_;
                } else {
                    result.custom1_ = ((Custom_Packet.readCustomPacket) this.custom1Builder_.build());
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
                if ((other instanceof Custom_Packet.readCustomPacket1)) {
                    return mergeFrom((Custom_Packet.readCustomPacket1) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Custom_Packet.readCustomPacket1 other) {
                if (other == Custom_Packet.readCustomPacket1.getDefaultInstance()) {
                    return this;
                }
                if (other.hasCustom()) {
                    setCustom(other.getCustom());
                }
                if (other.hasCustom1()) {
                    mergeCustom1(other.getCustom1());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            public boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Custom_Packet.readCustomPacket1 parsedMessage = null;
                try {
                    parsedMessage = (Custom_Packet.readCustomPacket1) Custom_Packet.readCustomPacket1.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Custom_Packet.readCustomPacket1) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            public boolean hasCustom() {
                return (this.bitField0_ & 0x1) == 1;
            }

            public int getCustom() {
                return this.custom_;
            }

            public Builder setCustom(int value) {
                this.bitField0_ |= 1;
                this.custom_ = value;
                onChanged();
                return this;
            }

            public Builder clearCustom() {
                this.bitField0_ &= -2;
                this.custom_ = 0;
                onChanged();
                return this;
            }

            public boolean hasCustom1() {
                return (this.bitField0_ & 0x2) == 2;
            }

            public Custom_Packet.readCustomPacket getCustom1() {
                if (this.custom1Builder_ == null) {
                    return this.custom1_ == null ? Custom_Packet.readCustomPacket.getDefaultInstance() : this.custom1_;
                }
                return (Custom_Packet.readCustomPacket) this.custom1Builder_.getMessage();
            }

            public Builder setCustom1(Custom_Packet.readCustomPacket value) {
                if (this.custom1Builder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.custom1_ = value;
                    onChanged();
                } else {
                    this.custom1Builder_.setMessage(value);
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder setCustom1(Custom_Packet.readCustomPacket.Builder builderForValue) {
                if (this.custom1Builder_ == null) {
                    this.custom1_ = builderForValue.build();
                    onChanged();
                } else {
                    this.custom1Builder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder mergeCustom1(Custom_Packet.readCustomPacket value) {
                if (this.custom1Builder_ == null) {
                    if (((this.bitField0_ & 0x2) == 2) && (this.custom1_ != null) && (this.custom1_ != Custom_Packet.readCustomPacket.getDefaultInstance())) {
                        this.custom1_ = Custom_Packet.readCustomPacket.newBuilder(this.custom1_).mergeFrom(value).buildPartial();
                    } else {
                        this.custom1_ = value;
                    }
                    onChanged();
                } else {
                    this.custom1Builder_.mergeFrom(value);
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder clearCustom1() {
                if (this.custom1Builder_ == null) {
                    this.custom1_ = null;
                    onChanged();
                } else {
                    this.custom1Builder_.clear();
                }
                this.bitField0_ &= -3;
                return this;
            }

            public Custom_Packet.readCustomPacket.Builder getCustom1Builder() {
                this.bitField0_ |= 2;
                onChanged();
                return (Custom_Packet.readCustomPacket.Builder) getCustom1FieldBuilder().getBuilder();
            }

            public Custom_Packet.readCustomPacketOrBuilder getCustom1OrBuilder() {
                if (this.custom1Builder_ != null) {
                    return (Custom_Packet.readCustomPacketOrBuilder) this.custom1Builder_.getMessageOrBuilder();
                }
                return this.custom1_ == null ? Custom_Packet.readCustomPacket.getDefaultInstance() : this.custom1_;
            }

            private SingleFieldBuilderV3<Custom_Packet.readCustomPacket, Custom_Packet.readCustomPacket.Builder, Custom_Packet.readCustomPacketOrBuilder> getCustom1FieldBuilder() {
                if (this.custom1Builder_ == null) {
                    this.custom1Builder_ = new SingleFieldBuilderV3<>(getCustom1(), getParentForChildren(), isClean());
                    this.custom1_ = null;
                }
                return this.custom1Builder_;
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
