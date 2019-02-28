package com.wyyabout.collectingcomments.util;

import com.wyyabout.collectingcomments.config.Configs;
import com.wyyabout.collectingcomments.model.Proxy;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public final class WyyyyUtils {

    //获得一个长度为16位的随机字符串
    private static String get16RandomString(int length){
        return "FFFFFFFFFFFFFFFF";
    }

    //通过偏移值得到页码
    //偏移值为 (评论页数-1) * 20
    public static String get_offset(int offset){
        String text ="";
        if (offset == 0){
            text = "{'rid': '', 'offset': '0', 'total': 'true', 'limit': '20', 'csrf_token': ''}";
        } else {
            text = "{'rid': '', 'offset': "+offset+", 'total': 'false', 'limit': '20', 'csrf_token': ''}";
        }
        return text;
    }

    //将明文text进行两次aes加密获得密文encText，因为secKey是在客户端上生成的，所以还需要对其进行RSA加密再传给服务端
    public static String encrypted_request(String text) throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException {
        String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
        String nonce = "0CoJUm6Qyw8W8jud";
        String pubKey = "010001";
        String iv = "0102030405060708";
        String secKey = get16RandomString(16);
        String encText = encrypt(encrypt(text, nonce, iv), secKey,iv);
        return encText;
    }

    public static String encryptForSongRecord(String text){

        String haha = "";
        try {
           haha  = encrypted_request(text);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return haha;
    }

    public static String get_encSecKey(){
        String encSecKey = "a6cd309ceb58db2e2ee096a59c7c47a531933954777ae21461dae6b20c83d5e052af486611664487125c9d343581c0fd782b4def2584108c1228045396bf788bda759ce8bc549a050d28e59ffab3aedfd19be603aa8b47b7194d3f1caac9eae830528202a657f778badada793a526877bbe3cbb05e7b3bb38a04d1d33b653cb5";
        return encSecKey;
    }

    //Java实现AES加密
    private static String encrypt(String content, String password, String ivParameter) throws InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = password.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes("utf-8"));//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(content.getBytes("utf-8"));
            return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码。
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSongRecord(String userid,String type){
        Map<String,String> wyy = new HashMap<>();
        wyy.put("encSecKey",Configs.wyyEnSecKey);
        String firstKey = "{'uid':'"+userid+"','type':'"+type+"','limit':'1000','offset':'0','total':'true','csrf_token':'7a1ef4a72d39401dd2ebfdd4a192aae3'}";
        wyy.put("params",encryptForSongRecord(firstKey));
//        wyy.put("params","O+Eul0X+THC/3ybmumj7hzISF1tXmWYb3HFnyNHlIc5ip/NLo+EG1dxHo2WGrnqmaNXS/gDowjKA1esruQepq1m0/M9gIW78F0Qbgui5ah5Vhj4FcUbPC7RuYcK/rvaBVVfMGQrLW2otLYUpKe3lwqf12Xya8ePtbvUuOW0ToMeiB3iQPjjJA4748OWjoBuxW7okR2xTNbi3HW0O7oWzzyfAFe1ir+MEx2FuSwIZjag=");
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
        headers.put("Referer", "https://music.163.com/user/home?id=105718059");
        headers.put("Origin", "https://music.163.com");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Host", "music.163.com");
        String html = HTTPUtils.getThemWithPost("http://music.163.com/weapi/v1/play/record?csrf_token=7a1ef4a72d39401dd2ebfdd4a192aae3", headers, null, wyy);
        return html;
    }

    public static String getSongList(String userid){
        Map<String,String> wyy = new HashMap<>();
        wyy.put("encSecKey",Configs.wyyEnSecKey);
        String firstKey = "{'uid':'"+userid+"','type':'-1','limit':'1000','offset':'0','total':'true','csrf_token':'d80061c6c1153dff9bbf80332461f7f1'}";
        wyy.put("Params",encryptForSongRecord(firstKey));
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36");
        headers.put("Referer", "http://music.163.com/user/songs/rank?id=64752156");
        headers.put("Content-Type","application/x-www-form-urlencoded");
        headers.put("Origin", "http://music.163.com");
        headers.put("Host", "music.163.com");
        String html = HTTPUtils.getThemWithPost("http://music.163.com/weapi/user/playlist?csrf_token=d80061c6c1153dff9bbf80332461f7f1", headers, null, wyy);
        return html;
    }

    /**
     * 获得某id歌曲某页评论
     * @param songId
     * @param pageIndex
     * @return
     */
    public static String getWyyyComment(String songId,int pageIndex) {
        Proxy proxy = new Proxy();
        Map<String,String> wyy = new HashMap<>();
        wyy.put("encSecKey",Configs.wyyEnSecKey);
//        wyy.put("params","O+Eul0X+THC/3ybmumj7hzISF1tXmWYb3HFnyNHlIc5ip/NLo+EG1dxHo2WGrnqmaNXS/gDowjKA1esruQepq1m0/M9gIW78F0Qbgui5ah5Vhj4FcUbPC7RuYcK/rvaBVVfMGQrLW2otLYUpKe3lwqf12Xya8ePtbvUuOW0ToMeiB3iQPjjJA4748OWjoBuxW7okR2xTNbi3HW0O7oWzzyfAFe1ir+MEx2FuSwIZjag=");
        try {
            wyy.put("params",encrypted_request(get_offset(pageIndex * 20)));
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36");
            headers.put("Referer", "http://music.163.com/song?id=" + songId);
            headers.put("Origin", "http://music.163.com");
            headers.put("Host", "music.163.com");
            String html = HTTPUtils.getThemWithPost("http://music.163.com/weapi/v1/resource/comments/R_SO_4_" + songId + "?csrf_token=", headers, null,  wyy);
            return html;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
