package com.netty.turingcat.encryption;

import java.util.Date;

/**
 * Created by RyanLee on 2015/10/10.
 */
public class Encrytor {
    private String encryptKey="";
    private String oldEncryptKey="";
    private long expireMillis;
    private long updateTime;
    private String remoteSN = "";

    public void setEncryptKey(String encryptKey) {
        this.oldEncryptKey = this.encryptKey;
        this.encryptKey = encryptKey;
        updateTime = System.currentTimeMillis();
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setExpireSecond(int expireSecond) {
        this.expireMillis = expireSecond * 1000;
    }

    public int getExpireSecond() {
        return (int) (expireMillis / 1000);
    }

    public long getUpdateTime() {
        return updateTime;
    }

	public boolean needExchangeKey() {
		if (System.currentTimeMillis() - updateTime > expireMillis - 300000 ||
				encryptKey.isEmpty()) {
			return true;
		}

		return false;
	}
    public long getUpdateKeyDalay() {
        long delay = expireMillis - (System.currentTimeMillis() - updateTime) - 300000;
        if (delay < 0) {
            delay = 0;
        }

        return delay;
    }

    public String getRemoteSN() {
        return remoteSN;
    }

    public void setRemoteSN(String remoteSN) {
        this.remoteSN = remoteSN;
    }

    public String symEncrypt(int encryType, String data) {
        if (encryType > 0) {
            return AESUtil.encodeAES(data, encryptKey);
        }

        return data;
    }

    public byte[] symEncrypt(int encryType, byte[] inputBytes) {
        return symEncrypt(encryType, inputBytes, 0, inputBytes.length);
    }
    public byte[] symEncrypt(int encryType, byte[] inputBytes, int pos, int length) {
        if (encryType > 0) {
            return AESUtil.encodeAES2(inputBytes, pos, length, encryptKey);
        }

        return inputBytes;
    }

    public String symDeEncrypt(int encryType, String data) {
        if (encryType > 0) {
            String decStr = AESUtil.decodeAES(data, encryptKey);
            if (decStr == null) {
                return AESUtil.decodeAES(data, oldEncryptKey);
            }
        }

        return null;
    }

    public byte[] symDeEncrypt(int encryType, byte[] inputBytes, int pos, int length) {
        if (encryType > 0) {
            byte[] decBytes = AESUtil.decodeAES2(inputBytes, pos, length, encryptKey);
            if (null == decBytes && !oldEncryptKey.isEmpty()) {
                return AESUtil.decodeAES2(inputBytes, pos, length, oldEncryptKey);
            }
            return decBytes;
        }
        return null;
    }
}
