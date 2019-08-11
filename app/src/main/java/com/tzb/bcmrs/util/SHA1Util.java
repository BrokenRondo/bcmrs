package com.tzb.bcmrs.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.security.MessageDigest;

public class SHA1Util {
   public String SHA1(String bytes){

       String sha1 = "";
       try {
           MessageDigest digest = MessageDigest.getInstance("SHA-1");
           digest.reset();
           digest.update(bytes.getBytes("utf8"));
           sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
       } catch (Exception e){
           e.printStackTrace();
       }
       return sha1;
   }
}
