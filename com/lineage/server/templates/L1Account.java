package com.lineage.server.templates;

import java.sql.Timestamp;

public class L1Account {
    public int _tam_point; // 成長果實系統(Tam幣)
    private String _login;
    private String _password;
    private Timestamp _lastactive;
    private int _access_level;
    private String _ip;
    private String _mac;
    private int _character_slot;
    private String _spw;
    private int _warehouse;
    private int _countCharacters;
    private boolean _isLoad;
    private int _server_no;
    private int _first_pay;// 帳號是否首儲

    public String get_login() {
        return _login;
    }

    public void set_login(String login) {
        _login = login;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String password) {
        _password = password;
    }

    public Timestamp get_lastactive() {
        return _lastactive;
    }

    public void set_lastactive(Timestamp lastactive) {
        _lastactive = lastactive;
    }

    public int get_access_level() {
        return _access_level;
    }

    public void set_access_level(int access_level) {
        _access_level = access_level;
    }

    public String get_ip() {
        return _ip;
    }

    public void set_ip(String ip) {
        _ip = ip;
    }

    public String get_mac() {
        return _mac;
    }

    public void set_mac(String mac) {
        _mac = mac;
    }

    public int get_character_slot() {
        return _character_slot;
    }

    public void set_character_slot(int character_slot) {
        _character_slot = character_slot;
    }

    public String get_spw() {
        return _spw;
    }

    public void set_spw(String spw) {
        _spw = spw;
    }

    public int get_warehouse() {
        return _warehouse;
    }

    public void set_warehouse(int warehouse) {
        _warehouse = warehouse;
    }

    public int get_countCharacters() {
        return _countCharacters;
    }

    public void set_countCharacters(int characters) {
        _countCharacters = characters;
    }

    public boolean is_isLoad() {
        return _isLoad;
    }

    public void set_isLoad(boolean load) {
        _isLoad = load;
    }

    public int get_server_no() {
        return _server_no;
    }

    public void set_server_no(int server_no) {
        _server_no = server_no;
    }

    /**
     * 帳戶是否首儲值
     *
     * @return
     */
    public int get_first_pay() {
        return _first_pay;
    }

    /**
     * 帳戶是否首儲值
     *
     * @param first_pay
     */
    public void set_first_pay(int first_pay) {
        _first_pay = first_pay;
    }

    /**
     * 帳戶tam點數
     *
     * @return
     */
    public int get_tam_point() { // 成長果實系統(Tam幣)
        return _tam_point;
    }

    /**
     * 帳戶tam點數
     *
     * @param tam_point
     */
    public void set_tam_point(int tam_point) { // 成長果實系統(Tam幣)
        _tam_point = tam_point;
    }
}
