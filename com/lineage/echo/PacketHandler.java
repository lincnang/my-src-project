package com.lineage.echo;

import com.lineage.config.ConfigLIN;
import com.lineage.server.clientpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 客戶端封包處理
 *
 * @author dexc
 */
public class PacketHandler extends PacketHandlerExecutor {
    private static final Log _log = LogFactory.getLog(PacketHandler.class);
    // Map<K,V>
    private static final Map<Integer, ClientBasePacket> _opListClient = new HashMap<Integer, ClientBasePacket>();
    private final ClientExecutor _client;

    public PacketHandler(final ClientExecutor client) {
        _client = client;
    }

    /**
     * 設置執行類
     */
    public static void put(final Integer key, final ClientBasePacket value) {
        if (_opListClient.get(key) == null) {
            _opListClient.put(key, value);
        } else {
            if (!key.equals(-1)) {
                _log.error("重複標記的OPID: " + key + " " + value.getType());
            }
        }
    }

    /**
     * 客戶端封包處理
     *
     * @param decrypt
     * @param //*object
     * @throws Exception
     */
    @Override
    public void handlePacket(final byte[] decrypt) {
        if (decrypt == null) {
            return;
        }
        if (decrypt.length <= 0) {
            return;
        }
        // 一般的處理封包方式
        final int i = decrypt[0] & 0xff;
        if (ConfigLIN.opcode_C) { // 日版封包顯示
            System.out.println("[C opocde] = " + i + "[Length] = " + decrypt.length);
            System.out.println(DataToPacket(decrypt, decrypt.length));
        }
        // try {
        //System.out.println("[Client] opcode = " + i);
        if (i == C_VOICE_CHAT) {
            new C_CharReset().start(decrypt, _client);
            //} else if (i == C_OPCODE_EXCLUDE) {
            //new C_Exclude().start(decrypt, _client);
        } else if (i == C_LOGOUT) {
            new C_ReturnToLogin().start(decrypt, _client);
        } else if (i == C_SAVEIO) {
            new C_CharacterConfig().start(decrypt, _client);
        } else if (i == C_OPEN) {
            new C_Door().start(decrypt, _client);
        } else if (i == C_TITLE) {
            new C_Title().start(decrypt, _client);
        } else if (i == C_BOARD_DELETE) {
            new C_BoardDelete().start(decrypt, _client);
        } else if (i == C_WHO_PLEDGE) {
            new C_Pledge().start(decrypt, _client);
        } else if (i == C_CHANGE_DIRECTION) {
            new C_ChangeHeading().start(decrypt, _client);
        } else if (i == C_HACTION) {
            new C_NPCAction().start(decrypt, _client);
        } else if (i == C_USE_SPELL) {
            new C_UseSkill().start(decrypt, _client);
        } else if (i == C_EMBLEM) {
            new C_EmblemDownload().start(decrypt, _client);
        } else if (i == C_UPLOAD_EMBLEM) {
            new C_EmblemUpload().start(decrypt, _client);
        } else if (i == C_CANCEL_XCHG) {
            new C_TradeCancel().start(decrypt, _client);
        } else if (i == C_BOOKMARK) {
            new C_AddBookmark().start(decrypt, _client);
        } else if (i == C_CREATE_PLEDGE) {
            new C_CreateClan().start(decrypt, _client);
        } else if (i == C_VERSION) {
            new C_ServerVersion().start(decrypt, _client);
        } else if (i == C_MARRIAGE) {
            new C_Propose().start(decrypt, _client);
        } else if (i == C_BOARD_LIST) {
            new C_BoardBack().start(decrypt, _client);
        } else if (i == C_PERSONAL_SHOP) {
            new C_Shop().start(decrypt, _client);
        } else if (i == C_BOARD_READ) {
            new C_BoardRead().start(decrypt, _client);
        } else if (i == C_ASK_XCHG) {
            new C_Trade().start(decrypt, _client);
        } else if (i == C_DELETE_CHARACTER) {
            new C_DeleteChar().start(decrypt, _client);
        } else if (i == C_ANSWER) {
            new C_Attr().start(decrypt, _client);
        } else if (i == C_LOGIN) {
            new C_AuthLogin().start(decrypt, this._client);
        } else if (i == C_BUY_SELL) {
            new C_Result().start(decrypt, _client);
        } else if (i == C_DEPOSIT) {
            new C_Deposit().start(decrypt, _client);
        } else if (i == C_WITHDRAW) {
            new C_Drawal().start(decrypt, _client);
        } else if (i == C_ONOFF) {
            new C_LoginToServerOK().start(decrypt, _client);
        } else if (i == C_BUYABLE_SPELL) {
            new C_SkillBuy().start(decrypt, _client);
        } else if (i == C_BUY_SPELL) {
            new C_SkillBuyOK().start(decrypt, _client);
        } else if (i == C_EXCHANGEABLE_SPELL) {
            new C_SkillBuyItem().start(decrypt, _client);
        } else if (i == C_EXCHANGE_SPELL) {
            new C_SkillBuyItemOK().start(decrypt, _client);
        } else if (i == C_ADD_XCHG) {
            new C_TradeAddItem().start(decrypt, _client);
        } else if (i == C_ADD_BUDDY) {
            new C_AddBuddy().start(decrypt, _client);
        } else if (i == C_SAY) {
            new C_Chat().start(decrypt, _client);
        } else if (i == C_ACCEPT_XCHG) {
            new C_TradeOK().start(decrypt, _client);
        } else if (i == C_CHECK_PK) {
            new C_CheckPK().start(decrypt, _client);
        } else if (i == C_TAX) {
            new C_TaxRate().start(decrypt, _client);
        } else if (i == C_RESTART) {
            new C_NewCharSelect().start(decrypt, _client);
        } else if (i == C_QUERY_BUDDY) {
            new C_Buddy().start(decrypt, _client);
        } else if (i == C_DROP) {
            new C_DropItem().start(decrypt, _client);
        } else if (i == C_LEAVE_PARTY) {
            new C_LeaveParty().start(decrypt, _client);
        } else if (i == C_ATTACK) {
            new C_Attack().start(decrypt, _client);
        } else if (i == C_FAR_ATTACK) {
            //new C_AttackBow().start(decrypt, _client);
            new C_Attack().start(decrypt, _client);
        } else if (i == C_BAN_MEMBER) {
            new C_BanClan().start(decrypt, _client);
        } else if (i == C_PLATE) {
            new C_Board().start(decrypt, _client);
        } else if (i == C_DESTROY_ITEM) {
            new C_DeleteInventoryItem().start(decrypt, _client);
        } else if (i == C_TELL) {
            new C_ChatWhisper().start(decrypt, _client);
        } else if (i == C_WHO_PARTY) {
            new C_Party().start(decrypt, _client);
        } else if (i == C_GET) {
            new C_PickUpItem().start(decrypt, _client);
        } else if (i == C_WHO) {
            new C_Who().start(decrypt, _client);
        } else if (i == C_GIVE) {
            new C_GiveItem().start(decrypt, _client);
        } else if (i == C_MOVE) {
            new C_MoveChar().start(decrypt, _client);
        } else if (i == C_DELETE_BOOKMARK) {
            new C_DeleteBookmark().start(decrypt, _client);
        } else if (i == C_DEAD_RESTART) {
            new C_Restart().start(decrypt, _client);
        } else if (i == C_LEAVE_PLEDGE) {
            new C_LeaveClan().start(decrypt, _client);
        } else if (i == C_DIALOG) {
            new C_NPCTalk().start(decrypt, _client);
        } else if (i == C_BANISH_PARTY) {
            new C_BanParty().start(decrypt, _client);
        } else if (i == C_REMOVE_BUDDY) {
            new C_DelBuddy().start(decrypt, _client);
        } else if (i == C_WAR) {
            new C_War().start(decrypt, _client);
        } else if (i == C_ENTER_WORLD) {
            new C_LoginToServer().start(decrypt, _client);
        } else if (i == C_QUERY_PERSONAL_SHOP) {
            new C_ShopList().start(decrypt, _client);
        } else if (i == C_CHAT) {
            new C_ChatGlobal().start(decrypt, _client);
        } else if (i == C_JOIN_PLEDGE) {
            new C_JoinClan().start(decrypt, _client);
        } else if (i == C_READ_NEWS) {
            new C_CommonClick().start(decrypt, _client);
        } else if (i == C_CREATE_CUSTOM_CHARACTER) {
            new C_CreateChar().start(decrypt, _client);
        } else if (i == C_ACTION) {
            new C_ExtraCommand().start(decrypt, _client);
        } else if (i == C_BOARD_WRITE) {
            new C_BoardWrite().start(decrypt, _client);
        } else if (i == C_USE_ITEM) {
            new C_ItemUSe().start(decrypt, _client);
        } else if (i == C_INVITE_PARTY_TARGET) {
            new C_CreateParty().start(decrypt, _client);
        } else if (i == C_ENTER_PORTAL) {
            new C_EnterPortal().start(decrypt, _client);
        } else if (i == C_HYPERTEXT_INPUT_RESULT) {
            new C_Amount().start(decrypt, _client);
        } else if (i == C_FIXABLE_ITEM) {
            new C_FixWeaponList().start(decrypt, _client);
        } else if (i == C_FIX) {
            new C_SelectList().start(decrypt, _client);
        } else if (i == C_EXIT_GHOST) {
            new C_ExitGhost().start(decrypt, _client);
        } else if (i == C_TELEPORT_USER) {
            new C_CallPlayer().start(decrypt, _client);
        } else if (i == C_SLAVE_CONTROL) {
            new C_SelectTarget().start(decrypt, _client);
        } else if (i == C_CHECK_INVENTORY) {
            new C_PetMenu().start(decrypt, _client);
        } else if (i == C_NPC_ITEM_CONTROL) {
            new C_UsePetItem().start(decrypt, _client);
        } else if (i == C_DUEL) {
            new C_Fight().start(decrypt, _client);
        } else if (i == C_MAIL) {
            new C_Mail().start(decrypt, _client);
        } else if (i == C_GOTO_MAP) {
            new C_Ship().start(decrypt, _client);
        } else if (i == C_RANK_CONTROL) {
            new C_Rank().start(decrypt, _client);
        } else if (i == C_RETURN_SUMMON) {
            new C_Teleport().start(decrypt, _client);
        } else if (i == C_BLINK) {
            new C_UnLock().start(decrypt, _client);
        } else if (i == C_ALIVE) {
            new C_KeepALIVE().start(decrypt, _client);
        } else if (i == C_CHANNEL) {
            new C_Windows().start(decrypt, _client);
        } else if (i == C_SHIFT_SERVER) {
            new C_AutoLogin().start(decrypt, this._client);
        } else if (i == C_THROW) {
            new C_FishClick().start(decrypt, _client);
        } else if (i == C_QUIT) {
            new C_Disconnect().start(decrypt, _client);
        } else if (i == C_SHUTDOWN) {
            new C_PledgeContent().start(decrypt, _client);
        } else if (i == C_EXTENDED_PROTOBUF) {
            new C_Craft().start(decrypt, _client);
            new C_ItemCraft1().start(decrypt, _client);
            if (ConfigLIN.Item_Craft == 1) {
                new C_ItemCraft().start(decrypt, _client); // 原本伊薇火神
            } else if (ConfigLIN.Item_Craft == 2) {
                new C_NewCreateItem().start(decrypt, _client); // 樂天堂火神製作(DB化)
            }
        } else if (i == C_ATTACK_CONTINUE) { // XXX C_OPCODE_ATTACK_CONTINUE
            // 修正為 C_OPCODE_ATTACKRUNING
            new C_AttackHandler().start(decrypt, _client);
        } else if (i == C_CLIENT_READY) {
            new C_ClientReady().start(decrypt, _client);
        } else {
            new C_Unkonwn().start(decrypt, _client);
        }
    }

