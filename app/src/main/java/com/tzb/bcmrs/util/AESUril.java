package com.tzb.bcmrs.util;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.apache.commons.codec.binary.Base64;

public class AESUril {
    public String geneKey(String seed) throws Exception {
        //获取一个密钥生成器实例
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecureRandom random = new SecureRandom();
        random.setSeed(seed.getBytes());//设置加密用的种子，密钥
        keyGenerator.init(random);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] key=secretKey.getEncoded();
        byte[] keyBase64= Base64.encodeBase64(key);
        return new String(keyBase64);
    }
}

