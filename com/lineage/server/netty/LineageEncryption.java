package com.lineage.server.netty;

import com.lineage.echo.encryptions.Cipher;

/**
 * Lineage 封包加解密處理器
 * 封裝 Blowfish (Cipher) 實作,提供統一的加解密介面
 * 
 * @author Netty Migration
 */
public class LineageEncryption {
    private final Cipher cipher;
    private boolean initialized = false;

    public LineageEncryption() {
        this.cipher = new Cipher();
    }

    /**
     * 初始化加密金鑰
     * @param seed 種子值 (從握手封包取得)
     */
    public void initKeys(int seed) {
        try {
            cipher.initKeys(seed);
            initialized = true;
        } catch (Exception e) {
            throw new RuntimeException("LineageEncryption 初始化失敗: seed=" + seed, e);
        }
    }

    /**
     * 加密資料 (發送給客戶端)
     * @param data 原始資料
     * @return 加密後的資料
     */
    public byte[] encrypt(byte[] data) {
        if (!initialized) {
            throw new IllegalStateException("加密器尚未初始化,請先呼叫 initKeys()");
        }
        return cipher.encryptClient(data);
    }

    /**
     * 解密資料 (來自客戶端)
     * @param data 加密資料
     * @return 解密後的資料
     */
    public byte[] decrypt(byte[] data) {
        if (!initialized) {
            throw new IllegalStateException("加密器尚未初始化,請先呼叫 initKeys()");
        }
        return cipher.decryptClient(data);
    }

    /**
     * 檢查是否已初始化
     */
    public boolean isInitialized() {
        return initialized;
    }
}