    /**
     * 日版封包顯示
     *
     * @param data
     * @param len
     * @return
     */
    public String DataToPacket(byte[] data, int len) {
        StringBuffer result = new StringBuffer();
        int counter = 0;
        for (int i = 0; i < len; i++) {
            if (counter % 16 == 0) {
                result.append(HexToDex(i, 4) + ": ");
            }
            result.append(HexToDex(data[i] & 0xff, 2) + " ");
            counter++;
            if (counter == 16) {
                result.append("   ");
                int charpoint = i - 15;
                for (int a = 0; a < 16; a++) {
                    int t1 = data[charpoint++];
                    if (t1 > 0x1f && t1 < 0x80) {
                        result.append((char) t1);
                    } else {
                        result.append('.');
                    }
                }
                result.append("\n");
                counter = 0;
            }
        }
        int rest = data.length % 16;
        if (rest > 0) {
            for (int i = 0; i < 17 - rest; i++) {
                result.append("   ");
            }
            int charpoint = data.length - rest;
            for (int a = 0; a < rest; a++) {
                int t1 = data[charpoint++];
                if (t1 > 0x1f && t1 < 0x80) {
                    result.append((char) t1);
                } else {
                    result.append('.');
                }
            }
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * 日版封包顯示
     *
     * @param data
     * @param digits
     * @return
     */
    private String HexToDex(int data, int digits) {
        String number = Integer.toHexString(data);
        for (int i = number.length(); i < digits; i++) {
            number = "0" + number;
        }
        return number;
    }
}
