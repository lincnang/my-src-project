package com.lineage.server.model.Instance;

import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.*;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.DeName;
import com.lineage.server.templates.L1EmblemIcon;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.DeAiThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 對象:虛擬玩家 控制項
 *
 * @author daien
 */
public class L1DeInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1DeInstance.class);
    private static final Random _random = new Random();
    private static final ArrayList<DeName> _denameList = new ArrayList<DeName>();
    private static final int[][] _class_list = {{0, 61, 138, 734, 2786, 6658, 6671}, {1, 48, 37, 1186, 2796, 6661, 6650}};
    private final Map<L1ItemInstance, Integer> _sellList = new HashMap<L1ItemInstance, Integer>();
    private final Map<Integer, int[]> _buyList = new HashMap<Integer, int[]>();
    private String _shop_chat1 = null;
    private String _shop_chat2 = null;
    private boolean _is_shop = false;
    private int _clanid = 0;
    private String _clan_name = null;
    private L1EmblemIcon _emblem = null;
    private DeName _de_name = null;
    private int _classId;
    private String _global_chat = null;
    private int _chat_mode = 0;
    private boolean _chat = false;
    private boolean _is_fishing = false;
    private int _fishX = -1;
    private int _fishY = -1;

    public L1DeInstance(L1Npc template) {
        super(template);
        startNpc();
    }

    public L1DeInstance(L1Npc template, DeName de) {
        super(template);
        _de_name = de;
        startNpc();
    }

    /**
     * 接觸資訊
     */
    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (!_is_shop) {
                start_shop();
            }
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_NPCPack_De(this));
            if (_emblem != null) {
                perceivedFrom.sendPackets(new S_Emblem(_emblem));
            }
            if (isShop()) {
                perceivedFrom.sendPackets(new S_DoActionShop(getId(), _shop_chat1));
                return;
            }
            if (isFishing()) {
                perceivedFrom.sendPackets(new S_Fishing(getId(), 71, _fishX, _fishY));
                return;
            }
            onNpcAI();
            if (getBraveSpeed() == 1) {
                perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void startNpc() {
        try {
            if (_de_name == null) {
                DeName[] des = DeNameTable.get().getDeNameList();
                int length = des.length;
                DeName de = des[ThreadLocalRandom.current().nextInt(length)];
                while (_denameList.contains(de)) {
                    de = des[ThreadLocalRandom.current().nextInt(length)];
                }
                _de_name = de;
            }
            _denameList.add(_de_name);
            setNameId(_de_name.get_name());
            int classid = _class_list[_de_name.get_sex()][_de_name.get_type()];
            int starus = 0;
            switch (classid) {
                case 0:
                case 1:
                    switch (ThreadLocalRandom.current().nextInt(5)) {
                        case 0:
                        case 2:
                            starus = 4;
                            break;
                        case 1:
                            starus = 24;
                            break;
                        case 3:
                            starus = 50;
                            break;
                        case 4:
                            starus = 20;
                            break;
                    }
                    break;
                case 48:
                case 61:
                    switch (ThreadLocalRandom.current().nextInt(5)) {
                        case 0:
                        case 2:
                            starus = 4;
                            break;
                        case 1:
                        case 3:
                            starus = 20;
                            break;
                        case 4:
                            starus = 50;
                    }
                    break;
                case 37:
                case 138:
                    switch (ThreadLocalRandom.current().nextInt(5)) {
                        case 0:
                        case 2:
                            starus = 4;
                            break;
                        case 1:
                        case 3:
                        case 4:
                            starus = 20;
                    }
                    break;
                case 734:
                case 1186:
                    switch (ThreadLocalRandom.current().nextInt(5)) {
                        case 0:
                        case 2:
                            starus = 4;
                            break;
                        case 1:
                            starus = 20;
                            break;
                        case 3:
                        case 4:
                            starus = 40;
                    }
                    break;
                case 2786:
                case 2796:
                    switch (ThreadLocalRandom.current().nextInt(5)) {
                        case 0:
                            starus = 4;
                            break;
                        case 2:
                            starus = 58;
                            break;
                        case 1:
                        case 3:
                            starus = 20;
                            break;
                        case 4:
                            starus = 54;
                    }
                    break;
                case 6658:
                case 6661:
                    switch (ThreadLocalRandom.current().nextInt(5)) {
                        case 0:
                        case 2:
                            starus = 4;
                            break;
                        case 1:
                            starus = 20;
                            break;
                        case 3:
                        case 4:
                            starus = 24;
                            break;
                    }
                    break;
                case 6650:
                case 6671:
                    switch (ThreadLocalRandom.current().nextInt(5)) {
                        case 0:
                        case 4:
                            starus = 40;
                            break;
                        case 1:
                        case 2:
                            starus = 20;
                            break;
                        case 3:
                            starus = 58;
                    }
                    break;
            }
            int[] polyids = { // 拿劍
                    20538, 20289, 20935,18319,20145,19600,20681,20430,20438};
            int[] polyids2 = { // 拿弓
                    20326, 18468, 20622, 18473,20501,20072,20789,20599};
            int[] polyids3 = { // 拿矛
                    18349, 18469, 18198, 18193, 20350, 18470, 20354};
            int polyid = polyids[ThreadLocalRandom.current().nextInt(polyids.length)];
            int polyid2 = polyids2[ThreadLocalRandom.current().nextInt(polyids2.length)];
            int polyid3 = polyids3[ThreadLocalRandom.current().nextInt(polyids3.length)];
			/*if (_de_name.get_clanid() != 0) {
				DeClan deClan = DeClanTable.get().get(_de_name.get_clanid());
				_clanid = deClan.get_clan_id();
				_clan_name = deClan.get_clanname();
				_emblem = ClanEmblemReading.get().get(_clanid);
			}*/
            // 具有血盟編號
            if (_de_name.get_clanid() != 0) {
                final L1Clan clan = ClanReading.get().getTemplate(_de_name.get_clanid());
                _clanid = clan.getClanId();
                _clan_name = clan.getClanName();
                _emblem = ClanEmblemReading.get().get(_clanid);
            }
            setStatus(starus);
            setClassId(classid);
            if (starus == 20) {// 拿弓
                setTempCharGfx(polyid2);
                set_ranged(10);
                setBowActId(66);
            } else if (starus == 24) {// 拿矛
                setTempCharGfx(polyid3);
            } else {// 拿刀
                setTempCharGfx(polyid);
            }
            setGfxId(classid);
            setTitle(DeTitleTable.get().getTitle());
            setBraveSpeed(1);
            setMoveSpeed(1);
            int attack = SprTable.get().getAttackSpeed(getTempCharGfx(), 1);
            int move = SprTable.get().getMoveSpeed(classid, getStatus());
            if (starus == 20) {// 拿弓
                attack = SprTable.get().getAttackSpeed(getTempCharGfx(), 21);
            } else if (starus == 24) {// 拿矛
                attack = SprTable.get().getAttackSpeed(getTempCharGfx(), 25);
            }
            setPassispeed(move);
            setAtkspeed(attack);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int getClassId() {
        return _classId;
    }

    public void setClassId(int i) {
        _classId = i;
    }

    public DeName get_deName() {
        return _de_name;
    }

    public int get_chat_mode() {
        return _chat_mode;
    }

    public String get_chat() {
        return _global_chat;
    }

    public void set_chat(String chat, int cmd) {
        _global_chat = chat;
        _chat_mode = cmd;
        if (ThreadLocalRandom.current().nextInt(100) <= 30) {
            _chat = true;
        }
    }

    public void globalChat() {
        try {
            if (_random.nextBoolean()) {
                return;
            }
            ServerBasePacket pack = null;
            if (_chat_mode != 27) {
                pack = new S_ChatGlobal(this, _global_chat);
            } else if (_chat_mode != 28) {
                pack = new S_ChatTransaction(this, _global_chat);
            }
			/*for (Iterator<L1PcInstance> iter = World.get().getAllPlayers().iterator(); iter.hasNext();) {
				L1PcInstance listner = (L1PcInstance) iter.next();
				if (!listner.getExcludingList().contains(getNameId())) {
					if (listner.isShowTradeChat()) {
						listner.sendPackets(pack);
					}
				}
			}*/
            // 黑名單
            for (Iterator<L1PcInstance> iter = World.get().getAllPlayers().iterator(); iter.hasNext(); ) {
                L1PcInstance listner = (L1PcInstance) iter.next();
                L1ExcludingList spamList20 = SpamTable.getInstance().getExcludeTable(listner.getId());
                if (!spamList20.contains(0, getNameId())) {
                    if (listner.isShowTradeChat()) {
                        listner.sendPackets(pack);
                    }
                }
            }
            if (_chat) {
                return;
            }
            pack = new S_Chat(this, _global_chat);
			/*for (L1PcInstance listner : World.get().getRecognizePlayer(this)) {
				if (!listner.getExcludingList().contains(getNameId())) {
					if (get_showId() == listner.get_showId())
						listner.sendPackets(pack);
				}
			}*/
            // 黑名單
            for (L1PcInstance listner : World.get().getRecognizePlayer(this)) {
                L1ExcludingList spamList21 = SpamTable.getInstance().getExcludeTable(listner.getId());
                if (!spamList21.contains(0, getNameId())) {
                    if (get_showId() == listner.get_showId()) {
                        listner.sendPackets(pack);
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean isFishing() {
        return _is_fishing;
    }

    public void start_fishingAI() {
        if (!_is_fishing) {
            Fishing_Runnable runnable = new Fishing_Runnable(this);
            runnable.startCmd();
        }
    }

    public void stop_fishing() {
        if (_is_fishing) {
            broadcastPacketAll(new S_CharVisualUpdate(getId(), 0));
        }
        _is_fishing = false;
    }

    public void start_fishing() {
        try {
            int locx = getX();
            int locy = getY();
            int count = 3 + ThreadLocalRandom.current().nextInt(2);
            for (int i = 0; i < count; i++) {
                locx += HEADING_TABLE_X[getHeading()];
                locy += HEADING_TABLE_Y[getHeading()];
            }
            int gab = getMap().getOriginalTile(locx, locy);
            if (gab % 28 == 0) {
                _fishX = locx;
                _fishY = locy;
                setHeading(targetDirection(_fishX, _fishY));
                broadcastPacketAll(new S_ChangeHeading(this));
                broadcastPacketAll(new S_Fishing(getId(), 71, _fishX, _fishY));
                _is_fishing = true;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int get_fishX() {
        return _fishX;
    }

    public int get_fishY() {
        return _fishY;
    }

    /**
     * 娃娃跟隨主人變更移動/速度狀態
     */
    public void setNpcSpeed() {
        try {
            // 取回娃娃
            if (!getDolls().isEmpty()) {
                for (final Object obj : getDolls().values().toArray()) {
                    final L1DollInstance doll = (L1DollInstance) obj;
                    if (doll != null) {
                        doll.setNpcMoveSpeed();
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void searchTarget() {
        try {
            if (isShop()) {
                return;
            }
            /*
             * if (isSafetyZone()) { return; }
             */
            L1NpcInstance targetNpc = searchTarget(this);
            if (targetNpc != null) {
                _hateList.add(targetNpc, 0);
                _target = targetNpc;
                if (!getPetList().isEmpty()) {
                    for (Iterator<?> iter = getPetList().values().iterator(); iter.hasNext(); ) {
                        L1NpcInstance summon = (L1NpcInstance) iter.next();
                        if ((summon != null) && ((summon instanceof L1SummonInstance))) {
                            L1SummonInstance su = (L1SummonInstance) summon;
                            su.setMasterTarget(_target);
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private L1MonsterInstance searchTarget(L1DeInstance npc) {
        try {
            for (L1Object tg : World.get().getVisibleObjects(this, -1)) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10L);
                } catch (InterruptedException e) {
                    _log.error(e.getLocalizedMessage(), e);
                }
                if ((tg instanceof L1MonsterInstance)) {
                    L1MonsterInstance tgNpc = (L1MonsterInstance) tg;
                    if (!tgNpc.isDead()) {
                        if (tgNpc.getCurrentHp() > 0) {
                            if (get_showId() == tgNpc.get_showId()) {
                                if ((tgNpc.getLevel() > 5) && (_random.nextBoolean())) {
                                    return tgNpc;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public void setLink(L1Character cha) {
        try {
            if (get_showId() != cha.get_showId()) {
                return;
            }
            if ((cha != null) && (_hateList.isEmpty())) {
                _hateList.add(cha, 0);
                checkTarget();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onNpcAI() {
        try {
            if (isShop()) {
                return;
            }
            //if (getMapId() == 4 && isSafetyZone()) { // 安全區發呆
            if (isSafetyZone()) {
                return;
            }
            if (isAiRunning()) {
                return;
            }
            setActived(false);
            startAI();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onTalkAction(L1PcInstance pc) {
    }

    public void onAction(L1PcInstance pc) {
        try {
            if ((isSafetyZone()) || (pc.isSafetyZone())) {
                L1AttackMode attack_mortion = new L1AttackPc(pc, this);
                attack_mortion.action();
                return;
            }
            if ((getCurrentHp() > 0) && (!isDead())) {
                L1AttackMode attack = new L1AttackPc(pc, this);
                if (attack.calcHit()) {
                    attack.calcDamage();
                }
                attack.action();
                attack.commit();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void ReceiveManaDamage(L1Character attacker, int mpDamage) {
        if ((mpDamage > 0) && (!isDead())) {
            setHate(attacker, mpDamage);
            onNpcAI();
            if ((attacker instanceof L1PcInstance)) {
                serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
            }
            int newMp = getCurrentMp() - mpDamage;
            if (newMp < 0) {
                newMp = 0;
            }
            setCurrentMp(newMp);
        }
    }

    public void receiveDamage(L1Character attacker, int damage) {
        if ((getCurrentHp() > 0) && (!isDead())) {
            if ((getHiddenStatus() == 1) || (getHiddenStatus() == 2)) {
                return;
            }
            if ((damage >= 0) && (!(attacker instanceof L1EffectInstance))) {
                if ((attacker instanceof L1IllusoryInstance)) {
                    L1IllusoryInstance ill = (L1IllusoryInstance) attacker;
                    attacker = ill.getMaster();
                    setHate(attacker, damage);
                } else {
                    setHate(attacker, damage);
                }
            }
            if (damage > 0) {
                removeSkillEffect(66);
                removeSkillEffect(212);
            }
            onNpcAI();
            if (((attacker instanceof L1PcInstance)) && (damage > 0)) {
                L1PcInstance player = (L1PcInstance) attacker;
                player.setPetTarget(this);
            }
            if (ThreadLocalRandom.current().nextInt(100) < 80) {// 80%機率喝水
                int newHp = getCurrentHp() + 75;
                int[] x = {189, 194, 197};
                setCurrentHp(newHp);
                broadcastPacketAll(new S_SkillSound(getId(), x[ThreadLocalRandom.current().nextInt(x.length)]));
            }
            int newHp = getCurrentHp() - damage;
            if ((newHp <= 0) && (!isDead())) {
                setCurrentHpDirect(0);
                setDead(true);
                setStatus(8);
                Death death = new Death(attacker);
                DeAiThreadPool.get().execute(death);
            }
            if (newHp > 0) {
                setCurrentHp(newHp);
            }
        } else if (!isDead()) {
            setDead(true);
            setStatus(8);
            Death death = new Death(attacker);
            DeAiThreadPool.get().execute(death);
        }
    }

    public void setCurrentHp(int i) {
        int currentHp = Math.min(i, getMaxHp());
        if (getCurrentHp() == currentHp) {
            return;
        }
        setCurrentHpDirect(currentHp);
    }

    public void setCurrentMp(int i) {
        int currentMp = Math.min(i, getMaxMp());
        if (getCurrentMp() == currentMp) {
            return;
        }
        setCurrentMpDirect(currentMp);
    }

    /**
     * 傳回血盟ID
     *
     * @return
     */
    public int getClanid() {
        return _clanid;
    }

    /**
     * 傳回血盟名稱
     *
     * @return
     */
    public String getClanname() {
        return _clan_name;
    }

    /**
     * 傳回盟徽
     *
     * @return
     */
    public L1EmblemIcon getEmblem() {
        return _emblem;
    }

    public void setShopChat(String shopChat1, String shopChat2) {
        _shop_chat1 = shopChat1;
        _shop_chat2 = shopChat2;
    }

    public void shopChat() {
        try {
            if (_random.nextBoolean()) {
                String info = "";
                if (_random.nextBoolean()) {
                    info = _shop_chat1;
                } else {
                    info = _shop_chat2;
                }
                broadcastPacketAll(new S_DoActionShop(getId(), info));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 停止商店模式
     */
    public void stop_shop() {
        try {
            if (isShop()) {
                set_isShop(false);
                _sellList.clear();
                _buyList.clear();
                _shop_chat1 = null;
                _shop_chat2 = null;
                broadcastPacketAll(new S_CharVisualUpdate(getId(), 0));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 啟動商店模式
     */
    public void start_shop() {
        try {
            if (!isShop()) {
                boolean isShopMap = false;
                switch (getMapId()) {
                    /*
                     * case 340: case 350: case 360: case 370:
                     */
                    case 800:
                        //isShopMap = true;
                        if ((this.getMap().isUsableShop() <= 0) || (ItemTable.get().getTemplate(this.getMap().isUsableShop()) == null)) { // 地圖商店貨幣
                            isShopMap = false;
                        } else {
                            isShopMap = true;
                        }
                }
                if (isShopMap) {
                    set_isShop(true);
                    DeShopItemTable.get().getItems(this);
                    int h = ThreadLocalRandom.current().nextInt(7);
                    setHeading(h);
                    broadcastPacketX8(new S_ChangeHeading(this));
                    broadcastPacketX8(new S_DoActionShop(getId(), _shop_chat1));
                    // 設置商店變身
                    int[] PolyList = {11479, 11427, 10047, 9688, 11322, 10069, 10034, 10032};
                    int polyIndex = ThreadLocalRandom.current().nextInt(8);
                    setStatus(0);
                    setTempCharGfx(PolyList[polyIndex]);
                    broadcastPacketX8(new S_CharVisualUpdate(getId(), 70));
                }
            }
            _is_shop = true;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 賣出 物品/價格
     *
     * @return
     */
    public Map<L1ItemInstance, Integer> get_sellList() {
        return _sellList;
    }

    /**
     * 購入 Integer:物品編號 0:價格 1:強化質 2:購入數量
     *
     * @return
     */
    public Map<Integer, int[]> get_buyList() {
        return _buyList;
    }

    public void sellList(Map<L1ItemInstance, Integer> sellList) {
        _sellList.putAll(sellList);
    }

    public void buyList(Map<Integer, int[]> buyList) {
        _buyList.putAll(buyList);
    }

    public void updateBuyList(Integer key, int[] value) {
        _buyList.put(key, value);
    }

    class Death implements Runnable {
        L1Character _lastAttacker;

        public Death(L1Character lastAttacker) {
            _lastAttacker = lastAttacker;
        }

        public void run() {
            try {
                L1DeInstance mob = L1DeInstance.this;
                L1DeInstance._denameList.remove(_de_name);
                mob.setDeathProcessing(true);
                mob.setCurrentHpDirect(0);
                mob.setDead(true);
                mob.setStatus(8);
                mob.broadcastPacketAll(new S_DoActionGFX(mob.getId(), 8));
                mob.getMap().setPassable(mob.getLocation(), true);
                mob.startChat(1);
                mob.setDeathProcessing(false);
                mob.allTargetClear();
                int deltime = 5;
                mob.startDeleteTimer(deltime);
                ListMapUtil.clear(_sellList);
                ListMapUtil.clear(_buyList);
            } catch (Exception e) {
                L1DeInstance._log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private class Fishing_Runnable implements Runnable {
        private L1DeInstance _npc = null;

        private Fishing_Runnable(L1DeInstance npc) {
            _npc = npc;
        }

        private void startCmd() {
            DeAiThreadPool.get().execute(this);
        }

        public void run() {
            try {
                int x = _npc.getX();
                int y = _npc.getY();
                int x1 = x - 18;
                int y1 = y - 18;
                int x2 = x + 18;
                int y2 = y + 18;
                int rows = x2 - x1;
                int columns = y2 - y1;
                int tgx = 0;
                int tgy = 0;
                L1Map map = _npc.getMap();
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        int cx = x1 + i;
                        int cy = y1 + j;
                        int gab = map.getOriginalTile(cx, cy);
                        if (gab % 28 == 0) {
                            tgx = cx;
                            tgy = cy;
                        }
                    }
                }
                TimeUnit.MILLISECONDS.sleep(10L);
                int i = 20;
                while ((_npc.getX() != tgx) && (_npc.getY() != tgy)) {
                    if (_npc == null) {
                        break;
                    }
                    double d = _npc.getLocation().getLineDistance(new Point(tgx, tgy));
                    if (d <= 1.0D) {
                        break;
                    }
                    int moveDirection = _npc.getMove().moveDirection(tgx, tgy);
                    int dir = _npc.getMove().checkObject(moveDirection);
                    if (dir != -1) {
                        _npc.getMove().setDirectionMove(dir);
                        _npc.setNpcSpeed();
                    }
                    TimeUnit.MILLISECONDS.sleep(_npc.calcSleepTime(_npc.getPassispeed(), 0));
                    i--;
                    if (i <= 0) {
                        break;
                    }
                }
                TimeUnit.MILLISECONDS.sleep(5000L);
                start_fishing();
            } catch (InterruptedException e) {
                L1DeInstance._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
