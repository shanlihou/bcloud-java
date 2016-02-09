package com.example.bcloud;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import android.util.Log;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by shily on 16-2-7.
 */
public class RSA {
    private RSA(){
    }
    private static RSA instance = null;
    public static RSA getInstance(){
        if (instance == null){
            instance = new RSA();
        }
        return instance;
    }
public String encrypt(String pubKey, String strToEnc){
        byte[] encPass = null;
        String basePass = null;
        try
        {
            int index = pubKey.indexOf("-----END PUBLIC KEY-----");
            String tmpStr = pubKey.substring(26, index);
            Log.d("shanlihou", "pub length:" + pubKey.length());
            Log.d("shanlihou", pubKey);
            Log.d("shanlihou", tmpStr);
            String tmpFinal = tmpStr.replaceAll("\n", "");
            Log.d("shanlihou", tmpFinal);

            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
                    new BASE64Decoder().decodeBuffer(tmpFinal));
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    new  BASE64Decoder().decodeBuffer(tmpFinal));
            Log.d("shanlihou", new BASE64Decoder().decodeBuffer(tmpFinal).toString().length() + "");
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            PublicKey publicKey = keyFactory.generatePublic(bobPubKeySpec);
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            Log.d("shanlihou", strToEnc.getBytes().length + strToEnc);
            encPass = cipher.doFinal(strToEnc.getBytes());
            basePass = new BASE64Encoder().encodeBuffer(encPass).toString();

            Log.d("shanlihou", "basePass:" + basePass);
            Log.d("shanlihou", "baPass length:" + basePass.length());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return basePass;
    }
    public String decrypt(String privateKey, String strToEnc){
        byte[] encPass = null;
        String basePass = null;
        try
        {
            Log.d("shanlihou", privateKey);

            int index = privateKey.indexOf("-----END PUBLIC KEY-----");
            String tmpStr = privateKey.substring(26, index);
            Log.d("shanlihou", tmpStr);
            String tmpFinal = tmpStr.replaceAll("\n", "");
            Log.d("shanlihou", tmpFinal);

            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    new  BASE64Decoder().decodeBuffer(tmpFinal));
            Log.d("shanlihou", new BASE64Decoder().decodeBuffer(tmpFinal).toString().length() + "");
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            PrivateKey privateKey1 = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey1);
            encPass = cipher.doFinal(new BASE64Decoder().decodeBuffer(strToEnc));
            Log.d("shanlihou", new String(encPass));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
